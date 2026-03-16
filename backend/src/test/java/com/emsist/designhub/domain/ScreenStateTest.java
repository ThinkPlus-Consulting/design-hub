package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class ScreenStateTest {

    @Test
    void builderSetsRequiredFields() {
        ScreenState state = ScreenState.builder()
                .stateId("STATE-SCR-AUTH-EMPTY")
                .name("Empty credentials")
                .stateType("EMPTY")
                .status(Status.DEFINED)
                .build();

        assertThat(state.getStateId()).isEqualTo("STATE-SCR-AUTH-EMPTY");
        assertThat(state.getName()).isEqualTo("Empty credentials");
        assertThat(state.getStateType()).isEqualTo("EMPTY");
        assertThat(state.getStatus()).isEqualTo(Status.DEFINED);
    }

    @Test
    void stateIdFollowsNamingPattern() {
        ScreenState state = ScreenState.builder()
                .stateId("STATE-SCR-AUTH-LOADING")
                .name("Loading auth")
                .stateType("LOADING")
                .entryCondition("User submits login form")
                .exitCondition("API response received")
                .status(Status.DEFINED)
                .build();

        assertThat(state.getStateId()).startsWith("STATE-");
        assertThat(state.getEntryCondition()).isNotNull();
        assertThat(state.getExitCondition()).isNotNull();
    }

    @Test
    void belongsToScreenRelationshipIsSettable() {
        Screen screen = Screen.builder().surfaceId("SCR-AUTH").label("Login").build();
        ScreenState state = ScreenState.builder()
                .stateId("STATE-SCR-AUTH-ERROR")
                .name("Auth error")
                .stateType("ERROR")
                .belongsToScreen(screen)
                .status(Status.DEFINED)
                .build();

        assertThat(state.getBelongsToScreen()).isNotNull();
        assertThat(state.getBelongsToScreen().getSurfaceId()).isEqualTo("SCR-AUTH");
    }
}
