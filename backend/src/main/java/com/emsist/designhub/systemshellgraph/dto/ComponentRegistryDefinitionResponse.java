package com.emsist.designhub.systemshellgraph.dto;

public record ComponentRegistryDefinitionResponse(
        String objectType,
        String assetType,
        String assetName,
        String description,
        String id,
        String status,
        String packageName,
        String packageExport,
        String packageVersion,
        String iconPackage,
        String themePackage,
        String defaultInstanceId
) {
}
