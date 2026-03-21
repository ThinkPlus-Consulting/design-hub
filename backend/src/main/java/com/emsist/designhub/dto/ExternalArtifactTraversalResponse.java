package com.emsist.designhub.dto;

import java.util.List;
import java.util.Map;

public record ExternalArtifactTraversalResponse(
        String externalId,
        String system,
        String externalType,
        String key,
        String title,
        String projectScope,
        String workflowState,
        String priority,
        String owner,
        String reporter,
        List<String> labels,
        Map<String, String> customFields,
        String url,
        String syncStatus,
        String lastSyncedAt,
        String status,
        List<ArtifactLinkSummary> parents,
        List<ArtifactLinkSummary> children,
        List<ArtifactLinkSummary> dependencies,
        List<ArtifactLinkSummary> relatedArtifacts,
        List<ArtifactLinkSummary> duplicates,
        List<GraphNodeReference> representedObjects
) {
    public record ArtifactLinkSummary(
            String externalId,
            String system,
            String externalType,
            String key,
            String title,
            String workflowState,
            String syncStatus,
            String status
    ) {
    }
}
