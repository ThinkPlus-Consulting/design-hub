package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResponseSchemaTest {

    @Test
    void shouldBuildResponseSchemaWithRequiredFields() {
        ResponseSchema schema = ResponseSchema.builder()
                .schemaId("RES-API-AGT-001")
                .contentType("application/json")
                .statusCode(200)
                .status(Status.DEFINED)
                .build();

        assertEquals("RES-API-AGT-001", schema.getSchemaId());
        assertEquals("application/json", schema.getContentType());
        assertEquals(200, schema.getStatusCode());
        assertEquals(Status.DEFINED, schema.getStatus());
    }

    @Test
    void shouldFollowSchemaIdPattern() {
        ResponseSchema schema = ResponseSchema.builder()
                .schemaId("RES-API-AGT-002")
                .contentType("application/json")
                .statusCode(404)
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(schema.getSchemaId().startsWith("RES-"),
                "schemaId must follow pattern RES-{contractId}-{seq}");
    }
}
