package com.emsist.designhub.dto;

public record ObjectDefinitionSummaryResponse(
        String type,
        String label,
        String displayName,
        String category,
        String tier,
        boolean benchmarkable,
        long instanceCount,
        int relationshipTypeCount
) {
}
