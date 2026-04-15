package com.emsist.designhub.systemshellgraph.service;

import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphEdgeResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphNodeResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellTreeNodeDataResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellTreeNodeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SystemShellGraphQueryService {

    public static final String GRAPH_SCOPE = "SYSTEM_FRONTEND_GRAPH";
    public static final String GRAPH_CODE = "SYSTEM_FRONTEND_GRAPH_V1";
    public static final String GRAPH_NAME = "Frontend System Graph";

    private final Neo4jClient neo4jClient;

    public SystemShellGraphResponse getGraph() {
        List<SystemShellGraphNodeResponse> nodes = fetchNodes(GRAPH_SCOPE);
        List<SystemShellGraphEdgeResponse> relationships = fetchRelationships(GRAPH_SCOPE);

        return new SystemShellGraphResponse(
                GRAPH_SCOPE,
                GRAPH_CODE,
                GRAPH_NAME,
                nodes,
                relationships,
                buildNavigationTree(nodes, relationships)
        );
    }

    private List<SystemShellGraphNodeResponse> fetchNodes(String graphScope) {
        return neo4jClient.query("""
                        MATCH (n:SystemShellGraphNode {graphScope: $graphScope})
                        RETURN
                          n.name AS name,
                          n.family AS family,
                          n.objectType AS objectType,
                          n.domain AS domain,
                          n.layer AS layer,
                          n.description AS description,
                          n.sortOrder AS sortOrder,
                          n.id AS id,
                          n.guid AS guid,
                          n.status AS status,
                          n.assetName AS assetName,
                          n.assetType AS assetType,
                          n.definitionId AS definitionId,
                          n.packageName AS packageName,
                          n.packageExport AS packageExport,
                          n.packageVersion AS packageVersion,
                          n.iconPackage AS iconPackage,
                          n.themePackage AS themePackage,
                          n.configurationJson AS configurationJson,
                          n.renderMode AS renderMode,
                          n.defaultState AS defaultState,
                          n.controlSource AS controlSource,
                          n.layoutRegion AS layoutRegion,
                          n.displayMode AS displayMode,
                          n.positionMode AS positionMode,
                          n.top AS top,
                          n.right AS right,
                          n.bottom AS bottom,
                          n.left AS left,
                          n.width AS width,
                          n.height AS height,
                          n.minWidth AS minWidth,
                          n.minHeight AS minHeight,
                          n.maxWidth AS maxWidth,
                          n.maxHeight AS maxHeight,
                          n.marginTop AS marginTop,
                          n.marginRight AS marginRight,
                          n.marginBottom AS marginBottom,
                          n.marginLeft AS marginLeft,
                          n.gap AS gap,
                          n.rowGap AS rowGap,
                          n.columnGap AS columnGap,
                          n.paddingTop AS paddingTop,
                          n.paddingRight AS paddingRight,
                          n.paddingBottom AS paddingBottom,
                          n.paddingLeft AS paddingLeft,
                          n.justifyContent AS justifyContent,
                          n.alignItems AS alignItems,
                          n.alignSelf AS alignSelf,
                          n.flexDirection AS flexDirection,
                          n.flexWrap AS flexWrap,
                          n.overflowX AS overflowX,
                          n.overflowY AS overflowY,
                          n.zIndex AS zIndex,
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
                          n.htmlTag AS htmlTag,
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
                        ORDER BY n.layer, coalesce(n.sortOrder, 2147483647), n.family, n.name, n.id
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

    private List<SystemShellTreeNodeResponse> buildNavigationTree(
            List<SystemShellGraphNodeResponse> nodes,
            List<SystemShellGraphEdgeResponse> relationships
    ) {
        Map<String, SystemShellGraphNodeResponse> nodeById = nodes.stream()
                .filter(node -> node.id() != null && !node.id().isBlank())
                .collect(HashMap::new, (map, node) -> map.put(node.id(), node), HashMap::putAll);

        Map<String, List<String>> outgoing = new HashMap<>();
        for (SystemShellGraphEdgeResponse relationship : relationships) {
            if (!isStructuralRelationship(relationship.relationshipType())) {
                continue;
            }
            if (relationship.fromId() == null || relationship.toId() == null) {
                continue;
            }

            outgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship.toId());
        }

        return nodeById.values().stream()
                .filter(node -> "instance".equals(node.layer()) && "Application".equals(node.family()))
                .sorted(this::compareNodes)
                .map(node -> buildNavigationSubtree(node.id(), nodeById, outgoing, new LinkedHashSet<>()))
                .toList();
    }

    private SystemShellTreeNodeResponse buildNavigationSubtree(
            String objectId,
            Map<String, SystemShellGraphNodeResponse> nodeById,
            Map<String, List<String>> outgoing,
            Set<String> path
    ) {
        SystemShellGraphNodeResponse node = nodeById.get(objectId);
        if (node == null) {
            throw new IllegalStateException("Missing graph node " + objectId);
        }

        List<SystemShellTreeNodeResponse> children = (outgoing.getOrDefault(objectId, List.of())).stream()
                .filter(childId -> !path.contains(childId) && nodeById.containsKey(childId))
                .map(nodeById::get)
                .filter(Objects::nonNull)
                .sorted(this::compareNodes)
                .map(child -> {
                    Set<String> nextPath = new LinkedHashSet<>(path);
                    nextPath.add(objectId);
                    return buildNavigationSubtree(child.id(), nodeById, outgoing, nextPath);
                })
                .toList();

        String label = defaulted(node.name(), node.id());
        String domTargetGuid = resolveDomTargetGuid(node, children);

        return new SystemShellTreeNodeResponse(
                defaulted(node.id(), objectId),
                label,
                false,
                true,
                new SystemShellTreeNodeDataResponse(
                        "graph",
                        label,
                        node.family(),
                        node.layer(),
                        node.id(),
                        node.guid(),
                        domTargetGuid,
                        node.assetType()
                ),
                children
        );
    }

    private String resolveDomTargetGuid(
            SystemShellGraphNodeResponse node,
            List<SystemShellTreeNodeResponse> children
    ) {
        if ("Application".equals(node.family())) {
            return children.stream()
                    .map(SystemShellTreeNodeResponse::data)
                    .filter(Objects::nonNull)
                    .map(SystemShellTreeNodeDataResponse::domTargetGuid)
                    .filter(Objects::nonNull)
                    .findFirst()
                    .orElse(null);
        }

        return stringValue(node.guid());
    }

    private boolean isStructuralRelationship(String relationshipType) {
        return Set.of("HAS_SHELL", "HAS_SCREEN", "HAS_SECTION", "HAS_COMPONENT").contains(relationshipType);
    }

    private int compareNodes(SystemShellGraphNodeResponse left, SystemShellGraphNodeResponse right) {
        int leftRank = familyPriority(left.family());
        int rightRank = familyPriority(right.family());
        if (leftRank != rightRank) {
            return Integer.compare(leftRank, rightRank);
        }

        int leftShellOrder = shellContainerOrder(left.name());
        int rightShellOrder = shellContainerOrder(right.name());
        if (leftShellOrder != rightShellOrder) {
            return Integer.compare(leftShellOrder, rightShellOrder);
        }

        int leftSortOrder = left.sortOrder() != null ? left.sortOrder() : Integer.MAX_VALUE;
        int rightSortOrder = right.sortOrder() != null ? right.sortOrder() : Integer.MAX_VALUE;
        if (leftSortOrder != rightSortOrder) {
            return Integer.compare(leftSortOrder, rightSortOrder);
        }

        String leftName = defaulted(left.name(), "");
        String rightName = defaulted(right.name(), "");
        if (!leftName.equals(rightName)) {
            return leftName.compareTo(rightName);
        }

        return defaulted(left.id(), "").compareTo(defaulted(right.id(), ""));
    }

    private int familyPriority(String family) {
        return switch (defaulted(family, "")) {
            case "Application" -> 5;
            case "Shell" -> 10;
            case "Screen" -> 20;
            case "Container" -> 30;
            case "Section" -> 35;
            case "Component" -> 40;
            default -> 999;
        };
    }

    private int shellContainerOrder(String name) {
        return switch (defaulted(name, "")) {
            case "Header Container" -> 10;
            case "Breadcrumb Container" -> 20;
            case "Main Container" -> 30;
            case "Footer Container" -> 40;
            default -> 999;
        };
    }

    private SystemShellGraphNodeResponse mapNode(Map<String, Object> row) {
        return new SystemShellGraphNodeResponse(
                stringValue(row.get("name")),
                stringValue(row.get("family")),
                stringValue(row.get("objectType")),
                stringValue(row.get("domain")),
                stringValue(row.get("layer")),
                stringValue(row.get("description")),
                integerValue(row.get("sortOrder")),
                stringValue(row.get("id")),
                stringValue(row.get("guid")),
                stringValue(row.get("status")),
                stringValue(row.get("assetName")),
                stringValue(row.get("assetType")),
                stringValue(row.get("definitionId")),
                stringValue(row.get("packageName")),
                stringValue(row.get("packageExport")),
                stringValue(row.get("packageVersion")),
                stringValue(row.get("iconPackage")),
                stringValue(row.get("themePackage")),
                stringValue(row.get("configurationJson")),
                stringValue(row.get("renderMode")),
                stringValue(row.get("defaultState")),
                stringValue(row.get("controlSource")),
                stringValue(row.get("layoutRegion")),
                stringValue(row.get("displayMode")),
                stringValue(row.get("positionMode")),
                stringValue(row.get("top")),
                stringValue(row.get("right")),
                stringValue(row.get("bottom")),
                stringValue(row.get("left")),
                stringValue(row.get("width")),
                stringValue(row.get("height")),
                stringValue(row.get("minWidth")),
                stringValue(row.get("minHeight")),
                stringValue(row.get("maxWidth")),
                stringValue(row.get("maxHeight")),
                stringValue(row.get("marginTop")),
                stringValue(row.get("marginRight")),
                stringValue(row.get("marginBottom")),
                stringValue(row.get("marginLeft")),
                stringValue(row.get("gap")),
                stringValue(row.get("rowGap")),
                stringValue(row.get("columnGap")),
                stringValue(row.get("paddingTop")),
                stringValue(row.get("paddingRight")),
                stringValue(row.get("paddingBottom")),
                stringValue(row.get("paddingLeft")),
                stringValue(row.get("justifyContent")),
                stringValue(row.get("alignItems")),
                stringValue(row.get("alignSelf")),
                stringValue(row.get("flexDirection")),
                stringValue(row.get("flexWrap")),
                stringValue(row.get("overflowX")),
                stringValue(row.get("overflowY")),
                integerValue(row.get("zIndex")),
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
                stringValue(row.get("htmlTag")),
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

    private String defaulted(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value;
    }
}
