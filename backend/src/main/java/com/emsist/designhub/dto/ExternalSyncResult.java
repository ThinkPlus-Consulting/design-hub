package com.emsist.designhub.dto;

import java.util.List;

public record ExternalSyncResult(
        boolean dryRun,
        String result,
        int processedCount,
        int createdCount,
        int updatedCount,
        int skippedCount,
        int failedCount,
        List<ItemResult> items
) {
    public record ItemResult(
            String externalId,
            String outcome,
            String contentHash,
            List<String> warnings
    ) {
    }
}
