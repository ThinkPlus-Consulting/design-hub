package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MilestoneTest {

    @Test
    void shouldBuildMilestoneWithRequiredFields() {
        Milestone milestone = Milestone.builder()
                .milestoneId("MS-DH-001")
                .name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT)
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals("MS-DH-001", milestone.getMilestoneId());
        assertEquals("Sprint 1", milestone.getName());
        assertEquals(MilestoneType.SPRINT, milestone.getMilestoneType());
    }

    @Test
    void shouldSupportAllMilestoneTypes() {
        for (MilestoneType type : MilestoneType.values()) {
            Milestone ms = Milestone.builder()
                    .milestoneId("MS-TEST-" + type.name())
                    .name(type.name() + " milestone")
                    .milestoneType(type)
                    .status(Status.IDENTIFIED)
                    .build();
            assertEquals(type, ms.getMilestoneType());
        }
    }

    @Test
    void shouldAttachTasksViaHasTask() {
        Task task = Task.builder()
                .taskId("TSK-AUTH-001")
                .title("Implement login endpoint")
                .taskType("BACKEND")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        Milestone milestone = Milestone.builder()
                .milestoneId("MS-DH-001")
                .name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT)
                .status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task))
                .build();

        assertEquals(1, milestone.getTasks().size());
        assertEquals("TSK-AUTH-001", milestone.getTasks().get(0).getTaskId());
    }

    @Test
    void shouldSupportOptionalDates() {
        Milestone milestone = Milestone.builder()
                .milestoneId("MS-DH-002")
                .name("Phase 1 Release")
                .milestoneType(MilestoneType.RELEASE_CUT)
                .startDate(LocalDate.of(2026, 3, 15))
                .endDate(LocalDate.of(2026, 4, 15))
                .status(Status.APPROVED)
                .build();

        assertEquals(LocalDate.of(2026, 3, 15), milestone.getStartDate());
        assertEquals(LocalDate.of(2026, 4, 15), milestone.getEndDate());
    }

    @Test
    void shouldFollowIdPattern() {
        Milestone milestone = Milestone.builder()
                .milestoneId("MS-PLAT-003")
                .name("Checkpoint 3")
                .milestoneType(MilestoneType.CHECKPOINT)
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(milestone.getMilestoneId().startsWith("MS-"),
                "milestoneId must follow pattern MS-{projectCode}-{seq}");
    }
}
