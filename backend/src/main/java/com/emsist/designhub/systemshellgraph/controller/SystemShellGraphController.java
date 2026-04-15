package com.emsist.designhub.systemshellgraph.controller;

import com.emsist.designhub.systemshellgraph.dto.ComponentRegistryDefinitionResponse;
import com.emsist.designhub.systemshellgraph.dto.ComponentRegistryInstanceResponse;
import com.emsist.designhub.systemshellgraph.dto.ComponentRegistryInstanceUpdateRequest;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphIssueScanRequest;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphIssueScanSummaryResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphIssueStatusUpdateRequest;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphIssueUpdateRequest;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphValidationResponse;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphComponentRegistryService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphIssueRegistryService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphQueryService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphSeedService;
import com.emsist.designhub.systemshellgraph.service.SystemShellGraphValidationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/system-shell-graph")
@RequiredArgsConstructor
@Tag(name = "System Screen Graph", description = "Read-only frontend system graph for the graph-driven screen baseline")
public class SystemShellGraphController {

    private final SystemShellGraphQueryService queryService;
    private final SystemShellGraphComponentRegistryService componentRegistryService;
    private final SystemShellGraphIssueRegistryService issueRegistryService;
    private final SystemShellGraphSeedService seedService;
    private final SystemShellGraphValidationService validationService;

    @GetMapping("/graph")
    @Operation(summary = "Return the frontend system definition and instance graph")
    public ResponseEntity<SystemShellGraphResponse> getGraph() {
        return ResponseEntity.ok(queryService.getGraph());
    }

    @GetMapping("/login")
    @Operation(summary = "Backward-compatible alias for the frontend system graph")
    public ResponseEntity<SystemShellGraphResponse> getLoginScenario() {
        return ResponseEntity.ok(queryService.getGraph());
    }

    @GetMapping("/validation")
    @Operation(summary = "Validate the live frontend system graph stored in Neo4j")
    public ResponseEntity<SystemShellGraphValidationResponse> getValidation() {
        return ResponseEntity.ok(validationService.validateLiveGraph());
    }

    @PostMapping("/admin/reseed")
    @Operation(summary = "Reseed the system-shell graph scopes and return the fresh validation result")
    public ResponseEntity<SystemShellGraphValidationResponse> reseedGraph() {
        seedService.reseedCurrentScope();
        return ResponseEntity.ok(validationService.validateLiveGraph());
    }

    @PostMapping("/issues/scan")
    @Operation(summary = "Persist the latest X-Ray scan results and return issue scan summary")
    public ResponseEntity<SystemShellGraphIssueScanSummaryResponse> scanIssues(
            @RequestBody SystemShellGraphIssueScanRequest request
    ) {
        return ResponseEntity.ok(issueRegistryService.synchronizeIssueScan(request));
    }

    @PutMapping("/issues/status")
    @Operation(summary = "Update status for one or more persisted design issues")
    public ResponseEntity<Void> updateIssueStatuses(
            @RequestBody SystemShellGraphIssueStatusUpdateRequest request
    ) {
        issueRegistryService.updateIssueStatuses(request.issueIds(), request.status());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/issues/{issueId}")
    @Operation(summary = "Update a persisted design issue")
    public ResponseEntity<Void> updateIssue(
            @PathVariable String issueId,
            @RequestBody SystemShellGraphIssueUpdateRequest request
    ) {
        issueRegistryService.updateIssuePrompt(issueId, request.issuePrompt());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/components")
    @Operation(summary = "Return the component registry definitions stored in Neo4j")
    public ResponseEntity<List<ComponentRegistryDefinitionResponse>> getComponentRegistry() {
        return ResponseEntity.ok(componentRegistryService.getDefinitions());
    }

    @GetMapping("/components/{assetType}/instance")
    @Operation(summary = "Return the default component instance for the selected asset type")
    public ResponseEntity<ComponentRegistryInstanceResponse> getComponentInstance(@PathVariable String assetType) {
        return ResponseEntity.ok(componentRegistryService.getDefaultInstance(assetType));
    }

    @PutMapping("/components/instances/{instanceId}")
    @Operation(summary = "Update and persist a component instance")
    public ResponseEntity<ComponentRegistryInstanceResponse> updateComponentInstance(
            @PathVariable String instanceId,
            @RequestBody ComponentRegistryInstanceUpdateRequest request
    ) {
        return ResponseEntity.ok(componentRegistryService.saveInstance(instanceId, request));
    }
}
