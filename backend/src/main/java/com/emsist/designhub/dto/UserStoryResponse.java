package com.emsist.designhub.dto;

import com.emsist.designhub.repository.UserStoryRepository;

public record UserStoryResponse(
        String storyId,
        String label,
        String module,
        String domain,
        String storyNumber,
        long screenCount
) {

    public static UserStoryResponse from(UserStoryRepository.UserStorySummaryProjection projection) {
        return new UserStoryResponse(
                projection.getStoryId(),
                projection.getLabel(),
                projection.getModule(),
                projection.getDomain(),
                projection.getStoryNumber(),
                projection.getScreenCount()
        );
    }
}
