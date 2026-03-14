package com.emsist.designhub.dto;
import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class NodeSummary {
    private String nodeId;
    private String nodeType;
    private String action; // CREATED, UPDATED, UNCHANGED
    private String confidence; // HIGH, MEDIUM, LOW
}
