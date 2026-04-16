package com.emsist.designhub.systemshellgraph.service;

import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphEdgeResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphNodeResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphValidationIssueResponse;
import com.emsist.designhub.systemshellgraph.dto.SystemShellGraphValidationResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SystemShellGraphValidationService {

    private static final Set<String> STRUCTURAL_RELATIONSHIP_TYPES = Set.of(
            "HAS_SHELL",
            "HAS_SCREEN",
            "HAS_SECTION",
            "HAS_COMPONENT"
    );

    private final SystemShellGraphQueryService queryService;

    public SystemShellGraphValidationResponse validateLiveGraph() {
        SystemShellGraphResponse graph = queryService.getGraph();
        Map<String, List<String>> issues = validate(graph.nodes(), graph.relationships());
        List<SystemShellGraphValidationIssueResponse> issueRows = issues.entrySet().stream()
                .map(entry -> new SystemShellGraphValidationIssueResponse(
                        entry.getKey(),
                        familyForObjectId(graph.nodes(), entry.getKey()),
                        List.copyOf(entry.getValue())
                ))
                .toList();

        return new SystemShellGraphValidationResponse(
                graph.graphScope(),
                graph.scenarioCode(),
                issueRows.isEmpty(),
                issueRows.stream().mapToInt(issue -> issue.messages().size()).sum(),
                issueRows
        );
    }

    private Map<String, List<String>> validate(
            List<SystemShellGraphNodeResponse> nodes,
            List<SystemShellGraphEdgeResponse> relationships
    ) {
        Map<String, List<String>> issues = new LinkedHashMap<>();
        Map<String, SystemShellGraphNodeResponse> nodeById = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> outgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> incoming = new LinkedHashMap<>();

        for (SystemShellGraphNodeResponse node : nodes) {
            String objectId = normalizedObjectId(node);
            if (objectId != null) {
                nodeById.put(objectId, node);
            }
        }

        for (SystemShellGraphEdgeResponse relationship : relationships) {
            if (!STRUCTURAL_RELATIONSHIP_TYPES.contains(relationship.relationshipType())) {
                continue;
            }
            if (relationship.fromId() == null || relationship.toId() == null) {
                continue;
            }

            outgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
            incoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
        }

        for (SystemShellGraphNodeResponse node : nodes) {
            if (!"instance".equals(node.layer())) {
                continue;
            }

            String objectId = normalizedObjectId(node);
            if (objectId == null) {
                continue;
            }

            validateSharedAttributes(node, issues);

            List<SystemShellGraphNodeResponse> children = outgoing.getOrDefault(objectId, List.of()).stream()
                    .map(relationship -> nodeById.get(relationship.toId()))
                    .filter(child -> child != null && "instance".equals(child.layer()))
                    .toList();
            List<SystemShellGraphNodeResponse> parents = incoming.getOrDefault(objectId, List.of()).stream()
                    .map(relationship -> nodeById.get(relationship.fromId()))
                    .filter(parent -> parent != null && "instance".equals(parent.layer()))
                    .toList();

            switch (node.family()) {
                case "Application" -> {
                    if (parents.size() > 0) {
                        addIssue(issues, objectId, "Application must be top-level and have no structural parent.");
                    }
                    if (children.isEmpty()) {
                        addIssue(issues, objectId, "Application must contain at least one Shell.");
                    }
                }
                case "Shell" -> {
                    if (parents.size() != 1 || !"Application".equals(parents.get(0).family())) {
                        addIssue(issues, objectId, "Shell must have exactly one Application parent.");
                    }
                    if (children.isEmpty()) {
                        addIssue(issues, objectId, "Shell must contain at least one Container.");
                    }
                }
                case "Screen" -> {
                    if (parents.size() != 1 || !"Container".equals(parents.get(0).family()) || !"Main Container".equals(parents.get(0).name())) {
                        addIssue(issues, objectId, "Screen must have exactly one Main Container parent.");
                    }
                }
                case "Container" -> {
                    if (parents.size() != 1 || !Set.of("Shell", "Screen", "Container", "Section").contains(parents.get(0).family())) {
                        addIssue(issues, objectId, "Container must have exactly one Shell, Screen, Container, or Section parent.");
                    }
                }
                case "Section" -> {
                    if (parents.size() != 1 || !Set.of("Shell", "Container", "Screen", "Section").contains(parents.get(0).family())) {
                        addIssue(issues, objectId, "Section must have exactly one Shell, Container, Screen, or Section parent.");
                    }
                }
                case "Component" -> {
                    if (parents.size() != 1 || !Set.of("Container", "Section").contains(parents.get(0).family())) {
                        addIssue(issues, objectId, "Component must have exactly one Container or Section parent.");
                    }
                    if (!children.isEmpty()) {
                        addIssue(issues, objectId, "Component must remain a leaf node.");
                    }
                }
                default -> {
                }
            }
        }

        return issues;
    }

    private void validateSharedAttributes(
            SystemShellGraphNodeResponse node,
            Map<String, List<String>> issues
    ) {
        String objectId = normalizedObjectId(node);
        if (objectId == null) {
            return;
        }

        if (blank(node.name())) {
            addIssue(issues, objectId, node.family() + " is missing required attribute: name.");
        }
        if (blank(node.description())) {
            addIssue(issues, objectId, node.family() + " is missing required attribute: description.");
        }
        if (blank(node.domain())) {
            addIssue(issues, objectId, node.family() + " is missing required attribute: domain.");
        }
        if (blank(node.status())) {
            addIssue(issues, objectId, node.family() + " is missing required attribute: status.");
        }
        if (requiresGuid(node.family()) && blank(node.guid())) {
            addIssue(issues, objectId, node.family() + " is missing required attribute: guid.");
        }
    }

    private boolean requiresGuid(String family) {
        return Set.of("Shell", "Container", "Screen", "Section", "Component").contains(family);
    }

    private void addIssue(Map<String, List<String>> issues, String objectId, String message) {
        List<String> existing = issues.get(objectId);
        if (existing == null) {
            existing = new ArrayList<>();
            issues.put(objectId, existing);
        }
        if (!existing.contains(message)) {
            existing.add(message);
        }
    }

    private String familyForObjectId(List<SystemShellGraphNodeResponse> nodes, String objectId) {
        return nodes.stream()
                .filter(node -> objectId.equals(normalizedObjectId(node)))
                .map(SystemShellGraphNodeResponse::family)
                .findFirst()
                .orElse("Unknown");
    }

    private String normalizedObjectId(SystemShellGraphNodeResponse node) {
        return blank(node.id()) ? null : node.id().trim();
    }

    private boolean blank(String value) {
        return value == null || value.isBlank();
    }
}
