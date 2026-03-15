package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EvidenceRecordTest {

    @Test
    void shouldBuildEvidenceRecordWithProvenance() {
        var record = EvidenceRecord.builder()
                .evidenceId("EVD-20260314-001")
                .evidenceType("TEST_RESULT")
                .artifactId("TC-SCR-042-01")
                .producedAt(Instant.now())
                .producedBy("qa-agent")
                .repoCommit("abc123")
                .result("PASS")
                .artifactPath("tests/results/TC-SCR-042-01.xml")
                .build();

        assertEquals("EVD-20260314-001", record.getEvidenceId());
        assertEquals("TEST_RESULT", record.getEvidenceType());
        assertEquals("PASS", record.getResult());
    }

    @Test
    void shouldAddExpectedAssertionsToTestCase() {
        var tc = TestCase.builder()
                .testCaseId("TC-SCR-042-01")
                .title("Screen renders")
                .expectedAssertions(List.of(
                        "Screen title visible",
                        "Interaction panel renders",
                        "No console errors"))
                .build();

        assertEquals(3, tc.getExpectedAssertions().size());
    }

    @Test
    void shouldAttachBaselineEvidenceToScreen() {
        var baseline = EvidenceRecord.builder()
                .evidenceId("EVD-BASELINE-001")
                .evidenceType("SCREENSHOT")
                .result("PASS")
                .build();

        var screen = Screen.builder()
                .surfaceId("SCR-LOGIN")
                .label("Login Screen")
                .baselines(List.of(baseline))
                .build();

        assertEquals(1, screen.getBaselines().size());
        assertEquals("EVD-BASELINE-001", screen.getBaselines().get(0).getEvidenceId());
    }

    @Test
    void shouldAttachBaselineEvidenceToApiContract() {
        var baseline = EvidenceRecord.builder()
                .evidenceId("EVD-BASELINE-002")
                .evidenceType("CONTRACT_SNAPSHOT")
                .result("PASS")
                .build();

        var api = ApiContract.builder()
                .contractId("API-AUTH-001")
                .path("/api/v1/auth/login")
                .method("POST")
                .baselines(List.of(baseline))
                .build();

        assertEquals(1, api.getBaselines().size());
        assertEquals("CONTRACT_SNAPSHOT", api.getBaselines().get(0).getEvidenceType());
    }
}
