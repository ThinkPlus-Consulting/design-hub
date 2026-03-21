package com.emsist.designhub.service;

import com.emsist.designhub.dto.ExternalSyncRequest;
import com.emsist.designhub.dto.ExternalSyncResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.RETURNS_DEEP_STUBS;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExternalArtifactSyncServiceTest {

    @Mock
    private Neo4jClient neo4jClient;

    @Spy
    private RequirementSyncService requirementSyncService = new RequirementSyncService();

    @InjectMocks
    private ExternalArtifactSyncService service;

    @Test
    void shouldReturnCreateUpdateAndSkipOutcomesDuringDryRun() {
        ExternalArtifactSyncService spy = org.mockito.Mockito.spy(service);

        ExternalSyncRequest.Artifact createArtifact = artifact("EXT-JIRA-201", "JIRA", "DH-201");
        ExternalSyncRequest.Artifact updateArtifact = artifact("EXT-JIRA-202", "JIRA", "DH-202");
        ExternalSyncRequest.Artifact skipArtifact = artifact("EXT-AZDO-203", "AZURE_DEVOPS", "AB#203");

        String skipHash = spy.computeContentHash(skipArtifact);

        doReturn(new ExternalArtifactSyncService.ArtifactLookup(false, null))
                .when(spy).lookupArtifact("EXT-JIRA-201");
        doReturn(new ExternalArtifactSyncService.ArtifactLookup(true, "sha256:older"))
                .when(spy).lookupArtifact("EXT-JIRA-202");
        doReturn(new ExternalArtifactSyncService.ArtifactLookup(true, skipHash))
                .when(spy).lookupArtifact("EXT-AZDO-203");

        ExternalSyncResult result = spy.sync(new ExternalSyncRequest(
                true,
                List.of(createArtifact, updateArtifact, skipArtifact)
        ));

        assertEquals("SUCCESS", result.result());
        assertEquals(3, result.processedCount());
        assertEquals(1, result.createdCount());
        assertEquals(1, result.updatedCount());
        assertEquals(1, result.skippedCount());
        assertEquals(0, result.failedCount());
        assertEquals(List.of("CREATE", "UPDATE", "SKIP"), result.items().stream().map(ExternalSyncResult.ItemResult::outcome).toList());
        verify(spy, never()).applyArtifactSync(any(), anyString());
    }

    @Test
    void shouldApplyCreateAndUpdateInLiveMode() {
        ExternalArtifactSyncService spy = org.mockito.Mockito.spy(service);
        doNothing().when(spy).applyArtifactSync(any(), anyString());

        ExternalSyncRequest.Artifact createArtifact = artifact("EXT-JIRA-301", "JIRA", "DH-301");
        ExternalSyncRequest.Artifact updateArtifact = artifact("EXT-AZDO-302", "AZURE_DEVOPS", "AB#302");

        doReturn(new ExternalArtifactSyncService.ArtifactLookup(false, null))
                .when(spy).lookupArtifact("EXT-JIRA-301");
        doReturn(new ExternalArtifactSyncService.ArtifactLookup(true, "sha256:stale"))
                .when(spy).lookupArtifact("EXT-AZDO-302");

        ExternalSyncResult result = spy.sync(new ExternalSyncRequest(
                false,
                List.of(createArtifact, updateArtifact)
        ));

        assertEquals("SUCCESS", result.result());
        assertEquals(1, result.createdCount());
        assertEquals(1, result.updatedCount());
        verify(spy, org.mockito.Mockito.times(2)).applyArtifactSync(any(), anyString());
    }

    @Test
    @SuppressWarnings("unchecked")
    void shouldLookupStoredContentHashForExternalArtifact() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.first()).thenReturn((Optional) Optional.of(Map.of(
                "nodeExists", true,
                "contentHash", "sha256:current"
        )));

        ExternalArtifactSyncService.ArtifactLookup lookup = service.lookupArtifact("EXT-JIRA-001");

        assertTrue(lookup.exists());
        assertEquals("sha256:current", lookup.contentHash());
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("OPTIONAL MATCH (ea:ExternalArtifact {externalId: $externalId})")
                        && cypher.contains("ea.contentHash AS contentHash")
        ));
    }

    @Test
    void shouldTreatUpperSpineRepresentationTypesAsSupported() {
        ExternalArtifactSyncService spy = org.mockito.Mockito.spy(service);
        doReturn(new ExternalArtifactSyncService.ArtifactLookup(false, null))
                .when(spy).lookupArtifact("EXT-JIRA-401");

        ExternalSyncResult result = spy.sync(new ExternalSyncRequest(
                true,
                List.of(new ExternalSyncRequest.Artifact(
                        "EXT-JIRA-401",
                        "JIRA",
                        "STORY",
                        "DH-401",
                        "External coverage",
                        "Design Hub / External",
                        "Ready",
                        "High",
                        "Lena Ortiz",
                        "Sam Boyd",
                        List.of("design-hub"),
                        Map.of("area", "External", "iteration", "Sprint 24"),
                        "https://jira.example.com/browse/DH-401",
                        "SYNCED",
                        "2026-03-18T10:00:00Z",
                        "DEFINED",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(new ExternalSyncRequest.Representation("epic", "EPIC-AI-001"))
                ))
        ));

        assertEquals("SUCCESS", result.result());
        assertTrue(result.items().get(0).warnings().isEmpty());
    }

    @Test
    void shouldNormalizeRepresentedPrimaryNodesAfterLiveSync() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.run()).thenReturn(null);

        service.applyArtifactSync(artifact("EXT-JIRA-501", "JIRA", "DH-501"), "sha256:normalized");

        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:UserStory)")
                        && cypher.contains("target.externalOwner = head(externalOwners)")
                        && cypher.contains("target.externalLabels = reduce")
                        && cypher.contains("target.externalRefs = externalRefs")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:Feature)")
                        && cypher.contains("target.targetIteration")
                        && cypher.contains("iterationPath=")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:Task)")
                        && cypher.contains("target.externalPriority = head(externalPriorities)")
                        && cypher.contains("target.externalLabels = reduce")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:Bug)")
                        && cypher.contains("target.externalWorkflowState = head(externalWorkflowStates)")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:Finding)")
                        && cypher.contains("target.externalPriority = head(externalPriorities)")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:ApiContract)")
                        && cypher.contains("target.externalOwner = head(externalOwners)")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:Epic)")
                        && cypher.contains("REPRESENTS_EPIC")
                        && cypher.contains("target.externalPriority = head(externalPriorities)")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:RequirementPortfolio)")
                        && cypher.contains("REPRESENTS_PORTFOLIO")
                        && cypher.contains("target.externalRefs = externalRefs")
        ));
        verify(neo4jClient).query(argThat((String cypher) ->
                cypher.contains("MATCH (target:BusinessObjective)")
                        && cypher.contains("REPRESENTS_OBJECTIVE")
                        && cypher.contains("target.externalWorkflowState = head(externalWorkflowStates)")
        ));
    }

    @Test
    void shouldTreatFindingAndApiContractAsSupportedRepresentationTypes() {
        ExternalArtifactSyncService spy = org.mockito.Mockito.spy(service);
        doReturn(new ExternalArtifactSyncService.ArtifactLookup(false, null))
                .when(spy).lookupArtifact("EXT-JIRA-601");

        ExternalSyncResult result = spy.sync(new ExternalSyncRequest(
                true,
                List.of(new ExternalSyncRequest.Artifact(
                        "EXT-JIRA-601",
                        "JIRA",
                        "TASK",
                        "DH-601",
                        "External enrichment",
                        "Design Hub / External",
                        "Ready",
                        "Medium",
                        "Lena Ortiz",
                        "Sam Boyd",
                        List.of("design-hub"),
                        Map.of("iteration", "Sprint 25"),
                        "https://jira.example.com/browse/DH-601",
                        "SYNCED",
                        "2026-03-18T10:00:00Z",
                        "DEFINED",
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(),
                        List.of(
                                new ExternalSyncRequest.Representation("finding", "FND-001"),
                                new ExternalSyncRequest.Representation("api-contract", "API-POST-API-V1-AUTH-LOGIN")
                        )
                ))
        ));

        assertEquals("SUCCESS", result.result());
        assertTrue(result.items().get(0).warnings().isEmpty());
    }

    private ExternalSyncRequest.Artifact artifact(String externalId, String system, String key) {
        return new ExternalSyncRequest.Artifact(
                externalId,
                system,
                "STORY",
                key,
                "External artifact " + externalId,
                "Design Hub / External",
                "In Progress",
                "High",
                "Aisha Coleman",
                "Marco Lane",
                List.of("design-hub", "external"),
                Map.of("area", "Design Hub", "iteration", "Sprint 24"),
                "https://example.test/" + key,
                "SYNCED",
                "2026-03-18T10:00:00Z",
                "DEFINED",
                List.of("EXT-JIRA-PARENT"),
                List.of("EXT-JIRA-DEP"),
                List.of("EXT-JIRA-REL"),
                List.of(),
                List.of(new ExternalSyncRequest.Representation("story", "US-AUTH-001"))
        );
    }
}
