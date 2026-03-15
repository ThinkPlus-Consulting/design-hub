package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SpineTraversalTest {

    @Test
    void shouldTraverseBacklogSpine() {
        // Portfolio → Epic → Feature → UserStory
        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001").label("Login with email").module("auth").build();

        Feature feat = Feature.builder()
                .featureId("FEAT-AUTH-001").title("Login Flow")
                .status(Status.APPROVED).stories(List.of(story)).build();

        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001").title("Authentication")
                .status(Status.APPROVED).features(List.of(feat)).build();

        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001").name("Design Hub Backlog")
                .status(Status.APPROVED).epics(List.of(epic)).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .portfolio(portfolio).build();

        // Traverse: Project → Portfolio → Epic → Feature → Story
        String resolvedStoryId = project.getPortfolio()
                .getEpics().get(0)
                .getFeatures().get(0)
                .getStories().get(0)
                .getStoryId();
        assertEquals("US-AUTH-001", resolvedStoryId);
    }

    @Test
    void shouldTraverseExecutionSpine() {
        // Project → Task (canonical) + Milestone → Task (scheduling)
        CodeAsset asset = CodeAsset.builder()
                .codeAssetId("CA-CMP-BACKEND-001")
                .filePath("src/main/java/Screen.java")
                .assetType("SOURCE").status(Status.IMPLEMENTED).build();

        Task task = Task.builder()
                .taskId("TSK-AUTH-001").title("Implement login endpoint")
                .taskType("BACKEND").status(Status.IN_IMPLEMENTATION)
                .implementsAssets(List.of(asset)).build();

        Milestone sprint = Milestone.builder()
                .milestoneId("MS-DH-001").name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT).status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task)).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task)).milestones(List.of(sprint)).build();

        // Canonical: Project → Task → CodeAsset
        String assetId = project.getTasks().get(0)
                .getImplementsAssets().get(0).getCodeAssetId();
        assertEquals("CA-CMP-BACKEND-001", assetId);

        // Scheduling: Project → Milestone → Task
        String scheduledTaskId = project.getMilestones().get(0)
                .getTasks().get(0).getTaskId();
        assertEquals("TSK-AUTH-001", scheduledTaskId);
    }

    @Test
    void shouldTraverseAssessmentSpine() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001").gapType("CAPABILITY_GAP")
                .severity("HIGH").status(Status.IDENTIFIED).build();

        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-CAP-001").name("Auth Maturity")
                .assessmentType(AssessmentType.CAPABILITY).targetKind(TargetKind.CAP)
                .assessmentDate(LocalDate.of(2026, 3, 15)).assessor("arch-agent")
                .status(Status.DEFINED).identifiedGaps(List.of(gap)).build();

        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-AUTH").name("Authentication")
                .status(Status.DEFINED).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .targetCapabilities(List.of(cap))
                .addressedGaps(List.of(gap)).build();

        // Assessment → Gap ← Project (converge on the same gap)
        assertEquals("GAP-CAP-AUTH-001",
                assessment.getIdentifiedGaps().get(0).getGapId());
        assertEquals("GAP-CAP-AUTH-001",
                project.getAddressedGaps().get(0).getGapId());
        assertEquals("CAP-AUTH",
                project.getTargetCapabilities().get(0).getCapabilityId());
    }

    @Test
    void shouldSupportApplicationEdgeVariants() {
        Application ownedApp = Application.builder()
                .applicationId("APP-DH").name("Design Hub")
                .status(Status.IN_IMPLEMENTATION).build();

        Application externalApp = Application.builder()
                .applicationId("APP-KEYCLOAK").name("Keycloak")
                .status(Status.IMPLEMENTED).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .enhancedApplications(List.of(ownedApp))
                .integratedApplications(List.of(externalApp))
                .build();

        assertEquals("APP-DH", project.getEnhancedApplications().get(0).getApplicationId());
        assertEquals("APP-KEYCLOAK", project.getIntegratedApplications().get(0).getApplicationId());
    }
}
