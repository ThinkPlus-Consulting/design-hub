package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import com.emsist.designhub.dto.ExternalSyncSourceStatusResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ExternalSyncStatusService {

    private static final String LATEST_JOB_BY_SOURCE_QUERY = """
            MATCH (job:ExternalSyncJob)
            WITH job.sourceSystem AS sourceSystem, job
            ORDER BY sourceSystem, job.receivedAt DESC, job.updatedAt DESC
            WITH sourceSystem, head(collect(job)) AS latestJob
            RETURN sourceSystem AS sourceSystem,
                   latestJob.jobId AS jobId,
                   latestJob.status AS status,
                   toString(latestJob.receivedAt) AS receivedAt,
                   latestJob.requestedBy AS requestedBy,
                   latestJob.triggerRef AS triggerRef,
                   latestJob.transportMode AS transportMode
            """;

    private final ExternalSyncProperties externalSyncProperties;
    private final Neo4jClient neo4jClient;

    public List<ExternalSyncSourceStatusResponse> listSourceStatuses() {
        Map<String, ExternalSyncSourceStatusResponse.LatestJobSummary> latestJobs = loadLatestJobsBySource();

        return Stream.of("AZURE_DEVOPS", "JIRA")
                .map(sourceSystem -> toStatusResponse(
                        sourceSystem,
                        externalSyncProperties.sourceFor(sourceSystem),
                        latestJobs.get(sourceSystem)
                ))
                .toList();
    }

    public Optional<ExternalSyncSourceStatusResponse> getSourceStatus(String sourceSystem) {
        if (sourceSystem == null || sourceSystem.isBlank()) {
            return Optional.empty();
        }

        String normalized = sourceSystem.trim().toUpperCase(Locale.ROOT);
        if (!List.of("AZURE_DEVOPS", "JIRA").contains(normalized)) {
            return Optional.empty();
        }

        Map<String, ExternalSyncSourceStatusResponse.LatestJobSummary> latestJobs = loadLatestJobsBySource();
        return Optional.of(toStatusResponse(
                normalized,
                externalSyncProperties.sourceFor(normalized),
                latestJobs.get(normalized)
        ));
    }

    @SuppressWarnings("unchecked")
    private Map<String, ExternalSyncSourceStatusResponse.LatestJobSummary> loadLatestJobsBySource() {
        return neo4jClient.query(LATEST_JOB_BY_SOURCE_QUERY)
                .fetch()
                .all()
                .stream()
                .map(row -> (Map<String, Object>) row)
                .filter(row -> row.get("sourceSystem") instanceof String)
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row.get("sourceSystem"),
                        this::toLatestJobSummary,
                        (left, right) -> left
                ));
    }

    private ExternalSyncSourceStatusResponse toStatusResponse(
            String sourceSystem,
            ExternalSyncProperties.SourceProperties sourceProperties,
            ExternalSyncSourceStatusResponse.LatestJobSummary latestJob
    ) {
        return new ExternalSyncSourceStatusResponse(
                sourceSystem,
                sourceProperties.isEnabled(),
                sourceProperties.getWebhook().isEnabled(),
                isConfigured(sourceProperties.getWebhook().getSecret()),
                sourceProperties.getPolling().isEnabled(),
                sourceProperties.hasPollingEndpoint(),
                isConfigured(sourceProperties.getBaseUrl()),
                isConfigured(sourceProperties.getPollPath()),
                hasScopeConfiguration(sourceSystem, sourceProperties),
                hasFilterConfiguration(sourceSystem, sourceProperties),
                isConfigured(sourceProperties.getToken()),
                externalSyncProperties.getScheduler().isEnabled(),
                sourceProperties.getPolling().isDryRun(),
                latestJob
        );
    }

    private ExternalSyncSourceStatusResponse.LatestJobSummary toLatestJobSummary(Map<String, Object> row) {
        return new ExternalSyncSourceStatusResponse.LatestJobSummary(
                (String) row.get("jobId"),
                (String) row.get("status"),
                (String) row.get("receivedAt"),
                (String) row.get("requestedBy"),
                (String) row.get("triggerRef"),
                (String) row.get("transportMode")
        );
    }

    private boolean isConfigured(String value) {
        return value != null && !value.isBlank();
    }

    private boolean hasScopeConfiguration(String sourceSystem, ExternalSyncProperties.SourceProperties sourceProperties) {
        return switch (sourceSystem) {
            case "AZURE_DEVOPS" -> isConfigured(sourceProperties.getOrganization()) && isConfigured(sourceProperties.getProject());
            case "JIRA" -> isConfigured(sourceProperties.getProjectKey());
            default -> false;
        };
    }

    private boolean hasFilterConfiguration(String sourceSystem, ExternalSyncProperties.SourceProperties sourceProperties) {
        boolean updatedSinceConfigured = isConfigured(sourceProperties.getPolling().getUpdatedSince());
        return switch (sourceSystem) {
            case "AZURE_DEVOPS" -> updatedSinceConfigured || isConfigured(sourceProperties.getPolling().getWiql());
            case "JIRA" -> updatedSinceConfigured || isConfigured(sourceProperties.getPolling().getJql());
            default -> false;
        };
    }
}
