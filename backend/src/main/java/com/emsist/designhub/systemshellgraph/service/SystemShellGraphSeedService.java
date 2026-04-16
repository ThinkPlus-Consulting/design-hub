package com.emsist.designhub.systemshellgraph.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class SystemShellGraphSeedService implements CommandLineRunner {

    private static final String GRAPH_SCOPE = SystemShellGraphQueryService.GRAPH_SCOPE;
    public static final String COMPONENT_REGISTRY_SCOPE = "SYSTEM_COMPONENT_REGISTRY";
    private static final String LEGACY_LOGIN_SCOPE = "LOGIN_SYSTEM_SHELL";
    private static final String RESTORE_SCRIPT_RESOURCE = "system-shell-graph-restore.cypher";

    private final Neo4jClient neo4jClient;

    @Value("${systemshellgraph.seed-data:true}")
    private boolean seedDataEnabled;

    @Override
    @Transactional
    public void run(String... args) {
        if (!seedDataEnabled) {
            log.info("Skipping system-shell graph seed because systemshellgraph.seed-data=false.");
            return;
        }
        reseedCurrentScope();
    }

    @Transactional
    public void reseedCurrentScope() {
        resetScopes();
        List<String> statements = loadRestoreStatements();
        for (String statement : statements) {
            neo4jClient.query(statement).run();
        }

        long nodeCount = countNodes();
        long relationshipCount = countRelationships();
        log.info(
                "Loaded {} Cypher statements into Neo4j for scopes {} and {}. Current graph has {} nodes and {} relationships.",
                statements.size(),
                GRAPH_SCOPE,
                COMPONENT_REGISTRY_SCOPE,
                nodeCount,
                relationshipCount
        );
    }

    private void resetScopes() {
        neo4jClient.query("""
                        MATCH (n:SystemShellGraphNode)
                        WHERE n.graphScope IN $graphScopes
                        DETACH DELETE n
                        """)
                .bind(List.of(GRAPH_SCOPE, COMPONENT_REGISTRY_SCOPE, LEGACY_LOGIN_SCOPE))
                .to("graphScopes")
                .run();

        // Previous broken seeds created unlabeled nodes when relationship statements referenced
        // transient Cypher variables across separate statements. Clean those invalid leftovers
        // before loading the corrected id-bound graph.
        neo4jClient.query("""
                        MATCH (n)
                        WHERE size(labels(n)) = 0
                        DETACH DELETE n
                        """)
                .run();
    }

    private List<String> loadRestoreStatements() {
        try {
            String script = new String(
                    new ClassPathResource(RESTORE_SCRIPT_RESOURCE).getInputStream().readAllBytes(),
                    StandardCharsets.UTF_8
            );
            return parseStatements(script).stream()
                    .filter(statement -> !statement.isBlank())
                    .toList();
        } catch (IOException exception) {
            throw new IllegalStateException("Unable to load graph restore resource " + RESTORE_SCRIPT_RESOURCE, exception);
        }
    }

    private List<String> parseStatements(String script) {
        StringBuilder cleaned = new StringBuilder();
        for (String line : script.replace("\r\n", "\n").split("\n", -1)) {
            String trimmed = line.trim();
            if (trimmed.startsWith("//")) {
                continue;
            }
            cleaned.append(line).append('\n');
        }

        List<String> statements = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inString = false;

        for (int index = 0; index < cleaned.length(); index += 1) {
            char ch = cleaned.charAt(index);
            if (ch == '\'' && !isEscaped(cleaned, index)) {
                inString = !inString;
            }
            if (ch == ';' && !inString) {
                String statement = current.toString().trim();
                if (!statement.isBlank()) {
                    statements.add(statement);
                }
                current.setLength(0);
                continue;
            }
            current.append(ch);
        }

        String trailing = current.toString().trim();
        if (!trailing.isBlank()) {
            statements.add(trailing);
        }
        return statements;
    }

    private boolean isEscaped(CharSequence value, int index) {
        int backslashCount = 0;
        for (int cursor = index - 1; cursor >= 0 && value.charAt(cursor) == '\\'; cursor -= 1) {
            backslashCount += 1;
        }
        return backslashCount % 2 == 1;
    }

    private long countNodes() {
        return neo4jClient.query("""
                        MATCH (n:SystemShellGraphNode)
                        WHERE n.graphScope IN $graphScopes
                        RETURN count(n) AS nodeCount
                        """)
                .bind(List.of(GRAPH_SCOPE, COMPONENT_REGISTRY_SCOPE))
                .to("graphScopes")
                .fetchAs(Long.class)
                .one()
                .orElse(0L);
    }

    private long countRelationships() {
        return neo4jClient.query("""
                        MATCH (from:SystemShellGraphNode)-[r]->(to:SystemShellGraphNode)
                        WHERE from.graphScope IN $graphScopes
                          AND to.graphScope IN $graphScopes
                        RETURN count(r) AS relationshipCount
                        """)
                .bind(List.of(GRAPH_SCOPE, COMPONENT_REGISTRY_SCOPE))
                .to("graphScopes")
                .fetchAs(Long.class)
                .one()
                .orElse(0L);
    }
}
