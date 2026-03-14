package com.emsist.designhub.service;

import com.emsist.designhub.domain.ImportSnapshot;
import com.emsist.designhub.dto.*;
import com.emsist.designhub.repository.ImportSnapshotRepository;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class MarkdownImporterService {

    private final FrontmatterParser parser;
    private final SchemaValidatorService schemaValidator;
    private final ReconciliationService reconciler;
    private final RequirementSyncService syncService;
    private final Neo4jClient neo4jClient;
    private final ImportSnapshotRepository snapshotRepo;
    private final AtomicInteger seqCounter = new AtomicInteger(1);

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

        // Stage 3: Compute hash and reconcile
        String contentHash = syncService.computeContentHash(fm.getId(), body);
        var decision = reconciler.decide(fm.getId(), fm.getType(), null, contentHash);

        // Stage 4: Build result
        List<NodeSummary> created = new ArrayList<>();
        List<NodeSummary> updated = new ArrayList<>();
        if (decision == ReconciliationService.Decision.CREATE) {
            created.add(NodeSummary.builder()
                    .nodeId(fm.getId()).nodeType(fm.getType())
                    .action("CREATED").confidence("HIGH").build());
        } else if (decision == ReconciliationService.Decision.UPDATE) {
            updated.add(NodeSummary.builder()
                    .nodeId(fm.getId()).nodeType(fm.getType())
                    .action("UPDATED").confidence("HIGH").build());
        }

        String snapshotId = generateSnapshotId();
        return ImportResult.builder()
                .snapshotId(snapshotId)
                .result("SUCCESS")
                .created(created).updated(updated).conflicts(List.of())
                .errors(List.of())
                .diffReport(buildDiffReport(snapshotId, filePath, created, updated))
                .build();
    }

    private String generateSnapshotId() {
        String date = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        return "IMP-" + date + "-" + String.format("%03d", seqCounter.getAndIncrement());
    }

    private String getIdField(String type) {
        return switch (type) {
            case "UserStory" -> "storyId";
            case "Screen" -> "surfaceId";
            case "Journey" -> "journeyId";
            default -> "id";
        };
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
