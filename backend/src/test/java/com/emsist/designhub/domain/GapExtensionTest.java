package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class GapExtensionTest {

    @Test
    void shouldSupportCapabilityGapType() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001")
                .gapType("CAPABILITY_GAP")
                .severity("HIGH")
                .description("Authentication capability lacks MFA support")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("CAPABILITY_GAP", gap.getGapType());
        assertEquals("HIGH", gap.getSeverity());
    }

    @Test
    void shouldSupportProcessGapType() {
        Gap gap = Gap.builder()
                .gapId("GAP-PROC-ONBOARD-001")
                .gapType("PROCESS_GAP")
                .severity("MEDIUM")
                .description("Onboarding process has no automated verification step")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("PROCESS_GAP", gap.getGapType());
    }

    @Test
    void shouldRetainExistingGapTypes() {
        for (String gapType : new String[]{
                "MISSING_ARTIFACT",
                "MISSING_RELATIONSHIP",
                "MISSING_ATTRIBUTE",
                "MISSING_RULE",
                "CAPABILITY_GAP",
                "PROCESS_GAP"}) {
            Gap gap = Gap.builder()
                    .gapId("GAP-TEST-" + gapType)
                    .gapType(gapType)
                    .severity("LOW")
                    .status(Status.IDENTIFIED)
                    .build();

            assertEquals(gapType, gap.getGapType());
        }
    }
}
