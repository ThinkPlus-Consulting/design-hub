package com.emsist.designhub.controller;

import com.emsist.designhub.dto.RoleResponse;
import com.emsist.designhub.service.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/design-hub/roles")
@RequiredArgsConstructor
@Tag(name = "Roles", description = "Canonical roles as first-class graph objects")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    @Operation(summary = "Get role graph summaries")
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.getAll());
    }
}
