package com.emsist.designhub.dto;

import java.util.List;

public record DataArchitectureResponse(
        String objectId,
        String name,
        String domain,
        String description,
        String sensitivity,
        String status,
        List<EntitySummary> entities,
        List<FlowSummary> flows,
        List<GraphNodeReference> apis,
        List<GraphNodeReference> screens,
        List<GraphNodeReference> children
) {
    public record EntitySummary(
            String entityId,
            String name,
            String entityType,
            long fieldCount,
            String status
    ) {
    }

    public record FlowSummary(
            String flowId,
            String name,
            String direction,
            String status,
            String sourceApplicationId,
            String sourceApplicationName,
            String targetApplicationId,
            String targetApplicationName
    ) {
    }
}
