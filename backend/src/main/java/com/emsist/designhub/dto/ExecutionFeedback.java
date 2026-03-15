package com.emsist.designhub.dto;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExecutionFeedback {
    private String feedbackId;           // Pattern: EXEC-{packId}-{seq}
    private String packId;               // Which pack was used
    private int packVersion;
    private Instant executedAt;
    private String executedBy;           // Agent identifier
    private List<String> plannedFiles;
    private List<String> actualFilesTouched;
    private List<String> newFilesCreated;
    private List<TestResultEntry> testsRun;
    private List<String> testsSkipped;
    private List<Deviation> deviations;
    private String result;               // ALIGNED, DEVIATED, FAILED

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TestResultEntry {
        private String testId;
        private String result;           // PASS, FAIL, ERROR, SKIPPED
        private long durationMs;
    }
}
