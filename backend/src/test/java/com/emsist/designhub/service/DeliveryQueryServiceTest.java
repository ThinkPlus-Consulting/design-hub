package com.emsist.designhub.service;

import com.emsist.designhub.dto.DeliveryStoryResponse;
import com.emsist.designhub.dto.ReadinessDiagnosticsResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryQueryServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @Mock
    private ReadinessDiagnosticsService readinessDiagnosticsService;

    @InjectMocks
    private DeliveryQueryService service;

    @Test
    @SuppressWarnings("unchecked")
    void shouldFilterAndSortDeliveryStories() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((List) List.of(
                storyRecord(
                        "US-AI-090",
                        "Builder canvas interactions ready for agent composition",
                        "ai",
                        "agent",
                        "DEFINED",
                        "FEAT-AI",
                        "Agent Builder",
                        List.of(screenMap("SCR-AGT-ORCH", "Agent Orchestration Canvas", "/agent/orchestration", "APPROVED")),
                        List.of(apiMap("API-AGT-001", "POST", "/api/agents/run", "APPROVED")),
                        List.of(),
                        List.of(gapMap("GAP-001", "MISSING_RULE", "MEDIUM", "Missing validation rules", "IDENTIFIED")),
                        List.of(externalMap("EXT-JIRA-001", "JIRA", "STORY", "DH-101", "SYNCED", "DEFINED")),
                        List.of()
                ),
                storyRecord(
                        "US-AUTH-001",
                        "User can sign in",
                        "core",
                        "auth",
                        "APPROVED",
                        "FEAT-AUTH",
                        "Authentication",
                        List.of(screenMap("SCR-AUTH", "Login / Sign In", "/auth/login", "IN_IMPLEMENTATION")),
                        List.of(),
                        List.of(bugMap("BUG-001", "AB#245", "Session refresh banner stays visible after login retry", "HIGH", "IDENTIFIED")),
                        List.of(),
                        List.of(externalMap("EXT-AZDO-001", "AZURE_DEVOPS", "BUG", "AB#245", "SYNCED", "DEFINED")),
                        List.of()
                )
        ));

        when(readinessDiagnosticsService.assessStory("US-AI-090"))
                .thenReturn(Optional.of(diagnostics("US-AI-090", true, 92.1, "GREEN")));
        when(readinessDiagnosticsService.assessStory("US-AUTH-001"))
                .thenReturn(Optional.of(diagnostics("US-AUTH-001", false, 64.3, "AMBER")));

        List<DeliveryStoryResponse> stories = service.getStories(
                null,
                null,
                null,
                true,
                true,
                true,
                null,
                "screenCount",
                "desc"
        );

        assertEquals(1, stories.size());
        DeliveryStoryResponse story = stories.get(0);
        assertEquals("US-AI-090", story.storyId());
        assertTrue(story.ready());
        assertEquals(1, story.screens().size());
        assertEquals(1, story.apis().size());
        assertEquals("SCR-AGT-ORCH", story.screens().get(0).surfaceId());
        assertEquals("API-AGT-001", story.apis().get(0).contractId());
        assertEquals(92.1, story.diagnostics().getCompletenessScore());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnSingleStoryWithDiagnostics() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(storyRecord(
                "US-AUTH-001",
                "User can sign in",
                "core",
                "auth",
                "APPROVED",
                "FEAT-AUTH",
                "Authentication",
                List.of(screenMap("SCR-AUTH", "Login / Sign In", "/auth/login", "IN_IMPLEMENTATION")),
                List.of(),
                List.of(bugMap("BUG-001", "AB#245", "Session refresh banner stays visible after login retry", "HIGH", "IDENTIFIED")),
                List.of(gapMap("GAP-AUTH-01", "MISSING_RULE", "MEDIUM", "No self-service reset flow", "IDENTIFIED")),
                List.of(externalMap("EXT-JIRA-001", "JIRA", "STORY", "DH-101", "SYNCED", "DEFINED")),
                List.of(externalMap("EXT-AZDO-001", "AZURE_DEVOPS", "BUG", "AB#245", "SYNCED", "DEFINED"))
        )));

        when(readinessDiagnosticsService.assessStory("US-AUTH-001"))
                .thenReturn(Optional.of(diagnostics("US-AUTH-001", false, 64.3, "AMBER")));

        DeliveryStoryResponse story = service.getStory("US-AUTH-001").orElseThrow();

        assertEquals("US-AUTH-001", story.storyId());
        assertFalse(story.ready());
        assertEquals("Authentication", story.feature().title());
        assertEquals(1, story.bugs().size());
        assertEquals(1, story.gaps().size());
        assertEquals(2, story.externalArtifacts().size());
        assertEquals("AMBER", story.diagnostics().getCompletenessLevel());
    }

    private Map<String, Object> storyRecord(
            String storyId,
            String label,
            String module,
            String domain,
            String status,
            String featureId,
            String featureTitle,
            List<Map<String, Object>> screens,
            List<Map<String, Object>> apis,
            List<Map<String, Object>> bugs,
            List<Map<String, Object>> gaps,
            List<Map<String, Object>> storyExternalArtifacts,
            List<Map<String, Object>> bugExternalArtifacts
    ) {
        return Map.ofEntries(
                Map.entry("storyId", storyId),
                Map.entry("label", label),
                Map.entry("module", module),
                Map.entry("domain", domain),
                Map.entry("storyNumber", storyId),
                Map.entry("status", status),
                Map.entry("feature", Map.of(
                        "featureId", featureId,
                        "title", featureTitle,
                        "status", "DEFINED"
                )),
                Map.entry("screens", screens),
                Map.entry("apis", apis),
                Map.entry("bugs", bugs),
                Map.entry("gaps", gaps),
                Map.entry("storyExternalArtifacts", storyExternalArtifacts),
                Map.entry("bugExternalArtifacts", bugExternalArtifacts)
        );
    }

    private Map<String, Object> screenMap(String surfaceId, String label, String routePath, String status) {
        return Map.of(
                "surfaceId", surfaceId,
                "label", label,
                "routePath", routePath,
                "status", status
        );
    }

    private Map<String, Object> apiMap(String contractId, String method, String path, String status) {
        return Map.of(
                "contractId", contractId,
                "method", method,
                "path", path,
                "status", status
        );
    }

    private Map<String, Object> bugMap(String bugId, String externalKey, String summary, String severity, String status) {
        return Map.of(
                "bugId", bugId,
                "externalKey", externalKey,
                "summary", summary,
                "severity", severity,
                "status", status
        );
    }

    private Map<String, Object> gapMap(String gapId, String gapType, String severity, String description, String status) {
        return Map.of(
                "gapId", gapId,
                "gapType", gapType,
                "severity", severity,
                "description", description,
                "status", status
        );
    }

    private Map<String, Object> externalMap(
            String externalId,
            String system,
            String externalType,
            String key,
            String syncStatus,
            String status
    ) {
        return Map.ofEntries(
                Map.entry("externalId", externalId),
                Map.entry("system", system),
                Map.entry("externalType", externalType),
                Map.entry("key", key),
                Map.entry("title", externalType + " " + key),
                Map.entry("projectScope", "Design Hub"),
                Map.entry("workflowState", "In Progress"),
                Map.entry("priority", "High"),
                Map.entry("owner", "Aisha Coleman"),
                Map.entry("reporter", "Marco Lane"),
                Map.entry("labels", List.of("design-hub", externalType.toLowerCase())),
                Map.entry("url", "https://example.invalid/" + externalId),
                Map.entry("syncStatus", syncStatus),
                Map.entry("status", status)
        );
    }

    private ReadinessDiagnosticsResponse diagnostics(String storyId, boolean qaReady, double score, String level) {
        return ReadinessDiagnosticsResponse.builder()
                .artifactType("UserStory")
                .artifactId(storyId)
                .status("APPROVED")
                .readiness(Map.of(
                        "requirementsReady", true,
                        "designReady", true,
                        "contractReady", qaReady,
                        "frontendReady", true,
                        "backendReady", qaReady,
                        "integrationReady", qaReady,
                        "qaReady", qaReady
                ))
                .completenessScore(score)
                .completenessLevel(level)
                .missingBlockingRules(List.of())
                .missingOptionalRules(List.of())
                .missingArtifacts(List.of())
                .advisoryRulesViolated(List.of())
                .build();
    }
}
