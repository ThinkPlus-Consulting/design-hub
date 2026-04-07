package com.emsist.designhub.systemshellgraph.dto;

public record SystemShellGraphIssueScanItemRequest(
        String targetObjectId,
        String targetName,
        String source,
        String category,
        String rule,
        String message,
        String severity
) {
}
