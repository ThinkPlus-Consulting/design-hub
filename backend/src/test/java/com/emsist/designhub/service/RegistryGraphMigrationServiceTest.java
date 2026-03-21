package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistryGraphMigrationServiceTest {

    @Mock private Neo4jClient neo4jClient;
    @Mock private ExternalArtifactSyncService externalArtifactSyncService;

    @InjectMocks
    private RegistryGraphMigrationService service;

    // ── Existing seed tests ────────────────────────────────────────────

    @Test
    void shouldSeedChannelRegistry() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedChannels();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (c:Channel")
                && ((String) cypher).contains("CH-WEB-DSK")));
    }

    @Test
    void shouldSeedPermissionRegistry() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedPermissions();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("Permission")
                && ((String) cypher).contains("MERGE")
                && ((String) cypher).contains("'ADMIN'")));
    }

    @Test
    void shouldSeedBusinessRoles() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedBusinessRoles();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("BusinessRole")
                && ((String) cypher).contains("MERGE")
                && ((String) cypher).contains("SUPER_ADMIN")
                && ((String) cypher).contains("ADMIN")));
    }

    @Test
    void shouldSeedValidationRoles() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedValidationRoles();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (r:ValidationRole")
                && ((String) cypher).contains("HITL_REVIEWER")));
    }

    @Test
    void shouldSeedProjectDeliveryAlignment() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedProjectDeliveryAlignment();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ProjectInstance")
                        && ((String) cypher).contains("PROJ-DH-AI-001")
                        && ((String) cypher).contains("Milestone")
                        && ((String) cypher).contains("MS-DH-AI-001")));
    }

    @Test
    void shouldSeedMissingT1Coverage() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedTopicCoverage();
        service.seedEdgeCaseCoverage();
        service.seedExceptionCaseCoverage();
        service.seedIntegrationCoverage();
        service.seedOpenQuestionCoverage();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (topicAi:Topic")
                        && ((String) cypher).contains("TOP-001")
                        && ((String) cypher).contains("GROUPS_JOURNEY")));
        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (builderEdge:EdgeCase")
                        && ((String) cypher).contains("EDGE-001")
                        && ((String) cypher).contains("AFFECTS_JOURNEY_STEP")));
        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (loginException:ExceptionCase")
                        && ((String) cypher).contains("EXC-001")
                        && ((String) cypher).contains("AFFECTS_API")));
        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (identityIntegration:Integration")
                        && ((String) cypher).contains("INTG-001")
                        && ((String) cypher).contains("USES_API")));
        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (questionAi:OpenQuestion")
                        && ((String) cypher).contains("OQ-001")
                        && ((String) cypher).contains("BLOCKS_ARTIFACT")));
    }

    // ── Existing backfill tests ────────────────────────────────────────

    @Test
    void shouldBackfillPersonaNodesFromStringFields() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillPersonas();

        // Should create Persona nodes from personaId on Journey and personaIds on Screen/Touchpoint
        verify(neo4jClient, atLeast(1)).query((String) argThat(cypher ->
                ((String) cypher).contains("Persona")));
    }

    @Test
    void shouldBackfillAccessibleByRoleEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillAccessibleByRoleEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ACCESSIBLE_BY_ROLE")
                && ((String) cypher).contains("BusinessRole")));
    }

    @Test
    void shouldBackfillDeliveredViaChannelEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillDeliveredViaChannelEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("DELIVERED_VIA_CHANNEL")
                && ((String) cypher).contains("Channel")));
    }

    @Test
    void shouldBackfillRequiresPermissionEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillRequiresPermissionEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("REQUIRES_PERMISSION")
                && ((String) cypher).contains("Permission")));
    }

    // ── New seed tests (Chunk 2 — Task 4) ──────────────────────────────

    @Test
    void shouldSeedConfirmationDialogs() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedConfirmationDialogs();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (d:ConfirmationDialog")
                && ((String) cypher).contains("CONFIRM-AGT-DELETE")
                && ((String) cypher).contains("CONFIRM-AGT-PUBLISH")));
    }

    @Test
    void shouldSeedErrorCodes() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedErrorCodes();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (code:ErrorCode")
                && ((String) cypher).contains("AUTH-E-401")
                && ((String) cypher).contains("CHAT-E-503")));
    }

    @Test
    void shouldUpsertApiContractsFromInteractionApiCalls() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);

        // Configure the fetch chain to return one apiCall
        Collection<Map<String, Object>> rows = List.of(
                Map.of("apiCall", "GET /api/v1/agents/{id}"));
        when(spec.fetch().all()).thenReturn(rows);

        service.upsertApiContractsFromInteractions();

        // Should have been called: once for DISTINCT fetch, once for MERGE
        verify(neo4jClient, atLeast(2)).query(anyString());
    }

    @Test
    void shouldSkipBlankApiCallsInUpsert() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);

        // Return blank and null entries
        Collection<Map<String, Object>> rows = List.of(
                Map.of("apiCall", ""),
                Map.of("apiCall", "   "));
        when(spec.fetch().all()).thenReturn(rows);

        service.upsertApiContractsFromInteractions();

        // Should only call query once (the DISTINCT fetch), no MERGE calls
        verify(neo4jClient, times(1)).query(anyString());
    }

    // ── ContractId generation tests (frozen ID rule) ───────────────────

    @ParameterizedTest(name = "generateContractId({0}, {1}) = {2}")
    @CsvSource({
            "GET,  /api/v1/agents/{id},              API-GET-API-V1-AGENTS-ID",
            "POST, /api/v1/agents/{id}/publish,       API-POST-API-V1-AGENTS-ID-PUBLISH",
            "DELETE, /api/v1/agents/{id},              API-DELETE-API-V1-AGENTS-ID",
            "PUT,  /api/v1/agents/{id}/draft,         API-PUT-API-V1-AGENTS-ID-DRAFT",
            "PATCH, /api/v1/notifications/{id}/read,   API-PATCH-API-V1-NOTIFICATIONS-ID-READ",
            "GET,  /api/v1/search?q={query},           API-GET-API-V1-SEARCH-Q-QUERY",
            "GET,  /api/v1/templates?category={cat},   API-GET-API-V1-TEMPLATES-CATEGORY-CAT",
            "POST, /api/v1/templates/{id}/fork,        API-POST-API-V1-TEMPLATES-ID-FORK",
            "POST, /api/v1/agents/{id}/chat,           API-POST-API-V1-AGENTS-ID-CHAT",
            "DELETE, /api/v1/agents/{id}/chat/stream,   API-DELETE-API-V1-AGENTS-ID-CHAT-STREAM",
            "POST, /api/v1/agents/{id}/chat/escalate,   API-POST-API-V1-AGENTS-ID-CHAT-ESCALATE",
            "POST, /api/v1/agents/{id}/test-session,    API-POST-API-V1-AGENTS-ID-TEST-SESSION",
            "POST, /api/v1/auth/refresh,                API-POST-API-V1-AUTH-REFRESH",
            "GET,  /api/v1/templates/{id},               API-GET-API-V1-TEMPLATES-ID"
    })
    void shouldGenerateContractIdPerFrozenRule(String method, String path, String expectedId) {
        assertEquals(expectedId, service.generateContractId(method, path));
    }

    @Test
    void shouldHandleRootPath() {
        assertEquals("API-GET", service.generateContractId("GET", "/"));
    }

    @Test
    void shouldCollapseConsecutiveSpecialChars() {
        assertEquals("API-GET-A-B", service.generateContractId("GET", "/a///b"));
    }

    @Test
    void shouldTrimLeadingAndTrailingDashes() {
        assertEquals("API-GET-PATH", service.generateContractId("GET", "/path/"));
    }

    @Test
    void shouldUppercaseMethodInContractId() {
        assertEquals("API-GET-API-V1-TEST", service.generateContractId("get", "/api/v1/test"));
    }

    // ── New backfill tests (Chunk 2 — Task 5) ─────────────────────────

    @Test
    void shouldBackfillInteractionPersonaEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillInteractionPersonaEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("USED_BY_PERSONA")
                && ((String) cypher).contains("Interaction")
                && ((String) cypher).contains("Persona")));
    }

    @Test
    void shouldBackfillInteractionRoleEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillInteractionRoleEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ACCESSIBLE_BY_ROLE")
                && ((String) cypher).contains("Interaction")
                && ((String) cypher).contains("BusinessRole")));
    }

    @Test
    void shouldBackfillTouchpointRoleEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillTouchpointRoleEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ACCESSIBLE_BY_ROLE")
                && ((String) cypher).contains("Touchpoint")
                && ((String) cypher).contains("BusinessRole")));
    }

    @Test
    void shouldBackfillCallsApiEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillCallsApiEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("CALLS_API")
                && ((String) cypher).contains("ApiContract")
                && ((String) cypher).contains("toUpper")));
    }

    @Test
    void shouldBackfillTriggersConfirmationEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillTriggersConfirmationEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("TRIGGERS_CONFIRMATION")
                && ((String) cypher).contains("ConfirmationDialog")));
    }

    @Test
    void shouldPatchInteractionOutcomes() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.patchInteractionOutcomes();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("outcomeSuccess")
                && ((String) cypher).contains("errorCodeRef")
                && ((String) cypher).contains("INT-R05-BUILDER-004")));
    }

    @Test
    void shouldBackfillOnErrorShowsEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillOnErrorShowsEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ON_ERROR_SHOWS")
                && ((String) cypher).contains("ErrorCode")
                && ((String) cypher).contains("errorCodeRef")));
    }

    @Test
    void shouldBackfillCanProduceErrorEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillCanProduceErrorEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("CAN_PRODUCE_ERROR")
                && ((String) cypher).contains("ON_ERROR_SHOWS")
                && ((String) cypher).contains("ON_SCREEN")));
    }

    @Test
    void shouldBackfillHasInteractionEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillHasInteractionEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("HAS_INTERACTION")
                && ((String) cypher).contains("Interaction")
                && ((String) cypher).contains("surfaceId")));
    }

    @Test
    void shouldBackfillDeliversEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillDeliversEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("DELIVERS")
                && ((String) cypher).contains("storyRefs")
                && ((String) cypher).contains("UserStory")));
    }

    @Test
    void shouldBackfillExecutesInteractionEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillExecutesInteractionEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("EXECUTES_INTERACTION")
                && ((String) cypher).contains("JourneyStep")
                && ((String) cypher).contains("interactionRef")));
    }

    @Test
    void shouldBackfillJourneyStepTraversalEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.backfillJourneyStepTraversalEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("USES_SCREEN")
                && ((String) cypher).contains("STARTS_AT_TOUCHPOINT")
                && ((String) cypher).contains("JRN-R05-001.01")
                && ((String) cypher).contains("TP-GALLERY-MENU")));
    }

    @Test
    void shouldSeedAcceptanceCriteria() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedAcceptanceCriteria();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("AcceptanceCriterion")
                && ((String) cypher).contains("HAS_CRITERION")
                && ((String) cypher).contains("US-AUTH-001")
                && ((String) cypher).contains("US-AI-090")));
    }

    @Test
    void shouldSeedDataFields() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedDataFields();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("DataField")
                && ((String) cypher).contains("HAS_FIELD")
                && ((String) cypher).contains("DE-AGENT")));
    }

    @Test
    void shouldSeedMessages() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedMessages();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("Message")
                && ((String) cypher).contains("HAS_MESSAGE")
                && ((String) cypher).contains("SCR-AUTH")));
    }

    @Test
    void shouldSeedValidationRules() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedValidationRules();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ValidationRule")
                && ((String) cypher).contains("ENFORCES_VALIDATION")
                && ((String) cypher).contains("HAS_VALIDATION_RULE")));
    }

    @Test
    void shouldSeedApiSchemas() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedApiSchemas();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("RequestSchema")
                && ((String) cypher).contains("ResponseSchema")
                && ((String) cypher).contains("ErrorContract")
                && ((String) cypher).contains("HAS_REQUEST")
                && ((String) cypher).contains("HAS_RESPONSE")
                && ((String) cypher).contains("HAS_ERROR")));
    }

    @Test
    void shouldSeedTestCaseVerifiesEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedTestCaseVerifies();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("TestCase")
                && ((String) cypher).contains("VERIFIES")
                && ((String) cypher).contains("SCR-AUTH")));
    }

    @Test
    void shouldSeedStoryRuleEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedStoryRuleEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("GOVERNED_BY_RULE")
                && ((String) cypher).contains("US-AUTH-001")
                && ((String) cypher).contains("RULE-AUTH-001")
                && ((String) cypher).contains("US-AI-090")
                && ((String) cypher).contains("RULE-AGT-BUILDER-001")));
    }

    @Test
    void shouldSeedStoryReadinessCoverage() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedStoryReadinessCoverage();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("US-AUTH-001")
                && ((String) cypher).contains("SCR-AUTH")
                && ((String) cypher).contains("INT-AUTH-LOGIN-001")
                && ((String) cypher).contains("API-POST-API-V1-AUTH-LOGIN")
                && ((String) cypher).contains("VERIFIED_BY")));
    }

    @Test
    void shouldSeedBusinessDomains() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedBusinessDomains();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("BusinessDomain")
                && ((String) cypher).contains("HAS_CAPABILITY")
                && ((String) cypher).contains("DOM-DESIGN")));
    }

    @Test
    void shouldSeedBusinessProcesses() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedBusinessProcesses();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("BusinessProcess")
                && ((String) cypher).contains("REALIZED_BY_PROCESS")
                && ((String) cypher).contains("HAS_FLOW_NODE")
                && ((String) cypher).contains("GW-PROC-SCREEN-REVIEW-001")
                && ((String) cypher).contains("EVT-PROC-SCREEN-REVIEW-001")));
    }

    @Test
    void shouldSeedProcessFlows() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedProcessFlows();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("FLOWS_TO")
                && ((String) cypher).contains("ProcessGateway")
                && ((String) cypher).contains("ProcessEvent")));
    }

    @Test
    void shouldSeedProcessExpansion() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedProcessExpansion();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("EXPANDS_TO")
                && ((String) cypher).contains("CALLS_PROCESS")
                && ((String) cypher).contains("PROC-SCREEN-DETAIL-REVIEW")));
    }

    @Test
    void shouldSeedBoundaryEvent() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedBoundaryEvent();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ATTACHED_TO")
                && ((String) cypher).contains("FLOWS_TO")
                && ((String) cypher).contains("EVT-PROC-SCREEN-REVIEW-002")));
    }

    @Test
    void shouldSeedStoryTasks() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedStoryTasks();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("HAS_TASK")
                && ((String) cypher).contains("TASK-US-AUTH-001-001")
                && ((String) cypher).contains("US-AUTH-001")));
    }

    @Test
    void shouldSeedSourceReferences() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedSourceReferences();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("SourceReference")
                && ((String) cypher).contains("SRC-US-AUTH-001")
                && ((String) cypher).contains("SRC-US-AI-090-001")
                && ((String) cypher).contains("lineRef")));
    }

    @Test
    void shouldSeedExternalArtifacts() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedExternalArtifacts();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ExternalArtifact")
                && ((String) cypher).contains("EXT-JIRA-001")
                && ((String) cypher).contains("datetime")));
    }

    @Test
    void shouldSeedTraceabilityEdges() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedTraceabilityEdges();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("REPRESENTS_STORY")
                && ((String) cypher).contains("REPRESENTS_BUG")
                && ((String) cypher).contains("AFFECTS_SCREEN")
                && ((String) cypher).contains("HAS_SOURCE")
                && ((String) cypher).contains("WITH story, screen, bug")));
    }

    @Test
    void shouldSeedExternalArtifactAlignment() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedExternalArtifactAlignment();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("REPRESENTS_EPIC")
                && ((String) cypher).contains("REPRESENTS_FEATURE")
                && ((String) cypher).contains("REPRESENTS_TASK")
                && ((String) cypher).contains("PARENT_OF")
                && ((String) cypher).contains("CHILD_OF")
                && ((String) cypher).contains("DEPENDS_ON")
                && ((String) cypher).contains("RELATES_TO")
                && ((String) cypher).contains("DUPLICATES")));
    }

    @Test
    void shouldSeedExternalFieldNormalization() {
        service.seedExternalFieldNormalization();

        verify(externalArtifactSyncService).normalizeRepresentedPrimaryNodes();
    }

    @Test
    void shouldSeedImplementationSourceCoverage() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedImplementationSourceCoverage();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("US-AI-090")
                && ((String) cypher).contains("SCR-AGT-BUILDER")
                && ((String) cypher).contains("JRN-R05-001")
                && ((String) cypher).contains("TP-AGT-DOCK")
                && ((String) cypher).contains("INT-R05-BUILDER-001")
                && ((String) cypher).contains("API-POST-API-V1-AGENTS-ID-PUBLISH")
                && ((String) cypher).contains("DE-AGENT")
                && ((String) cypher).contains("HAS_SOURCE")));
    }

    // ── D6a screen-flow seed tests (Chunk 3) ──────────────────────────

    @Test
    void shouldSeedScreenStates() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedScreenStates();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("ScreenState")
                && ((String) cypher).contains("BELONGS_TO_SCREEN")
                && ((String) cypher).contains("STATE-SCR-AUTH-EMPTY")
                && ((String) cypher).contains("STATE-SCR-AUTH-LOADING")
                && ((String) cypher).contains("STATE-SCR-AUTH-ERROR")));
    }

    @Test
    void shouldSeedTransitions() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedTransitions();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("Transition")
                && ((String) cypher).contains("FROM_SCREEN")
                && ((String) cypher).contains("TO_SCREEN")
                && ((String) cypher).contains("CAUSED_BY_INTERACTION")
                && ((String) cypher).contains("TRN-SCR-AUTH-TO-DASH")));
    }

    @Test
    void shouldSeedApplicationsAndComponents() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedApplicationsAndComponents();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("Application {applicationId: 'APP-DH'}")
                && ((String) cypher).contains("CMP-DH-FRONTEND")
                && ((String) cypher).contains("CMP-DH-BACKEND")
                && ((String) cypher).contains("DEPENDS_ON_COMPONENT")));
    }

    @Test
    void shouldSeedImplementationPackArtifacts() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedImplementationPackArtifacts();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("SUPPORTS_SCREEN")
                && ((String) cypher).contains("EXPOSES")
                && ((String) cypher).contains("OWNS_DATA_ENTITY")
                && ((String) cypher).contains("ENFORCES_RULE")
                && ((String) cypher).contains("HAS_CODE_ASSET")
                && ((String) cypher).contains("ASSET_FOR_SCREEN")
                && ((String) cypher).contains("GOVERNED_BY_CONVENTION")
                && ((String) cypher).contains("WITH convFe, convBe")
                && ((String) cypher).contains("CA-FE-BUILDER-E2E-001")));
    }

    @Test
    void shouldSeedDataArchitectureAlignment() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedDataArchitectureAlignment();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("BusinessObject {objectId: 'BO-AGENT-CONFIG'}")
                && ((String) cypher).contains("BusinessObject {objectId: 'BO-AGENT-PUBLISH-REQ'}")
                && ((String) cypher).contains("InformationFlow {flowId: 'FLOW-AGENT-DRAFT'}")
                && ((String) cypher).contains("InformationFlow {flowId: 'FLOW-AGENT-PUBLISH'}")
                && ((String) cypher).contains("STRUCTURED_IN")
                && ((String) cypher).contains("MAPPED_TO")
                && ((String) cypher).contains("CARRIES")
                && ((String) cypher).contains("EXPOSED_VIA")
                && ((String) cypher).contains("SOURCE_APPLICATION")
                && ((String) cypher).contains("TARGET_APPLICATION")));
    }

    @Test
    void shouldSeedInfrastructureArchitectureAlignment() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedInfrastructureArchitectureAlignment();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("Deployment {deploymentId: 'DEP-DEV-001'}")
                && ((String) cypher).contains("Deployment {deploymentId: 'DEP-DEV-002'}")
                && ((String) cypher).contains("InfrastructureNode {nodeId: 'INF-AKS-DEV-001'}")
                && ((String) cypher).contains("InfrastructureNode {nodeId: 'INF-AKS-DEV-002'}")
                && ((String) cypher).contains("HOSTS")
                && ((String) cypher).contains("DEPLOYED_ON")));
    }

    @Test
    void shouldSeedImplementationPackVerification() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedImplementationPackVerification();

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("MERGE (us:UserStory {storyId: 'US-AI-090'})")
                && ((String) cypher).contains("DELIVERS")
                && ((String) cypher).contains("VERIFIED_BY")
                && ((String) cypher).contains("LOCATED_IN")
                && ((String) cypher).contains("TASK-US-AI-090-001")
                && ((String) cypher).contains("TC-US-AI-090-001")
                && ((String) cypher).contains("US-AI-090")));
    }

    @Test
    void shouldSeedUpperTraceabilitySpine() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedUpperTraceabilitySpine();

        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MERGE (objective:BusinessObjective {objectiveId: row.objectiveId})")
                        && cypher.contains("objectiveId: 'OBJ-DH-AI-001'")
                        && cypher.contains("HAS_FEATURE")
                        && cypher.contains("RequirementPortfolio {portfolioId: 'PORT-DH-001'}")
                        && cypher.contains("HAS_EPIC")
                        && cypher.contains("HAS_STORY")
                        && cypher.contains("US-AUTH-001")
                        && cypher.contains("US-AI-090")));
    }

    @Test
    void shouldSeedGovernanceCoverage() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedGovernanceCoverage();

        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MERGE (decision:Decision {decisionId: row.decisionId})")
                        && cypher.contains("AFFECTS_FEATURE")
                        && cypher.contains("AFFECTS_SCREEN")
                        && cypher.contains("AFFECTS_API")
                        && cypher.contains("DEC-001")));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MERGE (assumption:Assumption {assumptionId: row.assumptionId})")
                        && cypher.contains("UNDERLIES_FEATURE")
                        && cypher.contains("UNDERLIES_STORY")
                        && cypher.contains("ASM-001")));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MERGE (constraint:Constraint {constraintId: row.constraintId})")
                        && cypher.contains("CONSTRAINS_FEATURE")
                        && cypher.contains("CONSTRAINS_API")
                        && cypher.contains("CON-001")));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MERGE (risk:Risk {riskId: row.riskId})")
                        && cypher.contains("THREATENS_FEATURE")
                        && cypher.contains("THREATENS_STORY")
                        && cypher.contains("RSK-001")));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MERGE (assessment:Assessment {assessmentId: row.assessmentId})")
                        && cypher.contains("ASSESSES")
                        && cypher.contains("IDENTIFIES_GAP")
                        && cypher.contains("ASSESS-API-002")));
    }

    @Test
    void shouldSeedRemainingBenchmarkBreadthCoverage() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.seedJourneyStepBenchmarkCoverage();
        service.seedImportSnapshotCoverage();
        service.seedEvidenceRecordCoverage();
        service.seedRemainingRegistryCoverage();

        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("JourneyStep")
                        && cypher.contains("SRC-JS-R05-001")
                        && cypher.contains("HAS_SOURCE")
                        && cypher.contains("step.trigger")));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("ImportSnapshot")
                        && cypher.contains("IMPORTED_BY")
                        && cypher.contains("IMP-20260318-001")));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("EvidenceRecord")
                        && cypher.contains("BASELINED_BY")
                        && cypher.contains("EVR-SCR-AGT-BUILDER-001")));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("TranslationKey")
                        && cypher.contains("HAS_TRANSLATIONS")
                        && cypher.contains("USED_BY_MESSAGE")
                        && cypher.contains("FIRED_BY_INTEGRATION")
                        && cypher.contains("USED_BY_FIELD")));
    }

    // ── Full migration orchestration ───────────────────────────────────

    @Test
    void shouldRunFullMigrationIncludingNewSteps() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);
        when(spec.fetch().all()).thenReturn(List.of()); // for upsertApiContracts fetch

        service.runFullMigration();

        // Existing 45 minimum + technical execution activation seeds (3) = 48 minimum with empty apiCalls fetch
        verify(neo4jClient, atLeast(48)).query(anyString());
        verify(externalArtifactSyncService).normalizeRepresentedPrimaryNodes();
    }
}
