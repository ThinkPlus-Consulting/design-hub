package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import com.emsist.designhub.domain.ExternalDependency;

class EnvironmentProfileTest {

    @Test
    void shouldBuildApplicationComponentWithEnvironmentProfile() {
        var comp = ApplicationComponent.builder()
                .componentId("CMP-DH-FE")
                .name("Design Hub Frontend")
                .toolchainVersions(Map.of("node", "20.11.0", "angular-cli", "17.3"))
                .secretPrerequisites(List.of("NEO4J_URI", "NEO4J_PASSWORD"))
                .fixturePrerequisites(List.of("seed data loaded via DataInitializer"))
                .localRunCommand("npm start")
                .localRunPrerequisites(List.of("Neo4j running on localhost:7687"))
                .build();

        assertEquals(2, comp.getToolchainVersions().size());
        assertEquals("20.11.0", comp.getToolchainVersions().get("node"));
        assertEquals("npm start", comp.getLocalRunCommand());
    }

    @Test
    void shouldBuildApplicationWithBootstrapSteps() {
        var app = Application.builder()
                .applicationId("APP-DH")
                .name("Design Hub")
                .bootstrapSteps(List.of(
                        "docker-compose up -d neo4j",
                        "cd backend && mvn spring-boot:run",
                        "cd frontend && npm start"))
                .build();

        assertEquals(3, app.getBootstrapSteps().size());
    }

    @Test
    void shouldBuildComponentWithExternalDependencies() {
        var comp = ApplicationComponent.builder()
                .componentId("CMP-DH-BE")
                .externalDependencies(List.of(
                        ExternalDependency.builder()
                                .name("Neo4j")
                                .healthCheckUrl("http://localhost:7474")
                                .required(true)
                                .build(),
                        ExternalDependency.builder()
                                .name("Valkey")
                                .healthCheckUrl("http://localhost:6379")
                                .required(false)
                                .build()))
                .build();

        assertEquals(2, comp.getExternalDependencies().size());
        assertTrue(comp.getExternalDependencies().get(0).isRequired());
    }
}
