package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InformationFlowTest {

    @Test
    void shouldBuildInformationFlowWithStubFields() {
        InformationFlow informationFlow = InformationFlow.builder()
                .flowId("IFL-001")
                .name("Design sync flow")
                .status(Status.DEFINED)
                .build();

        assertEquals("IFL-001", informationFlow.getFlowId());
        assertEquals("Design sync flow", informationFlow.getName());
        assertEquals(Status.DEFINED, informationFlow.getStatus());
    }

    @Test
    void shouldFollowInformationFlowIdPattern() {
        InformationFlow informationFlow = InformationFlow.builder()
                .flowId("IFL-032")
                .name("Import export pipeline")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(informationFlow.getFlowId().startsWith("IFL-"),
                "flowId must follow pattern IFL-{seq}");
    }
}
