package com.emsist.designhub.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;

@Service
public class RiskScoringService {

    public RiskResult score(RiskFactors factors) {
        int score = 0;

        // Blast radius: 3x weight, score = min(radius/3, 5) * 3
        score += Math.min(factors.blastRadius / 3, 5) * 3;

        // Cross-service: 3x weight (binary)
        if (factors.crossServiceImpact) score += 3;

        // Data model: 2x weight (binary)
        if (factors.dataModelChange) score += 2;

        // API contract: 2x weight (binary)
        if (factors.apiContractChange) score += 2;

        // Security: 3x weight (binary)
        if (factors.securitySensitive) score += 3;

        // First-time file: 1x weight (binary)
        if (factors.firstTimeFile) score += 1;

        String level;
        if (score <= 5) level = "LOW";
        else if (score <= 12) level = "MEDIUM";
        else if (score <= 20) level = "HIGH";
        else level = "CRITICAL";

        return new RiskResult(score, level);
    }

    public record RiskFactors(
            int blastRadius,
            boolean crossServiceImpact,
            boolean dataModelChange,
            boolean apiContractChange,
            boolean securitySensitive,
            boolean firstTimeFile
    ) {}

    @Data
    @AllArgsConstructor
    public static class RiskResult {
        private int score;
        private String level;
    }
}
