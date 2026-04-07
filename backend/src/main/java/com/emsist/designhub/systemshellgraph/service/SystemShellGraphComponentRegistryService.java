package com.emsist.designhub.systemshellgraph.service;

import com.emsist.designhub.systemshellgraph.dto.ComponentRegistryDefinitionResponse;
import com.emsist.designhub.systemshellgraph.dto.ComponentRegistryInstanceResponse;
import com.emsist.designhub.systemshellgraph.dto.ComponentRegistryInstanceUpdateRequest;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class SystemShellGraphComponentRegistryService {

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {
    };

    private final Neo4jClient neo4jClient;
    private final ObjectMapper objectMapper;

    public List<ComponentRegistryDefinitionResponse> getDefinitions() {
        return neo4jClient.query("""
                        MATCH (d:SystemShellGraphNode {graphScope: $graphScope, layer: 'definition', objectType: 'Component'})
                        OPTIONAL MATCH (i:SystemShellGraphNode {graphScope: $instanceGraphScope, layer: 'instance', objectType: 'Component'})-[:INSTANCE_OF]->(d)
                        WITH d, head(collect(i)) AS instance
                        RETURN
                          d.code AS code,
                          d.objectType AS objectType,
                          d.assetType AS assetType,
                          d.assetName AS assetName,
                          d.description AS description,
                          d.id AS id,
                          d.status AS status,
                          d.implementationSourcePath AS implementationSourcePath,
                          instance.code AS defaultInstanceCode
                        ORDER BY d.assetName
                        """)
                .bind(SystemShellGraphSeedService.COMPONENT_REGISTRY_SCOPE)
                .to("graphScope")
                .bind(SystemShellGraphQueryService.GRAPH_SCOPE)
                .to("instanceGraphScope")
                .fetch()
                .all()
                .stream()
                .map(row -> new ComponentRegistryDefinitionResponse(
                        stringValue(row.get("code")),
                        stringValue(row.get("objectType")),
                        stringValue(row.get("assetType")),
                        stringValue(row.get("assetName")),
                        stringValue(row.get("description")),
                        stringValue(row.get("id")),
                        stringValue(row.get("status")),
                        stringValue(row.get("implementationSourcePath")),
                        stringValue(row.get("defaultInstanceCode"))
                ))
                .toList();
    }

    public ComponentRegistryInstanceResponse getDefaultInstance(String assetType) {
        return fetchInstance("""
                MATCH (definition:SystemShellGraphNode {graphScope: $graphScope, layer: 'definition', objectType: 'Component', assetType: $assetType})
                OPTIONAL MATCH (instance:SystemShellGraphNode {graphScope: $instanceGraphScope, layer: 'instance', objectType: 'Component'})-[:INSTANCE_OF]->(definition)
                WITH definition, head(collect(instance)) AS instance
                OPTIONAL MATCH (target:SystemShellGraphNode {graphScope: $instanceGraphScope})-[:HAS_COMPONENT]->(instance)
                RETURN
                  instance.code AS code,
                  instance.objectType AS objectType,
                  instance.assetType AS assetType,
                  instance.assetName AS assetName,
                  instance.name AS name,
                  instance.description AS description,
                  instance.id AS id,
                  instance.status AS status,
                  definition.code AS definitionCode,
                  coalesce(instance.implementationSourcePath, definition.implementationSourcePath) AS implementationSourcePath,
                  target.code AS targetObjectCode,
                  target.name AS targetObjectName,
                  target.family AS targetObjectType,
                  instance.configurationJson AS configurationJson
                """, Map.of(
                "graphScope", SystemShellGraphSeedService.COMPONENT_REGISTRY_SCOPE,
                "instanceGraphScope", SystemShellGraphQueryService.GRAPH_SCOPE,
                "assetType", assetType
        ));
    }

    public ComponentRegistryInstanceResponse saveInstance(String instanceCode, ComponentRegistryInstanceUpdateRequest request) {
        TargetObject targetObject = resolveTargetObject(blankToNull(request.targetObjectCode()));
        Map<String, Object> configuration = request.configuration() == null
                ? Map.of()
                : new LinkedHashMap<>(request.configuration());

        String configurationJson;
        try {
            configurationJson = objectMapper.writeValueAsString(configuration);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Unable to serialize component configuration.", exception);
        }

        Map<String, Object> parameters = new LinkedHashMap<>();
        parameters.put("graphScope", SystemShellGraphQueryService.GRAPH_SCOPE);
        parameters.put("instanceCode", instanceCode);
        parameters.put("name", blankToNull(request.name()));
        parameters.put("description", blankToNull(request.description()));
        parameters.put("status", blankToNull(request.status()));
        parameters.put("targetObjectCode", targetObject.code());
        parameters.put("configurationJson", configurationJson);
        parameters.put("targetGraphScope", SystemShellGraphQueryService.GRAPH_SCOPE);

        long updated = neo4jClient.query("""
                        MATCH (instance:SystemShellGraphNode {graphScope: $graphScope, layer: 'instance', objectType: 'Component', code: $instanceCode})
                        SET instance.name = $name,
                            instance.description = $description,
                            instance.status = $status,
                            instance.configurationJson = $configurationJson
                        WITH instance
                        OPTIONAL MATCH (instance)-[existing:PLACED_WITHIN]->(:SystemShellGraphNode)
                        DELETE existing
                        WITH instance
                        OPTIONAL MATCH (:SystemShellGraphNode)-[existingParent:HAS_COMPONENT]->(instance)
                        DELETE existingParent
                        WITH instance
                        OPTIONAL MATCH (target:SystemShellGraphNode {graphScope: $targetGraphScope, layer: 'instance', code: $targetObjectCode})
                        FOREACH (_ IN CASE WHEN target IS NULL THEN [] ELSE [1] END |
                            MERGE (instance)-[:PLACED_WITHIN]->(target)
                        )
                        FOREACH (_ IN CASE WHEN target IS NULL THEN [] ELSE [1] END |
                            MERGE (target)-[:HAS_COMPONENT]->(instance)
                        )
                        RETURN count(instance) AS updated
                        """)
                .bindAll(parameters)
                .fetchAs(Long.class)
                .one()
                .orElse(0L);

        if (updated == 0L) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Component instance " + instanceCode + " was not found.");
        }

        return fetchInstance("""
                MATCH (instance:SystemShellGraphNode {graphScope: $graphScope, layer: 'instance', objectType: 'Component', code: $instanceCode})
                OPTIONAL MATCH (instance)-[:INSTANCE_OF]->(definition:SystemShellGraphNode {graphScope: $definitionGraphScope, layer: 'definition', objectType: 'Component'})
                OPTIONAL MATCH (target:SystemShellGraphNode {graphScope: $graphScope})-[:HAS_COMPONENT]->(instance)
                RETURN
                  instance.code AS code,
                  instance.objectType AS objectType,
                  instance.assetType AS assetType,
                  instance.assetName AS assetName,
                  instance.name AS name,
                  instance.description AS description,
                  instance.id AS id,
                  instance.status AS status,
                  definition.code AS definitionCode,
                  coalesce(instance.implementationSourcePath, definition.implementationSourcePath) AS implementationSourcePath,
                  target.code AS targetObjectCode,
                  target.name AS targetObjectName,
                  target.family AS targetObjectType,
                  instance.configurationJson AS configurationJson
                """, Map.of(
                "graphScope", SystemShellGraphQueryService.GRAPH_SCOPE,
                "definitionGraphScope", SystemShellGraphSeedService.COMPONENT_REGISTRY_SCOPE,
                "instanceCode", instanceCode
        ));
    }

    private ComponentRegistryInstanceResponse fetchInstance(String cypher, Map<String, Object> parameters) {
        Map<String, Object> row = neo4jClient.query(cypher)
                .bindAll(parameters)
                .fetch()
                .one()
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Component instance was not found."));

        return new ComponentRegistryInstanceResponse(
                stringValue(row.get("code")),
                stringValue(row.get("objectType")),
                stringValue(row.get("assetType")),
                stringValue(row.get("assetName")),
                stringValue(row.get("name")),
                stringValue(row.get("description")),
                stringValue(row.get("id")),
                stringValue(row.get("status")),
                stringValue(row.get("definitionCode")),
                stringValue(row.get("implementationSourcePath")),
                stringValue(row.get("targetObjectCode")),
                stringValue(row.get("targetObjectName")),
                stringValue(row.get("targetObjectType")),
                configurationValue(row.get("configurationJson"))
        );
    }

    private TargetObject resolveTargetObject(String targetObjectCode) {
        if (targetObjectCode == null) {
            return new TargetObject(null, null);
        }

        return neo4jClient.query("""
                        MATCH (target:SystemShellGraphNode {graphScope: $graphScope, layer: 'instance', code: $targetObjectCode})
                        RETURN target.code AS code, target.family AS family
                        """)
                .bind(SystemShellGraphQueryService.GRAPH_SCOPE)
                .to("graphScope")
                .bind(targetObjectCode)
                .to("targetObjectCode")
                .fetch()
                .one()
                .map(row -> {
                    String family = stringValue(row.get("family"));
                    if (!"Element".equals(family)) {
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target object must be an Element.");
                    }
                    return new TargetObject(stringValue(row.get("code")), family);
                })
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Target object " + targetObjectCode + " was not found."));
    }

    private Map<String, Object> configurationValue(Object value) {
        if (!(value instanceof String json) || json.isBlank()) {
            return Map.of();
        }

        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (IOException exception) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unable to read component configuration.", exception);
        }
    }

    private String stringValue(Object value) {
        return value instanceof String string && !string.isBlank() ? string : null;
    }

    private String blankToNull(String value) {
        return value == null || value.isBlank() ? null : value;
    }

    private record TargetObject(String code, String family) {
    }
}
