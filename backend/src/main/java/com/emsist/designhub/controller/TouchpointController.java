package com.emsist.designhub.controller;

import com.emsist.designhub.domain.Touchpoint;
import com.emsist.designhub.service.TouchpointService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/design-hub/touchpoints")
@RequiredArgsConstructor
@Tag(name = "Touchpoints", description = "Entry points into UX surfaces")
public class TouchpointController {

    private final TouchpointService touchpointService;

    @GetMapping
    @Operation(summary = "Get all touchpoints")
    public ResponseEntity<List<Touchpoint>> getAll() {
        return ResponseEntity.ok(touchpointService.getAll());
    }
}
