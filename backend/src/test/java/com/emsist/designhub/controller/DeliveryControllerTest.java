package com.emsist.designhub.controller;

import com.emsist.designhub.dto.DeliveryStoryResponse;
import com.emsist.designhub.dto.ReadinessDiagnosticsResponse;
import com.emsist.designhub.service.DeliveryQueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatusCode;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeliveryControllerTest {

    @Mock
    private DeliveryQueryService deliveryQueryService;

    @InjectMocks
    private DeliveryController deliveryController;

    @Test
    void shouldReturnDeliveryStories() {
        when(deliveryQueryService.getStories("DEFINED", null, null, null, null, null, null, "status", "asc"))
                .thenReturn(List.of(
                        new DeliveryStoryResponse(
                                "US-AI-090",
                                "Builder canvas interactions ready for agent composition",
                                "ai",
                                "agent",
                                "US-AI-090",
                                "DEFINED",
                                false,
                                new DeliveryStoryResponse.FeatureSummary("FEAT-AI", "Agent Builder", "DEFINED"),
                                List.of(),
                                List.of(),
                                List.of(),
                                List.of(),
                                List.of(),
                                List.of(),
                                ReadinessDiagnosticsResponse.builder()
                                        .artifactType("UserStory")
                                        .artifactId("US-AI-090")
                                        .status("DEFINED")
                                        .readiness(Map.of("qaReady", false))
                                        .completenessScore(64.3)
                                        .completenessLevel("AMBER")
                                        .missingBlockingRules(List.of("MCR-UST-004"))
                                        .missingOptionalRules(List.of())
                                        .missingArtifacts(List.of("No AcceptanceCriterion linked via HAS_CRITERION"))
                                        .advisoryRulesViolated(List.of())
                                        .build()
                        )
                ));

        var response = deliveryController.getStories("DEFINED", null, null, null, null, null, null, "status", "asc");

        assertEquals(HttpStatusCode.valueOf(200), response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals("US-AI-090", response.getBody().get(0).storyId());
    }

    @Test
    void shouldReturnNotFoundForMissingStory() {
        when(deliveryQueryService.getStory("US-MISSING")).thenReturn(Optional.empty());

        var response = deliveryController.getStory("US-MISSING");

        assertEquals(HttpStatusCode.valueOf(404), response.getStatusCode());
    }
}
