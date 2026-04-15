import { ChangeDetectionStrategy, Component, computed, inject, input } from '@angular/core';
import { ShellBackgroundConfig } from '../../../../../../models/graph.types';
import { DesignHubStateService } from '../../../../../../services/design-hub-state.service';
import { ApplicationShellPreviewComponent } from './application-shell/application-shell-preview.component';
import { LoginShellPreviewComponent } from './login-shell/login-shell-preview.component';

@Component({
  selector: 'app-preview-canvas',
  standalone: true,
  imports: [ApplicationShellPreviewComponent, LoginShellPreviewComponent],
  templateUrl: './preview-canvas.component.html',
  styleUrl: './preview-canvas.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PreviewCanvasComponent {
  private readonly state = inject(DesignHubStateService);

  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  readonly shellObjectId = input<string | null>(null);
  readonly activeScreenObjectId = input<string | null>(null);

  private readonly shellNode = computed(() => this.state.nodeById(this.shellObjectId()));

  readonly activeShellRenderer = computed(() => {
    const normalizedName = this.shellNode()?.name?.trim().toLowerCase() ?? '';
    switch (normalizedName) {
      case 'login-shell':
        return 'login-shell';
      case 'application-shell':
        return 'application-shell';
      default:
        return 'unknown';
    }
  });
}
