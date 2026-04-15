import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { TableModule } from 'primeng/table';
import { DesignHubWorkspaceService } from '../../../../../services/design-hub-workspace.service';

@Component({
  selector: 'app-design-hub-inspector-issues-tab',
  standalone: true,
  imports: [TableModule],
  templateUrl: './design-hub-inspector-issues-tab.component.html',
  styleUrl: './design-hub-inspector-issues-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubInspectorIssuesTabComponent {
  private readonly workspace = inject(DesignHubWorkspaceService);

  readonly selectedInspectorIssueSummary = this.workspace.selectedInspectorIssueSummary;
  readonly selectedInspectorIssues = this.workspace.selectedInspectorIssues;
  readonly inspectorIssueStatusOptions = this.workspace.inspectorIssueStatusOptions;
  readonly inspectorIssueStatusFilter = this.workspace.inspectorIssueStatusFilter;
  readonly inspectorIssueCategoryFilter = this.workspace.inspectorIssueCategoryFilter;
  readonly inspectorIssueCategoryOptions = this.workspace.inspectorIssueCategoryOptions;
  readonly selectedInspectorIssueSelection = this.workspace.selectedInspectorIssueSelection;
  readonly selectedOpenInspectorIssueCount = this.workspace.selectedOpenInspectorIssueCount;
  readonly filteredSelectedInspectorIssues = this.workspace.filteredSelectedInspectorIssues;
  readonly issuesTabInspectorIssuesFirst = this.workspace.issuesTabInspectorIssuesFirst;

  readonly onInspectorIssueStatusFilterChange = this.workspace.onInspectorIssueStatusFilterChange.bind(this.workspace);
  readonly onInspectorIssueCategoryFilterChange = this.workspace.onInspectorIssueCategoryFilterChange.bind(this.workspace);
  readonly selectAllInspectorIssues = this.workspace.selectAllInspectorIssues.bind(this.workspace);
  readonly clearInspectorIssueSelection = this.workspace.clearInspectorIssueSelection.bind(this.workspace);
  readonly resolveSelectedInspectorIssues = this.workspace.resolveSelectedInspectorIssues.bind(this.workspace);
  readonly onInspectorIssueSelectionChange = this.workspace.onInspectorIssueSelectionChange.bind(this.workspace);
  readonly onIssuesTabInspectorIssuesPage = this.workspace.onIssuesTabInspectorIssuesPage.bind(this.workspace);
  readonly focusInspectorIssue = this.workspace.focusInspectorIssue.bind(this.workspace);
  readonly viewInspectorIssue = this.workspace.viewInspectorIssue.bind(this.workspace);
  readonly resolveInspectorIssue = this.workspace.resolveInspectorIssue.bind(this.workspace);
  readonly inspectorIssueEmptyMessage = this.workspace.inspectorIssueEmptyMessage.bind(this.workspace);
}
