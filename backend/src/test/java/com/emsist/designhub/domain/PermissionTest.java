package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PermissionTest {

    @Test
    void shouldBuildPermissionWithRequiredFields() {
        Permission perm = Permission.builder()
                .permissionKey("ADMIN")
                .displayName("Administrator Access")
                .sortOrder(1)
                .build();

        assertEquals("ADMIN", perm.getPermissionKey());
        assertEquals("Administrator Access", perm.getDisplayName());
        assertEquals(1, perm.getSortOrder());
    }

    @Test
    void shouldUseBareKeyFormat() {
        Permission perm = Permission.builder()
                .permissionKey("VIEWER")
                .displayName("View Only")
                .sortOrder(5)
                .build();

        assertFalse(perm.getPermissionKey().contains("-"),
                "Permission keys use bare format (ADMIN), not prefixed (PERM-ADMIN)");
    }

    @Test
    void shouldSupportAllPublishedPermissionKeys() {
        String[] keys = {"SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER",
                          "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"};
        assertEquals(8, keys.length);
        for (int i = 0; i < keys.length; i++) {
            Permission p = Permission.builder()
                    .permissionKey(keys[i])
                    .displayName(keys[i])
                    .sortOrder(i)
                    .build();
            assertNotNull(p.getPermissionKey());
        }
    }
}
