package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AcceptanceCriterionTest {

    @Test
    void shouldBuildAcceptanceCriterionWithRequiredFields() {
        AcceptanceCriterion criterion = AcceptanceCriterion.builder()
                .criterionId("AC-US-DM-007-001")
                .description("Delete agent requires explicit confirmation")
                .givenWhenThen("Given an existing agent, when delete is selected, then a confirmation dialog is shown")
                .status(Status.DEFINED)
                .build();

        assertEquals("AC-US-DM-007-001", criterion.getCriterionId());
        assertEquals("Delete agent requires explicit confirmation", criterion.getDescription());
        assertEquals(Status.DEFINED, criterion.getStatus());
    }

    @Test
    void shouldFollowCriterionIdPattern() {
        AcceptanceCriterion criterion = AcceptanceCriterion.builder()
                .criterionId("AC-US-AI-139-001")
                .description("Generated criterion")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(criterion.getCriterionId().startsWith("AC-"),
                "criterionId must follow pattern AC-{storyId}-{seq}");
    }
}
