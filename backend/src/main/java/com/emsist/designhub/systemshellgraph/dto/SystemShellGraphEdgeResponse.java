package com.emsist.designhub.systemshellgraph.dto;

public record SystemShellGraphEdgeResponse(
        String fromId,
        String relationshipType,
        String toId,
        String activeName,
        String passiveName
) {
}
