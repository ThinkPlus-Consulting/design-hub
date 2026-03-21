package com.emsist.designhub.dto;

import java.util.List;

public record ChannelTraversalResponse(
        String channelCode,
        String displayName,
        String channelType,
        List<TouchpointSummary> touchpoints,
        List<GraphNodeReference> screens,
        List<CoverageGap> coverageGaps,
        List<GraphNodeReference> personaReach
) {
    public record TouchpointSummary(
            String touchpointId,
            String label,
            String surfaceId,
            List<String> entryMechanisms,
            List<String> roleKeys,
            List<String> personaIds,
            GraphNodeReference targetScreen
    ) {
    }

    public record CoverageGap(
            String touchpointId,
            String reason
    ) {
    }
}
