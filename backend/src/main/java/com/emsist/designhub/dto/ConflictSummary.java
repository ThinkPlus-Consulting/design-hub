package com.emsist.designhub.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class ConflictSummary {
    private String nodeId;
    private String field;
    private String docValue;
    private String graphValue;
    private String resolution; // QUEUED, OVERWRITTEN, SKIPPED
}
