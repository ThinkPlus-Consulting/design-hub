package com.emsist.designhub.dto;

import java.util.List;

public record ExternalArtifactSummaryResponse(
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
        String url,
        String syncStatus,
        String lastSyncedAt,
        String status,
        long representedObjectCount,
        long childCount,
        long dependencyCount,
        long relatedCount
) {
}
