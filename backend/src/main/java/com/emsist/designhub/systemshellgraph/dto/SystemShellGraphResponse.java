package com.emsist.designhub.systemshellgraph.dto;

import java.util.List;

public record SystemShellGraphResponse(
        String graphScope,
        String scenarioCode,
        String scenarioName,
        List<SystemShellGraphNodeResponse> nodes,
        List<SystemShellGraphEdgeResponse> relationships
) {
}
