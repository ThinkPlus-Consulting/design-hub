import { Component, computed, input, output, signal, ChangeDetectionStrategy } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { TagModule } from 'primeng/tag';
import { TableModule } from 'primeng/table';
import { TabsModule } from 'primeng/tabs';
import { PaginatorModule } from 'primeng/paginator';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { ChipModule } from 'primeng/chip';
import { PreviewIdentityBound } from '../../../../../../../../utils/preview-identity-bound';
import { parseNodeConfiguration } from '../../../../../../../../utils/node-configuration';
import { childAt, componentAt, orderedStructuralChildren } from '../../../../../../../../utils/preview-structure';

import {
  FactsheetTab,
  HealthStatus,
  LifecycleAction,
  LifecycleActionDef,
  TenantFactsheet,
  TenantFactsheetScreenContentConfig,
  TenantStatus,
  TenantType,
} from '../../../../../../../../models/preview-content.types';

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

@Component({
  selector: 'app-tenant-factsheet-screen-preview',
  standalone: true,
  imports: [
    FormsModule,
    ButtonModule,
    InputTextModule,
    TagModule,
    TableModule,
    TabsModule,
    PaginatorModule,
    ToggleButtonModule,
    ChipModule,
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
    const banner = childAt(this.state, screen?.id, 0);
    const tabs = childAt(this.state, screen?.id, 1);
    const usersTab = childAt(this.state, screen?.id, 2);
    const identitySection = childAt(this.state, banner?.id, 0);
    const actionsSection = childAt(this.state, banner?.id, 1);
    const usersToolbar = childAt(this.state, usersTab?.id, 0);
    const usersResults = childAt(this.state, usersTab?.id, 1);
    const identityComponents = orderedStructuralChildren(this.state, identitySection?.id).filter((node) => node.family === 'Component');

    return {
      screen,
      banner,
      identitySection,
      logo: identityComponents[0] ?? null,
      name: identityComponents[1] ?? null,
      typeBadge: identityComponents[2] ?? null,
      statusBadge: identityComponents[3] ?? null,
      healthBadge: identityComponents[4] ?? null,
      shortName: identityComponents[5] ?? null,
      kpi0: identityComponents[6] ?? null,
      kpi1: identityComponents[7] ?? null,
      kpi2: identityComponents[8] ?? null,
      kpi3: identityComponents[9] ?? null,
      actionsSection,
      backAction: componentAt(this.state, actionsSection?.id, 0),
      editAction: componentAt(this.state, actionsSection?.id, 1),
      lifecycleActionNode: componentAt(this.state, actionsSection?.id, 2),
      tabs,
      tabsElement: componentAt(this.state, tabs?.id, 0),
      usersTab,
      usersToolbar,
      usersSearchInput: componentAt(this.state, usersToolbar?.id, 0),
      usersFilterToggle: componentAt(this.state, usersToolbar?.id, 1),
      inviteUserAction: componentAt(this.state, usersToolbar?.id, 2),
      usersResults,
      usersTable: componentAt(this.state, usersResults?.id, 0),
      usersPaginator: componentAt(this.state, usersResults?.id, 1),
      userStatusBadge: componentAt(this.state, usersResults?.id, 2),
    };
  });

  // ─── Inputs / Outputs ──────────────────────────────────────────────────────

  readonly tenant = computed(() => this.content()?.tenant ?? EMPTY_TENANT);

  /** Emits when user clicks "Back" to return to tenant list. */
  readonly back = output<void>();

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

  backLabel(): string {
    return this.content()?.actions.backLabel ?? '';
  }

  editLabel(): string {
    return this.content()?.actions.editLabel ?? '';
  }

  inviteUserLabel(): string {
    return this.content()?.actions.inviteUserLabel ?? '';
  }

  usersSearchAriaLabel(): string {
    return this.content()?.usersSection.searchAriaLabel ?? '';
  }

  usersSearchPlaceholder(): string {
    return this.content()?.usersSection.searchPlaceholder ?? '';
  }

  usersFilterToggleAriaLabel(): string {
    return this.content()?.usersSection.filterToggleAriaLabel ?? '';
  }

  usersColumns() {
    return this.content()?.usersSection.tableColumns ?? null;
  }

  brandingSection() {
    return this.content()?.brandingSection ?? null;
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

  userStatusSeverity(
    status: 'Active' | 'Invited' | 'Disabled',
  ): 'success' | 'info' | 'warn' | 'danger' | 'secondary' {
    switch (status) {
      case 'Active':
        return 'success';
      case 'Invited':
        return 'info';
      case 'Disabled':
        return 'danger';
    }
  }

  // ─── Actions ───────────────────────────────────────────────────────────────

  onTabChange(tab: string): void {
    this.activeTab.set(tab as FactsheetTab);
  }

  onBack(): void {
    this.back.emit();
  }

  onLifecycleAction(): void {
    const def = this.lifecycleActionDef();
    if (def) {
      this.lifecycleAction.emit(def.action);
    }
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
