package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SourceReferenceTest {

    @Test
    void shouldBuildSourceReferenceWithStubFields() {
        SourceReference sourceReference = SourceReference.builder()
                .sourceId("SRC-001")
                .artifactPath("documentation/vision-benchmark.md")
                .section("Query 8")
                .lineRef("401-410")
                .url("https://example.com/spec")
                .status(Status.DEFINED)
                .build();

        assertEquals("SRC-001", sourceReference.getSourceId());
        assertEquals("documentation/vision-benchmark.md", sourceReference.getArtifactPath());
        assertEquals("Query 8", sourceReference.getSection());
        assertEquals("401-410", sourceReference.getLineRef());
        assertEquals("https://example.com/spec", sourceReference.getUrl());
        assertEquals(Status.DEFINED, sourceReference.getStatus());
    }

    @Test
    void shouldFollowSourceIdPattern() {
        SourceReference sourceReference = SourceReference.builder()
                .sourceId("SRC-014")
                .artifactPath("docs/superpowers/plans/2026-03-16-d5b1-strategic-and-architecture-stubs.md")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(sourceReference.getSourceId().startsWith("SRC-"),
                "sourceId must follow pattern SRC-{seq}");
    }
}
