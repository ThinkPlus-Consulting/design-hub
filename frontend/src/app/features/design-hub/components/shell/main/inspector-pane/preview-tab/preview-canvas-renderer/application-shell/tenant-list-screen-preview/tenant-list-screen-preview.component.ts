import { ChangeDetectionStrategy, Component, computed, input, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule } from 'primeng/paginator';
import { SelectModule } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { ToggleButtonModule } from 'primeng/togglebutton';

import {
  HealthStatus,
  TenantStatus,
  TenantListScreenContentConfig,
  TenantSummary,
  TenantType,
} from '../../../../../../../../models/preview-content.types';
import { SystemShellGraphNode } from '../../../../../../../../models/graph.types';
import { PreviewIdentityBound } from '../../../../../../../../utils/preview-identity-bound';
import { parseNodeConfiguration } from '../../../../../../../../utils/node-configuration';
import { childAt, componentAt } from '../../../../../../../../utils/preview-structure';

function getInitials(name: string): string {
  return name
    .split(/\s+/)
    .filter(Boolean)
    .slice(0, 2)
    .map((w) => w[0].toUpperCase())
    .join('');
}

@Component({
  selector: 'app-tenant-list-screen-preview',
  standalone: true,
  imports: [
    FormsModule,
    ButtonModule,
    InputTextModule,
    TagModule,
    TableModule,
    PaginatorModule,
    SelectModule,
    ToggleButtonModule,
  ],
  templateUrl: './tenant-list-screen-preview.component.html',
  styleUrl: './tenant-list-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantListScreenPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly screenObjectId = input<string | null>(null);
  private readonly screenNode = computed(() => this.state.nodeById(this.screenObjectId()));
  private readonly content = computed(() =>
    parseNodeConfiguration<TenantListScreenContentConfig>(this.screenNode()),
  );
  private readonly gridCardNodes = computed(() =>
    this.state
      .orderedChildrenOf(this.slots().gridView?.id, ['HAS_COMPONENT'])
      .filter(
        (node) =>
          node.family === 'Component' &&
          typeof node.name === 'string' &&
          node.name.startsWith('Tenant Card - '),
      ),
  );
  private readonly gridCardNodeByTenantName = computed(() => {
    const cards = new Map<string, SystemShellGraphNode>();
    for (const node of this.gridCardNodes()) {
      const tenantName = node.name?.replace(/^Tenant Card - /, '').trim();
      if (tenantName) {
        cards.set(tenantName, node);
      }
    }
    return cards;
  });
  private readonly slots = computed(() => {
    const screen = this.state.nodeById(this.screenObjectId());
    const header = childAt(this.state, screen?.id, 0);
    const toolbar = childAt(this.state, screen?.id, 1);
    const filterRow = childAt(this.state, screen?.id, 2);
    const emptyState = childAt(this.state, screen?.id, 3);
    const resultsSurface = childAt(this.state, screen?.id, 4);
    const searchSection = childAt(this.state, toolbar?.id, 0);
    const filterToggleSection = childAt(this.state, toolbar?.id, 1);
    const viewModeSection = childAt(this.state, toolbar?.id, 2);
    const createActionSection = childAt(this.state, toolbar?.id, 3);
    const gridView = childAt(this.state, resultsSurface?.id, 0);
    const tableView = childAt(this.state, resultsSurface?.id, 1);
    const pagination = childAt(this.state, resultsSurface?.id, 2);

    return {
      screen,
      header,
      headerTitle: componentAt(this.state, header?.id, 0),
      toolbar,
      searchSection,
      searchInput: componentAt(this.state, searchSection?.id, 0),
      filterToggleSection,
      filterToggleAction: componentAt(this.state, filterToggleSection?.id, 0),
      viewModeSection,
      viewModeToggle: componentAt(this.state, viewModeSection?.id, 0),
      createActionSection,
      createAction: componentAt(this.state, createActionSection?.id, 0),
      filterRow,
      typeFilter: componentAt(this.state, filterRow?.id, 0),
      statusFilter: componentAt(this.state, filterRow?.id, 1),
      clearFiltersAction: componentAt(this.state, filterRow?.id, 2),
      emptyState,
      emptyStateTitle: componentAt(this.state, emptyState?.id, 0),
      emptyStateMessage: componentAt(this.state, emptyState?.id, 1),
      emptyStateAction: componentAt(this.state, emptyState?.id, 2),
      resultsSurface,
      gridView,
      gridCard: componentAt(this.state, gridView?.id, 0),
      gridAvatar: componentAt(this.state, gridView?.id, 1),
      gridName: componentAt(this.state, gridView?.id, 2),
      gridShortName: componentAt(this.state, gridView?.id, 3),
      gridStatusBadge: componentAt(this.state, gridView?.id, 4),
      gridTypeBadge: componentAt(this.state, gridView?.id, 5),
      gridHealthBadge: componentAt(this.state, gridView?.id, 6),
      gridStatsSummary: componentAt(this.state, gridView?.id, 7),
      tableView,
      table: componentAt(this.state, tableView?.id, 0),
      tableTypeBadge: componentAt(this.state, tableView?.id, 1),
      pagination,
      paginator: componentAt(this.state, pagination?.id, 0),
    };
  });
  private readonly selectedObjectId = computed(() => {
    const guid = this.selectedGuid();
    return guid ? this.state.nodeGuidMap().get(guid)?.id ?? null : null;
  });

  readonly searchText = signal('');
  readonly activeTypeFilter = signal<TenantType | null>(null);
  readonly activeStatusFilter = signal<TenantStatus | null>(null);
  readonly viewMode = signal<'grid' | 'table'>('grid');
  readonly filtersVisible = signal(false);
  readonly currentPage = signal(0);
  readonly pageSize = signal(10);

  readonly tenants = computed<readonly TenantSummary[]>(() => this.content()?.tenants ?? []);
  readonly typeOptions = computed(() => this.content()?.typeOptions ?? []);
  readonly statusOptions = computed(() => this.content()?.statusOptions ?? []);

  readonly filteredTenants = computed(() => {
    const search = this.searchText().trim().toLowerCase();
    const type = this.activeTypeFilter();
    const status = this.activeStatusFilter();

    return this.tenants().filter((t) => {
      if (search) {
        const matchesSearch =
          t.name.toLowerCase().includes(search) || t.shortName.toLowerCase().includes(search);
        if (!matchesSearch) return false;
      }

      if (type !== null && t.type !== type) return false;
      if (status !== null && t.status !== status) return false;

      return true;
    });
  });

  readonly hasActiveFilters = computed(
    () => this.activeTypeFilter() !== null || this.activeStatusFilter() !== null,
  );

  readonly forceFilterRowVisible = computed(() => this.slotSelectionIs('filterRow', 'typeFilter', 'statusFilter', 'clearFiltersAction'));
  readonly forceEmptyStateVisible = computed(() => this.slotSelectionIs('emptyState', 'emptyStateTitle', 'emptyStateMessage', 'emptyStateAction'));
  readonly forcedViewMode = computed<'grid' | 'table' | null>(() => {
    if (this.slotSelectionIs('tableView', 'table', 'pagination', 'paginator', 'tableTypeBadge')) {
      return 'table';
    }
    if (
      this.selectedGridCardObjectId() ||
      this.slotSelectionIs(
        'gridView',
        'gridCard',
        'gridAvatar',
        'gridName',
        'gridShortName',
        'gridStatusBadge',
        'gridTypeBadge',
        'gridHealthBadge',
        'gridStatsSummary',
      )
    ) {
      return 'grid';
    }
    return null;
  });

  readonly showFilters = computed(() => this.filtersVisible() || this.forceFilterRowVisible());
  readonly effectiveViewMode = computed(() => this.forcedViewMode() ?? this.viewMode());
  readonly isEmpty = computed(
    () => this.forceEmptyStateVisible() || this.filteredTenants().length === 0,
  );
  readonly totalRecords = computed(() => (this.isEmpty() ? 0 : this.filteredTenants().length));
  readonly paginatedTenants = computed(() => {
    if (this.isEmpty()) {
      return [];
    }

    const filtered = this.filteredTenants();
    const start = this.currentPage() * this.pageSize();
    return filtered.slice(start, start + this.pageSize());
  });

  onSearchChange(value: string): void {
    this.searchText.set(value);
    this.currentPage.set(0);
  }

  onTypeFilterChange(value: TenantType | null): void {
    this.activeTypeFilter.set(value);
    this.currentPage.set(0);
  }

  onStatusFilterChange(value: TenantStatus | null): void {
    this.activeStatusFilter.set(value);
    this.currentPage.set(0);
  }

  toggleFilters(): void {
    this.filtersVisible.update((v) => !v);
  }

  clearFilters(): void {
    this.activeTypeFilter.set(null);
    this.activeStatusFilter.set(null);
    this.searchText.set('');
    this.currentPage.set(0);
  }

  onCardClick(_tenant: TenantSummary): void {}

  onCreateTenant(): void {}

  onPageChange(event: { first?: number; rows?: number }): void {
    const first = event.first ?? 0;
    const rows = event.rows ?? this.pageSize();
    this.currentPage.set(Math.floor(first / rows));
    this.pageSize.set(rows);
  }

  getInitials(name: string): string {
    return getInitials(name);
  }

  title(): string {
    return this.content()?.title ?? '';
  }

  searchAriaLabel(): string {
    return this.content()?.searchAriaLabel ?? '';
  }

  searchPlaceholder(): string {
    return this.content()?.searchPlaceholder ?? '';
  }

  filterToggleAriaLabel(): string {
    return this.content()?.actions.filterToggleAriaLabel ?? '';
  }

  viewToggleAriaLabel(): string {
    return this.content()?.viewToggleAriaLabel ?? '';
  }

  createLabel(): string {
    return this.content()?.actions.createLabel ?? '';
  }

  typePlaceholder(): string {
    return this.content()?.filters.typePlaceholder ?? '';
  }

  statusPlaceholder(): string {
    return this.content()?.filters.statusPlaceholder ?? '';
  }

  clearLabel(): string {
    return this.content()?.filters.clearLabel ?? '';
  }

  emptyStateTitle(): string {
    return this.content()?.emptyState.title ?? '';
  }

  emptyStateMessage(): string {
    if (this.hasActiveFilters() || this.searchText()) {
      return this.content()?.emptyState.noResultsMessage ?? '';
    }

    return this.content()?.emptyState.noTenantsMessage ?? '';
  }

  emptyStateActionLabel(): string {
    return this.content()?.filters.emptyStateActionLabel ?? '';
  }

  gridLabel(): string {
    return this.content()?.viewMode.gridLabel ?? '';
  }

  tableLabel(): string {
    return this.content()?.viewMode.tableLabel ?? '';
  }

  tableTenantColumnLabel(): string {
    return this.content()?.tableColumns.tenant ?? '';
  }

  tableTypeColumnLabel(): string {
    return this.content()?.tableColumns.type ?? '';
  }

  tableStatusColumnLabel(): string {
    return this.content()?.tableColumns.status ?? '';
  }

  tableHealthColumnLabel(): string {
    return this.content()?.tableColumns.health ?? '';
  }

  tableUsersColumnLabel(): string {
    return this.content()?.tableColumns.users ?? '';
  }

  tableAgentsColumnLabel(): string {
    return this.content()?.tableColumns.agents ?? '';
  }

  statsUsersLabel(): string {
    return this.content()?.statsLabels.users ?? '';
  }

  statsAgentsLabel(): string {
    return this.content()?.statsLabels.agents ?? '';
  }

  statsTypesLabel(): string {
    return this.content()?.statsLabels.types ?? '';
  }

  healthUnknownLabel(): string {
    return this.content()?.healthUnknownLabel ?? '';
  }

  getAvatarClass(type: TenantType): string {
    switch (type) {
      case 'MASTER':
        return 'avatar-master';
      case 'REGULAR':
        return 'avatar-regular';
      case 'DOMINANT':
        return 'avatar-dominant';
    }
  }

  getTypeSeverity(type: TenantType): 'success' | 'warn' | 'danger' | 'info' | 'secondary' {
    switch (type) {
      case 'MASTER':
        return 'success';
      case 'REGULAR':
        return 'warn';
      case 'DOMINANT':
        return 'danger';
    }
  }

  getStatusDotClass(status: TenantStatus): string {
    switch (status) {
      case 'ACTIVE':
        return 'dot-active';
      case 'SUSPENDED':
        return 'dot-suspended';
      case 'PROVISIONING':
        return 'dot-provisioning';
      case 'PROVISIONING_FAILED':
        return 'dot-failed';
    }
  }

  getStatusLabel(status: TenantStatus): string {
    switch (status) {
      case 'ACTIVE':
        return 'Active';
      case 'SUSPENDED':
        return 'Suspended';
      case 'PROVISIONING':
        return 'Provisioning';
      case 'PROVISIONING_FAILED':
        return 'Failed';
    }
  }

  getHealthClass(health: HealthStatus): string {
    switch (health) {
      case 'HEALTHY':
        return 'health-healthy';
      case 'DEGRADED':
        return 'health-degraded';
      case 'UNHEALTHY':
        return 'health-unhealthy';
    }
  }

  getHealthLabel(health: HealthStatus): string {
    switch (health) {
      case 'HEALTHY':
        return 'Healthy';
      case 'DEGRADED':
        return 'Degraded';
      case 'UNHEALTHY':
        return 'Unhealthy';
    }
  }

  getHealthIcon(health: HealthStatus | null): string {
    switch (health) {
      case 'HEALTHY':
        return 'pi pi-check-circle';
      case 'DEGRADED':
        return 'pi pi-exclamation-triangle';
      case 'UNHEALTHY':
        return 'pi pi-times-circle';
      default:
        return '';
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

  gridCardObjectId(tenant: TenantSummary): string | null {
    return this.gridCardNodeByTenantName().get(tenant.name)?.id?.trim() ?? null;
  }

  gridCardGuid(tenant: TenantSummary): string | null {
    return this.gridCardNodeByTenantName().get(tenant.name)?.guid?.trim() ?? null;
  }

  isGridCardFocused(tenant: TenantSummary): boolean {
    return this.selectedPreviewGuid() === this.gridCardGuid(tenant);
  }

  cardDomRef(tenant: TenantSummary): string {
    return `tenant-list-card-${tenant.shortName}`;
  }

  private slotSelectionIs(...slotNames: string[]): boolean {
    const selectedObjectId = this.selectedObjectId();
    if (!selectedObjectId) {
      return false;
    }

    return slotNames.some((slotName) => this.slotNode(slotName)?.id === selectedObjectId);
  }

  private selectedGridCardObjectId(): string | null {
    const selectedObjectId = this.selectedObjectId();
    if (!selectedObjectId) {
      return null;
    }

    return this.gridCardNodes().some((node) => node.id === selectedObjectId) ? selectedObjectId : null;
  }
}
