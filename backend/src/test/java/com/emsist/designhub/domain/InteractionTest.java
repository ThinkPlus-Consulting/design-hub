package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

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

    @Test
    void shouldLinkToPersonaRoleApiAndConfirmationEdges() {
        Persona persona = Persona.builder()
                .personaId("PER-AGENT-DESIGNER")
                .name("Agent Designer")
                .status(Status.DEFINED)
                .build();

        BusinessRole role = BusinessRole.builder()
                .roleKey("AGENT_DESIGNER")
                .displayName("Agent Designer")
                .roleGroup("design")
                .sortOrder(3)
                .status(Status.DEFINED)
                .build();

        ApiContract apiContract = ApiContract.builder()
                .contractId("API-POST-API-V1-AGENTS-ID-PUBLISH")
                .method("POST")
                .path("/api/v1/agents/{id}/publish")
                .description("Publish agent")
                .status(Status.DEFINED)
                .build();

        ConfirmationDialog dialog = ConfirmationDialog.builder()
                .dialogId("CONFIRM-AGT-PUBLISH")
                .triggerAction("Publish agent")
                .confirmLabel("Publish")
                .cancelLabel("Keep Draft")
                .consequenceText("The draft agent becomes available to end users.")
                .build();

        Interaction interaction = Interaction.builder()
                .interactionId("INT-R05-BUILDER-004")
                .element("Publish Agent button")
                .trigger("click")
                .usedByPersonas(List.of(persona))
                .accessibleByRoles(List.of(role))
                .callsApi(List.of(apiContract))
                .triggersConfirmation(dialog)
                .build();

        assertEquals(1, interaction.getUsedByPersonas().size());
        assertEquals("PER-AGENT-DESIGNER", interaction.getUsedByPersonas().get(0).getPersonaId());
        assertEquals(1, interaction.getAccessibleByRoles().size());
        assertEquals("AGENT_DESIGNER", interaction.getAccessibleByRoles().get(0).getRoleKey());
        assertEquals(1, interaction.getCallsApi().size());
        assertEquals("API-POST-API-V1-AGENTS-ID-PUBLISH", interaction.getCallsApi().get(0).getContractId());
        assertNotNull(interaction.getTriggersConfirmation());
        assertEquals("CONFIRM-AGT-PUBLISH", interaction.getTriggersConfirmation().getDialogId());
    }

    @Test
    void shouldHoldEmbeddedOutcomesAndErrorEdges() {
        ErrorCode errorCode = ErrorCode.builder()
                .code("AUTH-E-401")
                .severity("ERROR")
                .messageText("Session refresh failed.")
                .build();

        Interaction interaction = Interaction.builder()
                .interactionId("INT-G-004")
                .element("Extend session button")
                .trigger("click")
                .outcomeSuccess("Session is extended and the timeout modal closes.")
                .outcomeError("Session refresh failed.")
                .outcomeLoading("Refreshing session…")
                .errorCodeRef("AUTH-E-401")
                .onErrorShows(List.of(errorCode))
                .build();

        assertEquals("Session is extended and the timeout modal closes.", interaction.getOutcomeSuccess());
        assertEquals("Session refresh failed.", interaction.getOutcomeError());
        assertEquals("Refreshing session…", interaction.getOutcomeLoading());
        assertEquals("AUTH-E-401", interaction.getErrorCodeRef());
        assertEquals(1, interaction.getOnErrorShows().size());
        assertEquals("AUTH-E-401", interaction.getOnErrorShows().get(0).getCode());
    }
}
