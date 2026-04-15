import { ChangeDetectionStrategy, Component } from '@angular/core';
import { DesignHubInspectorPaneComponent } from './inspector-pane/design-hub-inspector-pane.component';
import { DesignHubTreePaneComponent } from './tree-pane/design-hub-tree-pane.component';

@Component({
  selector: 'app-design-hub-main',
  standalone: true,
  imports: [DesignHubTreePaneComponent, DesignHubInspectorPaneComponent],
  templateUrl: './design-hub-main.component.html',
  styleUrl: './design-hub-main.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubMainComponent {}
