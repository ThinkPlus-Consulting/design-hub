package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BusinessProcessTest {

    @Test
    void shouldBuildBusinessProcessWithRequiredFields() {
        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-SCREEN-REVIEW")
                .name("Screen Review Process")
                .description("End-to-end screen design review")
                .status(Status.DEFINED)
                .build();

        assertEquals("PROC-SCREEN-REVIEW", process.getProcessId());
        assertEquals("Screen Review Process", process.getName());
        assertEquals(Status.DEFINED, process.getStatus());
    }

    @Test
    void shouldFollowProcessIdPattern() {
        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-ONBOARD")
                .name("Onboarding")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(process.getProcessId().startsWith("PROC-"),
                "processId must follow pattern PROC-{code}");
    }

    @Test
    void shouldSupportDiagramAttributes() {
        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-SCREEN-REVIEW")
                .name("Screen Review Process")
                .diagramFormat("BPMN_XML")
                .diagramPath("/diagrams/screen-review.bpmn")
                .diagramVersion("1.0.0")
                .diagramSource("BPMN_IO")
                .isExecutableModel(false)
                .status(Status.DEFINED)
                .build();

        assertEquals("BPMN_XML", process.getDiagramFormat());
        assertEquals("BPMN_IO", process.getDiagramSource());
        assertFalse(process.isExecutableModel());
    }
}
