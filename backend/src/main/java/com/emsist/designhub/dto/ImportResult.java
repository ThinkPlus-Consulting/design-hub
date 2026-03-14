package com.emsist.designhub.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ImportResult {
    private String snapshotId;
    private String result; // SUCCESS, PARTIAL, FAILED, CONFLICTED
    private List<NodeSummary> created;
    private List<NodeSummary> updated;
    private List<ConflictSummary> conflicts;
    private List<String> errors;
    private String diffReport;
}
