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

    @Test
    void shouldTraverseApiContractToRequestSchema() {
        RequestSchema request = RequestSchema.builder()
                .schemaId("REQ-API-AGT-001")
                .contentType("application/json")
                .status(Status.DEFINED)
                .build();

        ApiContract contract = ApiContract.builder()
                .contractId("API-AGT-001")
                .path("/api/agents")
                .method("POST")
                .status(Status.DEFINED)
                .requestSchemas(List.of(request))
                .build();

        assertEquals("REQ-API-AGT-001", contract.getRequestSchemas().get(0).getSchemaId());
    }

    @Test
    void shouldTraverseApiContractToResponseSchema() {
        ResponseSchema response = ResponseSchema.builder()
                .schemaId("RES-API-AGT-001")
                .contentType("application/json")
                .statusCode(200)
                .status(Status.DEFINED)
                .build();

        ApiContract contract = ApiContract.builder()
                .contractId("API-AGT-001")
                .path("/api/agents")
                .method("GET")
                .status(Status.DEFINED)
                .responseSchemas(List.of(response))
                .build();

        assertEquals("RES-API-AGT-001", contract.getResponseSchemas().get(0).getSchemaId());
    }

    @Test
    void shouldTraverseApiContractToErrorContract() {
        ErrorContract error = ErrorContract.builder()
                .errorContractId("EC-API-AGT-001")
                .httpStatus(404)
                .errorCode("AGENT_NOT_FOUND")
                .status(Status.DEFINED)
                .build();

        ApiContract contract = ApiContract.builder()
                .contractId("API-AGT-001")
                .path("/api/agents/{id}")
                .method("GET")
                .status(Status.DEFINED)
                .errorContracts(List.of(error))
                .build();

        assertEquals("EC-API-AGT-001", contract.getErrorContracts().get(0).getErrorContractId());
    }

    @Test
    void shouldTraverseScreenToValidationRule() {
        ValidationRule rule = ValidationRule.builder()
                .validationRuleId("VR-AGENT-001")
                .fieldPath("agentName")
                .validationType("REQUIRED")
                .expression("notBlank()")
                .errorMessage("Agent name is required")
                .status(Status.DEFINED)
                .build();

        Screen screen = Screen.builder()
                .surfaceId("SCR-AGT-CREATE")
                .label("Create Agent")
                .status(Status.IN_IMPLEMENTATION)
                .validationRules(List.of(rule))
                .build();

        assertEquals("VR-AGENT-001", screen.getValidationRules().get(0).getValidationRuleId());
    }

    @Test
    void shouldTraverseRuleToValidationRule() {
        ValidationRule valRule = ValidationRule.builder()
                .validationRuleId("VR-NAMING-001")
                .fieldPath("name")
                .validationType("PATTERN")
                .expression("^[A-Za-z][A-Za-z0-9_-]{2,50}$")
                .errorMessage("Name must be alphanumeric, 3-50 chars")
                .status(Status.DEFINED)
                .build();

        Rule rule = Rule.builder()
                .ruleId("RULE-NAMING-001")
                .name("Agent naming convention")
                .ruleType("VALIDATION")
                .status(Status.DEFINED)
                .validationRules(List.of(valRule))
                .build();

        assertEquals("VR-NAMING-001", rule.getValidationRules().get(0).getValidationRuleId());
    }
}
