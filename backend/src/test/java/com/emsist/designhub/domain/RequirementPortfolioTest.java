package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RequirementPortfolioTest {

    @Test
    void shouldBuildPortfolioWithRequiredFields() {
        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .build();

        assertEquals("RPORT-DH-001", portfolio.getPortfolioId());
        assertEquals("Design Hub Backlog", portfolio.getName());
        assertEquals(Status.APPROVED, portfolio.getStatus());
    }

    @Test
    void shouldAttachEpicsViaHasEpic() {
        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication")
                .status(Status.APPROVED)
                .build();

        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .epics(List.of(epic))
                .build();

        assertEquals(1, portfolio.getEpics().size());
        assertEquals("EPIC-AUTH-001", portfolio.getEpics().get(0).getEpicId());
    }

    @Test
    void shouldTraversePortfolioToStory() {
        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001")
                .label("Login with email")
                .module("auth")
                .build();

        Feature feature = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.APPROVED)
                .stories(List.of(story))
                .build();

        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication")
                .status(Status.APPROVED)
                .features(List.of(feature))
                .build();

        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .epics(List.of(epic))
                .build();

        String storyId = portfolio.getEpics().get(0)
                .getFeatures().get(0)
                .getStories().get(0)
                .getStoryId();

        assertEquals("US-AUTH-001", storyId);
    }

    @Test
    void shouldFollowIdPattern() {
        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-PLATFORM-001")
                .name("Platform Backlog")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(portfolio.getPortfolioId().startsWith("RPORT-"),
                "portfolioId must follow pattern RPORT-{code}-{seq}");
    }
}
