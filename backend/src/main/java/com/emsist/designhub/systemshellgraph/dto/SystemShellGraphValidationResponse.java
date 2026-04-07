package com.emsist.designhub.systemshellgraph.dto;

import java.util.List;

public record SystemShellGraphValidationResponse(
        String graphScope,
        String scenarioCode,
        boolean valid,
        int issueCount,
        List<SystemShellGraphValidationIssueResponse> issues
) {
}
