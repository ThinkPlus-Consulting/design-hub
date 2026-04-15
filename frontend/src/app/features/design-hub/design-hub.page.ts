import { ChangeDetectionStrategy, Component, OnInit, ViewEncapsulation, inject } from '@angular/core';
import { DesignHubBreadcrumbComponent } from './components/shell/breadcrumb/design-hub-breadcrumb.component';
import { DesignHubFooterComponent } from './components/shell/footer/design-hub-footer.component';
import { DesignHubHeaderComponent } from './components/shell/header/design-hub-header.component';
import { DesignHubMainComponent } from './components/shell/main/design-hub-main.component';
import { DesignHubStateService } from './services/design-hub-state.service';
import { DesignHubWorkspaceService } from './services/design-hub-workspace.service';

@Component({
  selector: 'app-design-hub-page',
  standalone: true,
  imports: [
    DesignHubHeaderComponent,
    DesignHubBreadcrumbComponent,
    DesignHubMainComponent,
    DesignHubFooterComponent,
  ],
  providers: [DesignHubStateService, DesignHubWorkspaceService],
  encapsulation: ViewEncapsulation.None,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './design-hub.page.html',
  styleUrl: './design-hub.page.scss',
})
export class DesignHubPage implements OnInit {
  private readonly workspace = inject(DesignHubWorkspaceService);

  readonly activeWorkspace = this.workspace.activeWorkspace;
  readonly activeWorkspaceTitle = this.workspace.activeWorkspaceTitle;
  readonly inspectorBreadcrumb = this.workspace.inspectorBreadcrumb;
  readonly viewedInspectorIssue = this.workspace.viewedInspectorIssue;
  readonly issuePromptDraft = this.workspace.issuePromptDraft;
  readonly issuePromptSaving = this.workspace.issuePromptSaving;
  readonly xrayModalOpen = this.workspace.xrayModalOpen;
  readonly xrayRunning = this.workspace.xrayRunning;
  readonly xrayProgressMessage = this.workspace.xrayProgressMessage;
  readonly xrayConclusion = this.workspace.xrayConclusion;
  readonly xrayProgressLabel = this.workspace.xrayProgressLabel;
  readonly xrayProgressPercent = this.workspace.xrayProgressPercent;
  readonly xrayStageStates = this.workspace.xrayStageStates;
  readonly registryError = this.workspace.registryError;

  readonly activateWorkspace = this.workspace.activateWorkspace.bind(this.workspace);
  readonly onBreadcrumbClick = this.workspace.onBreadcrumbClick.bind(this.workspace);
  readonly closeInspectorIssueModal = this.workspace.closeInspectorIssueModal.bind(this.workspace);
  readonly resolvedIssueGuid = this.workspace.resolvedIssueGuid.bind(this.workspace);
  readonly onIssuePromptDraftChange = this.workspace.onIssuePromptDraftChange.bind(this.workspace);
  readonly saveInspectorIssuePrompt = this.workspace.saveInspectorIssuePrompt.bind(this.workspace);
  readonly focusInspectorIssue = this.workspace.focusInspectorIssue.bind(this.workspace);
  readonly resolveInspectorIssue = this.workspace.resolveInspectorIssue.bind(this.workspace);
  readonly closeXRaySummaryModal = this.workspace.closeXRaySummaryModal.bind(this.workspace);
  readonly openIssuesTabFromSummary = this.workspace.openIssuesTabFromSummary.bind(this.workspace);

  ngOnInit(): void {
    this.workspace.initialize();
  }
}
