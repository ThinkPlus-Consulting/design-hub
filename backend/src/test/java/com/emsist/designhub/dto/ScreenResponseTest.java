package com.emsist.designhub.dto;

import com.emsist.designhub.domain.BusinessRole;
import com.emsist.designhub.domain.Gap;
import com.emsist.designhub.domain.Screen;
import com.emsist.designhub.domain.Status;
import com.emsist.designhub.domain.UserStory;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ScreenResponseTest {

    @Test
    void shouldMapStatusToString() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-TEST")
                .label("Test Screen")
                .module("Test")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        ScreenResponse response = ScreenResponse.from(screen);

        assertEquals("IN_IMPLEMENTATION", response.status());
        // Legacy fields still present
        assertEquals("COMPLETE", response.designStatus());
        assertEquals("PROTOTYPED", response.prototypeStatus());
        assertEquals("INTEGRATED", response.deliveryStatus());
    }

    @Test
    void shouldMapNullStatusToNull() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-TEST")
                .label("Test Screen")
                .module("Test")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .build();

        ScreenResponse response = ScreenResponse.from(screen);

        assertNull(response.status());
    }

    @Test
    void shouldMapGapWithNewFields() {
        Gap gap = Gap.builder()
                .gapId("GAP-SCR-TEST-01")
                .gapType("MISSING_RULE")
                .severity("MEDIUM")
                .description("Missing validation")
                .status(Status.IDENTIFIED)
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-TEST")
                .label("Test Screen")
                .module("Test")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .gaps(List.of(gap))
                .build();

        ScreenResponse response = ScreenResponse.from(screen);

        assertEquals(1, response.gaps().size());
        ScreenResponse.GapResponse gapResponse = response.gaps().get(0);
        assertEquals("GAP-SCR-TEST-01", gapResponse.gapId());
        assertEquals("MISSING_RULE", gapResponse.gapType());
        assertEquals("MEDIUM", gapResponse.severity());
        assertEquals("Missing validation", gapResponse.description());
    }

    @Test
    void shouldHandleNullGapsList() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-TEST")
                .label("Test Screen")
                .module("Test")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .build();

        ScreenResponse response = ScreenResponse.from(screen);

        assertNotNull(response.gaps());
        assertTrue(response.gaps().isEmpty());
    }

    @Test
    void shouldPreferDeliveredStoriesWhenGraphEdgesExist() {
        UserStory deliveredStory = UserStory.builder()
                .storyId("US-DM-007")
                .label("View object types")
                .module("R04")
                .domain("definitions")
                .storyNumber("US-DM-007")
                .externalWorkflowState("READY")
                .externalPriority("High")
                .externalOwner("designer@example.com")
                .externalLabels(List.of("catalog"))
                .externalRefs(List.of("jira:US-DM-007"))
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-01")
                .label("Object Type List/Grid")
                .module("R04")
                .storyRefs(List.of("LEGACY-SHOULD-NOT-WIN"))
                .deliveredByStories(List.of(deliveredStory))
                .build();

        ScreenResponse response = ScreenResponse.from(screen);

        assertEquals(List.of("US-DM-007"), response.storyRefs());
        assertEquals(1, response.stories().size());
        assertEquals("US-DM-007", response.stories().get(0).storyId());
        assertEquals("READY", response.stories().get(0).externalWorkflowState());
        assertEquals(List.of("catalog"), response.stories().get(0).externalLabels());
    }

    @Test
    void shouldPreferAccessibleRolesWhenGraphEdgesExist() {
        BusinessRole role = BusinessRole.builder()
                .roleKey("ADMIN")
                .displayName("Administrator")
                .roleGroup("platform")
                .sortOrder(1)
                .status(Status.APPROVED)
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-ROLE")
                .label("Role-aware screen")
                .roleKeys(List.of("LEGACY-ROLE"))
                .accessibleByRoles(List.of(role))
                .build();

        ScreenResponse response = ScreenResponse.from(screen);

        assertEquals(List.of("ADMIN"), response.roleKeys());
        assertEquals(1, response.roles().size());
        assertEquals("ADMIN", response.roles().get(0).roleKey());
        assertEquals("Administrator", response.roles().get(0).displayName());
    }

    @Test
    void shouldNotProjectLegacyStoryOrRoleObjectsWhenGraphEdgesAreAbsent() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-LEGACY")
                .label("Legacy compatibility only")
                .storyRefs(List.of("US-LEGACY-001"))
                .roleKeys(List.of("ADMIN"))
                .build();

        ScreenResponse response = ScreenResponse.from(screen);

        assertEquals(List.of("US-LEGACY-001"), response.storyRefs());
        assertTrue(response.stories().isEmpty());
        assertEquals(List.of("ADMIN"), response.roleKeys());
        assertTrue(response.roles().isEmpty());
    }
}
