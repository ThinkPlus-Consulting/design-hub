package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class BugTest {

    @Test
    void shouldBuildBugWithStubFields() {
        Bug bug = Bug.builder()
                .bugId("BUG-001")
                .summary("Filter reset leaves stale selection state")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("BUG-001", bug.getBugId());
        assertEquals("Filter reset leaves stale selection state", bug.getSummary());
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
}
