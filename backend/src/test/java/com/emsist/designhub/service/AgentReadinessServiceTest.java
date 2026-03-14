package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AgentReadinessServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @InjectMocks
    private AgentReadinessService service;

    @Test
    void shouldReturnAllChecksForAgentReadinessAssessment() {
        // Verify the service defines the 6 check names
        var checkNames = AgentReadinessService.AGENT_READY_CHECKS;
        assertTrue(checkNames.contains("repoPath"));
        assertTrue(checkNames.contains("effectiveBuildCommand"));
        assertTrue(checkNames.contains("manifestPath"));
        assertTrue(checkNames.contains("codeAssetPresence"));
        assertTrue(checkNames.contains("testFileResolution"));
        assertTrue(checkNames.contains("entrypointPath"));
        assertEquals(6, checkNames.size());
    }
}
