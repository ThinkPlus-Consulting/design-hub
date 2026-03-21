package com.emsist.designhub.controller;

import com.emsist.designhub.dto.GraphNodeReference;
import com.emsist.designhub.dto.GraphBenchmarkResponse;
import com.emsist.designhub.dto.GraphObjectSummaryResponse;
import com.emsist.designhub.dto.GraphRelationExpansionResponse;
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
import com.emsist.designhub.dto.PersonaSummaryResponse;
import com.emsist.designhub.service.BenchmarkQueryService;
import com.emsist.designhub.service.ExternalAlignmentAuditService;
import com.emsist.designhub.service.GraphQueryService;
import com.emsist.designhub.dto.TraceabilityStoryResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GraphControllerTest {

    @Mock
    private GraphQueryService graphQueryService;

    @Mock
    private BenchmarkQueryService benchmarkQueryService;

    @Mock
    private ExternalAlignmentAuditService externalAlignmentAuditService;

    @InjectMocks
    private GraphController graphController;

    @Test
    void shouldReturnGraphObjects() {
        when(graphQueryService.getObjects("screen", "APPROVED", "core", "login", 10))
                .thenReturn(List.of(new GraphObjectSummaryResponse(
                        "SCR-AUTH",
                        "Screen",
                        "Login / Sign In",
                        "APPROVED",
                        "core",
                        "auth",
                        "/auth/login",
                        6L
                )));

        var response = graphController.getObjects("screen", "APPROVED", "core", "login", 10);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("SCR-AUTH", response.getBody().get(0).id());
    }

    @Test
    void shouldTranslateInvalidTypeToBadRequest() {
        when(graphQueryService.getObjects("invalid", null, null, null, null))
                .thenThrow(new IllegalArgumentException("Unsupported graph object type: invalid"));

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> graphController.getObjects("invalid", null, null, null, null));

        assertEquals(400, exception.getStatusCode().value());
    }

    @Test
    void shouldReturnExpandedRelations() {
        when(graphQueryService.expandObject("screen", "SCR-AUTH", 5))
                .thenReturn(Optional.of(new GraphRelationExpansionResponse(
                        new GraphObjectSummaryResponse("SCR-AUTH", "Screen", "Login / Sign In", "APPROVED", "core", "auth", "/auth/login", 5L),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "DELIVERS",
                                "INCOMING",
                                new GraphNodeReference("US-AUTH-001", "UserStory", "User can sign in", "APPROVED")
                        )),
                        List.of()
                )));

        var response = graphController.expandObject("screen", "SCR-AUTH", 5);

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().outgoing().size());
    }

    @Test
    void shouldReturnCodeAssetTraversal() {
        when(graphQueryService.getObjectTraversal("code-asset", "CA-FE-DH-PAGE-001"))
                .thenReturn(Optional.of(new GraphRelationExpansionResponse(
                        new GraphObjectSummaryResponse(
                                "CA-FE-DH-PAGE-001",
                                "CodeAsset",
                                "src/app/features/design-hub/design-hub.page.ts",
                                "IMPLEMENTED",
                                null,
                                null,
                                null,
                                3L
                        ),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "ASSET_FOR_SCREEN",
                                "OUTGOING",
                                new GraphNodeReference("SCR-AGT-LIST", "Screen", "Agent List", "DEFINED")
                        )),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "HAS_CODE_ASSET",
                                "INCOMING",
                                new GraphNodeReference("CMP-DH-FRONTEND", "ApplicationComponent", "Design Hub Frontend", "IMPLEMENTED")
                        ))
                )));

        var response = graphController.getCodeAssetTraversal("CA-FE-DH-PAGE-001");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("CA-FE-DH-PAGE-001", response.getBody().root().id());
        assertEquals("ASSET_FOR_SCREEN", response.getBody().outgoing().get(0).relationType());
    }

    @Test
    void shouldReturnValidationRuleTraversal() {
        when(graphQueryService.getObjectTraversal("validation-rule", "VR-AUTH-001"))
                .thenReturn(Optional.of(new GraphRelationExpansionResponse(
                        new GraphObjectSummaryResponse(
                                "VR-AUTH-001",
                                "ValidationRule",
                                "Password must include upper, lower, number, and be at least 8 characters.",
                                "DEFINED",
                                null,
                                null,
                                null,
                                2L
                        ),
                        List.of(),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "HAS_VALIDATION_RULE",
                                "INCOMING",
                                new GraphNodeReference("RULE-AUTH-001", "Rule", "Password policy", "DEFINED")
                        ))
                )));

        var response = graphController.getValidationRuleTraversal("VR-AUTH-001");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("VR-AUTH-001", response.getBody().root().id());
        assertEquals("HAS_VALIDATION_RULE", response.getBody().incoming().get(0).relationType());
    }

    @Test
    void shouldReturnProcessActivityTraversal() {
        when(graphQueryService.getObjectTraversal("process-activity", "ACT-PROC-SCREEN-REVIEW-001"))
                .thenReturn(Optional.of(new GraphRelationExpansionResponse(
                        new GraphObjectSummaryResponse(
                                "ACT-PROC-SCREEN-REVIEW-001",
                                "ProcessActivity",
                                "Review screen design",
                                "DEFINED",
                                null,
                                null,
                                null,
                                3L
                        ),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "FLOWS_TO",
                                "OUTGOING",
                                new GraphNodeReference("ACT-PROC-SCREEN-REVIEW-002", "ProcessActivity", "Capture decision", "DEFINED")
                        )),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "HAS_FLOW_NODE",
                                "INCOMING",
                                new GraphNodeReference("PROC-SCREEN-REVIEW", "BusinessProcess", "Screen Review Process", "DEFINED")
                        ))
                )));

        var response = graphController.getProcessActivityTraversal("ACT-PROC-SCREEN-REVIEW-001");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("ACT-PROC-SCREEN-REVIEW-001", response.getBody().root().id());
        assertEquals("FLOWS_TO", response.getBody().outgoing().get(0).relationType());
    }

    @Test
    void shouldReturnJourneyStepTraversal() {
        when(graphQueryService.getObjectTraversal("journey-step", "JRN-R05-001.06"))
                .thenReturn(Optional.of(new GraphRelationExpansionResponse(
                        new GraphObjectSummaryResponse(
                                "JRN-R05-001.06",
                                "JourneyStep",
                                "Save draft",
                                "DEFINED",
                                null,
                                null,
                                null,
                                4L
                        ),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "USES_SCREEN",
                                "OUTGOING",
                                new GraphNodeReference("SCR-AGT-BUILDER", "Screen", "Agent Builder", "DEFINED")
                        )),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "HAS_STEP",
                                "INCOMING",
                                new GraphNodeReference("JRN-R05-001", "Journey", "Compose a new AI agent", "DEFINED")
                        ))
                )));

        var response = graphController.getJourneyStepTraversal("JRN-R05-001.06");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("JRN-R05-001.06", response.getBody().root().id());
        assertEquals("USES_SCREEN", response.getBody().outgoing().get(0).relationType());
    }

    @Test
    void shouldReturnLocaleTraversal() {
        when(graphQueryService.getObjectTraversal("locale", "ar"))
                .thenReturn(Optional.of(new GraphRelationExpansionResponse(
                        new GraphObjectSummaryResponse(
                                "ar",
                                "Locale",
                                "Arabic",
                                null,
                                null,
                                null,
                                null,
                                1L
                        ),
                        List.of(new GraphRelationExpansionResponse.RelationEdge(
                                "HAS_TRANSLATIONS",
                                "OUTGOING",
                                new GraphNodeReference("auth.invalid_credentials.ar", "TranslationKey", "البريد الإلكتروني أو كلمة المرور غير صحيحة.", null)
                        )),
                        List.of()
                )));

        var response = graphController.getLocaleTraversal("ar");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("ar", response.getBody().root().id());
        assertEquals("HAS_TRANSLATIONS", response.getBody().outgoing().get(0).relationType());
    }

    @Test
    void shouldReturnBenchmarkAggregation() {
        when(benchmarkQueryService.getBenchmark()).thenReturn(new GraphBenchmarkResponse(
                new GraphBenchmarkResponse.BenchmarkSummary(
                        "scope",
                        2,
                        10L,
                        76.5,
                        List.of(new GraphBenchmarkResponse.BenchmarkDimensionScore(
                                "attributeDepth",
                                74.0,
                                "AMBER",
                                "detail"
                        ))
                ),
                List.of(new GraphBenchmarkResponse.BenchmarkTypeScore(
                        "Screen",
                        5L,
                        11,
                        81.0,
                        6,
                        70.0,
                        true,
                        40.0,
                        100.0,
                        72.8,
                        List.of("Source traceability coverage is sparse for this artifact type.")
                ))
        ));

        var response = graphController.getBenchmark();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(76.5, response.getBody().summary().overallScore());
        assertEquals(1, response.getBody().types().size());
    }

    @Test
    void shouldReturnExternalArtifactSummaries() {
        when(graphQueryService.getExternalArtifacts("JIRA", "SYNCED"))
                .thenReturn(List.of(new ExternalArtifactSummaryResponse(
                        "EXT-JIRA-001",
                        "JIRA",
                        "STORY",
                        "DH-101",
                        "User sign-in and session recovery",
                        "Design Hub / Identity",
                        "In Progress",
                        "High",
                        "Aisha Coleman",
                        "Marco Lane",
                        List.of("design-hub", "story", "auth"),
                        "https://jira.example.com/browse/DH-101",
                        "SYNCED",
                        "2026-03-18T08:00Z",
                        "DEFINED",
                        1L,
                        1L,
                        1L,
                        1L
                )));

        var response = graphController.getExternalArtifacts("JIRA", "SYNCED");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("EXT-JIRA-001", response.getBody().get(0).externalId());
    }

    @Test
    void shouldReturnExternalArtifactTraversal() {
        when(graphQueryService.getExternalArtifact("EXT-JIRA-001"))
                .thenReturn(Optional.of(new ExternalArtifactTraversalResponse(
                        "EXT-JIRA-001",
                        "JIRA",
                        "STORY",
                        "DH-101",
                        "User sign-in and session recovery",
                        "Design Hub / Identity",
                        "In Progress",
                        "High",
                        "Aisha Coleman",
                        "Marco Lane",
                        List.of("design-hub", "story", "auth"),
                        Map.of("area", "Design Hub", "iteration", "Sprint 24"),
                        "https://jira.example.com/browse/DH-101",
                        "SYNCED",
                        "2026-03-18T08:00Z",
                        "DEFINED",
                        List.of(new ExternalArtifactTraversalResponse.ArtifactLinkSummary(
                                "EXT-JIRA-EPIC-001",
                                "JIRA",
                                "EPIC",
                                "DH-100",
                                "Access and authentication hardening",
                                "In Progress",
                                "SYNCED",
                                "DEFINED"
                        )),
                        List.of(),
                        List.of(new ExternalArtifactTraversalResponse.ArtifactLinkSummary(
                                "EXT-AZDO-001",
                                "AZURE_DEVOPS",
                                "BUG",
                                "AB#245",
                                "Retry banner remains visible after successful login",
                                "Active",
                                "SYNCED",
                                "DEFINED"
                        )),
                        List.of(),
                        List.of(),
                        List.of(new GraphNodeReference("US-AUTH-001", "UserStory", "User can sign in", "APPROVED"))
                )));

        var response = graphController.getExternalArtifact("EXT-JIRA-001");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("EXT-JIRA-001", response.getBody().externalId());
        assertEquals("Sprint 24", response.getBody().customFields().get("iteration"));
        assertEquals(1, response.getBody().parents().size());
        assertEquals(1, response.getBody().representedObjects().size());
    }

    @Test
    void shouldReturnExternalParityAudit() {
        when(externalAlignmentAuditService.getParityAudit()).thenReturn(new ExternalParityAuditResponse(
                new ExternalParityAuditResponse.Summary(
                        5L,
                        10,
                        88.0,
                        "GREEN",
                        3L,
                        1L,
                        1L,
                        1L
                ),
                List.of(new ExternalParityAuditResponse.SystemCoverage(
                        "JIRA",
                        3L,
                        93.3,
                        2L,
                        1L,
                        List.of("reporter", "priority", "lastSyncedAt")
                )),
                List.of(new ExternalParityAuditResponse.FieldCoverage(
                        "title",
                        5L,
                        0L,
                        100.0,
                        List.of()
                ))
        ));

        var response = graphController.getExternalParityAudit();

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(88.0, response.getBody().summary().overallCoverageScore());
        assertEquals(1, response.getBody().systems().size());
    }

    @Test
    void shouldReturnNotFoundForMissingJourneyTraversal() {
        when(graphQueryService.getJourneyTraversal("JRN-MISSING")).thenReturn(Optional.empty());

        var response = graphController.getJourneyTraversal("JRN-MISSING");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }

    @Test
    void shouldReturnChannelSummaries() {
        when(graphQueryService.getChannels("WEB"))
                .thenReturn(List.of(new ChannelSummaryResponse(
                        "CH-WEB-DSK",
                        "Web Desktop",
                        "WEB",
                        8L,
                        6L
                )));

        var response = graphController.getChannels("WEB");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("CH-WEB-DSK", response.getBody().get(0).channelCode());
    }

    @Test
    void shouldReturnChannelTraversal() {
        when(graphQueryService.getChannelTraversal("CH-WEB-DSK"))
                .thenReturn(Optional.of(new ChannelTraversalResponse(
                        "CH-WEB-DSK",
                        "Web Desktop",
                        "WEB",
                        List.of(new ChannelTraversalResponse.TouchpointSummary(
                                "TP-GALLERY-MENU",
                                "Gallery menu entry",
                                "SCR-AGT-GALLERY",
                                List.of("Left-nav Gallery menu item click"),
                                List.of(),
                                List.of(),
                                new GraphNodeReference("SCR-AGT-GALLERY", "Screen", "Template Gallery", "IN_DEFINITION")
                        )),
                        List.of(new GraphNodeReference("SCR-AGT-GALLERY", "Screen", "Template Gallery", "IN_DEFINITION")),
                        List.of(),
                        List.of(new GraphNodeReference("PER-UX-007", "Persona", "PER-UX-007", "IDENTIFIED"))
                )));

        var response = graphController.getChannelTraversal("CH-WEB-DSK");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("CH-WEB-DSK", response.getBody().channelCode());
        assertEquals(1, response.getBody().touchpoints().size());
        assertEquals(1, response.getBody().personaReach().size());
    }

    @Test
    void shouldReturnDedicatedArtifactTraversals() {
        when(graphQueryService.getObjectTraversal("screen-state", "STATE-SCR-AUTH-LOADING"))
                .thenReturn(Optional.of(simpleTraversal("STATE-SCR-AUTH-LOADING", "ScreenState", "Authenticating")));
        when(graphQueryService.getObjectTraversal("transition", "TRN-SCR-AUTH-TO-DASH"))
                .thenReturn(Optional.of(simpleTraversal("TRN-SCR-AUTH-TO-DASH", "Transition", "Login success redirect")));
        when(graphQueryService.getObjectTraversal("touchpoint", "TP-AGT-DOCK"))
                .thenReturn(Optional.of(simpleTraversal("TP-AGT-DOCK", "Touchpoint", "Agent Manager dock entry")));
        when(graphQueryService.getObjectTraversal("interaction", "INT-R05-BUILDER-001"))
                .thenReturn(Optional.of(simpleTraversal("INT-R05-BUILDER-001", "Interaction", "Component from palette")));
        when(graphQueryService.getObjectTraversal("api", "API-POST-API-V1-AUTH-LOGIN"))
                .thenReturn(Optional.of(simpleTraversal("API-POST-API-V1-AUTH-LOGIN", "ApiContract", "POST /api/v1/auth/login")));
        when(graphQueryService.getObjectTraversal("data-entity", "DE-AGENT"))
                .thenReturn(Optional.of(simpleTraversal("DE-AGENT", "DataEntity", "Agent")));
        when(graphQueryService.getObjectTraversal("objective", "OBJ-DH-AI-001"))
                .thenReturn(Optional.of(simpleTraversal("OBJ-DH-AI-001", "BusinessObjective", "Enable agent designers to compose and publish AI agents")));
        when(graphQueryService.getObjectTraversal("topic", "TOP-001"))
                .thenReturn(Optional.of(simpleTraversal("TOP-001", "Topic", "AI agent composition")));
        when(graphQueryService.getObjectTraversal("feature", "FEAT-AI"))
                .thenReturn(Optional.of(simpleTraversal("FEAT-AI", "Feature", "Agent Builder")));
        when(graphQueryService.getObjectTraversal("decision", "DEC-001"))
                .thenReturn(Optional.of(simpleTraversal("DEC-001", "Decision", "Use agent pack export as the canonical automation contract")));
        when(graphQueryService.getObjectTraversal("assumption", "ASM-001"))
                .thenReturn(Optional.of(simpleTraversal("ASM-001", "Assumption", "Agent designers primarily compose and validate complex agents on desktop-class surfaces with reviewer collaboration available.")));
        when(graphQueryService.getObjectTraversal("governance-constraint", "CON-001"))
                .thenReturn(Optional.of(simpleTraversal("CON-001", "Constraint", "Publishing agents must preserve explicit approval evidence and graph-backed readiness context.")));
        when(graphQueryService.getObjectTraversal("assessment", "ASSESS-API-002"))
                .thenReturn(Optional.of(simpleTraversal("ASSESS-API-002", "Assessment", "Agent publish contract security assessment")));
        when(graphQueryService.getObjectTraversal("risk", "RSK-001"))
                .thenReturn(Optional.of(simpleTraversal("RSK-001", "Risk", "Publish readiness gaps could allow partially verified agents to ship")));
        when(graphQueryService.getObjectTraversal("edge-case", "EDGE-001"))
                .thenReturn(Optional.of(simpleTraversal("EDGE-001", "EdgeCase", "Reviewer resumes builder editing after unsaved draft work")));
        when(graphQueryService.getObjectTraversal("exception-case", "EXC-001"))
                .thenReturn(Optional.of(simpleTraversal("EXC-001", "ExceptionCase", "Identity service outage during login")));
        when(graphQueryService.getObjectTraversal("epic", "EPIC-AI-001"))
                .thenReturn(Optional.of(simpleTraversal("EPIC-AI-001", "Epic", "Agent builder and orchestration")));
        when(graphQueryService.getObjectTraversal("portfolio", "PORT-DH-001"))
                .thenReturn(Optional.of(simpleTraversal("PORT-DH-001", "RequirementPortfolio", "Design Hub Delivery Portfolio")));
        when(graphQueryService.getObjectTraversal("bug", "BUG-LOGIN-001"))
                .thenReturn(Optional.of(simpleTraversal("BUG-LOGIN-001", "Bug", "Retry banner remains visible")));
        when(graphQueryService.getObjectTraversal("task", "TASK-AI-001"))
                .thenReturn(Optional.of(simpleTraversal("TASK-AI-001", "Task", "Implement agent builder shell")));
        when(graphQueryService.getObjectTraversal("integration", "INTG-001"))
                .thenReturn(Optional.of(simpleTraversal("INTG-001", "Integration", "Design Hub to Identity Platform sign-in integration")));
        when(graphQueryService.getObjectTraversal("project", "PROJ-DH-AI-001"))
                .thenReturn(Optional.of(simpleTraversal("PROJ-DH-AI-001", "ProjectInstance", "Design Hub AI builder delivery wave")));
        when(graphQueryService.getObjectTraversal("milestone", "MS-DH-AI-001"))
                .thenReturn(Optional.of(simpleTraversal("MS-DH-AI-001", "Milestone", "Builder delivery checkpoint")));
        when(graphQueryService.getObjectTraversal("business-domain", "DOM-DESIGN"))
                .thenReturn(Optional.of(simpleTraversal("DOM-DESIGN", "BusinessDomain", "Design Management")));
        when(graphQueryService.getObjectTraversal("capability", "CAP-SCREEN-MGMT"))
                .thenReturn(Optional.of(simpleTraversal("CAP-SCREEN-MGMT", "BusinessCapability", "Screen Management")));
        when(graphQueryService.getObjectTraversal("process", "PROC-SCREEN-BUILD"))
                .thenReturn(Optional.of(simpleTraversal("PROC-SCREEN-BUILD", "BusinessProcess", "Screen Builder Process")));
        when(graphQueryService.getObjectTraversal("application", "APP-DH"))
                .thenReturn(Optional.of(simpleTraversal("APP-DH", "Application", "Design Hub")));
        when(graphQueryService.getObjectTraversal("organization", "ORG-DH-PLATFORM"))
                .thenReturn(Optional.of(simpleTraversal("ORG-DH-PLATFORM", "Organization", "Design Hub Platform Team")));
        when(graphQueryService.getObjectTraversal("component", "CMP-DH-FRONTEND"))
                .thenReturn(Optional.of(simpleTraversal("CMP-DH-FRONTEND", "ApplicationComponent", "Design Hub Frontend")));
        when(graphQueryService.getObjectTraversal("business-object", "BO-AGENT-CONFIG"))
                .thenReturn(Optional.of(simpleTraversal("BO-AGENT-CONFIG", "BusinessObject", "Agent Configuration")));
        when(graphQueryService.getObjectTraversal("deployment", "DEP-DEV-001"))
                .thenReturn(Optional.of(simpleTraversal("DEP-DEV-001", "Deployment", "Design Hub Dev Environment")));
        when(graphQueryService.getObjectTraversal("convention", "CONV-API-IDEMPOTENCY"))
                .thenReturn(Optional.of(simpleTraversal("CONV-API-IDEMPOTENCY", "CodingConvention", "API idempotency policy")));
        when(graphQueryService.getObjectTraversal("quality-constraint", "QC-API-P95"))
                .thenReturn(Optional.of(simpleTraversal("QC-API-P95", "QualityConstraint", "API p95 latency threshold")));
        when(graphQueryService.getObjectTraversal("policy", "POL-DH-AGENT-001"))
                .thenReturn(Optional.of(simpleTraversal("POL-DH-AGENT-001", "AgentPolicy", "Design Hub agent policy")));
        when(graphQueryService.getObjectTraversal("source", "SRC-US-AI-090"))
                .thenReturn(Optional.of(simpleTraversal("SRC-US-AI-090", "SourceReference", "documentation/vision-benchmark.md")));
        when(graphQueryService.getObjectTraversal("finding", "FND-001"))
                .thenReturn(Optional.of(simpleTraversal("FND-001", "Finding", "Builder publish review gap")));
        when(graphQueryService.getObjectTraversal("open-question", "OQ-001"))
                .thenReturn(Optional.of(simpleTraversal("OQ-001", "OpenQuestion", "What approval evidence is minimally required before a draft agent can enter the publish flow?")));
        when(graphQueryService.getObjectTraversal("acceptance-criterion", "AC-US-AI-090-001"))
                .thenReturn(Optional.of(simpleTraversal("AC-US-AI-090-001", "AcceptanceCriterion", "Canvas selection stays synchronized with the detail context")));
        when(graphQueryService.getObjectTraversal("data-field", "DF-DE-AGENT-001"))
                .thenReturn(Optional.of(simpleTraversal("DF-DE-AGENT-001", "DataField", "agentName")));
        when(graphQueryService.getObjectTraversal("role", "ARCHITECT"))
                .thenReturn(Optional.of(simpleTraversal("ARCHITECT", "BusinessRole", "Architect")));
        when(graphQueryService.getObjectTraversal("validation-role", "HITL_REVIEWER"))
                .thenReturn(Optional.of(simpleTraversal("HITL_REVIEWER", "ValidationRole", "HITL Reviewer")));
        when(graphQueryService.getObjectTraversal("permission", "ADMIN"))
                .thenReturn(Optional.of(simpleTraversal("ADMIN", "Permission", "Administrator")));
        when(graphQueryService.getObjectTraversal("dialog", "CONFIRM-AGT-PUBLISH"))
                .thenReturn(Optional.of(simpleTraversal("CONFIRM-AGT-PUBLISH", "ConfirmationDialog", "Publish agent")));
        when(graphQueryService.getObjectTraversal("error-code", "AGT-E-403"))
                .thenReturn(Optional.of(simpleTraversal("AGT-E-403", "ErrorCode", "Agent action is not permitted.")));

        assertEquals("STATE-SCR-AUTH-LOADING", graphController.getScreenStateTraversal("STATE-SCR-AUTH-LOADING").getBody().root().id());
        assertEquals("TRN-SCR-AUTH-TO-DASH", graphController.getTransitionTraversal("TRN-SCR-AUTH-TO-DASH").getBody().root().id());
        assertEquals("TP-AGT-DOCK", graphController.getTouchpointTraversal("TP-AGT-DOCK").getBody().root().id());
        assertEquals("INT-R05-BUILDER-001", graphController.getInteractionTraversal("INT-R05-BUILDER-001").getBody().root().id());
        assertEquals("API-POST-API-V1-AUTH-LOGIN", graphController.getApiTraversal("API-POST-API-V1-AUTH-LOGIN").getBody().root().id());
        assertEquals("DE-AGENT", graphController.getDataEntityTraversal("DE-AGENT").getBody().root().id());
        assertEquals("OBJ-DH-AI-001", graphController.getObjectiveTraversal("OBJ-DH-AI-001").getBody().root().id());
        assertEquals("TOP-001", graphController.getTopicTraversal("TOP-001").getBody().root().id());
        assertEquals("FEAT-AI", graphController.getFeatureTraversal("FEAT-AI").getBody().root().id());
        assertEquals("DEC-001", graphController.getDecisionTraversal("DEC-001").getBody().root().id());
        assertEquals("ASM-001", graphController.getAssumptionTraversal("ASM-001").getBody().root().id());
        assertEquals("CON-001", graphController.getGovernanceConstraintTraversal("CON-001").getBody().root().id());
        assertEquals("ASSESS-API-002", graphController.getAssessmentTraversal("ASSESS-API-002").getBody().root().id());
        assertEquals("RSK-001", graphController.getRiskTraversal("RSK-001").getBody().root().id());
        assertEquals("EDGE-001", graphController.getEdgeCaseTraversal("EDGE-001").getBody().root().id());
        assertEquals("EXC-001", graphController.getExceptionCaseTraversal("EXC-001").getBody().root().id());
        assertEquals("EPIC-AI-001", graphController.getEpicTraversal("EPIC-AI-001").getBody().root().id());
        assertEquals("PORT-DH-001", graphController.getPortfolioTraversal("PORT-DH-001").getBody().root().id());
        assertEquals("BUG-LOGIN-001", graphController.getBugTraversal("BUG-LOGIN-001").getBody().root().id());
        assertEquals("TASK-AI-001", graphController.getTaskTraversal("TASK-AI-001").getBody().root().id());
        assertEquals("INTG-001", graphController.getIntegrationTraversal("INTG-001").getBody().root().id());
        assertEquals("PROJ-DH-AI-001", graphController.getProjectTraversal("PROJ-DH-AI-001").getBody().root().id());
        assertEquals("MS-DH-AI-001", graphController.getMilestoneTraversal("MS-DH-AI-001").getBody().root().id());
        assertEquals("DOM-DESIGN", graphController.getBusinessDomainTraversal("DOM-DESIGN").getBody().root().id());
        assertEquals("CAP-SCREEN-MGMT", graphController.getCapabilityTraversal("CAP-SCREEN-MGMT").getBody().root().id());
        assertEquals("PROC-SCREEN-BUILD", graphController.getProcessTraversal("PROC-SCREEN-BUILD").getBody().root().id());
        assertEquals("APP-DH", graphController.getApplicationTraversal("APP-DH").getBody().root().id());
        assertEquals("ORG-DH-PLATFORM", graphController.getOrganizationTraversal("ORG-DH-PLATFORM").getBody().root().id());
        assertEquals("CMP-DH-FRONTEND", graphController.getComponentTraversal("CMP-DH-FRONTEND").getBody().root().id());
        assertEquals("BO-AGENT-CONFIG", graphController.getBusinessObjectTraversal("BO-AGENT-CONFIG").getBody().root().id());
        assertEquals("DEP-DEV-001", graphController.getDeploymentTraversal("DEP-DEV-001").getBody().root().id());
        assertEquals("CONV-API-IDEMPOTENCY", graphController.getConventionTraversal("CONV-API-IDEMPOTENCY").getBody().root().id());
        assertEquals("QC-API-P95", graphController.getQualityConstraintTraversal("QC-API-P95").getBody().root().id());
        assertEquals("POL-DH-AGENT-001", graphController.getPolicyTraversal("POL-DH-AGENT-001").getBody().root().id());
        assertEquals("SRC-US-AI-090", graphController.getSourceTraversal("SRC-US-AI-090").getBody().root().id());
        assertEquals("FND-001", graphController.getFindingTraversal("FND-001").getBody().root().id());
        assertEquals("OQ-001", graphController.getOpenQuestionTraversal("OQ-001").getBody().root().id());
        assertEquals("AC-US-AI-090-001", graphController.getAcceptanceCriterionTraversal("AC-US-AI-090-001").getBody().root().id());
        assertEquals("DF-DE-AGENT-001", graphController.getDataFieldTraversal("DF-DE-AGENT-001").getBody().root().id());
        assertEquals("ARCHITECT", graphController.getBusinessRoleTraversal("ARCHITECT").getBody().root().id());
        assertEquals("HITL_REVIEWER", graphController.getValidationRoleTraversal("HITL_REVIEWER").getBody().root().id());
        assertEquals("ADMIN", graphController.getPermissionTraversal("ADMIN").getBody().root().id());
        assertEquals("CONFIRM-AGT-PUBLISH", graphController.getDialogTraversal("CONFIRM-AGT-PUBLISH").getBody().root().id());
        assertEquals("AGT-E-403", graphController.getErrorCodeTraversal("AGT-E-403").getBody().root().id());

        verify(graphQueryService).getObjectTraversal("screen-state", "STATE-SCR-AUTH-LOADING");
        verify(graphQueryService).getObjectTraversal("transition", "TRN-SCR-AUTH-TO-DASH");
        verify(graphQueryService).getObjectTraversal("touchpoint", "TP-AGT-DOCK");
        verify(graphQueryService).getObjectTraversal("interaction", "INT-R05-BUILDER-001");
        verify(graphQueryService).getObjectTraversal("api", "API-POST-API-V1-AUTH-LOGIN");
        verify(graphQueryService).getObjectTraversal("data-entity", "DE-AGENT");
        verify(graphQueryService).getObjectTraversal("objective", "OBJ-DH-AI-001");
        verify(graphQueryService).getObjectTraversal("topic", "TOP-001");
        verify(graphQueryService).getObjectTraversal("feature", "FEAT-AI");
        verify(graphQueryService).getObjectTraversal("decision", "DEC-001");
        verify(graphQueryService).getObjectTraversal("assumption", "ASM-001");
        verify(graphQueryService).getObjectTraversal("governance-constraint", "CON-001");
        verify(graphQueryService).getObjectTraversal("assessment", "ASSESS-API-002");
        verify(graphQueryService).getObjectTraversal("risk", "RSK-001");
        verify(graphQueryService).getObjectTraversal("edge-case", "EDGE-001");
        verify(graphQueryService).getObjectTraversal("exception-case", "EXC-001");
        verify(graphQueryService).getObjectTraversal("epic", "EPIC-AI-001");
        verify(graphQueryService).getObjectTraversal("portfolio", "PORT-DH-001");
        verify(graphQueryService).getObjectTraversal("bug", "BUG-LOGIN-001");
        verify(graphQueryService).getObjectTraversal("task", "TASK-AI-001");
        verify(graphQueryService).getObjectTraversal("integration", "INTG-001");
        verify(graphQueryService).getObjectTraversal("project", "PROJ-DH-AI-001");
        verify(graphQueryService).getObjectTraversal("milestone", "MS-DH-AI-001");
        verify(graphQueryService).getObjectTraversal("business-domain", "DOM-DESIGN");
        verify(graphQueryService).getObjectTraversal("capability", "CAP-SCREEN-MGMT");
        verify(graphQueryService).getObjectTraversal("process", "PROC-SCREEN-BUILD");
        verify(graphQueryService).getObjectTraversal("application", "APP-DH");
        verify(graphQueryService).getObjectTraversal("organization", "ORG-DH-PLATFORM");
        verify(graphQueryService).getObjectTraversal("component", "CMP-DH-FRONTEND");
        verify(graphQueryService).getObjectTraversal("business-object", "BO-AGENT-CONFIG");
        verify(graphQueryService).getObjectTraversal("deployment", "DEP-DEV-001");
        verify(graphQueryService).getObjectTraversal("convention", "CONV-API-IDEMPOTENCY");
        verify(graphQueryService).getObjectTraversal("quality-constraint", "QC-API-P95");
        verify(graphQueryService).getObjectTraversal("policy", "POL-DH-AGENT-001");
        verify(graphQueryService).getObjectTraversal("source", "SRC-US-AI-090");
        verify(graphQueryService).getObjectTraversal("finding", "FND-001");
        verify(graphQueryService).getObjectTraversal("open-question", "OQ-001");
        verify(graphQueryService).getObjectTraversal("acceptance-criterion", "AC-US-AI-090-001");
        verify(graphQueryService).getObjectTraversal("data-field", "DF-DE-AGENT-001");
        verify(graphQueryService).getObjectTraversal("role", "ARCHITECT");
        verify(graphQueryService).getObjectTraversal("validation-role", "HITL_REVIEWER");
        verify(graphQueryService).getObjectTraversal("permission", "ADMIN");
        verify(graphQueryService).getObjectTraversal("dialog", "CONFIRM-AGT-PUBLISH");
        verify(graphQueryService).getObjectTraversal("error-code", "AGT-E-403");
    }

    @Test
    void shouldReturnPersonaSummaries() {
        when(graphQueryService.getPersonas("IDENTIFIED"))
                .thenReturn(List.of(new PersonaSummaryResponse(
                        "PER-UX-007",
                        "Design Lead",
                        "Owns the experience flow from exploration through delivery sign-off.",
                        "IDENTIFIED",
                        2L,
                        7L,
                        4L,
                        2L
                )));

        var response = graphController.getPersonas("IDENTIFIED");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("PER-UX-007", response.getBody().get(0).personaId());
    }

    @Test
    void shouldReturnStoryTraceability() {
        when(graphQueryService.getStoryTraceability("US-AI-090"))
                .thenReturn(Optional.of(new TraceabilityStoryResponse(
                        null,
                        null,
                        null,
                        null,
                        new GraphNodeReference("US-AI-090", "UserStory", "Builder canvas interactions ready for agent composition", "DEFINED"),
                        List.of(new GraphNodeReference("SCR-AGT-BUILDER", "Screen", "Agent Builder (3-panel)", "IN_DEFINITION")),
                        List.of(),
                        List.of(new GraphNodeReference("API-AGT-001", "ApiContract", "POST /api/agents/run", "APPROVED")),
                        List.of(),
                        List.of(),
                        List.of(new GraphNodeReference("TASK-AI-001", "Task", "Implement builder canvas interactions", "IN_IMPLEMENTATION")),
                        List.of("BusinessObjective", "RequirementPortfolio", "Epic", "Feature")
                )));

        var response = graphController.getStoryTraceability("US-AI-090");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("US-AI-090", response.getBody().story().id());
        assertEquals(4, response.getBody().missingSpineSegments().size());
    }

    @Test
    void shouldReturnBusinessCapabilitySummaries() {
        when(graphQueryService.getBusinessCapabilities("DOM-DESIGN"))
                .thenReturn(List.of(new BusinessCapabilitySummaryResponse(
                        "CAP-SCREEN-MGMT",
                        "Screen Management",
                        "DOM-DESIGN",
                        "Design Management",
                        1L,
                        1L,
                        1L,
                        2L
                )));

        var response = graphController.getBusinessCapabilities("DOM-DESIGN");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("CAP-SCREEN-MGMT", response.getBody().get(0).capabilityId());
    }

    @Test
    void shouldReturnBusinessArchitectureTraversal() {
        when(graphQueryService.getBusinessArchitecture("CAP-SCREEN-MGMT"))
                .thenReturn(Optional.of(new BusinessArchitectureResponse(
                        "CAP-SCREEN-MGMT",
                        "Screen Management",
                        "Manage screen inventory and review workflow",
                        "DEFINED",
                        "DOM-DESIGN",
                        "Design Management",
                        List.of(new GraphNodeReference("PROC-SCREEN-REVIEW", "BusinessProcess", "Screen Review Process", "DEFINED")),
                        List.of(new GraphNodeReference("APP-DH", "Application", "Design Hub", "IMPLEMENTED")),
                        List.of(new GraphNodeReference("FEAT-AI", "Feature", "Agent Builder", "IN_IMPLEMENTATION")),
                        List.of(new BusinessArchitectureResponse.OrganizationSummary(
                                "ORG-DH-PLATFORM",
                                "Design Hub Platform Team",
                                "TEAM",
                                "IMPLEMENTED"
                        ))
                )));

        var response = graphController.getBusinessArchitecture("CAP-SCREEN-MGMT");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("CAP-SCREEN-MGMT", response.getBody().capabilityId());
        assertEquals(1, response.getBody().processes().size());
        assertEquals(1, response.getBody().organizations().size());
    }

    @Test
    void shouldReturnApplicationSummaries() {
        when(graphQueryService.getApplications("WEB"))
                .thenReturn(List.of(new ApplicationSummaryResponse(
                        "APP-DH",
                        "Design Hub",
                        "WEB",
                        "IMPLEMENTED",
                        2L,
                        1L,
                        3L,
                        1L,
                        1L,
                        List.of("Design Hub Platform Team", "Design Hub Product Design")
                )));

        var response = graphController.getApplications("WEB");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("APP-DH", response.getBody().get(0).applicationId());
    }

    @Test
    void shouldReturnApplicationArchitectureTraversal() {
        when(graphQueryService.getApplicationArchitecture("APP-DH"))
                .thenReturn(Optional.of(new ApplicationArchitectureResponse(
                        "APP-DH",
                        "Design Hub",
                        "Design Hub workspace and graph explorer",
                        "WEB",
                        "IMPLEMENTED",
                        List.of("Design Hub Platform Team", "Design Hub Product Design"),
                        List.of(new ApplicationArchitectureResponse.ComponentSummary(
                                "CMP-DH-FRONTEND",
                                "Design Hub Frontend",
                                "FRONTEND_APP",
                                "ANGULAR",
                                "BROWSER",
                                "frontend",
                                "IMPLEMENTED",
                                List.of(),
                                List.of(new GraphNodeReference("SCR-AGT-BUILDER", "Screen", "Agent Builder (3-panel)", "IN_DEFINITION")),
                                List.of(new GraphNodeReference("CMP-DH-BACKEND", "ApplicationComponent", "Design Hub Backend", "IMPLEMENTED"))
                        )),
                        List.of(new GraphNodeReference("API-POST-API-V1-AGENTS-ID-PUBLISH", "ApiContract", "POST /api/v1/agents/{id}/publish", "DEFINED")),
                        List.of(new GraphNodeReference("SCR-AGT-BUILDER", "Screen", "Agent Builder (3-panel)", "IN_DEFINITION")),
                        List.of(new GraphNodeReference("FEAT-AI", "Feature", "Agent Builder", "IN_IMPLEMENTATION")),
                        List.of(new ApplicationArchitectureResponse.DependencySummary(
                                "APP-IDP",
                                "EMSIST Identity Service",
                                "OUTBOUND",
                                "IMPLEMENTED"
                        ))
                )));

        var response = graphController.getApplicationArchitecture("APP-DH");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("APP-DH", response.getBody().applicationId());
        assertEquals(1, response.getBody().components().size());
        assertEquals(1, response.getBody().dependencies().size());
    }

    @Test
    void shouldReturnDataObjectSummaries() {
        when(graphQueryService.getDataObjects("AI_DESIGN"))
                .thenReturn(List.of(new DataArchitectureObjectSummaryResponse(
                        "BO-AGENT-CONFIG",
                        "Agent Configuration",
                        "AI_DESIGN",
                        "INTERNAL",
                        "DEFINED",
                        1L,
                        1L,
                        1L,
                        3L
                )));

        var response = graphController.getDataObjects("AI_DESIGN");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("BO-AGENT-CONFIG", response.getBody().get(0).objectId());
    }

    @Test
    void shouldReturnDataArchitectureTraversal() {
        when(graphQueryService.getDataArchitecture("BO-AGENT-CONFIG"))
                .thenReturn(Optional.of(new DataArchitectureResponse(
                        "BO-AGENT-CONFIG",
                        "Agent Configuration",
                        "AI_DESIGN",
                        "Business-level representation of an agent definition and its authoring metadata.",
                        "INTERNAL",
                        "DEFINED",
                        List.of(new DataArchitectureResponse.EntitySummary(
                                "DE-AGENT",
                                "Agent",
                                "CONFIGURATION",
                                1L,
                                "DEFINED"
                        )),
                        List.of(new DataArchitectureResponse.FlowSummary(
                                "FLOW-AGENT-DRAFT",
                                "Agent draft persistence flow",
                                "OUTBOUND",
                                "DEFINED",
                                "APP-DH",
                                "Design Hub",
                                "APP-DH",
                                "Design Hub"
                        )),
                        List.of(new GraphNodeReference(
                                "API-PUT-API-V1-AGENTS-ID-DRAFT",
                                "ApiContract",
                                "PUT /api/v1/agents/{id}/draft",
                                "DEFINED"
                        )),
                        List.of(new GraphNodeReference(
                                "SCR-AGT-BUILDER",
                                "Screen",
                                "Agent Builder (3-panel)",
                                "IN_DEFINITION"
                        )),
                        List.of(new GraphNodeReference(
                                "BO-AGENT-PUBLISH-REQ",
                                "BusinessObject",
                                "Agent Publish Request",
                                "DEFINED"
                        ))
                )));

        var response = graphController.getDataArchitecture("BO-AGENT-CONFIG");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("BO-AGENT-CONFIG", response.getBody().objectId());
        assertEquals(1, response.getBody().flows().size());
        assertEquals(1, response.getBody().children().size());
    }

    @Test
    void shouldReturnInfrastructureDeploymentSummaries() {
        when(graphQueryService.getInfrastructureDeployments("DEV"))
                .thenReturn(List.of(new InfrastructureDeploymentSummaryResponse(
                        "DEP-DEV-001",
                        "Design Hub Dev Stack",
                        "DEV",
                        "IMPLEMENTED",
                        2L,
                        1L,
                        1L
                )));

        var response = graphController.getInfrastructureDeployments("DEV");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("DEP-DEV-001", response.getBody().get(0).deploymentId());
    }

    @Test
    void shouldReturnInfrastructureArchitectureTraversal() {
        when(graphQueryService.getInfrastructureArchitecture("DEP-DEV-001"))
                .thenReturn(Optional.of(new InfrastructureArchitectureResponse(
                        "DEP-DEV-001",
                        "Design Hub Dev Stack",
                        "DEV",
                        "Development deployment topology for the Design Hub frontend and backend.",
                        "IMPLEMENTED",
                        List.of(new GraphNodeReference(
                                "CMP-DH-FRONTEND",
                                "ApplicationComponent",
                                "Design Hub Frontend",
                                "IMPLEMENTED"
                        )),
                        List.of(new InfrastructureArchitectureResponse.InfrastructureNodeSummary(
                                "INF-AKS-DEV-001",
                                "EMSIST Platform Dev Cluster",
                                "KUBERNETES_CLUSTER",
                                "Azure UAE North",
                                "IMPLEMENTED"
                        )),
                        List.of(new GraphNodeReference(
                                "APP-DH",
                                "Application",
                                "Design Hub",
                                "IMPLEMENTED"
                        )),
                        List.of()
                )));

        var response = graphController.getInfrastructureArchitecture("DEP-DEV-001");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("DEP-DEV-001", response.getBody().deploymentId());
        assertEquals(1, response.getBody().components().size());
        assertEquals(1, response.getBody().infrastructureNodes().size());
    }

    private GraphRelationExpansionResponse simpleTraversal(String id, String nodeType, String displayName) {
        return new GraphRelationExpansionResponse(
                new GraphObjectSummaryResponse(id, nodeType, displayName, "DEFINED", null, null, null, 1L),
                List.of(),
                List.of()
        );
    }
}
