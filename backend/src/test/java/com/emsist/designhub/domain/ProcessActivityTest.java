package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProcessActivityTest {

    @Test
    void shouldBuildProcessActivityWithRequiredFields() {
        ProcessActivity activity = ProcessActivity.builder()
                .activityId("ACT-PROC-SCREEN-REVIEW-001")
                .name("Review screen design")
                .activityType("TASK")
                .actionType("REVIEW")
                .taskNature("USER")
                .status(Status.DEFINED)
                .build();

        assertEquals("ACT-PROC-SCREEN-REVIEW-001", activity.getActivityId());
        assertEquals("TASK", activity.getActivityType());
        assertEquals("REVIEW", activity.getActionType());
        assertEquals("USER", activity.getTaskNature());
    }

    @Test
    void shouldFollowActivityIdPattern() {
        ProcessActivity activity = ProcessActivity.builder()
                .activityId("ACT-PROC-ONBOARD-002")
                .name("Submit form")
                .activityType("TASK")
                .actionType("SUBMIT")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(activity.getActivityId().startsWith("ACT-"),
                "activityId must follow pattern ACT-{processId}-{seq}");
    }

    @Test
    void shouldSupportSubprocessType() {
        ProcessActivity activity = ProcessActivity.builder()
                .activityId("ACT-PROC-MAIN-003")
                .name("Execute sub-review")
                .activityType("SUBPROCESS")
                .actionType("REVIEW")
                .status(Status.DEFINED)
                .build();

        assertEquals("SUBPROCESS", activity.getActivityType());
    }
}
