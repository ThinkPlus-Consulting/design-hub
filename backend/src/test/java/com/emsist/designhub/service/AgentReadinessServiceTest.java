package com.emsist.designhub.service;

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
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentReadinessServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @InjectMocks
    private AgentReadinessService service;

    @Test
    void shouldReturnAllChecksForAgentReadinessAssessment() {
        var checkNames = AgentReadinessService.AGENT_READY_CHECKS;
        assertTrue(checkNames.contains("repoPath"));
        assertTrue(checkNames.contains("effectiveBuildCommand"));
        assertTrue(checkNames.contains("manifestPath"));
        assertTrue(checkNames.contains("codeAssetPresence"));
        assertTrue(checkNames.contains("testFileResolution"));
        assertTrue(checkNames.contains("entrypointPath"));
        assertEquals(6, checkNames.size());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldQueryImplementationPackTraversalAndMapResults() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "hasRepoPath", true,
                "hasBuildCommand", true,
                "hasManifestPath", true,
                "hasCodeAsset", true,
                "hasTestFile", true,
                "hasEntryPoint", false
        )));

        var result = service.assessAgentReadiness("US-AI-090");

        assertEquals(Map.of(
                "repoPath", true,
                "effectiveBuildCommand", true,
                "manifestPath", true,
                "codeAssetPresence", true,
                "testFileResolution", true,
                "entrypointPath", false
        ), result);

        verify(neo4jClient).query((String) argThat(cypher ->
                ((String) cypher).contains("DELIVERS")
                && ((String) cypher).contains("SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE")
                && ((String) cypher).contains("HAS_CODE_ASSET")
                && ((String) cypher).contains("VERIFIED_BY")
                && ((String) cypher).contains("LOCATED_IN")
                && ((String) cypher).contains("collect(DISTINCT comp2) AS messageComps")
                && ((String) cypher).contains("directComps + messageComps AS allComps")));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnAllFalseWhenStoryIsMissing() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.empty());

        var result = service.assessAgentReadiness("US-MISSING");

        assertEquals(6, result.size());
        assertTrue(result.values().stream().noneMatch(Boolean.TRUE::equals));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldTreatEntryPointAsAdvisory() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "hasRepoPath", true,
                "hasBuildCommand", true,
                "hasManifestPath", true,
                "hasCodeAsset", true,
                "hasTestFile", true,
                "hasEntryPoint", false
        )));

        assertTrue(service.isAgentReady("US-AI-090"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnFalseWhenBlockingCheckFails() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(any(String.class))).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(any(String.class))).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "hasRepoPath", true,
                "hasBuildCommand", true,
                "hasManifestPath", true,
                "hasCodeAsset", false,
                "hasTestFile", true,
                "hasEntryPoint", true
        )));

        assertFalse(service.isAgentReady("US-AI-090"));
    }
}
