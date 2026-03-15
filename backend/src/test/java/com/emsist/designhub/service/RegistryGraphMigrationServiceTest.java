package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegistryGraphMigrationServiceTest {

    @Mock private Neo4jClient neo4jClient;

    @InjectMocks
    private RegistryGraphMigrationService service;

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

    @Test
    void shouldRunFullMigration() {
        var spec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(spec);
        when(spec.run()).thenReturn(null);

        service.runFullMigration();

        // Should invoke multiple queries for seeding + backfilling
        verify(neo4jClient, atLeast(7)).query(anyString());
    }
}
