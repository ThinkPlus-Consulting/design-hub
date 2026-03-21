package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import com.emsist.designhub.dto.ExternalSyncSourceStatusResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalSyncStatusServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    private ExternalSyncProperties externalSyncProperties;

    private ExternalSyncStatusService service;

    @BeforeEach
    void setUp() {
        externalSyncProperties = new ExternalSyncProperties();
        externalSyncProperties.getJira().setBaseUrl("https://jira.example.com");
        externalSyncProperties.getJira().setPollPath("/api/design-hub/issues");
        externalSyncProperties.getJira().setProjectKey("DH");
        externalSyncProperties.getJira().setToken("secret");
        externalSyncProperties.getJira().getWebhook().setSecret("jira-webhook-secret");
        externalSyncProperties.getJira().getPolling().setJql("project = DH ORDER BY updated DESC");
        externalSyncProperties.getJira().getPolling().setUpdatedSince("2026-03-18T00:00:00Z");
        service = new ExternalSyncStatusService(externalSyncProperties, neo4jClient);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldListSourceStatusesWithLatestJob() {
        var query = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetch = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(anyString())).thenReturn(query);
        when(query.fetch()).thenReturn(fetch);
        when(fetch.all()).thenReturn((List) List.of(
                Map.of(
                        "sourceSystem", "JIRA",
                        "jobId", "XSJ-STATUS001",
                        "status", "SKIPPED",
                        "receivedAt", "2026-03-18T13:44:43.726674Z",
                        "requestedBy", "manual-check",
                        "triggerRef", "poll/config-missing/jira",
                        "transportMode", "POLL"
                )
        ));

        List<ExternalSyncSourceStatusResponse> statuses = service.listSourceStatuses();

        assertEquals(2, statuses.size());
        ExternalSyncSourceStatusResponse jira = statuses.stream()
                .filter(status -> "JIRA".equals(status.sourceSystem()))
                .findFirst()
                .orElseThrow();
        ExternalSyncSourceStatusResponse azure = statuses.stream()
                .filter(status -> "AZURE_DEVOPS".equals(status.sourceSystem()))
                .findFirst()
                .orElseThrow();

        assertTrue(jira.enabled());
        assertTrue(jira.pollingConfigured());
        assertTrue(jira.webhookSecretConfigured());
        assertTrue(jira.baseUrlConfigured());
        assertTrue(jira.pollPathConfigured());
        assertTrue(jira.scopeConfigured());
        assertTrue(jira.filterConfigured());
        assertTrue(jira.tokenConfigured());
        assertEquals("XSJ-STATUS001", jira.latestJob().jobId());
        assertEquals("manual-check", jira.latestJob().requestedBy());
        assertFalse(azure.pollingConfigured());
        assertFalse(azure.scopeConfigured());
        assertFalse(azure.filterConfigured());
        assertNull(azure.latestJob());
    }

    @Test
    void shouldReturnEmptyForUnknownSource() {
        Optional<ExternalSyncSourceStatusResponse> status = service.getSourceStatus("LINEAR");

        assertTrue(status.isEmpty());
    }
}
