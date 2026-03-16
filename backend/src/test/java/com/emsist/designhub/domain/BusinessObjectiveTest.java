package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessObjectiveTest {

    @Test
    void shouldBuildBusinessObjectiveWithStubFields() {
        BusinessObjective objective = BusinessObjective.builder()
                .objectiveId("OBJ-DESIGN-001")
                .title("Improve design governance")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("OBJ-DESIGN-001", objective.getObjectiveId());
        assertEquals("Improve design governance", objective.getTitle());
        assertEquals(Status.IDENTIFIED, objective.getStatus());
    }

    @Test
    void shouldFollowObjectiveIdPattern() {
        BusinessObjective objective = BusinessObjective.builder()
                .objectiveId("OBJ-CORE-001")
                .title("Stabilize platform core")
                .status(Status.DEFINED)
                .build();

        assertTrue(objective.getObjectiveId().startsWith("OBJ-"),
                "objectiveId must follow pattern OBJ-{module}-{seq}");
    }
}
