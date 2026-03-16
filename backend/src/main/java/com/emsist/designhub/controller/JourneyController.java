package com.emsist.designhub.controller;

import com.emsist.designhub.domain.Journey;
import com.emsist.designhub.service.JourneyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/design-hub/journeys")
@RequiredArgsConstructor
@Tag(name = "Journeys", description = "User journey flows through UX surfaces")
public class JourneyController {

    private final JourneyService journeyService;

    @GetMapping
    @Operation(summary = "Get all journeys")
    public ResponseEntity<List<Journey>> getAll() {
        return ResponseEntity.ok(journeyService.getAll());
    }
}
