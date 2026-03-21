package com.emsist.designhub.service;

import com.emsist.designhub.dto.ExternalParityAuditResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalAlignmentAuditService {

    private static final List<String> TRACKED_FIELDS = List.of(
            "title",
            "projectScope",
            "workflowState",
            "priority",
            "owner",
            "reporter",
            "labels",
            "url",
            "syncStatus",
            "lastSyncedAt"
    );

    private static final String PARITY_QUERY = """
            MATCH (ea:ExternalArtifact)
            RETURN ea.externalId AS externalId,
                   coalesce(ea.system, 'UNKNOWN') AS system,
                   ea.title AS title,
                   ea.projectScope AS projectScope,
                   ea.workflowState AS workflowState,
                   ea.priority AS priority,
                   ea.owner AS owner,
                   ea.reporter AS reporter,
                   coalesce(ea.labels, []) AS labels,
                   ea.url AS url,
                   ea.syncStatus AS syncStatus,
                   toString(ea.lastSyncedAt) AS lastSyncedAt,
                   EXISTS { (ea)-[:PARENT_OF]->(:ExternalArtifact) } OR EXISTS { (:ExternalArtifact)-[:PARENT_OF]->(ea) } AS hasHierarchy,
                   EXISTS { (ea)-[:DEPENDS_ON]->(:ExternalArtifact) } OR EXISTS { (:ExternalArtifact)-[:DEPENDS_ON]->(ea) } AS hasDependency,
                   EXISTS { (ea)-[:RELATES_TO]-(:ExternalArtifact) } AS hasRelated,
                   EXISTS { (ea)-[:DUPLICATES]-(:ExternalArtifact) } AS hasDuplicate
            ORDER BY coalesce(ea.system, 'UNKNOWN'), coalesce(ea.title, ea.key, ea.externalId)
            """;

    private final Neo4jClient neo4jClient;

    public ExternalParityAuditResponse getParityAudit() {
        List<ArtifactAuditRow> artifacts = neo4jClient.query(PARITY_QUERY)
                .fetch()
                .all()
                .stream()
                .map(this::toAuditRow)
                .toList();

        List<ExternalParityAuditResponse.FieldCoverage> fields = TRACKED_FIELDS.stream()
                .map(field -> fieldCoverage(field, artifacts))
                .sorted(Comparator.comparing(ExternalParityAuditResponse.FieldCoverage::coverageScore)
                        .thenComparing(ExternalParityAuditResponse.FieldCoverage::field, String.CASE_INSENSITIVE_ORDER))
                .toList();

        Map<String, List<ArtifactAuditRow>> bySystem = artifacts.stream()
                .collect(Collectors.groupingBy(ArtifactAuditRow::system, LinkedHashMap::new, Collectors.toList()));

        List<ExternalParityAuditResponse.SystemCoverage> systems = bySystem.entrySet().stream()
                .map(entry -> systemCoverage(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(ExternalParityAuditResponse.SystemCoverage::coverageScore).reversed()
                        .thenComparing(ExternalParityAuditResponse.SystemCoverage::system, String.CASE_INSENSITIVE_ORDER))
                .toList();

        long hierarchyArtifacts = artifacts.stream().filter(ArtifactAuditRow::hasHierarchy).count();
        long dependencyArtifacts = artifacts.stream().filter(ArtifactAuditRow::hasDependency).count();
        long relatedArtifacts = artifacts.stream().filter(ArtifactAuditRow::hasRelated).count();
        long duplicateArtifacts = artifacts.stream().filter(ArtifactAuditRow::hasDuplicate).count();
        double overallCoverageScore = round(average(fields.stream()
                .map(ExternalParityAuditResponse.FieldCoverage::coverageScore)
                .toList()));

        return new ExternalParityAuditResponse(
                new ExternalParityAuditResponse.Summary(
                        artifacts.size(),
                        TRACKED_FIELDS.size(),
                        overallCoverageScore,
                        status(overallCoverageScore),
                        hierarchyArtifacts,
                        dependencyArtifacts,
                        relatedArtifacts,
                        duplicateArtifacts
                ),
                systems,
                fields
        );
    }

    private ArtifactAuditRow toAuditRow(Map<String, Object> row) {
        return new ArtifactAuditRow(
                string(row, "externalId"),
                string(row, "system"),
                string(row, "title"),
                string(row, "projectScope"),
                string(row, "workflowState"),
                string(row, "priority"),
                string(row, "owner"),
                string(row, "reporter"),
                strings(row.get("labels")),
                string(row, "url"),
                string(row, "syncStatus"),
                string(row, "lastSyncedAt"),
                bool(row.get("hasHierarchy")),
                bool(row.get("hasDependency")),
                bool(row.get("hasRelated")),
                bool(row.get("hasDuplicate"))
        );
    }

    private ExternalParityAuditResponse.SystemCoverage systemCoverage(String system, List<ArtifactAuditRow> artifacts) {
        List<ExternalParityAuditResponse.FieldCoverage> fields = TRACKED_FIELDS.stream()
                .map(field -> fieldCoverage(field, artifacts))
                .toList();

        double coverageScore = round(average(fields.stream()
                .map(ExternalParityAuditResponse.FieldCoverage::coverageScore)
                .toList()));

        List<String> weakestFields = fields.stream()
                .sorted(Comparator.comparing(ExternalParityAuditResponse.FieldCoverage::coverageScore)
                        .thenComparing(ExternalParityAuditResponse.FieldCoverage::field, String.CASE_INSENSITIVE_ORDER))
                .limit(3)
                .map(ExternalParityAuditResponse.FieldCoverage::field)
                .toList();

        return new ExternalParityAuditResponse.SystemCoverage(
                system,
                artifacts.size(),
                coverageScore,
                artifacts.stream().filter(ArtifactAuditRow::hasHierarchy).count(),
                artifacts.stream().filter(ArtifactAuditRow::hasDependency).count(),
                weakestFields
        );
    }

    private ExternalParityAuditResponse.FieldCoverage fieldCoverage(String field, List<ArtifactAuditRow> artifacts) {
        long populatedArtifacts = artifacts.stream()
                .filter(artifact -> fieldPresent(field, artifact))
                .count();
        long missingArtifacts = Math.max(0, artifacts.size() - populatedArtifacts);
        List<String> exampleMissingArtifacts = artifacts.stream()
                .filter(artifact -> !fieldPresent(field, artifact))
                .map(ArtifactAuditRow::externalId)
                .filter(Objects::nonNull)
                .limit(3)
                .toList();

        return new ExternalParityAuditResponse.FieldCoverage(
                field,
                populatedArtifacts,
                missingArtifacts,
                coverage(populatedArtifacts, artifacts.size()),
                exampleMissingArtifacts
        );
    }

    private boolean fieldPresent(String field, ArtifactAuditRow artifact) {
        return switch (field) {
            case "title" -> present(artifact.title());
            case "projectScope" -> present(artifact.projectScope());
            case "workflowState" -> present(artifact.workflowState());
            case "priority" -> present(artifact.priority());
            case "owner" -> present(artifact.owner());
            case "reporter" -> present(artifact.reporter());
            case "labels" -> !artifact.labels().isEmpty();
            case "url" -> present(artifact.url());
            case "syncStatus" -> present(artifact.syncStatus());
            case "lastSyncedAt" -> present(artifact.lastSyncedAt());
            default -> false;
        };
    }

    private boolean present(String value) {
        return value != null && !value.isBlank();
    }

    private boolean bool(Object value) {
        return Boolean.TRUE.equals(value);
    }

    private List<String> strings(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .toList();
        }
        return List.of();
    }

    private String string(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private double coverage(long populated, int total) {
        if (total == 0) {
            return 0.0;
        }
        return round((populated * 100.0) / total);
    }

    private double average(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private String status(double score) {
        if (score >= 80.0) {
            return "GREEN";
        }
        if (score >= 60.0) {
            return "AMBER";
        }
        return "RED";
    }

    private record ArtifactAuditRow(
            String externalId,
            String system,
            String title,
            String projectScope,
            String workflowState,
            String priority,
            String owner,
            String reporter,
            List<String> labels,
            String url,
            String syncStatus,
            String lastSyncedAt,
            boolean hasHierarchy,
            boolean hasDependency,
            boolean hasRelated,
            boolean hasDuplicate
    ) {
    }
}
