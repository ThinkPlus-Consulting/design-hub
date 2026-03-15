package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class BusinessRoleTest {

    @Test
    void shouldBuildBusinessRoleWithRequiredFields() {
        BusinessRole role = BusinessRole.builder()
                .roleKey("ADMIN")
                .displayName("Administrator")
                .roleGroup("tenant")
                .sortOrder(2)
                .status(Status.DEFINED)
                .build();

        assertEquals("ADMIN", role.getRoleKey());
        assertEquals("Administrator", role.getDisplayName());
        assertEquals("tenant", role.getRoleGroup());
        assertEquals(2, role.getSortOrder());
        assertEquals(Status.DEFINED, role.getStatus());
    }

    @Test
    void shouldSupportOptionalAttributes() {
        BusinessRole role = BusinessRole.builder()
                .roleKey("ARCHITECT")
                .displayName("Architect")
                .roleGroup("design")
                .scope("design-hub")
                .sortOrder(3)
                .status(Status.DEFINED)
                .sourceRefs(List.of("role-matrix.md"))
                .build();

        assertEquals("design", role.getRoleGroup());
        assertEquals("design-hub", role.getScope());
        assertEquals(1, role.getSourceRefs().size());
    }

    @Test
    void shouldCoverAllBusinessRoleKeys() {
        String[] businessKeys = {"SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"};
        for (String key : businessKeys) {
            BusinessRole role = BusinessRole.builder()
                    .roleKey(key)
                    .displayName(key)
                    .roleGroup("test")
                    .sortOrder(1)
                    .status(Status.DEFINED)
                    .build();
            assertEquals(key, role.getRoleKey());
        }
    }
}
