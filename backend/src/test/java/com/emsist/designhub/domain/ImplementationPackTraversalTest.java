package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ImplementationPackTraversalTest {

    @Test
    void shouldTraverseStoryToVerificationAndTaskTargets() {
        CodeAsset asset = CodeAsset.builder()
                .codeAssetId("CA-FE-BUILDER-001")
                .filePath("src/app/features/design-hub/design-hub.page.ts")
                .assetType("SOURCE")
                .status(Status.IMPLEMENTED)
                .build();

        ApplicationComponent component = ApplicationComponent.builder()
                .componentId("CMP-DH-FRONTEND")
                .name("Design Hub Frontend")
                .status(Status.IMPLEMENTED)
                .build();

        Task task = Task.builder()
                .taskId("TASK-US-AI-090-001")
                .title("Update builder canvas")
                .taskType("FRONTEND")
                .status(Status.IN_IMPLEMENTATION)
                .implementsAssets(List.of(asset))
                .implementsComponents(List.of(component))
                .build();

        TestCase testCase = TestCase.builder()
                .testCaseId("TC-US-AI-090-001")
                .title("Builder interaction smoke")
                .status(Status.DEFINED)
                .locatedIn(asset)
                .build();

        UserStory story = UserStory.builder()
                .storyId("US-AI-090")
                .label("As an agent designer, I can compose an agent on the builder canvas")
                .tasks(List.of(task))
                .verifiedByTests(List.of(testCase))
                .build();

        assertEquals("CMP-DH-FRONTEND", story.getTasks().get(0).getImplementsComponents().get(0).getComponentId());
        assertEquals("CA-FE-BUILDER-001", story.getTasks().get(0).getImplementsAssets().get(0).getCodeAssetId());
        assertEquals("CA-FE-BUILDER-001", story.getVerifiedByTests().get(0).getLocatedIn().getCodeAssetId());
    }

    @Test
    void shouldTraverseComponentToOwnedArtifacts() {
        Screen screen = Screen.builder()
                .surfaceId("SCR-AGT-BUILDER")
                .label("Agent Builder")
                .status(Status.IN_DEFINITION)
                .build();

        ApiContract api = ApiContract.builder()
                .contractId("API-POST-API-V1-AGENTS-ID-PUBLISH")
                .method("POST")
                .path("/api/v1/agents/{id}/publish")
                .status(Status.DEFINED)
                .build();

        DataEntity entity = DataEntity.builder()
                .entityId("DE-AGENT")
                .name("Agent")
                .status(Status.DEFINED)
                .build();

        Rule rule = Rule.builder()
                .ruleId("RULE-AGENT-PUBLISH-001")
                .name("Only published-ready agents may be published")
                .status(Status.DEFINED)
                .build();

        ApplicationComponent component = ApplicationComponent.builder()
                .componentId("CMP-DH-BACKEND")
                .name("Design Hub Backend")
                .status(Status.IMPLEMENTED)
                .supportedScreens(List.of(screen))
                .exposedApis(List.of(api))
                .ownedEntities(List.of(entity))
                .enforcedRules(List.of(rule))
                .build();

        assertEquals("SCR-AGT-BUILDER", component.getSupportedScreens().get(0).getSurfaceId());
        assertEquals("API-POST-API-V1-AGENTS-ID-PUBLISH", component.getExposedApis().get(0).getContractId());
        assertEquals("DE-AGENT", component.getOwnedEntities().get(0).getEntityId());
        assertEquals("RULE-AGENT-PUBLISH-001", component.getEnforcedRules().get(0).getRuleId());
    }

    @Test
    void shouldTraverseComponentDependenciesAndConventions() {
        CodingConvention convention = CodingConvention.builder()
                .conventionCode("CONV-FE-TEST-001")
                .name("Frontend testing convention")
                .category("TESTING")
                .enforcement("MANDATORY")
                .scope("FRONTEND")
                .docRef("docs/conventions/frontend-testing.md")
                .build();

        ApplicationComponent backend = ApplicationComponent.builder()
                .componentId("CMP-DH-BACKEND")
                .name("Design Hub Backend")
                .status(Status.IMPLEMENTED)
                .build();

        ApplicationComponent frontend = ApplicationComponent.builder()
                .componentId("CMP-DH-FRONTEND")
                .name("Design Hub Frontend")
                .status(Status.IMPLEMENTED)
                .dependsOnComponents(List.of(backend))
                .conventions(List.of(convention))
                .build();

        assertEquals("CMP-DH-BACKEND", frontend.getDependsOnComponents().get(0).getComponentId());
        assertEquals("CONV-FE-TEST-001", frontend.getConventions().get(0).getConventionCode());
    }
}
