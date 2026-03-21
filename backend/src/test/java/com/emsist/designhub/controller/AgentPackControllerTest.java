package com.emsist.designhub.controller;

import com.emsist.designhub.dto.AgentPackExportResponse;
import com.emsist.designhub.dto.GraphNodeReference;
import com.emsist.designhub.dto.PackCompleteness;
import com.emsist.designhub.service.AgentPackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AgentPackControllerTest {

    @Mock private AgentPackService packService;
    @InjectMocks private AgentPackController controller;

    @Test
    void shouldReturnCompletenessForStory() {
        when(packService.computeCompleteness("US-SCR-042"))
                .thenReturn(PackCompleteness.builder()
                        .complete(true).missingConcerns(List.of())
                        .missingFields(List.of()).readinessScore(100).build());

        var response = controller.getCompleteness("US-SCR-042");
        assertTrue(response.getBody().isComplete());
        assertEquals(100, response.getBody().getReadinessScore());
    }

    @Test
    void shouldReturnAgentPackForStory() {
        when(packService.buildPack("US-AI-090"))
                .thenReturn(Optional.of(new AgentPackExportResponse(
                        "PACK-US-AI-090",
                        1,
                        Instant.parse("2026-03-18T09:00:00Z"),
                        new GraphNodeReference("US-AI-090", "UserStory", "Agent builder story", "APPROVED"),
                        PackCompleteness.builder()
                                .complete(true)
                                .missingConcerns(List.of())
                                .missingFields(List.of())
                                .readinessScore(100)
                                .build(),
                        Map.of("repoPath", true),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of()
                )));

        var response = controller.getAgentPack("US-AI-090");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("PACK-US-AI-090", response.getBody().packId());
        assertEquals("US-AI-090", response.getBody().story().id());
    }

    @Test
    void shouldReturnNotFoundWhenAgentPackStoryMissing() {
        when(packService.buildPack("US-MISSING-001")).thenReturn(Optional.empty());

        var response = controller.getAgentPack("US-MISSING-001");

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }
}
