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
}
