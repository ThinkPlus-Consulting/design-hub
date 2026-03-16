package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DecisionTest {

    @Test
    void shouldBuildDecisionWithStubFields() {
        Decision decision = Decision.builder()
                .decisionId("DEC-001")
                .title("Adopt typed graph edges")
                .status(Status.APPROVED)
                .build();

        assertEquals("DEC-001", decision.getDecisionId());
        assertEquals("Adopt typed graph edges", decision.getTitle());
        assertEquals(Status.APPROVED, decision.getStatus());
    }

    @Test
    void shouldFollowDecisionIdPattern() {
        Decision decision = Decision.builder()
                .decisionId("DEC-042")
                .title("Prefer direct node identities")
                .status(Status.DEFINED)
                .build();

        assertTrue(decision.getDecisionId().startsWith("DEC-"),
                "decisionId must follow pattern DEC-{seq}");
    }
}
