package com.emsist.designhub.systemshellgraph.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemShellGraphSeedService implements CommandLineRunner {

    private static final String GRAPH_SCOPE = SystemShellGraphQueryService.GRAPH_SCOPE;
    public static final String COMPONENT_REGISTRY_SCOPE = "SYSTEM_COMPONENT_REGISTRY";
    private static final String REQUIREMENT_SOURCE = "/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/01-Persona-Journey-Touchpoint-Variant.md";
    private static final String PREVIEW_SOURCE = "/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/02-Proposed-UI-Sketch.html";
    private static final String BPMN_SOURCE = "/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.01 Access to System Services/G01.01.01 Login Scenarios/03-Authenticate-User.bpmn";
    private static final String TENANT_LIST_REQUIREMENT_SOURCE = "/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.02 Settings Shell/G01.02.01 Tenant Registry/G01.02.01.01 View Tenant List/01-Persona-Journey-Channel-Touchpoint-Variant.md";
    private static final String TENANT_LIST_PREVIEW_SOURCE = "/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-list/tenant-list.component.html";
    private static final String TENANT_FACTSHEET_REQUIREMENT_SOURCE = "/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/data-architecture/Documentation/.Requirements/G01. Business Architecture/G01.03 Tenant Fact Sheet/G01.03.01 View Tenant Fact Sheet/01-Persona-Journey-Touchpoint-Variant.md";
    private static final String TENANT_FACTSHEET_PREVIEW_SOURCE = "/Users/mksulty/Claude/Projects/Emsist-app/.worktrees/tenant-branding-integration/frontend/src/app/_parking/tenant-factsheet/tenant-factsheet.component.html";
    private static final String COMPONENT_REGISTRY_RESOURCE = "system-shell-graph-component-registry.json";

    private final Neo4jClient neo4jClient;
    private final ObjectMapper objectMapper;
    @Value("${designhub.seed-data:true}")
    private boolean seedDataEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDataEnabled) {
            log.info("Skipping system-shell graph seed because designhub.seed-data=false.");
            return;
        }
        reseedCurrentScope();
    }

    @Transactional
    public void reseedCurrentScope() {
        SeedGraph seedGraph = buildSeedGraph();
        validateInstanceStructure(seedGraph.nodes(), seedGraph.relationships());
        resetScope();
        seedNodes(seedGraph.nodes());
        seedRelationships(seedGraph.nodes(), seedGraph.relationships());
        log.info("Seeded {} system-screen-graph nodes and {} relationships into scope {}.",
                seedGraph.nodes().size(), seedGraph.relationships().size(), GRAPH_SCOPE);
    }

    private void resetScope() {
        neo4jClient.query("""
                        MATCH (n:SystemShellGraphNode)
                        WHERE n.graphScope IN $graphScopes
                        DETACH DELETE n
                        """)
                .bind(List.of(GRAPH_SCOPE, COMPONENT_REGISTRY_SCOPE, "LOGIN_SYSTEM_SHELL"))
                .to("graphScopes")
                .run();
    }

    private void seedNodes(List<NodeSeed> nodes) {
        for (NodeSeed node : nodes) {
            String labelClause = String.join(":", node.labels());
            neo4jClient.query("MERGE (n:" + labelClause + " {code: $code}) SET n += $props")
                    .bind(node.code())
                    .to("code")
                    .bind(node.properties())
                    .to("props")
                    .run();
        }
    }

    private void seedRelationships(List<NodeSeed> nodes, List<RelationshipSeed> relationships) {
        Map<String, NodeSeed> nodeIndex = new LinkedHashMap<>();
        for (NodeSeed node : nodes) {
            String graphScope = String.valueOf(node.properties().get("graphScope"));
            nodeIndex.put(graphNodeKey(graphScope, node.code()), node);
        }

        for (RelationshipSeed relationship : relationships) {
            NodeSeed fromNode = nodeIndex.get(graphNodeKey(relationship.fromScope(), relationship.fromCode()));
            NodeSeed toNode = nodeIndex.get(graphNodeKey(relationship.toScope(), relationship.toCode()));
            RelationshipDisplayNames displayNames = relationshipDisplayNames(relationship.type(), fromNode, toNode);
            neo4jClient.query("""
                            MATCH (from:SystemShellGraphNode {code: $fromCode, graphScope: $fromScope})
                            MATCH (to:SystemShellGraphNode {code: $toCode, graphScope: $toScope})
                            MERGE (from)-[r:%s]->(to)
                            SET r.activeName = $activeName,
                                r.passiveName = $passiveName
                            """.formatted(relationship.type()))
                    .bind(relationship.fromCode())
                    .to("fromCode")
                    .bind(relationship.toCode())
                    .to("toCode")
                    .bind(relationship.fromScope())
                    .to("fromScope")
                    .bind(relationship.toScope())
                    .to("toScope")
                    .bind(displayNames.activeName())
                    .to("activeName")
                    .bind(displayNames.passiveName())
                    .to("passiveName")
                    .run();
        }
    }

    private SeedGraph buildSeedGraph() {
        List<NodeSeed> nodes = new ArrayList<>();
        List<RelationshipSeed> relationships = new ArrayList<>();

        nodes.add(instance("PER.USER", "Persona", "User", "Primary login actor.", 1000,
                props("hierarchyCode", "PER.USER", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("J01", "Journey", "Authenticate User",
                "Authenticate user and authorize access to the landing page.", 1010,
                props("hierarchyCode", "J01", "sourceArtifactPath", REQUIREMENT_SOURCE)));

        nodes.add(instance("J01.JS01", "JourneyStep", "Open Login Screen",
                "Open the login screen and expose the baseline sign-in structure.", 1020,
                props("hierarchyCode", "J01.JS01", "stepOrder", 1, "executionMethod", "mandatory", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("J01.JS02", "JourneyStep", "Select Tenant",
                "Capture the required tenant before provider resolution proceeds.", 1030,
                props("hierarchyCode", "J01.JS02", "stepOrder", 2, "executionMethod", "mandatory", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("J01.JS03", "JourneyStep", "Resolve Tenant Authentication Methods",
                "Resolve available authentication methods and tenant access-state blockers.", 1040,
                props("hierarchyCode", "J01.JS03", "stepOrder", 3, "executionMethod", "mandatory", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("J01.JS04", "JourneyStep", "Select Authentication Provider",
                "Expose and select an available provider in the login screen.", 1050,
                props("hierarchyCode", "J01.JS04", "stepOrder", 4, "executionMethod", "mandatory", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("J01.JS05", "JourneyStep", "Submit Credentials Or Redirect Sign-In",
                "Submit credentials or continue through the provider redirect path.", 1060,
                props("hierarchyCode", "J01.JS05", "stepOrder", 5, "executionMethod", "mandatory", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("J01.JS06", "JourneyStep", "Open MFA Screen",
                "Open the MFA screen when primary authentication succeeds and MFA is required.", 1070,
                props("hierarchyCode", "J01.JS06", "stepOrder", 6, "executionMethod", "conditional", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("J01.JS07", "JourneyStep", "Verify MFA Challenge",
                "Capture and validate the MFA code before issuing the final redirect.", 1080,
                props("hierarchyCode", "J01.JS07", "stepOrder", 7, "executionMethod", "conditional", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("J01.JS08", "JourneyStep", "Redirect To Landing Page",
                "Redirect the authenticated user to the target application screen and landing page.", 1090,
                props("hierarchyCode", "J01.JS08", "stepOrder", 8, "executionMethod", "conditional", "sourceArtifactPath", BPMN_SOURCE)));

        nodes.add(instance("BR01", "BusinessRule", "Tenant Resolution Required",
                "Tenant resolution must complete before authentication options can be determined.", 1100,
                props("hierarchyCode", "BR01", "ruleScope", "journey_step", "conditionExpression", "tenant_selected = true", "executionEffect", "require_step", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("BR02", "BusinessRule", "Tenant Access State Must Permit Authentication",
                "Tenant-level access state must allow the login journey to continue.", 1110,
                props("hierarchyCode", "BR02", "ruleScope", "journey_step", "conditionExpression", "tenant_exists = true AND tenant_access_state = 'allowed'", "executionEffect", "raise_blocker", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("BR03", "BusinessRule", "Available Authentication Methods Required",
                "Provider selection can proceed only when at least one authentication method is available.", 1120,
                props("hierarchyCode", "BR03", "ruleScope", "journey_step", "conditionExpression", "auth_methods_count > 0", "executionEffect", "raise_blocker", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("BR04", "BusinessRule", "Selected Provider Type Governs Sign-In Path",
                "Credential and redirect providers follow different authentication flows.", 1130,
                props("hierarchyCode", "BR04", "ruleScope", "journey_step", "conditionExpression", "selected_provider_type IN ['credential', 'redirect']", "executionEffect", "allow_step", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("BR05", "BusinessRule", "MFA Required After Successful Primary Authentication",
                "A successful primary sign-in may still require the MFA screen.", 1140,
                props("hierarchyCode", "BR05", "ruleScope", "journey_step", "conditionExpression", "primary_authentication_succeeded = true AND mfa_required = true", "executionEffect", "require_step", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("BR06", "BusinessRule", "Successful Authentication Redirects User To Landing",
                "A fully authenticated user is redirected into the destination application screen and landing page.", 1150,
                props("hierarchyCode", "BR06", "ruleScope", "journey_step", "conditionExpression", "authentication_succeeded = true", "executionEffect", "redirect_outcome", "sourceArtifactPath", REQUIREMENT_SOURCE)));

        nodes.add(instance("BL01", "Blocker", "TENANT_NOT_FOUND",
                "The resolved tenant does not exist or has been deleted.", 1200,
                props("hierarchyCode", "BL01", "blockerType", "data", "blockingEffect", "redirect_outcome", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("BL02", "Blocker", "TENANT_SUSPENDED",
                "The tenant is suspended and access is blocked before login can continue.", 1210,
                props("hierarchyCode", "BL02", "blockerType", "access", "blockingEffect", "prevent_execution", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("BL03", "Blocker", "TENANT_LICENSE_EXPIRED",
                "The tenant license is expired and normal sign-in is blocked.", 1220,
                props("hierarchyCode", "BL03", "blockerType", "access", "blockingEffect", "prevent_execution", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("BL04", "Blocker", "TENANT_ACCESS_BLOCKED",
                "The tenant access state blocks normal sign-in within the login screen.", 1230,
                props("hierarchyCode", "BL04", "blockerType", "policy", "blockingEffect", "prevent_execution", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("BL05", "Blocker", "NO_AUTH_METHODS",
                "No active authentication methods are available for the resolved tenant.", 1240,
                props("hierarchyCode", "BL05", "blockerType", "business", "blockingEffect", "prevent_execution", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("BL06", "Blocker", "INVALID_CREDENTIALS",
                "Primary authentication failed because the supplied credentials were invalid.", 1250,
                props("hierarchyCode", "BL06", "blockerType", "business", "blockingEffect", "prevent_completion", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("BL07", "Blocker", "AUTH_PROVIDER_FAILURE",
                "The external authentication provider did not return a successful login outcome.", 1260,
                props("hierarchyCode", "BL07", "blockerType", "technical", "blockingEffect", "prevent_completion", "sourceArtifactPath", BPMN_SOURCE)));
        nodes.add(instance("BL08", "Blocker", "INVALID_MFA_CODE",
                "The MFA challenge failed because the supplied verification code was invalid.", 1270,
                props("hierarchyCode", "BL08", "blockerType", "business", "blockingEffect", "prevent_completion", "sourceArtifactPath", BPMN_SOURCE)));

        nodes.add(instance("APP01", "Application", "ObjectsLogic",
                "Top-level frontend application object that owns the shell structure and shared presentation hierarchy.", 1285,
                props("hierarchyCode", "APP01", "sourceArtifactPath", PREVIEW_SOURCE)));
        nodes.add(instance("SHL01", "Shell", "login-shell",
                "Login shell that owns the dedicated header, main, and footer containers for unauthenticated screens.", 1290,
                props(
                        "hierarchyCode", "SHL01",
                        "backgroundType", "color_pattern",
                        "backgroundColorStyle", "var(--tp-primary)",
                        "backgroundPatternKey", "emsist-shell-pattern",
                        "backgroundPatternOpacity", 0.13,
                        "sourceArtifactPath", PREVIEW_SOURCE
                )));
        nodes.add(container("SHL01.SEC01", "Header Container", "Login-shell header container.", 1291));
        nodes.add(container("SHL01.SEC02", "Main Container", "Login-shell main content container.", 1292));
        nodes.add(container("SHL01.SEC03", "Footer Container", "Login-shell footer container.", 1293));
        nodes.add(instance("SHL02", "Shell", "application-shell",
                "Application shell that owns the shared header, main, and footer containers.", 1294,
                props(
                        "hierarchyCode", "SHL02",
                        "backgroundType", "color",
                        "backgroundColorStyle", "var(--tp-bg)",
                        "sourceArtifactPath", PREVIEW_SOURCE
                )));
        nodes.add(container("SHL02.SEC01", "Header Container", "Application-shell header container.", 1295));
        nodes.add(container("SHL02.SEC02", "Main Container", "Application-shell main content container.", 1296));
        nodes.add(container("SHL02.SEC03", "Footer Container", "Application-shell footer container.", 1297));
        nodes.add(instance("SHL01.SCN01", "Screen", "Login Screen",
                "Primary unauthenticated login screen.", 1300,
                props("hierarchyCode", "SHL01.SCN01", "sourceArtifactPath", PREVIEW_SOURCE)));
        nodes.add(instance("SHL01.SCN02", "Screen", "MFA Screen",
                "Step-up verification screen for MFA challenge entry.", 1310,
                props("hierarchyCode", "SHL01.SCN02", "sourceArtifactPath", PREVIEW_SOURCE)));
        nodes.add(instance("SHL01.SCN03", "Screen", "Tenant Not Found Screen",
                "Dedicated tenant-not-found screen.", 1320,
                props("hierarchyCode", "SHL01.SCN03", "sourceArtifactPath", PREVIEW_SOURCE)));
        nodes.add(instance("SHL02.SCN01", "Screen", "Administration Page",
                "Authenticated administration workspace page hosted inside ShellLayoutComponent.", 1330,
                props("hierarchyCode", "SHL02.SCN01", "sourceArtifactPath", PREVIEW_SOURCE)));
        nodes.add(instance("SHL02.SCN02", "Screen", "View Tenant List",
                "Main tenant-management screen for browsing, searching, filtering, and selecting tenants.", 1340,
                props("hierarchyCode", "SHL02.SCN02", "sourceArtifactPath", TENANT_LIST_REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL02.SCN03", "Screen", "Tenant Fact Sheet",
                "Tenant detail screen with banner hero, actions, KPI chips, and tabbed workspaces.", 1350,
                props("hierarchyCode", "SHL02.SCN03", "sourceArtifactPath", TENANT_FACTSHEET_REQUIREMENT_SOURCE)));
        nodes.add(instance("VPR90", "ViewportProfile", "Web Viewport",
                "Generic web viewport used by the preview runtime for every selected screen.", 1359,
                props(
                        "hierarchyCode", "VPR90",
                        "viewportWidth", 1440,
                        "viewportHeight", 1024,
                        "viewportCategory", "desktop",
                        "sourceArtifactPath", PREVIEW_SOURCE
                )));
        nodes.add(instance("VPR91", "ViewportProfile", "Tablet Device Viewport",
                "Generic tablet device viewport used by the preview runtime.", 1360,
                props(
                        "hierarchyCode", "VPR91",
                        "viewportWidth", 1024,
                        "viewportHeight", 1366,
                        "viewportCategory", "tablet",
                        "sourceArtifactPath", PREVIEW_SOURCE
                )));
        nodes.add(instance("VPR92", "ViewportProfile", "Mobile Device Viewport",
                "Generic mobile device viewport used by the preview runtime.", 1361,
                props(
                        "hierarchyCode", "VPR92",
                        "viewportWidth", 390,
                        "viewportHeight", 844,
                        "viewportCategory", "mobile",
                        "sourceArtifactPath", PREVIEW_SOURCE
                )));

        addSh01Structure(nodes);
        addSh02Structure(nodes);
        addSh03Structure(nodes);
        addSh04Structure(nodes);
        addSh05Structure(nodes);
        addSh06Structure(nodes);

        nodes.add(instance("SHL01.SCN01.VRS01", "ValidationRuleSet", "Login Validation Rule Set",
                "Controls runtime UI state inside the login screen.", 1800,
                props("hierarchyCode", "SHL01.SCN01.VRS01", "ruleSetType", "screen_runtime", "ruleSetScope", "screen", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN02.VRS01", "ValidationRuleSet", "MFA Validation Rule Set",
                "Controls runtime UI state inside the MFA screen.", 1810,
                props("hierarchyCode", "SHL01.SCN02.VRS01", "ruleSetType", "screen_runtime", "ruleSetScope", "screen", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN03.VRS01", "ValidationRuleSet", "Tenant Not Found Validation Rule Set",
                "Controls runtime UI state inside the tenant-not-found screen.", 1820,
                props("hierarchyCode", "SHL01.SCN03.VRS01", "ruleSetType", "screen_runtime", "ruleSetScope", "screen", "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL02.SCN02.VRS01", "ValidationRuleSet", "Tenant List Validation Rule Set",
                "Controls runtime UI state inside the tenant list screen.", 1825,
                props("hierarchyCode", "SHL02.SCN02.VRS01", "ruleSetType", "screen_runtime", "ruleSetScope", "screen", "sourceArtifactPath", TENANT_LIST_REQUIREMENT_SOURCE)));

        nodes.add(instance("SHL01.SCN01.VRS01.R01", "ValidationRule", "Show No Auth Section",
                "Shows the No Auth section when no active providers are available.", 1830,
                props("hierarchyCode", "SHL01.SCN01.VRS01.R01", "conditionExpression", "auth_methods_count = 0", "actionType", "show", "priority", 10, "stopProcessing", false, "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN01.VRS01.R02", "ValidationRule", "Show Tenant Access-State Banner",
                "Shows the blocked-state banner when tenant access conditions block login.", 1840,
                props("hierarchyCode", "SHL01.SCN01.VRS01.R02", "conditionExpression", "tenant_access_blocked = true", "actionType", "show", "priority", 20, "stopProcessing", false, "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN01.VRS01.R03", "ValidationRule", "Show Invalid Credentials Banner",
                "Shows the invalid-credentials feedback banner inside the login screen.", 1850,
                props("hierarchyCode", "SHL01.SCN01.VRS01.R03", "conditionExpression", "credentials_valid = false", "actionType", "show", "priority", 30, "stopProcessing", false, "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN01.VRS01.R04", "ValidationRule", "Reveal Credential Inputs For Credential Provider",
                "Shows username and password fields when the selected provider requires credentials.", 1860,
                props("hierarchyCode", "SHL01.SCN01.VRS01.R04", "conditionExpression", "selected_provider_type = 'credential'", "actionType", "show", "priority", 40, "stopProcessing", false, "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN01.VRS01.R05", "ValidationRule", "Collapse Credential Inputs For Redirect Provider",
                "Hides credential fields and keeps only the submit action for redirect providers.", 1870,
                props("hierarchyCode", "SHL01.SCN01.VRS01.R05", "conditionExpression", "selected_provider_type = 'redirect'", "actionType", "hide", "priority", 50, "stopProcessing", false, "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN02.VRS01.R01", "ValidationRule", "Show Invalid MFA Banner",
                "Shows invalid MFA feedback in the MFA screen.", 1880,
                props("hierarchyCode", "SHL01.SCN02.VRS01.R01", "conditionExpression", "mfa_code_valid = false", "actionType", "show", "priority", 60, "stopProcessing", false, "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL01.SCN03.VRS01.R01", "ValidationRule", "Show Tenant Not Found Screen",
                "Activates the tenant-not-found screen when tenant resolution fails.", 1890,
                props("hierarchyCode", "SHL01.SCN03.VRS01.R01", "conditionExpression", "tenant_exists = false", "actionType", "transition", "actionValue", "SHL01.SCN03", "priority", 70, "stopProcessing", true, "sourceArtifactPath", REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL02.SCN02.VRS01.R01", "ValidationRule", "Show Filter Row",
                "Shows the filter row when tenant-list filters are expanded.", 1892,
                props("hierarchyCode", "SHL02.SCN02.VRS01.R01", "conditionExpression", "filters_expanded = true", "actionType", "show", "priority", 10, "stopProcessing", false, "sourceArtifactPath", TENANT_LIST_REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL02.SCN02.VRS01.R02", "ValidationRule", "Show Empty State",
                "Shows the empty state when no tenant results are available.", 1893,
                props("hierarchyCode", "SHL02.SCN02.VRS01.R02", "conditionExpression", "tenant_result_count = 0", "actionType", "show", "priority", 20, "stopProcessing", false, "sourceArtifactPath", TENANT_LIST_REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL02.SCN02.VRS01.R03", "ValidationRule", "Show Grid View",
                "Shows the grid presentation when tenant view mode is grid.", 1894,
                props("hierarchyCode", "SHL02.SCN02.VRS01.R03", "conditionExpression", "tenant_view_mode = 'grid'", "actionType", "show", "priority", 30, "stopProcessing", false, "sourceArtifactPath", TENANT_LIST_REQUIREMENT_SOURCE)));
        nodes.add(instance("SHL02.SCN02.VRS01.R04", "ValidationRule", "Show Table View",
                "Shows the table presentation when tenant view mode is table.", 1895,
                props("hierarchyCode", "SHL02.SCN02.VRS01.R04", "conditionExpression", "tenant_view_mode = 'table'", "actionType", "show", "priority", 40, "stopProcessing", false, "sourceArtifactPath", TENANT_LIST_REQUIREMENT_SOURCE)));

        relationships.add(rel("PER.USER", "CAN_EXECUTE", "J01"));
        for (String stepCode : List.of("J01.JS01", "J01.JS02", "J01.JS03", "J01.JS04", "J01.JS05", "J01.JS06", "J01.JS07", "J01.JS08")) {
            relationships.add(rel("J01", "HAS_STEP", stepCode));
        }

        relationships.add(rel("J01.JS01", "GOVERNED_BY", "BR01"));
        relationships.add(rel("J01.JS01", "ACTIVATES_SCREEN", "SHL01.SCN01"));

        relationships.add(rel("J01.JS02", "GOVERNED_BY", "BR01"));
        relationships.add(rel("J01.JS02", "GOVERNED_BY", "BR02"));
        relationships.add(rel("J01.JS02", "HAS_BLOCKER", "BL01"));
        relationships.add(rel("J01.JS02", "HAS_BLOCKER", "BL02"));
        relationships.add(rel("J01.JS02", "HAS_BLOCKER", "BL03"));
        relationships.add(rel("J01.JS02", "HAS_BLOCKER", "BL04"));
        relationships.add(rel("J01.JS02", "ACTIVATES_SCREEN", "SHL01.SCN01"));

        relationships.add(rel("J01.JS03", "GOVERNED_BY", "BR03"));
        relationships.add(rel("J01.JS03", "HAS_BLOCKER", "BL05"));
        relationships.add(rel("J01.JS03", "ACTIVATES_SCREEN", "SHL01.SCN01"));

        relationships.add(rel("J01.JS04", "GOVERNED_BY", "BR04"));
        relationships.add(rel("J01.JS04", "ACTIVATES_SCREEN", "SHL01.SCN01"));

        relationships.add(rel("J01.JS05", "GOVERNED_BY", "BR04"));
        relationships.add(rel("J01.JS05", "HAS_BLOCKER", "BL06"));
        relationships.add(rel("J01.JS05", "HAS_BLOCKER", "BL07"));
        relationships.add(rel("J01.JS05", "ACTIVATES_SCREEN", "SHL01.SCN01"));

        relationships.add(rel("J01.JS06", "GOVERNED_BY", "BR05"));
        relationships.add(rel("J01.JS06", "ACTIVATES_SCREEN", "SHL01.SCN02"));

        relationships.add(rel("J01.JS07", "GOVERNED_BY", "BR05"));
        relationships.add(rel("J01.JS07", "HAS_BLOCKER", "BL08"));
        relationships.add(rel("J01.JS07", "ACTIVATES_SCREEN", "SHL01.SCN02"));

        relationships.add(rel("J01.JS08", "ACTIVATES_SCREEN", "SHL02.SCN01"));
        relationships.add(rel("J01.JS08", "GOVERNED_BY", "BR06"));

        relationships.add(rel("BR02", "RAISES", "BL01"));
        relationships.add(rel("BR02", "RAISES", "BL02"));
        relationships.add(rel("BR02", "RAISES", "BL03"));
        relationships.add(rel("BR02", "RAISES", "BL04"));
        relationships.add(rel("BR03", "RAISES", "BL05"));
        relationships.add(rel("BR04", "RAISES", "BL06"));
        relationships.add(rel("BR04", "RAISES", "BL07"));
        relationships.add(rel("BR05", "RAISES", "BL08"));

        relationships.add(rel("APP01", "HAS_SHELL", "SHL01"));
        relationships.add(rel("APP01", "HAS_SHELL", "SHL02"));
        relationships.add(rel("SHL01", "HAS_SECTION", "SHL01.SEC01"));
        relationships.add(rel("SHL01", "HAS_SECTION", "SHL01.SEC02"));
        relationships.add(rel("SHL01", "HAS_SECTION", "SHL01.SEC03"));
        relationships.add(rel("SHL01.SEC02", "HAS_SCREEN", "SHL01.SCN01"));
        relationships.add(rel("SHL01.SEC02", "HAS_SCREEN", "SHL01.SCN02"));
        relationships.add(rel("SHL01.SEC02", "HAS_SCREEN", "SHL01.SCN03"));
        relationships.add(rel("SHL02", "HAS_SECTION", "SHL02.SEC01"));
        relationships.add(rel("SHL02", "HAS_SECTION", "SHL02.SEC02"));
        relationships.add(rel("SHL02", "HAS_SECTION", "SHL02.SEC03"));
        relationships.add(rel("SHL02.SEC02", "HAS_SCREEN", "SHL02.SCN01"));
        relationships.add(rel("SHL02.SEC02", "HAS_SCREEN", "SHL02.SCN02"));
        relationships.add(rel("SHL02.SEC02", "HAS_SCREEN", "SHL02.SCN03"));

        addSh01Relationships(relationships);
        addSh02Relationships(relationships);
        addSh03Relationships(relationships);
        addSh04Relationships(relationships);
        addSh05Relationships(relationships);
        addSh06Relationships(relationships);

        relationships.add(rel("SHL01.SCN01", "USES_RULE_SET", "SHL01.SCN01.VRS01"));
        relationships.add(rel("SHL01.SCN02", "USES_RULE_SET", "SHL01.SCN02.VRS01"));
        relationships.add(rel("SHL01.SCN03", "USES_RULE_SET", "SHL01.SCN03.VRS01"));
        relationships.add(rel("SHL02.SCN02", "USES_RULE_SET", "SHL02.SCN02.VRS01"));

        relationships.add(rel("SHL01.SCN01.VRS01", "HAS_RULE", "SHL01.SCN01.VRS01.R01"));
        relationships.add(rel("SHL01.SCN01.VRS01", "HAS_RULE", "SHL01.SCN01.VRS01.R02"));
        relationships.add(rel("SHL01.SCN01.VRS01", "HAS_RULE", "SHL01.SCN01.VRS01.R03"));
        relationships.add(rel("SHL01.SCN01.VRS01", "HAS_RULE", "SHL01.SCN01.VRS01.R04"));
        relationships.add(rel("SHL01.SCN01.VRS01", "HAS_RULE", "SHL01.SCN01.VRS01.R05"));
        relationships.add(rel("SHL01.SCN02.VRS01", "HAS_RULE", "SHL01.SCN02.VRS01.R01"));
        relationships.add(rel("SHL01.SCN03.VRS01", "HAS_RULE", "SHL01.SCN03.VRS01.R01"));
        relationships.add(rel("SHL02.SCN02.VRS01", "HAS_RULE", "SHL02.SCN02.VRS01.R01"));
        relationships.add(rel("SHL02.SCN02.VRS01", "HAS_RULE", "SHL02.SCN02.VRS01.R02"));
        relationships.add(rel("SHL02.SCN02.VRS01", "HAS_RULE", "SHL02.SCN02.VRS01.R03"));
        relationships.add(rel("SHL02.SCN02.VRS01", "HAS_RULE", "SHL02.SCN02.VRS01.R04"));

        relationships.add(rel("SHL01.SCN01.VRS01.R01", "TARGETS", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC01"));
        relationships.add(rel("SHL01.SCN01.VRS01.R02", "TARGETS", "SHL01.SCN01.SEC03.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN01.VRS01.R03", "TARGETS", "SHL01.SCN01.SEC03.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN01.VRS01.R04", "TARGETS", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01"));
        relationships.add(rel("SHL01.SCN01.VRS01.R04", "TARGETS", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02"));
        relationships.add(rel("SHL01.SCN01.VRS01.R05", "TARGETS", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02"));
        relationships.add(rel("SHL01.SCN02.VRS01.R01", "TARGETS", "SHL01.SCN02.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN03.VRS01.R01", "TARGETS", "SHL01.SCN03"));
        relationships.add(rel("SHL01.SCN03.VRS01.R01", "TARGETS", "SHL01.SCN03.SEC03"));
        relationships.add(rel("SHL02.SCN02.VRS01.R01", "TARGETS", "SHL02.SCN02.SEC03"));
        relationships.add(rel("SHL02.SCN02.VRS01.R02", "TARGETS", "SHL02.SCN02.SEC04"));
        relationships.add(rel("SHL02.SCN02.VRS01.R03", "TARGETS", "SHL02.SCN02.SEC05.SEC01"));
        relationships.add(rel("SHL02.SCN02.VRS01.R04", "TARGETS", "SHL02.SCN02.SEC05.SEC02"));

        seedComponentRegistry(nodes, relationships);

        return new SeedGraph(nodes, relationships);
    }

    private void addSh01Structure(List<NodeSeed> nodes) {
        nodes.add(container("SHL01.SCN01.SEC01", "Welcome Container", "Top welcome copy container for the login screen.", 1400));
        nodes.add(uiElement("SHL01.SCN01.SEC01.ELT01", "Screen Title", 1401, "display", "static", "Message", List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC01.ELT02", "Screen Subtitle", 1402, "display", "static", "Message", List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(container("SHL01.SCN01.SEC02", "Logo Container", "Left-side brand logo container for the login screen.", 1403));
        nodes.add(uiElement("SHL01.SCN01.SEC02.ELT01", "SVG Logo Renderer", 1404, "display", "static", null, null));

        nodes.add(container("SHL01.SCN01.SEC03", "Login Card", "Primary login card surface inside SHL01.SCN01.", 1420));
        nodes.add(container("SHL01.SCN01.SEC03.SEC01", "Tenant Selection Step Section", "Tenant selection step region.", 1430));
        nodes.add(container("SHL01.SCN01.SEC03.SEC01.SEC01", "Tenant Selection Header Section", "Tenant step header group.", 1431));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC01.SEC01.ELT01", "Tenant Selection Step Indicator", 1432, "display", "static", null, null));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC01.SEC01.ELT02", "Tenant Selection Step Title", 1433, "display", "static", null, null));
        nodes.add(container("SHL01.SCN01.SEC03.SEC01.SEC02", "Tenant Selection Content Section", "Tenant selection controls.", 1434));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC01.SEC02.ELT01", "Tenant Registry Searchable Dropdown", 1435, "required", "static", null, List.of("--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC01.SEC02.ELT02", "Remember Tenant Selection Checkbox", 1436, "optional", "static", null, List.of("--tp-space-*")));

        nodes.add(container("SHL01.SCN01.SEC03.SEC02", "Authentication Methods Step Section", "Authentication method step region.", 1440));
        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC01", "Authentication Methods Header Section", "Authentication step header group.", 1441));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC01.ELT01", "Authentication Methods Step Indicator", 1442, "display", "static", null, null));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC01.ELT02", "Authentication Methods Step Title", 1443, "display", "static", null, null));
        nodes.add(conditionalContainer("SHL01.SCN01.SEC03.SEC02.SEC02", "Status Message Section", "Status and feedback region.", 1444));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC02.ELT01", "Status Banner", 1445, "feedback", "conditional", null, List.of("--tp-text-*", "--tp-space-*")));

        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC03", "Provider Selection Section", "Provider selection and no-auth outcomes.", 1450));
        nodes.add(conditionalContainer("SHL01.SCN01.SEC03.SEC02.SEC03.SEC01", "No Auth Section", "No-auth outcome container.", 1451));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT01", "No Auth Title", 1452, "display", "conditional", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT02", "No Auth Description", 1453, "display", "conditional", null, List.of("--tp-text-*", "--tp-space-*")));

        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02", "Auth Provider Section", "Repeatable provider option container.", 1460));
        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01", "Provider Header Section", "Provider header row.", 1461));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT01", "Auth Logo", 1462, "display", "static", null, List.of("--tp-primary-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT02", "Auth Method Name", 1463, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT03", "Remember Me Selection", 1464, "optional", "static", null, List.of("--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT04", "Expand Action", 1465, "toggle", "static", null, List.of("--tp-space-*")));

        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02", "Provider Variant Section", "Provider-specific variant body.", 1470));
        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01", "User Name Field Section", "Credential username field group.", 1472));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT01", "User Name Label", 1473, "display", "conditional", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT02", "User Name Input", 1474, "required", "conditional", null, List.of("--tp-space-*")));
        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02", "Password Field Section", "Credential password field group.", 1475));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT01", "Password Label", 1476, "display", "conditional", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT02", "Password Input", 1477, "required", "conditional", null, List.of("--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT03", "Password Visibility Toggle", 1478, "toggle", "conditional", null, List.of("--tp-space-*")));
        nodes.add(container("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03", "Sign In Action Section", "Provider submit action group.", 1479));
        nodes.add(uiElement("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03.ELT01", "Sign In Action", 1480, "submit", "static", null, List.of("--tp-space-*")));

    }

    private void addSh02Structure(List<NodeSeed> nodes) {
        nodes.add(container("SHL01.SCN02.SEC02", "Verification Modal Section", "Primary MFA modal container.", 1510));
        nodes.add(container("SHL01.SCN02.SEC02.SEC01", "Verification Header Section", "MFA header group.", 1511));
        nodes.add(uiElement("SHL01.SCN02.SEC02.SEC01.ELT01", "Verification Title", 1512, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN02.SEC02.SEC01.ELT02", "Verification Description", 1513, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));

        nodes.add(conditionalContainer("SHL01.SCN02.SEC02.SEC02", "Status Message Section", "MFA status-message region.", 1514));
        nodes.add(uiElement("SHL01.SCN02.SEC02.SEC02.ELT01", "Status Banner", 1515, "feedback", "conditional", null, List.of("--tp-text-*", "--tp-space-*")));

        nodes.add(container("SHL01.SCN02.SEC02.SEC03", "Verification Code Section", "OTP input container backed by the registered InputOtp asset.", 1520));
        nodes.add(uiElement("SHL01.SCN02.SEC02.SEC03.ELT01", "Verification Code Input", 1521, "required", "static", "INPUTOTP", List.of("--tp-space-*")));

        nodes.add(container("SHL01.SCN02.SEC02.SEC04", "Verification Action Bar", "MFA action buttons.", 1530));
        nodes.add(uiElement("SHL01.SCN02.SEC02.SEC04.ELT01", "Back Action", 1531, "navigate", "static", null, List.of("--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN02.SEC02.SEC04.ELT02", "Verify Action", 1532, "submit", "static", null, List.of("--tp-space-*")));
    }

    private void addSh03Structure(List<NodeSeed> nodes) {
        nodes.add(container("SHL01.SCN03.SEC01", "Screen Header Section", "Tenant-not-found shell header container.", 1600));
        nodes.add(uiElement("SHL01.SCN03.SEC01.ELT01", "Screen Title", 1601, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN03.SEC01.ELT02", "Screen Subtitle", 1602, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));

        nodes.add(container("SHL01.SCN03.SEC02", "Logo Section", "Tenant-not-found shell logo container.", 1610));
        nodes.add(uiElement("SHL01.SCN03.SEC02.ELT01", "SVG Logo Renderer", 1611, "display", "static", null, List.of("--tp-primary-*", "--tp-space-*")));

        nodes.add(container("SHL01.SCN03.SEC03", "Tenant Not Found Section", "Tenant-not-found outcome container.", 1620));
        nodes.add(uiElement("SHL01.SCN03.SEC03.ELT01", "Not Found Title", 1621, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN03.SEC03.ELT02", "Not Found Description", 1622, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL01.SCN03.SEC03.ELT03", "Back To Login Action", 1623, "navigate", "static", null, List.of("--tp-space-*")));
    }

    private void addSh04Structure(List<NodeSeed> nodes) {
        nodes.add(container("SHL02.SCN01.SEC01", "Header Bar Section", "Authenticated shell header bar.", 1700));
        nodes.add(uiElement("SHL02.SCN01.SEC01.ELT01", "Shell Title", 1701, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL02.SCN01.SEC01.ELT02", "Header Actions", 1702, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));

        nodes.add(container("SHL02.SCN01.SEC02", "Navigation Dock Section", "Authenticated navigation dock.", 1710));
        nodes.add(uiElement("SHL02.SCN01.SEC02.ELT01", "Navigation Dock Label", 1711, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL02.SCN01.SEC02.ELT02", "Active Module", 1712, "display", "static", null, List.of("--tp-primary-*", "--tp-space-*")));

        nodes.add(container("SHL02.SCN01.SEC03", "Workspace Content Section", "Authenticated workspace content region.", 1720));
        nodes.add(uiElement("SHL02.SCN01.SEC03.ELT01", "Workspace Title", 1721, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElement("SHL02.SCN01.SEC03.ELT02", "Workspace Description", 1722, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
    }

    private void addSh05Structure(List<NodeSeed> nodes) {
        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC01", "List Header Section", "Tenant-list title region.", 1730));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC01.ELT01", "Screen Title",
                "Tenant-list title element.", 1731, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02", "Toolbar Section", "Tenant-list primary toolbar.", 1740));
        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC01", "Search Section", "Search entry region for the tenant list.", 1741));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC01.ELT01", "Search Input",
                "Search input for tenant list filtering.", 1742, "search", "static", "InputText", List.of("--p-inputtext-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC02", "Filter Toggle Section", "Toolbar filter-toggle action region.", 1743));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC02.ELT01", "Filter Toggle Action",
                "Toolbar action that reveals filter controls.", 1744, "toggle", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC03", "View Mode Section", "Toolbar view-mode control region.", 1745));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC03.ELT01", "View Mode Toggle",
                "Toggle control switching between grid and table presentation.", 1746, "toggle", "static", "ToggleButton", List.of("--p-togglebutton-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC04", "Create Tenant Action Section", "Toolbar create-tenant action region.", 1747));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC02.SEC04.ELT01", "New Tenant Action",
                "Primary action that starts tenant creation.", 1748, "submit", "static", "Button", List.of("--p-button-*", "--tp-space-*")));

        nodes.add(statefulContainerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC03", "Filter Row Section", "Expanded tenant-list filter controls.", 1750));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC03.ELT01", "Type Filter Select",
                "Tenant-type filter selection.", 1751, "filter", "static", "Select", List.of("--p-select-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC03.ELT02", "Status Filter Select",
                "Tenant-status filter selection.", 1752, "filter", "static", "Select", List.of("--p-select-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC03.ELT03", "Clear Filters Action",
                "Action that clears active tenant-list filters.", 1753, "action", "static", "Button", List.of("--p-button-*", "--tp-space-*")));

        nodes.add(statefulContainerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC04", "Empty State Section", "Zero-results or no-tenants state.", 1760));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC04.ELT01", "Empty State Title",
                "Title shown when the tenant list has no visible results.", 1761, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC04.ELT02", "Empty State Message",
                "Message shown when the tenant list has no visible results.", 1762, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC04.ELT03", "Empty State Action",
                "Recovery action shown in the empty state.", 1763, "action", "static", "Button", List.of("--p-button-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05", "Results Surface Section", "Main results area for tenant browsing.", 1770));
        nodes.add(statefulContainerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC01", "Grid View Section", "Grid presentation for tenant results.", 1771));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC01.ELT01", "Tenant Card Result",
                "Representative tenant card unit in the grid view.", 1772, "display", "static", null, List.of("--tp-surface-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC01.ELT02", "Tenant Type Badge",
                "Representative tenant type badge within the grid card.", 17725, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(statefulContainerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC02", "Table View Section", "Table presentation for tenant results.", 1773));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC02.ELT01", "Tenant Data Table",
                "Structured tenant table view.", 1774, "display", "static", "Table", List.of("--p-datatable-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC02.ELT02", "Tenant Type Badge",
                "Representative tenant type badge within the table view.", 17745, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC03", "Pagination Section", "Paging control region for tenant results.", 1775));
        nodes.add(uiElementAt(TENANT_LIST_PREVIEW_SOURCE, "SHL02.SCN02.SEC05.SEC03.ELT01", "Tenant Paginator",
                "Paginator for tenant list navigation.", 1776, "navigate", "static", "Paginator", List.of("--p-paginator-*", "--tp-space-*")));
    }

    private void addSh06Structure(List<NodeSeed> nodes) {
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC01", "Breadcrumb Section", "Tenant fact-sheet breadcrumb region.", 1780));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC01.ELT01", "Tenant Breadcrumb Trail",
                "Breadcrumb path for the tenant fact sheet.", 1781, "navigate", "static", "Breadcrumb", List.of("--p-breadcrumb-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02", "Banner Hero Section", "Tenant fact-sheet banner hero.", 1790));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01", "Tenant Identity Section", "Tenant identity, status, and KPI content.", 1791));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT01", "Tenant Logo",
                "Tenant logo or initials avatar.", 1792, "display", "static", null, List.of("--tp-primary-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT02", "Tenant Name",
                "Primary tenant title in the banner hero.", 1793, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT03", "Tenant Type Badge",
                "Tenant classification badge.", 1794, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT04", "Tenant Status Badge",
                "Lifecycle state badge for the selected tenant.", 1795, "display", "static", null, List.of("--tp-success-*", "--tp-warning-*", "--tp-danger-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT05", "Tenant Health Badge",
                "Health indicator badge for the selected tenant.", 1796, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT06", "Tenant Short Name",
                "Short-name slug shown under the banner title.", 1797, "display", "static", null, List.of("--tp-text-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT07", "Users KPI Chip",
                "User-count KPI chip in the banner hero.", 1798, "display", "static", "Chip", List.of("--p-chip-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT08", "Agents KPI Chip",
                "Agent-count KPI chip in the banner hero.", 1799, "display", "static", "Chip", List.of("--p-chip-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT09", "Object Types KPI Chip",
                "Object-type-count KPI chip in the banner hero.", 1800, "display", "static", "Chip", List.of("--p-chip-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC01.ELT10", "License KPI Chip",
                "License-utilization KPI chip in the banner hero.", 1801, "display", "static", "Chip", List.of("--p-chip-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC02", "Tenant Actions Section", "Tenant fact-sheet action region.", 1802));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC02.ELT01", "Back Action",
                "Return from the tenant fact sheet to the previous screen.", 1803, "navigate", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC02.ELT02", "Edit Action",
                "Enter tenant factsheet edit mode.", 1804, "action", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC02.SEC02.ELT03", "Lifecycle Action",
                "Contextual lifecycle action for the selected tenant.", 1805, "action", "static", "Button", List.of("--p-button-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC03", "Tab Bar Section", "Primary factsheet tab bar.", 1810));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC03.ELT01", "Factsheet Tabs",
                "Primary factsheet tab bar.", 1811, "navigate", "static", "Tabs", List.of("--p-tabs-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04", "Users Tab Section", "Active users tab workspace.", 1812));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC01", "Users Toolbar Section", "Users-tab search and actions toolbar.", 1813));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC01.ELT01", "Users Search Input",
                "Search field for tenant users.", 1814, "search", "static", "InputText", List.of("--p-inputtext-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC01.ELT02", "Users Filter Toggle",
                "Filter toggle for users tab.", 1815, "toggle", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC01.ELT03", "Invite User Action",
                "Primary action for inviting a tenant user.", 1816, "submit", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC02", "Users Results Section", "Users table and paging region.", 1817));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC02.ELT01", "Users Table",
                "Tabular users listing for the tenant.", 1818, "display", "static", "Table", List.of("--p-datatable-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC02.ELT02", "Users Paginator",
                "Paginator for the users table.", 1819, "navigate", "static", "Paginator", List.of("--p-paginator-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC04.SEC02.ELT03", "User Status Badge",
                "Representative user status badge within the users table.", 1820, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC05", "Branding Tab Section", "Branding tab workspace.", 1825));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC05.ELT01", "Branding Preview Action",
                "Preview action for branding changes.", 1827, "action", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC05.ELT02", "Branding Publish Action",
                "Publish action for branding changes.", 1828, "submit", "static", "Button", List.of("--p-button-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06", "Integrations Tab Section", "Integrations tab workspace.", 1830));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC01", "Integrations Toolbar Section", "Integrations tab toolbar.", 1831));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC01.ELT01", "Integrations Search Input",
                "Search field for integrations.", 1832, "search", "static", "InputText", List.of("--p-inputtext-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC01.ELT02", "Integrations Filter Toggle",
                "Filter toggle for integrations.", 1833, "toggle", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC01.ELT03", "Integrations View Toggle",
                "Grid/table toggle for integrations.", 1834, "toggle", "static", "ToggleButton", List.of("--p-togglebutton-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC02", "Integrations Results Section", "Integrations cards and paging.", 1835));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC02.ELT01", "Integration Protocol Badge",
                "Representative protocol badge in the integrations grid.", 1836, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC02.ELT02", "Integration Status Badge",
                "Representative enabled/disabled badge in the integrations grid.", 1837, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC06.SEC02.ELT03", "Integrations Paginator",
                "Paginator for integrations results.", 1838, "navigate", "static", "Paginator", List.of("--p-paginator-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07", "Dictionary Tab Section", "Dictionary tab workspace.", 1840));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC01", "Dictionary Toolbar Section", "Dictionary tab toolbar.", 1841));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC01.ELT01", "Dictionary Search Input",
                "Search field for dictionary entries.", 1842, "search", "static", "InputText", List.of("--p-inputtext-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC01.ELT02", "Dictionary Filter Toggle",
                "Filter toggle for dictionary results.", 1843, "toggle", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC01.ELT03", "Dictionary View Toggle",
                "Grid/table toggle for dictionary results.", 1844, "toggle", "static", "ToggleButton", List.of("--p-togglebutton-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC02", "Dictionary Results Section", "Dictionary results and paging.", 1845));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC02.ELT01", "Dictionary Table",
                "Tabular dictionary view.", 1846, "display", "static", "Table", List.of("--p-datatable-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC02.ELT02", "Dictionary Origin Badge",
                "Representative origin badge in dictionary results.", 1847, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC07.SEC02.ELT03", "Dictionary Paginator",
                "Paginator for dictionary results.", 1848, "navigate", "static", "Paginator", List.of("--p-paginator-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC08", "Agents Tab Section", "Agents tab workspace.", 1850));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC08.ELT01", "Agent Status Badge",
                "Representative agent status badge in the agents grid.", 1851, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC09", "Studio Tab Section", "Studio tab workspace.", 1860));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC09.ELT01", "Studio Search Input",
                "Search field for studio processes.", 1862, "search", "static", "InputText", List.of("--p-inputtext-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC09.ELT02", "Open Studio Action",
                "Action to open Studio.", 1863, "navigate", "static", "Button", List.of("--p-button-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10", "Audit Tab Section", "Audit log tab workspace.", 1870));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10.SEC01", "Audit Toolbar Section", "Audit tab toolbar.", 1871));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10.SEC01.ELT01", "Audit Search Input",
                "Search field for audit log entries.", 1872, "search", "static", "InputText", List.of("--p-inputtext-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10.SEC01.ELT02", "Audit Filter Toggle",
                "Filter toggle for audit results.", 1873, "toggle", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10.SEC01.ELT03", "Audit View Toggle",
                "Grid/table toggle for audit results.", 1874, "toggle", "static", "ToggleButton", List.of("--p-togglebutton-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10.SEC02", "Audit Results Section", "Audit results and paging.", 1875));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10.SEC02.ELT01", "Audit Table",
                "Tabular audit log view.", 1876, "display", "static", "Table", List.of("--p-datatable-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC10.SEC02.ELT02", "Audit Paginator",
                "Paginator for audit results.", 1877, "navigate", "static", "Paginator", List.of("--p-paginator-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11", "Health Tab Section", "Health checks tab workspace.", 1880));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11.SEC01", "Health Toolbar Section", "Health tab toolbar.", 1881));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11.SEC01.ELT01", "Health Search Input",
                "Search field for health checks.", 1882, "search", "static", "InputText", List.of("--p-inputtext-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11.SEC01.ELT02", "Health Filter Toggle",
                "Filter toggle for health results.", 1883, "toggle", "static", "Button", List.of("--p-button-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11.SEC01.ELT03", "Health View Toggle",
                "Grid/table toggle for health results.", 1884, "toggle", "static", "ToggleButton", List.of("--p-togglebutton-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11.SEC02", "Health Results Section", "Health cards and paging.", 1885));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11.SEC02.ELT01", "Health Status Badge",
                "Representative health status badge in the health grid.", 1886, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC11.SEC02.ELT02", "Health Paginator",
                "Paginator for health results.", 1887, "navigate", "static", "Paginator", List.of("--p-paginator-*", "--tp-space-*")));

        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC12", "License Tab Section", "License tab workspace.", 1890));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC12.SEC01", "License Summary Section", "License summary region.", 1891));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC12.SEC01.ELT01", "License Status Badge",
                "Status badge for the tenant license.", 1892, "display", "static", "Tag", List.of("--p-tag-*", "--tp-space-*")));
        nodes.add(containerAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC12.SEC02", "License Results Section", "License allocations table region.", 1893));
        nodes.add(uiElementAt(TENANT_FACTSHEET_PREVIEW_SOURCE, "SHL02.SCN03.SEC12.SEC02.ELT01", "License Table",
                "Tabular license allocation view.", 1894, "display", "static", "Table", List.of("--p-datatable-*", "--tp-space-*")));
    }

    private void addSh01Relationships(List<RelationshipSeed> relationships) {
        relationships.add(rel("SHL01.SCN01", "HAS_SECTION", "SHL01.SCN01.SEC01"));
        relationships.add(rel("SHL01.SCN01", "HAS_SECTION", "SHL01.SCN01.SEC02"));
        relationships.add(rel("SHL01.SCN01", "HAS_SECTION", "SHL01.SCN01.SEC03"));

        relationships.add(rel("SHL01.SCN01.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN01.SEC02", "HAS_ELEMENT", "SHL01.SCN01.SEC02.ELT01"));

        relationships.add(rel("SHL01.SCN01.SEC03", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC01"));
        relationships.add(rel("SHL01.SCN01.SEC03", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC01", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC01.SEC01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC01", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC01.SEC02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC01.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC01.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC01.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC01.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC01.SEC02", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC01.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC01.SEC02", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC01.SEC02.ELT02"));

        relationships.add(rel("SHL01.SCN01.SEC03.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC02", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT03"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC01.ELT04"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02", "HAS_SECTION", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT02"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC02.ELT03"));
        relationships.add(rel("SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03", "HAS_ELEMENT", "SHL01.SCN01.SEC03.SEC02.SEC03.SEC02.SEC02.SEC03.ELT01"));

    }

    private void addSh02Relationships(List<RelationshipSeed> relationships) {
        relationships.add(rel("SHL01.SCN02", "HAS_SECTION", "SHL01.SCN02.SEC02"));
        relationships.add(rel("SHL01.SCN02.SEC02", "HAS_SECTION", "SHL01.SCN02.SEC02.SEC01"));
        relationships.add(rel("SHL01.SCN02.SEC02", "HAS_SECTION", "SHL01.SCN02.SEC02.SEC02"));
        relationships.add(rel("SHL01.SCN02.SEC02", "HAS_SECTION", "SHL01.SCN02.SEC02.SEC03"));
        relationships.add(rel("SHL01.SCN02.SEC02", "HAS_SECTION", "SHL01.SCN02.SEC02.SEC04"));
        relationships.add(rel("SHL01.SCN02.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN02.SEC02.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN02.SEC02.SEC01", "HAS_ELEMENT", "SHL01.SCN02.SEC02.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN02.SEC02.SEC02", "HAS_ELEMENT", "SHL01.SCN02.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN02.SEC02.SEC03", "HAS_ELEMENT", "SHL01.SCN02.SEC02.SEC03.ELT01"));
        relationships.add(rel("SHL01.SCN02.SEC02.SEC04", "HAS_ELEMENT", "SHL01.SCN02.SEC02.SEC04.ELT01"));
        relationships.add(rel("SHL01.SCN02.SEC02.SEC04", "HAS_ELEMENT", "SHL01.SCN02.SEC02.SEC04.ELT02"));
    }

    private void addSh03Relationships(List<RelationshipSeed> relationships) {
        relationships.add(rel("SHL01.SCN03", "HAS_SECTION", "SHL01.SCN03.SEC01"));
        relationships.add(rel("SHL01.SCN03", "HAS_SECTION", "SHL01.SCN03.SEC02"));
        relationships.add(rel("SHL01.SCN03", "HAS_SECTION", "SHL01.SCN03.SEC03"));
        relationships.add(rel("SHL01.SCN03.SEC01", "HAS_ELEMENT", "SHL01.SCN03.SEC01.ELT01"));
        relationships.add(rel("SHL01.SCN03.SEC01", "HAS_ELEMENT", "SHL01.SCN03.SEC01.ELT02"));
        relationships.add(rel("SHL01.SCN03.SEC02", "HAS_ELEMENT", "SHL01.SCN03.SEC02.ELT01"));
        relationships.add(rel("SHL01.SCN03.SEC03", "HAS_ELEMENT", "SHL01.SCN03.SEC03.ELT01"));
        relationships.add(rel("SHL01.SCN03.SEC03", "HAS_ELEMENT", "SHL01.SCN03.SEC03.ELT02"));
        relationships.add(rel("SHL01.SCN03.SEC03", "HAS_ELEMENT", "SHL01.SCN03.SEC03.ELT03"));
    }

    private void addSh04Relationships(List<RelationshipSeed> relationships) {
        relationships.add(rel("SHL02.SCN01", "HAS_SECTION", "SHL02.SCN01.SEC01"));
        relationships.add(rel("SHL02.SCN01", "HAS_SECTION", "SHL02.SCN01.SEC02"));
        relationships.add(rel("SHL02.SCN01", "HAS_SECTION", "SHL02.SCN01.SEC03"));
        relationships.add(rel("SHL02.SCN01.SEC01", "HAS_ELEMENT", "SHL02.SCN01.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN01.SEC01", "HAS_ELEMENT", "SHL02.SCN01.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN01.SEC02", "HAS_ELEMENT", "SHL02.SCN01.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN01.SEC02", "HAS_ELEMENT", "SHL02.SCN01.SEC02.ELT02"));
        relationships.add(rel("SHL02.SCN01.SEC03", "HAS_ELEMENT", "SHL02.SCN01.SEC03.ELT01"));
        relationships.add(rel("SHL02.SCN01.SEC03", "HAS_ELEMENT", "SHL02.SCN01.SEC03.ELT02"));
    }

    private void addSh05Relationships(List<RelationshipSeed> relationships) {
        relationships.add(rel("SHL02.SCN02", "HAS_SECTION", "SHL02.SCN02.SEC01"));
        relationships.add(rel("SHL02.SCN02", "HAS_SECTION", "SHL02.SCN02.SEC02"));
        relationships.add(rel("SHL02.SCN02", "HAS_SECTION", "SHL02.SCN02.SEC03"));
        relationships.add(rel("SHL02.SCN02", "HAS_SECTION", "SHL02.SCN02.SEC04"));
        relationships.add(rel("SHL02.SCN02", "HAS_SECTION", "SHL02.SCN02.SEC05"));

        relationships.add(rel("SHL02.SCN02.SEC01", "HAS_ELEMENT", "SHL02.SCN02.SEC01.ELT01"));

        relationships.add(rel("SHL02.SCN02.SEC02", "HAS_SECTION", "SHL02.SCN02.SEC02.SEC01"));
        relationships.add(rel("SHL02.SCN02.SEC02", "HAS_SECTION", "SHL02.SCN02.SEC02.SEC02"));
        relationships.add(rel("SHL02.SCN02.SEC02", "HAS_SECTION", "SHL02.SCN02.SEC02.SEC03"));
        relationships.add(rel("SHL02.SCN02.SEC02", "HAS_SECTION", "SHL02.SCN02.SEC02.SEC04"));
        relationships.add(rel("SHL02.SCN02.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN02.SEC02.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN02.SEC02.SEC02", "HAS_ELEMENT", "SHL02.SCN02.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN02.SEC02.SEC03", "HAS_ELEMENT", "SHL02.SCN02.SEC02.SEC03.ELT01"));
        relationships.add(rel("SHL02.SCN02.SEC02.SEC04", "HAS_ELEMENT", "SHL02.SCN02.SEC02.SEC04.ELT01"));

        relationships.add(rel("SHL02.SCN02.SEC03", "HAS_ELEMENT", "SHL02.SCN02.SEC03.ELT01"));
        relationships.add(rel("SHL02.SCN02.SEC03", "HAS_ELEMENT", "SHL02.SCN02.SEC03.ELT02"));
        relationships.add(rel("SHL02.SCN02.SEC03", "HAS_ELEMENT", "SHL02.SCN02.SEC03.ELT03"));

        relationships.add(rel("SHL02.SCN02.SEC04", "HAS_ELEMENT", "SHL02.SCN02.SEC04.ELT01"));
        relationships.add(rel("SHL02.SCN02.SEC04", "HAS_ELEMENT", "SHL02.SCN02.SEC04.ELT02"));
        relationships.add(rel("SHL02.SCN02.SEC04", "HAS_ELEMENT", "SHL02.SCN02.SEC04.ELT03"));

        relationships.add(rel("SHL02.SCN02.SEC05", "HAS_SECTION", "SHL02.SCN02.SEC05.SEC01"));
        relationships.add(rel("SHL02.SCN02.SEC05", "HAS_SECTION", "SHL02.SCN02.SEC05.SEC02"));
        relationships.add(rel("SHL02.SCN02.SEC05", "HAS_SECTION", "SHL02.SCN02.SEC05.SEC03"));
        relationships.add(rel("SHL02.SCN02.SEC05.SEC01", "HAS_ELEMENT", "SHL02.SCN02.SEC05.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN02.SEC05.SEC01", "HAS_ELEMENT", "SHL02.SCN02.SEC05.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN02.SEC05.SEC02", "HAS_ELEMENT", "SHL02.SCN02.SEC05.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN02.SEC05.SEC02", "HAS_ELEMENT", "SHL02.SCN02.SEC05.SEC02.ELT02"));
        relationships.add(rel("SHL02.SCN02.SEC05.SEC03", "HAS_ELEMENT", "SHL02.SCN02.SEC05.SEC03.ELT01"));
    }

    private void addSh06Relationships(List<RelationshipSeed> relationships) {
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC01"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC02"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC03"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC04"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC05"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC06"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC07"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC08"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC09"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC10"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC11"));
        relationships.add(rel("SHL02.SCN03", "HAS_SECTION", "SHL02.SCN03.SEC12"));

        relationships.add(rel("SHL02.SCN03.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC01.ELT01"));

        relationships.add(rel("SHL02.SCN03.SEC02", "HAS_SECTION", "SHL02.SCN03.SEC02.SEC01"));
        relationships.add(rel("SHL02.SCN03.SEC02", "HAS_SECTION", "SHL02.SCN03.SEC02.SEC02"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT03"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT04"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT05"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT06"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT07"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT08"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT09"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC01.ELT10"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC02.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC02.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC02.SEC02.ELT03"));

        relationships.add(rel("SHL02.SCN03.SEC03", "HAS_ELEMENT", "SHL02.SCN03.SEC03.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC04", "HAS_SECTION", "SHL02.SCN03.SEC04.SEC01"));
        relationships.add(rel("SHL02.SCN03.SEC04", "HAS_SECTION", "SHL02.SCN03.SEC04.SEC02"));
        relationships.add(rel("SHL02.SCN03.SEC04.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC04.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC04.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC04.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC04.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC04.SEC01.ELT03"));
        relationships.add(rel("SHL02.SCN03.SEC04.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC04.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC04.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC04.SEC02.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC04.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC04.SEC02.ELT03"));

        relationships.add(rel("SHL02.SCN03.SEC05", "HAS_ELEMENT", "SHL02.SCN03.SEC05.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC05", "HAS_ELEMENT", "SHL02.SCN03.SEC05.ELT02"));

        relationships.add(rel("SHL02.SCN03.SEC06", "HAS_SECTION", "SHL02.SCN03.SEC06.SEC01"));
        relationships.add(rel("SHL02.SCN03.SEC06", "HAS_SECTION", "SHL02.SCN03.SEC06.SEC02"));
        relationships.add(rel("SHL02.SCN03.SEC06.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC06.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC06.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC06.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC06.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC06.SEC01.ELT03"));
        relationships.add(rel("SHL02.SCN03.SEC06.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC06.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC06.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC06.SEC02.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC06.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC06.SEC02.ELT03"));

        relationships.add(rel("SHL02.SCN03.SEC07", "HAS_SECTION", "SHL02.SCN03.SEC07.SEC01"));
        relationships.add(rel("SHL02.SCN03.SEC07", "HAS_SECTION", "SHL02.SCN03.SEC07.SEC02"));
        relationships.add(rel("SHL02.SCN03.SEC07.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC07.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC07.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC07.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC07.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC07.SEC01.ELT03"));
        relationships.add(rel("SHL02.SCN03.SEC07.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC07.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC07.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC07.SEC02.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC07.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC07.SEC02.ELT03"));

        relationships.add(rel("SHL02.SCN03.SEC08", "HAS_ELEMENT", "SHL02.SCN03.SEC08.ELT01"));

        relationships.add(rel("SHL02.SCN03.SEC09", "HAS_ELEMENT", "SHL02.SCN03.SEC09.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC09", "HAS_ELEMENT", "SHL02.SCN03.SEC09.ELT02"));

        relationships.add(rel("SHL02.SCN03.SEC10", "HAS_SECTION", "SHL02.SCN03.SEC10.SEC01"));
        relationships.add(rel("SHL02.SCN03.SEC10", "HAS_SECTION", "SHL02.SCN03.SEC10.SEC02"));
        relationships.add(rel("SHL02.SCN03.SEC10.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC10.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC10.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC10.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC10.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC10.SEC01.ELT03"));
        relationships.add(rel("SHL02.SCN03.SEC10.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC10.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC10.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC10.SEC02.ELT02"));

        relationships.add(rel("SHL02.SCN03.SEC11", "HAS_SECTION", "SHL02.SCN03.SEC11.SEC01"));
        relationships.add(rel("SHL02.SCN03.SEC11", "HAS_SECTION", "SHL02.SCN03.SEC11.SEC02"));
        relationships.add(rel("SHL02.SCN03.SEC11.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC11.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC11.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC11.SEC01.ELT02"));
        relationships.add(rel("SHL02.SCN03.SEC11.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC11.SEC01.ELT03"));
        relationships.add(rel("SHL02.SCN03.SEC11.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC11.SEC02.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC11.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC11.SEC02.ELT02"));

        relationships.add(rel("SHL02.SCN03.SEC12", "HAS_SECTION", "SHL02.SCN03.SEC12.SEC01"));
        relationships.add(rel("SHL02.SCN03.SEC12", "HAS_SECTION", "SHL02.SCN03.SEC12.SEC02"));
        relationships.add(rel("SHL02.SCN03.SEC12.SEC01", "HAS_ELEMENT", "SHL02.SCN03.SEC12.SEC01.ELT01"));
        relationships.add(rel("SHL02.SCN03.SEC12.SEC02", "HAS_ELEMENT", "SHL02.SCN03.SEC12.SEC02.ELT01"));
    }

    private NodeSeed definition(String code, String family, String description, int sortOrder) {
        Map<String, Object> properties = props(
                "code", code,
                "name", family,
                "family", family,
                "domain", inferDomain(family),
                "layer", "definition",
                "description", description,
                "hierarchyCode", code,
                "id", stableIdForCode(code),
                "status", "active",
                "graphScope", GRAPH_SCOPE
        );
        return new NodeSeed(code, List.of("SystemShellGraphNode", "Definition", family + "Definition"), properties);
    }

    private NodeSeed instance(String code, String family, String name, String description, int sortOrder, Map<String, Object> extras) {
        String normalizedCode = normalizeFrontendGraphCode(code);
        Map<String, Object> properties = props(
                "code", normalizedCode,
                "name", name,
                "family", family,
                "domain", inferDomain(family),
                "layer", "instance",
                "description", description,
                "id", stableIdForCode(normalizedCode),
                "guid", requiresGuid(family) ? stableGuidForCode(normalizedCode) : null,
                "status", "active",
                "graphScope", GRAPH_SCOPE
        );
        properties.putAll(normalizeFrontendProperties(extras));
        return new NodeSeed(normalizedCode, List.of("SystemShellGraphNode", "Instance", family), properties);
    }

    private String inferDomain(String family) {
        return switch (family) {
            case "Application", "Shell", "Screen", "Section", "Element", "ValidationRuleSet", "ValidationRule", "ViewportProfile", "Issue" -> "frontend";
            case "Persona", "Journey", "JourneyStep", "BusinessRule", "Blocker" -> "business";
            default -> "backend";
        };
    }

    private NodeSeed container(String code, String name, String description, int sortOrder) {
        return containerAt(PREVIEW_SOURCE, code, name, description, sortOrder);
    }

    private NodeSeed containerAt(String sourceArtifactPath, String code, String name, String description, int sortOrder) {
        return instance(code, "Section", name, description, sortOrder, props(
                "hierarchyCode", code,
                "sectionType", inferContainerType(name),
                "repeatable", inferRepeatableContainer(name),
                "renderMode", "static",
                "defaultState", "visible",
                "controlSource", "none",
                "sourceArtifactPath", sourceArtifactPath
        ));
    }

    private NodeSeed conditionalContainer(String code, String name, String description, int sortOrder) {
        return conditionalContainerAt(PREVIEW_SOURCE, code, name, description, sortOrder);
    }

    private NodeSeed conditionalContainerAt(String sourceArtifactPath, String code, String name, String description, int sortOrder) {
        return instance(code, "Section", name, description, sortOrder, props(
                "hierarchyCode", code,
                "sectionType", inferContainerType(name),
                "repeatable", inferRepeatableContainer(name),
                "renderMode", "conditional",
                "defaultState", "hidden",
                "controlSource", "validation_rule_set",
                "sourceArtifactPath", sourceArtifactPath
        ));
    }

    private NodeSeed statefulContainerAt(String sourceArtifactPath, String code, String name, String description, int sortOrder) {
        return instance(code, "Section", name, description, sortOrder, props(
                "hierarchyCode", code,
                "sectionType", inferContainerType(name),
                "repeatable", inferRepeatableContainer(name),
                "renderMode", "conditional",
                "defaultState", "hidden",
                "controlSource", "validation_rule_set",
                "sourceArtifactPath", sourceArtifactPath
        ));
    }

    private NodeSeed uiElement(String code, String name, int sortOrder, String elementHint, String renderMode,
                               String primeComponent, List<String> tokenFamilies) {
        return uiElementAt(
                PREVIEW_SOURCE,
                code,
                name,
                "Element instance for the frontend screen preview.",
                sortOrder,
                elementHint,
                renderMode,
                primeComponent,
                tokenFamilies
        );
    }

    private NodeSeed uiElementAt(
            String sourceArtifactPath,
            String code,
            String name,
            String description,
            int sortOrder,
            String elementHint,
            String renderMode,
            String primeComponent,
            List<String> tokenFamilies
    ) {
        return instance(code, "Element", name, description, sortOrder, props(
                "hierarchyCode", code,
                "elementType", inferElementType(name, primeComponent),
                "semanticLevel", inferSemanticLevel(name),
                "renderMode", renderMode,
                "defaultState", "static".equals(renderMode) ? "visible" : "hidden",
                "controlSource", "static".equals(renderMode) ? "none" : "validation_rule_set",
                "primeComponent", primeComponent,
                "tokenFamilies", tokenFamilies,
                "sourceArtifactPath", sourceArtifactPath
        ));
    }

    private void seedComponentRegistry(List<NodeSeed> nodes, List<RelationshipSeed> relationships) {
        List<ComponentRegistryItem> registryItems = loadComponentRegistryItems();
        Map<String, ComponentRegistryItem> itemByComponentCode = new LinkedHashMap<>();

        for (ComponentRegistryItem item : registryItems) {
            String definitionCode = componentDefinitionCode(item.code());
            String assetType = assetTypeFor(item);

            nodes.add(componentDefinition(definitionCode, item, assetType));
            itemByComponentCode.put(normalizeComponentKey(item.code()), item);
        }

        List<NodeSeed> elementNodes = nodes.stream()
                .filter(node -> "Element".equals(node.properties().get("family")))
                .toList();

        for (NodeSeed node : elementNodes) {
            String primeComponent = stringProperty(node.properties(), "primeComponent");
            if (primeComponent == null) {
                continue;
            }

            ComponentRegistryItem item = itemByComponentCode.get(normalizeComponentKey(primeComponent));
            if (item == null) {
                continue;
            }

            String assetType = assetTypeFor(item);
            String definitionCode = componentDefinitionCode(item.code());
            String instanceCode = placedComponentInstanceCode(node.code(), assetType);

            nodes.add(componentInstance(
                    instanceCode,
                    definitionCode,
                    item,
                    assetType,
                    node.code(),
                    stringProperty(node.properties(), "name")
            ));
            relationships.add(relAcrossScopes(GRAPH_SCOPE, instanceCode, "INSTANCE_OF", COMPONENT_REGISTRY_SCOPE, definitionCode));
            relationships.add(rel(node.code(), "HAS_COMPONENT", instanceCode));
            relationships.add(rel(instanceCode, "PLACED_WITHIN", node.code()));
        }
    }

    private NodeSeed componentDefinition(String code, ComponentRegistryItem item, String assetType) {
        Map<String, Object> properties = props(
                "code", code,
                "name", item.name(),
                "family", "Component",
                "objectType", "Component",
                "assetName", item.name(),
                "assetType", assetType,
                "domain", "frontend",
                "layer", "definition",
                "description", item.description(),
                "hierarchyCode", code,
                "id", stableIdForCode(code),
                "status", "active",
                "implementationSourcePath", item.implementationSourcePath(),
                "assetSource", "PrimeNG",
                "graphScope", COMPONENT_REGISTRY_SCOPE,
                "sourceArtifactPath", COMPONENT_REGISTRY_RESOURCE
        );
        return new NodeSeed(code, List.of("SystemShellGraphNode", "Definition", "ComponentDefinition"), properties);
    }

    private NodeSeed componentInstance(
            String code,
            String definitionCode,
            ComponentRegistryItem item,
            String assetType,
            String targetObjectCode,
            String targetObjectName
    ) {
        Map<String, Object> properties = props(
                "code", code,
                "name", componentInstanceName(item.name(), targetObjectName),
                "family", "Component",
                "objectType", "Component",
                "assetName", item.name(),
                "assetType", assetType,
                "domain", "frontend",
                "layer", "instance",
                "description", "Editable instance configuration for the " + item.name() + " component asset.",
                "hierarchyCode", code,
                "id", stableIdForCode(code),
                "guid", stableGuidForCode(code),
                "status", "active",
                "definitionCode", definitionCode,
                "implementationSourcePath", item.implementationSourcePath(),
                "configurationJson", configurationJsonFor(item.code()),
                "graphScope", GRAPH_SCOPE,
                "sourceArtifactPath", COMPONENT_REGISTRY_RESOURCE
        );
        return new NodeSeed(code, List.of("SystemShellGraphNode", "Instance", "ComponentInstance"), properties);
    }

    private RelationshipSeed rel(String fromCode, String type, String toCode) {
        return new RelationshipSeed(normalizeFrontendGraphCode(fromCode), type, normalizeFrontendGraphCode(toCode), GRAPH_SCOPE, GRAPH_SCOPE);
    }

    private RelationshipSeed relInScope(String graphScope, String fromCode, String type, String toCode) {
        return new RelationshipSeed(normalizeFrontendGraphCode(fromCode), type, normalizeFrontendGraphCode(toCode), graphScope, graphScope);
    }

    private RelationshipSeed relAcrossScopes(String fromScope, String fromCode, String type, String toScope, String toCode) {
        return new RelationshipSeed(normalizeFrontendGraphCode(fromCode), type, normalizeFrontendGraphCode(toCode), fromScope, toScope);
    }

    private String stableIdForCode(String code) {
        return UUID.nameUUIDFromBytes(code.getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String stableGuidForCode(String code) {
        return UUID.nameUUIDFromBytes(("dom:" + code).getBytes(StandardCharsets.UTF_8)).toString();
    }

    private String graphNodeKey(String graphScope, String code) {
        return graphScope + "|" + code;
    }

    private RelationshipDisplayNames relationshipDisplayNames(String relationshipType, NodeSeed fromNode, NodeSeed toNode) {
        String fromFamily = relationshipDisplayToken(fromNode);
        String toFamily = relationshipDisplayToken(toNode);

        return switch (relationshipType) {
            case "HAS_SHELL", "HAS_SCREEN", "HAS_SECTION", "HAS_ELEMENT", "HAS_COMPONENT", "HAS_STEP", "HAS_RULE", "HAS_BLOCKER" ->
                    new RelationshipDisplayNames("has-child-" + toFamily, "has-parent-" + fromFamily);
            case "USES_RULE_SET" -> new RelationshipDisplayNames("uses-rule-set", "used-by-" + fromFamily);
            case "USES_VIEWPORT_PROFILE" -> new RelationshipDisplayNames("uses-viewport-profile", "used-by-" + fromFamily);
            case "TARGETS" -> new RelationshipDisplayNames("targets-" + toFamily, "targeted-by-" + fromFamily);
            case "CAN_EXECUTE" -> new RelationshipDisplayNames("can-execute-" + toFamily, "executable-by-" + fromFamily);
            case "GOVERNED_BY" -> new RelationshipDisplayNames("governed-by-" + toFamily, "governs-" + fromFamily);
            case "RAISES" -> new RelationshipDisplayNames("raises-" + toFamily, "raised-by-" + fromFamily);
            case "ACTIVATES_SCREEN" -> new RelationshipDisplayNames("activates-" + toFamily, "activated-by-" + fromFamily);
            case "REFERENCES" -> new RelationshipDisplayNames("references-" + toFamily, "referenced-by-" + fromFamily);
            default -> {
                String normalizedType = relationshipType.toLowerCase(Locale.ROOT).replace('_', '-');
                yield new RelationshipDisplayNames(normalizedType, normalizedType);
            }
        };
    }

    private String relationshipDisplayToken(NodeSeed node) {
        if (node == null) {
            return "object";
        }

        String family = stringProperty(node, "family");
        if (family == null || family.isBlank()) {
            return "object";
        }

        return family
                .replaceAll("([a-z0-9])([A-Z])", "$1-$2")
                .replace('_', '-')
                .toLowerCase(Locale.ROOT);
    }

    private boolean requiresGuid(String family) {
        return Set.of("Shell", "Screen", "Section", "Element", "Component").contains(family);
    }

    private List<ComponentRegistryItem> loadComponentRegistryItems() {
        try {
            return objectMapper.readValue(
                    new ClassPathResource(COMPONENT_REGISTRY_RESOURCE).getInputStream(),
                    new TypeReference<List<ComponentRegistryItem>>() {
                    }
            );
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load component registry resource " + COMPONENT_REGISTRY_RESOURCE, exception);
        }
    }

    private String componentDefinitionCode(String registryCode) {
        return "CD." + registryCode;
    }

    private String placedComponentInstanceCode(String targetObjectCode, String assetType) {
        return targetObjectCode + ".CP01." + assetType.toUpperCase();
    }

    private String assetTypeFor(ComponentRegistryItem item) {
        return item.code().toLowerCase(Locale.ROOT);
    }

    private String configurationJsonFor(String registryCode) {
        Map<String, Object> configuration = new LinkedHashMap<>();

        if ("ACCORDION".equals(registryCode)) {
            configuration.put("renderMethod", "Static");
            configuration.put("multiple", false);
            configuration.put("defaultValue", "0");
            configuration.put("selectOnFocus", false);
            configuration.put("expandIcon", "");
            configuration.put("collapseIcon", "");
            configuration.put("panels", List.of(
                    Map.of(
                            "value", "0",
                            "header", "Provider Details",
                            "content", "Provider-specific sign-in guidance and supporting context.",
                            "disabled", false
                    ),
                    Map.of(
                            "value", "1",
                            "header", "Access Notes",
                            "content", "Conditional access notes and provider requirements.",
                            "disabled", false
                    )
            ));
        }

        try {
            return objectMapper.writeValueAsString(configuration);
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to serialize component configuration for " + registryCode, exception);
        }
    }

    private String componentInstanceName(String assetName, String targetObjectName) {
        String elementName = targetObjectName == null || targetObjectName.isBlank() ? assetName : targetObjectName;
        return elementName + " " + assetName + " Component";
    }

    private String normalizeComponentKey(String value) {
        return value.replaceAll("[^A-Za-z0-9]+", "").toUpperCase(Locale.ROOT);
    }

    private String stringProperty(Map<String, Object> properties, String key) {
        Object value = properties.get(key);
        return value instanceof String string && !string.isBlank() ? string : null;
    }

    private String inferContainerType(String name) {
        String normalized = name.toLowerCase();
        if (normalized.contains("background")) {
            return "background";
        }
        if (normalized.contains("header")) {
            return "header";
        }
        if (normalized.contains("logo")) {
            return "logo";
        }
        if (normalized.contains("surface")) {
            return "surface";
        }
        if (normalized.contains("step")) {
            return "step";
        }
        if (normalized.contains("message")) {
            return "message";
        }
        if (normalized.contains("field")) {
            return "field";
        }
        if (normalized.contains("action")) {
            return "action_bar";
        }
        if (normalized.contains("support")) {
            return "support";
        }
        if (normalized.contains("modal")) {
            return "modal";
        }
        if (normalized.contains("outcome") || normalized.contains("not found") || normalized.contains("no auth")) {
            return "outcome";
        }
        if (normalized.contains("provider")) {
            return "provider";
        }
        return "section";
    }

    private boolean inferRepeatableContainer(String name) {
        return name.toLowerCase().contains("auth provider");
    }

    private String inferElementType(String name, String primeComponent) {
        String normalized = name.toLowerCase();
        if (normalized.contains("title")) {
            return "title";
        }
        if (normalized.contains("subtitle") || normalized.contains("description")) {
            return "text";
        }
        if (normalized.contains("banner")) {
            return "banner";
        }
        if (normalized.contains("logo") || normalized.contains("background") || normalized.contains("pattern") || normalized.contains("color")) {
            return "visual";
        }
        if (normalized.contains("checkbox") || "Checkbox".equals(primeComponent)) {
            return "checkbox";
        }
        if (normalized.contains("toggle")) {
            return "toggle";
        }
        if (normalized.contains("input") || "InputText".equals(primeComponent) || "Password".equals(primeComponent) || "Select".equals(primeComponent)) {
            return "input";
        }
        if (normalized.contains("action") || "Button".equals(primeComponent)) {
            return "button";
        }
        if (normalized.contains("indicator")) {
            return "indicator";
        }
        if (normalized.contains("message") || "Message".equals(primeComponent)) {
            return "message";
        }
        return "display";
    }

    private String inferSemanticLevel(String name) {
        String normalized = name.toLowerCase();
        if ("screen title".equals(normalized)) {
            return "H1";
        }
        if ("screen subtitle".equals(normalized)) {
            return "H2";
        }
        if (normalized.contains("step title") || normalized.contains("verification title") || normalized.contains("not found title")) {
            return "H2";
        }
        if (normalized.contains("no auth title")) {
            return "H4";
        }
        return "none";
    }

    private String definitionCodeFor(String instanceCode) {
        String normalizedCode = normalizeFrontendGraphCode(instanceCode);
        if (normalizedCode.startsWith("PER.")) {
            return "DEF.PERSONA";
        }
        if (normalizedCode.startsWith("J01.JS")) {
            return "DEF.JOURNEY_STEP";
        }
        if ("J01".equals(normalizedCode)) {
            return "DEF.JOURNEY";
        }
        if (normalizedCode.startsWith("BR")) {
            return "DEF.BUSINESS_RULE";
        }
        if (normalizedCode.startsWith("BL")) {
            return "DEF.BLOCKER";
        }
        if (normalizedCode.matches("APP\\d+")) {
            return "DEF.APPLICATION";
        }
        if ("SHL01".equals(normalizedCode) || normalizedCode.matches("SHL\\d+")) {
            return "DEF.SHELL";
        }
        if (normalizedCode.matches("SHL\\d+\\.SCN\\d+\\.VRS\\d+")) {
            return "DEF.VALIDATION_RULE_SET";
        }
        if (normalizedCode.matches("SHL\\d+\\.SCN\\d+\\.VRS\\d+\\.R\\d+")) {
            return "DEF.VALIDATION_RULE";
        }
        if (normalizedCode.matches("SHL\\d+\\.SCN\\d+(?:\\.SEC\\d+)+\\.ELT\\d+")) {
            return "DEF.ELEMENT";
        }
        if (normalizedCode.matches("SHL\\d+\\.SCN\\d+(?:\\.SEC\\d+)+")) {
            return "DEF.SECTION";
        }
        if (normalizedCode.matches("SHL\\d+\\.SCN\\d+")) {
            return "DEF.SCREEN";
        }
        if (normalizedCode.matches("VPR\\d+")) {
            return "DEF.VIEWPORT_PROFILE";
        }
        throw new IllegalArgumentException("No definition mapping for " + normalizedCode);
    }

    private void validateInstanceStructure(List<NodeSeed> nodes, List<RelationshipSeed> relationships) {
        Map<String, NodeSeed> nodeMap = new LinkedHashMap<>();
        for (NodeSeed node : nodes) {
            nodeMap.put(node.code(), node);
        }

        Map<String, List<RelationshipSeed>> structuralOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> structuralIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> ruleSetOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> ruleSetIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> ruleOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> ruleIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> viewportOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> viewportIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> targetOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> canExecuteOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> canExecuteIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> hasStepOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> hasStepIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> governedByOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> governedByIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> hasBlockerOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> hasBlockerIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> raisesOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> raisesIncoming = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> activatesScreenOutgoing = new LinkedHashMap<>();
        Map<String, List<RelationshipSeed>> activatesScreenIncoming = new LinkedHashMap<>();
        for (RelationshipSeed relationship : relationships) {
            switch (relationship.type()) {
                case "HAS_SHELL", "HAS_SCREEN", "HAS_SECTION", "HAS_ELEMENT", "HAS_COMPONENT" -> {
                    structuralOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    structuralIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "USES_RULE_SET" -> {
                    ruleSetOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    ruleSetIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "HAS_RULE" -> {
                    ruleOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    ruleIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "USES_VIEWPORT_PROFILE" -> {
                    viewportOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    viewportIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "TARGETS" -> targetOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                case "CAN_EXECUTE" -> {
                    canExecuteOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    canExecuteIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "HAS_STEP" -> {
                    hasStepOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    hasStepIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "GOVERNED_BY" -> {
                    governedByOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    governedByIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "HAS_BLOCKER" -> {
                    hasBlockerOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    hasBlockerIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "RAISES" -> {
                    raisesOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    raisesIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "ACTIVATES_SCREEN" -> {
                    activatesScreenOutgoing.computeIfAbsent(relationship.fromCode(), ignored -> new ArrayList<>()).add(relationship);
                    activatesScreenIncoming.computeIfAbsent(relationship.toCode(), ignored -> new ArrayList<>()).add(relationship);
                }
                default -> {
                }
            }
        }

        List<String> issues = new ArrayList<>();
        for (NodeSeed node : nodes) {
            if (!"instance".equals(node.properties().get("layer"))) {
                continue;
            }

            String family = String.valueOf(node.properties().get("family"));
            List<RelationshipSeed> outgoing = structuralOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> incoming = structuralIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> usesRuleSet = ruleSetOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> usedByScreens = ruleSetIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> rules = ruleOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> ownedByRuleSets = ruleIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> viewportProfiles = viewportOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> referencedByScreens = viewportIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> targets = targetOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> canExecute = canExecuteOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> executableBy = canExecuteIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> steps = hasStepOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> withinJourneys = hasStepIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> governedByRules = governedByOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> governingSteps = governedByIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> blockers = hasBlockerOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> blockedSteps = hasBlockerIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> raises = raisesOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> raisedByRules = raisesIncoming.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> activatedScreens = activatesScreenOutgoing.getOrDefault(node.code(), List.of());
            List<RelationshipSeed> activatedBySteps = activatesScreenIncoming.getOrDefault(node.code(), List.of());
            validateSharedAttributes(node, issues);

            if ("Application".equals(family)) {
                if (!node.code().matches("APP\\d{2}")) {
                    issues.add("Application " + node.code() + " code must follow APP##.");
                }
                if (!incoming.isEmpty()) {
                    issues.add("Application " + node.code() + " must not have a structural parent.");
                }
                if (outgoing.isEmpty()) {
                    issues.add("Application " + node.code() + " must contain at least one shell.");
                }
                List<String> nonShellChildren = outgoing.stream()
                        .filter(relationship -> !"Shell".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!nonShellChildren.isEmpty()) {
                    issues.add("Application " + node.code() + " contains non-shell children: " + String.join(", ", nonShellChildren));
                }
            }

            if ("Shell".equals(family)) {
                if (!node.code().matches("SHL\\d{2}")) {
                    issues.add("Shell " + node.code() + " code must follow SHL##.");
                }
                if (incoming.size() != 1 || !"Application".equals(nodeFamily(nodeMap.get(incoming.get(0).fromCode())))) {
                    issues.add("Shell " + node.code() + " must have exactly one Application parent.");
                }
                if (outgoing.isEmpty()) {
                    issues.add("Shell " + node.code() + " must contain at least one container.");
                }
                List<String> nonContainerChildren = outgoing.stream()
                        .filter(relationship -> !"Section".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!nonContainerChildren.isEmpty()) {
                    issues.add("Shell " + node.code() + " contains non-container children: " + String.join(", ", nonContainerChildren));
                }
            }

            if ("Screen".equals(family)) {
                if (!node.code().matches("SHL\\d{2}\\.SCN\\d{2}")) {
                    issues.add("Screen " + node.code() + " code must follow SHL##.SCN##.");
                }
                if (incoming.size() != 1 || !Set.of("Shell", "Section").contains(nodeFamily(nodeMap.get(incoming.get(0).fromCode())))) {
                    issues.add("Screen " + node.code() + " must have exactly one Shell or Section parent.");
                }
                if (outgoing.isEmpty()) {
                    issues.add("Screen " + node.code() + " should contain at least one section.");
                }
                List<String> nonContainerChildren = outgoing.stream()
                        .filter(relationship -> !"Section".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!nonContainerChildren.isEmpty()) {
                    issues.add("Screen " + node.code() + " contains non-section children: " + String.join(", ", nonContainerChildren));
                }
                long ruleSetCount = usesRuleSet.stream()
                        .filter(relationship -> "ValidationRuleSet".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .count();
                if (screenHasConditionalDescendants(node.code(), nodes) && ruleSetCount != 1) {
                    issues.add("Screen " + node.code() + " must use exactly one ValidationRuleSet when it contains conditional UI nodes.");
                }
            }

            if ("Section".equals(family)) {
                if (!node.code().matches("SHL\\d{2}\\.SEC\\d{2}|SHL\\d{2}\\.SCN\\d{2}(?:\\.SEC\\d{2})+")) {
                    issues.add("Section " + node.code() + " code must be shell-level or screen-level SEC##.");
                }
                if (incoming.size() != 1) {
                    issues.add("Section " + node.code() + " must have exactly one Shell, Screen, or Section parent.");
                } else {
                    String parentCode = incoming.get(0).fromCode();
                    String parentFamily = nodeFamily(nodeMap.get(parentCode));
                    if (!Set.of("Shell", "Screen", "Section").contains(parentFamily)) {
                        issues.add("Section " + node.code() + " must have a Shell, Screen, or Section parent.");
                    }
                }
                validateRequiredProperties(node, issues, "sectionType", "repeatable", "renderMode", "defaultState", "controlSource");
                boolean hasContainerChildren = outgoing.stream().anyMatch(relationship -> "HAS_SECTION".equals(relationship.type()));
                boolean hasScreenChildren = outgoing.stream().anyMatch(relationship -> "HAS_SCREEN".equals(relationship.type()));
                boolean hasElementChildren = outgoing.stream().anyMatch(relationship -> "HAS_ELEMENT".equals(relationship.type()));
                int childFamilyModes = (hasContainerChildren ? 1 : 0) + (hasScreenChildren ? 1 : 0) + (hasElementChildren ? 1 : 0);
                if (childFamilyModes > 1) {
                    issues.add("Section " + node.code() + " mixes child containers, screens, and elements.");
                }
                List<String> invalidChildren = outgoing.stream()
                        .filter(relationship -> !"Section".equals(nodeFamily(nodeMap.get(relationship.toCode())))
                                && !"Screen".equals(nodeFamily(nodeMap.get(relationship.toCode())))
                                && !"Element".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidChildren.isEmpty()) {
                    issues.add("Section " + node.code() + " contains invalid children: " + String.join(", ", invalidChildren));
                }

                long containerChildCount = outgoing.stream().filter(relationship -> "HAS_SECTION".equals(relationship.type())).count();
                boolean repeatable = Boolean.TRUE.equals(node.properties().get("repeatable"));
                if (containerChildCount == 1 && !repeatable) {
                    issues.add("Section " + node.code() + " is an orphan single-child section and should be flattened.");
                }
                if (repeatable && !isMeaningfulRepeatableSection(node)) {
                    issues.add("Section " + node.code() + " is repeatable but does not describe a meaningful repeated implementation pattern.");
                }
                String renderMode = stringProperty(node, "renderMode");
                String controlSource = stringProperty(node, "controlSource");
                if ("conditional".equals(renderMode) && !"validation_rule_set".equals(controlSource)) {
                    issues.add("Section " + node.code() + " with render_mode = conditional must use control_source = validation_rule_set.");
                }
                if ("static".equals(renderMode) && !"none".equals(controlSource)) {
                    issues.add("Section " + node.code() + " with render_mode = static must use control_source = none.");
                }
                if (!isBlankProperty(node.properties().get("semanticLevel"))) {
                    issues.add("Section " + node.code() + " must not define semantic_level; semantic heading levels belong to Element.");
                }
            }

            if ("Element".equals(family)) {
                if (!node.code().matches("SHL\\d{2}\\.SCN\\d{2}(?:\\.SEC\\d{2})+\\.ELT\\d{2}")) {
                    issues.add("Element " + node.code() + " code must end with ELT## and extend its parent section path.");
                }
                if (incoming.size() != 1 || !"Section".equals(nodeFamily(nodeMap.get(incoming.get(0).fromCode())))) {
                    issues.add("Element " + node.code() + " must have exactly one Section parent.");
                } else if (!node.code().startsWith(incoming.get(0).fromCode() + ".ELT")) {
                    issues.add("Element " + node.code() + " code must extend its parent Section code.");
                }
                validateRequiredProperties(node, issues, "elementType", "renderMode", "defaultState", "controlSource");
                String renderMode = stringProperty(node, "renderMode");
                String controlSource = stringProperty(node, "controlSource");
                if ("title".equals(stringProperty(node, "elementType")) && isBlankProperty(node.properties().get("semanticLevel"))) {
                    issues.add("Element " + node.code() + " is a title element and should define semantic_level.");
                }
                if ("conditional".equals(renderMode) && !"validation_rule_set".equals(controlSource)) {
                    issues.add("Element " + node.code() + " with render_mode = conditional must use control_source = validation_rule_set.");
                }
                if ("static".equals(renderMode) && !"none".equals(controlSource)) {
                    issues.add("Element " + node.code() + " with render_mode = static must use control_source = none.");
                }
                List<String> invalidChildren = outgoing.stream()
                        .filter(relationship -> !"Component".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidChildren.isEmpty()) {
                    issues.add("Element " + node.code() + " may contain Component instances only: " + String.join(", ", invalidChildren));
                }
                if (outgoing.stream().anyMatch(relationship -> !"HAS_COMPONENT".equals(relationship.type()))) {
                    issues.add("Element " + node.code() + " has structural children.");
                }
            }

            if ("Component".equals(family)) {
                if (incoming.size() != 1 || !"Element".equals(nodeFamily(nodeMap.get(incoming.get(0).fromCode())))) {
                    issues.add("Component " + node.code() + " must be attached to exactly one Element parent.");
                }
                if (!outgoing.isEmpty()) {
                    issues.add("Component " + node.code() + " must remain a leaf.");
                }
                validateRequiredProperties(node, issues, "assetType", "assetName");
                if (incoming.size() == 1) {
                    NodeSeed targetNode = nodeMap.get(incoming.get(0).fromCode());
                    if (targetNode == null || !"Element".equals(nodeFamily(targetNode))) {
                        issues.add("Component " + node.code() + " must target an existing Element parent.");
                    }
                }
            }

            if ("ValidationRuleSet".equals(family)) {
                if (!node.code().matches("SHL\\d{2}\\.SCN\\d{2}\\.VRS\\d{2}")) {
                    issues.add("ValidationRuleSet " + node.code() + " code must follow SHL##.SCN##.VRS##.");
                }
                if (usedByScreens.size() != 1 || !"Screen".equals(nodeFamily(nodeMap.get(usedByScreens.get(0).fromCode())))) {
                    issues.add("ValidationRuleSet " + node.code() + " must be referenced by exactly one Screen.");
                } else if (!node.code().startsWith(usedByScreens.get(0).fromCode() + ".VRS")) {
                    issues.add("ValidationRuleSet " + node.code() + " code must extend its parent Screen code.");
                }
                validateRequiredProperties(node, issues, "ruleSetType", "ruleSetScope");
                List<String> invalidRules = rules.stream()
                        .filter(relationship -> !"ValidationRule".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidRules.isEmpty()) {
                    issues.add("ValidationRuleSet " + node.code() + " may contain ValidationRule only: " + String.join(", ", invalidRules));
                }
                if (rules.isEmpty() && usedByScreens.size() == 1 && screenHasConditionalDescendants(usedByScreens.get(0).fromCode(), nodes)) {
                    issues.add("ValidationRuleSet " + node.code() + " must contain at least one ValidationRule when its screen has conditional UI nodes.");
                }
            }

            if ("ValidationRule".equals(family)) {
                if (!node.code().matches("SHL\\d{2}\\.SCN\\d{2}\\.VRS\\d{2}\\.R\\d{2,3}")) {
                    issues.add("ValidationRule " + node.code() + " code must extend its parent ValidationRuleSet code.");
                }
                if (ownedByRuleSets.size() != 1 || !"ValidationRuleSet".equals(nodeFamily(nodeMap.get(ownedByRuleSets.get(0).fromCode())))) {
                    issues.add("ValidationRule " + node.code() + " must be owned by exactly one ValidationRuleSet.");
                } else if (!node.code().startsWith(ownedByRuleSets.get(0).fromCode() + ".R")) {
                    issues.add("ValidationRule " + node.code() + " code must extend its parent ValidationRuleSet code.");
                }
                validateRequiredProperties(node, issues, "conditionExpression", "actionType", "priority", "stopProcessing");
                if (targets.isEmpty()) {
                    issues.add("ValidationRule " + node.code() + " must target at least one Screen, Section, or Element.");
                }
                List<String> invalidTargets = targets.stream()
                        .filter(relationship -> !Set.of("Screen", "Section", "Element").contains(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidTargets.isEmpty()) {
                    issues.add("ValidationRule " + node.code() + " may target Screen, Section, or Element only: " + String.join(", ", invalidTargets));
                }
            }

            if ("ViewportProfile".equals(family)) {
                if (!node.code().matches("VPR\\d{2}")) {
                    issues.add("ViewportProfile " + node.code() + " code must follow VPR##.");
                }
                Integer viewportWidth = integerProperty(node, "viewportWidth");
                Integer viewportHeight = integerProperty(node, "viewportHeight");
                if (viewportWidth == null || viewportWidth <= 0) {
                    issues.add("ViewportProfile " + node.code() + " must define a positive viewportWidth.");
                }
                if (viewportHeight == null || viewportHeight <= 0) {
                    issues.add("ViewportProfile " + node.code() + " must define a positive viewportHeight.");
                }
                if (isBlankProperty(node.properties().get("viewportCategory"))) {
                    issues.add("ViewportProfile " + node.code() + " must define viewportCategory.");
                }
            }

            if ("Persona".equals(family)) {
                if (!node.code().matches("PER\\.[A-Z0-9_]+") && !node.code().matches("P\\d{3}")) {
                    issues.add("Persona " + node.code() + " code must follow the supported persona convention.");
                }
                if (canExecute.isEmpty()) {
                    issues.add("Persona " + node.code() + " must execute at least one Journey.");
                }
                List<String> invalidJourneys = canExecute.stream()
                        .filter(relationship -> !"Journey".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidJourneys.isEmpty()) {
                    issues.add("Persona " + node.code() + " may execute Journey only: " + String.join(", ", invalidJourneys));
                }
            }

            if ("Journey".equals(family)) {
                if (!node.code().matches("J\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}")) {
                    issues.add("Journey " + node.code() + " code must follow the supported journey convention.");
                }
                if (executableBy.isEmpty()) {
                    issues.add("Journey " + node.code() + " must be executable by at least one Persona.");
                }
                if (steps.isEmpty()) {
                    issues.add("Journey " + node.code() + " must contain at least one JourneyStep.");
                }
                List<String> invalidSteps = steps.stream()
                        .filter(relationship -> !"JourneyStep".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidSteps.isEmpty()) {
                    issues.add("Journey " + node.code() + " may contain JourneyStep only: " + String.join(", ", invalidSteps));
                }
            }

            if ("JourneyStep".equals(family)) {
                if (!node.code().matches("J\\d{2}\\.JS\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}\\.ST\\d{3}")) {
                    issues.add("JourneyStep " + node.code() + " code must follow the supported journey-step convention.");
                }
                validateRequiredProperties(node, issues, "stepOrder", "executionMethod");
                String executionMethod = stringProperty(node, "executionMethod");
                if (!Set.of("mandatory", "conditional").contains(executionMethod)) {
                    issues.add("JourneyStep " + node.code() + " execution_method must be mandatory or conditional.");
                }
                if (withinJourneys.size() != 1 || !"Journey".equals(nodeFamily(nodeMap.get(withinJourneys.get(0).fromCode())))) {
                    issues.add("JourneyStep " + node.code() + " must belong to exactly one Journey.");
                }
                if (activatedScreens.isEmpty()) {
                    issues.add("JourneyStep " + node.code() + " must activate at least one Screen.");
                }
                List<String> invalidScreens = activatedScreens.stream()
                        .filter(relationship -> !"Screen".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidScreens.isEmpty()) {
                    issues.add("JourneyStep " + node.code() + " may activate Screen only: " + String.join(", ", invalidScreens));
                }
                List<String> invalidRules = governedByRules.stream()
                        .filter(relationship -> !"BusinessRule".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidRules.isEmpty()) {
                    issues.add("JourneyStep " + node.code() + " may be governed by BusinessRule only: " + String.join(", ", invalidRules));
                }
                List<String> invalidBlockers = blockers.stream()
                        .filter(relationship -> !"Blocker".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidBlockers.isEmpty()) {
                    issues.add("JourneyStep " + node.code() + " may reference Blocker only: " + String.join(", ", invalidBlockers));
                }
            }

            if ("BusinessRule".equals(family)) {
                if (!node.code().matches("BR\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}\\.ST\\d{3}\\.BR\\d{3}")) {
                    issues.add("BusinessRule " + node.code() + " code must follow the supported business-rule convention.");
                }
                validateRequiredProperties(node, issues, "ruleScope", "conditionExpression", "executionEffect");
                if (governingSteps.isEmpty()) {
                    issues.add("BusinessRule " + node.code() + " must govern at least one JourneyStep.");
                }
                List<String> invalidSteps = governingSteps.stream()
                        .filter(relationship -> !"JourneyStep".equals(nodeFamily(nodeMap.get(relationship.fromCode()))))
                        .map(RelationshipSeed::fromCode)
                        .toList();
                if (!invalidSteps.isEmpty()) {
                    issues.add("BusinessRule " + node.code() + " may be attached from JourneyStep only: " + String.join(", ", invalidSteps));
                }
                List<String> invalidRaisedBlockers = raises.stream()
                        .filter(relationship -> !"Blocker".equals(nodeFamily(nodeMap.get(relationship.toCode()))))
                        .map(RelationshipSeed::toCode)
                        .toList();
                if (!invalidRaisedBlockers.isEmpty()) {
                    issues.add("BusinessRule " + node.code() + " may raise Blocker only: " + String.join(", ", invalidRaisedBlockers));
                }
            }

            if ("Blocker".equals(family)) {
                if (!node.code().matches("BL\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}\\.ST\\d{3}\\.B\\d{3}")) {
                    issues.add("Blocker " + node.code() + " code must follow the supported blocker convention.");
                }
                validateRequiredProperties(node, issues, "blockerType", "blockingEffect");
                if (blockedSteps.isEmpty() && raisedByRules.isEmpty()) {
                    issues.add("Blocker " + node.code() + " must be referenced by a JourneyStep or BusinessRule.");
                }
            }
        }

        if (!issues.isEmpty()) {
            throw new IllegalStateException("System screen graph structural validation failed: " + String.join(" | ", issues));
        }
    }

    private void validateSharedAttributes(NodeSeed node, List<String> issues) {
        String family = nodeFamily(node);
        if (!Set.of("Persona", "Journey", "JourneyStep", "BusinessRule", "Blocker", "Application", "Shell", "Screen", "Section", "Element", "Component", "ValidationRuleSet", "ValidationRule", "ViewportProfile").contains(family)) {
            return;
        }

        validateRequiredProperties(node, issues, "name", "description", "id", "status", "domain");
        if (isBlankProperty(node.properties().get("hierarchyCode"))) {
            issues.add(family + " " + node.code() + " is missing required attribute: hierarchyCode.");
        }
        if (requiresGuid(family) && isBlankProperty(node.properties().get("guid"))) {
            issues.add(family + " " + node.code() + " is missing required attribute: guid.");
        }
        if (!"Element".equals(family) && !isBlankProperty(node.properties().get("semanticLevel"))) {
            issues.add(family + " " + node.code() + " must not define semanticLevel.");
        }
        if ("Screen".equals(family) && !isBlankProperty(node.properties().get("backgroundType"))) {
            issues.add("Screen " + node.code() + " must not own shell background attributes.");
        }
    }

    private void validateRequiredProperties(NodeSeed node, List<String> issues, String... keys) {
        for (String key : keys) {
            if (isBlankProperty(node.properties().get(key))) {
                issues.add(nodeFamily(node) + " " + node.code() + " is missing required attribute: " + key + ".");
            }
        }
    }

    private boolean isBlankProperty(Object value) {
        if (value == null) {
            return true;
        }
        if (value instanceof String stringValue) {
            return stringValue.isBlank();
        }
        return false;
    }

    private String stringProperty(NodeSeed node, String key) {
        Object value = node.properties().get(key);
        return value instanceof String stringValue ? stringValue : null;
    }

    private Integer integerProperty(NodeSeed node, String key) {
        Object value = node.properties().get(key);
        return value instanceof Number number ? number.intValue() : null;
    }

    private boolean screenHasConditionalDescendants(String screenCode, List<NodeSeed> nodes) {
        return nodes.stream()
                .filter(node -> "instance".equals(node.properties().get("layer")))
                .filter(node -> node.code().startsWith(screenCode + "."))
                .filter(node -> Set.of("Section", "Element").contains(nodeFamily(node)))
                .anyMatch(node -> "conditional".equals(stringProperty(node, "renderMode")));
    }

    private boolean isMeaningfulRepeatableSection(NodeSeed node) {
        String sectionType = stringProperty(node, "sectionType");
        String name = stringProperty(node, "name");
        if (sectionType == null || "section".equalsIgnoreCase(sectionType)) {
            return false;
        }
        String normalizedName = name == null ? "" : name.toLowerCase(Locale.ROOT);
        String normalizedType = sectionType.toLowerCase(Locale.ROOT);
        return Stream.of("provider", "list", "item", "card", "row", "record", "tile", "entry", "result", "tab", "table", "grid")
                .anyMatch(keyword -> normalizedType.contains(keyword) || normalizedName.contains(keyword));
    }

    private String nodeFamily(NodeSeed node) {
        return node == null ? null : String.valueOf(node.properties().get("family"));
    }

    private Map<String, Object> normalizeFrontendProperties(Map<String, Object> extras) {
        Map<String, Object> normalized = new LinkedHashMap<>();
        extras.forEach((key, value) -> {
            if (value instanceof String stringValue
                    && List.of("hierarchyCode", "actionValue").contains(key)) {
                normalized.put(key, normalizeFrontendGraphCode(stringValue));
                return;
            }
            normalized.put(key, value);
        });
        return normalized;
    }

    private String normalizeFrontendGraphCode(String code) {
        if (code == null || code.isBlank() || !code.startsWith("SC")) {
            return code;
        }

        String normalized = code.replaceFirst("^SC(\\d+)", "SHL01.SCN$1");
        normalized = normalized.replaceAll("\\.SE(\\d+)", ".SEC$1");
        normalized = normalized.replaceAll("\\.EL(\\d+)", ".ELT$1");
        return normalized;
    }

    private Map<String, Object> props(Object... entries) {
        Map<String, Object> values = new LinkedHashMap<>();
        for (int index = 0; index < entries.length; index += 2) {
            Object key = entries[index];
            Object value = entries[index + 1];
            if (!(key instanceof String stringKey) || value == null) {
                continue;
            }
            values.put(stringKey, value);
        }
        return values;
    }

    private record SeedGraph(List<NodeSeed> nodes, List<RelationshipSeed> relationships) {
    }

    private record NodeSeed(String code, List<String> labels, Map<String, Object> properties) {
    }

    private record RelationshipSeed(String fromCode, String type, String toCode, String fromScope, String toScope) {
    }

    private record RelationshipDisplayNames(String activeName, String passiveName) {
    }

    private record ComponentRegistryItem(
            String code,
            String name,
            String kind,
            String implementationSourcePath,
            String description
    ) {
    }
}
