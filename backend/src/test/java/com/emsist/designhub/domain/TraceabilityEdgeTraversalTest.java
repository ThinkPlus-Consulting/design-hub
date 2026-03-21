package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TraceabilityEdgeTraversalTest {

    @Test
    void shouldTraverseExternalArtifactToRepresentedStory() {
        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001")
                .label("User can sign in")
                .module("core")
                .build();

        ExternalArtifact externalArtifact = ExternalArtifact.builder()
                .externalId("EXT-JIRA-001")
                .system("JIRA")
                .externalType("STORY")
                .key("DH-101")
                .status(Status.DEFINED)
                .representsStories(List.of(story))
                .build();

        assertEquals("US-AUTH-001", externalArtifact.getRepresentsStories().get(0).getStoryId());
    }

    @Test
    void shouldTraverseExternalArtifactToRepresentedBug() {
        Bug bug = Bug.builder()
                .bugId("BUG-001")
                .summary("Session refresh banner stays visible after login retry")
                .severity("HIGH")
                .status(Status.IDENTIFIED)
                .build();

        ExternalArtifact externalArtifact = ExternalArtifact.builder()
                .externalId("EXT-AZURE_DEVOPS-001")
                .system("AZURE_DEVOPS")
                .externalType("BUG")
                .key("AB#245")
                .status(Status.DEFINED)
                .representsBugs(List.of(bug))
                .build();

        assertEquals("BUG-001", externalArtifact.getRepresentsBugs().get(0).getBugId());
    }

    @Test
    void shouldTraverseExternalArtifactHierarchyAndDependencies() {
        ExternalArtifact parent = ExternalArtifact.builder()
                .externalId("EXT-JIRA-EPIC-001")
                .system("JIRA")
                .externalType("EPIC")
                .key("DH-100")
                .status(Status.DEFINED)
                .build();

        ExternalArtifact dependency = ExternalArtifact.builder()
                .externalId("EXT-AZDO-001")
                .system("AZURE_DEVOPS")
                .externalType("BUG")
                .key("AB#245")
                .status(Status.DEFINED)
                .build();

        ExternalArtifact story = ExternalArtifact.builder()
                .externalId("EXT-JIRA-001")
                .system("JIRA")
                .externalType("STORY")
                .key("DH-101")
                .status(Status.DEFINED)
                .parents(List.of(parent))
                .dependencies(List.of(dependency))
                .build();

        assertEquals("EXT-JIRA-EPIC-001", story.getParents().get(0).getExternalId());
        assertEquals("EXT-AZDO-001", story.getDependencies().get(0).getExternalId());
    }

    @Test
    void shouldTraverseUserStoryToSourceReference() {
        SourceReference sourceReference = SourceReference.builder()
                .sourceId("SRC-US-AUTH-001")
                .artifactPath("Documentation/.Requirements/CONSOLIDATED-STORY-INVENTORY.md")
                .status(Status.DEFINED)
                .build();

        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001")
                .label("User can sign in")
                .module("core")
                .sourceReferences(List.of(sourceReference))
                .build();

        assertEquals("SRC-US-AUTH-001", story.getSourceReferences().get(0).getSourceId());
    }

    @Test
    void shouldTraverseBugToAffectedScreen() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-AUTH")
                .label("Login / Sign In")
                .status(Status.DEFINED)
                .build();

        Bug bug = Bug.builder()
                .bugId("BUG-001")
                .summary("Session refresh banner stays visible after login retry")
                .severity("HIGH")
                .status(Status.IDENTIFIED)
                .affectsScreens(List.of(screen))
                .build();

        assertEquals("SCR-AUTH", bug.getAffectsScreens().get(0).getSurfaceId());
    }

    @Test
    void shouldTraverseScreenToSourceReference() {
        SourceReference sourceReference = SourceReference.builder()
                .sourceId("SRC-SCR-AUTH-001")
                .artifactPath("documentation/vision-benchmark.md")
                .status(Status.DEFINED)
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-AUTH")
                .label("Login / Sign In")
                .status(Status.DEFINED)
                .sourceReferences(List.of(sourceReference))
                .build();

        assertEquals("SRC-SCR-AUTH-001", screen.getSourceReferences().get(0).getSourceId());
    }
}
