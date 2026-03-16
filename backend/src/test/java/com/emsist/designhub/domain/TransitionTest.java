package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class TransitionTest {

    @Test
    void builderSetsRequiredFields() {
        Transition transition = Transition.builder()
                .transitionId("TRN-SCR-AUTH-TO-DASH")
                .name("Login success redirect")
                .transitionType("NAVIGATION")
                .status(Status.DEFINED)
                .build();

        assertThat(transition.getTransitionId()).isEqualTo("TRN-SCR-AUTH-TO-DASH");
        assertThat(transition.getName()).isEqualTo("Login success redirect");
        assertThat(transition.getTransitionType()).isEqualTo("NAVIGATION");
        assertThat(transition.getStatus()).isEqualTo(Status.DEFINED);
    }

    @Test
    void transitionIdFollowsNamingPattern() {
        Transition transition = Transition.builder()
                .transitionId("TRN-SCR-AUTH-TO-DASH")
                .name("Login success redirect")
                .transitionType("NAVIGATION")
                .guard("authenticated == true")
                .status(Status.DEFINED)
                .build();

        assertThat(transition.getTransitionId()).startsWith("TRN-");
        assertThat(transition.getGuard()).isEqualTo("authenticated == true");
    }

    @Test
    void screenAndInteractionRelationshipsAreSettable() {
        Screen from = Screen.builder().surfaceId("SCR-AUTH").label("Login").build();
        Screen to = Screen.builder().surfaceId("SCR-DASHBOARD").label("Dashboard").build();
        Interaction trigger = Interaction.builder()
                .interactionId("INT-G-001")
                .element("Submit login")
                .build();

        Transition transition = Transition.builder()
                .transitionId("TRN-SCR-AUTH-TO-DASH")
                .name("Login success redirect")
                .transitionType("NAVIGATION")
                .fromScreen(from)
                .toScreen(to)
                .causedByInteraction(trigger)
                .status(Status.DEFINED)
                .build();

        assertThat(transition.getFromScreen().getSurfaceId()).isEqualTo("SCR-AUTH");
        assertThat(transition.getToScreen().getSurfaceId()).isEqualTo("SCR-DASHBOARD");
        assertThat(transition.getCausedByInteraction().getInteractionId()).isEqualTo("INT-G-001");
    }
}
