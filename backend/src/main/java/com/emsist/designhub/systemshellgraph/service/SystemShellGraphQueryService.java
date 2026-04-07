package com.emsist.designhub.systemshellgraph.service;

import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphEdgeResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphNodeResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemShellGraphQueryService {

    public static final String GRAPH_SCOPE = "SYSTEM_FRONTEND_GRAPH";
    public static final String GRAPH_CODE = "SYSTEM_FRONTEND_GRAPH_V1";
    public static final String GRAPH_NAME = "Frontend System Graph";

    private final Neo4jClient neo4jClient;

    public SystemShellGraphResponse getGraph() {
        return new SystemShellGraphResponse(
                GRAPH_SCOPE,
                GRAPH_CODE,
                GRAPH_NAME,
                fetchNodes(GRAPH_SCOPE),
                fetchRelationships(GRAPH_SCOPE)
        );
    }

    private List<SystemShellGraphNodeResponse> fetchNodes(String graphScope) {
        return neo4jClient.query("""
                        MATCH (n:SystemShellGraphNode {graphScope: $graphScope})
                        RETURN
                          n.code AS code,
                          n.name AS name,
                          n.family AS family,
                          n.objectType AS objectType,
                          n.domain AS domain,
                          n.layer AS layer,
                          n.description AS description,
                          n.hierarchyCode AS hierarchyCode,
                          n.id AS id,
                          n.guid AS guid,
                          n.status AS status,
                          n.assetName AS assetName,
                          n.assetType AS assetType,
                          n.definitionCode AS definitionCode,
                          n.implementationSourcePath AS implementationSourcePath,
                          n.configurationJson AS configurationJson,
                          n.renderMode AS renderMode,
                          n.defaultState AS defaultState,
                          n.controlSource AS controlSource,
                          n.backgroundType AS backgroundType,
                          n.backgroundColorStyle AS backgroundColorStyle,
                          n.backgroundPatternKey AS backgroundPatternKey,
                          n.backgroundPatternOpacity AS backgroundPatternOpacity,
                          n.backgroundImagePath AS backgroundImagePath,
                          n.viewportWidth AS viewportWidth,
                          n.viewportHeight AS viewportHeight,
                          n.viewportCategory AS viewportCategory,
                          n.executionMethod AS executionMethod,
                          n.stepOrder AS stepOrder,
                          n.ruleScope AS ruleScope,
                          n.conditionExpression AS conditionExpression,
                          n.executionEffect AS executionEffect,
                          n.blockerType AS blockerType,
                          n.blockingEffect AS blockingEffect,
                          n.sectionType AS sectionType,
                          n.repeatable AS repeatable,
                          n.elementType AS elementType,
                          n.semanticLevel AS semanticLevel,
                          n.primeComponent AS primeComponent,
                          n.tokenFamilies AS tokenFamilies,
                          n.issueSource AS issueSource,
                          n.issueCategory AS issueCategory,
                          n.issueRule AS issueRule,
                          n.issueSeverity AS issueSeverity,
                          n.issuePrompt AS issuePrompt,
                          n.ruleSetType AS ruleSetType,
                          n.ruleSetScope AS ruleSetScope,
                          n.actionType AS actionType,
                          n.actionValue AS actionValue,
                          n.priority AS priority,
                          n.stopProcessing AS stopProcessing
                        ORDER BY n.layer, n.hierarchyCode, n.code
                        """)
                .bind(graphScope)
                .to("graphScope")
                .fetch()
                .all()
                .stream()
                .map(this::mapNode)
                .toList();
    }

    private List<SystemShellGraphEdgeResponse> fetchRelationships(String graphScope) {
        return neo4jClient.query("""
                        MATCH (from:SystemShellGraphNode {graphScope: $graphScope})-[r]->(to:SystemShellGraphNode {graphScope: $graphScope})
                        RETURN
                          from.id AS fromId,
                          type(r) AS relationshipType,
                          to.id AS toId,
                          r.activeName AS activeName,
                          r.passiveName AS passiveName
                        ORDER BY from.id, relationshipType, to.id
                        """)
                .bind(graphScope)
                .to("graphScope")
                .fetch()
                .all()
                .stream()
                .map(row -> new SystemShellGraphEdgeResponse(
                        stringValue(row.get("fromId")),
                        stringValue(row.get("relationshipType")),
                        stringValue(row.get("toId")),
                        stringValue(row.get("activeName")),
                        stringValue(row.get("passiveName"))
                ))
                .toList();
    }

    private SystemShellGraphNodeResponse mapNode(Map<String, Object> row) {
        return new SystemShellGraphNodeResponse(
                stringValue(row.get("code")),
                stringValue(row.get("name")),
                stringValue(row.get("family")),
                stringValue(row.get("objectType")),
                stringValue(row.get("domain")),
                stringValue(row.get("layer")),
                stringValue(row.get("description")),
                stringValue(row.get("hierarchyCode")),
                stringValue(row.get("id")),
                stringValue(row.get("guid")),
                stringValue(row.get("status")),
                stringValue(row.get("assetName")),
                stringValue(row.get("assetType")),
                stringValue(row.get("definitionCode")),
                stringValue(row.get("implementationSourcePath")),
                stringValue(row.get("configurationJson")),
                stringValue(row.get("renderMode")),
                stringValue(row.get("defaultState")),
                stringValue(row.get("controlSource")),
                stringValue(row.get("backgroundType")),
                stringValue(row.get("backgroundColorStyle")),
                stringValue(row.get("backgroundPatternKey")),
                doubleValue(row.get("backgroundPatternOpacity")),
                stringValue(row.get("backgroundImagePath")),
                integerValue(row.get("viewportWidth")),
                integerValue(row.get("viewportHeight")),
                stringValue(row.get("viewportCategory")),
                stringValue(row.get("executionMethod")),
                integerValue(row.get("stepOrder")),
                stringValue(row.get("ruleScope")),
                stringValue(row.get("conditionExpression")),
                stringValue(row.get("executionEffect")),
                stringValue(row.get("blockerType")),
                stringValue(row.get("blockingEffect")),
                stringValue(row.get("sectionType")),
                booleanValue(row.get("repeatable")),
                stringValue(row.get("elementType")),
                stringValue(row.get("semanticLevel")),
                stringValue(row.get("primeComponent")),
                stringListValue(row.get("tokenFamilies")),
                stringValue(row.get("issueSource")),
                stringValue(row.get("issueCategory")),
                stringValue(row.get("issueRule")),
                stringValue(row.get("issueSeverity")),
                stringValue(row.get("issuePrompt")),
                stringValue(row.get("ruleSetType")),
                stringValue(row.get("ruleSetScope")),
                stringValue(row.get("actionType")),
                stringValue(row.get("actionValue")),
                integerValue(row.get("priority")),
                booleanValue(row.get("stopProcessing"))
        );
    }

    private String stringValue(Object value) {
        return value instanceof String string && !string.isBlank() ? string : null;
    }

    private Integer integerValue(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private Double doubleValue(Object value) {
        return value instanceof Number number ? number.doubleValue() : null;
    }

    private Boolean booleanValue(Object value) {
        return value instanceof Boolean bool ? bool : null;
    }

    private List<String> stringListValue(Object value) {
        if (!(value instanceof List<?> rawList)) {
            return List.of();
        }

        return rawList.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .toList();
    }
}
