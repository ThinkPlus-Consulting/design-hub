package com.emsist.designhub.service;

import com.emsist.designhub.dto.ExternalSyncRequest;
import com.emsist.designhub.dto.ExternalSyncResult;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class ExternalArtifactSyncService {

    private static final String LOOKUP_QUERY = """
            OPTIONAL MATCH (ea:ExternalArtifact {externalId: $externalId})
            RETURN ea IS NOT NULL AS nodeExists,
                   ea.contentHash AS contentHash
            """;

    private static final String UPSERT_ARTIFACT_QUERY = """
            MERGE (ea:ExternalArtifact {externalId: $externalId})
            SET ea.system = $system,
                ea.externalType = $externalType,
                ea.key = $key,
                ea.title = $title,
                ea.projectScope = $projectScope,
                ea.workflowState = $workflowState,
                ea.priority = $priority,
                ea.owner = $owner,
                ea.reporter = $reporter,
                ea.labels = $labels,
                ea.customFields = $customFields,
                ea.url = $url,
                ea.syncStatus = $syncStatus,
                ea.lastSyncedAt = CASE
                    WHEN $lastSyncedAt IS NULL THEN null
                    ELSE datetime($lastSyncedAt)
                END,
                ea.status = $status,
                ea.contentHash = $contentHash
            """;

    private static final String REPLACE_PARENT_LINKS_QUERY = """
            MATCH (ea:ExternalArtifact {externalId: $externalId})
            OPTIONAL MATCH (existingParent:ExternalArtifact)-[parentRel:PARENT_OF]->(ea)
            DELETE parentRel
            WITH ea
            OPTIONAL MATCH (ea)-[childRel:CHILD_OF]->(:ExternalArtifact)
            DELETE childRel
            WITH ea
            UNWIND $targetIds AS targetId
            MERGE (target:ExternalArtifact {externalId: targetId})
            ON CREATE SET target.system = $system,
                          target.syncStatus = 'PENDING',
                          target.status = 'DEFINED'
            MERGE (target)-[:PARENT_OF]->(ea)
            MERGE (ea)-[:CHILD_OF]->(target)
            """;

    private static final String REPLACE_DEPENDENCY_LINKS_QUERY = """
            MATCH (ea:ExternalArtifact {externalId: $externalId})
            OPTIONAL MATCH (ea)-[rel:DEPENDS_ON]->(:ExternalArtifact)
            DELETE rel
            WITH ea
            UNWIND $targetIds AS targetId
            MERGE (target:ExternalArtifact {externalId: targetId})
            ON CREATE SET target.system = $system,
                          target.syncStatus = 'PENDING',
                          target.status = 'DEFINED'
            MERGE (ea)-[:DEPENDS_ON]->(target)
            """;

    private static final String REPLACE_RELATED_LINKS_QUERY = """
            MATCH (ea:ExternalArtifact {externalId: $externalId})
            OPTIONAL MATCH (ea)-[rel:RELATES_TO]->(:ExternalArtifact)
            DELETE rel
            WITH ea
            UNWIND $targetIds AS targetId
            MERGE (target:ExternalArtifact {externalId: targetId})
            ON CREATE SET target.system = $system,
                          target.syncStatus = 'PENDING',
                          target.status = 'DEFINED'
            MERGE (ea)-[:RELATES_TO]->(target)
            """;

    private static final String REPLACE_DUPLICATE_LINKS_QUERY = """
            MATCH (ea:ExternalArtifact {externalId: $externalId})
            OPTIONAL MATCH (ea)-[rel:DUPLICATES]->(:ExternalArtifact)
            DELETE rel
            WITH ea
            UNWIND $targetIds AS targetId
            MERGE (target:ExternalArtifact {externalId: targetId})
            ON CREATE SET target.system = $system,
                          target.syncStatus = 'PENDING',
                          target.status = 'DEFINED'
            MERGE (ea)-[:DUPLICATES]->(target)
            """;

    private static final String NORMALIZE_STORY_FIELDS_QUERY = """
            MATCH (target:UserStory)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_STORY]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.priority) WHERE value IS NOT NULL] AS externalPriorities,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates,
                 reduce(rawLabels = [], labelList IN collect(coalesce(artifact.labels, [])) | rawLabels + labelList) AS collectedLabels
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalPriority = head(externalPriorities),
                target.externalWorkflowState = head(externalWorkflowStates),
                target.externalLabels = reduce(labels = [], label IN collectedLabels |
                    CASE
                        WHEN label IS NULL OR label IN labels THEN labels
                        ELSE labels + label
                    END)
            """;

    private static final String NORMALIZE_BUG_FIELDS_QUERY = """
            MATCH (target:Bug)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_BUG]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.priority) WHERE value IS NOT NULL] AS externalPriorities,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalPriority = head(externalPriorities),
                target.externalWorkflowState = head(externalWorkflowStates)
            """;

    private static final String NORMALIZE_FEATURE_FIELDS_QUERY = """
            MATCH (target:Feature)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_FEATURE]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates,
                 reduce(rawFields = [], customFieldList IN collect(coalesce(artifact.customFields, [])) | rawFields + customFieldList) AS collectedFields
            WITH target, externalRefs, externalOwners, externalWorkflowStates,
                 reduce(iterations = [], fieldEntry IN collectedFields |
                    CASE
                        WHEN fieldEntry IS NULL THEN iterations
                        WHEN fieldEntry STARTS WITH 'iteration=' THEN iterations + substring(fieldEntry, size('iteration='))
                        WHEN fieldEntry STARTS WITH 'iterationPath=' THEN iterations + substring(fieldEntry, size('iterationPath='))
                        WHEN fieldEntry STARTS WITH 'targetIteration=' THEN iterations + substring(fieldEntry, size('targetIteration='))
                        ELSE iterations
                    END) AS rawIterations
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalWorkflowState = head(externalWorkflowStates),
                target.targetIteration = head([iteration IN rawIterations WHERE iteration IS NOT NULL AND trim(iteration) <> ''])
            """;

    private static final String NORMALIZE_TASK_FIELDS_QUERY = """
            MATCH (target:Task)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_TASK]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.priority) WHERE value IS NOT NULL] AS externalPriorities,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates,
                 reduce(rawLabels = [], labelList IN collect(coalesce(artifact.labels, [])) | rawLabels + labelList) AS collectedLabels
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalPriority = head(externalPriorities),
                target.externalWorkflowState = head(externalWorkflowStates),
                target.externalLabels = reduce(labels = [], label IN collectedLabels |
                    CASE
                        WHEN label IS NULL OR label IN labels THEN labels
                        ELSE labels + label
                    END)
            """;

    private static final String NORMALIZE_FINDING_FIELDS_QUERY = """
            MATCH (target:Finding)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_FINDING]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.priority) WHERE value IS NOT NULL] AS externalPriorities,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalPriority = head(externalPriorities),
                target.externalWorkflowState = head(externalWorkflowStates)
            """;

    private static final String NORMALIZE_API_FIELDS_QUERY = """
            MATCH (target:ApiContract)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_API_CONTRACT]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalWorkflowState = head(externalWorkflowStates)
            """;

    private static final String NORMALIZE_EPIC_FIELDS_QUERY = """
            MATCH (target:Epic)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_EPIC]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.priority) WHERE value IS NOT NULL] AS externalPriorities,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalPriority = head(externalPriorities),
                target.externalWorkflowState = head(externalWorkflowStates)
            """;

    private static final String NORMALIZE_PORTFOLIO_FIELDS_QUERY = """
            MATCH (target:RequirementPortfolio)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_PORTFOLIO]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.priority) WHERE value IS NOT NULL] AS externalPriorities,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalPriority = head(externalPriorities),
                target.externalWorkflowState = head(externalWorkflowStates)
            """;

    private static final String NORMALIZE_OBJECTIVE_FIELDS_QUERY = """
            MATCH (target:BusinessObjective)
            OPTIONAL MATCH (artifact:ExternalArtifact)-[:REPRESENTS_OBJECTIVE]->(target)
            WITH target,
                 [ref IN collect(DISTINCT artifact.externalId) WHERE ref IS NOT NULL] AS externalRefs,
                 [value IN collect(DISTINCT artifact.owner) WHERE value IS NOT NULL] AS externalOwners,
                 [value IN collect(DISTINCT artifact.priority) WHERE value IS NOT NULL] AS externalPriorities,
                 [value IN collect(DISTINCT artifact.workflowState) WHERE value IS NOT NULL] AS externalWorkflowStates
            SET target.externalRefs = externalRefs,
                target.externalOwner = head(externalOwners),
                target.externalPriority = head(externalPriorities),
                target.externalWorkflowState = head(externalWorkflowStates)
            """;

    private static final Map<RepresentationType, String> REPRESENTATION_DELETE_QUERIES = Map.of(
            RepresentationType.STORY, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_STORY]->(:UserStory)
                    DELETE rel
                    """,
            RepresentationType.BUG, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_BUG]->(:Bug)
                    DELETE rel
                    """,
            RepresentationType.FEATURE, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_FEATURE]->(:Feature)
                    DELETE rel
                    """,
            RepresentationType.TASK, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_TASK]->(:Task)
                    DELETE rel
                    """,
            RepresentationType.FINDING, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_FINDING]->(:Finding)
                    DELETE rel
                    """,
            RepresentationType.API_CONTRACT, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_API_CONTRACT]->(:ApiContract)
                    DELETE rel
                    """,
            RepresentationType.EPIC, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_EPIC]->(:Epic)
                    DELETE rel
                    """,
            RepresentationType.PORTFOLIO, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_PORTFOLIO]->(:RequirementPortfolio)
                    DELETE rel
                    """,
            RepresentationType.OBJECTIVE, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    OPTIONAL MATCH (ea)-[rel:REPRESENTS_OBJECTIVE]->(:BusinessObjective)
                    DELETE rel
                    """
    );

    private static final Map<RepresentationType, String> REPRESENTATION_UPSERT_QUERIES = Map.of(
            RepresentationType.STORY, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:UserStory {storyId: targetId})
                    MERGE (ea)-[:REPRESENTS_STORY]->(target)
                    """,
            RepresentationType.BUG, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:Bug {bugId: targetId})
                    MERGE (ea)-[:REPRESENTS_BUG]->(target)
                    """,
            RepresentationType.FEATURE, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:Feature {featureId: targetId})
                    MERGE (ea)-[:REPRESENTS_FEATURE]->(target)
                    """,
            RepresentationType.TASK, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:Task {taskId: targetId})
                    MERGE (ea)-[:REPRESENTS_TASK]->(target)
                    """,
            RepresentationType.FINDING, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:Finding {findingId: targetId})
                    MERGE (ea)-[:REPRESENTS_FINDING]->(target)
                    """,
            RepresentationType.API_CONTRACT, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:ApiContract {contractId: targetId})
                    MERGE (ea)-[:REPRESENTS_API_CONTRACT]->(target)
                    """,
            RepresentationType.EPIC, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:Epic {epicId: targetId})
                    MERGE (ea)-[:REPRESENTS_EPIC]->(target)
                    """,
            RepresentationType.PORTFOLIO, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:RequirementPortfolio {portfolioId: targetId})
                    MERGE (ea)-[:REPRESENTS_PORTFOLIO]->(target)
                    """,
            RepresentationType.OBJECTIVE, """
                    MATCH (ea:ExternalArtifact {externalId: $externalId})
                    UNWIND $targetIds AS targetId
                    MATCH (target:BusinessObjective {objectiveId: targetId})
                    MERGE (ea)-[:REPRESENTS_OBJECTIVE]->(target)
                    """
    );

    private final Neo4jClient neo4jClient;
    private final RequirementSyncService requirementSyncService;

    public ExternalSyncResult sync(ExternalSyncRequest request) {
        List<ExternalSyncRequest.Artifact> artifacts = request.artifacts() == null ? List.of() : request.artifacts();
        if (artifacts.isEmpty()) {
            return new ExternalSyncResult(request.dryRun(), "FAILED", 0, 0, 0, 0, 0, List.of());
        }

        List<ExternalSyncResult.ItemResult> items = new ArrayList<>();
        int created = 0;
        int updated = 0;
        int skipped = 0;
        int failed = 0;

        for (ExternalSyncRequest.Artifact artifact : artifacts) {
            try {
                validate(artifact);
                String contentHash = computeContentHash(artifact);
                ArtifactLookup lookup = lookupArtifact(artifact.externalId());
                SyncAction action = decide(lookup.exists(), lookup.contentHash(), contentHash);
                List<String> warnings = collectWarnings(artifact);

                if (!request.dryRun() && action != SyncAction.SKIP) {
                    applyArtifactSync(artifact, contentHash);
                }

                switch (action) {
                    case CREATE -> created++;
                    case UPDATE -> updated++;
                    case SKIP -> skipped++;
                }

                items.add(new ExternalSyncResult.ItemResult(
                        artifact.externalId(),
                        action.name(),
                        contentHash,
                        warnings
                ));
            } catch (IllegalArgumentException exception) {
                failed++;
                items.add(new ExternalSyncResult.ItemResult(
                        artifact == null ? null : artifact.externalId(),
                        "FAILED",
                        null,
                        List.of(exception.getMessage())
                ));
            }
        }

        String result = failed == artifacts.size()
                ? "FAILED"
                : failed > 0
                ? "PARTIAL"
                : "SUCCESS";

        return new ExternalSyncResult(
                request.dryRun(),
                result,
                artifacts.size(),
                created,
                updated,
                skipped,
                failed,
                items
        );
    }

    SyncAction decide(boolean exists, String storedHash, String currentHash) {
        if (!exists) {
            return SyncAction.CREATE;
        }
        if (Objects.equals(storedHash, currentHash)) {
            return SyncAction.SKIP;
        }
        return SyncAction.UPDATE;
    }

    String computeContentHash(ExternalSyncRequest.Artifact artifact) {
        return requirementSyncService.computeContentHash(
                artifact.externalId(),
                normalize(artifact.system()),
                normalize(artifact.externalType()),
                normalize(artifact.key()),
                normalize(artifact.title()),
                normalize(artifact.projectScope()),
                normalize(artifact.workflowState()),
                normalize(artifact.priority()),
                normalize(artifact.owner()),
                normalize(artifact.reporter()),
                join(canonicalizeStrings(artifact.labels())),
                join(canonicalizeCustomFields(artifact.customFields())),
                normalize(artifact.url()),
                normalize(defaultSyncStatus(artifact.syncStatus())),
                normalize(defaultStatus(artifact.status())),
                normalize(artifact.lastSyncedAt()),
                join(canonicalizeStrings(artifact.parentExternalIds())),
                join(canonicalizeStrings(artifact.dependencyExternalIds())),
                join(canonicalizeStrings(artifact.relatedExternalIds())),
                join(canonicalizeStrings(artifact.duplicateExternalIds())),
                join(canonicalizeRepresentations(artifact.represents()))
        );
    }

    ArtifactLookup lookupArtifact(String externalId) {
        Optional<Map<String, Object>> result = neo4jClient.query(LOOKUP_QUERY)
                .bind(externalId).to("externalId")
                .fetch()
                .first();

        if (result.isEmpty()) {
            return new ArtifactLookup(false, null);
        }

        Map<String, Object> row = result.get();
        return new ArtifactLookup(
                Boolean.TRUE.equals(row.get("nodeExists")),
                row.get("contentHash") == null ? null : String.valueOf(row.get("contentHash"))
        );
    }

    void applyArtifactSync(ExternalSyncRequest.Artifact artifact, String contentHash) {
        upsertArtifactNode(artifact, contentHash);
        replaceExternalLinks(artifact, REPLACE_PARENT_LINKS_QUERY, canonicalizeStrings(artifact.parentExternalIds()));
        replaceExternalLinks(artifact, REPLACE_DEPENDENCY_LINKS_QUERY, canonicalizeStrings(artifact.dependencyExternalIds()));
        replaceExternalLinks(artifact, REPLACE_RELATED_LINKS_QUERY, canonicalizeStrings(artifact.relatedExternalIds()));
        replaceExternalLinks(artifact, REPLACE_DUPLICATE_LINKS_QUERY, canonicalizeStrings(artifact.duplicateExternalIds()));
        replaceRepresentationLinks(artifact);
        normalizeRepresentedPrimaryNodes();
    }

    public void normalizeRepresentedPrimaryNodes() {
        neo4jClient.query(NORMALIZE_STORY_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_BUG_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_FEATURE_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_TASK_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_FINDING_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_API_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_EPIC_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_PORTFOLIO_FIELDS_QUERY).run();
        neo4jClient.query(NORMALIZE_OBJECTIVE_FIELDS_QUERY).run();
    }

    private void validate(ExternalSyncRequest.Artifact artifact) {
        if (artifact == null) {
            throw new IllegalArgumentException("Artifact payload is required");
        }
        if (normalize(artifact.externalId()) == null) {
            throw new IllegalArgumentException("externalId is required");
        }
        if (normalize(artifact.system()) == null) {
            throw new IllegalArgumentException("system is required");
        }
        if (normalize(artifact.key()) == null) {
            throw new IllegalArgumentException("key is required");
        }
    }

    private List<String> collectWarnings(ExternalSyncRequest.Artifact artifact) {
        List<String> warnings = new ArrayList<>();
        for (ExternalSyncRequest.Representation representation : artifact.represents() == null ? List.<ExternalSyncRequest.Representation>of() : artifact.represents()) {
            if (normalizeRepresentationType(representation.nodeType()) == null) {
                warnings.add("Unsupported representation type: " + representation.nodeType());
            }
            if (normalize(representation.id()) == null) {
                warnings.add("Missing representation id for type: " + representation.nodeType());
            }
        }
        return warnings.stream().distinct().toList();
    }

    private void upsertArtifactNode(ExternalSyncRequest.Artifact artifact, String contentHash) {
        neo4jClient.query(UPSERT_ARTIFACT_QUERY)
                .bind(artifact.externalId()).to("externalId")
                .bind(normalize(artifact.system())).to("system")
                .bind(normalize(artifact.externalType())).to("externalType")
                .bind(normalize(artifact.key())).to("key")
                .bind(normalize(artifact.title())).to("title")
                .bind(normalize(artifact.projectScope())).to("projectScope")
                .bind(normalize(artifact.workflowState())).to("workflowState")
                .bind(normalize(artifact.priority())).to("priority")
                .bind(normalize(artifact.owner())).to("owner")
                .bind(normalize(artifact.reporter())).to("reporter")
                .bind(canonicalizeStrings(artifact.labels())).to("labels")
                .bind(canonicalizeCustomFields(artifact.customFields())).to("customFields")
                .bind(normalize(artifact.url())).to("url")
                .bind(defaultSyncStatus(artifact.syncStatus())).to("syncStatus")
                .bind(normalize(artifact.lastSyncedAt())).to("lastSyncedAt")
                .bind(defaultStatus(artifact.status())).to("status")
                .bind(contentHash).to("contentHash")
                .run();
    }

    private void replaceExternalLinks(ExternalSyncRequest.Artifact artifact, String query, List<String> targetIds) {
        neo4jClient.query(query)
                .bind(artifact.externalId()).to("externalId")
                .bind(targetIds).to("targetIds")
                .bind(defaultSystem(artifact.system())).to("system")
                .run();
    }

    private void replaceRepresentationLinks(ExternalSyncRequest.Artifact artifact) {
        Map<RepresentationType, List<String>> targetsByType = artifact.represents() == null
                ? Map.of()
                : artifact.represents().stream()
                .filter(Objects::nonNull)
                .filter(representation -> normalize(representation.id()) != null)
                .map(representation -> Map.entry(normalizeRepresentationType(representation.nodeType()), normalize(representation.id())))
                .filter(entry -> entry.getKey() != null)
                .collect(java.util.stream.Collectors.groupingBy(
                        Map.Entry::getKey,
                        java.util.LinkedHashMap::new,
                        java.util.stream.Collectors.mapping(Map.Entry::getValue, java.util.stream.Collectors.toList())
                ));

        for (RepresentationType type : RepresentationType.values()) {
            neo4jClient.query(REPRESENTATION_DELETE_QUERIES.get(type))
                    .bind(artifact.externalId()).to("externalId")
                    .run();

            List<String> targetIds = canonicalizeStrings(targetsByType.get(type));
            if (targetIds.isEmpty()) {
                continue;
            }

            neo4jClient.query(REPRESENTATION_UPSERT_QUERIES.get(type))
                    .bind(artifact.externalId()).to("externalId")
                    .bind(targetIds).to("targetIds")
                    .run();
        }
    }

    private List<String> canonicalizeStrings(Collection<String> values) {
        if (values == null) {
            return List.of();
        }
        return values.stream()
                .map(this::normalize)
                .filter(Objects::nonNull)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf
                ));
    }

    private List<String> canonicalizeCustomFields(Map<String, String> customFields) {
        if (customFields == null || customFields.isEmpty()) {
            return List.of();
        }
        return customFields.entrySet().stream()
                .filter(Objects::nonNull)
                .map(entry -> {
                    String key = normalize(entry.getKey());
                    String value = normalize(entry.getValue());
                    return key == null || value == null ? null : key + "=" + value;
                })
                .filter(Objects::nonNull)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf
                ));
    }

    private List<String> canonicalizeRepresentations(Collection<ExternalSyncRequest.Representation> representations) {
        if (representations == null) {
            return List.of();
        }
        return representations.stream()
                .filter(Objects::nonNull)
                .map(representation -> {
                    RepresentationType type = normalizeRepresentationType(representation.nodeType());
                    String id = normalize(representation.id());
                    return type == null || id == null ? null : type.name() + ":" + id;
                })
                .filter(Objects::nonNull)
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .collect(java.util.stream.Collectors.collectingAndThen(
                        java.util.stream.Collectors.toCollection(LinkedHashSet::new),
                        List::copyOf
                ));
    }

    private RepresentationType normalizeRepresentationType(String value) {
        String normalized = normalize(value);
        if (normalized == null) {
            return null;
        }
        return switch (normalized.toUpperCase(Locale.ROOT)) {
            case "STORY", "USERSTORY", "USER_STORY" -> RepresentationType.STORY;
            case "BUG" -> RepresentationType.BUG;
            case "FEATURE" -> RepresentationType.FEATURE;
            case "EPIC" -> RepresentationType.EPIC;
            case "PORTFOLIO", "REQUIREMENTPORTFOLIO", "REQUIREMENT_PORTFOLIO", "REQUIREMENT-PORTFOLIO" -> RepresentationType.PORTFOLIO;
            case "OBJECTIVE", "BUSINESSOBJECTIVE", "BUSINESS_OBJECTIVE", "BUSINESS-OBJECTIVE" -> RepresentationType.OBJECTIVE;
            case "TASK" -> RepresentationType.TASK;
            case "FINDING" -> RepresentationType.FINDING;
            case "API", "APICONTRACT", "API_CONTRACT", "API-CONTRACT" -> RepresentationType.API_CONTRACT;
            default -> null;
        };
    }

    private String defaultSyncStatus(String value) {
        return Optional.ofNullable(normalize(value)).map(v -> v.toUpperCase(Locale.ROOT)).orElse("SYNCED");
    }

    private String defaultStatus(String value) {
        return Optional.ofNullable(normalize(value)).map(v -> v.toUpperCase(Locale.ROOT)).orElse("DEFINED");
    }

    private String defaultSystem(String value) {
        return Optional.ofNullable(normalize(value)).map(v -> v.toUpperCase(Locale.ROOT)).orElse("UNKNOWN");
    }

    private String normalize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String join(List<String> values) {
        return values.isEmpty() ? null : String.join("|", values);
    }

    record ArtifactLookup(boolean exists, String contentHash) {
    }

    enum SyncAction {
        CREATE,
        UPDATE,
        SKIP
    }

    private enum RepresentationType {
        STORY,
        BUG,
        FEATURE,
        EPIC,
        PORTFOLIO,
        OBJECTIVE,
        TASK,
        FINDING,
        API_CONTRACT
    }
}
