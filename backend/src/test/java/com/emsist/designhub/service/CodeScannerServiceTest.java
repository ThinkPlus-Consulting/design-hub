package com.emsist.designhub.service;

import com.emsist.designhub.repository.CodeAssetRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodeScannerServiceTest {

    @Mock
    private CodeAssetRepository codeAssetRepo;

    @Mock
    private Neo4jClient neo4jClient;

    @InjectMocks
    private CodeScannerService scanner;

    @Test
    @SuppressWarnings("unchecked")
    void shouldDetectOrphanedCodeAssets() {
        // Mock the Neo4jClient query chain for detectOrphans
        var unboundSpec = mock(Neo4jClient.UnboundRunnableSpec.class);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);

        when(neo4jClient.query(anyString())).thenReturn(unboundSpec);
        when(unboundSpec.fetch()).thenReturn(fetchSpec);

        // Return a record with no modulePath and a non-existent filePath
        Collection<Map<String, Object>> records = List.of(
                Map.of("assetId", "CA-001", "filePath", "src/main/java/Missing.java")
                // modulePath absent → null in the map
        );
        when(fetchSpec.all()).thenReturn(records);

        var orphans = scanner.detectOrphans("/nonexistent/repo");
        assertEquals(1, orphans.size());
        assertEquals("CA-001", orphans.get(0));
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldResolveModulePathWhenDetectingOrphans(@TempDir Path tempDir) throws IOException {
        // Create file at the modulePath-joined path: tempDir/backend/src/main/java/Foo.java
        Path moduleJoinedPath = tempDir.resolve("backend/src/main/java/Foo.java");
        Files.createDirectories(moduleJoinedPath.getParent());
        Files.createFile(moduleJoinedPath);

        // Do NOT create at tempDir/src/main/java/Foo.java (the fallback path).
        // This discriminates: if the service ignores modulePath, it checks the
        // wrong path, doesn't find the file, and incorrectly flags as orphan.

        var unboundSpec = mock(Neo4jClient.UnboundRunnableSpec.class);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);

        when(neo4jClient.query(anyString())).thenReturn(unboundSpec);
        when(unboundSpec.fetch()).thenReturn(fetchSpec);

        Map<String, Object> record = new java.util.HashMap<>();
        record.put("assetId", "CA-002");
        record.put("filePath", "src/main/java/Foo.java");
        record.put("modulePath", "backend");
        Collection<Map<String, Object>> records = List.of(record);
        when(fetchSpec.all()).thenReturn(records);

        var orphans = scanner.detectOrphans(tempDir.toString());

        // Asset should NOT be orphaned — file exists at repoPath/modulePath/filePath
        assertTrue(orphans.isEmpty(),
                "Asset should not be orphaned when file exists at modulePath-joined path");
        verify(neo4jClient).query(anyString());
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
