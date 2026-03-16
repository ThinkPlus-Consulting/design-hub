package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DataFieldTest {

    @Test
    void shouldBuildDataFieldWithRequiredFields() {
        DataField field = DataField.builder()
                .fieldId("DF-DE-AGENT-001")
                .name("agentName")
                .dataType("STRING")
                .required(true)
                .constraints("maxLength=120")
                .status(Status.DEFINED)
                .build();

        assertEquals("DF-DE-AGENT-001", field.getFieldId());
        assertEquals("agentName", field.getName());
        assertTrue(field.isRequired());
        assertEquals(Status.DEFINED, field.getStatus());
    }

    @Test
    void shouldFollowFieldIdPattern() {
        DataField field = DataField.builder()
                .fieldId("DF-DE-AGENT-002")
                .name("status")
                .dataType("ENUM")
                .required(false)
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(field.getFieldId().startsWith("DF-"),
                "fieldId must follow pattern DF-{entityId}-{seq}");
    }
}
