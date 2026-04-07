import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ButtonModule } from 'primeng/button';
import { InputOtpModule } from 'primeng/inputotp';
import { ShellBackgroundConfig } from '../../models/system-shell-graph.model';
import { SystemShellGraphStateService } from '../../services/system-shell-graph-state.service';

@Component({
  selector: 'app-mfa-screen-preview',
  standalone: true,
  imports: [FormsModule, InputOtpModule, ButtonModule],
  templateUrl: './mfa-screen-preview.component.html',
  styleUrl: './mfa-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MfaScreenPreviewComponent {
  private readonly state = inject(SystemShellGraphStateService);
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);
  otpValue = '123456';

  isFocused(graphCode: string): boolean {
    return this.selectedGuid() === this.guid(graphCode);
  }

  sourceObjectId(graphCode: string | null | undefined): string | null {
    return this.resolveNode(graphCode)?.id?.trim() ?? null;
  }

  guid(graphCode: string | null | undefined): string | null {
    return this.resolveNode(graphCode)?.guid?.trim() ?? null;
  }

  backgroundColorStyle(): string {
    return this.backgroundConfig()?.backgroundColorStyle ?? 'color-mix(in srgb, var(--tp-primary-dark) 42%, transparent)';
  }

  backgroundPatternOpacity(): string {
    const opacity = this.backgroundConfig()?.backgroundPatternOpacity;
    return typeof opacity === 'number' ? String(opacity) : '0.13';
  }

  showPattern(): boolean {
    const patternKey = this.backgroundConfig()?.backgroundPatternKey;
    return patternKey ? true : this.backgroundConfig() === null;
  }

  private resolveNode(graphCode: string | null | undefined) {
    const normalizedCode = graphCode?.trim();
    if (!normalizedCode) {
      return null;
    }

    return this.state.nodeHierarchyMap().get(normalizedCode) ?? this.state.nodeMap().get(normalizedCode) ?? null;
  }
}
