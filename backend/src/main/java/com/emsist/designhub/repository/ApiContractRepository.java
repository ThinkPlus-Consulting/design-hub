package com.emsist.designhub.repository;

import com.emsist.designhub.domain.ApiContract;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface ApiContractRepository extends Neo4jRepository<ApiContract, String> {
}
