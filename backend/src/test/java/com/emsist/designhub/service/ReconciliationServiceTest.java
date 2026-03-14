package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ReconciliationServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @Mock
    private RequirementSyncService syncService;

    @InjectMocks
    private ReconciliationService reconciler;

    @Test
    void shouldDecideCreateForNewNode() {
        var decision = reconciler.decide("US-NEW-001", "UserStory", null, "sha256:abc123");
        assertEquals(ReconciliationService.Decision.CREATE, decision);
    }

    @Test
    void shouldDecideSkipForUnchangedNode() {
        var decision = reconciler.decide("US-SCR-042", "UserStory", "sha256:abc123", "sha256:abc123");
        assertEquals(ReconciliationService.Decision.SKIP, decision);
    }

    @Test
    void shouldDecideUpdateForChangedNode() {
        var decision = reconciler.decide("US-SCR-042", "UserStory", "sha256:abc123", "sha256:def456");
        assertEquals(ReconciliationService.Decision.UPDATE, decision);
    }
}
