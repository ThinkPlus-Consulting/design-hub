package com.emsist.designhub.service;

import com.emsist.designhub.dto.*;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.*;

@Service
public class AgentPackService {

    private static final int PACK_VERSION = 2;
    private static final Comparator<String> NULL_SAFE_STRING = Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER);
    private static final Comparator<GraphNodeReference> NODE_REFERENCE_ORDER = Comparator
            .comparing(GraphNodeReference::displayName, NULL_SAFE_STRING)
            .thenComparing(GraphNodeReference::nodeType, NULL_SAFE_STRING)
            .thenComparing(GraphNodeReference::id, NULL_SAFE_STRING);
    private static final Comparator<AgentPackExportResponse.ApplicationTargetSummary> APPLICATION_TARGET_ORDER = Comparator
            .comparing(AgentPackExportResponse.ApplicationTargetSummary::name, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.ApplicationTargetSummary::id, NULL_SAFE_STRING);
    private static final Comparator<AgentPackExportResponse.ComponentTargetSummary> COMPONENT_TARGET_ORDER = Comparator
            .comparing(AgentPackExportResponse.ComponentTargetSummary::displayName, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.ComponentTargetSummary::applicationName, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.ComponentTargetSummary::id, NULL_SAFE_STRING);
    private static final Comparator<AgentPackExportResponse.CodeTargetSummary> CODE_TARGET_ORDER = Comparator
            .comparing(AgentPackExportResponse.CodeTargetSummary::displayName, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.CodeTargetSummary::filePath, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.CodeTargetSummary::id, NULL_SAFE_STRING);
    private static final Comparator<AgentPackExportResponse.TestCaseSummary> TEST_CASE_ORDER = Comparator
            .comparing(AgentPackExportResponse.TestCaseSummary::displayName, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.TestCaseSummary::testFilePath, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.TestCaseSummary::id, NULL_SAFE_STRING);
    private static final Comparator<AgentPackExportResponse.PolicySummary> POLICY_ORDER = Comparator
            .comparing(AgentPackExportResponse.PolicySummary::name, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.PolicySummary::id, NULL_SAFE_STRING);
    private static final Comparator<AgentPackExportResponse.ConventionSummary> CONVENTION_ORDER = Comparator
            .comparing(AgentPackExportResponse.ConventionSummary::name, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.ConventionSummary::id, NULL_SAFE_STRING);
    private static final Comparator<AgentPackExportResponse.QualityConstraintSummary> QUALITY_CONSTRAINT_ORDER = Comparator
            .comparing(AgentPackExportResponse.QualityConstraintSummary::name, NULL_SAFE_STRING)
            .thenComparing(AgentPackExportResponse.QualityConstraintSummary::id, NULL_SAFE_STRING);

    private static final List<String> BLOCKING_CHECKS = List.of(
            "repoPath", "effectiveBuildCommand", "manifestPath",
            "codeAssetPresence", "testFileResolution"
    );

    private static final String STORY_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            RETURN us.storyId AS id,
                   'UserStory' AS nodeType,
                   coalesce(us.label, us.storyId) AS displayName,
                   toString(us.status) AS status
            """;

    private static final String TASK_QUERY = """
            MATCH (:UserStory {storyId: $storyId})-[:HAS_TASK]->(task:Task)
            RETURN DISTINCT task.taskId AS id,
                   'Task' AS nodeType,
                   coalesce(task.title, task.taskId) AS displayName,
                   toString(task.status) AS status
            ORDER BY coalesce(task.title, task.taskId)
            """;

    private static final String SCREEN_QUERY = """
            MATCH (:UserStory {storyId: $storyId})-[:DELIVERS]->(screen:Screen)
            RETURN DISTINCT screen.surfaceId AS id,
                   'Screen' AS nodeType,
                   coalesce(screen.label, screen.surfaceId) AS displayName,
                   toString(screen.status) AS status
            ORDER BY coalesce(screen.label, screen.surfaceId)
            """;

    private static final String API_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            CALL (us) {
                MATCH (us)-[:DELIVERS]->(api:ApiContract)
                RETURN api
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_INTERACTION]->(:Interaction)-[:CALLS_API]->(api:ApiContract)
                RETURN api
            }
            RETURN DISTINCT api.contractId AS id,
                   'ApiContract' AS nodeType,
                   coalesce(api.path, api.contractId) AS displayName,
                   toString(api.status) AS status
            ORDER BY coalesce(api.path, api.contractId)
            """;

    private static final String DATA_ENTITY_QUERY = """
            MATCH (:UserStory {storyId: $storyId})-[:DELIVERS]->(entity:DataEntity)
            RETURN DISTINCT entity.entityId AS id,
                   'DataEntity' AS nodeType,
                   coalesce(entity.name, entity.entityId) AS displayName,
                   toString(entity.status) AS status
            ORDER BY coalesce(entity.name, entity.entityId)
            """;

    private static final String COMPONENT_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            CALL (us) {
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-(component:ApplicationComponent)
                OPTIONAL MATCH (component)<-[:HAS_COMPONENT]-(app:Application)
                RETURN component, app
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-(component:ApplicationComponent)
                OPTIONAL MATCH (component)<-[:HAS_COMPONENT]-(app:Application)
                RETURN component, app
            }
            RETURN DISTINCT component.componentId AS id,
                   'ApplicationComponent' AS nodeType,
                   coalesce(component.name, component.componentId) AS displayName,
                   toString(component.status) AS status,
                   app.applicationId AS applicationId,
                   app.name AS applicationName,
                   component.frameworkFamily AS frameworkFamily,
                   component.frameworkName AS frameworkName,
                   component.frameworkVersion AS frameworkVersion,
                   component.runtime AS runtime,
                   component.language AS language,
                   component.languageVersion AS languageVersion,
                   component.modulePath AS modulePath,
                   component.manifestPath AS manifestPath,
                   coalesce(component.buildCommand, app.defaultBuildCommand) AS buildCommand,
                   coalesce(component.testCommand, app.defaultTestCommand) AS testCommand,
                   component.entrypointPath AS entrypointPath,
                   component.localRunCommand AS localRunCommand,
                   component.secretPrerequisites AS secretPrerequisites,
                   component.fixturePrerequisites AS fixturePrerequisites,
                   component.localRunPrerequisites AS localRunPrerequisites
            ORDER BY coalesce(component.name, component.componentId)
            """;

    private static final String APPLICATION_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            CALL (us) {
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (:ApplicationComponent)<-[:HAS_COMPONENT]-(app:Application)
                RETURN app
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (:ApplicationComponent)<-[:HAS_COMPONENT]-(app:Application)
                RETURN app
            }
            RETURN DISTINCT app.applicationId AS id,
                   app.name AS name,
                   app.applicationType AS applicationType,
                   app.workspaceType AS workspaceType,
                   app.repoPath AS repoPath,
                   app.repoUrl AS repoUrl,
                   app.defaultBuildCommand AS defaultBuildCommand,
                   app.defaultTestCommand AS defaultTestCommand,
                   app.bootstrapSteps AS bootstrapSteps
            ORDER BY coalesce(app.name, app.applicationId)
            """;

    private static final String CODE_TARGET_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            CALL (us) {
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (component:ApplicationComponent)-[:HAS_CODE_ASSET]->(asset:CodeAsset)
                OPTIONAL MATCH (component)<-[:HAS_COMPONENT]-(app:Application)
                RETURN asset, component, app
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (component:ApplicationComponent)-[:HAS_CODE_ASSET]->(asset:CodeAsset)
                OPTIONAL MATCH (component)<-[:HAS_COMPONENT]-(app:Application)
                RETURN asset, component, app
                UNION
                WITH us
                MATCH (us)-[:VERIFIED_BY]->(:TestCase)-[:LOCATED_IN]->(asset:CodeAsset)
                OPTIONAL MATCH (component:ApplicationComponent)-[:HAS_CODE_ASSET]->(asset)
                OPTIONAL MATCH (component)<-[:HAS_COMPONENT]-(app:Application)
                RETURN asset, component, app
            }
            RETURN DISTINCT asset.codeAssetId AS id,
                   'CodeAsset' AS nodeType,
                   coalesce(asset.filePath, asset.className, asset.codeAssetId) AS displayName,
                   toString(asset.status) AS status,
                   asset.assetType AS assetType,
                   asset.filePath AS filePath,
                   asset.language AS language,
                   asset.layerType AS layerType,
                   asset.changePolicy AS changePolicy,
                   component.componentId AS componentId,
                   component.name AS componentName,
                   app.applicationId AS applicationId,
                   app.name AS applicationName
            ORDER BY coalesce(asset.filePath, asset.className, asset.codeAssetId)
            """;

    private static final String TEST_CASE_QUERY = """
            MATCH (:UserStory {storyId: $storyId})-[:VERIFIED_BY]->(tc:TestCase)
            OPTIONAL MATCH (tc)-[:LOCATED_IN]->(asset:CodeAsset)
            RETURN DISTINCT tc.testCaseId AS id,
                   coalesce(tc.title, tc.testCaseId) AS displayName,
                   toString(tc.status) AS status,
                   tc.testType AS testType,
                   tc.testCommand AS testCommand,
                   tc.testFilePath AS testFilePath,
                   asset.codeAssetId AS locatedInId,
                   asset.filePath AS locatedInPath
            ORDER BY coalesce(tc.title, tc.testCaseId)
            """;

    private static final String POLICY_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            CALL (us) {
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (:ApplicationComponent)-[:GOVERNED_BY_POLICY]->(policy:AgentPolicy)
                RETURN policy
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (:ApplicationComponent)<-[:HAS_COMPONENT]-(app:Application)-[:GOVERNED_BY_POLICY]->(policy:AgentPolicy)
                RETURN policy
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (:ApplicationComponent)-[:GOVERNED_BY_POLICY]->(policy:AgentPolicy)
                RETURN policy
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (:ApplicationComponent)<-[:HAS_COMPONENT]-(app:Application)-[:GOVERNED_BY_POLICY]->(policy:AgentPolicy)
                RETURN policy
            }
            RETURN DISTINCT policy.policyId AS id,
                   policy.name AS name,
                   policy.allowedRepos AS allowedRepos,
                   policy.allowedCommands AS allowedCommands,
                   policy.forbiddenCommands AS forbiddenCommands,
                   policy.allowedEnvironments AS allowedEnvironments,
                   policy.secretScopes AS secretScopes,
                   policy.maxFilesTouched AS maxFilesTouched,
                   policy.requiresHumanApproval AS requiresHumanApproval,
                   policy.approvalThreshold AS approvalThreshold
            ORDER BY policy.policyId
            """;

    private static final String CONVENTION_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            CALL (us) {
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (:ApplicationComponent)-[:GOVERNED_BY_CONVENTION]->(conv:CodingConvention)
                RETURN conv
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (:ApplicationComponent)-[:GOVERNED_BY_CONVENTION]->(conv:CodingConvention)
                RETURN conv
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (:ApplicationComponent)<-[:HAS_COMPONENT]-(:Application)-[:GOVERNED_BY_CONVENTION]->(conv:CodingConvention)
                RETURN conv
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (:ApplicationComponent)<-[:HAS_COMPONENT]-(:Application)-[:GOVERNED_BY_CONVENTION]->(conv:CodingConvention)
                RETURN conv
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (:ApplicationComponent)-[:HAS_CODE_ASSET]->(:CodeAsset)-[:GOVERNED_BY_CONVENTION]->(conv:CodingConvention)
                RETURN conv
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (:ApplicationComponent)-[:HAS_CODE_ASSET]->(:CodeAsset)-[:GOVERNED_BY_CONVENTION]->(conv:CodingConvention)
                RETURN conv
                UNION
                WITH us
                MATCH (us)-[:VERIFIED_BY]->(:TestCase)-[:LOCATED_IN]->(:CodeAsset)-[:GOVERNED_BY_CONVENTION]->(conv:CodingConvention)
                RETURN conv
            }
            RETURN DISTINCT conv.conventionCode AS id,
                   conv.name AS name,
                   conv.category AS category,
                   conv.enforcement AS enforcement,
                   conv.scope AS scope,
                   conv.docRef AS docRef,
                   conv.activeStatus AS activeStatus
            ORDER BY conv.conventionCode
            """;

    private static final String QUALITY_CONSTRAINT_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            CALL (us) {
                MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_QUALITY_CONSTRAINT]->(qc:QualityConstraint)
                RETURN qc
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(:ApiContract)-[:HAS_QUALITY_CONSTRAINT]->(qc:QualityConstraint)
                RETURN qc
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(:DataEntity)-[:HAS_QUALITY_CONSTRAINT]->(qc:QualityConstraint)
                RETURN qc
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(deliverable)
                WHERE NOT deliverable:Message
                MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-
                      (:ApplicationComponent)-[:HAS_QUALITY_CONSTRAINT]->(qc:QualityConstraint)
                RETURN qc
                UNION
                WITH us
                MATCH (us)-[:DELIVERS]->(message:Message)<-[:HAS_MESSAGE]-(screen:Screen)<-[:SUPPORTS_SCREEN]-
                      (:ApplicationComponent)-[:HAS_QUALITY_CONSTRAINT]->(qc:QualityConstraint)
                RETURN qc
            }
            RETURN DISTINCT qc.constraintId AS id,
                   qc.name AS name,
                   qc.constraintType AS constraintType,
                   qc.priority AS priority,
                   qc.threshold AS threshold,
                   toString(qc.status) AS status
            ORDER BY qc.constraintId
            """;

    private final Neo4jClient neo4jClient;
    private final AgentReadinessService readinessService;

    public AgentPackService(Neo4jClient neo4jClient, AgentReadinessService readinessService) {
        this.neo4jClient = neo4jClient;
        this.readinessService = readinessService;
    }

    public Optional<AgentPackExportResponse> buildPack(String storyId) {
        Optional<Map<String, Object>> storyRecord = fetchFirst(STORY_QUERY, storyId);
        if (storyRecord.isEmpty()) {
            return Optional.empty();
        }

        Map<String, Boolean> readinessChecks = new LinkedHashMap<>(readinessService.assessAgentReadiness(storyId));
        PackCompleteness completeness = computeCompleteness(readinessChecks);

        return Optional.of(new AgentPackExportResponse(
                "PACK-" + storyId,
                PACK_VERSION,
                Instant.now(),
                toNodeReference(storyRecord.get()),
                completeness,
                Collections.unmodifiableMap(new LinkedHashMap<>(readinessChecks)),
                toNodeReferences(fetchAll(TASK_QUERY, storyId)),
                toNodeReferences(fetchAll(SCREEN_QUERY, storyId)),
                toNodeReferences(fetchAll(API_QUERY, storyId)),
                toNodeReferences(fetchAll(DATA_ENTITY_QUERY, storyId)),
                toApplicationTargets(fetchAll(APPLICATION_QUERY, storyId)),
                toComponentTargets(fetchAll(COMPONENT_QUERY, storyId)),
                toCodeTargets(fetchAll(CODE_TARGET_QUERY, storyId)),
                toTestCases(fetchAll(TEST_CASE_QUERY, storyId)),
                toPolicies(fetchAll(POLICY_QUERY, storyId)),
                toConventions(fetchAll(CONVENTION_QUERY, storyId)),
                toQualityConstraints(fetchAll(QUALITY_CONSTRAINT_QUERY, storyId))
        ));
    }

    public PackCompleteness computeCompleteness(String storyId) {
        return computeCompleteness(readinessService.assessAgentReadiness(storyId));
    }

    private PackCompleteness computeCompleteness(Map<String, Boolean> checks) {
        List<String> missing = new ArrayList<>();
        int passed = 0;

        for (String check : BLOCKING_CHECKS) {
            if (Boolean.TRUE.equals(checks.get(check))) {
                passed++;
            } else {
                missing.add(check);
            }
        }

        boolean complete = missing.isEmpty();
        int score = BLOCKING_CHECKS.isEmpty() ? 0 : (passed * 100) / BLOCKING_CHECKS.size();

        return PackCompleteness.builder()
                .complete(complete)
                .missingConcerns(missing)
                .missingFields(List.of())
                .readinessScore(score)
                .build();
    }

    private Optional<Map<String, Object>> fetchFirst(String query, String storyId) {
        return neo4jClient.query(query)
                .bind(storyId).to("storyId")
                .fetch()
                .first()
                .map(this::map);
    }

    private List<Map<String, Object>> fetchAll(String query, String storyId) {
        return neo4jClient.query(query)
                .bind(storyId).to("storyId")
                .fetch()
                .all()
                .stream()
                .map(this::map)
                .toList();
    }

    private GraphNodeReference toNodeReference(Map<String, Object> record) {
        String id = string(record, "id");
        if (id == null) {
            return null;
        }
        return new GraphNodeReference(
                id,
                string(record, "nodeType"),
                string(record, "displayName"),
                string(record, "status")
        );
    }

    private List<GraphNodeReference> toNodeReferences(List<Map<String, Object>> rows) {
        LinkedHashMap<String, GraphNodeReference> nodes = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            GraphNodeReference node = toNodeReference(row);
            if (node == null || node.id() == null || nodes.containsKey(node.id())) {
                continue;
            }
            nodes.put(node.id(), node);
        }
        return sortedCopy(nodes.values(), NODE_REFERENCE_ORDER);
    }

    private List<AgentPackExportResponse.ApplicationTargetSummary> toApplicationTargets(List<Map<String, Object>> rows) {
        LinkedHashMap<String, AgentPackExportResponse.ApplicationTargetSummary> applications = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = string(row, "id");
            if (id == null || applications.containsKey(id)) {
                continue;
            }
            applications.put(id, new AgentPackExportResponse.ApplicationTargetSummary(
                    id,
                    string(row, "name"),
                    string(row, "applicationType"),
                    string(row, "workspaceType"),
                    string(row, "repoPath"),
                    string(row, "repoUrl"),
                    string(row, "defaultBuildCommand"),
                    string(row, "defaultTestCommand"),
                    stringList(row, "bootstrapSteps")
            ));
        }
        return sortedCopy(applications.values(), APPLICATION_TARGET_ORDER);
    }

    private List<AgentPackExportResponse.ComponentTargetSummary> toComponentTargets(List<Map<String, Object>> rows) {
        LinkedHashMap<String, AgentPackExportResponse.ComponentTargetSummary> components = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = string(row, "id");
            if (id == null || components.containsKey(id)) {
                continue;
            }
            components.put(id, new AgentPackExportResponse.ComponentTargetSummary(
                    id,
                    string(row, "nodeType"),
                    string(row, "displayName"),
                    string(row, "status"),
                    string(row, "applicationId"),
                    string(row, "applicationName"),
                    string(row, "frameworkFamily"),
                    string(row, "frameworkName"),
                    string(row, "frameworkVersion"),
                    string(row, "runtime"),
                    string(row, "language"),
                    string(row, "languageVersion"),
                    string(row, "modulePath"),
                    string(row, "manifestPath"),
                    string(row, "buildCommand"),
                    string(row, "testCommand"),
                    string(row, "entrypointPath"),
                    string(row, "localRunCommand"),
                    sortedStrings(row, "secretPrerequisites"),
                    sortedStrings(row, "fixturePrerequisites"),
                    sortedStrings(row, "localRunPrerequisites")
            ));
        }
        return sortedCopy(components.values(), COMPONENT_TARGET_ORDER);
    }

    private List<AgentPackExportResponse.CodeTargetSummary> toCodeTargets(List<Map<String, Object>> rows) {
        LinkedHashMap<String, AgentPackExportResponse.CodeTargetSummary> codeTargets = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = string(row, "id");
            if (id == null || codeTargets.containsKey(id)) {
                continue;
            }
            codeTargets.put(id, new AgentPackExportResponse.CodeTargetSummary(
                    id,
                    string(row, "nodeType"),
                    string(row, "displayName"),
                    string(row, "status"),
                    string(row, "assetType"),
                    string(row, "filePath"),
                    string(row, "language"),
                    string(row, "layerType"),
                    string(row, "changePolicy"),
                    string(row, "componentId"),
                    string(row, "componentName"),
                    string(row, "applicationId"),
                    string(row, "applicationName")
            ));
        }
        return sortedCopy(codeTargets.values(), CODE_TARGET_ORDER);
    }

    private List<AgentPackExportResponse.TestCaseSummary> toTestCases(List<Map<String, Object>> rows) {
        LinkedHashMap<String, AgentPackExportResponse.TestCaseSummary> testCases = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = string(row, "id");
            if (id == null || testCases.containsKey(id)) {
                continue;
            }
            testCases.put(id, new AgentPackExportResponse.TestCaseSummary(
                    id,
                    string(row, "displayName"),
                    string(row, "status"),
                    string(row, "testType"),
                    string(row, "testCommand"),
                    string(row, "testFilePath"),
                    string(row, "locatedInId"),
                    string(row, "locatedInPath")
            ));
        }
        return sortedCopy(testCases.values(), TEST_CASE_ORDER);
    }

    private List<AgentPackExportResponse.PolicySummary> toPolicies(List<Map<String, Object>> rows) {
        LinkedHashMap<String, AgentPackExportResponse.PolicySummary> policies = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = string(row, "id");
            if (id == null || policies.containsKey(id)) {
                continue;
            }
            policies.put(id, new AgentPackExportResponse.PolicySummary(
                    id,
                    string(row, "name"),
                    sortedStrings(row, "allowedRepos"),
                    sortedStrings(row, "allowedCommands"),
                    sortedStrings(row, "forbiddenCommands"),
                    sortedStrings(row, "allowedEnvironments"),
                    sortedStrings(row, "secretScopes"),
                    integer(row, "maxFilesTouched"),
                    bool(row, "requiresHumanApproval"),
                    string(row, "approvalThreshold")
            ));
        }
        return sortedCopy(policies.values(), POLICY_ORDER);
    }

    private List<AgentPackExportResponse.ConventionSummary> toConventions(List<Map<String, Object>> rows) {
        LinkedHashMap<String, AgentPackExportResponse.ConventionSummary> conventions = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = string(row, "id");
            if (id == null || conventions.containsKey(id)) {
                continue;
            }
            conventions.put(id, new AgentPackExportResponse.ConventionSummary(
                    id,
                    string(row, "name"),
                    string(row, "category"),
                    string(row, "enforcement"),
                    string(row, "scope"),
                    string(row, "docRef"),
                    string(row, "activeStatus")
            ));
        }
        return sortedCopy(conventions.values(), CONVENTION_ORDER);
    }

    private List<AgentPackExportResponse.QualityConstraintSummary> toQualityConstraints(List<Map<String, Object>> rows) {
        LinkedHashMap<String, AgentPackExportResponse.QualityConstraintSummary> constraints = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String id = string(row, "id");
            if (id == null || constraints.containsKey(id)) {
                continue;
            }
            constraints.put(id, new AgentPackExportResponse.QualityConstraintSummary(
                    id,
                    string(row, "name"),
                    string(row, "constraintType"),
                    string(row, "priority"),
                    string(row, "threshold"),
                    string(row, "status")
            ));
        }
        return sortedCopy(constraints.values(), QUALITY_CONSTRAINT_ORDER);
    }

    private Map<String, Object> map(Map<String, Object> record) {
        return new LinkedHashMap<>(record);
    }

    private String string(Map<String, Object> record, String key) {
        Object value = record.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private List<String> stringList(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (!(value instanceof List<?> values)) {
            return List.of();
        }
        return values.stream()
                .filter(Objects::nonNull)
                .map(String::valueOf)
                .toList();
    }

    private List<String> sortedStrings(Map<String, Object> record, String key) {
        return stringList(record, key).stream()
                .sorted(NULL_SAFE_STRING)
                .toList();
    }

    private <T> List<T> sortedCopy(Collection<T> values, Comparator<? super T> comparator) {
        return values.stream()
                .sorted(comparator)
                .toList();
    }

    private Integer integer(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (value instanceof Number number) {
            return number.intValue();
        }
        return null;
    }

    private Boolean bool(Map<String, Object> record, String key) {
        Object value = record.get(key);
        if (value instanceof Boolean bool) {
            return bool;
        }
        if (value == null) {
            return null;
        }
        return Boolean.valueOf(String.valueOf(value));
    }
}
