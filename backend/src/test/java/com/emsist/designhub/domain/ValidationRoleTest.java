package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ValidationRoleTest {

    @Test
    void shouldBuildValidationRoleWithRequiredFields() {
        ValidationRole role = ValidationRole.builder()
                .validationRoleKey("HITL_REVIEWER")
                .displayName("HITL Reviewer")
                .status(Status.DEFINED)
                .build();

        assertEquals("HITL_REVIEWER", role.getValidationRoleKey());
        assertEquals("HITL Reviewer", role.getDisplayName());
        assertEquals(Status.DEFINED, role.getStatus());
    }

    @Test
    void shouldSupportOptionalAttributes() {
        ValidationRole role = ValidationRole.builder()
                .validationRoleKey("AUDITOR")
                .displayName("Auditor")
                .scope("compliance")
                .status(Status.DEFINED)
                .sourceRefs(List.of("governance-framework.md"))
                .build();

        assertEquals("compliance", role.getScope());
        assertEquals(1, role.getSourceRefs().size());
    }

    @Test
    void shouldCoverAllValidationRoleKeys() {
        for (String key : new String[]{"HITL_REVIEWER", "AUDITOR"}) {
            ValidationRole role = ValidationRole.builder()
                    .validationRoleKey(key)
                    .displayName(key)
                    .status(Status.DEFINED)
                    .build();
            assertEquals(key, role.getValidationRoleKey());
        }
    }
}
