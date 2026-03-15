package com.emsist.designhub.service;

import com.emsist.designhub.dto.DriftCheckResult;
import com.emsist.designhub.dto.DriftItem;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

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

    public DriftCheckResult checkAll(List<FieldCheck> fieldChecks) {
        List<DriftItem> docAuthored = new ArrayList<>();
        List<DriftItem> graphComputed = new ArrayList<>();

        for (var check : fieldChecks) {
            var item = checkField(check.nodeId(), check.field(),
                    check.graphValue(), check.docValue(), check.driftType());
            if (item != null) {
                if (item.getDriftType() == DriftItem.DriftType.DOC_AUTHORED) {
                    docAuthored.add(item);
                } else {
                    graphComputed.add(item);
                }
            }
        }

        boolean passed = docAuthored.isEmpty();
        return DriftCheckResult.builder()
                .passed(passed)
                .docAuthoredDrift(docAuthored)
                .graphComputedDrift(graphComputed)
                .orphanedNodes(List.of())
                .staleNodes(List.of())
                .build();
    }

    public record FieldCheck(String nodeId, String field,
                              String graphValue, String docValue,
                              DriftItem.DriftType driftType) {}
}
