package com.emsist.designhub.service;

import com.emsist.designhub.dto.CodeAssetCandidate;
import com.emsist.designhub.repository.CodeAssetRepository;
import org.springframework.stereotype.Service;
import java.nio.file.*;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CodeScannerService {

    private final CodeAssetRepository codeAssetRepo;

    public CodeScannerService(CodeAssetRepository codeAssetRepo) {
        this.codeAssetRepo = codeAssetRepo;
    }

    public List<String> detectOrphans(String repoPath) {
        var allAssets = codeAssetRepo.findAll();
        return allAssets.stream()
                .filter(ca -> ca.getFilePath() != null)
                .filter(ca -> !Files.exists(Path.of(repoPath, ca.getFilePath())))
                .map(ca -> ca.getCodeAssetId())
                .collect(Collectors.toList());
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
