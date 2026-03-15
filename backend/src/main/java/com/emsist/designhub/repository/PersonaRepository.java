package com.emsist.designhub.repository;

import com.emsist.designhub.domain.Persona;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonaRepository extends Neo4jRepository<Persona, String> {
}
