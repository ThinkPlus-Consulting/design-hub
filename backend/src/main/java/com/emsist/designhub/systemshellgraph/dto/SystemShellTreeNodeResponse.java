package com.emsist.designhub.systemshellgraph.dto;

import java.util.List;

public record SystemShellTreeNodeResponse(
        String key,
        String label,
        Boolean selectable,
        SystemShellTreeNodeDataResponse data,
        List<SystemShellTreeNodeResponse> children
) {
}
