package com.emsist.designhub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.neo4j.repository.query.Query;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ScreenRepositoryQueryTest {

    @Test
    void findFullGraphShouldLoadStoriesAndInteractions() throws NoSuchMethodException {
        Method method = ScreenRepository.class.getMethod("findFullGraph", String.class);
        Query query = method.getAnnotation(Query.class);

        assertNotNull(query);
        String cypher = query.value();

        assertAll(
                () -> assertTrue(cypher.contains("MATCH (s:Screen {surfaceId: $surfaceId})")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (story:UserStory)-[storyRel:DELIVERS]->(s)")),
                () -> assertTrue(cypher.contains("OPTIONAL MATCH (s)-[interactionRel:HAS_INTERACTION]->(interaction:Interaction)"))
        );
    }

    @Test
    void findFullGraphShouldReturnRelatedNodesAndRelationships() throws NoSuchMethodException {
        Method method = ScreenRepository.class.getMethod("findFullGraph", String.class);
        Query query = method.getAnnotation(Query.class);

        assertNotNull(query);
        String cypher = query.value();

        assertAll(
                () -> assertTrue(cypher.contains("collect(DISTINCT storyRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT story)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT interactionRel)")),
                () -> assertTrue(cypher.contains("collect(DISTINCT interaction)"))
        );
    }
}
