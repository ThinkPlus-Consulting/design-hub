package com.emsist.designhub.dto;

public record DataArchitectureObjectSummaryResponse(
        String objectId,
        String name,
        String domain,
        String sensitivity,
        String status,
        long mappedEntityCount,
        long flowCount,
        long apiCount,
        long screenCount
) {
}
