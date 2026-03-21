package com.emsist.designhub.service;

import com.emsist.designhub.dto.AzureDevOpsSyncRequest;
import com.emsist.designhub.dto.ExternalSyncRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AzureDevOpsSyncMapperService {

    public ExternalSyncRequest toExternalSyncRequest(AzureDevOpsSyncRequest request) {
        List<ExternalSyncRequest.Artifact> artifacts = request == null || request.workItems() == null
                ? List.of()
                : request.workItems().stream()
                .map(this::toArtifact)
                .toList();
        return new ExternalSyncRequest(request != null && request.dryRun(), artifacts);
    }

    private ExternalSyncRequest.Artifact toArtifact(AzureDevOpsSyncRequest.WorkItem workItem) {
        String externalId = toExternalId(workItem == null ? null : workItem.workItemId());
        return new ExternalSyncRequest.Artifact(
                externalId,
                "AZURE_DEVOPS",
                workItem == null ? null : workItem.workItemType(),
                toKey(workItem == null ? null : workItem.workItemId()),
                workItem == null ? null : workItem.title(),
                workItem == null ? null : workItem.areaPath(),
                workItem == null ? null : workItem.state(),
                workItem == null ? null : workItem.priority(),
                workItem == null ? null : workItem.assignedTo(),
                workItem == null ? null : workItem.createdBy(),
                workItem == null ? null : workItem.tags(),
                mergeCustomFields(workItem),
                workItem == null ? null : workItem.url(),
                "SYNCED",
                workItem == null ? null : workItem.changedDate(),
                "DEFINED",
                mapIds(workItem == null ? null : workItem.parentWorkItemIds()),
                mapIds(workItem == null ? null : workItem.predecessorWorkItemIds()),
                mapIds(workItem == null ? null : workItem.relatedWorkItemIds()),
                mapIds(workItem == null ? null : workItem.duplicateWorkItemIds()),
                workItem == null || workItem.represents() == null
                        ? List.of()
                        : workItem.represents().stream()
                        .map(reference -> new ExternalSyncRequest.Representation(reference.nodeType(), reference.id()))
                        .toList()
        );
    }

    private Map<String, String> mergeCustomFields(AzureDevOpsSyncRequest.WorkItem workItem) {
        if (workItem == null) {
            return Map.of();
        }
        LinkedHashMap<String, String> customFields = new LinkedHashMap<>();
        if (workItem.customFields() != null) {
            customFields.putAll(workItem.customFields());
        }
        putIfPresent(customFields, "areaPath", workItem.areaPath());
        putIfPresent(customFields, "iterationPath", workItem.iterationPath());
        putIfPresent(customFields, "workItemId", workItem.workItemId() == null ? null : String.valueOf(workItem.workItemId()));
        return Map.copyOf(customFields);
    }

    private List<String> mapIds(List<Long> ids) {
        if (ids == null) {
            return List.of();
        }
        return ids.stream()
                .map(this::toExternalId)
                .toList();
    }

    private String toExternalId(Long workItemId) {
        return workItemId == null ? null : "EXT-AZDO-" + workItemId;
    }

    private String toKey(Long workItemId) {
        return workItemId == null ? null : "AB#" + workItemId;
    }

    private void putIfPresent(Map<String, String> target, String key, String value) {
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }
}
