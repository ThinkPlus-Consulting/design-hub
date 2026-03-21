package com.emsist.designhub.dto;

import java.util.List;
import java.util.Map;

public record JiraSyncRequest(
        boolean dryRun,
        List<Issue> issues
) {
    public record Issue(
            String issueId,
            String issueKey,
            String issueType,
            String summary,
            String projectKey,
            String status,
            String priority,
            String assignee,
            String reporter,
            List<String> labels,
            Map<String, String> customFields,
            String url,
            String updatedAt,
            String parentIssueKey,
            List<String> blocksIssueKeys,
            List<String> relatedIssueKeys,
            List<String> duplicateIssueKeys,
            List<Reference> represents
    ) {
    }

    public record Reference(
            String nodeType,
            String id
    ) {
    }
}
