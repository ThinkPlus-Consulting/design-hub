package com.emsist.designhub.dto;

import java.util.List;

public record GraphBenchmarkResponse(
        BenchmarkSummary summary,
        List<BenchmarkTypeScore> types
) {
    public record BenchmarkSummary(
            String scopeNote,
            int coveredNodeTypes,
            long totalNodes,
            double overallScore,
            List<BenchmarkDimensionScore> dimensions
    ) {
    }

    public record BenchmarkDimensionScore(
            String dimension,
            double score,
            String status,
            String detail
    ) {
    }

    public record BenchmarkTypeScore(
            String nodeType,
            long totalNodes,
            int targetAttributeCount,
            double attributeDepthScore,
            int targetRelationshipCount,
            double relationshipCoverageScore,
            boolean sourceTraceabilityApplicable,
            Double sourceTraceabilityScore,
            double queryabilityScore,
            double overallScore,
            List<String> gapRecommendations
    ) {
    }
}
