import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { InplaceModule } from 'primeng/inplace';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { TableModule } from 'primeng/table';
import { Tab } from 'primeng/tabs';
import { TabList } from 'primeng/tabs';
import { TabPanel } from 'primeng/tabs';
import { TabPanels } from 'primeng/tabs';
import { Tabs } from 'primeng/tabs';
import { DesignHubWorkspaceService } from '../../../../../services/design-hub-workspace.service';

@Component({
  selector: 'app-design-hub-inspector-overview-tab',
  standalone: true,
  imports: [FormsModule, InplaceModule, InputTextModule, SelectModule, TableModule, Tabs, TabList, Tab, TabPanels, TabPanel],
  templateUrl: './design-hub-inspector-overview-tab.component.html',
  styleUrl: './design-hub-inspector-overview-tab.component.scss',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DesignHubInspectorOverviewTabComponent {
  private readonly workspace = inject(DesignHubWorkspaceService);

  readonly selectedFrontendComponentInstanceView = this.workspace.selectedFrontendComponentInstanceView;
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
  readonly selectedFactSheetHeader = this.workspace.selectedFactSheetHeader;
  readonly selectedFactSheetSummary = this.workspace.selectedFactSheetSummary;
  readonly selectedInspectorIssues = this.workspace.selectedInspectorIssues;
  readonly inspectorIssueStatusOptions = this.workspace.inspectorIssueStatusOptions;
  readonly inspectorIssueStatusFilter = this.workspace.inspectorIssueStatusFilter;
  readonly inspectorIssueCategoryFilter = this.workspace.inspectorIssueCategoryFilter;
  readonly inspectorIssueCategoryOptions = this.workspace.inspectorIssueCategoryOptions;
  readonly filteredSelectedInspectorIssues = this.workspace.filteredSelectedInspectorIssues;
  readonly overviewInspectorIssuesFirst = this.workspace.overviewInspectorIssuesFirst;
  readonly selectedAttributePanelTitle = this.workspace.selectedAttributePanelTitle;
  readonly selectedAttributeRows = this.workspace.selectedAttributeRows;
  readonly selectedRemainingAttributeRows = this.workspace.selectedRemainingAttributeRows;
  readonly activeRelationshipTab = this.workspace.activeRelationshipTab;
  readonly selectedRelationshipGroups = this.workspace.selectedRelationshipGroups;

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
  readonly onInspectorIssueStatusFilterChange = this.workspace.onInspectorIssueStatusFilterChange.bind(this.workspace);
  readonly onInspectorIssueCategoryFilterChange = this.workspace.onInspectorIssueCategoryFilterChange.bind(this.workspace);
  readonly onOverviewInspectorIssuesPage = this.workspace.onOverviewInspectorIssuesPage.bind(this.workspace);
  readonly focusInspectorIssue = this.workspace.focusInspectorIssue.bind(this.workspace);
  readonly viewInspectorIssue = this.workspace.viewInspectorIssue.bind(this.workspace);
  readonly resolveInspectorIssue = this.workspace.resolveInspectorIssue.bind(this.workspace);
  readonly openIssuesTab = this.workspace.openIssuesTab.bind(this.workspace);
  readonly attributeDisplayValue = this.workspace.attributeDisplayValue.bind(this.workspace);
  readonly attributeDraftValue = this.workspace.attributeDraftValue.bind(this.workspace);
  readonly onAttributeDraftInput = this.workspace.onAttributeDraftInput.bind(this.workspace);
  readonly onAttributeDraftValueChange = this.workspace.onAttributeDraftValueChange.bind(this.workspace);
  readonly onAttributeEditorActivate = this.workspace.onAttributeEditorActivate.bind(this.workspace);
  readonly onAttributeEditorDeactivate = this.workspace.onAttributeEditorDeactivate.bind(this.workspace);
  readonly applyAttributeDraft = this.workspace.applyAttributeDraft.bind(this.workspace);
  readonly cancelAttributeDraft = this.workspace.cancelAttributeDraft.bind(this.workspace);
  readonly selectedAttributeEmptyMessage = this.workspace.selectedAttributeEmptyMessage.bind(this.workspace);
  readonly onRelationshipTabChange = this.workspace.onRelationshipTabChange.bind(this.workspace);
  readonly onRelatedObjectClick = this.workspace.onRelatedObjectClick.bind(this.workspace);
  readonly inspectorIssueEmptyMessage = this.workspace.inspectorIssueEmptyMessage.bind(this.workspace);
}
