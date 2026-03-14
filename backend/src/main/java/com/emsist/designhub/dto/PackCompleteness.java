package com.emsist.designhub.dto;
import lombok.*;
import java.util.List;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PackCompleteness {
    private boolean complete;
    private List<String> missingConcerns;
    private List<String> missingFields;
    private int readinessScore;
}
