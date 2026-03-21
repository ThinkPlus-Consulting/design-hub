package com.emsist.designhub.dto;

public record InfrastructureDeploymentSummaryResponse(
        String deploymentId,
        String name,
        String environment,
        String status,
        long componentCount,
        long applicationCount,
        long infrastructureCount
) {
}
