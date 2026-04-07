package com.emsist.designhub.service;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;

enum GraphNodeType {
    SCREEN("Screen", "surfaceId", "n.label", "screen", "screens"),
    SCREEN_STATE("ScreenState", "stateId", "n.name", "screen-state", "screen-states"),
    TRANSITION("Transition", "transitionId", "n.name", "transition", "transitions"),
    TOPIC("Topic", "topicId", "n.name", "topic", "topics"),
    EDGE_CASE("EdgeCase", "edgeCaseId", "n.context", "edge-case", "edge-cases"),
    EXCEPTION_CASE("ExceptionCase", "exceptionId", "n.context", "exception-case", "exception-cases"),
    USER_STORY("UserStory", "storyId", "n.label", "story", "stories", "user-story", "user-stories"),
    INTEGRATION("Integration", "integrationId", "n.name", "integration", "integrations"),
    PROJECT_INSTANCE("ProjectInstance", "projectId", "n.name", "project", "projects", "project-instance", "project-instances"),
    MILESTONE("Milestone", "milestoneId", "n.name", "milestone", "milestones"),
    JOURNEY("Journey", "journeyId", "n.title", "journey", "journeys"),
    JOURNEY_STEP("JourneyStep", "stepId", "n.label", "journey-step", "journey-steps", "step", "steps"),
    IMPORT_SNAPSHOT("ImportSnapshot", "snapshotId", "n.snapshotId", "import-snapshot", "import-snapshots", "import", "imports"),
    EVIDENCE_RECORD("EvidenceRecord", "evidenceId", "n.evidenceId", "evidence-record", "evidence-records", "evidence", "evidences"),
    ENUM_DEFINITION("Enum", "enumId", "n.name", "enum", "enums"),
    DOMAIN_EVENT("Event", "eventCode", "n.displayName", "event-registry", "event-registries", "event", "events"),
    LOCALE_REGISTRY("Locale", "localeCode", "n.displayName", "locale", "locales"),
    TRANSLATION_KEY("TranslationKey", "key", "n.defaultText", "translation-key", "translation-keys"),
    PERSONA("Persona", "personaId", "n.name", "persona", "personas"),
    EXTERNAL_ARTIFACT("ExternalArtifact", "externalId", "coalesce(n.title, n.key, n.externalId)",
            "external-artifact", "external-artifacts", "external", "externals"),
    BUSINESS_DOMAIN("BusinessDomain", "domainCode", "n.name", "business-domain", "business-domains", "domain", "domains"),
    ORGANIZATION("Organization", "orgId", "n.name", "organization", "organizations", "org", "orgs"),
    BUSINESS_OBJECTIVE("BusinessObjective", "objectiveId", "n.title", "business-objective", "business-objectives", "objective", "objectives"),
    REQUIREMENT_PORTFOLIO("RequirementPortfolio", "portfolioId", "n.name", "portfolio", "portfolios", "requirement-portfolio", "requirement-portfolios"),
    EPIC("Epic", "epicId", "n.title", "epic", "epics"),
    FEATURE("Feature", "featureId", "n.title", "feature", "features"),
    DECISION("Decision", "decisionId", "n.title", "decision", "decisions"),
    ASSUMPTION("Assumption", "assumptionId", "n.statement", "assumption", "assumptions"),
    GOVERNANCE_CONSTRAINT("Constraint", "constraintId", "n.statement",
            "governance-constraint", "governance-constraints"),
    ASSESSMENT("Assessment", "assessmentId", "n.name", "assessment", "assessments"),
    RISK("Risk", "riskId", "n.title", "risk", "risks"),
    FINDING("Finding", "findingId", "n.summary", "finding", "findings"),
    BUG("Bug", "bugId", "coalesce(n.summary, n.externalKey)", "bug", "bugs"),
    TASK("Task", "taskId", "n.title", "task", "tasks"),
    API_CONTRACT("ApiContract", "contractId", "n.path", "api", "apis", "api-contract", "api-contracts"),
    DATA_ENTITY("DataEntity", "entityId", "n.name", "data-entity", "data-entities", "entity", "entities"),
    INTERACTION("Interaction", "interactionId", "n.element", "interaction", "interactions"),
    TOUCHPOINT("Touchpoint", "touchpointId", "n.label", "touchpoint", "touchpoints"),
    CHANNEL("Channel", "channelCode", "n.displayName", "channel", "channels"),
    BUSINESS_CAPABILITY("BusinessCapability", "capabilityId", "n.name", "business-capability", "business-capabilities", "capability", "capabilities"),
    BUSINESS_PROCESS("BusinessProcess", "processId", "n.name", "business-process", "business-processes", "process", "processes"),
    PROCESS_ACTIVITY("ProcessActivity", "activityId", "n.name", "process-activity", "process-activities", "activity", "activities"),
    PROCESS_GATEWAY("ProcessGateway", "gatewayId", "n.name", "process-gateway", "process-gateways", "gateway", "gateways"),
    PROCESS_EVENT("ProcessEvent", "eventId", "n.name", "process-event", "process-events"),
    APPLICATION("Application", "applicationId", "n.name", "application", "applications"),
    APPLICATION_COMPONENT("ApplicationComponent", "componentId", "n.name", "application-component", "application-components", "component", "components"),
    BUSINESS_OBJECT("BusinessObject", "objectId", "n.name", "business-object", "business-objects", "data-object", "data-objects"),
    INFORMATION_FLOW("InformationFlow", "flowId", "n.name", "information-flow", "information-flows", "flow", "flows"),
    DEPLOYMENT("Deployment", "deploymentId", "n.name", "deployment", "deployments"),
    INFRASTRUCTURE_NODE("InfrastructureNode", "nodeId", "n.name",
            "infrastructure-node", "infrastructure-nodes", "infra-node", "infra-nodes"),
    VALIDATION_RULE("ValidationRule", "validationRuleId", "coalesce(n.errorMessage, n.validationRuleId)",
            "validation-rule", "validation-rules"),
    REQUEST_SCHEMA("RequestSchema", "schemaId", "n.schemaId", "request-schema", "request-schemas"),
    RESPONSE_SCHEMA("ResponseSchema", "schemaId", "n.schemaId", "response-schema", "response-schemas"),
    ERROR_CONTRACT("ErrorContract", "errorContractId", "coalesce(n.errorCode, n.errorContractId)",
            "error-contract", "error-contracts"),
    CODE_ASSET("CodeAsset", "codeAssetId", "coalesce(n.className, n.filePath, n.codeAssetId)",
            "code-asset", "code-assets", "asset", "assets"),
    TEST_CASE("TestCase", "testCaseId", "n.title", "test-case", "test-cases"),
    RULE("Rule", "ruleId", "n.name", "rule", "rules"),
    MESSAGE("Message", "messageId", "coalesce(n.messageText, n.messageId)", "message", "messages"),
    GAP("Gap", "gapId", "coalesce(n.description, n.gapId)", "gap", "gaps"),
    OPEN_QUESTION("OpenQuestion", "questionId", "n.question", "open-question", "open-questions"),
    ACCEPTANCE_CRITERION("AcceptanceCriterion", "criterionId", "n.description",
            "acceptance-criterion", "acceptance-criteria", "criterion", "criteria"),
    DATA_FIELD("DataField", "fieldId", "n.name", "data-field", "data-fields", "field", "fields"),
    CODING_CONVENTION("CodingConvention", "conventionCode", "n.name",
            "coding-convention", "coding-conventions", "convention", "conventions"),
    QUALITY_CONSTRAINT("QualityConstraint", "constraintId", "n.name",
            "quality-constraint", "quality-constraints", "constraint", "constraints"),
    AGENT_POLICY("AgentPolicy", "policyId", "n.name", "agent-policy", "agent-policies", "policy", "policies"),
    SOURCE_REFERENCE("SourceReference", "sourceId", "coalesce(n.artifactPath, n.url, n.sourceId)",
            "source-reference", "source-references", "source", "sources"),
    BUSINESS_ROLE("BusinessRole", "roleKey", "n.displayName", "business-role", "business-roles", "role", "roles"),
    VALIDATION_ROLE("ValidationRole", "validationRoleKey", "n.displayName",
            "validation-role", "validation-roles"),
    PERMISSION("Permission", "permissionKey", "n.displayName", "permission", "permissions"),
    CONFIRMATION_DIALOG("ConfirmationDialog", "dialogId", "coalesce(n.triggerAction, n.dialogId)",
            "confirmation-dialog", "confirmation-dialogs", "dialog", "dialogs"),
    ERROR_CODE("ErrorCode", "code", "coalesce(n.messageText, n.code)", "error-code", "error-codes");

