package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ValidationRuleTest {

    @Test
    void shouldBuildValidationRuleWithRequiredFields() {
        ValidationRule rule = ValidationRule.builder()
                .validationRuleId("VR-AGENT-001")
                .fieldPath("agentName")
                .validationType("REQUIRED")
                .expression("notBlank()")
                .errorMessage("Agent name is required")
                .status(Status.DEFINED)
                .build();

        assertEquals("VR-AGENT-001", rule.getValidationRuleId());
        assertEquals("agentName", rule.getFieldPath());
        assertEquals("REQUIRED", rule.getValidationType());
        assertEquals(Status.DEFINED, rule.getStatus());
    }

    @Test
    void shouldFollowValidationRuleIdPattern() {
        ValidationRule rule = ValidationRule.builder()
                .validationRuleId("VR-AGENT-002")
                .fieldPath("agentType")
                .validationType("PATTERN")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(rule.getValidationRuleId().startsWith("VR-"),
                "validationRuleId must follow pattern VR-{domain}-{seq}");
    }
}
