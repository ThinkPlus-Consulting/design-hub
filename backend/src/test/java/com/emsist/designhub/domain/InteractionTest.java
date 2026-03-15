package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class InteractionTest {

    @Test
    void shouldLinkToPermissionViaRequiresPermission() {
        Permission perm = Permission.builder()
                .permissionKey("ADMIN")
                .displayName("Administrator")
                .sortOrder(1)
                .build();

        Interaction interaction = Interaction.builder()
                .interactionId("INT-SETTINGS-001")
                .element("Save Settings Button")
                .trigger("click")
                .requiresPermission(perm)
                .build();

        assertNotNull(interaction.getRequiresPermission());
        assertEquals("ADMIN", interaction.getRequiresPermission().getPermissionKey());
    }
}
