package com.emsist.designhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RegistryGraphMigrationService {

    private final Neo4jClient neo4jClient;

    // ── Registry seeds (existing) ──────────────────────────────────────

    @Transactional
    public void seedChannels() {
        neo4jClient.query("""
                UNWIND [
                  {code: 'CH-WEB-DSK', name: 'Web Desktop',    type: 'WEB'},
                  {code: 'CH-WEB-TAB', name: 'Web Tablet',     type: 'WEB'},
                  {code: 'CH-WEB-MOB', name: 'Web Mobile',     type: 'WEB'},
                  {code: 'CH-API',     name: 'REST API',       type: 'API'},
                  {code: 'CH-WEBHOOK', name: 'Webhook',        type: 'API'},
                  {code: 'CH-AI-CHAT', name: 'AI Chat',        type: 'AI'},
                  {code: 'CH-AI-BG',   name: 'AI Background',  type: 'AI'},
                  {code: 'CH-EMAIL',   name: 'Email',          type: 'NOTIFICATION'},
                  {code: 'CH-INAPP',   name: 'In-App',         type: 'NOTIFICATION'}
                ] AS ch
                MERGE (c:Channel {channelCode: ch.code})
                SET c.displayName = ch.name, c.channelType = ch.type
                """).run();
    }

    @Transactional
    public void seedPermissions() {
        neo4jClient.query("""
                UNWIND [
                  {key: 'SUPER_ADMIN',    name: 'Super Admin',      sort: 0},
                  {key: 'ADMIN',          name: 'Administrator',    sort: 1},
                  {key: 'ARCHITECT',      name: 'Architect',        sort: 2},
                  {key: 'AGENT_DESIGNER', name: 'Agent Designer',   sort: 3},
                  {key: 'USER',           name: 'User',             sort: 4},
                  {key: 'VIEWER',         name: 'Viewer',           sort: 5},
                  {key: 'HITL_REVIEWER',  name: 'HITL Reviewer',    sort: 6},
                  {key: 'AUDITOR',        name: 'Auditor',          sort: 7}
                ] AS p
                MERGE (perm:Permission {permissionKey: p.key})
                SET perm.displayName = p.name, perm.sortOrder = p.sort
                """).run();
    }

    @Transactional
    public void seedBusinessRoles() {
        neo4jClient.query("""
                UNWIND [
                  {key: 'SUPER_ADMIN',    name: 'Super Admin',    grp: 'platform',  sort: 0},
                  {key: 'ADMIN',          name: 'Administrator',  grp: 'tenant',    sort: 1},
                  {key: 'ARCHITECT',      name: 'Architect',      grp: 'design',    sort: 2},
                  {key: 'AGENT_DESIGNER', name: 'Agent Designer', grp: 'design',    sort: 3},
                  {key: 'USER',           name: 'User',           grp: 'operational', sort: 4},
                  {key: 'VIEWER',         name: 'Viewer',         grp: 'operational', sort: 5}
                ] AS r
                MERGE (br:BusinessRole {roleKey: r.key})
                SET br.displayName = r.name, br.roleGroup = r.grp, br.sortOrder = r.sort,
                    br.status = 'DEFINED'
                """).run();
    }

    @Transactional
    public void seedValidationRoles() {
        neo4jClient.query("""
                UNWIND [
                  {key: 'HITL_REVIEWER', name: 'HITL Reviewer', scope: 'review'},
                  {key: 'AUDITOR',       name: 'Auditor',       scope: 'audit'}
                ] AS vr
                MERGE (r:ValidationRole {validationRoleKey: vr.key})
                SET r.displayName = vr.name, r.scope = vr.scope, r.status = 'DEFINED'
                """).run();
    }

    // ── New seeds (Chunk 2 — Task 4) ───────────────────────────────────

    @Transactional
    public void seedConfirmationDialogs() {
        neo4jClient.query("""
                UNWIND [
                  {id: 'CONFIRM-AGT-DELETE',  action: 'Delete agent',  confirm: 'Delete',  cancel: 'Cancel',     consequence: 'The selected agent will be permanently removed.'},
                  {id: 'CONFIRM-AGT-PUBLISH', action: 'Publish agent', confirm: 'Publish', cancel: 'Keep Draft', consequence: 'The draft agent becomes available to end users.'}
                ] AS dlg
                MERGE (d:ConfirmationDialog {dialogId: dlg.id})
                SET d.triggerAction    = dlg.action,
                    d.confirmLabel     = dlg.confirm,
                    d.cancelLabel      = dlg.cancel,
                    d.consequenceText  = dlg.consequence
                """).run();
    }

    @Transactional
    public void seedErrorCodes() {
        neo4jClient.query("""
                UNWIND [
                  {code: 'AUTH-E-401',           severity: 'ERROR', text: 'Session refresh failed.',                     trigger: 'Session refresh API returns 401',              hint: 'Prompt the user to sign in again.'},
                  {code: 'CORE-E-SEARCH-001',    severity: 'ERROR', text: 'Search could not be completed.',              trigger: 'Global search API fails',                       hint: 'Retry search after connectivity is restored.'},
                  {code: 'CORE-E-NOTIF-001',     severity: 'ERROR', text: 'Notification update failed.',                 trigger: 'Notification read API fails',                   hint: 'Retry from the notification center.'},
                  {code: 'AGT-E-403',            severity: 'ERROR', text: 'Agent action is not permitted.',              trigger: 'Create or publish agent without permission',    hint: 'Request a role with agent publishing rights.'},
                  {code: 'AGT-E-404',            severity: 'ERROR', text: 'Agent could not be found.',                   trigger: 'Agent lookup or deletion targets a missing id', hint: 'Refresh the list and retry the action.'},
                  {code: 'AGT-E-BUILDER-001',    severity: 'ERROR', text: 'Component could not be added to the canvas.', trigger: 'Builder mutation fails',                         hint: 'Retry the drag-and-drop action.'},
                  {code: 'AGT-E-BUILDER-002',    severity: 'ERROR', text: 'Draft save failed.',                          trigger: 'Draft persistence API fails',                   hint: 'Review validation messages and retry save.'},
                  {code: 'AGT-E-PLAYGROUND-001', severity: 'ERROR', text: 'Test session could not be created.',          trigger: 'Playground session API fails',                  hint: 'Retry after the agent draft is saved.'},
                  {code: 'TPL-E-404',            severity: 'ERROR', text: 'Template details could not be loaded.',       trigger: 'Template details request fails',                hint: 'Refresh the gallery and try again.'},
                  {code: 'TPL-E-QUERY-001',      severity: 'ERROR', text: 'Template filter could not be applied.',       trigger: 'Template query request fails',                  hint: 'Retry the selected category filter.'},
                  {code: 'TPL-E-FORK-001',       severity: 'ERROR', text: 'Template fork failed.',                       trigger: 'Template fork API fails',                       hint: 'Retry after verifying access to the template.'},
                  {code: 'CHAT-E-STREAM-001',    severity: 'ERROR', text: 'Chat response could not be generated.',       trigger: 'Agent chat stream fails',                       hint: 'Retry the prompt or switch models.'},
                  {code: 'CHAT-E-STREAM-002',    severity: 'ERROR', text: 'Generation could not be stopped.',            trigger: 'Chat stream cancellation fails',                hint: 'Retry stop or refresh the chat session.'},
                  {code: 'CHAT-E-503',           severity: 'ERROR', text: 'Escalation failed.',                          trigger: 'Human escalation service is unavailable',       hint: 'Retry escalation or contact a reviewer directly.'}
                ] AS ec
                MERGE (code:ErrorCode {code: ec.code})
                SET code.severity = ec.severity,
                    code.messageText = ec.text,
                    code.triggerCondition = ec.trigger,
                    code.resolutionHint = ec.hint
                """).run();
    }

    @Transactional
    public void upsertApiContractsFromInteractions() {
        // Collect distinct apiCall strings from all interactions
        Collection<Map<String, Object>> rows = neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.apiCalls IS NOT NULL AND size(i.apiCalls) > 0
                UNWIND i.apiCalls AS apiCall
                RETURN DISTINCT apiCall
                """).fetch().all();

        for (Map<String, Object> row : rows) {
            String apiCall = (String) row.get("apiCall");
            if (apiCall == null || apiCall.isBlank()) continue;
            String[] parts = apiCall.split(" ", 2);
            if (parts.length < 2) continue;

            String method = parts[0].toUpperCase();
            String path = parts[1];
            String contractId = generateContractId(method, path);

            neo4jClient.query("""
                    MERGE (ac:ApiContract {contractId: $contractId})
                    SET ac.method = $method, ac.path = $path, ac.status = 'DEFINED'
                    """)
                    .bind(contractId).to("contractId")
                    .bind(method).to("method")
                    .bind(path).to("path")
                    .run();
        }
    }

    /**
     * Frozen ID rule: API-{METHOD}-{SANITIZED_PATH}
     * where SANITIZED_PATH is the request path uppercased with
     * non-alphanumeric runs collapsed to "-" and leading/trailing "-" trimmed.
     */
    String generateContractId(String method, String path) {
        String upperMethod = method.toUpperCase();
        String sanitized = path.toUpperCase()
                .replaceAll("[^A-Z0-9]+", "-")
                .replaceAll("^-|-$", "");
        if (sanitized.isEmpty()) {
            return "API-" + upperMethod;
        }
        return "API-" + upperMethod + "-" + sanitized;
    }

    // ── Existing backfills ─────────────────────────────────────────────

    @Transactional
    public void backfillPersonas() {
        // Create Persona nodes from personaId on Journey
        neo4jClient.query("""
                MATCH (j:Journey) WHERE j.personaId IS NOT NULL AND j.personaId <> ''
                MERGE (p:Persona {personaId: j.personaId})
                ON CREATE SET p.name = j.personaId, p.status = 'IDENTIFIED'
                MERGE (j)-[:PERFORMED_BY_PERSONA]->(p)
                """).run();

        // Create Persona nodes from personaIds on Screen
        neo4jClient.query("""
                MATCH (s:Screen) WHERE s.personaIds IS NOT NULL
                UNWIND s.personaIds AS pid
                WITH s, pid WHERE pid <> ''
                MERGE (p:Persona {personaId: pid})
                ON CREATE SET p.name = pid, p.status = 'IDENTIFIED'
                MERGE (s)-[:USED_BY_PERSONA]->(p)
                """).run();

        // Create Persona nodes from personaIds on Touchpoint
        neo4jClient.query("""
                MATCH (tp:Touchpoint) WHERE tp.personaIds IS NOT NULL
                UNWIND tp.personaIds AS pid
                WITH tp, pid WHERE pid <> ''
                MERGE (p:Persona {personaId: pid})
                ON CREATE SET p.name = pid, p.status = 'IDENTIFIED'
                MERGE (tp)-[:USED_BY_PERSONA]->(p)
                """).run();
    }

    @Transactional
    public void backfillAccessibleByRoleEdges() {
        // Screen.roleKeys → BusinessRole
        neo4jClient.query("""
                MATCH (s:Screen) WHERE s.roleKeys IS NOT NULL
                UNWIND s.roleKeys AS rk
                WITH s, rk WHERE rk <> ''
                MATCH (br:BusinessRole {roleKey: rk})
                MERGE (s)-[:ACCESSIBLE_BY_ROLE]->(br)
                """).run();
    }

    @Transactional
    public void backfillDeliveredViaChannelEdges() {
        // Touchpoint → Channel via channelId in EntryMode
        neo4jClient.query("""
                MATCH (tp:Touchpoint)-[:HAS_ENTRY_MODE]->(em:EntryMode)
                WHERE em.channelId IS NOT NULL AND em.channelId <> ''
                MATCH (ch:Channel {channelCode: em.channelId})
                MERGE (tp)-[:DELIVERED_VIA_CHANNEL]->(ch)
                """).run();
    }

    @Transactional
    public void backfillRequiresPermissionEdges() {
        // Interaction.permission → Permission
        neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.permission IS NOT NULL AND i.permission <> ''
                MATCH (perm:Permission {permissionKey: i.permission})
                MERGE (i)-[:REQUIRES_PERMISSION]->(perm)
                """).run();
    }

    // ── New backfills (Chunk 2 — Task 5) ───────────────────────────────

    @Transactional
    public void backfillInteractionPersonaEdges() {
        // Interaction.personaIds → USED_BY_PERSONA
        neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.personaIds IS NOT NULL
                UNWIND i.personaIds AS pid
                WITH i, pid WHERE pid <> ''
                MERGE (p:Persona {personaId: pid})
                ON CREATE SET p.name = pid, p.status = 'IDENTIFIED'
                MERGE (i)-[:USED_BY_PERSONA]->(p)
                """).run();
    }

    @Transactional
    public void backfillInteractionRoleEdges() {
        // Interaction.roleKeys → ACCESSIBLE_BY_ROLE
        neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.roleKeys IS NOT NULL
                UNWIND i.roleKeys AS rk
                WITH i, rk WHERE rk <> ''
                MATCH (br:BusinessRole {roleKey: rk})
                MERGE (i)-[:ACCESSIBLE_BY_ROLE]->(br)
                """).run();
    }

    @Transactional
    public void backfillTouchpointRoleEdges() {
        // Touchpoint.roleKeys → ACCESSIBLE_BY_ROLE
        neo4jClient.query("""
                MATCH (tp:Touchpoint) WHERE tp.roleKeys IS NOT NULL
                UNWIND tp.roleKeys AS rk
                WITH tp, rk WHERE rk <> ''
                MATCH (br:BusinessRole {roleKey: rk})
                MERGE (tp)-[:ACCESSIBLE_BY_ROLE]->(br)
                """).run();
    }

    @Transactional
    public void backfillCallsApiEdges() {
        // Interaction.apiCalls → CALLS_API (via ApiContract nodes created by upsert)
        neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.apiCalls IS NOT NULL AND size(i.apiCalls) > 0
                UNWIND i.apiCalls AS apiCall
                WITH i, apiCall, split(apiCall, ' ') AS parts
                WHERE size(parts) >= 2
                WITH i, toUpper(parts[0]) AS method, parts[1] AS path
                MATCH (ac:ApiContract {method: method, path: path})
                MERGE (i)-[:CALLS_API]->(ac)
                """).run();
    }

    @Transactional
    public void backfillTriggersConfirmationEdges() {
        // Interaction.confirmationCode → TRIGGERS_CONFIRMATION
        neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.confirmationCode IS NOT NULL AND i.confirmationCode <> ''
                MATCH (dlg:ConfirmationDialog {dialogId: i.confirmationCode})
                MERGE (i)-[:TRIGGERS_CONFIRMATION]->(dlg)
                """).run();
    }

    @Transactional
    public void backfillOnErrorShowsEdges() {
        neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.errorCodeRef IS NOT NULL AND i.errorCodeRef <> ''
                MATCH (ec:ErrorCode {code: i.errorCodeRef})
                MERGE (i)-[:ON_ERROR_SHOWS]->(ec)
                """).run();
    }

    @Transactional
    public void backfillCanProduceErrorEdges() {
        neo4jClient.query("""
                MATCH (i:Interaction)-[:ON_SCREEN]->(s:Screen)
                MATCH (i)-[:ON_ERROR_SHOWS]->(ec:ErrorCode)
                MERGE (s)-[:CAN_PRODUCE_ERROR]->(ec)
                """).run();
    }

    @Transactional
    public void backfillHasInteractionEdges() {
        neo4jClient.query("""
                MATCH (i:Interaction) WHERE i.surfaceId IS NOT NULL AND i.surfaceId <> ''
                MATCH (s:Screen {surfaceId: i.surfaceId})
                MERGE (s)-[:HAS_INTERACTION]->(i)
                """).run();
    }

    @Transactional
    public void backfillDeliversEdges() {
        neo4jClient.query("""
                MATCH (s:Screen) WHERE s.storyRefs IS NOT NULL AND size(s.storyRefs) > 0
                UNWIND s.storyRefs AS storyId
                WITH s, storyId WHERE storyId <> ''
                MATCH (us:UserStory {storyId: storyId})
                MERGE (us)-[:DELIVERS]->(s)
                """).run();
    }

    @Transactional
    public void backfillExecutesInteractionEdges() {
        neo4jClient.query("""
                MATCH (j:Journey)-[:HAS_STEP]->(step:JourneyStep)
                WHERE step.interactionRef IS NOT NULL AND step.interactionRef <> ''
                MATCH (i:Interaction {interactionId: step.interactionRef})
                MERGE (step)-[:EXECUTES_INTERACTION]->(i)
                """).run();
    }

    @Transactional
    public void backfillJourneyStepTraversalEdges() {
        neo4jClient.query("""
                UNWIND [
                  {stepId: 'JRN-R05-001.01', screenId: 'SCR-AGT-GALLERY', touchpointId: 'TP-GALLERY-MENU'},
                  {stepId: 'JRN-R05-001.02', screenId: 'SCR-AGT-GALLERY', touchpointId: null},
                  {stepId: 'JRN-R05-001.03', screenId: 'SCR-AGT-GALLERY', touchpointId: null},
                  {stepId: 'JRN-R05-001.04', screenId: 'SCR-AGT-GALLERY', touchpointId: null},
                  {stepId: 'JRN-R05-001.05', screenId: 'SCR-AGT-BUILDER', touchpointId: null},
                  {stepId: 'JRN-R05-001.06', screenId: 'SCR-AGT-BUILDER', touchpointId: null},
                  {stepId: 'JRN-R05-001.07', screenId: 'SCR-AGT-BUILDER', touchpointId: null},
                  {stepId: 'JRN-R05-002.01', screenId: 'SCR-AGT-LIST', touchpointId: 'TP-AGT-DOCK'},
                  {stepId: 'JRN-R05-002.02', screenId: 'SCR-AGT-LIST', touchpointId: null},
                  {stepId: 'JRN-R05-003.01', screenId: 'SCR-AGT-CHAT', touchpointId: 'TP-CHAT-FAB'},
                  {stepId: 'JRN-R05-003.02', screenId: 'SCR-AGT-CHAT', touchpointId: null},
                  {stepId: 'JRN-R01-001.01', screenId: 'SCR-AUTH', touchpointId: 'TP-AUTH-DIRECT'},
                  {stepId: 'JRN-R01-001.02', screenId: 'SCR-AUTH', touchpointId: null},
                  {stepId: 'JRN-R01-001.03', screenId: 'SURF-HEADER', touchpointId: null},
                  {stepId: 'JRN-R01-002.01', screenId: 'SCR-AUTH', touchpointId: 'TP-PWD-RESET-LINK'},
                  {stepId: 'JRN-R01-002.02', screenId: 'SCR-AUTH-PWD-RESET-REQ', touchpointId: null},
                  {stepId: 'JRN-R01-002.03', screenId: 'SCR-AUTH-PWD-RESET-CONFIRM', touchpointId: null},
                  {stepId: 'JRN-R01-002.04', screenId: 'SCR-AUTH-PWD-RESET-CONFIRM', touchpointId: null}
                ] AS mapping
                MATCH (step:JourneyStep {stepId: mapping.stepId})
                MATCH (screen:Screen {surfaceId: mapping.screenId})
                OPTIONAL MATCH (tp:Touchpoint {touchpointId: mapping.touchpointId})
                MERGE (step)-[:USES_SCREEN]->(screen)
                FOREACH (_ IN CASE WHEN tp IS NULL THEN [] ELSE [1] END |
                  MERGE (step)-[:STARTS_AT_TOUCHPOINT]->(tp)
                )
                """).run();
    }

    // ── D4 engineering seeds (Chunk 3) ────────────────────────────────

    @Transactional
    public void seedAcceptanceCriteria() {
        neo4jClient.query("""
                MERGE (us:UserStory {storyId: 'US-AUTH-001'})
                ON CREATE SET us.label = 'User can sign in', us.module = 'core', us.domain = 'auth', us.storyNumber = 'US-AUTH-001'
                MERGE (ac:AcceptanceCriterion {criterionId: 'AC-US-AUTH-001-001'})
                SET ac.description = 'Login requires valid email and password',
                    ac.givenWhenThen = 'Given valid credentials, when the user submits the login form, then the dashboard is shown',
                    ac.status = 'DEFINED'
                MERGE (us)-[:HAS_CRITERION]->(ac)
                """).run();
    }

    @Transactional
    public void seedDataFields() {
        neo4jClient.query("""
                MERGE (de:DataEntity {entityId: 'DE-AGENT'})
                ON CREATE SET de.name = 'Agent', de.description = 'AI agent configuration', de.entityType = 'CONFIGURATION', de.status = 'DEFINED'
                MERGE (df:DataField {fieldId: 'DF-DE-AGENT-001'})
                SET df.name = 'agentName',
                    df.dataType = 'STRING',
                    df.required = true,
                    df.constraints = 'maxLength=120',
                    df.status = 'DEFINED'
                MERGE (de)-[:HAS_FIELD]->(df)
                """).run();
    }

    @Transactional
    public void seedMessages() {
        neo4jClient.query("""
                MATCH (s:Screen {surfaceId: 'SCR-AUTH'})
                MERGE (m:Message {messageId: 'MSG-CORE-LOGIN-001'})
                SET m.messageText = 'Invalid email or password.',
                    m.messageType = 'VALIDATION',
                    m.severity = 'MEDIUM',
                    m.status = 'DEFINED'
                MERGE (s)-[:HAS_MESSAGE]->(m)
                SET s.messageRegistryCount = 1
                """).run();
    }

    @Transactional
    public void seedValidationRules() {
        neo4jClient.query("""
                MATCH (s:Screen {surfaceId: 'SCR-AUTH'})
                MERGE (r:Rule {ruleId: 'RULE-AUTH-001'})
                ON CREATE SET r.name = 'Password policy', r.description = 'Password must meet security policy', r.ruleType = 'SECURITY', r.status = 'DEFINED'
                MERGE (vr:ValidationRule {validationRuleId: 'VR-AUTH-001'})
                SET vr.fieldPath = 'password',
                    vr.validationType = 'PATTERN',
                    vr.expression = '^(?=.*[A-Z])(?=.*[a-z])(?=.*\\\\d).{8,}$',
                    vr.errorMessage = 'Password must include upper, lower, number, and be at least 8 characters.',
                    vr.status = 'DEFINED'
                MERGE (s)-[:ENFORCES_VALIDATION]->(vr)
                MERGE (r)-[:HAS_VALIDATION_RULE]->(vr)
                """).run();
    }

    @Transactional
    public void seedApiSchemas() {
        neo4jClient.query("""
                MERGE (ac:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                ON CREATE SET ac.method = 'POST', ac.path = '/api/v1/auth/login', ac.description = 'Authenticate user login', ac.status = 'DEFINED'
                MERGE (req:RequestSchema {schemaId: 'REQ-API-POST-API-V1-AUTH-LOGIN-001'})
                SET req.contentType = 'application/json', req.status = 'DEFINED'
                MERGE (res:ResponseSchema {schemaId: 'RES-API-POST-API-V1-AUTH-LOGIN-200'})
                SET res.contentType = 'application/json', res.statusCode = 200, res.status = 'DEFINED'
                MERGE (err:ErrorContract {errorContractId: 'EC-API-POST-API-V1-AUTH-LOGIN-401'})
                SET err.httpStatus = 401, err.errorCode = 'AUTH_INVALID_CREDENTIALS', err.description = 'Credentials are invalid', err.status = 'DEFINED'
                MERGE (ac)-[:HAS_REQUEST]->(req)
                MERGE (ac)-[:HAS_RESPONSE]->(res)
                MERGE (ac)-[:HAS_ERROR]->(err)
                """).run();
    }

    @Transactional
    public void seedTestCaseVerifies() {
        neo4jClient.query("""
                MATCH (s:Screen {surfaceId: 'SCR-AUTH'})
                MERGE (tc:TestCase {testCaseId: 'TC-AUTH-001'})
                SET tc.title = 'Login screen renders',
                    tc.description = 'Validates login screen load and submit action',
                    tc.testType = 'E2E',
                    tc.preconditions = 'Authentication service available',
                    tc.expectedResult = 'User reaches dashboard after valid login',
                    tc.status = 'DEFINED'
                MERGE (tc)-[:VERIFIES]->(s)
                """).run();
    }

    @Transactional
    public void seedStoryRuleEdges() {
        neo4jClient.query("""
                MERGE (us:UserStory {storyId: 'US-AUTH-001'})
                ON CREATE SET us.label = 'User can sign in', us.module = 'core', us.domain = 'auth', us.storyNumber = 'US-AUTH-001'
                MERGE (r:Rule {ruleId: 'RULE-AUTH-001'})
                ON CREATE SET r.name = 'Password policy', r.description = 'Password must meet security policy', r.ruleType = 'SECURITY', r.status = 'DEFINED'
                MERGE (us)-[:GOVERNED_BY_RULE]->(r)
                """).run();
    }

    // ── D5a process spine seeds (Chunk 2) ─────────────────────────────

    @Transactional
    public void seedBusinessDomains() {
        neo4jClient.query("""
                MERGE (dom:BusinessDomain {domainCode: 'DOM-DESIGN'})
                SET dom.name = 'Design Management',
                    dom.description = 'Business domain for design operations',
                    dom.activeStatus = 'ACTIVE'
                MERGE (cap:BusinessCapability {capabilityId: 'CAP-SCREEN-MGMT'})
                ON CREATE SET cap.name = 'Screen Management',
                              cap.description = 'Manage screen inventory and review workflow',
                              cap.status = 'DEFINED'
                MERGE (dom)-[:HAS_CAPABILITY]->(cap)
                """).run();
    }

    @Transactional
    public void seedBusinessProcesses() {
        neo4jClient.query("""
                MATCH (cap:BusinessCapability {capabilityId: 'CAP-SCREEN-MGMT'})
                MERGE (proc:BusinessProcess {processId: 'PROC-SCREEN-REVIEW'})
                SET proc.name = 'Screen Review Process',
                    proc.description = 'Review and approve screen designs',
                    proc.diagramFormat = 'BPMN_XML',
                    proc.diagramVersion = '1.0',
                    proc.diagramSource = 'OMG_BPMN',
                    proc.isExecutableModel = false,
                    proc.status = 'DEFINED'
                MERGE (act:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-001'})
                SET act.name = 'Review screen design',
                    act.description = 'Review submitted design assets',
                    act.activityType = 'TASK',
                    act.actionType = 'REVIEW',
                    act.taskNature = 'USER',
                    act.orderIndex = 1,
                    act.status = 'DEFINED'
                MERGE (gw:ProcessGateway {gatewayId: 'GW-PROC-SCREEN-REVIEW-001'})
                SET gw.name = 'Review outcome',
                    gw.gatewayType = 'EXCLUSIVE',
                    gw.defaultFlowTarget = 'ACT-PROC-SCREEN-REVIEW-003',
                    gw.status = 'DEFINED'
                MERGE (evt:ProcessEvent {eventId: 'EVT-PROC-SCREEN-REVIEW-001'})
                SET evt.name = 'Review started',
                    evt.eventPosition = 'START',
                    evt.eventTrigger = 'NONE',
                    evt.isInterrupting = false,
                    evt.status = 'DEFINED'
                MERGE (cap)-[:REALIZED_BY_PROCESS]->(proc)
                MERGE (proc)-[:HAS_FLOW_NODE]->(act)
                MERGE (proc)-[:HAS_FLOW_NODE]->(gw)
                MERGE (proc)-[:HAS_FLOW_NODE]->(evt)
                """).run();
    }

    @Transactional
    public void seedProcessFlows() {
        neo4jClient.query("""
                MERGE (act1:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-001'})
                ON CREATE SET act1.name = 'Review screen design',
                              act1.activityType = 'TASK',
                              act1.actionType = 'REVIEW',
                              act1.status = 'DEFINED'
                MERGE (act2:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-002'})
                SET act2.name = 'Capture decision',
                    act2.activityType = 'TASK',
                    act2.actionType = 'APPROVE',
                    act2.taskNature = 'USER',
                    act2.orderIndex = 2,
                    act2.status = 'DEFINED'
                MERGE (act3:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-003'})
                SET act3.name = 'Request rework',
                    act3.activityType = 'TASK',
                    act3.actionType = 'UPDATE',
                    act3.taskNature = 'USER',
                    act3.orderIndex = 3,
                    act3.status = 'DEFINED'
                MERGE (gw:ProcessGateway {gatewayId: 'GW-PROC-SCREEN-REVIEW-001'})
                ON CREATE SET gw.name = 'Review outcome',
                              gw.gatewayType = 'EXCLUSIVE',
                              gw.status = 'DEFINED'
                MERGE (evt:ProcessEvent {eventId: 'EVT-PROC-SCREEN-REVIEW-001'})
                ON CREATE SET evt.name = 'Review started',
                              evt.eventPosition = 'START',
                              evt.eventTrigger = 'NONE',
                              evt.status = 'DEFINED'
                MERGE (act1)-[:FLOWS_TO]->(act2)
                MERGE (gw)-[:FLOWS_TO]->(act3)
                MERGE (evt)-[:FLOWS_TO]->(act1)
                """).run();
    }

    @Transactional
    public void seedProcessExpansion() {
        neo4jClient.query("""
                MERGE (subProc:BusinessProcess {processId: 'PROC-SCREEN-DETAIL-REVIEW'})
                SET subProc.name = 'Detailed Screen Review',
                    subProc.description = 'Subprocess for design review details',
                    subProc.diagramFormat = 'BPMN_XML',
                    subProc.diagramVersion = '1.0',
                    subProc.diagramSource = 'OMG_BPMN',
                    subProc.isExecutableModel = false,
                    subProc.status = 'DEFINED'
                MERGE (callProc:BusinessProcess {processId: 'PROC-NOTIFY-REVIEWERS'})
                SET callProc.name = 'Notify Reviewers',
                    callProc.description = 'Called process for reviewer notifications',
                    callProc.diagramFormat = 'BPMN_XML',
                    callProc.diagramVersion = '1.0',
                    callProc.diagramSource = 'OMG_BPMN',
                    callProc.isExecutableModel = false,
                    callProc.status = 'DEFINED'
                MERGE (subAct:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-010'})
                SET subAct.name = 'Run detailed review',
                    subAct.activityType = 'SUBPROCESS',
                    subAct.actionType = 'REVIEW',
                    subAct.taskNature = 'USER',
                    subAct.orderIndex = 10,
                    subAct.status = 'DEFINED'
                MERGE (callAct:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-011'})
                SET callAct.name = 'Notify reviewers',
                    callAct.activityType = 'CALL_ACTIVITY',
                    callAct.actionType = 'NOTIFY',
                    callAct.taskNature = 'SERVICE',
                    callAct.orderIndex = 11,
                    callAct.status = 'DEFINED'
                MERGE (subAct)-[:EXPANDS_TO]->(subProc)
                MERGE (callAct)-[:CALLS_PROCESS]->(callProc)
                """).run();
    }

    @Transactional
    public void seedBoundaryEvent() {
        neo4jClient.query("""
                MERGE (host:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-002'})
                ON CREATE SET host.name = 'Capture decision',
                              host.activityType = 'TASK',
                              host.actionType = 'APPROVE',
                              host.status = 'DEFINED'
                MERGE (escalate:ProcessActivity {activityId: 'ACT-PROC-SCREEN-REVIEW-004'})
                SET escalate.name = 'Escalate stalled review',
                    escalate.activityType = 'TASK',
                    escalate.actionType = 'NOTIFY',
                    escalate.taskNature = 'SERVICE',
                    escalate.orderIndex = 4,
                    escalate.status = 'DEFINED'
                MERGE (evt:ProcessEvent {eventId: 'EVT-PROC-SCREEN-REVIEW-002'})
                SET evt.name = 'Review timeout',
                    evt.eventPosition = 'BOUNDARY',
                    evt.eventTrigger = 'TIMER',
                    evt.isInterrupting = true,
                    evt.attachedToRef = 'ACT-PROC-SCREEN-REVIEW-002',
                    evt.status = 'DEFINED'
                MERGE (evt)-[:ATTACHED_TO]->(host)
                MERGE (evt)-[:FLOWS_TO]->(escalate)
                """).run();
    }

    @Transactional
    public void seedStoryTasks() {
        neo4jClient.query("""
                MERGE (us:UserStory {storyId: 'US-AUTH-001'})
                ON CREATE SET us.label = 'User can sign in', us.module = 'core', us.domain = 'auth', us.storyNumber = 'US-AUTH-001'
                MERGE (t:Task {taskId: 'TASK-US-AUTH-001-001'})
                SET t.title = 'Implement login form',
                    t.description = 'Build login form validation and submit behavior',
                    t.taskType = 'IMPLEMENTATION',
                    t.status = 'DEFINED',
                    t.priority = 'HIGH'
                MERGE (us)-[:HAS_TASK]->(t)
                """).run();
    }

    // ── D6a traceability seeds (Chunk 2) ─────────────────────────────

    @Transactional
    public void seedSourceReferences() {
        neo4jClient.query("""
                UNWIND [
                  {id: 'SRC-US-AUTH-001', path: 'Documentation/.Requirements/CONSOLIDATED-STORY-INVENTORY.md', section: 'Authentication', line: '120-138', url: null},
                  {id: 'SRC-SCR-AUTH-001', path: 'documentation/vision-benchmark.md', section: 'Query 8', line: '401-410', url: null},
                  {id: 'SRC-BUG-001', path: 'Documentation/governance/ai-discussions/discussion-20260301-155248.md', section: 'Login refresh issue', line: '44-61', url: null}
                ] AS src
                MERGE (sr:SourceReference {sourceId: src.id})
                SET sr.artifactPath = src.path,
                    sr.section = src.section,
                    sr.lineRef = src.line,
                    sr.url = src.url,
                    sr.status = 'DEFINED'
                """).run();
    }

    @Transactional
    public void seedExternalArtifacts() {
        neo4jClient.query("""
                UNWIND [
                  {id: 'EXT-JIRA-001', system: 'JIRA', externalType: 'STORY', key: 'DH-101', url: 'https://jira.example.com/browse/DH-101', sync: 'SYNCED', syncedAt: '2026-03-16T08:00:00Z'},
                  {id: 'EXT-AZDO-001', system: 'AZURE_DEVOPS', externalType: 'BUG', key: 'AB#245', url: 'https://dev.azure.com/example/designhub/_workitems/edit/245', sync: 'SYNCED', syncedAt: '2026-03-16T08:05:00Z'}
                ] AS ext
                MERGE (ea:ExternalArtifact {externalId: ext.id})
                SET ea.system = ext.system,
                    ea.externalType = ext.externalType,
                    ea.key = ext.key,
                    ea.url = ext.url,
                    ea.syncStatus = ext.sync,
                    ea.lastSyncedAt = datetime(ext.syncedAt),
                    ea.status = 'DEFINED'
                """).run();
    }

    @Transactional
    public void seedTraceabilityEdges() {
        neo4jClient.query("""
                MERGE (story:UserStory {storyId: 'US-AUTH-001'})
                ON CREATE SET story.label = 'User can sign in',
                              story.module = 'core',
                              story.domain = 'auth',
                              story.storyNumber = 'US-AUTH-001'
                MERGE (screen:Screen {surfaceId: 'SCR-AUTH'})
                ON CREATE SET screen.label = 'Login / Sign In',
                              screen.module = 'core',
                              screen.routePath = '/login',
                              screen.status = 'DEFINED'
                MERGE (bug:Bug {bugId: 'BUG-001'})
                SET bug.externalKey = 'AB#245',
                    bug.summary = 'Session refresh banner stays visible after login retry',
                    bug.severity = 'HIGH',
                    bug.status = 'IDENTIFIED'
                MATCH (storySrc:SourceReference {sourceId: 'SRC-US-AUTH-001'})
                MATCH (screenSrc:SourceReference {sourceId: 'SRC-SCR-AUTH-001'})
                MATCH (bugSrc:SourceReference {sourceId: 'SRC-BUG-001'})
                MATCH (jiraStory:ExternalArtifact {externalId: 'EXT-JIRA-001'})
                MATCH (azdoBug:ExternalArtifact {externalId: 'EXT-AZDO-001'})
                MERGE (story)-[:HAS_SOURCE]->(storySrc)
                MERGE (screen)-[:HAS_SOURCE]->(screenSrc)
                MERGE (bug)-[:HAS_SOURCE]->(bugSrc)
                MERGE (bug)-[:AFFECTS_SCREEN]->(screen)
                MERGE (jiraStory)-[:REPRESENTS_STORY]->(story)
                MERGE (azdoBug)-[:REPRESENTS_BUG]->(bug)
                """).run();
    }

    // ── D6a screen-flow seeds (Chunk 3) ────────────────────────────────

    @Transactional
    public void seedScreenStates() {
        neo4jClient.query("""
                MATCH (authScreen:Screen {surfaceId: 'SCR-AUTH'})
                MERGE (empty:ScreenState {stateId: 'STATE-SCR-AUTH-EMPTY'})
                SET empty.name = 'Empty credentials',
                    empty.stateType = 'EMPTY',
                    empty.entryCondition = 'Page loads with no prior input',
                    empty.exitCondition = 'User types in any field',
                    empty.status = 'DEFINED'
                MERGE (loading:ScreenState {stateId: 'STATE-SCR-AUTH-LOADING'})
                SET loading.name = 'Authenticating',
                    loading.stateType = 'LOADING',
                    loading.entryCondition = 'User submits login form',
                    loading.exitCondition = 'API response received',
                    loading.status = 'DEFINED'
                MERGE (error:ScreenState {stateId: 'STATE-SCR-AUTH-ERROR'})
                SET error.name = 'Login failed',
                    error.stateType = 'ERROR',
                    error.entryCondition = 'API returns 401',
                    error.exitCondition = 'User retries or navigates away',
                    error.status = 'DEFINED'
                MERGE (empty)-[:BELONGS_TO_SCREEN]->(authScreen)
                MERGE (loading)-[:BELONGS_TO_SCREEN]->(authScreen)
                MERGE (error)-[:BELONGS_TO_SCREEN]->(authScreen)
                """).run();
    }

    @Transactional
    public void seedTransitions() {
        neo4jClient.query("""
                MATCH (authScreen:Screen {surfaceId: 'SCR-AUTH'})
                MERGE (dashScreen:Screen {surfaceId: 'SCR-DASHBOARD'})
                ON CREATE SET dashScreen.label = 'Dashboard',
                              dashScreen.module = 'core',
                              dashScreen.routePath = '/dashboard',
                              dashScreen.status = 'DEFINED'
                MERGE (loginInt:Interaction {interactionId: 'INT-G-001'})
                ON CREATE SET loginInt.element = 'Submit login',
                              loginInt.trigger = 'CLICK',
                              loginInt.surfaceId = 'SCR-AUTH'
                MERGE (trn:Transition {transitionId: 'TRN-SCR-AUTH-TO-DASH'})
                SET trn.name = 'Login success redirect',
                    trn.transitionType = 'NAVIGATION',
                    trn.guard = 'authenticated == true',
                    trn.status = 'DEFINED'
                MERGE (trn)-[:FROM_SCREEN]->(authScreen)
                MERGE (trn)-[:TO_SCREEN]->(dashScreen)
                MERGE (trn)-[:CAUSED_BY_INTERACTION]->(loginInt)
                """).run();
    }

    // ── Patches (existing) ─────────────────────────────────────────────

    @Transactional
    public void patchChannelCodes() {
        // Migrate legacy CH-WEB → CH-WEB-DSK on persisted EntryMode nodes
        neo4jClient.query("""
                MATCH (em:EntryMode) WHERE em.channelId = 'CH-WEB'
                SET em.channelId = 'CH-WEB-DSK'
                """).run();
    }

    @Transactional
    public void patchInteractionPermissions() {
        // Set permission on interactions that logically require one.
        // These were seeded as null; this patch makes permissions observable live.
        neo4jClient.query("""
                UNWIND [
                  {id: 'INT-R05-AGT-LIST-003', perm: 'ADMIN'},
                  {id: 'INT-R05-BUILDER-004',  perm: 'ADMIN'},
                  {id: 'INT-R05-AGT-LIST-002', perm: 'AGENT_DESIGNER'},
                  {id: 'INT-R05-CHAT-003',     perm: 'HITL_REVIEWER'}
                ] AS patch
                MATCH (i:Interaction {interactionId: patch.id})
                SET i.permission = patch.perm
                """).run();
    }

    @Transactional
    public void patchInteractionOutcomes() {
        neo4jClient.query("""
                UNWIND [
                  {id: 'INT-G-002', success: 'Matching entities are returned as the user types.', error: 'Search could not be completed.', loading: 'Searching…', code: 'CORE-E-SEARCH-001'},
                  {id: 'INT-G-003', success: 'Notification is marked as read and the linked target is opened.', error: 'Notification update failed.', loading: 'Updating notification…', code: 'CORE-E-NOTIF-001'},
                  {id: 'INT-G-004', success: 'Session is extended and the timeout modal closes.', error: 'Session refresh failed.', loading: 'Refreshing session…', code: 'AUTH-E-401'},
                  {id: 'INT-R05-AGT-LIST-001', success: 'Agent details are loaded.', error: 'Agent details could not be loaded.', loading: 'Loading agent details…', code: 'AGT-E-404'},
                  {id: 'INT-R05-AGT-LIST-002', success: 'Agent builder opens in create mode.', error: 'You are not allowed to create agents.', loading: null, code: 'AGT-E-403'},
                  {id: 'INT-R05-AGT-LIST-003', success: 'Agent was deleted successfully.', error: 'Agent could not be deleted.', loading: 'Deleting agent…', code: 'AGT-E-404'},
                  {id: 'INT-R05-BUILDER-001', success: 'Component is dropped onto the canvas.', error: 'Component could not be added to the canvas.', loading: null, code: 'AGT-E-BUILDER-001'},
                  {id: 'INT-R05-BUILDER-002', success: 'Draft agent is saved.', error: 'Draft save failed.', loading: 'Saving draft…', code: 'AGT-E-BUILDER-002'},
                  {id: 'INT-R05-BUILDER-003', success: 'Playground session is ready.', error: 'Test session could not be created.', loading: 'Creating test session…', code: 'AGT-E-PLAYGROUND-001'},
                  {id: 'INT-R05-BUILDER-004', success: 'Agent was published.', error: 'Agent publish failed.', loading: 'Publishing agent…', code: 'AGT-E-403'},
                  {id: 'INT-R05-GALLERY-001', success: 'Template details drawer opens.', error: 'Template details could not be loaded.', loading: 'Loading template…', code: 'TPL-E-404'},
                  {id: 'INT-R05-GALLERY-002', success: 'Gallery is filtered by category.', error: 'Category filter could not be applied.', loading: 'Filtering templates…', code: 'TPL-E-QUERY-001'},
                  {id: 'INT-R05-GALLERY-003', success: 'Template is forked into a draft agent.', error: 'Template fork failed.', loading: 'Forking template…', code: 'TPL-E-FORK-001'},
                  {id: 'INT-R05-CHAT-001', success: 'Message is sent to the agent.', error: 'Chat response could not be generated.', loading: 'Agent is thinking…', code: 'CHAT-E-STREAM-001'},
                  {id: 'INT-R05-CHAT-002', success: 'Generation is stopped.', error: 'Generation could not be stopped.', loading: null, code: 'CHAT-E-STREAM-002'},
                  {id: 'INT-R05-CHAT-003', success: 'Conversation is escalated to a human reviewer.', error: 'Escalation failed.', loading: 'Escalating…', code: 'CHAT-E-503'}
                ] AS patch
                MATCH (i:Interaction {interactionId: patch.id})
                SET i.outcomeSuccess = patch.success,
                    i.outcomeError = patch.error,
                    i.outcomeLoading = patch.loading,
                    i.errorCodeRef = patch.code
                """).run();
    }

    // ── Technical execution context activation ────────────────────────

    @Transactional
    public void seedApplicationsAndComponents() {
        neo4jClient.query("""
                MERGE (app:Application {applicationId: 'APP-DH'})
                SET app.name = 'Design Hub',
                    app.description = 'Design Hub workspace and graph explorer',
                    app.repoPath = '.',
                    app.repoUrl = 'https://example.invalid/emsist/design-hub.git',
                    app.workspaceType = 'MONOREPO',
                    app.defaultBuildCommand = 'mvn -q -DskipTests package',
                    app.defaultTestCommand = 'mvn -q test',
                    app.status = 'IMPLEMENTED'
                MERGE (fe:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                SET fe.name = 'Design Hub Frontend',
                    fe.description = 'Angular user interface for the Design Hub workspace',
                    fe.componentType = 'FRONTEND_APP',
                    fe.frameworkFamily = 'ANGULAR',
                    fe.frameworkName = 'Angular',
                    fe.frameworkVersion = '21',
                    fe.runtime = 'BROWSER',
                    fe.language = 'TYPESCRIPT',
                    fe.languageVersion = '5',
                    fe.modulePath = 'frontend',
                    fe.manifestPath = 'package.json',
                    fe.buildCommand = 'npm run build',
                    fe.testCommand = 'npm run test:e2e',
                    fe.entrypointPath = 'src/main.ts',
                    fe.status = 'IMPLEMENTED'
                MERGE (be:ApplicationComponent {componentId: 'CMP-DH-BACKEND'})
                SET be.name = 'Design Hub Backend',
                    be.description = 'Spring Boot API and graph orchestration layer',
                    be.componentType = 'MICROSERVICE',
                    be.frameworkFamily = 'SPRING_BOOT',
                    be.frameworkName = 'Spring Boot',
                    be.frameworkVersion = '3.4',
                    be.runtime = 'JVM',
                    be.language = 'JAVA',
                    be.languageVersion = '23',
                    be.modulePath = 'backend',
                    be.manifestPath = 'pom.xml',
                    be.buildCommand = 'mvn -q -DskipTests package',
                    be.testCommand = 'mvn -q test',
                    be.entrypointPath = 'src/main/java/com/emsist/designhub/DesignHubApplication.java',
                    be.status = 'IMPLEMENTED'
                MERGE (app)-[:HAS_COMPONENT]->(fe)
                MERGE (app)-[:HAS_COMPONENT]->(be)
                MERGE (fe)-[:DEPENDS_ON_COMPONENT]->(be)
                """).run();
    }

    @Transactional
    public void seedImplementationPackArtifacts() {
        neo4jClient.query("""
                MERGE (convFe:CodingConvention {conventionCode: 'CONV-FE-TEST-001'})
                SET convFe.name = 'Frontend smoke and semantic test convention',
                    convFe.category = 'TESTING',
                    convFe.enforcement = 'MANDATORY',
                    convFe.scope = 'FRONTEND',
                    convFe.docRef = 'documentation/design-testing-strategy.md',
                    convFe.summary = 'Frontend changes must preserve Playwright smoke and semantic interaction coverage.',
                    convFe.activeStatus = 'ACTIVE'
                MERGE (convBe:CodingConvention {conventionCode: 'CONV-BE-LAYER-001'})
                SET convBe.name = 'Backend layering convention',
                    convBe.category = 'STRUCTURE',
                    convBe.enforcement = 'MANDATORY',
                    convBe.scope = 'BACKEND',
                    convBe.docRef = 'documentation/architecture-blueprint.md',
                    convBe.summary = 'Keep graph orchestration inside services and preserve explicit domain layering.',
                    convBe.activeStatus = 'ACTIVE'
                MATCH (app:Application {applicationId: 'APP-DH'})
                MATCH (fe:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (be:ApplicationComponent {componentId: 'CMP-DH-BACKEND'})
                MATCH (builder:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MATCH (gallery:Screen {surfaceId: 'SCR-AGT-GALLERY'})
                MATCH (list:Screen {surfaceId: 'SCR-AGT-LIST'})
                MATCH (api:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MATCH (entity:DataEntity {entityId: 'DE-AGENT'})
                MATCH (rule:Rule {ruleId: 'RULE-AUTH-001'})
                MERGE (fe)-[:SUPPORTS_SCREEN]->(builder)
                MERGE (fe)-[:SUPPORTS_SCREEN]->(gallery)
                MERGE (fe)-[:SUPPORTS_SCREEN]->(list)
                MERGE (be)-[:EXPOSES]->(api)
                MERGE (be)-[:OWNS_DATA_ENTITY]->(entity)
                MERGE (be)-[:ENFORCES_RULE]->(rule)
                MERGE (app)-[:GOVERNED_BY_CONVENTION]->(convFe)
                MERGE (fe)-[:GOVERNED_BY_CONVENTION]->(convFe)
                MERGE (be)-[:GOVERNED_BY_CONVENTION]->(convBe)
                MERGE (caPage:CodeAsset {codeAssetId: 'CA-FE-DH-PAGE-001'})
                SET caPage.filePath = 'src/app/features/design-hub/design-hub.page.ts',
                    caPage.assetType = 'SOURCE',
                    caPage.language = 'TYPESCRIPT',
                    caPage.layerType = 'PAGE',
                    caPage.description = 'Design Hub page shell',
                    caPage.status = 'IMPLEMENTED'
                MERGE (caCanvas:CodeAsset {codeAssetId: 'CA-FE-BUILDER-CANVAS-001'})
                SET caCanvas.filePath = 'src/app/features/design-hub/components/flow-canvas/flow-canvas.component.ts',
                    caCanvas.assetType = 'SOURCE',
                    caCanvas.language = 'TYPESCRIPT',
                    caCanvas.layerType = 'COMPONENT',
                    caCanvas.description = 'Builder canvas implementation',
                    caCanvas.status = 'IMPLEMENTED'
                MERGE (caSidebar:CodeAsset {codeAssetId: 'CA-FE-BUILDER-SIDEBAR-001'})
                SET caSidebar.filePath = 'src/app/features/design-hub/components/screen-sidebar/screen-sidebar.component.ts',
                    caSidebar.assetType = 'SOURCE',
                    caSidebar.language = 'TYPESCRIPT',
                    caSidebar.layerType = 'COMPONENT',
                    caSidebar.description = 'Builder sidebar implementation',
                    caSidebar.status = 'IMPLEMENTED'
                MERGE (caDetail:CodeAsset {codeAssetId: 'CA-FE-BUILDER-DETAIL-001'})
                SET caDetail.filePath = 'src/app/features/design-hub/components/detail-panel/detail-panel.component.ts',
                    caDetail.assetType = 'SOURCE',
                    caDetail.language = 'TYPESCRIPT',
                    caDetail.layerType = 'COMPONENT',
                    caDetail.description = 'Builder detail panel implementation',
                    caDetail.status = 'IMPLEMENTED'
                MERGE (caSpec:CodeAsset {codeAssetId: 'CA-FE-BUILDER-E2E-001'})
                SET caSpec.filePath = 'tests/graph/screen-selection.spec.ts',
                    caSpec.assetType = 'TEST',
                    caSpec.language = 'TYPESCRIPT',
                    caSpec.layerType = 'TEST',
                    caSpec.description = 'Playwright builder interaction coverage',
                    caSpec.status = 'IMPLEMENTED'
                MERGE (fe)-[:HAS_CODE_ASSET]->(caPage)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caCanvas)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caSidebar)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caDetail)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caSpec)
                MERGE (caCanvas)-[:ASSET_FOR_SCREEN]->(builder)
                MERGE (caSidebar)-[:ASSET_FOR_SCREEN]->(builder)
                MERGE (caDetail)-[:ASSET_FOR_SCREEN]->(builder)
                MERGE (caPage)-[:ASSET_FOR_SCREEN]->(list)
                MERGE (caPage)-[:ASSET_FOR_SCREEN]->(gallery)
                MERGE (caCanvas)-[:GOVERNED_BY_CONVENTION]->(convFe)
                MERGE (caSpec)-[:GOVERNED_BY_CONVENTION]->(convFe)
                """).run();
    }

    @Transactional
    public void seedImplementationPackVerification() {
        neo4jClient.query("""
                MATCH (us:UserStory {storyId: 'US-AI-090'})
                MATCH (fe:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (builderAsset:CodeAsset {codeAssetId: 'CA-FE-BUILDER-CANVAS-001'})
                MATCH (testAsset:CodeAsset {codeAssetId: 'CA-FE-BUILDER-E2E-001'})
                MATCH (builderScreen:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MERGE (task:Task {taskId: 'TASK-US-AI-090-001'})
                SET task.title = 'Implement builder canvas interactions',
                    task.description = 'Wire the builder canvas and related panels for agent composition.',
                    task.taskType = 'FRONTEND',
                    task.priority = 'HIGH',
                    task.status = 'IN_IMPLEMENTATION'
                MERGE (tc:TestCase {testCaseId: 'TC-US-AI-090-001'})
                SET tc.title = 'Builder screen selection smoke',
                    tc.description = 'Verifies builder screen selection and detail synchronization.',
                    tc.testType = 'E2E',
                    tc.preconditions = 'Frontend and backend are running with seeded data',
                    tc.expectedResult = 'Selecting a builder screen updates detail context with no runtime errors.',
                    tc.testFilePath = 'frontend/tests/graph/screen-selection.spec.ts',
                    tc.testFramework = 'PLAYWRIGHT',
                    tc.suiteName = 'frontend-playwright',
                    tc.testCommand = 'npm run test:e2e',
                    tc.status = 'DEFINED'
                MERGE (us)-[:HAS_TASK]->(task)
                MERGE (us)-[:VERIFIED_BY]->(tc)
                MERGE (tc)-[:VERIFIES]->(builderScreen)
                MERGE (tc)-[:LOCATED_IN]->(testAsset)
                MERGE (task)-[:IMPLEMENTS]->(fe)
                MERGE (task)-[:IMPLEMENTS]->(builderAsset)
                """).run();
    }

    // ── Orchestration ──────────────────────────────────────────────────

    @Transactional
    public void runFullMigration() {
        // 1. Seed all registry nodes first
        seedChannels();
        seedPermissions();
        seedBusinessRoles();
        seedValidationRoles();
        seedConfirmationDialogs();
        seedErrorCodes();

        // 2. Patch persisted data before backfilling edges
        patchChannelCodes();
        patchInteractionPermissions();
        patchInteractionOutcomes();

        // 3. Upsert ApiContract nodes from interaction apiCalls strings
        upsertApiContractsFromInteractions();

        // 4. Backfill existing edges (personas, screen roles, channels, permissions)
        backfillPersonas();
        backfillAccessibleByRoleEdges();
        backfillDeliveredViaChannelEdges();
        backfillRequiresPermissionEdges();

        // 5. Backfill new edges (interaction/touchpoint roles, API calls, confirmations)
        backfillInteractionPersonaEdges();
        backfillInteractionRoleEdges();
        backfillTouchpointRoleEdges();
        backfillCallsApiEdges();
        backfillTriggersConfirmationEdges();
        backfillOnErrorShowsEdges();
        backfillCanProduceErrorEdges();
        backfillHasInteractionEdges();
        backfillDeliversEdges();
        backfillExecutesInteractionEdges();
        backfillJourneyStepTraversalEdges();

        // 6. Seed D4 engineering edge coverage
        seedAcceptanceCriteria();
        seedDataFields();
        seedMessages();
        seedValidationRules();
        seedApiSchemas();
        seedTestCaseVerifies();
        seedStoryRuleEdges();

        // 7. Seed D5a process spine coverage
        seedBusinessDomains();
        seedBusinessProcesses();
        seedProcessFlows();
        seedProcessExpansion();
        seedBoundaryEvent();
        seedStoryTasks();

        // 8. Seed D6a traceability coverage
        seedSourceReferences();
        seedExternalArtifacts();
        seedTraceabilityEdges();

        // 9. Seed D6a screen-flow coverage
        seedScreenStates();
        seedTransitions();

        // 10. Seed technical execution context coverage
        seedApplicationsAndComponents();
        seedImplementationPackArtifacts();
        seedImplementationPackVerification();
    }
}
