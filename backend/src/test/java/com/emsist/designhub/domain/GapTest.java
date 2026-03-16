package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GapTest {

    @Test
    void shouldBuildWithNewFields() {
        Gap gap = Gap.builder()
                .gapId("GAP-SCR-AUTH-01")
                .gapType("MISSING_RULE")
                .severity("HIGH")
                .description("Missing error handling")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("GAP-SCR-AUTH-01", gap.getGapId());
        assertEquals("MISSING_RULE", gap.getGapType());
        assertEquals("HIGH", gap.getSeverity());
        assertEquals("Missing error handling", gap.getDescription());
        assertEquals(Status.IDENTIFIED, gap.getStatus());
    }

    @Test
    void shouldAllowNullDescription() {
        Gap gap = Gap.builder()
                .gapId("GAP-SCR-01-01")
                .gapType("MISSING_ARTIFACT")
                .severity("MEDIUM")
                .status(Status.IDENTIFIED)
                .build();

        assertNull(gap.getDescription());
    }

    @Test
    void gapIdShouldFollowPattern() {
        Gap gap = Gap.builder()
                .gapId("GAP-SCR-DASH-01")
                .gapType("MISSING_ATTRIBUTE")
                .severity("LOW")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(gap.getGapId().startsWith("GAP-"));
    }
}
