package com.emsist.designhub.dto;

public record GraphObjectSummaryResponse(
        String id,
        String nodeType,
        String displayName,
        String status,
        String module,
        String domain,
        String routePath,
        long relationCount
) {
}
