package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @Mock
    private RequirementSyncService syncService;

    @InjectMocks
    private ReconciliationService reconciler;

    // --- Pure decision logic (decide) ---

    @Test
    void shouldDecideCreateWhenNodeDoesNotExist() {
        var decision = reconciler.decide(false, null, "sha256:abc123");
        assertEquals(ReconciliationService.Decision.CREATE, decision);
    }

    @Test
    void shouldDecideSkipWhenHashesMatch() {
        var decision = reconciler.decide(true, "sha256:abc123", "sha256:abc123");
        assertEquals(ReconciliationService.Decision.SKIP, decision);
    }

    @Test
    void shouldDecideUpdateWhenHashesDiffer() {
        var decision = reconciler.decide(true, "sha256:abc123", "sha256:def456");
        assertEquals(ReconciliationService.Decision.UPDATE, decision);
    }

    @Test
    void shouldDecideConflictWhenNodeExistsWithoutContentHash() {
        // Node was created manually (no contentHash) — import should flag conflict
        var decision = reconciler.decide(true, null, "sha256:abc123");
        assertEquals(ReconciliationService.Decision.CONFLICT, decision);
    }

    // --- reconcile() with Neo4j lookup ---

    @Test
    @SuppressWarnings("unchecked")
    void shouldReconcileAsCreateWhenNodeNotInGraph() {
        var unboundSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(unboundSpec);
        when(unboundSpec.bind(any()).to(anyString())).thenReturn(unboundSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(unboundSpec.fetch()).thenReturn(fetchSpec);
        // OPTIONAL MATCH returns a row with nodeExists=false
        when(fetchSpec.first()).thenReturn(Optional.of(
                Map.of("nodeExists", false)));

        var decision = reconciler.reconcile("US-NEW-001", "UserStory", "sha256:abc123");
        assertEquals(ReconciliationService.Decision.CREATE, decision);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReconcileAsSkipWhenHashesMatch() {
        var unboundSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(unboundSpec);
        when(unboundSpec.bind(any()).to(anyString())).thenReturn(unboundSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(unboundSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn(Optional.of(
                Map.of("nodeExists", true, "contentHash", "sha256:abc123")));

        var decision = reconciler.reconcile("US-SCR-042", "UserStory", "sha256:abc123");
        assertEquals(ReconciliationService.Decision.SKIP, decision);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReconcileAsUpdateWhenHashesDiffer() {
        var unboundSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(unboundSpec);
        when(unboundSpec.bind(any()).to(anyString())).thenReturn(unboundSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(unboundSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn(Optional.of(
                Map.of("nodeExists", true, "contentHash", "sha256:old")));

        var decision = reconciler.reconcile("US-SCR-042", "UserStory", "sha256:new");
        assertEquals(ReconciliationService.Decision.UPDATE, decision);
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReconcileAsConflictWhenNodeHasNoContentHash() {
        var unboundSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(unboundSpec);
        when(unboundSpec.bind(any()).to(anyString())).thenReturn(unboundSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(unboundSpec.fetch()).thenReturn(fetchSpec);
        // Node exists but contentHash is absent (manually created node)
        java.util.HashMap<String, Object> record = new java.util.HashMap<>();
        record.put("nodeExists", true);
        record.put("contentHash", null);
        when(fetchSpec.first()).thenReturn(Optional.of(record));

        var decision = reconciler.reconcile("SCR-SETTINGS-01", "Screen", "sha256:abc");
        assertEquals(ReconciliationService.Decision.CONFLICT, decision);
    }

    // --- Label sanitization ---

    @Test
    void shouldRejectInvalidLabel() {
        assertThrows(IllegalArgumentException.class,
                () -> reconciler.reconcile("id", "Bad-Label!", "sha256:abc"));
    }
}
