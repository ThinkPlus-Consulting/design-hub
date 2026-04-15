import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ToggleButtonModule } from 'primeng/togglebutton';
import { TreeModule } from 'primeng/tree';
import { DesignHubWorkspaceService } from '../../../../services/design-hub-workspace.service';

@Component({
  selector: 'app-design-hub-tree-pane',
  standalone: true,
  imports: [FormsModule, ToggleButtonModule, TreeModule],
  templateUrl: './design-hub-tree-pane.component.html',
  styleUrl: './design-hub-tree-pane.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubTreePaneComponent {
  private readonly workspace = inject(DesignHubWorkspaceService);

  readonly activeWorkspace = this.workspace.activeWorkspace;
  readonly activeWorkspaceTitle = this.workspace.activeWorkspaceTitle;
  readonly registrySearchTerm = this.workspace.registrySearchTerm;
  readonly state = this.workspace.state;
  readonly registryLoading = this.workspace.registryLoading;
  readonly registryError = this.workspace.registryError;
  readonly activeTree = this.workspace.activeTree;
  readonly activeTreeSelection = this.workspace.activeTreeSelection;
  readonly activeTreeExpanded = this.workspace.activeTreeExpanded;

  readonly setActiveTreeExpanded = this.workspace.setActiveTreeExpanded.bind(this.workspace);
  readonly onRegistrySearch = this.workspace.onRegistrySearch.bind(this.workspace);
  readonly onTreeSelection = this.workspace.onTreeSelection.bind(this.workspace);
  readonly hasInspectionViolation = this.workspace.hasInspectionViolation.bind(this.workspace);
  readonly inspectionViolationTooltip = this.workspace.inspectionViolationTooltip.bind(this.workspace);
  readonly inspectionViolationAriaLabel = this.workspace.inspectionViolationAriaLabel.bind(this.workspace);
  readonly openIssueCount = this.workspace.openIssueCount.bind(this.workspace);
  readonly isSelected = this.workspace.isSelected.bind(this.workspace);
  readonly isPreviewHovered = this.workspace.isPreviewHovered.bind(this.workspace);
  readonly onTreeNodeClick = this.workspace.onTreeNodeClick.bind(this.workspace);
  readonly onTreeNodeEnter = this.workspace.onTreeNodeEnter.bind(this.workspace);
  readonly updateTreeNodeTooltip = this.workspace.updateTreeNodeTooltip.bind(this.workspace);
  readonly onTreeNodeLeave = this.workspace.onTreeNodeLeave.bind(this.workspace);
}
