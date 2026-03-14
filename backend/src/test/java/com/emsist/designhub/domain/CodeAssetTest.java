package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CodeAssetTest {

    @Test
    void shouldBuildCodeAssetWithRequiredFields() {
        CodeAsset asset = CodeAsset.builder()
                .codeAssetId("CA-CMP-BACKEND-001")
                .filePath("src/main/java/com/emsist/designhub/domain/Screen.java")
                .assetType("SOURCE")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("CA-CMP-BACKEND-001", asset.getCodeAssetId());
        assertEquals("src/main/java/com/emsist/designhub/domain/Screen.java", asset.getFilePath());
        assertEquals("SOURCE", asset.getAssetType());
        assertEquals(Status.IDENTIFIED, asset.getStatus());
    }

    @Test
    void shouldBuildCodeAssetWithOptionalFields() {
        CodeAsset asset = CodeAsset.builder()
                .codeAssetId("CA-CMP-BACKEND-002")
                .filePath("src/main/java/com/emsist/designhub/domain/Screen.java")
                .assetType("SOURCE")
                .language("JAVA")
                .layerType("DOMAIN")
                .packageName("com.emsist.designhub.domain")
                .className("Screen")
                .description("Screen domain entity")
                .status(Status.IMPLEMENTED)
                .build();

        assertEquals("JAVA", asset.getLanguage());
        assertEquals("DOMAIN", asset.getLayerType());
        assertEquals("com.emsist.designhub.domain", asset.getPackageName());
        assertEquals("Screen", asset.getClassName());
    }

    @Test
    void shouldNotRequireFilePathToStartWithSlash() {
        CodeAsset asset = CodeAsset.builder()
                .codeAssetId("CA-CMP-FE-001")
                .filePath("src/app/features/design-hub/design-hub.page.ts")
                .assetType("SOURCE")
                .language("TYPESCRIPT")
                .status(Status.IDENTIFIED)
                .build();

        assertFalse(asset.getFilePath().startsWith("/"),
                "filePath must be relative (not start with /)");
    }

    @Test
    void shouldSupportTestAssetType() {
        CodeAsset asset = CodeAsset.builder()
                .codeAssetId("CA-CMP-BACKEND-003")
                .filePath("src/test/java/com/emsist/designhub/domain/ScreenTest.java")
                .assetType("TEST")
                .language("JAVA")
                .layerType("TEST")
                .status(Status.IMPLEMENTED)
                .build();

        assertEquals("TEST", asset.getAssetType());
        assertEquals("TEST", asset.getLayerType());
    }

    @Test
    void shouldSupportConfigAndStyleAssetTypes() {
        CodeAsset config = CodeAsset.builder()
                .codeAssetId("CA-CMP-BACKEND-004")
                .filePath("src/main/resources/application.yml")
                .assetType("CONFIG")
                .fileFormat("YAML")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("CONFIG", config.getAssetType());
        assertEquals("YAML", config.getFileFormat());

        CodeAsset style = CodeAsset.builder()
                .codeAssetId("CA-CMP-FE-002")
                .filePath("src/styles.scss")
                .assetType("STYLE")
                .fileFormat("SCSS")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("STYLE", style.getAssetType());
    }
}
