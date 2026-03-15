package com.emsist.designhub.service;

import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;

@Service
public class ImpactAnalysisService {

    private final Neo4jClient neo4jClient;

    public ImpactAnalysisService(Neo4jClient neo4jClient) {
        this.neo4jClient = neo4jClient;
    }

    public String buildBlastRadiusQuery(String codeAssetId) {
        return """
            MATCH (ca:CodeAsset {codeAssetId: $codeAssetId})
            OPTIONAL MATCH path = (ca)-[:DEPENDS_ON_ASSET*1..5]->(dep:CodeAsset)
            WITH ca, collect(DISTINCT dep) + [ca] AS allAssets
            UNWIND allAssets AS asset
            OPTIONAL MATCH (asset)-[:ASSET_FOR_SCREEN]->(scr:Screen)
            OPTIONAL MATCH (asset)-[:ASSET_FOR_API]->(api:ApiContract)
            OPTIONAL MATCH (asset)-[:ASSET_FOR_ENTITY]->(de:DataEntity)
            OPTIONAL MATCH (asset)-[:ASSET_FOR_RULE]->(r:Rule)
            WITH allAssets,
                 collect(DISTINCT scr) AS screens,
                 collect(DISTINCT api) AS apis,
                 collect(DISTINCT de) AS entities,
                 collect(DISTINCT r) AS rules
            UNWIND (screens + apis + entities + rules) AS artifact
            OPTIONAL MATCH (us:UserStory)-[:DELIVERS]->(artifact)
            OPTIONAL MATCH (us)-[:VERIFIED_BY]->(tc:TestCase)
            RETURN
                 size(allAssets) AS blastRadiusFiles,
                 collect(DISTINCT us.storyId) AS affectedStories,
                 collect(DISTINCT tc.testCaseId) AS affectedTests
            """;
    }
}
