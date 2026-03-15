package com.emsist.designhub.controller;

import com.emsist.designhub.dto.ImportResult;
import com.emsist.designhub.service.MarkdownImporterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/import")
public class ImportController {

    private final MarkdownImporterService importerService;

    public ImportController(MarkdownImporterService importerService) {
        this.importerService = importerService;
    }

    @PostMapping
    public ResponseEntity<ImportResult> importDocument(@RequestBody ImportDocRequest request) {
        var result = importerService.importDocument(request.content(), request.filePath());
        int status = "FAILED".equals(result.getResult()) ? 422 : 200;
        return ResponseEntity.status(status).body(result);
    }

    public record ImportDocRequest(String content, String filePath) {}
}
