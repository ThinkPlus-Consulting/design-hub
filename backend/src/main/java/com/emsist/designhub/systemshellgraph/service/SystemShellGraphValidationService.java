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
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class SystemShellGraphValidationService {

    private final SystemShellGraphQueryService queryService;

    public SystemShellGraphValidationResponse validateLiveGraph() {
        SystemShellGraphResponse graph = queryService.getGraph();
        Map<String, List<String>> issues = validate(graph.nodes(), graph.relationships());
        List<SystemShellGraphValidationIssueResponse> issueRows = issues.entrySet().stream()
                .map(entry -> new SystemShellGraphValidationIssueResponse(
                        entry.getKey(),
                        graph.nodes().stream()
                                .filter(node -> entry.getKey().equals(node.code()))
                                .map(SystemShellGraphNodeResponse::family)
                                .findFirst()
                                .orElse("Unknown"),
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
        Map<String, SystemShellGraphNodeResponse> nodeMap = new LinkedHashMap<>();
        Map<String, String> nodeCodeById = new LinkedHashMap<>();
        for (SystemShellGraphNodeResponse node : nodes) {
            nodeMap.put(node.code(), node);
            if (node.id() != null) {
                nodeCodeById.put(node.id(), node.code());
            }
        }

        List<SystemShellGraphEdgeResponse> codeRelationships = relationships.stream()
                .map(relationship -> new SystemShellGraphEdgeResponse(
                        nodeCodeById.get(relationship.fromId()),
                        relationship.relationshipType(),
                        nodeCodeById.get(relationship.toId()),
                        relationship.activeName(),
                        relationship.passiveName()
                ))
                .filter(relationship -> relationship.fromId() != null && relationship.toId() != null)
                .toList();

        Map<String, List<SystemShellGraphEdgeResponse>> structuralOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> structuralIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> ruleSetOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> ruleSetIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> ruleOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> ruleIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> viewportOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> viewportIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> targetOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> canExecuteOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> canExecuteIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> hasStepOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> hasStepIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> governedByOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> governedByIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> hasBlockerOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> hasBlockerIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> raisesOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> raisesIncoming = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> activatesScreenOutgoing = new LinkedHashMap<>();
        Map<String, List<SystemShellGraphEdgeResponse>> activatesScreenIncoming = new LinkedHashMap<>();

        for (SystemShellGraphEdgeResponse relationship : codeRelationships) {
            switch (relationship.relationshipType()) {
                case "HAS_SHELL", "HAS_SCREEN", "HAS_SECTION", "HAS_ELEMENT", "HAS_COMPONENT" -> {
                    structuralOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    structuralIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "USES_RULE_SET" -> {
                    ruleSetOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    ruleSetIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "USES_VIEWPORT_PROFILE" -> {
                    viewportOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    viewportIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "HAS_RULE" -> {
                    ruleOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    ruleIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "TARGETS" -> targetOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                case "CAN_EXECUTE" -> {
                    canExecuteOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    canExecuteIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "HAS_STEP" -> {
                    hasStepOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    hasStepIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "GOVERNED_BY" -> {
                    governedByOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    governedByIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "HAS_BLOCKER" -> {
                    hasBlockerOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    hasBlockerIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "RAISES" -> {
                    raisesOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    raisesIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                case "ACTIVATES_SCREEN" -> {
                    activatesScreenOutgoing.computeIfAbsent(relationship.fromId(), ignored -> new ArrayList<>()).add(relationship);
                    activatesScreenIncoming.computeIfAbsent(relationship.toId(), ignored -> new ArrayList<>()).add(relationship);
                }
                default -> {
                }
            }
        }

        Map<String, List<String>> issues = new LinkedHashMap<>();
        for (SystemShellGraphNodeResponse node : nodes) {
            if (!"instance".equals(node.layer())) {
                continue;
            }

            List<SystemShellGraphEdgeResponse> outgoing = structuralOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> incoming = structuralIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> usesRuleSet = ruleSetOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> usedByScreens = ruleSetIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> rules = ruleOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> ownedByRuleSets = ruleIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> referencedByScreens = viewportIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> targets = targetOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> canExecute = canExecuteOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> executableBy = canExecuteIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> steps = hasStepOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> withinJourneys = hasStepIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> governedByRules = governedByOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> governingSteps = governedByIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> blockers = hasBlockerOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> blockedSteps = hasBlockerIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> raises = raisesOutgoing.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> raisedByRules = raisesIncoming.getOrDefault(node.code(), List.of());
            List<SystemShellGraphEdgeResponse> activatedScreens = activatesScreenOutgoing.getOrDefault(node.code(), List.of());

            validateSharedAttributes(node, issues);

            switch (node.family()) {
                case "Application" -> validateApplication(node, outgoing, incoming, nodeMap, issues);
                case "Shell" -> validateShell(node, outgoing, incoming, nodeMap, issues);
                case "Screen" -> validateScreen(node, outgoing, incoming, usesRuleSet, nodeMap, nodes, issues);
                case "Section" -> validateSection(node, outgoing, incoming, nodeMap, issues);
                case "Element" -> validateElement(node, outgoing, incoming, nodeMap, issues);
                case "Component" -> validateComponent(node, outgoing, incoming, nodeMap, issues);
                case "ValidationRuleSet" -> validateValidationRuleSet(node, usedByScreens, rules, nodeMap, nodes, issues);
                case "ValidationRule" -> validateValidationRule(node, ownedByRuleSets, targets, nodeMap, issues);
                case "ViewportProfile" -> validateViewportProfile(node, referencedByScreens, nodeMap, issues);
                case "Persona" -> validatePersona(node, canExecute, nodeMap, issues);
                case "Journey" -> validateJourney(node, executableBy, steps, nodeMap, issues);
                case "JourneyStep" -> validateJourneyStep(node, withinJourneys, governedByRules, blockers, activatedScreens, nodeMap, issues);
                case "BusinessRule" -> validateBusinessRule(node, governingSteps, raises, nodeMap, issues);
                case "Blocker" -> validateBlocker(node, blockedSteps, raisedByRules, issues);
                default -> {
                }
            }
        }

        return issues;
    }

    private void validateApplication(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> outgoing,
            List<SystemShellGraphEdgeResponse> incoming,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("APP\\d{2}")) {
            addIssue(issues, node.code(), "Application code must follow APP##.");
        }
        if (!incoming.isEmpty()) {
            addIssue(issues, node.code(), "Application must be top structural object and cannot have a structural parent.");
        }
        if (outgoing.isEmpty()) {
            addIssue(issues, node.code(), "Application must contain at least one Shell.");
        }
        for (SystemShellGraphEdgeResponse childEdge : outgoing) {
            if (!"Shell".equals(nodeFamily(nodeMap.get(childEdge.toId())))) {
                addIssue(issues, node.code(), "Application may contain Shell only.");
            }
        }
    }

    private void validateShell(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> outgoing,
            List<SystemShellGraphEdgeResponse> incoming,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (incoming.size() != 1 || !"Application".equals(nodeFamily(nodeMap.get(incoming.get(0).fromId())))) {
            addIssue(issues, node.code(), "Shell must have exactly one Application parent.");
        }
        if (outgoing.isEmpty()) {
            addIssue(issues, node.code(), "Shell must contain at least one Section.");
        }
        for (SystemShellGraphEdgeResponse childEdge : outgoing) {
            if (!"Section".equals(nodeFamily(nodeMap.get(childEdge.toId())))) {
                addIssue(issues, node.code(), "Shell may contain Section only.");
            }
        }
    }

    private void validateScreen(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> outgoing,
            List<SystemShellGraphEdgeResponse> incoming,
            List<SystemShellGraphEdgeResponse> usesRuleSet,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            List<SystemShellGraphNodeResponse> nodes,
            Map<String, List<String>> issues
    ) {
        if (incoming.size() != 1 || !Set.of("Shell", "Section").contains(nodeFamily(nodeMap.get(incoming.get(0).fromId())))) {
            addIssue(issues, node.code(), "Screen must have exactly one Shell or Section parent.");
        }
        if (outgoing.isEmpty()) {
            addIssue(issues, node.code(), "Screen should contain at least one Section.");
        }
        for (SystemShellGraphEdgeResponse childEdge : outgoing) {
            if (!"Section".equals(nodeFamily(nodeMap.get(childEdge.toId())))) {
                addIssue(issues, node.code(), "Screen may contain Section only.");
            }
        }
        long ruleSetCount = usesRuleSet.stream()
                .filter(relationship -> "ValidationRuleSet".equals(nodeFamily(nodeMap.get(relationship.toId()))))
                .count();
        if (screenHasConditionalDescendants(node.code(), nodes) && ruleSetCount != 1) {
            addIssue(issues, node.code(), "Screen with conditional UI nodes must use exactly one ValidationRuleSet.");
        }
        if (!isBlank(node.backgroundType())) {
            addIssue(issues, node.code(), "Screen must not own shell background attributes.");
        }
    }

    private void validateSection(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> outgoing,
            List<SystemShellGraphEdgeResponse> incoming,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (incoming.size() != 1 || !Set.of("Shell", "Screen", "Section").contains(nodeFamily(nodeMap.get(incoming.get(0).fromId())))) {
            addIssue(issues, node.code(), "Section must have exactly one Shell, Screen, or Section parent.");
        }
        require(node.code(), node.sectionType(), "section_type", issues);
        if (node.repeatable() == null) {
            addIssue(issues, node.code(), "Section is missing required attribute: repeatable.");
        }
        require(node.code(), node.renderMode(), "render_mode", issues);
        require(node.code(), node.defaultState(), "default_state", issues);
        require(node.code(), node.controlSource(), "control_source", issues);

        boolean hasSectionChildren = outgoing.stream().anyMatch(edge -> "Section".equals(nodeFamily(nodeMap.get(edge.toId()))));
        boolean hasScreenChildren = outgoing.stream().anyMatch(edge -> "Screen".equals(nodeFamily(nodeMap.get(edge.toId()))));
        boolean hasElementChildren = outgoing.stream().anyMatch(edge -> "Element".equals(nodeFamily(nodeMap.get(edge.toId()))));
        int childModes = (hasSectionChildren ? 1 : 0) + (hasScreenChildren ? 1 : 0) + (hasElementChildren ? 1 : 0);
        if (childModes > 1) {
            addIssue(issues, node.code(), "Section must not mix child sections, screens, and elements at the same level.");
        }
        if (Boolean.FALSE.equals(node.repeatable()) && outgoing.size() == 1 && "Section".equals(nodeFamily(nodeMap.get(outgoing.get(0).toId())))) {
            addIssue(issues, node.code(), "Section is an orphan single-child section and should be flattened.");
        }
        if (Boolean.TRUE.equals(node.repeatable()) && !isMeaningfulRepeatableSection(node)) {
            addIssue(issues, node.code(), "Repeatable Section must represent a meaningful repeated pattern.");
        }
        if ("conditional".equals(node.renderMode()) && !"validation_rule_set".equals(node.controlSource())) {
            addIssue(issues, node.code(), "Conditional Section must use control_source = validation_rule_set.");
        }
        if ("static".equals(node.renderMode()) && !"none".equals(node.controlSource())) {
            addIssue(issues, node.code(), "Static Section must use control_source = none.");
        }
        if (!isBlank(node.semanticLevel())) {
            addIssue(issues, node.code(), "Semantic heading levels belong to Element, not Section.");
        }
    }

    private void validateElement(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> outgoing,
            List<SystemShellGraphEdgeResponse> incoming,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (incoming.size() != 1 || !"Section".equals(nodeFamily(nodeMap.get(incoming.get(0).fromId())))) {
            addIssue(issues, node.code(), "Element must have exactly one Section parent.");
        }
        require(node.code(), node.elementType(), "element_type", issues);
        require(node.code(), node.renderMode(), "render_mode", issues);
        require(node.code(), node.defaultState(), "default_state", issues);
        require(node.code(), node.controlSource(), "control_source", issues);
        if ("title".equals(node.elementType()) && isBlank(node.semanticLevel())) {
            addIssue(issues, node.code(), "Title Element should define semantic_level.");
        }
        if ("conditional".equals(node.renderMode()) && !"validation_rule_set".equals(node.controlSource())) {
            addIssue(issues, node.code(), "Conditional Element must use control_source = validation_rule_set.");
        }
        if ("static".equals(node.renderMode()) && !"none".equals(node.controlSource())) {
            addIssue(issues, node.code(), "Static Element must use control_source = none.");
        }
        for (SystemShellGraphEdgeResponse childEdge : outgoing) {
            if (!"Component".equals(nodeFamily(nodeMap.get(childEdge.toId())))) {
                addIssue(issues, node.code(), "Element may contain Component instances only.");
            }
        }
    }

    private void validateComponent(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> outgoing,
            List<SystemShellGraphEdgeResponse> incoming,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (incoming.size() != 1 || !"Element".equals(nodeFamily(nodeMap.get(incoming.get(0).fromId())))) {
            addIssue(issues, node.code(), "Component instance must be attached to exactly one Element parent.");
        }
        if (!outgoing.isEmpty()) {
            addIssue(issues, node.code(), "Component instance must remain a leaf.");
        }
        require(node.code(), node.assetType(), "asset_type", issues);
        require(node.code(), node.assetName(), "asset_name", issues);
        if (incoming.size() == 1) {
            SystemShellGraphNodeResponse parent = nodeMap.get(incoming.get(0).fromId());
            if (parent == null || !"Element".equals(parent.family())) {
                addIssue(issues, node.code(), "Component instance must target an existing Element parent.");
            }
        }
    }

    private void validateValidationRuleSet(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> usedByScreens,
            List<SystemShellGraphEdgeResponse> rules,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            List<SystemShellGraphNodeResponse> nodes,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("SHL\\d{2}\\.SCN\\d{2}\\.VRS\\d{2}")) {
            addIssue(issues, node.code(), "ValidationRuleSet code must follow SHL##.SCN##.VRS##.");
        }
        if (usedByScreens.size() != 1 || !"Screen".equals(nodeFamily(nodeMap.get(usedByScreens.get(0).fromId())))) {
            addIssue(issues, node.code(), "ValidationRuleSet must be referenced by exactly one Screen.");
        }
        require(node.code(), node.ruleSetType(), "rule_set_type", issues);
        require(node.code(), node.ruleSetScope(), "rule_set_scope", issues);
        if (rules.isEmpty() && usedByScreens.size() == 1 && screenHasConditionalDescendants(usedByScreens.get(0).fromId(), nodes)) {
            addIssue(issues, node.code(), "ValidationRuleSet must contain at least one ValidationRule when its screen has conditional UI nodes.");
        }
    }

    private void validateValidationRule(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> ownedByRuleSets,
            List<SystemShellGraphEdgeResponse> targets,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("SHL\\d{2}\\.SCN\\d{2}\\.VRS\\d{2}\\.R\\d{2,3}")) {
            addIssue(issues, node.code(), "ValidationRule code must extend its parent ValidationRuleSet code.");
        }
        if (ownedByRuleSets.size() != 1 || !"ValidationRuleSet".equals(nodeFamily(nodeMap.get(ownedByRuleSets.get(0).fromId())))) {
            addIssue(issues, node.code(), "ValidationRule must be owned by exactly one ValidationRuleSet.");
        }
        require(node.code(), node.conditionExpression(), "condition_expression", issues);
        require(node.code(), node.actionType(), "action_type", issues);
        if (node.priority() == null) {
            addIssue(issues, node.code(), "ValidationRule is missing required attribute: priority.");
        }
        if (node.stopProcessing() == null) {
            addIssue(issues, node.code(), "ValidationRule is missing required attribute: stop_processing.");
        }
        if (targets.isEmpty()) {
            addIssue(issues, node.code(), "ValidationRule must target at least one Screen, Section, or Element.");
        }
        for (SystemShellGraphEdgeResponse targetEdge : targets) {
            if (!Set.of("Screen", "Section", "Element").contains(nodeFamily(nodeMap.get(targetEdge.toId())))) {
                addIssue(issues, node.code(), "ValidationRule may target Screen, Section, or Element only.");
            }
        }
    }

    private void validateViewportProfile(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> referencedByScreens,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("VPR\\d{2}")) {
            addIssue(issues, node.code(), "ViewportProfile code must follow VPR##.");
        }
        if (node.viewportWidth() == null || node.viewportWidth() <= 0) {
            addIssue(issues, node.code(), "ViewportProfile must define a positive viewportWidth.");
        }
        if (node.viewportHeight() == null || node.viewportHeight() <= 0) {
            addIssue(issues, node.code(), "ViewportProfile must define a positive viewportHeight.");
        }
        require(node.code(), node.viewportCategory(), "viewport_category", issues);
    }

    private void validatePersona(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> canExecute,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("PER\\.[A-Z0-9_]+") && !node.code().matches("P\\d{3}")) {
            addIssue(issues, node.code(), "Persona code must follow the supported persona convention.");
        }
        if (canExecute.isEmpty()) {
            addIssue(issues, node.code(), "Persona must execute at least one Journey.");
        }
        for (SystemShellGraphEdgeResponse edge : canExecute) {
            if (!"Journey".equals(nodeFamily(nodeMap.get(edge.toId())))) {
                addIssue(issues, node.code(), "Persona may execute Journey only.");
            }
        }
    }

    private void validateJourney(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> executableBy,
            List<SystemShellGraphEdgeResponse> steps,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("J\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}")) {
            addIssue(issues, node.code(), "Journey code must follow the supported journey convention.");
        }
        if (executableBy.isEmpty()) {
            addIssue(issues, node.code(), "Journey must be executable by at least one Persona.");
        }
        if (steps.isEmpty()) {
            addIssue(issues, node.code(), "Journey must contain at least one JourneyStep.");
        }
        for (SystemShellGraphEdgeResponse edge : steps) {
            if (!"JourneyStep".equals(nodeFamily(nodeMap.get(edge.toId())))) {
                addIssue(issues, node.code(), "Journey may contain JourneyStep only.");
            }
        }
    }

    private void validateJourneyStep(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> withinJourneys,
            List<SystemShellGraphEdgeResponse> governedByRules,
            List<SystemShellGraphEdgeResponse> blockers,
            List<SystemShellGraphEdgeResponse> activatedScreens,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("J\\d{2}\\.JS\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}\\.ST\\d{3}")) {
            addIssue(issues, node.code(), "JourneyStep code must follow the supported journey-step convention.");
        }
        if (node.stepOrder() == null) {
            addIssue(issues, node.code(), "JourneyStep is missing required attribute: step_order.");
        }
        require(node.code(), node.executionMethod(), "execution_method", issues);
        if (!Set.of("mandatory", "conditional").contains(node.executionMethod())) {
            addIssue(issues, node.code(), "JourneyStep execution_method must be mandatory or conditional.");
        }
        if (withinJourneys.size() != 1 || !"Journey".equals(nodeFamily(nodeMap.get(withinJourneys.get(0).fromId())))) {
            addIssue(issues, node.code(), "JourneyStep must belong to exactly one Journey.");
        }
        if (activatedScreens.isEmpty()) {
            addIssue(issues, node.code(), "JourneyStep must activate at least one Screen.");
        }
        for (SystemShellGraphEdgeResponse edge : governedByRules) {
            if (!"BusinessRule".equals(nodeFamily(nodeMap.get(edge.toId())))) {
                addIssue(issues, node.code(), "JourneyStep may be governed by BusinessRule only.");
            }
        }
        for (SystemShellGraphEdgeResponse edge : blockers) {
            if (!"Blocker".equals(nodeFamily(nodeMap.get(edge.toId())))) {
                addIssue(issues, node.code(), "JourneyStep may reference Blocker only.");
            }
        }
    }

    private void validateBusinessRule(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> governingSteps,
            List<SystemShellGraphEdgeResponse> raises,
            Map<String, SystemShellGraphNodeResponse> nodeMap,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("BR\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}\\.ST\\d{3}\\.BR\\d{3}")) {
            addIssue(issues, node.code(), "BusinessRule code must follow the supported business-rule convention.");
        }
        require(node.code(), node.ruleScope(), "rule_scope", issues);
        require(node.code(), node.conditionExpression(), "condition_expression", issues);
        require(node.code(), node.executionEffect(), "execution_effect", issues);
        if (governingSteps.isEmpty()) {
            addIssue(issues, node.code(), "BusinessRule must govern at least one JourneyStep.");
        }
        for (SystemShellGraphEdgeResponse edge : raises) {
            if (!"Blocker".equals(nodeFamily(nodeMap.get(edge.toId())))) {
                addIssue(issues, node.code(), "BusinessRule may raise Blocker only.");
            }
        }
    }

    private void validateBlocker(
            SystemShellGraphNodeResponse node,
            List<SystemShellGraphEdgeResponse> blockedSteps,
            List<SystemShellGraphEdgeResponse> raisedByRules,
            Map<String, List<String>> issues
    ) {
        if (!node.code().matches("BL\\d{2}") && !node.code().matches("P\\d{3}\\.J\\d{3}\\.ST\\d{3}\\.B\\d{3}")) {
            addIssue(issues, node.code(), "Blocker code must follow the supported blocker convention.");
        }
        require(node.code(), node.blockerType(), "blocker_type", issues);
        require(node.code(), node.blockingEffect(), "blocking_effect", issues);
        if (blockedSteps.isEmpty() && raisedByRules.isEmpty()) {
            addIssue(issues, node.code(), "Blocker must be referenced by a JourneyStep or BusinessRule.");
        }
    }

    private void validateSharedAttributes(SystemShellGraphNodeResponse node, Map<String, List<String>> issues) {
        if (!Set.of("Persona", "Journey", "JourneyStep", "BusinessRule", "Blocker", "Application", "Shell", "Screen", "Section", "Element", "Component", "ValidationRuleSet", "ValidationRule", "ViewportProfile").contains(node.family())) {
            return;
        }
        require(node.code(), node.name(), "name", issues);
        require(node.code(), node.description(), "description", issues);
        require(node.code(), node.id(), "id", issues);
        if ("instance".equalsIgnoreCase(node.layer())
                && Set.of("Shell", "Screen", "Section", "Element", "Component").contains(node.family())) {
            require(node.code(), node.guid(), "guid", issues);
        }
        require(node.code(), node.status(), "status", issues);
        require(node.code(), node.domain(), "domain", issues);
        require(node.code(), node.hierarchyCode(), "hierarchy_code", issues);
        if (!"Element".equals(node.family()) && !isBlank(node.semanticLevel())) {
            addIssue(issues, node.code(), node.family() + " must not define semantic_level.");
        }
    }

    private void require(String code, Object value, String attribute, Map<String, List<String>> issues) {
        if (value == null || (value instanceof String string && string.isBlank())) {
            addIssue(issues, code, "Missing required attribute: " + attribute + ".");
        }
    }

    private void addIssue(Map<String, List<String>> issues, String code, String message) {
        issues.computeIfAbsent(code, ignored -> new ArrayList<>());
        List<String> nodeIssues = issues.get(code);
        if (!nodeIssues.contains(message)) {
            nodeIssues.add(message);
        }
    }

    private boolean screenHasConditionalDescendants(String screenCode, List<SystemShellGraphNodeResponse> nodes) {
        return nodes.stream()
                .filter(node -> "instance".equals(node.layer()))
                .filter(node -> node.code().startsWith(screenCode + "."))
                .filter(node -> Set.of("Section", "Element").contains(node.family()))
                .anyMatch(node -> "conditional".equals(node.renderMode()));
    }

    private String nodeFamily(SystemShellGraphNodeResponse node) {
        return node == null ? null : node.family();
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private boolean isMeaningfulRepeatableSection(SystemShellGraphNodeResponse node) {
        String sectionType = node.sectionType() == null ? "" : node.sectionType().toLowerCase(Locale.ROOT);
        String name = node.name() == null ? "" : node.name().toLowerCase(Locale.ROOT);
        if (sectionType.isBlank() || "section".equals(sectionType)) {
            return false;
        }

        return Stream.of("provider", "list", "item", "card", "row", "record", "tile", "entry", "result", "tab", "table", "grid")
                .anyMatch(keyword -> sectionType.contains(keyword) || name.contains(keyword));
    }
}
