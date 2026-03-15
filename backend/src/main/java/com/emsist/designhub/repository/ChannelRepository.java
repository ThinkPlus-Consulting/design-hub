package com.emsist.designhub.repository;

import com.emsist.designhub.domain.Channel;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChannelRepository extends Neo4jRepository<Channel, String> {
}
