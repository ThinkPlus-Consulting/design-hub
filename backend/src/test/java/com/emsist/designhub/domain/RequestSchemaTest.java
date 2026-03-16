package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequestSchemaTest {

    @Test
    void shouldBuildRequestSchemaWithRequiredFields() {
        RequestSchema schema = RequestSchema.builder()
                .schemaId("REQ-API-AGT-001")
                .contentType("application/json")
                .status(Status.DEFINED)
                .build();

        assertEquals("REQ-API-AGT-001", schema.getSchemaId());
        assertEquals("application/json", schema.getContentType());
        assertEquals(Status.DEFINED, schema.getStatus());
    }

    @Test
    void shouldFollowSchemaIdPattern() {
        RequestSchema schema = RequestSchema.builder()
                .schemaId("REQ-API-AGT-002")
                .contentType("multipart/form-data")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(schema.getSchemaId().startsWith("REQ-"),
                "schemaId must follow pattern REQ-{contractId}-{seq}");
    }
}
