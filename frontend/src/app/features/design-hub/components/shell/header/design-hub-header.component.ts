import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';

export type DesignHubWorkspace = 'frontend' | 'components-registry';

@Component({
  selector: 'app-design-hub-header',
  standalone: true,
  templateUrl: './design-hub-header.component.html',
  styleUrl: './design-hub-header.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubHeaderComponent {
  readonly title = input('Design Hub');
  readonly subtitle = input('');
  readonly activeWorkspace = input<DesignHubWorkspace>('frontend');
  readonly workspaceChange = output<DesignHubWorkspace>();

  setWorkspace(workspace: DesignHubWorkspace): void {
    this.workspaceChange.emit(workspace);
  }
}
