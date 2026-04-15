import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { TableModule } from 'primeng/table';
import { Tab } from 'primeng/tabs';
import { TabList } from 'primeng/tabs';
import { TabPanel } from 'primeng/tabs';
import { TabPanels } from 'primeng/tabs';
import { Tabs } from 'primeng/tabs';
import { DesignHubWorkspaceService } from '../../../../services/design-hub-workspace.service';
import { DesignHubInspectorIssuesTabComponent } from './issues-tab/design-hub-inspector-issues-tab.component';
import { DesignHubInspectorOverviewTabComponent } from './overview-tab/design-hub-inspector-overview-tab.component';
import { DesignHubInspectorPreviewTabComponent } from './preview-tab/design-hub-inspector-preview-tab.component';

@Component({
  selector: 'app-design-hub-inspector-pane',
  standalone: true,
  imports: [
    Tabs,
    TabList,
    Tab,
    TabPanels,
    TabPanel,
    TableModule,
    DesignHubInspectorOverviewTabComponent,
    DesignHubInspectorPreviewTabComponent,
    DesignHubInspectorIssuesTabComponent,
  ],
  templateUrl: './design-hub-inspector-pane.component.html',
  styleUrl: './design-hub-inspector-pane.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubInspectorPaneComponent {
  private readonly workspace = inject(DesignHubWorkspaceService);

  readonly activeWorkspace = this.workspace.activeWorkspace;
  readonly activeInspectorTab = this.workspace.activeInspectorTab;
  readonly xrayRunning = this.workspace.xrayRunning;
  readonly activeComponentInstanceView = this.workspace.activeComponentInstanceView;
  readonly registryEditMode = this.workspace.registryEditMode;
  readonly registryInstanceSaving = this.workspace.registryInstanceSaving;
  readonly registryFactSheetHeader = this.workspace.registryFactSheetHeader;
  readonly registryInstanceSummary = this.workspace.registryInstanceSummary;
  readonly registryPlacementRows = this.workspace.registryPlacementRows;
  readonly accordionInstanceName = this.workspace.accordionInstanceName;
  readonly accordionInstanceStatus = this.workspace.accordionInstanceStatus;
  readonly accordionInstanceDescription = this.workspace.accordionInstanceDescription;
  readonly accordionTargetObjectId = this.workspace.accordionTargetObjectId;
  readonly registryTargetOptions = this.workspace.registryTargetOptions;
  readonly accordionConfigurationRows = this.workspace.accordionConfigurationRows;
  readonly accordionBuilderStaticPanels = this.workspace.accordionBuilderStaticPanels;
  readonly accordionBuilderRenderMethod = this.workspace.accordionBuilderRenderMethod;
  readonly accordionBuilderPanelCount = this.workspace.accordionBuilderPanelCount;
  readonly accordionBuilderDataSource = this.workspace.accordionBuilderDataSource;
  readonly accordionBuilderValueField = this.workspace.accordionBuilderValueField;
  readonly accordionBuilderHeaderField = this.workspace.accordionBuilderHeaderField;
  readonly accordionBuilderContentField = this.workspace.accordionBuilderContentField;
  readonly accordionBuilderDisabledField = this.workspace.accordionBuilderDisabledField;
  readonly accordionBuilderMultiple = this.workspace.accordionBuilderMultiple;
  readonly accordionBuilderSelectOnFocus = this.workspace.accordionBuilderSelectOnFocus;
  readonly accordionBuilderDefaultValue = this.workspace.accordionBuilderDefaultValue;
  readonly accordionBuilderExpandIcon = this.workspace.accordionBuilderExpandIcon;
  readonly accordionBuilderCollapseIcon = this.workspace.accordionBuilderCollapseIcon;

  readonly onInspectorTabChange = this.workspace.onInspectorTabChange.bind(this.workspace);
  readonly toggleInspectMode = this.workspace.toggleInspectMode.bind(this.workspace);
  readonly enableRegistryEdit = this.workspace.enableRegistryEdit.bind(this.workspace);
  readonly cancelRegistryEdit = this.workspace.cancelRegistryEdit.bind(this.workspace);
  readonly saveRegistryInstance = this.workspace.saveRegistryInstance.bind(this.workspace);
  readonly onAccordionInstanceFieldChange = this.workspace.onAccordionInstanceFieldChange.bind(this.workspace);
  readonly onAccordionBuilderRenderMethodChange = this.workspace.onAccordionBuilderRenderMethodChange.bind(this.workspace);
  readonly onAccordionBuilderPanelCountChange = this.workspace.onAccordionBuilderPanelCountChange.bind(this.workspace);
  readonly onAccordionBuilderPanelFieldChange = this.workspace.onAccordionBuilderPanelFieldChange.bind(this.workspace);
  readonly onAccordionBuilderPanelDisabledChange = this.workspace.onAccordionBuilderPanelDisabledChange.bind(this.workspace);
  readonly onAccordionBuilderSimpleFieldChange = this.workspace.onAccordionBuilderSimpleFieldChange.bind(this.workspace);
  readonly onAccordionBuilderBooleanChange = this.workspace.onAccordionBuilderBooleanChange.bind(this.workspace);
}
