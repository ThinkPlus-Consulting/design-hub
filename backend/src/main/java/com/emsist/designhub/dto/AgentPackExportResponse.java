package com.emsist.designhub.dto;

import java.time.Instant;
import java.util.List;
import java.util.Map;

public record AgentPackExportResponse(
        String packId,
        int packVersion,
        Instant generatedAt,
        GraphNodeReference story,
        PackCompleteness completeness,
        Map<String, Boolean> readinessChecks,
        List<GraphNodeReference> tasks,
        List<GraphNodeReference> deliveredScreens,
        List<GraphNodeReference> deliveredApis,
        List<GraphNodeReference> deliveredEntities,
        List<ApplicationTargetSummary> applications,
        List<ComponentTargetSummary> components,
        List<CodeTargetSummary> codeTargets,
        List<TestCaseSummary> testCases,
        List<PolicySummary> policies,
        List<ConventionSummary> conventions,
        List<QualityConstraintSummary> qualityConstraints
) {

    public record ApplicationTargetSummary(
            String id,
            String name,
            String applicationType,
            String workspaceType,
            String repoPath,
            String repoUrl,
            String defaultBuildCommand,
            String defaultTestCommand,
            List<String> bootstrapSteps
    ) {
    }

    public record ComponentTargetSummary(
            String id,
            String nodeType,
            String displayName,
            String status,
            String applicationId,
            String applicationName,
            String frameworkFamily,
            String frameworkName,
            String frameworkVersion,
            String runtime,
            String language,
            String languageVersion,
            String modulePath,
            String manifestPath,
            String buildCommand,
            String testCommand,
            String entrypointPath,
            String localRunCommand,
            List<String> secretPrerequisites,
            List<String> fixturePrerequisites,
            List<String> localRunPrerequisites
    ) {
    }

    public record CodeTargetSummary(
            String id,
            String nodeType,
            String displayName,
            String status,
            String assetType,
            String filePath,
            String language,
            String layerType,
            String changePolicy,
            String componentId,
            String componentName,
            String applicationId,
            String applicationName
    ) {
    }

    public record TestCaseSummary(
            String id,
            String displayName,
            String status,
            String testType,
            String testCommand,
            String testFilePath,
            String locatedInId,
            String locatedInPath
    ) {
    }

    public record PolicySummary(
            String id,
            String name,
            List<String> allowedRepos,
            List<String> allowedCommands,
            List<String> forbiddenCommands,
            List<String> allowedEnvironments,
            List<String> secretScopes,
            Integer maxFilesTouched,
            Boolean requiresHumanApproval,
            String approvalThreshold
    ) {
    }

    public record ConventionSummary(
            String id,
            String name,
            String category,
            String enforcement,
            String scope,
            String docRef,
            String activeStatus
    ) {
    }

    public record QualityConstraintSummary(
            String id,
            String name,
            String constraintType,
            String priority,
            String threshold,
            String status
    ) {
    }
}
