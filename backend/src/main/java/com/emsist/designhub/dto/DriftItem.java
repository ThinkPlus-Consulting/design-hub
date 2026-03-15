package com.emsist.designhub.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriftItem {
    private String nodeId;
    private String field;
    private String graphValue;
    private String docValue;
    private DriftType driftType;

    public enum DriftType { DOC_AUTHORED, GRAPH_COMPUTED }
}
