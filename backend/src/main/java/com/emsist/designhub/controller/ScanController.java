package com.emsist.designhub.controller;

import com.emsist.designhub.service.CodeScannerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/scan")
public class ScanController {

    private final CodeScannerService scannerService;
    private final List<String> allowedRepoPaths;

    public ScanController(CodeScannerService scannerService,
                           @org.springframework.beans.factory.annotation.Value("${designhub.scan.allowed-repo-paths:}") String allowedPaths) {
        this.scannerService = scannerService;
        this.allowedRepoPaths = allowedPaths.isBlank()
                ? List.of()
                : List.of(allowedPaths.split(","));
    }

    @PostMapping("/orphans")
    public ResponseEntity<Map<String, Object>> detectOrphans(@RequestBody ScanRequest request) {
        if (!isAllowedPath(request.repoPath())) {
            return ResponseEntity.status(403)
                    .body(Map.of("error", "Path not in allowed repository paths",
                                 "repoPath", request.repoPath()));
        }
        var orphans = scannerService.detectOrphans(request.repoPath());
        return ResponseEntity.ok(Map.of("orphanedCodeAssets", orphans));
    }

    private boolean isAllowedPath(String repoPath) {
        if (repoPath == null || repoPath.isBlank()) return false;
        if (allowedRepoPaths.isEmpty()) return true; // No restrictions configured
        return allowedRepoPaths.stream()
                .anyMatch(allowed -> repoPath.startsWith(allowed.trim()));
    }

    public record ScanRequest(String repoPath) {}
}
