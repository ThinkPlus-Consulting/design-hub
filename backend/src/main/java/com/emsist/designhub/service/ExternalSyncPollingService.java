package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import com.emsist.designhub.dto.AzureDevOpsSyncRequest;
import com.emsist.designhub.dto.ExternalSyncJobRequest;
import com.emsist.designhub.dto.ExternalSyncJobResponse;
import com.emsist.designhub.dto.ExternalSyncResult;
import com.emsist.designhub.dto.JiraSyncRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExternalSyncPollingService {

    private final ExternalSyncPollingClient externalSyncPollingClient;
    private final ExternalSyncOrchestrationService externalSyncOrchestrationService;
    private final ExternalSyncProperties externalSyncProperties;
    private final ObjectMapper objectMapper;

    private final Map<String, Instant> lastScheduledPolls = new ConcurrentHashMap<>();

    public ExternalSyncJobResponse pollNow(
            String sourceSystem,
            Boolean dryRunOverride,
            String requestedBy,
            String triggerRef
    ) {
        String normalizedSource = normalizeSourceSystem(sourceSystem);
        ExternalSyncProperties.SourceProperties sourceProperties = externalSyncProperties.sourceFor(normalizedSource);
        validatePollingEnabled(normalizedSource, sourceProperties);

        boolean dryRun = dryRunOverride != null ? dryRunOverride : sourceProperties.getPolling().isDryRun();
        String effectiveRequestedBy = normalizeBlank(requestedBy);
        String effectiveTriggerRef = normalizeBlank(triggerRef);
        String receivedAt = Instant.now().toString();

        if (!sourceProperties.hasPollingEndpoint()) {
            return externalSyncOrchestrationService.recordJob(
                    normalizedSource,
                    "POLL",
                    nextCorrelationId(normalizedSource),
                    effectiveRequestedBy,
                    receivedAt,
                    effectiveTriggerRef != null ? effectiveTriggerRef : "poll/config-missing/" + normalizedSource.toLowerCase(Locale.ROOT),
                    dryRun,
                    "SKIPPED",
                    0,
                    List.of("Polling endpoint is not configured in application.yml for source system: " + normalizedSource),
                    new ExternalSyncResult(dryRun, "SKIPPED", 0, 0, 0, 1, 0, List.of())
            );
        }

        try {
            ExternalSyncJobRequest jobRequest = buildJobRequest(
                    normalizedSource,
                    sourceProperties,
                    dryRun,
                    effectiveRequestedBy,
                    effectiveTriggerRef,
                    receivedAt
            );
            return externalSyncOrchestrationService.submit(jobRequest);
        } catch (RuntimeException exception) {
            log.warn("Polling failed for source system {}", normalizedSource, exception);
            return externalSyncOrchestrationService.recordJob(
                    normalizedSource,
                    "POLL",
                    nextCorrelationId(normalizedSource),
                    effectiveRequestedBy,
                    receivedAt,
                    effectiveTriggerRef != null ? effectiveTriggerRef : "poll/error/" + normalizedSource.toLowerCase(Locale.ROOT),
                    dryRun,
                    "FAILED",
                    0,
                    List.of(exception.getMessage()),
                    new ExternalSyncResult(dryRun, "FAILED", 0, 0, 0, 0, 1, List.of())
            );
        }
    }

    @Scheduled(fixedDelayString = "#{@externalSyncProperties.scheduler.fixedDelay.toMillis()}")
    public void runScheduledPolling() {
        if (!externalSyncProperties.getScheduler().isEnabled()) {
            return;
        }

        maybePollSource("AZURE_DEVOPS", externalSyncProperties.getAzureDevops());
        maybePollSource("JIRA", externalSyncProperties.getJira());
    }

    private void maybePollSource(String sourceSystem, ExternalSyncProperties.SourceProperties sourceProperties) {
        if (!sourceProperties.isEnabled() || !sourceProperties.getPolling().isEnabled() || !sourceProperties.hasPollingEndpoint()) {
            return;
        }

        Instant now = Instant.now();
        Instant lastPoll = lastScheduledPolls.get(sourceSystem);
        if (lastPoll != null && now.isBefore(lastPoll.plus(sourceProperties.getPolling().getInterval()))) {
            return;
        }

        lastScheduledPolls.put(sourceSystem, now);
        pollNow(
                sourceSystem,
                sourceProperties.getPolling().isDryRun(),
                externalSyncProperties.getScheduler().getRequestedBy(),
                "schedule/" + sourceSystem.toLowerCase(Locale.ROOT)
        );
    }

    private ExternalSyncJobRequest buildJobRequest(
            String sourceSystem,
            ExternalSyncProperties.SourceProperties sourceProperties,
            boolean dryRun,
            String requestedBy,
            String triggerRef,
            String receivedAt
    ) {
        String payload = externalSyncPollingClient.fetchPayload(sourceSystem, sourceProperties);
        String effectiveTriggerRef = triggerRef != null
                ? triggerRef
                : "poll/" + sourceSystem.toLowerCase(Locale.ROOT) + sourceProperties.getPollPath().trim();

        return switch (sourceSystem) {
            case "AZURE_DEVOPS" -> new ExternalSyncJobRequest(
                    sourceSystem,
                    "POLL",
                    nextCorrelationId(sourceSystem),
                    requestedBy,
                    receivedAt,
                    effectiveTriggerRef,
                    withDryRun(readPayload(payload, AzureDevOpsSyncRequest.class), dryRun),
                    null
            );
            case "JIRA" -> new ExternalSyncJobRequest(
                    sourceSystem,
                    "POLL",
                    nextCorrelationId(sourceSystem),
                    requestedBy,
                    receivedAt,
                    effectiveTriggerRef,
                    null,
                    withDryRun(readJiraPayload(payload, sourceProperties), dryRun)
            );
            default -> throw new IllegalArgumentException("Unsupported source system: " + sourceSystem);
        };
    }

    private void validatePollingEnabled(String sourceSystem, ExternalSyncProperties.SourceProperties sourceProperties) {
        if (!sourceProperties.isEnabled()) {
            throw new IllegalArgumentException("External sync is disabled for source system: " + sourceSystem);
        }
        if (!sourceProperties.getPolling().isEnabled()) {
            throw new IllegalArgumentException("Transport mode POLL is disabled for source system: " + sourceSystem);
        }
    }

    private <T> T readPayload(String payload, Class<T> payloadType) {
        try {
            return objectMapper.readValue(payload, payloadType);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Polling payload could not be parsed into " + payloadType.getSimpleName(), exception);
        }
    }

    private JiraSyncRequest readJiraPayload(
            String payload,
            ExternalSyncProperties.SourceProperties sourceProperties
    ) {
        try {
            JsonNode root = objectMapper.readTree(payload);
            if (root == null || root.isNull()) {
                return new JiraSyncRequest(false, List.of());
            }

            if (isDirectJiraSearchPayload(root)) {
                return parseDirectJiraSearchPayload(root, sourceProperties);
            }

            return objectMapper.treeToValue(root, JiraSyncRequest.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Polling payload could not be parsed into JiraSyncRequest", exception);
        }
    }

    private boolean isDirectJiraSearchPayload(JsonNode root) {
        JsonNode issues = root.path("issues");
        return issues.isArray()
                && !issues.isEmpty()
                && issues.path(0).path("fields").isObject();
    }

    private JiraSyncRequest parseDirectJiraSearchPayload(
            JsonNode root,
            ExternalSyncProperties.SourceProperties sourceProperties
    ) {
        List<JiraSyncRequest.Issue> issues = new ArrayList<>();
        for (JsonNode issueNode : root.path("issues")) {
            JsonNode fields = issueNode.path("fields");
            String issueKey = text(issueNode, "key");
            issues.add(new JiraSyncRequest.Issue(
                    text(issueNode, "id"),
                    issueKey,
                    text(fields.path("issuetype"), "name"),
                    text(fields, "summary"),
                    text(fields.path("project"), "key"),
                    text(fields.path("status"), "name"),
                    text(fields.path("priority"), "name"),
                    displayName(fields.path("assignee")),
                    displayName(fields.path("reporter")),
                    stringList(fields.path("labels")),
                    customFields(fields),
                    browseUrl(sourceProperties.getBaseUrl(), issueKey),
                    text(fields, "updated"),
                    text(fields.path("parent"), "key"),
                    linkKeys(fields.path("issuelinks"), "block"),
                    linkKeys(fields.path("issuelinks"), "relate"),
                    linkKeys(fields.path("issuelinks"), "duplicate"),
                    List.of()
            ));
        }
        return new JiraSyncRequest(false, List.copyOf(issues));
    }

    private String text(JsonNode node, String fieldName) {
        if (node == null || node.isMissingNode() || node.isNull()) {
            return null;
        }
        JsonNode value = node.path(fieldName);
        if (value.isMissingNode() || value.isNull() || value.isContainerNode()) {
            return null;
        }
        String textValue = value.asText();
        return textValue == null || textValue.isBlank() ? null : textValue;
    }

    private String displayName(JsonNode node) {
        String displayName = text(node, "displayName");
        if (displayName != null) {
            return displayName;
        }
        String emailAddress = text(node, "emailAddress");
        if (emailAddress != null) {
            return emailAddress;
        }
        return text(node, "accountId");
    }

    private List<String> stringList(JsonNode node) {
        if (node == null || !node.isArray()) {
            return List.of();
        }

        List<String> values = new ArrayList<>();
        for (JsonNode entry : node) {
            if (entry == null || entry.isNull() || entry.isContainerNode()) {
                continue;
            }
            String value = entry.asText();
            if (value != null && !value.isBlank()) {
                values.add(value);
            }
        }
        return List.copyOf(values);
    }

    private Map<String, String> customFields(JsonNode fields) {
        if (fields == null || !fields.isObject()) {
            return Map.of();
        }

        LinkedHashMap<String, String> values = new LinkedHashMap<>();
        fields.fields().forEachRemaining(entry -> {
            String key = entry.getKey();
            JsonNode value = entry.getValue();
            if (!key.startsWith("customfield_") || value == null || value.isNull()) {
                return;
            }

            if (value.isValueNode()) {
                values.put(key, value.asText());
                return;
            }

            if (value.isObject()) {
                String name = text(value, "name");
                if (name != null) {
                    values.put(key, name);
                    return;
                }
                String keyValue = text(value, "key");
                if (keyValue != null) {
                    values.put(key, keyValue);
                    return;
                }
            }

            values.put(key, value.toString());
        });
        return Map.copyOf(values);
    }

    private List<String> linkKeys(JsonNode issueLinks, String relationKeyword) {
        if (issueLinks == null || !issueLinks.isArray()) {
            return List.of();
        }

        LinkedHashSet<String> keys = new LinkedHashSet<>();
        for (JsonNode issueLink : issueLinks) {
            String relationText = String.join(" ",
                    defaultString(text(issueLink.path("type"), "name")),
                    defaultString(text(issueLink.path("type"), "outward")),
                    defaultString(text(issueLink.path("type"), "inward"))
            ).toLowerCase(Locale.ROOT);
            if (!relationText.contains(relationKeyword)) {
                continue;
            }

            String outwardKey = text(issueLink.path("outwardIssue"), "key");
            String inwardKey = text(issueLink.path("inwardIssue"), "key");
            if (outwardKey != null) {
                keys.add(outwardKey);
            }
            if (inwardKey != null) {
                keys.add(inwardKey);
            }
        }
        return List.copyOf(keys);
    }

    private String browseUrl(String baseUrl, String issueKey) {
        if (baseUrl == null || baseUrl.isBlank() || issueKey == null || issueKey.isBlank()) {
            return null;
        }

        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        if (normalizedBaseUrl.contains(".atlassian.net")) {
            return normalizedBaseUrl + "/browse/" + issueKey;
        }
        return null;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private AzureDevOpsSyncRequest withDryRun(AzureDevOpsSyncRequest request, boolean dryRun) {
        return new AzureDevOpsSyncRequest(dryRun, request == null ? List.of() : request.workItems());
    }

    private JiraSyncRequest withDryRun(JiraSyncRequest request, boolean dryRun) {
        return new JiraSyncRequest(dryRun, request == null ? List.of() : request.issues());
    }

    private String normalizeSourceSystem(String sourceSystem) {
        if (sourceSystem == null || sourceSystem.isBlank()) {
            throw new IllegalArgumentException("Source system is required");
        }
        return sourceSystem.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizeBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim();
    }

    private String nextCorrelationId(String sourceSystem) {
        return "poll-" + sourceSystem.toLowerCase(Locale.ROOT) + "-" + Instant.now().toEpochMilli();
    }
}
