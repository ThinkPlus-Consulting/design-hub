package com.emsist.designhub.repository;

import com.emsist.designhub.domain.Interaction;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InteractionRepository extends Neo4jRepository<Interaction, String> {

    @Query("""
            MATCH (i:Interaction {surfaceId: $surfaceId})
            OPTIONAL MATCH (i)-[effectRel:HAS_EFFECT]->(e:Effect)
            OPTIONAL MATCH (i)-[permissionRel:REQUIRES_PERMISSION]->(perm:Permission)
            OPTIONAL MATCH (i)-[personaRel:USED_BY_PERSONA]->(persona:Persona)
            OPTIONAL MATCH (i)-[roleRel:ACCESSIBLE_BY_ROLE]->(role:BusinessRole)
            OPTIONAL MATCH (i)-[apiRel:CALLS_API]->(api:ApiContract)
            OPTIONAL MATCH (i)-[dialogRel:TRIGGERS_CONFIRMATION]->(dialog:ConfirmationDialog)
            RETURN i,
                   collect(DISTINCT effectRel),
                   collect(DISTINCT e),
                   collect(DISTINCT permissionRel),
                   collect(DISTINCT perm),
                   collect(DISTINCT personaRel),
                   collect(DISTINCT persona),
                   collect(DISTINCT roleRel),
                   collect(DISTINCT role),
                   collect(DISTINCT apiRel),
                   collect(DISTINCT api),
                   collect(DISTINCT dialogRel),
                   collect(DISTINCT dialog)
            """)
    List<Interaction> findBySurfaceId(@Param("surfaceId") String surfaceId);
}
