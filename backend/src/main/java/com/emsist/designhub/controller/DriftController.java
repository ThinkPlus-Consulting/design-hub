package com.emsist.designhub.controller;

import com.emsist.designhub.dto.DriftCheckResult;
import com.emsist.designhub.dto.DriftItem;
import com.emsist.designhub.service.DriftCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/drift-check")
public class DriftController {

    private final DriftCheckService driftCheckService;

    public DriftController(DriftCheckService driftCheckService) {
        this.driftCheckService = driftCheckService;
    }

    @PostMapping
    public ResponseEntity<DriftCheckResult> checkAll(@RequestBody DriftCheckBatchRequest request) {
        var fieldChecks = request.checks().stream()
                .map(c -> new DriftCheckService.FieldCheck(
                        c.nodeId(), c.field(), c.graphValue(), c.docValue(), c.driftType()))
                .toList();
        var result = driftCheckService.checkAll(fieldChecks);
        int status = result.isPassed() ? 200 : 422;
        return ResponseEntity.status(status).body(result);
    }

    public record DriftCheckBatchRequest(List<FieldCheckRequest> checks) {}
    public record FieldCheckRequest(String nodeId, String field,
                                     String graphValue, String docValue,
                                     DriftItem.DriftType driftType) {}
}
