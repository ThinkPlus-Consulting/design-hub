import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { DesignHubStateService } from '../../services/design-hub-state.service';
import { DisplayOptions } from '../../../../models';
import { LocaleService } from '../../../../core/i18n/locale.service';

@Component({
  selector: 'app-screen-sidebar',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="sidebar" data-testid="sidebar-root">
      <!-- Stats summary -->
      <div class="sidebar__stats" data-testid="stats-summary">
        <div class="sidebar__header">
          <div>
            <div class="sidebar__locale-label">{{ locale.t('locale.label') }}</div>
            <h3 class="sidebar__title" data-testid="sidebar-title">{{ locale.t('sidebar.title') }}</h3>
          </div>
          <div class="sidebar__locale-switch" data-testid="locale-switch">
            @for (option of locale.localeOptions; track option.value) {
              <button
                type="button"
                class="sidebar__locale-button"
                [class.sidebar__locale-button--active]="locale.isActive(option.value)"
                [attr.aria-pressed]="locale.isActive(option.value)"
                [attr.data-testid]="'locale-option-' + option.value"
                (click)="locale.setLocale(option.value)"
              >
                {{ locale.t(option.labelKey) }}
              </button>
            }
          </div>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-label">{{ locale.t('sidebar.stats.screens') }}</span>
          <span class="sidebar__stat-value" data-testid="stat-total">{{ stats().totalScreens }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-dot sidebar__stat-dot--complete"></span>
          <span class="sidebar__stat-label">{{ locale.t('sidebar.stats.complete') }}</span>
          <span class="sidebar__stat-value" data-testid="stat-complete">{{ stats().completeCount }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-dot sidebar__stat-dot--specified"></span>
          <span class="sidebar__stat-label">{{ locale.t('sidebar.stats.specified') }}</span>
          <span class="sidebar__stat-value" data-testid="stat-specified">{{ stats().specifiedCount }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-dot sidebar__stat-dot--not-started"></span>
          <span class="sidebar__stat-label">{{ locale.t('sidebar.stats.notStarted') }}</span>
          <span class="sidebar__stat-value" data-testid="stat-not-started">{{ stats().notStartedCount }}</span>
        </div>
        <div class="sidebar__stat-row">
          <span class="sidebar__stat-label">{{ locale.t('sidebar.stats.gaps') }}</span>
          <span class="sidebar__stat-value sidebar__stat-value--gap" data-testid="stat-gaps">{{ stats().totalGaps }}</span>
        </div>
        <div class="sidebar__coverage" data-testid="stat-coverage">
          <div class="sidebar__coverage-bar">
            <div
              class="sidebar__coverage-fill"
              [style.width.%]="stats().coveragePercent"
            ></div>
          </div>
          <span class="sidebar__coverage-label">{{ locale.format('sidebar.stats.coverage', { percent: stats().coveragePercent }) }}</span>
        </div>
      </div>

      <!-- Module filter -->
      <div class="sidebar__section" data-testid="module-filter">
        <h4 class="sidebar__section-title">{{ locale.t('sidebar.section.module') }}</h4>
        <div class="sidebar__button-group" data-testid="module-select">
          @for (option of moduleButtonOptions(); track option.value) {
            <button
              type="button"
              class="sidebar__filter-button"
              [class.sidebar__filter-button--active]="state.selectedModule() === option.value"
              [attr.aria-pressed]="state.selectedModule() === option.value"
              [attr.data-testid]="'module-option-' + option.value"
              (click)="state.setModuleFilter(option.value)"
            >
              {{ option.label }}
            </button>
          }
        </div>
      </div>

      <!-- Status filter -->
      <div class="sidebar__section" data-testid="status-filter">
        <h4 class="sidebar__section-title">{{ locale.t('sidebar.section.status') }}</h4>
        <div class="sidebar__button-group" data-testid="status-select">
          @for (option of statusOptions(); track option.value) {
            <button
              type="button"
              class="sidebar__filter-button"
              [class.sidebar__filter-button--active]="state.selectedDesignStatus() === option.value"
              [attr.aria-pressed]="state.selectedDesignStatus() === option.value"
              [attr.data-testid]="'status-option-' + option.value"
              (click)="state.setDesignStatusFilter(option.value)"
            >
              {{ option.label }}
            </button>
          }
        </div>
      </div>

      <div class="sidebar__section" data-testid="role-filter">
        <h4 class="sidebar__section-title">Access role</h4>
        <label class="sidebar__select-field">
          <span class="sidebar__select-label">Active role</span>
          <select
            class="sidebar__select-input"
            [value]="state.selectedRoleKey() ?? ''"
            data-testid="role-select"
            (change)="state.setSelectedRole(($any($event.target)).value || null)"
          >
            <option value="">Open access</option>
            @for (role of roleOptions(); track role.roleKey) {
              <option [value]="role.roleKey">
                {{ role.displayName }}@if (role.copyRestricted) { · Copy Restricted }
              </option>
            }
          </select>
        </label>
        @if (state.selectedRole(); as role) {
          <p class="sidebar__role-hint" data-testid="role-copy-policy">
            @if (role.copyRestricted) {
              Copy and text selection are blocked for {{ role.displayName }}.
            } @else {
              Copy is allowed for {{ role.displayName }}.
            }
          </p>
        }
      </div>

      <!-- Search -->
      <div class="sidebar__section" data-testid="search-section">
        <h4 class="sidebar__section-title">{{ locale.t('sidebar.section.search') }}</h4>
        <label class="sidebar__search-field">
          <span class="sidebar__search-icon" aria-hidden="true" data-testid="sidebar-search-icon">{{ locale.t('sidebar.search.icon') }}</span>
          <input
            type="text"
            [placeholder]="locale.t('sidebar.search.placeholder')"
            [value]="state.searchTerm()"
            (input)="state.searchTerm.set(($any($event.target)).value)"
            data-testid="search-input"
            class="sidebar__search-input"
          />
        </label>
      </div>

      <!-- Display options -->
      <div class="sidebar__section" data-testid="display-options">
        <h4 class="sidebar__section-title">{{ locale.t('sidebar.section.display') }}</h4>
        @for (opt of displayOptionsList(); track opt.key) {
          <label class="sidebar__checkbox-row" [for]="opt.key">
            <input
              type="checkbox"
              class="sidebar__checkbox-input"
              [id]="opt.key"
              [checked]="state.displayOptions()[opt.key]"
              [attr.data-testid]="'display-' + opt.key"
              (change)="state.toggleDisplayOption(opt.key)"
            />
            <span class="sidebar__checkbox-label">{{ opt.label }}</span>
          </label>
        }
      </div>

      <!-- Screen list -->
      <div class="sidebar__section sidebar__screen-list" data-testid="screen-list">
        <h4 class="sidebar__section-title">{{ locale.format('sidebar.section.screens', { count: state.filteredScreens().length }) }}</h4>
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
          <p class="sidebar__empty" data-testid="screen-list-empty">{{ locale.t('sidebar.empty.noScreens') }}</p>
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

    .sidebar__header {
      display: flex;
      align-items: flex-start;
      justify-content: space-between;
      gap: var(--tp-space-3);
      margin-bottom: var(--tp-space-3);
    }

    .sidebar__locale-label {
      font-size: 0.65rem;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
      margin-bottom: var(--tp-space-1);
    }

    .sidebar__title {
      font-size: 1.25rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
      margin-bottom: 0;
    }

    .sidebar__stats {
      padding-bottom: var(--tp-space-4);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
    }

    .sidebar__locale-switch {
      display: inline-flex;
      flex-wrap: wrap;
      justify-content: flex-end;
      gap: var(--tp-space-1);
      max-width: 50%;
    }

    .sidebar__locale-button {
      min-height: calc(var(--tp-touch-target-min-size) - 8px);
      padding: 0.3rem 0.6rem;
      border: 1px solid color-mix(in srgb, var(--tp-border) 28%, transparent);
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-white) 92%, transparent);
      color: var(--tp-text-muted);
      font-size: 0.72rem;
      font-weight: 700;
      cursor: pointer;
      transition: border-color 0.15s ease, background 0.15s ease, color 0.15s ease;
    }

    .sidebar__locale-button:hover {
      border-color: color-mix(in srgb, var(--tp-primary) 28%, transparent);
      color: var(--tp-text-dark);
    }

    .sidebar__locale-button--active {
      border-color: color-mix(in srgb, var(--tp-primary) 36%, transparent);
      background: var(--tp-primary-bg);
      color: var(--tp-primary-dark);
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
      background: color-mix(in srgb, var(--tp-border) 15%, transparent);
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

    .sidebar__select-field {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
    }

    .sidebar__select-label {
      font-size: 0.78rem;
      font-weight: 600;
      color: var(--tp-text-muted);
    }

    .sidebar__select-input {
      min-height: var(--tp-touch-target-min-size);
      border: 1px solid color-mix(in srgb, var(--tp-border) 28%, transparent);
      border-radius: 0.8rem;
      background: var(--tp-surface);
      color: var(--tp-text-dark);
      padding: 0.7rem 0.9rem;
      font: inherit;
    }

    .sidebar__role-hint {
      margin: var(--tp-space-2) 0 0;
      font-size: 0.76rem;
      line-height: 1.5;
      color: var(--tp-text-muted);
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

    .sidebar__screen-list {
      flex: 1;
      overflow-y: auto;
    }

    .sidebar__button-group {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
    }

    .sidebar__filter-button {
      min-height: var(--tp-touch-target-min-size);
      padding: 0.35rem 0.65rem;
      border: 1px solid color-mix(in srgb, var(--tp-border) 28%, transparent);
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-white) 92%, transparent);
      color: var(--tp-text-muted);
      font-size: 0.74rem;
      font-weight: 600;
      cursor: pointer;
      transition: border-color 0.15s ease, background 0.15s ease, color 0.15s ease;
    }

    .sidebar__filter-button:hover {
      border-color: color-mix(in srgb, var(--tp-primary) 28%, transparent);
      color: var(--tp-text-dark);
    }

    .sidebar__filter-button--active {
      border-color: color-mix(in srgb, var(--tp-primary) 36%, transparent);
      background: var(--tp-primary-bg);
      color: var(--tp-primary-dark);
    }

    .sidebar__filter-button:focus-visible,
    .sidebar__checkbox-input:focus-visible,
    .sidebar__search-input:focus-visible {
      outline: none;
      box-shadow: var(--tp-focus-ring);
    }

    .sidebar__search-field {
      position: relative;
      display: block;
    }

    .sidebar__search-icon {
      position: absolute;
      inset-inline-start: var(--tp-space-3);
      top: 50%;
      transform: translateY(-50%);
      font-size: 0.67rem;
      font-weight: 700;
      letter-spacing: 0.06em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
      pointer-events: none;
    }

    .sidebar__search-input {
      width: 100%;
      min-height: var(--tp-touch-target-min-size);
      padding-block: 0.7rem;
      padding-inline-start: 4.7rem;
      padding-inline-end: var(--tp-space-3);
      border: 1px solid color-mix(in srgb, var(--tp-border) 24%, transparent);
      border-radius: 0.9rem;
      background: color-mix(in srgb, var(--tp-white) 94%, transparent);
      color: var(--tp-text);
      font-size: 0.82rem;
    }

    .sidebar__checkbox-row {
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      padding: var(--tp-space-1) 0;
      cursor: pointer;
    }

    .sidebar__checkbox-input {
      width: 1rem;
      height: 1rem;
      margin: 0;
      accent-color: var(--tp-primary);
    }

    .sidebar__checkbox-label {
      font-size: 0.8rem;
      color: var(--tp-text);
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
      text-align: start;
      transition: background 0.15s ease;
      font-family: inherit;

      &:hover {
        background: color-mix(in srgb, var(--tp-primary) 17%, transparent);
      }

      &--selected {
        background: color-mix(in srgb, var(--tp-primary) 12%, transparent);
        box-shadow: inset 0 0 0 1px color-mix(in srgb, var(--tp-primary) 25%, transparent);
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
  `],
})
export class ScreenSidebarComponent {
  readonly state = inject(DesignHubStateService);
  readonly locale = inject(LocaleService);

  readonly stats = this.state.computedStats;
  readonly roleOptions = computed(() =>
    [...this.state.roles()].sort((left, right) => {
      const leftSort = left.sortOrder ?? Number.MAX_SAFE_INTEGER;
      const rightSort = right.sortOrder ?? Number.MAX_SAFE_INTEGER;
      return leftSort - rightSort || left.displayName.localeCompare(right.displayName);
    })
  );

  readonly statusOptions = computed(() => [
    { label: this.locale.t('filters.all'), value: 'all' },
    { label: this.locale.t('sidebar.stats.complete'), value: 'COMPLETE' },
    { label: this.locale.t('sidebar.stats.specified'), value: 'SPECIFIED' },
    { label: this.locale.t('sidebar.stats.notStarted'), value: 'NOT_STARTED' },
  ]);

  readonly displayOptionsList = computed<{ key: keyof DisplayOptions; label: string }[]>(() => [
    { key: 'showTransitions', label: this.locale.t('display.transitions') },
    { key: 'showGaps', label: this.locale.t('display.gaps') },
    { key: 'showErrorCodes', label: this.locale.t('display.errorCodes') },
    { key: 'showDialogs', label: this.locale.t('display.dialogs') },
    { key: 'showEmptyStates', label: this.locale.t('display.emptyStates') },
  ]);

  readonly moduleButtonOptions = computed(() =>
    this.state.modules().map((moduleName) => ({
      label: moduleName === 'all' ? this.locale.t('filters.all') : moduleName,
      value: moduleName,
    }))
  );
}
