import { ChangeDetectionStrategy, Component, computed, input, output, signal } from '@angular/core';
import { ButtonModule } from 'primeng/button';
import { ImageModule } from 'primeng/image';
import { TagModule } from 'primeng/tag';
import { TabsModule } from 'primeng/tabs';
import { ChipModule } from 'primeng/chip';
import { PreviewIdentityBound } from '../../../../../../../../../utils/preview-identity-bound';
import { parseNodeConfiguration } from '../../../../../../../../../utils/node-configuration';
import {
  childByName,
  componentAt,
  componentByName,
} from '../../../../../../../../../utils/preview-structure';
import { TenantFactsheetAgentsTabComponent } from './agents-tab/tenant-factsheet-agents-tab.component';
import { TenantFactsheetAuditTabComponent } from './audit-tab/tenant-factsheet-audit-tab.component';
import { TenantFactsheetBrandingTabComponent } from './branding-tab/tenant-factsheet-branding-tab.component';
import { TenantFactsheetDictionaryTabComponent } from './dictionary-tab/tenant-factsheet-dictionary-tab.component';
import { TenantFactsheetHealthTabComponent } from './health-tab/tenant-factsheet-health-tab.component';
import { TenantFactsheetIntegrationsTabComponent } from './integrations-tab/tenant-factsheet-integrations-tab.component';
import { TenantFactsheetLicenseTabComponent } from './license-tab/tenant-factsheet-license-tab.component';
import { TenantFactsheetStudioTabComponent } from './studio-tab/tenant-factsheet-studio-tab.component';
import { TenantFactsheetUsersTabComponent } from './users-tab/tenant-factsheet-users-tab.component';
import { TenantFactsheetUsersTabBindings } from './users-tab/tenant-factsheet-users-tab.bindings';

import {
  FactsheetTab,
  HealthStatus,
  LifecycleAction,
  LifecycleActionDef,
  TenantFactsheet,
  TenantFactsheetScreenContentConfig,
  TenantStatus,
  TenantUser,
  TenantType,
} from '../../../../../../../../../models/preview-content.types';

const EMPTY_TENANT: TenantFactsheet = {
  id: '',
  name: '',
  shortName: '',
  type: 'REGULAR',
  status: 'PROVISIONING',
  health: 'HEALTHY',
  logoUrl: null,
  kpis: [],
};

const EMPTY_LICENSE = {
  status: 'Active' as const,
  validFrom: '',
  validUntil: '',
  allocations: [],
};

function buildTenantLogoDataUrl(initials: string): string {
  const safeInitials = initials.replace(/[^A-Z0-9]/gi, '').slice(0, 2).toUpperCase() || 'TN';
  const svg = `
    <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 160 160">
      <rect width="160" height="160" rx="16" fill="#f2efe9" />
      <rect x="1" y="1" width="158" height="158" rx="15" fill="none" stroke="#d8d1c4" stroke-width="2" />
      <text x="80" y="98" text-anchor="middle" font-family="Segoe UI, Arial, sans-serif" font-size="54" font-weight="700" fill="#1d4ed8">${safeInitials}</text>
    </svg>
  `.trim();

  return `data:image/svg+xml;utf8,${encodeURIComponent(svg)}`;
}

