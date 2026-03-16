package com.emsist.designhub.repository;

import com.emsist.designhub.domain.Touchpoint;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TouchpointRepository extends Neo4jRepository<Touchpoint, String> {

    List<Touchpoint> findBySurfaceId(String surfaceId);
}
