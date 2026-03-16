package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScreenFlowTraversalTest {

    @Test
    void shouldTraverseScreenStateBelongsToScreen() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-AUTH")
                .label("Login / Sign In")
                .build();

        ScreenState state = ScreenState.builder()
                .stateId("STATE-SCR-AUTH-EMPTY")
                .name("Empty credentials")
                .stateType("EMPTY")
                .belongsToScreen(screen)
                .status(Status.DEFINED)
                .build();

        assertThat(state.getBelongsToScreen()).isNotNull();
        assertThat(state.getBelongsToScreen().getSurfaceId()).isEqualTo("SCR-AUTH");
    }

    @Test
    void shouldTraverseTransitionFromScreen() {
        Screen from = Screen.builder().surfaceId("SCR-AUTH").label("Login").build();

        Transition transition = Transition.builder()
                .transitionId("TRN-SCR-AUTH-TO-DASH")
                .name("Login success redirect")
                .fromScreen(from)
                .status(Status.DEFINED)
                .build();

        assertThat(transition.getFromScreen()).isNotNull();
        assertThat(transition.getFromScreen().getSurfaceId()).isEqualTo("SCR-AUTH");
    }

    @Test
    void shouldTraverseTransitionToScreen() {
        Screen to = Screen.builder().surfaceId("SCR-DASHBOARD").label("Dashboard").build();

        Transition transition = Transition.builder()
                .transitionId("TRN-SCR-AUTH-TO-DASH")
                .name("Login success redirect")
                .toScreen(to)
                .status(Status.DEFINED)
                .build();

        assertThat(transition.getToScreen()).isNotNull();
        assertThat(transition.getToScreen().getSurfaceId()).isEqualTo("SCR-DASHBOARD");
    }

    @Test
    void shouldTraverseTransitionCausedByInteraction() {
        Interaction trigger = Interaction.builder()
                .interactionId("INT-G-001")
                .element("Submit login")
                .trigger("CLICK")
                .build();

        Transition transition = Transition.builder()
                .transitionId("TRN-SCR-AUTH-TO-DASH")
                .name("Login success redirect")
                .causedByInteraction(trigger)
                .status(Status.DEFINED)
                .build();

        assertThat(transition.getCausedByInteraction()).isNotNull();
        assertThat(transition.getCausedByInteraction().getInteractionId()).isEqualTo("INT-G-001");
    }

    @Test
    void shouldTraverseFullScreenFlow() {
        // Screen → ScreenState (via BELONGS_TO_SCREEN inverse)
        // Transition → FROM_SCREEN → Screen, TO_SCREEN → Screen, CAUSED_BY_INTERACTION → Interaction
        Screen authScreen = Screen.builder().surfaceId("SCR-AUTH").label("Login").build();
        Screen dashScreen = Screen.builder().surfaceId("SCR-DASHBOARD").label("Dashboard").build();

        Interaction loginClick = Interaction.builder()
                .interactionId("INT-G-001")
                .element("Submit login")
                .trigger("CLICK")
                .build();

        ScreenState emptyState = ScreenState.builder()
                .stateId("STATE-SCR-AUTH-EMPTY")
                .name("Empty credentials")
                .stateType("EMPTY")
                .belongsToScreen(authScreen)
                .status(Status.DEFINED)
                .build();

        ScreenState loadingState = ScreenState.builder()
                .stateId("STATE-SCR-AUTH-LOADING")
                .name("Authenticating")
                .stateType("LOADING")
                .belongsToScreen(authScreen)
                .status(Status.DEFINED)
                .build();

        Transition loginTransition = Transition.builder()
                .transitionId("TRN-SCR-AUTH-TO-DASH")
                .name("Login success redirect")
                .transitionType("NAVIGATION")
                .guard("authenticated == true")
                .fromScreen(authScreen)
                .toScreen(dashScreen)
                .causedByInteraction(loginClick)
                .status(Status.DEFINED)
                .build();

        // Full traversal assertions
        assertThat(emptyState.getBelongsToScreen().getSurfaceId()).isEqualTo("SCR-AUTH");
        assertThat(loadingState.getBelongsToScreen().getSurfaceId()).isEqualTo("SCR-AUTH");
        assertThat(loginTransition.getFromScreen().getSurfaceId()).isEqualTo("SCR-AUTH");
        assertThat(loginTransition.getToScreen().getSurfaceId()).isEqualTo("SCR-DASHBOARD");
        assertThat(loginTransition.getCausedByInteraction().getInteractionId()).isEqualTo("INT-G-001");
    }
}
