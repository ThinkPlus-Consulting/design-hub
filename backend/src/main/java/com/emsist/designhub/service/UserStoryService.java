package com.emsist.designhub.service;

import com.emsist.designhub.dto.UserStoryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserStoryService {

    private final Neo4jClient neo4jClient;

    public List<UserStoryResponse> getAll() {
        log.debug("Fetching user story graph summaries");
        return neo4jClient.query("""
                        MATCH (u:UserStory)
                        OPTIONAL MATCH (s:Screen)-[:IMPLEMENTS_STORY]->(u)
                        RETURN u.storyId AS storyId,
                               u.label AS label,
                               u.module AS module,
                               u.domain AS domain,
                               u.storyNumber AS storyNumber,
                               count(DISTINCT s) AS screenCount
                        ORDER BY u.module, u.storyId
                        """)
                .fetch().all().stream()
                .map(UserStoryService::toUserStoryResponse)
                .toList();
    }

    private static UserStoryResponse toUserStoryResponse(Map<String, Object> row) {
        return new UserStoryResponse(
                (String) row.get("storyId"),
                (String) row.get("label"),
                (String) row.get("module"),
                (String) row.get("domain"),
                (String) row.get("storyNumber"),
                toLong(row.get("screenCount"))
        );
    }

    private static long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }
}
