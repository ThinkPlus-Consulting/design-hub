package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.StringJoiner;

@Service
@Slf4j
public class HttpExternalSyncPollingClient implements ExternalSyncPollingClient {

    private final HttpClient httpClient;

    public HttpExternalSyncPollingClient() {
        this(HttpClient.newHttpClient());
    }

    HttpExternalSyncPollingClient(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public String fetchPayload(String sourceSystem, ExternalSyncProperties.SourceProperties sourceProperties) {
        if (!sourceProperties.hasPollingEndpoint()) {
            throw new IllegalArgumentException("Polling endpoint is not configured for source system: " + sourceSystem);
        }

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(buildUri(sourceSystem, sourceProperties))
                .header("Accept", "application/json")
                .header("User-Agent", "DesignHubExternalSync/1.0")
                .GET();

        applyAuthentication(sourceSystem, sourceProperties, requestBuilder);
        applyConfiguredHeaders(sourceProperties, requestBuilder);

        try {
            HttpResponse<String> response = httpClient.send(requestBuilder.build(), HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "Polling endpoint returned HTTP " + response.statusCode() + " for source system: " + sourceSystem
                );
            }
            return response.body();
        } catch (IOException exception) {
            throw new IllegalStateException("Polling request failed for source system: " + sourceSystem, exception);
        } catch (InterruptedException exception) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Polling request interrupted for source system: " + sourceSystem, exception);
        }
    }

    private URI buildUri(String sourceSystem, ExternalSyncProperties.SourceProperties sourceProperties) {
        String baseUrl = sourceProperties.getBaseUrl().trim();
        String pollPath = sourceProperties.getPollPath().trim();
        String normalizedBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String normalizedPath = pollPath.startsWith("/") ? pollPath : "/" + pollPath;
        URI baseUri = URI.create(normalizedBase + normalizedPath);
        Map<String, String> queryParameters = buildQueryParameters(sourceSystem, sourceProperties);
        if (queryParameters.isEmpty()) {
            return baseUri;
        }

        StringJoiner query = new StringJoiner("&");
        if (baseUri.getRawQuery() != null && !baseUri.getRawQuery().isBlank()) {
            query.add(baseUri.getRawQuery());
        }
        queryParameters.forEach((key, value) -> query.add(encode(key) + "=" + encode(value)));

        return URI.create(new StringBuilder()
                .append(baseUri.getScheme())
                .append("://")
                .append(baseUri.getRawAuthority())
                .append(baseUri.getRawPath())
                .append("?")
                .append(query)
                .toString());
    }

    private Map<String, String> buildQueryParameters(
            String sourceSystem,
            ExternalSyncProperties.SourceProperties sourceProperties
    ) {
        LinkedHashMap<String, String> parameters = new LinkedHashMap<>();
        switch (sourceSystem) {
            case "AZURE_DEVOPS" -> {
                putIfConfigured(parameters, "organization", sourceProperties.getOrganization());
                putIfConfigured(parameters, "project", sourceProperties.getProject());
                putIfConfigured(parameters, "wiql", sourceProperties.getPolling().getWiql());
            }
            case "JIRA" -> {
                putIfConfigured(parameters, "projectKey", sourceProperties.getProjectKey());
                putIfConfigured(parameters, "jql", sourceProperties.getPolling().getJql());
            }
            default -> {
                return Map.of();
            }
        }
        putIfConfigured(parameters, "updatedSince", sourceProperties.getPolling().getUpdatedSince());
        return Collections.unmodifiableMap(new LinkedHashMap<>(parameters));
    }

    private void applyAuthentication(
            String sourceSystem,
            ExternalSyncProperties.SourceProperties sourceProperties,
            HttpRequest.Builder requestBuilder
    ) {
        String token = sourceProperties.getToken();
        if (token == null || token.isBlank()) {
            return;
        }

        String authHeader = switch (sourceSystem) {
            case "AZURE_DEVOPS" -> "Basic " + Base64.getEncoder()
                    .encodeToString((":" + token.trim()).getBytes(StandardCharsets.UTF_8));
            case "JIRA" -> "Bearer " + token.trim();
            default -> null;
        };

        if (authHeader != null) {
            requestBuilder.header("Authorization", authHeader);
        }
    }

    private void applyConfiguredHeaders(
            ExternalSyncProperties.SourceProperties sourceProperties,
            HttpRequest.Builder requestBuilder
    ) {
        for (Map.Entry<String, String> entry : sourceProperties.getHeaders().entrySet()) {
            if (entry.getKey() == null || entry.getKey().isBlank() || entry.getValue() == null || entry.getValue().isBlank()) {
                continue;
            }
            requestBuilder.header(entry.getKey().trim(), entry.getValue().trim());
        }
    }

    private void putIfConfigured(Map<String, String> parameters, String key, String value) {
        if (value != null && !value.isBlank()) {
            parameters.put(key, value.trim());
        }
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}
