package com.emsist.designhub.systemshellgraph.dto;

import java.util.List;

public record SystemShellGraphIssueStatusUpdateRequest(
        List<String> issueIds,
        String status
) {
}
