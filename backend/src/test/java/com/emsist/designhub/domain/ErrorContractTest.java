package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorContractTest {

    @Test
    void shouldBuildErrorContractWithRequiredFields() {
        ErrorContract contract = ErrorContract.builder()
                .errorContractId("EC-API-AGT-001")
                .httpStatus(404)
                .errorCode("AGENT_NOT_FOUND")
                .description("The requested agent does not exist")
                .status(Status.DEFINED)
                .build();

        assertEquals("EC-API-AGT-001", contract.getErrorContractId());
        assertEquals(404, contract.getHttpStatus());
        assertEquals("AGENT_NOT_FOUND", contract.getErrorCode());
        assertEquals(Status.DEFINED, contract.getStatus());
    }

    @Test
    void shouldFollowErrorContractIdPattern() {
        ErrorContract contract = ErrorContract.builder()
                .errorContractId("EC-API-AGT-002")
                .httpStatus(409)
                .errorCode("AGENT_CONFLICT")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(contract.getErrorContractId().startsWith("EC-"),
                "errorContractId must follow pattern EC-{contractId}-{seq}");
    }
}
