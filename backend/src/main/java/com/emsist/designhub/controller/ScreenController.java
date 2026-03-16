package com.emsist.designhub.controller;

import com.emsist.designhub.dto.RoleResponse;
import com.emsist.designhub.dto.ScreenResponse;
import com.emsist.designhub.dto.UserStoryResponse;
import com.emsist.designhub.service.RoleService;
import com.emsist.designhub.service.ScreenService;
import com.emsist.designhub.service.UserStoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/design-hub/screens")
@RequiredArgsConstructor
@Tag(name = "Screens", description = "UX design surface management")
public class ScreenController {

    private final ScreenService screenService;
    private final RoleService roleService;
    private final UserStoryService userStoryService;

    @GetMapping
    @Operation(summary = "Get all screens")
    public ResponseEntity<List<ScreenResponse>> getAllScreens() {
        return ResponseEntity.ok(toScreenResponses(screenService.getAllScreens()));
    }

    @GetMapping("/{surfaceId}")
    @Operation(summary = "Get single screen with full graph (gaps, content, transitions)")
    public ResponseEntity<ScreenResponse> getScreen(@PathVariable String surfaceId) {
        Map<String, RoleResponse> roleLookup = roleLookup();
        Map<String, UserStoryResponse> storyLookup = storyLookup();
        return screenService.getScreen(surfaceId)
                .map(screen -> ScreenResponse.from(screen, roleLookup, storyLookup))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filtered")
    @Operation(summary = "Get screens filtered by module and/or design status")
    public ResponseEntity<List<ScreenResponse>> getFilteredScreens(
            @RequestParam(required = false) String module,
            @RequestParam(required = false) String status) {
        return ResponseEntity.ok(toScreenResponses(screenService.getFilteredScreens(module, status)));
    }

    @PutMapping("/{surfaceId}/notes")
    @Operation(summary = "Save notes for a screen")
    public ResponseEntity<ScreenResponse> saveNotes(
            @PathVariable String surfaceId,
            @RequestBody Map<String, String> body) {
        String text = body.getOrDefault("text", "");
        return ResponseEntity.ok(ScreenResponse.from(
                screenService.saveNotes(surfaceId, text),
                roleLookup(),
                storyLookup()
        ));
    }

    @GetMapping("/{surfaceId}/notes")
    @Operation(summary = "Get notes for a screen")
    public ResponseEntity<Map<String, String>> getNotes(@PathVariable String surfaceId) {
        return screenService.getNotes(surfaceId)
                .map(notes -> ResponseEntity.ok(Map.of("text", notes)))
                .orElse(ResponseEntity.notFound().build());
    }

    private List<ScreenResponse> toScreenResponses(List<com.emsist.designhub.domain.Screen> screens) {
        Map<String, RoleResponse> roleLookup = roleLookup();
        Map<String, UserStoryResponse> storyLookup = storyLookup();
        return screens.stream()
                .map(screen -> ScreenResponse.from(screen, roleLookup, storyLookup))
                .toList();
    }

    private Map<String, RoleResponse> roleLookup() {
        return roleService.getAll().stream().collect(Collectors.toMap(
                RoleResponse::roleKey,
                Function.identity(),
                (left, right) -> left,
                LinkedHashMap::new
        ));
    }

    private Map<String, UserStoryResponse> storyLookup() {
        return userStoryService.getAll().stream().collect(Collectors.toMap(
                UserStoryResponse::storyId,
                Function.identity(),
                (left, right) -> left,
                LinkedHashMap::new
        ));
    }
}
