package com.emsist.designhub.dto;

import java.util.List;
import java.util.Map;

public record DeliveryStoryResponse(
        String storyId,
        String label,
        String module,
        String domain,
        String storyNumber,
        String status,
        boolean ready,
        FeatureSummary feature,
        List<ScreenSummary> screens,
        List<ApiSummary> apis,
        List<BugSummary> bugs,
        List<FindingSummary> findings,
        List<GapSummary> gaps,
        List<ExternalArtifactSummary> externalArtifacts,
        ReadinessDiagnosticsResponse diagnostics
) {

    public DeliveryStoryResponse withDiagnostics(ReadinessDiagnosticsResponse diagnostics) {
        boolean qaReady = diagnostics != null
                && diagnostics.getReadiness() != null
                && Boolean.TRUE.equals(diagnostics.getReadiness().get("qaReady"));
        return new DeliveryStoryResponse(
                storyId,
                label,
                module,
                domain,
                storyNumber,
                status,
                qaReady,
                feature,
                screens,
                apis,
                bugs,
                findings,
                gaps,
                externalArtifacts,
                diagnostics
        );
    }

    public record FeatureSummary(
            String featureId,
            String title,
            String status
    ) {
    }

    public record ScreenSummary(
            String surfaceId,
            String label,
            String routePath,
            String status
    ) {
    }

    public record ApiSummary(
            String contractId,
            String method,
            String path,
            String status
    ) {
    }

    public record BugSummary(
            String bugId,
            String externalKey,
            String summary,
            String severity,
            String status
    ) {
    }

    public record FindingSummary(
            String findingId,
            String summary,
            String status
    ) {
    }

    public record GapSummary(
            String gapId,
            String gapType,
            String severity,
            String description,
            String status
    ) {
    }

    public record ExternalArtifactSummary(
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
            String status
    ) {
    }
}
