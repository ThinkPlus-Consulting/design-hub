package com.emsist.designhub.systemshellgraph.dto;

public record SystemShellTreeNodeDataResponse(
        String kind,
        String label,
        String family,
        String layer,
        String objectId,
        String guid,
        String domTargetGuid,
        String assetType
) {
}