    private final String label;
    private final String idProperty;
    private final String displayProperty;
    private final List<String> aliasList;
    private final Set<String> aliasSet;

    GraphNodeType(String label, String idProperty, String displayProperty, String... aliases) {
        this.label = label;
        this.idProperty = idProperty;
        this.displayProperty = displayProperty;
        this.aliasList = Arrays.stream(aliases)
                .map(alias -> alias.toLowerCase(Locale.ROOT))
                .toList();
        this.aliasSet = Set.copyOf(aliasList);
    }

    String label() {
        return label;
    }

    String idProperty() {
        return idProperty;
    }

    String displayProperty() {
        return displayProperty;
    }

    String primaryAlias() {
        return aliasList.getFirst();
    }

    List<String> aliases() {
        return aliasList;
    }

    private boolean matches(String candidate) {
        return aliasSet.contains(candidate);
    }

    static GraphNodeType require(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            throw new IllegalArgumentException("type is required");
        }
        String normalized = candidate.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(type -> type.matches(normalized))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported graph object type: " + candidate));
    }

    static List<GraphNodeType> resolveRequested(String candidate) {
        if (candidate == null || candidate.isBlank() || "all".equalsIgnoreCase(candidate)) {
            return List.of(values());
        }
        return List.of(require(candidate));
    }

    static Optional<GraphNodeType> fromLabel(String label) {
        if (label == null || label.isBlank()) {
            return Optional.empty();
        }
        String normalized = label.toLowerCase(Locale.ROOT);
        return Arrays.stream(values())
                .filter(type -> type.label.toLowerCase(Locale.ROOT).equals(normalized))
                .findFirst();
    }
}
