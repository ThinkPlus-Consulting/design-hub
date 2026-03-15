package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ImpactAnalysisServiceTest {

    @Mock private Neo4jClient neo4jClient;

    @InjectMocks
    private ImpactAnalysisService impactService;

    @Test
    void shouldBuildBlastRadiusCypherForCodeAsset() {
        String cypher = impactService.buildBlastRadiusQuery("CA-DH-001");
        assertNotNull(cypher);
        assertTrue(cypher.contains("DEPENDS_ON_ASSET"));
        assertTrue(cypher.contains("ASSET_FOR_SCREEN"));
        assertTrue(cypher.contains("DELIVERS"));
    }

    @Test
    void shouldHandleEmptyArtifactListWithoutDroppingRows() {
        // The query must use a CASE guard so that assets with no ASSET_FOR_* edges
        // still return blastRadiusFiles instead of producing zero rows from UNWIND [].
        String cypher = impactService.buildBlastRadiusQuery("CA-ORPHAN-001");
        assertTrue(cypher.contains("CASE WHEN size(artifacts) = 0 THEN [null] ELSE artifacts END"),
                "Query must guard against empty UNWIND to preserve blast radius result");
        assertTrue(cypher.contains("WHERE s IS NOT NULL"),
                "Query must filter null storyIds introduced by the null sentinel");
    }
}
