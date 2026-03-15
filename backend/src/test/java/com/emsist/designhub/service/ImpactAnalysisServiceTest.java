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
}
