package com.emsist.designhub.repository;

import org.junit.jupiter.api.Test;
import org.springframework.data.neo4j.repository.query.Query;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UserStoryRepositoryQueryTest {

    @Test
    void findAllSummariesShouldUseCanonicalDeliversEdge() throws NoSuchMethodException {
        Method method = UserStoryRepository.class.getMethod("findAllSummaries");
        Query query = method.getAnnotation(Query.class);

        assertNotNull(query);
        String cypher = query.value();

        assertTrue(cypher.contains("OPTIONAL MATCH (u)-[:DELIVERS]->(s:Screen)"));
        assertFalse(cypher.contains("IMPLEMENTS_STORY"));
    }
}
