package com.emsist.designhub.dto;

import java.util.List;

public record ObjectDefinitionDetailResponse(
        String type,
        String label,
        String displayName,
        String category,
        String tier,
        boolean benchmarkable,
        String purpose,
        String implementationStatus,
        List<String> aliases,
        List<AttributeDefinitionResponse> attributes,
        List<RelationshipDefinitionResponse> relationships,
        long instanceCount,
        List<GraphObjectSummaryResponse> instances
) {
    public record AttributeDefinitionResponse(
            String name,
            String type,
            boolean required,
            String description,
            String constraints
    ) {
    }

    public record RelationshipDefinitionResponse(
            String name,
            String direction,
            String target,
            String cardinality,
            boolean required,
            String severity,
            String implementation
    ) {
    }
}
