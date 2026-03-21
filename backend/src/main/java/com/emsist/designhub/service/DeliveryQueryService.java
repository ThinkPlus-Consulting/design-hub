package com.emsist.designhub.service;

import com.emsist.designhub.dto.DeliveryStoryResponse;
import com.emsist.designhub.dto.ReadinessDiagnosticsResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DeliveryQueryService {

    private static final String DELIVERY_STORIES_QUERY = """
            MATCH (us:UserStory)
            OPTIONAL MATCH (feature:Feature)-[:HAS_STORY]->(us)
            WITH us, head(collect(DISTINCT feature)) AS feature
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(screen:Screen)
                RETURN collect(DISTINCT CASE WHEN screen IS NULL THEN null ELSE {
                    surfaceId: screen.surfaceId,
                    label: screen.label,
                    routePath: screen.routePath,
                    status: toString(screen.status)
                } END) AS screens
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_INTERACTION]->(:Interaction)-[:CALLS_API]->(api:ApiContract)
                RETURN collect(DISTINCT CASE WHEN api IS NULL THEN null ELSE {
                    contractId: api.contractId,
                    method: api.method,
                    path: api.path,
                    status: toString(api.status)
                } END) AS apis
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)<-[:AFFECTS_SCREEN]-(bug:Bug)
                RETURN collect(DISTINCT CASE WHEN bug IS NULL THEN null ELSE {
                    bugId: bug.bugId,
                    externalKey: bug.externalKey,
                    summary: bug.summary,
                    severity: bug.severity,
                    status: toString(bug.status)
                } END) AS bugs
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_GAP]->(gap:Gap)
                RETURN collect(DISTINCT CASE WHEN gap IS NULL THEN null ELSE {
                    gapId: gap.gapId,
                    gapType: gap.gapType,
                    severity: gap.severity,
                    description: gap.description,
                    status: toString(gap.status)
                } END) AS gaps
            }
            CALL (us) {
                OPTIONAL MATCH (storyExt:ExternalArtifact)-[:REPRESENTS_STORY]->(us)
                RETURN collect(DISTINCT CASE WHEN storyExt IS NULL THEN null ELSE {
                    externalId: storyExt.externalId,
                    system: storyExt.system,
                    externalType: storyExt.externalType,
                    key: storyExt.key,
                    title: storyExt.title,
                    projectScope: storyExt.projectScope,
                    workflowState: storyExt.workflowState,
                    priority: storyExt.priority,
                    owner: storyExt.owner,
                    reporter: storyExt.reporter,
                    labels: coalesce(storyExt.labels, []),
                    url: storyExt.url,
                    syncStatus: storyExt.syncStatus,
                    status: toString(storyExt.status)
                } END) AS storyExternalArtifacts
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)<-[:AFFECTS_SCREEN]-(bug:Bug)<-[:REPRESENTS_BUG]-(bugExt:ExternalArtifact)
                RETURN collect(DISTINCT CASE WHEN bugExt IS NULL THEN null ELSE {
                    externalId: bugExt.externalId,
                    system: bugExt.system,
                    externalType: bugExt.externalType,
                    key: bugExt.key,
                    title: bugExt.title,
                    projectScope: bugExt.projectScope,
                    workflowState: bugExt.workflowState,
                    priority: bugExt.priority,
                    owner: bugExt.owner,
                    reporter: bugExt.reporter,
                    labels: coalesce(bugExt.labels, []),
                    url: bugExt.url,
                    syncStatus: bugExt.syncStatus,
                    status: toString(bugExt.status)
                } END) AS bugExternalArtifacts
            }
            RETURN us.storyId AS storyId,
                   us.label AS label,
                   us.module AS module,
                   us.domain AS domain,
                   us.storyNumber AS storyNumber,
                   toString(us.status) AS status,
                   CASE WHEN feature IS NULL THEN null ELSE {
                       featureId: feature.featureId,
                       title: feature.title,
                       status: toString(feature.status)
                   } END AS feature,
                   screens,
                   apis,
                   bugs,
                   gaps,
                   storyExternalArtifacts,
                   bugExternalArtifacts
            """;

    private static final String DELIVERY_STORY_QUERY = """
            MATCH (us:UserStory {storyId: $storyId})
            OPTIONAL MATCH (feature:Feature)-[:HAS_STORY]->(us)
            WITH us, head(collect(DISTINCT feature)) AS feature
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(screen:Screen)
                RETURN collect(DISTINCT CASE WHEN screen IS NULL THEN null ELSE {
                    surfaceId: screen.surfaceId,
                    label: screen.label,
                    routePath: screen.routePath,
                    status: toString(screen.status)
                } END) AS screens
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_INTERACTION]->(:Interaction)-[:CALLS_API]->(api:ApiContract)
                RETURN collect(DISTINCT CASE WHEN api IS NULL THEN null ELSE {
                    contractId: api.contractId,
                    method: api.method,
                    path: api.path,
                    status: toString(api.status)
                } END) AS apis
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)<-[:AFFECTS_SCREEN]-(bug:Bug)
                RETURN collect(DISTINCT CASE WHEN bug IS NULL THEN null ELSE {
                    bugId: bug.bugId,
                    externalKey: bug.externalKey,
                    summary: bug.summary,
                    severity: bug.severity,
                    status: toString(bug.status)
                } END) AS bugs
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)-[:HAS_GAP]->(gap:Gap)
                RETURN collect(DISTINCT CASE WHEN gap IS NULL THEN null ELSE {
                    gapId: gap.gapId,
                    gapType: gap.gapType,
                    severity: gap.severity,
                    description: gap.description,
                    status: toString(gap.status)
                } END) AS gaps
            }
            CALL (us) {
                OPTIONAL MATCH (storyExt:ExternalArtifact)-[:REPRESENTS_STORY]->(us)
                RETURN collect(DISTINCT CASE WHEN storyExt IS NULL THEN null ELSE {
                    externalId: storyExt.externalId,
                    system: storyExt.system,
                    externalType: storyExt.externalType,
                    key: storyExt.key,
                    title: storyExt.title,
                    projectScope: storyExt.projectScope,
                    workflowState: storyExt.workflowState,
                    priority: storyExt.priority,
                    owner: storyExt.owner,
                    reporter: storyExt.reporter,
                    labels: coalesce(storyExt.labels, []),
                    url: storyExt.url,
                    syncStatus: storyExt.syncStatus,
                    status: toString(storyExt.status)
                } END) AS storyExternalArtifacts
            }
            CALL (us) {
                OPTIONAL MATCH (us)-[:DELIVERS]->(:Screen)<-[:AFFECTS_SCREEN]-(bug:Bug)<-[:REPRESENTS_BUG]-(bugExt:ExternalArtifact)
                RETURN collect(DISTINCT CASE WHEN bugExt IS NULL THEN null ELSE {
                    externalId: bugExt.externalId,
                    system: bugExt.system,
                    externalType: bugExt.externalType,
                    key: bugExt.key,
                    title: bugExt.title,
                    projectScope: bugExt.projectScope,
                    workflowState: bugExt.workflowState,
                    priority: bugExt.priority,
                    owner: bugExt.owner,
                    reporter: bugExt.reporter,
                    labels: coalesce(bugExt.labels, []),
                    url: bugExt.url,
                    syncStatus: bugExt.syncStatus,
                    status: toString(bugExt.status)
                } END) AS bugExternalArtifacts
            }
            RETURN us.storyId AS storyId,
                   us.label AS label,
                   us.module AS module,
                   us.domain AS domain,
                   us.storyNumber AS storyNumber,
                   toString(us.status) AS status,
                   CASE WHEN feature IS NULL THEN null ELSE {
                       featureId: feature.featureId,
                       title: feature.title,
                       status: toString(feature.status)
                   } END AS feature,
                   screens,
                   apis,
                   bugs,
                   gaps,
                   storyExternalArtifacts,
                   bugExternalArtifacts
            """;

    private final Neo4jClient neo4jClient;
    private final ReadinessDiagnosticsService readinessDiagnosticsService;

    public List<DeliveryStoryResponse> getStories(
            String status,
            String feature,
            String module,
            Boolean isReady,
            Boolean hasScreens,
            Boolean hasApis,
            String search,
            String sortBy,
            String sortDirection
    ) {
        Stream<DeliveryStoryResponse> stream = fetchAllStories().stream()
                .map(this::toStory)
                .map(this::withDiagnostics)
                .filter(story -> matches(status, story.status()))
                .filter(story -> matches(module, story.module()))
                .filter(story -> matchesFeature(feature, story.feature()))
                .filter(story -> matchesSearch(search, story))
                .filter(story -> hasValue(hasScreens, !story.screens().isEmpty()))
                .filter(story -> hasValue(hasApis, !story.apis().isEmpty()))
                .filter(story -> hasValue(isReady, story.ready()));

        return stream.sorted(comparator(sortBy, sortDirection)).toList();
    }

    public Optional<DeliveryStoryResponse> getStory(String storyId) {
        return fetchStoryRecord(storyId)
                .map(this::toStory)
                .map(this::withDiagnostics);
    }

    private List<Map<String, Object>> fetchAllStories() {
        return new ArrayList<>(neo4jClient.query(DELIVERY_STORIES_QUERY)
                .fetch()
                .all());
    }

    private Optional<Map<String, Object>> fetchStoryRecord(String storyId) {
        return neo4jClient.query(DELIVERY_STORY_QUERY)
                .bind(storyId).to("storyId")
                .fetch()
                .first();
    }

    private DeliveryStoryResponse withDiagnostics(DeliveryStoryResponse story) {
        ReadinessDiagnosticsResponse diagnostics = readinessDiagnosticsService.assessStory(story.storyId()).orElse(null);
        return story.withDiagnostics(diagnostics);
    }

    private DeliveryStoryResponse toStory(Map<String, Object> record) {
        return new DeliveryStoryResponse(
                string(record, "storyId"),
                string(record, "label"),
                string(record, "module"),
                string(record, "domain"),
                string(record, "storyNumber"),
                string(record, "status"),
                false,
                toFeature(record.get("feature")),
                toScreens(record.get("screens")),
                toApis(record.get("apis")),
                toBugs(record.get("bugs")),
                List.of(),
                toGaps(record.get("gaps")),
                toExternalArtifacts(record.get("storyExternalArtifacts"), record.get("bugExternalArtifacts")),
                null
        );
    }

    private boolean matches(String expected, String actual) {
        if (expected == null || expected.isBlank()) {
            return true;
        }
        return actual != null && actual.equalsIgnoreCase(expected);
    }

    private boolean matchesFeature(String feature, DeliveryStoryResponse.FeatureSummary summary) {
        if (feature == null || feature.isBlank()) {
            return true;
        }
        if (summary == null) {
            return false;
        }
        return feature.equalsIgnoreCase(summary.featureId())
                || feature.equalsIgnoreCase(summary.title());
    }

    private boolean matchesSearch(String search, DeliveryStoryResponse story) {
        if (search == null || search.isBlank()) {
            return true;
        }
        String needle = search.toLowerCase(Locale.ROOT);
        return Stream.of(
                        story.storyId(),
                        story.label(),
                        story.module(),
                        story.domain(),
                        story.storyNumber(),
                        story.feature() == null ? null : story.feature().title()
                )
                .filter(Objects::nonNull)
                .map(value -> value.toLowerCase(Locale.ROOT))
                .anyMatch(value -> value.contains(needle));
    }

    private boolean hasValue(Boolean expected, boolean actual) {
        return expected == null || expected == actual;
    }

    private Comparator<DeliveryStoryResponse> comparator(String sortBy, String sortDirection) {
        String key = normalize(sortBy, "status");
        Comparator<DeliveryStoryResponse> comparator = switch (key) {
            case "title", "label" -> Comparator.<DeliveryStoryResponse, String>comparing(
                    story -> normalize(story.label(), ""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "screencount" -> Comparator.comparingInt(story -> story.screens().size());
            case "completeness", "completenessscore" -> Comparator.comparingDouble(
                    story -> story.diagnostics() == null ? 0.0 : story.diagnostics().getCompletenessScore()
            );
            case "readiness", "ready" -> Comparator.comparing(DeliveryStoryResponse::ready);
            case "module" -> Comparator.<DeliveryStoryResponse, String>comparing(
                    story -> normalize(story.module(), ""),
                    String.CASE_INSENSITIVE_ORDER
            );
            case "status" -> Comparator.<DeliveryStoryResponse, String>comparing(
                    story -> normalize(story.status(), ""),
                    String.CASE_INSENSITIVE_ORDER
            ).thenComparing(story -> normalize(story.label(), ""), String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.<DeliveryStoryResponse, String>comparing(
                    story -> normalize(story.status(), ""),
                    String.CASE_INSENSITIVE_ORDER
            ).thenComparing(story -> normalize(story.label(), ""), String.CASE_INSENSITIVE_ORDER);
        };

        if ("desc".equalsIgnoreCase(normalize(sortDirection, "asc"))) {
            return comparator.reversed();
        }
        return comparator;
    }

    private String normalize(String value, String fallback) {
        return value == null || value.isBlank() ? fallback : value.toLowerCase(Locale.ROOT);
    }

    private DeliveryStoryResponse.FeatureSummary toFeature(Object value) {
        Map<String, Object> feature = mapValue(value);
        if (feature == null || string(feature, "featureId") == null) {
            return null;
        }
        return new DeliveryStoryResponse.FeatureSummary(
                string(feature, "featureId"),
                string(feature, "title"),
                string(feature, "status")
        );
    }

    private List<DeliveryStoryResponse.ScreenSummary> toScreens(Object value) {
        return maps(value).stream()
                .filter(screen -> string(screen, "surfaceId") != null)
                .map(screen -> new DeliveryStoryResponse.ScreenSummary(
                        string(screen, "surfaceId"),
                        string(screen, "label"),
                        string(screen, "routePath"),
                        string(screen, "status")
                ))
                .toList();
    }

    private List<DeliveryStoryResponse.ApiSummary> toApis(Object value) {
        return maps(value).stream()
                .filter(api -> string(api, "contractId") != null)
                .map(api -> new DeliveryStoryResponse.ApiSummary(
                        string(api, "contractId"),
                        string(api, "method"),
                        string(api, "path"),
                        string(api, "status")
                ))
                .toList();
    }

    private List<DeliveryStoryResponse.BugSummary> toBugs(Object value) {
        return maps(value).stream()
                .filter(bug -> string(bug, "bugId") != null)
                .map(bug -> new DeliveryStoryResponse.BugSummary(
                        string(bug, "bugId"),
                        string(bug, "externalKey"),
                        string(bug, "summary"),
                        string(bug, "severity"),
                        string(bug, "status")
                ))
                .toList();
    }

    private List<DeliveryStoryResponse.GapSummary> toGaps(Object value) {
        return maps(value).stream()
                .filter(gap -> string(gap, "gapId") != null)
                .map(gap -> new DeliveryStoryResponse.GapSummary(
                        string(gap, "gapId"),
                        string(gap, "gapType"),
                        string(gap, "severity"),
                        string(gap, "description"),
                        string(gap, "status")
                ))
                .toList();
    }

    private List<DeliveryStoryResponse.ExternalArtifactSummary> toExternalArtifacts(Object storyArtifacts, Object bugArtifacts) {
        Map<String, DeliveryStoryResponse.ExternalArtifactSummary> artifacts = Stream.concat(
                        maps(storyArtifacts).stream(),
                        maps(bugArtifacts).stream()
                )
                .filter(artifact -> string(artifact, "externalId") != null)
                .map(artifact -> new DeliveryStoryResponse.ExternalArtifactSummary(
                        string(artifact, "externalId"),
                        string(artifact, "system"),
                        string(artifact, "externalType"),
                        string(artifact, "key"),
                        string(artifact, "title"),
                        string(artifact, "projectScope"),
                        string(artifact, "workflowState"),
                        string(artifact, "priority"),
                        string(artifact, "owner"),
                        string(artifact, "reporter"),
                        strings(artifact.get("labels")),
                        string(artifact, "url"),
                        string(artifact, "syncStatus"),
                        string(artifact, "status")
                ))
                .collect(Collectors.toMap(
                        DeliveryStoryResponse.ExternalArtifactSummary::externalId,
                        Function.identity(),
                        (left, right) -> left,
                        LinkedHashMap::new
                ));
        return new ArrayList<>(artifacts.values());
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> mapValue(Object value) {
        if (value instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    private List<Map<String, Object>> maps(Object value) {
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Map.class::isInstance)
                    .map(entry -> (Map<String, Object>) entry)
                    .toList();
        }
        return List.of();
    }

    private List<String> strings(Object value) {
        if (value instanceof List<?> list) {
            return list.stream()
                    .filter(Objects::nonNull)
                    .map(String::valueOf)
                    .toList();
        }
        return List.of();
    }

    private String string(Map<String, Object> record, String key) {
        Object value = record.get(key);
        return value == null ? null : String.valueOf(value);
    }
}
