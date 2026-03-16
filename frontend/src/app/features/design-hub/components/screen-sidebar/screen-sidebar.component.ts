import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { SelectButton } from 'primeng/selectbutton';
import { Checkbox } from 'primeng/checkbox';
import { InputText } from 'primeng/inputtext';
import { IconField } from 'primeng/iconfield';
import { InputIcon } from 'primeng/inputicon';
import { DesignHubStateService } from '../../services/design-hub-state.service';
import { DisplayOptions } from '../../../../models';

@Component({
  selector: 'app-screen-sidebar',
  standalone: true,
  imports: [FormsModule, SelectButton, Checkbox, InputText, IconField, InputIcon],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="sidebar" data-testid="sidebar-root">
      <!-- Stats summary -->
      <div class="sidebar__stats" data-testid="stats-summary">
        <h3 class="sidebar__title">Design Hub</h3>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-label">Screens</span>
          <span class="sidebar__stat-value" data-testid="stat-total">{{ stats().totalScreens }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-dot sidebar__stat-dot--complete"></span>
          <span class="sidebar__stat-label">Complete</span>
          <span class="sidebar__stat-value" data-testid="stat-complete">{{ stats().completeCount }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-dot sidebar__stat-dot--specified"></span>
          <span class="sidebar__stat-label">Specified</span>
          <span class="sidebar__stat-value" data-testid="stat-specified">{{ stats().specifiedCount }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-dot sidebar__stat-dot--not-started"></span>
          <span class="sidebar__stat-label">Not Started</span>
          <span class="sidebar__stat-value" data-testid="stat-not-started">{{ stats().notStartedCount }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-label">Gaps</span>
          <span class="sidebar__stat-value sidebar__stat-value--gap" data-testid="stat-gaps">{{ stats().totalGaps }}</span>
        </div>
        <div class="sidebar__coverage" data-testid="stat-coverage">
          <div class="sidebar__coverage-bar">
            <div
              class="sidebar__coverage-fill"
              [style.width.%]="stats().coveragePercent"
            ></div>
          </div>
          <span class="sidebar__coverage-label">{{ stats().coveragePercent }}% coverage</span>
        </div>
      </div>

      <!-- Module filter -->
      <div class="sidebar__section" data-testid="module-filter">
        <h4 class="sidebar__section-title">Module</h4>
        <p-selectbutton
          [options]="moduleButtonOptions()"
          [ngModel]="state.selectedModule()"
          (ngModelChange)="state.setModuleFilter($event)"
          [allowEmpty]="false"
          optionLabel="label"
          optionValue="value"
          size="small"
          data-testid="module-select"
        />
      </div>

      <!-- Status filter -->
      <div class="sidebar__section" data-testid="status-filter">
        <h4 class="sidebar__section-title">Design Status</h4>
        <p-selectbutton
          [options]="statusOptions"
          [ngModel]="state.selectedDesignStatus()"
          (ngModelChange)="state.setDesignStatusFilter($event)"
          [allowEmpty]="false"
          optionLabel="label"
          optionValue="value"
          size="small"
          data-testid="status-select"
        />
      </div>

      <!-- Search -->
      <div class="sidebar__section" data-testid="search-section">
        <h4 class="sidebar__section-title">Search</h4>
        <p-iconfield>
          <p-inputicon styleClass="pi pi-search" />
          <input
            type="text"
            pInputText
            placeholder="Search screens..."
            [ngModel]="state.searchTerm()"
            (ngModelChange)="state.searchTerm.set($event)"
            data-testid="search-input"
            class="sidebar__search-input"
          />
        </p-iconfield>
      </div>

      <!-- Display options -->
      <div class="sidebar__section" data-testid="display-options">
        <h4 class="sidebar__section-title">Display</h4>
        @for (opt of displayOptionsList; track opt.key) {
          <div class="sidebar__checkbox-row">
            <p-checkbox
              [binary]="true"
              [ngModel]="state.displayOptions()[opt.key]"
              (ngModelChange)="state.toggleDisplayOption(opt.key)"
              [inputId]="opt.key"
              [attr.data-testid]="'display-' + opt.key"
            />
            <label [for]="opt.key" class="sidebar__checkbox-label">{{ opt.label }}</label>
          </div>
        }
      </div>

      <!-- Screen list -->
      <div class="sidebar__section sidebar__screen-list" data-testid="screen-list">
        <h4 class="sidebar__section-title">Screens ({{ state.filteredScreens().length }})</h4>
        @for (screen of state.filteredScreens(); track screen.surfaceId) {
          <button
            class="sidebar__screen-item"
            [class.sidebar__screen-item--selected]="screen.surfaceId === state.selectedScreenId()"
            [attr.data-testid]="'screen-' + screen.surfaceId"
            [attr.data-module]="screen.module"
            [attr.data-design-status]="screen.designStatus"
            (click)="state.selectScreen(screen.surfaceId)"
          >
            <span
              class="sidebar__screen-dot"
              [attr.data-testid]="'screen-dot-' + screen.surfaceId"
              [class.sidebar__screen-dot--complete]="screen.designStatus === 'COMPLETE'"
              [class.sidebar__screen-dot--specified]="screen.designStatus === 'SPECIFIED'"
              [class.sidebar__screen-dot--not-started]="screen.designStatus === 'NOT_STARTED'"
            ></span>
            <div class="sidebar__screen-info">
              <span class="sidebar__screen-label" [attr.data-testid]="'screen-label-' + screen.surfaceId">{{ screen.label }}</span>
              <span class="sidebar__screen-id" [attr.data-testid]="'screen-id-' + screen.surfaceId">{{ screen.surfaceId }}</span>
            </div>
            <span class="sidebar__screen-module" [attr.data-testid]="'screen-module-' + screen.surfaceId">{{ screen.module }}</span>
          </button>
        } @empty {
          <p class="sidebar__empty" data-testid="screen-list-empty">No screens match filters</p>
        }
      </div>
    </div>
  `,
  styles: [`
    .sidebar {
      padding: var(--tp-space-4);
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
    }

    .sidebar__title {
      font-size: 1.25rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
      margin-bottom: var(--tp-space-3);
    }

    .sidebar__stats {
      padding-bottom: var(--tp-space-4);
      border-bottom: 1px solid rgba(152, 133, 97, 0.18);
    }

    .sidebar__stat-row {
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      padding: var(--tp-space-1) 0;
    }

    .sidebar__stat-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      flex-shrink: 0;

      &--complete { background: var(--dh-complete); }
      &--specified { background: var(--dh-specified); }
      &--not-started { background: var(--dh-not-started); }
    }

    .sidebar__stat-label {
      flex: 1;
      font-size: 0.8rem;
      color: var(--tp-text);
    }

    .sidebar__stat-value {
      font-weight: 700;
      font-size: 0.85rem;

      &--gap { color: var(--dh-gap); }
    }

    .sidebar__coverage {
      margin-top: var(--tp-space-2);
    }

    .sidebar__coverage-bar {
      height: 6px;
      border-radius: 3px;
      background: rgba(152, 133, 97, 0.15);
      overflow: hidden;
    }

    .sidebar__coverage-fill {
      height: 100%;
      border-radius: 3px;
      background: var(--dh-complete);
      transition: width 0.3s ease;
    }

    .sidebar__coverage-label {
      font-size: 0.72rem;
      color: var(--tp-text-muted);
      margin-top: var(--tp-space-1);
      display: block;
    }

    .sidebar__section {
      &-title {
        font-size: 0.75rem;
        font-weight: 600;
        text-transform: uppercase;
        letter-spacing: 0.05em;
        color: var(--tp-text-muted);
        margin-bottom: var(--tp-space-2);
      }
    }

    .sidebar__search-input {
      width: 100%;
      font-size: 0.8rem;
    }

    .sidebar__checkbox-row {
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      padding: var(--tp-space-1) 0;
    }

    .sidebar__checkbox-label {
      font-size: 0.8rem;
      cursor: pointer;
    }

    .sidebar__screen-list {
      flex: 1;
      overflow-y: auto;
    }

    .sidebar__screen-item {
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      width: 100%;
      padding: var(--tp-space-2) var(--tp-space-3);
      border: none;
      border-radius: 0.72rem;
      background: transparent;
      cursor: pointer;
      text-align: left;
      transition: background 0.15s ease;
      font-family: inherit;

      &:hover {
        background: rgba(66, 129, 119, 0.17);
      }

      &--selected {
        background: rgba(66, 129, 119, 0.12);
        box-shadow: inset 0 0 0 1px rgba(66, 129, 119, 0.25);
      }
    }

    .sidebar__screen-dot {
      width: 8px;
      height: 8px;
      border-radius: 50%;
      flex-shrink: 0;

      &--complete { background: var(--dh-complete); }
      &--specified { background: var(--dh-specified); }
      &--not-started { background: var(--dh-not-started); }
    }

    .sidebar__screen-info {
      flex: 1;
      min-width: 0;
      display: flex;
      flex-direction: column;
    }

    .sidebar__screen-label {
      font-size: 0.82rem;
      font-weight: 500;
      color: var(--tp-text);
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .sidebar__screen-id {
      font-size: 0.68rem;
      color: var(--tp-text-muted);
      opacity: 0.7;
      font-family: monospace;
    }

    .sidebar__screen-module {
      font-size: 0.68rem;
      color: var(--tp-text-muted);
      opacity: 0.7;
      flex-shrink: 0;
    }

    .sidebar__empty {
      font-size: 0.8rem;
      color: var(--tp-text-muted);
      font-style: italic;
      padding: var(--tp-space-4);
      text-align: center;
    }

    :host ::ng-deep .p-selectbutton {
      display: flex;
      flex-wrap: wrap;
      gap: 2px;
    }

    :host ::ng-deep .p-selectbutton .p-togglebutton {
      font-size: 0.72rem;
      padding: 0.3rem 0.5rem;
    }

    :host ::ng-deep .p-iconfield {
      width: 100%;
    }
  `],
})
export class ScreenSidebarComponent {
  readonly state = inject(DesignHubStateService);

  readonly stats = this.state.computedStats;

  readonly statusOptions: { label: string; value: string }[] = [
    { label: 'All', value: 'all' },
    { label: 'Complete', value: 'COMPLETE' },
    { label: 'Specified', value: 'SPECIFIED' },
    { label: 'Not Started', value: 'NOT_STARTED' },
  ];

  readonly displayOptionsList: { key: keyof DisplayOptions; label: string }[] = [
    { key: 'showTransitions', label: 'Transitions' },
    { key: 'showGaps', label: 'Gaps' },
    { key: 'showErrorCodes', label: 'Error Codes' },
    { key: 'showDialogs', label: 'Dialogs' },
    { key: 'showEmptyStates', label: 'Empty States' },
  ];

  readonly moduleButtonOptions = computed(() =>
    this.state.modules().map((m) => ({ label: m === 'all' ? 'All' : m, value: m }))
  );
}
