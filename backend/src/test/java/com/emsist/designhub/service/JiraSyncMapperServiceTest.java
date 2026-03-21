package com.emsist.designhub.service;

import com.emsist.designhub.dto.ExternalSyncRequest;
import com.emsist.designhub.dto.JiraSyncRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JiraSyncMapperServiceTest {

    private final JiraSyncMapperService service = new JiraSyncMapperService();

    @Test
    void shouldMapJiraIssueToGenericExternalSyncRequest() {
        ExternalSyncRequest request = service.toExternalSyncRequest(new JiraSyncRequest(
                false,
                List.of(new JiraSyncRequest.Issue(
                        "10001",
                        "DH-101",
                        "Story",
                        "User sign-in and session recovery",
                        "DH",
                        "In Progress",
                        "High",
                        "Aisha Coleman",
                        "Marco Lane",
                        List.of("design-hub", "auth"),
                        Map.of("storyPoints", "5", "team", "Identity"),
                        "https://jira.example.com/browse/DH-101",
                        "2026-03-18T10:00:00Z",
                        "DH-100",
                        List.of("DH-245"),
                        List.of("DH-240"),
                        List.of("DH-120"),
                        List.of(new JiraSyncRequest.Reference("story", "US-AUTH-001"))
                ))
        ));

        assertEquals(false, request.dryRun());
        assertEquals(1, request.artifacts().size());

        ExternalSyncRequest.Artifact artifact = request.artifacts().getFirst();
        assertEquals("EXT-JIRA-DH-101", artifact.externalId());
        assertEquals("JIRA", artifact.system());
        assertEquals("Story", artifact.externalType());
        assertEquals("DH-101", artifact.key());
        assertEquals("DH", artifact.projectScope());
        assertEquals("In Progress", artifact.workflowState());
        assertEquals("10001", artifact.customFields().get("issueId"));
        assertEquals(List.of("EXT-JIRA-DH-100"), artifact.parentExternalIds());
        assertEquals(List.of("EXT-JIRA-DH-245"), artifact.dependencyExternalIds());
        assertEquals(List.of("EXT-JIRA-DH-240"), artifact.relatedExternalIds());
        assertEquals(List.of("EXT-JIRA-DH-120"), artifact.duplicateExternalIds());
        assertEquals("story", artifact.represents().getFirst().nodeType());
    }
}
