package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PersonaTest {

    @Test
    void shouldBuildPersonaWithRequiredFields() {
        Persona persona = Persona.builder()
                .personaId("PER-ADMIN")
                .name("Platform Administrator")
                .status(Status.DEFINED)
                .build();

        assertEquals("PER-ADMIN", persona.getPersonaId());
        assertEquals("Platform Administrator", persona.getName());
        assertEquals(Status.DEFINED, persona.getStatus());
    }

    @Test
    void shouldSupportOptionalAttributes() {
        Persona persona = Persona.builder()
                .personaId("PER-DESIGNER")
                .name("Agent Designer")
                .summary("Designs and configures AI agents")
                .goals(List.of("Create effective agents", "Monitor agent performance"))
                .painPoints(List.of("Complex configuration", "Limited visibility"))
                .roleKeys(List.of("AGENT_DESIGNER", "ARCHITECT"))
                .sourceRefs(List.of("CONSOLIDATED-STORY-INVENTORY.md"))
                .status(Status.DEFINED)
                .build();

        assertEquals("Designs and configures AI agents", persona.getSummary());
        assertEquals(2, persona.getGoals().size());
        assertEquals(2, persona.getPainPoints().size());
        assertEquals(2, persona.getRoleKeys().size());
        assertEquals(1, persona.getSourceRefs().size());
    }

    @Test
    void shouldFollowIdPattern() {
        Persona persona = Persona.builder()
                .personaId("PER-VIEWER")
                .name("Read-Only Viewer")
                .status(Status.DEFINED)
                .build();

        assertTrue(persona.getPersonaId().startsWith("PER-"));
    }
}
