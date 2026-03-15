package com.emsist.designhub.repository;

import com.emsist.designhub.domain.ValidationRole;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationRoleRepository extends Neo4jRepository<ValidationRole, String> {
}
