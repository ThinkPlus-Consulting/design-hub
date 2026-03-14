package com.emsist.designhub.service;

import org.junit.jupiter.api.Test;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class SchemaValidatorServiceTest {

    private final SchemaValidatorService validator = new SchemaValidatorService();

    @Test
    void shouldAcceptValidUserStoryCandidate() {
        var candidate = Map.<String, Object>of(
                "storyId", "US-SCR-042",
                "label", "As a user...",
                "module", "SCR",
                "domain", "Screen Management"
        );
        var result = validator.validate("UserStory", candidate);
        assertTrue(result.isValid());
    }

    @Test
    void shouldRejectCandidateWithMissingRequiredId() {
        var candidate = Map.<String, Object>of(
                "label", "As a user..."
        );
        var result = validator.validate("UserStory", candidate);
        assertFalse(result.isValid());
        assertTrue(result.getErrors().stream()
                .anyMatch(e -> e.contains("storyId")));
    }

    @Test
    void shouldRejectUnknownArtifactType() {
        var candidate = Map.<String, Object>of("id", "X-001");
        var result = validator.validate("UnknownType", candidate);
        assertFalse(result.isValid());
    }
}
