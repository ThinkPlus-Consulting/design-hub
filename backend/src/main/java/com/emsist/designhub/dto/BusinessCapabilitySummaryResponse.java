package com.emsist.designhub.dto;

public record BusinessCapabilitySummaryResponse(
        String capabilityId,
        String name,
        String domainCode,
        String domainName,
        long processCount,
        long applicationCount,
        long featureCount,
        long organizationCount
) {
}
