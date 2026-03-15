package com.emsist.designhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriftCheckResult {
    private boolean passed;
    private List<DriftItem> docAuthoredDrift;
    private List<DriftItem> graphComputedDrift;
    private List<String> orphanedNodes;
    private List<String> staleNodes;
}
