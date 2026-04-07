package com.emsist.designhub.systemshellgraph.dto;

import java.util.Map;

public record ComponentRegistryInstanceResponse(
        String code,
        String objectType,
        String assetType,
        String assetName,
        String name,
        String description,
        String id,
        String status,
        String definitionCode,
        String implementationSourcePath,
        String targetObjectCode,
        String targetObjectName,
        String targetObjectType,
        Map<String, Object> configuration
) {
}
