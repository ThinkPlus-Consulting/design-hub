package com.emsist.designhub.dto;

import com.emsist.designhub.repository.UserStoryRepository;

import java.util.List;

public record UserStoryResponse(
        String storyId,
        String label,
        String module,
        String domain,
        String storyNumber,
        long screenCount,
        String externalWorkflowState,
        String externalPriority,
        String externalOwner,
        List<String> externalLabels,
        List<String> externalRefs
) {

    public static UserStoryResponse from(UserStoryRepository.UserStorySummaryProjection projection) {
        return new UserStoryResponse(
                projection.getStoryId(),
                projection.getLabel(),
                projection.getModule(),
                projection.getDomain(),
                projection.getStoryNumber(),
                resolveScreenCount(projection),
                projection.getExternalWorkflowState(),
                projection.getExternalPriority(),
                projection.getExternalOwner(),
                projection.getExternalLabels() == null ? List.of() : List.copyOf(projection.getExternalLabels()),
                projection.getExternalRefs() == null ? List.of() : List.copyOf(projection.getExternalRefs())
        );
    }

    private static long resolveScreenCount(UserStoryRepository.UserStorySummaryProjection projection) {
        try {
            return projection.getScreenCount();
        } catch (RuntimeException ignored) {
            // Some projection proxies fall back to entity property access and can miss computed aliases.
            return 0L;
        }
    }
}
