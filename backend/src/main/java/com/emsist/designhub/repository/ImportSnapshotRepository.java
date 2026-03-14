package com.emsist.designhub.repository;

import com.emsist.designhub.domain.ImportSnapshot;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ImportSnapshotRepository extends Neo4jRepository<ImportSnapshot, String> {}
