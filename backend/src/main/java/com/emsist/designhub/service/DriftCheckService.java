package com.emsist.designhub.service;

import com.emsist.designhub.dto.DriftItem;
import org.springframework.stereotype.Service;

@Service
public class DriftCheckService {

    private final RequirementSyncService syncService;

    public DriftCheckService(RequirementSyncService syncService) {
        this.syncService = syncService;
    }

    public DriftItem checkField(String nodeId, String field,
                                 String graphValue, String docValue,
                                 DriftItem.DriftType driftType) {
        if (graphValue == null && docValue == null) return null;
        if (graphValue != null && graphValue.equals(docValue)) return null;

        return DriftItem.builder()
                .nodeId(nodeId)
                .field(field)
                .graphValue(graphValue)
                .docValue(docValue)
                .driftType(driftType)
                .build();
    }
}
