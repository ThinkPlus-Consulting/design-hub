import { ChangeDetectionStrategy, Component, inject, input } from '@angular/core';
import { ShellBackgroundConfig } from '../../models/system-shell-graph.model';
import { SystemShellGraphStateService } from '../../services/system-shell-graph-state.service';

@Component({
  selector: 'app-administration-screen-preview',
  standalone: true,
  templateUrl: './administration-screen-preview.component.html',
  styleUrl: './administration-screen-preview.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdministrationScreenPreviewComponent {
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
    return this.backgroundConfig()?.backgroundColorStyle ?? 'var(--tp-bg)';
  }

  private resolveNode(graphCode: string | null | undefined) {
    const normalizedCode = graphCode?.trim();
    if (!normalizedCode) {
      return null;
    }

    return this.state.nodeHierarchyMap().get(normalizedCode) ?? this.state.nodeMap().get(normalizedCode) ?? null;
  }
}
