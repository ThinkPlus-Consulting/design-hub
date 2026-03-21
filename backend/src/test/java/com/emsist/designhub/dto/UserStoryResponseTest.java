package com.emsist.designhub.dto;

import com.emsist.designhub.repository.UserStoryRepository;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class UserStoryResponseTest {

    @Test
    void shouldFallBackToZeroWhenProjectionDoesNotExposeComputedScreenCount() {
        UserStoryRepository.UserStorySummaryProjection projection = new UserStoryRepository.UserStorySummaryProjection() {
            @Override
            public String getStoryId() {
                return "US-1";
            }

            @Override
            public String getLabel() {
                return "Story";
            }

            @Override
            public String getModule() {
                return "Core";
            }

            @Override
            public String getDomain() {
                return "design";
            }

            @Override
            public String getStoryNumber() {
                return "US-1";
            }

            @Override
            public String getExternalWorkflowState() {
                return "In Progress";
            }

            @Override
            public String getExternalPriority() {
                return "High";
            }

            @Override
            public String getExternalOwner() {
                return "Aisha Coleman";
            }

            @Override
            public List<String> getExternalLabels() {
                return List.of("design-hub", "auth");
            }

            @Override
            public List<String> getExternalRefs() {
                return List.of("EXT-JIRA-001");
            }

            @Override
            public long getScreenCount() {
                throw new IllegalStateException("Computed alias unavailable");
            }
        };

        UserStoryResponse response = UserStoryResponse.from(projection);

        assertEquals(0L, response.screenCount());
        assertEquals("In Progress", response.externalWorkflowState());
        assertEquals("High", response.externalPriority());
        assertEquals("Aisha Coleman", response.externalOwner());
        assertEquals(List.of("design-hub", "auth"), response.externalLabels());
        assertEquals(List.of("EXT-JIRA-001"), response.externalRefs());
    }
}
