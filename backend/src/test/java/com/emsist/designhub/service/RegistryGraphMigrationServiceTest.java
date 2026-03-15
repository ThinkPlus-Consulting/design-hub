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

    // ── Full migration orchestration ───────────────────────────────────

    @Test
    void shouldRunFullMigrationIncludingNewSteps() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);
        when(spec.fetch().all()).thenReturn(List.of()); // for upsertApiContracts fetch

        service.runFullMigration();

        // Seeds (5) + patches (2) + upsert fetch (1) + existing backfills (6 — backfillPersonas has 3 internal)
        // + new backfills (5) = 19 queries with empty apiCalls fetch
        verify(neo4jClient, atLeast(19)).query(anyString());
    }
}
