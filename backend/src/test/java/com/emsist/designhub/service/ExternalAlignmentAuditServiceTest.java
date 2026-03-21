package com.emsist.designhub.service;

import com.emsist.designhub.dto.ExternalParityAuditResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalAlignmentAuditServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @InjectMocks
    private ExternalAlignmentAuditService service;

    @Test
    @SuppressWarnings("unchecked")
    void shouldAggregateExternalFieldParityAcrossSystems() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(
                Map.ofEntries(
                        Map.entry("externalId", "EXT-JIRA-001"),
                        Map.entry("system", "JIRA"),
                        Map.entry("title", "User sign-in and session recovery"),
                        Map.entry("projectScope", "Design Hub / Identity"),
                        Map.entry("workflowState", "In Progress"),
                        Map.entry("priority", "High"),
                        Map.entry("owner", "Aisha Coleman"),
                        Map.entry("reporter", "Marco Lane"),
                        Map.entry("labels", List.of("design-hub", "auth")),
                        Map.entry("url", "https://jira.example.com/browse/DH-101"),
                        Map.entry("syncStatus", "SYNCED"),
                        Map.entry("lastSyncedAt", "2026-03-18T09:00Z"),
                        Map.entry("hasHierarchy", true),
                        Map.entry("hasDependency", true),
                        Map.entry("hasRelated", false),
                        Map.entry("hasDuplicate", false)
                ),
                Map.ofEntries(
                        Map.entry("externalId", "EXT-JIRA-TASK-001"),
                        Map.entry("system", "JIRA"),
                        Map.entry("title", "Token audit rollout"),
                        Map.entry("projectScope", "Design Hub / Quality"),
                        Map.entry("workflowState", "In Review"),
                        Map.entry("priority", "Medium"),
                        Map.entry("owner", "Kira Holt"),
                        Map.entry("reporter", ""),
                        Map.entry("labels", List.of()),
                        Map.entry("url", "https://jira.example.com/browse/DH-118"),
                        Map.entry("syncStatus", "SYNCED"),
                        Map.entry("lastSyncedAt", ""),
                        Map.entry("hasHierarchy", true),
                        Map.entry("hasDependency", false),
                        Map.entry("hasRelated", true),
                        Map.entry("hasDuplicate", false)
                ),
                Map.ofEntries(
                        Map.entry("externalId", "EXT-AZDO-001"),
                        Map.entry("system", "AZURE_DEVOPS"),
                        Map.entry("title", "Retry banner remains visible after successful login"),
                        Map.entry("projectScope", ""),
                        Map.entry("workflowState", "Active"),
                        Map.entry("priority", ""),
                        Map.entry("owner", ""),
                        Map.entry("reporter", "Priya Shah"),
                        Map.entry("labels", List.of("bug", "login")),
                        Map.entry("url", ""),
                        Map.entry("syncStatus", "SYNCED"),
                        Map.entry("lastSyncedAt", ""),
                        Map.entry("hasHierarchy", false),
                        Map.entry("hasDependency", true),
                        Map.entry("hasRelated", false),
                        Map.entry("hasDuplicate", true)
                )
        ));

        ExternalParityAuditResponse response = service.getParityAudit();

        assertEquals(3L, response.summary().totalArtifacts());
        assertEquals(10, response.summary().trackedFields());
        assertEquals(73.4, response.summary().overallCoverageScore());
        assertEquals("AMBER", response.summary().status());
        assertEquals(2L, response.summary().hierarchyArtifacts());
        assertEquals(2L, response.summary().dependencyArtifacts());
        assertEquals(1L, response.summary().relatedArtifacts());
        assertEquals(1L, response.summary().duplicateArtifacts());

        assertEquals(2, response.systems().size());
        ExternalParityAuditResponse.SystemCoverage jira = response.systems().get(0);
        assertEquals("JIRA", jira.system());
        assertEquals(85.0, jira.coverageScore());
        assertEquals(List.of("labels", "lastSyncedAt", "reporter"), jira.weakestFields());

        ExternalParityAuditResponse.SystemCoverage azureDevops = response.systems().get(1);
        assertEquals("AZURE_DEVOPS", azureDevops.system());
        assertEquals(50.0, azureDevops.coverageScore());
        assertEquals(List.of("lastSyncedAt", "owner", "priority"), azureDevops.weakestFields());

        ExternalParityAuditResponse.FieldCoverage lastSyncedAt = response.fields().stream()
                .filter(field -> "lastSyncedAt".equals(field.field()))
                .findFirst()
                .orElseThrow();
        assertEquals(1L, lastSyncedAt.populatedArtifacts());
        assertEquals(2L, lastSyncedAt.missingArtifacts());
        assertEquals(33.3, lastSyncedAt.coverageScore());
        assertEquals(List.of("EXT-JIRA-TASK-001", "EXT-AZDO-001"), lastSyncedAt.exampleMissingArtifacts());

        assertTrue(response.fields().stream()
                .anyMatch(field -> "projectScope".equals(field.field()) && field.coverageScore() == 66.7));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (ea:ExternalArtifact)")
                        && cypher.contains("ea.projectScope AS projectScope")
                        && cypher.contains("hasHierarchy")
        ));
    }
}
