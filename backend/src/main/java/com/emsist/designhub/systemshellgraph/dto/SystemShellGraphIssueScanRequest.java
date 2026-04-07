package com.emsist.designhub.systemshellgraph.dto;

import java.util.List;

public record SystemShellGraphIssueScanRequest(
        List<SystemShellGraphIssueScanItemRequest> issues
) {
}
