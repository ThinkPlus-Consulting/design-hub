package com.emsist.designhub.dto;
import lombok.*;
import java.time.Instant;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ScanResult {
    private String scanId;
    private Instant scannedAt;
    private String repoCommit;
    private String branch;
    private List<CodeAssetCandidate> discovered;
    private List<CodeAssetCandidate> updated;
    private List<String> orphaned;
    private List<String> undocumented;
    private List<String> implementationGaps;
    private List<TestDiscoveryResult> testDiscovery;
}
