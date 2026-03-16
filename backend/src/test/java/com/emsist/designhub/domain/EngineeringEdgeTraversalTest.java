package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class EngineeringEdgeTraversalTest {

    @Test
    void shouldTraverseUserStoryToAcceptanceCriterion() {
        AcceptanceCriterion criterion = AcceptanceCriterion.builder()
                .criterionId("AC-US-AI-139-001")
                .description("Publishing an agent requires confirmation")
                .status(Status.DEFINED)
                .build();

        UserStory story = UserStory.builder()
                .storyId("US-AI-139")
                .label("Publish agent")
                .module("ai")
                .criteria(List.of(criterion))
                .build();

        assertEquals("AC-US-AI-139-001", story.getCriteria().get(0).getCriterionId());
    }

    @Test
    void shouldTraverseDataEntityToDataField() {
        DataField field = DataField.builder()
                .fieldId("DF-DE-AGENT-001")
                .name("agentName")
                .dataType("STRING")
                .required(true)
                .status(Status.DEFINED)
                .build();

        DataEntity entity = DataEntity.builder()
                .entityId("DE-AGENT")
                .name("Agent")
                .entityType("CONFIGURATION")
                .status(Status.DEFINED)
                .fields(List.of(field))
                .build();

        assertEquals("DF-DE-AGENT-001", entity.getFields().get(0).getFieldId());
    }

    @Test
    void shouldTraverseScreenToMessage() {
        Message message = Message.builder()
                .messageId("MSG-AI-001")
                .messageText("Agent published successfully.")
                .messageType("SUCCESS")
                .severity("LOW")
                .status(Status.DEFINED)
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-AGT-LIST")
                .label("Agent List")
                .status(Status.IN_IMPLEMENTATION)
                .messages(List.of(message))
                .build();

        assertEquals("MSG-AI-001", screen.getMessages().get(0).getMessageId());
    }
}
