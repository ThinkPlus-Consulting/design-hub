package com.emsist.designhub.controller;

import com.emsist.designhub.dto.ReadinessDiagnosticsResponse;
import com.emsist.designhub.service.ReadinessDiagnosticsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/readiness")
@RequiredArgsConstructor
@Tag(name = "Readiness", description = "Readiness and completeness diagnostics")
public class ReadinessController {

    private final ReadinessDiagnosticsService readinessDiagnosticsService;

    @GetMapping("/screens/{surfaceId}")
    @Operation(summary = "Get readiness and completeness diagnostics for a Screen")
    public ResponseEntity<ReadinessDiagnosticsResponse> getScreenDiagnostics(@PathVariable String surfaceId) {
        return readinessDiagnosticsService.assessScreen(surfaceId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stories/{storyId}")
    @Operation(summary = "Get readiness and completeness diagnostics for a UserStory")
    public ResponseEntity<ReadinessDiagnosticsResponse> getStoryDiagnostics(@PathVariable String storyId) {
        return readinessDiagnosticsService.assessStory(storyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
