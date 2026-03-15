package com.emsist.designhub.controller;

import com.emsist.designhub.dto.ImportRequest;
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
    public ResponseEntity<ImportResult> importDocument(@RequestBody ImportRequest request) {
        // For now, process the first source. Multi-source batch is a future enhancement.
        if (request.getSources() == null || request.getSources().isEmpty()) {
            var result = ImportResult.builder()
                    .result("FAILED")
                    .errors(java.util.List.of("No sources provided"))
                    .build();
            return ResponseEntity.unprocessableEntity().body(result);
        }

        String filePath = request.getSources().get(0);
        // Content must be resolved from file system or provided separately.
        // For now, the controller delegates single-file import.
        // The importer reads content from the file path.
        var result = importerService.importFile(filePath, request.getConflictStrategy());
        int status = switch (result.getResult()) {
            case "FAILED" -> 422;
            case "CONFLICTED" -> 409;
            default -> 200;
        };
        return ResponseEntity.status(status).body(result);
    }
}
