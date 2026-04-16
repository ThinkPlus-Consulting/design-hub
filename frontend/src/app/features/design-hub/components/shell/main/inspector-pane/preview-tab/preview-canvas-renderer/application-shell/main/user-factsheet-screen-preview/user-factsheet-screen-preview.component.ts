import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { CardModule } from 'primeng/card';
import { ImageModule } from 'primeng/image';
import { MessageModule } from 'primeng/message';
import { SkeletonModule } from 'primeng/skeleton';
import { TableModule } from 'primeng/table';
import { TabsModule } from 'primeng/tabs';
import { TagModule } from 'primeng/tag';
import {
  TenantUser,
  UserFactsheetScreenContentConfig,
  UserFactsheetSessionRecord,
  UserFactsheetStatus,
  UserFactsheetSummary,
  UserFactsheetTab,
} from '../../../../../../../../../models/preview-content.types';
import { PreviewIdentityBound } from '../../../../../../../../../utils/preview-identity-bound';
import { parseNodeConfiguration } from '../../../../../../../../../utils/node-configuration';
import { childByName, componentByName } from '../../../../../../../../../utils/preview-structure';

const EMPTY_USER: UserFactsheetSummary = {
  id: '',
  displayName: '',
  email: '',
  username: '',
  status: 'Active',
  avatarUrl: null,
  primaryAuthProvider: '',
  linkedProviders: [],
  lastLogin: '',
  activeSessions: 0,
  licensedType: '',
};

function userStatusFromTenantStatus(status: TenantUser['status']): UserFactsheetStatus {
  switch (status) {
    case 'Active':
      return 'Active';
    case 'Invited':
      return 'Invited';
    case 'Disabled':
      return 'Disabled';
  }
}

