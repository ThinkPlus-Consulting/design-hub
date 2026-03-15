package com.emsist.designhub.repository;

import com.emsist.designhub.domain.BusinessRole;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BusinessRoleRepository extends Neo4jRepository<BusinessRole, String> {
}
