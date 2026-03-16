package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void shouldPopulateBoundaryEventRelationships() {
        ProcessActivity hostActivity = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-002")
                .name("Review screen design")
                .activityType("TASK")
                .actionType("REVIEW")
                .status(Status.DEFINED)
                .build();

        ProcessActivity escalationStep = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-003")
                .name("Escalate review")
                .activityType("TASK")
                .actionType("NOTIFY")
                .status(Status.DEFINED)
                .build();

        ProcessEvent event = ProcessEvent.builder()
                .eventId("EVT-PROC-REVIEW-002")
                .name("Review timeout")
                .eventPosition("BOUNDARY")
                .eventTrigger("TIMER")
                .attachedToRef("ACT-PROC-REVIEW-002")
                .status(Status.DEFINED)
                .attachedTo(List.of(hostActivity))
                .flowsToActivities(List.of(escalationStep))
                .build();

        assertEquals("ACT-PROC-REVIEW-002", event.getAttachedTo().get(0).getActivityId());
        assertEquals("ACT-PROC-REVIEW-003", event.getFlowsToActivities().get(0).getActivityId());
    }
}
