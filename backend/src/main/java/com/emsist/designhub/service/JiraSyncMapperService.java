package com.emsist.designhub.service;

import com.emsist.designhub.dto.ExternalSyncRequest;
import com.emsist.designhub.dto.JiraSyncRequest;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class JiraSyncMapperService {

    public ExternalSyncRequest toExternalSyncRequest(JiraSyncRequest request) {
        List<ExternalSyncRequest.Artifact> artifacts = request == null || request.issues() == null
                ? List.of()
                : request.issues().stream()
                .map(this::toArtifact)
                .toList();
        return new ExternalSyncRequest(request != null && request.dryRun(), artifacts);
    }

    private ExternalSyncRequest.Artifact toArtifact(JiraSyncRequest.Issue issue) {
        String key = resolveKey(issue);
        return new ExternalSyncRequest.Artifact(
                key == null ? null : "EXT-JIRA-" + key,
                "JIRA",
                issue == null ? null : issue.issueType(),
                key,
                issue == null ? null : issue.summary(),
                issue == null ? null : issue.projectKey(),
                issue == null ? null : issue.status(),
                issue == null ? null : issue.priority(),
                issue == null ? null : issue.assignee(),
                issue == null ? null : issue.reporter(),
                issue == null ? null : issue.labels(),
                mergeCustomFields(issue),
                issue == null ? null : issue.url(),
                "SYNCED",
                issue == null ? null : issue.updatedAt(),
                "DEFINED",
                key == null || issue == null || issue.parentIssueKey() == null
                        ? List.of()
                        : List.of("EXT-JIRA-" + issue.parentIssueKey()),
                mapKeys(issue == null ? null : issue.blocksIssueKeys()),
                mapKeys(issue == null ? null : issue.relatedIssueKeys()),
                mapKeys(issue == null ? null : issue.duplicateIssueKeys()),
                issue == null || issue.represents() == null
                        ? List.of()
                        : issue.represents().stream()
                        .map(reference -> new ExternalSyncRequest.Representation(reference.nodeType(), reference.id()))
                        .toList()
        );
    }

    private Map<String, String> mergeCustomFields(JiraSyncRequest.Issue issue) {
        if (issue == null) {
            return Map.of();
        }
        LinkedHashMap<String, String> customFields = new LinkedHashMap<>();
        if (issue.customFields() != null) {
            customFields.putAll(issue.customFields());
        }
        putIfPresent(customFields, "issueId", issue.issueId());
        putIfPresent(customFields, "projectKey", issue.projectKey());
        return Map.copyOf(customFields);
    }

    private List<String> mapKeys(List<String> keys) {
        if (keys == null) {
            return List.of();
        }
        return keys.stream()
                .map(key -> key == null || key.isBlank() ? null : "EXT-JIRA-" + key)
                .filter(java.util.Objects::nonNull)
                .toList();
    }

    private String resolveKey(JiraSyncRequest.Issue issue) {
        if (issue == null) {
            return null;
        }
        if (issue.issueKey() != null && !issue.issueKey().isBlank()) {
            return issue.issueKey();
        }
        return issue.issueId();
    }

    private void putIfPresent(Map<String, String> target, String key, String value) {
        if (value != null && !value.isBlank()) {
            target.put(key, value);
        }
    }
}
