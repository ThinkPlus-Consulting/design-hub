package com.emsist.designhub.service;

import com.emsist.designhub.dto.AgentPackExportResponse;
import com.emsist.designhub.dto.PackCompleteness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentPackServiceTest {

    @Mock private Neo4jClient neo4jClient;
    @Mock private AgentReadinessService readinessService;

    @InjectMocks
    private AgentPackService packService;

    @Test
    void shouldComputeCompletenessFromReadinessChecks() {
        when(readinessService.assessAgentReadiness("US-SCR-042"))
                .thenReturn(Map.of(
                        "repoPath", true,
                        "effectiveBuildCommand", true,
                        "manifestPath", true,
                        "codeAssetPresence", true,
                        "testFileResolution", true,
                        "entrypointPath", false
                ));

        var completeness = packService.computeCompleteness("US-SCR-042");
        assertTrue(completeness.isComplete()); // 5/5 blocking pass, entrypoint is advisory
        assertEquals(100, completeness.getReadinessScore());
    }

    @Test
    void shouldReportIncompleteWhenBlockingCheckFails() {
        when(readinessService.assessAgentReadiness("US-SCR-043"))
                .thenReturn(Map.of(
                        "repoPath", true,
                        "effectiveBuildCommand", false,
                        "manifestPath", true,
                        "codeAssetPresence", false,
                        "testFileResolution", true,
                        "entrypointPath", false
                ));

        var completeness = packService.computeCompleteness("US-SCR-043");
        assertFalse(completeness.isComplete());
        assertTrue(completeness.getMissingConcerns().contains("effectiveBuildCommand"));
        assertTrue(completeness.getMissingConcerns().contains("codeAssetPresence"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldBuildAgentPackExportForStory() {
        var storyQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var taskQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var screenQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var apiQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var entityQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var applicationQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var componentQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var codeTargetQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var testCaseQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var policyQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var conventionQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var qualityConstraintQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);

        var storyFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var taskFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var screenFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var apiFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var entityFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var applicationFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var componentFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var codeTargetFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var testCaseFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var policyFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var conventionFetch = mock(Neo4jClient.RecordFetchSpec.class);
        var qualityConstraintFetch = mock(Neo4jClient.RecordFetchSpec.class);

        when(neo4jClient.query(any(String.class))).thenReturn(
                storyQuery,
                taskQuery,
                screenQuery,
                apiQuery,
                entityQuery,
                applicationQuery,
                componentQuery,
                codeTargetQuery,
                testCaseQuery,
                policyQuery,
                conventionQuery,
                qualityConstraintQuery
        );

        when(storyQuery.bind(any()).to(any(String.class))).thenReturn(storyQuery);
        when(taskQuery.bind(any()).to(any(String.class))).thenReturn(taskQuery);
        when(screenQuery.bind(any()).to(any(String.class))).thenReturn(screenQuery);
        when(apiQuery.bind(any()).to(any(String.class))).thenReturn(apiQuery);
        when(entityQuery.bind(any()).to(any(String.class))).thenReturn(entityQuery);
        when(applicationQuery.bind(any()).to(any(String.class))).thenReturn(applicationQuery);
        when(componentQuery.bind(any()).to(any(String.class))).thenReturn(componentQuery);
        when(codeTargetQuery.bind(any()).to(any(String.class))).thenReturn(codeTargetQuery);
        when(testCaseQuery.bind(any()).to(any(String.class))).thenReturn(testCaseQuery);
        when(policyQuery.bind(any()).to(any(String.class))).thenReturn(policyQuery);
        when(conventionQuery.bind(any()).to(any(String.class))).thenReturn(conventionQuery);
        when(qualityConstraintQuery.bind(any()).to(any(String.class))).thenReturn(qualityConstraintQuery);

        when(storyQuery.fetch()).thenReturn(storyFetch);
        when(taskQuery.fetch()).thenReturn(taskFetch);
        when(screenQuery.fetch()).thenReturn(screenFetch);
        when(apiQuery.fetch()).thenReturn(apiFetch);
        when(entityQuery.fetch()).thenReturn(entityFetch);
        when(applicationQuery.fetch()).thenReturn(applicationFetch);
        when(componentQuery.fetch()).thenReturn(componentFetch);
        when(codeTargetQuery.fetch()).thenReturn(codeTargetFetch);
        when(testCaseQuery.fetch()).thenReturn(testCaseFetch);
        when(policyQuery.fetch()).thenReturn(policyFetch);
        when(conventionQuery.fetch()).thenReturn(conventionFetch);
        when(qualityConstraintQuery.fetch()).thenReturn(qualityConstraintFetch);

        when(storyFetch.first()).thenReturn((Optional) Optional.of(Map.of(
                "id", "US-AI-090",
                "nodeType", "UserStory",
                "displayName", "Builder canvas interactions ready for agent composition",
                "status", "APPROVED"
        )));
        when(taskFetch.all()).thenReturn((List) List.of(Map.of(
                "id", "TASK-AI-001",
                "nodeType", "Task",
                "displayName", "Implement builder orchestration handlers",
                "status", "IN_IMPLEMENTATION"
        )));
        when(screenFetch.all()).thenReturn((List) List.of(Map.of(
                "id", "SCR-AGT-ORCH",
                "nodeType", "Screen",
                "displayName", "Agent Orchestration Canvas",
                "status", "APPROVED"
        )));
        when(apiFetch.all()).thenReturn((List) List.of(Map.of(
                "id", "API-AGT-001",
                "nodeType", "ApiContract",
                "displayName", "/api/agents/run",
                "status", "APPROVED"
        )));
        when(entityFetch.all()).thenReturn((List) List.of(Map.of(
                "id", "ENT-AGT-001",
                "nodeType", "DataEntity",
                "displayName", "AgentRunRequest",
                "status", "APPROVED"
        )));
        when(applicationFetch.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("id", "APP-DH"),
                Map.entry("name", "Design Hub"),
                Map.entry("applicationType", "WEB"),
                Map.entry("workspaceType", "MONOREPO"),
                Map.entry("repoPath", "."),
                Map.entry("repoUrl", "https://example.invalid/emsist/design-hub.git"),
                Map.entry("defaultBuildCommand", "mvn -q -DskipTests package"),
                Map.entry("defaultTestCommand", "mvn -q test"),
                Map.entry("bootstrapSteps", List.of("npm install", "docker compose up -d neo4j"))
        )));
        when(componentFetch.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("id", "CMP-FE-001"),
                Map.entry("nodeType", "ApplicationComponent"),
                Map.entry("displayName", "Builder Canvas"),
                Map.entry("status", "APPROVED"),
                Map.entry("applicationId", "APP-DH"),
                Map.entry("applicationName", "Design Hub"),
                Map.entry("frameworkFamily", "Angular"),
                Map.entry("frameworkName", "Angular"),
                Map.entry("frameworkVersion", "21"),
                Map.entry("runtime", "BROWSER"),
                Map.entry("language", "TypeScript"),
                Map.entry("languageVersion", "5"),
                Map.entry("modulePath", "frontend/src/app/features/design-hub"),
                Map.entry("manifestPath", "frontend/package.json"),
                Map.entry("buildCommand", "npm run build"),
                Map.entry("testCommand", "npm run test:e2e"),
                Map.entry("entrypointPath", "frontend/src/main.ts"),
                Map.entry("localRunCommand", "npm start -- --port 4300"),
                Map.entry("secretPrerequisites", List.of("JIRA_WEBHOOK_SECRET")),
                Map.entry("fixturePrerequisites", List.of("Seeded registry graph")),
                Map.entry("localRunPrerequisites", List.of("Backend running on localhost:8091"))
        )));
        when(codeTargetFetch.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("id", "CA-FE-001"),
                Map.entry("nodeType", "CodeAsset"),
                Map.entry("displayName", "frontend/src/app/features/design-hub/design-hub.page.ts"),
                Map.entry("status", "APPROVED"),
                Map.entry("assetType", "SOURCE"),
                Map.entry("filePath", "frontend/src/app/features/design-hub/design-hub.page.ts"),
                Map.entry("language", "TypeScript"),
                Map.entry("layerType", "PAGE"),
                Map.entry("changePolicy", "OPEN"),
                Map.entry("componentId", "CMP-FE-001"),
                Map.entry("componentName", "Builder Canvas"),
                Map.entry("applicationId", "APP-DH"),
                Map.entry("applicationName", "Design Hub")
        )));
        when(testCaseFetch.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("id", "TC-AI-001"),
                Map.entry("displayName", "Builder orchestration renders state"),
                Map.entry("status", "APPROVED"),
                Map.entry("testType", "E2E"),
                Map.entry("testCommand", "npm run verify:ui"),
                Map.entry("testFilePath", "frontend/tests/graph/delivery-view.spec.ts"),
                Map.entry("locatedInId", "CA-TEST-001"),
                Map.entry("locatedInPath", "frontend/tests/graph/delivery-view.spec.ts")
        )));
        when(policyFetch.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("id", "POL-DH-AGENT-001"),
                Map.entry("name", "Design Hub bounded automation policy"),
                Map.entry("allowedRepos", List.of(".", "frontend")),
                Map.entry("allowedCommands", List.of("npm run build", "npm run test:e2e")),
                Map.entry("forbiddenCommands", List.of("git reset --hard")),
                Map.entry("allowedEnvironments", List.of("LOCAL_DEV", "CI")),
                Map.entry("secretScopes", List.of("neo4j", "jira")),
                Map.entry("maxFilesTouched", 40),
                Map.entry("requiresHumanApproval", true),
                Map.entry("approvalThreshold", "EXTERNAL_SIDE_EFFECT_OR_DESTRUCTIVE_ACTION")
        )));
        when(conventionFetch.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("id", "CONV-FE-001"),
                Map.entry("name", "Use EMSIST token surfaces"),
                Map.entry("category", "STRUCTURE"),
                Map.entry("enforcement", "MANDATORY"),
                Map.entry("scope", "FRONTEND"),
                Map.entry("docRef", "Documentation/design-system"),
                Map.entry("activeStatus", "ACTIVE")
        )));
        when(qualityConstraintFetch.all()).thenReturn((List) List.of(Map.ofEntries(
                Map.entry("id", "QC-AI-001"),
                Map.entry("name", "Response under 2s"),
                Map.entry("constraintType", "PERFORMANCE"),
                Map.entry("priority", "HIGH"),
                Map.entry("threshold", "<=2s"),
                Map.entry("status", "APPROVED")
        )));

        when(readinessService.assessAgentReadiness("US-AI-090"))
                .thenReturn(Map.of(
                        "repoPath", true,
                        "effectiveBuildCommand", true,
                        "manifestPath", true,
                        "codeAssetPresence", true,
                        "testFileResolution", true,
                        "entrypointPath", true
                ));

        AgentPackExportResponse pack = packService.buildPack("US-AI-090").orElseThrow();

        assertEquals("PACK-US-AI-090", pack.packId());
        assertEquals("US-AI-090", pack.story().id());
        assertEquals(100, pack.completeness().getReadinessScore());
        assertEquals(1, pack.tasks().size());
        assertEquals(1, pack.deliveredScreens().size());
        assertEquals(1, pack.deliveredApis().size());
        assertEquals(1, pack.deliveredEntities().size());
        assertEquals(1, pack.applications().size());
        assertEquals(".", pack.applications().get(0).repoPath());
        assertEquals(1, pack.components().size());
        assertEquals("APP-DH", pack.components().get(0).applicationId());
        assertEquals("npm start -- --port 4300", pack.components().get(0).localRunCommand());
        assertEquals(1, pack.codeTargets().size());
        assertEquals("CMP-FE-001", pack.codeTargets().get(0).componentId());
        assertEquals("OPEN", pack.codeTargets().get(0).changePolicy());
        assertEquals(1, pack.testCases().size());
        assertEquals(1, pack.policies().size());
        assertEquals(Integer.valueOf(40), pack.policies().get(0).maxFilesTouched());
        assertEquals(1, pack.conventions().size());
        assertEquals(1, pack.qualityConstraints().size());
        assertTrue(pack.readinessChecks().get("repoPath"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnEmptyWhenStoryDoesNotExist() {
        var storyQuery = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var storyFetch = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(any(String.class))).thenReturn(storyQuery);
        when(storyQuery.bind(any()).to(any(String.class))).thenReturn(storyQuery);
        when(storyQuery.fetch()).thenReturn(storyFetch);
        when(storyFetch.first()).thenReturn((Optional) Optional.empty());

        Optional<AgentPackExportResponse> pack = packService.buildPack("US-MISSING-001");

        assertTrue(pack.isEmpty());
    }
}
