package com.emsist.designhub.service;

import com.emsist.designhub.dto.ReadinessDiagnosticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReadinessDiagnosticsService {

    private final Neo4jClient neo4jClient;
    private final AgentReadinessService agentReadinessService;

    public Optional<ReadinessDiagnosticsResponse> assessScreen(String surfaceId) {
        return fetchFirst("""
                MATCH (scr:Screen {surfaceId: $artifactId})
                OPTIONAL MATCH (us:UserStory)-[:DELIVERS]->(scr)
                OPTIONAL MATCH (scr)-[:HAS_INTERACTION]->(i:Interaction)
                OPTIONAL MATCH (scr)-[:ACCESSIBLE_BY_ROLE]->(r:BusinessRole)
                OPTIONAL MATCH (scr)-[:HAS_MESSAGE]->(m:Message)
                OPTIONAL MATCH (scr)-[:TRANSITIONS_TO]->(t:Screen)
                OPTIONAL MATCH (ss:ScreenState)-[:BELONGS_TO_SCREEN]->(scr)
                OPTIONAL MATCH (scr)-[:HAS_GAP]->(g:Gap)
                OPTIONAL MATCH (scr)-[:HAS_INTERACTION]->(:Interaction)-[:CALLS_API]->(api:ApiContract)
                OPTIONAL MATCH (qaStory:UserStory)-[:DELIVERS]->(scr)
                OPTIONAL MATCH (qaStory)-[:VERIFIED_BY]->(tc:TestCase)
                RETURN scr.surfaceId AS artifactId,
                       scr.status AS status,
                       scr.surfaceId IS NOT NULL AS hasSurfaceId,
                       scr.label IS NOT NULL AND trim(scr.label) <> '' AS hasLabel,
                       scr.routePath IS NOT NULL AND trim(scr.routePath) <> '' AS hasRoutePath,
                       scr.status IS NOT NULL AS hasStatus,
                       count(DISTINCT us) > 0 AS hasStory,
                       count(DISTINCT i) > 0 AS hasInteraction,
                       count(DISTINCT r) > 0 AS hasRole,
                       count(DISTINCT m) > 0 AS hasMessage,
                       count(DISTINCT ss) > 0 AS hasState,
                       count(DISTINCT t) > 0 AS hasTransition,
                       count(DISTINCT g) > 0 AS hasGap,
                       count(DISTINCT api) > 0 AS hasApi,
                       count(DISTINCT tc) > 0 AS hasVerifiedTests
                """, surfaceId).map(this::toScreenDiagnostics);
    }

    public Optional<ReadinessDiagnosticsResponse> assessStory(String storyId) {
        return fetchFirst("""
                MATCH (us:UserStory {storyId: $artifactId})
                OPTIONAL MATCH (us)-[:DELIVERS]->(scr:Screen)
                OPTIONAL MATCH (us)-[:HAS_CRITERION]->(ac:AcceptanceCriterion)
                OPTIONAL MATCH (feature:Feature)-[:HAS_STORY]->(us)
                OPTIONAL MATCH (us)-[:GOVERNED_BY_RULE]->(rule:Rule)
                OPTIONAL MATCH (us)-[:DELIVERS]->(directApi:ApiContract)
                OPTIONAL MATCH (us)-[:DELIVERS]->(de:DataEntity)
                OPTIONAL MATCH (us)-[:DELIVERS]->(msg:Message)
                OPTIONAL MATCH (scr)-[:HAS_INTERACTION]->(:Interaction)-[:CALLS_API]->(screenApi:ApiContract)
                OPTIONAL MATCH (scr)-[:ACCESSIBLE_BY_ROLE]->(role:BusinessRole)
                OPTIONAL MATCH (us)-[:HAS_TASK]->(task:Task)
                OPTIONAL MATCH (us)-[:VERIFIED_BY]->(tc:TestCase)
                OPTIONAL MATCH (us)-[:REALIZES]->(trace)
                RETURN us.storyId AS artifactId,
                       us.status AS status,
                       us.label IS NOT NULL AND trim(us.label) <> '' AS hasLabel,
                       us.status IS NOT NULL AS hasStatus,
                       count(DISTINCT scr) > 0 AS hasScreen,
                       count(DISTINCT ac) > 0 AS hasCriterion,
                       count(DISTINCT feature) > 0 AS hasFeature,
                       count(DISTINCT rule) > 0 AS hasRule,
                       (count(DISTINCT directApi) + count(DISTINCT screenApi)) > 0 AS hasApiContract,
                       count(DISTINCT de) > 0 AS hasDataEntity,
                       count(DISTINCT msg) > 0 AS hasMessage,
                       count(DISTINCT role) > 0 AS hasRoleContext,
                       count(DISTINCT task) > 0 AS hasTask,
                       count(DISTINCT tc) > 0 AS hasVerifiedBy,
                       count(DISTINCT trace) > 0 AS hasTraceability
                """, storyId).map(this::toStoryDiagnostics);
    }

    private Optional<Map<String, Object>> fetchFirst(String cypher, String artifactId) {
        return neo4jClient.query(cypher)
                .bind(artifactId).to("artifactId")
                .fetch()
                .first();
    }

    private ReadinessDiagnosticsResponse toScreenDiagnostics(Map<String, Object> record) {
        boolean hasStory = bool(record, "hasStory");
        boolean hasInteraction = bool(record, "hasInteraction");
        boolean hasRole = bool(record, "hasRole");
        boolean hasMessage = bool(record, "hasMessage");
        boolean hasState = bool(record, "hasState");
        boolean hasTransition = bool(record, "hasTransition");
        boolean hasGap = bool(record, "hasGap");
        boolean hasApi = bool(record, "hasApi");
        boolean hasVerifiedTests = bool(record, "hasVerifiedTests");

        List<RuleCheck> rules = List.of(
                blockingEdge("MCR-SCR-001", "Missing DELIVERS edge from a UserStory to this Screen", hasStory),
                blockingEdge("MCR-SCR-002", "No Interaction linked via HAS_INTERACTION", hasInteraction),
                blockingAttr("MCR-SCR-003", "surfaceId is missing", bool(record, "hasSurfaceId")),
                blockingAttr("MCR-SCR-004", "label is missing", bool(record, "hasLabel")),
                blockingAttr("MCR-SCR-005", "status is missing", bool(record, "hasStatus")),
                blockingAttr("MCR-SCR-006", "routePath or invocation context is missing", bool(record, "hasRoutePath")),
                optionalEdge("MCR-SCR-007", "No BusinessRole linked via ACCESSIBLE_BY_ROLE", hasRole),
                optionalEdge("MCR-SCR-008", "No Message linked via HAS_MESSAGE", hasMessage),
                optionalEdge("MCR-SCR-009", "No ScreenState linked via BELONGS_TO_SCREEN", hasState),
                optionalEdge("MCR-SCR-010", "No transition linked via TRANSITIONS_TO", hasTransition),
                optionalEdge("MCR-SCR-011", "No gap or finding linked to the Screen", hasGap)
        );

        Map<String, Boolean> readiness = new LinkedHashMap<>();
        boolean requirementsReady = hasStory && bool(record, "hasLabel") && bool(record, "hasStatus");
        boolean designReady = requirementsReady && hasInteraction;
        boolean frontendReady = designReady && bool(record, "hasRoutePath") && hasRole;
        boolean integrationReady = designReady && hasApi;
        boolean qaReady = frontendReady && integrationReady && hasVerifiedTests;
        readiness.put("requirementsReady", requirementsReady);
        readiness.put("designReady", designReady);
        readiness.put("frontendReady", frontendReady);
        readiness.put("integrationReady", integrationReady);
        readiness.put("qaReady", qaReady);

        return buildResponse("Screen", string(record, "artifactId"), string(record, "status"),
                readiness, rules, List.of());
    }

    private ReadinessDiagnosticsResponse toStoryDiagnostics(Map<String, Object> record) {
        boolean hasScreen = bool(record, "hasScreen");
        boolean hasCriterion = bool(record, "hasCriterion");
        boolean hasFeature = bool(record, "hasFeature");
        boolean hasRoleContext = bool(record, "hasRoleContext");
        boolean hasRule = bool(record, "hasRule");
        boolean hasApiContract = bool(record, "hasApiContract");
        boolean hasDataEntity = bool(record, "hasDataEntity");
        boolean hasMessage = bool(record, "hasMessage");
        boolean hasTask = bool(record, "hasTask");
        boolean hasVerifiedBy = bool(record, "hasVerifiedBy");

        List<RuleCheck> rules = List.of(
                blockingAttr("MCR-UST-001", "label is missing", bool(record, "hasLabel")),
                blockingAttr("MCR-UST-002", "status is missing", bool(record, "hasStatus")),
                blockingEdge("MCR-UST-003", "No Screen linked via DELIVERS", hasScreen),
                blockingEdge("MCR-UST-004", "No AcceptanceCriterion linked via HAS_CRITERION", hasCriterion),
                optionalEdge("MCR-UST-005", "No Feature linked via HAS_STORY", hasFeature),
                optionalAttr("MCR-UST-006", "No persona or role context is linked", hasRoleContext),
                optionalEdge("MCR-UST-007", "No linked rules or validations", hasRule),
                optionalEdge("MCR-UST-008", "No linked ApiContract is available", hasApiContract)
        );

        Map<String, Boolean> readiness = new LinkedHashMap<>();
        boolean requirementsReady = bool(record, "hasLabel") && bool(record, "hasStatus") && hasCriterion;
        boolean designReady = requirementsReady && hasScreen;
        boolean contractReady = designReady && (hasApiContract || hasRule || hasDataEntity || hasMessage);
        boolean frontendReady = designReady && hasScreen;
        boolean backendReady = contractReady && (hasTask || hasApiContract || hasDataEntity);
        boolean integrationReady = contractReady && (hasApiContract || hasDataEntity || hasMessage);
        boolean qaReady = frontendReady && backendReady && integrationReady && hasVerifiedBy;
        readiness.put("requirementsReady", requirementsReady);
        readiness.put("designReady", designReady);
        readiness.put("contractReady", contractReady);
        readiness.put("frontendReady", frontendReady);
        readiness.put("backendReady", backendReady);
        readiness.put("integrationReady", integrationReady);
        readiness.put("qaReady", qaReady);

        List<String> advisoryRules = new ArrayList<>();
        if (!agentReadinessService.isAgentReady(string(record, "artifactId"))) {
            advisoryRules.add("MCR-STORY-AGENT-READY-001");
        }

        return buildResponse("UserStory", string(record, "artifactId"), string(record, "status"),
                readiness, rules, advisoryRules);
    }

    private ReadinessDiagnosticsResponse buildResponse(
            String artifactType,
            String artifactId,
            String status,
            Map<String, Boolean> readiness,
            List<RuleCheck> rules,
            List<String> advisoryRules
    ) {
        double completenessScore = computeCompletenessScore(rules);
        return ReadinessDiagnosticsResponse.builder()
                .artifactType(artifactType)
                .artifactId(artifactId)
                .status(status)
                .readiness(readiness)
                .completenessScore(completenessScore)
                .completenessLevel(completenessLevel(completenessScore))
                .missingBlockingRules(rules.stream()
                        .filter(rule -> rule.blocking && !rule.satisfied)
                        .map(rule -> rule.ruleId)
                        .toList())
                .missingOptionalRules(rules.stream()
                        .filter(rule -> !rule.blocking && !rule.satisfied)
                        .map(rule -> rule.ruleId)
                        .toList())
                .missingArtifacts(rules.stream()
                        .filter(rule -> rule.blocking && !rule.satisfied)
                        .map(rule -> rule.description)
                        .toList())
                .advisoryRulesViolated(advisoryRules)
                .build();
    }

    private double computeCompletenessScore(List<RuleCheck> rules) {
        int numerator = 0;
        int denominator = 0;
        for (RuleCheck rule : rules) {
            denominator += rule.weight;
            if (rule.satisfied) {
                numerator += rule.weight;
            }
        }
        if (denominator == 0) {
            return 0.0;
        }
        return Math.round((numerator * 1000.0 / denominator)) / 10.0;
    }

    private String completenessLevel(double score) {
        if (score < 40.0) {
            return "RED";
        }
        if (score < 80.0) {
            return "AMBER";
        }
        return "GREEN";
    }

    private boolean bool(Map<String, Object> record, String key) {
        return Boolean.TRUE.equals(record.get(key));
    }

    private String string(Map<String, Object> record, String key) {
        Object value = record.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private RuleCheck blockingEdge(String ruleId, String description, boolean satisfied) {
        return new RuleCheck(ruleId, description, true, satisfied, 3);
    }

    private RuleCheck blockingAttr(String ruleId, String description, boolean satisfied) {
        return new RuleCheck(ruleId, description, true, satisfied, 2);
    }

    private RuleCheck optionalEdge(String ruleId, String description, boolean satisfied) {
        return new RuleCheck(ruleId, description, false, satisfied, 1);
    }

    private RuleCheck optionalAttr(String ruleId, String description, boolean satisfied) {
        return new RuleCheck(ruleId, description, false, satisfied, 1);
    }

    private record RuleCheck(String ruleId, String description, boolean blocking, boolean satisfied, int weight) {
    }
}
