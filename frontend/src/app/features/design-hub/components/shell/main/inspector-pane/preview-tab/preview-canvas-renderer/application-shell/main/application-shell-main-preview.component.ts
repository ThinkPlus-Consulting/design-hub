import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { DesignHubStateService } from '../../../../../../../../services/design-hub-state.service';
import { TenantFactsheetScreenPreviewComponent } from './tenant-factsheet-screen-preview/tenant-factsheet-screen-preview.component';
import { TenantListScreenPreviewComponent } from './tenant-list-screen-preview/tenant-list-screen-preview.component';
import { UserFactsheetScreenPreviewComponent } from './user-factsheet-screen-preview/user-factsheet-screen-preview.component';

@Component({
  selector: 'app-application-shell-main-preview',
  standalone: true,
  imports: [
    TenantFactsheetScreenPreviewComponent,
    TenantListScreenPreviewComponent,
    UserFactsheetScreenPreviewComponent,
  ],
  templateUrl: './application-shell-main-preview.component.html',
  styleUrl: './application-shell-main-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ApplicationShellMainPreviewComponent {
  private readonly state = inject(DesignHubStateService);

  readonly selectedGuid = input<string | null>(null);
  readonly activeScreenObjectId = input<string | null>(null);

  private readonly screenNode = computed(() => this.state.nodeById(this.activeScreenObjectId()));

  readonly activeScreenRenderer = computed(() => {
    const normalizedName = this.screenNode()?.name?.trim().toLowerCase() ?? '';
    switch (normalizedName) {
      case 'view tenant list':
        return 'tenant-list';
      case 'tenant fact sheet':
        return 'tenant-factsheet';
      case 'view user fact sheet':
        return 'user-factsheet';
      default:
        return 'unknown';
    }
  });
}
