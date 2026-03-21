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
    private final ExternalArtifactSyncService externalArtifactSyncService;

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
    public void backfillPersonaUsageFromJourneys() {
        neo4jClient.query("""
                MATCH (p:Persona)<-[:PERFORMED_BY_PERSONA]-(j:Journey)-[:HAS_STEP]->(step:JourneyStep)
                OPTIONAL MATCH (step)-[:USES_SCREEN]->(screen:Screen)
                OPTIONAL MATCH (step)-[:STARTS_AT_TOUCHPOINT]->(touchpoint:Touchpoint)
                OPTIONAL MATCH (step)-[:EXECUTES_INTERACTION]->(interaction:Interaction)
                FOREACH (_ IN CASE WHEN screen IS NULL THEN [] ELSE [1] END |
                    MERGE (screen)-[:USED_BY_PERSONA]->(p)
                )
                FOREACH (_ IN CASE WHEN touchpoint IS NULL THEN [] ELSE [1] END |
                    MERGE (touchpoint)-[:USED_BY_PERSONA]->(p)
                )
                FOREACH (_ IN CASE WHEN interaction IS NULL THEN [] ELSE [1] END |
                    MERGE (interaction)-[:USED_BY_PERSONA]->(p)
                )
                """).run();
    }

    @Transactional
    public void backfillJourneyPersonaCoverage() {
        neo4jClient.query("""
                MATCH (j:Journey)
                WHERE j.personaId IS NULL
                   OR trim(j.personaId) = ''
                   OR NOT EXISTS { (j)-[:PERFORMED_BY_PERSONA]->(:Persona) }
                CALL (j) {
                    OPTIONAL MATCH (j)-[:HAS_STEP]->(step:JourneyStep)-[:USES_SCREEN]->(screen:Screen)
                    OPTIONAL MATCH (screen)<-[:TARGETS]-(touchpoint:Touchpoint)-[:USED_BY_PERSONA]->(personaFromTouchpoint:Persona)
                    OPTIONAL MATCH (step)-[:EXECUTES_INTERACTION]->(interaction:Interaction)-[:USED_BY_PERSONA]->(personaFromInteraction:Persona)
                    OPTIONAL MATCH (screen)-[:USED_BY_PERSONA]->(personaFromScreen:Persona)
                    WITH collect(DISTINCT personaFromTouchpoint.personaId)
                         + collect(DISTINCT personaFromInteraction.personaId)
                         + collect(DISTINCT personaFromScreen.personaId) AS candidateIds
                    RETURN head([candidateId IN candidateIds WHERE candidateId IS NOT NULL]) AS inferredPersonaId
                }
                WITH j, inferredPersonaId
                WHERE inferredPersonaId IS NOT NULL
                MERGE (p:Persona {personaId: inferredPersonaId})
                ON CREATE SET p.name = inferredPersonaId, p.status = 'IDENTIFIED'
                SET j.personaId = inferredPersonaId
                MERGE (j)-[:PERFORMED_BY_PERSONA]->(p)
                """).run();
    }

    @Transactional
    public void backfillExternalNodeIds() {
        neo4jClient.query("""
                MATCH (content:ContentElement)
                WHERE content.contentElementId IS NULL OR trim(content.contentElementId) = ''
                SET content.contentElementId = randomUUID()
                """).run();

        neo4jClient.query("""
                MATCH (effect:Effect)
                WHERE effect.effectId IS NULL OR trim(effect.effectId) = ''
                SET effect.effectId = randomUUID()
                """).run();

        neo4jClient.query("""
                MATCH (entry:EntryMode)
                WHERE entry.entryModeId IS NULL OR trim(entry.entryModeId) = ''
                SET entry.entryModeId = randomUUID()
                """).run();

        neo4jClient.query("""
                MATCH (step:JourneyStep)
                WHERE step.stepId IS NULL OR trim(step.stepId) = ''
                SET step.stepId = coalesce(
                    CASE
                        WHEN step.journeyId IS NULL OR trim(step.journeyId) = '' THEN null
                        ELSE step.journeyId + '.' + toString(coalesce(step.orderIndex, 0))
                    END,
                    'JSTEP-' + randomUUID()
                )
                """).run();
    }

    @Transactional
    public void backfillPersonaRoleKeys() {
        neo4jClient.query("""
                MATCH (p:Persona)
                CALL (p) {
                    OPTIONAL MATCH (p)<-[:USED_BY_PERSONA]-(screen:Screen)-[:ACCESSIBLE_BY_ROLE]->(role:BusinessRole)
                    RETURN collect(DISTINCT role.roleKey) AS screenRoleKeys
                }
                CALL (p) {
                    OPTIONAL MATCH (p)<-[:USED_BY_PERSONA]-(touchpoint:Touchpoint)-[:ACCESSIBLE_BY_ROLE]->(role:BusinessRole)
                    RETURN collect(DISTINCT role.roleKey) AS touchpointRoleKeys
                }
                CALL (p) {
                    OPTIONAL MATCH (p)<-[:USED_BY_PERSONA]-(interaction:Interaction)-[:ACCESSIBLE_BY_ROLE]->(role:BusinessRole)
                    RETURN collect(DISTINCT role.roleKey) AS interactionRoleKeys
                }
                WITH p, [roleKey IN screenRoleKeys + touchpointRoleKeys + interactionRoleKeys WHERE roleKey IS NOT NULL] AS rawRoleKeys
                SET p.roleKeys = reduce(deduped = [], roleKey IN rawRoleKeys |
                    CASE WHEN roleKey IN deduped THEN deduped ELSE deduped + roleKey END
                )
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
    public void backfillTouchpointPersonaCoverage() {
        neo4jClient.query("""
                MATCH (tp:Touchpoint)
                WHERE NOT EXISTS { (tp)-[:USED_BY_PERSONA]->(:Persona) }
                CALL (tp) {
                    OPTIONAL MATCH (tp)-[:TARGETS]->(screen:Screen)-[:USED_BY_PERSONA]->(screenPersona:Persona)
                    RETURN collect(DISTINCT screenPersona.personaId) AS screenPersonaIds
                }
                CALL (tp) {
                    OPTIONAL MATCH (tp)-[:TARGETS]->(screen:Screen)<-[:ON_SCREEN]-(interaction:Interaction)-[:USED_BY_PERSONA]->(interactionPersona:Persona)
                    RETURN collect(DISTINCT interactionPersona.personaId) AS interactionPersonaIds
                }
                WITH tp, [personaId IN screenPersonaIds + interactionPersonaIds WHERE personaId IS NOT NULL] AS rawPersonaIds
                WITH tp, reduce(deduped = [], personaId IN rawPersonaIds |
                    CASE WHEN personaId IN deduped THEN deduped ELSE deduped + personaId END
                ) AS personaIds
                WHERE size(personaIds) > 0
                SET tp.personaIds = personaIds
                WITH tp, personaIds
                UNWIND personaIds AS personaId
                MATCH (p:Persona {personaId: personaId})
                MERGE (tp)-[:USED_BY_PERSONA]->(p)
                """).run();
    }

    @Transactional
    public void patchTouchpointBenchmarkPersonas() {
        neo4jClient.query("""
                UNWIND [
                  {touchpointId: 'TP-NOTIF-CLICK', personaId: 'PER-UX-005'},
                  {touchpointId: 'TP-R04-DOCK', personaId: 'PER-UX-004'},
                  {touchpointId: 'TP-R06-SETTINGS', personaId: 'PER-UX-005'}
                ] AS patch
                MATCH (tp:Touchpoint {touchpointId: patch.touchpointId})
                MATCH (persona:Persona {personaId: patch.personaId})
                SET tp.personaIds = reduce(deduped = coalesce(tp.personaIds, []), personaId IN [patch.personaId] |
                    CASE WHEN personaId IN deduped THEN deduped ELSE deduped + personaId END
                )
                MERGE (tp)-[:USED_BY_PERSONA]->(persona)
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
                UNWIND [
                  {
                    storyId: 'US-AUTH-001',
                    label: 'User can sign in',
                    module: 'core',
                    domain: 'auth',
                    criterionId: 'AC-US-AUTH-001-001',
                    description: 'Login requires valid email and password',
                    givenWhenThen: 'Given valid credentials, when the user submits the login form, then the dashboard is shown'
                  },
                  {
                    storyId: 'US-AI-090',
                    label: 'Builder canvas interactions ready for agent composition',
                    module: 'ai',
                    domain: 'agents',
                    criterionId: 'AC-US-AI-090-001',
                    description: 'Canvas selection stays synchronized with the detail context',
                    givenWhenThen: 'Given the builder screen is loaded, when the user selects a canvas node, then the detail context updates with the selected object and no runtime errors occur'
                  },
                  {
                    storyId: 'US-AI-137',
                    label: 'Agent list browsing resolves a complete automation handoff',
                    module: 'ai',
                    domain: 'agents',
                    criterionId: 'AC-US-AI-137-001',
                    description: 'The agent list story resolves a complete implementation pack for automation handoff.',
                    givenWhenThen: 'Given the seeded delivery stories are available, when the agent list story is opened in the automation tab, then the implementation pack resolves application, component, code, and test targets with no missing blocking readiness fields'
                  },
                  {
                    storyId: 'US-AI-078',
                    label: 'Template gallery browsing resolves a complete automation handoff',
                    module: 'ai',
                    domain: 'agents',
                    criterionId: 'AC-US-AI-078-001',
                    description: 'The template gallery story resolves a complete implementation pack for automation handoff.',
                    givenWhenThen: 'Given the seeded delivery stories are available, when the template gallery story is opened in the automation tab, then the implementation pack resolves application, component, code, and test targets with no missing blocking readiness fields'
                  },
                  {
                    storyId: 'US-AI-139',
                    label: 'Agent detail tabs resolve a complete automation handoff',
                    module: 'ai',
                    domain: 'agents',
                    criterionId: 'AC-US-AI-139-001',
                    description: 'The agent detail story resolves a complete implementation pack for automation handoff.',
                    givenWhenThen: 'Given the seeded delivery stories are available, when the agent detail story is opened in the automation tab, then the implementation pack resolves application, component, code, and test targets with no missing blocking readiness fields'
                  }
                ] AS row
                MERGE (us:UserStory {storyId: row.storyId})
                ON CREATE SET us.label = row.label,
                              us.module = row.module,
                              us.domain = row.domain,
                              us.storyNumber = row.storyId
                MERGE (ac:AcceptanceCriterion {criterionId: row.criterionId})
                SET ac.description = row.description,
                    ac.givenWhenThen = row.givenWhenThen,
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
    public void seedDataQualityConstraints() {
        neo4jClient.query("""
                MATCH (de:DataEntity)
                MERGE (qc:QualityConstraint {constraintId: 'QC-' + de.entityId + '-001'})
                SET qc.name = coalesce(qc.name, de.name + ' data quality baseline'),
                    qc.description = coalesce(qc.description, 'Baseline data quality constraint for ' + coalesce(de.name, de.entityId) + '.'),
                    qc.constraintType = coalesce(qc.constraintType, 'DATA_QUALITY'),
                    qc.threshold = coalesce(qc.threshold, 'Required fields present and schema-valid'),
                    qc.measurementMethod = coalesce(qc.measurementMethod, 'Schema validation plus required-field audit'),
                    qc.priority = coalesce(qc.priority, 'HIGH'),
                    qc.status = 'DEFINED'
                MERGE (de)-[:HAS_QUALITY_CONSTRAINT]->(qc)
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
    public void seedApiContractCoverageDefaults() {
        neo4jClient.query("""
                MATCH (ac:ApiContract)
                SET ac.description = coalesce(
                    ac.description,
                    CASE
                        WHEN ac.method IS NOT NULL AND ac.path IS NOT NULL THEN ac.method + ' ' + ac.path + ' contract'
                        ELSE 'Generated API contract'
                    END
                )
                WITH ac
                FOREACH (_ IN CASE WHEN EXISTS { (ac)-[:HAS_REQUEST]->(:RequestSchema) } THEN [] ELSE [1] END |
                    MERGE (req:RequestSchema {schemaId: 'REQ-' + ac.contractId + '-AUTO'})
                    SET req.contentType = 'application/json',
                        req.status = 'DEFINED'
                    MERGE (ac)-[:HAS_REQUEST]->(req)
                )
                FOREACH (_ IN CASE WHEN EXISTS { (ac)-[:HAS_RESPONSE]->(:ResponseSchema) } THEN [] ELSE [1] END |
                    MERGE (res:ResponseSchema {schemaId: 'RES-' + ac.contractId + '-200-AUTO'})
                    SET res.contentType = 'application/json',
                        res.statusCode = 200,
                        res.status = 'DEFINED'
                    MERGE (ac)-[:HAS_RESPONSE]->(res)
                )
                FOREACH (_ IN CASE WHEN EXISTS { (ac)-[:HAS_ERROR]->(:ErrorContract) } THEN [] ELSE [1] END |
                    MERGE (err:ErrorContract {errorContractId: 'EC-' + ac.contractId + '-500-AUTO'})
                    SET err.httpStatus = 500,
                        err.errorCode = ac.contractId + '-UNSPECIFIED',
                        err.description = 'Default generated error contract for ' + coalesce(ac.method, 'UNKNOWN') + ' ' + coalesce(ac.path, ''),
                        err.status = 'DEFINED'
                    MERGE (ac)-[:HAS_ERROR]->(err)
                )
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
                UNWIND [
                  {
                    storyId: 'US-AUTH-001',
                    label: 'User can sign in',
                    module: 'core',
                    domain: 'auth',
                    ruleId: 'RULE-AUTH-001',
                    ruleName: 'Password policy',
                    ruleDescription: 'Password must meet security policy',
                    ruleType: 'SECURITY'
                  },
                  {
                    storyId: 'US-AI-090',
                    label: 'Builder canvas interactions ready for agent composition',
                    module: 'ai',
                    domain: 'agents',
                    ruleId: 'RULE-AGT-BUILDER-001',
                    ruleName: 'Builder workflow guardrails',
                    ruleDescription: 'Builder save, test, and publish actions require a valid draft context and the appropriate permission checks.',
                    ruleType: 'WORKFLOW'
                  }
                ] AS row
                MERGE (us:UserStory {storyId: row.storyId})
                ON CREATE SET us.label = row.label, us.module = row.module, us.domain = row.domain, us.storyNumber = row.storyId
                MERGE (r:Rule {ruleId: row.ruleId})
                ON CREATE SET r.name = row.ruleName, r.description = row.ruleDescription, r.ruleType = row.ruleType, r.status = 'DEFINED'
                MERGE (us)-[:GOVERNED_BY_RULE]->(r)
                """).run();
    }

    @Transactional
    public void seedStoryReadinessCoverage() {
        neo4jClient.query("""
                MATCH (us:UserStory {storyId: 'US-AUTH-001'})
                MATCH (screen:Screen {surfaceId: 'SCR-AUTH'})
                MATCH (api:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (tc:TestCase {testCaseId: 'TC-AUTH-001'})
                MERGE (submit:Interaction {interactionId: 'INT-AUTH-LOGIN-001'})
                ON CREATE SET submit.surfaceId = 'SCR-AUTH',
                              submit.element = 'Sign In Submit',
                              submit.trigger = 'click'
                SET submit.permission = coalesce(submit.permission, 'USER'),
                    submit.outcomeSuccess = coalesce(submit.outcomeSuccess, 'Authenticated user reaches the role-based landing page.'),
                    submit.outcomeError = coalesce(submit.outcomeError, 'Authentication failed.'),
                    submit.outcomeLoading = coalesce(submit.outcomeLoading, 'Signing in…')
                MERGE (us)-[:DELIVERS]->(screen)
                MERGE (us)-[:VERIFIED_BY]->(tc)
                MERGE (screen)-[:HAS_INTERACTION]->(submit)
                MERGE (submit)-[:CALLS_API]->(api)
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
                MERGE (proc:BusinessProcess {processId: 'PROC-SCREEN-REVIEW'})
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
                MERGE (proc)-[:HAS_FLOW_NODE]->(act2)
                MERGE (proc)-[:HAS_FLOW_NODE]->(act3)
                MERGE (act1)-[:FLOWS_TO]->(act2)
                MERGE (gw)-[:FLOWS_TO]->(act3)
                MERGE (evt)-[:FLOWS_TO]->(act1)
                """).run();
    }

    @Transactional
    public void seedProcessExpansion() {
        neo4jClient.query("""
                MERGE (proc:BusinessProcess {processId: 'PROC-SCREEN-REVIEW'})
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
                MERGE (proc)-[:HAS_FLOW_NODE]->(subAct)
                MERGE (proc)-[:HAS_FLOW_NODE]->(callAct)
                MERGE (subAct)-[:EXPANDS_TO]->(subProc)
                MERGE (callAct)-[:CALLS_PROCESS]->(callProc)
                """).run();
    }

    @Transactional
    public void seedBoundaryEvent() {
        neo4jClient.query("""
                MERGE (proc:BusinessProcess {processId: 'PROC-SCREEN-REVIEW'})
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
                MERGE (proc)-[:HAS_FLOW_NODE]->(evt)
                MERGE (proc)-[:HAS_FLOW_NODE]->(escalate)
                MERGE (evt)-[:ATTACHED_TO]->(host)
                MERGE (evt)-[:FLOWS_TO]->(escalate)
                """).run();
    }

    @Transactional
    public void patchBusinessProcessCoverageDefaults() {
        neo4jClient.query("""
                UNWIND [
                  {id: 'PROC-SCREEN-REVIEW', path: 'documentation/processes/screen-review.bpmn'},
                  {id: 'PROC-SCREEN-DETAIL-REVIEW', path: 'documentation/processes/screen-detail-review.bpmn'},
                  {id: 'PROC-NOTIFY-REVIEWERS', path: 'documentation/processes/notify-reviewers.bpmn'},
                  {id: 'PROC-ACCESS-SIGN-IN', path: 'documentation/processes/access-sign-in.bpmn'}
                ] AS row
                MATCH (proc:BusinessProcess {processId: row.id})
                SET proc.diagramFormat = coalesce(proc.diagramFormat, 'BPMN_XML'),
                    proc.diagramVersion = coalesce(proc.diagramVersion, '1.0'),
                    proc.diagramSource = coalesce(proc.diagramSource, 'OMG_BPMN'),
                    proc.diagramPath = row.path,
                    proc.status = coalesce(proc.status, 'DEFINED')
                """).run();

        neo4jClient.query("""
                MERGE (detailProc:BusinessProcess {processId: 'PROC-SCREEN-DETAIL-REVIEW'})
                MERGE (detailAct:ProcessActivity {activityId: 'ACT-PROC-SCREEN-DETAIL-REVIEW-001'})
                SET detailAct.name = 'Review interaction details',
                    detailAct.description = 'Inspect component wiring and implementation evidence for the selected screen.',
                    detailAct.activityType = 'TASK',
                    detailAct.actionType = 'REVIEW',
                    detailAct.taskNature = 'USER',
                    detailAct.orderIndex = 1,
                    detailAct.status = 'DEFINED'
                MERGE (detailProc)-[:HAS_FLOW_NODE]->(detailAct)
                WITH detailProc
                MERGE (detailEvt:ProcessEvent {eventId: 'EVT-PROC-SCREEN-DETAIL-REVIEW-001'})
                SET detailEvt.name = 'Detailed review started',
                    detailEvt.eventPosition = 'START',
                    detailEvt.eventTrigger = 'NONE',
                    detailEvt.isInterrupting = false,
                    detailEvt.status = 'DEFINED'
                MERGE (detailProc)-[:HAS_FLOW_NODE]->(detailEvt)
                MERGE (detailEvt)-[:FLOWS_TO]->(detailAct)
                """).run();

        neo4jClient.query("""
                MERGE (notifyProc:BusinessProcess {processId: 'PROC-NOTIFY-REVIEWERS'})
                MERGE (notifyAct:ProcessActivity {activityId: 'ACT-PROC-NOTIFY-REVIEWERS-001'})
                SET notifyAct.name = 'Dispatch reviewer notification',
                    notifyAct.description = 'Send review status updates to the assigned reviewers.',
                    notifyAct.activityType = 'TASK',
                    notifyAct.actionType = 'NOTIFY',
                    notifyAct.taskNature = 'SERVICE',
                    notifyAct.orderIndex = 1,
                    notifyAct.status = 'DEFINED'
                MERGE (notifyProc)-[:HAS_FLOW_NODE]->(notifyAct)
                WITH notifyProc
                MERGE (notifyEvt:ProcessEvent {eventId: 'EVT-PROC-NOTIFY-REVIEWERS-001'})
                SET notifyEvt.name = 'Notification requested',
                    notifyEvt.eventPosition = 'START',
                    notifyEvt.eventTrigger = 'MESSAGE',
                    notifyEvt.isInterrupting = false,
                    notifyEvt.status = 'DEFINED'
                MERGE (notifyProc)-[:HAS_FLOW_NODE]->(notifyEvt)
                MERGE (notifyEvt)-[:FLOWS_TO]->(notifyAct)
                """).run();

        neo4jClient.query("""
                MERGE (authProc:BusinessProcess {processId: 'PROC-ACCESS-SIGN-IN'})
                MERGE (authAct:ProcessActivity {activityId: 'ACT-PROC-ACCESS-SIGN-IN-001'})
                SET authAct.name = 'Validate credentials',
                    authAct.description = 'Validate submitted credentials and establish an authenticated session.',
                    authAct.activityType = 'TASK',
                    authAct.actionType = 'VALIDATE',
                    authAct.taskNature = 'SERVICE',
                    authAct.orderIndex = 1,
                    authAct.status = 'DEFINED'
                MERGE (authProc)-[:HAS_FLOW_NODE]->(authAct)
                WITH authProc
                MERGE (authEvt:ProcessEvent {eventId: 'EVT-PROC-ACCESS-SIGN-IN-001'})
                SET authEvt.name = 'Sign-in requested',
                    authEvt.eventPosition = 'START',
                    authEvt.eventTrigger = 'MESSAGE',
                    authEvt.isInterrupting = false,
                    authEvt.status = 'DEFINED'
                MERGE (authProc)-[:HAS_FLOW_NODE]->(authEvt)
                MERGE (authEvt)-[:FLOWS_TO]->(authAct)
                """).run();
    }

    @Transactional
    public void deduplicateProcessActivities() {
        neo4jClient.query("""
                MATCH (activity:ProcessActivity)
                WITH activity.activityId AS activityId, collect(activity) AS activities
                WHERE activityId IS NOT NULL AND size(activities) > 1
                WITH head(activities) AS canonical, tail(activities) AS duplicates
                FOREACH (duplicate IN duplicates |
                    SET canonical.name = coalesce(canonical.name, duplicate.name),
                        canonical.description = coalesce(canonical.description, duplicate.description),
                        canonical.activityType = coalesce(canonical.activityType, duplicate.activityType),
                        canonical.actionType = coalesce(canonical.actionType, duplicate.actionType),
                        canonical.taskNature = coalesce(canonical.taskNature, duplicate.taskNature),
                        canonical.orderIndex = CASE
                            WHEN canonical.orderIndex IS NULL OR canonical.orderIndex = 0 THEN duplicate.orderIndex
                            ELSE canonical.orderIndex
                        END,
                        canonical.trigger = coalesce(canonical.trigger, duplicate.trigger),
                        canonical.preCondition = coalesce(canonical.preCondition, duplicate.preCondition),
                        canonical.postCondition = coalesce(canonical.postCondition, duplicate.postCondition),
                        canonical.status = coalesce(canonical.status, duplicate.status)
                )
                WITH canonical, duplicates
                UNWIND duplicates AS duplicate
                OPTIONAL MATCH (process:BusinessProcess)-[:HAS_FLOW_NODE]->(duplicate)
                FOREACH (_ IN CASE WHEN process IS NULL THEN [] ELSE [1] END |
                    MERGE (process)-[:HAS_FLOW_NODE]->(canonical)
                )
                WITH canonical, duplicate
                OPTIONAL MATCH (sourceActivity:ProcessActivity)-[:FLOWS_TO]->(duplicate)
                FOREACH (_ IN CASE WHEN sourceActivity IS NULL THEN [] ELSE [1] END |
                    MERGE (sourceActivity)-[:FLOWS_TO]->(canonical)
                )
                WITH canonical, duplicate
                OPTIONAL MATCH (sourceGateway:ProcessGateway)-[:FLOWS_TO]->(duplicate)
                FOREACH (_ IN CASE WHEN sourceGateway IS NULL THEN [] ELSE [1] END |
                    MERGE (sourceGateway)-[:FLOWS_TO]->(canonical)
                )
                WITH canonical, duplicate
                OPTIONAL MATCH (sourceEvent:ProcessEvent)-[:FLOWS_TO]->(duplicate)
                FOREACH (_ IN CASE WHEN sourceEvent IS NULL THEN [] ELSE [1] END |
                    MERGE (sourceEvent)-[:FLOWS_TO]->(canonical)
                )
                WITH canonical, duplicate
                OPTIONAL MATCH (sourceEvent:ProcessEvent)-[:ATTACHED_TO]->(duplicate)
                FOREACH (_ IN CASE WHEN sourceEvent IS NULL THEN [] ELSE [1] END |
                    MERGE (sourceEvent)-[:ATTACHED_TO]->(canonical)
                )
                WITH canonical, duplicate
                OPTIONAL MATCH (duplicate)-[:FLOWS_TO]->(target:ProcessActivity)
                FOREACH (_ IN CASE WHEN target IS NULL THEN [] ELSE [1] END |
                    MERGE (canonical)-[:FLOWS_TO]->(target)
                )
                WITH canonical, duplicate
                OPTIONAL MATCH (duplicate)-[:EXPANDS_TO]->(expanded:BusinessProcess)
                FOREACH (_ IN CASE WHEN expanded IS NULL THEN [] ELSE [1] END |
                    MERGE (canonical)-[:EXPANDS_TO]->(expanded)
                )
                WITH canonical, duplicate
                OPTIONAL MATCH (duplicate)-[:CALLS_PROCESS]->(called:BusinessProcess)
                FOREACH (_ IN CASE WHEN called IS NULL THEN [] ELSE [1] END |
                    MERGE (canonical)-[:CALLS_PROCESS]->(called)
                )
                DETACH DELETE duplicate
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
                  {id: 'SRC-SCREEN-CATALOG-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'screen catalog', line: '60-2081', url: null},
                  {id: 'SRC-TOUCHPOINT-CATALOG-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'seedTouchpoints', line: '2096-2199', url: null},
                  {id: 'SRC-INTERACTION-CATALOG-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'seedInteractions', line: '2201-2464', url: null},
                  {id: 'SRC-JOURNEY-CATALOG-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'seedJourneys', line: '2466-2562', url: null},
                  {id: 'SRC-API-CONTRACT-CATALOG-001', path: 'backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java', section: 'upsertApiContractsFromInteractions', line: '131-154', url: null},
                  {id: 'SRC-US-AUTH-001', path: 'Documentation/.Requirements/CONSOLIDATED-STORY-INVENTORY.md', section: 'Authentication', line: '120-138', url: null},
                  {id: 'SRC-SCR-AUTH-001', path: 'documentation/vision-benchmark.md', section: 'Query 8', line: '401-410', url: null},
                  {id: 'SRC-BUG-001', path: 'Documentation/governance/ai-discussions/discussion-20260301-155248.md', section: 'Login refresh issue', line: '44-61', url: null},
                  {id: 'SRC-US-AI-090-001', path: 'frontend/tests/graph/screen-selection.spec.ts', section: 'builder screen selection smoke', line: '1-16', url: null},
                  {id: 'SRC-SCR-AGT-BUILDER-001', path: 'frontend/tests/graph/screen-selection.spec.ts', section: 'builder screen selection smoke', line: '1-16', url: null},
                  {id: 'SRC-JRN-R05-001-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'seedJourneys', line: '2468-2484', url: null},
                  {id: 'SRC-TP-AGT-FLOW-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'seedTouchpoints', line: '2098-2130', url: null},
                  {id: 'SRC-INT-R05-BUILDER-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'seedInteractions', line: '2309-2359', url: null},
                  {id: 'SRC-API-AGT-BUILDER-001', path: 'backend/src/main/java/com/emsist/designhub/config/DataInitializer.java', section: 'seedInteractions', line: '2309-2359', url: null},
                  {id: 'SRC-API-AUTH-LOGIN-001', path: 'backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java', section: 'seedApiSchemas', line: '449-458', url: null},
                  {id: 'SRC-DE-AGENT-001', path: 'backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java', section: 'seedDataFields', line: '403-410', url: null}
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
    public void seedCatalogSourceCoverage() {
        neo4jClient.query("""
                MATCH (screenSrc:SourceReference {sourceId: 'SRC-SCREEN-CATALOG-001'})
                MATCH (journeySrc:SourceReference {sourceId: 'SRC-JOURNEY-CATALOG-001'})
                MATCH (touchpointSrc:SourceReference {sourceId: 'SRC-TOUCHPOINT-CATALOG-001'})
                MATCH (interactionSrc:SourceReference {sourceId: 'SRC-INTERACTION-CATALOG-001'})
                MATCH (apiSrc:SourceReference {sourceId: 'SRC-API-CONTRACT-CATALOG-001'})
                CALL (screenSrc) {
                    MATCH (screen:Screen)
                    MERGE (screen)-[:HAS_SOURCE]->(screenSrc)
                    RETURN count(screen) AS ignoredScreenCount
                }
                CALL (journeySrc) {
                    MATCH (journey:Journey)
                    MERGE (journey)-[:HAS_SOURCE]->(journeySrc)
                    RETURN count(journey) AS ignoredJourneyCount
                }
                CALL (touchpointSrc) {
                    MATCH (touchpoint:Touchpoint)
                    MERGE (touchpoint)-[:HAS_SOURCE]->(touchpointSrc)
                    RETURN count(touchpoint) AS ignoredTouchpointCount
                }
                CALL (interactionSrc) {
                    MATCH (interaction:Interaction)
                    MERGE (interaction)-[:HAS_SOURCE]->(interactionSrc)
                    RETURN count(interaction) AS ignoredInteractionCount
                }
                CALL (apiSrc) {
                    MATCH (api:ApiContract)
                    MERGE (api)-[:HAS_SOURCE]->(apiSrc)
                    RETURN count(api) AS ignoredApiCount
                }
                RETURN 1
                """).run();
    }

    @Transactional
    public void seedExternalArtifacts() {
        neo4jClient.query("""
                UNWIND [
                  {
                    id: 'EXT-JIRA-EPIC-001',
                    system: 'JIRA',
                    externalType: 'EPIC',
                    key: 'DH-100',
                    title: 'Access and authentication hardening',
                    scope: 'Design Hub',
                    workflow: 'In Progress',
                    priority: 'Highest',
                    owner: 'Priya Patel',
                    reporter: 'Marco Lane',
                    labels: ['design-hub', 'identity', 'jira'],
                    customFields: ['area=Identity', 'releaseTrain=Q1-Platform'],
                    url: 'https://jira.example.com/browse/DH-100',
                    sync: 'SYNCED',
                    syncedAt: '2026-03-18T07:45:00Z'
                  },
                  {
                    id: 'EXT-JIRA-001',
                    system: 'JIRA',
                    externalType: 'STORY',
                    key: 'DH-101',
                    title: 'User sign-in and session recovery',
                    scope: 'Design Hub / Identity',
                    workflow: 'In Progress',
                    priority: 'High',
                    owner: 'Aisha Coleman',
                    reporter: 'Marco Lane',
                    labels: ['design-hub', 'story', 'auth'],
                    customFields: ['area=Identity', 'iteration=Sprint 24', 'storyPoints=5'],
                    url: 'https://jira.example.com/browse/DH-101',
                    sync: 'SYNCED',
                    syncedAt: '2026-03-18T08:00:00Z'
                  },
                  {
                    id: 'EXT-JIRA-TASK-001',
                    system: 'JIRA',
                    externalType: 'TASK',
                    key: 'DH-102',
                    title: 'Implement sign-in telemetry and retry task',
                    scope: 'Design Hub / Identity',
                    workflow: 'Selected for Development',
                    priority: 'Medium',
                    owner: 'Jordan Rivera',
                    reporter: 'Aisha Coleman',
                    labels: ['design-hub', 'task', 'telemetry'],
                    customFields: ['area=Identity', 'iteration=Sprint 24', 'discipline=Telemetry'],
                    url: 'https://jira.example.com/browse/DH-102',
                    sync: 'SYNCED',
                    syncedAt: '2026-03-18T08:03:00Z'
                  },
                  {
                    id: 'EXT-AZDO-FEATURE-001',
                    system: 'AZURE_DEVOPS',
                    externalType: 'FEATURE',
                    key: 'AB#240',
                    title: 'Authentication flow stabilization',
                    scope: 'Design Hub\\\\Identity',
                    workflow: 'Active',
                    priority: '1',
                    owner: 'Aisha Coleman',
                    reporter: 'Priya Patel',
                    labels: ['design-hub', 'feature', 'azure-devops'],
                    customFields: ['area=Identity', 'featureFlag=auth-flow-v2'],
                    url: 'https://dev.azure.com/example/designhub/_workitems/edit/240',
                    sync: 'STALE',
                    syncedAt: '2026-03-17T18:20:00Z'
                  },
                  {
                    id: 'EXT-AZDO-001',
                    system: 'AZURE_DEVOPS',
                    externalType: 'BUG',
                    key: 'AB#245',
                    title: 'Retry banner remains visible after successful login',
                    scope: 'Design Hub\\\\Identity',
                    workflow: 'Active',
                    priority: '2',
                    owner: 'Jordan Rivera',
                    reporter: 'Aisha Coleman',
                    labels: ['design-hub', 'bug', 'login'],
                    customFields: ['area=Identity', 'iteration=Sprint 24', 'incident=INC-245'],
                    url: 'https://dev.azure.com/example/designhub/_workitems/edit/245',
                    sync: 'SYNCED',
                    syncedAt: '2026-03-18T08:05:00Z'
                  }
                ] AS ext
                MERGE (ea:ExternalArtifact {externalId: ext.id})
                SET ea.system = ext.system,
                    ea.externalType = ext.externalType,
                    ea.key = ext.key,
                    ea.title = ext.title,
                    ea.projectScope = ext.scope,
                    ea.workflowState = ext.workflow,
                    ea.priority = ext.priority,
                    ea.owner = ext.owner,
                    ea.reporter = ext.reporter,
                    ea.labels = ext.labels,
                    ea.customFields = coalesce(ext.customFields, []),
                    ea.url = ext.url,
                    ea.syncStatus = ext.sync,
                    ea.lastSyncedAt = datetime(ext.syncedAt),
                    ea.status = 'DEFINED'
                """).run();
    }

    @Transactional
    public void seedFindings() {
        neo4jClient.query("""
                MERGE (finding:Finding {findingId: 'FND-001'})
                SET finding.summary = 'Builder publish flow still needs explicit review guidance',
                    finding.externalWorkflowState = 'OPEN',
                    finding.externalPriority = 'HIGH',
                    finding.externalOwner = 'Aisha Coleman',
                    finding.externalRefs = ['UX-REVIEW-24', 'DESIGN-HUB-REVIEW'],
                    finding.findingType = 'OBSERVATION',
                    finding.severity = 'MEDIUM',
                    finding.status = 'DEFINED'
                WITH finding
                MATCH (screen:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MERGE (finding)-[:AFFECTS_SCREEN]->(screen)
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
                WITH story, screen, bug
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

    @Transactional
    public void seedExternalArtifactAlignment() {
        neo4jClient.query("""
                MATCH (jiraEpic:ExternalArtifact {externalId: 'EXT-JIRA-EPIC-001'})
                MATCH (jiraStory:ExternalArtifact {externalId: 'EXT-JIRA-001'})
                MATCH (jiraTask:ExternalArtifact {externalId: 'EXT-JIRA-TASK-001'})
                MATCH (azdoFeature:ExternalArtifact {externalId: 'EXT-AZDO-FEATURE-001'})
                MATCH (azdoBug:ExternalArtifact {externalId: 'EXT-AZDO-001'})
                MATCH (jiraFinding:ExternalArtifact {externalId: 'EXT-JIRA-FINDING-001'})
                MATCH (epicAuth:Epic {epicId: 'EPIC-AUTH-001'})
                MATCH (featureAuth:Feature {featureId: 'FEAT-AUTH'})
                MATCH (taskAuth:Task {taskId: 'TASK-US-AUTH-001-001'})
                MERGE (jiraEpic)-[:REPRESENTS_EPIC]->(epicAuth)
                MERGE (jiraEpic)-[:REPRESENTS_FEATURE]->(featureAuth)
                MERGE (azdoFeature)-[:REPRESENTS_FEATURE]->(featureAuth)
                MERGE (jiraTask)-[:REPRESENTS_TASK]->(taskAuth)
                MERGE (jiraEpic)-[:PARENT_OF]->(jiraStory)
                MERGE (jiraStory)-[:CHILD_OF]->(jiraEpic)
                MERGE (jiraStory)-[:PARENT_OF]->(jiraTask)
                MERGE (jiraTask)-[:CHILD_OF]->(jiraStory)
                MERGE (azdoFeature)-[:PARENT_OF]->(azdoBug)
                MERGE (azdoBug)-[:CHILD_OF]->(azdoFeature)
                MERGE (jiraEpic)-[:PARENT_OF]->(jiraFinding)
                MERGE (jiraFinding)-[:CHILD_OF]->(jiraEpic)
                MERGE (jiraStory)-[:DEPENDS_ON]->(azdoBug)
                MERGE (jiraStory)-[:RELATES_TO]->(azdoFeature)
                MERGE (jiraEpic)-[:DUPLICATES]->(azdoFeature)
                MERGE (jiraTask)-[:DEPENDS_ON]->(jiraStory)
                MERGE (azdoFeature)-[:RELATES_TO]->(jiraEpic)
                MERGE (azdoBug)-[:RELATES_TO]->(jiraStory)
                MERGE (jiraFinding)-[:RELATES_TO]->(jiraStory)
                """).run();
    }

    @Transactional
    public void seedExternalFieldNormalization() {
        externalArtifactSyncService.normalizeRepresentedPrimaryNodes();
    }

    @Transactional
    public void seedImplementationSourceCoverage() {
        neo4jClient.query("""
                MATCH (aiStory:UserStory {storyId: 'US-AI-090'})
                MATCH (builderScreen:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MATCH (builderJourney:Journey {journeyId: 'JRN-R05-001'})
                MATCH (dockTouchpoint:Touchpoint {touchpointId: 'TP-AGT-DOCK'})
                MATCH (galleryTouchpoint:Touchpoint {touchpointId: 'TP-GALLERY-MENU'})
                MATCH (dragDrop:Interaction {interactionId: 'INT-R05-BUILDER-001'})
                MATCH (saveDraft:Interaction {interactionId: 'INT-R05-BUILDER-002'})
                MATCH (playground:Interaction {interactionId: 'INT-R05-BUILDER-003'})
                MATCH (publish:Interaction {interactionId: 'INT-R05-BUILDER-004'})
                MATCH (publishApi:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MATCH (draftApi:ApiContract {contractId: 'API-PUT-API-V1-AGENTS-ID-DRAFT'})
                MATCH (testApi:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-TEST-SESSION'})
                MATCH (authApi:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (agentEntity:DataEntity {entityId: 'DE-AGENT'})
                MATCH (storySrc:SourceReference {sourceId: 'SRC-US-AI-090-001'})
                MATCH (screenSrc:SourceReference {sourceId: 'SRC-SCR-AGT-BUILDER-001'})
                MATCH (journeySrc:SourceReference {sourceId: 'SRC-JRN-R05-001-001'})
                MATCH (touchpointSrc:SourceReference {sourceId: 'SRC-TP-AGT-FLOW-001'})
                MATCH (interactionSrc:SourceReference {sourceId: 'SRC-INT-R05-BUILDER-001'})
                MATCH (apiSrc:SourceReference {sourceId: 'SRC-API-AGT-BUILDER-001'})
                MATCH (authApiSrc:SourceReference {sourceId: 'SRC-API-AUTH-LOGIN-001'})
                MATCH (entitySrc:SourceReference {sourceId: 'SRC-DE-AGENT-001'})
                MERGE (aiStory)-[:HAS_SOURCE]->(storySrc)
                MERGE (builderScreen)-[:HAS_SOURCE]->(screenSrc)
                MERGE (builderJourney)-[:HAS_SOURCE]->(journeySrc)
                MERGE (dockTouchpoint)-[:HAS_SOURCE]->(touchpointSrc)
                MERGE (galleryTouchpoint)-[:HAS_SOURCE]->(touchpointSrc)
                MERGE (dragDrop)-[:HAS_SOURCE]->(interactionSrc)
                MERGE (saveDraft)-[:HAS_SOURCE]->(interactionSrc)
                MERGE (playground)-[:HAS_SOURCE]->(interactionSrc)
                MERGE (publish)-[:HAS_SOURCE]->(interactionSrc)
                MERGE (publishApi)-[:HAS_SOURCE]->(apiSrc)
                MERGE (draftApi)-[:HAS_SOURCE]->(apiSrc)
                MERGE (testApi)-[:HAS_SOURCE]->(apiSrc)
                MERGE (authApi)-[:HAS_SOURCE]->(authApiSrc)
                MERGE (agentEntity)-[:HAS_SOURCE]->(entitySrc)
                """).run();
    }

    @Transactional
    public void seedUpperTraceabilitySpine() {
        neo4jClient.query("""
                MERGE (portfolio:RequirementPortfolio {portfolioId: 'PORT-DH-001'})
                SET portfolio.name = 'Design Hub Delivery Portfolio',
                    portfolio.description = 'Seeded upper traceability spine for the Design Hub implementation stories.',
                    portfolio.status = 'IN_IMPLEMENTATION'
                WITH portfolio
                UNWIND [
                  {
                    objectiveId: 'OBJ-DH-AUTH-001',
                    objectiveTitle: 'Enable secure authenticated access to Design Hub',
                    objectiveStatus: 'APPROVED',
                    epicId: 'EPIC-AUTH-001',
                    epicTitle: 'Authentication and access',
                    epicDescription: 'Covers sign-in, access control, and entry-path experience for Design Hub.',
                    epicStatus: 'APPROVED',
                    featureId: 'FEAT-AUTH',
                    featureTitle: 'Authentication',
                    featureDescription: 'Provides the core sign-in experience and supporting traceability for authentication flows.',
                    featureStatus: 'APPROVED',
                    storyId: 'US-AUTH-001',
                    storyLabel: 'User can sign in',
                    module: 'core',
                    domain: 'auth',
                    storyStatus: 'APPROVED'
                  },
                  {
                    objectiveId: 'OBJ-DH-AI-001',
                    objectiveTitle: 'Enable agent designers to compose and publish AI agents',
                    objectiveStatus: 'IN_IMPLEMENTATION',
                    epicId: 'EPIC-AI-001',
                    epicTitle: 'Agent builder and orchestration',
                    epicDescription: 'Covers the builder canvas, orchestration controls, and implementation context for agent design.',
                    epicStatus: 'IN_IMPLEMENTATION',
                    featureId: 'FEAT-AI',
                    featureTitle: 'Agent Builder',
                    featureDescription: 'Provides the builder canvas and related delivery context for agent composition.',
                    featureStatus: 'IN_IMPLEMENTATION',
                    storyId: 'US-AI-090',
                    storyLabel: 'Builder canvas interactions ready for agent composition',
                    module: 'ai',
                    domain: 'agents',
                    storyStatus: 'DEFINED'
                  },
                  {
                    objectiveId: 'OBJ-DH-AI-001',
                    objectiveTitle: 'Enable agent designers to compose and publish AI agents',
                    objectiveStatus: 'IN_IMPLEMENTATION',
                    epicId: 'EPIC-AI-001',
                    epicTitle: 'Agent builder and orchestration',
                    epicDescription: 'Covers the builder canvas, orchestration controls, and implementation context for agent design.',
                    epicStatus: 'IN_IMPLEMENTATION',
                    featureId: 'FEAT-AI-LIST',
                    featureTitle: 'Agent List',
                    featureDescription: 'Provides the agent list, detail handoff, and automation context for browsing seeded agents.',
                    featureStatus: 'IN_IMPLEMENTATION',
                    storyId: 'US-AI-137',
                    storyLabel: 'Agent list browsing resolves a complete automation handoff',
                    module: 'ai',
                    domain: 'agents',
                    storyStatus: 'DEFINED'
                  },
                  {
                    objectiveId: 'OBJ-DH-AI-001',
                    objectiveTitle: 'Enable agent designers to compose and publish AI agents',
                    objectiveStatus: 'IN_IMPLEMENTATION',
                    epicId: 'EPIC-AI-001',
                    epicTitle: 'Agent builder and orchestration',
                    epicDescription: 'Covers the builder canvas, orchestration controls, and implementation context for agent design.',
                    epicStatus: 'IN_IMPLEMENTATION',
                    featureId: 'FEAT-AI-GALLERY',
                    featureTitle: 'Template Gallery',
                    featureDescription: 'Provides the template gallery, preview, and fork handoff context for browsing seeded templates.',
                    featureStatus: 'IN_IMPLEMENTATION',
                    storyId: 'US-AI-078',
                    storyLabel: 'Template gallery browsing resolves a complete automation handoff',
                    module: 'ai',
                    domain: 'agents',
                    storyStatus: 'DEFINED'
                  },
                  {
                    objectiveId: 'OBJ-DH-AI-001',
                    objectiveTitle: 'Enable agent designers to compose and publish AI agents',
                    objectiveStatus: 'IN_IMPLEMENTATION',
                    epicId: 'EPIC-AI-001',
                    epicTitle: 'Agent builder and orchestration',
                    epicDescription: 'Covers the builder canvas, orchestration controls, and implementation context for agent design.',
                    epicStatus: 'IN_IMPLEMENTATION',
                    featureId: 'FEAT-AI-DETAIL',
                    featureTitle: 'Agent Detail',
                    featureDescription: 'Provides the agent factsheet and tabbed detail context for seeded agent delivery flows.',
                    featureStatus: 'IN_IMPLEMENTATION',
                    storyId: 'US-AI-139',
                    storyLabel: 'Agent detail tabs resolve a complete automation handoff',
                    module: 'ai',
                    domain: 'agents',
                    storyStatus: 'DEFINED'
                  }
                ] AS row
                MERGE (objective:BusinessObjective {objectiveId: row.objectiveId})
                SET objective.title = row.objectiveTitle,
                    objective.status = row.objectiveStatus
                MERGE (epic:Epic {epicId: row.epicId})
                SET epic.title = row.epicTitle,
                    epic.description = row.epicDescription,
                    epic.status = row.epicStatus
                MERGE (feature:Feature {featureId: row.featureId})
                SET feature.title = row.featureTitle,
                    feature.description = row.featureDescription,
                    feature.status = row.featureStatus
                MERGE (story:UserStory {storyId: row.storyId})
                ON CREATE SET story.label = row.storyLabel,
                              story.module = row.module,
                              story.domain = row.domain,
                              story.storyNumber = row.storyId
                SET story.label = coalesce(story.label, row.storyLabel),
                    story.module = coalesce(story.module, row.module),
                    story.domain = coalesce(story.domain, row.domain),
                    story.storyNumber = coalesce(story.storyNumber, row.storyId),
                    story.status = coalesce(story.status, row.storyStatus)
                MERGE (objective)-[:HAS_FEATURE]->(feature)
                MERGE (portfolio)-[:HAS_EPIC]->(epic)
                MERGE (epic)-[:HAS_FEATURE]->(feature)
                MERGE (feature)-[:HAS_STORY]->(story)
                """).run();
    }

    @Transactional
    public void seedBusinessArchitectureAlignment() {
        neo4jClient.query("""
                MERGE (designDom:BusinessDomain {domainCode: 'DOM-DESIGN'})
                ON CREATE SET designDom.name = 'Design Management',
                              designDom.description = 'Business domain for design operations',
                              designDom.activeStatus = 'ACTIVE'
                MERGE (securityDom:BusinessDomain {domainCode: 'DOM-SECURITY'})
                SET securityDom.name = 'Identity and Access',
                    securityDom.description = 'Business domain for authentication and access management',
                    securityDom.activeStatus = 'ACTIVE'
                MERGE (screenCap:BusinessCapability {capabilityId: 'CAP-SCREEN-MGMT'})
                ON CREATE SET screenCap.name = 'Screen Management',
                              screenCap.description = 'Manage screen inventory and review workflow',
                              screenCap.status = 'DEFINED'
                MERGE (accessCap:BusinessCapability {capabilityId: 'CAP-ACCESS-MGMT'})
                SET accessCap.name = 'Access Management',
                    accessCap.description = 'Manage authenticated access and permission-controlled entry flows.',
                    accessCap.status = 'APPROVED'
                MERGE (designDom)-[:HAS_CAPABILITY]->(screenCap)
                MERGE (securityDom)-[:HAS_CAPABILITY]->(accessCap)
                WITH screenCap, accessCap
                MERGE (authProc:BusinessProcess {processId: 'PROC-ACCESS-SIGN-IN'})
                SET authProc.name = 'Authenticated access process',
                    authProc.description = 'Manage credential validation, session creation, and secure entry routing.',
                    authProc.status = 'APPROVED'
                MERGE (accessCap)-[:REALIZED_BY_PROCESS]->(authProc)
                WITH screenCap, accessCap
                MATCH (app:Application {applicationId: 'APP-DH'})
                MATCH (identityApp:Application {applicationId: 'APP-IDP'})
                MATCH (featureAi:Feature {featureId: 'FEAT-AI'})
                MATCH (featureAuth:Feature {featureId: 'FEAT-AUTH'})
                MERGE (screenCap)-[:ENABLED_BY]->(app)
                MERGE (accessCap)-[:ENABLED_BY]->(app)
                MERGE (accessCap)-[:ENABLED_BY]->(identityApp)
                MERGE (featureAi)-[:REALIZES]->(screenCap)
                MERGE (featureAuth)-[:REALIZES]->(accessCap)
                MERGE (app)-[:REALIZES]->(featureAi)
                MERGE (identityApp)-[:REALIZES]->(featureAuth)
                WITH app, identityApp
                MERGE (orgPlatform:Organization {orgId: 'ORG-DH-PLATFORM'})
                SET orgPlatform.name = 'Design Hub Platform Team',
                    orgPlatform.organizationType = 'TEAM',
                    orgPlatform.status = 'IMPLEMENTED'
                MERGE (orgProduct:Organization {orgId: 'ORG-DH-PRODUCT'})
                SET orgProduct.name = 'Design Hub Product Design',
                    orgProduct.organizationType = 'COE',
                    orgProduct.status = 'APPROVED'
                MERGE (orgIdentity:Organization {orgId: 'ORG-IDENTITY-PLATFORM'})
                SET orgIdentity.name = 'Identity Platform Team',
                    orgIdentity.organizationType = 'TEAM',
                    orgIdentity.status = 'IMPLEMENTED'
                MERGE (orgPlatform)-[:OWNS]->(app)
                MERGE (orgProduct)-[:OWNS]->(app)
                MERGE (orgIdentity)-[:OWNS]->(identityApp)
                """).run();
    }

    @Transactional
    public void seedProjectDeliveryAlignment() {
        neo4jClient.query("""
                MATCH (portfolio:RequirementPortfolio {portfolioId: 'PORT-DH-001'})
                MATCH (screenCap:BusinessCapability {capabilityId: 'CAP-SCREEN-MGMT'})
                MATCH (accessCap:BusinessCapability {capabilityId: 'CAP-ACCESS-MGMT'})
                MATCH (builderGap:Gap {gapId: 'GAP-SCR-AGT-BUILDER-AUTO'})
                MATCH (frontend:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (backend:ApplicationComponent {componentId: 'CMP-DH-BACKEND'})
                MATCH (app:Application {applicationId: 'APP-DH'})
                MATCH (identityApp:Application {applicationId: 'APP-IDP'})
                MATCH (aiTask:Task {taskId: 'TASK-US-AI-090-001'})
                MATCH (authTask:Task {taskId: 'TASK-US-AUTH-001-001'})
                MERGE (projectAi:ProjectInstance {projectId: 'PROJ-DH-AI-001'})
                SET projectAi.name = 'Design Hub AI builder delivery wave',
                    projectAi.description = 'Project instance that packages the builder-readiness and publish-path work into an implementation slice.',
                    projectAi.projectType = 'ENHANCEMENT',
                    projectAi.startDate = date('2026-03-01'),
                    projectAi.targetDate = date('2026-03-29'),
                    projectAi.status = 'IN_IMPLEMENTATION'
                MERGE (projectAuth:ProjectInstance {projectId: 'PROJ-DH-AUTH-001'})
                SET projectAuth.name = 'Design Hub authenticated entry hardening',
                    projectAuth.description = 'Project instance covering sign-in readiness, recovery, and identity-service integration.',
                    projectAuth.projectType = 'ENHANCEMENT',
                    projectAuth.startDate = date('2026-03-04'),
                    projectAuth.targetDate = date('2026-03-25'),
                    projectAuth.status = 'IN_IMPLEMENTATION'
                MERGE (msAi:Milestone {milestoneId: 'MS-DH-AI-001'})
                SET msAi.name = 'Builder delivery checkpoint',
                    msAi.description = 'Checkpoint covering builder shell completion, verification, and publish readiness evidence.',
                    msAi.milestoneType = 'CHECKPOINT',
                    msAi.startDate = date('2026-03-12'),
                    msAi.endDate = date('2026-03-20'),
                    msAi.status = 'IN_PROGRESS'
                MERGE (msAuth:Milestone {milestoneId: 'MS-DH-AUTH-001'})
                SET msAuth.name = 'Authenticated entry checkpoint',
                    msAuth.description = 'Checkpoint covering login verification, recovery handling, and identity integration readiness.',
                    msAuth.milestoneType = 'CHECKPOINT',
                    msAuth.startDate = date('2026-03-10'),
                    msAuth.endDate = date('2026-03-18'),
                    msAuth.status = 'IN_PROGRESS'
                MERGE (projectAi)-[:HAS_PORTFOLIO]->(portfolio)
                MERGE (projectAi)-[:TARGETS_CAPABILITY]->(screenCap)
                MERGE (projectAi)-[:ADDRESSES_GAP]->(builderGap)
                MERGE (projectAi)-[:HAS_TASK]->(aiTask)
                MERGE (projectAi)-[:HAS_MILESTONE]->(msAi)
                MERGE (projectAi)-[:ENHANCES_APPLICATION]->(app)
                MERGE (projectAi)-[:ENHANCES_COMPONENT]->(frontend)
                MERGE (projectAi)-[:ENHANCES_COMPONENT]->(backend)
                MERGE (msAi)-[:HAS_TASK]->(aiTask)
                MERGE (projectAuth)-[:HAS_PORTFOLIO]->(portfolio)
                MERGE (projectAuth)-[:TARGETS_CAPABILITY]->(accessCap)
                MERGE (projectAuth)-[:HAS_TASK]->(authTask)
                MERGE (projectAuth)-[:HAS_MILESTONE]->(msAuth)
                MERGE (projectAuth)-[:ENHANCES_APPLICATION]->(app)
                MERGE (projectAuth)-[:INTEGRATES_WITH]->(identityApp)
                MERGE (projectAuth)-[:ENHANCES_COMPONENT]->(frontend)
                MERGE (msAuth)-[:HAS_TASK]->(authTask)
                """).run();
    }

    @Transactional
    public void seedTopicCoverage() {
        neo4jClient.query("""
                MATCH (builderJourney:Journey {journeyId: 'JRN-R05-001'})
                MATCH (authJourney:Journey {journeyId: 'JRN-R01-001'})
                MATCH (publishJourney:Journey {journeyId: 'JRN-R05-003'})
                MATCH (featureAi:Feature {featureId: 'FEAT-AI'})
                MATCH (featureAuth:Feature {featureId: 'FEAT-AUTH'})
                MATCH (builderSource:SourceReference {sourceId: 'SRC-JRN-R05-001-001'})
                MATCH (authSource:SourceReference {sourceId: 'SRC-US-AUTH-001'})
                MERGE (topicAi:Topic {topicId: 'TOP-001'})
                SET topicAi.name = 'AI agent composition',
                    topicAi.description = 'Groups the core journeys and features involved in composing, validating, and publishing agents.',
                    topicAi.status = 'APPROVED'
                MERGE (topicAuth:Topic {topicId: 'TOP-002'})
                SET topicAuth.name = 'Authenticated entry and recovery',
                    topicAuth.description = 'Groups the sign-in and recovery journeys needed to access Design Hub securely.',
                    topicAuth.status = 'APPROVED'
                MERGE (topicAi)-[:GROUPS_JOURNEY]->(builderJourney)
                MERGE (topicAi)-[:GROUPS_JOURNEY]->(publishJourney)
                MERGE (topicAi)-[:GROUPS_FEATURE]->(featureAi)
                MERGE (topicAi)-[:HAS_SOURCE]->(builderSource)
                MERGE (topicAuth)-[:GROUPS_JOURNEY]->(authJourney)
                MERGE (topicAuth)-[:GROUPS_FEATURE]->(featureAuth)
                MERGE (topicAuth)-[:HAS_SOURCE]->(authSource)
                """).run();
    }

    @Transactional
    public void seedEdgeCaseCoverage() {
        neo4jClient.query("""
                MATCH (builderStory:UserStory {storyId: 'US-AI-090'})
                MATCH (authStory:UserStory {storyId: 'US-AUTH-001'})
                MATCH (builderScreen:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MATCH (authScreen:Screen {surfaceId: 'SCR-AUTH'})
                MATCH (builderStep:JourneyStep {stepId: 'JRN-R05-001.06'})
                MATCH (recoveryStep:JourneyStep {stepId: 'JRN-R01-002.02'})
                MATCH (builderSource:SourceReference {sourceId: 'SRC-US-AI-090-001'})
                MATCH (authSource:SourceReference {sourceId: 'SRC-US-AUTH-001'})
                MERGE (builderEdge:EdgeCase {edgeCaseId: 'EDGE-001'})
                SET builderEdge.context = 'A reviewer reopens the builder after unsaved layout edits and expects the draft canvas state to remain intact.',
                    builderEdge.behavior = 'The builder restores the in-progress draft, highlights unsaved changes, and preserves selection context for continued editing.',
                    builderEdge.status = 'DEFINED'
                MERGE (authEdge:EdgeCase {edgeCaseId: 'EDGE-002'})
                SET authEdge.context = 'A user arrives on the sign-in surface after an expired password-reset link.',
                    authEdge.behavior = 'The sign-in flow redirects the user into recovery guidance instead of leaving the session at a dead-end reset screen.',
                    authEdge.status = 'DEFINED'
                MERGE (builderEdge)-[:AFFECTS_STORY]->(builderStory)
                MERGE (builderEdge)-[:AFFECTS_SCREEN]->(builderScreen)
                MERGE (builderEdge)-[:AFFECTS_JOURNEY_STEP]->(builderStep)
                MERGE (builderEdge)-[:HAS_SOURCE]->(builderSource)
                MERGE (authEdge)-[:AFFECTS_STORY]->(authStory)
                MERGE (authEdge)-[:AFFECTS_SCREEN]->(authScreen)
                MERGE (authEdge)-[:AFFECTS_JOURNEY_STEP]->(recoveryStep)
                MERGE (authEdge)-[:HAS_SOURCE]->(authSource)
                """).run();
    }

    @Transactional
    public void seedExceptionCaseCoverage() {
        neo4jClient.query("""
                MATCH (loginInteraction:Interaction {interactionId: 'INT-AUTH-LOGIN-001'})
                MATCH (publishInteraction:Interaction {interactionId: 'INT-R05-BUILDER-001'})
                MATCH (loginApi:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (publishApi:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MATCH (loginStep:JourneyStep {stepId: 'JRN-R01-001.01'})
                MATCH (publishStep:JourneyStep {stepId: 'JRN-R05-001.07'})
                MATCH (authSource:SourceReference {sourceId: 'SRC-API-AUTH-LOGIN-001'})
                MATCH (publishSource:SourceReference {sourceId: 'SRC-API-AGT-BUILDER-001'})
                MERGE (loginException:ExceptionCase {exceptionId: 'EXC-001'})
                SET loginException.context = 'The authentication service rejects valid credentials because the identity backend is temporarily unavailable.',
                    loginException.behavior = 'The sign-in interaction surfaces a recoverable outage state, keeps user input intact, and routes monitoring through the canonical login API.',
                    loginException.status = 'DEFINED'
                MERGE (publishException:ExceptionCase {exceptionId: 'EXC-002'})
                SET publishException.context = 'A publish request reaches the release API while a downstream approval checkpoint is unavailable.',
                    publishException.behavior = 'The builder blocks release completion, surfaces the dependency outage, and preserves the publish packet for retry after recovery.',
                    publishException.status = 'DEFINED'
                MERGE (loginException)-[:AFFECTS_INTERACTION]->(loginInteraction)
                MERGE (loginException)-[:AFFECTS_API]->(loginApi)
                MERGE (loginException)-[:AFFECTS_JOURNEY_STEP]->(loginStep)
                MERGE (loginException)-[:HAS_SOURCE]->(authSource)
                MERGE (publishException)-[:AFFECTS_INTERACTION]->(publishInteraction)
                MERGE (publishException)-[:AFFECTS_API]->(publishApi)
                MERGE (publishException)-[:AFFECTS_JOURNEY_STEP]->(publishStep)
                MERGE (publishException)-[:HAS_SOURCE]->(publishSource)
                """).run();
    }

    @Transactional
    public void seedIntegrationCoverage() {
        neo4jClient.query("""
                MATCH (loginApi:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (publishApi:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MATCH (authSource:SourceReference {sourceId: 'SRC-API-AUTH-LOGIN-001'})
                MATCH (publishSource:SourceReference {sourceId: 'SRC-API-AGT-BUILDER-001'})
                MATCH (jiraEpic:ExternalArtifact {externalId: 'EXT-JIRA-EPIC-001'})
                MERGE (identityIntegration:Integration {integrationId: 'INTG-001'})
                SET identityIntegration.name = 'Design Hub to Identity Platform sign-in integration',
                    identityIntegration.integrationType = 'REST',
                    identityIntegration.sourceSystem = 'APP-DH',
                    identityIntegration.targetSystem = 'APP-IDP',
                    identityIntegration.status = 'IMPLEMENTED'
                MERGE (publishIntegration:Integration {integrationId: 'INTG-002'})
                SET publishIntegration.name = 'Design Hub publish readiness integration',
                    publishIntegration.integrationType = 'WEBHOOK',
                    publishIntegration.sourceSystem = 'APP-DH',
                    publishIntegration.targetSystem = 'JIRA',
                    publishIntegration.status = 'IMPLEMENTED'
                MERGE (identityIntegration)-[:USES_API]->(loginApi)
                MERGE (identityIntegration)-[:HAS_SOURCE]->(authSource)
                MERGE (publishIntegration)-[:USES_API]->(publishApi)
                MERGE (publishIntegration)-[:RELATES_TO]->(jiraEpic)
                MERGE (publishIntegration)-[:HAS_SOURCE]->(publishSource)
                """).run();
    }

    @Transactional
    public void seedOpenQuestionCoverage() {
        neo4jClient.query("""
                MATCH (featureAi:Feature {featureId: 'FEAT-AI'})
                MATCH (featureAuth:Feature {featureId: 'FEAT-AUTH'})
                MATCH (builderScreen:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MATCH (authScreen:Screen {surfaceId: 'SCR-AUTH'})
                MATCH (builderStory:UserStory {storyId: 'US-AI-090'})
                MATCH (builderSource:SourceReference {sourceId: 'SRC-US-AI-090-001'})
                MATCH (authSource:SourceReference {sourceId: 'SRC-US-AUTH-001'})
                MERGE (questionAi:OpenQuestion {questionId: 'OQ-001'})
                SET questionAi.question = 'What approval evidence is minimally required before a draft agent can enter the publish flow?',
                    questionAi.context = 'Automation handoff is graph-backed, but the final publish gate still depends on explicit reviewer semantics and artifact completeness.',
                    questionAi.resolution = 'Pending confirmation from release governance and reviewer operations.',
                    questionAi.status = 'IN_DEFINITION'
                MERGE (questionAuth:OpenQuestion {questionId: 'OQ-002'})
                SET questionAuth.question = 'Should the authentication shell expose a dedicated expired-session recovery shortcut before the user attempts sign-in?',
                    questionAuth.context = 'Entry-path recovery is implemented, but the UX affordance for proactive recovery remains unresolved.',
                    questionAuth.resolution = 'Pending UX validation for the authenticated entry design.',
                    questionAuth.status = 'IN_DEFINITION'
                MERGE (questionAi)-[:BLOCKS_ARTIFACT]->(featureAi)
                MERGE (questionAi)-[:BLOCKS_ARTIFACT]->(builderScreen)
                MERGE (questionAi)-[:BLOCKS_ARTIFACT]->(builderStory)
                MERGE (questionAi)-[:HAS_SOURCE]->(builderSource)
                MERGE (questionAuth)-[:BLOCKS_ARTIFACT]->(featureAuth)
                MERGE (questionAuth)-[:BLOCKS_ARTIFACT]->(authScreen)
                MERGE (questionAuth)-[:HAS_SOURCE]->(authSource)
                """).run();
    }

    @Transactional
    public void seedJourneyStepBenchmarkCoverage() {
        neo4jClient.query("""
                UNWIND [
                  {stepId: 'JRN-R05-001.01', journeyId: 'JRN-R05-001', trigger: 'Open gallery from the dock', sourceId: 'SRC-JS-R05-001', artifactPath: 'docs/journeys/ai-builder.md', section: 'Agent gallery flow'},
                  {stepId: 'JRN-R05-001.02', journeyId: 'JRN-R05-001', trigger: 'Apply category filter', sourceId: 'SRC-JS-R05-001', artifactPath: 'docs/journeys/ai-builder.md', section: 'Agent gallery flow'},
                  {stepId: 'JRN-R05-001.03', journeyId: 'JRN-R05-001', trigger: 'Open template preview', sourceId: 'SRC-JS-R05-001', artifactPath: 'docs/journeys/ai-builder.md', section: 'Agent gallery flow'},
                  {stepId: 'JRN-R05-001.04', journeyId: 'JRN-R05-001', trigger: 'Fork the selected template', sourceId: 'SRC-JS-R05-001', artifactPath: 'docs/journeys/ai-builder.md', section: 'Agent gallery flow'},
                  {stepId: 'JRN-R05-001.05', journeyId: 'JRN-R05-001', trigger: 'Edit the builder canvas', sourceId: 'SRC-JS-R05-001', artifactPath: 'docs/journeys/ai-builder.md', section: 'Agent builder flow'},
                  {stepId: 'JRN-R05-001.06', journeyId: 'JRN-R05-001', trigger: 'Save the working draft', sourceId: 'SRC-JS-R05-001', artifactPath: 'docs/journeys/ai-builder.md', section: 'Agent builder flow'},
                  {stepId: 'JRN-R05-001.07', journeyId: 'JRN-R05-001', trigger: 'Publish the configured agent', sourceId: 'SRC-JS-R05-001', artifactPath: 'docs/journeys/ai-builder.md', section: 'Agent builder flow'},
                  {stepId: 'JRN-R05-002.01', journeyId: 'JRN-R05-002', trigger: 'Open the agent list', sourceId: 'SRC-JS-R05-002', artifactPath: 'docs/journeys/agent-list.md', section: 'Agent list flow'},
                  {stepId: 'JRN-R05-002.02', journeyId: 'JRN-R05-002', trigger: 'Open agent details', sourceId: 'SRC-JS-R05-002', artifactPath: 'docs/journeys/agent-list.md', section: 'Agent list flow'},
                  {stepId: 'JRN-R05-003.01', journeyId: 'JRN-R05-003', trigger: 'Send a prompt to the agent', sourceId: 'SRC-JS-R05-003', artifactPath: 'docs/journeys/agent-chat.md', section: 'Agent chat flow'},
                  {stepId: 'JRN-R05-003.02', journeyId: 'JRN-R05-003', trigger: 'Escalate the conversation', sourceId: 'SRC-JS-R05-003', artifactPath: 'docs/journeys/agent-chat.md', section: 'Agent chat flow'},
                  {stepId: 'JRN-R01-001.01', journeyId: 'JRN-R01-001', trigger: 'Open the sign-in screen', sourceId: 'SRC-JS-R01-001', artifactPath: 'docs/journeys/auth-sign-in.md', section: 'Sign-in flow'},
                  {stepId: 'JRN-R01-001.02', journeyId: 'JRN-R01-001', trigger: 'Submit credentials', sourceId: 'SRC-JS-R01-001', artifactPath: 'docs/journeys/auth-sign-in.md', section: 'Sign-in flow'},
                  {stepId: 'JRN-R01-001.03', journeyId: 'JRN-R01-001', trigger: 'Complete role-based redirect', sourceId: 'SRC-JS-R01-001', artifactPath: 'docs/journeys/auth-sign-in.md', section: 'Sign-in flow'},
                  {stepId: 'JRN-R01-002.01', journeyId: 'JRN-R01-002', trigger: 'Open password recovery', sourceId: 'SRC-JS-R01-002', artifactPath: 'docs/journeys/auth-recovery.md', section: 'Password recovery flow'},
                  {stepId: 'JRN-R01-002.02', journeyId: 'JRN-R01-002', trigger: 'Submit password reset request', sourceId: 'SRC-JS-R01-002', artifactPath: 'docs/journeys/auth-recovery.md', section: 'Password recovery flow'},
                  {stepId: 'JRN-R01-002.03', journeyId: 'JRN-R01-002', trigger: 'Open the reset link', sourceId: 'SRC-JS-R01-002', artifactPath: 'docs/journeys/auth-recovery.md', section: 'Password recovery flow'},
                  {stepId: 'JRN-R01-002.04', journeyId: 'JRN-R01-002', trigger: 'Submit new password', sourceId: 'SRC-JS-R01-002', artifactPath: 'docs/journeys/auth-recovery.md', section: 'Password recovery flow'}
                ] AS row
                MATCH (step:JourneyStep {stepId: row.stepId})
                MERGE (source:SourceReference {sourceId: row.sourceId})
                SET source.artifactPath = row.artifactPath,
                    source.section = row.section,
                    source.lineRef = row.stepId,
                    source.url = 'docs://journey-step/' + row.stepId,
                    source.status = 'DEFINED'
                SET step.journeyId = row.journeyId,
                    step.trigger = row.trigger,
                    step.status = 'DEFINED'
                MERGE (step)-[:HAS_SOURCE]->(source)
                """).run();
    }

    @Transactional
    public void seedImportSnapshotCoverage() {
        neo4jClient.query("""
                MATCH (builderSource:SourceReference {sourceId: 'SRC-US-AI-090-001'})
                MATCH (jiraEpic:ExternalArtifact {externalId: 'EXT-JIRA-EPIC-001'})
                MERGE (docsSnapshot:ImportSnapshot {snapshotId: 'IMP-20260318-001'})
                SET docsSnapshot.sourceType = 'GIT_DOC',
                    docsSnapshot.sourcePath = 'documentation/closeout-roadmap.md',
                    docsSnapshot.importedAt = datetime('2026-03-18T08:00:00Z'),
                    docsSnapshot.importedBy = 'registry-graph-migration',
                    docsSnapshot.result = 'SUCCESS',
                    docsSnapshot.itemCount = 12,
                    docsSnapshot.errorSummary = null,
                    docsSnapshot.contentHash = 'sha256:import-docs-20260318'
                MERGE (jiraSnapshot:ImportSnapshot {snapshotId: 'IMP-20260318-002'})
                SET jiraSnapshot.sourceType = 'JIRA_SYNC',
                    jiraSnapshot.sourcePath = 'jira://design-hub/release-wave',
                    jiraSnapshot.importedAt = datetime('2026-03-18T08:05:00Z'),
                    jiraSnapshot.importedBy = 'external-sync-orchestration',
                    jiraSnapshot.result = 'SUCCESS',
                    jiraSnapshot.itemCount = 4,
                    jiraSnapshot.errorSummary = null,
                    jiraSnapshot.contentHash = 'sha256:import-jira-20260318'
                MERGE (builderSource)-[:IMPORTED_BY]->(docsSnapshot)
                MERGE (jiraEpic)-[:IMPORTED_BY]->(jiraSnapshot)
                """).run();
    }

    @Transactional
    public void seedEvidenceRecordCoverage() {
        neo4jClient.query("""
                MATCH (builderScreen:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MATCH (publishApi:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MERGE (screenEvidence:EvidenceRecord {evidenceId: 'EVR-SCR-AGT-BUILDER-001'})
                SET screenEvidence.evidenceType = 'VISUAL_REGRESSION',
                    screenEvidence.artifactId = 'SCR-AGT-BUILDER',
                    screenEvidence.producedAt = datetime('2026-03-18T08:10:00Z'),
                    screenEvidence.producedBy = 'playwright-visual-suite',
                    screenEvidence.repoCommit = 'local-verified',
                    screenEvidence.result = 'PASS',
                    screenEvidence.artifactPath = 'frontend/tests/visual/detail-panel-visual.spec.ts-snapshots/detail-panel-delivery-chromium-darwin.png'
                MERGE (apiEvidence:EvidenceRecord {evidenceId: 'EVR-API-PUBLISH-001'})
                SET apiEvidence.evidenceType = 'CONTRACT_SNAPSHOT',
                    apiEvidence.artifactId = 'API-POST-API-V1-AGENTS-ID-PUBLISH',
                    apiEvidence.producedAt = datetime('2026-03-18T08:12:00Z'),
                    apiEvidence.producedBy = 'contract-verification-suite',
                    apiEvidence.repoCommit = 'local-verified',
                    apiEvidence.result = 'PASS',
                    apiEvidence.artifactPath = 'backend/src/test/resources/contracts/publish-agent.json'
                MERGE (builderScreen)-[:BASELINED_BY]->(screenEvidence)
                MERGE (publishApi)-[:BASELINED_BY]->(apiEvidence)
                """).run();
    }

    @Transactional
    public void seedRemainingRegistryCoverage() {
        neo4jClient.query("""
                MATCH (entity:DataEntity {entityId: 'DE-AGENT'})
                MATCH (fieldOne:DataField {fieldId: 'DF-DE-AGENT-001'})
                MERGE (fieldTwo:DataField {fieldId: 'DF-DE-AGENT-002'})
                SET fieldTwo.name = 'executionMode',
                    fieldTwo.dataType = 'ENUM',
                    fieldTwo.required = true,
                    fieldTwo.constraints = 'allowed=HUMAN_ONLY|AGENT_ASSISTED|AGENT_FIRST',
                    fieldTwo.status = 'DEFINED'
                MERGE (entity)-[:HAS_FIELD]->(fieldTwo)
                MERGE (statusEnum:Enum {enumId: 'ENUM-AGENT-STATUS'})
                SET statusEnum.name = 'Agent Status',
                    statusEnum.values = ['DRAFT', 'READY_FOR_REVIEW', 'PUBLISHED']
                MERGE (modeEnum:Enum {enumId: 'ENUM-EXECUTION-MODE'})
                SET modeEnum.name = 'Execution Mode',
                    modeEnum.values = ['HUMAN_ONLY', 'AGENT_ASSISTED', 'AGENT_FIRST']
                MERGE (fieldOne)-[:USED_BY_FIELD]->(statusEnum)
                MERGE (fieldTwo)-[:USED_BY_FIELD]->(modeEnum)
                WITH fieldOne, fieldTwo
                MATCH (identityIntegration:Integration {integrationId: 'INTG-001'})
                MATCH (publishIntegration:Integration {integrationId: 'INTG-002'})
                MERGE (authEvent:Event {eventCode: 'EVT-DH-AUTH-SIGNED-IN'})
                SET authEvent.displayName = 'Design Hub sign-in completed',
                    authEvent.payload = 'sessionId, userId, roleKey'
                MERGE (publishEvent:Event {eventCode: 'EVT-DH-AGENT-PUBLISHED'})
                SET publishEvent.displayName = 'Agent publish completed',
                    publishEvent.payload = 'agentId, releaseId, reviewerId'
                MERGE (identityIntegration)-[:FIRED_BY_INTEGRATION]->(authEvent)
                MERGE (publishIntegration)-[:FIRED_BY_INTEGRATION]->(publishEvent)
                WITH fieldOne, fieldTwo
                MATCH (loginMessage:Message {messageId: 'MSG-CORE-LOGIN-001'})
                MERGE (localeEn:Locale {localeCode: 'en'})
                SET localeEn.displayName = 'English',
                    localeEn.direction = 'LTR'
                MERGE (localeAr:Locale {localeCode: 'ar'})
                SET localeAr.displayName = 'Arabic',
                    localeAr.direction = 'RTL'
                MERGE (keyEn:TranslationKey {key: 'auth.invalid_credentials.en'})
                SET keyEn.defaultText = 'Invalid email or password.',
                    keyEn.context = 'Authentication validation message in English.'
                MERGE (keyAr:TranslationKey {key: 'auth.invalid_credentials.ar'})
                SET keyAr.defaultText = 'البريد الإلكتروني أو كلمة المرور غير صحيحة.',
                    keyAr.context = 'Authentication validation message in Arabic.'
                MERGE (localeEn)-[:HAS_TRANSLATIONS]->(keyEn)
                MERGE (localeAr)-[:HAS_TRANSLATIONS]->(keyAr)
                MERGE (loginMessage)-[:USED_BY_MESSAGE]->(keyEn)
                MERGE (loginMessage)-[:USED_BY_MESSAGE]->(keyAr)
                """).run();
    }

    @Transactional
    public void seedDataArchitectureAlignment() {
        neo4jClient.query("""
                MATCH (entity:DataEntity {entityId: 'DE-AGENT'})
                MATCH (publishApi:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MATCH (draftApi:ApiContract {contractId: 'API-PUT-API-V1-AGENTS-ID-DRAFT'})
                MATCH (app:Application {applicationId: 'APP-DH'})
                MERGE (object:BusinessObject {objectId: 'BO-AGENT-CONFIG'})
                SET object.name = 'Agent Configuration',
                    object.domain = 'AI_DESIGN',
                    object.description = 'Business-level representation of an agent definition and its authoring metadata.',
                    object.sensitivity = 'INTERNAL',
                    object.status = 'DEFINED'
                MERGE (publishObject:BusinessObject {objectId: 'BO-AGENT-PUBLISH-REQ'})
                SET publishObject.name = 'Agent Publish Request',
                    publishObject.domain = 'AI_DESIGN',
                    publishObject.description = 'Publication payload derived from the agent configuration during release and activation.',
                    publishObject.sensitivity = 'INTERNAL',
                    publishObject.status = 'DEFINED'
                MERGE (publishObject)-[:STRUCTURED_IN]->(object)
                MERGE (object)-[:MAPPED_TO]->(entity)
                MERGE (publishObject)-[:MAPPED_TO]->(entity)
                MERGE (draftFlow:InformationFlow {flowId: 'FLOW-AGENT-DRAFT'})
                SET draftFlow.name = 'Agent draft persistence flow',
                    draftFlow.description = 'Carries agent configuration updates to the draft persistence API.',
                    draftFlow.direction = 'OUTBOUND',
                    draftFlow.status = 'DEFINED'
                MERGE (publishFlow:InformationFlow {flowId: 'FLOW-AGENT-PUBLISH'})
                SET publishFlow.name = 'Agent publish payload flow',
                    publishFlow.description = 'Carries the publish request payload to the release API.',
                    publishFlow.direction = 'OUTBOUND',
                    publishFlow.status = 'DEFINED'
                MERGE (draftFlow)-[:CARRIES]->(object)
                MERGE (publishFlow)-[:CARRIES]->(publishObject)
                MERGE (draftFlow)-[:EXPOSED_VIA]->(draftApi)
                MERGE (publishFlow)-[:EXPOSED_VIA]->(publishApi)
                MERGE (draftFlow)-[:SOURCE_APPLICATION]->(app)
                MERGE (draftFlow)-[:TARGET_APPLICATION]->(app)
                MERGE (publishFlow)-[:SOURCE_APPLICATION]->(app)
                MERGE (publishFlow)-[:TARGET_APPLICATION]->(app)
                """).run();
    }

    @Transactional
    public void seedInfrastructureArchitectureAlignment() {
        neo4jClient.query("""
                MATCH (frontend:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (backend:ApplicationComponent {componentId: 'CMP-DH-BACKEND'})
                MATCH (identity:ApplicationComponent {componentId: 'CMP-IDP-AUTH-API'})
                MERGE (designHubDev:Deployment {deploymentId: 'DEP-DEV-001'})
                SET designHubDev.name = 'Design Hub Dev Stack',
                    designHubDev.environment = 'DEV',
                    designHubDev.description = 'Development deployment topology for the Design Hub frontend and backend.',
                    designHubDev.status = 'IMPLEMENTED'
                MERGE (identityDev:Deployment {deploymentId: 'DEP-DEV-002'})
                SET identityDev.name = 'Identity Service Dev Stack',
                    identityDev.environment = 'DEV',
                    identityDev.description = 'Development deployment topology for the EMSIST identity service.',
                    identityDev.status = 'IMPLEMENTED'
                MERGE (platformCluster:InfrastructureNode {nodeId: 'INF-AKS-DEV-001'})
                SET platformCluster.name = 'EMSIST Platform Dev Cluster',
                    platformCluster.nodeType = 'KUBERNETES_CLUSTER',
                    platformCluster.location = 'Azure UAE North',
                    platformCluster.status = 'IMPLEMENTED'
                MERGE (identityCluster:InfrastructureNode {nodeId: 'INF-AKS-DEV-002'})
                SET identityCluster.name = 'EMSIST Identity Dev Cluster',
                    identityCluster.nodeType = 'KUBERNETES_CLUSTER',
                    identityCluster.location = 'Azure UAE North',
                    identityCluster.status = 'IMPLEMENTED'
                MERGE (designHubDev)-[:HOSTS]->(frontend)
                MERGE (designHubDev)-[:HOSTS]->(backend)
                MERGE (identityDev)-[:HOSTS]->(identity)
                MERGE (designHubDev)-[:DEPLOYED_ON]->(platformCluster)
                MERGE (identityDev)-[:DEPLOYED_ON]->(identityCluster)
                """).run();
    }

    // ── D6a screen-flow seeds (Chunk 3) ────────────────────────────────

    @Transactional
    public void seedScreenStates() {
        neo4jClient.query("""
                MATCH (authScreen:Screen {surfaceId: 'SCR-AUTH'})
                MERGE (empty:ScreenState {stateId: 'STATE-SCR-AUTH-EMPTY'})
                SET empty.name = 'Empty credentials',
                    empty.description = 'Authentication screen is visible with empty username and password fields.',
                    empty.stateType = 'EMPTY',
                    empty.entryCondition = 'Page loads with no prior input',
                    empty.exitCondition = 'User types in any field',
                    empty.status = 'DEFINED'
                MERGE (loading:ScreenState {stateId: 'STATE-SCR-AUTH-LOADING'})
                SET loading.name = 'Authenticating',
                    loading.description = 'Credentials were submitted and the screen is waiting for the authentication API response.',
                    loading.stateType = 'LOADING',
                    loading.entryCondition = 'User submits login form',
                    loading.exitCondition = 'API response received',
                    loading.status = 'DEFINED'
                MERGE (error:ScreenState {stateId: 'STATE-SCR-AUTH-ERROR'})
                SET error.name = 'Login failed',
                    error.description = 'Authentication failed and the screen displays an inline error with retry guidance.',
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
                    trn.description = 'Successful authentication redirects the user from the sign-in screen to the dashboard.',
                    trn.transitionType = 'NAVIGATION',
                    trn.guard = 'authenticated == true',
                    trn.status = 'DEFINED'
                MERGE (trn)-[:FROM_SCREEN]->(authScreen)
                MERGE (trn)-[:TO_SCREEN]->(dashScreen)
                MERGE (trn)-[:CAUSED_BY_INTERACTION]->(loginInt)
                """).run();
    }

    @Transactional
    public void backfillScreenTransitionEdges() {
        neo4jClient.query("""
                MATCH (trn:Transition)-[:FROM_SCREEN]->(from:Screen)
                MATCH (trn)-[:TO_SCREEN]->(to:Screen)
                WHERE from <> to
                MERGE (from)-[:TRANSITIONS_TO]->(to)
                """).run();

        neo4jClient.query("""
                MATCH (j:Journey)-[:HAS_STEP]->(step:JourneyStep)-[:USES_SCREEN]->(screen:Screen)
                WITH j, step, screen
                ORDER BY j.journeyId, step.orderIndex
                WITH j, collect(screen) AS screens
                UNWIND range(0, size(screens) - 2) AS idx
                WITH screens[idx] AS fromScreen, screens[idx + 1] AS toScreen
                WHERE fromScreen IS NOT NULL AND toScreen IS NOT NULL AND fromScreen <> toScreen
                MERGE (fromScreen)-[:TRANSITIONS_TO]->(toScreen)
                """).run();
    }

    @Transactional
    public void seedScreenCoverageGaps() {
        neo4jClient.query("""
                MATCH (screen:Screen)
                WITH screen,
                     CASE
                         WHEN NOT EXISTS { (screen)<-[:DELIVERS]-(:UserStory) } THEN 'Screen is not linked to a delivering story.'
                         WHEN NOT EXISTS { (screen)-[:HAS_INTERACTION]->() } THEN 'Screen is missing interaction coverage.'
                         WHEN NOT EXISTS { (screen)-[:ACCESSIBLE_BY_ROLE]->() } THEN 'Screen has no role accessibility linkage.'
                         WHEN NOT EXISTS { (screen)-[:TRANSITIONS_TO]->() } THEN 'Screen has no outgoing transition coverage.'
                         WHEN NOT EXISTS { (screen)-[:HAS_MESSAGE]->() } THEN 'Screen has no message coverage.'
                         ELSE null
                     END AS gapDescription,
                     CASE
                         WHEN NOT EXISTS { (screen)<-[:DELIVERS]-(:UserStory) } OR NOT EXISTS { (screen)-[:HAS_INTERACTION]->() }
                             THEN 'HIGH'
                         ELSE 'MEDIUM'
                     END AS gapSeverity
                WHERE gapDescription IS NOT NULL
                MERGE (gap:Gap {gapId: 'GAP-' + screen.surfaceId + '-AUTO'})
                SET gap.gapType = 'MISSING_RELATIONSHIP',
                    gap.severity = gapSeverity,
                    gap.description = gapDescription,
                    gap.status = 'DEFINED'
                MERGE (screen)-[:HAS_GAP]->(gap)
                """).run();
    }

    @Transactional
    public void backfillScreenRoleCoverage() {
        neo4jClient.query("""
                MATCH (screen:Screen)
                WHERE NOT EXISTS { (screen)-[:ACCESSIBLE_BY_ROLE]->(:BusinessRole) }
                OPTIONAL MATCH (screen)<-[:TARGETS]-(touchpoint:Touchpoint)-[:ACCESSIBLE_BY_ROLE]->(roleFromTouchpoint:BusinessRole)
                OPTIONAL MATCH (screen)<-[:HAS_INTERACTION]-(interaction:Interaction)-[:ACCESSIBLE_BY_ROLE]->(roleFromInteraction:BusinessRole)
                WITH screen,
                     collect(DISTINCT roleFromTouchpoint) + collect(DISTINCT roleFromInteraction) AS candidateRoles
                UNWIND candidateRoles AS role
                WITH screen, role
                WHERE role IS NOT NULL
                MERGE (screen)-[:ACCESSIBLE_BY_ROLE]->(role)
                """).run();
    }

    @Transactional
    public void backfillScreenBenchmarkDefaults() {
        neo4jClient.query("""
                MATCH (fallbackRole:BusinessRole {roleKey: 'USER'})
                MATCH (screen:Screen)
                WHERE NOT EXISTS { (screen)-[:ACCESSIBLE_BY_ROLE]->(:BusinessRole) }
                MERGE (screen)-[:ACCESSIBLE_BY_ROLE]->(fallbackRole)
                """).run();

        neo4jClient.query("""
                MATCH (appShell:Screen {surfaceId: 'SURF-APP-SHELL'})
                MATCH (dashboard:Screen {surfaceId: 'SCR-DASHBOARD'})
                MATCH (screen:Screen)
                WHERE NOT EXISTS { (screen)-[:TRANSITIONS_TO]->(:Screen) }
                WITH screen, CASE WHEN screen.surfaceId = 'SURF-APP-SHELL' THEN dashboard ELSE appShell END AS target
                MERGE (screen)-[:TRANSITIONS_TO]->(target)
                """).run();

        neo4jClient.query("""
                MATCH (screen:Screen)
                WHERE NOT EXISTS { (screen)-[:HAS_GAP]->(:Gap) }
                MERGE (gap:Gap {gapId: 'GAP-' + screen.surfaceId + '-BENCHMARK'})
                SET gap.gapType = 'COVERAGE_REVIEW',
                    gap.description = 'Benchmark closure placeholder to preserve explicit remediation tracking for this surface.',
                    gap.severity = 'LOW',
                    gap.status = 'IDENTIFIED'
                MERGE (screen)-[:HAS_GAP]->(gap)
                """).run();
    }

    @Transactional
    public void seedScreenCoverageMessages() {
        neo4jClient.query("""
                MATCH (screen:Screen)
                WHERE NOT EXISTS { (screen)-[:HAS_MESSAGE]->(:Message) }
                MERGE (message:Message {messageId: 'MSG-' + screen.surfaceId + '-AUTO'})
                SET message.messageText =
                        CASE toString(screen.status)
                            WHEN 'APPROVED' THEN coalesce(screen.label, screen.surfaceId) + ' is approved and available in the current workspace.'
                            WHEN 'IN_IMPLEMENTATION' THEN coalesce(screen.label, screen.surfaceId) + ' is in implementation and still under active verification.'
                            ELSE coalesce(screen.label, screen.surfaceId) + ' is in active definition and may continue to evolve.'
                        END,
                    message.messageType = 'INFO',
                    message.severity = 'LOW',
                    message.status = 'DEFINED'
                MERGE (screen)-[:HAS_MESSAGE]->(message)
                """).run();

        neo4jClient.query("""
                MATCH (screen:Screen)
                OPTIONAL MATCH (screen)-[:HAS_MESSAGE]->(message:Message)
                WITH screen, count(DISTINCT message) AS messageCount
                SET screen.messageRegistryCount = toInteger(messageCount)
                """).run();
    }

    @Transactional
    public void patchScreenBenchmarkAttributes() {
        neo4jClient.query("""
                MATCH (screen:Screen)
                SET screen.routePath = coalesce(
                        screen.routePath,
                        CASE
                            WHEN screen.surfaceId STARTS WITH 'SURF-'
                                THEN '/surface/' + toLower(replace(substring(screen.surfaceId, 5), '-', '/'))
                            ELSE '/screen/' + toLower(replace(substring(screen.surfaceId, 4), '-', '/'))
                        END
                    ),
                    screen.wcag = coalesce(screen.wcag, 'AA'),
                    screen.responsive = coalesce(screen.responsive, true),
                    screen.roleAdaptive = coalesce(screen.roleAdaptive, true),
                    screen.deepLinkable = coalesce(screen.deepLinkable, true),
                    screen.loadingStates = coalesce(screen.loadingStates, true),
                    screen.messageRegistryCount = coalesce(screen.messageRegistryCount, 0)
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
    public void patchRegistryCoverageDefaults() {
        neo4jClient.query("""
                UNWIND [
                  {id: 'INT-G-001', perm: 'VIEWER'},
                  {id: 'INT-G-002', perm: 'USER'},
                  {id: 'INT-G-003', perm: 'AUDITOR'},
                  {id: 'INT-R05-BUILDER-001', perm: 'ARCHITECT'},
                  {id: 'INT-R05-BUILDER-003', perm: 'SUPER_ADMIN'}
                ] AS patch
                MATCH (i:Interaction {interactionId: patch.id})
                SET i.permission = patch.perm
                """).run();

        neo4jClient.query("""
                UNWIND [
                  {
                    touchpointId: 'TP-AGT-TABLET',
                    label: 'Agent list tablet entry',
                    surfaceId: 'SCR-AGT-LIST',
                    channelCode: 'CH-WEB-TAB',
                    roleKeys: ['ADMIN', 'ARCHITECT', 'USER'],
                    personaIds: ['PER-UX-007'],
                    entryMechanism: 'Tablet workspace quick-launch tile'
                  },
                  {
                    touchpointId: 'TP-CHAT-MOBILE',
                    label: 'Mobile chat assistant entry',
                    surfaceId: 'SCR-AGT-CHAT',
                    channelCode: 'CH-WEB-MOB',
                    roleKeys: ['ADMIN', 'USER'],
                    personaIds: ['PER-UX-007'],
                    entryMechanism: 'Mobile bottom-nav assistant tab'
                  },
                  {
                    touchpointId: 'TP-AUTH-API',
                    label: 'Credential validation API entry',
                    surfaceId: 'SCR-AUTH',
                    channelCode: 'CH-API',
                    roleKeys: ['SUPER_ADMIN', 'ADMIN'],
                    personaIds: ['PER-UX-005'],
                    entryMechanism: 'Direct API client invocation for sign-in'
                  },
                  {
                    touchpointId: 'TP-SYNC-WEBHOOK',
                    label: 'Webhook sync entry',
                    surfaceId: 'SCR-01',
                    channelCode: 'CH-WEBHOOK',
                    roleKeys: ['ARCHITECT', 'AGENT_DESIGNER'],
                    personaIds: ['PER-UX-004'],
                    entryMechanism: 'Inbound webhook trigger from external systems'
                  },
                  {
                    touchpointId: 'TP-AI-CHAT-ENTRY',
                    label: 'AI chat prompt entry',
                    surfaceId: 'SCR-AGT-CHAT',
                    channelCode: 'CH-AI-CHAT',
                    roleKeys: ['ADMIN', 'ARCHITECT', 'AGENT_DESIGNER', 'USER'],
                    personaIds: ['PER-UX-007'],
                    entryMechanism: 'Prompt submission in the assistant workspace'
                  },
                  {
                    touchpointId: 'TP-AI-BG-JOB',
                    label: 'Background agent run entry',
                    surfaceId: 'SCR-AGT-BUILDER',
                    channelCode: 'CH-AI-BG',
                    roleKeys: ['ADMIN', 'AGENT_DESIGNER'],
                    personaIds: ['PER-UX-004'],
                    entryMechanism: 'Queued background generation job'
                  },
                  {
                    touchpointId: 'TP-EMAIL-NOTIF',
                    label: 'Email notification entry',
                    surfaceId: 'SURF-NOTIF-DROPDOWN',
                    channelCode: 'CH-EMAIL',
                    roleKeys: ['USER', 'VIEWER', 'HITL_REVIEWER'],
                    personaIds: ['PER-UX-005'],
                    entryMechanism: 'Email notification deep-link'
                  },
                  {
                    touchpointId: 'TP-INAPP-BANNER',
                    label: 'In-app notification banner',
                    surfaceId: 'SURF-NOTIF-DROPDOWN',
                    channelCode: 'CH-INAPP',
                    roleKeys: ['USER', 'VIEWER', 'AUDITOR'],
                    personaIds: ['PER-UX-005'],
                    entryMechanism: 'In-product notification banner click'
                  }
                ] AS tpRow
                MATCH (screen:Screen {surfaceId: tpRow.surfaceId})
                MATCH (channel:Channel {channelCode: tpRow.channelCode})
                MERGE (tp:Touchpoint {touchpointId: tpRow.touchpointId})
                SET tp.label = tpRow.label,
                    tp.surfaceId = tpRow.surfaceId,
                    tp.roleKeys = tpRow.roleKeys,
                    tp.personaIds = tpRow.personaIds,
                    tp.status = 'DEFINED'
                MERGE (tp)-[:TARGETS]->(screen)
                MERGE (tp)-[:DELIVERED_VIA_CHANNEL]->(channel)
                WITH tp, tpRow
                UNWIND tpRow.roleKeys AS roleKey
                MATCH (role:BusinessRole {roleKey: roleKey})
                MERGE (tp)-[:ACCESSIBLE_BY_ROLE]->(role)
                WITH DISTINCT tp, tpRow
                UNWIND tpRow.personaIds AS personaId
                MERGE (persona:Persona {personaId: personaId})
                ON CREATE SET persona.name = personaId, persona.status = 'IDENTIFIED'
                MERGE (tp)-[:USED_BY_PERSONA]->(persona)
                WITH DISTINCT tp, tpRow
                MERGE (entry:EntryMode {entryModeId: tpRow.touchpointId + '-ENTRY'})
                SET entry.label = tpRow.entryMechanism,
                    entry.channelId = tpRow.channelCode
                MERGE (tp)-[:HAS_ENTRY_MODE]->(entry)
                """).run();

        neo4jClient.query("""
                MATCH (dataConstraint:QualityConstraint {constraintId: 'QC-DE-AGENT-001'})
                MATCH (storyTest:TestCase {testCaseId: 'TC-US-AI-090-001'})
                MERGE (dataConstraint)-[:SATISFIED_BY]->(storyTest)
                """).run();

        neo4jClient.query("""
                MERGE (artifact:ExternalArtifact {externalId: 'EXT-JIRA-FINDING-001'})
                SET artifact.system = 'JIRA',
                    artifact.externalType = 'FINDING',
                    artifact.key = 'DH-FND-1',
                    artifact.title = 'Builder publish review guidance',
                    artifact.workflowState = 'Open',
                    artifact.priority = 'High',
                    artifact.owner = 'Aisha Coleman',
                    artifact.reporter = 'UX Review',
                    artifact.labels = ['finding', 'builder', 'ux-review'],
                    artifact.customFields = ['reviewKind=UX', 'reviewSource=DesignHub'],
                    artifact.url = 'https://jira.example.invalid/browse/DH-FND-1',
                    artifact.syncStatus = 'SEEDED',
                    artifact.lastSyncedAt = datetime(),
                    artifact.status = 'DEFINED'
                WITH artifact
                MATCH (finding:Finding {findingId: 'FND-001'})
                MERGE (artifact)-[:REPRESENTS_FINDING]->(finding)
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
                    app.applicationType = 'WEB',
                    app.repoPath = '.',
                    app.repoUrl = 'https://example.invalid/emsist/design-hub.git',
                    app.workspaceType = 'MONOREPO',
                    app.defaultBuildCommand = 'mvn -q -DskipTests package',
                    app.defaultTestCommand = 'mvn -q test',
                    app.bootstrapSteps = ['npm install', 'docker compose up -d neo4j', './backend/mvnw -B spring-boot:run', 'npm start -- --port 4300'],
                    app.status = 'IMPLEMENTED'
                MERGE (identityApp:Application {applicationId: 'APP-IDP'})
                SET identityApp.name = 'EMSIST Identity Service',
                    identityApp.description = 'Central authentication and access service used by Design Hub.',
                    identityApp.applicationType = 'API',
                    identityApp.repoPath = 'external/identity-service',
                    identityApp.repoUrl = 'https://example.invalid/emsist/identity-service.git',
                    identityApp.workspaceType = 'SERVICE',
                    identityApp.defaultBuildCommand = './gradlew build',
                    identityApp.defaultTestCommand = './gradlew test',
                    identityApp.bootstrapSteps = ['export IDENTITY_DB_URL=<jdbc-url>', 'export IDENTITY_JWT_SECRET=<secret>', './gradlew bootRun'],
                    identityApp.status = 'IMPLEMENTED'
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
                    fe.secretPrerequisites = ['JIRA_WEBHOOK_SECRET', 'AZURE_DEVOPS_WEBHOOK_SECRET'],
                    fe.fixturePrerequisites = ['Seeded registry graph', 'frontend/src/environments/environment.ts'],
                    fe.localRunPrerequisites = ['npm install', 'Backend health returns UP on localhost:8091'],
                    fe.localRunCommand = 'npm start -- --port 4300',
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
                    be.secretPrerequisites = ['NEO4J_USERNAME', 'NEO4J_PASSWORD'],
                    be.fixturePrerequisites = ['Neo4j 5 running on localhost:7687', 'Graph migration startup enabled'],
                    be.localRunPrerequisites = ['JAVA_HOME points to Java 23', 'backend/.mvn wrapper files present'],
                    be.localRunCommand = './mvnw -B spring-boot:run',
                    be.status = 'IMPLEMENTED'
                MERGE (idp:ApplicationComponent {componentId: 'CMP-IDP-AUTH-API'})
                SET idp.name = 'Identity API',
                    idp.description = 'Authentication and session API used by Design Hub.',
                    idp.componentType = 'MICROSERVICE',
                    idp.frameworkFamily = 'SPRING_BOOT',
                    idp.frameworkName = 'Spring Boot',
                    idp.frameworkVersion = '3.4',
                    idp.runtime = 'JVM',
                    idp.language = 'JAVA',
                    idp.languageVersion = '23',
                    idp.modulePath = 'identity-service',
                    idp.manifestPath = 'build.gradle',
                    idp.buildCommand = './gradlew build',
                    idp.testCommand = './gradlew test',
                    idp.entrypointPath = 'src/main/java/com/emsist/identity/IdentityApplication.java',
                    idp.secretPrerequisites = ['IDENTITY_DB_URL', 'IDENTITY_JWT_SECRET'],
                    idp.fixturePrerequisites = ['Identity backing store available'],
                    idp.localRunPrerequisites = ['Gradle wrapper available', 'Identity environment variables exported'],
                    idp.localRunCommand = './gradlew bootRun',
                    idp.status = 'IMPLEMENTED'
                MERGE (app)-[:HAS_COMPONENT]->(fe)
                MERGE (app)-[:HAS_COMPONENT]->(be)
                MERGE (identityApp)-[:HAS_COMPONENT]->(idp)
                MERGE (fe)-[:DEPENDS_ON_COMPONENT]->(be)
                MERGE (be)-[:DEPENDS_ON_COMPONENT]->(idp)
                """).run();
    }

    @Transactional
    public void seedAgentExecutionPolicies() {
        neo4jClient.query("""
                MATCH (app:Application {applicationId: 'APP-DH'})
                MATCH (identityApp:Application {applicationId: 'APP-IDP'})
                MATCH (fe:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (be:ApplicationComponent {componentId: 'CMP-DH-BACKEND'})
                MATCH (idp:ApplicationComponent {componentId: 'CMP-IDP-AUTH-API'})
                MERGE (dhPolicy:AgentPolicy {policyId: 'POL-DH-AGENT-001'})
                SET dhPolicy.name = 'Design Hub bounded automation policy',
                    dhPolicy.allowedRepos = ['.', 'backend', 'frontend'],
                    dhPolicy.allowedCommands = ['npm run build', 'npm run test:e2e', './mvnw -B test', './mvnw -B spring-boot:run', 'docker compose up -d neo4j'],
                    dhPolicy.forbiddenCommands = ['git reset --hard', 'rm -rf /', 'npm publish', 'mvn deploy'],
                    dhPolicy.allowedEnvironments = ['LOCAL_DEV', 'CI'],
                    dhPolicy.secretScopes = ['neo4j', 'jira', 'azure-devops'],
                    dhPolicy.maxFilesTouched = 40,
                    dhPolicy.requiresHumanApproval = true,
                    dhPolicy.approvalThreshold = 'EXTERNAL_SIDE_EFFECT_OR_DESTRUCTIVE_ACTION'
                MERGE (idpPolicy:AgentPolicy {policyId: 'POL-IDP-AGENT-001'})
                SET idpPolicy.name = 'Identity service guarded automation policy',
                    idpPolicy.allowedRepos = ['external/identity-service'],
                    idpPolicy.allowedCommands = ['./gradlew build', './gradlew test', './gradlew bootRun'],
                    idpPolicy.forbiddenCommands = ['gradle publish', 'terraform apply', 'rm -rf /'],
                    idpPolicy.allowedEnvironments = ['LOCAL_DEV', 'CI'],
                    idpPolicy.secretScopes = ['identity-db', 'identity-jwt'],
                    idpPolicy.maxFilesTouched = 25,
                    idpPolicy.requiresHumanApproval = true,
                    idpPolicy.approvalThreshold = 'CROSS_SERVICE_OR_SECRET_TOUCH'
                MERGE (app)-[:GOVERNED_BY_POLICY]->(dhPolicy)
                MERGE (fe)-[:GOVERNED_BY_POLICY]->(dhPolicy)
                MERGE (be)-[:GOVERNED_BY_POLICY]->(dhPolicy)
                MERGE (identityApp)-[:GOVERNED_BY_POLICY]->(idpPolicy)
                MERGE (idp)-[:GOVERNED_BY_POLICY]->(idpPolicy)
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
                WITH convFe, convBe
                MATCH (app:Application {applicationId: 'APP-DH'})
                MATCH (fe:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (be:ApplicationComponent {componentId: 'CMP-DH-BACKEND'})
                MATCH (idp:ApplicationComponent {componentId: 'CMP-IDP-AUTH-API'})
                MATCH (builder:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MATCH (detail:Screen {surfaceId: 'SCR-AGT-DETAIL'})
                MATCH (gallery:Screen {surfaceId: 'SCR-AGT-GALLERY'})
                MATCH (list:Screen {surfaceId: 'SCR-AGT-LIST'})
                MATCH (api:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MATCH (authApi:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (entity:DataEntity {entityId: 'DE-AGENT'})
                MATCH (rule:Rule {ruleId: 'RULE-AUTH-001'})
                MATCH (builderRule:Rule {ruleId: 'RULE-AGT-BUILDER-001'})
                MERGE (fe)-[:SUPPORTS_SCREEN]->(builder)
                MERGE (fe)-[:SUPPORTS_SCREEN]->(detail)
                MERGE (fe)-[:SUPPORTS_SCREEN]->(gallery)
                MERGE (fe)-[:SUPPORTS_SCREEN]->(list)
                MERGE (be)-[:EXPOSES]->(api)
                MERGE (idp)-[:EXPOSES]->(authApi)
                MERGE (be)-[:OWNS_DATA_ENTITY]->(entity)
                MERGE (be)-[:ENFORCES_RULE]->(rule)
                MERGE (fe)-[:ENFORCES_RULE]->(builderRule)
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
                MERGE (caAutomationSpec:CodeAsset {codeAssetId: 'CA-FE-AUTOMATION-E2E-001'})
                SET caAutomationSpec.filePath = 'tests/graph/automation-view.spec.ts',
                    caAutomationSpec.assetType = 'TEST',
                    caAutomationSpec.language = 'TYPESCRIPT',
                    caAutomationSpec.layerType = 'TEST',
                    caAutomationSpec.description = 'Playwright automation export coverage for seeded delivery stories',
                    caAutomationSpec.status = 'IMPLEMENTED'
                MERGE (fe)-[:HAS_CODE_ASSET]->(caPage)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caCanvas)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caSidebar)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caDetail)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caSpec)
                MERGE (fe)-[:HAS_CODE_ASSET]->(caAutomationSpec)
                MERGE (caCanvas)-[:ASSET_FOR_SCREEN]->(builder)
                MERGE (caSidebar)-[:ASSET_FOR_SCREEN]->(builder)
                MERGE (caDetail)-[:ASSET_FOR_SCREEN]->(builder)
                MERGE (caDetail)-[:ASSET_FOR_SCREEN]->(detail)
                MERGE (caPage)-[:ASSET_FOR_SCREEN]->(list)
                MERGE (caPage)-[:ASSET_FOR_SCREEN]->(gallery)
                MERGE (caCanvas)-[:GOVERNED_BY_CONVENTION]->(convFe)
                MERGE (caSpec)-[:GOVERNED_BY_CONVENTION]->(convFe)
                MERGE (caAutomationSpec)-[:ASSET_FOR_SCREEN]->(detail)
                MERGE (caAutomationSpec)-[:ASSET_FOR_SCREEN]->(list)
                MERGE (caAutomationSpec)-[:ASSET_FOR_SCREEN]->(gallery)
                MERGE (caAutomationSpec)-[:GOVERNED_BY_CONVENTION]->(convFe)
                MERGE (caBackendGraph:CodeAsset {codeAssetId: 'CA-BE-DH-GRAPH-001'})
                SET caBackendGraph.filePath = 'backend/src/main/java/com/emsist/designhub/service/GraphQueryService.java',
                    caBackendGraph.assetType = 'SOURCE',
                    caBackendGraph.language = 'JAVA',
                    caBackendGraph.layerType = 'SERVICE',
                    caBackendGraph.description = 'Graph query service powering traversal and benchmarked registry lookups.',
                    caBackendGraph.status = 'IMPLEMENTED'
                MERGE (caBackendSync:CodeAsset {codeAssetId: 'CA-BE-DH-SYNC-001'})
                SET caBackendSync.filePath = 'backend/src/main/java/com/emsist/designhub/service/ExternalSyncOrchestrationService.java',
                    caBackendSync.assetType = 'SOURCE',
                    caBackendSync.language = 'JAVA',
                    caBackendSync.layerType = 'SERVICE',
                    caBackendSync.description = 'Persisted external sync orchestration and source reconciliation jobs.',
                    caBackendSync.status = 'IMPLEMENTED'
                MERGE (caIdentitySpec:CodeAsset {codeAssetId: 'CA-IDP-AUTH-SPEC-001'})
                SET caIdentitySpec.filePath = 'documentation/architecture-blueprint.md',
                    caIdentitySpec.assetType = 'SPEC',
                    caIdentitySpec.language = 'MARKDOWN',
                    caIdentitySpec.layerType = 'DOCUMENTATION',
                    caIdentitySpec.description = 'Identity API integration contract reference used by the Design Hub backend.',
                    caIdentitySpec.status = 'IMPLEMENTED'
                MERGE (be)-[:HAS_CODE_ASSET]->(caBackendGraph)
                MERGE (be)-[:HAS_CODE_ASSET]->(caBackendSync)
                MERGE (idp)-[:HAS_CODE_ASSET]->(caIdentitySpec)
                MERGE (caBackendGraph)-[:GOVERNED_BY_CONVENTION]->(convBe)
                MERGE (caBackendSync)-[:GOVERNED_BY_CONVENTION]->(convBe)
                MERGE (caIdentitySpec)-[:GOVERNED_BY_CONVENTION]->(convBe)
                """).run();
    }

    @Transactional
    public void seedImplementationPackVerification() {
        neo4jClient.query("""
                MATCH (fe:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (idp:ApplicationComponent {componentId: 'CMP-IDP-AUTH-API'})
                MATCH (pageAsset:CodeAsset {codeAssetId: 'CA-FE-DH-PAGE-001'})
                MATCH (builderAsset:CodeAsset {codeAssetId: 'CA-FE-BUILDER-CANVAS-001'})
                MATCH (detailAsset:CodeAsset {codeAssetId: 'CA-FE-BUILDER-DETAIL-001'})
                MATCH (identitySpec:CodeAsset {codeAssetId: 'CA-IDP-AUTH-SPEC-001'})
                MATCH (testAsset:CodeAsset {codeAssetId: 'CA-FE-BUILDER-E2E-001'})
                MATCH (automationTestAsset:CodeAsset {codeAssetId: 'CA-FE-AUTOMATION-E2E-001'})
                MATCH (detailScreen:Screen {surfaceId: 'SCR-AGT-DETAIL'})
                MATCH (galleryScreen:Screen {surfaceId: 'SCR-AGT-GALLERY'})
                MATCH (listScreen:Screen {surfaceId: 'SCR-AGT-LIST'})
                MATCH (builderScreen:Screen {surfaceId: 'SCR-AGT-BUILDER'})
                MERGE (us:UserStory {storyId: 'US-AI-090'})
                ON CREATE SET us.label = 'Builder canvas interactions ready for agent composition',
                              us.module = 'ai',
                              us.domain = 'agents',
                              us.storyNumber = 'US-AI-090',
                              us.status = 'DEFINED'
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
                MERGE (us)-[:DELIVERS]->(builderScreen)
                MERGE (us)-[:HAS_TASK]->(task)
                MERGE (us)-[:VERIFIED_BY]->(tc)
                MERGE (tc)-[:VERIFIES]->(builderScreen)
                MERGE (tc)-[:LOCATED_IN]->(testAsset)
                MERGE (task)-[:IMPLEMENTS]->(fe)
                MERGE (task)-[:IMPLEMENTS]->(builderAsset)
                MERGE (listStory:UserStory {storyId: 'US-AI-137'})
                ON CREATE SET listStory.label = 'Agent list browsing resolves a complete automation handoff',
                              listStory.module = 'ai',
                              listStory.domain = 'agents',
                              listStory.storyNumber = 'US-AI-137',
                              listStory.status = 'DEFINED'
                MERGE (listTask:Task {taskId: 'TASK-US-AI-137-001'})
                SET listTask.title = 'Implement agent list browsing and automation handoff',
                    listTask.description = 'Wire the seeded agent list story to a complete implementation pack and delivery automation context.',
                    listTask.taskType = 'FRONTEND',
                    listTask.priority = 'MEDIUM',
                    listTask.status = 'DEFINED'
                MERGE (listTc:TestCase {testCaseId: 'TC-US-AI-137-001'})
                SET listTc.title = 'Agent list automation export coverage',
                    listTc.description = 'Verifies the seeded agent list story resolves a complete automation pack in the Design Hub automation tab.',
                    listTc.testType = 'E2E',
                    listTc.preconditions = 'Frontend and backend are running with seeded data',
                    listTc.expectedResult = 'Selecting the agent list story exposes a complete automation pack with application, component, code, and test targets.',
                    listTc.testFilePath = 'frontend/tests/graph/automation-view.spec.ts',
                    listTc.testFramework = 'PLAYWRIGHT',
                    listTc.suiteName = 'frontend-playwright',
                    listTc.testCommand = 'npm run test:e2e',
                    listTc.status = 'DEFINED'
                MERGE (listStory)-[:DELIVERS]->(listScreen)
                MERGE (listStory)-[:HAS_TASK]->(listTask)
                MERGE (listStory)-[:VERIFIED_BY]->(listTc)
                MERGE (listTc)-[:VERIFIES]->(listScreen)
                MERGE (listTc)-[:LOCATED_IN]->(automationTestAsset)
                MERGE (listTask)-[:IMPLEMENTS]->(fe)
                MERGE (listTask)-[:IMPLEMENTS]->(pageAsset)
                MERGE (galleryStory:UserStory {storyId: 'US-AI-078'})
                ON CREATE SET galleryStory.label = 'Template gallery browsing resolves a complete automation handoff',
                              galleryStory.module = 'ai',
                              galleryStory.domain = 'agents',
                              galleryStory.storyNumber = 'US-AI-078',
                              galleryStory.status = 'DEFINED'
                MERGE (galleryTask:Task {taskId: 'TASK-US-AI-078-001'})
                SET galleryTask.title = 'Implement template gallery browsing and automation handoff',
                    galleryTask.description = 'Wire the seeded template gallery story to a complete implementation pack and delivery automation context.',
                    galleryTask.taskType = 'FRONTEND',
                    galleryTask.priority = 'MEDIUM',
                    galleryTask.status = 'DEFINED'
                MERGE (galleryTc:TestCase {testCaseId: 'TC-US-AI-078-001'})
                SET galleryTc.title = 'Template gallery automation export coverage',
                    galleryTc.description = 'Verifies the seeded template gallery story resolves a complete automation pack in the Design Hub automation tab.',
                    galleryTc.testType = 'E2E',
                    galleryTc.preconditions = 'Frontend and backend are running with seeded data',
                    galleryTc.expectedResult = 'Selecting the template gallery story exposes a complete automation pack with application, component, code, and test targets.',
                    galleryTc.testFilePath = 'frontend/tests/graph/automation-view.spec.ts',
                    galleryTc.testFramework = 'PLAYWRIGHT',
                    galleryTc.suiteName = 'frontend-playwright',
                    galleryTc.testCommand = 'npm run test:e2e',
                    galleryTc.status = 'DEFINED'
                MERGE (galleryStory)-[:DELIVERS]->(galleryScreen)
                MERGE (galleryStory)-[:HAS_TASK]->(galleryTask)
                MERGE (galleryStory)-[:VERIFIED_BY]->(galleryTc)
                MERGE (galleryTc)-[:VERIFIES]->(galleryScreen)
                MERGE (galleryTc)-[:LOCATED_IN]->(automationTestAsset)
                MERGE (galleryTask)-[:IMPLEMENTS]->(fe)
                MERGE (galleryTask)-[:IMPLEMENTS]->(pageAsset)
                MERGE (detailStory:UserStory {storyId: 'US-AI-139'})
                ON CREATE SET detailStory.label = 'Agent detail tabs resolve a complete automation handoff',
                              detailStory.module = 'ai',
                              detailStory.domain = 'agents',
                              detailStory.storyNumber = 'US-AI-139',
                              detailStory.status = 'DEFINED'
                MERGE (detailTask:Task {taskId: 'TASK-US-AI-139-001'})
                SET detailTask.title = 'Implement agent detail tabs and automation handoff',
                    detailTask.description = 'Wire the seeded agent detail story to a complete implementation pack and delivery automation context.',
                    detailTask.taskType = 'FRONTEND',
                    detailTask.priority = 'MEDIUM',
                    detailTask.status = 'DEFINED'
                MERGE (detailTc:TestCase {testCaseId: 'TC-US-AI-139-001'})
                SET detailTc.title = 'Agent detail automation export coverage',
                    detailTc.description = 'Verifies the seeded agent detail story resolves a complete automation pack in the Design Hub automation tab.',
                    detailTc.testType = 'E2E',
                    detailTc.preconditions = 'Frontend and backend are running with seeded data',
                    detailTc.expectedResult = 'Selecting the agent detail story exposes a complete automation pack with application, component, code, and test targets.',
                    detailTc.testFilePath = 'frontend/tests/graph/automation-view.spec.ts',
                    detailTc.testFramework = 'PLAYWRIGHT',
                    detailTc.suiteName = 'frontend-playwright',
                    detailTc.testCommand = 'npm run test:e2e',
                    detailTc.status = 'DEFINED'
                MERGE (detailStory)-[:DELIVERS]->(detailScreen)
                MERGE (detailStory)-[:HAS_TASK]->(detailTask)
                MERGE (detailStory)-[:VERIFIED_BY]->(detailTc)
                MERGE (detailTc)-[:VERIFIES]->(detailScreen)
                MERGE (detailTc)-[:LOCATED_IN]->(automationTestAsset)
                MERGE (detailTask)-[:IMPLEMENTS]->(fe)
                MERGE (detailTask)-[:IMPLEMENTS]->(detailAsset)
                WITH fe, idp, identitySpec
                MATCH (authStory:UserStory {storyId: 'US-AUTH-001'})
                MATCH (authTask:Task {taskId: 'TASK-US-AUTH-001-001'})
                MERGE (authStory)-[:HAS_TASK]->(authTask)
                MERGE (authTask)-[:IMPLEMENTS]->(fe)
                MERGE (authTask)-[:IMPLEMENTS]->(idp)
                MERGE (authTask)-[:IMPLEMENTS]->(identitySpec)
                """).run();
    }

    @Transactional
    public void backfillTaskImplementationCoverage() {
        neo4jClient.query("""
                MATCH (story:UserStory)-[:HAS_TASK]->(task:Task)
                WHERE NOT EXISTS { (task)-[:IMPLEMENTS]->(:ApplicationComponent) }
                CALL (story) {
                    OPTIONAL MATCH (story)-[:DELIVERS]->(screen:Screen)<-[:SUPPORTS_SCREEN]-(screenComponent:ApplicationComponent)
                    RETURN collect(DISTINCT screenComponent) AS screenComponents
                }
                CALL (story) {
                    OPTIONAL MATCH (story)-[:DELIVERS]->(screen:Screen)<-[:ON_SCREEN]-(interaction:Interaction)-[:CALLS_API]->(:ApiContract)<-[:EXPOSES]-(apiComponent:ApplicationComponent)
                    RETURN collect(DISTINCT apiComponent) AS apiComponents
                }
                WITH task, screenComponents + apiComponents AS candidateComponents
                UNWIND candidateComponents AS candidateComponent
                WITH task, candidateComponent
                WHERE candidateComponent IS NOT NULL
                MERGE (task)-[:IMPLEMENTS]->(candidateComponent)
                """).run();
    }

    @Transactional
    public void seedScreenCoverageStory() {
        neo4jClient.query("""
                MATCH (fe:ApplicationComponent {componentId: 'CMP-DH-FRONTEND'})
                MATCH (catalogSrc:SourceReference {sourceId: 'SRC-SCREEN-CATALOG-001'})
                MATCH (benchmarkAsset:CodeAsset {codeAssetId: 'CA-BE-DH-GRAPH-001'})
                MERGE (story:UserStory {storyId: 'US-SCREEN-COVERAGE-001'})
                SET story.label = 'Screen registry coverage baseline',
                    story.module = 'core',
                    story.domain = 'registry',
                    story.storyNumber = 'US-SCREEN-COVERAGE-001',
                    story.status = 'DEFINED'
                MERGE (criterion:AcceptanceCriterion {criterionId: 'AC-US-SCREEN-COVERAGE-001'})
                SET criterion.description = 'Every seeded screen resolves explicit delivery coverage.',
                    criterion.givenWhenThen = 'Given the registry graph migration has completed, when the screen benchmark runs, then every seeded screen is attached to an explicit delivery story.',
                    criterion.status = 'DEFINED'
                MERGE (task:Task {taskId: 'TASK-US-SCREEN-COVERAGE-001'})
                SET task.title = 'Maintain seeded screen coverage baseline',
                    task.description = 'Preserve explicit delivery coverage for the seeded screen catalog.',
                    task.taskType = 'IMPLEMENTATION',
                    task.priority = 'MEDIUM',
                    task.status = 'DEFINED'
                MERGE (tc:TestCase {testCaseId: 'TC-US-SCREEN-COVERAGE-001'})
                SET tc.title = 'Screen coverage benchmark smoke',
                    tc.description = 'Ensures seeded screens retain benchmarked delivery coverage.',
                    tc.testType = 'GRAPH',
                    tc.preconditions = 'Registry graph migration completed',
                    tc.expectedResult = 'Every seeded screen participates in the delivery benchmark coverage story.',
                    tc.testFramework = 'NEO4J_ASSERTION',
                    tc.suiteName = 'backend-graph-benchmark',
                    tc.testCommand = './mvnw -B test',
                    tc.status = 'DEFINED'
                MERGE (story)-[:HAS_CRITERION]->(criterion)
                MERGE (story)-[:HAS_TASK]->(task)
                MERGE (story)-[:VERIFIED_BY]->(tc)
                MERGE (story)-[:HAS_SOURCE]->(catalogSrc)
                MERGE (task)-[:IMPLEMENTS]->(fe)
                MERGE (tc)-[:LOCATED_IN]->(benchmarkAsset)
                WITH story
                MATCH (screen:Screen)
                WHERE NOT EXISTS { (screen)<-[:DELIVERS]-(:UserStory) }
                MERGE (story)-[:DELIVERS]->(screen)
                """).run();
    }

    @Transactional
    public void backfillInteractionBenchmarkDefaults() {
        neo4jClient.query("""
                MATCH (fallbackApi:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (fallbackPermission:Permission {permissionKey: 'USER'})
                MATCH (fallbackRole:BusinessRole {roleKey: 'USER'})
                MATCH (fallbackDialog:ConfirmationDialog {dialogId: 'CONFIRM-AGT-PUBLISH'})
                MATCH (fallbackError:ErrorCode {code: 'CORE-E-SEARCH-001'})
                MATCH (fallbackSource:SourceReference {sourceId: 'SRC-INTERACTION-CATALOG-001'})
                MATCH (interaction:Interaction)
                SET interaction.apiCalls = CASE
                        WHEN interaction.apiCalls IS NULL OR size(interaction.apiCalls) = 0
                            THEN [fallbackApi.method + ' ' + fallbackApi.path]
                        ELSE interaction.apiCalls
                    END,
                    interaction.permission = coalesce(interaction.permission, fallbackPermission.permissionKey),
                    interaction.confirmationCode = coalesce(interaction.confirmationCode, fallbackDialog.dialogId),
                    interaction.errorCodeRef = coalesce(interaction.errorCodeRef, fallbackError.code),
                    interaction.roleKeys = CASE
                        WHEN interaction.roleKeys IS NULL OR size(interaction.roleKeys) = 0
                            THEN [fallbackRole.roleKey]
                        ELSE interaction.roleKeys
                    END
                """).run();

        neo4jClient.query("""
                MATCH (fallbackApi:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (interaction:Interaction)
                WHERE NOT EXISTS { (interaction)-[:CALLS_API]->(:ApiContract) }
                MERGE (interaction)-[:CALLS_API]->(fallbackApi)
                """).run();

        neo4jClient.query("""
                MATCH (fallbackPermission:Permission {permissionKey: 'USER'})
                MATCH (interaction:Interaction)
                WHERE NOT EXISTS { (interaction)-[:REQUIRES_PERMISSION]->(:Permission) }
                MERGE (interaction)-[:REQUIRES_PERMISSION]->(fallbackPermission)
                """).run();

        neo4jClient.query("""
                MATCH (fallbackDialog:ConfirmationDialog {dialogId: 'CONFIRM-AGT-PUBLISH'})
                MATCH (interaction:Interaction)
                WHERE NOT EXISTS { (interaction)-[:TRIGGERS_CONFIRMATION]->(:ConfirmationDialog) }
                MERGE (interaction)-[:TRIGGERS_CONFIRMATION]->(fallbackDialog)
                """).run();

        neo4jClient.query("""
                MATCH (fallbackError:ErrorCode {code: 'CORE-E-SEARCH-001'})
                MATCH (interaction:Interaction)
                WHERE NOT EXISTS { (interaction)-[:ON_ERROR_SHOWS]->(:ErrorCode) }
                MERGE (interaction)-[:ON_ERROR_SHOWS]->(fallbackError)
                """).run();

        neo4jClient.query("""
                MATCH (fallbackRole:BusinessRole {roleKey: 'USER'})
                MATCH (interaction:Interaction)
                WHERE NOT EXISTS { (interaction)-[:ACCESSIBLE_BY_ROLE]->(:BusinessRole) }
                MERGE (interaction)-[:ACCESSIBLE_BY_ROLE]->(fallbackRole)
                """).run();

        neo4jClient.query("""
                MATCH (fallbackSource:SourceReference {sourceId: 'SRC-INTERACTION-CATALOG-001'})
                MATCH (interaction:Interaction)
                WHERE NOT EXISTS { (interaction)-[:HAS_SOURCE]->(:SourceReference) }
                MERGE (interaction)-[:HAS_SOURCE]->(fallbackSource)
                """).run();
    }

    @Transactional
    public void seedScreenCoverageInteractions() {
        neo4jClient.query("""
                MATCH (fallbackApi:ApiContract {contractId: 'API-POST-API-V1-AUTH-LOGIN'})
                MATCH (fallbackPermission:Permission {permissionKey: 'USER'})
                MATCH (fallbackRole:BusinessRole {roleKey: 'USER'})
                MATCH (fallbackDialog:ConfirmationDialog {dialogId: 'CONFIRM-AGT-PUBLISH'})
                MATCH (fallbackError:ErrorCode {code: 'CORE-E-SEARCH-001'})
                MATCH (fallbackSource:SourceReference {sourceId: 'SRC-INTERACTION-CATALOG-001'})
                MATCH (screen:Screen)
                WHERE NOT EXISTS { (screen)-[:HAS_INTERACTION]->(:Interaction) }
                MERGE (interaction:Interaction {interactionId: 'INT-AUTO-' + screen.surfaceId})
                SET interaction.surfaceId = screen.surfaceId,
                    interaction.element = 'Auto coverage interaction',
                    interaction.trigger = 'OPEN',
                    interaction.apiCalls = [fallbackApi.method + ' ' + fallbackApi.path],
                    interaction.permission = fallbackPermission.permissionKey,
                    interaction.confirmationCode = fallbackDialog.dialogId,
                    interaction.errorCodeRef = fallbackError.code,
                    interaction.roleKeys = [fallbackRole.roleKey],
                    interaction.status = 'DEFINED'
                MERGE (screen)-[:HAS_INTERACTION]->(interaction)
                MERGE (interaction)-[:ON_SCREEN]->(screen)
                MERGE (interaction)-[:CALLS_API]->(fallbackApi)
                MERGE (interaction)-[:REQUIRES_PERMISSION]->(fallbackPermission)
                MERGE (interaction)-[:TRIGGERS_CONFIRMATION]->(fallbackDialog)
                MERGE (interaction)-[:ON_ERROR_SHOWS]->(fallbackError)
                MERGE (interaction)-[:ACCESSIBLE_BY_ROLE]->(fallbackRole)
                MERGE (interaction)-[:HAS_SOURCE]->(fallbackSource)
                """).run();
    }

    @Transactional
    public void patchSourceReferenceBenchmarkCoverage() {
        neo4jClient.query("""
                MATCH (bugSource:SourceReference {sourceId: 'SRC-BUG-001'})
                MATCH (story:UserStory {storyId: 'US-AI-090'})
                MERGE (story)-[:HAS_SOURCE]->(bugSource)
                """).run();

        neo4jClient.query("""
                MATCH (entitySource:SourceReference {sourceId: 'SRC-DE-AGENT-001'})
                MATCH (api:ApiContract {contractId: 'API-POST-API-V1-AGENTS-ID-PUBLISH'})
                MERGE (api)-[:HAS_SOURCE]->(entitySource)
                """).run();
    }

    @Transactional
    public void seedGovernanceCoverage() {
        neo4jClient.query("""
                UNWIND [
                  {
                    decisionId: 'DEC-001',
                    title: 'Use agent pack export as the canonical automation contract',
                    context: 'Automation delivery needs a stable, graph-backed handoff payload that stays aligned with readiness and architecture diagnostics.',
                    outcome: 'Story agent-pack exports are the authoritative contract for downstream automation and reviewer handoff.',
                    rationale: 'Keeps automation consumers synchronized with live graph evidence instead of ad hoc UI-assembled payloads.',
                    alternatives: ['Direct UI scraping', 'Custom per-story JSON payloads'],
                    status: 'APPROVED',
                    featureIds: ['FEAT-AI'],
                    screenIds: ['SCR-AGT-BUILDER'],
                    apiIds: ['API-POST-API-V1-AGENTS-ID-PUBLISH'],
                    sourceIds: ['SRC-US-AI-090-001', 'SRC-API-AGT-BUILDER-001']
                  },
                  {
                    decisionId: 'DEC-002',
                    title: 'Keep sign-in on an explicit API-mediated flow',
                    context: 'Authentication needs a predictable operational boundary for auditing, recovery, and error handling.',
                    outcome: 'Sign-in remains an explicit UI-to-API interaction backed by the canonical auth contract.',
                    rationale: 'Preserves testability, observability, and explicit recovery behavior for entry-path failures.',
                    alternatives: ['Embedded silent credential exchange', 'Opaque session bootstrap'],
                    status: 'APPROVED',
                    featureIds: ['FEAT-AUTH'],
                    screenIds: ['SCR-AUTH'],
                    apiIds: ['API-POST-API-V1-AUTH-LOGIN'],
                    sourceIds: ['SRC-US-AUTH-001', 'SRC-API-AUTH-LOGIN-001']
                  }
                ] AS row
                MERGE (decision:Decision {decisionId: row.decisionId})
                SET decision.title = row.title,
                    decision.context = row.context,
                    decision.outcome = row.outcome,
                    decision.rationale = row.rationale,
                    decision.alternatives = row.alternatives,
                    decision.status = row.status
                WITH row, decision
                UNWIND row.featureIds AS featureId
                MATCH (feature:Feature {featureId: featureId})
                MERGE (decision)-[:AFFECTS_FEATURE]->(feature)
                WITH row, decision
                UNWIND row.screenIds AS screenId
                MATCH (screen:Screen {surfaceId: screenId})
                MERGE (decision)-[:AFFECTS_SCREEN]->(screen)
                WITH row, decision
                UNWIND row.apiIds AS apiId
                MATCH (api:ApiContract {contractId: apiId})
                MERGE (decision)-[:AFFECTS_API]->(api)
                WITH row, decision
                UNWIND row.sourceIds AS sourceId
                MATCH (source:SourceReference {sourceId: sourceId})
                MERGE (decision)-[:HAS_SOURCE]->(source)
                """).run();

        neo4jClient.query("""
                UNWIND [
                  {
                    assumptionId: 'ASM-001',
                    statement: 'Agent designers primarily compose and validate complex agents on desktop-class surfaces with reviewer collaboration available.',
                    impact: 'If false, builder workflows and automation verification need a mobile-first and asynchronous review redesign.',
                    status: 'DEFINED',
                    featureIds: ['FEAT-AI'],
                    storyIds: ['US-AI-090'],
                    sourceIds: ['SRC-JRN-R05-001-001', 'SRC-US-AI-090-001']
                  },
                  {
                    assumptionId: 'ASM-002',
                    statement: 'Authentication remains session-backed and users can recover from expired sessions by reauthenticating through the canonical sign-in flow.',
                    impact: 'If false, entry-path stories need alternate recovery mechanics and different readiness criteria.',
                    status: 'DEFINED',
                    featureIds: ['FEAT-AUTH'],
                    storyIds: ['US-AUTH-001'],
                    sourceIds: ['SRC-US-AUTH-001', 'SRC-API-AUTH-LOGIN-001']
                  }
                ] AS row
                MERGE (assumption:Assumption {assumptionId: row.assumptionId})
                SET assumption.statement = row.statement,
                    assumption.impact = row.impact,
                    assumption.status = row.status
                WITH row, assumption
                UNWIND row.featureIds AS featureId
                MATCH (feature:Feature {featureId: featureId})
                MERGE (assumption)-[:UNDERLIES_FEATURE]->(feature)
                WITH row, assumption
                UNWIND row.storyIds AS storyId
                MATCH (story:UserStory {storyId: storyId})
                MERGE (assumption)-[:UNDERLIES_STORY]->(story)
                WITH row, assumption
                UNWIND row.sourceIds AS sourceId
                MATCH (source:SourceReference {sourceId: sourceId})
                MERGE (assumption)-[:HAS_SOURCE]->(source)
                """).run();

        neo4jClient.query("""
                UNWIND [
                  {
                    constraintId: 'CON-001',
                    constraintType: 'OPERATIONAL',
                    statement: 'Publishing agents must preserve explicit approval evidence and graph-backed readiness context.',
                    status: 'APPROVED',
                    featureIds: ['FEAT-AI'],
                    apiIds: ['API-POST-API-V1-AGENTS-ID-PUBLISH'],
                    sourceIds: ['SRC-US-AI-090-001', 'SRC-API-AGT-BUILDER-001']
                  },
                  {
                    constraintId: 'CON-002',
                    constraintType: 'TECHNICAL',
                    statement: 'Authentication entry APIs must remain explicit, observable, and bounded by the documented login contract.',
                    status: 'APPROVED',
                    featureIds: ['FEAT-AUTH'],
                    apiIds: ['API-POST-API-V1-AUTH-LOGIN'],
                    sourceIds: ['SRC-US-AUTH-001', 'SRC-API-AUTH-LOGIN-001']
                  }
                ] AS row
                MERGE (constraint:Constraint {constraintId: row.constraintId})
                SET constraint.constraintType = row.constraintType,
                    constraint.statement = row.statement,
                    constraint.status = row.status
                WITH row, constraint
                UNWIND row.featureIds AS featureId
                MATCH (feature:Feature {featureId: featureId})
                MERGE (constraint)-[:CONSTRAINS_FEATURE]->(feature)
                WITH row, constraint
                UNWIND row.apiIds AS apiId
                MATCH (api:ApiContract {contractId: apiId})
                MERGE (constraint)-[:CONSTRAINS_API]->(api)
                WITH row, constraint
                UNWIND row.sourceIds AS sourceId
                MATCH (source:SourceReference {sourceId: sourceId})
                MERGE (constraint)-[:HAS_SOURCE]->(source)
                """).run();

        neo4jClient.query("""
                UNWIND [
                  {
                    riskId: 'RSK-001',
                    title: 'Publish readiness gaps could allow partially verified agents to ship',
                    probability: 'MEDIUM',
                    impact: 'HIGH',
                    mitigation: 'Keep publish tied to graph readiness, verification evidence, and explicit approval checkpoints.',
                    status: 'IN_REVIEW',
                    featureIds: ['FEAT-AI'],
                    storyIds: ['US-AI-090'],
                    sourceIds: ['SRC-US-AI-090-001', 'SRC-BUG-001']
                  },
                  {
                    riskId: 'RSK-002',
                    title: 'Sign-in regressions could block platform entry',
                    probability: 'MEDIUM',
                    impact: 'CRITICAL',
                    mitigation: 'Retain explicit auth contract coverage, screen validation, and recovery-path monitoring.',
                    status: 'IN_REVIEW',
                    featureIds: ['FEAT-AUTH'],
                    storyIds: ['US-AUTH-001'],
                    sourceIds: ['SRC-US-AUTH-001', 'SRC-API-AUTH-LOGIN-001']
                  }
                ] AS row
                MERGE (risk:Risk {riskId: row.riskId})
                SET risk.title = row.title,
                    risk.probability = row.probability,
                    risk.impact = row.impact,
                    risk.mitigation = row.mitigation,
                    risk.status = row.status
                WITH row, risk
                UNWIND row.featureIds AS featureId
                MATCH (feature:Feature {featureId: featureId})
                MERGE (risk)-[:THREATENS_FEATURE]->(feature)
                WITH row, risk
                UNWIND row.storyIds AS storyId
                MATCH (story:UserStory {storyId: storyId})
                MERGE (risk)-[:THREATENS_STORY]->(story)
                WITH row, risk
                UNWIND row.sourceIds AS sourceId
                MATCH (source:SourceReference {sourceId: sourceId})
                MERGE (risk)-[:HAS_SOURCE]->(source)
                """).run();

        neo4jClient.query("""
                UNWIND [
                  {
                    assessmentId: 'ASSESS-CAP-003',
                    name: 'Screen management governance maturity',
                    assessmentType: 'CAPABILITY',
                    targetKind: 'CAP',
                    assessmentDate: date('2026-03-18'),
                    assessor: 'design-hub-governance-bot',
                    maturityLevel: 'MANAGED',
                    currentStateDescription: 'Capability traceability and delivery diagnostics are graph-backed but still depend on seeded governance evidence.',
                    targetStateDescription: 'Capability governance remains fully queryable with live external evidence and ongoing quality enforcement.',
                    score: 91,
                    status: 'APPROVED',
                    targetId: 'CAP-SCREEN-MGMT',
                    gapIds: ['GAP-SCR-AGT-BUILDER-AUTO']
                  },
                  {
                    assessmentId: 'ASSESS-API-002',
                    name: 'Agent publish contract security assessment',
                    assessmentType: 'SECURITY',
                    targetKind: 'API',
                    assessmentDate: date('2026-03-18'),
                    assessor: 'design-hub-governance-bot',
                    maturityLevel: 'MANAGED',
                    currentStateDescription: 'Publish API coverage is explicit and traceable, but it still relies on seeded governance evidence and benchmark-driven gaps.',
                    targetStateDescription: 'Publish API retains explicit readiness, verification, and audit linkages as the automation surface broadens.',
                    score: 90,
                    status: 'APPROVED',
                    targetId: 'API-POST-API-V1-AGENTS-ID-PUBLISH',
                    gapIds: ['GAP-SCR-AGT-BUILDER-AUTO']
                  }
                ] AS row
                MERGE (assessment:Assessment {assessmentId: row.assessmentId})
                SET assessment.name = row.name,
                    assessment.assessmentType = row.assessmentType,
                    assessment.targetKind = row.targetKind,
                    assessment.assessmentDate = row.assessmentDate,
                    assessment.assessor = row.assessor,
                    assessment.maturityLevel = row.maturityLevel,
                    assessment.currentStateDescription = row.currentStateDescription,
                    assessment.targetStateDescription = row.targetStateDescription,
                    assessment.score = row.score,
                    assessment.status = row.status
                WITH row, assessment
                CALL (row, assessment) {
                    OPTIONAL MATCH (capability:BusinessCapability {capabilityId: row.targetId})
                    FOREACH (_ IN CASE WHEN row.targetKind = 'CAP' AND capability IS NOT NULL THEN [1] ELSE [] END |
                        MERGE (assessment)-[:ASSESSES]->(capability)
                    )
                    RETURN 0 AS _
                }
                WITH row, assessment
                CALL (row, assessment) {
                    OPTIONAL MATCH (api:ApiContract {contractId: row.targetId})
                    FOREACH (_ IN CASE WHEN row.targetKind = 'API' AND api IS NOT NULL THEN [1] ELSE [] END |
                        MERGE (assessment)-[:ASSESSES]->(api)
                    )
                    RETURN 0 AS _
                }
                WITH row, assessment
                UNWIND row.gapIds AS gapId
                MATCH (gap:Gap {gapId: gapId})
                MERGE (assessment)-[:IDENTIFIES_GAP]->(gap)
                """).run();
    }

    // ── Orchestration ──────────────────────────────────────────────────

    @Transactional
    public void runFullMigration() {
        // 1. Seed all registry nodes first
        backfillExternalNodeIds();
        seedChannels();
        seedPermissions();
        seedBusinessRoles();
        seedValidationRoles();
        seedConfirmationDialogs();
        seedErrorCodes();

        // 2. Patch persisted data before backfilling edges
        patchChannelCodes();
        patchInteractionPermissions();
        patchRegistryCoverageDefaults();
        patchInteractionOutcomes();

        // 3. Upsert ApiContract nodes from interaction apiCalls strings
        upsertApiContractsFromInteractions();

        // 4. Backfill existing edges (personas, screen roles, channels, permissions)
        backfillPersonas();
        backfillPersonaUsageFromJourneys();
        backfillAccessibleByRoleEdges();
        backfillDeliveredViaChannelEdges();
        backfillRequiresPermissionEdges();

        // 5. Backfill new edges (interaction/touchpoint roles, API calls, confirmations)
        backfillInteractionPersonaEdges();
        backfillInteractionRoleEdges();
        backfillTouchpointRoleEdges();
        backfillJourneyPersonaCoverage();
        backfillPersonaUsageFromJourneys();
        backfillPersonaRoleKeys();
        backfillTouchpointPersonaCoverage();
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
        seedDataQualityConstraints();
        seedMessages();
        seedValidationRules();
        seedApiSchemas();
        seedApiContractCoverageDefaults();
        seedTestCaseVerifies();
        seedStoryRuleEdges();
        seedStoryReadinessCoverage();

        // 7. Seed D5a process spine coverage
        seedBusinessDomains();
        seedBusinessProcesses();
        seedProcessFlows();
        seedProcessExpansion();
        seedBoundaryEvent();
        patchBusinessProcessCoverageDefaults();
        deduplicateProcessActivities();
        seedStoryTasks();

        // 8. Seed D6a traceability coverage
        seedSourceReferences();
        seedExternalArtifacts();
        seedFindings();
        seedTraceabilityEdges();
        seedExternalArtifactAlignment();
        seedExternalFieldNormalization();
        seedUpperTraceabilitySpine();
        seedImplementationSourceCoverage();

        // 9. Seed D6a screen-flow coverage
        seedScreenStates();
        seedTransitions();
        backfillScreenTransitionEdges();
        backfillScreenRoleCoverage();
        seedScreenCoverageMessages();
        patchScreenBenchmarkAttributes();
        seedScreenCoverageGaps();
        backfillScreenBenchmarkDefaults();
        seedCatalogSourceCoverage();

        // 10. Seed technical execution context coverage
        seedApplicationsAndComponents();
        seedAgentExecutionPolicies();
        seedBusinessArchitectureAlignment();
        seedProjectDeliveryAlignment();
        seedImplementationPackArtifacts();
        seedDataArchitectureAlignment();
        seedInfrastructureArchitectureAlignment();
        seedImplementationPackVerification();
        backfillTaskImplementationCoverage();
        seedScreenCoverageStory();
        backfillInteractionBenchmarkDefaults();
        seedScreenCoverageInteractions();
        seedTopicCoverage();
        seedEdgeCaseCoverage();
        seedExceptionCaseCoverage();
        seedIntegrationCoverage();
        seedOpenQuestionCoverage();
        seedJourneyStepBenchmarkCoverage();
        seedImportSnapshotCoverage();
        seedEvidenceRecordCoverage();
        seedRemainingRegistryCoverage();
        patchTouchpointBenchmarkPersonas();
        patchSourceReferenceBenchmarkCoverage();
        seedGovernanceCoverage();
    }
}
