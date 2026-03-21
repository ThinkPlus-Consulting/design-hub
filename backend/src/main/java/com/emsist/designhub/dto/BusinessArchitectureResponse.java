package com.emsist.designhub.dto;

import java.util.List;

public record BusinessArchitectureResponse(
        String capabilityId,
        String name,
        String description,
        String status,
        String domainCode,
        String domainName,
        List<GraphNodeReference> processes,
        List<GraphNodeReference> applications,
        List<GraphNodeReference> features,
        List<OrganizationSummary> organizations
) {
    public record OrganizationSummary(
            String orgId,
            String name,
            String organizationType,
            String status
    ) {
    }
}
