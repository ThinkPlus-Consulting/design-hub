package com.emsist.designhub.controller;

import com.emsist.designhub.service.ScreenService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/design-hub/stats")
@RequiredArgsConstructor
@Tag(name = "Stats", description = "Aggregate design surface statistics")
public class StatsController {

    private final ScreenService screenService;

    @GetMapping
    @Operation(summary = "Get aggregate statistics for all screens")
    public ResponseEntity<Map<String, Object>> getStats() {
        return ResponseEntity.ok(screenService.getStats());
    }
}
