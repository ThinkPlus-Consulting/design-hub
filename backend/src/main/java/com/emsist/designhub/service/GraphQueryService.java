package com.emsist.designhub.service;

import com.emsist.designhub.dto.GraphNodeReference;
import com.emsist.designhub.dto.GraphObjectSummaryResponse;
import com.emsist.designhub.dto.GraphRelationExpansionResponse;
import com.emsist.designhub.dto.JourneyTraversalResponse;
import com.emsist.designhub.dto.PersonaSummaryResponse;
import com.emsist.designhub.dto.PersonaTraversalResponse;
import com.emsist.designhub.dto.ApplicationArchitectureResponse;
import com.emsist.designhub.dto.ApplicationSummaryResponse;
import com.emsist.designhub.dto.BusinessArchitectureResponse;
import com.emsist.designhub.dto.BusinessCapabilitySummaryResponse;
import com.emsist.designhub.dto.ChannelSummaryResponse;
import com.emsist.designhub.dto.ChannelTraversalResponse;
import com.emsist.designhub.dto.DataArchitectureObjectSummaryResponse;
import com.emsist.designhub.dto.DataArchitectureResponse;
import com.emsist.designhub.dto.ExternalArtifactSummaryResponse;
import com.emsist.designhub.dto.ExternalArtifactTraversalResponse;
import com.emsist.designhub.dto.InfrastructureDeploymentSummaryResponse;
import com.emsist.designhub.dto.InfrastructureArchitectureResponse;
import com.emsist.designhub.dto.TraceabilityStoryResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class GraphQueryService {

    private static final int DEFAULT_LIMIT = 50;
    private static final int MAX_LIMIT = 200;
    private static final int DEFAULT_MAX_NEIGHBORS = 50;

    private static final String RELATED_NODE_ID_EXPR = """
            coalesce(
                toString(m.surfaceId),
                toString(m.stateId),
                toString(m.transitionId),
                toString(m.topicId),
                toString(m.edgeCaseId),
                toString(m.exceptionId),
                toString(m.integrationId),
                toString(m.questionId),
                toString(m.storyId),
                toString(m.projectId),
                toString(m.milestoneId),
                toString(m.journeyId),
                toString(m.stepId),
                toString(m.snapshotId),
                toString(m.evidenceId),
                toString(m.enumId),
                toString(m.eventCode),
                toString(m.localeCode),
                toString(m.key),
                toString(m.personaId),
                toString(m.domainCode),
                toString(m.orgId),
                toString(m.portfolioId),
                toString(m.epicId),
                toString(m.featureId),
                toString(m.objectiveId),
                toString(m.decisionId),
                toString(m.assumptionId),
                toString(m.assessmentId),
                toString(m.riskId),
                toString(m.contractId),
                toString(m.entityId),
                toString(m.touchpointId),
                toString(m.interactionId),
                toString(m.activityId),
                toString(m.gatewayId),
                toString(m.eventId),
                toString(m.flowId),
                toString(m.taskId),
                toString(m.gapId),
                toString(m.bugId),
                toString(m.capabilityId),
                toString(m.processId),
                toString(m.applicationId),
                toString(m.componentId),
                toString(m.objectId),
                toString(m.deploymentId),
                toString(m.criterionId),
                toString(m.fieldId),
                toString(m.constraintId),
                toString(m.conventionCode),
                toString(m.policyId),
                toString(m.sourceId),
                toString(m.validationRuleId),
                toString(m.schemaId),
                toString(m.errorContractId),
                toString(m.codeAssetId),
                toString(m.testCaseId),
                toString(m.ruleId),
                toString(m.messageId),
                toString(m.roleKey),
                toString(m.validationRoleKey),
                toString(m.channelCode),
                toString(m.permissionKey),
                toString(m.dialogId),
                toString(m.code),
                toString(m.gapId),
                toString(m.nodeId),
                toString(m.externalId),
                elementId(m)
            )
            """;

    private static final String RELATED_NODE_DISPLAY_EXPR = """
            coalesce(
                m.label,
                m.title,
                m.name,
                m.statement,
                m.displayName,
                CASE
                    WHEN m.method IS NOT NULL AND m.path IS NOT NULL THEN m.method + ' ' + m.path
                    ELSE null
                END,
                m.path,
                m.summary,
                %s,
                head(labels(m))
            )
            """.formatted(RELATED_NODE_ID_EXPR);

    private static final Comparator<GraphObjectSummaryResponse> OBJECT_COMPARATOR = Comparator
            .comparing(GraphObjectSummaryResponse::nodeType, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(response -> safe(response.displayName()), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(response -> safe(response.id()), String.CASE_INSENSITIVE_ORDER);

    private static final Comparator<GraphRelationExpansionResponse.RelationEdge> EDGE_COMPARATOR = Comparator
            .comparing(GraphRelationExpansionResponse.RelationEdge::relationType, String.CASE_INSENSITIVE_ORDER)
            .thenComparing(edge -> safe(edge.node().nodeType()), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(edge -> safe(edge.node().displayName()), String.CASE_INSENSITIVE_ORDER)
            .thenComparing(edge -> safe(edge.node().id()), String.CASE_INSENSITIVE_ORDER);

    private final Neo4jClient neo4jClient;

    public List<GraphObjectSummaryResponse> getObjects(
            String type,
            String status,
            String module,
            String search,
            Integer limit
    ) {
        int resolvedLimit = bounded(limit, DEFAULT_LIMIT, MAX_LIMIT);
        List<GraphNodeType> requestedTypes = GraphNodeType.resolveRequested(type);

        return requestedTypes.stream()
                .flatMap(nodeType -> fetchObjects(nodeType, status, module, search, resolvedLimit).stream())
                .sorted(OBJECT_COMPARATOR)
                .limit(resolvedLimit)
                .toList();
    }

    public long countObjects(String type) {
        GraphNodeType nodeType = GraphNodeType.require(type);
        String query = "MATCH (n:%s) RETURN count(n) AS count".formatted(nodeType.label());

        return neo4jClient.query(query)
                .fetchAs(Long.class)
                .mappedBy((typeSystem, record) -> record.get("count").asLong())
                .one()
                .orElse(0L);
    }

    public Optional<GraphRelationExpansionResponse> expandObject(String type, String id, Integer maxNeighbors) {
        GraphNodeType nodeType = GraphNodeType.require(type);
        int resolvedMaxNeighbors = bounded(maxNeighbors, DEFAULT_MAX_NEIGHBORS, MAX_LIMIT);

        return fetchExpansion(nodeType, id).map(response -> new GraphRelationExpansionResponse(
                response.root(),
                limitEdges(response.outgoing(), resolvedMaxNeighbors),
                limitEdges(response.incoming(), resolvedMaxNeighbors)
        ));
    }

    public Optional<GraphRelationExpansionResponse> getObjectTraversal(String type, String id) {
        GraphNodeType nodeType = GraphNodeType.require(type);
        return fetchExpansion(nodeType, id);
    }

    public Optional<PersonaTraversalResponse> getPersonaTraversal(String personaId) {
        return neo4jClient.query(PERSONA_TRAVERSAL_QUERY)
                .bind(personaId).to("personaId")
                .fetch()
                .first()
                .map(this::toPersonaTraversal);
    }

    public List<PersonaSummaryResponse> getPersonas(String status) {
        return neo4jClient.query(PERSONA_SUMMARY_QUERY)
                .bind(status == null ? "" : status).to("status")
                .fetch()
                .all()
                .stream()
                .map(this::toPersonaSummary)
                .sorted(Comparator.comparing(PersonaSummaryResponse::name,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(PersonaSummaryResponse::personaId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    public Optional<JourneyTraversalResponse> getJourneyTraversal(String journeyId) {
        return neo4jClient.query(JOURNEY_TRAVERSAL_QUERY)
                .bind(journeyId).to("journeyId")
                .fetch()
                .first()
                .map(this::toJourneyTraversal);
    }

    public List<ChannelSummaryResponse> getChannels(String channelType) {
        return neo4jClient.query(CHANNEL_SUMMARY_QUERY)
                .bind(channelType == null ? "" : channelType).to("channelType")
                .fetch()
                .all()
                .stream()
                .map(this::toChannelSummary)
                .sorted(Comparator.comparing(ChannelSummaryResponse::displayName,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(ChannelSummaryResponse::channelCode, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<ChannelTraversalResponse> getChannelTraversal(String channelCode) {
        return neo4jClient.query(CHANNEL_TRAVERSAL_QUERY)
                .bind(channelCode).to("channelCode")
                .fetch()
                .first()
                .map(this::toChannelTraversal);
    }

    public List<BusinessCapabilitySummaryResponse> getBusinessCapabilities(String domain) {
        return neo4jClient.query(BUSINESS_CAPABILITY_SUMMARY_QUERY)
                .bind(domain == null ? "" : domain).to("domain")
                .fetch()
                .all()
                .stream()
                .map(this::toBusinessCapabilitySummary)
                .sorted(Comparator.comparing(BusinessCapabilitySummaryResponse::name,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(BusinessCapabilitySummaryResponse::capabilityId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    public Optional<BusinessArchitectureResponse> getBusinessArchitecture(String capabilityId) {
        return neo4jClient.query(BUSINESS_ARCHITECTURE_QUERY)
                .bind(capabilityId).to("capabilityId")
                .fetch()
                .first()
                .map(this::toBusinessArchitecture);
    }

    public List<ApplicationSummaryResponse> getApplications(String applicationType) {
        return neo4jClient.query(APPLICATION_SUMMARY_QUERY)
                .bind(applicationType == null ? "" : applicationType).to("applicationType")
                .fetch()
                .all()
                .stream()
                .map(this::toApplicationSummary)
                .sorted(Comparator.comparing(ApplicationSummaryResponse::name,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(ApplicationSummaryResponse::applicationId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    public Optional<ApplicationArchitectureResponse> getApplicationArchitecture(String applicationId) {
        return neo4jClient.query(APPLICATION_ARCHITECTURE_QUERY)
                .bind(applicationId).to("applicationId")
                .fetch()
                .first()
                .map(this::toApplicationArchitecture);
    }

    public List<DataArchitectureObjectSummaryResponse> getDataObjects(String domain) {
        return neo4jClient.query(DATA_ARCHITECTURE_SUMMARY_QUERY)
                .bind(domain == null ? "" : domain).to("domain")
                .fetch()
                .all()
                .stream()
                .map(this::toDataObjectSummary)
                .sorted(Comparator.comparing(DataArchitectureObjectSummaryResponse::name,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(DataArchitectureObjectSummaryResponse::objectId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    public Optional<DataArchitectureResponse> getDataArchitecture(String objectId) {
        return neo4jClient.query(DATA_ARCHITECTURE_QUERY)
                .bind(objectId).to("objectId")
                .fetch()
                .first()
                .map(this::toDataArchitecture);
    }

    public List<InfrastructureDeploymentSummaryResponse> getInfrastructureDeployments(String environment) {
        return neo4jClient.query(INFRASTRUCTURE_SUMMARY_QUERY)
                .bind(environment == null ? "" : environment).to("environment")
                .fetch()
                .all()
                .stream()
                .map(this::toInfrastructureDeploymentSummary)
                .sorted(Comparator.comparing(InfrastructureDeploymentSummaryResponse::name,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(InfrastructureDeploymentSummaryResponse::deploymentId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    public Optional<InfrastructureArchitectureResponse> getInfrastructureArchitecture(String deploymentId) {
        return neo4jClient.query(INFRASTRUCTURE_ARCHITECTURE_QUERY)
                .bind(deploymentId).to("deploymentId")
                .fetch()
                .first()
                .map(this::toInfrastructureArchitecture);
    }

    public List<ExternalArtifactSummaryResponse> getExternalArtifacts(String system, String syncStatus) {
        return neo4jClient.query(EXTERNAL_ARTIFACT_SUMMARY_QUERY)
                .bind(system == null ? "" : system).to("system")
                .bind(syncStatus == null ? "" : syncStatus).to("syncStatus")
                .fetch()
                .all()
                .stream()
                .map(this::toExternalArtifactSummary)
                .sorted(Comparator.comparing(ExternalArtifactSummaryResponse::title,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(ExternalArtifactSummaryResponse::key,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(ExternalArtifactSummaryResponse::externalId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    public Optional<ExternalArtifactTraversalResponse> getExternalArtifact(String externalId) {
        return neo4jClient.query(EXTERNAL_ARTIFACT_TRAVERSAL_QUERY)
                .bind(externalId).to("externalId")
                .fetch()
                .first()
                .map(this::toExternalArtifactTraversal);
    }

    public Optional<TraceabilityStoryResponse> getStoryTraceability(String storyId) {
        return neo4jClient.query(STORY_TRACEABILITY_QUERY)
                .bind(storyId).to("storyId")
                .fetch()
                .first()
                .map(this::toStoryTraceability);
    }

    private List<GraphObjectSummaryResponse> fetchObjects(
            GraphNodeType nodeType,
            String status,
            String module,
            String search,
            int limit
    ) {
        return neo4jClient.query(objectQuery(nodeType))
                .bind(status == null ? "" : status).to("status")
                .bind(module == null ? "" : module).to("module")
                .bind(search == null ? "" : search).to("search")
                .bind(limit).to("limit")
                .fetch()
                .all()
                .stream()
                .map(this::toObjectSummary)
                .toList();
    }

    private Optional<GraphRelationExpansionResponse> fetchExpansion(GraphNodeType nodeType, String id) {
        return neo4jClient.query(expansionQuery(nodeType))
                .bind(id).to("id")
                .fetch()
                .first()
                .map(this::toExpansion);
    }

    private GraphObjectSummaryResponse toObjectSummary(Map<String, Object> row) {
        return new GraphObjectSummaryResponse(
                string(row, "id"),
                string(row, "nodeType"),
                string(row, "displayName"),
                string(row, "status"),
                string(row, "module"),
                string(row, "domain"),
                string(row, "routePath"),
                toLong(row.get("relationCount"))
        );
    }

    private GraphRelationExpansionResponse toExpansion(Map<String, Object> row) {
        return new GraphRelationExpansionResponse(
                toObjectSummary(map(row.get("root"))),
                relationEdges(row.get("outgoing")),
                relationEdges(row.get("incoming"))
        );
    }

    private List<GraphRelationExpansionResponse.RelationEdge> relationEdges(Object value) {
        return maps(value).stream()
                .filter(Objects::nonNull)
                .map(this::toRelationEdge)
                .filter(Objects::nonNull)
                .filter(edge -> edge.node() != null && edge.node().id() != null)
                .sorted(EDGE_COMPARATOR)
                .toList();
    }

    private GraphRelationExpansionResponse.RelationEdge toRelationEdge(Map<String, Object> row) {
        Map<String, Object> node = map(row.get("node"));
        if (node.isEmpty()) {
            return null;
        }
        return new GraphRelationExpansionResponse.RelationEdge(
                string(row, "relationType"),
                string(row, "direction"),
                toNodeReference(node)
        );
    }

    private PersonaTraversalResponse toPersonaTraversal(Map<String, Object> row) {
        return new PersonaTraversalResponse(
                string(row, "personaId"),
                string(row, "name"),
                string(row, "summary"),
                string(row, "status"),
                strings(row.get("roleKeys")),
                maps(row.get("journeys")).stream()
                        .filter(item -> item.get("journeyId") != null)
                        .map(item -> new PersonaTraversalResponse.JourneySummary(
                                string(item, "journeyId"),
                                string(item, "title"),
                                string(item, "status"),
                                toLong(item.get("stepCount")),
                                toLong(item.get("screenCount"))
                        ))
                        .sorted(Comparator.comparing(PersonaTraversalResponse.JourneySummary::title,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .toList(),
                distinctNodes(row.get("roles")),
                distinctNodes(row.get("channelReach")),
                toLong(row.get("screenCount")),
                toLong(row.get("storyCount"))
        );
    }

    private PersonaSummaryResponse toPersonaSummary(Map<String, Object> row) {
        return new PersonaSummaryResponse(
                string(row, "personaId"),
                string(row, "name"),
                string(row, "summary"),
                string(row, "status"),
                toLong(row.get("journeyCount")),
                toLong(row.get("screenCount")),
                toLong(row.get("storyCount")),
                toLong(row.get("channelCount"))
        );
    }

    private JourneyTraversalResponse toJourneyTraversal(Map<String, Object> row) {
        return new JourneyTraversalResponse(
                string(row, "journeyId"),
                string(row, "title"),
                string(row, "goalStatement"),
                string(row, "status"),
                toNodeReference(map(row.get("persona"))),
                maps(row.get("steps")).stream()
                        .filter(item -> item.get("stepId") != null)
                        .map(item -> new JourneyTraversalResponse.StepSummary(
                                string(item, "stepId"),
                                string(item, "label"),
                                toInt(item.get("orderIndex")),
                                toNodeReference(map(item.get("screen"))),
                                toNodeReference(map(item.get("touchpoint"))),
                                toNodeReference(map(item.get("interaction")))
                        ))
                        .sorted(Comparator.comparingInt(JourneyTraversalResponse.StepSummary::orderIndex)
                                .thenComparing(step -> safe(step.stepId()), String.CASE_INSENSITIVE_ORDER))
                        .toList()
        );
    }

    private TraceabilityStoryResponse toStoryTraceability(Map<String, Object> row) {
        GraphNodeReference objective = toNodeReference(map(row.get("objective")));
        GraphNodeReference portfolio = toNodeReference(map(row.get("portfolio")));
        GraphNodeReference epic = toNodeReference(map(row.get("epic")));
        GraphNodeReference feature = toNodeReference(map(row.get("feature")));
        GraphNodeReference story = toNodeReference(map(row.get("story")));
        List<GraphNodeReference> screens = distinctNodes(row.get("screens"));
        List<GraphNodeReference> interactions = distinctNodes(row.get("interactions"));
        List<GraphNodeReference> apis = distinctNodes(row.get("apis"));
        List<GraphNodeReference> dataEntities = distinctNodes(row.get("dataEntities"));
        List<GraphNodeReference> messages = distinctNodes(row.get("messages"));
        List<GraphNodeReference> tasks = distinctNodes(row.get("tasks"));

        List<String> missingSpineSegments = new ArrayList<>();
        if (objective == null) {
            missingSpineSegments.add("BusinessObjective");
        }
        if (portfolio == null) {
            missingSpineSegments.add("RequirementPortfolio");
        }
        if (epic == null) {
            missingSpineSegments.add("Epic");
        }
        if (feature == null) {
            missingSpineSegments.add("Feature");
        }
        if (screens.isEmpty()) {
            missingSpineSegments.add("Screen");
        }
        if (apis.isEmpty()) {
            missingSpineSegments.add("ApiContract");
        }

        return new TraceabilityStoryResponse(
                objective,
                portfolio,
                epic,
                feature,
                story,
                screens,
                interactions,
                apis,
                dataEntities,
                messages,
                tasks,
                List.copyOf(missingSpineSegments)
        );
    }

    private BusinessCapabilitySummaryResponse toBusinessCapabilitySummary(Map<String, Object> row) {
        return new BusinessCapabilitySummaryResponse(
                string(row, "capabilityId"),
                string(row, "name"),
                string(row, "domainCode"),
                string(row, "domainName"),
                toLong(row.get("processCount")),
                toLong(row.get("applicationCount")),
                toLong(row.get("featureCount")),
                toLong(row.get("organizationCount"))
        );
    }

    private BusinessArchitectureResponse toBusinessArchitecture(Map<String, Object> row) {
        return new BusinessArchitectureResponse(
                string(row, "capabilityId"),
                string(row, "name"),
                string(row, "description"),
                string(row, "status"),
                string(row, "domainCode"),
                string(row, "domainName"),
                distinctNodes(row.get("processes")),
                distinctNodes(row.get("applications")),
                distinctNodes(row.get("features")),
                distinctOrganizations(row.get("organizations"))
        );
    }

    private ApplicationSummaryResponse toApplicationSummary(Map<String, Object> row) {
        return new ApplicationSummaryResponse(
                string(row, "applicationId"),
                string(row, "name"),
                string(row, "applicationType"),
                string(row, "status"),
                toLong(row.get("componentCount")),
                toLong(row.get("apiCount")),
                toLong(row.get("screenCount")),
                toLong(row.get("featureCount")),
                toLong(row.get("dependencyCount")),
                strings(row.get("ownerNames"))
        );
    }

    private ApplicationArchitectureResponse toApplicationArchitecture(Map<String, Object> row) {
        return new ApplicationArchitectureResponse(
                string(row, "applicationId"),
                string(row, "name"),
                string(row, "description"),
                string(row, "applicationType"),
                string(row, "status"),
                strings(row.get("ownerNames")),
                toApplicationComponents(row.get("components")),
                distinctNodes(row.get("apis")),
                distinctNodes(row.get("screens")),
                distinctNodes(row.get("features")),
                toApplicationDependencies(row.get("dependencies"))
        );
    }

    private DataArchitectureObjectSummaryResponse toDataObjectSummary(Map<String, Object> row) {
        return new DataArchitectureObjectSummaryResponse(
                string(row, "objectId"),
                string(row, "name"),
                string(row, "domain"),
                string(row, "sensitivity"),
                string(row, "status"),
                toLong(row.get("mappedEntityCount")),
                toLong(row.get("flowCount")),
                toLong(row.get("apiCount")),
                toLong(row.get("screenCount"))
        );
    }

    private DataArchitectureResponse toDataArchitecture(Map<String, Object> row) {
        return new DataArchitectureResponse(
                string(row, "objectId"),
                string(row, "name"),
                string(row, "domain"),
                string(row, "description"),
                string(row, "sensitivity"),
                string(row, "status"),
                toDataEntities(row.get("entities")),
                toDataFlows(row.get("flows")),
                distinctNodes(row.get("apis")),
                distinctNodes(row.get("screens")),
                distinctNodes(row.get("children"))
        );
    }

    private InfrastructureDeploymentSummaryResponse toInfrastructureDeploymentSummary(Map<String, Object> row) {
        return new InfrastructureDeploymentSummaryResponse(
                string(row, "deploymentId"),
                string(row, "name"),
                string(row, "environment"),
                string(row, "status"),
                toLong(row.get("componentCount")),
                toLong(row.get("applicationCount")),
                toLong(row.get("infrastructureCount"))
        );
    }

    private InfrastructureArchitectureResponse toInfrastructureArchitecture(Map<String, Object> row) {
        return new InfrastructureArchitectureResponse(
                string(row, "deploymentId"),
                string(row, "name"),
                string(row, "environment"),
                string(row, "description"),
                string(row, "status"),
                distinctNodes(row.get("components")),
                toInfrastructureNodes(row.get("infrastructureNodes")),
                distinctNodes(row.get("applications")),
                distinctNodes(row.get("elements"))
        );
    }

    private ExternalArtifactSummaryResponse toExternalArtifactSummary(Map<String, Object> row) {
        return new ExternalArtifactSummaryResponse(
                string(row, "externalId"),
                string(row, "system"),
                string(row, "externalType"),
                string(row, "key"),
                string(row, "title"),
                string(row, "projectScope"),
                string(row, "workflowState"),
                string(row, "priority"),
                string(row, "owner"),
                string(row, "reporter"),
                strings(row.get("labels")),
                string(row, "url"),
                string(row, "syncStatus"),
                string(row, "lastSyncedAt"),
                string(row, "status"),
                toLong(row.get("representedObjectCount")),
                toLong(row.get("childCount")),
                toLong(row.get("dependencyCount")),
                toLong(row.get("relatedCount"))
        );
    }

    private ExternalArtifactTraversalResponse toExternalArtifactTraversal(Map<String, Object> row) {
        return new ExternalArtifactTraversalResponse(
                string(row, "externalId"),
                string(row, "system"),
                string(row, "externalType"),
                string(row, "key"),
                string(row, "title"),
                string(row, "projectScope"),
                string(row, "workflowState"),
                string(row, "priority"),
                string(row, "owner"),
                string(row, "reporter"),
                strings(row.get("labels")),
                mapFromEntries(row.get("customFields")),
                string(row, "url"),
                string(row, "syncStatus"),
                string(row, "lastSyncedAt"),
                string(row, "status"),
                toArtifactLinks(row.get("parents")),
                toArtifactLinks(row.get("children")),
                toArtifactLinks(row.get("dependencies")),
                toArtifactLinks(row.get("relatedArtifacts")),
                toArtifactLinks(row.get("duplicates")),
                distinctNodes(row.get("representedObjects"))
        );
    }

    private ChannelSummaryResponse toChannelSummary(Map<String, Object> row) {
        return new ChannelSummaryResponse(
                string(row, "channelCode"),
                string(row, "displayName"),
                string(row, "channelType"),
                toLong(row.get("touchpointCount")),
                toLong(row.get("screenCount"))
        );
    }

    private ChannelTraversalResponse toChannelTraversal(Map<String, Object> row) {
        return new ChannelTraversalResponse(
                string(row, "channelCode"),
                string(row, "displayName"),
                string(row, "channelType"),
                maps(row.get("touchpoints")).stream()
                        .filter(item -> item.get("touchpointId") != null)
                        .map(item -> new ChannelTraversalResponse.TouchpointSummary(
                                string(item, "touchpointId"),
                                string(item, "label"),
                                string(item, "surfaceId"),
                                strings(item.get("entryMechanisms")),
                                strings(item.get("roleKeys")),
                                strings(item.get("personaIds")),
                                toNodeReference(map(item.get("targetScreen")))
                        ))
                        .sorted(Comparator.comparing(ChannelTraversalResponse.TouchpointSummary::label,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                                .thenComparing(ChannelTraversalResponse.TouchpointSummary::touchpointId,
                                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .toList(),
                distinctNodes(row.get("screens")),
                maps(row.get("coverageGaps")).stream()
                        .filter(item -> item.get("touchpointId") != null)
                        .map(item -> new ChannelTraversalResponse.CoverageGap(
                                string(item, "touchpointId"),
                                string(item, "reason")
                        ))
                        .sorted(Comparator.comparing(ChannelTraversalResponse.CoverageGap::touchpointId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                        .toList(),
                distinctNodes(row.get("personaReach"))
        );
    }

    private List<ExternalArtifactTraversalResponse.ArtifactLinkSummary> toArtifactLinks(Object value) {
        LinkedHashMap<String, ExternalArtifactTraversalResponse.ArtifactLinkSummary> links = new LinkedHashMap<>();
        for (Map<String, Object> item : maps(value)) {
            String externalId = string(item, "externalId");
            if (externalId == null || links.containsKey(externalId)) {
                continue;
            }
            links.put(externalId, new ExternalArtifactTraversalResponse.ArtifactLinkSummary(
                    externalId,
                    string(item, "system"),
                    string(item, "externalType"),
                    string(item, "key"),
                    string(item, "title"),
                    string(item, "workflowState"),
                    string(item, "syncStatus"),
                    string(item, "status")
            ));
        }

        return links.values().stream()
                .sorted(Comparator.comparing(ExternalArtifactTraversalResponse.ArtifactLinkSummary::title,
                        Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(ExternalArtifactTraversalResponse.ArtifactLinkSummary::key,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER))
                        .thenComparing(ExternalArtifactTraversalResponse.ArtifactLinkSummary::externalId,
                                Comparator.nullsLast(String.CASE_INSENSITIVE_ORDER)))
                .toList();
    }

    private List<GraphNodeReference> distinctNodes(Object value) {
        LinkedHashSet<GraphNodeReference> uniqueNodes = maps(value).stream()
                .map(this::toNodeReference)
                .filter(node -> node != null && node.id() != null)
                .sorted(Comparator.comparing((GraphNodeReference node) -> safe(node.nodeType()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(node -> safe(node.displayName()), String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(node -> safe(node.id()), String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        return List.copyOf(uniqueNodes);
    }

    private List<BusinessArchitectureResponse.OrganizationSummary> distinctOrganizations(Object value) {
        LinkedHashMap<String, BusinessArchitectureResponse.OrganizationSummary> organizations = new LinkedHashMap<>();
        for (Map<String, Object> item : maps(value)) {
            String orgId = string(item, "id");
            if (orgId == null || organizations.containsKey(orgId)) {
                continue;
            }
            organizations.put(orgId, new BusinessArchitectureResponse.OrganizationSummary(
                    orgId,
                    string(item, "displayName"),
                    string(item, "organizationType"),
                    string(item, "status")
            ));
        }
        return List.copyOf(organizations.values());
    }

    private List<ApplicationArchitectureResponse.ComponentSummary> toApplicationComponents(Object value) {
        LinkedHashMap<String, ApplicationArchitectureResponse.ComponentSummary> components = new LinkedHashMap<>();
        for (Map<String, Object> item : maps(value)) {
            String componentId = string(item, "componentId");
            if (componentId == null || components.containsKey(componentId)) {
                continue;
            }
            components.put(componentId, new ApplicationArchitectureResponse.ComponentSummary(
                    componentId,
                    string(item, "name"),
                    string(item, "componentType"),
                    string(item, "frameworkFamily"),
                    string(item, "runtime"),
                    string(item, "modulePath"),
                    string(item, "status"),
                    distinctNodes(item.get("apis")),
                    distinctNodes(item.get("screens")),
                    distinctNodes(item.get("dependencies"))
            ));
        }
        return List.copyOf(components.values());
    }

    private List<ApplicationArchitectureResponse.DependencySummary> toApplicationDependencies(Object value) {
        LinkedHashMap<String, ApplicationArchitectureResponse.DependencySummary> dependencies = new LinkedHashMap<>();
        for (Map<String, Object> item : maps(value)) {
            String applicationId = string(item, "applicationId");
            if (applicationId == null || dependencies.containsKey(applicationId)) {
                continue;
            }
            dependencies.put(applicationId, new ApplicationArchitectureResponse.DependencySummary(
                    applicationId,
                    string(item, "name"),
                    string(item, "direction"),
                    string(item, "status")
            ));
        }
        return List.copyOf(dependencies.values());
    }

    private List<DataArchitectureResponse.EntitySummary> toDataEntities(Object value) {
        LinkedHashMap<String, DataArchitectureResponse.EntitySummary> entities = new LinkedHashMap<>();
        for (Map<String, Object> item : maps(value)) {
            String entityId = string(item, "entityId");
            if (entityId == null || entities.containsKey(entityId)) {
                continue;
            }
            entities.put(entityId, new DataArchitectureResponse.EntitySummary(
                    entityId,
                    string(item, "name"),
                    string(item, "entityType"),
                    toLong(item.get("fieldCount")),
                    string(item, "status")
            ));
        }
        return List.copyOf(entities.values());
    }

    private List<DataArchitectureResponse.FlowSummary> toDataFlows(Object value) {
        LinkedHashMap<String, DataArchitectureResponse.FlowSummary> flows = new LinkedHashMap<>();
        for (Map<String, Object> item : maps(value)) {
            String flowId = string(item, "flowId");
            if (flowId == null || flows.containsKey(flowId)) {
                continue;
            }
            flows.put(flowId, new DataArchitectureResponse.FlowSummary(
                    flowId,
                    string(item, "name"),
                    string(item, "direction"),
                    string(item, "status"),
                    string(item, "sourceApplicationId"),
                    string(item, "sourceApplicationName"),
                    string(item, "targetApplicationId"),
                    string(item, "targetApplicationName")
            ));
        }
        return List.copyOf(flows.values());
    }

    private List<InfrastructureArchitectureResponse.InfrastructureNodeSummary> toInfrastructureNodes(Object value) {
        LinkedHashMap<String, InfrastructureArchitectureResponse.InfrastructureNodeSummary> nodes = new LinkedHashMap<>();
        for (Map<String, Object> item : maps(value)) {
            String nodeId = string(item, "nodeId");
            if (nodeId == null || nodes.containsKey(nodeId)) {
                continue;
            }
            nodes.put(nodeId, new InfrastructureArchitectureResponse.InfrastructureNodeSummary(
                    nodeId,
                    string(item, "name"),
                    string(item, "nodeType"),
                    string(item, "location"),
                    string(item, "status")
            ));
        }
        return List.copyOf(nodes.values());
    }

    private GraphNodeReference toNodeReference(Map<String, Object> row) {
        if (row.isEmpty()) {
            return null;
        }
        String id = string(row, "id");
        if (id == null) {
            return null;
        }
        return new GraphNodeReference(
                id,
                string(row, "nodeType"),
                string(row, "displayName"),
                string(row, "status")
        );
    }

    private List<GraphRelationExpansionResponse.RelationEdge> limitEdges(
            List<GraphRelationExpansionResponse.RelationEdge> edges,
            int maxNeighbors
    ) {
        return edges.stream()
                .limit(maxNeighbors)
                .toList();
    }

    private String objectQuery(GraphNodeType nodeType) {
        return """
                MATCH (n:%s)
                WHERE ($status = '' OR toString(n.status) = $status)
                  AND ($module = '' OR n.module = $module)
                  AND ($search = ''
                       OR toLower(coalesce(%s, toString(n.%s))) CONTAINS toLower($search)
                       OR toLower(toString(n.%s)) CONTAINS toLower($search))
                RETURN n.%s AS id,
                       '%s' AS nodeType,
                       coalesce(%s, toString(n.%s)) AS displayName,
                       toString(n.status) AS status,
                       n.module AS module,
                       n.domain AS domain,
                       n.routePath AS routePath,
                       COUNT { (n)--() } AS relationCount
                ORDER BY coalesce(%s, toString(n.%s))
                LIMIT $limit
                """.formatted(
                nodeType.label(),
                nodeType.displayProperty(),
                nodeType.idProperty(),
                nodeType.idProperty(),
                nodeType.idProperty(),
                nodeType.label(),
                nodeType.displayProperty(),
                nodeType.idProperty(),
                nodeType.displayProperty(),
                nodeType.idProperty()
        );
    }

    private String expansionQuery(GraphNodeType nodeType) {
        return """
                MATCH (n:%s {%s: $id})
                CALL (n) {
                    OPTIONAL MATCH (n)-[r]->(m)
                    RETURN collect(
                        CASE
                            WHEN r IS NULL OR m IS NULL THEN null
                            ELSE {
                                relationType: type(r),
                                direction: 'OUTGOING',
                                node: {
                                    id: %s,
                                    nodeType: head(labels(m)),
                                    displayName: %s,
                                    status: toString(m.status)
                                }
                            }
                        END
                    ) AS outgoing
                }
                CALL (n) {
                    OPTIONAL MATCH (m)-[r]->(n)
                    RETURN collect(
                        CASE
                            WHEN r IS NULL OR m IS NULL THEN null
                            ELSE {
                                relationType: type(r),
                                direction: 'INCOMING',
                                node: {
                                    id: %s,
                                    nodeType: head(labels(m)),
                                    displayName: %s,
                                    status: toString(m.status)
                                }
                            }
                        END
                    ) AS incoming
                }
                RETURN {
                    id: n.%s,
                    nodeType: '%s',
                    displayName: coalesce(%s, toString(n.%s)),
                    status: toString(n.status),
                    module: n.module,
                    domain: n.domain,
                    routePath: n.routePath,
                    relationCount: COUNT { (n)--() }
                } AS root,
                outgoing,
                incoming
                """.formatted(
                nodeType.label(),
                nodeType.idProperty(),
                RELATED_NODE_ID_EXPR,
                RELATED_NODE_DISPLAY_EXPR,
                RELATED_NODE_ID_EXPR,
                RELATED_NODE_DISPLAY_EXPR,
                nodeType.idProperty(),
                nodeType.label(),
                nodeType.displayProperty(),
                nodeType.idProperty()
        );
    }

    private static int bounded(Integer value, int defaultValue, int maximum) {
        int resolved = value == null ? defaultValue : value;
        return Math.max(1, Math.min(resolved, maximum));
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private String string(Map<String, Object> row, String key) {
        Object value = row.get(key);
        return value == null ? null : String.valueOf(value);
    }

    private int toInt(Object value) {
        return value instanceof Number number ? number.intValue() : 0;
    }

    private long toLong(Object value) {
        return value instanceof Number number ? number.longValue() : 0L;
    }

    private Map<String, Object> map(Object value) {
        if (value instanceof Map<?, ?> rawMap) {
            Map<String, Object> mapped = new LinkedHashMap<>();
            for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                if (entry.getKey() instanceof String key) {
                    mapped.put(key, entry.getValue());
                }
            }
            return mapped;
        }
        return Map.of();
    }

    private List<Map<String, Object>> maps(Object value) {
        if (value instanceof Collection<?> collection) {
            List<Map<String, Object>> mapped = new ArrayList<>(collection.size());
            for (Object item : collection) {
                mapped.add(map(item));
            }
            return mapped;
        }
        return List.of();
    }

    private List<String> strings(Object value) {
        if (value instanceof Collection<?> collection) {
            return collection.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .toList();
        }
        return List.of();
    }

    private Map<String, String> mapFromEntries(Object value) {
        LinkedHashMap<String, String> mapped = new LinkedHashMap<>();
        for (String entry : strings(value)) {
            int separatorIndex = entry.indexOf('=');
            if (separatorIndex <= 0) {
                continue;
            }
            String key = entry.substring(0, separatorIndex).trim();
            if (key.isEmpty()) {
                continue;
            }
            mapped.put(key, entry.substring(separatorIndex + 1).trim());
        }
        return Map.copyOf(mapped);
    }

    private static final String PERSONA_TRAVERSAL_QUERY = """
            MATCH (p:Persona {personaId: $personaId})
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(j:Journey)
                OPTIONAL MATCH (j)-[:HAS_STEP]->(step:JourneyStep)
                OPTIONAL MATCH (j)-[:HAS_STEP]->(:JourneyStep)-[:USES_SCREEN]->(screen:Screen)
                WITH j, count(DISTINCT step) AS stepCount, count(DISTINCT screen) AS screenCount
                RETURN collect(
                    DISTINCT CASE
                        WHEN j IS NULL THEN null
                        ELSE {
                            journeyId: j.journeyId,
                            title: j.title,
                            status: toString(j.status),
                            stepCount: stepCount,
                            screenCount: screenCount
                        }
                    END
                ) AS journeys
            }
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(:Journey)-[:HAS_STEP]->(:JourneyStep)-[:STARTS_AT_TOUCHPOINT]->(:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(channel:Channel)
                RETURN collect(
                    DISTINCT CASE
                        WHEN channel IS NULL THEN null
                        ELSE {
                            id: channel.channelCode,
                            nodeType: 'Channel',
                            displayName: coalesce(channel.displayName, channel.channelCode),
                            status: null
                        }
                    END
                ) AS channelReach
            }
            CALL (p) {
                OPTIONAL MATCH (p)<-[:USED_BY_PERSONA]-(screen:Screen)-[:ACCESSIBLE_BY_ROLE]->(role:BusinessRole)
                RETURN collect(
                    DISTINCT CASE
                        WHEN role IS NULL THEN null
                        ELSE {
                            id: role.roleKey,
                            nodeType: 'BusinessRole',
                            displayName: coalesce(role.displayName, role.roleKey),
                            status: toString(role.status)
                        }
                    END
                ) AS roles
            }
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(:Journey)-[:HAS_STEP]->(:JourneyStep)-[:USES_SCREEN]->(screen:Screen)
                RETURN count(DISTINCT screen) AS screenCount
            }
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(:Journey)-[:HAS_STEP]->(:JourneyStep)-[:USES_SCREEN]->(:Screen)<-[:DELIVERS]-(story:UserStory)
                RETURN count(DISTINCT story) AS storyCount
            }
            RETURN p.personaId AS personaId,
                   p.name AS name,
                   p.summary AS summary,
                   toString(p.status) AS status,
                   coalesce(p.roleKeys, []) AS roleKeys,
                   journeys,
                   roles,
                   channelReach,
                   screenCount,
                   storyCount
            """;

    private static final String PERSONA_SUMMARY_QUERY = """
            MATCH (p:Persona)
            WHERE ($status = '' OR toString(p.status) = $status)
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(j:Journey)
                RETURN count(DISTINCT j) AS journeyCount
            }
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(:Journey)-[:HAS_STEP]->(:JourneyStep)-[:USES_SCREEN]->(screen:Screen)
                RETURN count(DISTINCT screen) AS screenCount
            }
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(:Journey)-[:HAS_STEP]->(:JourneyStep)-[:USES_SCREEN]->(:Screen)<-[:DELIVERS]-(story:UserStory)
                RETURN count(DISTINCT story) AS storyCount
            }
            CALL (p) {
                OPTIONAL MATCH (p)<-[:PERFORMED_BY_PERSONA]-(:Journey)-[:HAS_STEP]->(:JourneyStep)-[:STARTS_AT_TOUCHPOINT]->(:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(channel:Channel)
                RETURN count(DISTINCT channel) AS channelCount
            }
            RETURN p.personaId AS personaId,
                   coalesce(p.name, p.personaId) AS name,
                   p.summary AS summary,
                   toString(p.status) AS status,
                   journeyCount,
                   screenCount,
                   storyCount,
                   channelCount
            ORDER BY coalesce(p.name, p.personaId)
            """;

    private static final String JOURNEY_TRAVERSAL_QUERY = """
            MATCH (j:Journey {journeyId: $journeyId})
            OPTIONAL MATCH (j)-[:PERFORMED_BY_PERSONA]->(persona:Persona)
            CALL (j) {
                OPTIONAL MATCH (j)-[:HAS_STEP]->(step:JourneyStep)
                OPTIONAL MATCH (step)-[:USES_SCREEN]->(screen:Screen)
                OPTIONAL MATCH (step)-[:STARTS_AT_TOUCHPOINT]->(touchpoint:Touchpoint)
                OPTIONAL MATCH (step)-[:EXECUTES_INTERACTION]->(interaction:Interaction)
                RETURN collect(
                    DISTINCT CASE
                        WHEN step IS NULL THEN null
                        ELSE {
                            stepId: step.stepId,
                            label: step.label,
                            orderIndex: coalesce(step.orderIndex, 0),
                            screen: CASE
                                WHEN screen IS NULL THEN null
                                ELSE {
                                    id: screen.surfaceId,
                                    nodeType: 'Screen',
                                    displayName: coalesce(screen.label, screen.surfaceId),
                                    status: toString(screen.status)
                                }
                            END,
                            touchpoint: CASE
                                WHEN touchpoint IS NULL THEN null
                                ELSE {
                                    id: touchpoint.touchpointId,
                                    nodeType: 'Touchpoint',
                                    displayName: coalesce(touchpoint.label, touchpoint.touchpointId),
                                    status: null
                                }
                            END,
                            interaction: CASE
                                WHEN interaction IS NULL THEN null
                                ELSE {
                                    id: interaction.interactionId,
                                    nodeType: 'Interaction',
                                    displayName: coalesce(interaction.element, interaction.interactionId),
                                    status: null
                                }
                            END
                        }
                    END
                ) AS steps
            }
            RETURN j.journeyId AS journeyId,
                   j.title AS title,
                   j.goalStatement AS goalStatement,
                   toString(j.status) AS status,
                   CASE
                       WHEN persona IS NULL THEN null
                       ELSE {
                           id: persona.personaId,
                           nodeType: 'Persona',
                           displayName: coalesce(persona.name, persona.personaId),
                           status: toString(persona.status)
                       }
                   END AS persona,
                   steps
            """;

    private static final String CHANNEL_SUMMARY_QUERY = """
            MATCH (ch:Channel)
            WHERE ($channelType = '' OR toString(ch.channelType) = $channelType)
            OPTIONAL MATCH (tp:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(ch)
            OPTIONAL MATCH (tp)-[:TARGETS]->(screen:Screen)
            RETURN ch.channelCode AS channelCode,
                   coalesce(ch.displayName, ch.channelCode) AS displayName,
                   toString(ch.channelType) AS channelType,
                   count(DISTINCT tp) AS touchpointCount,
                   count(DISTINCT screen) AS screenCount
            ORDER BY coalesce(ch.displayName, ch.channelCode)
            """;

    private static final String CHANNEL_TRAVERSAL_QUERY = """
            MATCH (ch:Channel {channelCode: $channelCode})
            CALL (ch) {
                OPTIONAL MATCH (tp:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(ch)
                OPTIONAL MATCH (tp)-[:TARGETS]->(screen:Screen)
                OPTIONAL MATCH (tp)-[:HAS_ENTRY_MODE]->(entry:EntryMode)
                WITH tp, screen, collect(DISTINCT entry.mechanism) AS entryMechanisms
                RETURN collect(
                    DISTINCT CASE
                        WHEN tp IS NULL THEN null
                        ELSE {
                            touchpointId: tp.touchpointId,
                            label: coalesce(tp.label, tp.touchpointId),
                            surfaceId: tp.surfaceId,
                            entryMechanisms: [mechanism IN entryMechanisms WHERE mechanism IS NOT NULL],
                            roleKeys: coalesce(tp.roleKeys, []),
                            personaIds: coalesce(tp.personaIds, []),
                            targetScreen: CASE
                                WHEN screen IS NULL THEN null
                                ELSE {
                                    id: screen.surfaceId,
                                    nodeType: 'Screen',
                                    displayName: coalesce(screen.label, screen.surfaceId),
                                    status: toString(screen.status)
                                }
                            END
                        }
                    END
                ) AS touchpoints
            }
            CALL (ch) {
                OPTIONAL MATCH (tp:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(ch)
                OPTIONAL MATCH (tp)-[:TARGETS]->(screen:Screen)
                RETURN collect(
                    DISTINCT CASE
                        WHEN screen IS NULL THEN null
                        ELSE {
                            id: screen.surfaceId,
                            nodeType: 'Screen',
                            displayName: coalesce(screen.label, screen.surfaceId),
                            status: toString(screen.status)
                        }
                    END
                ) AS screens
            }
            CALL (ch) {
                OPTIONAL MATCH (tp:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(ch)
                WHERE NOT EXISTS { (tp)-[:TARGETS]->(:Screen) }
                RETURN collect(
                    DISTINCT CASE
                        WHEN tp IS NULL THEN null
                        ELSE {
                            touchpointId: tp.touchpointId,
                            reason: 'Touchpoint is not linked to a target screen'
                        }
                    END
                ) AS coverageGaps
            }
            CALL (ch) {
                OPTIONAL MATCH (tp:Touchpoint)-[:DELIVERED_VIA_CHANNEL]->(ch)
                OPTIONAL MATCH (tp)<-[:STARTS_AT_TOUCHPOINT]-(:JourneyStep)<-[:HAS_STEP]-(:Journey)-[:PERFORMED_BY_PERSONA]->(persona:Persona)
                RETURN collect(
                    DISTINCT CASE
                        WHEN persona IS NULL THEN null
                        ELSE {
                            id: persona.personaId,
                            nodeType: 'Persona',
                            displayName: coalesce(persona.name, persona.personaId),
                            status: toString(persona.status)
                        }
                    END
                ) AS personaReach
            }
            RETURN ch.channelCode AS channelCode,
                   coalesce(ch.displayName, ch.channelCode) AS displayName,
                   toString(ch.channelType) AS channelType,
                   touchpoints,
                   screens,
                   coverageGaps,
                   personaReach
            """;

    private static final String BUSINESS_CAPABILITY_SUMMARY_QUERY = """
            MATCH (cap:BusinessCapability)
            OPTIONAL MATCH (dom:BusinessDomain)-[:HAS_CAPABILITY]->(cap)
            WITH cap, dom
            WHERE ($domain = ''
                   OR coalesce(dom.domainCode, '') = $domain
                   OR coalesce(dom.name, '') = $domain)
            OPTIONAL MATCH (cap)-[:REALIZED_BY_PROCESS]->(process:BusinessProcess)
            OPTIONAL MATCH (cap)-[:ENABLED_BY]->(application:Application)
            OPTIONAL MATCH (cap)<-[:REALIZES]-(feature:Feature)
            OPTIONAL MATCH (organization:Organization)-[:OWNS]->(application)
            RETURN cap.capabilityId AS capabilityId,
                   coalesce(cap.name, cap.capabilityId) AS name,
                   dom.domainCode AS domainCode,
                   dom.name AS domainName,
                   count(DISTINCT process) AS processCount,
                   count(DISTINCT application) AS applicationCount,
                   count(DISTINCT feature) AS featureCount,
                   count(DISTINCT organization) AS organizationCount
            ORDER BY coalesce(cap.name, cap.capabilityId)
            """;

    private static final String BUSINESS_ARCHITECTURE_QUERY = """
            MATCH (cap:BusinessCapability {capabilityId: $capabilityId})
            OPTIONAL MATCH (dom:BusinessDomain)-[:HAS_CAPABILITY]->(cap)
            CALL (cap) {
                OPTIONAL MATCH (cap)-[:REALIZED_BY_PROCESS]->(process:BusinessProcess)
                RETURN collect(
                    DISTINCT CASE
                        WHEN process IS NULL THEN null
                        ELSE {
                            id: process.processId,
                            nodeType: 'BusinessProcess',
                            displayName: coalesce(process.name, process.processId),
                            status: toString(process.status)
                        }
                    END
                ) AS processes
            }
            CALL (cap) {
                OPTIONAL MATCH (cap)-[:ENABLED_BY]->(application:Application)
                RETURN collect(
                    DISTINCT CASE
                        WHEN application IS NULL THEN null
                        ELSE {
                            id: application.applicationId,
                            nodeType: 'Application',
                            displayName: coalesce(application.name, application.applicationId),
                            status: toString(application.status)
                        }
                    END
                ) AS applications
            }
            CALL (cap) {
                OPTIONAL MATCH (cap)<-[:REALIZES]-(feature:Feature)
                RETURN collect(
                    DISTINCT CASE
                        WHEN feature IS NULL THEN null
                        ELSE {
                            id: feature.featureId,
                            nodeType: 'Feature',
                            displayName: coalesce(feature.title, feature.featureId),
                            status: toString(feature.status)
                        }
                    END
                ) AS features
            }
            CALL (cap) {
                OPTIONAL MATCH (cap)-[:ENABLED_BY]->(application:Application)
                OPTIONAL MATCH (organization:Organization)-[:OWNS]->(application)
                RETURN collect(
                    DISTINCT CASE
                        WHEN organization IS NULL THEN null
                        ELSE {
                            id: organization.orgId,
                            nodeType: 'Organization',
                            displayName: coalesce(organization.name, organization.orgId),
                            organizationType: toString(organization.organizationType),
                            status: toString(organization.status)
                        }
                    END
                ) AS organizations
            }
            RETURN cap.capabilityId AS capabilityId,
                   coalesce(cap.name, cap.capabilityId) AS name,
                   cap.description AS description,
                   toString(cap.status) AS status,
                   dom.domainCode AS domainCode,
                   dom.name AS domainName,
                   processes,
                   applications,
                   features,
                   organizations
            """;

    private static final String APPLICATION_SUMMARY_QUERY = """
            MATCH (app:Application)
            WHERE ($applicationType = '' OR coalesce(toString(app.applicationType), '') = $applicationType)
            OPTIONAL MATCH (owner:Organization)-[:OWNS]->(app)
            WITH app, collect(DISTINCT owner.name) AS ownerNames
            OPTIONAL MATCH (app)-[:HAS_COMPONENT]->(component:ApplicationComponent)
            OPTIONAL MATCH (component)-[:EXPOSES]->(api:ApiContract)
            OPTIONAL MATCH (component)-[:SUPPORTS_SCREEN]->(screen:Screen)
            OPTIONAL MATCH (app)-[:REALIZES]->(feature:Feature)
            OPTIONAL MATCH (component)-[:DEPENDS_ON_COMPONENT]->(:ApplicationComponent)<-[:HAS_COMPONENT]-(dependencyApp:Application)
            WHERE dependencyApp IS NULL OR dependencyApp <> app
            RETURN app.applicationId AS applicationId,
                   coalesce(app.name, app.applicationId) AS name,
                   toString(app.applicationType) AS applicationType,
                   toString(app.status) AS status,
                   count(DISTINCT component) AS componentCount,
                   count(DISTINCT api) AS apiCount,
                   count(DISTINCT screen) AS screenCount,
                   count(DISTINCT feature) AS featureCount,
                   count(DISTINCT dependencyApp) AS dependencyCount,
                   [ownerName IN ownerNames WHERE ownerName IS NOT NULL] AS ownerNames
            ORDER BY coalesce(app.name, app.applicationId)
            """;

    private static final String APPLICATION_ARCHITECTURE_QUERY = """
            MATCH (app:Application {applicationId: $applicationId})
            CALL (app) {
                OPTIONAL MATCH (owner:Organization)-[:OWNS]->(app)
                RETURN collect(DISTINCT owner.name) AS ownerNames
            }
            CALL (app) {
                OPTIONAL MATCH (app)-[:HAS_COMPONENT]->(component:ApplicationComponent)
                CALL (component) {
                    OPTIONAL MATCH (component)-[:EXPOSES]->(api:ApiContract)
                    RETURN collect(
                        DISTINCT CASE
                            WHEN api IS NULL THEN null
                            ELSE {
                                id: api.contractId,
                                nodeType: 'ApiContract',
                                displayName: coalesce(
                                    CASE
                                        WHEN api.method IS NOT NULL AND api.path IS NOT NULL THEN api.method + ' ' + api.path
                                        ELSE null
                                    END,
                                    api.contractId
                                ),
                                status: toString(api.status)
                            }
                        END
                    ) AS apis
                }
                CALL (component) {
                    OPTIONAL MATCH (component)-[:SUPPORTS_SCREEN]->(screen:Screen)
                    RETURN collect(
                        DISTINCT CASE
                            WHEN screen IS NULL THEN null
                            ELSE {
                                id: screen.surfaceId,
                                nodeType: 'Screen',
                                displayName: coalesce(screen.label, screen.surfaceId),
                                status: toString(screen.status)
                            }
                        END
                    ) AS screens
                }
                CALL (component, app) {
                    OPTIONAL MATCH (component)-[:DEPENDS_ON_COMPONENT]->(dependency:ApplicationComponent)
                    RETURN collect(
                        DISTINCT CASE
                            WHEN dependency IS NULL THEN null
                            ELSE {
                                id: dependency.componentId,
                                nodeType: 'ApplicationComponent',
                                displayName: coalesce(dependency.name, dependency.componentId),
                                status: toString(dependency.status)
                            }
                        END
                    ) AS dependencies
                }
                RETURN collect(
                    DISTINCT CASE
                        WHEN component IS NULL THEN null
                        ELSE {
                            componentId: component.componentId,
                            name: coalesce(component.name, component.componentId),
                            componentType: component.componentType,
                            frameworkFamily: component.frameworkFamily,
                            runtime: component.runtime,
                            modulePath: component.modulePath,
                            status: toString(component.status),
                            apis: apis,
                            screens: screens,
                            dependencies: dependencies
                        }
                    END
                ) AS components
            }
            CALL (app) {
                OPTIONAL MATCH (app)-[:HAS_COMPONENT]->(:ApplicationComponent)-[:EXPOSES]->(api:ApiContract)
                RETURN collect(
                    DISTINCT CASE
                        WHEN api IS NULL THEN null
                        ELSE {
                            id: api.contractId,
                            nodeType: 'ApiContract',
                            displayName: coalesce(
                                CASE
                                    WHEN api.method IS NOT NULL AND api.path IS NOT NULL THEN api.method + ' ' + api.path
                                    ELSE null
                                END,
                                api.contractId
                            ),
                            status: toString(api.status)
                        }
                    END
                ) AS apis
            }
            CALL (app) {
                OPTIONAL MATCH (app)-[:HAS_COMPONENT]->(:ApplicationComponent)-[:SUPPORTS_SCREEN]->(screen:Screen)
                RETURN collect(
                    DISTINCT CASE
                        WHEN screen IS NULL THEN null
                        ELSE {
                            id: screen.surfaceId,
                            nodeType: 'Screen',
                            displayName: coalesce(screen.label, screen.surfaceId),
                            status: toString(screen.status)
                        }
                    END
                ) AS screens
            }
            CALL (app) {
                OPTIONAL MATCH (app)-[:REALIZES]->(feature:Feature)
                RETURN collect(
                    DISTINCT CASE
                        WHEN feature IS NULL THEN null
                        ELSE {
                            id: feature.featureId,
                            nodeType: 'Feature',
                            displayName: coalesce(feature.title, feature.featureId),
                            status: toString(feature.status)
                        }
                    END
                ) AS features
            }
            CALL (app) {
                OPTIONAL MATCH (app)-[:HAS_COMPONENT]->(:ApplicationComponent)-[:DEPENDS_ON_COMPONENT]->(:ApplicationComponent)<-[:HAS_COMPONENT]-(dependencyApp:Application)
                WHERE dependencyApp <> app
                RETURN collect(
                    DISTINCT CASE
                        WHEN dependencyApp IS NULL THEN null
                        ELSE {
                            applicationId: dependencyApp.applicationId,
                            name: coalesce(dependencyApp.name, dependencyApp.applicationId),
                            direction: 'OUTBOUND',
                            status: toString(dependencyApp.status)
                        }
                    END
                ) AS dependencies
            }
            RETURN app.applicationId AS applicationId,
                   coalesce(app.name, app.applicationId) AS name,
                   app.description AS description,
                   toString(app.applicationType) AS applicationType,
                   toString(app.status) AS status,
                   [ownerName IN ownerNames WHERE ownerName IS NOT NULL] AS ownerNames,
                   components,
                   apis,
                   screens,
                   features,
                   dependencies
            """;

    private static final String DATA_ARCHITECTURE_SUMMARY_QUERY = """
            MATCH (obj:BusinessObject)
            WHERE ($domain = '' OR coalesce(toString(obj.domain), '') = $domain)
            OPTIONAL MATCH (obj)-[:MAPPED_TO]->(entity:DataEntity)
            OPTIONAL MATCH (obj)<-[:CARRIES]-(flow:InformationFlow)
            OPTIONAL MATCH (flow)-[:EXPOSED_VIA]->(api:ApiContract)
            OPTIONAL MATCH (entity)<-[:OWNS_DATA_ENTITY]-(:ApplicationComponent)<-[:HAS_COMPONENT]-(app:Application)
            OPTIONAL MATCH (app)-[:HAS_COMPONENT]->(:ApplicationComponent)-[:SUPPORTS_SCREEN]->(screen:Screen)
            RETURN obj.objectId AS objectId,
                   coalesce(obj.name, obj.objectId) AS name,
                   toString(obj.domain) AS domain,
                   toString(obj.sensitivity) AS sensitivity,
                   toString(obj.status) AS status,
                   count(DISTINCT entity) AS mappedEntityCount,
                   count(DISTINCT flow) AS flowCount,
                   count(DISTINCT api) AS apiCount,
                   count(DISTINCT screen) AS screenCount
            ORDER BY coalesce(obj.name, obj.objectId)
            """;

    private static final String DATA_ARCHITECTURE_QUERY = """
            MATCH (obj:BusinessObject {objectId: $objectId})
            CALL (obj) {
                OPTIONAL MATCH (obj)-[:MAPPED_TO]->(entity:DataEntity)
                CALL (entity) {
                    OPTIONAL MATCH (entity)-[:HAS_FIELD]->(field:DataField)
                    RETURN count(DISTINCT field) AS fieldCount
                }
                RETURN collect(
                    DISTINCT CASE
                        WHEN entity IS NULL THEN null
                        ELSE {
                            entityId: entity.entityId,
                            name: coalesce(entity.name, entity.entityId),
                            entityType: entity.entityType,
                            fieldCount: fieldCount,
                            status: toString(entity.status)
                        }
                    END
                ) AS entities
            }
            CALL (obj) {
                OPTIONAL MATCH (flow:InformationFlow)-[:CARRIES]->(obj)
                OPTIONAL MATCH (flow)-[:SOURCE_APPLICATION]->(source:Application)
                OPTIONAL MATCH (flow)-[:TARGET_APPLICATION]->(target:Application)
                RETURN collect(
                    DISTINCT CASE
                        WHEN flow IS NULL THEN null
                        ELSE {
                            flowId: flow.flowId,
                            name: coalesce(flow.name, flow.flowId),
                            direction: toString(flow.direction),
                            status: toString(flow.status),
                            sourceApplicationId: source.applicationId,
                            sourceApplicationName: coalesce(source.name, source.applicationId),
                            targetApplicationId: target.applicationId,
                            targetApplicationName: coalesce(target.name, target.applicationId)
                        }
                    END
                ) AS flows
            }
            CALL (obj) {
                OPTIONAL MATCH (flow:InformationFlow)-[:CARRIES]->(obj)
                OPTIONAL MATCH (flow)-[:EXPOSED_VIA]->(api:ApiContract)
                RETURN collect(
                    DISTINCT CASE
                        WHEN api IS NULL THEN null
                        ELSE {
                            id: api.contractId,
                            nodeType: 'ApiContract',
                            displayName: coalesce(
                                CASE
                                    WHEN api.method IS NOT NULL AND api.path IS NOT NULL THEN api.method + ' ' + api.path
                                    ELSE null
                                END,
                                api.contractId
                            ),
                            status: toString(api.status)
                        }
                    END
                ) AS apis
            }
            CALL (obj) {
                OPTIONAL MATCH (obj)-[:MAPPED_TO]->(entity:DataEntity)<-[:OWNS_DATA_ENTITY]-(:ApplicationComponent)<-[:HAS_COMPONENT]-(app:Application)
                OPTIONAL MATCH (app)-[:HAS_COMPONENT]->(:ApplicationComponent)-[:SUPPORTS_SCREEN]->(screen:Screen)
                RETURN collect(
                    DISTINCT CASE
                        WHEN screen IS NULL THEN null
                        ELSE {
                            id: screen.surfaceId,
                            nodeType: 'Screen',
                            displayName: coalesce(screen.label, screen.surfaceId),
                            status: toString(screen.status)
                        }
                    END
                ) AS screens
            }
            CALL (obj) {
                OPTIONAL MATCH (child:BusinessObject)-[:STRUCTURED_IN]->(obj)
                RETURN collect(
                    DISTINCT CASE
                        WHEN child IS NULL THEN null
                        ELSE {
                            id: child.objectId,
                            nodeType: 'BusinessObject',
                            displayName: coalesce(child.name, child.objectId),
                            status: toString(child.status)
                        }
                    END
                ) AS children
            }
            RETURN obj.objectId AS objectId,
                   coalesce(obj.name, obj.objectId) AS name,
                   toString(obj.domain) AS domain,
                   obj.description AS description,
                   toString(obj.sensitivity) AS sensitivity,
                   toString(obj.status) AS status,
                   entities,
                   flows,
                   apis,
                   screens,
                   children
            """;

    private static final String INFRASTRUCTURE_SUMMARY_QUERY = """
            MATCH (dep:Deployment)
            WHERE ($environment = '' OR coalesce(toString(dep.environment), '') = $environment)
            OPTIONAL MATCH (dep)-[:HOSTS]->(component:ApplicationComponent)
            OPTIONAL MATCH (component)<-[:HAS_COMPONENT]-(app:Application)
            OPTIONAL MATCH (dep)-[:DEPLOYED_ON]->(node:InfrastructureNode)
            RETURN dep.deploymentId AS deploymentId,
                   coalesce(dep.name, dep.deploymentId) AS name,
                   toString(dep.environment) AS environment,
                   toString(dep.status) AS status,
                   count(DISTINCT component) AS componentCount,
                   count(DISTINCT app) AS applicationCount,
                   count(DISTINCT node) AS infrastructureCount
            ORDER BY coalesce(dep.name, dep.deploymentId)
            """;

    private static final String INFRASTRUCTURE_ARCHITECTURE_QUERY = """
            MATCH (dep:Deployment {deploymentId: $deploymentId})
            CALL (dep) {
                OPTIONAL MATCH (dep)-[:HOSTS]->(component:ApplicationComponent)
                RETURN collect(
                    DISTINCT CASE
                        WHEN component IS NULL THEN null
                        ELSE {
                            id: component.componentId,
                            nodeType: 'ApplicationComponent',
                            displayName: coalesce(component.name, component.componentId),
                            status: toString(component.status)
                        }
                    END
                ) AS components
            }
            CALL (dep) {
                OPTIONAL MATCH (dep)-[:DEPLOYED_ON]->(node:InfrastructureNode)
                RETURN collect(
                    DISTINCT CASE
                        WHEN node IS NULL THEN null
                        ELSE {
                            nodeId: node.nodeId,
                            name: coalesce(node.name, node.nodeId),
                            nodeType: toString(node.nodeType),
                            location: toString(node.location),
                            status: toString(node.status)
                        }
                    END
                ) AS infrastructureNodes
            }
            CALL (dep) {
                OPTIONAL MATCH (dep)-[:HOSTS]->(:ApplicationComponent)<-[:HAS_COMPONENT]-(app:Application)
                RETURN collect(
                    DISTINCT CASE
                        WHEN app IS NULL THEN null
                        ELSE {
                            id: app.applicationId,
                            nodeType: 'Application',
                            displayName: coalesce(app.name, app.applicationId),
                            status: toString(app.status)
                        }
                    END
                ) AS applications
            }
            RETURN dep.deploymentId AS deploymentId,
                   coalesce(dep.name, dep.deploymentId) AS name,
                   toString(dep.environment) AS environment,
                   dep.description AS description,
                   toString(dep.status) AS status,
                   components,
                   infrastructureNodes,
                   applications,
                   [] AS elements
            """;

    private static final String EXTERNAL_ARTIFACT_SUMMARY_QUERY = """
            MATCH (ea:ExternalArtifact)
            WHERE ($system = '' OR toUpper(coalesce(ea.system, '')) = toUpper($system))
              AND ($syncStatus = '' OR toUpper(coalesce(ea.syncStatus, '')) = toUpper($syncStatus))
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:REPRESENTS_STORY|REPRESENTS_BUG|REPRESENTS_FEATURE|REPRESENTS_EPIC|REPRESENTS_PORTFOLIO|REPRESENTS_OBJECTIVE|REPRESENTS_TASK]->(represented)
                RETURN count(DISTINCT represented) AS representedObjectCount
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:PARENT_OF]->(child:ExternalArtifact)
                RETURN count(DISTINCT child) AS childCount
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:DEPENDS_ON]->(dependency:ExternalArtifact)
                RETURN count(DISTINCT dependency) AS dependencyCount
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:RELATES_TO]-(related:ExternalArtifact)
                RETURN count(DISTINCT related) AS relatedCount
            }
            RETURN ea.externalId AS externalId,
                   ea.system AS system,
                   ea.externalType AS externalType,
                   ea.key AS key,
                   coalesce(ea.title, ea.key, ea.externalId) AS title,
                   ea.projectScope AS projectScope,
                   ea.workflowState AS workflowState,
                   ea.priority AS priority,
                   ea.owner AS owner,
                   ea.reporter AS reporter,
                   coalesce(ea.labels, []) AS labels,
                   coalesce(ea.customFields, []) AS customFields,
                   ea.url AS url,
                   ea.syncStatus AS syncStatus,
                   toString(ea.lastSyncedAt) AS lastSyncedAt,
                   toString(ea.status) AS status,
                   representedObjectCount,
                   childCount,
                   dependencyCount,
                   relatedCount
            ORDER BY coalesce(ea.title, ea.key, ea.externalId)
            """;

    private static final String EXTERNAL_ARTIFACT_TRAVERSAL_QUERY = """
            MATCH (ea:ExternalArtifact {externalId: $externalId})
            CALL (ea) {
                OPTIONAL MATCH (parent:ExternalArtifact)-[:PARENT_OF]->(ea)
                RETURN collect(
                    DISTINCT CASE
                        WHEN parent IS NULL THEN null
                        ELSE {
                            externalId: parent.externalId,
                            system: parent.system,
                            externalType: parent.externalType,
                            key: parent.key,
                            title: coalesce(parent.title, parent.key, parent.externalId),
                            workflowState: parent.workflowState,
                            syncStatus: parent.syncStatus,
                            status: toString(parent.status)
                        }
                    END
                ) AS parents
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:PARENT_OF]->(child:ExternalArtifact)
                RETURN collect(
                    DISTINCT CASE
                        WHEN child IS NULL THEN null
                        ELSE {
                            externalId: child.externalId,
                            system: child.system,
                            externalType: child.externalType,
                            key: child.key,
                            title: coalesce(child.title, child.key, child.externalId),
                            workflowState: child.workflowState,
                            syncStatus: child.syncStatus,
                            status: toString(child.status)
                        }
                    END
                ) AS children
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:DEPENDS_ON]->(dependency:ExternalArtifact)
                RETURN collect(
                    DISTINCT CASE
                        WHEN dependency IS NULL THEN null
                        ELSE {
                            externalId: dependency.externalId,
                            system: dependency.system,
                            externalType: dependency.externalType,
                            key: dependency.key,
                            title: coalesce(dependency.title, dependency.key, dependency.externalId),
                            workflowState: dependency.workflowState,
                            syncStatus: dependency.syncStatus,
                            status: toString(dependency.status)
                        }
                    END
                ) AS dependencies
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:RELATES_TO]-(related:ExternalArtifact)
                RETURN collect(
                    DISTINCT CASE
                        WHEN related IS NULL THEN null
                        ELSE {
                            externalId: related.externalId,
                            system: related.system,
                            externalType: related.externalType,
                            key: related.key,
                            title: coalesce(related.title, related.key, related.externalId),
                            workflowState: related.workflowState,
                            syncStatus: related.syncStatus,
                            status: toString(related.status)
                        }
                    END
                ) AS relatedArtifacts
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:DUPLICATES]-(duplicate:ExternalArtifact)
                RETURN collect(
                    DISTINCT CASE
                        WHEN duplicate IS NULL THEN null
                        ELSE {
                            externalId: duplicate.externalId,
                            system: duplicate.system,
                            externalType: duplicate.externalType,
                            key: duplicate.key,
                            title: coalesce(duplicate.title, duplicate.key, duplicate.externalId),
                            workflowState: duplicate.workflowState,
                            syncStatus: duplicate.syncStatus,
                            status: toString(duplicate.status)
                        }
                    END
                ) AS duplicates
            }
            CALL (ea) {
                OPTIONAL MATCH (ea)-[:REPRESENTS_STORY|REPRESENTS_BUG|REPRESENTS_FEATURE|REPRESENTS_EPIC|REPRESENTS_PORTFOLIO|REPRESENTS_OBJECTIVE|REPRESENTS_TASK]->(represented)
                RETURN collect(
                    DISTINCT CASE
                        WHEN represented IS NULL THEN null
                        ELSE {
                            id: coalesce(
                                toString(represented.objectiveId),
                                toString(represented.portfolioId),
                                toString(represented.epicId),
                                toString(represented.storyId),
                                toString(represented.bugId),
                                toString(represented.featureId),
                                toString(represented.taskId),
                                elementId(represented)
                            ),
                            nodeType: head(labels(represented)),
                            displayName: coalesce(
                                represented.label,
                                represented.title,
                                represented.name,
                                represented.summary,
                                toString(represented.objectiveId),
                                toString(represented.portfolioId),
                                toString(represented.epicId),
                                toString(represented.taskId),
                                toString(represented.storyId),
                                toString(represented.featureId),
                                toString(represented.bugId),
                                head(labels(represented))
                            ),
                            status: toString(represented.status)
                        }
                    END
                ) AS representedObjects
            }
            RETURN ea.externalId AS externalId,
                   ea.system AS system,
                   ea.externalType AS externalType,
                   ea.key AS key,
                   coalesce(ea.title, ea.key, ea.externalId) AS title,
                   ea.projectScope AS projectScope,
                   ea.workflowState AS workflowState,
                   ea.priority AS priority,
                   ea.owner AS owner,
                   ea.reporter AS reporter,
                   coalesce(ea.labels, []) AS labels,
                   coalesce(ea.customFields, []) AS customFields,
                   ea.url AS url,
                   ea.syncStatus AS syncStatus,
                   toString(ea.lastSyncedAt) AS lastSyncedAt,
                   toString(ea.status) AS status,
                   parents,
                   children,
                   dependencies,
                   relatedArtifacts,
                   duplicates,
                   representedObjects
            """;

    private static final String STORY_TRACEABILITY_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            OPTIONAL MATCH (feature:Feature)-[:HAS_STORY]->(us)
            OPTIONAL MATCH (objective:BusinessObjective)-[:HAS_FEATURE]->(feature)
            OPTIONAL MATCH (epic:Epic)-[:HAS_FEATURE]->(feature)
            OPTIONAL MATCH (portfolio:RequirementPortfolio)-[:HAS_EPIC]->(epic)
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(screen:Screen)
                RETURN collect(
                    DISTINCT CASE
                        WHEN screen IS NULL THEN null
                        ELSE {
                            id: screen.surfaceId,
                            nodeType: 'Screen',
                            displayName: coalesce(screen.label, screen.surfaceId),
                            status: toString(screen.status)
                        }
                    END
                ) AS screens
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_INTERACTION]->(interaction:Interaction)
                RETURN collect(
                    DISTINCT CASE
                        WHEN interaction IS NULL THEN null
                        ELSE {
                            id: interaction.interactionId,
                            nodeType: 'Interaction',
                            displayName: coalesce(interaction.element, interaction.interactionId),
                            status: null
                        }
                    END
                ) AS interactions
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_INTERACTION]->(:Interaction)-[:CALLS_API]->(api:ApiContract)
                RETURN collect(
                    DISTINCT CASE
                        WHEN api IS NULL THEN null
                        ELSE {
                            id: api.contractId,
                            nodeType: 'ApiContract',
                            displayName: CASE
                                WHEN api.method IS NOT NULL AND api.path IS NOT NULL THEN api.method + ' ' + api.path
                                ELSE coalesce(api.path, api.contractId)
                            END,
                            status: toString(api.status)
                        }
                    END
                ) AS apis
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(entity:DataEntity)
                RETURN collect(
                    DISTINCT CASE
                        WHEN entity IS NULL THEN null
                        ELSE {
                            id: entity.entityId,
                            nodeType: 'DataEntity',
                            displayName: coalesce(entity.name, entity.entityId),
                            status: toString(entity.status)
                        }
                    END
                ) AS dataEntities
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(message:Message)
                RETURN collect(
                    DISTINCT CASE
                        WHEN message IS NULL THEN null
                        ELSE {
                            id: message.messageId,
                            nodeType: 'Message',
                            displayName: coalesce(message.messageText, message.messageId),
                            status: toString(message.status)
                        }
                    END
                ) AS messages
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:HAS_TASK]->(task:Task)
                RETURN collect(
                    DISTINCT CASE
                        WHEN task IS NULL THEN null
                        ELSE {
                            id: task.taskId,
                            nodeType: 'Task',
                            displayName: coalesce(task.title, task.taskId),
                            status: toString(task.status)
                        }
                    END
                ) AS tasks
            }
            RETURN CASE
                       WHEN objective IS NULL THEN null
                       ELSE {
                           id: objective.objectiveId,
                           nodeType: 'BusinessObjective',
                           displayName: coalesce(objective.title, objective.objectiveId),
                           status: toString(objective.status)
                       }
                   END AS objective,
                   CASE
                       WHEN portfolio IS NULL THEN null
                       ELSE {
                           id: portfolio.portfolioId,
                           nodeType: 'RequirementPortfolio',
                           displayName: coalesce(portfolio.name, portfolio.portfolioId),
                           status: toString(portfolio.status)
                       }
                   END AS portfolio,
                   CASE
                       WHEN epic IS NULL THEN null
                       ELSE {
                           id: epic.epicId,
                           nodeType: 'Epic',
                           displayName: coalesce(epic.title, epic.epicId),
                           status: toString(epic.status)
                       }
                   END AS epic,
                   CASE
                       WHEN feature IS NULL THEN null
                       ELSE {
                           id: feature.featureId,
                           nodeType: 'Feature',
                           displayName: coalesce(feature.title, feature.featureId),
                           status: toString(feature.status)
                       }
                   END AS feature,
                   {
                       id: us.storyId,
                       nodeType: 'UserStory',
                       displayName: coalesce(us.label, us.storyId),
                       status: toString(us.status)
                   } AS story,
                   screens,
                   interactions,
                   apis,
                   dataEntities,
                   messages,
                   tasks
            """;
}
