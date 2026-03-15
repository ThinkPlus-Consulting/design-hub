package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessCapabilityTest {

    @Test
    void shouldBuildCapabilityWithRequiredFields() {
        BusinessCapability capability = BusinessCapability.builder()
                .capabilityId("CAP-AUTH")
                .name("Authentication & Identity")
                .status(Status.DEFINED)
                .build();

        assertEquals("CAP-AUTH", capability.getCapabilityId());
        assertEquals("Authentication & Identity", capability.getName());
        assertEquals(Status.DEFINED, capability.getStatus());
    }

    @Test
    void shouldBuildCapabilityWithOptionalDescription() {
        BusinessCapability capability = BusinessCapability.builder()
                .capabilityId("CAP-GRAPH")
                .name("Graph Intelligence")
                .description("Ability to traverse and query the design graph for delivery insights")
                .status(Status.APPROVED)
                .build();

        assertEquals("Graph Intelligence", capability.getName());
        assertNotNull(capability.getDescription());
    }

    @Test
    void shouldFollowIdPattern() {
        BusinessCapability capability = BusinessCapability.builder()
                .capabilityId("CAP-DRIFT-DETECT")
                .name("Drift Detection")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(capability.getCapabilityId().startsWith("CAP-"),
                "capabilityId must follow pattern CAP-{code}");
    }
}
