package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JourneyStepTest {

    @Test
    void shouldLinkToInteractionViaExecutesInteraction() {
        Interaction interaction = Interaction.builder()
                .interactionId("INT-R05-GALLERY-002")
                .element("Category filter")
                .trigger("click")
                .build();

        JourneyStep step = JourneyStep.builder()
                .stepId("JRN-R05-001.02")
                .label("Filter templates by category")
                .interactionRef("INT-R05-GALLERY-002")
                .executesInteraction(interaction)
                .orderIndex(1)
                .build();

        assertNotNull(step.getExecutesInteraction());
        assertEquals("INT-R05-GALLERY-002", step.getExecutesInteraction().getInteractionId());
        assertEquals("INT-R05-GALLERY-002", step.getInteractionRef());
    }

    @Test
    void shouldLinkToScreenViaUsesScreen() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-AGT-GALLERY")
                .label("Agent Gallery")
                .status(Status.DEFINED)
                .build();

        JourneyStep step = JourneyStep.builder()
                .stepId("JRN-R05-001.01")
                .label("Navigate to Gallery via dock")
                .usesScreen(screen)
                .orderIndex(0)
                .build();

        assertNotNull(step.getUsesScreen());
        assertEquals("SCR-AGT-GALLERY", step.getUsesScreen().getSurfaceId());
    }

    @Test
    void shouldLinkToTouchpointViaStartsAtTouchpoint() {
        Touchpoint touchpoint = Touchpoint.builder()
                .touchpointId("TP-GALLERY-MENU")
                .label("Gallery menu entry")
                .build();

        JourneyStep step = JourneyStep.builder()
                .stepId("JRN-R05-001.01")
                .label("Navigate to Gallery via dock")
                .startsAtTouchpoint(touchpoint)
                .orderIndex(0)
                .build();

        assertNotNull(step.getStartsAtTouchpoint());
        assertEquals("TP-GALLERY-MENU", step.getStartsAtTouchpoint().getTouchpointId());
    }
}
