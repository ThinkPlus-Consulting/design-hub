package com.emsist.designhub.service;

import com.emsist.designhub.domain.TargetKind;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final Neo4jClient neo4jClient;

    public String resolveTargetLabel(TargetKind kind) {
        return switch (kind) {
            case CAP  -> "BusinessCapability";
            case PROC -> "BusinessProcess";
            case ACT  -> "ProcessActivity";
            case APP  -> "Application";
            case CMP  -> "ApplicationComponent";
            case API  -> "ApiContract";
            case DE   -> "DataEntity";
        };
    }

    public String resolveTargetIdField(TargetKind kind) {
        return switch (kind) {
            case CAP  -> "capabilityId";
            case PROC -> "processId";
            case ACT  -> "activityId";
            case APP  -> "applicationId";
            case CMP  -> "componentId";
            case API  -> "contractId";
            case DE   -> "entityId";
        };
    }

    @Transactional
    public void createAssessesEdge(String assessmentId, TargetKind targetKind, String targetId) {
        String targetLabel = resolveTargetLabel(targetKind);
        String targetIdField = resolveTargetIdField(targetKind);

        neo4jClient.query("""
                MATCH (a:Assessment {assessmentId: $assessmentId})
                MATCH (target:%s {%s: $targetId})
                MERGE (a)-[:ASSESSES]->(target)
                """.formatted(targetLabel, targetIdField))
                .bind(assessmentId).to("assessmentId")
                .bind(targetId).to("targetId")
                .run();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("unchecked")
    public List<Map<String, Object>> findAssessmentsForTarget(TargetKind targetKind, String targetId) {
        String targetLabel = resolveTargetLabel(targetKind);
        String targetIdField = resolveTargetIdField(targetKind);

        return (List<Map<String, Object>>) neo4jClient.query("""
                MATCH (a:Assessment)-[:ASSESSES]->(target:%s {%s: $targetId})
                RETURN a.assessmentId AS assessmentId, a.name AS name
                """.formatted(targetLabel, targetIdField))
                .bind(targetId).to("targetId")
                .fetch()
                .all()
                .stream()
                .toList();
    }
}
