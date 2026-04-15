import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { ShellBackgroundConfig, SystemShellGraphNode } from '../../../../../../../models/graph.types';
import { PreviewIdentityBound } from '../../../../../../../utils/preview-identity-bound';
import { LoginScreenPreviewComponent } from './login-screen-preview/login-screen-preview.component';
import { MfaScreenPreviewComponent } from './mfa-screen-preview/mfa-screen-preview.component';
import { TenantNotFoundScreenPreviewComponent } from './tenant-not-found-screen-preview/tenant-not-found-screen-preview.component';

@Component({
  selector: 'app-login-shell-preview',
  standalone: true,
  imports: [
    LoginScreenPreviewComponent,
    MfaScreenPreviewComponent,
    TenantNotFoundScreenPreviewComponent,
  ],
  templateUrl: './login-shell-preview.component.html',
  styleUrl: './login-shell-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class LoginShellPreviewComponent extends PreviewIdentityBound {
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  readonly shellObjectId = input<string | null>(null);
  readonly activeScreenObjectId = input<string | null>(null);

  private readonly shellNode = computed(() => this.state.nodeById(this.shellObjectId()));
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

  protected slotNode(slot: string | null | undefined): SystemShellGraphNode | null {
    const normalizedSlot = slot?.trim();
    if (normalizedSlot === 'shell') {
      return this.shellNode();
    }
    return null;
  }

  protected selectedPreviewGuid(): string | null {
    return this.selectedGuid();
  }
}
