package com.emsist.designhub.controller;

import com.emsist.designhub.domain.Interaction;
import com.emsist.designhub.service.InteractionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/design-hub/interactions")
@RequiredArgsConstructor
@Tag(name = "Interactions", description = "User interactions on UX surfaces")
public class InteractionController {

    private final InteractionService interactionService;

    @GetMapping
    @Operation(summary = "Get all interactions")
    public ResponseEntity<List<Interaction>> getAll() {
        return ResponseEntity.ok(interactionService.getAll());
    }

    @GetMapping("/by-screen/{surfaceId}")
    @Operation(summary = "Get interactions for a specific screen")
    public ResponseEntity<List<Interaction>> getBySurfaceId(@PathVariable String surfaceId) {
        return ResponseEntity.ok(interactionService.getBySurfaceId(surfaceId));
    }
}
