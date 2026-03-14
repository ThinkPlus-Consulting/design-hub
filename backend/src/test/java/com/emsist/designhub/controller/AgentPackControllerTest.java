package com.emsist.designhub.controller;

import com.emsist.designhub.dto.PackCompleteness;
import com.emsist.designhub.service.AgentPackService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
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
}
