package com.emsist.designhub.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistryGraphMigrationService {

    private final Neo4jClient neo4jClient;

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

    @Transactional
    public void runFullMigration() {
        seedChannels();
        seedPermissions();
        seedBusinessRoles();
        seedValidationRoles();
        backfillPersonas();
        backfillAccessibleByRoleEdges();
        backfillDeliveredViaChannelEdges();
        backfillRequiresPermissionEdges();
    }
}
