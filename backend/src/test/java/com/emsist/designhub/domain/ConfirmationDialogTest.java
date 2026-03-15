package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConfirmationDialogTest {

    @Test
    void shouldBuildConfirmationDialogWithRequiredFields() {
        ConfirmationDialog dialog = ConfirmationDialog.builder()
                .dialogId("CONFIRM-AGT-DELETE")
                .triggerAction("Delete agent")
                .confirmLabel("Delete")
                .cancelLabel("Cancel")
                .consequenceText("The selected agent will be permanently removed.")
                .build();

        assertEquals("CONFIRM-AGT-DELETE", dialog.getDialogId());
        assertEquals("Delete agent", dialog.getTriggerAction());
        assertEquals("Delete", dialog.getConfirmLabel());
        assertEquals("Cancel", dialog.getCancelLabel());
        assertEquals("The selected agent will be permanently removed.", dialog.getConsequenceText());
    }

    @Test
    void shouldUseCurrentSeedDialogIds() {
        ConfirmationDialog dialog = ConfirmationDialog.builder()
                .dialogId("CONFIRM-AGT-PUBLISH")
                .triggerAction("Publish agent")
                .confirmLabel("Publish")
                .cancelLabel("Keep Draft")
                .build();

        assertTrue(dialog.getDialogId().startsWith("CONFIRM-"));
        assertFalse(dialog.getDialogId().isBlank());
    }

    @Test
    void shouldSupportPublishedDialogShape() {
        ConfirmationDialog dialog = ConfirmationDialog.builder()
                .dialogId("CONFIRM-AGT-DELETE")
                .triggerAction("Delete agent")
                .confirmLabel("Delete")
                .cancelLabel("Cancel")
                .consequenceText("This action cannot be undone.")
                .build();

        assertTrue(dialog.getConfirmLabel().length() > 0);
        assertTrue(dialog.getCancelLabel().length() > 0);
        assertTrue(dialog.getTriggerAction().length() > 0);
    }
}
