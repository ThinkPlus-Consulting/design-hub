import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputTextModule } from 'primeng/inputtext';
import { PaginatorModule } from 'primeng/paginator';
import { TenantUser, UsersSectionContentConfig } from '../../../../../../../../../../models/preview-content.types';
import { TenantFactsheetUsersDataStateComponent } from './data-state/tenant-factsheet-users-data-state.component';
import { TenantFactsheetUsersEmptyStateComponent } from './empty-state/tenant-factsheet-users-empty-state.component';
import { TenantFactsheetUsersLoadingStateComponent } from './loading-state/tenant-factsheet-users-loading-state.component';
import { TenantFactsheetUsersTabBindings } from './tenant-factsheet-users-tab.bindings';

@Component({
  selector: 'app-tenant-factsheet-users-tab',
  standalone: true,
  imports: [
    FormsModule,
    ButtonModule,
    InputTextModule,
    PaginatorModule,
    TenantFactsheetUsersLoadingStateComponent,
    TenantFactsheetUsersEmptyStateComponent,
    TenantFactsheetUsersDataStateComponent,
  ],
  templateUrl: './tenant-factsheet-users-tab.component.html',
  styleUrl: './tenant-factsheet-users-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetUsersTabComponent {
  readonly bindings = input.required<TenantFactsheetUsersTabBindings>();
  readonly section = input.required<UsersSectionContentConfig | null>();
  readonly loading = input(false);
  readonly users = input.required<readonly TenantUser[]>();
  readonly searchValue = input.required<string>();
  readonly filtersVisible = input.required<boolean>();

  readonly searchChange = output<string>();
  readonly toggleFilters = output<void>();
  readonly openUserFactsheet = output<TenantUser>();

  readonly filteredUsers = computed(() => {
    const query = this.searchValue().trim().toLowerCase();
    if (!query) {
      return this.users();
    }

    return this.users().filter((user) =>
      [user.name, user.email, user.role, user.status].some((value) =>
        value.toLowerCase().includes(query),
      ),
    );
  });

  readonly showLoadingState = computed(() => this.loading());
  readonly showEmptyState = computed(() => !this.loading() && this.filteredUsers().length === 0);
  readonly showDataState = computed(() => !this.loading() && this.filteredUsers().length > 0);
  readonly showPagination = computed(() => this.showDataState());
  readonly loadingSkeletonRowCount = computed(() => this.section()?.previewState?.skeletonRowCount ?? 5);
  readonly emptyStateTitle = computed(() => this.section()?.emptyState.title ?? 'No users found');
  readonly emptyStateMessage = computed(() => {
    const emptyState = this.section()?.emptyState;
    if (!emptyState) {
      return 'No users are available for this tenant.';
    }

    return this.users().length === 0
      ? emptyState.noUsersMessage
      : emptyState.noResultsMessage;
  });

  onSearchInput(value: string): void {
    this.searchChange.emit(value);
  }

  onToggleFilters(): void {
    this.toggleFilters.emit();
  }

  onOpenUserFactsheet(user: TenantUser): void {
    this.openUserFactsheet.emit(user);
  }
}
