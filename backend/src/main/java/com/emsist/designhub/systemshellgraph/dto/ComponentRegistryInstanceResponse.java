package com.emsist.designhub.systemshellgraph.dto;

import java.util.Map;

public record ComponentRegistryInstanceResponse(
        String objectType,
        String assetType,
        String assetName,
        String name,
        String description,
        String id,
        String status,
        String definitionId,
        String packageName,
        String packageExport,
        String packageVersion,
        String iconPackage,
        String themePackage,
        String targetObjectId,
        String targetObjectName,
        String targetObjectType,
        Map<String, Object> configuration
) {
}
