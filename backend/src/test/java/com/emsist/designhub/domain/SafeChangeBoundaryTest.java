package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SafeChangeBoundaryTest {

    @Test
    void shouldBuildCodeAssetWithChangePolicyAttributes() {
        var asset = CodeAsset.builder()
                .codeAssetId("CA-DH-001")
                .filePath("src/main/java/com/emsist/designhub/domain/Screen.java")
                .changePolicy("EXTENSION_ONLY")
                .ownerAgent("dev-agent")
                .migrationRequired(false)
                .backwardCompatibilityObligation(true)
                .build();

        assertEquals("EXTENSION_ONLY", asset.getChangePolicy());
        assertEquals("dev-agent", asset.getOwnerAgent());
        assertTrue(asset.getBackwardCompatibilityObligation());
    }

    @Test
    void shouldModelDependsOnAssetSelfRelationship() {
        var dependency = CodeAsset.builder()
                .codeAssetId("CA-DH-002")
                .filePath("src/main/java/.../ScreenService.java")
                .build();
        var asset = CodeAsset.builder()
                .codeAssetId("CA-DH-001")
                .filePath("src/main/java/.../ScreenController.java")
                .dependsOn(List.of(dependency))
                .build();

        assertEquals(1, asset.getDependsOn().size());
        assertEquals("CA-DH-002", asset.getDependsOn().get(0).getCodeAssetId());
    }
}
