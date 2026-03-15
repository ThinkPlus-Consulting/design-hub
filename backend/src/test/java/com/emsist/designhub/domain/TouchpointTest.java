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
}
