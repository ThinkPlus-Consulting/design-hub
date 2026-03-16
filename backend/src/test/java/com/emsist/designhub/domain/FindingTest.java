package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FindingTest {

    @Test
    void shouldBuildFindingWithStubFields() {
        Finding finding = Finding.builder()
                .findingId("FND-001")
                .summary("Screen graph still has residual string references")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("FND-001", finding.getFindingId());
        assertEquals("Screen graph still has residual string references", finding.getSummary());
        assertEquals(Status.IDENTIFIED, finding.getStatus());
    }

    @Test
    void shouldFollowFindingIdPattern() {
        Finding finding = Finding.builder()
                .findingId("FND-021")
                .summary("Process spine now enables BPMN-aligned traversal")
                .status(Status.DEFINED)
                .build();

        assertTrue(finding.getFindingId().startsWith("FND-"),
                "findingId must follow pattern FND-{seq}");
    }
}
