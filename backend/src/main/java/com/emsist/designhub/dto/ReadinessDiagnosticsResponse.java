package com.emsist.designhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReadinessDiagnosticsResponse {
    private String artifactType;
    private String artifactId;
    private String status;
    private Map<String, Boolean> readiness;
    private double completenessScore;
    private String completenessLevel;
    private List<String> missingBlockingRules;
    private List<String> missingOptionalRules;
    private List<String> missingArtifacts;
    private List<String> advisoryRulesViolated;
}
