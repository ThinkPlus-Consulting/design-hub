package com.emsist.designhub.dto;

import java.util.List;
import java.util.Map;

public record AzureDevOpsSyncRequest(
        boolean dryRun,
        List<WorkItem> workItems
) {
    public record WorkItem(
            Long workItemId,
            String workItemType,
            String title,
            String areaPath,
            String iterationPath,
            String state,
            String priority,
            String assignedTo,
            String createdBy,
            List<String> tags,
            Map<String, String> customFields,
            String url,
            String changedDate,
            List<Long> parentWorkItemIds,
            List<Long> predecessorWorkItemIds,
            List<Long> relatedWorkItemIds,
            List<Long> duplicateWorkItemIds,
            List<Reference> represents
    ) {
    }

    public record Reference(
            String nodeType,
            String id
    ) {
    }
}
