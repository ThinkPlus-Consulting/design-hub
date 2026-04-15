package com.emsist.designhub.systemshellgraph.service;

import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphIssueScanItemRequest;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphIssueScanRequest;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphIssueScanSummaryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SystemShellGraphIssueRegistryService {

    private static final String ISSUE_FAMILY = "Issue";
    private static final String ISSUE_OBJECT_TYPE = "DesignIssue";

    private final Neo4jClient neo4jClient;

    @Transactional
    public SystemShellGraphIssueScanSummaryResponse synchronizeIssueScan(SystemShellGraphIssueScanRequest request) {
        List<NormalizedIssue> normalizedIssues = normalize(request == null ? List.of() : request.issues());
        Set<String> incomingIssueIds = normalizedIssues.stream()
                .map(NormalizedIssue::id)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
        Set<String> existingIssueIds = fetchExistingIssueIds(incomingIssueIds);

        int newIssues = 0;
        int existingIssues = 0;
        for (NormalizedIssue issue : normalizedIssues) {
            if (existingIssueIds.contains(issue.id())) {
                existingIssues += 1;
            } else {
                newIssues += 1;
            }
            upsertIssue(issue);
        }

        int resolvedByRetest = closeOpenIssuesMissingFromScan(incomingIssueIds);
        return new SystemShellGraphIssueScanSummaryResponse(
                normalizedIssues.size(),
                newIssues,
                existingIssues,
                resolvedByRetest
        );
    }

    @Transactional
    public void updateIssueStatuses(List<String> issueIds, String status) {
        List<String> normalizedIssueIds = issueIds == null ? List.of() : issueIds.stream()
                .filter(issueId -> issueId != null && !issueId.isBlank())
                .distinct()
                .toList();
        if (normalizedIssueIds.isEmpty()) {
            return;
        }

        String normalizedStatus = normalizeStatus(status);
        neo4jClient.query("""
                        MATCH (issue:SystemShellGraphNode {graphScope: $graphScope, family: $family})
                        WHERE issue.id IN $issueIds
                        SET issue.status = $status,
                            issue.updatedAt = $updatedAt
                        """)
                .bind(SystemShellGraphQueryService.GRAPH_SCOPE).to("graphScope")
                .bind(ISSUE_FAMILY).to("family")
                .bind(normalizedIssueIds).to("issueIds")
                .bind(normalizedStatus).to("status")
                .bind(OffsetDateTime.now().toString()).to("updatedAt")
                .run();
    }

    @Transactional
    public void updateIssuePrompt(String issueId, String issuePrompt) {
        String normalizedIssueId = sanitize(issueId);
        if (normalizedIssueId == null) {
            return;
        }

        neo4jClient.query("""
                        MATCH (issue:SystemShellGraphNode {
                          graphScope: $graphScope,
                          family: $family,
                          id: $issueId
                        })
                        SET issue.issuePrompt = $issuePrompt,
                            issue.updatedAt = $updatedAt
                        """)
                .bind(SystemShellGraphQueryService.GRAPH_SCOPE).to("graphScope")
                .bind(ISSUE_FAMILY).to("family")
                .bind(normalizedIssueId).to("issueId")
                .bind(sanitize(issuePrompt)).to("issuePrompt")
                .bind(OffsetDateTime.now().toString()).to("updatedAt")
                .run();
    }

    private List<NormalizedIssue> normalize(List<SystemShellGraphIssueScanItemRequest> rawIssues) {
        if (rawIssues == null || rawIssues.isEmpty()) {
            return List.of();
        }

        Map<String, NormalizedIssue> deduped = new LinkedHashMap<>();
        for (SystemShellGraphIssueScanItemRequest rawIssue : rawIssues) {
            if (rawIssue == null) {
                continue;
            }

            String targetObjectId = sanitize(rawIssue.targetObjectId());
            String message = sanitize(rawIssue.message());
            if (targetObjectId == null || message == null) {
                continue;
            }

            String source = defaulted(rawIssue.source(), "X-Ray Agent");
            String category = defaulted(rawIssue.category(), "Structure");
            String rule = defaulted(rawIssue.rule(), "Design Rule");
            String severity = defaulted(rawIssue.severity(), "error");
            String issueId = buildIssueId(targetObjectId, category, rule, message);
            String issuePrompt = buildIssuePrompt(targetObjectId, category, rule, message);
            deduped.put(issueId, new NormalizedIssue(
                    issueId,
                    issueName(category),
                    targetObjectId,
                    source,
                    category,
                    rule,
                    message,
                    severity,
                    issuePrompt
            ));
        }

        return List.copyOf(deduped.values());
    }

    private Set<String> fetchExistingIssueIds(Set<String> issueIds) {
        if (issueIds.isEmpty()) {
            return Set.of();
        }

        return neo4jClient.query("""
                        MATCH (issue:SystemShellGraphNode {graphScope: $graphScope, family: $family})
                        WHERE issue.id IN $issueIds
                        RETURN issue.id AS issueId
                        """)
                .bind(SystemShellGraphQueryService.GRAPH_SCOPE).to("graphScope")
                .bind(ISSUE_FAMILY).to("family")
                .bind(List.copyOf(issueIds)).to("issueIds")
                .fetch()
                .all()
                .stream()
                .map(row -> row.get("issueId"))
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(LinkedHashSet::new, Set::add, Set::addAll);
    }

    private void upsertIssue(NormalizedIssue issue) {
        neo4jClient.query("""
                        MATCH (target:SystemShellGraphNode {graphScope: $graphScope})
                        WHERE target.id = $targetObjectId
                        MERGE (issue:SystemShellGraphNode {graphScope: $graphScope, id: $issueId})
                        ON CREATE SET
                          issue.family = $family,
                          issue.objectType = $objectType,
                          issue.domain = 'frontend',
                          issue.layer = 'instance',
                          issue.createdAt = $updatedAt
                        SET
                          issue.name = $name,
                          issue.description = $message,
                          issue.status = 'open',
                          issue.issueSource = $source,
                          issue.issueCategory = $category,
                          issue.issueRule = $rule,
                          issue.issueSeverity = $severity,
                          issue.issuePrompt = coalesce(issue.issuePrompt, $issuePrompt),
                          issue.updatedAt = $updatedAt
                        MERGE (target)-[:HAS_ISSUE]->(issue)
                        """)
                .bind(SystemShellGraphQueryService.GRAPH_SCOPE).to("graphScope")
                .bind(issue.targetObjectId()).to("targetObjectId")
                .bind(issue.id()).to("issueId")
                .bind(ISSUE_FAMILY).to("family")
                .bind(ISSUE_OBJECT_TYPE).to("objectType")
                .bind(issue.name()).to("name")
                .bind(issue.message()).to("message")
                .bind(issue.source()).to("source")
                .bind(issue.category()).to("category")
                .bind(issue.rule()).to("rule")
                .bind(issue.severity()).to("severity")
                .bind(issue.prompt()).to("issuePrompt")
                .bind(OffsetDateTime.now().toString()).to("updatedAt")
                .run();
    }

    private int closeOpenIssuesMissingFromScan(Set<String> incomingIssueIds) {
        Object rawCount = neo4jClient.query("""
                        MATCH (issue:SystemShellGraphNode {graphScope: $graphScope, family: $family})
                        WHERE issue.status = 'open'
                          AND (size($incomingIssueIds) = 0 OR NOT issue.id IN $incomingIssueIds)
                        SET issue.status = 'closed',
                            issue.updatedAt = $updatedAt
                        RETURN count(issue) AS resolvedCount
                        """)
                .bind(SystemShellGraphQueryService.GRAPH_SCOPE).to("graphScope")
                .bind(ISSUE_FAMILY).to("family")
                .bind(List.copyOf(incomingIssueIds)).to("incomingIssueIds")
                .bind(OffsetDateTime.now().toString()).to("updatedAt")
                .fetch()
                .one()
                .map(row -> row.get("resolvedCount"))
                .orElse(0L);

        return rawCount instanceof Number number ? number.intValue() : 0;
    }

    private String buildIssueId(
            String targetObjectId,
            String category,
            String rule,
            String message
    ) {
        String raw = String.join("|", defaulted(targetObjectId, ""), category, rule, message);
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.getBytes(StandardCharsets.UTF_8));
            StringBuilder builder = new StringBuilder("ISS-");
            for (int index = 0; index < 8; index += 1) {
                builder.append(String.format("%02X", hash[index]));
            }
            return builder.toString();
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("Unable to create issue id hash.", exception);
        }
    }

    private String normalizeStatus(String status) {
        return "closed".equalsIgnoreCase(status) ? "closed" : "open";
    }

    private String issueName(String category) {
        return switch (category) {
            case "Accessibility" -> "Accessibility Issue";
            case "Container Styling" -> "Container Styling Issue";
            case "Viewport Navigation" -> "Viewport Navigation Issue";
            case "HTML Element Violation" -> "HTML Element Violation";
            case "Preview-Tree Parity" -> "Preview-Tree Parity Issue";
            default -> "Structural Issue";
        };
    }

    private String buildIssuePrompt(
            String targetObjectId,
            String category,
            String rule,
            String message
    ) {
        String fixGuidance = switch (category) {
            case "Container Styling" -> """
                    - Reset the styled container back to a plain structural container.
                    - Remove decorative styling from the container itself: background, border, shadow, and color overrides.
                    - If the visual surface is intentional, remodel it as an explicit valid artifact instead of leaving it on the container.
                    - Run X-Ray Agent again to confirm the container is plain.
                    """.trim();
            default -> """
                    - Inspect the rendered artifact and the mapped graph object.
                    - Update the design structure, styling, or accessibility implementation so this violation no longer appears.
                    - Run X-Ray Agent again to confirm the issue is resolved.
                    """.trim();
        };

        return """
                Issue
                %s

                Context
                - Object ID: %s
                - Category: %s
                - Rule: %s

                Fix Guidance
                %s
                """.formatted(
                        message,
                        defaulted(targetObjectId, "Not recorded"),
                        category,
                        rule,
                        fixGuidance
                ).trim();
    }

    private String sanitize(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private String defaulted(String value, String fallback) {
        String sanitized = sanitize(value);
        return sanitized == null ? fallback : sanitized;
    }

    private record NormalizedIssue(
            String id,
            String name,
            String targetObjectId,
            String source,
            String category,
            String rule,
            String message,
            String severity,
            String prompt
    ) {
    }
}