function buildUserAvatarDataUrl(initials: string): string {
  const safeInitials = initials.replace(/[^A-Z0-9]/gi, '').slice(0, 2).toUpperCase() || 'US';
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 160 160">
      <rect width="160" height="160" rx="20" fill="#f6f2eb" />
      <rect x="1" y="1" width="158" height="158" rx="19" fill="none" stroke="#d7cec0" stroke-width="2" />
      <text x="80" y="98" text-anchor="middle" font-family="Segoe UI, Arial, sans-serif" font-size="50" font-weight="700" fill="#1854d1">${safeInitials}</text>
    </svg>
  `.trim();

  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

@Component({
  selector: 'app-user-factsheet-screen-preview',
  standalone: true,
  imports: [
    ButtonModule,
    CardModule,
    ImageModule,
    MessageModule,
    SkeletonModule,
    TableModule,
    TabsModule,
    TagModule,
  ],
  templateUrl: './user-factsheet-screen-preview.component.html',
  styleUrl: './user-factsheet-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class UserFactsheetScreenPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly screenObjectId = input<string | null>(null);

  private readonly screenNode = computed(() => this.state.nodeById(this.screenObjectId()));
  readonly content = computed(() =>
    parseNodeConfiguration<UserFactsheetScreenContentConfig>(this.screenNode()),
  );
  private readonly previewUser = computed(() => this.state.previewUserFactsheetUser());
  private readonly slots = computed(() => {
    const screen = this.screenNode();
    const userHeader = childByName(this.state, screen?.id, 'User Header', 'Container');
    const detailsContainer = childByName(this.state, screen?.id, 'Details Container', 'Container');
    const userAvatarSection = childByName(this.state, userHeader?.id, 'User Avatar Section', 'Section');
    const userIdentitySection = childByName(this.state, userHeader?.id, 'User Identity Section', 'Section');
    const userActionsSection = childByName(this.state, userHeader?.id, 'User Actions Section', 'Section');
    const userNameRowSection = childByName(this.state, userIdentitySection?.id, 'User Name Row Section', 'Section');
    const userProfileRowSection = childByName(this.state, userIdentitySection?.id, 'User Profile Row Section', 'Section');
    const userAuthProvenanceRowSection = childByName(
      this.state,
      userIdentitySection?.id,
      'User Auth Provenance Row Section',
      'Section',
    );
    const userSummaryRowSection = childByName(this.state, userIdentitySection?.id, 'User Summary Row Section', 'Section');
    const factsheetTabsRowSection = childByName(this.state, detailsContainer?.id, 'Factsheet Tabs Row Section', 'Section');
    const factsheetTabContentsRowSection = childByName(
      this.state,
      detailsContainer?.id,
      'Factsheet Tab Contents Row Section',
      'Section',
    );
    const overviewTabSection = childByName(this.state, factsheetTabContentsRowSection?.id, 'Overview Tab Section', 'Section');
    const organizationTabSection = childByName(
      this.state,
      factsheetTabContentsRowSection?.id,
      'Organization Tab Section',
      'Section',
    );
    const authProvidersTabSection = childByName(
      this.state,
      factsheetTabContentsRowSection?.id,
      'Auth Providers Tab Section',
      'Section',
    );
    const rolesAndAccessTabSection = childByName(
      this.state,
      factsheetTabContentsRowSection?.id,
      'Roles And Access Tab Section',
      'Section',
    );
    const sessionsPanelSection = childByName(this.state, overviewTabSection?.id, 'Sessions Panel Section', 'Section');
    const sessionsLoadingStateSection = childByName(
      this.state,
      sessionsPanelSection?.id,
      'Sessions Loading State Section',
      'Section',
    );
    const sessionsEmptyStateSection = childByName(
      this.state,
      sessionsPanelSection?.id,
      'Sessions Empty State Section',
      'Section',
    );
    const sessionsErrorStateSection = childByName(
      this.state,
      sessionsPanelSection?.id,
      'Sessions Error State Section',
      'Section',
    );
    const sessionsDataStateSection = childByName(
      this.state,
      sessionsPanelSection?.id,
      'Sessions Data State Section',
      'Section',
    );

    return {
      screen,
      userHeader,
      userAvatarSection,
      userAvatar: componentByName(this.state, userAvatarSection?.id, 'User Avatar'),
      userIdentitySection,
      userActionsSection,
      userNameRowSection,
      userDisplayName: componentByName(this.state, userNameRowSection?.id, 'User Display Name'),
      userProfileRowSection,
      userEmail: componentByName(this.state, userProfileRowSection?.id, 'User Email'),
      userUsername: componentByName(this.state, userProfileRowSection?.id, 'User Username'),
      userStatusBadge: componentByName(this.state, userProfileRowSection?.id, 'User Status Badge'),
      userAuthProvenanceRowSection,
      primaryAuthProviderTag: componentByName(this.state, userAuthProvenanceRowSection?.id, 'Primary Auth Provider Tag'),
      linkedProvidersSummary: componentByName(this.state, userAuthProvenanceRowSection?.id, 'Linked Providers Summary'),
      userSummaryRowSection,
      lastLoginFact: componentByName(this.state, userSummaryRowSection?.id, 'Last Login Fact'),
      activeSessionsFact: componentByName(this.state, userSummaryRowSection?.id, 'Active Sessions Fact'),
      licensedTypeBadge: componentByName(this.state, userSummaryRowSection?.id, 'Licensed Type Badge'),
      refreshUser: componentByName(this.state, userActionsSection?.id, 'Refresh User'),
      revokeAllSessions: componentByName(this.state, userActionsSection?.id, 'Revoke All Sessions'),
      detailsContainer,
      factsheetTabsRowSection,
      factsheetTabContentsRowSection,
      overviewTab: componentByName(this.state, factsheetTabsRowSection?.id, 'Overview Tab'),
      organizationTab: componentByName(this.state, factsheetTabsRowSection?.id, 'Organization Tab'),
      authProvidersTab: componentByName(this.state, factsheetTabsRowSection?.id, 'Auth Providers Tab'),
      rolesAndAccessTab: componentByName(this.state, factsheetTabsRowSection?.id, 'Roles And Access Tab'),
      overviewTabSection,
      profileSummarySection: childByName(this.state, overviewTabSection?.id, 'Profile Summary Section', 'Section'),
      profileSummaryCard: componentByName(this.state, childByName(this.state, overviewTabSection?.id, 'Profile Summary Section', 'Section')?.id, 'Profile Summary Card'),
      sessionsPanelSection,
      sessionsLoadingStateSection,
      sessionsSkeleton: componentByName(this.state, sessionsLoadingStateSection?.id, 'Sessions Skeleton'),
      sessionsEmptyStateSection,
      sessionsEmptyTitle: componentByName(this.state, sessionsEmptyStateSection?.id, 'Sessions Empty Title'),
      sessionsEmptyMessage: componentByName(this.state, sessionsEmptyStateSection?.id, 'Sessions Empty Message'),
      sessionsErrorStateSection,
      sessionsErrorBanner: componentByName(this.state, sessionsErrorStateSection?.id, 'Sessions Error Banner'),
      sessionsRetryAction: componentByName(this.state, sessionsErrorStateSection?.id, 'Sessions Retry Action'),
      sessionsDataStateSection,
      sessionsTable: componentByName(this.state, sessionsDataStateSection?.id, 'Sessions Table'),
      currentSessionBadge: componentByName(this.state, sessionsDataStateSection?.id, 'Current Session Badge'),
      rememberMeBadge: componentByName(this.state, sessionsDataStateSection?.id, 'Remember Me Badge'),
      mfaBadge: componentByName(this.state, sessionsDataStateSection?.id, 'Mfa Badge'),
      organizationTabSection,
      organizationSummarySection: childByName(this.state, organizationTabSection?.id, 'Organization Summary Section', 'Section'),
      organizationContextCard: componentByName(this.state, childByName(this.state, organizationTabSection?.id, 'Organization Summary Section', 'Section')?.id, 'Organization Context Card'),
      reportingLineSection: childByName(this.state, organizationTabSection?.id, 'Reporting Line Section', 'Section'),
      reportingLineFact: componentByName(this.state, childByName(this.state, organizationTabSection?.id, 'Reporting Line Section', 'Section')?.id, 'Reporting Line Fact'),
      teamRelationshipsSection: childByName(this.state, organizationTabSection?.id, 'Team Relationships Section', 'Section'),
      teamRelationshipsFact: componentByName(this.state, childByName(this.state, organizationTabSection?.id, 'Team Relationships Section', 'Section')?.id, 'Team Relationships Fact'),
      authProvidersTabSection,
      primaryProviderSection: childByName(this.state, authProvidersTabSection?.id, 'Primary Provider Section', 'Section'),
      primaryProviderDetail: componentByName(this.state, childByName(this.state, authProvidersTabSection?.id, 'Primary Provider Section', 'Section')?.id, 'Primary Provider Detail'),
      linkedProvidersSection: childByName(this.state, authProvidersTabSection?.id, 'Linked Providers Section', 'Section'),
      linkedProvidersList: componentByName(this.state, childByName(this.state, authProvidersTabSection?.id, 'Linked Providers Section', 'Section')?.id, 'Linked Providers List'),
      providerIdentityReferencesSection: childByName(
        this.state,
        authProvidersTabSection?.id,
        'Provider Identity References Section',
        'Section',
      ),
      providerIdentityReferencesList: componentByName(
        this.state,
        childByName(this.state, authProvidersTabSection?.id, 'Provider Identity References Section', 'Section')?.id,
        'Provider Identity References List',
      ),
      rolesAndAccessTabSection,
      licensedTypeSection: childByName(this.state, rolesAndAccessTabSection?.id, 'Licensed Type Section', 'Section'),
      licensedTypeDetailBadge: componentByName(this.state, childByName(this.state, rolesAndAccessTabSection?.id, 'Licensed Type Section', 'Section')?.id, 'Licensed Type Detail Badge'),
      assignedRolesSection: childByName(this.state, rolesAndAccessTabSection?.id, 'Assigned Roles Section', 'Section'),
      assignedRolesList: componentByName(this.state, childByName(this.state, rolesAndAccessTabSection?.id, 'Assigned Roles Section', 'Section')?.id, 'Assigned Roles List'),
      assignedGroupsSection: childByName(this.state, rolesAndAccessTabSection?.id, 'Assigned Groups Section', 'Section'),
      assignedGroupsList: componentByName(this.state, childByName(this.state, rolesAndAccessTabSection?.id, 'Assigned Groups Section', 'Section')?.id, 'Assigned Groups List'),
      allowListSection: childByName(this.state, rolesAndAccessTabSection?.id, 'Allow List Section', 'Section'),
      allowListSummary: componentByName(this.state, childByName(this.state, rolesAndAccessTabSection?.id, 'Allow List Section', 'Section')?.id, 'Allow List Summary'),
      blockListSection: childByName(this.state, rolesAndAccessTabSection?.id, 'Block List Section', 'Section'),
      blockListSummary: componentByName(this.state, childByName(this.state, rolesAndAccessTabSection?.id, 'Block List Section', 'Section')?.id, 'Block List Summary'),
      effectiveAccessSummarySection: childByName(
        this.state,
        rolesAndAccessTabSection?.id,
        'Effective Access Summary Section',
        'Section',
      ),
      effectiveAccessSummaryCard: componentByName(
        this.state,
        childByName(this.state, rolesAndAccessTabSection?.id, 'Effective Access Summary Section', 'Section')?.id,
        'Effective Access Summary Card',
      ),
    };
  });

  readonly activeTab = signal<UserFactsheetTab>('overview');
  readonly user = computed<UserFactsheetSummary>(() => {
    const baseUser = this.content()?.user ?? EMPTY_USER;
    const previewUser = this.previewUser();
    if (!previewUser) {
      return baseUser;
    }

    const inferredUsername = previewUser.email.split('@')[0]?.trim() || baseUser.username;
    return {
      ...baseUser,
      id: previewUser.id || baseUser.id,
      displayName: previewUser.name || baseUser.displayName,
      email: previewUser.email || baseUser.email,
      username: inferredUsername,
      status: userStatusFromTenantStatus(previewUser.status),
      licensedType: previewUser.role || baseUser.licensedType,
      lastLogin: previewUser.lastActive || baseUser.lastLogin,
    };
  });
  readonly tabs = computed(() => this.content()?.tabs ?? []);
  readonly avatarInitials = computed(() => {
    const parts = this.user().displayName.split(/\s+/).filter(Boolean);
    if (parts.length >= 2) {
      return `${parts[0][0]}${parts[1][0]}`.toUpperCase();
    }
    return this.user().displayName.slice(0, 2).toUpperCase();
  });
  readonly avatarSrc = computed(() => this.user().avatarUrl ?? buildUserAvatarDataUrl(this.avatarInitials()));
  readonly linkedProvidersLabel = computed(() => this.user().linkedProviders.join(' • '));
  readonly sessions = computed(() => this.content()?.overview.sessions.items ?? []);
  readonly sessionTableRows = computed(() => [...this.sessions()]);
  readonly sessionState = computed(() => this.content()?.overview.sessions.state ?? 'loading');
  readonly sessionRows = computed(() => this.content()?.overview.sessions.skeletonRowCount ?? 4);
  readonly sessionRowIndices = computed(() =>
    Array.from({ length: Math.max(1, this.sessionRows()) }, (_, index) => index),
  );
  readonly showSessionsLoading = computed(() => this.sessionState() === 'loading');
  readonly showSessionsEmpty = computed(() => this.sessionState() === 'empty');
  readonly showSessionsError = computed(() => this.sessionState() === 'error');
  readonly showSessionsData = computed(() => this.sessionState() === 'data');
  readonly linkedProviders = computed(() => this.content()?.authProviders?.linkedProviders ?? []);
  readonly authProviderReferences = computed(() => this.content()?.authProviders?.references ?? []);
  readonly assignedRoles = computed(() => {
    const previewRole = this.previewUser()?.role?.trim();
    const baseRoles = this.content()?.rolesAccess?.assignedRoles ?? [];
    if (!previewRole) {
      return baseRoles;
    }

    return baseRoles.includes(previewRole) ? baseRoles : [previewRole, ...baseRoles];
  });
  readonly assignedGroups = computed(() => this.content()?.rolesAccess?.assignedGroups ?? []);
  readonly allowList = computed(() => this.content()?.rolesAccess?.allowList ?? []);
  readonly blockList = computed(() => this.content()?.rolesAccess?.blockList ?? []);

  onTabChange(tab: string): void {
    this.activeTab.set(tab as UserFactsheetTab);
  }

  retrySessions(): void {
    return;
  }

  userStatusSeverity(status: UserFactsheetStatus): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (status) {
      case 'Active':
        return 'success';
      case 'Invited':
        return 'info';
      case 'Disabled':
        return 'danger';
      case 'Locked':
        return 'warn';
    }
  }

  sessionStatusSeverity(status: string): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (status.toLowerCase()) {
      case 'active':
        return 'success';
      case 'revoked':
        return 'secondary';
      case 'expired':
        return 'danger';
      default:
        return 'info';
    }
  }

  protected slotNode(slot: string | null | undefined) {
    const normalizedSlot = slot?.trim();
    if (!normalizedSlot) {
      return null;
    }

    return this.slots()[normalizedSlot as keyof ReturnType<typeof this.slots>] ?? null;
  }

  protected selectedPreviewGuid(): string | null {
    return this.selectedGuid();
  }
  trackSession(_index: number, session: UserFactsheetSessionRecord): string {
    return session.id;
  }
}
