package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BugTest {

    @Test
    void shouldBuildBugWithStubFields() {
        Bug bug = Bug.builder()
                .bugId("BUG-001")
                .externalKey("AB#245")
                .summary("Filter reset leaves stale selection state")
                .severity("HIGH")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("BUG-001", bug.getBugId());
        assertEquals("AB#245", bug.getExternalKey());
        assertEquals("Filter reset leaves stale selection state", bug.getSummary());
        assertEquals("HIGH", bug.getSeverity());
        assertEquals(Status.IDENTIFIED, bug.getStatus());
    }

    @Test
    void shouldFollowBugIdPattern() {
        Bug bug = Bug.builder()
                .bugId("BUG-404")
                .summary("Screen detail panel does not refresh")
                .status(Status.DEFINED)
                .build();

        assertTrue(bug.getBugId().startsWith("BUG-"),
                "bugId must follow pattern BUG-{seq}");
    }

    @Test
    void shouldLinkBugToAffectedScreenAndSource() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-AUTH")
                .label("Login / Sign In")
                .status(Status.DEFINED)
                .build();

        SourceReference sourceReference = SourceReference.builder()
                .sourceId("SRC-BUG-001")
                .artifactPath("Documentation/governance/ai-discussions/discussion-20260301-155248.md")
                .status(Status.DEFINED)
                .build();

        Bug bug = Bug.builder()
                .bugId("BUG-001")
                .summary("Session refresh banner stays visible after login retry")
                .severity("HIGH")
                .status(Status.IDENTIFIED)
                .affectsScreens(List.of(screen))
                .sourceReferences(List.of(sourceReference))
                .build();

        assertEquals("SCR-AUTH", bug.getAffectsScreens().get(0).getSurfaceId());
        assertEquals("SRC-BUG-001", bug.getSourceReferences().get(0).getSourceId());
    }
}
