package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ProcessSpineTraversalTest {

    @Test
    void shouldTraverseBusinessDomainToCapability() {
        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-SCREEN-MGMT")
                .name("Screen Management")
                .status(Status.DEFINED)
                .build();

        BusinessDomain domain = BusinessDomain.builder()
                .domainCode("DOM-DESIGN")
                .name("Design Management")
                .activeStatus("ACTIVE")
                .capabilities(List.of(cap))
                .build();

        assertEquals("CAP-SCREEN-MGMT", domain.getCapabilities().get(0).getCapabilityId());
    }

    @Test
    void shouldTraverseCapabilityToProcess() {
        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-SCREEN-REVIEW")
                .name("Screen Review Process")
                .status(Status.DEFINED)
                .build();

        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-SCREEN-MGMT")
                .name("Screen Management")
                .status(Status.DEFINED)
                .realizedByProcesses(List.of(process))
                .build();

        assertEquals("PROC-SCREEN-REVIEW", cap.getRealizedByProcesses().get(0).getProcessId());
    }

    @Test
    void shouldTraverseProcessToActivity() {
        ProcessActivity activity = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-001")
                .name("Review screen design")
                .activityType("TASK")
                .actionType("REVIEW")
                .status(Status.DEFINED)
                .build();

        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-SCREEN-REVIEW")
                .name("Screen Review Process")
                .status(Status.DEFINED)
                .activities(List.of(activity))
                .build();

        assertEquals("ACT-PROC-REVIEW-001", process.getActivities().get(0).getActivityId());
    }

    @Test
    void shouldTraverseProcessToGateway() {
        ProcessGateway gateway = ProcessGateway.builder()
                .gatewayId("GW-PROC-REVIEW-001")
                .name("Review outcome")
                .gatewayType("EXCLUSIVE")
                .status(Status.DEFINED)
                .build();

        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-SCREEN-REVIEW")
                .name("Screen Review Process")
                .status(Status.DEFINED)
                .gateways(List.of(gateway))
                .build();

        assertEquals("GW-PROC-REVIEW-001", process.getGateways().get(0).getGatewayId());
    }

    @Test
    void shouldTraverseProcessToEvent() {
        ProcessEvent event = ProcessEvent.builder()
                .eventId("EVT-PROC-REVIEW-001")
                .name("Review started")
                .eventPosition("START")
                .eventTrigger("NONE")
                .status(Status.DEFINED)
                .build();

        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-SCREEN-REVIEW")
                .name("Screen Review Process")
                .status(Status.DEFINED)
                .events(List.of(event))
                .build();

        assertEquals("EVT-PROC-REVIEW-001", process.getEvents().get(0).getEventId());
    }

    @Test
    void shouldTraverseActivityFlowsToActivity() {
        ProcessActivity step2 = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-002")
                .name("Approve screen")
                .activityType("TASK")
                .actionType("APPROVE")
                .status(Status.DEFINED)
                .build();

        ProcessActivity step1 = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-001")
                .name("Review screen design")
                .activityType("TASK")
                .actionType("REVIEW")
                .status(Status.DEFINED)
                .flowsToActivities(List.of(step2))
                .build();

        assertEquals("ACT-PROC-REVIEW-002", step1.getFlowsToActivities().get(0).getActivityId());
    }

    @Test
    void shouldTraverseActivityExpandsToProcess() {
        BusinessProcess subProcess = BusinessProcess.builder()
                .processId("PROC-DETAIL-REVIEW")
                .name("Detailed Review Sub-Process")
                .status(Status.DEFINED)
                .build();

        ProcessActivity subprocess = ProcessActivity.builder()
                .activityId("ACT-PROC-MAIN-003")
                .name("Execute detailed review")
                .activityType("SUBPROCESS")
                .actionType("REVIEW")
                .status(Status.DEFINED)
                .expandsTo(List.of(subProcess))
                .build();

        assertEquals("PROC-DETAIL-REVIEW", subprocess.getExpandsTo().get(0).getProcessId());
    }

    @Test
    void shouldTraverseActivityCallsProcess() {
        BusinessProcess calledProcess = BusinessProcess.builder()
                .processId("PROC-NOTIFICATION")
                .name("Notification Process")
                .status(Status.DEFINED)
                .build();

        ProcessActivity callActivity = ProcessActivity.builder()
                .activityId("ACT-PROC-MAIN-004")
                .name("Send notifications")
                .activityType("CALL_ACTIVITY")
                .actionType("NOTIFY")
                .status(Status.DEFINED)
                .callsProcess(List.of(calledProcess))
                .build();

        assertEquals("PROC-NOTIFICATION", callActivity.getCallsProcess().get(0).getProcessId());
    }

    @Test
    void shouldTraverseGatewayFlowsToActivity() {
        ProcessActivity approve = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-003")
                .name("Approve screen")
                .activityType("TASK")
                .actionType("APPROVE")
                .status(Status.DEFINED)
                .build();

        ProcessGateway gateway = ProcessGateway.builder()
                .gatewayId("GW-PROC-REVIEW-001")
                .name("Review outcome")
                .gatewayType("EXCLUSIVE")
                .status(Status.DEFINED)
                .flowsToActivities(List.of(approve))
                .build();

        assertEquals("ACT-PROC-REVIEW-003", gateway.getFlowsToActivities().get(0).getActivityId());
    }

    @Test
    void shouldTraverseEventAttachedToActivity() {
        ProcessActivity review = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-002")
                .name("Review screen design")
                .activityType("TASK")
                .actionType("REVIEW")
                .status(Status.DEFINED)
                .build();

        ProcessEvent event = ProcessEvent.builder()
                .eventId("EVT-PROC-REVIEW-002")
                .name("Review timeout")
                .eventPosition("BOUNDARY")
                .eventTrigger("TIMER")
                .status(Status.DEFINED)
                .attachedTo(List.of(review))
                .build();

        assertEquals("ACT-PROC-REVIEW-002", event.getAttachedTo().get(0).getActivityId());
    }

    @Test
    void shouldTraverseEventFlowsToActivity() {
        ProcessActivity escalate = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-004")
                .name("Escalate review")
                .activityType("TASK")
                .actionType("NOTIFY")
                .status(Status.DEFINED)
                .build();

        ProcessEvent event = ProcessEvent.builder()
                .eventId("EVT-PROC-REVIEW-003")
                .name("Escalation trigger")
                .eventPosition("INTERMEDIATE_THROW")
                .eventTrigger("ESCALATION")
                .status(Status.DEFINED)
                .flowsToActivities(List.of(escalate))
                .build();

        assertEquals("ACT-PROC-REVIEW-004", event.getFlowsToActivities().get(0).getActivityId());
    }

    @Test
    void shouldTraverseUserStoryToTask() {
        Task task = Task.builder()
                .taskId("TASK-US-AUTH-001-001")
                .title("Implement login screen")
                .description("Create the login form and submission flow")
                .taskType("IMPLEMENTATION")
                .priority("HIGH")
                .status(Status.DEFINED)
                .build();

        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001")
                .label("User can sign in")
                .module("core")
                .domain("auth")
                .storyNumber("US-AUTH-001")
                .tasks(List.of(task))
                .build();

        assertEquals("TASK-US-AUTH-001-001", story.getTasks().get(0).getTaskId());
    }

    @Test
    void shouldTraverseFullProcessSpine() {
        // Build bottom-up: Activity → Process → Capability → Domain
        ProcessActivity activity = ProcessActivity.builder()
                .activityId("ACT-PROC-REVIEW-001")
                .name("Review screen design")
                .activityType("TASK")
                .actionType("REVIEW")
                .status(Status.DEFINED)
                .build();

        BusinessProcess process = BusinessProcess.builder()
                .processId("PROC-SCREEN-REVIEW")
                .name("Screen Review Process")
                .status(Status.DEFINED)
                .activities(List.of(activity))
                .build();

        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-SCREEN-MGMT")
                .name("Screen Management")
                .status(Status.DEFINED)
                .realizedByProcesses(List.of(process))
                .build();

        BusinessDomain domain = BusinessDomain.builder()
                .domainCode("DOM-DESIGN")
                .name("Design Management")
                .activeStatus("ACTIVE")
                .capabilities(List.of(cap))
                .build();

        // Full spine traversal: Domain → Capability → Process → Activity
        assertEquals("ACT-PROC-REVIEW-001",
                domain.getCapabilities().get(0)
                        .getRealizedByProcesses().get(0)
                        .getActivities().get(0)
                        .getActivityId());
    }
}
