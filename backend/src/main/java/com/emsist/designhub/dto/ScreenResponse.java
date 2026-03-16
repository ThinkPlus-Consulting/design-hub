package com.emsist.designhub.dto;

import com.emsist.designhub.domain.ContentElement;
import com.emsist.designhub.domain.Gap;
import com.emsist.designhub.domain.Screen;

import java.util.List;
import java.util.Map;

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
        return from(screen, Map.of(), Map.of());
    }

    public static ScreenResponse from(
            Screen screen,
            Map<String, RoleResponse> roleLookup,
            Map<String, UserStoryResponse> storyLookup
    ) {
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
                safeList(screen.getStoryRefs()),
                resolveStories(screen.getStoryRefs(), storyLookup),
                safeList(screen.getRoleKeys()),
                resolveRoles(screen.getRoleKeys(), roleLookup),
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

    private static List<RoleResponse> resolveRoles(
            List<String> roleKeys,
            Map<String, RoleResponse> roleLookup
    ) {
        return safeList(roleKeys).stream()
                .map(roleLookup::get)
                .filter(role -> role != null)
                .toList();
    }

    private static List<UserStoryResponse> resolveStories(
            List<String> storyRefs,
            Map<String, UserStoryResponse> storyLookup
    ) {
        return safeList(storyRefs).stream()
                .map(storyLookup::get)
                .filter(story -> story != null)
                .toList();
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
