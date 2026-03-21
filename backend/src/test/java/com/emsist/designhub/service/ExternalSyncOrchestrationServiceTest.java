package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import com.emsist.designhub.dto.AzureDevOpsSyncRequest;
import com.emsist.designhub.dto.ExternalSyncJobRequest;
import com.emsist.designhub.dto.ExternalSyncRequest;
import com.emsist.designhub.dto.ExternalSyncResult;
import com.emsist.designhub.dto.JiraSyncRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalSyncOrchestrationServiceTest {

    @Mock
    private AzureDevOpsSyncMapperService azureDevOpsSyncMapperService;

    @Mock
    private ExternalArtifactSyncService externalArtifactSyncService;

    private final ExternalSyncProperties externalSyncProperties = new ExternalSyncProperties();

    @Mock
    private JiraSyncMapperService jiraSyncMapperService;

    @Mock
    private Neo4jClient neo4jClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private ExternalSyncOrchestrationService service;

    @BeforeEach
    void setUp() {
        service = new ExternalSyncOrchestrationService(
                azureDevOpsSyncMapperService,
                externalSyncProperties,
                externalArtifactSyncService,
                jiraSyncMapperService,
                neo4jClient,
                objectMapper
        );
    }

    @Test
    void shouldSubmitAzureDevOpsSyncJob() {
        stubWriteQuery();

        AzureDevOpsSyncRequest payload = new AzureDevOpsSyncRequest(
                true,
                List.of(new AzureDevOpsSyncRequest.WorkItem(
                        245L,
                        "Bug",
                        "Retry banner remains visible after login",
                        "Design Hub\\Identity",
                        "Sprint 24",
                        "Active",
                        "2",
                        "Jordan Rivera",
                        "Aisha Coleman",
                        List.of("design-hub", "bug"),
                        Map.of("storyPoints", "3"),
                        "https://dev.azure.com/example/designhub/_workitems/edit/245",
                        "2026-03-18T10:00:00Z",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of()
                ))
        );
        ExternalSyncRequest mapped = new ExternalSyncRequest(true, List.of(new ExternalSyncRequest.Artifact(
                "EXT-AZDO-245",
                "AZURE_DEVOPS",
                "Bug",
                "AB#245",
                "Retry banner remains visible after login",
                "Design Hub\\Identity",
                "Active",
                "2",
                "Jordan Rivera",
                "Aisha Coleman",
                List.of("design-hub", "bug"),
                Map.of("storyPoints", "3"),
                "https://dev.azure.com/example/designhub/_workitems/edit/245",
                "SYNCED",
                "2026-03-18T10:00:00Z",
                "DEFINED",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        )));
        when(azureDevOpsSyncMapperService.toExternalSyncRequest(payload)).thenReturn(mapped);
        when(externalArtifactSyncService.sync(mapped)).thenReturn(new ExternalSyncResult(
                true,
                "SUCCESS",
                1,
                1,
                0,
                0,
                0,
                List.of()
        ));

        var response = service.submit(new ExternalSyncJobRequest(
                "AZURE_DEVOPS",
                "WEBHOOK",
                "corr-245",
                "ci-bot",
                "2026-03-18T12:00:00Z",
                "azdo-webhook-245",
                payload,
                null
        ));

        assertTrue(response.jobId().startsWith("XSJ-"));
        assertEquals("AZURE_DEVOPS", response.sourceSystem());
        assertEquals("WEBHOOK", response.transportMode());
        assertEquals("SUCCESS", response.status());
        assertEquals(1, response.artifactCount());
        assertTrue(response.warnings().isEmpty());
        verify(azureDevOpsSyncMapperService).toExternalSyncRequest(payload);
        verify(externalArtifactSyncService).sync(mapped);
        verify(neo4jClient).query(org.mockito.ArgumentMatchers.contains("MERGE (job:ExternalSyncJob {jobId: $jobId})"));
    }

    @Test
    void shouldTrackWarningsForMissingCorrelationData() {
        stubWriteQuery();

        JiraSyncRequest payload = new JiraSyncRequest(false, List.of());
        ExternalSyncRequest mapped = new ExternalSyncRequest(false, List.of());
        when(jiraSyncMapperService.toExternalSyncRequest(payload)).thenReturn(mapped);
        when(externalArtifactSyncService.sync(mapped)).thenReturn(new ExternalSyncResult(
                false,
                "FAILED",
                0,
                0,
                0,
                0,
                0,
                List.of()
        ));

        var response = service.submit(new ExternalSyncJobRequest(
                "JIRA",
                null,
                null,
                null,
                null,
                null,
                null,
                payload
        ));

        assertEquals("MANUAL", response.transportMode());
        assertEquals("FAILED", response.status());
        assertEquals(List.of(
                "Missing correlationId; generated job will rely on jobId only",
                "Missing triggerRef; poll/webhook provenance is not captured",
                "No artifacts mapped from JIRA payload"
        ), response.warnings());
    }

    @Test
    void shouldReadPersistedJobsByIdAndList() {
        var findQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var listQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var filteredListQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var findFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var listFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var filteredListFetch = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(org.mockito.ArgumentMatchers.contains("MATCH (job:ExternalSyncJob {jobId: $jobId})"))).thenReturn(findQuery);
        when(neo4jClient.query(org.mockito.ArgumentMatchers.contains("MATCH (job:ExternalSyncJob)\n"))).thenReturn(listQuery);
        when(neo4jClient.query(org.mockito.ArgumentMatchers.contains("MATCH (job:ExternalSyncJob {sourceSystem: $sourceSystem})"))).thenReturn(filteredListQuery);
        when(findQuery.bind(any()).to(anyString())).thenReturn(findQuery);
        when(listQuery.bind(any()).to(anyString())).thenReturn(listQuery);
        when(filteredListQuery.bind(any()).to(anyString())).thenReturn(filteredListQuery);
        when(findQuery.fetch()).thenReturn(findFetch);
        when(listQuery.fetch()).thenReturn(listFetch);
        when(filteredListQuery.fetch()).thenReturn(filteredListFetch);
        when(findFetch.first()).thenReturn((Optional) Optional.of(jobRow("XSJ-11111111", "SUCCESS")));
        when(listFetch.all()).thenReturn((List) List.of(jobRow("XSJ-22222222", "SKIPPED")));
        when(filteredListFetch.all()).thenReturn((List) List.of(jobRow("XSJ-33333333", "SUCCESS", "AZURE_DEVOPS")));

        var found = service.findById("XSJ-11111111").orElseThrow();
        var listed = service.list(10);
        var filtered = service.list(10, "azure_devops");

        assertEquals("XSJ-11111111", found.jobId());
        assertEquals("SUCCESS", found.status());
        assertEquals("XSJ-22222222", listed.getFirst().jobId());
        assertEquals("SKIPPED", listed.getFirst().status());
        assertEquals(List.of("poll not configured"), listed.getFirst().warnings());
        assertEquals("XSJ-33333333", filtered.getFirst().jobId());
        assertEquals("AZURE_DEVOPS", filtered.getFirst().sourceSystem());
    }

    @Test
    void shouldRejectDisabledPollingTransport() {
        externalSyncProperties.getJira().getPolling().setEnabled(false);

        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.submit(new ExternalSyncJobRequest(
                        "JIRA",
                        "POLL",
                        "corr",
                        "ci-bot",
                        "2026-03-18T12:00:00Z",
                        "schedule/15m",
                        null,
                        new JiraSyncRequest(false, List.of())
                ))
        );

        assertEquals("Transport mode POLL is disabled for source system: JIRA", exception.getMessage());
        verify(externalArtifactSyncService, never()).sync(any());
    }

    @Test
    void shouldRejectMismatchedSourceAndPayload() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () ->
                service.submit(new ExternalSyncJobRequest(
                        "JIRA",
                        "POLL",
                        "corr",
                        "ci-bot",
                        "2026-03-18T12:00:00Z",
                        "schedule/15m",
                        new AzureDevOpsSyncRequest(false, List.of()),
                        null
                ))
        );

        assertEquals("Source system does not match supplied payload", exception.getMessage());
    }

    @SuppressWarnings("unchecked")
    private void stubWriteQuery() {
        var writeQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(org.mockito.ArgumentMatchers.contains("MERGE (job:ExternalSyncJob {jobId: $jobId})"))).thenReturn(writeQuery);
        when(writeQuery.bind(any()).to(anyString())).thenReturn(writeQuery);
        when(writeQuery.run()).thenReturn(null);
    }

    private Map<String, Object> jobRow(String jobId, String status) {
        return jobRow(jobId, status, "JIRA");
    }

    private Map<String, Object> jobRow(String jobId, String status, String sourceSystem) {
        try {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("jobId", jobId);
            row.put("sourceSystem", sourceSystem);
            row.put("transportMode", "POLL");
            row.put("correlationId", "corr-101");
            row.put("requestedBy", "scheduler");
            row.put("receivedAt", "2026-03-18T12:00:00Z");
            row.put("triggerRef", "schedule/jira");
            row.put("dryRun", true);
            row.put("status", status);
            row.put("artifactCount", 0);
            row.put("warningsJson", objectMapper.writeValueAsString(List.of("poll not configured")));
            row.put("resultJson", objectMapper.writeValueAsString(new ExternalSyncResult(
                    true,
                    status,
                    0,
                    0,
                    0,
                    1,
                    0,
                    List.of()
            )));
            return row;
        } catch (Exception exception) {
            throw new IllegalStateException(exception);
        }
    }
}
