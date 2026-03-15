package com.emsist.designhub.service;

import com.emsist.designhub.dto.DriftItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DriftCheckServiceTest {

    @Mock private RequirementSyncService syncService;

    @InjectMocks
    private DriftCheckService driftChecker;

    @Test
    void shouldPassWhenNoDocAuthoredDrift() {
        var result = driftChecker.checkField("US-SCR-042", "description",
                "As a user...", "As a user...", DriftItem.DriftType.DOC_AUTHORED);
        assertNull(result); // No drift
    }

    @Test
    void shouldDetectDocAuthoredDrift() {
        var result = driftChecker.checkField("US-SCR-042", "description",
                "As a user...", "As an admin...", DriftItem.DriftType.DOC_AUTHORED);
        assertNotNull(result);
        assertEquals("US-SCR-042", result.getNodeId());
        assertEquals("description", result.getField());
        assertEquals(DriftItem.DriftType.DOC_AUTHORED, result.getDriftType());
    }

    @Test
    void shouldReportGraphComputedDriftAsInformational() {
        var result = driftChecker.checkField("US-SCR-042", "completenessScore",
                "80", "75", DriftItem.DriftType.GRAPH_COMPUTED);
        assertNotNull(result);
        assertEquals(DriftItem.DriftType.GRAPH_COMPUTED, result.getDriftType());
    }
}
