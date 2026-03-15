package com.emsist.designhub.dto;

public record RoleResponse(
        String roleKey,
        String displayName,
        String roleGroup,
        Integer sortOrder,
        long screenCount,
        long touchpointCount,
        long interactionCount,
        long journeyCount
) {
}
