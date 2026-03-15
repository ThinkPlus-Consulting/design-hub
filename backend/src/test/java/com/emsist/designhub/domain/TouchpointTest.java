package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TouchpointTest {

    @Test
    void shouldLinkToPersonaViaUsedByPersona() {
        Persona persona = Persona.builder()
                .personaId("PER-DESIGNER").name("Designer").status(Status.DEFINED).build();

        Touchpoint tp = Touchpoint.builder()
                .touchpointId("TP-WEB-001")
                .label("Web Entry")
                .usedByPersonas(List.of(persona))
                .build();

        assertEquals(1, tp.getUsedByPersonas().size());
        assertEquals("PER-DESIGNER", tp.getUsedByPersonas().get(0).getPersonaId());
    }

    @Test
    void shouldLinkToChannelViaDeliveredViaChannel() {
        Channel channel = Channel.builder()
                .channelCode("CH-WEB-DSK").displayName("Web Desktop").channelType("WEB").build();

        Touchpoint tp = Touchpoint.builder()
                .touchpointId("TP-WEB-001")
                .label("Web Entry")
                .deliveredViaChannel(channel)
                .build();

        assertNotNull(tp.getDeliveredViaChannel());
        assertEquals("CH-WEB-DSK", tp.getDeliveredViaChannel().getChannelCode());
    }

    @Test
    void shouldLinkToBusinessRoleViaAccessibleByRole() {
        BusinessRole role = BusinessRole.builder()
                .roleKey("ADMIN")
                .displayName("Administrator")
                .roleGroup("tenant")
                .sortOrder(1)
                .status(Status.DEFINED)
                .build();

        Touchpoint tp = Touchpoint.builder()
                .touchpointId("TP-WEB-001")
                .label("Web Entry")
                .accessibleByRoles(List.of(role))
                .build();

        assertEquals(1, tp.getAccessibleByRoles().size());
        assertEquals("ADMIN", tp.getAccessibleByRoles().get(0).getRoleKey());
    }
}
