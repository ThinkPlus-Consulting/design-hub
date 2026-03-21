package com.emsist.designhub.service;

import com.emsist.designhub.dto.AzureDevOpsSyncRequest;
import com.emsist.designhub.dto.ExternalSyncRequest;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AzureDevOpsSyncMapperServiceTest {

    private final AzureDevOpsSyncMapperService service = new AzureDevOpsSyncMapperService();

    @Test
    void shouldMapAzureDevOpsWorkItemToGenericExternalSyncRequest() {
        ExternalSyncRequest request = service.toExternalSyncRequest(new AzureDevOpsSyncRequest(
                true,
                List.of(new AzureDevOpsSyncRequest.WorkItem(
                        245L,
                        "Bug",
                        "Retry banner remains visible after login",
                        "Design Hub\\Identity",
                        "Sprint 24",
                        "Active",
                        "2",
                        "Jordan Rivera",
                        "Aisha Coleman",
                        List.of("design-hub", "bug"),
                        Map.of("storyPoints", "3"),
                        "https://dev.azure.com/example/designhub/_workitems/edit/245",
                        "2026-03-18T10:00:00Z",
                        List.of(240L),
                        List.of(201L),
                        List.of(199L),
                        List.of(198L),
                        List.of(new AzureDevOpsSyncRequest.Reference("bug", "BUG-001"))
                ))
        ));

        assertEquals(true, request.dryRun());
        assertEquals(1, request.artifacts().size());

        ExternalSyncRequest.Artifact artifact = request.artifacts().getFirst();
        assertEquals("EXT-AZDO-245", artifact.externalId());
        assertEquals("AZURE_DEVOPS", artifact.system());
        assertEquals("Bug", artifact.externalType());
        assertEquals("AB#245", artifact.key());
        assertEquals("Design Hub\\Identity", artifact.projectScope());
        assertEquals("Active", artifact.workflowState());
        assertEquals("Sprint 24", artifact.customFields().get("iterationPath"));
        assertEquals("245", artifact.customFields().get("workItemId"));
        assertEquals(List.of("EXT-AZDO-240"), artifact.parentExternalIds());
        assertEquals(List.of("EXT-AZDO-201"), artifact.dependencyExternalIds());
        assertEquals(List.of("EXT-AZDO-199"), artifact.relatedExternalIds());
        assertEquals(List.of("EXT-AZDO-198"), artifact.duplicateExternalIds());
        assertEquals("bug", artifact.represents().getFirst().nodeType());
    }
}
