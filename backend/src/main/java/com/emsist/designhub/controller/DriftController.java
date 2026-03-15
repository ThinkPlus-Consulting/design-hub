package com.emsist.designhub.controller;

import com.emsist.designhub.dto.DriftItem;
import com.emsist.designhub.service.DriftCheckService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/drift-check")
public class DriftController {

    private final DriftCheckService driftCheckService;

    public DriftController(DriftCheckService driftCheckService) {
        this.driftCheckService = driftCheckService;
    }

    @PostMapping
    public ResponseEntity<DriftItem> checkField(@RequestBody DriftCheckRequest request) {
        var result = driftCheckService.checkField(
                request.nodeId(), request.field(),
                request.graphValue(), request.docValue(),
                request.driftType());
        if (result == null) {
            return ResponseEntity.ok().build(); // No drift
        }
        int status = result.getDriftType() == DriftItem.DriftType.DOC_AUTHORED ? 422 : 200;
        return ResponseEntity.status(status).body(result);
    }

    public record DriftCheckRequest(String nodeId, String field,
                                     String graphValue, String docValue,
                                     DriftItem.DriftType driftType) {}
}
