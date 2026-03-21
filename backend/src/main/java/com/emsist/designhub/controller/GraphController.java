package com.emsist.designhub.controller;

import com.emsist.designhub.dto.GraphObjectSummaryResponse;
import com.emsist.designhub.dto.GraphRelationExpansionResponse;
import com.emsist.designhub.dto.GraphBenchmarkResponse;
import com.emsist.designhub.dto.JourneyTraversalResponse;
import com.emsist.designhub.dto.PersonaSummaryResponse;
import com.emsist.designhub.dto.PersonaTraversalResponse;
import com.emsist.designhub.dto.ApplicationArchitectureResponse;
import com.emsist.designhub.dto.ApplicationSummaryResponse;
import com.emsist.designhub.dto.BusinessArchitectureResponse;
import com.emsist.designhub.dto.BusinessCapabilitySummaryResponse;
import com.emsist.designhub.dto.ChannelSummaryResponse;
import com.emsist.designhub.dto.ChannelTraversalResponse;
import com.emsist.designhub.dto.DataArchitectureObjectSummaryResponse;
import com.emsist.designhub.dto.DataArchitectureResponse;
import com.emsist.designhub.dto.ExternalArtifactSummaryResponse;
import com.emsist.designhub.dto.ExternalArtifactTraversalResponse;
import com.emsist.designhub.dto.ExternalParityAuditResponse;
import com.emsist.designhub.dto.InfrastructureDeploymentSummaryResponse;
import com.emsist.designhub.dto.InfrastructureArchitectureResponse;
import com.emsist.designhub.dto.TraceabilityStoryResponse;
import com.emsist.designhub.service.BenchmarkQueryService;
import com.emsist.designhub.service.ExternalAlignmentAuditService;
import com.emsist.designhub.service.GraphQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/v1/graph")
@RequiredArgsConstructor
@Tag(name = "Graph", description = "Graph-wide object queries and traversal expansion")
public class GraphController {

    private final GraphQueryService graphQueryService;
    private final BenchmarkQueryService benchmarkQueryService;
    private final ExternalAlignmentAuditService externalAlignmentAuditService;

