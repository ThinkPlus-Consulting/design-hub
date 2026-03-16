package com.emsist.designhub.repository;

import com.emsist.designhub.domain.Journey;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JourneyRepository extends Neo4jRepository<Journey, String> {

    List<Journey> findByPersonaId(String personaId);
}