@Component({
  selector: 'app-tenant-factsheet-screen-preview',
  standalone: true,
  imports: [
    ButtonModule,
    ImageModule,
    TagModule,
    TabsModule,
    ChipModule,
    TenantFactsheetAgentsTabComponent,
    TenantFactsheetAuditTabComponent,
    TenantFactsheetBrandingTabComponent,
    TenantFactsheetDictionaryTabComponent,
    TenantFactsheetHealthTabComponent,
    TenantFactsheetIntegrationsTabComponent,
    TenantFactsheetLicenseTabComponent,
    TenantFactsheetStudioTabComponent,
    TenantFactsheetUsersTabComponent,
  ],
  templateUrl: './tenant-factsheet-screen-preview.component.html',
  styleUrl: './tenant-factsheet-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetScreenPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly screenObjectId = input<string | null>(null);
  private readonly screenNode = computed(() => this.state.nodeById(this.screenObjectId()));
  private readonly content = computed(() =>
    parseNodeConfiguration<TenantFactsheetScreenContentConfig>(this.screenNode()),
  );
  private readonly slots = computed(() => {
    const screen = this.state.nodeById(this.screenObjectId());
    const mainContainer = this.state
      .orderedParentsOf(screen?.id, ['HAS_SCREEN'])
      .find((node) => node.family === 'Container' && node.name === 'Main Container');
    const tenantHeader = childByName(this.state, screen?.id, 'Tenant Header', 'Container');
    const detailsContainer = childByName(this.state, screen?.id, 'Details Container', 'Container');
    const tabsRowSection = childByName(this.state, detailsContainer?.id, 'Tabs Row Section', 'Section');
    const tabContentsRowSection = childByName(this.state, detailsContainer?.id, 'Tab Contents Row Section', 'Section');
    const usersTab = childByName(this.state, tabContentsRowSection?.id, 'Users Tab Section', 'Container');
    const logoSection = childByName(this.state, tenantHeader?.id, 'Logo Section', 'Section');
    const generalSection = childByName(this.state, tenantHeader?.id, 'General Section', 'Section');
    const nameRowSection = childByName(this.state, generalSection?.id, 'Name Row Section', 'Section');
    const badgeRowSection = childByName(this.state, generalSection?.id, 'Badge Row Section', 'Section');
    const idRowSection = childByName(this.state, generalSection?.id, 'Id Row Section', 'Section');
    const kpiRowSection = childByName(this.state, generalSection?.id, 'Kpi Row Section', 'Section');
    const actionsSection = childByName(this.state, tenantHeader?.id, 'Actions Section', 'Section');
    const usersControlsRow = childByName(this.state, usersTab?.id, 'Users Controls Row Section', 'Section');
    const usersTableRow = childByName(this.state, usersTab?.id, 'Users Table Row Section', 'Section');
    const usersLoadingStateSection = childByName(
      this.state,
      usersTableRow?.id,
      'Users Loading State Section',
      'Section',
    );
    const usersEmptyStateSection = childByName(
      this.state,
      usersTableRow?.id,
      'Users Empty State Section',
      'Section',
    );
    const usersDataStateSection = childByName(
      this.state,
      usersTableRow?.id,
      'Users Data State Section',
      'Section',
    );
    const usersPaginationRow = childByName(
      this.state,
      usersTab?.id,
      'Users Pagination Row Section',
      'Section',
    );

    return {
      screen,
      mainContainer,
      userFactsheetScreen: childByName(this.state, mainContainer?.id, 'View User Fact Sheet', 'Screen'),
      tenantHeader,
      logoSection,
      logo: componentByName(this.state, logoSection?.id, 'Tenant Logo'),
      generalSection,
      nameRowSection,
      name: componentByName(this.state, nameRowSection?.id, 'Tenant Name'),
      badgeRowSection,
      typeBadge: componentByName(this.state, badgeRowSection?.id, 'Tenant Type Badge'),
      statusBadge: componentByName(this.state, badgeRowSection?.id, 'Tenant Status Badge'),
      healthBadge: componentByName(this.state, badgeRowSection?.id, 'Tenant Health Badge'),
      idRowSection,
      tenantId: componentByName(this.state, idRowSection?.id, 'Tenant Id'),
      kpiRowSection,
      kpi0: componentByName(this.state, kpiRowSection?.id, 'Users KPI Chip'),
      kpi1: componentByName(this.state, kpiRowSection?.id, 'Agents KPI Chip'),
      kpi2: componentByName(this.state, kpiRowSection?.id, 'Object Types KPI Chip'),
      kpi3: componentByName(this.state, kpiRowSection?.id, 'License KPI Chip'),
      actionsSection,
      editAction: componentByName(this.state, actionsSection?.id, 'Edit Action'),
      lifecycleActionNode: componentByName(this.state, actionsSection?.id, 'Lifecycle Action'),
      detailsContainer,
      tabsRowSection,
      tabContentsRowSection,
      tabsElement: componentAt(this.state, tabsRowSection?.id, 0),
      usersTab,
      usersControlsRow,
      usersSearchInput: componentByName(this.state, usersControlsRow?.id, 'Users Search Input'),
      usersFilterToggle: componentByName(this.state, usersControlsRow?.id, 'Users Filter Toggle'),
      usersTableRow,
      usersLoadingStateSection,
      usersTableSkeleton: componentByName(this.state, usersLoadingStateSection?.id, 'Users Table Skeleton'),
      usersEmptyStateSection,
      usersEmptyStateTitle: componentByName(this.state, usersEmptyStateSection?.id, 'Users Empty State Title'),
      usersEmptyStateMessage: componentByName(this.state, usersEmptyStateSection?.id, 'Users Empty State Message'),
      usersDataStateSection,
      usersTable: componentByName(this.state, usersDataStateSection?.id, 'Users Table'),
      userStatusBadge: componentByName(this.state, usersDataStateSection?.id, 'User Status Badge'),
      usersPaginationRow,
      usersPaginator: componentByName(this.state, usersPaginationRow?.id, 'Users Paginator'),
    };
  });

  // ─── Inputs / Outputs ──────────────────────────────────────────────────────

  readonly tenant = computed(() => this.content()?.tenant ?? EMPTY_TENANT);

  /** Emits lifecycle actions (suspend, reactivate). */
  readonly lifecycleAction = output<LifecycleAction>();

  // ─── Signals ───────────────────────────────────────────────────────────────

  /** Currently active tab. */
  readonly activeTab = signal<FactsheetTab>('users');

  /** Users tab — search + filter toggle. */
  /** Per-tab search + filter + view mode signals. */
  readonly usersSearch = signal('');
  readonly usersFiltersVisible = signal(false);

  readonly integrationsSearch = signal('');
  readonly integrationsFiltersVisible = signal(false);
  readonly integrationsViewMode = signal<'table' | 'grid'>('grid');

  readonly dictionarySearch = signal('');
  readonly dictionaryFiltersVisible = signal(false);
  readonly dictionaryViewMode = signal<'table' | 'grid'>('table');

  readonly auditSearch = signal('');
  readonly auditFiltersVisible = signal(false);
  readonly auditViewMode = signal<'table' | 'grid'>('table');

  readonly healthSearch = signal('');
  readonly healthFiltersVisible = signal(false);
  readonly healthViewMode = signal<'table' | 'grid'>('grid');

  // ─── Tab Definitions ───────────────────────────────────────────────────────

  readonly tabs = computed(() => this.content()?.tabs ?? []);
  readonly users = computed(() => this.content()?.users ?? []);
  readonly integrations = computed(() => this.content()?.integrations ?? []);
  readonly dictionary = computed(() => this.content()?.dictionary ?? []);
  readonly agents = computed(() => this.content()?.agents ?? []);
  readonly auditLog = computed(() => this.content()?.auditLog ?? []);
  readonly healthChecks = computed(() => this.content()?.healthChecks ?? []);
  readonly license = computed(() => this.content()?.license ?? EMPTY_LICENSE);
  readonly usersLoading = computed(() => this.usersSection()?.previewState?.loading ?? false);
  readonly userFactsheetScreenObjectId = computed(() => this.slots().userFactsheetScreen?.id ?? null);

  // ─── Computed ──────────────────────────────────────────────────────────────

  /** Initials for the logo placeholder. */
  readonly initials = computed(() => {
    const name = this.tenant().name;
    const parts = name.split(/\s+/).filter(Boolean);
    if (parts.length >= 2) {
      return (parts[0][0] + parts[1][0]).toUpperCase();
    }
    return name.substring(0, 2).toUpperCase();
  });
  readonly logoSrc = computed(() => this.tenant().logoUrl ?? buildTenantLogoDataUrl(this.initials()));

  /** Contextual lifecycle action button config. */
  readonly lifecycleActionDef = computed<LifecycleActionDef | null>(() => {
    return this.content()?.lifecycleActions?.[this.tenant().status] ?? null;
  });

  readonly allocatedSeats = computed(() =>
    this.license().allocations.reduce((total, allocation) => total + allocation.allocated, 0),
  );

  readonly assignedSeats = computed(() =>
    this.license().allocations.reduce((total, allocation) => total + allocation.assigned, 0),
  );

  readonly availableSeats = computed(() =>
    this.license().allocations.reduce((total, allocation) => total + allocation.available, 0),
  );
  readonly usersTabBindings = computed<TenantFactsheetUsersTabBindings>(() => ({
    usersTabSourceObjectId: this.sourceObjectId('usersTab'),
    usersTabGuid: this.guid('usersTab'),
    usersTabFocused: this.isFocused('usersTab'),
    usersControlsRowSourceObjectId: this.sourceObjectId('usersControlsRow'),
    usersControlsRowGuid: this.guid('usersControlsRow'),
    usersControlsRowFocused: this.isFocused('usersControlsRow'),
    usersSearchInputSourceObjectId: this.sourceObjectId('usersSearchInput'),
    usersSearchInputGuid: this.guid('usersSearchInput'),
    usersSearchInputFocused: this.isFocused('usersSearchInput'),
    usersFilterToggleSourceObjectId: this.sourceObjectId('usersFilterToggle'),
    usersFilterToggleGuid: this.guid('usersFilterToggle'),
    usersFilterToggleFocused: this.isFocused('usersFilterToggle'),
    usersTableRowSourceObjectId: this.sourceObjectId('usersTableRow'),
    usersTableRowGuid: this.guid('usersTableRow'),
    usersTableRowFocused: this.isFocused('usersTableRow'),
    usersLoadingStateSectionSourceObjectId: this.sourceObjectId('usersLoadingStateSection'),
    usersLoadingStateSectionGuid: this.guid('usersLoadingStateSection'),
    usersLoadingStateSectionFocused: this.isFocused('usersLoadingStateSection'),
    usersTableSkeletonSourceObjectId: this.sourceObjectId('usersTableSkeleton'),
    usersTableSkeletonGuid: this.guid('usersTableSkeleton'),
    usersTableSkeletonFocused: this.isFocused('usersTableSkeleton'),
    usersEmptyStateSectionSourceObjectId: this.sourceObjectId('usersEmptyStateSection'),
    usersEmptyStateSectionGuid: this.guid('usersEmptyStateSection'),
    usersEmptyStateSectionFocused: this.isFocused('usersEmptyStateSection'),
    usersEmptyStateTitleSourceObjectId: this.sourceObjectId('usersEmptyStateTitle'),
    usersEmptyStateTitleGuid: this.guid('usersEmptyStateTitle'),
    usersEmptyStateTitleFocused: this.isFocused('usersEmptyStateTitle'),
    usersEmptyStateMessageSourceObjectId: this.sourceObjectId('usersEmptyStateMessage'),
    usersEmptyStateMessageGuid: this.guid('usersEmptyStateMessage'),
    usersEmptyStateMessageFocused: this.isFocused('usersEmptyStateMessage'),
    usersDataStateSectionSourceObjectId: this.sourceObjectId('usersDataStateSection'),
    usersDataStateSectionGuid: this.guid('usersDataStateSection'),
    usersDataStateSectionFocused: this.isFocused('usersDataStateSection'),
    usersTableSourceObjectId: this.sourceObjectId('usersTable'),
    usersTableGuid: this.guid('usersTable'),
    usersTableFocused: this.isFocused('usersTable'),
    usersPaginationRowSourceObjectId: this.sourceObjectId('usersPaginationRow'),
    usersPaginationRowGuid: this.guid('usersPaginationRow'),
    usersPaginationRowFocused: this.isFocused('usersPaginationRow'),
    usersPaginatorSourceObjectId: this.sourceObjectId('usersPaginator'),
    usersPaginatorGuid: this.guid('usersPaginator'),
    usersPaginatorFocused: this.isFocused('usersPaginator'),
    userStatusBadgeSourceObjectId: this.sourceObjectId('userStatusBadge'),
    userStatusBadgeGuid: this.guid('userStatusBadge'),
    userStatusBadgeFocused: this.isFocused('userStatusBadge'),
  }));

  editLabel(): string {
    return this.content()?.actions.editLabel ?? '';
  }

  brandingSection() {
    return this.content()?.brandingSection ?? null;
  }

  usersSection() {
    return this.content()?.usersSection ?? null;
  }

  integrationsSection() {
    return this.content()?.integrationsSection ?? null;
  }

  dictionarySection() {
    return this.content()?.dictionarySection ?? null;
  }

  agentsSection() {
    return this.content()?.agentsSection ?? null;
  }

  studioSection() {
    return this.content()?.studioSection ?? null;
  }

  auditSection() {
    return this.content()?.auditSection ?? null;
  }

  healthSection() {
    return this.content()?.healthSection ?? null;
  }

  licenseSection() {
    return this.content()?.licenseSection ?? null;
  }

  // ─── Template Helpers ──────────────────────────────────────────────────────

  typeSeverity(type: TenantType): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (type) {
      case 'MASTER':
        return 'info';
      case 'DOMINANT':
        return 'warn';
      case 'REGULAR':
        return 'secondary';
    }
  }

  statusColor(status: TenantStatus): string {
    switch (status) {
      case 'ACTIVE':
        return 'var(--tp-success)';
      case 'SUSPENDED':
        return 'var(--tp-warning)';
      case 'PROVISIONING':
        return 'var(--tp-primary)';
      case 'PROVISIONING_FAILED':
        return 'var(--tp-danger)';
    }
  }

  healthColor(health: HealthStatus): string {
    switch (health) {
      case 'HEALTHY':
        return 'var(--tp-success)';
      case 'DEGRADED':
        return 'var(--tp-warning)';
      case 'UNHEALTHY':
        return 'var(--tp-danger)';
    }
  }

  healthSeverity(health: HealthStatus): 'success' | 'warn' | 'danger' {
    switch (health) {
      case 'HEALTHY':
        return 'success';
      case 'DEGRADED':
        return 'warn';
      case 'UNHEALTHY':
        return 'danger';
    }
  }

  // ─── Actions ───────────────────────────────────────────────────────────────

  onTabChange(tab: string): void {
    this.activeTab.set(tab as FactsheetTab);
  }

  onLifecycleAction(): void {
    const def = this.lifecycleActionDef();
    if (def) {
      this.lifecycleAction.emit(def.action);
    }
  }

  onOpenUserFactsheet(user: TenantUser): void {
    const screenObjectId = this.userFactsheetScreenObjectId();
    if (!screenObjectId) {
      return;
    }

    this.state.setPreviewUserFactsheetUser(user);
    this.state.selectObjectId(screenObjectId);
  }

  kpiSlot(index: number): string {
    return `kpi${index}`;
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
}
