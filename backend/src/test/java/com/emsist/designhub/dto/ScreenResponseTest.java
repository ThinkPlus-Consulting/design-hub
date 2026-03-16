package com.emsist.designhub.dto;

import com.emsist.designhub.domain.Gap;
import com.emsist.designhub.domain.Screen;
import com.emsist.designhub.domain.Status;
import com.emsist.designhub.domain.UserStory;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

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
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-01")
                .label("Object Type List/Grid")
                .module("R04")
                .storyRefs(List.of("LEGACY-SHOULD-NOT-WIN"))
                .deliveredByStories(List.of(deliveredStory))
                .build();

        ScreenResponse response = ScreenResponse.from(
                screen,
                Map.of(),
                Map.of("US-DM-007", new UserStoryResponse("US-DM-007", "View object types", "R04", "definitions", "US-DM-007", 1))
        );

        assertEquals(List.of("US-DM-007"), response.storyRefs());
        assertEquals(1, response.stories().size());
        assertEquals("US-DM-007", response.stories().get(0).storyId());
    }
}
