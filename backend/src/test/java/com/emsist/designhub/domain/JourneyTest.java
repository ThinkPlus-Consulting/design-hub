package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class JourneyTest {

    @Test
    void shouldHoldBothLegacyAndUniversalStatus() {
        Journey journey = Journey.builder()
                .journeyId("JRN-TEST")
                .title("Test Journey")
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.APPROVED)
                .build();

        // Legacy fields preserved
        assertEquals("COMPLETE", journey.getDesignStatus());
        assertEquals("NOT_STARTED", journey.getPrototypeStatus());
        assertEquals("NOT_STARTED", journey.getDeliveryStatus());
        // New universal status
        assertEquals(Status.APPROVED, journey.getStatus());
    }

    @Test
    void shouldLinkToPersonaViaPerformedByPersona() {
        Persona persona = Persona.builder()
                .personaId("PER-ADMIN")
                .name("Platform Administrator")
                .status(Status.DEFINED)
                .build();

        Journey journey = Journey.builder()
                .journeyId("JRN-ADMIN-ONBOARD")
                .title("Admin Onboarding")
                .status(Status.DEFINED)
                .performedByPersona(persona)
                .steps(List.of())
                .build();

        assertNotNull(journey.getPerformedByPersona());
        assertEquals("PER-ADMIN", journey.getPerformedByPersona().getPersonaId());
    }

    @Test
    void shouldRetainLegacyRoleKeyDuringMigrationWindow() {
        Journey journey = Journey.builder()
                .journeyId("JRN-ROLE-COMPAT")
                .title("Legacy Role Compatibility")
                .roleKey("ADMIN")
                .status(Status.DEFINED)
                .build();

        assertEquals("ADMIN", journey.getRoleKey());
    }
}
