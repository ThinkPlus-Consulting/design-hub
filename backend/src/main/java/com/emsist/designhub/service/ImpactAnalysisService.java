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
            OPTIONAL MATCH (ca)-[:DEPENDS_ON_ASSET*1..5]->(dep:CodeAsset)
            WITH ca, collect(DISTINCT dep) + [ca] AS allAssets
            UNWIND allAssets AS asset
            OPTIONAL MATCH (asset)-[:ASSET_FOR_SCREEN]->(scr:Screen)
            OPTIONAL MATCH (asset)-[:ASSET_FOR_API]->(api:ApiContract)
            OPTIONAL MATCH (asset)-[:ASSET_FOR_ENTITY]->(de:DataEntity)
            OPTIONAL MATCH (asset)-[:ASSET_FOR_RULE]->(r:Rule)
            WITH allAssets,
                 collect(DISTINCT scr) + collect(DISTINCT api)
                 + collect(DISTINCT de) + collect(DISTINCT r) AS artifacts
            WITH allAssets, artifacts,
                 CASE WHEN size(artifacts) = 0 THEN [null] ELSE artifacts END AS safeArtifacts
            UNWIND safeArtifacts AS artifact
            OPTIONAL MATCH (us:UserStory)-[:DELIVERS]->(artifact)
            OPTIONAL MATCH (us)-[:VERIFIED_BY]->(tc:TestCase)
            RETURN
                 size(allAssets) AS blastRadiusFiles,
                 [s IN collect(DISTINCT us.storyId) WHERE s IS NOT NULL] AS affectedStories,
                 [t IN collect(DISTINCT tc.testCaseId) WHERE t IS NOT NULL] AS affectedTests
            """;
    }
}
