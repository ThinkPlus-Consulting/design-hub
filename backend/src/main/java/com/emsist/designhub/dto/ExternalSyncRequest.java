package com.emsist.designhub.dto;

import java.util.List;
import java.util.Map;

public record ExternalSyncRequest(
        boolean dryRun,
        List<Artifact> artifacts
) {
    public record Artifact(
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
            List<String> parentExternalIds,
            List<String> dependencyExternalIds,
            List<String> relatedExternalIds,
            List<String> duplicateExternalIds,
            List<Representation> represents
    ) {
    }

    public record Representation(
            String nodeType,
            String id
    ) {
    }
}
