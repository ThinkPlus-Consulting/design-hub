package com.emsist.designhub.dto;

import java.util.List;

public record ApplicationSummaryResponse(
        String applicationId,
        String name,
        String applicationType,
        String status,
        long componentCount,
        long apiCount,
        long screenCount,
        long featureCount,
        long dependencyCount,
        List<String> ownerNames
) {
}
