package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AssumptionTest {

    @Test
    void shouldBuildAssumptionWithStubFields() {
        Assumption assumption = Assumption.builder()
                .assumptionId("ASM-001")
                .statement("Seed data remains stable across local runs")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("ASM-001", assumption.getAssumptionId());
        assertEquals("Seed data remains stable across local runs", assumption.getStatement());
        assertEquals(Status.IDENTIFIED, assumption.getStatus());
    }

    @Test
    void shouldFollowAssumptionIdPattern() {
        Assumption assumption = Assumption.builder()
                .assumptionId("ASM-015")
                .statement("Playwright uses the live backend")
                .status(Status.DEFINED)
                .build();

        assertTrue(assumption.getAssumptionId().startsWith("ASM-"),
                "assumptionId must follow pattern ASM-{seq}");
    }
}
