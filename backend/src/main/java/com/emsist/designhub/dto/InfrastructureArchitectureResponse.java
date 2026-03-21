package com.emsist.designhub.dto;

import java.util.List;

public record InfrastructureArchitectureResponse(
        String deploymentId,
        String name,
        String environment,
        String description,
        String status,
        List<GraphNodeReference> components,
        List<InfrastructureNodeSummary> infrastructureNodes,
        List<GraphNodeReference> applications,
        List<GraphNodeReference> elements
) {
    public record InfrastructureNodeSummary(
            String nodeId,
            String name,
            String nodeType,
            String location,
            String status
    ) {
    }
}
