package com.emsist.designhub.dto;

import java.util.List;

public record PersonaTraversalResponse(
        String personaId,
        String name,
        String summary,
        String status,
        List<String> roleKeys,
        List<JourneySummary> journeys,
        List<GraphNodeReference> roles,
        List<GraphNodeReference> channelReach,
        long screenCount,
        long storyCount
) {
    public record JourneySummary(
            String journeyId,
            String title,
            String status,
            long stepCount,
            long screenCount
    ) {
    }
}
