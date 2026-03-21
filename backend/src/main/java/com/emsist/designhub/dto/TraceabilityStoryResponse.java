package com.emsist.designhub.dto;

import java.util.List;

public record TraceabilityStoryResponse(
        GraphNodeReference objective,
        GraphNodeReference portfolio,
        GraphNodeReference epic,
        GraphNodeReference feature,
        GraphNodeReference story,
        List<GraphNodeReference> screens,
        List<GraphNodeReference> interactions,
        List<GraphNodeReference> apis,
        List<GraphNodeReference> dataEntities,
        List<GraphNodeReference> messages,
        List<GraphNodeReference> tasks,
        List<String> missingSpineSegments
) {
}
