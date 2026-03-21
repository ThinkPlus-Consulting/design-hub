package com.emsist.designhub.dto;

import com.emsist.designhub.domain.ContentElement;
import com.emsist.designhub.domain.Gap;
import com.emsist.designhub.domain.BusinessRole;
import com.emsist.designhub.domain.Screen;
import com.emsist.designhub.domain.UserStory;

import java.util.List;

public record ScreenResponse(
        String surfaceId,
        String label,
        String module,
        String routePath,
        String designStatus,
        String prototypeStatus,
        String deliveryStatus,
        String status,
        String wcag,
        boolean responsive,
        boolean roleAdaptive,
        boolean deepLinkable,
        boolean loadingStates,
        int messageRegistryCount,
        String notes,
        List<String> storyRefs,
        List<UserStoryResponse> stories,
        List<String> roleKeys,
        List<RoleResponse> roles,
        List<String> personaIds,
        List<GapResponse> gaps,
        List<ContentElementResponse> contentElements,
        List<TransitionResponse> transitionsTo
) {

    public static ScreenResponse from(Screen screen) {
        return new ScreenResponse(
                screen.getSurfaceId(),
                screen.getLabel(),
                screen.getModule(),
                screen.getRoutePath(),
                screen.getDesignStatus(),
                screen.getPrototypeStatus(),
                screen.getDeliveryStatus(),
                screen.getStatus() != null ? screen.getStatus().name() : null,
                screen.getWcag(),
                screen.isResponsive(),
                screen.isRoleAdaptive(),
                screen.isDeepLinkable(),
                screen.isLoadingStates(),
                screen.getMessageRegistryCount(),
                screen.getNotes(),
                storyIds(screen),
                resolveStories(screen),
                roleKeys(screen),
                resolveRoles(screen),
                safeList(screen.getPersonaIds()),
                screen.getGaps() == null
                        ? List.of()
                        : screen.getGaps().stream().map(GapResponse::from).toList(),
                screen.getContentElements() == null
                        ? List.of()
                        : screen.getContentElements().stream().map(ContentElementResponse::from).toList(),
                screen.getTransitionsTo() == null
                        ? List.of()
                        : screen.getTransitionsTo().stream().map(TransitionResponse::from).toList()
        );
    }

    private static List<String> safeList(List<String> values) {
        return values == null ? List.of() : values;
    }

    private static List<String> storyIds(Screen screen) {
        List<UserStory> deliveredStories = screen.getDeliveredByStories();
        if (deliveredStories != null && !deliveredStories.isEmpty()) {
            return deliveredStories.stream()
                    .map(UserStory::getStoryId)
                    .toList();
        }
        return safeList(screen.getStoryRefs());
    }

    private static List<String> roleKeys(Screen screen) {
        List<BusinessRole> accessibleByRoles = screen.getAccessibleByRoles();
        if (accessibleByRoles != null && !accessibleByRoles.isEmpty()) {
            return accessibleByRoles.stream()
                    .map(BusinessRole::getRoleKey)
                    .toList();
        }
        return safeList(screen.getRoleKeys());
    }

    private static List<RoleResponse> resolveRoles(Screen screen) {
        List<BusinessRole> accessibleByRoles = screen.getAccessibleByRoles();
        if (accessibleByRoles != null && !accessibleByRoles.isEmpty()) {
            return accessibleByRoles.stream()
                    .map(ScreenResponse::toRoleResponse)
                    .toList();
        }
        return List.of();
    }

    private static RoleResponse toRoleResponse(BusinessRole role) {
        return new RoleResponse(
                role.getRoleKey(),
                role.getDisplayName(),
                role.getRoleGroup(),
                role.getSortOrder(),
                0,
                0,
                0,
                0
        );
    }

    private static List<UserStoryResponse> resolveStories(Screen screen) {
        List<UserStory> deliveredStories = screen.getDeliveredByStories();
        if (deliveredStories != null && !deliveredStories.isEmpty()) {
            return deliveredStories.stream()
                    .map(ScreenResponse::toUserStoryResponse)
                    .toList();
        }
        return List.of();
    }

    private static UserStoryResponse toUserStoryResponse(UserStory story) {
        long screenCount = story.getDeliversScreens() == null ? 0 : story.getDeliversScreens().size();
        return new UserStoryResponse(
                story.getStoryId(),
                story.getLabel(),
                story.getModule(),
                story.getDomain(),
                story.getStoryNumber(),
                screenCount,
                story.getExternalWorkflowState(),
                story.getExternalPriority(),
                story.getExternalOwner(),
                story.getExternalLabels() == null ? List.of() : List.copyOf(story.getExternalLabels()),
                story.getExternalRefs() == null ? List.of() : List.copyOf(story.getExternalRefs())
        );
    }

    public record GapResponse(
            String gapId,
            String gapType,
            String severity,
            String description
    ) {
        private static GapResponse from(Gap gap) {
            return new GapResponse(gap.getGapId(), gap.getGapType(), gap.getSeverity(), gap.getDescription());
        }
    }

    public record ContentElementResponse(
            String element,
            String type,
            String description,
            int orderIndex
    ) {
        private static ContentElementResponse from(ContentElement contentElement) {
            return new ContentElementResponse(
                    contentElement.getElement(),
                    contentElement.getType(),
                    contentElement.getDescription(),
                    contentElement.getOrderIndex()
            );
        }
    }

    public record TransitionResponse(
            String surfaceId,
            String label
    ) {
        private static TransitionResponse from(Screen screen) {
            return new TransitionResponse(screen.getSurfaceId(), screen.getLabel());
        }
    }
}
