import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { DesignHubStateService } from '../../../../../../../../services/design-hub-state.service';
import { LoginScreenPreviewComponent } from './login-screen-preview/login-screen-preview.component';
import { MfaScreenPreviewComponent } from './mfa-screen-preview/mfa-screen-preview.component';
import { TenantNotFoundScreenPreviewComponent } from './tenant-not-found-screen-preview/tenant-not-found-screen-preview.component';

@Component({
  selector: 'app-login-shell-main-preview',
  standalone: true,
  imports: [
    LoginScreenPreviewComponent,
    MfaScreenPreviewComponent,
    TenantNotFoundScreenPreviewComponent,
  ],
  template: `
    <div class="login-shell-main-slot">
      @if (activeScreenRenderer() === 'login') {
        <app-login-screen-preview
          [selectedGuid]="selectedGuid()"
          [screenObjectId]="activeScreenObjectId()"
        />
      } @else if (activeScreenRenderer() === 'mfa') {
        <app-mfa-screen-preview
          [selectedGuid]="selectedGuid()"
          [screenObjectId]="activeScreenObjectId()"
        />
      } @else if (activeScreenRenderer() === 'tenant-not-found') {
        <app-tenant-not-found-screen-preview
          [selectedGuid]="selectedGuid()"
          [screenObjectId]="activeScreenObjectId()"
        />
      }
    </div>
  `,
  styles: [`
    :host {
      display: block;
      width: 100%;
      height: 100%;
      min-height: 100%;
    }

    .login-shell-main-slot {
      width: 100%;
      height: 100%;
      min-height: 100%;
    }
  `],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginShellMainPreviewComponent {
  private readonly state = inject(DesignHubStateService);

  readonly selectedGuid = input<string | null>(null);
  readonly activeScreenObjectId = input<string | null>(null);

  private readonly screenNode = computed(() => this.state.nodeById(this.activeScreenObjectId()));

  readonly activeScreenRenderer = computed(() => {
    const normalizedName = this.screenNode()?.name?.trim().toLowerCase() ?? '';
    switch (normalizedName) {
      case 'login':
      case 'login screen':
        return 'login';
      case 'mfa':
      case 'mfa screen':
      case 'verification':
        return 'mfa';
      case 'tenant not found':
        return 'tenant-not-found';
      default:
        return 'unknown';
    }
  });
}
