package com.emsist.designhub.controller;

import com.emsist.designhub.dto.ReadinessDiagnosticsResponse;
import com.emsist.designhub.service.ReadinessDiagnosticsService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReadinessControllerTest {

    @Mock
    private ReadinessDiagnosticsService readinessDiagnosticsService;

    @InjectMocks
    private ReadinessController readinessController;

    @Test
    void shouldReturnScreenDiagnostics() {
        Map<String, Boolean> readiness = new LinkedHashMap<>();
        readiness.put("requirementsReady", true);
        readiness.put("designReady", true);

        when(readinessDiagnosticsService.assessScreen("SCR-1")).thenReturn(Optional.of(
                ReadinessDiagnosticsResponse.builder()
                        .artifactType("Screen")
                        .artifactId("SCR-1")
                        .status("APPROVED")
                        .readiness(readiness)
                        .completenessScore(84.2)
                        .completenessLevel("GREEN")
                        .missingBlockingRules(List.of())
                        .missingOptionalRules(List.of("MCR-SCR-008"))
                        .missingArtifacts(List.of())
                        .advisoryRulesViolated(List.of())
                        .build()
        ));

        var response = readinessController.getScreenDiagnostics("SCR-1");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals("SCR-1", response.getBody().getArtifactId());
        assertEquals("GREEN", response.getBody().getCompletenessLevel());
    }

    @Test
    void shouldReturnNotFoundForMissingStory() {
        when(readinessDiagnosticsService.assessStory("US-MISSING")).thenReturn(Optional.empty());

        var response = readinessController.getStoryDiagnostics("US-MISSING");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
}
