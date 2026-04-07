package com.emsist.designhub.service;

import com.emsist.designhub.dto.GraphObjectSummaryResponse;
import com.emsist.designhub.dto.ObjectDefinitionDetailResponse;
import com.emsist.designhub.dto.ObjectDefinitionSummaryResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class GraphObjectDefinitionCatalogService {

    private static final int INSTANCE_PREVIEW_LIMIT = 200;
    private static final Pattern OBJECT_HEADING_PATTERN = Pattern.compile("^###\\s+([A-Za-z][A-Za-z0-9]+)\\s*$");
    private static final Pattern TIER_PATTERN = Pattern.compile("^\\*\\*Tier\\*\\*:\\s*(.+)$");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile("^\\*\\*Category\\*\\*:\\s*(.+)$");
    private static final Pattern PURPOSE_PATTERN = Pattern.compile("^\\*\\*Purpose\\*\\*:\\s*(.+)$");
    private static final Pattern IMPLEMENTATION_STATUS_PATTERN = Pattern.compile("^\\*\\*Implementation Status\\*\\*:\\s*(.+)$");

    private final GraphQueryService graphQueryService;

    private volatile CatalogSnapshot cachedCatalog;

    public List<ObjectDefinitionSummaryResponse> listObjectDefinitions() {
        return catalog().definitions().stream()
                .map(this::toSummary)
                .sorted(Comparator.comparing(ObjectDefinitionSummaryResponse::displayName, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    public Optional<ObjectDefinitionDetailResponse> getObjectDefinition(String type) {
        return catalog().find(type)
                .map(this::toDetail);
    }

    private ObjectDefinitionSummaryResponse toSummary(CatalogDefinition definition) {
        long instanceCount = definition.nodeType()
                .map(nodeType -> graphQueryService.countObjects(nodeType.primaryAlias()))
                .orElse(0L);

        return new ObjectDefinitionSummaryResponse(
                definition.type(),
                definition.label(),
                humanize(definition.label()),
                definition.category(),
                definition.tier(),
                definition.benchmarkable(),
                instanceCount,
                definition.relationships().size()
        );
    }

    private ObjectDefinitionDetailResponse toDetail(CatalogDefinition definition) {
        long instanceCount = definition.nodeType()
                .map(nodeType -> graphQueryService.countObjects(nodeType.primaryAlias()))
                .orElse(0L);
        List<GraphObjectSummaryResponse> instances = definition.nodeType()
                .map(nodeType -> graphQueryService.getObjects(nodeType.primaryAlias(), null, null, null, INSTANCE_PREVIEW_LIMIT))
                .orElse(List.of());

        return new ObjectDefinitionDetailResponse(
                definition.type(),
                definition.label(),
                humanize(definition.label()),
                definition.category(),
                definition.tier(),
                definition.benchmarkable(),
                definition.purpose(),
                definition.implementationStatus(),
                definition.aliases(),
                definition.attributes().stream()
                        .map(attribute -> new ObjectDefinitionDetailResponse.AttributeDefinitionResponse(
                                attribute.name(),
                                attribute.type(),
                                attribute.required(),
                                attribute.description(),
                                attribute.constraints()
                        ))
                        .toList(),
                definition.relationships().stream()
                        .map(relationship -> new ObjectDefinitionDetailResponse.RelationshipDefinitionResponse(
                                relationship.name(),
                                relationship.direction(),
                                relationship.target(),
                                relationship.cardinality(),
                                relationship.required(),
                                relationship.severity(),
                                relationship.implementation()
                        ))
                        .toList(),
                instanceCount,
                instances
        );
    }

    private CatalogSnapshot catalog() {
        CatalogSnapshot snapshot = cachedCatalog;
        if (snapshot != null) {
            return snapshot;
        }

        synchronized (this) {
            if (cachedCatalog == null) {
                cachedCatalog = loadCatalog();
            }
            return cachedCatalog;
        }
    }

    private CatalogSnapshot loadCatalog() {
        Path catalogPath = resolveCatalogPath();
        if (catalogPath == null) {
            log.warn("graph-object-catalog.md was not found; object definitions explorer will be empty");
            return new CatalogSnapshot(List.of());
        }

        try {
            List<String> lines = Files.readAllLines(catalogPath, StandardCharsets.UTF_8);
            List<CatalogDefinition> definitions = parseDefinitions(lines);
            return new CatalogSnapshot(definitions);
        } catch (IOException exception) {
            log.error("Failed to read graph object catalog from {}", catalogPath, exception);
            return new CatalogSnapshot(List.of());
        }
    }

    private Path resolveCatalogPath() {
        List<Path> candidates = List.of(
                Path.of("documentation/graph-object-catalog.md"),
                Path.of("../documentation/graph-object-catalog.md"),
                Path.of("../../documentation/graph-object-catalog.md")
        );

        return candidates.stream()
                .filter(Files::exists)
                .findFirst()
                .orElse(null);
    }

    private List<CatalogDefinition> parseDefinitions(List<String> lines) {
        List<CatalogDefinition> definitions = new ArrayList<>();

        for (int index = 0; index < lines.size(); index++) {
            String line = lines.get(index).trim();
            var matcher = OBJECT_HEADING_PATTERN.matcher(line);
            if (!matcher.matches()) {
                continue;
            }

            String label = matcher.group(1);
            int sectionEnd = findNextHeading(lines, index + 1);
            List<String> sectionLines = lines.subList(index + 1, sectionEnd);
            if (sectionLines.stream().noneMatch(sectionLine -> sectionLine.startsWith("**Tier**:"))) {
                continue;
            }

            definitions.add(parseDefinition(label, sectionLines));
            index = sectionEnd - 1;
        }

        return definitions;
    }

    private CatalogDefinition parseDefinition(String label, List<String> sectionLines) {
        String tier = extractField(sectionLines, TIER_PATTERN);
        String category = extractField(sectionLines, CATEGORY_PATTERN);
        String purpose = extractField(sectionLines, PURPOSE_PATTERN);
        String implementationStatus = sanitizeInlineMarkdown(extractField(sectionLines, IMPLEMENTATION_STATUS_PATTERN));
        List<CatalogAttribute> attributes = parseAttributes(sectionLines);
        List<CatalogRelationship> relationships = parseRelationships(sectionLines);
        Optional<GraphNodeType> nodeType = GraphNodeType.fromLabel(label);
        String type = nodeType.map(GraphNodeType::primaryAlias).orElse(slugify(label));
        List<String> aliases = nodeType.map(GraphNodeType::aliases).orElse(List.of(type));

        return new CatalogDefinition(
                type,
                label,
                category,
                tier,
                purpose,
                implementationStatus,
                aliases,
                !"3".equals(firstToken(tier)),
                nodeType,
                attributes,
                relationships
        );
    }

    private List<CatalogAttribute> parseAttributes(List<String> sectionLines) {
        return parseTable(sectionLines, "#### Attributes").stream()
                .filter(columns -> columns.size() >= 5)
                .map(columns -> new CatalogAttribute(
                        columns.get(0),
                        columns.get(1),
                        isRequired(columns.get(2)),
                        sanitizeInlineMarkdown(columns.get(3)),
                        sanitizeInlineMarkdown(columns.get(4))
                ))
                .toList();
    }

    private List<CatalogRelationship> parseRelationships(List<String> sectionLines) {
        return parseTable(sectionLines, "#### Relationships").stream()
                .filter(columns -> columns.size() >= 7)
                .map(columns -> new CatalogRelationship(
                        columns.get(0),
                        columns.get(1),
                        sanitizeInlineMarkdown(columns.get(2)),
                        columns.get(3),
                        isRequired(columns.get(4)),
                        sanitizeInlineMarkdown(columns.get(5)),
                        sanitizeInlineMarkdown(columns.get(6))
                ))
                .toList();
    }

    private List<List<String>> parseTable(List<String> sectionLines, String heading) {
        int headingIndex = indexOf(sectionLines, heading);
        if (headingIndex < 0) {
            return List.of();
        }

        List<List<String>> rows = new ArrayList<>();
        for (int index = headingIndex + 1; index < sectionLines.size(); index++) {
            String line = sectionLines.get(index).trim();
            if (line.startsWith("#### ")) {
                break;
            }
            if (!line.startsWith("|")) {
                continue;
            }
            if (line.contains("---")) {
                continue;
            }

            List<String> columns = splitTableRow(line);
            if (!columns.isEmpty() && !columns.getFirst().equalsIgnoreCase("Attribute")
                    && !columns.getFirst().equalsIgnoreCase("Relationship")) {
                rows.add(columns);
            }
        }
        return rows;
    }

    private List<String> splitTableRow(String line) {
        String trimmed = line.trim();
        String noLeading = trimmed.startsWith("|") ? trimmed.substring(1) : trimmed;
        String noTrailing = noLeading.endsWith("|") ? noLeading.substring(0, noLeading.length() - 1) : noLeading;

        List<String> columns = new ArrayList<>();
        for (String column : noTrailing.split("\\|", -1)) {
            columns.add(column.trim());
        }
        return columns;
    }

    private int indexOf(List<String> lines, String expected) {
        for (int index = 0; index < lines.size(); index++) {
            if (lines.get(index).trim().equals(expected)) {
                return index;
            }
        }
        return -1;
    }

    private int findNextHeading(List<String> lines, int startIndex) {
        for (int index = startIndex; index < lines.size(); index++) {
            if (lines.get(index).startsWith("### ")) {
                return index;
            }
        }
        return lines.size();
    }

    private String extractField(List<String> sectionLines, Pattern pattern) {
        return sectionLines.stream()
                .map(String::trim)
                .map(pattern::matcher)
                .filter(java.util.regex.Matcher::matches)
                .map(matcher -> matcher.group(1).trim())
                .findFirst()
                .orElse("");
    }

    private boolean isRequired(String value) {
        return "Yes".equalsIgnoreCase(value);
    }

    private String sanitizeInlineMarkdown(String value) {
        return value
                .replace("`", "")
                .replace("**", "")
                .trim();
    }

    private String firstToken(String value) {
        int separatorIndex = value.indexOf(' ');
        return separatorIndex >= 0 ? value.substring(0, separatorIndex) : value;
    }

    private String slugify(String value) {
        return value
                .replaceAll("([a-z])([A-Z])", "$1-$2")
                .replaceAll("\\s+", "-")
                .toLowerCase(Locale.ROOT);
    }

    private String humanize(String value) {
        return value.replaceAll("([a-z])([A-Z])", "$1 $2").trim();
    }

    private record CatalogSnapshot(List<CatalogDefinition> definitions) {
        private Optional<CatalogDefinition> find(String type) {
            if (type == null || type.isBlank()) {
                return Optional.empty();
            }
            String normalized = type.toLowerCase(Locale.ROOT);
            return definitions.stream()
                    .filter(definition -> definition.matches(normalized))
                    .findFirst();
        }
    }

    private record CatalogDefinition(
            String type,
            String label,
            String category,
            String tier,
            String purpose,
            String implementationStatus,
            List<String> aliases,
            boolean benchmarkable,
            Optional<GraphNodeType> nodeType,
            List<CatalogAttribute> attributes,
            List<CatalogRelationship> relationships
    ) {
        private boolean matches(String candidate) {
            if (type.equalsIgnoreCase(candidate) || label.equalsIgnoreCase(candidate)) {
                return true;
            }
            return aliases.stream().anyMatch(alias -> alias.equalsIgnoreCase(candidate));
        }
    }

    private record CatalogAttribute(
            String name,
            String type,
            boolean required,
            String description,
            String constraints
    ) {
    }

    private record CatalogRelationship(
            String name,
            String direction,
            String target,
            String cardinality,
            boolean required,
            String severity,
            String implementation
    ) {
    }
}
