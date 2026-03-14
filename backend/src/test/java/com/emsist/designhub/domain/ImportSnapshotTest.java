package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.Instant;
import static org.junit.jupiter.api.Assertions.*;

class ImportSnapshotTest {

    @Test
    void shouldBuildImportSnapshotWithRequiredFields() {
        Instant now = Instant.now();
        ImportSnapshot snapshot = ImportSnapshot.builder()
                .snapshotId("IMP-20260314-001")
                .sourceType("GIT_DOC")
                .importedAt(now)
                .importedBy("content-agent")
                .result("SUCCESS")
                .build();

        assertEquals("IMP-20260314-001", snapshot.getSnapshotId());
        assertEquals("GIT_DOC", snapshot.getSourceType());
        assertEquals(now, snapshot.getImportedAt());
        assertEquals("content-agent", snapshot.getImportedBy());
        assertEquals("SUCCESS", snapshot.getResult());
    }

    @Test
    void shouldSupportOptionalFields() {
        ImportSnapshot snapshot = ImportSnapshot.builder()
                .snapshotId("IMP-20260314-002")
                .sourceType("GIT_DOC")
                .sourcePath("docs/stories/US-AUTH-002.md")
                .importedAt(Instant.now())
                .importedBy("content-agent")
                .result("SUCCESS")
                .itemCount(3)
                .contentHash("sha256:e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855")
                .build();

        assertEquals("docs/stories/US-AUTH-002.md", snapshot.getSourcePath());
        assertTrue(snapshot.getContentHash().startsWith("sha256:"));
        assertEquals(3, snapshot.getItemCount());
    }

    @Test
    void shouldSupportErrorSummaryForFailedImports() {
        ImportSnapshot snapshot = ImportSnapshot.builder()
                .snapshotId("IMP-20260314-003")
                .sourceType("JIRA_SYNC")
                .sourcePath("PROJ-123")
                .importedAt(Instant.now())
                .importedBy("jira-sync")
                .result("PARTIAL")
                .itemCount(5)
                .errorSummary("2 of 5 stories failed schema validation")
                .build();

        assertEquals("PARTIAL", snapshot.getResult());
        assertNotNull(snapshot.getErrorSummary());
    }

    @Test
    void shouldSupportConflictedResult() {
        ImportSnapshot snapshot = ImportSnapshot.builder()
                .snapshotId("IMP-20260314-004")
                .sourceType("JIRA_SYNC")
                .sourcePath("PROJ-456")
                .importedAt(Instant.now())
                .importedBy("jira-sync")
                .result("CONFLICTED")
                .build();

        assertEquals("CONFLICTED", snapshot.getResult());
    }
}
