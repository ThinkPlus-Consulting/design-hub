package com.emsist.designhub.dto;

import java.util.List;

public record JourneyTraversalResponse(
        String journeyId,
        String title,
        String goalStatement,
        String status,
        GraphNodeReference persona,
        List<StepSummary> steps
) {
    public record StepSummary(
            String stepId,
            String label,
            int orderIndex,
            GraphNodeReference screen,
            GraphNodeReference touchpoint,
            GraphNodeReference interaction
    ) {
    }
}
