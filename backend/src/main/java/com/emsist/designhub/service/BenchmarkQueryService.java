package com.emsist.designhub.service;

import com.emsist.designhub.dto.GraphBenchmarkResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BenchmarkQueryService {

    private final Neo4jClient neo4jClient;

    public GraphBenchmarkResponse getBenchmark() {
        List<GraphBenchmarkResponse.BenchmarkTypeScore> types = Arrays.stream(BenchmarkNodeType.values())
                .map(this::measureType)
                .toList();

        double attributeDepth = average(types.stream()
                .map(GraphBenchmarkResponse.BenchmarkTypeScore::attributeDepthScore)
                .toList());
        double relationshipCoverage = average(types.stream()
                .map(GraphBenchmarkResponse.BenchmarkTypeScore::relationshipCoverageScore)
                .toList());
        double sourceTraceability = average(types.stream()
                .filter(GraphBenchmarkResponse.BenchmarkTypeScore::sourceTraceabilityApplicable)
                .map(GraphBenchmarkResponse.BenchmarkTypeScore::sourceTraceabilityScore)
                .filter(Objects::nonNull)
                .toList());
        double queryability = average(types.stream()
                .map(GraphBenchmarkResponse.BenchmarkTypeScore::queryabilityScore)
                .toList());
        double overall = round(average(List.of(attributeDepth, relationshipCoverage, sourceTraceability, queryability)));
        long totalNodes = types.stream().mapToLong(GraphBenchmarkResponse.BenchmarkTypeScore::totalNodes).sum();

        List<GraphBenchmarkResponse.BenchmarkDimensionScore> dimensions = List.of(
                dimension(
                        "attributeDepth",
                        attributeDepth,
                        "Average populated target-attribute coverage across the currently benchmarked graph object types."
                ),
                dimension(
                        "relationshipCoverage",
                        relationshipCoverage,
                        "Average implemented relationship-pattern coverage across the currently benchmarked graph object types."
                ),
                dimension(
                        "sourceTraceability",
                        sourceTraceability,
                        "Average `HAS_SOURCE` coverage for the implementation-driving artifact types in this benchmark slice."
                ),
                dimension(
                        "queryability",
                        queryability,
                        "API support across graph object search, relation expansion, and dedicated traversal/traceability entry points."
                )
        );

        return new GraphBenchmarkResponse(
                new GraphBenchmarkResponse.BenchmarkSummary(
                        "Covers the current primary traversal types, upper traceability spine nodes, and the active automation/traceability metadata families; full 71-node benchmark aggregation is still pending.",
                        types.size(),
                        totalNodes,
                        overall,
                        dimensions
                ),
                types
        );
    }

    private GraphBenchmarkResponse.BenchmarkTypeScore measureType(BenchmarkNodeType type) {
        Map<String, Object> row = neo4jClient.query(query(type))
                .fetch()
                .first()
                .orElseGet(this::emptyRow);

        long totalNodes = toLong(row.get("totalNodes"));
        double attributeDepthScore = round(toDouble(row.get("attributeDepthScore")));
        double relationshipCoverageScore = round(toDouble(row.get("relationshipCoverageScore")));
        Double sourceTraceabilityScore = type.sourceTraceabilityApplicable
                ? roundNullable(row.get("sourceTraceabilityScore"))
                : null;
        double queryabilityScore = type.queryabilityScore();

        List<Double> overallInputs = sourceTraceabilityScore == null
                ? List.of(attributeDepthScore, relationshipCoverageScore, queryabilityScore)
                : List.of(attributeDepthScore, relationshipCoverageScore, sourceTraceabilityScore, queryabilityScore);
        double overallScore = round(average(overallInputs));

        return new GraphBenchmarkResponse.BenchmarkTypeScore(
                type.label,
                totalNodes,
                type.attributeChecks.size(),
                attributeDepthScore,
                type.relationshipChecks.size(),
                relationshipCoverageScore,
                type.sourceTraceabilityApplicable,
                sourceTraceabilityScore,
                queryabilityScore,
                overallScore,
                recommendations(type, totalNodes, attributeDepthScore, relationshipCoverageScore, sourceTraceabilityScore, queryabilityScore)
        );
    }

    private List<String> recommendations(
            BenchmarkNodeType type,
            long totalNodes,
            double attributeDepthScore,
            double relationshipCoverageScore,
            Double sourceTraceabilityScore,
            double queryabilityScore
    ) {
        List<String> recommendations = new java.util.ArrayList<>();
        if (totalNodes == 0) {
            recommendations.add("No nodes currently present in the graph for this type.");
        }
        if (attributeDepthScore < 70.0) {
            recommendations.add("Attribute depth is below the current target threshold.");
        }
        if (relationshipCoverageScore < 60.0) {
            recommendations.add("Relationship coverage is below the current target threshold.");
        }
        if (type.sourceTraceabilityApplicable && sourceTraceabilityScore != null && sourceTraceabilityScore < 50.0) {
            recommendations.add("Source traceability coverage is sparse for this artifact type.");
        }
        if (queryabilityScore < 100.0) {
            recommendations.add("This type is not fully exposed through the current graph query API surface.");
        }
        if (recommendations.isEmpty()) {
            recommendations.add("No immediate benchmark gaps detected in the current slice.");
        }
        return List.copyOf(recommendations);
    }

    private GraphBenchmarkResponse.BenchmarkDimensionScore dimension(String name, double score, String detail) {
        return new GraphBenchmarkResponse.BenchmarkDimensionScore(name, score, status(score), detail);
    }

    private String query(BenchmarkNodeType type) {
        String attributeScoreExpression = scoreExpression(type.attributeChecks, "attributeDepthScore");
        String relationshipScoreExpression = scoreExpression(type.relationshipChecks, "relationshipCoverageScore");
        String sourceTraceabilityExpression = type.sourceTraceabilityApplicable
                ? """
                        CASE WHEN count(n) = 0 THEN 0.0
                             ELSE round(1000.0 * avg(CASE WHEN EXISTS { (n)-[:HAS_SOURCE]->(:SourceReference) } THEN 1.0 ELSE 0.0 END)) / 10.0
                        END AS sourceTraceabilityScore
                        """
                : "null AS sourceTraceabilityScore";

        return """
                MATCH (n:%s)
                RETURN count(n) AS totalNodes,
                       %s,
                       %s,
                       %s
                """.formatted(
                type.label,
                attributeScoreExpression,
                relationshipScoreExpression,
                sourceTraceabilityExpression
        );
    }

    private String scoreExpression(List<String> checks, String alias) {
        if (checks.isEmpty()) {
            return "100.0 AS " + alias;
        }
        return """
                CASE WHEN count(n) = 0 THEN 0.0
                     ELSE round(1000.0 * avg((%s) * 1.0 / %d)) / 10.0
                END AS %s
                """.formatted(sumChecks(checks), checks.size(), alias).strip();
    }

    private String sumChecks(List<String> checks) {
        return checks.stream()
                .map(check -> "CASE WHEN " + check + " THEN 1 ELSE 0 END")
                .collect(Collectors.joining(" + "));
    }

    private Map<String, Object> emptyRow() {
        Map<String, Object> row = new java.util.LinkedHashMap<>();
        row.put("totalNodes", 0L);
        row.put("attributeDepthScore", 0.0);
        row.put("relationshipCoverageScore", 0.0);
        row.put("sourceTraceabilityScore", null);
        return row;
    }

    private double average(List<Double> values) {
        if (values.isEmpty()) {
            return 0.0;
        }
        return values.stream().mapToDouble(Double::doubleValue).average().orElse(0.0);
    }

    private double round(double value) {
        return Math.round(value * 10.0) / 10.0;
    }

    private Double roundNullable(Object value) {
        if (value == null) {
            return null;
        }
        return round(toDouble(value));
    }

    private double toDouble(Object value) {
        return value instanceof Number number ? number.doubleValue() : 0.0;
    }

    private long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private String status(double score) {
        if (score >= 80.0) {
            return "GREEN";
        }
        if (score >= 50.0) {
            return "AMBER";
        }
        return "RED";
    }

    private enum BenchmarkNodeType {
        SCREEN(
                "Screen",
                List.of(
                        "n.surfaceId IS NOT NULL",
                        "n.label IS NOT NULL AND trim(n.label) <> ''",
                        "n.module IS NOT NULL AND trim(n.module) <> ''",
                        "n.routePath IS NOT NULL AND trim(n.routePath) <> ''",
                        "n.status IS NOT NULL",
                        "n.wcag IS NOT NULL AND trim(n.wcag) <> ''",
                        "n.responsive IS NOT NULL",
                        "n.roleAdaptive IS NOT NULL",
                        "n.deepLinkable IS NOT NULL",
                        "n.loadingStates IS NOT NULL",
                        "n.messageRegistryCount IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)<-[:DELIVERS]-(:UserStory) }",
                        "EXISTS { (n)-[:HAS_INTERACTION]->() }",
                        "EXISTS { (n)-[:ACCESSIBLE_BY_ROLE]->() }",
                        "EXISTS { (n)-[:HAS_MESSAGE]->() }",
                        "EXISTS { (n)-[:TRANSITIONS_TO]->() }",
                        "EXISTS { (n)-[:HAS_GAP]->() }"
                ),
                true,
                true,
                true,
                true
        ),
        SCREEN_STATE(
                "ScreenState",
                List.of(
                        "n.stateId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.stateType IS NOT NULL AND trim(n.stateType) <> ''",
                        "n.entryCondition IS NOT NULL AND trim(n.entryCondition) <> ''",
                        "n.exitCondition IS NOT NULL AND trim(n.exitCondition) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:BELONGS_TO_SCREEN]->(:Screen) }"
                ),
                false,
                true,
                true,
                true
        ),
        USER_STORY(
                "UserStory",
                List.of(
                        "n.storyId IS NOT NULL",
                        "n.label IS NOT NULL AND trim(n.label) <> ''",
                        "n.module IS NOT NULL AND trim(n.module) <> ''",
                        "n.domain IS NOT NULL AND trim(n.domain) <> ''",
                        "n.storyNumber IS NOT NULL AND trim(n.storyNumber) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:DELIVERS]->() }",
                        "EXISTS { (n)-[:HAS_CRITERION]->() }",
                        "EXISTS { (n)-[:HAS_TASK]->() }",
                        "EXISTS { (n)-[:VERIFIED_BY]->() }",
                        "EXISTS { (n)-[:HAS_SOURCE]->(:SourceReference) }"
                ),
                true,
                true,
                true,
                true
        ),
        PROJECT_INSTANCE(
                "ProjectInstance",
                List.of(
                        "n.projectId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.projectType IS NOT NULL AND trim(n.projectType) <> ''",
                        "n.startDate IS NOT NULL",
                        "n.targetDate IS NOT NULL",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_PORTFOLIO]->(:RequirementPortfolio) }",
                        "EXISTS { (n)-[:TARGETS_CAPABILITY]->(:BusinessCapability) }",
                        "EXISTS { (n)-[:HAS_MILESTONE]->(:Milestone) }",
                        "EXISTS { (n)-[:HAS_TASK]->(:Task) }",
                        "EXISTS { (n)-[:ENHANCES_APPLICATION]->(:Application) } OR EXISTS { (n)-[:CREATES_APPLICATION]->(:Application) } OR EXISTS { (n)-[:INTEGRATES_WITH]->(:Application) }",
                        "EXISTS { (n)-[:ENHANCES_COMPONENT]->(:ApplicationComponent) } OR EXISTS { (n)-[:CREATES_COMPONENT]->(:ApplicationComponent) }"
                ),
                false,
                true,
                true,
                true
        ),
        JOURNEY(
                "Journey",
                List.of(
                        "n.journeyId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.goalStatement IS NOT NULL AND trim(n.goalStatement) <> ''",
                        "n.status IS NOT NULL",
                        "n.personaId IS NOT NULL AND trim(n.personaId) <> ''"
                ),
                List.of(
                        "EXISTS { (n)-[:PERFORMED_BY_PERSONA]->() }",
                        "EXISTS { (n)-[:HAS_STEP]->() }"
                ),
                true,
                true,
                true,
                true
        ),
        JOURNEY_STEP(
                "JourneyStep",
                List.of(
                        "n.stepId IS NOT NULL",
                        "n.journeyId IS NOT NULL AND trim(n.journeyId) <> ''",
                        "n.label IS NOT NULL AND trim(n.label) <> ''",
                        "n.trigger IS NOT NULL AND trim(n.trigger) <> ''",
                        "n.preCondition IS NOT NULL AND trim(n.preCondition) <> ''",
                        "n.postCondition IS NOT NULL AND trim(n.postCondition) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Journey)-[:HAS_STEP]->(n) }",
                        "EXISTS { (n)-[:USES_SCREEN]->(:Screen) }"
                ),
                true,
                true,
                true,
                true
        ),
        PERSONA(
                "Persona",
                List.of(
                        "n.personaId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.status IS NOT NULL",
                        "size(coalesce(n.roleKeys, [])) > 0"
                ),
                List.of(
                        "EXISTS { (:Journey)-[:PERFORMED_BY_PERSONA]->(n) }",
                        "EXISTS { (:Screen)-[:USED_BY_PERSONA]->(n) } OR EXISTS { (:Touchpoint)-[:USED_BY_PERSONA]->(n) } OR EXISTS { (:Interaction)-[:USED_BY_PERSONA]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        EXTERNAL_ARTIFACT(
                "ExternalArtifact",
                List.of(
                        "n.externalId IS NOT NULL",
                        "n.system IS NOT NULL AND trim(n.system) <> ''",
                        "n.externalType IS NOT NULL AND trim(n.externalType) <> ''",
                        "coalesce(n.title, n.key) IS NOT NULL AND trim(coalesce(n.title, n.key)) <> ''",
                        "n.syncStatus IS NOT NULL AND trim(n.syncStatus) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:REPRESENTS_STORY]->() } OR EXISTS { (n)-[:REPRESENTS_BUG]->() } OR EXISTS { (n)-[:REPRESENTS_FEATURE]->() } OR EXISTS { (n)-[:REPRESENTS_TASK]->() } OR EXISTS { (n)-[:REPRESENTS_FINDING]->() }",
                        "EXISTS { (n)-[:PARENT_OF]->() } OR EXISTS { (n)-[:CHILD_OF]->() }",
                        "EXISTS { (n)-[:DEPENDS_ON]->() } OR EXISTS { (n)-[:RELATES_TO]->() } OR EXISTS { (n)-[:DUPLICATES]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        INTERACTION(
                "Interaction",
                List.of(
                        "n.interactionId IS NOT NULL",
                        "n.surfaceId IS NOT NULL AND trim(n.surfaceId) <> ''",
                        "n.element IS NOT NULL AND trim(n.element) <> ''",
                        "n.trigger IS NOT NULL AND trim(n.trigger) <> ''",
                        "size(coalesce(n.apiCalls, [])) > 0 OR EXISTS { (n)-[:CALLS_API]->() }"
                ),
                List.of(
                        "EXISTS { (n)-[:CALLS_API]->() }",
                        "EXISTS { (n)-[:REQUIRES_PERMISSION]->() }",
                        "EXISTS { (n)-[:TRIGGERS_CONFIRMATION]->() }",
                        "EXISTS { (n)-[:ON_ERROR_SHOWS]->() }",
                        "EXISTS { (n)-[:ACCESSIBLE_BY_ROLE]->() }"
                ),
                true,
                true,
                true,
                true
        ),
        TRANSITION(
                "Transition",
                List.of(
                        "n.transitionId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.transitionType IS NOT NULL AND trim(n.transitionType) <> ''",
                        "n.guard IS NOT NULL AND trim(n.guard) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:FROM_SCREEN]->(:Screen) }",
                        "EXISTS { (n)-[:TO_SCREEN]->(:Screen) }",
                        "EXISTS { (n)-[:CAUSED_BY_INTERACTION]->(:Interaction) }"
                ),
                false,
                true,
                true,
                true
        ),
        TOPIC(
                "Topic",
                List.of(
                        "n.topicId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:GROUPS_JOURNEY]->(:Journey) }",
                        "EXISTS { (n)-[:GROUPS_FEATURE]->(:Feature) }"
                ),
                false,
                true,
                true,
                true
        ),
        TOUCHPOINT(
                "Touchpoint",
                List.of(
                        "n.touchpointId IS NOT NULL",
                        "n.label IS NOT NULL AND trim(n.label) <> ''",
                        "n.surfaceId IS NOT NULL AND trim(n.surfaceId) <> ''",
                        "size(coalesce(n.personaIds, [])) > 0 OR EXISTS { (n)-[:USED_BY_PERSONA]->() }"
                ),
                List.of(
                        "EXISTS { (n)-[:TARGETS]->() }",
                        "EXISTS { (n)-[:DELIVERED_VIA_CHANNEL]->() }",
                        "EXISTS { (n)-[:USED_BY_PERSONA]->() }",
                        "EXISTS { (n)-[:ACCESSIBLE_BY_ROLE]->() }"
                ),
                true,
                true,
                true,
                true
        ),
        EDGE_CASE(
                "EdgeCase",
                List.of(
                        "n.edgeCaseId IS NOT NULL",
                        "n.context IS NOT NULL AND trim(n.context) <> ''",
                        "n.behavior IS NOT NULL AND trim(n.behavior) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:AFFECTS_STORY]->(:UserStory) }",
                        "EXISTS { (n)-[:AFFECTS_SCREEN]->(:Screen) }",
                        "EXISTS { (n)-[:AFFECTS_JOURNEY_STEP]->(:JourneyStep) }"
                ),
                false,
                true,
                true,
                true
        ),
        EXCEPTION_CASE(
                "ExceptionCase",
                List.of(
                        "n.exceptionId IS NOT NULL",
                        "n.context IS NOT NULL AND trim(n.context) <> ''",
                        "n.behavior IS NOT NULL AND trim(n.behavior) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:AFFECTS_INTERACTION]->(:Interaction) }",
                        "EXISTS { (n)-[:AFFECTS_API]->(:ApiContract) }",
                        "EXISTS { (n)-[:AFFECTS_JOURNEY_STEP]->(:JourneyStep) }"
                ),
                false,
                true,
                true,
                true
        ),
        CHANNEL(
                "Channel",
                List.of(
                        "n.channelCode IS NOT NULL",
                        "n.displayName IS NOT NULL AND trim(n.displayName) <> ''",
                        "n.channelType IS NOT NULL AND trim(n.channelType) <> ''"
                ),
                List.of(
                        "EXISTS { (:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        INTEGRATION(
                "Integration",
                List.of(
                        "n.integrationId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.integrationType IS NOT NULL AND trim(n.integrationType) <> ''",
                        "n.sourceSystem IS NOT NULL AND trim(n.sourceSystem) <> ''",
                        "n.targetSystem IS NOT NULL AND trim(n.targetSystem) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:USES_API]->(:ApiContract) }"
                ),
                false,
                true,
                true,
                true
        ),
        API_CONTRACT(
                "ApiContract",
                List.of(
                        "n.contractId IS NOT NULL",
                        "n.method IS NOT NULL AND trim(n.method) <> ''",
                        "n.path IS NOT NULL AND trim(n.path) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_REQUEST]->() }",
                        "EXISTS { (n)-[:HAS_RESPONSE]->() }",
                        "EXISTS { (n)-[:HAS_ERROR]->() }"
                ),
                true,
                true,
                true,
                true
        ),
        DATA_ENTITY(
                "DataEntity",
                List.of(
                        "n.entityId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.entityType IS NOT NULL AND trim(n.entityType) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_FIELD]->() }",
                        "EXISTS { (n)-[:HAS_QUALITY_CONSTRAINT]->() }"
                ),
                true,
                true,
                true,
                true
        ),
        ACCEPTANCE_CRITERION(
                "AcceptanceCriterion",
                List.of(
                        "n.criterionId IS NOT NULL",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.givenWhenThen IS NOT NULL AND trim(n.givenWhenThen) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:UserStory)-[:HAS_CRITERION]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        DATA_FIELD(
                "DataField",
                List.of(
                        "n.fieldId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.dataType IS NOT NULL AND trim(n.dataType) <> ''",
                        "n.required IS NOT NULL",
                        "n.constraints IS NOT NULL AND trim(n.constraints) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:DataEntity)-[:HAS_FIELD]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        BUSINESS_DOMAIN(
                "BusinessDomain",
                List.of(
                        "n.domainCode IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.activeStatus IS NOT NULL AND trim(n.activeStatus) <> ''"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_CAPABILITY]->(:BusinessCapability) }"
                ),
                false,
                true,
                true,
                true
        ),
        BUSINESS_OBJECTIVE(
                "BusinessObjective",
                List.of(
                        "n.objectiveId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_FEATURE]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        FEATURE(
                "Feature",
                List.of(
                        "n.featureId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_STORY]->() }",
                        "EXISTS { (:Epic)-[:HAS_FEATURE]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        DECISION(
                "Decision",
                List.of(
                        "n.decisionId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.context IS NOT NULL AND trim(n.context) <> ''",
                        "n.outcome IS NOT NULL AND trim(n.outcome) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:AFFECTS_FEATURE]->(:Feature) }",
                        "EXISTS { (n)-[:AFFECTS_SCREEN]->(:Screen) }",
                        "EXISTS { (n)-[:AFFECTS_API]->(:ApiContract) }"
                ),
                false,
                true,
                true,
                true
        ),
        ASSUMPTION(
                "Assumption",
                List.of(
                        "n.assumptionId IS NOT NULL",
                        "n.statement IS NOT NULL AND trim(n.statement) <> ''",
                        "n.impact IS NOT NULL AND trim(n.impact) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:UNDERLIES_FEATURE]->(:Feature) }",
                        "EXISTS { (n)-[:UNDERLIES_STORY]->(:UserStory) }"
                ),
                false,
                true,
                true,
                true
        ),
        GOVERNANCE_CONSTRAINT(
                "Constraint",
                List.of(
                        "n.constraintId IS NOT NULL",
                        "n.constraintType IS NOT NULL AND trim(n.constraintType) <> ''",
                        "n.statement IS NOT NULL AND trim(n.statement) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:CONSTRAINS_FEATURE]->(:Feature) }",
                        "EXISTS { (n)-[:CONSTRAINS_API]->(:ApiContract) }"
                ),
                false,
                true,
                true,
                true
        ),
        ASSESSMENT(
                "Assessment",
                List.of(
                        "n.assessmentId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.assessmentType IS NOT NULL",
                        "n.targetKind IS NOT NULL",
                        "n.assessmentDate IS NOT NULL",
                        "n.assessor IS NOT NULL AND trim(n.assessor) <> ''",
                        "n.maturityLevel IS NOT NULL",
                        "n.score IS NOT NULL",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:ASSESSES]->() }",
                        "EXISTS { (n)-[:IDENTIFIES_GAP]->(:Gap) }"
                ),
                false,
                true,
                true,
                true
        ),
        RISK(
                "Risk",
                List.of(
                        "n.riskId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.probability IS NOT NULL AND trim(n.probability) <> ''",
                        "n.impact IS NOT NULL AND trim(n.impact) <> ''",
                        "n.mitigation IS NOT NULL AND trim(n.mitigation) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:THREATENS_FEATURE]->(:Feature) }",
                        "EXISTS { (n)-[:THREATENS_STORY]->(:UserStory) }"
                ),
                false,
                true,
                true,
                true
        ),
        EPIC(
                "Epic",
                List.of(
                        "n.epicId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_FEATURE]->() }",
                        "EXISTS { (:RequirementPortfolio)-[:HAS_EPIC]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        REQUIREMENT_PORTFOLIO(
                "RequirementPortfolio",
                List.of(
                        "n.portfolioId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_EPIC]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        FINDING(
                "Finding",
                List.of(
                        "n.findingId IS NOT NULL",
                        "n.summary IS NOT NULL AND trim(n.summary) <> ''",
                        "n.externalWorkflowState IS NOT NULL AND trim(n.externalWorkflowState) <> ''",
                        "n.externalPriority IS NOT NULL AND trim(n.externalPriority) <> ''",
                        "n.externalOwner IS NOT NULL AND trim(n.externalOwner) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:AFFECTS_SCREEN]->() } OR EXISTS { (:ExternalArtifact)-[:REPRESENTS_FINDING]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        BUG(
                "Bug",
                List.of(
                        "n.bugId IS NOT NULL",
                        "n.summary IS NOT NULL AND trim(n.summary) <> ''",
                        "n.severity IS NOT NULL AND trim(n.severity) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:AFFECTS_SCREEN]->() }"
                ),
                true,
                true,
                true,
                true
        ),
        TASK(
                "Task",
                List.of(
                        "n.taskId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.taskType IS NOT NULL AND trim(n.taskType) <> ''",
                        "n.status IS NOT NULL",
                        "n.priority IS NOT NULL AND trim(n.priority) <> ''"
                ),
                List.of(
                        "EXISTS { (:UserStory)-[:HAS_TASK]->(n) }",
                        "EXISTS { (n)-[:IMPLEMENTS]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        MILESTONE(
                "Milestone",
                List.of(
                        "n.milestoneId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.milestoneType IS NOT NULL",
                        "n.startDate IS NOT NULL",
                        "n.endDate IS NOT NULL",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:ProjectInstance)-[:HAS_MILESTONE]->(n) }",
                        "EXISTS { (n)-[:HAS_TASK]->(:Task) }"
                ),
                false,
                true,
                true,
                true
        ),
        BUSINESS_CAPABILITY(
                "BusinessCapability",
                List.of(
                        "n.capabilityId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:REALIZED_BY_PROCESS]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        BUSINESS_PROCESS(
                "BusinessProcess",
                List.of(
                        "n.processId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.diagramFormat IS NOT NULL AND trim(n.diagramFormat) <> ''",
                        "n.diagramPath IS NOT NULL AND trim(n.diagramPath) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_FLOW_NODE]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        PROCESS_ACTIVITY(
                "ProcessActivity",
                List.of(
                        "n.activityId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.activityType IS NOT NULL AND trim(n.activityType) <> ''",
                        "n.actionType IS NOT NULL AND trim(n.actionType) <> ''",
                        "n.taskNature IS NOT NULL AND trim(n.taskNature) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:BusinessProcess)-[:HAS_FLOW_NODE]->(n) } OR EXISTS { (n)-[:EXPANDS_TO]->(:BusinessProcess) } OR EXISTS { (n)-[:CALLS_PROCESS]->(:BusinessProcess) }",
                        "EXISTS { (n)-[:FLOWS_TO]->(:ProcessActivity) } OR EXISTS { (:ProcessActivity)-[:FLOWS_TO]->(n) } OR EXISTS { (:ProcessGateway)-[:FLOWS_TO]->(n) } OR EXISTS { (:ProcessEvent)-[:FLOWS_TO]->(n) } OR EXISTS { (n)-[:EXPANDS_TO]->(:BusinessProcess) } OR EXISTS { (n)-[:CALLS_PROCESS]->(:BusinessProcess) }"
                ),
                false,
                true,
                true,
                true
        ),
        PROCESS_GATEWAY(
                "ProcessGateway",
                List.of(
                        "n.gatewayId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.gatewayType IS NOT NULL AND trim(n.gatewayType) <> ''",
                        "n.defaultFlowTarget IS NOT NULL AND trim(n.defaultFlowTarget) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:BusinessProcess)-[:HAS_FLOW_NODE]->(n) }",
                        "EXISTS { (n)-[:FLOWS_TO]->(:ProcessActivity) }"
                ),
                false,
                true,
                true,
                true
        ),
        PROCESS_EVENT(
                "ProcessEvent",
                List.of(
                        "n.eventId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.eventPosition IS NOT NULL AND trim(n.eventPosition) <> ''",
                        "n.eventTrigger IS NOT NULL AND trim(n.eventTrigger) <> ''",
                        "n.isInterrupting IS NOT NULL",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:BusinessProcess)-[:HAS_FLOW_NODE]->(n) } OR EXISTS { (n)-[:ATTACHED_TO]->(:ProcessActivity) }",
                        "EXISTS { (n)-[:FLOWS_TO]->(:ProcessActivity) }"
                ),
                false,
                true,
                true,
                true
        ),
        ORGANIZATION(
                "Organization",
                List.of(
                        "n.orgId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.organizationType IS NOT NULL AND trim(n.organizationType) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:OWNS]->(:Application) }"
                ),
                false,
                true,
                true,
                true
        ),
        APPLICATION(
                "Application",
                List.of(
                        "n.applicationId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.applicationType IS NOT NULL AND trim(n.applicationType) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_COMPONENT]->() }",
                        "EXISTS { (n)-[:GOVERNED_BY_CONVENTION]->() } OR EXISTS { (n)-[:GOVERNED_BY_POLICY]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        APPLICATION_COMPONENT(
                "ApplicationComponent",
                List.of(
                        "n.componentId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.componentType IS NOT NULL AND trim(n.componentType) <> ''",
                        "n.frameworkFamily IS NOT NULL AND trim(n.frameworkFamily) <> ''",
                        "n.modulePath IS NOT NULL AND trim(n.modulePath) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Application)-[:HAS_COMPONENT]->(n) }",
                        "EXISTS { (n)-[:HAS_CODE_ASSET]->() }",
                        "EXISTS { (n)-[:SUPPORTS_SCREEN]->() } OR EXISTS { (n)-[:EXPOSES]->() } OR EXISTS { (n)-[:OWNS_DATA_ENTITY]->() } OR EXISTS { (n)-[:ENFORCES_RULE]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        BUSINESS_ROLE(
                "BusinessRole",
                List.of(
                        "n.roleKey IS NOT NULL",
                        "n.displayName IS NOT NULL AND trim(n.displayName) <> ''",
                        "n.roleGroup IS NOT NULL AND trim(n.roleGroup) <> ''",
                        "n.sortOrder IS NOT NULL",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Screen)-[:ACCESSIBLE_BY_ROLE]->(n) } OR EXISTS { (:Interaction)-[:ACCESSIBLE_BY_ROLE]->(n) } OR EXISTS { (:Touchpoint)-[:ACCESSIBLE_BY_ROLE]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        VALIDATION_ROLE(
                "ValidationRole",
                List.of(
                        "n.validationRoleKey IS NOT NULL",
                        "n.displayName IS NOT NULL AND trim(n.displayName) <> ''",
                        "n.scope IS NOT NULL AND trim(n.scope) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(),
                false,
                true,
                true,
                true
        ),
        PERMISSION(
                "Permission",
                List.of(
                        "n.permissionKey IS NOT NULL",
                        "n.displayName IS NOT NULL AND trim(n.displayName) <> ''",
                        "n.sortOrder IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Interaction)-[:REQUIRES_PERMISSION]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        CONFIRMATION_DIALOG(
                "ConfirmationDialog",
                List.of(
                        "n.dialogId IS NOT NULL",
                        "n.triggerAction IS NOT NULL AND trim(n.triggerAction) <> ''",
                        "n.confirmLabel IS NOT NULL AND trim(n.confirmLabel) <> ''",
                        "n.cancelLabel IS NOT NULL AND trim(n.cancelLabel) <> ''",
                        "n.consequenceText IS NOT NULL AND trim(n.consequenceText) <> ''"
                ),
                List.of(
                        "EXISTS { (:Interaction)-[:TRIGGERS_CONFIRMATION]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        ERROR_CODE(
                "ErrorCode",
                List.of(
                        "n.code IS NOT NULL",
                        "n.severity IS NOT NULL AND trim(n.severity) <> ''",
                        "n.messageText IS NOT NULL AND trim(n.messageText) <> ''",
                        "n.triggerCondition IS NOT NULL AND trim(n.triggerCondition) <> ''",
                        "n.resolutionHint IS NOT NULL AND trim(n.resolutionHint) <> ''"
                ),
                List.of(
                        "EXISTS { (:Interaction)-[:ON_ERROR_SHOWS]->(n) } OR EXISTS { (:Screen)-[:CAN_PRODUCE_ERROR]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        BUSINESS_OBJECT(
                "BusinessObject",
                List.of(
                        "n.objectId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.domain IS NOT NULL AND trim(n.domain) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:MAPPED_TO]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        INFORMATION_FLOW(
                "InformationFlow",
                List.of(
                        "n.flowId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.direction IS NOT NULL AND trim(n.direction) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:CARRIES]->(:BusinessObject) }",
                        "EXISTS { (n)-[:EXPOSED_VIA]->(:ApiContract) }",
                        "EXISTS { (n)-[:SOURCE_APPLICATION]->(:Application) }",
                        "EXISTS { (n)-[:TARGET_APPLICATION]->(:Application) }"
                ),
                false,
                true,
                true,
                true
        ),
        DEPLOYMENT(
                "Deployment",
                List.of(
                        "n.deploymentId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.environment IS NOT NULL AND trim(n.environment) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:HOSTS]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        INFRASTRUCTURE_NODE(
                "InfrastructureNode",
                List.of(
                        "n.nodeId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.nodeType IS NOT NULL AND trim(n.nodeType) <> ''",
                        "n.location IS NOT NULL AND trim(n.location) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Deployment)-[:DEPLOYED_ON]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        VALIDATION_RULE(
                "ValidationRule",
                List.of(
                        "n.validationRuleId IS NOT NULL",
                        "n.fieldPath IS NOT NULL AND trim(n.fieldPath) <> ''",
                        "n.validationType IS NOT NULL AND trim(n.validationType) <> ''",
                        "n.expression IS NOT NULL AND trim(n.expression) <> ''",
                        "n.errorMessage IS NOT NULL AND trim(n.errorMessage) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Screen)-[:ENFORCES_VALIDATION]->(n) } OR EXISTS { (:Rule)-[:HAS_VALIDATION_RULE]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        REQUEST_SCHEMA(
                "RequestSchema",
                List.of(
                        "n.schemaId IS NOT NULL",
                        "n.contentType IS NOT NULL AND trim(n.contentType) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:ApiContract)-[:HAS_REQUEST]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        RESPONSE_SCHEMA(
                "ResponseSchema",
                List.of(
                        "n.schemaId IS NOT NULL",
                        "n.contentType IS NOT NULL AND trim(n.contentType) <> ''",
                        "n.statusCode > 0",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:ApiContract)-[:HAS_RESPONSE]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        ERROR_CONTRACT(
                "ErrorContract",
                List.of(
                        "n.errorContractId IS NOT NULL",
                        "n.httpStatus > 0",
                        "n.errorCode IS NOT NULL AND trim(n.errorCode) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:ApiContract)-[:HAS_ERROR]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        CODE_ASSET(
                "CodeAsset",
                List.of(
                        "n.codeAssetId IS NOT NULL",
                        "n.filePath IS NOT NULL AND trim(n.filePath) <> ''",
                        "n.assetType IS NOT NULL AND trim(n.assetType) <> ''",
                        "n.language IS NOT NULL AND trim(n.language) <> ''",
                        "n.layerType IS NOT NULL AND trim(n.layerType) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:ApplicationComponent)-[:HAS_CODE_ASSET]->(n) } OR EXISTS { (:TestCase)-[:LOCATED_IN]->(n) }",
                        "EXISTS { (n)-[:ASSET_FOR_SCREEN]->() } OR EXISTS { (n)-[:ASSET_FOR_API]->() } OR EXISTS { (n)-[:ASSET_FOR_ENTITY]->() } OR EXISTS { (n)-[:ASSET_FOR_RULE]->() } OR EXISTS { (n)-[:GOVERNED_BY_CONVENTION]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        TEST_CASE(
                "TestCase",
                List.of(
                        "n.testCaseId IS NOT NULL",
                        "n.title IS NOT NULL AND trim(n.title) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.testType IS NOT NULL AND trim(n.testType) <> ''",
                        "n.expectedResult IS NOT NULL AND trim(n.expectedResult) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:UserStory)-[:VERIFIED_BY]->(n) }",
                        "EXISTS { (n)-[:VERIFIES]->() } OR EXISTS { (n)-[:LOCATED_IN]->() }"
                ),
                false,
                true,
                true,
                true
        ),
        RULE(
                "Rule",
                List.of(
                        "n.ruleId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.ruleType IS NOT NULL AND trim(n.ruleType) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:UserStory)-[:GOVERNED_BY_RULE]->(n) }",
                        "EXISTS { (n)-[:HAS_VALIDATION_RULE]->() } OR EXISTS { (:ApplicationComponent)-[:ENFORCES_RULE]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        MESSAGE(
                "Message",
                List.of(
                        "n.messageId IS NOT NULL",
                        "n.messageText IS NOT NULL AND trim(n.messageText) <> ''",
                        "n.messageType IS NOT NULL AND trim(n.messageType) <> ''",
                        "n.severity IS NOT NULL AND trim(n.severity) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Screen)-[:HAS_MESSAGE]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        GAP(
                "Gap",
                List.of(
                        "n.gapId IS NOT NULL",
                        "n.gapType IS NOT NULL AND trim(n.gapType) <> ''",
                        "n.severity IS NOT NULL AND trim(n.severity) <> ''",
                        "n.description IS NOT NULL AND trim(n.description) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Screen)-[:HAS_GAP]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        OPEN_QUESTION(
                "OpenQuestion",
                List.of(
                        "n.questionId IS NOT NULL",
                        "n.question IS NOT NULL AND trim(n.question) <> ''",
                        "n.context IS NOT NULL AND trim(n.context) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (n)-[:BLOCKS_ARTIFACT]->(:Feature) } OR EXISTS { (n)-[:BLOCKS_ARTIFACT]->(:Screen) } OR EXISTS { (n)-[:BLOCKS_ARTIFACT]->(:UserStory) }"
                ),
                false,
                true,
                true,
                true
        ),
        IMPORT_SNAPSHOT(
                "ImportSnapshot",
                List.of(
                        "n.snapshotId IS NOT NULL",
                        "n.sourceType IS NOT NULL AND trim(n.sourceType) <> ''",
                        "n.importedAt IS NOT NULL",
                        "n.importedBy IS NOT NULL AND trim(n.importedBy) <> ''",
                        "n.result IS NOT NULL AND trim(n.result) <> ''",
                        "n.contentHash IS NOT NULL AND trim(n.contentHash) <> ''"
                ),
                List.of(
                        "EXISTS { (:SourceReference)-[:IMPORTED_BY]->(n) } OR EXISTS { (:ExternalArtifact)-[:IMPORTED_BY]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        EVIDENCE_RECORD(
                "EvidenceRecord",
                List.of(
                        "n.evidenceId IS NOT NULL",
                        "n.evidenceType IS NOT NULL AND trim(n.evidenceType) <> ''",
                        "n.producedAt IS NOT NULL",
                        "n.result IS NOT NULL AND trim(n.result) <> ''",
                        "n.artifactPath IS NOT NULL AND trim(n.artifactPath) <> ''"
                ),
                List.of(
                        "EXISTS { (:Screen)-[:BASELINED_BY]->(n) } OR EXISTS { (:ApiContract)-[:BASELINED_BY]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        ENUM_DEFINITION(
                "Enum",
                List.of(
                        "n.enumId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "size(coalesce(n.values, [])) > 0"
                ),
                List.of(
                        "EXISTS { (:DataField)-[:USED_BY_FIELD]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        DOMAIN_EVENT(
                "Event",
                List.of(
                        "n.eventCode IS NOT NULL",
                        "n.displayName IS NOT NULL AND trim(n.displayName) <> ''",
                        "n.payload IS NOT NULL AND trim(n.payload) <> ''"
                ),
                List.of(
                        "EXISTS { (:Integration)-[:FIRED_BY_INTEGRATION]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        LOCALE_REGISTRY(
                "Locale",
                List.of(
                        "n.localeCode IS NOT NULL",
                        "n.displayName IS NOT NULL AND trim(n.displayName) <> ''",
                        "n.direction IS NOT NULL AND trim(n.direction) <> ''"
                ),
                List.of(
                        "EXISTS { (n)-[:HAS_TRANSLATIONS]->(:TranslationKey) }"
                ),
                false,
                true,
                true,
                true
        ),
        TRANSLATION_KEY(
                "TranslationKey",
                List.of(
                        "n.key IS NOT NULL",
                        "n.defaultText IS NOT NULL AND trim(n.defaultText) <> ''",
                        "n.context IS NOT NULL AND trim(n.context) <> ''"
                ),
                List.of(
                        "EXISTS { (:Locale)-[:HAS_TRANSLATIONS]->(n) }",
                        "EXISTS { (:Message)-[:USED_BY_MESSAGE]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        CODING_CONVENTION(
                "CodingConvention",
                List.of(
                        "n.conventionCode IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.category IS NOT NULL AND trim(n.category) <> ''",
                        "n.enforcement IS NOT NULL AND trim(n.enforcement) <> ''",
                        "n.scope IS NOT NULL AND trim(n.scope) <> ''"
                ),
                List.of(
                        "EXISTS { (:Application)-[:GOVERNED_BY_CONVENTION]->(n) } OR EXISTS { (:ApplicationComponent)-[:GOVERNED_BY_CONVENTION]->(n) } OR EXISTS { (:CodeAsset)-[:GOVERNED_BY_CONVENTION]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        QUALITY_CONSTRAINT(
                "QualityConstraint",
                List.of(
                        "n.constraintId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "n.constraintType IS NOT NULL AND trim(n.constraintType) <> ''",
                        "n.threshold IS NOT NULL AND trim(n.threshold) <> ''",
                        "n.priority IS NOT NULL AND trim(n.priority) <> ''",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Screen)-[:HAS_QUALITY_CONSTRAINT]->(n) } OR EXISTS { (:ApiContract)-[:HAS_QUALITY_CONSTRAINT]->(n) } OR EXISTS { (:DataEntity)-[:HAS_QUALITY_CONSTRAINT]->(n) } OR EXISTS { (:ApplicationComponent)-[:HAS_QUALITY_CONSTRAINT]->(n) }",
                        "EXISTS { (n)-[:SATISFIED_BY]->(:TestCase) }"
                ),
                false,
                true,
                true,
                true
        ),
        AGENT_POLICY(
                "AgentPolicy",
                List.of(
                        "n.policyId IS NOT NULL",
                        "n.name IS NOT NULL AND trim(n.name) <> ''",
                        "size(coalesce(n.allowedCommands, [])) > 0",
                        "n.maxFilesTouched IS NOT NULL",
                        "n.requiresHumanApproval IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Application)-[:GOVERNED_BY_POLICY]->(n) } OR EXISTS { (:ApplicationComponent)-[:GOVERNED_BY_POLICY]->(n) }"
                ),
                false,
                true,
                true,
                true
        ),
        SOURCE_REFERENCE(
                "SourceReference",
                List.of(
                        "n.sourceId IS NOT NULL",
                        "coalesce(n.artifactPath, n.url) IS NOT NULL",
                        "n.status IS NOT NULL"
                ),
                List.of(
                        "EXISTS { (:Screen)-[:HAS_SOURCE]->(n) } OR EXISTS { (:UserStory)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Journey)-[:HAS_SOURCE]->(n) } OR EXISTS { (:JourneyStep)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Touchpoint)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Interaction)-[:HAS_SOURCE]->(n) } OR EXISTS { (:ApiContract)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Topic)-[:HAS_SOURCE]->(n) } OR EXISTS { (:EdgeCase)-[:HAS_SOURCE]->(n) } OR EXISTS { (:ExceptionCase)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Integration)-[:HAS_SOURCE]->(n) } OR EXISTS { (:OpenQuestion)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Decision)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Assumption)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Constraint)-[:HAS_SOURCE]->(n) } OR EXISTS { (:Risk)-[:HAS_SOURCE]->(n) }"
                ),
                false,
                true,
                true,
                true
        );

        private final String label;
        private final List<String> attributeChecks;
        private final List<String> relationshipChecks;
        private final boolean sourceTraceabilityApplicable;
        private final boolean objectQuerySupported;
        private final boolean relationExpansionSupported;
        private final boolean specializedQuerySupported;

        BenchmarkNodeType(
                String label,
                List<String> attributeChecks,
                List<String> relationshipChecks,
                boolean sourceTraceabilityApplicable,
                boolean objectQuerySupported,
                boolean relationExpansionSupported,
                boolean specializedQuerySupported
        ) {
            this.label = label;
            this.attributeChecks = attributeChecks;
            this.relationshipChecks = relationshipChecks;
            this.sourceTraceabilityApplicable = sourceTraceabilityApplicable;
            this.objectQuerySupported = objectQuerySupported;
            this.relationExpansionSupported = relationExpansionSupported;
            this.specializedQuerySupported = specializedQuerySupported;
        }

        private double queryabilityScore() {
            double score = 0.0;
            if (objectQuerySupported) {
                score += 1.0;
            }
            if (relationExpansionSupported) {
                score += 1.0;
            }
            if (specializedQuerySupported) {
                score += 1.0;
            }
            return Math.round((score / 3.0) * 1000.0) / 10.0;
        }
    }
}
