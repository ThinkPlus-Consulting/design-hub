package com.emsist.designhub.controller;

import com.emsist.designhub.dto.UserStoryResponse;
import com.emsist.designhub.service.UserStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/design-hub/stories")
@RequiredArgsConstructor
@Tag(name = "User Stories", description = "Canonical user stories as first-class graph objects")
public class UserStoryController {

    private final UserStoryService userStoryService;

    @GetMapping
    @Operation(summary = "Get user story graph summaries")
    public ResponseEntity<List<UserStoryResponse>> getAll() {
        return ResponseEntity.ok(userStoryService.getAll());
    }
}
