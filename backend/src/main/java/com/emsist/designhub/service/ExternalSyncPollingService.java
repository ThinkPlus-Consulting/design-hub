package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import com.emsist.designhub.dto.AzureDevOpsSyncRequest;
import com.emsist.designhub.dto.ExternalSyncJobRequest;
import com.emsist.designhub.dto.ExternalSyncJobResponse;
import com.emsist.designhub.dto.ExternalSyncResult;
import com.emsist.designhub.dto.JiraSyncRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
                    withDryRun(readPayload(payload, JiraSyncRequest.class), dryRun)
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
