package com.emsist.designhub.service;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.util.Map;
import java.util.Optional;

@Service
public class ReconciliationService {

    private final Neo4jClient neo4jClient;
    private final RequirementSyncService syncService;

    public ReconciliationService(Neo4jClient neo4jClient, RequirementSyncService syncService) {
        this.neo4jClient = neo4jClient;
        this.syncService = syncService;
    }

    public enum Decision { CREATE, UPDATE, SKIP, CONFLICT }

    /**
     * Pure decision logic — testable without Neo4j.
     * nodeExists distinguishes "node not found" (CREATE) from
     * "node found but no contentHash" (CONFLICT — manually created).
     */
    public Decision decide(boolean nodeExists, String storedHash, String currentHash) {
        if (!nodeExists) {
            return Decision.CREATE;
        }
        if (storedHash == null) {
            // Node exists but was never imported (no contentHash) — manual creation
            return Decision.CONFLICT;
        }
        if (storedHash.equals(currentHash)) {
            return Decision.SKIP;
        }
        return Decision.UPDATE;
    }

    /**
     * Graph-aware reconciliation: checks if node exists and retrieves its
     * contentHash from Neo4j, then delegates to decide().
     */
    public Decision reconcile(String nodeId, String nodeType, String currentHash) {
        var lookup = lookupNode(nodeId, nodeType);
        return decide(lookup.exists(), lookup.contentHash(), currentHash);
    }

    record NodeLookup(boolean exists, String contentHash) {}

    /**
     * Query Neo4j for node existence and its contentHash.
     * Returns exists=false if the node is not found.
     * Returns exists=true with contentHash=null if node exists but has no hash.
     */
    NodeLookup lookupNode(String nodeId, String nodeType) {
        String idField = getIdField(nodeType);
        String label = sanitizeLabel(nodeType);
        String cypher = String.format(
                "OPTIONAL MATCH (n:%s {%s: $nodeId}) " +
                "RETURN n IS NOT NULL AS nodeExists, n.contentHash AS contentHash",
                label, idField);

        var result = neo4jClient.query(cypher)
                .bind(nodeId).to("nodeId")
                .fetch().first();

        if (result.isEmpty()) {
            return new NodeLookup(false, null);
        }

        Map<String, Object> record = result.get();
        boolean exists = Boolean.TRUE.equals(record.get("nodeExists"));
        Object hashVal = record.get("contentHash");
        String contentHash = (hashVal != null) ? hashVal.toString() : null;
        return new NodeLookup(exists, contentHash);
    }

    private String getIdField(String type) {
        return switch (type) {
            case "UserStory" -> "storyId";
            case "Screen" -> "surfaceId";
            case "Journey" -> "journeyId";
            case "Epic" -> "epicId";
            case "Feature" -> "featureId";
            case "Task" -> "taskId";
            case "TestCase" -> "testCaseId";
            case "ApiContract" -> "contractId";
            case "DataEntity" -> "entityId";
            case "Rule" -> "ruleId";
            case "ProcessActivity" -> "activityId";
            case "BusinessProcess" -> "processId";
            default -> "id";
        };
    }

    private String sanitizeLabel(String label) {
        if (!label.matches("[A-Za-z][A-Za-z0-9]*")) {
            throw new IllegalArgumentException("Invalid node label: " + label);
        }
        return label;
    }
}
