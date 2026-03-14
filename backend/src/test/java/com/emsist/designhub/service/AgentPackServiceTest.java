package com.emsist.designhub.service;

import com.emsist.designhub.dto.PackCompleteness;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
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
}
