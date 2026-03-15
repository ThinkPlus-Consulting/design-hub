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

    public ScanController(CodeScannerService scannerService) {
        this.scannerService = scannerService;
    }

    @PostMapping("/orphans")
    public ResponseEntity<Map<String, List<String>>> detectOrphans(@RequestBody ScanRequest request) {
        var orphans = scannerService.detectOrphans(request.repoPath());
        return ResponseEntity.ok(Map.of("orphanedCodeAssets", orphans));
    }

    public record ScanRequest(String repoPath) {}
}
