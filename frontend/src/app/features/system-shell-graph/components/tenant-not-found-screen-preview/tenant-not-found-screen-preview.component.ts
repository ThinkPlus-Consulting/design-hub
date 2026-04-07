import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ShellBackgroundConfig } from '../../models/system-shell-graph.model';
import { SystemShellGraphStateService } from '../../services/system-shell-graph-state.service';

@Component({
  selector: 'app-tenant-not-found-screen-preview',
  standalone: true,
  templateUrl: './tenant-not-found-screen-preview.component.html',
  styleUrl: './tenant-not-found-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class TenantNotFoundScreenPreviewComponent {
  private readonly state = inject(SystemShellGraphStateService);
  readonly selectedGuid = input<string | null>(null);
  readonly backgroundConfig = input<ShellBackgroundConfig | null>(null);

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
    return this.backgroundConfig()?.backgroundColorStyle ?? 'var(--tp-primary)';
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
