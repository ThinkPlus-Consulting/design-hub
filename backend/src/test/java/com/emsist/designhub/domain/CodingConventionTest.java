package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CodingConventionTest {

    @Test
    void shouldBuildCodingConventionWithRequiredFields() {
        CodingConvention conv = CodingConvention.builder()
                .conventionCode("CONV-DI-001")
                .name("Spring DI Pattern")
                .category("DEPENDENCY_INJECTION")
                .enforcement("MANDATORY")
                .scope("BACKEND")
                .docRef("docs/conventions/spring-di-pattern.md")
                .build();

        assertEquals("CONV-DI-001", conv.getConventionCode());
        assertEquals("MANDATORY", conv.getEnforcement());
        assertEquals("BACKEND", conv.getScope());
        assertEquals("docs/conventions/spring-di-pattern.md", conv.getDocRef());
    }

    @Test
    void shouldSupportGlobalScope() {
        CodingConvention conv = CodingConvention.builder()
                .conventionCode("CONV-NAME-001")
                .name("Variable Naming Convention")
                .category("NAMING")
                .enforcement("RECOMMENDED")
                .scope("GLOBAL")
                .docRef("docs/conventions/naming.md")
                .summary("Use camelCase for variables, PascalCase for classes")
                .build();

        assertEquals("GLOBAL", conv.getScope());
        assertNotNull(conv.getSummary());
    }

    @Test
    void shouldDefaultToActiveStatus() {
        CodingConvention conv = CodingConvention.builder()
                .conventionCode("CONV-ERR-001")
                .name("Error Handling Pattern")
                .category("ERROR_HANDLING")
                .enforcement("ADVISORY")
                .scope("FRONTEND")
                .docRef("docs/conventions/error-handling.md")
                .build();

        // activeStatus is null by default, which means ACTIVE
        assertNull(conv.getActiveStatus());
    }
}
