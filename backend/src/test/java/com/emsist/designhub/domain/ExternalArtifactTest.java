package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ExternalArtifactTest {

    @Test
    void shouldBuildExternalArtifactWithTraceabilityFields() {
        ExternalArtifact externalArtifact = ExternalArtifact.builder()
                .externalId("EXT-JIRA-001")
                .system("JIRA")
                .externalType("STORY")
                .key("DH-101")
                .url("https://jira.example.com/browse/DH-101")
                .syncStatus("SYNCED")
                .lastSyncedAt(Instant.parse("2026-03-16T08:00:00Z"))
                .status(Status.DEFINED)
                .build();

        assertEquals("EXT-JIRA-001", externalArtifact.getExternalId());
        assertEquals("JIRA", externalArtifact.getSystem());
        assertEquals("DH-101", externalArtifact.getKey());
        assertEquals("SYNCED", externalArtifact.getSyncStatus());
        assertEquals(Instant.parse("2026-03-16T08:00:00Z"), externalArtifact.getLastSyncedAt());
    }

    @Test
    void shouldFollowExternalArtifactIdPattern() {
        ExternalArtifact externalArtifact = ExternalArtifact.builder()
                .externalId("EXT-AZURE_DEVOPS-001")
                .system("AZURE_DEVOPS")
                .externalType("BUG")
                .key("AB#245")
                .status(Status.DEFINED)
                .build();

        assertTrue(externalArtifact.getExternalId().startsWith("EXT-"),
                "externalId must follow pattern EXT-{system}-{seq}");
    }
}
