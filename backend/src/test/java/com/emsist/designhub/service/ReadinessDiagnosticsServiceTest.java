package com.emsist.designhub.service;

import com.emsist.designhub.dto.ReadinessDiagnosticsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadinessDiagnosticsServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @Mock
    private AgentReadinessService agentReadinessService;

    @InjectMocks
    private ReadinessDiagnosticsService service;

    @Test
    @SuppressWarnings("unchecked")
    void shouldComputeScreenDiagnostics() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.ofEntries(
                Map.entry("artifactId", "SCR-1"),
                Map.entry("status", "APPROVED"),
                Map.entry("hasSurfaceId", true),
                Map.entry("hasLabel", true),
                Map.entry("hasRoutePath", true),
                Map.entry("hasStatus", true),
                Map.entry("hasStory", true),
                Map.entry("hasInteraction", true),
                Map.entry("hasRole", true),
                Map.entry("hasMessage", false),
                Map.entry("hasState", false),
                Map.entry("hasTransition", true),
                Map.entry("hasGap", true),
                Map.entry("hasApi", true),
                Map.entry("hasVerifiedTests", true)
        )));

        ReadinessDiagnosticsResponse response = service.assessScreen("SCR-1").orElseThrow();

        assertEquals("Screen", response.getArtifactType());
        assertEquals("SCR-1", response.getArtifactId());
        assertEquals(89.5, response.getCompletenessScore());
        assertEquals("GREEN", response.getCompletenessLevel());
        assertTrue(response.getReadiness().get("requirementsReady"));
        assertTrue(response.getReadiness().get("designReady"));
        assertTrue(response.getReadiness().get("frontendReady"));
        assertTrue(response.getReadiness().get("integrationReady"));
        assertTrue(response.getReadiness().get("qaReady"));
        assertTrue(response.getMissingOptionalRules().contains("MCR-SCR-008"));
        assertTrue(response.getMissingOptionalRules().contains("MCR-SCR-009"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldComputeStoryDiagnosticsAndIncludeAgentReadyAdvisory() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.ofEntries(
                Map.entry("artifactId", "US-1"),
                Map.entry("status", "APPROVED"),
                Map.entry("hasLabel", true),
                Map.entry("hasStatus", true),
                Map.entry("hasScreen", true),
                Map.entry("hasCriterion", true),
                Map.entry("hasFeature", false),
                Map.entry("hasRule", true),
                Map.entry("hasApiContract", false),
                Map.entry("hasDataEntity", false),
                Map.entry("hasMessage", false),
                Map.entry("hasRoleContext", false),
                Map.entry("hasTask", false),
                Map.entry("hasVerifiedBy", false),
                Map.entry("hasTraceability", false)
        )));
        when(agentReadinessService.isAgentReady("US-1")).thenReturn(false);

        ReadinessDiagnosticsResponse response = service.assessStory("US-1").orElseThrow();

        assertEquals("UserStory", response.getArtifactType());
        assertEquals("US-1", response.getArtifactId());
        assertEquals(78.6, response.getCompletenessScore());
        assertEquals("AMBER", response.getCompletenessLevel());
        assertTrue(response.getReadiness().get("requirementsReady"));
        assertTrue(response.getReadiness().get("designReady"));
        assertTrue(response.getReadiness().get("frontendReady"));
        assertTrue(response.getReadiness().get("contractReady"));
        assertFalse(response.getReadiness().get("backendReady"));
        assertFalse(response.getReadiness().get("qaReady"));
        assertTrue(response.getMissingOptionalRules().contains("MCR-UST-005"));
        assertTrue(response.getMissingOptionalRules().contains("MCR-UST-006"));
        assertEquals(1, response.getAdvisoryRulesViolated().size());
        assertEquals("MCR-STORY-AGENT-READY-001", response.getAdvisoryRulesViolated().get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnEmptyWhenArtifactDoesNotExist() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.empty());

        assertTrue(service.assessScreen("SCR-MISSING").isEmpty());
    }
}
