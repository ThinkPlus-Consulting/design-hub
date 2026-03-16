package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConstraintTest {

    @Test
    void shouldBuildConstraintWithStubFields() {
        Constraint constraint = Constraint.builder()
                .constraintId("CON-001")
                .statement("The graph model must remain deterministic")
                .status(Status.DEFINED)
                .build();

        assertEquals("CON-001", constraint.getConstraintId());
        assertEquals("The graph model must remain deterministic", constraint.getStatement());
        assertEquals(Status.DEFINED, constraint.getStatus());
    }

    @Test
    void shouldFollowConstraintIdPattern() {
        Constraint constraint = Constraint.builder()
                .constraintId("CON-020")
                .statement("All runtime APIs stay backward-compatible")
                .status(Status.APPROVED)
                .build();

        assertTrue(constraint.getConstraintId().startsWith("CON-"),
                "constraintId must follow pattern CON-{seq}");
    }
}
