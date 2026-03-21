package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import com.emsist.designhub.dto.ExternalSyncJobRequest;
import com.emsist.designhub.dto.ExternalSyncJobResponse;
import com.emsist.designhub.dto.ExternalSyncResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalSyncPollingServiceTest {

    @Mock
    private ExternalSyncPollingClient externalSyncPollingClient;

    @Mock
    private ExternalSyncOrchestrationService externalSyncOrchestrationService;

    private ExternalSyncProperties externalSyncProperties;

    private ExternalSyncPollingService service;

    @BeforeEach
    void setUp() {
        externalSyncProperties = new ExternalSyncProperties();
        externalSyncProperties.getScheduler().setRequestedBy("scheduler");
        externalSyncProperties.getScheduler().setFixedDelay(Duration.ofMinutes(1));
        service = new ExternalSyncPollingService(
                externalSyncPollingClient,
                externalSyncOrchestrationService,
                externalSyncProperties,
                new ObjectMapper()
        );
    }

    @Test
    void shouldPersistSkippedJobWhenPollingEndpointIsMissing() {
        ExternalSyncJobResponse skipped = new ExternalSyncJobResponse(
                "XSJ-SKIP0001",
                "JIRA",
                "POLL",
                "poll-jira-1",
                "scheduler",
                "2026-03-18T12:00:00Z",
                "poll/config-missing/jira",
                true,
                "SKIPPED",
                0,
                List.of("Polling endpoint is not configured in application.yml for source system: JIRA"),
                new ExternalSyncResult(true, "SKIPPED", 0, 0, 0, 1, 0, List.of())
        );
        when(externalSyncOrchestrationService.recordJob(
                eq("JIRA"),
                eq("POLL"),
                any(),
                eq("scheduler"),
                any(),
                eq("poll/config-missing/jira"),
                eq(true),
                eq("SKIPPED"),
                eq(0),
                eq(List.of("Polling endpoint is not configured in application.yml for source system: JIRA")),
                any(ExternalSyncResult.class)
        )).thenReturn(skipped);

        ExternalSyncJobResponse response = service.pollNow("JIRA", null, "scheduler", null);

        assertEquals("SKIPPED", response.status());
        verify(externalSyncPollingClient, never()).fetchPayload(any(), any());
    }

    @Test
    void shouldBuildJiraPollingJobFromFetchedPayload() {
        externalSyncProperties.getJira().setBaseUrl("https://jira.example.com");
        externalSyncProperties.getJira().setPollPath("/api/design-hub/issues");
        when(externalSyncPollingClient.fetchPayload(eq("JIRA"), any())).thenReturn("""
                {"dryRun":true,"issues":[{"issueId":"10001","issueKey":"DH-101","issueType":"Story","summary":"Story sync","projectKey":"DH","status":"In Progress","priority":"High","assignee":"Aisha Coleman","reporter":"Marco Lane","labels":["design-hub"],"customFields":{"storyPoints":"5"},"url":"https://jira.example.com/browse/DH-101","updatedAt":"2026-03-18T10:00:00Z","parentIssueKey":"DH-100","blocksIssueKeys":[],"relatedIssueKeys":[],"duplicateIssueKeys":[],"represents":[]}]}
                """);
        ExternalSyncJobResponse success = new ExternalSyncJobResponse(
                "XSJ-POLL0001",
                "JIRA",
                "POLL",
                "poll-jira-2",
                "system",
                "2026-03-18T12:00:00Z",
                "poll/jira/api/design-hub/issues",
                false,
                "SUCCESS",
                1,
                List.of(),
                new ExternalSyncResult(false, "SUCCESS", 1, 1, 0, 0, 0, List.of())
        );
        when(externalSyncOrchestrationService.submit(any(ExternalSyncJobRequest.class))).thenReturn(success);

        ExternalSyncJobResponse response = service.pollNow("JIRA", false, "system", null);

        assertEquals("SUCCESS", response.status());
        verify(externalSyncOrchestrationService).submit(org.mockito.ArgumentMatchers.argThat(job ->
                "JIRA".equals(job.sourceSystem())
                        && "POLL".equals(job.transportMode())
                        && "system".equals(job.requestedBy())
                        && job.jiraRequest() != null
                        && !job.jiraRequest().dryRun()
                        && job.jiraRequest().issues().size() == 1
        ));
    }

    @Test
    void shouldRunScheduledPollOncePerInterval() {
        externalSyncProperties.getJira().setBaseUrl("https://jira.example.com");
        externalSyncProperties.getJira().setPollPath("/api/design-hub/issues");
        externalSyncProperties.getJira().getPolling().setInterval(Duration.ofHours(1));
        when(externalSyncPollingClient.fetchPayload(eq("JIRA"), any())).thenReturn("""
                {"dryRun":true,"issues":[]}
                """);
        when(externalSyncOrchestrationService.submit(any(ExternalSyncJobRequest.class))).thenReturn(new ExternalSyncJobResponse(
                "XSJ-POLL0002",
                "JIRA",
                "POLL",
                "poll-jira-3",
                "scheduler",
                "2026-03-18T12:00:00Z",
                "schedule/jira",
                true,
                "SUCCESS",
                0,
                List.of("No artifacts mapped from JIRA payload"),
                new ExternalSyncResult(true, "SUCCESS", 0, 0, 0, 0, 0, List.of())
        ));

        service.runScheduledPolling();
        service.runScheduledPolling();

        verify(externalSyncOrchestrationService, times(1)).submit(any(ExternalSyncJobRequest.class));
        verify(externalSyncPollingClient, times(1)).fetchPayload(eq("JIRA"), any());
    }

    @Test
    void shouldFailFastWhenPollingIsDisabled() {
        externalSyncProperties.getAzureDevops().getPolling().setEnabled(false);

        IllegalArgumentException exception = org.junit.jupiter.api.Assertions.assertThrows(IllegalArgumentException.class,
                () -> service.pollNow("AZURE_DEVOPS", true, "system", null));

        assertTrue(exception.getMessage().contains("Transport mode POLL is disabled"));
    }
}
