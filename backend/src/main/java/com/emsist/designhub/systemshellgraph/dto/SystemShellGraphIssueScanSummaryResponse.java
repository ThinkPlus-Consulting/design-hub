package com.emsist.designhub.systemshellgraph.dto;

public record SystemShellGraphIssueScanSummaryResponse(
        int totalIssues,
        int newIssues,
        int existingIssues,
        int resolvedByRetest
) {
}
