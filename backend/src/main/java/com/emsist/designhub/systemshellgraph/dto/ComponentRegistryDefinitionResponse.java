package com.emsist.designhub.systemshellgraph.dto;

public record ComponentRegistryDefinitionResponse(
        String code,
        String objectType,
        String assetType,
        String assetName,
        String description,
        String id,
        String status,
        String implementationSourcePath,
        String defaultInstanceCode
) {
}
