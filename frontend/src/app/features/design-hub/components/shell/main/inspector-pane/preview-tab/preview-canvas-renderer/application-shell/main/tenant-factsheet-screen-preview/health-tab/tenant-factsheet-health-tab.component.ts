import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule } from 'primeng/paginator';
import { TagModule } from 'primeng/tag';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { HealthCheck, HealthStatus, SectionWithSearchAndViewModeConfig } from '../../../../../../../../../../models/preview-content.types';

@Component({
  selector: 'app-tenant-factsheet-health-tab',
  standalone: true,
  imports: [FormsModule, ButtonModule, InputTextModule, PaginatorModule, TagModule, ToggleButtonModule],
  templateUrl: './tenant-factsheet-health-tab.component.html',
  styleUrl: './tenant-factsheet-health-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetHealthTabComponent {
  readonly section = input.required<SectionWithSearchAndViewModeConfig | null>();
  readonly items = input.required<readonly HealthCheck[]>();
  readonly searchValue = input.required<string>();
  readonly filtersVisible = input.required<boolean>();
  readonly viewMode = input.required<'table' | 'grid'>();

  readonly searchChange = output<string>();
  readonly toggleFilters = output<void>();
  readonly viewModeChange = output<'table' | 'grid'>();

  onSearchInput(value: string): void { this.searchChange.emit(value); }
  onToggleFilters(): void { this.toggleFilters.emit(); }
  onViewModeChange(tableMode: boolean): void { this.viewModeChange.emit(tableMode ? 'table' : 'grid'); }

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
}
