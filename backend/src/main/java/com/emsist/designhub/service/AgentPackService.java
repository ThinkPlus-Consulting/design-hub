package com.emsist.designhub.service;

import com.emsist.designhub.dto.*;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class AgentPackService {

    private static final List<String> BLOCKING_CHECKS = List.of(
            "repoPath", "effectiveBuildCommand", "manifestPath",
            "codeAssetPresence", "testFileResolution"
    );

    private final Neo4jClient neo4jClient;
    private final AgentReadinessService readinessService;

    public AgentPackService(Neo4jClient neo4jClient, AgentReadinessService readinessService) {
        this.neo4jClient = neo4jClient;
        this.readinessService = readinessService;
    }

    public PackCompleteness computeCompleteness(String storyId) {
        var checks = readinessService.assessAgentReadiness(storyId);
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
}
