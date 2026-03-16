package com.emsist.designhub.repository;

import com.emsist.designhub.domain.Screen;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ScreenRepository extends Neo4jRepository<Screen, String> {

    @Query("""
            MATCH (s:Screen)
            WHERE ($module IS NULL OR s.module = $module)
              AND ($designStatus IS NULL OR s.designStatus = $designStatus)
            RETURN s
            ORDER BY s.module, s.label
            """)
    List<Screen> findFiltered(@Param("module") String module,
                              @Param("designStatus") String designStatus);

    @Query("""
            MATCH (s:Screen {surfaceId: $surfaceId})
            OPTIONAL MATCH (s)-[:HAS_GAP]->(g:Gap)
            OPTIONAL MATCH (s)-[:HAS_CONTENT]->(c:ContentElement)
            OPTIONAL MATCH (s)-[:TRANSITIONS_TO]->(t:Screen)
            RETURN s, collect(DISTINCT g) AS gaps,
                   collect(DISTINCT c) AS contentElements,
                   collect(DISTINCT t) AS transitionsTo
            """)
    Optional<Screen> findFullGraph(@Param("surfaceId") String surfaceId);

    List<Screen> findByModule(String module);

    long countByDesignStatus(String designStatus);

    long countByDeliveryStatus(String deliveryStatus);
}
