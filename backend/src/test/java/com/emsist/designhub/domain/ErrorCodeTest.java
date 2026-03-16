package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorCodeTest {

    @Test
    void shouldBuildErrorCodeWithRequiredFields() {
        ErrorCode errorCode = ErrorCode.builder()
                .code("AUTH-E-401")
                .severity("ERROR")
                .messageText("Session refresh failed.")
                .triggerCondition("Session refresh API returns 401")
                .resolutionHint("Prompt the user to sign in again.")
                .build();

        assertEquals("AUTH-E-401", errorCode.getCode());
        assertEquals("ERROR", errorCode.getSeverity());
        assertEquals("Session refresh failed.", errorCode.getMessageText());
    }

    @Test
    void shouldFollowErrorCodePattern() {
        ErrorCode errorCode = ErrorCode.builder()
                .code("AGT-E-404")
                .severity("ERROR")
                .messageText("Agent could not be found.")
                .build();

        assertTrue(errorCode.getCode().contains("-E-"),
                "ErrorCode must follow pattern {domain}-E-{seq}");
    }
}
