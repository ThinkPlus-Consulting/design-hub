import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule } from 'primeng/paginator';
import { TagModule } from 'primeng/tag';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { Integration, SectionWithSearchAndViewModeConfig } from '../../../../../../../../../../models/preview-content.types';

@Component({
  selector: 'app-tenant-factsheet-integrations-tab',
  standalone: true,
  imports: [FormsModule, ButtonModule, InputTextModule, PaginatorModule, TagModule, ToggleButtonModule],
  templateUrl: './tenant-factsheet-integrations-tab.component.html',
  styleUrl: './tenant-factsheet-integrations-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetIntegrationsTabComponent {
  readonly section = input.required<(SectionWithSearchAndViewModeConfig & { readonly enabledLabel: string; readonly disabledLabel: string }) | null>();
  readonly items = input.required<readonly Integration[]>();
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
