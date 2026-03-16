package com.emsist.designhub.service;

import com.emsist.designhub.domain.ImportSnapshot;
import com.emsist.designhub.repository.ImportSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MarkdownImporterServiceTest {

    @Mock private FrontmatterParser parser;
    @Mock private SchemaValidatorService schemaValidator;
    @Mock private ReconciliationService reconciler;
    @Mock private RequirementSyncService syncService;
    @Mock(answer = org.mockito.Answers.RETURNS_DEEP_STUBS)
    private Neo4jClient neo4jClient;
    @Mock private ImportSnapshotRepository snapshotRepo;

    @InjectMocks
    private MarkdownImporterService importer;

    @Test
    void shouldRejectUnparsableDocument() {
        when(parser.parse(anyString())).thenReturn(Optional.empty());

        var result = importer.importDocument("bad content", "bad.md");
        assertEquals("FAILED", result.getResult());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void shouldCreateNodeAndSnapshotOnSuccess() {
        var fm = Frontmatter.builder()
                .id("US-SCR-042").type("UserStory").status("DEFINED").version(1).build();
        when(parser.parse(anyString())).thenReturn(Optional.of(fm));
        when(parser.extractBody(anyString())).thenReturn("# Desc\nBody");
        when(schemaValidator.validate(anyString(), anyMap()))
                .thenReturn(new SchemaValidatorService.ValidationResult(true, List.of()));
        when(syncService.computeContentHash(anyString(), any(String[].class)))
                .thenReturn("sha256:abc123");
        when(reconciler.reconcile(anyString(), anyString(), anyString()))
                .thenReturn(ReconciliationService.Decision.CREATE);

        var result = importer.importDocument(
                "---\nid: US-SCR-042\ntype: UserStory\nstatus: DEFINED\nversion: 1\n---\n# Desc\nBody",
                "docs/stories/US-SCR-042.md");

        assertNotNull(result.getSnapshotId());
        assertTrue(result.getSnapshotId().startsWith("IMP-"));
        assertEquals("SUCCESS", result.getResult());
        assertEquals(1, result.getCreated().size());
        verify(snapshotRepo).save(any(ImportSnapshot.class));
    }

    @Test
    void shouldSkipPersistenceWhenContentUnchanged() {
        var fm = Frontmatter.builder()
                .id("US-SCR-042").type("UserStory").status("DEFINED").version(1).build();
        when(parser.parse(anyString())).thenReturn(Optional.of(fm));
        when(parser.extractBody(anyString())).thenReturn("# Desc\nBody");
        when(schemaValidator.validate(anyString(), anyMap()))
                .thenReturn(new SchemaValidatorService.ValidationResult(true, List.of()));
        when(syncService.computeContentHash(anyString(), any(String[].class)))
                .thenReturn("sha256:abc123");
        when(reconciler.reconcile(anyString(), anyString(), anyString()))
                .thenReturn(ReconciliationService.Decision.SKIP);

        var result = importer.importDocument(
                "---\nid: US-SCR-042\ntype: UserStory\nstatus: DEFINED\nversion: 1\n---\n# Desc\nBody",
                "docs/stories/US-SCR-042.md");

        assertEquals("SKIPPED", result.getResult());
        assertTrue(result.getCreated().isEmpty());
        assertTrue(result.getUpdated().isEmpty());
        // Snapshot is still saved (audit trail)
        verify(snapshotRepo).save(any(ImportSnapshot.class));
    }

    @Test
    void shouldReportConflictWhenReconcilerDetectsConflict() {
        var fm = Frontmatter.builder()
                .id("US-SCR-042").type("UserStory").status("DEFINED").version(1).build();
        when(parser.parse(anyString())).thenReturn(Optional.of(fm));
        when(parser.extractBody(anyString())).thenReturn("# Desc\nBody");
        when(schemaValidator.validate(anyString(), anyMap()))
                .thenReturn(new SchemaValidatorService.ValidationResult(true, List.of()));
        when(syncService.computeContentHash(anyString(), any(String[].class)))
                .thenReturn("sha256:abc123");
        when(reconciler.reconcile(anyString(), anyString(), anyString()))
                .thenReturn(ReconciliationService.Decision.CONFLICT);

        var result = importer.importDocument(
                "---\nid: US-SCR-042\ntype: UserStory\nstatus: DEFINED\nversion: 1\n---\n# Desc\nBody",
                "docs/stories/US-SCR-042.md");

        assertEquals("CONFLICTED", result.getResult());
        assertTrue(result.getCreated().isEmpty());
        assertTrue(result.getUpdated().isEmpty());
        assertEquals(1, result.getConflicts().size());
        assertEquals("US-SCR-042", result.getConflicts().get(0).getNodeId());
        verify(snapshotRepo).save(any(ImportSnapshot.class));
    }
}
