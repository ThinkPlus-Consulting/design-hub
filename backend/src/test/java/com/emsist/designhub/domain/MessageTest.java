package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MessageTest {

    @Test
    void shouldBuildMessageWithRequiredFields() {
        Message message = Message.builder()
                .messageId("MSG-AGT-001")
                .messageText("Agent deleted successfully.")
                .messageType("SUCCESS")
                .severity("LOW")
                .status(Status.DEFINED)
                .build();

        assertEquals("MSG-AGT-001", message.getMessageId());
        assertEquals("SUCCESS", message.getMessageType());
        assertEquals(Status.DEFINED, message.getStatus());
    }

    @Test
    void shouldFollowMessageIdPattern() {
        Message message = Message.builder()
                .messageId("MSG-CORE-001")
                .messageText("Validation failed.")
                .messageType("VALIDATION")
                .severity("MEDIUM")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(message.getMessageId().startsWith("MSG-"),
                "messageId must follow pattern MSG-{module}-{seq}");
    }
}
