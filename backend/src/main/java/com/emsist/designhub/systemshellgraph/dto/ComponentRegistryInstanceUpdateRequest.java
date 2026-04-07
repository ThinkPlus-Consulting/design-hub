package com.emsist.designhub.systemshellgraph.dto;

import java.util.Map;

public record ComponentRegistryInstanceUpdateRequest(
        String name,
        String description,
        String status,
        String targetObjectCode,
        Map<String, Object> configuration
) {
}
