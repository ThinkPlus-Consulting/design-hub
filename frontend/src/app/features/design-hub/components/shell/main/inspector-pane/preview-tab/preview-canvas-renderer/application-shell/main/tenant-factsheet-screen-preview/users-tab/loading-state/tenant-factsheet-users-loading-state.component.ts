import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { SkeletonModule } from 'primeng/skeleton';
import { TenantFactsheetUsersLoadingStateBindings } from '../tenant-factsheet-users-tab.bindings';

@Component({
  selector: 'app-tenant-factsheet-users-loading-state',
  standalone: true,
  imports: [SkeletonModule],
  templateUrl: './tenant-factsheet-users-loading-state.component.html',
  styleUrl: './tenant-factsheet-users-loading-state.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantFactsheetUsersLoadingStateComponent {
  readonly bindings = input.required<TenantFactsheetUsersLoadingStateBindings>();
  readonly rowCount = input(5);

  readonly rowIndices = computed(() =>
    Array.from({ length: Math.max(1, this.rowCount()) }, (_, index) => index),
  );
}
