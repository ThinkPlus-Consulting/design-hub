package com.emsist.designhub.dto;

public record ExternalSyncSourceStatusResponse(
        String sourceSystem,
        boolean enabled,
        boolean webhookEnabled,
        boolean webhookSecretConfigured,
        boolean pollingEnabled,
        boolean pollingConfigured,
        boolean baseUrlConfigured,
        boolean pollPathConfigured,
        boolean scopeConfigured,
        boolean filterConfigured,
        boolean tokenConfigured,
        boolean schedulerEnabled,
        boolean pollingDryRun,
        LatestJobSummary latestJob
) {
    public record LatestJobSummary(
            String jobId,
            String status,
            String receivedAt,
            String requestedBy,
            String triggerRef,
            String transportMode
    ) {
    }
}
