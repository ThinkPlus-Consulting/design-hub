package com.emsist.designhub.repository;

import com.emsist.designhub.domain.Permission;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends Neo4jRepository<Permission, String> {
}
