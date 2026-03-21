package com.emsist.designhub.dto;

public record ExternalSyncJobRequest(
        String sourceSystem,
        String transportMode,
        String correlationId,
        String requestedBy,
        String receivedAt,
        String triggerRef,
        AzureDevOpsSyncRequest azureDevOpsRequest,
        JiraSyncRequest jiraRequest
) {
}
