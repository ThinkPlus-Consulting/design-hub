package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EpicTest {

    @Test
    void shouldBuildEpicWithRequiredFields() {
        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication & Authorization")
                .status(Status.APPROVED)
                .build();

        assertEquals("EPIC-AUTH-001", epic.getEpicId());
        assertEquals("Authentication & Authorization", epic.getTitle());
        assertEquals(Status.APPROVED, epic.getStatus());
    }

    @Test
    void shouldAttachFeaturesViaHasFeature() {
        Feature feature = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication & Authorization")
                .status(Status.APPROVED)
                .features(List.of(feature))
                .build();

        assertEquals(1, epic.getFeatures().size());
        assertEquals("FEAT-AUTH-001", epic.getFeatures().get(0).getFeatureId());
    }

    @Test
    void shouldFollowIdPattern() {
        Epic epic = Epic.builder()
                .epicId("EPIC-GRAPH-002")
                .title("Graph Intelligence")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(epic.getEpicId().startsWith("EPIC-"),
                "epicId must follow pattern EPIC-{module}-{seq}");
    }
}
