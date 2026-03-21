package com.emsist.designhub.dto;

public record ChannelSummaryResponse(
        String channelCode,
        String displayName,
        String channelType,
        long touchpointCount,
        long screenCount
) {
}
