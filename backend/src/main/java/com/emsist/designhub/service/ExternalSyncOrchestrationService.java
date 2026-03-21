package com.emsist.designhub.service;

import com.emsist.designhub.config.ExternalSyncProperties;
import com.emsist.designhub.dto.ExternalSyncJobRequest;
import com.emsist.designhub.dto.ExternalSyncJobResponse;
import com.emsist.designhub.dto.ExternalSyncRequest;
import com.emsist.designhub.dto.ExternalSyncResult;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class ExternalSyncOrchestrationService {

    private static final String UPSERT_JOB_QUERY = """
            MERGE (job:ExternalSyncJob {jobId: $jobId})
            SET job.sourceSystem = $sourceSystem,
                job.transportMode = $transportMode,
                job.correlationId = $correlationId,
                job.requestedBy = $requestedBy,
                job.receivedAt = datetime($receivedAt),
                job.triggerRef = $triggerRef,
                job.dryRun = $dryRun,
                job.status = $status,
                job.artifactCount = $artifactCount,
                job.warningsJson = $warningsJson,
                job.resultJson = $resultJson,
                job.createdAt = coalesce(job.createdAt, datetime()),
                job.updatedAt = datetime()
            """;

    private static final String FIND_JOB_QUERY = """
            MATCH (job:ExternalSyncJob {jobId: $jobId})
            RETURN job.jobId AS jobId,
                   job.sourceSystem AS sourceSystem,
                   job.transportMode AS transportMode,
                   job.correlationId AS correlationId,
                   job.requestedBy AS requestedBy,
                   toString(job.receivedAt) AS receivedAt,
                   job.triggerRef AS triggerRef,
                   job.dryRun AS dryRun,
                   job.status AS status,
                   job.artifactCount AS artifactCount,
                   job.warningsJson AS warningsJson,
                   job.resultJson AS resultJson
            """;

    private static final String LIST_JOBS_QUERY = """
            MATCH (job:ExternalSyncJob)
            RETURN job.jobId AS jobId,
                   job.sourceSystem AS sourceSystem,
                   job.transportMode AS transportMode,
                   job.correlationId AS correlationId,
                   job.requestedBy AS requestedBy,
                   toString(job.receivedAt) AS receivedAt,
                   job.triggerRef AS triggerRef,
                   job.dryRun AS dryRun,
                   job.status AS status,
                   job.artifactCount AS artifactCount,
                   job.warningsJson AS warningsJson,
                   job.resultJson AS resultJson
            ORDER BY job.receivedAt DESC, job.updatedAt DESC
            LIMIT $limit
            """;

    private static final String LIST_JOBS_BY_SOURCE_QUERY = """
            MATCH (job:ExternalSyncJob {sourceSystem: $sourceSystem})
            RETURN job.jobId AS jobId,
                   job.sourceSystem AS sourceSystem,
                   job.transportMode AS transportMode,
                   job.correlationId AS correlationId,
                   job.requestedBy AS requestedBy,
                   toString(job.receivedAt) AS receivedAt,
                   job.triggerRef AS triggerRef,
                   job.dryRun AS dryRun,
                   job.status AS status,
                   job.artifactCount AS artifactCount,
                   job.warningsJson AS warningsJson,
                   job.resultJson AS resultJson
            ORDER BY job.receivedAt DESC, job.updatedAt DESC
            LIMIT $limit
            """;

    private final AzureDevOpsSyncMapperService azureDevOpsSyncMapperService;
    private final ExternalSyncProperties externalSyncProperties;
    private final ExternalArtifactSyncService externalArtifactSyncService;
    private final JiraSyncMapperService jiraSyncMapperService;
    private final Neo4jClient neo4jClient;
    private final ObjectMapper objectMapper;

    public ExternalSyncJobResponse submit(ExternalSyncJobRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("External sync job request is required");
        }

        String sourceSystem = normalizeSourceSystem(request);
        String transportMode = normalizeTransportMode(request.transportMode());
        validateTransport(sourceSystem, transportMode);
        ExternalSyncRequest syncRequest = switch (sourceSystem) {
            case "AZURE_DEVOPS" -> azureDevOpsSyncMapperService.toExternalSyncRequest(request.azureDevOpsRequest());
            case "JIRA" -> jiraSyncMapperService.toExternalSyncRequest(request.jiraRequest());
            default -> throw new IllegalArgumentException("Unsupported source system: " + sourceSystem);
        };

        List<String> warnings = collectWarnings(request, sourceSystem, syncRequest);
        ExternalSyncResult result = externalArtifactSyncService.sync(syncRequest);

        return recordJob(
                sourceSystem,
                transportMode,
                request.correlationId(),
                request.requestedBy(),
                request.receivedAt(),
                request.triggerRef(),
                syncRequest.dryRun(),
                result.result(),
                result.processedCount(),
                warnings,
                result
        );
    }

    public Optional<ExternalSyncJobResponse> findById(String jobId) {
        return findPersistedById(jobId);
    }

    public List<ExternalSyncJobResponse> list(int limit) {
        return list(limit, null);
    }

    public List<ExternalSyncJobResponse> list(int limit, String sourceSystem) {
        return listPersisted(limit, sourceSystem);
    }

    ExternalSyncJobResponse recordJob(
            String sourceSystem,
            String transportMode,
            String correlationId,
            String requestedBy,
            String receivedAt,
            String triggerRef,
            boolean dryRun,
            String status,
            int artifactCount,
            List<String> warnings,
            ExternalSyncResult result
    ) {
        ExternalSyncJobResponse response = new ExternalSyncJobResponse(
                nextJobId(),
                sourceSystem,
                normalizeTransportMode(transportMode),
                normalizeBlank(correlationId),
                normalizeBlank(requestedBy),
                normalizeReceivedAt(receivedAt),
                normalizeBlank(triggerRef),
                dryRun,
                status,
                artifactCount,
                warnings == null ? List.of() : List.copyOf(warnings),
                result
        );
        persistJob(response);
        return response;
    }

    void persistJob(ExternalSyncJobResponse response) {
        neo4jClient.query(UPSERT_JOB_QUERY)
                .bind(response.jobId()).to("jobId")
                .bind(response.sourceSystem()).to("sourceSystem")
                .bind(response.transportMode()).to("transportMode")
                .bind(response.correlationId()).to("correlationId")
                .bind(response.requestedBy()).to("requestedBy")
                .bind(response.receivedAt()).to("receivedAt")
                .bind(response.triggerRef()).to("triggerRef")
                .bind(response.dryRun()).to("dryRun")
                .bind(response.status()).to("status")
                .bind(response.artifactCount()).to("artifactCount")
                .bind(writeJson(response.warnings())).to("warningsJson")
                .bind(writeJson(response.result())).to("resultJson")
                .run();
    }

    Optional<ExternalSyncJobResponse> findPersistedById(String jobId) {
        if (jobId == null || jobId.isBlank()) {
            return Optional.empty();
        }

        return neo4jClient.query(FIND_JOB_QUERY)
                .bind(jobId).to("jobId")
                .fetch()
                .first()
                .map(this::toJobResponse);
    }

    List<ExternalSyncJobResponse> listPersisted(int limit, String sourceSystem) {
        int effectiveLimit = Math.max(1, Math.min(limit, 50));
        String normalizedSourceSystem = normalizeSourceSystemFilter(sourceSystem);
        Neo4jClient.RunnableSpec query = neo4jClient.query(
                normalizedSourceSystem == null ? LIST_JOBS_QUERY : LIST_JOBS_BY_SOURCE_QUERY
        );
        query = query.bind(effectiveLimit).to("limit");
        if (normalizedSourceSystem != null) {
            query = query.bind(normalizedSourceSystem).to("sourceSystem");
        }
        return query
                .fetch()
                .all()
                .stream()
                .map(this::toJobResponse)
                .toList();
    }

    private List<String> collectWarnings(
            ExternalSyncJobRequest request,
            String sourceSystem,
            ExternalSyncRequest syncRequest
    ) {
        LinkedHashSet<String> warnings = new LinkedHashSet<>();

        if (request.correlationId() == null || request.correlationId().isBlank()) {
            warnings.add("Missing correlationId; generated job will rely on jobId only");
        }
        if (request.triggerRef() == null || request.triggerRef().isBlank()) {
            warnings.add("Missing triggerRef; poll/webhook provenance is not captured");
        }
        if (syncRequest.artifacts() == null || syncRequest.artifacts().isEmpty()) {
            warnings.add("No artifacts mapped from " + sourceSystem + " payload");
        }

        return List.copyOf(warnings);
    }

    private void validateTransport(String sourceSystem, String transportMode) {
        ExternalSyncProperties.SourceProperties sourceProperties = externalSyncProperties.sourceFor(sourceSystem);
        if (!sourceProperties.isEnabled()) {
            throw new IllegalArgumentException("External sync is disabled for source system: " + sourceSystem);
        }
        if (!sourceProperties.isTransportEnabled(transportMode)) {
            throw new IllegalArgumentException("Transport mode " + transportMode + " is disabled for source system: " + sourceSystem);
        }
    }

    private String normalizeSourceSystem(ExternalSyncJobRequest request) {
        String explicit = normalizeBlank(request.sourceSystem());
        boolean hasAzurePayload = request.azureDevOpsRequest() != null;
        boolean hasJiraPayload = request.jiraRequest() != null;

        if (hasAzurePayload == hasJiraPayload) {
            if (explicit == null) {
                throw new IllegalArgumentException("Exactly one source payload is required");
            }
            return explicit;
        }

        String inferred = hasAzurePayload ? "AZURE_DEVOPS" : "JIRA";
        if (explicit == null) {
            return inferred;
        }
        if (!explicit.equals(inferred)) {
            throw new IllegalArgumentException("Source system does not match supplied payload");
        }
        return explicit;
    }

    private String normalizeTransportMode(String transportMode) {
        String normalized = normalizeBlank(transportMode);
        return normalized == null ? "MANUAL" : normalized;
    }

    private String normalizeSourceSystemFilter(String sourceSystem) {
        if (sourceSystem == null || sourceSystem.isBlank()) {
            return null;
        }
        String normalized = sourceSystem.trim().toUpperCase(Locale.ROOT);
        return switch (normalized) {
            case "AZURE_DEVOPS", "JIRA" -> normalized;
            default -> throw new IllegalArgumentException("Unsupported source system: " + normalized);
        };
    }

    private String normalizeReceivedAt(String receivedAt) {
        String normalized = normalizeBlank(receivedAt);
        if (normalized == null) {
            return Instant.now().toString();
        }
        try {
            return Instant.parse(normalized).toString();
        } catch (DateTimeParseException exception) {
            throw new IllegalArgumentException("receivedAt must be an ISO-8601 instant", exception);
        }
    }

    private String normalizeBlank(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        return value.trim().toUpperCase(Locale.ROOT).equals(value.trim())
                ? value.trim()
                : value.trim();
    }

    private String nextJobId() {
        return "XSJ-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private ExternalSyncJobResponse toJobResponse(Map<String, Object> row) {
        return new ExternalSyncJobResponse(
                (String) row.get("jobId"),
                (String) row.get("sourceSystem"),
                (String) row.get("transportMode"),
                (String) row.get("correlationId"),
                (String) row.get("requestedBy"),
                (String) row.get("receivedAt"),
                (String) row.get("triggerRef"),
                toBoolean(row.get("dryRun")),
                (String) row.get("status"),
                toInt(row.get("artifactCount")),
                readWarnings((String) row.get("warningsJson")),
                readResult((String) row.get("resultJson"))
        );
    }

    private String writeJson(Object value) {
        try {
            return objectMapper.writeValueAsString(value);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to serialize external sync job payload", exception);
        }
    }

    private List<String> readWarnings(String warningsJson) {
        if (warningsJson == null || warningsJson.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readValue(warningsJson, new TypeReference<>() {
            });
        } catch (JsonProcessingException exception) {
            log.warn("Failed to deserialize persisted external sync job warnings", exception);
            return List.of("Failed to deserialize persisted warnings");
        }
    }

    private ExternalSyncResult readResult(String resultJson) {
        if (resultJson == null || resultJson.isBlank()) {
            return null;
        }
        try {
            return objectMapper.readValue(resultJson, ExternalSyncResult.class);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Failed to deserialize persisted external sync result", exception);
        }
    }

    private boolean toBoolean(Object value) {
        return value instanceof Boolean bool && bool;
    }

    private int toInt(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }
}
