package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DeploymentTest {

    @Test
    void shouldBuildDeploymentWithStubFields() {
        Deployment deployment = Deployment.builder()
                .deploymentId("DEP-DEV-001")
                .name("Local developer deployment")
                .status(Status.DEFINED)
                .build();

        assertEquals("DEP-DEV-001", deployment.getDeploymentId());
        assertEquals("Local developer deployment", deployment.getName());
        assertEquals(Status.DEFINED, deployment.getStatus());
    }

    @Test
    void shouldFollowDeploymentIdPattern() {
        Deployment deployment = Deployment.builder()
                .deploymentId("DEP-PRD-010")
                .name("Production deployment")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(deployment.getDeploymentId().startsWith("DEP-"),
                "deploymentId must follow pattern DEP-{env}-{seq}");
    }
}
