package com.emsist.designhub.service;

import com.emsist.designhub.dto.GraphBenchmarkResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BenchmarkQueryServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @InjectMocks
    private BenchmarkQueryService service;

    @Test
    @SuppressWarnings("unchecked")
    void shouldAggregateBenchmarkScoresAcrossConfiguredTypes() {
        when(neo4jClient.query(anyString())).thenAnswer(invocation -> {
            String cypher = invocation.getArgument(0, String.class);
            String label = cypher.substring(cypher.indexOf("MATCH (n:") + 9, cypher.indexOf(")", cypher.indexOf("MATCH (n:")));

            Map<String, Object> row = switch (label) {
                case "Screen" -> Map.of(
                        "totalNodes", 10L,
                        "attributeDepthScore", 82.0,
                        "relationshipCoverageScore", 70.0,
                        "sourceTraceabilityScore", 40.0
                );
                case "Epic", "RequirementPortfolio" -> {
                    Map<String, Object> emptyRow = new java.util.LinkedHashMap<>();
                    emptyRow.put("totalNodes", 0L);
                    emptyRow.put("attributeDepthScore", 0.0);
                    emptyRow.put("relationshipCoverageScore", 0.0);
                    emptyRow.put("sourceTraceabilityScore", null);
                    yield emptyRow;
                }
                case "ValidationRole" -> {
                    Map<String, Object> validationRoleRow = new java.util.LinkedHashMap<>();
                    validationRoleRow.put("totalNodes", 2L);
                    validationRoleRow.put("attributeDepthScore", 75.0);
                    validationRoleRow.put("relationshipCoverageScore", 100.0);
                    validationRoleRow.put("sourceTraceabilityScore", null);
                    yield validationRoleRow;
                }
                default -> Map.of(
                        "totalNodes", 5L,
                        "attributeDepthScore", 75.0,
                        "relationshipCoverageScore", 60.0,
                        "sourceTraceabilityScore", 50.0
                );
            };

            var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
            var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
            when(runnableSpec.fetch()).thenReturn(fetchSpec);
            when(fetchSpec.first()).thenReturn((Optional) Optional.of(row));
            return runnableSpec;
        });

        GraphBenchmarkResponse response = service.getBenchmark();

        assertEquals(71, response.summary().coveredNodeTypes());
        assertTrue(response.summary().overallScore() > 0.0);

        GraphBenchmarkResponse.BenchmarkTypeScore screen = response.types().stream()
                .filter(type -> "Screen".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(10L, screen.totalNodes());
        assertEquals(82.0, screen.attributeDepthScore());
        assertEquals(100.0, screen.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore portfolio = response.types().stream()
                .filter(type -> "RequirementPortfolio".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, portfolio.queryabilityScore());
        assertTrue(portfolio.gapRecommendations().stream()
                .anyMatch(message -> message.contains("No nodes currently present")));

        GraphBenchmarkResponse.BenchmarkTypeScore policy = response.types().stream()
                .filter(type -> "AgentPolicy".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, policy.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore codeAsset = response.types().stream()
                .filter(type -> "CodeAsset".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, codeAsset.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore validationRule = response.types().stream()
                .filter(type -> "ValidationRule".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, validationRule.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore processActivity = response.types().stream()
                .filter(type -> "ProcessActivity".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, processActivity.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore decision = response.types().stream()
                .filter(type -> "Decision".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, decision.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore assessment = response.types().stream()
                .filter(type -> "Assessment".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, assessment.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore organization = response.types().stream()
                .filter(type -> "Organization".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, organization.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore screenState = response.types().stream()
                .filter(type -> "ScreenState".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, screenState.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore project = response.types().stream()
                .filter(type -> "ProjectInstance".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, project.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore milestone = response.types().stream()
                .filter(type -> "Milestone".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, milestone.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore topic = response.types().stream()
                .filter(type -> "Topic".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, topic.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore integration = response.types().stream()
                .filter(type -> "Integration".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, integration.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore openQuestion = response.types().stream()
                .filter(type -> "OpenQuestion".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, openQuestion.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore journeyStep = response.types().stream()
                .filter(type -> "JourneyStep".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, journeyStep.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore locale = response.types().stream()
                .filter(type -> "Locale".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, locale.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore translationKey = response.types().stream()
                .filter(type -> "TranslationKey".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(100.0, translationKey.queryabilityScore());

        GraphBenchmarkResponse.BenchmarkTypeScore validationRole = response.types().stream()
                .filter(type -> "ValidationRole".equals(type.nodeType()))
                .findFirst()
                .orElseThrow();
        assertEquals(0, validationRole.targetRelationshipCount());
        assertEquals(100.0, validationRole.relationshipCoverageScore());
    }
}
