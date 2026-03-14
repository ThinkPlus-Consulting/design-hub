package com.emsist.designhub.service;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
public class ReconciliationService {

    private final Neo4jClient neo4jClient;
    private final RequirementSyncService syncService;

    public ReconciliationService(Neo4jClient neo4jClient, RequirementSyncService syncService) {
        this.neo4jClient = neo4jClient;
        this.syncService = syncService;
    }

    public enum Decision { CREATE, UPDATE, SKIP, CONFLICT }

    public Decision decide(String nodeId, String nodeType, String storedHash, String currentHash) {
        if (storedHash == null) {
            return Decision.CREATE;
        }
        if (storedHash.equals(currentHash)) {
            return Decision.SKIP;
        }
        return Decision.UPDATE;
    }
}
