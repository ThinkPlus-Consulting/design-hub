package com.emsist.designhub.systemshellgraph.dto;

import java.util.List;

public record SystemShellGraphValidationIssueResponse(
        String objectId,
        String family,
        List<String> messages
) {
}
