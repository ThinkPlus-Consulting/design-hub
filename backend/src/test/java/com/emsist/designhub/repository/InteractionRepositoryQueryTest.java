package com.emsist.designhub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.neo4j.repository.query.Query;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class InteractionRepositoryQueryTest {

    @Test
    void findBySurfaceIdShouldLoadAllRuntimeEdges() throws NoSuchMethodException {
        Method method = InteractionRepository.class.getMethod("findBySurfaceId", String.class);
        Query query = method.getAnnotation(Query.class);

        assertNotNull(query);
        String cypher = query.value();

        assertAll(
                () -> assertTrue(cypher.contains("MATCH (s:Screen {surfaceId: $surfaceId})-[:HAS_INTERACTION]->(i:Interaction)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (i)-[effectRel:HAS_EFFECT]->(e:Effect)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (i)-[permissionRel:REQUIRES_PERMISSION]->(perm:Permission)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (i)-[personaRel:USED_BY_PERSONA]->(persona:Persona)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (i)-[roleRel:ACCESSIBLE_BY_ROLE]->(role:BusinessRole)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (i)-[apiRel:CALLS_API]->(api:ApiContract)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (i)-[dialogRel:TRIGGERS_CONFIRMATION]->(dialog:ConfirmationDialog)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (i)-[errorRel:ON_ERROR_SHOWS]->(errorCode:ErrorCode)"))
        );
    }

    @Test
    void findBySurfaceIdShouldReturnRelatedNodesAndRelationships() throws NoSuchMethodException {
        Method method = InteractionRepository.class.getMethod("findBySurfaceId", String.class);
        Query query = method.getAnnotation(Query.class);

        assertNotNull(query);
        String cypher = query.value();

        assertAll(
                () -> assertTrue(cypher.contains("collect(DISTINCT effectRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT e)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT permissionRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT perm)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT personaRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT persona)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT roleRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT role)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT apiRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT api)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT dialogRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT dialog)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT errorRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT errorCode)"))
        );
    }
}
