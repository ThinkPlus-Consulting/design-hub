package com.emsist.designhub.service;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AgentReadinessService {

    public static final List<String> AGENT_READY_CHECKS = List.of(
            "repoPath",
            "effectiveBuildCommand",
            "manifestPath",
            "codeAssetPresence",
            "testFileResolution",
            "entrypointPath"
    );

    private final Neo4jClient neo4jClient;

    public AgentReadinessService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    /**
     * Assess agent readiness for a UserStory.
     * Returns a map of check name to pass/fail boolean.
     * The 6th check (entrypointPath) is ADVISORY — does not block.
     */
    public Map<String, Boolean> assessAgentReadiness(String storyId) {
        Map<String, Boolean> results = new LinkedHashMap<>();

        var record = neo4jClient.query("""
            MATCH (us:UserStory {storyId: $storyId})
            // Branch 1: non-Message deliverables
            OPTIONAL MATCH (us)-[:DELIVERS]->(d) WHERE NOT d:Message
            OPTIONAL MATCH (d)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-(comp:ApplicationComponent)
            OPTIONAL MATCH (comp)<-[:HAS_COMPONENT]-(app:Application)
            OPTIONAL MATCH (comp)-[:HAS_CODE_ASSET]->(ca:CodeAsset)
            WITH us,
                 collect(DISTINCT comp) AS directComps,
                 collect(DISTINCT app) AS directApps,
                 collect(DISTINCT ca) AS directAssets
            // Branch 2: Message deliverables (transitive via Screen)
            OPTIONAL MATCH (us)-[:DELIVERS]->(m:Message)
            OPTIONAL MATCH (m)<-[:HAS_MESSAGE]-(scr:Screen)<-[:SUPPORTS_SCREEN]-(comp2:ApplicationComponent)
            OPTIONAL MATCH (comp2)<-[:HAS_COMPONENT]-(app2:Application)
            OPTIONAL MATCH (comp2)-[:HAS_CODE_ASSET]->(ca2:CodeAsset)
            WITH us,
                 directComps + collect(DISTINCT comp2) AS allComps,
                 directApps + collect(DISTINCT app2) AS allApps,
                 directAssets + collect(DISTINCT ca2) AS allAssets
            // Verification test-file resolution
            OPTIONAL MATCH (us)-[:VERIFIED_BY]->(tc:TestCase)-[:LOCATED_IN]->(tca:CodeAsset)
            WITH us, allComps, allApps, allAssets,
                 count(DISTINCT tca) AS testFileCount
            RETURN
                 any(a IN allApps WHERE a.repoPath IS NOT NULL) AS hasRepoPath,
                 any(c IN allComps WHERE
                      COALESCE(c.buildCommand,
                           [a IN allApps WHERE a.defaultBuildCommand IS NOT NULL][0].defaultBuildCommand
                      ) IS NOT NULL) AS hasBuildCommand,
                 any(c IN allComps WHERE c.manifestPath IS NOT NULL) AS hasManifestPath,
                 size(allAssets) >= 1 AS hasCodeAsset,
                 testFileCount >= 1 AS hasTestFile,
                 any(c IN allComps WHERE c.entrypointPath IS NOT NULL) AS hasEntryPoint
            """)
                .bind(storyId).to("storyId")
                .fetch().first();

        if (record.isEmpty()) {
            AGENT_READY_CHECKS.forEach(check -> results.put(check, false));
            return results;
        }

        var r = record.get();
        results.put("repoPath", Boolean.TRUE.equals(r.get("hasRepoPath")));
        results.put("effectiveBuildCommand", Boolean.TRUE.equals(r.get("hasBuildCommand")));
        results.put("manifestPath", Boolean.TRUE.equals(r.get("hasManifestPath")));
        results.put("codeAssetPresence", Boolean.TRUE.equals(r.get("hasCodeAsset")));
        results.put("testFileResolution", Boolean.TRUE.equals(r.get("hasTestFile")));
        results.put("entrypointPath", Boolean.TRUE.equals(r.get("hasEntryPoint"))); // ADVISORY

        return results;
    }

    /**
     * Check if a story passes the BLOCKING agent-ready checks (5 of 6).
     * entrypointPath is ADVISORY and does not block.
     */
    public boolean isAgentReady(String storyId) {
        var results = assessAgentReadiness(storyId);
        return results.getOrDefault("repoPath", false)
                && results.getOrDefault("effectiveBuildCommand", false)
                && results.getOrDefault("manifestPath", false)
                && results.getOrDefault("codeAssetPresence", false)
                && results.getOrDefault("testFileResolution", false);
    }
}
