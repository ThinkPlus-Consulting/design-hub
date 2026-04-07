package com.emsist.designhub.systemshellgraph.dto;

import java.util.List;

public record SystemShellGraphValidationIssueResponse(
        String code,
        String family,
        List<String> messages
) {
}
