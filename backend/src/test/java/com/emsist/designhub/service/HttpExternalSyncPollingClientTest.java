package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class HttpExternalSyncPollingClientTest {

    @Mock
    private HttpClient httpClient;

    @Mock
    private HttpResponse<String> httpResponse;

    @Test
    void shouldBuildJiraRequestWithConfiguredFilters() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"dryRun\":true,\"issues\":[]}");

        ExternalSyncProperties.SourceProperties sourceProperties = new ExternalSyncProperties.SourceProperties();
        sourceProperties.setBaseUrl("https://jira.example.com");
        sourceProperties.setPollPath("/api/design-hub/issues");
        sourceProperties.setProjectKey("DH");
        sourceProperties.setToken("jira-token");
        sourceProperties.getPolling().setJql("project = DH ORDER BY updated DESC");
        sourceProperties.getPolling().setUpdatedSince("2026-03-18T00:00:00Z");

        HttpExternalSyncPollingClient client = new HttpExternalSyncPollingClient(httpClient);

        client.fetchPayload("JIRA", sourceProperties);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest request = requestCaptor.getValue();

        assertEquals(
                "https://jira.example.com/api/design-hub/issues?projectKey=DH&jql=project+%3D+DH+ORDER+BY+updated+DESC&updatedSince=2026-03-18T00%3A00%3A00Z",
                request.uri().toString()
        );
        assertEquals("Bearer jira-token", request.headers().firstValue("Authorization").orElseThrow());
    }

    @Test
    void shouldBuildDirectJiraCloudRequestWithBasicAuth() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"issues\":[]}");

        ExternalSyncProperties.SourceProperties sourceProperties = new ExternalSyncProperties.SourceProperties();
        sourceProperties.setBaseUrl("https://thinkplus.atlassian.net");
        sourceProperties.setPollPath("/rest/api/3/search/jql");
        sourceProperties.setProjectKey("DPAA");
        sourceProperties.setAccountEmail("info@thinkplus.ae");
        sourceProperties.setToken("jira-cloud-token");
        sourceProperties.getPolling().setJql("project = DPAA ORDER BY updated DESC");
        sourceProperties.getPolling().setUpdatedSince("2026-03-18T00:00:00Z");

        HttpExternalSyncPollingClient client = new HttpExternalSyncPollingClient(httpClient);

        client.fetchPayload("JIRA", sourceProperties);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest request = requestCaptor.getValue();

        assertEquals(
                "https://thinkplus.atlassian.net/rest/api/3/search/jql?projectKey=DPAA&jql=project+%3D+DPAA+ORDER+BY+updated+DESC&updatedSince=2026-03-18T00%3A00%3A00Z",
                request.uri().toString()
        );
        assertTrue(request.headers().firstValue("Authorization").orElseThrow().startsWith("Basic "));
    }

    @Test
    void shouldBuildAzureDevOpsRequestWithScopeFiltersAndHeaders() throws Exception {
        when(httpClient.send(any(HttpRequest.class), any(HttpResponse.BodyHandler.class))).thenReturn(httpResponse);
        when(httpResponse.statusCode()).thenReturn(200);
        when(httpResponse.body()).thenReturn("{\"dryRun\":true,\"workItems\":[]}");

        ExternalSyncProperties.SourceProperties sourceProperties = new ExternalSyncProperties.SourceProperties();
        sourceProperties.setBaseUrl("https://dev.azure.com/example");
        sourceProperties.setPollPath("/api/design-hub/work-items?api-version=7.1");
        sourceProperties.setOrganization("example-org");
        sourceProperties.setProject("design-hub");
        sourceProperties.setToken("azdo-token");
        sourceProperties.getHeaders().put("X-DesignHub-Test", "true");
        sourceProperties.getPolling().setWiql("Select [System.Id] From WorkItems");
        sourceProperties.getPolling().setUpdatedSince("2026-03-18T01:00:00Z");

        HttpExternalSyncPollingClient client = new HttpExternalSyncPollingClient(httpClient);

        client.fetchPayload("AZURE_DEVOPS", sourceProperties);

        ArgumentCaptor<HttpRequest> requestCaptor = ArgumentCaptor.forClass(HttpRequest.class);
        verify(httpClient).send(requestCaptor.capture(), any(HttpResponse.BodyHandler.class));
        HttpRequest request = requestCaptor.getValue();

        assertEquals(
                "https://dev.azure.com/example/api/design-hub/work-items?api-version=7.1&organization=example-org&project=design-hub&wiql=Select+%5BSystem.Id%5D+From+WorkItems&updatedSince=2026-03-18T01%3A00%3A00Z",
                request.uri().toString()
        );
        assertTrue(request.headers().firstValue("Authorization").orElseThrow().startsWith("Basic "));
        assertEquals("true", request.headers().firstValue("X-DesignHub-Test").orElseThrow());
    }
}
