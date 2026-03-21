package com.emsist.designhub.controller;

import com.emsist.designhub.dto.DeliveryStoryResponse;
import com.emsist.designhub.service.DeliveryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/delivery")
@RequiredArgsConstructor
@Tag(name = "Delivery", description = "Graph-backed delivery intelligence queries")
public class DeliveryController {

    private final DeliveryQueryService deliveryQueryService;

    @GetMapping("/stories")
    @Operation(summary = "Get delivery-oriented story aggregates with linked screens, APIs, diagnostics, and external context")
    public ResponseEntity<List<DeliveryStoryResponse>> getStories(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String feature,
            @RequestParam(required = false) String module,
            @RequestParam(required = false) Boolean isReady,
            @RequestParam(required = false) Boolean hasScreens,
            @RequestParam(required = false) Boolean hasApis,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String sortBy,
            @RequestParam(required = false) String sortDirection
    ) {
        return ResponseEntity.ok(deliveryQueryService.getStories(
                status,
                feature,
                module,
                isReady,
                hasScreens,
                hasApis,
                search,
                sortBy,
                sortDirection
        ));
    }

    @GetMapping("/stories/{storyId}")
    @Operation(summary = "Get a single delivery-oriented story aggregate")
    public ResponseEntity<DeliveryStoryResponse> getStory(@PathVariable String storyId) {
        return deliveryQueryService.getStory(storyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
