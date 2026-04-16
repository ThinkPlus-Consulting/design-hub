export interface TenantFactsheetUsersLoadingStateBindings {
  readonly usersLoadingStateSectionSourceObjectId: string | null;
  readonly usersLoadingStateSectionGuid: string | null;
  readonly usersLoadingStateSectionFocused: boolean;
  readonly usersTableSkeletonSourceObjectId: string | null;
  readonly usersTableSkeletonGuid: string | null;
  readonly usersTableSkeletonFocused: boolean;
}

export interface TenantFactsheetUsersEmptyStateBindings {
  readonly usersEmptyStateSectionSourceObjectId: string | null;
  readonly usersEmptyStateSectionGuid: string | null;
  readonly usersEmptyStateSectionFocused: boolean;
  readonly usersEmptyStateTitleSourceObjectId: string | null;
  readonly usersEmptyStateTitleGuid: string | null;
  readonly usersEmptyStateTitleFocused: boolean;
  readonly usersEmptyStateMessageSourceObjectId: string | null;
  readonly usersEmptyStateMessageGuid: string | null;
  readonly usersEmptyStateMessageFocused: boolean;
}

export interface TenantFactsheetUsersDataStateBindings {
  readonly usersDataStateSectionSourceObjectId: string | null;
  readonly usersDataStateSectionGuid: string | null;
  readonly usersDataStateSectionFocused: boolean;
  readonly usersTableSourceObjectId: string | null;
  readonly usersTableGuid: string | null;
  readonly usersTableFocused: boolean;
  readonly userStatusBadgeSourceObjectId: string | null;
  readonly userStatusBadgeGuid: string | null;
  readonly userStatusBadgeFocused: boolean;
}

export interface TenantFactsheetUsersTabBindings
  extends TenantFactsheetUsersLoadingStateBindings,
    TenantFactsheetUsersEmptyStateBindings,
    TenantFactsheetUsersDataStateBindings {
  readonly usersTabSourceObjectId: string | null;
  readonly usersTabGuid: string | null;
  readonly usersTabFocused: boolean;
  readonly usersControlsRowSourceObjectId: string | null;
  readonly usersControlsRowGuid: string | null;
  readonly usersControlsRowFocused: boolean;
  readonly usersSearchInputSourceObjectId: string | null;
  readonly usersSearchInputGuid: string | null;
  readonly usersSearchInputFocused: boolean;
  readonly usersFilterToggleSourceObjectId: string | null;
  readonly usersFilterToggleGuid: string | null;
  readonly usersFilterToggleFocused: boolean;
  readonly usersTableRowSourceObjectId: string | null;
  readonly usersTableRowGuid: string | null;
  readonly usersTableRowFocused: boolean;
  readonly usersPaginationRowSourceObjectId: string | null;
  readonly usersPaginationRowGuid: string | null;
  readonly usersPaginationRowFocused: boolean;
  readonly usersPaginatorSourceObjectId: string | null;
  readonly usersPaginatorGuid: string | null;
  readonly usersPaginatorFocused: boolean;
}
