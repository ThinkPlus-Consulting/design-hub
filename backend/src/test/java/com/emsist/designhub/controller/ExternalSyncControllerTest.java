package com.emsist.designhub.controller;

import com.emsist.designhub.dto.AzureDevOpsSyncRequest;
import com.emsist.designhub.dto.ExternalSyncJobRequest;
import com.emsist.designhub.dto.ExternalSyncJobResponse;
import com.emsist.designhub.dto.ExternalSyncRequest;
import com.emsist.designhub.dto.ExternalSyncResult;
import com.emsist.designhub.dto.ExternalSyncSourceStatusResponse;
import com.emsist.designhub.dto.JiraSyncRequest;
import com.emsist.designhub.service.AzureDevOpsSyncMapperService;
import com.emsist.designhub.service.ExternalArtifactSyncService;
import com.emsist.designhub.service.ExternalSyncOrchestrationService;
import com.emsist.designhub.service.ExternalSyncPollingService;
import com.emsist.designhub.service.ExternalSyncStatusService;
import com.emsist.designhub.service.JiraSyncMapperService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalSyncControllerTest {

    @Mock
    private AzureDevOpsSyncMapperService azureDevOpsSyncMapperService;

    @Mock
    private ExternalArtifactSyncService externalArtifactSyncService;

    @Mock
    private ExternalSyncOrchestrationService externalSyncOrchestrationService;

    @Mock
    private ExternalSyncPollingService externalSyncPollingService;

    @Mock
    private ExternalSyncStatusService externalSyncStatusService;

    @Mock
    private JiraSyncMapperService jiraSyncMapperService;

    @InjectMocks
    private ExternalSyncController controller;

    @Test
    void shouldRejectEmptyArtifactSyncPayload() {
        var response = controller.syncArtifacts(new ExternalSyncRequest(false, List.of()));

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertEquals("FAILED", response.getBody().result());
        verify(externalArtifactSyncService, never()).sync(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void shouldReturnSyncSummaryForExternalArtifacts() {
        ExternalSyncRequest request = new ExternalSyncRequest(
                false,
                List.of(new ExternalSyncRequest.Artifact(
                        "EXT-JIRA-001",
                        "JIRA",
                        "STORY",
                        "DH-101",
                        "Story sync",
                        "Design Hub / Identity",
                        "In Progress",
                        "High",
                        "Aisha Coleman",
                        "Marco Lane",
                        List.of("design-hub"),
                        Map.of("area", "Identity", "iteration", "Sprint 24"),
                        "https://jira.example.com/browse/DH-101",
                        "SYNCED",
                        "2026-03-18T10:00:00Z",
                        "DEFINED",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(new ExternalSyncRequest.Representation("story", "US-AUTH-001"))
                ))
        );

        when(externalArtifactSyncService.sync(request)).thenReturn(new ExternalSyncResult(
                false,
                "SUCCESS",
                1,
                1,
                0,
                0,
                0,
                List.of(new ExternalSyncResult.ItemResult(
                        "EXT-JIRA-001",
                        "CREATE",
                        "sha256:abc",
                        List.of()
                ))
        ));

        var response = controller.syncArtifacts(request);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("SUCCESS", response.getBody().result());
        assertEquals(1, response.getBody().createdCount());
    }

    @Test
    void shouldMapAndSyncAzureDevOpsWorkItems() {
        AzureDevOpsSyncRequest request = new AzureDevOpsSyncRequest(
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
                        List.of(240L),
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
                Map.of("iterationPath", "Sprint 24"),
                "https://dev.azure.com/example/designhub/_workitems/edit/245",
                "SYNCED",
                "2026-03-18T10:00:00Z",
                "DEFINED",
                List.of("EXT-AZDO-240"),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        )));
        when(azureDevOpsSyncMapperService.toExternalSyncRequest(request)).thenReturn(mapped);
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

        var response = controller.syncAzureDevOpsWorkItems(request);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        verify(azureDevOpsSyncMapperService).toExternalSyncRequest(request);
        verify(externalArtifactSyncService).sync(mapped);
    }

    @Test
    void shouldMapAndSyncJiraIssues() {
        JiraSyncRequest request = new JiraSyncRequest(
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
                        List.of("design-hub"),
                        Map.of("storyPoints", "5"),
                        "https://jira.example.com/browse/DH-101",
                        "2026-03-18T10:00:00Z",
                        "DH-100",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of()
                ))
        );
        ExternalSyncRequest mapped = new ExternalSyncRequest(false, List.of(new ExternalSyncRequest.Artifact(
                "EXT-JIRA-DH-101",
                "JIRA",
                "Story",
                "DH-101",
                "User sign-in and session recovery",
                "DH",
                "In Progress",
                "High",
                "Aisha Coleman",
                "Marco Lane",
                List.of("design-hub"),
                Map.of("storyPoints", "5"),
                "https://jira.example.com/browse/DH-101",
                "SYNCED",
                "2026-03-18T10:00:00Z",
                "DEFINED",
                List.of(),
                List.of(),
                List.of(),
                List.of(),
                List.of()
        )));
        when(jiraSyncMapperService.toExternalSyncRequest(request)).thenReturn(mapped);
        when(externalArtifactSyncService.sync(mapped)).thenReturn(new ExternalSyncResult(
                false,
                "SUCCESS",
                1,
                1,
                0,
                0,
                0,
                List.of()
        ));

        var response = controller.syncJiraIssues(request);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        verify(jiraSyncMapperService).toExternalSyncRequest(request);
        verify(externalArtifactSyncService).sync(mapped);
    }

    @Test
    void shouldSubmitExternalSyncJob() {
        ExternalSyncJobRequest request = new ExternalSyncJobRequest(
                "JIRA",
                "WEBHOOK",
                "corr-101",
                "ci-bot",
                "2026-03-18T12:00:00Z",
                "jira-webhook-101",
                null,
                new JiraSyncRequest(false, List.of())
        );
        ExternalSyncJobResponse responseBody = new ExternalSyncJobResponse(
                "XSJ-12345678",
                "JIRA",
                "WEBHOOK",
                "corr-101",
                "ci-bot",
                "2026-03-18T12:00:00Z",
                "jira-webhook-101",
                false,
                "SUCCESS",
                1,
                List.of(),
                new ExternalSyncResult(false, "SUCCESS", 1, 1, 0, 0, 0, List.of())
        );
        when(externalSyncOrchestrationService.submit(request)).thenReturn(responseBody);

        var response = controller.submitSyncJob(request);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("XSJ-12345678", response.getBody().jobId());
        verify(externalSyncOrchestrationService).submit(request);
    }

    @Test
    void shouldReturnValidationFailureForInvalidSyncJob() {
        ExternalSyncJobRequest request = new ExternalSyncJobRequest(
                "JIRA",
                "POLL",
                null,
                null,
                "2026-03-18T12:00:00Z",
                null,
                new AzureDevOpsSyncRequest(false, List.of()),
                null
        );
        when(externalSyncOrchestrationService.submit(request))
                .thenThrow(new IllegalArgumentException("Source system does not match supplied payload"));

        var response = controller.submitSyncJob(request);

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertEquals("FAILED", response.getBody().status());
        assertEquals(List.of("Source system does not match supplied payload"), response.getBody().warnings());
    }

    @Test
    void shouldListSubmittedSyncJobs() {
        ExternalSyncJobResponse job = new ExternalSyncJobResponse(
                "XSJ-12345678",
                "JIRA",
                "WEBHOOK",
                "corr-101",
                "ci-bot",
                "2026-03-18T12:00:00Z",
                "jira-webhook-101",
                false,
                "SUCCESS",
                1,
                List.of(),
                null
        );
        when(externalSyncOrchestrationService.list(5, null)).thenReturn(List.of(job));

        var response = controller.listSyncJobs(5, null);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("XSJ-12345678", response.getBody().getFirst().jobId());
    }

    @Test
    void shouldListSubmittedSyncJobsBySource() {
        ExternalSyncJobResponse job = new ExternalSyncJobResponse(
                "XSJ-87654321",
                "AZURE_DEVOPS",
                "POLL",
                "corr-202",
                "ui-verification",
                "2026-03-18T12:30:00Z",
                "ui/verification/azure_devops",
                true,
                "SKIPPED",
                0,
                List.of("Polling endpoint is not configured"),
                null
        );
        when(externalSyncOrchestrationService.list(5, "AZURE_DEVOPS")).thenReturn(List.of(job));

        var response = controller.listSyncJobs(5, "AZURE_DEVOPS");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("AZURE_DEVOPS", response.getBody().getFirst().sourceSystem());
        verify(externalSyncOrchestrationService).list(5, "AZURE_DEVOPS");
    }

    @Test
    void shouldTriggerPollingJob() {
        ExternalSyncJobResponse job = new ExternalSyncJobResponse(
                "XSJ-POLL0001",
                "JIRA",
                "POLL",
                "poll-jira-1",
                "scheduler",
                "2026-03-18T12:00:00Z",
                "schedule/jira",
                true,
                "SUCCESS",
                0,
                List.of("No artifacts mapped from JIRA payload"),
                new ExternalSyncResult(true, "SUCCESS", 0, 0, 0, 0, 0, List.of())
        );
        when(externalSyncPollingService.pollNow("JIRA", true, "scheduler", "schedule/jira")).thenReturn(job);

        var response = controller.pollSourceSystem("JIRA", true, "scheduler", "schedule/jira");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("XSJ-POLL0001", response.getBody().jobId());
    }

    @Test
    void shouldReturnValidationFailureForInvalidPollingRequest() {
        when(externalSyncPollingService.pollNow("JIRA", null, null, null))
                .thenThrow(new IllegalArgumentException("Polling endpoint is not configured"));

        var response = controller.pollSourceSystem("JIRA", null, null, null);

        assertEquals(HttpStatusCode.valueOf(422), response.getStatusCode());
        assertEquals("FAILED", response.getBody().status());
        assertEquals(List.of("Polling endpoint is not configured"), response.getBody().warnings());
    }

    @Test
    void shouldListExternalSyncSourceStatuses() {
        ExternalSyncSourceStatusResponse status = new ExternalSyncSourceStatusResponse(
                "JIRA",
                true,
                true,
                false,
                true,
                false,
                false,
                false,
                false,
                false,
                false,
                true,
                true,
                null
        );
        when(externalSyncStatusService.listSourceStatuses()).thenReturn(List.of(status));

        var response = controller.listSourceStatuses();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("JIRA", response.getBody().getFirst().sourceSystem());
    }

    @Test
    void shouldReturnNotFoundForUnknownSourceStatus() {
        when(externalSyncStatusService.getSourceStatus("LINEAR")).thenReturn(java.util.Optional.empty());

        var response = controller.getSourceStatus("LINEAR");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    void shouldReturnNotFoundForUnknownSyncJob() {
        when(externalSyncOrchestrationService.findById("XSJ-404")).thenReturn(java.util.Optional.empty());

        var response = controller.getSyncJob("XSJ-404");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
}
