package com.emsist.designhub.controller;

import com.emsist.designhub.dto.LintResult;
import com.emsist.designhub.service.RequirementLinterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/lint")
public class LintController {

    private final RequirementLinterService linterService;

    public LintController(RequirementLinterService linterService) {
        this.linterService = linterService;
    }

    @PostMapping
    public ResponseEntity<LintResult> lint(@RequestBody LintRequest request) {
        var result = linterService.lint(request.content(), request.filePath());
        int status = result.hasBlockingErrors() ? 422 : 200;
        return ResponseEntity.status(status).body(result);
    }

    public record LintRequest(String content, String filePath) {}
}
