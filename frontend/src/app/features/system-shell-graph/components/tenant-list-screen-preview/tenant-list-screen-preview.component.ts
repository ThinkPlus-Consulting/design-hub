import { ChangeDetectionStrategy, Component, computed, inject, input, signal } from '@angular/core';
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
  SAMPLE_TENANTS,
  TenantStatus,
  TenantSummary,
  TenantType,
} from './tenant-list-screen-preview.models';
import { SystemShellGraphStateService } from '../../services/system-shell-graph-state.service';

const FILTER_CODES = new Set([
  'SHL02.SCN02.SEC03',
  'SHL02.SCN02.SEC03.ELT01',
  'SHL02.SCN02.SEC03.ELT02',
  'SHL02.SCN02.SEC03.ELT03',
]);

const EMPTY_STATE_CODES = new Set([
  'SHL02.SCN02.SEC04',
  'SHL02.SCN02.SEC04.ELT01',
  'SHL02.SCN02.SEC04.ELT02',
  'SHL02.SCN02.SEC04.ELT03',
]);

const GRID_VIEW_CODES = new Set([
  'SHL02.SCN02.SEC05.SEC01',
  'SHL02.SCN02.SEC05.SEC01.ELT01',
]);

const TABLE_VIEW_CODES = new Set([
  'SHL02.SCN02.SEC05.SEC02',
  'SHL02.SCN02.SEC05.SEC02.ELT01',
  'SHL02.SCN02.SEC05.SEC03',
  'SHL02.SCN02.SEC05.SEC03.ELT01',
]);

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
export class TenantListScreenPreviewComponent {
  private readonly state = inject(SystemShellGraphStateService);
  readonly selectedGuid = input<string | null>(null);

  readonly searchText = signal('');
  readonly activeTypeFilter = signal<TenantType | null>(null);
  readonly activeStatusFilter = signal<TenantStatus | null>(null);
  readonly tenants = signal<readonly TenantSummary[]>(SAMPLE_TENANTS);
  readonly viewMode = signal<'grid' | 'table'>('grid');
  readonly filtersVisible = signal(false);
  readonly currentPage = signal(0);
  readonly pageSize = signal(10);

  readonly typeOptions = [
    { label: 'All Types', value: null },
    { label: 'Master', value: 'MASTER' as TenantType },
    { label: 'Regular', value: 'REGULAR' as TenantType },
    { label: 'Dominant', value: 'DOMINANT' as TenantType },
  ];

  readonly statusOptions = [
    { label: 'All Statuses', value: null },
    { label: 'Active', value: 'ACTIVE' as TenantStatus },
    { label: 'Suspended', value: 'SUSPENDED' as TenantStatus },
    { label: 'Provisioning', value: 'PROVISIONING' as TenantStatus },
    { label: 'Failed', value: 'PROVISIONING_FAILED' as TenantStatus },
  ];

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

  readonly forceFilterRowVisible = computed(() => FILTER_CODES.has(this.selectedCode() ?? ''));
  readonly forceEmptyStateVisible = computed(() => EMPTY_STATE_CODES.has(this.selectedCode() ?? ''));
  readonly forcedViewMode = computed<'grid' | 'table' | null>(() => {
    const code = this.selectedCode() ?? '';
    if (TABLE_VIEW_CODES.has(code)) {
      return 'table';
    }
    if (GRID_VIEW_CODES.has(code)) {
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

  sourceObjectId(graphCode: string | null | undefined): string | null {
    const normalizedCode = graphCode?.trim();
    if (!normalizedCode) {
      return null;
    }

    return this.state.nodeMap().get(normalizedCode)?.id?.trim() ?? null;
  }

  guid(graphCode: string | null | undefined): string | null {
    const normalizedCode = graphCode?.trim();
    if (!normalizedCode) {
      return null;
    }

    return this.state.nodeMap().get(normalizedCode)?.guid?.trim() ?? null;
  }

  isFocused(graphCode: string): boolean {
    return this.selectedGuid() === this.guid(graphCode);
  }

  selectedCode(): string | null {
    const guid = this.selectedGuid();
    return guid ? this.state.nodeGuidMap().get(guid)?.code ?? null : null;
  }

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
}
