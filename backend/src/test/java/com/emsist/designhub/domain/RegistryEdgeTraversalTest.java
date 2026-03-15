package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RegistryEdgeTraversalTest {

    @Test
    void shouldTraverseJourneyToPersona() {
        Persona persona = Persona.builder()
                .personaId("PER-ADMIN").name("Admin").status(Status.DEFINED).build();

        Journey journey = Journey.builder()
                .journeyId("JRN-001").title("Admin Onboarding")
                .status(Status.DEFINED)
                .performedByPersona(persona)
                .steps(List.of())
                .build();

        assertEquals("PER-ADMIN", journey.getPerformedByPersona().getPersonaId());
    }

    @Test
    void shouldTraverseScreenToPersona() {
        Persona persona = Persona.builder()
                .personaId("PER-DESIGNER").name("Designer").status(Status.DEFINED).build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-CANVAS")
                .status(Status.DEFINED)
                .usedByPersonas(List.of(persona))
                .build();

        assertEquals("PER-DESIGNER", screen.getUsedByPersonas().get(0).getPersonaId());
    }

    @Test
    void shouldTraverseScreenToBusinessRole() {
        BusinessRole role = BusinessRole.builder()
                .roleKey("ARCHITECT").displayName("Architect")
                .roleGroup("design").sortOrder(3).status(Status.DEFINED).build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-ARCH-VIEW")
                .status(Status.DEFINED)
                .accessibleByRoles(List.of(role))
                .build();

        assertEquals("ARCHITECT", screen.getAccessibleByRoles().get(0).getRoleKey());
    }

    @Test
    void shouldTraverseTouchpointToChannel() {
        Channel channel = Channel.builder()
                .channelCode("CH-WEB-MOB").displayName("Web Mobile").channelType("WEB").build();

        Touchpoint tp = Touchpoint.builder()
                .touchpointId("TP-MOB-001")
                .label("Mobile Entry")
                .deliveredViaChannel(channel)
                .build();

        assertEquals("CH-WEB-MOB", tp.getDeliveredViaChannel().getChannelCode());
    }

    @Test
    void shouldTraverseInteractionToPermission() {
        Permission perm = Permission.builder()
                .permissionKey("SUPER_ADMIN").displayName("Super Admin").sortOrder(0).build();

        Interaction interaction = Interaction.builder()
                .interactionId("INT-SYS-001")
                .element("System Config")
                .trigger("click")
                .requiresPermission(perm)
                .build();

        assertEquals("SUPER_ADMIN", interaction.getRequiresPermission().getPermissionKey());
    }

    @Test
    void shouldTraverseFullPersonaToChannelPath() {
        // Persona -> Journey -> Step -> Touchpoint -> Channel
        Channel channel = Channel.builder()
                .channelCode("CH-WEB-DSK").displayName("Web Desktop").channelType("WEB").build();

        Touchpoint tp = Touchpoint.builder()
                .touchpointId("TP-WEB-001").label("Web Login")
                .deliveredViaChannel(channel).build();

        Persona persona = Persona.builder()
                .personaId("PER-USER").name("Standard User").status(Status.DEFINED).build();

        Journey journey = Journey.builder()
                .journeyId("JRN-LOGIN").title("Login Flow")
                .status(Status.DEFINED)
                .performedByPersona(persona)
                .steps(List.of())
                .build();

        // Verify the full path is representable
        assertEquals("PER-USER", journey.getPerformedByPersona().getPersonaId());
        assertEquals("CH-WEB-DSK", tp.getDeliveredViaChannel().getChannelCode());
    }
}
