package com.emsist.designhub.service;

import com.emsist.designhub.dto.DriftCheckResult;
import com.emsist.designhub.dto.DriftItem;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
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

    @Test
    void shouldReturnPassedWhenNoDriftInBatch() {
        var checks = List.of(
                new DriftCheckService.FieldCheck("US-001", "description",
                        "same", "same", DriftItem.DriftType.DOC_AUTHORED),
                new DriftCheckService.FieldCheck("US-001", "completenessScore",
                        "80", "80", DriftItem.DriftType.GRAPH_COMPUTED));
        var result = driftChecker.checkAll(checks);
        assertTrue(result.isPassed());
        assertTrue(result.getDocAuthoredDrift().isEmpty());
        assertTrue(result.getGraphComputedDrift().isEmpty());
    }

    @Test
    void shouldFailWhenDocAuthoredDriftDetectedInBatch() {
        var checks = List.of(
                new DriftCheckService.FieldCheck("US-001", "description",
                        "old text", "new text", DriftItem.DriftType.DOC_AUTHORED),
                new DriftCheckService.FieldCheck("US-001", "completenessScore",
                        "80", "75", DriftItem.DriftType.GRAPH_COMPUTED));
        var result = driftChecker.checkAll(checks);
        assertFalse(result.isPassed());
        assertEquals(1, result.getDocAuthoredDrift().size());
        assertEquals(1, result.getGraphComputedDrift().size());
    }

    @Test
    void shouldPassWhenOnlyGraphComputedDriftInBatch() {
        var checks = List.of(
                new DriftCheckService.FieldCheck("US-001", "completenessScore",
                        "80", "75", DriftItem.DriftType.GRAPH_COMPUTED));
        var result = driftChecker.checkAll(checks);
        assertTrue(result.isPassed()); // Graph-computed drift is informational, not blocking
        assertEquals(1, result.getGraphComputedDrift().size());
    }
}
