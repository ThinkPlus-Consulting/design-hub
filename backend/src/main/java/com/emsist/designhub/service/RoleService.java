package com.emsist.designhub.service;

import com.emsist.designhub.dto.RoleResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleService {

    private final Neo4jClient neo4jClient;

    public List<RoleResponse> getAll() {
        log.debug("Fetching business-role and validation-role graph summaries");
        // ACCESSIBLE_BY_ROLE on Touchpoint/Interaction is deferred (no @Relationship yet).
        // Only Screen→BusinessRole edges exist; touchpointCount/interactionCount hardcoded to 0.
        return neo4jClient.query("""
                        MATCH (r:BusinessRole)
                        OPTIONAL MATCH (s:Screen)-[:ACCESSIBLE_BY_ROLE]->(r)
                        WITH r, count(DISTINCT s) AS screenCount
                        OPTIONAL MATCH (j:Journey)-[:PERFORMED_BY_PERSONA]->(:Persona)<-[:USED_BY_PERSONA]-(s2:Screen)-[:ACCESSIBLE_BY_ROLE]->(r)
                        RETURN r.roleKey AS roleKey,
                               r.displayName AS displayName,
                               r.roleGroup AS roleGroup,
                               r.sortOrder AS sortOrder,
                               screenCount,
                               0 AS touchpointCount,
                               0 AS interactionCount,
                               count(DISTINCT j) AS journeyCount
                        UNION ALL
                        MATCH (vr:ValidationRole)
                        OPTIONAL MATCH (s:Screen)-[:ACCESSIBLE_BY_ROLE]->(vr)
                        WITH vr, count(DISTINCT s) AS screenCount
                        RETURN vr.validationRoleKey AS roleKey,
                               vr.displayName AS displayName,
                               vr.scope AS roleGroup,
                               null AS sortOrder,
                               screenCount,
                               0 AS touchpointCount,
                               0 AS interactionCount,
                               0 AS journeyCount
                        """)
                .fetch().all().stream()
                .map(RoleService::toRoleResponse)
                .toList();
    }

    private static RoleResponse toRoleResponse(Map<String, Object> row) {
        return new RoleResponse(
                (String) row.get("roleKey"),
                (String) row.get("displayName"),
                (String) row.get("roleGroup"),
                toInteger(row.get("sortOrder")),
                toLong(row.get("screenCount")),
                toLong(row.get("touchpointCount")),
                toLong(row.get("interactionCount")),
                toLong(row.get("journeyCount"))
        );
    }

    private static Integer toInteger(Object value) {
        return value instanceof Number number ? number.intValue() : null;
    }

    private static long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }
}
