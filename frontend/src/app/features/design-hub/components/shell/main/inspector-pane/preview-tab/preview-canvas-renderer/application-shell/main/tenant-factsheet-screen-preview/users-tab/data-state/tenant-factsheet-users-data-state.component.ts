import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { TableModule } from 'primeng/table';
import { TagModule } from 'primeng/tag';
import { TenantFactsheetUsersDataStateBindings } from '../tenant-factsheet-users-tab.bindings';

export interface TenantFactsheetUsersDataRecord {
  readonly id: string;
  readonly name: string;
  readonly email: string;
  readonly role: string;
  readonly status: 'Active' | 'Invited' | 'Disabled';
  readonly lastActive: string;
}

export interface TenantFactsheetUsersTableColumns {
  readonly name: string;
  readonly email: string;
  readonly role: string;
  readonly status: string;
  readonly lastActive: string;
}

@Component({
  selector: 'app-tenant-factsheet-users-data-state',
  standalone: true,
  imports: [TableModule, TagModule],
  templateUrl: './tenant-factsheet-users-data-state.component.html',
  styleUrl: './tenant-factsheet-users-data-state.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetUsersDataStateComponent {
  readonly bindings = input.required<TenantFactsheetUsersDataStateBindings>();
  readonly columns = input.required<TenantFactsheetUsersTableColumns | null>();
  readonly users = input.required<readonly TenantFactsheetUsersDataRecord[]>();
  readonly openUserFactsheet = output<TenantFactsheetUsersDataRecord>();

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

  onOpenUserFactsheet(event: MouseEvent, user: TenantFactsheetUsersDataRecord): void {
    event.preventDefault();
    event.stopPropagation();
    this.openUserFactsheet.emit(user);
  }
}
