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

    // ── Orchestration ──────────────────────────────────────────────────

    @Transactional
    public void runFullMigration() {
        // 1. Seed all registry nodes first
        seedChannels();
        seedPermissions();
        seedBusinessRoles();
        seedValidationRoles();
        seedConfirmationDialogs();

        // 2. Patch persisted data before backfilling edges
        patchChannelCodes();
        patchInteractionPermissions();

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
    }
}
