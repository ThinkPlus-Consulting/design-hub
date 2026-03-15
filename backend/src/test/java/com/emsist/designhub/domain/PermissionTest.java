package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldBuildPermissionWithRequiredFields() {
        Permission perm = Permission.builder()
                .permissionKey("PERM-ADMIN")
                .displayName("Administrator Access")
                .sortOrder(1)
                .build();

        assertEquals("PERM-ADMIN", perm.getPermissionKey());
        assertEquals("Administrator Access", perm.getDisplayName());
        assertEquals(1, perm.getSortOrder());
    }

    @Test
    void shouldFollowIdPattern() {
        Permission perm = Permission.builder()
                .permissionKey("PERM-VIEWER")
                .displayName("View Only")
                .sortOrder(6)
                .build();

        assertTrue(perm.getPermissionKey().startsWith("PERM-"));
    }

    @Test
    void shouldSupportAllPermissionKeys() {
        String[] keys = {"ADMIN", "SUPER_ADMIN", "ARCHITECT", "AGENT_DESIGNER",
                          "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"};
        for (int i = 0; i < keys.length; i++) {
            Permission p = Permission.builder()
                    .permissionKey("PERM-" + keys[i])
                    .displayName(keys[i])
                    .sortOrder(i + 1)
                    .build();
            assertNotNull(p.getPermissionKey());
        }
    }
}
