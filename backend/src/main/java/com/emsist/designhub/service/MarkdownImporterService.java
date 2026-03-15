package com.emsist.designhub.service;

import com.emsist.designhub.domain.ImportSnapshot;
import com.emsist.designhub.dto.*;
import com.emsist.designhub.repository.ImportSnapshotRepository;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class MarkdownImporterService {

    private final FrontmatterParser parser;
    private final SchemaValidatorService schemaValidator;
    private final ReconciliationService reconciler;
    private final RequirementSyncService syncService;
    private final Neo4jClient neo4jClient;
    private final ImportSnapshotRepository snapshotRepo;

    public MarkdownImporterService(FrontmatterParser parser,
                                    SchemaValidatorService schemaValidator,
                                    ReconciliationService reconciler,
                                    RequirementSyncService syncService,
                                    Neo4jClient neo4jClient,
                                    ImportSnapshotRepository snapshotRepo) {
        this.parser = parser;
        this.schemaValidator = schemaValidator;
        this.reconciler = reconciler;
        this.syncService = syncService;
        this.neo4jClient = neo4jClient;
        this.snapshotRepo = snapshotRepo;
    }

    public ImportResult importDocument(String markdownContent, String filePath) {
        // Stage 1: Parse frontmatter
        var fmOpt = parser.parse(markdownContent);
        if (fmOpt.isEmpty()) {
            return ImportResult.builder()
                    .snapshotId(generateSnapshotId())
                    .result("FAILED")
                    .created(List.of()).updated(List.of()).conflicts(List.of())
                    .errors(List.of("Failed to parse frontmatter from " + filePath))
                    .diffReport("Parse error — no frontmatter found")
                    .build();
        }

        var fm = fmOpt.get();
        String body = parser.extractBody(markdownContent);
        if (body == null) body = "";

        // Stage 2: Validate schema
        Map<String, Object> candidate = new LinkedHashMap<>();
        candidate.put(getIdField(fm.getType()), fm.getId());
        candidate.put("label", body.lines().findFirst().orElse(""));
        var validation = schemaValidator.validate(fm.getType(), candidate);
        if (!validation.isValid()) {
            return ImportResult.builder()
                    .snapshotId(generateSnapshotId())
                    .result("FAILED")
                    .created(List.of()).updated(List.of()).conflicts(List.of())
                    .errors(validation.getErrors())
                    .diffReport("Schema validation failed")
                    .build();
        }

        // Stage 3: Compute hash and reconcile against graph state
        String contentHash = syncService.computeContentHash(fm.getId(), body);
        var decision = reconciler.reconcile(fm.getId(), fm.getType(), contentHash);

        // Stage 4: Persist to Neo4j and build result
        List<NodeSummary> created = new ArrayList<>();
        List<NodeSummary> updated = new ArrayList<>();
        String snapshotId = generateSnapshotId();

        List<ConflictSummary> conflicts = new ArrayList<>();

        switch (decision) {
            case CREATE -> {
                upsertNode(fm, body, contentHash);
                created.add(NodeSummary.builder()
                        .nodeId(fm.getId()).nodeType(fm.getType())
                        .action("CREATED").confidence("HIGH").build());
            }
            case UPDATE -> {
                upsertNode(fm, body, contentHash);
                updated.add(NodeSummary.builder()
                        .nodeId(fm.getId()).nodeType(fm.getType())
                        .action("UPDATED").confidence("HIGH").build());
            }
            case SKIP -> {
                // No persistence — graph already has current content
            }
            case CONFLICT -> {
                conflicts.add(ConflictSummary.builder()
                        .nodeId(fm.getId())
                        .field("contentHash")
                        .docValue(contentHash)
                        .graphValue("(stored)")
                        .resolution("MANUAL_REVIEW_REQUIRED")
                        .build());
            }
        }

        // Stage 5: Create audit snapshot with accurate result
        String result = switch (decision) {
            case CREATE, UPDATE -> "SUCCESS";
            case SKIP -> "SKIPPED";
            case CONFLICT -> "CONFLICTED";
        };
        int itemCount = created.size() + updated.size();
        saveSnapshot(snapshotId, filePath, result, itemCount, contentHash,
                conflicts.isEmpty() ? null : "Conflict on " + fm.getId());

        return ImportResult.builder()
                .snapshotId(snapshotId)
                .result(result)
                .created(created).updated(updated).conflicts(conflicts)
                .errors(List.of())
                .diffReport(buildDiffReport(snapshotId, filePath, created, updated))
                .build();
    }

    public ImportResult importFile(String filePath, ImportRequest.ConflictStrategy strategy) {
        try {
            String content = Files.readString(Path.of(filePath));
            var result = importDocument(content, filePath);
            // If conflict detected and strategy says SKIP, change result
            if ("CONFLICTED".equals(result.getResult())
                    && strategy == ImportRequest.ConflictStrategy.SKIP) {
                result.setResult("SKIPPED");
                result.getConflicts().clear();
            }
            return result;
        } catch (Exception e) {
            return ImportResult.builder()
                    .result("FAILED")
                    .errors(List.of("Failed to read file: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Upsert a node into Neo4j using MERGE. Sets contentHash for drift detection,
     * label from first body line, status from frontmatter, and version.
     */
    private void upsertNode(Frontmatter fm, String body, String contentHash) {
        String idField = getIdField(fm.getType());
        String label = sanitizeLabel(fm.getType());
        String firstLine = body.lines().findFirst().orElse("");

        String cypher = String.format(
                "MERGE (n:%s {%s: $nodeId}) " +
                "SET n.contentHash = $contentHash, " +
                "    n.label = $label, " +
                "    n.status = $status, " +
                "    n.version = $version",
                label, idField);

        neo4jClient.query(cypher)
                .bind(fm.getId()).to("nodeId")
                .bind(contentHash).to("contentHash")
                .bind(firstLine).to("label")
                .bind(fm.getStatus()).to("status")
                .bind(fm.getVersion()).to("version")
                .run();
    }

    private void saveSnapshot(String snapshotId, String filePath, String result,
                               int itemCount, String contentHash, String errorSummary) {
        var snapshot = ImportSnapshot.builder()
                .snapshotId(snapshotId)
                .sourceType("GIT_DOC")
                .sourcePath(filePath)
                .importedAt(Instant.now())
                .importedBy("markdown-importer")
                .result(result)
                .itemCount(itemCount)
                .contentHash(contentHash)
                .errorSummary(errorSummary)
                .build();
        snapshotRepo.save(snapshot);
    }

    private String generateSnapshotId() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String suffix = UUID.randomUUID().toString().substring(0, 8);
        return "IMP-" + date + "-" + suffix;
    }

    private String getIdField(String type) {
        return switch (type) {
            case "UserStory" -> "storyId";
            case "Screen" -> "surfaceId";
            case "Journey" -> "journeyId";
            case "Epic" -> "epicId";
            case "Feature" -> "featureId";
            case "Task" -> "taskId";
            case "TestCase" -> "testCaseId";
            case "ApiContract" -> "contractId";
            case "DataEntity" -> "entityId";
            case "Rule" -> "ruleId";
            case "ProcessActivity" -> "activityId";
            case "BusinessProcess" -> "processId";
            default -> "id";
        };
    }

    private String sanitizeLabel(String label) {
        if (!label.matches("[A-Za-z][A-Za-z0-9]*")) {
            throw new IllegalArgumentException("Invalid node label: " + label);
        }
        return label;
    }

    private String buildDiffReport(String snapshotId, String filePath,
                                    List<NodeSummary> created, List<NodeSummary> updated) {
        var sb = new StringBuilder();
        sb.append("Import: ").append(snapshotId).append("\n");
        sb.append("Source: ").append(filePath).append("\n");
        sb.append("Created: ").append(created.size()).append(" nodes\n");
        sb.append("Updated: ").append(updated.size()).append(" nodes\n");
        return sb.toString();
    }
}
