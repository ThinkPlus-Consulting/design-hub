package com.emsist.designhub.dto;

import java.util.List;

public record ExternalSyncJobResponse(
        String jobId,
        String sourceSystem,
        String transportMode,
        String correlationId,
        String requestedBy,
        String receivedAt,
        String triggerRef,
        boolean dryRun,
        String status,
        int artifactCount,
        List<String> warnings,
        ExternalSyncResult result
) {
}
