package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProjectInstanceTest {

    @Test
    void shouldBuildProjectWithRequiredFields() {
        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals("PROJ-DH-001", project.getProjectId());
        assertEquals("Design Hub v1", project.getName());
        assertEquals("ENHANCEMENT", project.getProjectType());
    }

    @Test
    void shouldSupportAllProjectTypes() {
        for (String type : new String[]{"GREENFIELD", "ENHANCEMENT", "MIGRATION", "INTEGRATION"}) {
            ProjectInstance proj = ProjectInstance.builder()
                    .projectId("PROJ-TEST-" + type)
                    .name(type + " project")
                    .projectType(type)
                    .status(Status.IDENTIFIED)
                    .build();
            assertEquals(type, proj.getProjectType());
        }
    }

    @Test
    void shouldTargetCapabilities() {
        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-AUTH")
                .name("Authentication")
                .status(Status.DEFINED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .targetCapabilities(List.of(cap))
                .build();

        assertEquals(1, project.getTargetCapabilities().size());
        assertEquals("CAP-AUTH", project.getTargetCapabilities().get(0).getCapabilityId());
    }

    @Test
    void shouldAddressGaps() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001")
                .gapType("CAPABILITY_GAP")
                .severity("HIGH")
                .status(Status.IDENTIFIED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .addressedGaps(List.of(gap))
                .build();

        assertEquals(1, project.getAddressedGaps().size());
        assertEquals("CAPABILITY_GAP", project.getAddressedGaps().get(0).getGapType());
    }

    @Test
    void shouldOwnPortfolio() {
        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .portfolio(portfolio)
                .build();

        assertNotNull(project.getPortfolio());
        assertEquals("RPORT-DH-001", project.getPortfolio().getPortfolioId());
    }

    @Test
    void shouldOwnTasksCanonically() {
        Task task = Task.builder()
                .taskId("TSK-AUTH-001")
                .title("Implement login")
                .taskType("BACKEND")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task))
                .build();

        assertEquals(1, project.getTasks().size());
    }

    @Test
    void shouldOwnMilestones() {
        Milestone ms = Milestone.builder()
                .milestoneId("MS-DH-001")
                .name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT)
                .status(Status.IN_IMPLEMENTATION)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .milestones(List.of(ms))
                .build();

        assertEquals(1, project.getMilestones().size());
    }

    @Test
    void shouldLinkToApplicationsWithThreeEdgeTypes() {
        Application newApp = Application.builder()
                .applicationId("APP-NEW")
                .name("New Service")
                .status(Status.IDENTIFIED)
                .build();

        Application existingApp = Application.builder()
                .applicationId("APP-DH")
                .name("Design Hub")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        Application externalApp = Application.builder()
                .applicationId("APP-KEYCLOAK")
                .name("Keycloak")
                .status(Status.IMPLEMENTED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .createdApplications(List.of(newApp))
                .enhancedApplications(List.of(existingApp))
                .integratedApplications(List.of(externalApp))
                .build();

        assertEquals(1, project.getCreatedApplications().size());
        assertEquals("APP-NEW", project.getCreatedApplications().get(0).getApplicationId());
        assertEquals(1, project.getEnhancedApplications().size());
        assertEquals("APP-DH", project.getEnhancedApplications().get(0).getApplicationId());
        assertEquals(1, project.getIntegratedApplications().size());
        assertEquals("APP-KEYCLOAK", project.getIntegratedApplications().get(0).getApplicationId());
    }

    @Test
    void shouldLinkToComponentsWithCreateAndEnhanceEdges() {
        ApplicationComponent newComp = ApplicationComponent.builder()
                .componentId("CMP-NEW-001")
                .name("New Backend Service")
                .status(Status.IDENTIFIED)
                .build();

        ApplicationComponent existingComp = ApplicationComponent.builder()
                .componentId("CMP-DH-BACKEND")
                .name("Design Hub Backend")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .createdComponents(List.of(newComp))
                .enhancedComponents(List.of(existingComp))
                .build();

        assertEquals(1, project.getCreatedComponents().size());
        assertEquals("CMP-NEW-001", project.getCreatedComponents().get(0).getComponentId());
        assertEquals(1, project.getEnhancedComponents().size());
        assertEquals("CMP-DH-BACKEND", project.getEnhancedComponents().get(0).getComponentId());
    }

    @Test
    void shouldSupportOptionalDates() {
        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .startDate(LocalDate.of(2026, 1, 1))
                .targetDate(LocalDate.of(2026, 6, 30))
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals(LocalDate.of(2026, 1, 1), project.getStartDate());
        assertEquals(LocalDate.of(2026, 6, 30), project.getTargetDate());
    }
}
