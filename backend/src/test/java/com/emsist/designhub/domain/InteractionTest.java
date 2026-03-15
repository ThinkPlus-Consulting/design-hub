package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InteractionTest {

    @Test
    void shouldLinkToPermissionViaRequiresPermission() {
        Permission perm = Permission.builder()
                .permissionKey("PERM-ADMIN")
                .displayName("Administrator Access")
                .sortOrder(1)
                .build();

        Interaction interaction = Interaction.builder()
                .interactionId("INT-SETTINGS-001")
                .element("Save Settings Button")
                .trigger("click")
                .requiresPermission(perm)
                .build();

        assertNotNull(interaction.getRequiresPermission());
        assertEquals("PERM-ADMIN", interaction.getRequiresPermission().getPermissionKey());
    }
}
