package com.emsist.designhub.dto;

public record PersonaSummaryResponse(
        String personaId,
        String name,
        String summary,
        String status,
        long journeyCount,
        long screenCount,
        long storyCount,
        long channelCount
) {
}
