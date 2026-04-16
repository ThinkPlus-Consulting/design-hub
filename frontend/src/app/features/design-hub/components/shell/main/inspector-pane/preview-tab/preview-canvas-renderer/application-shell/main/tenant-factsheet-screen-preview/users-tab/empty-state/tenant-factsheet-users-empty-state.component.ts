import { ChangeDetectionStrategy, Component, input } from '@angular/core';
import { TenantFactsheetUsersEmptyStateBindings } from '../tenant-factsheet-users-tab.bindings';

@Component({
  selector: 'app-tenant-factsheet-users-empty-state',
  standalone: true,
  templateUrl: './tenant-factsheet-users-empty-state.component.html',
  styleUrl: './tenant-factsheet-users-empty-state.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetUsersEmptyStateComponent {
  readonly bindings = input.required<TenantFactsheetUsersEmptyStateBindings>();
  readonly title = input.required<string>();
  readonly message = input.required<string>();
}
