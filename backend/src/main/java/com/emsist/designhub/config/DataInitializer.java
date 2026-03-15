package com.emsist.designhub.config;

import com.emsist.designhub.domain.ContentElement;
import com.emsist.designhub.domain.Effect;
import com.emsist.designhub.domain.EntryMode;
import com.emsist.designhub.domain.Gap;
import com.emsist.designhub.domain.Status;
import com.emsist.designhub.domain.Interaction;
import com.emsist.designhub.domain.Journey;
import com.emsist.designhub.domain.JourneyStep;
import com.emsist.designhub.domain.Screen;
import com.emsist.designhub.domain.Touchpoint;
import com.emsist.designhub.repository.InteractionRepository;
import com.emsist.designhub.repository.JourneyRepository;
import com.emsist.designhub.repository.ScreenRepository;
import com.emsist.designhub.repository.TouchpointRepository;
import com.emsist.designhub.service.RegistryGraphMigrationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
@ConditionalOnProperty(name = "designhub.seed-data", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private final ScreenRepository screenRepository;
    private final TouchpointRepository touchpointRepository;
    private final InteractionRepository interactionRepository;
    private final JourneyRepository journeyRepository;
    private final RegistryGraphMigrationService registryGraphMigrationService;

    @Override
    @Transactional
    public void run(String... args) {
        if (screenRepository.count() > 0) {
            log.info("Seed data already exists ({} screens). Skipping.", screenRepository.count());
            registryGraphMigrationService.runFullMigration();
            return;
        }

        log.info("No screens found. Seeding design-hub data...");
        Map<String, Screen> screenMap = seedScreens();
        seedTouchpoints(screenMap);
        seedInteractions(screenMap);
        seedJourneys();
        registryGraphMigrationService.runFullMigration();
        log.info("Seed complete: {} screens, {} touchpoints, {} interactions, {} journeys.",
                screenRepository.count(), touchpointRepository.count(),
                interactionRepository.count(), journeyRepository.count());
    }

    private Map<String, Screen> seedScreens() {
        Map<String, Screen> map = new HashMap<>();

        Screen scr_auth = Screen.builder()
                .surfaceId("SCR-AUTH")
                .label("Login / Sign In")
                .module("Core")
                .routePath("/auth/login")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AUTH", scr_auth);
        scr_auth.setContentElements(List.of(
                ContentElement.builder().element("Sign in with Email Button").type("Primary Button").description("Animates signin-card-stage into view with email/password form").orderIndex(0).build(),
                ContentElement.builder().element("Email Input").type("Text Input").description("Validates email format on blur").orderIndex(1).build(),
                ContentElement.builder().element("Password Input").type("Password Input").description("Masked input with show/hide toggle").orderIndex(2).build(),
                ContentElement.builder().element("Sign In Submit").type("Primary Button").description("Authenticates via Keycloak OIDC, stores JWT, redirects to role-based landing").orderIndex(3).build(),
                ContentElement.builder().element("Forgot Password Link").type("Text Link").description("Currently mailto only (GAP-AUTH-01: no self-service reset flow)").orderIndex(4).build(),
                ContentElement.builder().element("Back Button").type("Icon Button").description("Animates form out, returns to initial signin-section").orderIndex(5).build(),
                ContentElement.builder().element("Error Banner").type("Alert Banner").description("Displays auth error message with icon, dismissible").orderIndex(6).build(),
                ContentElement.builder().element("Language Switcher (Login)").type("Pill Buttons").description("Rerenders login page in selected language, preference stored in localStorage").orderIndex(7).build()
        ));

        Screen surf_app_shell = Screen.builder()
                .surfaceId("SURF-APP-SHELL")
                .label("App Shell")
                .module("Core")
                .routePath(null)
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.APPROVED)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SURF-APP-SHELL", surf_app_shell);

        Screen surf_header = Screen.builder()
                .surfaceId("SURF-HEADER")
                .label("Header Bar")
                .module("Core")
                .routePath(null)
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.APPROVED)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(false)
                .loadingStates(false)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SURF-HEADER", surf_header);

        Screen surf_notif_dropdown = Screen.builder()
                .surfaceId("SURF-NOTIF-DROPDOWN")
                .label("Notification Dropdown")
                .module("Core")
                .routePath(null)
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SURF-NOTIF-DROPDOWN", surf_notif_dropdown);

        Screen surf_chatbot_fab = Screen.builder()
                .surfaceId("SURF-CHATBOT-FAB")
                .label("Chatbot FAB")
                .module("Core")
                .routePath(null)
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .personaIds(List.of())
                .build();
        map.put("SURF-CHATBOT-FAB", surf_chatbot_fab);

        Screen surf_session = Screen.builder()
                .surfaceId("SURF-SESSION")
                .label("Session Expiry Modal")
                .module("Core")
                .routePath(null)
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(false)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SURF-SESSION", surf_session);

        Screen scr_auth_pwd_reset_req = Screen.builder()
                .surfaceId("SCR-AUTH-PWD-RESET-REQ")
                .label("Password Reset Request")
                .module("Core")
                .routePath("/auth/password-reset")
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AUTH-PWD-RESET-REQ", scr_auth_pwd_reset_req);

        Screen scr_auth_pwd_reset_confirm = Screen.builder()
                .surfaceId("SCR-AUTH-PWD-RESET-CONFIRM")
                .label("Password Reset Confirm")
                .module("Core")
                .routePath("/auth/password-reset/confirm")
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AUTH-PWD-RESET-CONFIRM", scr_auth_pwd_reset_confirm);

        Screen scr_auth_mfa_setup = Screen.builder()
                .surfaceId("SCR-AUTH-MFA-SETUP")
                .label("MFA Setup")
                .module("Core")
                .routePath("/auth/mfa/setup")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AUTH-MFA-SETUP", scr_auth_mfa_setup);

        Screen scr_auth_mfa_verify = Screen.builder()
                .surfaceId("SCR-AUTH-MFA-VERIFY")
                .label("MFA Verification")
                .module("Core")
                .routePath("/auth/mfa/verify")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AUTH-MFA-VERIFY", scr_auth_mfa_verify);

        Screen scr_01 = Screen.builder()
                .surfaceId("SCR-01")
                .label("Object Type List/Grid")
                .module("R04")
                .routePath("/definitions/list")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-007"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-01", scr_01);
        scr_01.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-01-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-01-02").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_01.setContentElements(List.of(
                ContentElement.builder().element("Table/Card View Toggle").type("Toggle Group").description("Switch between table, card, and graph layout views").orderIndex(0).build(),
                ContentElement.builder().element("Search Bar").type("Text Input").description("Full-text search across object type names, typeKey, and code (debounce 300ms)").orderIndex(1).build(),
                ContentElement.builder().element("Status Filter").type("Dropdown").description("Filter by lifecycle status (Planned/Active/Hold/Retired)").orderIndex(2).build(),
                ContentElement.builder().element("State Filter").type("Dropdown").description("Filter by state (Default/Customized/Inherited/User-defined)").orderIndex(3).build(),
                ContentElement.builder().element("Sort by Column Header").type("Column Header").description("Toggle asc/desc sort on name, typeKey, code, status, dates, attributeCount").orderIndex(4).build(),
                ContentElement.builder().element("Pagination Controls").type("Paginator").description("Page navigation with size selector (default 25, max 100)").orderIndex(5).build(),
                ContentElement.builder().element("FAB (+) Create").type("Floating Button").description("Opens Create Object Type Wizard overlay").orderIndex(6).build(),
                ContentElement.builder().element("Row Action - View").type("Table Row").description("Opens Object Type Detail side panel on click").orderIndex(7).build(),
                ContentElement.builder().element("Row Action - Deactivate").type("Action Button").description("Status transition with confirmation dialog per lifecycle rule").orderIndex(8).build(),
                ContentElement.builder().element("Row Action - Delete").type("Danger Action").description("Delete with confirmation; blocked if mandated or has instances").orderIndex(9).build(),
                ContentElement.builder().element("Row Action - Duplicate").type("Action Button").description("Creates copy of object type with -copy suffix").orderIndex(10).build(),
                ContentElement.builder().element("Cross-Tenant Toggle").type("Toggle Switch").description("Super Admin only: shows types from all tenants with tenant column").orderIndex(11).build(),
                ContentElement.builder().element("Lock Icon (Mandated)").type("Icon").description("Shows pi-lock on mandated types for Tenant Admin").orderIndex(12).build(),
                ContentElement.builder().element("Inherited Badge").type("Tag").description("Inherited badge on propagated types for Tenant Admin").orderIndex(13).build()
        ));

        Screen scr_03 = Screen.builder()
                .surfaceId("SCR-03")
                .label("Create Wizard")
                .module("R04")
                .routePath("/definitions/new")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-005"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-03", scr_03);
        scr_03.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-03-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-03-02").gapType("MISSING_RULE").severity("LOW").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_03.setContentElements(List.of(
                ContentElement.builder().element("Step 1: Name Input").type("Text Input").description("Required, max 255 chars, validates not blank").orderIndex(0).build(),
                ContentElement.builder().element("Step 1: Description").type("Textarea").description("Optional freeform description").orderIndex(1).build(),
                ContentElement.builder().element("Step 1: Icon Selector").type("Dropdown").description("Icon picker from PrimeNG icon set with preview").orderIndex(2).build(),
                ContentElement.builder().element("Step 1: Icon Color").type("Color Picker").description("Hex color for icon background").orderIndex(3).build(),
                ContentElement.builder().element("Step 2: Connections").type("Multi-select").description("Pick target types and set connection properties").orderIndex(4).build(),
                ContentElement.builder().element("Step 3: Attributes").type("Checkbox List").description("Pick attributes from library; system defaults auto-selected and disabled").orderIndex(5).build(),
                ContentElement.builder().element("Step 3: AI Suggestions").type("Suggestion Panel").description("AI-suggested attributes based on type name; accept or dismiss").orderIndex(6).build(),
                ContentElement.builder().element("Step 4: Status Select").type("Radio/Dropdown").description("Choose initial status: planned or active").orderIndex(7).build(),
                ContentElement.builder().element("Step 5: Review & Confirm").type("Display").description("5-step wizard final step — shows all selections (name, icon, connections, attributes, status) for confirmation before creation").orderIndex(8).build(),
                ContentElement.builder().element("Create Button").type("Primary Button").description("Submits wizard on Step 5; stays on Step 5 with data preserved on failure").orderIndex(9).build(),
                ContentElement.builder().element("Cancel Button").type("Secondary Button").description("Closes wizard, all data lost (no confirmation)").orderIndex(10).build(),
                ContentElement.builder().element("Next/Back Buttons").type("Navigation Buttons").description("Step navigation across all 5 steps; Next blocked if name empty on Step 1").orderIndex(11).build()
        ));

        Screen scr_02_t1 = Screen.builder()
                .surfaceId("SCR-02-T1")
                .label("General Tab")
                .module("R04")
                .routePath("/definitions/:id/general")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-003"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-T1", scr_02_t1);
        scr_02_t1.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-02-T1-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_02_t1.setContentElements(List.of(
                ContentElement.builder().element("Edit Button").type("Icon Button").description("Enters edit mode; on default state shows customization warning (CD-06)").orderIndex(0).build(),
                ContentElement.builder().element("Save Button").type("Primary Button").description("Saves changes with optimistic locking via If-Match header").orderIndex(1).build(),
                ContentElement.builder().element("Cancel Button").type("Secondary Button").description("Reverts changes to last saved state").orderIndex(2).build(),
                ContentElement.builder().element("Set Parent Type").type("Button").description("Dialog with eligible parents list (excludes self, descendants); max depth 5").orderIndex(3).build(),
                ContentElement.builder().element("Concurrent Edit Warning").type("Banner").description("Shows on 409: \"Modified by {user} at {timestamp}\" with Reload/Force Save/Cancel").orderIndex(4).build(),
                ContentElement.builder().element("Maturity Score Overview").type("Display").description("Read-only maturity scores (completeness, compliance, relationship, freshness) shown as general object type information — not a separate tab").orderIndex(5).build(),
                ContentElement.builder().element("Maturity Radar Chart").type("Visualization").description("4-axis radar showing current maturity scores per dimension as part of general info").orderIndex(6).build(),
                ContentElement.builder().element("Tab Navigation Bar").type("Tab Strip").description("7-tab navigation: General (T1), Attributes (T2), Relations (T3), Governance (T4), Data Sources (T5), Measures Categories (T6), Measures (T7)").orderIndex(7).build()
        ));

        Screen scr_02_t2 = Screen.builder()
                .surfaceId("SCR-02-T2")
                .label("Attributes Tab")
                .module("R04")
                .routePath("/definitions/:id/attributes")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-008", "US-DM-009", "US-DM-010", "US-DM-011", "US-DM-012", "US-DM-013", "US-DM-014", "US-DM-015a"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-T2", scr_02_t2);
        scr_02_t2.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-02-T2-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-02-T2-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build()
        ));
        scr_02_t2.setContentElements(List.of(
                ContentElement.builder().element("Add Attribute Button").type("Button").description("Opens attribute pick-list dialog with search, group filter, and checkboxes").orderIndex(0).build(),
                ContentElement.builder().element("Attribute Row - Lifecycle Chip").type("Tag").description("Blue=planned, green=active, grey=retired; retired rows opacity 0.6").orderIndex(1).build(),
                ContentElement.builder().element("Activate/Retire/Reactivate Actions").type("Action Buttons").description("Lifecycle transitions with confirmation dialogs per state").orderIndex(2).build(),
                ContentElement.builder().element("Remove Attribute").type("Danger Action").description("Unlinks attribute from type; blocked for system defaults and mandated").orderIndex(3).build(),
                ContentElement.builder().element("Select All Checkbox").type("Checkbox").description("Selects all non-system-default attributes; shows bulk toolbar").orderIndex(4).build(),
                ContentElement.builder().element("Bulk Activate/Retire").type("Toolbar Buttons").description("Batch lifecycle transitions on selected attributes").orderIndex(5).build(),
                ContentElement.builder().element("Drag-and-Drop Reorder").type("Drag Handle").description("Updates displayOrder values via PATCH").orderIndex(6).build(),
                ContentElement.builder().element("isRequired Toggle").type("Toggle").description("Per-attribute required flag; TA can override inherited value").orderIndex(7).build(),
                ContentElement.builder().element("Maturity Class Dropdown").type("Dropdown").description("Mandatory/Conditional/Optional classification per attribute").orderIndex(8).build(),
                ContentElement.builder().element("Inherited Badge").type("Tag").description("Shows \"Inherited from {parentName}\" with pi-arrow-down for TA view").orderIndex(9).build(),
                ContentElement.builder().element("Language Dependent Toggle").type("Toggle").description("Per-attribute toggle to mark as language-dependent (moved from Locale Tab)").orderIndex(10).build(),
                ContentElement.builder().element("Per-Locale Translation Inputs").type("Textarea").description("When language-dependent is on: translation inputs per active locale with dir=rtl for RTL locales").orderIndex(11).build(),
                ContentElement.builder().element("Lookup Code Config").type("Configuration").description("Per-attribute lookup code configuration for localized values").orderIndex(12).build()
        ));

        Screen scr_02_t3 = Screen.builder()
                .surfaceId("SCR-02-T3")
                .label("Relations Tab")
                .module("R04")
                .routePath("/definitions/:id/relations")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-016", "US-DM-017", "US-DM-018", "US-DM-019", "US-DM-020"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-T3", scr_02_t3);
        scr_02_t3.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-02-T3-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-02-T3-02").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_02_t3.setContentElements(List.of(
                ContentElement.builder().element("Add Connection Button").type("Button").description("Opens connection creation dialog to pick target type and set properties").orderIndex(0).build(),
                ContentElement.builder().element("Incoming/Outgoing Sections").type("Grouped Lists").description("Connections split by direction with counts").orderIndex(1).build(),
                ContentElement.builder().element("Connection Lifecycle Actions").type("Action Buttons").description("Activate, retire, reactivate connections with confirmations").orderIndex(2).build(),
                ContentElement.builder().element("Importance Badge").type("Tag").description("Visual indicator of connection importance level").orderIndex(3).build(),
                ContentElement.builder().element("Required Badge").type("Tag").description("Shows if connection is mandatory for the type").orderIndex(4).build(),
                ContentElement.builder().element("Maturity Class Indicator").type("Dropdown").description("Mandatory/Conditional/Optional classification per connection").orderIndex(5).build()
        ));

        Screen scr_02_t4 = Screen.builder()
                .surfaceId("SCR-02-T4")
                .label("Governance Tab")
                .module("R04")
                .routePath("/definitions/:id/governance")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-037", "US-DM-038", "US-DM-039", "US-DM-040", "US-DM-041", "US-DM-042", "US-DM-043"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-T4", scr_02_t4);
        scr_02_t4.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-02-T4-01").gapType("MISSING_ARTIFACT").severity("HIGH").description("Missing screen artifact").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-02-T4-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing permission check").status(Status.IDENTIFIED).build()
        ));
        scr_02_t4.setContentElements(List.of(
                ContentElement.builder().element("Add Workflow Button").type("Button").description("Dialog with workflow selector from process-service, behaviour radio, permission table").orderIndex(0).build(),
                ContentElement.builder().element("Edit Workflow").type("Icon Button").description("Pre-populated dialog; workflow selector disabled").orderIndex(1).build(),
                ContentElement.builder().element("Delete Workflow").type("Icon Button").description("Confirmation CD-43; row animates out on success").orderIndex(2).build(),
                ContentElement.builder().element("allowDirectCreate Toggle").type("Toggle").description("Permits direct instance creation without workflow").orderIndex(3).build(),
                ContentElement.builder().element("allowDirectUpdate Toggle").type("Toggle").description("Permits direct instance updates without workflow").orderIndex(4).build(),
                ContentElement.builder().element("allowDirectDelete Toggle").type("Toggle").description("Permits direct instance deletion without workflow").orderIndex(5).build(),
                ContentElement.builder().element("Mandate Governance Toggle").type("Toggle").description("SA only: locks governance config for all child tenants").orderIndex(6).build()
        ));

        Screen scr_02_t5 = Screen.builder()
                .surfaceId("SCR-02-T5")
                .label("Data Sources Tab")
                .module("R04")
                .routePath("/definitions/:id/data-sources")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-086", "US-DM-087", "US-DM-088", "US-DM-089"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-T5", scr_02_t5);
        scr_02_t5.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-02-T5-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-02-T5-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build()
        ));
        scr_02_t5.setContentElements(List.of(
                ContentElement.builder().element("Add Source Button").type("Button").description("Opens 3-step dialog: source type, credentials, mapping").orderIndex(0).build(),
                ContentElement.builder().element("Edit Source").type("Icon Button").description("Edit existing data source configuration").orderIndex(1).build(),
                ContentElement.builder().element("Delete Source").type("Danger Button").description("Confirmation CD-50 before removal").orderIndex(2).build(),
                ContentElement.builder().element("Test Connection").type("Button").description("Tests connectivity to external data source").orderIndex(3).build(),
                ContentElement.builder().element("Schedule Sync").type("Configuration").description("Set up periodic data synchronization schedule").orderIndex(4).build(),
                ContentElement.builder().element("Execute/Preview").type("Button").description("Run or preview data import from source; max 10 sources per type").orderIndex(5).build()
        ));

        Screen scr_02_t6 = Screen.builder()
                .surfaceId("SCR-02-T6")
                .label("Measures Categories Tab")
                .module("R04")
                .routePath("/definitions/:id/measure-categories")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-090", "US-DM-091"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-T6", scr_02_t6);
        scr_02_t6.setContentElements(List.of(
                ContentElement.builder().element("Add Category Button").type("Button").description("Creates a new measure category").orderIndex(0).build(),
                ContentElement.builder().element("Edit Category").type("Icon Button").description("Edits category name and properties").orderIndex(1).build(),
                ContentElement.builder().element("Delete Category").type("Danger Button").description("Blocked if category has measures; confirmation required").orderIndex(2).build(),
                ContentElement.builder().element("Mandate Toggle").type("Toggle").description("SA only: mandates category to child tenants").orderIndex(3).build()
        ));

        Screen scr_02_t7 = Screen.builder()
                .surfaceId("SCR-02-T7")
                .label("Measures Tab")
                .module("R04")
                .routePath("/definitions/:id/measures")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-092", "US-DM-093", "US-DM-094", "US-DM-095"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-T7", scr_02_t7);
        scr_02_t7.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-02-T7-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build()
        ));
        scr_02_t7.setContentElements(List.of(
                ContentElement.builder().element("Add Measure Button").type("Button").description("Creates measure with name, unit, target, warning, critical thresholds, formula").orderIndex(0).build(),
                ContentElement.builder().element("Threshold Indicators").type("Status Indicators").description("Green/amber/red visual indicators based on threshold values").orderIndex(1).build(),
                ContentElement.builder().element("Calculated Measures").type("Display").description("Shows computed measures based on formula definitions").orderIndex(2).build(),
                ContentElement.builder().element("Edit Measure").type("Icon Button").description("Edits measure properties and threshold values").orderIndex(3).build(),
                ContentElement.builder().element("Delete Measure").type("Danger Button").description("Removes measure definition with confirmation").orderIndex(4).build()
        ));

        Screen scr_02_mat = Screen.builder()
                .surfaceId("SCR-02-MAT")
                .label("Maturity Configuration")
                .module("R04")
                .routePath("/definitions/:id/maturity-config")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-044", "US-DM-045", "US-DM-046", "US-DM-047", "US-DM-048", "US-DM-049", "US-DM-050", "US-DM-051", "US-DM-052", "US-DM-053", "US-DM-054"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-02-MAT", scr_02_mat);
        scr_02_mat.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-02-MAT-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-02-MAT-02").gapType("MISSING_RULE").severity("LOW").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-02-MAT-03").gapType("MISSING_ATTRIBUTE").severity("LOW").description("Design decision pending").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-02-MAT-04").gapType("MISSING_ARTIFACT").severity("MEDIUM").description("Requirements update needed").status(Status.IDENTIFIED).build()
        ));
        scr_02_mat.setContentElements(List.of(
                ContentElement.builder().element("Completeness Weight Slider").type("Slider/Input").description("Adjusts completeness axis weight (default 40%); sum must equal 100").orderIndex(0).build(),
                ContentElement.builder().element("Compliance Weight Slider").type("Slider/Input").description("Adjusts compliance axis weight (default 25%)").orderIndex(1).build(),
                ContentElement.builder().element("Relationship Weight Slider").type("Slider/Input").description("Adjusts relationship axis weight (default 20%)").orderIndex(2).build(),
                ContentElement.builder().element("Freshness Weight Slider").type("Slider/Input").description("Adjusts freshness axis weight (default 15%)").orderIndex(3).build(),
                ContentElement.builder().element("Freshness Threshold").type("Numeric Input").description("Days threshold (e.g. 90); negative rejected, 0 means always stale").orderIndex(4).build(),
                ContentElement.builder().element("Scoring Preview").type("Live Panel").description("Client-side recalculation preview as weights are adjusted (radar chart moved to General Tab)").orderIndex(5).build()
        ));

        Screen scr_04 = Screen.builder()
                .surfaceId("SCR-04")
                .label("Release Dashboard")
                .module("R04")
                .routePath("/definitions/releases")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-063", "US-DM-064", "US-DM-065", "US-DM-066", "US-DM-067", "US-DM-068", "US-DM-069", "US-DM-070", "US-DM-071", "US-DM-072", "US-DM-073", "US-DM-074"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-04", scr_04);
        scr_04.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-04-01").gapType("MISSING_ARTIFACT").severity("HIGH").description("Missing screen artifact").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-04-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing permission check").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-04-03").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_04.setContentElements(List.of(
                ContentElement.builder().element("Release List Table").type("Table").description("Shows releases with version, status, date, author columns").orderIndex(0).build(),
                ContentElement.builder().element("Publish Button").type("Primary Button").description("SA/ARCH: publishes release with breaking change count confirmation (CD-30)").orderIndex(1).build(),
                ContentElement.builder().element("Rollback Button").type("Danger Button").description("SA/ARCH: rollback confirmation (CD-31), creates new version from old").orderIndex(2).build(),
                ContentElement.builder().element("Diff View Button").type("Button").description("Shows added (green), modified (amber), removed (red) between versions").orderIndex(3).build(),
                ContentElement.builder().element("Adopt Button").type("Primary Button").description("TA: accepts release with impact assessment and safe pull (CD-32)").orderIndex(4).build(),
                ContentElement.builder().element("Defer Button").type("Secondary Button").description("TA: requires reason text, sets status=deferred").orderIndex(5).build(),
                ContentElement.builder().element("Reject Button").type("Danger Button").description("TA: requires feedback text, sets status=rejected").orderIndex(6).build(),
                ContentElement.builder().element("Adoption Tracker").type("Display").description("SA/ARCH: shows adoption status per child tenant").orderIndex(7).build(),
                ContentElement.builder().element("Version History").type("Table").description("Chronological release list; select two for comparison").orderIndex(8).build()
        ));

        Screen scr_04_m1 = Screen.builder()
                .surfaceId("SCR-04-M1")
                .label("Release Detail Modal")
                .module("R04")
                .routePath(null)
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(false)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-065"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-04-M1", scr_04_m1);
        scr_04_m1.setContentElements(List.of(
                ContentElement.builder().element("Release Summary").type("Display").description("Version number, status, author, creation date, description").orderIndex(0).build(),
                ContentElement.builder().element("Included Changes List").type("List").description("Object types added, modified, or removed in this release").orderIndex(1).build(),
                ContentElement.builder().element("Close Button").type("Button").description("Closes modal, returns to release dashboard").orderIndex(2).build()
        ));

        Screen scr_05 = Screen.builder()
                .surfaceId("SCR-05")
                .label("Maturity Dashboard")
                .module("R04")
                .routePath("/definitions/maturity")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-044", "US-DM-054"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-05", scr_05);
        scr_05.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-05-01").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_05.setContentElements(List.of(
                ContentElement.builder().element("Maturity Overview Cards").type("Stat Cards").description("Aggregate maturity scores across all object types").orderIndex(0).build(),
                ContentElement.builder().element("Maturity Trend Chart").type("Chart").description("Time-series chart showing maturity progression").orderIndex(1).build(),
                ContentElement.builder().element("Object Type Maturity Grid").type("Grid").description("Per-type maturity scores with drill-down to type detail").orderIndex(2).build(),
                ContentElement.builder().element("Filter by Status").type("Dropdown").description("Filter maturity view by object type lifecycle status").orderIndex(3).build()
        ));

        Screen scr_06 = Screen.builder()
                .surfaceId("SCR-06")
                .label("Locale Management")
                .module("R04")
                .routePath("/definitions/locale")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-055", "US-DM-062"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-06", scr_06);
        scr_06.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-06-01").gapType("MISSING_ARTIFACT").severity("HIGH").description("Architecture conflict detected").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-06-02").gapType("MISSING_ATTRIBUTE").severity("LOW").description("Design decision pending").status(Status.IDENTIFIED).build()
        ));
        scr_06.setContentElements(List.of(
                ContentElement.builder().element("Locale List").type("Table").description("Shows available locales with activation status and coverage").orderIndex(0).build(),
                ContentElement.builder().element("Activate/Deactivate Toggle").type("Toggle").description("Enable or disable a locale for definition translations").orderIndex(1).build(),
                ContentElement.builder().element("Translation Editor").type("Form").description("Per-locale translation inputs for definition metadata").orderIndex(2).build()
        ));

        Screen scr_gv = Screen.builder()
                .surfaceId("SCR-GV")
                .label("Graph Visualization")
                .module("R04")
                .routePath("/definitions/graph")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-075", "US-DM-076", "US-DM-077", "US-DM-078", "US-DM-079", "US-DM-080"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-GV", scr_gv);
        scr_gv.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-GV-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-GV-02").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_gv.setContentElements(List.of(
                ContentElement.builder().element("Graph Canvas").type("Interactive Canvas").description("cytoscape.js canvas with pan/zoom (0.2x-3x) and node click for detail overlay").orderIndex(0).build(),
                ContentElement.builder().element("Status Filter").type("Dropdown").description("Filter visible nodes by status; badge shows \"Showing X of Y\"").orderIndex(1).build(),
                ContentElement.builder().element("Zoom In/Out/Fit").type("Toolbar Buttons").description("Zoom controls with 0.2x increment; min 0.2x, max 3x").orderIndex(2).build(),
                ContentElement.builder().element("Reset Layout").type("Toolbar Button").description("Re-runs layout algorithm to reposition nodes").orderIndex(3).build(),
                ContentElement.builder().element("Layout Selector").type("Dropdown").description("Force-Directed, Hierarchical, Circular, Grid layouts").orderIndex(4).build(),
                ContentElement.builder().element("Export Button").type("Dropdown Button").description("Downloads graph as PNG or SVG image").orderIndex(5).build(),
                ContentElement.builder().element("Search Input").type("Text Input").description("Highlights and centers matching node by name").orderIndex(6).build(),
                ContentElement.builder().element("Node Click Overlay").type("Panel").description("Shows type name, typeKey, status, counts, and \"Open Full Detail\" button").orderIndex(7).build()
        ));

        Screen scr_ai = Screen.builder()
                .surfaceId("SCR-AI")
                .label("AI Insights Panel")
                .module("R04")
                .routePath(null)
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-096", "US-DM-097"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AI", scr_ai);
        scr_ai.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AI-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AI-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_ai.setContentElements(List.of(
                ContentElement.builder().element("AI Suggestions List").type("Card List").description("AI-generated attribute, connection, and improvement suggestions").orderIndex(0).build(),
                ContentElement.builder().element("Accept Suggestion").type("Button").description("Applies AI suggestion to the object type definition").orderIndex(1).build(),
                ContentElement.builder().element("Dismiss Suggestion").type("Button").description("Removes suggestion from the list").orderIndex(2).build(),
                ContentElement.builder().element("Duplicate Detection").type("Display").description("Shows potential duplicate object types with merge option").orderIndex(3).build(),
                ContentElement.builder().element("Refresh Insights").type("Button").description("Re-runs AI analysis on current type data").orderIndex(4).build()
        ));

        Screen scr_notif = Screen.builder()
                .surfaceId("SCR-NOTIF")
                .label("Notifications")
                .module("R04")
                .routePath("/definitions/notifications")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-073", "US-DM-074"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-NOTIF", scr_notif);
        scr_notif.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-NOTIF-01").gapType("MISSING_ARTIFACT").severity("MEDIUM").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_notif.setContentElements(List.of(
                ContentElement.builder().element("Notification List").type("List").description("Chronological notifications with type icon, message, timestamp").orderIndex(0).build(),
                ContentElement.builder().element("Mark as Read").type("Action Button").description("Marks individual notification as read").orderIndex(1).build(),
                ContentElement.builder().element("Mark All Read").type("Text Button").description("Marks all visible notifications as read, badge resets to 0").orderIndex(2).build(),
                ContentElement.builder().element("Notification Item Click").type("List Item").description("Navigates to related entity (release, agent, approval)").orderIndex(3).build(),
                ContentElement.builder().element("Filter by Category").type("Dropdown").description("Filter notifications by type (release, governance, system)").orderIndex(4).build()
        ));

        Screen scr_prop = Screen.builder()
                .surfaceId("SCR-PROP")
                .label("Propagation View")
                .module("R04")
                .routePath("/definitions/propagate")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-021", "US-DM-022", "US-DM-023", "US-DM-024", "US-DM-025"))
                .roleKeys(List.of("SUPER_ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-PROP", scr_prop);
        scr_prop.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-PROP-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-PROP-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_prop.setContentElements(List.of(
                ContentElement.builder().element("Target Tenant Selector").type("Multi-select").description("Select child tenants to propagate definitions to").orderIndex(0).build(),
                ContentElement.builder().element("Propagation Preview").type("Display").description("Shows what will be pushed: types, attributes, connections affected").orderIndex(1).build(),
                ContentElement.builder().element("Confirm Propagation").type("Primary Button").description("Starts propagation with confirmation (CD-42)").orderIndex(2).build(),
                ContentElement.builder().element("Progress Tracker").type("Progress Display").description("Per-tenant propagation status (pending/in-progress/success/failed)").orderIndex(3).build(),
                ContentElement.builder().element("Retry Failed").type("Button").description("Retries propagation for failed tenants only").orderIndex(4).build()
        ));

        Screen scr_diff = Screen.builder()
                .surfaceId("SCR-DIFF")
                .label("Diff View")
                .module("R04")
                .routePath("/definitions/diff")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(false)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-068"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-DIFF", scr_diff);
        scr_diff.setContentElements(List.of(
                ContentElement.builder().element("Version Selector (From)").type("Dropdown").description("Select base version for comparison").orderIndex(0).build(),
                ContentElement.builder().element("Version Selector (To)").type("Dropdown").description("Select target version for comparison").orderIndex(1).build(),
                ContentElement.builder().element("Diff Display").type("Diff Panel").description("Added (green), modified (amber), removed (red) changes between versions").orderIndex(2).build(),
                ContentElement.builder().element("Navigate to Type").type("Link").description("Click changed item to open its detail panel").orderIndex(3).build()
        ));

        Screen scr_mandate = Screen.builder()
                .surfaceId("SCR-MANDATE")
                .label("Mandate Configuration")
                .module("R04")
                .routePath("/definitions/mandates")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-031", "US-DM-032", "US-DM-033", "US-DM-034", "US-DM-035", "US-DM-036"))
                .roleKeys(List.of("SUPER_ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-MANDATE", scr_mandate);
        scr_mandate.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-MANDATE-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing permission check").status(Status.IDENTIFIED).build()
        ));
        scr_mandate.setContentElements(List.of(
                ContentElement.builder().element("Mandated Types List").type("Table").description("Lists object types with mandate toggle per type").orderIndex(0).build(),
                ContentElement.builder().element("Mandate Toggle").type("Toggle").description("SA only: enables/disables mandate for child tenants").orderIndex(1).build(),
                ContentElement.builder().element("Mandate Scope Selector").type("Multi-select").description("Select which aspects are mandated (attributes, connections, governance)").orderIndex(2).build(),
                ContentElement.builder().element("Push Updates Button").type("Primary Button").description("Pushes mandate changes to child tenants (CD-41)").orderIndex(3).build(),
                ContentElement.builder().element("Impact Preview").type("Display").description("Shows affected tenants and what will change").orderIndex(4).build()
        ));

        Screen scr_export = Screen.builder()
                .surfaceId("SCR-EXPORT")
                .label("Export View")
                .module("R04")
                .routePath("/definitions/export")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-DM-081", "US-DM-082", "US-DM-083", "US-DM-084", "US-DM-085"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-EXPORT", scr_export);
        scr_export.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-EXPORT-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-EXPORT-02").gapType("MISSING_RULE").severity("LOW").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_export.setContentElements(List.of(
                ContentElement.builder().element("Export Format Selector").type("Dropdown").description("Choose export format (JSON, CSV, Excel)").orderIndex(0).build(),
                ContentElement.builder().element("Export Scope Selector").type("Multi-select").description("Select which object types to include in export").orderIndex(1).build(),
                ContentElement.builder().element("Export Button").type("Primary Button").description("Downloads export file with selected types and format").orderIndex(2).build(),
                ContentElement.builder().element("Import File Upload").type("File Input").description("Upload definition import file with format validation").orderIndex(3).build(),
                ContentElement.builder().element("Import Preview").type("Display").description("Shows what will be imported: new, updated, conflicts").orderIndex(4).build(),
                ContentElement.builder().element("Conflict Resolution").type("Radio Group").description("Skip, overwrite, or merge per-item on conflicts (CD-60)").orderIndex(5).build(),
                ContentElement.builder().element("Confirm Import").type("Primary Button").description("Applies import with selected conflict resolution strategy").orderIndex(6).build()
        ));

        Screen scr_agt_list = Screen.builder()
                .surfaceId("SCR-AGT-LIST")
                .label("Agent List (Card/Table)")
                .module("R05")
                .routePath("/ai/agents")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-137", "US-AI-138"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-LIST", scr_agt_list);
        scr_agt_list.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-LIST-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-LIST-02").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_list.setContentElements(List.of(
                ContentElement.builder().element("View Toggle (Card/Table)").type("Toggle").description("Switches between card grid and table view; mobile defaults to card").orderIndex(0).build(),
                ContentElement.builder().element("Search Input").type("Text Input").description("Filters agents by name, type, status").orderIndex(1).build(),
                ContentElement.builder().element("Filter Chips").type("Chip Group").description("Filter by status (Active/Draft/Training/Archived), type, domain").orderIndex(2).build(),
                ContentElement.builder().element("+ New Agent Button").type("Button").description("Navigates to Template Gallery or Agent Builder; hidden for USR/VW").orderIndex(3).build(),
                ContentElement.builder().element("Agent Card - View").type("Button").description("Opens agent factsheet detail panel").orderIndex(4).build(),
                ContentElement.builder().element("Agent Card - Chat").type("Button").description("Opens full-page chat with agent preselected; hidden for VW").orderIndex(5).build(),
                ContentElement.builder().element("Context Menu (3-dot)").type("Menu Button").description("Edit (navigates to Agent Builder), Duplicate, Export, Archive, Delete actions; hidden for USR/VW. Note: Edit navigates to Builder screen, not inline edit — consistent with pattern: list actions are navigation, editing is in detail/builder").orderIndex(6).build(),
                ContentElement.builder().element("Bulk Select Checkbox").type("Checkbox").description("Per-card/row selection; enables bulk toolbar (Activate, Archive, Delete, Export)").orderIndex(7).build(),
                ContentElement.builder().element("Favorite Star").type("Icon Toggle").description("Marks agent as favorite, persists per user").orderIndex(8).build()
        ));

        Screen scr_agt_detail = Screen.builder()
                .surfaceId("SCR-AGT-DETAIL")
                .label("Agent Detail (Tabs)")
                .module("R05")
                .routePath("/ai/agents/:id")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-139"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-DETAIL", scr_agt_detail);
        scr_agt_detail.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-DETAIL-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_detail.setContentElements(List.of(
                ContentElement.builder().element("Overview Tab").type("Tab Panel").description("Agent name, description, status, domain, hierarchy badge, ATS dimensions").orderIndex(0).build(),
                ContentElement.builder().element("Configuration Tab").type("Tab Panel").description("Prompt config, model selection, system prompt (SA/TA/AD only)").orderIndex(1).build(),
                ContentElement.builder().element("Skills Tab").type("Tab Panel").description("Linked skills list with add/remove and skill testing").orderIndex(2).build(),
                ContentElement.builder().element("Tools Tab").type("Tab Panel").description("Linked tools list with add/remove, testing, timeout config").orderIndex(3).build(),
                ContentElement.builder().element("Training Tab").type("Tab Panel").description("Training datasets, run history, metrics charts, start training").orderIndex(4).build(),
                ContentElement.builder().element("Performance Tab").type("Tab Panel").description("Quality scores, response times, satisfaction, token usage charts").orderIndex(5).build(),
                ContentElement.builder().element("History Tab").type("Tab Panel").description("Version list, compare versions, rollback button").orderIndex(6).build()
        ));

        Screen scr_agt_builder = Screen.builder()
                .surfaceId("SCR-AGT-BUILDER")
                .label("Agent Builder (3-panel)")
                .module("R05")
                .routePath("/ai/agents/builder")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-090", "US-AI-091", "US-AI-092", "US-AI-093", "US-AI-094"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-BUILDER", scr_agt_builder);
        scr_agt_builder.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-BUILDER-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-BUILDER-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-BUILDER-03").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_builder.setContentElements(List.of(
                ContentElement.builder().element("Left Panel - Config").type("Form Panel").description("Agent properties: name, description, model, temperature, max tokens").orderIndex(0).build(),
                ContentElement.builder().element("Center Panel - Canvas").type("Visual Editor").description("Drag-and-drop skill/tool wiring canvas").orderIndex(1).build(),
                ContentElement.builder().element("Right Panel - Preview").type("Chat Preview").description("Live test chat panel to test agent configuration").orderIndex(2).build(),
                ContentElement.builder().element("System Prompt Editor").type("Textarea").description("Edit system prompt with token counter").orderIndex(3).build(),
                ContentElement.builder().element("Skill Palette").type("Drag Source").description("Available skills to drag onto canvas").orderIndex(4).build(),
                ContentElement.builder().element("Tool Palette").type("Drag Source").description("Available tools to drag onto canvas").orderIndex(5).build(),
                ContentElement.builder().element("Fork Button").type("Button").description("Creates a fork of the current agent configuration").orderIndex(6).build(),
                ContentElement.builder().element("Save Button").type("Primary Button").description("Saves current builder state").orderIndex(7).build(),
                ContentElement.builder().element("Version History Link").type("Link").description("Opens version history panel for comparison and rollback").orderIndex(8).build()
        ));

        Screen scr_agt_gallery = Screen.builder()
                .surfaceId("SCR-AGT-GALLERY")
                .label("Template Gallery")
                .module("R05")
                .routePath("/ai/agents/gallery")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-078", "US-AI-079", "US-AI-080", "US-AI-142", "US-AI-143"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-GALLERY", scr_agt_gallery);
        scr_agt_gallery.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-GALLERY-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing permission check").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-GALLERY-02").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_gallery.setContentElements(List.of(
                ContentElement.builder().element("Template Card Grid").type("Card Grid").description("Gallery of agent templates with preview, description, and use count").orderIndex(0).build(),
                ContentElement.builder().element("Use Template Button").type("Primary Button").description("Creates new agent from selected template in Agent Builder").orderIndex(1).build(),
                ContentElement.builder().element("Template Factsheet").type("Panel").description("Preview, Config, Fork History tabs for selected template").orderIndex(2).build(),
                ContentElement.builder().element("Submit to Gallery").type("Button").description("Submits agent config for gallery review and approval").orderIndex(3).build(),
                ContentElement.builder().element("Search Templates").type("Text Input").description("Search templates by name, domain, or capability").orderIndex(4).build(),
                ContentElement.builder().element("Category Filter").type("Chip Group").description("Filter templates by category or domain").orderIndex(5).build()
        ));

        Screen scr_agt_chat = Screen.builder()
                .surfaceId("SCR-AGT-CHAT")
                .label("Chat Interface")
                .module("R05")
                .routePath("/ai/agents/chat")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-133", "US-AI-134", "US-AI-135", "US-AI-136"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-CHAT", scr_agt_chat);
        scr_agt_chat.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-CHAT-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-CHAT-02").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-CHAT-03").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-CHAT-04").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build()
        ));
        scr_agt_chat.setContentElements(List.of(
                ContentElement.builder().element("Agent Selector").type("Dropdown").description("Lists active agents for current tenant; select to start/switch conversation").orderIndex(0).build(),
                ContentElement.builder().element("Message Input").type("Textarea").description("Type message, send via Enter or Send button; max 4096 tokens per request").orderIndex(1).build(),
                ContentElement.builder().element("Send Button").type("Icon Button").description("Sends message; disabled when empty or streaming").orderIndex(2).build(),
                ContentElement.builder().element("Thinking Indicator").type("Animation").description("Shows during processing with elapsed timer").orderIndex(3).build(),
                ContentElement.builder().element("Tool Call Visibility").type("Expandable").description("Shows which tools are being called during processing").orderIndex(4).build(),
                ContentElement.builder().element("Code Block").type("Formatted Block").description("Syntax-highlighted code with copy button").orderIndex(5).build(),
                ContentElement.builder().element("Rating Thumbs").type("Icon Buttons").description("Rate response quality (thumbs up/down)").orderIndex(6).build(),
                ContentElement.builder().element("Submit Correction").type("Text Action").description("Opens correction form for wrong responses").orderIndex(7).build(),
                ContentElement.builder().element("New Conversation").type("Button").description("Clears context and starts fresh conversation").orderIndex(8).build(),
                ContentElement.builder().element("Conversation History").type("Sidebar List").description("Previous conversations list; click to load").orderIndex(9).build(),
                ContentElement.builder().element("Search Conversations").type("Text Input").description("Search across conversation history").orderIndex(10).build()
        ));

        Screen scr_agt_notif = Screen.builder()
                .surfaceId("SCR-AGT-NOTIF")
                .label("Notification Center")
                .module("R05")
                .routePath("/ai/notifications")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-NOTIF", scr_agt_notif);
        scr_agt_notif.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-NOTIF-01").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_notif.setContentElements(List.of(
                ContentElement.builder().element("Notification List").type("Table").description("Chronological agent-related notifications with type, message, timestamp").orderIndex(0).build(),
                ContentElement.builder().element("Filter by Type").type("Dropdown").description("Filter by notification type (training, deployment, error, approval)").orderIndex(1).build(),
                ContentElement.builder().element("Mark as Read").type("Action Button").description("Marks individual notification as read").orderIndex(2).build(),
                ContentElement.builder().element("Navigate to Source").type("Link").description("Navigates to the agent, pipeline, or training run that triggered the notification").orderIndex(3).build()
        ));

        Screen scr_agt_audit = Screen.builder()
                .surfaceId("SCR-AGT-AUDIT")
                .label("Audit Log Viewer")
                .module("R05")
                .routePath("/ai/audit")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-145"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-AUDIT", scr_agt_audit);
        scr_agt_audit.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-AUDIT-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_audit.setContentElements(List.of(
                ContentElement.builder().element("Audit Log Table").type("Table").description("Filterable log entries: timestamp, user, action, entity, details").orderIndex(0).build(),
                ContentElement.builder().element("Date Range Filter").type("Date Picker").description("Filter audit entries by date range").orderIndex(1).build(),
                ContentElement.builder().element("Action Type Filter").type("Dropdown").description("Filter by action type (create, update, delete, deploy, train)").orderIndex(2).build(),
                ContentElement.builder().element("Export Audit Log").type("Button").description("Downloads filtered audit log as CSV").orderIndex(3).build(),
                ContentElement.builder().element("Log Entry Detail").type("Expandable Row").description("Expand row to see full before/after diff").orderIndex(4).build()
        ));

        Screen scr_agt_pipeline = Screen.builder()
                .surfaceId("SCR-AGT-PIPELINE")
                .label("Pipeline Run Viewer")
                .module("R05")
                .routePath("/ai/agents/pipelines")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-149", "US-AI-150"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-PIPELINE", scr_agt_pipeline);
        scr_agt_pipeline.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-PIPELINE-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-PIPELINE-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_pipeline.setContentElements(List.of(
                ContentElement.builder().element("Pipeline Run List").type("Table").description("Lists pipeline runs with status, agent, start time, duration").orderIndex(0).build(),
                ContentElement.builder().element("Trigger Run Button").type("Primary Button").description("Starts a new pipeline run with confirmation").orderIndex(1).build(),
                ContentElement.builder().element("Run Detail Panel").type("Panel").description("Step-by-step execution view with logs and status per stage").orderIndex(2).build(),
                ContentElement.builder().element("Cancel Run").type("Danger Button").description("Cancels an in-progress pipeline run").orderIndex(3).build(),
                ContentElement.builder().element("Retry Run").type("Button").description("Retries failed pipeline run from failed step").orderIndex(4).build()
        ));

        Screen scr_agt_rbac = Screen.builder()
                .surfaceId("SCR-AGT-RBAC")
                .label("RBAC Matrix")
                .module("R05")
                .routePath("/ai/admin/rbac")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-146", "US-AI-147"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-RBAC", scr_agt_rbac);
        scr_agt_rbac.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-RBAC-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-RBAC-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing permission check").status(Status.IDENTIFIED).build()
        ));
        scr_agt_rbac.setContentElements(List.of(
                ContentElement.builder().element("Role/Permission Matrix").type("Grid").description("Rows=roles, columns=permissions with checkboxes for each combination").orderIndex(0).build(),
                ContentElement.builder().element("Save Changes Button").type("Primary Button").description("Persists RBAC matrix changes with confirmation").orderIndex(1).build(),
                ContentElement.builder().element("User Role Assignment").type("Dropdown").description("Assign roles to users with confirmation on role change").orderIndex(2).build(),
                ContentElement.builder().element("Role Definition Editor").type("Form").description("Create or edit role definitions with name and description").orderIndex(3).build()
        ));

        Screen scr_agt_settings = Screen.builder()
                .surfaceId("SCR-AGT-SETTINGS")
                .label("AI Module Settings")
                .module("R05")
                .routePath("/ai/admin/settings")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-140"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-SETTINGS", scr_agt_settings);
        scr_agt_settings.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-SETTINGS-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build()
        ));
        scr_agt_settings.setContentElements(List.of(
                ContentElement.builder().element("General Settings Tab").type("Tab Panel").description("Platform-level AI configuration (default model, max tokens, temperature)").orderIndex(0).build(),
                ContentElement.builder().element("Notification Preferences Tab").type("Tab Panel").description("Configure which agent events trigger notifications").orderIndex(1).build(),
                ContentElement.builder().element("Integrations Tab").type("Tab Panel").description("API keys, webhook URLs, external service connections").orderIndex(2).build(),
                ContentElement.builder().element("Save Settings").type("Primary Button").description("Persists settings changes").orderIndex(3).build()
        ));

        Screen scr_agt_cost = Screen.builder()
                .surfaceId("SCR-AGT-COST")
                .label("Token/Cost Dashboard")
                .module("R05")
                .routePath("/ai/analytics/cost")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-141"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-COST", scr_agt_cost);
        scr_agt_cost.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-COST-01").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_cost.setContentElements(List.of(
                ContentElement.builder().element("Total Token Usage Card").type("Stat Card").description("Aggregate token usage across all agents for selected period").orderIndex(0).build(),
                ContentElement.builder().element("Cost Breakdown Chart").type("Chart").description("Cost per agent/model/tenant over time").orderIndex(1).build(),
                ContentElement.builder().element("Date Range Selector").type("Date Picker").description("Select time period for cost analysis").orderIndex(2).build(),
                ContentElement.builder().element("Per-Agent Cost Table").type("Table").description("Token and cost breakdown per agent with sort/filter").orderIndex(3).build(),
                ContentElement.builder().element("Budget Alert Config").type("Form").description("Set cost alert thresholds and notification preferences").orderIndex(4).build()
        ));

        Screen scr_agt_knowledge = Screen.builder()
                .surfaceId("SCR-AGT-KNOWLEDGE")
                .label("Knowledge Source Manager")
                .module("R05")
                .routePath("/ai/agents/:id/knowledge")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-KNOWLEDGE", scr_agt_knowledge);
        scr_agt_knowledge.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-KNOWLEDGE-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-KNOWLEDGE-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-KNOWLEDGE-03").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_knowledge.setContentElements(List.of(
                ContentElement.builder().element("Knowledge Source List").type("Table").description("Lists sources with name, type, status, document count, last sync").orderIndex(0).build(),
                ContentElement.builder().element("Upload Document").type("File Upload").description("Upload documents for embedding with format/size validation").orderIndex(1).build(),
                ContentElement.builder().element("Embedding Model Selector").type("Dropdown").description("Select embedding model with confirmation on change").orderIndex(2).build(),
                ContentElement.builder().element("Delete Source").type("Danger Button").description("Deletes knowledge source and its embeddings with confirmation").orderIndex(3).build(),
                ContentElement.builder().element("Sync Status").type("Progress Display").description("Shows embedding pipeline progress per source").orderIndex(4).build(),
                ContentElement.builder().element("Search Knowledge").type("Text Input").description("Search across indexed knowledge sources").orderIndex(5).build(),
                ContentElement.builder().element("Source Detail Panel").type("Panel").description("Shows document list, chunk count, embedding stats").orderIndex(6).build()
        ));

        Screen scr_agt_train = Screen.builder()
                .surfaceId("SCR-AGT-TRAIN")
                .label("Training Dashboard")
                .module("R05")
                .routePath("/ai/agents/:id/training")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-TRAIN", scr_agt_train);
        scr_agt_train.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-TRAIN-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-TRAIN-02").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-TRAIN-03").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_train.setContentElements(List.of(
                ContentElement.builder().element("Start Training Button").type("Primary Button").description("Starts new training run with model/dataset selection confirmation").orderIndex(0).build(),
                ContentElement.builder().element("Start DPO Training").type("Button").description("Initiates Direct Preference Optimization training with confirmation").orderIndex(1).build(),
                ContentElement.builder().element("Generate Teacher Examples").type("Button").description("Generates synthetic training examples from teacher model").orderIndex(2).build(),
                ContentElement.builder().element("Training Run List").type("Table").description("History of training runs with status, metrics, duration").orderIndex(3).build(),
                ContentElement.builder().element("Metrics Charts").type("Charts").description("Loss curves, accuracy, and performance metrics over epochs").orderIndex(4).build(),
                ContentElement.builder().element("Deploy Model").type("Primary Button").description("Deploys trained model to production with confirmation").orderIndex(5).build(),
                ContentElement.builder().element("Freeze Dataset").type("Button").description("Locks dataset version for reproducibility").orderIndex(6).build(),
                ContentElement.builder().element("Rollback Model").type("Danger Button").description("Reverts to previous model version with confirmation").orderIndex(7).build(),
                ContentElement.builder().element("Dataset Manager").type("Panel").description("Upload, preview, and manage training datasets").orderIndex(8).build()
        ));

        Screen scr_agt_eval = Screen.builder()
                .surfaceId("SCR-AGT-EVAL")
                .label("Eval/Benchmark Dashboard")
                .module("R05")
                .routePath("/ai/agents/:id/eval")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-EVAL", scr_agt_eval);
        scr_agt_eval.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-EVAL-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_eval.setContentElements(List.of(
                ContentElement.builder().element("Benchmark Suite Selector").type("Dropdown").description("Select evaluation benchmark suite to run").orderIndex(0).build(),
                ContentElement.builder().element("Run Evaluation Button").type("Primary Button").description("Starts benchmark evaluation against selected model").orderIndex(1).build(),
                ContentElement.builder().element("Results Table").type("Table").description("Benchmark results with scores per test case").orderIndex(2).build(),
                ContentElement.builder().element("Version Comparison").type("Chart").description("Side-by-side metric comparison across model versions").orderIndex(3).build(),
                ContentElement.builder().element("Pass/Fail Summary").type("Stat Cards").description("Aggregate pass rate, average score, regressions detected").orderIndex(4).build()
        ));

        Screen scr_agt_hitl = Screen.builder()
                .surfaceId("SCR-AGT-HITL")
                .label("HITL Approval Queue")
                .module("R05")
                .routePath("/ai/agents/hitl")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-HITL", scr_agt_hitl);
        scr_agt_hitl.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-HITL-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-HITL-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing permission check").status(Status.IDENTIFIED).build()
        ));
        scr_agt_hitl.setContentElements(List.of(
                ContentElement.builder().element("Approval Queue Table").type("Table").description("Pending items requiring human review: agent action, context, requester").orderIndex(0).build(),
                ContentElement.builder().element("Approve Button").type("Primary Button").description("Approves the pending agent action").orderIndex(1).build(),
                ContentElement.builder().element("Reject Button").type("Danger Button").description("Rejects the pending action with reason").orderIndex(2).build(),
                ContentElement.builder().element("Review Detail Panel").type("Panel").description("Full context of the action: input, output, risk assessment").orderIndex(3).build(),
                ContentElement.builder().element("Filter by Priority").type("Dropdown").description("Filter queue by urgency level").orderIndex(4).build(),
                ContentElement.builder().element("Bulk Approve").type("Button").description("Approves multiple selected items at once").orderIndex(5).build()
        ));

        Screen scr_agt_analytics = Screen.builder()
                .surfaceId("SCR-AGT-ANALYTICS")
                .label("Analytics Dashboard")
                .module("R05")
                .routePath("/ai/analytics")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-ANALYTICS", scr_agt_analytics);
        scr_agt_analytics.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-ANALYTICS-01").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_analytics.setContentElements(List.of(
                ContentElement.builder().element("KPI Summary Cards").type("Stat Cards").description("Total conversations, avg response time, satisfaction score, active agents").orderIndex(0).build(),
                ContentElement.builder().element("Usage Trend Chart").type("Chart").description("Conversation volume and response time trends over time").orderIndex(1).build(),
                ContentElement.builder().element("Agent Performance Grid").type("Grid").description("Per-agent metrics comparison with drill-down").orderIndex(2).build(),
                ContentElement.builder().element("Date Range Selector").type("Date Picker").description("Select analysis time period").orderIndex(3).build(),
                ContentElement.builder().element("Export Report").type("Button").description("Downloads analytics report as PDF or CSV").orderIndex(4).build()
        ));

        Screen scr_agt_orch = Screen.builder()
                .surfaceId("SCR-AGT-ORCH")
                .label("Orchestration Dashboard")
                .module("R05")
                .routePath("/ai/agents/orchestration")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-ORCH", scr_agt_orch);
        scr_agt_orch.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-ORCH-01").gapType("MISSING_ARTIFACT").severity("HIGH").description("Missing screen artifact").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-AGT-ORCH-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_orch.setContentElements(List.of(
                ContentElement.builder().element("Orchestration Flow Canvas").type("Visual Editor").description("Multi-agent workflow designer with drag-and-drop agent nodes").orderIndex(0).build(),
                ContentElement.builder().element("Agent Node Palette").type("Drag Source").description("Available agents to add to orchestration flow").orderIndex(1).build(),
                ContentElement.builder().element("Flow Execution Monitor").type("Display").description("Real-time execution state of active orchestration flows").orderIndex(2).build(),
                ContentElement.builder().element("Circuit Breaker Status").type("Status Indicators").description("Health status of each agent in the orchestration chain").orderIndex(3).build(),
                ContentElement.builder().element("Execution History").type("Table").description("Past orchestration runs with status, duration, and error logs").orderIndex(4).build(),
                ContentElement.builder().element("Save Flow").type("Primary Button").description("Saves orchestration flow configuration").orderIndex(5).build(),
                ContentElement.builder().element("Run Flow").type("Primary Button").description("Manually triggers an orchestration flow execution").orderIndex(6).build()
        ));

        Screen scr_agt_history = Screen.builder()
                .surfaceId("SCR-AGT-HISTORY")
                .label("Version History Panel")
                .module("R05")
                .routePath("/ai/agents/:id/history")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-148"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-HISTORY", scr_agt_history);
        scr_agt_history.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-AGT-HISTORY-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_agt_history.setContentElements(List.of(
                ContentElement.builder().element("Version List").type("Table").description("Chronological list of agent versions with date, author, change summary").orderIndex(0).build(),
                ContentElement.builder().element("Compare Versions").type("Button").description("Select two versions to view diff between configurations").orderIndex(1).build(),
                ContentElement.builder().element("Version Diff View").type("Diff Panel").description("Side-by-side comparison of selected versions").orderIndex(2).build(),
                ContentElement.builder().element("Rollback Button").type("Danger Button").description("Reverts agent to selected version with confirmation").orderIndex(3).build(),
                ContentElement.builder().element("Version Detail").type("Panel").description("Full configuration snapshot for selected version").orderIndex(4).build()
        ));

        Screen scr_agt_import = Screen.builder()
                .surfaceId("SCR-AGT-IMPORT")
                .label("Import/Export")
                .module("R05")
                .routePath("/ai/agents/import-export")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-IMPORT", scr_agt_import);

        Screen scr_agt_compare = Screen.builder()
                .surfaceId("SCR-AGT-COMPARE")
                .label("Agent Comparison")
                .module("R05")
                .routePath("/ai/agents/compare")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-COMPARE", scr_agt_compare);

        Screen scr_agt_workspace = Screen.builder()
                .surfaceId("SCR-AGT-WORKSPACE")
                .label("Agent Workspace")
                .module("R05")
                .routePath("/ai/agents/workspace")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-WORKSPACE", scr_agt_workspace);

        Screen scr_agt_embed = Screen.builder()
                .surfaceId("SCR-AGT-EMBED")
                .label("Embedded Agent Panel")
                .module("R05")
                .routePath("/ai/agents/embed")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(false)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-EMBED", scr_agt_embed);

        Screen scr_agt_triggers = Screen.builder()
                .surfaceId("SCR-AGT-TRIGGERS")
                .label("Event Trigger Management")
                .module("R05")
                .routePath("/ai/agents/triggers")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-TRIGGERS", scr_agt_triggers);

        Screen scr_agt_maturity = Screen.builder()
                .surfaceId("SCR-AGT-MATURITY")
                .label("Maturity Dashboard")
                .module("R05")
                .routePath("/ai/agents/maturity")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-MATURITY", scr_agt_maturity);

        Screen scr_agt_skill_editor = Screen.builder()
                .surfaceId("SCR-AGT-SKILL-EDITOR")
                .label("Skill Editor")
                .module("R05")
                .routePath("/ai/agents/:id/skills/editor")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-SKILL-EDITOR", scr_agt_skill_editor);

        Screen scr_agt_notif_prefs = Screen.builder()
                .surfaceId("SCR-AGT-NOTIF-PREFS")
                .label("Notification Preferences")
                .module("R05")
                .routePath("/ai/notifications/preferences")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(true)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-AI-144"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .personaIds(List.of())
                .build();
        map.put("SCR-AGT-NOTIF-PREFS", scr_agt_notif_prefs);

        Screen scr_lm_lang = Screen.builder()
                .surfaceId("SCR-LM-LANG")
                .label("Languages Tab")
                .module("R06")
                .routePath("/localization/languages")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-001", "US-LM-002", "US-LM-003", "US-LM-004", "US-LM-005", "US-LM-006", "US-LM-007", "US-LM-020"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-LANG", scr_lm_lang);
        scr_lm_lang.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-LANG-01").gapType("MISSING_RULE").severity("LOW").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_lang.setContentElements(List.of(
                ContentElement.builder().element("Search Input").type("Text Input").description("Filters locales by code or name (debounce 300ms)").orderIndex(0).build(),
                ContentElement.builder().element("Locale Table").type("Table").description("Columns: flag, code, name, active toggle, alternative radio, coverage bar").orderIndex(1).build(),
                ContentElement.builder().element("Active Toggle").type("Toggle Switch").description("Activates/deactivates locale; deactivation with users shows confirmation (CD-LM-01)").orderIndex(2).build(),
                ContentElement.builder().element("Alternative Radio").type("Radio Button").description("Sets fallback locale; only active locales selectable").orderIndex(3).build(),
                ContentElement.builder().element("Coverage Bar").type("Progress Bar").description("Translation coverage: >80% green, 40-80% amber, <40% red").orderIndex(4).build(),
                ContentElement.builder().element("Format Config Accordion").type("Expandable Row").description("Calendar, numeral, currency, date/time format fields per locale").orderIndex(5).build(),
                ContentElement.builder().element("Format Config Save").type("Button").description("Saves format configuration for the locale").orderIndex(6).build()
        ));

        Screen scr_lm_dict = Screen.builder()
                .surfaceId("SCR-LM-DICT")
                .label("Dictionary Tab")
                .module("R06")
                .routePath("/localization/dictionary")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-008", "US-LM-009", "US-LM-010", "US-LM-011"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-DICT", scr_lm_dict);
        scr_lm_dict.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-DICT-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing validation rules").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-LM-DICT-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_dict.setContentElements(List.of(
                ContentElement.builder().element("Search Input").type("Text Input").description("Filters by technical_name or module with highlighting (debounce 300ms)").orderIndex(0).build(),
                ContentElement.builder().element("Dictionary Table").type("Table").description("Columns: key, module, plus one column per active locale (dynamic)").orderIndex(1).build(),
                ContentElement.builder().element("Edit Button (per row)").type("Icon Button").description("Opens edit dialog with per-locale inputs; RTL dir for RTL locales").orderIndex(2).build(),
                ContentElement.builder().element("Edit Dialog - Locale Inputs").type("Textarea").description("Per-locale text input with character counter and translator_notes").orderIndex(3).build(),
                ContentElement.builder().element("Edit Dialog - Save").type("Button").description("Persists translations; status=ACTIVE immediately").orderIndex(4).build(),
                ContentElement.builder().element("Coverage Report Link").type("Link").description("Shows translated/total keys count and list of missing keys per locale").orderIndex(5).build()
        ));

        Screen scr_lm_import = Screen.builder()
                .surfaceId("SCR-LM-IMPORT")
                .label("Import/Export Tab")
                .module("R06")
                .routePath("/localization/import-export")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-012", "US-LM-013"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-IMPORT", scr_lm_import);
        scr_lm_import.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-IMPORT-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_import.setContentElements(List.of(
                ContentElement.builder().element("Export CSV Button").type("Button").description("Downloads UTF-8 BOM CSV with one column per locale").orderIndex(0).build(),
                ContentElement.builder().element("Choose CSV Upload").type("File Input").description("Validates: not empty, <=10MB, no injection; shows preview with 30-min timer").orderIndex(1).build(),
                ContentElement.builder().element("Preview Card").type("Display").description("Shows total rows, updates, new keys, errors, countdown timer").orderIndex(2).build(),
                ContentElement.builder().element("Confirm Import Button").type("Primary Button").description("Upserts translations, creates snapshot, invalidates cache (CD-LM-02)").orderIndex(3).build(),
                ContentElement.builder().element("Cancel Import Button").type("Secondary Button").description("Discards preview and uploaded file").orderIndex(4).build()
        ));

        Screen scr_lm_roll = Screen.builder()
                .surfaceId("SCR-LM-ROLL")
                .label("Rollback Tab")
                .module("R06")
                .routePath("/localization/rollback")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-014", "US-LM-015", "US-LM-016"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-ROLL", scr_lm_roll);
        scr_lm_roll.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-ROLL-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_roll.setContentElements(List.of(
                ContentElement.builder().element("Version History Table").type("Table").description("Columns: version#, type badge (EDIT/IMPORT/ROLLBACK), summary, date, creator").orderIndex(0).build(),
                ContentElement.builder().element("Current Badge").type("Tag").description("Green \"CURRENT\" tag on latest version; no rollback button for current").orderIndex(1).build(),
                ContentElement.builder().element("Rollback Button").type("Danger Button").description("Creates pre-rollback snapshot then restores selected version (CD-LM-03)").orderIndex(2).build(),
                ContentElement.builder().element("Version Detail Click").type("Link").description("Shows full snapshot data in read-only JSON viewer").orderIndex(3).build()
        ));

        Screen scr_lm_ai = Screen.builder()
                .surfaceId("SCR-LM-AI")
                .label("Agentic Translation Tab")
                .module("R06")
                .routePath("/localization/ai-translation")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-017", "US-LM-018", "US-LM-019"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-AI", scr_lm_ai);
        scr_lm_ai.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-AI-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-LM-AI-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-LM-AI-03").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_ai.setContentElements(List.of(
                ContentElement.builder().element("Target Locale Selector").type("Dropdown").description("Choose locale to translate missing keys for").orderIndex(0).build(),
                ContentElement.builder().element("Translate Missing Button").type("Primary Button").description("Sends missing keys to AI; 2s loading spinner").orderIndex(1).build(),
                ContentElement.builder().element("Auto-Applied Summary").type("Display").description("Shows count of unambiguous translations auto-applied (status=ACTIVE)").orderIndex(2).build(),
                ContentElement.builder().element("HITL Review Table").type("Table").description("Columns: key, source, AI translation, ambiguity reason, approve/reject").orderIndex(3).build(),
                ContentElement.builder().element("Approve Button (per row)").type("Button").description("Sets translation status=ACTIVE, included in bundle").orderIndex(4).build(),
                ContentElement.builder().element("Reject Button (per row)").type("Danger Button").description("Sets status=REJECTED; must re-translate manually").orderIndex(5).build(),
                ContentElement.builder().element("Approve All Pending").type("Bulk Button").description("Approves all pending HITL items at once").orderIndex(6).build()
        ));

        Screen scr_lm_format = Screen.builder()
                .surfaceId("SCR-LM-FORMAT")
                .label("Format Config Accordion")
                .module("R06")
                .routePath("/localization/format-config")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(false)
                .messageRegistryCount(0)
                .storyRefs(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-FORMAT", scr_lm_format);
        scr_lm_format.setContentElements(List.of(
                ContentElement.builder().element("Calendar System Selector").type("Dropdown").description("Choose calendar system (Gregorian, Hijri, etc.)").orderIndex(0).build(),
                ContentElement.builder().element("Numeral System Selector").type("Dropdown").description("Choose numeral system (Western Arabic, Eastern Arabic, etc.)").orderIndex(1).build(),
                ContentElement.builder().element("Currency Format").type("Text Input").description("Configure currency symbol position and formatting").orderIndex(2).build(),
                ContentElement.builder().element("Date/Time Format").type("Text Input").description("Configure date and time display patterns").orderIndex(3).build(),
                ContentElement.builder().element("Save Format Config").type("Button").description("Persists format configuration for the locale").orderIndex(4).build()
        ));

        Screen scr_lm_override = Screen.builder()
                .surfaceId("SCR-LM-OVERRIDE")
                .label("Tenant Overrides Sub-Tab")
                .module("R06")
                .routePath("/localization/overrides")
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(true)
                .loadingStates(true)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-046", "US-LM-047", "US-LM-048", "US-LM-049", "US-LM-050", "US-LM-051", "US-LM-052"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-OVERRIDE", scr_lm_override);
        scr_lm_override.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-OVERRIDE-01").gapType("MISSING_RULE").severity("HIGH").description("Missing error handling").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-LM-OVERRIDE-02").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_override.setContentElements(List.of(
                ContentElement.builder().element("Overrides Table").type("Table").description("Columns: key, global value (struck-through), override value (highlighted), locale, actions").orderIndex(0).build(),
                ContentElement.builder().element("Add Override Button").type("Button").description("Dialog: autocomplete key selector, locale selector, override value").orderIndex(1).build(),
                ContentElement.builder().element("Edit Override").type("Icon Button").description("Dialog with current value; global shown struck-through").orderIndex(2).build(),
                ContentElement.builder().element("Delete Override").type("Danger Button").description("Reverts to global value with confirmation (CD-LM-04)").orderIndex(3).build(),
                ContentElement.builder().element("Import Overrides CSV").type("Button").description("Upload CSV with key, locale, value columns; preview + confirm (CD-LM-05)").orderIndex(4).build(),
                ContentElement.builder().element("Export Overrides CSV").type("Button").description("Downloads CSV with global + override values").orderIndex(5).build()
        ));

        Screen scr_lm_switcher_auth = Screen.builder()
                .surfaceId("SCR-LM-SWITCHER-AUTH")
                .label("Language Switcher (Auth)")
                .module("R06")
                .routePath(null)
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(false)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-054", "US-LM-055", "US-LM-063", "US-LM-064"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-SWITCHER-AUTH", scr_lm_switcher_auth);
        scr_lm_switcher_auth.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-SWITCHER-AUTH-01").gapType("MISSING_RULE").severity("MEDIUM").description("Missing edge case coverage").status(Status.IDENTIFIED).build(),
                Gap.builder().gapId("GAP-SCR-LM-SWITCHER-AUTH-02").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_switcher_auth.setContentElements(List.of(
                ContentElement.builder().element("Language Dropdown Pill").type("Dropdown Pill").description("Opens dropdown with active locales (flag + native name); current has checkmark").orderIndex(0).build(),
                ContentElement.builder().element("Language Option").type("List Item").description("Click to switch language; UI updates without reload (Signal-based)").orderIndex(1).build(),
                ContentElement.builder().element("RTL/LTR Flip").type("Auto").description("Document dir attribute flips within 300ms on language switch").orderIndex(2).build(),
                ContentElement.builder().element("Preference Persistence").type("API Call").description("Saves language preference via PUT /api/v1/user/locale").orderIndex(3).build()
        ));

        Screen scr_lm_switcher_anon = Screen.builder()
                .surfaceId("SCR-LM-SWITCHER-ANON")
                .label("Language Switcher (Login)")
                .module("R06")
                .routePath(null)
                .designStatus("SPECIFIED")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.IN_DEFINITION)
                .wcag("AAA")
                .responsive(true)
                .roleAdaptive(false)
                .deepLinkable(false)
                .loadingStates(false)
                .messageRegistryCount(0)
                .storyRefs(List.of("US-LM-054"))
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .personaIds(List.of())
                .build();
        map.put("SCR-LM-SWITCHER-ANON", scr_lm_switcher_anon);
        scr_lm_switcher_anon.setGaps(List.of(
                Gap.builder().gapId("GAP-SCR-LM-SWITCHER-ANON-01").gapType("MISSING_ARTIFACT").severity("LOW").description("Missing requirement coverage").status(Status.IDENTIFIED).build()
        ));
        scr_lm_switcher_anon.setContentElements(List.of(
                ContentElement.builder().element("Language Pill Buttons").type("Pill Buttons").description("Pill-style language selector on login page (unauthenticated)").orderIndex(0).build(),
                ContentElement.builder().element("Language Selection").type("Click Action").description("Rerenders login page in selected language; stored in localStorage").orderIndex(1).build(),
                ContentElement.builder().element("Auto-Detection").type("Auto").description("Browser language auto-detected on first visit").orderIndex(2).build(),
                ContentElement.builder().element("Bundle Fetch").type("API Call").description("Fetches locale bundle from public endpoint GET /api/v1/locales/{code}/bundle").orderIndex(3).build()
        ));

        screenRepository.saveAll(map.values());
        log.info("Seeded {} screens", map.size());

        // Set transitions (second pass)
        scr_auth.setTransitionsTo(resolveScreens(map, List.of("SURF-APP-SHELL", "SCR-01")));
        scr_auth_pwd_reset_req.setTransitionsTo(resolveScreens(map, List.of("SCR-AUTH-PWD-RESET-CONFIRM")));
        scr_auth_pwd_reset_confirm.setTransitionsTo(resolveScreens(map, List.of("SCR-AUTH")));
        scr_auth_mfa_setup.setTransitionsTo(resolveScreens(map, List.of("SCR-AUTH-MFA-VERIFY")));
        scr_auth_mfa_verify.setTransitionsTo(resolveScreens(map, List.of("SCR-AUTH")));
        scr_01.setTransitionsTo(resolveScreens(map, List.of("SCR-03", "SCR-02-T1", "SCR-GV")));
        scr_03.setTransitionsTo(resolveScreens(map, List.of("SCR-01", "SCR-02-T1")));
        scr_02_t1.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T2", "SCR-02-T3", "SCR-02-T4", "SCR-02-T5", "SCR-02-T6", "SCR-02-T7", "SCR-01")));
        scr_02_t2.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T1", "SCR-02-T3")));
        scr_02_t3.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T1", "SCR-02-T2", "SCR-GV")));
        scr_02_t4.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T1", "SCR-MANDATE", "SCR-PROP")));
        scr_02_t5.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T1")));
        scr_02_t6.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T7", "SCR-02-T1")));
        scr_02_t7.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T6", "SCR-02-T1")));
        scr_02_mat.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T1", "SCR-05")));
        scr_04.setTransitionsTo(resolveScreens(map, List.of("SCR-04-M1", "SCR-01", "SCR-DIFF", "SCR-NOTIF")));
        scr_04_m1.setTransitionsTo(resolveScreens(map, List.of("SCR-04")));
        scr_05.setTransitionsTo(resolveScreens(map, List.of("SCR-02-MAT", "SCR-01")));
        scr_06.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T2", "SCR-01")));
        scr_gv.setTransitionsTo(resolveScreens(map, List.of("SCR-01", "SCR-02-T1")));
        scr_ai.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T1")));
        scr_notif.setTransitionsTo(resolveScreens(map, List.of("SCR-01", "SCR-04")));
        scr_prop.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T4", "SCR-01")));
        scr_diff.setTransitionsTo(resolveScreens(map, List.of("SCR-04", "SCR-02-T1")));
        scr_mandate.setTransitionsTo(resolveScreens(map, List.of("SCR-02-T4", "SCR-PROP")));
        scr_export.setTransitionsTo(resolveScreens(map, List.of("SCR-01")));
        scr_agt_list.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-DETAIL", "SCR-AGT-BUILDER", "SCR-AGT-GALLERY", "SCR-AGT-CHAT")));
        scr_agt_detail.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-BUILDER", "SCR-AGT-CHAT", "SCR-AGT-HISTORY")));
        scr_agt_builder.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-DETAIL", "SCR-AGT-HISTORY")));
        scr_agt_gallery.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-BUILDER")));
        scr_agt_chat.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-DETAIL")));
        scr_agt_notif.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-DETAIL", "SCR-AGT-PIPELINE", "SCR-AGT-TRAIN")));
        scr_agt_audit.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST")));
        scr_agt_pipeline.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-DETAIL")));
        scr_agt_rbac.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST")));
        scr_agt_settings.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST")));
        scr_agt_cost.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-ANALYTICS", "SCR-AGT-LIST")));
        scr_agt_knowledge.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-DETAIL")));
        scr_agt_train.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-DETAIL", "SCR-AGT-EVAL")));
        scr_agt_eval.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-TRAIN", "SCR-AGT-LIST")));
        scr_agt_hitl.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-DETAIL")));
        scr_agt_analytics.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-COST")));
        scr_agt_orch.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-LIST", "SCR-AGT-PIPELINE")));
        scr_agt_history.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-BUILDER", "SCR-AGT-DETAIL")));
        scr_agt_notif_prefs.setTransitionsTo(resolveScreens(map, List.of("SCR-AGT-NOTIF")));
        scr_lm_lang.setTransitionsTo(resolveScreens(map, List.of("SCR-LM-DICT", "SCR-LM-IMPORT", "SCR-LM-ROLL", "SCR-LM-AI", "SCR-LM-FORMAT", "SCR-LM-OVERRIDE")));
        scr_lm_dict.setTransitionsTo(resolveScreens(map, List.of("SCR-LM-LANG", "SCR-LM-IMPORT")));
        scr_lm_import.setTransitionsTo(resolveScreens(map, List.of("SCR-LM-LANG", "SCR-LM-DICT")));
        scr_lm_roll.setTransitionsTo(resolveScreens(map, List.of("SCR-LM-LANG", "SCR-LM-DICT")));
        scr_lm_ai.setTransitionsTo(resolveScreens(map, List.of("SCR-LM-LANG", "SCR-LM-DICT")));
        scr_lm_format.setTransitionsTo(resolveScreens(map, List.of("SCR-LM-LANG")));
        scr_lm_override.setTransitionsTo(resolveScreens(map, List.of("SCR-LM-LANG", "SCR-LM-DICT")));
        scr_lm_switcher_anon.setTransitionsTo(resolveScreens(map, List.of("SCR-AUTH")));
        screenRepository.saveAll(map.values());
        log.info("Transitions set for all screens");
        return map;
    }

    private List<Screen> resolveScreens(Map<String, Screen> map, List<String> ids) {
        List<Screen> result = new ArrayList<>();
        for (String id : ids) {
            Screen s = map.get(id);
            if (s != null) result.add(s);
        }
        return result;
    }

    private void seedTouchpoints(Map<String, Screen> screenMap) {
        Touchpoint tp_agt_dock = Touchpoint.builder()
                .touchpointId("TP-AGT-DOCK")
                .label("Agent Manager dock entry")
                .surfaceId("SCR-AGT-LIST")
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .targetScreen(screenMap.get("SCR-AGT-LIST"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Left-nav dock icon click").build()
                ))
                .build();
        Touchpoint tp_notif_click = Touchpoint.builder()
                .touchpointId("TP-NOTIF-CLICK")
                .label("Notification bell click")
                .surfaceId("SURF-NOTIF-DROPDOWN")
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .targetScreen(screenMap.get("SURF-NOTIF-DROPDOWN"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Header notification bell icon click").build()
                ))
                .build();
        Touchpoint tp_gallery_menu = Touchpoint.builder()
                .touchpointId("TP-GALLERY-MENU")
                .label("Gallery menu entry")
                .surfaceId("SCR-AGT-GALLERY")
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .targetScreen(screenMap.get("SCR-AGT-GALLERY"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Left-nav Gallery menu item click").build()
                ))
                .build();
        Touchpoint tp_chat_fab = Touchpoint.builder()
                .touchpointId("TP-CHAT-FAB")
                .label("Chatbot FAB entry")
                .surfaceId("SCR-AGT-CHAT")
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .targetScreen(screenMap.get("SCR-AGT-CHAT"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Floating action button (bottom-right)").build()
                ))
                .build();
        Touchpoint tp_auth_direct = Touchpoint.builder()
                .touchpointId("TP-AUTH-DIRECT")
                .label("Direct login URL")
                .surfaceId("SCR-AUTH")
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .targetScreen(screenMap.get("SCR-AUTH"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Direct URL navigation to /auth/login").build(),
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Redirect from unauthenticated route").build()
                ))
                .build();
        Touchpoint tp_pwd_reset_link = Touchpoint.builder()
                .touchpointId("TP-PWD-RESET-LINK")
                .label("Password reset link")
                .surfaceId("SCR-AUTH-PWD-RESET-REQ")
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .targetScreen(screenMap.get("SCR-AUTH-PWD-RESET-REQ"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Forgot Password link on login page").build()
                ))
                .build();
        Touchpoint tp_r04_dock = Touchpoint.builder()
                .touchpointId("TP-R04-DOCK")
                .label("Definition Manager dock entry")
                .surfaceId("SCR-01")
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "USER", "VIEWER"))
                .targetScreen(screenMap.get("SCR-01"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Left-nav dock icon click").build()
                ))
                .build();
        Touchpoint tp_r06_settings = Touchpoint.builder()
                .touchpointId("TP-R06-SETTINGS")
                .label("Localization settings entry")
                .surfaceId("SCR-LM-LANG")
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT"))
                .targetScreen(screenMap.get("SCR-LM-LANG"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Settings menu → Localization").build()
                ))
                .build();
        Touchpoint tp_global_search = Touchpoint.builder()
                .touchpointId("TP-GLOBAL-SEARCH")
                .label("Global search entry")
                .surfaceId("SURF-HEADER")
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .targetScreen(screenMap.get("SURF-HEADER"))
                .entryModes(List.of(
                        EntryMode.builder().channelId("CH-WEB-DSK").mechanism("Header search bar focus/click").build()
                ))
                .build();
        touchpointRepository.saveAll(List.of(tp_agt_dock, tp_notif_click, tp_gallery_menu, tp_chat_fab, tp_auth_direct, tp_pwd_reset_link, tp_r04_dock, tp_r06_settings, tp_global_search));
        log.info("Seeded {} touchpoints", 9);
    }

    private void seedInteractions(Map<String, Screen> screenMap) {
        Interaction int_g_001 = Interaction.builder()
                .interactionId("INT-G-001")
                .surfaceId("SURF-HEADER")
                .element("Logo / Home link")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .apiCalls(List.of())
                .onScreen(screenMap.get("SURF-HEADER"))
                .effects(List.of(
                        Effect.builder().type("navigate").target(null).targetMode("role-based").resolutionRule("role-landing-map").defaultTarget("SCR-AGT-LIST").build()
                ))
                .build();
        Interaction int_g_002 = Interaction.builder()
                .interactionId("INT-G-002")
                .surfaceId("SURF-HEADER")
                .element("Global search input")
                .trigger("type")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .apiCalls(List.of("GET /api/v1/search?q={query}"))
                .onScreen(screenMap.get("SURF-HEADER"))
                .effects(List.of(
                        Effect.builder().type("filter").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build(),
                        Effect.builder().type("navigate").target(null).targetMode("resolved").resolutionRule("entity-type-to-screen-map").defaultTarget("SCR-AGT-LIST").build()
                ))
                .build();
        Interaction int_g_003 = Interaction.builder()
                .interactionId("INT-G-003")
                .surfaceId("SURF-NOTIF-DROPDOWN")
                .element("Notification item")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .apiCalls(List.of("PATCH /api/v1/notifications/{id}/read"))
                .onScreen(screenMap.get("SURF-NOTIF-DROPDOWN"))
                .effects(List.of(
                        Effect.builder().type("navigate").target(null).targetMode("resolved").resolutionRule("entity-type-to-screen-map").defaultTarget("SCR-AGT-LIST").build()
                ))
                .build();
        Interaction int_g_004 = Interaction.builder()
                .interactionId("INT-G-004")
                .surfaceId("SURF-SESSION")
                .element("Extend session button")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("SUPER_ADMIN", "ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER", "HITL_REVIEWER", "AUDITOR"))
                .apiCalls(List.of("POST /api/v1/auth/refresh"))
                .onScreen(screenMap.get("SURF-SESSION"))
                .effects(List.of(
                        Effect.builder().type("close-overlay").target("SURF-SESSION").targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_agt_list_001 = Interaction.builder()
                .interactionId("INT-R05-AGT-LIST-001")
                .surfaceId("SCR-AGT-LIST")
                .element("Agent card")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER", "VIEWER"))
                .apiCalls(List.of("GET /api/v1/agents/{id}"))
                .onScreen(screenMap.get("SCR-AGT-LIST"))
                .effects(List.of(
                        Effect.builder().type("navigate").target("SCR-AGT-DETAIL").targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_agt_list_002 = Interaction.builder()
                .interactionId("INT-R05-AGT-LIST-002")
                .surfaceId("SCR-AGT-LIST")
                .element("Create Agent button")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .apiCalls(List.of())
                .onScreen(screenMap.get("SCR-AGT-LIST"))
                .effects(List.of(
                        Effect.builder().type("navigate").target("SCR-AGT-BUILDER").targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_agt_list_003 = Interaction.builder()
                .interactionId("INT-R05-AGT-LIST-003")
                .surfaceId("SCR-AGT-LIST")
                .element("Delete agent action")
                .trigger("click")
                .permission(null)
                .confirmationCode("CONFIRM-AGT-DELETE")
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT"))
                .apiCalls(List.of("DELETE /api/v1/agents/{id}"))
                .onScreen(screenMap.get("SCR-AGT-LIST"))
                .effects(List.of(
                        Effect.builder().type("mutation").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_builder_001 = Interaction.builder()
                .interactionId("INT-R05-BUILDER-001")
                .surfaceId("SCR-AGT-BUILDER")
                .element("Component from palette")
                .trigger("drag-drop")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .apiCalls(List.of())
                .onScreen(screenMap.get("SCR-AGT-BUILDER"))
                .effects(List.of(
                        Effect.builder().type("mutation").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_builder_002 = Interaction.builder()
                .interactionId("INT-R05-BUILDER-002")
                .surfaceId("SCR-AGT-BUILDER")
                .element("Save Draft button")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .apiCalls(List.of("PUT /api/v1/agents/{id}/draft"))
                .onScreen(screenMap.get("SCR-AGT-BUILDER"))
                .effects(List.of(
                        Effect.builder().type("mutation").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build(),
                        Effect.builder().type("toast").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_builder_003 = Interaction.builder()
                .interactionId("INT-R05-BUILDER-003")
                .surfaceId("SCR-AGT-BUILDER")
                .element("Test in Playground button")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .apiCalls(List.of("POST /api/v1/agents/{id}/test-session"))
                .onScreen(screenMap.get("SCR-AGT-BUILDER"))
                .effects(List.of(
                        Effect.builder().type("navigate").target("SCR-AGT-PLAYGROUND").targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_builder_004 = Interaction.builder()
                .interactionId("INT-R05-BUILDER-004")
                .surfaceId("SCR-AGT-BUILDER")
                .element("Publish Agent button")
                .trigger("click")
                .permission(null)
                .confirmationCode("CONFIRM-AGT-PUBLISH")
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT"))
                .apiCalls(List.of("POST /api/v1/agents/{id}/publish"))
                .onScreen(screenMap.get("SCR-AGT-BUILDER"))
                .effects(List.of(
                        Effect.builder().type("mutation").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build(),
                        Effect.builder().type("toast").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_gallery_001 = Interaction.builder()
                .interactionId("INT-R05-GALLERY-001")
                .surfaceId("SCR-AGT-GALLERY")
                .element("Template card")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .apiCalls(List.of("GET /api/v1/templates/{id}"))
                .onScreen(screenMap.get("SCR-AGT-GALLERY"))
                .effects(List.of(
                        Effect.builder().type("open-drawer").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_gallery_002 = Interaction.builder()
                .interactionId("INT-R05-GALLERY-002")
                .surfaceId("SCR-AGT-GALLERY")
                .element("Category filter")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .apiCalls(List.of("GET /api/v1/templates?category={cat}"))
                .onScreen(screenMap.get("SCR-AGT-GALLERY"))
                .effects(List.of(
                        Effect.builder().type("filter").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_gallery_003 = Interaction.builder()
                .interactionId("INT-R05-GALLERY-003")
                .surfaceId("SCR-AGT-GALLERY")
                .element("Fork template button")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER"))
                .apiCalls(List.of("POST /api/v1/templates/{id}/fork"))
                .onScreen(screenMap.get("SCR-AGT-GALLERY"))
                .effects(List.of(
                        Effect.builder().type("navigate").target("SCR-AGT-BUILDER").targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_chat_001 = Interaction.builder()
                .interactionId("INT-R05-CHAT-001")
                .surfaceId("SCR-AGT-CHAT")
                .element("Message input")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .apiCalls(List.of("POST /api/v1/agents/{id}/chat"))
                .onScreen(screenMap.get("SCR-AGT-CHAT"))
                .effects(List.of(
                        Effect.builder().type("mutation").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build(),
                        Effect.builder().type("stream-start").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_chat_002 = Interaction.builder()
                .interactionId("INT-R05-CHAT-002")
                .surfaceId("SCR-AGT-CHAT")
                .element("Stop generation button")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .apiCalls(List.of("DELETE /api/v1/agents/{id}/chat/stream"))
                .onScreen(screenMap.get("SCR-AGT-CHAT"))
                .effects(List.of(
                        Effect.builder().type("stream-stop").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        Interaction int_r05_chat_003 = Interaction.builder()
                .interactionId("INT-R05-CHAT-003")
                .surfaceId("SCR-AGT-CHAT")
                .element("Escalate to human button")
                .trigger("click")
                .permission(null)
                .confirmationCode(null)
                .personaIds(List.of())
                .roleKeys(List.of("ADMIN", "ARCHITECT", "AGENT_DESIGNER", "USER"))
                .apiCalls(List.of("POST /api/v1/agents/{id}/chat/escalate"))
                .onScreen(screenMap.get("SCR-AGT-CHAT"))
                .effects(List.of(
                        Effect.builder().type("mutation").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build(),
                        Effect.builder().type("toast").target(null).targetMode("static").resolutionRule(null).defaultTarget(null).build()
                ))
                .build();
        interactionRepository.saveAll(List.of(int_g_001, int_g_002, int_g_003, int_g_004, int_r05_agt_list_001, int_r05_agt_list_002, int_r05_agt_list_003, int_r05_builder_001, int_r05_builder_002, int_r05_builder_003, int_r05_builder_004, int_r05_gallery_001, int_r05_gallery_002, int_r05_gallery_003, int_r05_chat_001, int_r05_chat_002, int_r05_chat_003));
        log.info("Seeded {} interactions", 17);
    }

    private void seedJourneys() {
        Journey jrn_r05_001 = Journey.builder()
                .journeyId("JRN-R05-001")
                .title("Create New Agent from Gallery Template")
                .personaId("PER-UX-007")
                .roleKey("AGENT_DESIGNER")
                .goalStatement("Designer discovers a template in the gallery, forks it, customizes it in the builder, and publishes a new agent")
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.APPROVED)
                .steps(List.of(
                        JourneyStep.builder().stepId("JRN-R05-001.01").label("Navigate to Gallery via dock").preCondition("User is authenticated").postCondition("Gallery screen loads").interactionRef(null).orderIndex(0).build(),
                        JourneyStep.builder().stepId("JRN-R05-001.02").label("Filter templates by category").preCondition("Gallery is loaded").postCondition("Filtered templates displayed").interactionRef("INT-R05-GALLERY-002").orderIndex(1).build(),
                        JourneyStep.builder().stepId("JRN-R05-001.03").label("Click template card to preview").preCondition("Templates visible").postCondition("Template detail drawer open").interactionRef("INT-R05-GALLERY-001").orderIndex(2).build(),
                        JourneyStep.builder().stepId("JRN-R05-001.04").label("Fork template to start new agent").preCondition("Template drawer open").postCondition("Builder opens with forked agent").interactionRef("INT-R05-GALLERY-003").orderIndex(3).build(),
                        JourneyStep.builder().stepId("JRN-R05-001.05").label("Drag-drop components onto canvas").preCondition("Builder loaded").postCondition("Components placed on canvas").interactionRef("INT-R05-BUILDER-001").orderIndex(4).build(),
                        JourneyStep.builder().stepId("JRN-R05-001.06").label("Save draft").preCondition("Changes made in builder").postCondition("Draft saved").interactionRef("INT-R05-BUILDER-002").orderIndex(5).build(),
                        JourneyStep.builder().stepId("JRN-R05-001.07").label("Publish agent").preCondition("Agent configured").postCondition("Agent published and available").interactionRef("INT-R05-BUILDER-004").orderIndex(6).build()
                ))
                .build();
        Journey jrn_r05_002 = Journey.builder()
                .journeyId("JRN-R05-002")
                .title("View and Manage Agent List")
                .personaId("PER-UX-004")
                .roleKey("ARCHITECT")
                .goalStatement("Architect views the list of agents, selects one to view details")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.DEFINED)
                .steps(List.of(
                        JourneyStep.builder().stepId("JRN-R05-002.01").label("Navigate to Agent List via dock").preCondition("User is authenticated").postCondition("Agent List loads").interactionRef(null).orderIndex(0).build(),
                        JourneyStep.builder().stepId("JRN-R05-002.02").label("Click agent card to view details").preCondition("Agent List loaded").postCondition("Agent detail view loads").interactionRef("INT-R05-AGT-LIST-001").orderIndex(1).build()
                ))
                .build();
        Journey jrn_r05_003 = Journey.builder()
                .journeyId("JRN-R05-003")
                .title("Chat with Agent")
                .personaId("PER-UX-005")
                .roleKey("AGENT_DESIGNER")
                .goalStatement("Designer opens chat with an agent to test its responses and escalates if needed")
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.APPROVED)
                .steps(List.of(
                        JourneyStep.builder().stepId("JRN-R05-003.01").label("Send a message to the agent").preCondition("Chat screen loaded").postCondition("Agent response streams in").interactionRef("INT-R05-CHAT-001").orderIndex(0).build(),
                        JourneyStep.builder().stepId("JRN-R05-003.02").label("Escalate to human reviewer").preCondition("Agent response unsatisfactory").postCondition("HITL review request created").interactionRef("INT-R05-CHAT-003").orderIndex(1).build()
                ))
                .build();
        Journey jrn_r01_001 = Journey.builder()
                .journeyId("JRN-R01-001")
                .title("Standard Login")
                .personaId(null)
                .roleKey(null)
                .goalStatement("Unauthenticated user logs in via Keycloak and reaches their role-appropriate landing page")
                .designStatus("COMPLETE")
                .prototypeStatus("PROTOTYPED")
                .deliveryStatus("INTEGRATED")
                .status(Status.IN_IMPLEMENTATION)
                .steps(List.of(
                        JourneyStep.builder().stepId("JRN-R01-001.01").label("Navigate to login page").preCondition("User is unauthenticated").postCondition("Login form displayed").interactionRef(null).orderIndex(0).build(),
                        JourneyStep.builder().stepId("JRN-R01-001.02").label("Enter credentials and submit").preCondition("Login form displayed").postCondition("Keycloak authenticates user").interactionRef(null).orderIndex(1).build(),
                        JourneyStep.builder().stepId("JRN-R01-001.03").label("Redirect to role-based landing").preCondition("User authenticated").postCondition("User sees their landing page").interactionRef("INT-G-001").orderIndex(2).build()
                ))
                .build();
        Journey jrn_r01_002 = Journey.builder()
                .journeyId("JRN-R01-002")
                .title("Password Reset")
                .personaId(null)
                .roleKey(null)
                .goalStatement("User who forgot their password requests a reset, receives a link, and sets a new password")
                .designStatus("COMPLETE")
                .prototypeStatus("NOT_STARTED")
                .deliveryStatus("NOT_STARTED")
                .status(Status.APPROVED)
                .steps(List.of(
                        JourneyStep.builder().stepId("JRN-R01-002.01").label("Click Forgot Password on login page").preCondition("Login page displayed").postCondition("Password reset request form displayed").interactionRef(null).orderIndex(0).build(),
                        JourneyStep.builder().stepId("JRN-R01-002.02").label("Enter email and submit reset request").preCondition("Reset form displayed").postCondition("Reset email sent, confirmation shown").interactionRef(null).orderIndex(1).build(),
                        JourneyStep.builder().stepId("JRN-R01-002.03").label("Click reset link in email").preCondition("Email received").postCondition("Password reset confirmation page loads").interactionRef(null).orderIndex(2).build(),
                        JourneyStep.builder().stepId("JRN-R01-002.04").label("Enter new password and confirm").preCondition("Reset confirm page loaded").postCondition("Password changed, redirect to login").interactionRef(null).orderIndex(3).build()
                ))
                .build();
        journeyRepository.saveAll(List.of(jrn_r05_001, jrn_r05_002, jrn_r05_003, jrn_r01_001, jrn_r01_002));
        log.info("Seeded {} journeys", 5);
    }
}
