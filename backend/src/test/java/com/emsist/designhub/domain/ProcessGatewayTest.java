package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessGatewayTest {

    @Test
    void shouldBuildProcessGatewayWithRequiredFields() {
        ProcessGateway gateway = ProcessGateway.builder()
                .gatewayId("GW-PROC-REVIEW-001")
                .name("Review outcome")
                .gatewayType("EXCLUSIVE")
                .status(Status.DEFINED)
                .build();

        assertEquals("GW-PROC-REVIEW-001", gateway.getGatewayId());
        assertEquals("EXCLUSIVE", gateway.getGatewayType());
        assertEquals(Status.DEFINED, gateway.getStatus());
    }

    @Test
    void shouldFollowGatewayIdPattern() {
        ProcessGateway gateway = ProcessGateway.builder()
                .gatewayId("GW-PROC-ONBOARD-002")
                .name("Parallel split")
                .gatewayType("PARALLEL")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(gateway.getGatewayId().startsWith("GW-"),
                "gatewayId must follow pattern GW-{processId}-{seq}");
    }
}
