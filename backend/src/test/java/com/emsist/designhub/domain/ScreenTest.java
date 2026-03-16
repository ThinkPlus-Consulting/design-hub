package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ScreenTest {

    @Test
    void shouldHoldBothLegacyAndUniversalStatus() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-TEST")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        // Legacy fields preserved
        assertEquals("COMPLETE", screen.getDesignStatus());
        assertEquals("PROTOTYPED", screen.getPrototypeStatus());
        assertEquals("INTEGRATED", screen.getDeliveryStatus());
        // New universal status
        assertEquals(Status.IN_IMPLEMENTATION, screen.getStatus());
    }

    @Test
    void shouldLinkToPersonaViaUsedByPersona() {
        Persona persona = Persona.builder()
                .personaId("PER-ADMIN").name("Admin").status(Status.DEFINED).build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-DASH")
                .status(Status.DEFINED)
                .usedByPersonas(List.of(persona))
                .build();

        assertEquals(1, screen.getUsedByPersonas().size());
        assertEquals("PER-ADMIN", screen.getUsedByPersonas().get(0).getPersonaId());
    }

    @Test
    void shouldLinkToBusinessRoleViaAccessibleByRole() {
        BusinessRole role = BusinessRole.builder()
                .roleKey("ADMIN").displayName("Administrator")
                .roleGroup("tenant").sortOrder(2).status(Status.DEFINED).build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-SETTINGS")
                .status(Status.DEFINED)
                .accessibleByRoles(List.of(role))
                .build();

        assertEquals(1, screen.getAccessibleByRoles().size());
        assertEquals("ADMIN", screen.getAccessibleByRoles().get(0).getRoleKey());
    }

    @Test
    void shouldLinkToErrorCodeViaCanProduceError() {
        ErrorCode errorCode = ErrorCode.builder()
                .code("AGT-E-404")
                .severity("ERROR")
                .messageText("Agent could not be found.")
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-AGT-LIST")
                .status(Status.DEFINED)
                .canProduceErrors(List.of(errorCode))
                .build();

        assertEquals(1, screen.getCanProduceErrors().size());
        assertEquals("AGT-E-404", screen.getCanProduceErrors().get(0).getCode());
    }

    @Test
    void shouldLinkToSourceReferenceViaHasSource() {
        SourceReference sourceReference = SourceReference.builder()
                .sourceId("SRC-SCR-AUTH-001")
                .artifactPath("documentation/vision-benchmark.md")
                .section("Query 8")
                .lineRef("401-410")
                .status(Status.DEFINED)
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-AUTH")
                .status(Status.DEFINED)
                .sourceReferences(List.of(sourceReference))
                .build();

        assertEquals(1, screen.getSourceReferences().size());
        assertEquals("SRC-SCR-AUTH-001", screen.getSourceReferences().get(0).getSourceId());
    }

    @Test
    void shouldLinkToInteractionViaHasInteraction() {
        Interaction interaction = Interaction.builder()
                .interactionId("INT-G-002")
                .surfaceId("SURF-HEADER")
                .element("Global search input")
                .trigger("type")
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SURF-HEADER")
                .status(Status.DEFINED)
                .interactions(List.of(interaction))
                .build();

        assertEquals(1, screen.getInteractions().size());
        assertEquals("INT-G-002", screen.getInteractions().get(0).getInteractionId());
    }

    @Test
    void shouldLinkToDeliveredStoriesViaIncomingDelivers() {
        UserStory story = UserStory.builder()
                .storyId("US-DM-007")
                .label("View object types")
                .module("R04")
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-01")
                .status(Status.DEFINED)
                .deliveredByStories(List.of(story))
                .build();

        assertEquals(1, screen.getDeliveredByStories().size());
        assertEquals("US-DM-007", screen.getDeliveredByStories().get(0).getStoryId());
    }
}
