package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class FeatureTest {

    @Test
    void shouldBuildFeatureWithRequiredFields() {
        Feature feature = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals("FEAT-AUTH-001", feature.getFeatureId());
        assertEquals("Login Flow", feature.getTitle());
    }

    @Test
    void shouldAttachStoriesViaHasStory() {
        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001")
                .label("User can log in with email")
                .module("auth")
                .build();

        Feature feature = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.APPROVED)
                .stories(List.of(story))
                .build();

        assertEquals(1, feature.getStories().size());
        assertEquals("US-AUTH-001", feature.getStories().get(0).getStoryId());
    }

    @Test
    void shouldSupportFeatureWithoutStories() {
        Feature feature = Feature.builder()
                .featureId("FEAT-GRAPH-001")
                .title("Graph Traversal View")
                .status(Status.IDENTIFIED)
                .build();

        assertNull(feature.getStories());
    }
}
