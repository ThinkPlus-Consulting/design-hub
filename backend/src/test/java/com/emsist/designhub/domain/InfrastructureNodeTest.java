package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InfrastructureNodeTest {

    @Test
    void shouldBuildInfrastructureNodeWithStubFields() {
        InfrastructureNode node = InfrastructureNode.builder()
                .nodeId("INF-001")
                .name("designhub-app-01")
                .status(Status.DEFINED)
                .build();

        assertEquals("INF-001", node.getNodeId());
        assertEquals("designhub-app-01", node.getName());
        assertEquals(Status.DEFINED, node.getStatus());
    }

    @Test
    void shouldFollowInfrastructureNodeIdPattern() {
        InfrastructureNode node = InfrastructureNode.builder()
                .nodeId("INF-014")
                .name("designhub-worker-02")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(node.getNodeId().startsWith("INF-"),
                "nodeId must follow pattern INF-{seq}");
    }
}
