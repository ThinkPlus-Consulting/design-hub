package com.emsist.designhub.service;

import com.emsist.designhub.domain.CodeAsset;
import com.emsist.designhub.domain.Status;
import com.emsist.designhub.repository.CodeAssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CodeScannerServiceTest {

    @Mock
    private CodeAssetRepository codeAssetRepo;

    @InjectMocks
    private CodeScannerService scanner;

    @Test
    void shouldDetectOrphanedCodeAssets() {
        var orphan = CodeAsset.builder()
                .codeAssetId("CA-001")
                .filePath("src/main/java/Missing.java")
                .status(Status.IMPLEMENTED)
                .build();
        when(codeAssetRepo.findAll()).thenReturn(List.of(orphan));

        var orphans = scanner.detectOrphans("/nonexistent/repo");
        assertEquals(1, orphans.size());
        assertEquals("CA-001", orphans.get(0));
    }

    @Test
    void shouldClassifyJavaTestFile() {
        var result = scanner.classifyFile("src/test/java/com/example/FooTest.java");
        assertEquals("TEST", result.getAssetType());
        assertEquals("java", result.getLanguage());
    }

    @Test
    void shouldClassifyJavaSourceFile() {
        var result = scanner.classifyFile("src/main/java/com/example/FooService.java");
        assertEquals("SOURCE", result.getAssetType());
        assertEquals("java", result.getLanguage());
    }

    @Test
    void shouldClassifyTypeScriptFile() {
        var result = scanner.classifyFile("src/app/components/foo.component.ts");
        assertEquals("SOURCE", result.getAssetType());
        assertEquals("typescript", result.getLanguage());
    }
}
