package com.emsist.designhub.dto;

import java.util.List;

public record ExternalParityAuditResponse(
        Summary summary,
        List<SystemCoverage> systems,
        List<FieldCoverage> fields
) {
    public record Summary(
            long totalArtifacts,
            int trackedFields,
            double overallCoverageScore,
            String status,
            long hierarchyArtifacts,
            long dependencyArtifacts,
            long relatedArtifacts,
            long duplicateArtifacts
    ) {
    }

    public record SystemCoverage(
            String system,
            long artifactCount,
            double coverageScore,
            long hierarchyArtifacts,
            long dependencyArtifacts,
            List<String> weakestFields
    ) {
    }

    public record FieldCoverage(
            String field,
            long populatedArtifacts,
            long missingArtifacts,
            double coverageScore,
            List<String> exampleMissingArtifacts
    ) {
    }
}
