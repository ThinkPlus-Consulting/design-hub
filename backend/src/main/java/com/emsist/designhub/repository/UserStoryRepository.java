package com.emsist.designhub.repository;

import com.emsist.designhub.domain.UserStory;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserStoryRepository extends Neo4jRepository<UserStory, String> {

    @Query("""
            MATCH (u:UserStory)
            OPTIONAL MATCH (u)-[:DELIVERS]->(s:Screen)
            RETURN u.storyId AS storyId,
                   u.label AS label,
                   u.module AS module,
                   u.domain AS domain,
                   u.storyNumber AS storyNumber,
                   count(DISTINCT s) AS screenCount
            ORDER BY u.module, u.storyId
            """)
    List<UserStorySummaryProjection> findAllSummaries();

    interface UserStorySummaryProjection {
        String getStoryId();
        String getLabel();
        String getModule();
        String getDomain();
        String getStoryNumber();
        long getScreenCount();
    }
}
