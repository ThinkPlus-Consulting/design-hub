package com.emsist.designhub.controller;

import com.emsist.designhub.dto.PackCompleteness;
import com.emsist.designhub.dto.AgentPackExportResponse;
import com.emsist.designhub.service.AgentPackService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/stories")
public class AgentPackController {

    private final AgentPackService packService;

    public AgentPackController(AgentPackService packService) {
        this.packService = packService;
    }

    @GetMapping("/{storyId}/agent-pack/completeness")
    public ResponseEntity<PackCompleteness> getCompleteness(@PathVariable String storyId) {
        var completeness = packService.computeCompleteness(storyId);
        return ResponseEntity.ok(completeness);
    }

    @GetMapping("/{storyId}/agent-pack")
    public ResponseEntity<AgentPackExportResponse> getAgentPack(@PathVariable String storyId) {
        return packService.buildPack(storyId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