    @GetMapping("/objects")
    @Operation(summary = "Query graph objects by type or across the full graph")
    public ResponseEntity<List<GraphObjectSummaryResponse>> getObjects(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Integer limit
    ) {
        try {
            return ResponseEntity.ok(graphQueryService.getObjects(type, status, module, search, limit));
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @GetMapping("/objects/{type}/{id}/relations")
    @Operation(summary = "Expand a graph object to its incoming and outgoing relations")
    public ResponseEntity<GraphRelationExpansionResponse> expandObject(
            @PathVariable String type,
            @PathVariable String id,
            @RequestParam(required = false) Integer maxNeighbors
    ) {
        try {
            return graphQueryService.expandObject(type, id, maxNeighbors)
                    .map(ResponseEntity::ok)
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, exception.getMessage(), exception);
        }
    }

    @GetMapping("/personas/{personaId}")
    @Operation(summary = "Traverse a persona through journeys, roles, channels, screens, and stories")
    public ResponseEntity<PersonaTraversalResponse> getPersonaTraversal(@PathVariable String personaId) {
        return graphQueryService.getPersonaTraversal(personaId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/personas")
    @Operation(summary = "List personas with journey, screen, story, and channel coverage summaries")
    public ResponseEntity<List<PersonaSummaryResponse>> getPersonas(
            @RequestParam(required = false) String status
    ) {
        return ResponseEntity.ok(graphQueryService.getPersonas(status));
    }

    @GetMapping("/journeys/{journeyId}")
    @Operation(summary = "Traverse a journey through ordered steps, screens, touchpoints, and interactions")
    public ResponseEntity<JourneyTraversalResponse> getJourneyTraversal(@PathVariable String journeyId) {
        return graphQueryService.getJourneyTraversal(journeyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/journey-steps/{stepId}")
    @Operation(summary = "Traverse a journey step through parent journeys, screens, touchpoints, interactions, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getJourneyStepTraversal(@PathVariable String stepId) {
        return typedTraversal("journey-step", stepId);
    }

    @GetMapping("/screen-states/{stateId}")
    @Operation(summary = "Traverse a screen state through its parent screen and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getScreenStateTraversal(@PathVariable String stateId) {
        return typedTraversal("screen-state", stateId);
    }

    @GetMapping("/transitions/{transitionId}")
    @Operation(summary = "Traverse a transition through source screens, target screens, triggering interactions, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getTransitionTraversal(@PathVariable String transitionId) {
        return typedTraversal("transition", transitionId);
    }

    @GetMapping("/touchpoints/{touchpointId}")
    @Operation(summary = "Traverse a touchpoint through channels, target screens, personas, roles, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getTouchpointTraversal(@PathVariable String touchpointId) {
        return typedTraversal("touchpoint", touchpointId);
    }

    @GetMapping("/interactions/{interactionId}")
    @Operation(summary = "Traverse an interaction through screens, APIs, permissions, confirmations, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getInteractionTraversal(@PathVariable String interactionId) {
        return typedTraversal("interaction", interactionId);
    }

    @GetMapping("/apis/{contractId}")
    @Operation(summary = "Traverse an API contract through interactions, schemas, errors, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getApiTraversal(@PathVariable String contractId) {
        return typedTraversal("api", contractId);
    }

    @GetMapping("/data-entities/{entityId}")
    @Operation(summary = "Traverse a data entity through fields, business objects, flows, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getDataEntityTraversal(@PathVariable String entityId) {
        return typedTraversal("data-entity", entityId);
    }

    @GetMapping("/objectives/{objectiveId}")
    @Operation(summary = "Traverse a business objective through linked features, epics, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getObjectiveTraversal(@PathVariable String objectiveId) {
        return typedTraversal("objective", objectiveId);
    }

    @GetMapping("/topics/{topicId}")
    @Operation(summary = "Traverse a topic through grouped journeys, grouped features, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getTopicTraversal(@PathVariable String topicId) {
        return typedTraversal("topic", topicId);
    }

    @GetMapping("/features/{featureId}")
    @Operation(summary = "Traverse a feature through linked stories, epics, objectives, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getFeatureTraversal(@PathVariable String featureId) {
        return typedTraversal("feature", featureId);
    }

    @GetMapping("/decisions/{decisionId}")
    @Operation(summary = "Traverse a decision through affected features, screens, APIs, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getDecisionTraversal(@PathVariable String decisionId) {
        return typedTraversal("decision", decisionId);
    }

    @GetMapping("/assumptions/{assumptionId}")
    @Operation(summary = "Traverse an assumption through underlying features, stories, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getAssumptionTraversal(@PathVariable String assumptionId) {
        return typedTraversal("assumption", assumptionId);
    }

    @GetMapping("/governance-constraints/{constraintId}")
    @Operation(summary = "Traverse a governance constraint through constrained features, APIs, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getGovernanceConstraintTraversal(@PathVariable String constraintId) {
        return typedTraversal("governance-constraint", constraintId);
    }

    @GetMapping("/assessments/{assessmentId}")
    @Operation(summary = "Traverse an assessment through assessed targets, identified gaps, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getAssessmentTraversal(@PathVariable String assessmentId) {
        return typedTraversal("assessment", assessmentId);
    }

    @GetMapping("/risks/{riskId}")
    @Operation(summary = "Traverse a risk through threatened features, stories, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getRiskTraversal(@PathVariable String riskId) {
        return typedTraversal("risk", riskId);
    }

    @GetMapping("/edge-cases/{edgeCaseId}")
    @Operation(summary = "Traverse an edge case through affected stories, screens, journey steps, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getEdgeCaseTraversal(@PathVariable String edgeCaseId) {
        return typedTraversal("edge-case", edgeCaseId);
    }

    @GetMapping("/exception-cases/{exceptionId}")
    @Operation(summary = "Traverse an exception case through affected interactions, APIs, journey steps, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getExceptionCaseTraversal(@PathVariable String exceptionId) {
        return typedTraversal("exception-case", exceptionId);
    }

    @GetMapping("/epics/{epicId}")
    @Operation(summary = "Traverse an epic through linked portfolio, features, stories, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getEpicTraversal(@PathVariable String epicId) {
        return typedTraversal("epic", epicId);
    }

    @GetMapping("/portfolios/{portfolioId}")
    @Operation(summary = "Traverse a requirement portfolio through linked epics, features, stories, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getPortfolioTraversal(@PathVariable String portfolioId) {
        return typedTraversal("portfolio", portfolioId);
    }

    @GetMapping("/bugs/{bugId}")
    @Operation(summary = "Traverse a bug through affected screens, representations, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getBugTraversal(@PathVariable String bugId) {
        return typedTraversal("bug", bugId);
    }

    @GetMapping("/tasks/{taskId}")
    @Operation(summary = "Traverse a task through stories, implementation targets, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getTaskTraversal(@PathVariable String taskId) {
        return typedTraversal("task", taskId);
    }

    @GetMapping("/integrations/{integrationId}")
    @Operation(summary = "Traverse an integration through APIs, external artifacts, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getIntegrationTraversal(@PathVariable String integrationId) {
        return typedTraversal("integration", integrationId);
    }

    @GetMapping("/projects/{projectId}")
    @Operation(summary = "Traverse a project instance through portfolio, capabilities, milestones, tasks, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getProjectTraversal(@PathVariable String projectId) {
        return typedTraversal("project", projectId);
    }

    @GetMapping("/milestones/{milestoneId}")
    @Operation(summary = "Traverse a milestone through owning projects, tasks, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getMilestoneTraversal(@PathVariable String milestoneId) {
        return typedTraversal("milestone", milestoneId);
    }

    @GetMapping("/import-snapshots/{snapshotId}")
    @Operation(summary = "Traverse an import snapshot through imported source artifacts and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getImportSnapshotTraversal(@PathVariable String snapshotId) {
        return typedTraversal("import-snapshot", snapshotId);
    }

    @GetMapping("/evidence-records/{evidenceId}")
    @Operation(summary = "Traverse an evidence record through baselined screens, APIs, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getEvidenceRecordTraversal(@PathVariable String evidenceId) {
        return typedTraversal("evidence-record", evidenceId);
    }

    @GetMapping("/enums/{enumId}")
    @Operation(summary = "Traverse an enum registry entry through data fields and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getEnumTraversal(@PathVariable String enumId) {
        return typedTraversal("enum", enumId);
    }

    @GetMapping("/events/{eventCode}")
    @Operation(summary = "Traverse a domain event through linked integrations and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getEventTraversal(@PathVariable String eventCode) {
        return typedTraversal("event", eventCode);
    }

    @GetMapping("/locales/{localeCode}")
    @Operation(summary = "Traverse a locale through translation keys and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getLocaleTraversal(@PathVariable String localeCode) {
        return typedTraversal("locale", localeCode);
    }

    @GetMapping("/translation-keys/{key}")
    @Operation(summary = "Traverse a translation key through owning locales, messages, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getTranslationKeyTraversal(@PathVariable String key) {
        return typedTraversal("translation-key", key);
    }

    @GetMapping("/business-domains/{domainCode}")
    @Operation(summary = "Traverse a business domain through capabilities and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getBusinessDomainTraversal(@PathVariable String domainCode) {
        return typedTraversal("business-domain", domainCode);
    }

    @GetMapping("/capabilities/{capabilityId}")
    @Operation(summary = "Traverse a business capability through processes and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getCapabilityTraversal(@PathVariable String capabilityId) {
        return typedTraversal("capability", capabilityId);
    }

    @GetMapping("/processes/{processId}")
    @Operation(summary = "Traverse a business process through flow nodes and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getProcessTraversal(@PathVariable String processId) {
        return typedTraversal("process", processId);
    }

    @GetMapping("/process-activities/{activityId}")
    @Operation(summary = "Traverse a process activity through parent processes, flow transitions, subprocess expansion, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getProcessActivityTraversal(@PathVariable String activityId) {
        return typedTraversal("process-activity", activityId);
    }

    @GetMapping("/process-gateways/{gatewayId}")
    @Operation(summary = "Traverse a process gateway through parent processes, routed activities, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getProcessGatewayTraversal(@PathVariable String gatewayId) {
        return typedTraversal("process-gateway", gatewayId);
    }

    @GetMapping("/process-events/{eventId}")
    @Operation(summary = "Traverse a process event through parent processes, attached activities, routed activities, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getProcessEventTraversal(@PathVariable String eventId) {
        return typedTraversal("process-event", eventId);
    }

    @GetMapping("/applications/{applicationId}")
    @Operation(summary = "Traverse an application through components, dependencies, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getApplicationTraversal(@PathVariable String applicationId) {
        return typedTraversal("application", applicationId);
    }

    @GetMapping("/organizations/{orgId}")
    @Operation(summary = "Traverse an organization through owned applications and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getOrganizationTraversal(@PathVariable String orgId) {
        return typedTraversal("organization", orgId);
    }

    @GetMapping("/components/{componentId}")
    @Operation(summary = "Traverse an application component through screens, APIs, data, code assets, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getComponentTraversal(@PathVariable String componentId) {
        return typedTraversal("component", componentId);
    }

    @GetMapping("/business-objects/{objectId}")
    @Operation(summary = "Traverse a business object through mapped data entities and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getBusinessObjectTraversal(@PathVariable String objectId) {
        return typedTraversal("business-object", objectId);
    }

    @GetMapping("/information-flows/{flowId}")
    @Operation(summary = "Traverse an information flow through carried business objects, exposed APIs, source applications, target applications, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getInformationFlowTraversal(@PathVariable String flowId) {
        return typedTraversal("information-flow", flowId);
    }

    @GetMapping("/deployments/{deploymentId}")
    @Operation(summary = "Traverse a deployment through hosted components and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getDeploymentTraversal(@PathVariable String deploymentId) {
        return typedTraversal("deployment", deploymentId);
    }

    @GetMapping("/infrastructure-nodes/{nodeId}")
    @Operation(summary = "Traverse an infrastructure node through deployments, hosted applications, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getInfrastructureNodeTraversal(@PathVariable String nodeId) {
        return typedTraversal("infrastructure-node", nodeId);
    }

    @GetMapping("/validation-rules/{validationRuleId}")
    @Operation(summary = "Traverse a validation rule through parent rules, enforced screens, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getValidationRuleTraversal(@PathVariable String validationRuleId) {
        return typedTraversal("validation-rule", validationRuleId);
    }

    @GetMapping("/request-schemas/{schemaId}")
    @Operation(summary = "Traverse a request schema through parent API contracts and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getRequestSchemaTraversal(@PathVariable String schemaId) {
        return typedTraversal("request-schema", schemaId);
    }

    @GetMapping("/response-schemas/{schemaId}")
    @Operation(summary = "Traverse a response schema through parent API contracts and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getResponseSchemaTraversal(@PathVariable String schemaId) {
        return typedTraversal("response-schema", schemaId);
    }

    @GetMapping("/error-contracts/{errorContractId}")
    @Operation(summary = "Traverse an error contract through parent API contracts and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getErrorContractTraversal(@PathVariable String errorContractId) {
        return typedTraversal("error-contract", errorContractId);
    }

    @GetMapping("/code-assets/{codeAssetId}")
    @Operation(summary = "Traverse a code asset through owning components, governed conventions, and implemented graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getCodeAssetTraversal(@PathVariable String codeAssetId) {
        return typedTraversal("code-asset", codeAssetId);
    }

    @GetMapping("/test-cases/{testCaseId}")
    @Operation(summary = "Traverse a test case through verified stories, verified screens, located code assets, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getTestCaseTraversal(@PathVariable String testCaseId) {
        return typedTraversal("test-case", testCaseId);
    }

    @GetMapping("/rules/{ruleId}")
    @Operation(summary = "Traverse a rule through governed stories, enforcing components, validation rules, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getRuleTraversal(@PathVariable String ruleId) {
        return typedTraversal("rule", ruleId);
    }

    @GetMapping("/messages/{messageId}")
    @Operation(summary = "Traverse a message through surfaced screens and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getMessageTraversal(@PathVariable String messageId) {
        return typedTraversal("message", messageId);
    }

    @GetMapping("/gaps/{gapId}")
    @Operation(summary = "Traverse a gap through impacted graph objects and related remediation context")
    public ResponseEntity<GraphRelationExpansionResponse> getGapTraversal(@PathVariable String gapId) {
        return typedTraversal("gap", gapId);
    }

    @GetMapping("/conventions/{conventionCode}")
    @Operation(summary = "Traverse a coding convention through governed applications, components, code assets, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getConventionTraversal(@PathVariable String conventionCode) {
        return typedTraversal("convention", conventionCode);
    }

    @GetMapping("/quality-constraints/{constraintId}")
    @Operation(summary = "Traverse a quality constraint through constrained screens, APIs, entities, tests, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getQualityConstraintTraversal(@PathVariable String constraintId) {
        return typedTraversal("quality-constraint", constraintId);
    }

    @GetMapping("/policies/{policyId}")
    @Operation(summary = "Traverse an agent policy through governed applications, governed components, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getPolicyTraversal(@PathVariable String policyId) {
        return typedTraversal("policy", policyId);
    }

    @GetMapping("/sources/{sourceId}")
    @Operation(summary = "Traverse a source reference through linked artifacts and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getSourceTraversal(@PathVariable String sourceId) {
        return typedTraversal("source", sourceId);
    }

    @GetMapping("/findings/{findingId}")
    @Operation(summary = "Traverse a finding through represented artifacts, affected objects, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getFindingTraversal(@PathVariable String findingId) {
        return typedTraversal("finding", findingId);
    }

    @GetMapping("/open-questions/{questionId}")
    @Operation(summary = "Traverse an open question through blocked features, screens, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getOpenQuestionTraversal(@PathVariable String questionId) {
        return typedTraversal("open-question", questionId);
    }

    @GetMapping("/acceptance-criteria/{criterionId}")
    @Operation(summary = "Traverse an acceptance criterion through linked stories and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getAcceptanceCriterionTraversal(@PathVariable String criterionId) {
        return typedTraversal("acceptance-criterion", criterionId);
    }

    @GetMapping("/data-fields/{fieldId}")
    @Operation(summary = "Traverse a data field through parent entities, validation rules, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getDataFieldTraversal(@PathVariable String fieldId) {
        return typedTraversal("data-field", fieldId);
    }

    @GetMapping("/roles/{roleKey}")
    @Operation(summary = "Traverse a business role through accessible screens, touchpoints, interactions, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getBusinessRoleTraversal(@PathVariable String roleKey) {
        return typedTraversal("role", roleKey);
    }

    @GetMapping("/validation-roles/{validationRoleKey}")
    @Operation(summary = "Traverse a validation role through related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getValidationRoleTraversal(@PathVariable String validationRoleKey) {
        return typedTraversal("validation-role", validationRoleKey);
    }

    @GetMapping("/permissions/{permissionKey}")
    @Operation(summary = "Traverse a permission through protected interactions and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getPermissionTraversal(@PathVariable String permissionKey) {
        return typedTraversal("permission", permissionKey);
    }

    @GetMapping("/dialogs/{dialogId}")
    @Operation(summary = "Traverse a confirmation dialog through triggering interactions and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getDialogTraversal(@PathVariable String dialogId) {
        return typedTraversal("dialog", dialogId);
    }

    @GetMapping("/error-codes/{code}")
    @Operation(summary = "Traverse an error code through linked interactions, screens, and related graph objects")
    public ResponseEntity<GraphRelationExpansionResponse> getErrorCodeTraversal(@PathVariable String code) {
        return typedTraversal("error-code", code);
    }

    @GetMapping("/channels")
    @Operation(summary = "List channels with touchpoint and screen coverage summaries")
    public ResponseEntity<List<ChannelSummaryResponse>> getChannels(
            @RequestParam(required = false) String channelType
    ) {
        return ResponseEntity.ok(graphQueryService.getChannels(channelType));
    }

    @GetMapping("/channels/{channelCode}")
    @Operation(summary = "Traverse a channel through touchpoints, screens, coverage gaps, and reachable personas")
    public ResponseEntity<ChannelTraversalResponse> getChannelTraversal(@PathVariable String channelCode) {
        return graphQueryService.getChannelTraversal(channelCode)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/benchmark")
    @Operation(summary = "Get benchmark aggregation for the current primary graph object types")
    public ResponseEntity<GraphBenchmarkResponse> getBenchmark() {
        return ResponseEntity.ok(benchmarkQueryService.getBenchmark());
    }

    @GetMapping("/external-artifacts")
    @Operation(summary = "List external artifacts with sync, hierarchy, and representation coverage summaries")
    public ResponseEntity<List<ExternalArtifactSummaryResponse>> getExternalArtifacts(
            @RequestParam(required = false) String system,
            @RequestParam(required = false) String syncStatus
    ) {
        return ResponseEntity.ok(graphQueryService.getExternalArtifacts(system, syncStatus));
    }

    @GetMapping("/external-artifacts/{externalId}")
    @Operation(summary = "Traverse an external artifact through hierarchy, dependencies, duplicates, and represented domain objects")
    public ResponseEntity<ExternalArtifactTraversalResponse> getExternalArtifact(@PathVariable String externalId) {
        return graphQueryService.getExternalArtifact(externalId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/external-artifacts/parity-audit")
    @Operation(summary = "Audit parity coverage for the canonical external delivery-system fields")
    public ResponseEntity<ExternalParityAuditResponse> getExternalParityAudit() {
        return ResponseEntity.ok(externalAlignmentAuditService.getParityAudit());
    }

    @GetMapping("/traceability/stories/{storyId}")
    @Operation(summary = "Traverse a story through the current upstream and downstream implementation spine")
    public ResponseEntity<TraceabilityStoryResponse> getStoryTraceability(@PathVariable String storyId) {
        return graphQueryService.getStoryTraceability(storyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/architecture/business/capabilities")
    @Operation(summary = "List business capabilities with linked process, application, feature, and ownership summaries")
    public ResponseEntity<List<BusinessCapabilitySummaryResponse>> getBusinessCapabilities(
            @RequestParam(required = false) String domain
    ) {
        return ResponseEntity.ok(graphQueryService.getBusinessCapabilities(domain));
    }

    @GetMapping("/architecture/business/capabilities/{capabilityId}")
    @Operation(summary = "Traverse a business capability through processes, supporting applications, features, and owning organizations")
    public ResponseEntity<BusinessArchitectureResponse> getBusinessArchitecture(@PathVariable String capabilityId) {
        return graphQueryService.getBusinessArchitecture(capabilityId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/architecture/applications")
    @Operation(summary = "List applications with component, API, screen, feature, and dependency summaries")
    public ResponseEntity<List<ApplicationSummaryResponse>> getApplications(
            @RequestParam(required = false) String applicationType
    ) {
        return ResponseEntity.ok(graphQueryService.getApplications(applicationType));
    }

    @GetMapping("/architecture/applications/{applicationId}")
    @Operation(summary = "Traverse an application through components, APIs, screens, features, and application dependencies")
    public ResponseEntity<ApplicationArchitectureResponse> getApplicationArchitecture(@PathVariable String applicationId) {
        return graphQueryService.getApplicationArchitecture(applicationId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/architecture/data/business-objects")
    @Operation(summary = "List business objects with mapped entities, information flows, API exposure, and screen support summaries")
    public ResponseEntity<List<DataArchitectureObjectSummaryResponse>> getDataObjects(
            @RequestParam(required = false) String domain
    ) {
        return ResponseEntity.ok(graphQueryService.getDataObjects(domain));
    }

    @GetMapping("/architecture/data/business-objects/{objectId}")
    @Operation(summary = "Traverse a business object through mapped data entities, information flows, APIs, screens, and child business objects")
    public ResponseEntity<DataArchitectureResponse> getDataArchitecture(@PathVariable String objectId) {
        return graphQueryService.getDataArchitecture(objectId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/architecture/infrastructure/deployments")
    @Operation(summary = "List deployments with hosted component, application, and infrastructure summaries")
    public ResponseEntity<List<InfrastructureDeploymentSummaryResponse>> getInfrastructureDeployments(
            @RequestParam(required = false) String environment
    ) {
        return ResponseEntity.ok(graphQueryService.getInfrastructureDeployments(environment));
    }

    @GetMapping("/architecture/infrastructure/deployments/{deploymentId}")
    @Operation(summary = "Traverse a deployment through hosted components, infrastructure nodes, and related applications")
    public ResponseEntity<InfrastructureArchitectureResponse> getInfrastructureArchitecture(@PathVariable String deploymentId) {
        return graphQueryService.getInfrastructureArchitecture(deploymentId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    private ResponseEntity<GraphRelationExpansionResponse> typedTraversal(String type, String id) {
        return graphQueryService.getObjectTraversal(type, id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
