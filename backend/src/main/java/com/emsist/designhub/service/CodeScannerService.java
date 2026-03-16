package com.emsist.designhub.service;

import com.emsist.designhub.dto.CodeAssetCandidate;
import com.emsist.designhub.repository.CodeAssetRepository;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CodeScannerService {

    private final CodeAssetRepository codeAssetRepo;
    private final Neo4jClient neo4jClient;

    public CodeScannerService(CodeAssetRepository codeAssetRepo, Neo4jClient neo4jClient) {
        this.codeAssetRepo = codeAssetRepo;
        this.neo4jClient = neo4jClient;
    }

    /**
     * Detect orphaned code assets by resolving the full filesystem path:
     * repoPath + ApplicationComponent.modulePath + CodeAsset.filePath
     *
     * If a CodeAsset has no owning ApplicationComponent, falls back to
     * repoPath + filePath (graceful degradation).
     */
    public List<String> detectOrphans(String repoPath) {
        // Query all code assets with their owning component's modulePath
        var assetPaths = neo4jClient.query(
                "MATCH (ca:CodeAsset) " +
                "OPTIONAL MATCH (comp:ApplicationComponent)-[:HAS_CODE_ASSET]->(ca) " +
                "RETURN ca.codeAssetId AS assetId, ca.filePath AS filePath, " +
                "       comp.modulePath AS modulePath")
                .fetch().all();

        List<String> orphans = new ArrayList<>();
        for (var record : assetPaths) {
            String assetId = (String) record.get("assetId");
            String filePath = (String) record.get("filePath");
            if (filePath == null) continue;

            String modulePath = (String) record.get("modulePath");
            Path fullPath;
            if (modulePath != null && !modulePath.isBlank()) {
                fullPath = Path.of(repoPath, modulePath, filePath);
            } else {
                fullPath = Path.of(repoPath, filePath);
            }

            if (!Files.exists(fullPath)) {
                orphans.add(assetId);
            }
        }
        return orphans;
    }

    public CodeAssetCandidate classifyFile(String filePath) {
        String language = detectLanguage(filePath);
        String assetType = detectAssetType(filePath);
        return CodeAssetCandidate.builder()
                .filePath(filePath)
                .language(language)
                .assetType(assetType)
                .build();
    }

    private String detectLanguage(String path) {
        if (path.endsWith(".java")) return "java";
        if (path.endsWith(".ts")) return "typescript";
        if (path.endsWith(".js")) return "javascript";
        if (path.endsWith(".py")) return "python";
        if (path.endsWith(".html")) return "html";
        if (path.endsWith(".scss") || path.endsWith(".css")) return "css";
        return "unknown";
    }

    private String detectAssetType(String path) {
        if (path.contains("/test/") || path.contains(".spec.") || path.contains("Test.java")) {
            return "TEST";
        }
        if (path.endsWith(".json") || path.endsWith(".yml") || path.endsWith(".yaml")) {
            return "CONFIG";
        }
        return "SOURCE";
    }
}
