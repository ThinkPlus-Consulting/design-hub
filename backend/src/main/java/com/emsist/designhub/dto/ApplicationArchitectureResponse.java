package com.emsist.designhub.dto;

import java.util.List;

public record ApplicationArchitectureResponse(
        String applicationId,
        String name,
        String description,
        String applicationType,
        String status,
        List<String> ownerNames,
        List<ComponentSummary> components,
        List<GraphNodeReference> apis,
        List<GraphNodeReference> screens,
        List<GraphNodeReference> features,
        List<DependencySummary> dependencies
) {
    public record ComponentSummary(
            String componentId,
            String name,
            String componentType,
            String frameworkFamily,
            String runtime,
            String modulePath,
            String status,
            List<GraphNodeReference> apis,
            List<GraphNodeReference> screens,
            List<GraphNodeReference> dependencies
    ) {
    }

    public record DependencySummary(
            String applicationId,
            String name,
            String direction,
            String status
    ) {
    }
}
