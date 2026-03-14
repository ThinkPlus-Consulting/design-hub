package com.emsist.designhub.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class SchemaValidatorService {

    private static final Map<String, String> REQUIRED_ID_FIELDS = Map.of(
            "UserStory", "storyId",
            "Screen", "surfaceId",
            "Journey", "journeyId",
            "ApiContract", "contractId",
            "DataEntity", "entityId",
            "TestCase", "testCaseId",
            "CodeAsset", "codeAssetId",
            "Rule", "ruleId",
            "Task", "taskId"
    );

    public ValidationResult validate(String artifactType, Map<String, Object> candidate) {
        List<String> errors = new ArrayList<>();

        if (!REQUIRED_ID_FIELDS.containsKey(artifactType)) {
            errors.add("Unknown artifact type: " + artifactType);
            return new ValidationResult(false, errors);
        }

        String idField = REQUIRED_ID_FIELDS.get(artifactType);
        if (!candidate.containsKey(idField) || candidate.get(idField) == null) {
            errors.add("Missing required ID field: " + idField);
        }

        return new ValidationResult(errors.isEmpty(), errors);
    }

    @Data
    @AllArgsConstructor
    public static class ValidationResult {
        private boolean valid;
        private List<String> errors;
    }
}
