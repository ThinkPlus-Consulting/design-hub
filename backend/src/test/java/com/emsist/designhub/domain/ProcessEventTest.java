package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessEventTest {

    @Test
    void shouldBuildProcessEventWithRequiredFields() {
        ProcessEvent event = ProcessEvent.builder()
                .eventId("EVT-PROC-REVIEW-001")
                .name("Review started")
                .eventPosition("START")
                .eventTrigger("NONE")
                .isInterrupting(true)
                .status(Status.DEFINED)
                .build();

        assertEquals("EVT-PROC-REVIEW-001", event.getEventId());
        assertEquals("START", event.getEventPosition());
        assertEquals("NONE", event.getEventTrigger());
        assertTrue(event.isInterrupting());
    }

    @Test
    void shouldFollowEventIdPattern() {
        ProcessEvent event = ProcessEvent.builder()
                .eventId("EVT-PROC-ONBOARD-002")
                .name("Timeout")
                .eventPosition("BOUNDARY")
                .eventTrigger("TIMER")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(event.getEventId().startsWith("EVT-"),
                "eventId must follow pattern EVT-{processId}-{seq}");
    }
}
