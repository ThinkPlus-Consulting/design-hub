package com.emsist.designhub.service;

import com.emsist.designhub.dto.ImportRequest;
import com.emsist.designhub.repository.ImportSnapshotRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class MarkdownImporterServiceTest {

    @Mock private FrontmatterParser parser;
    @Mock private SchemaValidatorService schemaValidator;
    @Mock private ReconciliationService reconciler;
    @Mock private RequirementSyncService syncService;
    @Mock private Neo4jClient neo4jClient;
    @Mock private ImportSnapshotRepository snapshotRepo;

    @InjectMocks
    private MarkdownImporterService importer;

    @Test
    void shouldRejectUnparsableDocument() {
        org.mockito.Mockito.when(parser.parse(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.empty());

        var result = importer.importDocument("bad content", "bad.md");
        assertEquals("FAILED", result.getResult());
        assertFalse(result.getErrors().isEmpty());
    }

    @Test
    void shouldCreateSnapshotOnSuccess() {
        var fm = Frontmatter.builder()
                .id("US-SCR-042").type("UserStory").status("DEFINED").version(1).build();
        org.mockito.Mockito.when(parser.parse(org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(java.util.Optional.of(fm));
        org.mockito.Mockito.when(schemaValidator.validate(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyMap()))
                .thenReturn(new SchemaValidatorService.ValidationResult(true, java.util.List.of()));
        org.mockito.Mockito.when(syncService.computeContentHash(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.any(String[].class)))
                .thenReturn("sha256:abc123");
        org.mockito.Mockito.when(reconciler.decide(
                org.mockito.ArgumentMatchers.anyString(), org.mockito.ArgumentMatchers.anyString(),
                org.mockito.ArgumentMatchers.isNull(), org.mockito.ArgumentMatchers.anyString()))
                .thenReturn(ReconciliationService.Decision.CREATE);

        var result = importer.importDocument(
                "---\nid: US-SCR-042\ntype: UserStory\nstatus: DEFINED\nversion: 1\n---\n# Desc\nBody",
                "docs/stories/US-SCR-042.md");

        assertNotNull(result.getSnapshotId());
        assertTrue(result.getSnapshotId().startsWith("IMP-"));
    }
}
