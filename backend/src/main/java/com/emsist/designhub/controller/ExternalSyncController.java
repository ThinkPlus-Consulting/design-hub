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
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@RestController
@RequestMapping("/api/v1/external-sync")
@RequiredArgsConstructor
public class ExternalSyncController {

    private final AzureDevOpsSyncMapperService azureDevOpsSyncMapperService;
    private final ExternalArtifactSyncService externalArtifactSyncService;
    private final ExternalSyncOrchestrationService externalSyncOrchestrationService;
    private final ExternalSyncPollingService externalSyncPollingService;
    private final ExternalSyncStatusService externalSyncStatusService;
    private final JiraSyncMapperService jiraSyncMapperService;

    @PostMapping("/artifacts")
    public ResponseEntity<ExternalSyncResult> syncArtifacts(@RequestBody ExternalSyncRequest request) {
        return syncRequest(request);
    }

    @PostMapping("/azure-devops/work-items")
    public ResponseEntity<ExternalSyncResult> syncAzureDevOpsWorkItems(@RequestBody AzureDevOpsSyncRequest request) {
        return syncRequest(azureDevOpsSyncMapperService.toExternalSyncRequest(request));
    }

    @PostMapping("/jira/issues")
    public ResponseEntity<ExternalSyncResult> syncJiraIssues(@RequestBody JiraSyncRequest request) {
        return syncRequest(jiraSyncMapperService.toExternalSyncRequest(request));
    }

    @PostMapping("/jobs")
    public ResponseEntity<ExternalSyncJobResponse> submitSyncJob(@RequestBody ExternalSyncJobRequest request) {
        try {
            ExternalSyncJobResponse response = externalSyncOrchestrationService.submit(request);
            return "FAILED".equals(response.status())
                    ? ResponseEntity.unprocessableEntity().body(response)
                    : ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.unprocessableEntity().body(new ExternalSyncJobResponse(
                    null,
                    request == null ? null : request.sourceSystem(),
                    request == null ? null : request.transportMode(),
                    request == null ? null : request.correlationId(),
                    request == null ? null : request.requestedBy(),
                    request == null ? null : request.receivedAt(),
                    request == null ? null : request.triggerRef(),
                    false,
                    "FAILED",
                    0,
                    List.of(exception.getMessage()),
                    null
            ));
        }
    }

    @PostMapping("/jobs/poll/{sourceSystem}")
    public ResponseEntity<ExternalSyncJobResponse> pollSourceSystem(
            @PathVariable String sourceSystem,
            @RequestParam(name = "dryRun", required = false) Boolean dryRun,
            @RequestParam(name = "requestedBy", required = false) String requestedBy,
            @RequestParam(name = "triggerRef", required = false) String triggerRef
    ) {
        try {
            ExternalSyncJobResponse response = externalSyncPollingService.pollNow(
                    sourceSystem,
                    dryRun,
                    requestedBy,
                    triggerRef
            );
            return "FAILED".equals(response.status())
                    ? ResponseEntity.unprocessableEntity().body(response)
                    : ResponseEntity.ok(response);
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.unprocessableEntity().body(new ExternalSyncJobResponse(
                    null,
                    sourceSystem,
                    "POLL",
                    null,
                    requestedBy,
                    null,
                    triggerRef,
                    dryRun != null && dryRun,
                    "FAILED",
                    0,
                    List.of(exception.getMessage()),
                    null
            ));
        }
    }

    @GetMapping("/jobs")
    public ResponseEntity<List<ExternalSyncJobResponse>> listSyncJobs(
            @RequestParam(name = "limit", defaultValue = "10") int limit,
            @RequestParam(name = "sourceSystem", required = false) String sourceSystem
    ) {
        try {
            return ResponseEntity.ok(externalSyncOrchestrationService.list(limit, sourceSystem));
        } catch (IllegalArgumentException exception) {
            return ResponseEntity.unprocessableEntity().build();
        }
    }

    @GetMapping("/jobs/{jobId}")
    public ResponseEntity<ExternalSyncJobResponse> getSyncJob(@PathVariable String jobId) {
        return externalSyncOrchestrationService.findById(jobId)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/sources")
    public ResponseEntity<List<ExternalSyncSourceStatusResponse>> listSourceStatuses() {
        return ResponseEntity.ok(externalSyncStatusService.listSourceStatuses());
    }

    @GetMapping("/sources/{sourceSystem}")
    public ResponseEntity<ExternalSyncSourceStatusResponse> getSourceStatus(@PathVariable String sourceSystem) {
        return externalSyncStatusService.getSourceStatus(sourceSystem)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    private ResponseEntity<ExternalSyncResult> syncRequest(ExternalSyncRequest request) {
        if (request == null || request.artifacts() == null || request.artifacts().isEmpty()) {
            return ResponseEntity.unprocessableEntity().body(new ExternalSyncResult(
                    true,
                    "FAILED",
                    0,
                    0,
                    0,
                    0,
                    0,
                    List.of()
            ));
        }

        ExternalSyncResult result = externalArtifactSyncService.sync(request);
        return "FAILED".equals(result.result())
                ? ResponseEntity.unprocessableEntity().body(result)
                : ResponseEntity.ok(result);
    }
}
