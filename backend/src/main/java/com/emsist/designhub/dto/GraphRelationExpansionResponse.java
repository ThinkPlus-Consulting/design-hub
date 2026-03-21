package com.emsist.designhub.dto;

import java.util.List;

public record GraphRelationExpansionResponse(
        GraphObjectSummaryResponse root,
        List<RelationEdge> outgoing,
        List<RelationEdge> incoming
) {
    public record RelationEdge(
            String relationType,
            String direction,
            GraphNodeReference node
    ) {
    }
}
