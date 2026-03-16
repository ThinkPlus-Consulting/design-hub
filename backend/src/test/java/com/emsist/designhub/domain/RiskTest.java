package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RiskTest {

    @Test
    void shouldBuildRiskWithStubFields() {
        Risk risk = Risk.builder()
                .riskId("RSK-001")
                .title("Documentation drift can outpace code changes")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("RSK-001", risk.getRiskId());
        assertEquals("Documentation drift can outpace code changes", risk.getTitle());
        assertEquals(Status.IDENTIFIED, risk.getStatus());
    }

    @Test
    void shouldFollowRiskIdPattern() {
        Risk risk = Risk.builder()
                .riskId("RSK-030")
                .title("Seed data can mask runtime gaps")
                .status(Status.DEFINED)
                .build();

        assertTrue(risk.getRiskId().startsWith("RSK-"),
                "riskId must follow pattern RSK-{seq}");
    }
}
