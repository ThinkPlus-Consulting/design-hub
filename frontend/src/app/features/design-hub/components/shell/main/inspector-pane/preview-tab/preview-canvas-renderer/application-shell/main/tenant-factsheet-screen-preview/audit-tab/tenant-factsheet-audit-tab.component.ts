import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule } from 'primeng/paginator';
import { TableModule } from 'primeng/table';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { AuditEntry, AuditSectionContentConfig } from '../../../../../../../../../../models/preview-content.types';

@Component({
  selector: 'app-tenant-factsheet-audit-tab',
  standalone: true,
  imports: [FormsModule, ButtonModule, InputTextModule, PaginatorModule, TableModule, ToggleButtonModule],
  templateUrl: './tenant-factsheet-audit-tab.component.html',
  styleUrl: './tenant-factsheet-audit-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetAuditTabComponent {
  readonly section = input.required<AuditSectionContentConfig | null>();
  readonly items = input.required<readonly AuditEntry[]>();
  readonly searchValue = input.required<string>();
  readonly filtersVisible = input.required<boolean>();
  readonly viewMode = input.required<'table' | 'grid'>();

  readonly searchChange = output<string>();
  readonly toggleFilters = output<void>();
  readonly viewModeChange = output<'table' | 'grid'>();

  onSearchInput(value: string): void { this.searchChange.emit(value); }
  onToggleFilters(): void { this.toggleFilters.emit(); }
  onViewModeChange(tableMode: boolean): void { this.viewModeChange.emit(tableMode ? 'table' : 'grid'); }
}
