import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  NgZone,
  OnDestroy,
  OnInit,
  ViewChild,
  computed,
  effect,
  inject,
  signal,
} from '@angular/core';
import { FormsModule } from '@angular/forms';
import { MenuItem, TreeNode } from 'primeng/api';
import { DockModule } from 'primeng/dock';
import { InplaceModule } from 'primeng/inplace';
import { InputTextModule } from 'primeng/inputtext';
import { SelectModule } from 'primeng/select';
import { TreeModule } from 'primeng/tree';
import { TableModule } from 'primeng/table';
import { Tab } from 'primeng/tabs';
import { TabList } from 'primeng/tabs';
import { TabPanel } from 'primeng/tabs';
import { TabPanels } from 'primeng/tabs';
import { Tabs } from 'primeng/tabs';
import { firstValueFrom } from 'rxjs';
import { SystemShellGraphStateService } from './services/system-shell-graph-state.service';
import { SystemShellGraphApiService } from './services/system-shell-graph-api.service';
import { AppShellPreviewComponent } from './components/app-shell-preview/app-shell-preview.component';
import {
  ComponentRegistryDefinition,
  ComponentRegistryInstance,
  ShellBackgroundConfig,
  SystemShellGraphIssueScanSummary,
  SystemShellGraphIssueNode,
  SystemShellGraphNode,
  SystemShellGraphRelationship,
  SystemShellGraphResponse,
  SystemShellTreeNodeData,
} from './models/system-shell-graph.model';
import {
  AccessibilityConformanceLevel,
  auditPreviewAccessibility,
} from './utils/preview-accessibility-audit';
import { PreviewDomResolver } from './utils/preview-dom-resolver';

interface RelationshipRow {
  direction: string;
  canonicalType: string;
  type: string;
  connectedFamily: string;
  connectedLayer: string;
  connectedObjectId: string | null;
  connectedCode: string;
  connectedName: string;
}

interface RelationshipGroup {
  key: string;
  label: string;
  rows: RelationshipRow[];
}

interface AccordionBuilderPanel {
  value: string;
  header: string;
  content: string;
  disabled: boolean;
}

type XRayStageKey =
  | 'graph-inventory'
  | 'preview-inventory'
  | 'completeness-checks'
  | 'parity-checks'
  | 'interactivity-checks'
  | 'issue-aggregation'
  | 'issue-persistence';

interface XRayStageState {
  key: XRayStageKey;
  label: string;
  description: string;
  status: 'pending' | 'active' | 'completed';
}

interface XRayCountRow {
  label: string;
  value: number;
}

interface XRayPreviewArtifactSnapshot {
  tag: string;
  description: string;
  textSnippet: string | null;
  sourceObjectId: string | null;
  guid: string | null;
  ownerObjectId: string | null;
  ownerCode: string | null;
  isPrimeArtifact: boolean;
  isSelectable: boolean;
}

interface XRayPreviewAuditSnapshot {
  artifacts: XRayPreviewArtifactSnapshot[];
  previewSummaryRows: XRayCountRow[];
  previewTagRows: XRayCountRow[];
  missingPreviewObjectIdCount: number;
  missingPreviewGuidCount: number;
  unmodeledPreviewArtifactCount: number;
  unselectablePreviewArtifactCount: number;
}

interface XRayGraphInventorySnapshot {
  nodes: SystemShellGraphNode[];
  rows: XRayCountRow[];
  totalObjectsScanned: number;
  missingObjectIdCount: number;
  missingObjectGuidCount: number;
}

interface XRayConclusion {
  totalObjectsScanned: number;
  totalPreviewArtifactsScanned: number;
  graphObjectRows: XRayCountRow[];
  previewSummaryRows: XRayCountRow[];
  previewTagRows: XRayCountRow[];
  issueCategoryRows: XRayCountRow[];
  missingObjectIdCount: number;
  missingObjectGuidCount: number;
  missingPreviewObjectIdCount: number;
  missingPreviewGuidCount: number;
  unmodeledPreviewArtifactCount: number;
  unselectablePreviewArtifactCount: number;
  summary: SystemShellGraphIssueScanSummary | null;
  conclusionText: string;
}

type AttributeValueType = 'string' | 'number' | 'boolean' | 'array';
type AttributeEditorKind = 'text' | 'enum' | 'readonly';

interface AttributeOption {
  label: string;
  value: string;
}

interface AttributeDefinition {
  label: string;
  attributeKey: keyof SystemShellGraphNode;
  valueType: AttributeValueType;
  readonly?: boolean;
}

interface AttributeRow {
  nodeCode: string;
  label: string;
  attributeKey: keyof SystemShellGraphNode;
  valueType: AttributeValueType;
  rawValue: unknown;
  value: string;
  editable: boolean;
  editorKind: AttributeEditorKind;
  options: AttributeOption[];
}

const COMMON_FACT_SHEET_ATTRIBUTE_KEYS = new Set<keyof SystemShellGraphNode>([
  'name',
  'family',
  'id',
  'guid',
  'status',
  'description',
]);

const REMAINING_ATTRIBUTE_DEFINITIONS: readonly AttributeDefinition[] = [
  { label: 'Code', attributeKey: 'code', valueType: 'string', readonly: true },
  { label: 'Domain', attributeKey: 'domain', valueType: 'string', readonly: true },
  { label: 'Layer', attributeKey: 'layer', valueType: 'string', readonly: true },
  { label: 'Object Type', attributeKey: 'objectType', valueType: 'string', readonly: true },
  { label: 'Hierarchy Code', attributeKey: 'hierarchyCode', valueType: 'string', readonly: true },
  { label: 'Definition Code', attributeKey: 'definitionCode', valueType: 'string', readonly: true },
  { label: 'Implementation Source Path', attributeKey: 'implementationSourcePath', valueType: 'string', readonly: true },
  { label: 'Configuration JSON', attributeKey: 'configurationJson', valueType: 'string', readonly: true },
  { label: 'Asset Name', attributeKey: 'assetName', valueType: 'string', readonly: true },
  { label: 'Asset Type', attributeKey: 'assetType', valueType: 'string', readonly: true },
  { label: 'Render Mode', attributeKey: 'renderMode', valueType: 'string', readonly: true },
  { label: 'Default State', attributeKey: 'defaultState', valueType: 'string', readonly: true },
  { label: 'Control Source', attributeKey: 'controlSource', valueType: 'string', readonly: true },
  { label: 'Background Type', attributeKey: 'backgroundType', valueType: 'string', readonly: true },
  { label: 'Background Color Style', attributeKey: 'backgroundColorStyle', valueType: 'string', readonly: true },
  { label: 'Background Pattern', attributeKey: 'backgroundPatternKey', valueType: 'string', readonly: true },
  { label: 'Background Pattern Opacity', attributeKey: 'backgroundPatternOpacity', valueType: 'number', readonly: true },
  { label: 'Background Image Path', attributeKey: 'backgroundImagePath', valueType: 'string', readonly: true },
  { label: 'Viewport Width', attributeKey: 'viewportWidth', valueType: 'number', readonly: true },
  { label: 'Viewport Height', attributeKey: 'viewportHeight', valueType: 'number', readonly: true },
  { label: 'Viewport Category', attributeKey: 'viewportCategory', valueType: 'string', readonly: true },
  { label: 'Execution Method', attributeKey: 'executionMethod', valueType: 'string', readonly: true },
  { label: 'Step Order', attributeKey: 'stepOrder', valueType: 'number', readonly: true },
  { label: 'Rule Scope', attributeKey: 'ruleScope', valueType: 'string', readonly: true },
  { label: 'Condition Expression', attributeKey: 'conditionExpression', valueType: 'string', readonly: true },
  { label: 'Execution Effect', attributeKey: 'executionEffect', valueType: 'string', readonly: true },
  { label: 'Blocker Type', attributeKey: 'blockerType', valueType: 'string', readonly: true },
  { label: 'Blocking Effect', attributeKey: 'blockingEffect', valueType: 'string', readonly: true },
  { label: 'Section Type', attributeKey: 'sectionType', valueType: 'string', readonly: true },
  { label: 'Repeatable', attributeKey: 'repeatable', valueType: 'boolean', readonly: true },
  { label: 'Element Type', attributeKey: 'elementType', valueType: 'string', readonly: true },
  { label: 'Semantic Level', attributeKey: 'semanticLevel', valueType: 'string', readonly: true },
  { label: 'PrimeNG Source', attributeKey: 'primeComponent', valueType: 'string', readonly: true },
  { label: 'Token Families', attributeKey: 'tokenFamilies', valueType: 'array', readonly: true },
  { label: 'Issue Source', attributeKey: 'issueSource', valueType: 'string', readonly: true },
  { label: 'Issue Category', attributeKey: 'issueCategory', valueType: 'string', readonly: true },
  { label: 'Issue Rule', attributeKey: 'issueRule', valueType: 'string', readonly: true },
  { label: 'Issue Severity', attributeKey: 'issueSeverity', valueType: 'string', readonly: true },
  { label: 'Issue Prompt', attributeKey: 'issuePrompt', valueType: 'string', readonly: true },
  { label: 'Rule Set Type', attributeKey: 'ruleSetType', valueType: 'string', readonly: true },
  { label: 'Rule Set Scope', attributeKey: 'ruleSetScope', valueType: 'string', readonly: true },
  { label: 'Action Type', attributeKey: 'actionType', valueType: 'string', readonly: true },
  { label: 'Action Value', attributeKey: 'actionValue', valueType: 'string', readonly: true },
  { label: 'Priority', attributeKey: 'priority', valueType: 'number', readonly: true },
  { label: 'Stop Processing', attributeKey: 'stopProcessing', valueType: 'boolean', readonly: true },
];

type PreviewViewportProfile = 'web' | 'tablet' | 'mobile';

interface PreviewViewportOption {
  value: PreviewViewportProfile;
  label: string;
  viewportProfileCode?: string;
}

interface PreviewInteractionPalette {
  selected: string;
  hover: string;
}

const HTML_CONTAINER_TAGS = new Set([
  'article',
  'aside',
  'div',
  'footer',
  'form',
  'header',
  'main',
  'nav',
  'section',
  'ul',
]);

const HTML_NATIVE_TAGS = new Set([
  'a',
  'article',
  'aside',
  'button',
  'div',
  'footer',
  'form',
  'h1',
  'h2',
  'h3',
  'h4',
  'header',
  'i',
  'img',
  'input',
  'label',
  'li',
  'main',
  'nav',
  'p',
  'section',
  'small',
  'span',
  'strong',
  'td',
  'th',
  'tr',
  'ul',
]);

const PREVIEW_VIEWPORT_OPTIONS: readonly PreviewViewportOption[] = [
  { value: 'web', label: 'Web', viewportProfileCode: 'VPR90' },
  { value: 'tablet', label: 'Tablet', viewportProfileCode: 'VPR91' },
  { value: 'mobile', label: 'Mobile', viewportProfileCode: 'VPR92' },
];

const X_RAY_STAGE_DEFINITIONS: readonly Omit<XRayStageState, 'status'>[] = [
  {
    key: 'graph-inventory',
    label: 'Graph Inventory',
    description: 'Count Shell, Screen, Section, Element, and Component objects from Neo4j.',
  },
  {
    key: 'preview-inventory',
    label: 'Preview Inventory',
    description: 'Count rendered preview canvas artifacts across native HTML and PrimeNG output.',
  },
  {
    key: 'completeness-checks',
    label: 'Completeness Checks',
    description: 'Verify required id, guid, source-object-id, and guid bindings are present.',
  },
  {
    key: 'parity-checks',
    label: 'Parity Checks',
    description: 'Detect preview artifacts missing graph representation and graph objects missing preview bindings.',
  },
  {
    key: 'interactivity-checks',
    label: 'Interactivity Checks',
    description: 'Detect visible preview artifacts that cannot be independently identified by hover or click.',
  },
  {
    key: 'issue-aggregation',
    label: 'Issue Aggregation',
    description: 'Merge new inventory findings with existing X-Ray structure, HTML, parity, accessibility, and viewport rules.',
  },
  {
    key: 'issue-persistence',
    label: 'Issue Persistence',
    description: 'Persist the refreshed issue set and conclude the X-Ray scan.',
  },
];

@Component({
  selector: 'app-system-shell-graph-page',
  standalone: true,
  imports: [
    DockModule,
    FormsModule,
    InplaceModule,
    InputTextModule,
    SelectModule,
    TreeModule,
    Tabs,
    TabList,
    Tab,
    TabPanels,
    TabPanel,
    TableModule,
    AppShellPreviewComponent,
  ],
  providers: [SystemShellGraphStateService],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="ssg-page">
      <aside class="ssg-panel ssg-panel-tree">
        <div class="ssg-side-menu-title-section">
          <h1>{{ activeWorkspaceTitle() }}</h1>
        </div>

        <div class="ssg-side-menu-controls-section">
          <div class="ssg-toolbar">
            <button type="button" class="tree-toolbar-button" (click)="expandActiveTree()">
              <span class="tree-toolbar-icon" aria-hidden="true">+</span>
              <span>Expand all</span>
            </button>
            <button type="button" class="tree-toolbar-button" (click)="collapseActiveTree()">
              <span class="tree-toolbar-icon" aria-hidden="true">−</span>
              <span>Collapse all</span>
            </button>
            <button
              type="button"
              class="tree-toolbar-button"
              [class.is-active]="xrayRunning()"
              [disabled]="activeWorkspace() !== 'frontend'"
              (click)="toggleInspectMode()"
            >
              <span class="tree-toolbar-icon" aria-hidden="true">
                <svg class="ssg-agentic-icon" viewBox="0 0 24 24" aria-hidden="true">
                  <rect x="6.5" y="7" width="11" height="10" rx="2"></rect>
                  <path d="M9 7V5.75C9 4.78 9.78 4 10.75 4h2.5C14.22 4 15 4.78 15 5.75V7"></path>
                  <circle cx="10" cy="11.5" r="1"></circle>
                  <circle cx="14" cy="11.5" r="1"></circle>
                  <path d="M10 15c.45-.42 1.12-.67 2-.67s1.55.25 2 .67"></path>
                  <path d="M4.5 12h-1"></path>
                  <path d="M20.5 12h-1"></path>
                </svg>
              </span>
              <span>X-Ray Agent</span>
            </button>
          </div>
          <div class="ssg-search-bar">
            <input
              type="search"
              class="ssg-search-input"
              [value]="registrySearchTerm()"
              (input)="onRegistrySearch($event)"
              [disabled]="activeWorkspace() !== 'components-registry'"
              placeholder="Search"
              aria-label="Search"
            >
          </div>
        </div>

        <div class="ssg-side-menu-tree-section">
          @if (activeWorkspace() === 'frontend' && state.loading()) {
            <div class="ssg-empty">Loading graph…</div>
          } @else if (activeWorkspace() === 'frontend' && state.error()) {
            <div class="ssg-empty ssg-error">{{ state.error() }}</div>
          } @else if (activeWorkspace() === 'components-registry' && registryLoading()) {
            <div class="ssg-empty">Loading components…</div>
          } @else if (activeWorkspace() === 'components-registry' && registryError()) {
            <div class="ssg-empty ssg-error">{{ registryError() }}</div>
          } @else {
            <p-tree
              class="ssg-tree"
              [value]="activeTree()"
              selectionMode="single"
              [selection]="activeTreeSelection()"
              (selectionChange)="onTreeSelection($event)"
            >
              <ng-template let-node pTemplate="default">
                <div
                  class="ssg-tree-label"
                  [class.kind-root-row]="node.data.kind === 'navigation-root' || node.data.kind === 'registry-root'"
                  [class.kind-registry-row]="node.data.kind === 'registry-item'"
                  [class.kind-shell-row]="node.data.family === 'Shell'"
                  [class.kind-screen-row]="node.data.family === 'Screen'"
                  [class.kind-container-row]="node.data.family === 'Section'"
                  [class.kind-element-row]="node.data.family === 'Element'"
                  [class.has-violation]="hasInspectionViolation(node.data.objectId)"
                  [class.is-selected]="isSelected(node.data.objectId)"
                  [class.is-preview-hovered]="isPreviewHovered(node.data.objectId)"
                  (click)="onTreeNodeClick(node, $event)"
                  (mouseenter)="onTreeNodeEnter(node.data); updateTreeNodeTooltip($event)"
                  (mouseleave)="onTreeNodeLeave()"
                >
                  <span
                    class="ssg-tree-icon"
                    [class.kind-application]="node.data.family === 'Application'"
                    [class.kind-shell]="node.data.family === 'Shell'"
                    [class.kind-screen]="node.data.family === 'Screen'"
                    [class.kind-container]="node.data.family === 'Section'"
                    [class.kind-element]="node.data.family === 'Element'"
                  >
                    @if (node.data.kind === 'navigation-root') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="3.5" y="4.5" width="17" height="15" rx="2"></rect>
                        <path d="M7 9.5h10"></path>
                        <path d="M7 13h10"></path>
                        <path d="M7 16.5h6"></path>
                      </svg>
                    } @else if (node.data.kind === 'registry-root') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="4.5" y="5.5" width="15" height="13" rx="2"></rect>
                        <path d="M8 9h8"></path>
                        <path d="M8 12.5h8"></path>
                        <path d="M8 16h5"></path>
                      </svg>
                    } @else if (node.data.kind === 'registry-item') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="5.5" y="5.5" width="13" height="13" rx="2"></rect>
                        <path d="M9 5.5v13"></path>
                        <path d="M5.5 9h13"></path>
                      </svg>
                    } @else if (node.data.family === 'Application') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="3.5" y="4.5" width="17" height="15" rx="2"></rect>
                        <path d="M7 9h10"></path>
                        <path d="M7 13h10"></path>
                        <path d="M7 17h6"></path>
                        <path d="M12 4.5v15"></path>
                      </svg>
                    } @else if (node.data.family === 'Shell') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="3.5" y="5.5" width="17" height="13" rx="2"></rect>
                        <path d="M7 9.5h10"></path>
                        <path d="M7 13.5h10"></path>
                        <path d="M7 17.5h6"></path>
                      </svg>
                    } @else if (node.data.family === 'Screen') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="3.5" y="4.5" width="17" height="15" rx="2"></rect>
                        <path d="M3.5 8.5h17"></path>
                        <path d="M6.5 6.5h.01"></path>
                        <path d="M9.5 6.5h.01"></path>
                        <path d="M6.5 12h11"></path>
                        <path d="M6.5 16h7"></path>
                      </svg>
                    } @else if (node.data.family === 'Section') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="4.5" y="5.5" width="15" height="4" rx="1.2"></rect>
                        <rect x="4.5" y="11.5" width="15" height="7" rx="1.2"></rect>
                      </svg>
                    } @else if (node.data.family === 'Component') {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="8" y="8" width="8" height="8" rx="1.5"></rect>
                        <path d="M12 4v3"></path>
                        <path d="M12 17v3"></path>
                        <path d="M4 12h3"></path>
                        <path d="M17 12h3"></path>
                        <path d="M6.5 6.5l2 2"></path>
                        <path d="M15.5 15.5l2 2"></path>
                        <path d="M17.5 6.5l-2 2"></path>
                        <path d="M8.5 15.5l-2 2"></path>
                      </svg>
                    } @else {
                      <svg viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="6" y="6" width="12" height="12" rx="2"></rect>
                        <path d="M9 12h6"></path>
                        <path d="M12 9v6"></path>
                      </svg>
                    }
                  </span>
                  <span class="ssg-tree-text">{{ node.label }}</span>
                  @if (hasInspectionViolation(node.data.objectId)) {
                    <span
                      class="ssg-violation-flag"
                      [attr.title]="inspectionViolationTooltip(node.data.objectId)"
                      [attr.aria-label]="inspectionViolationAriaLabel(node.data.objectId)"
                    >
                      <span class="ssg-violation-count">{{ openIssueCount(node.data.objectId) }}</span>
                    </span>
                  }
                </div>
              </ng-template>
            </p-tree>
          }
        </div>
      </aside>

      <section class="ssg-panel ssg-panel-preview">
        <div class="ssg-panel-header">
          <h2>Inspector</h2>
          @if (activeWorkspace() === 'frontend') {
            <nav class="ssg-breadcrumb" aria-label="Inspector path">
              @for (crumb of inspectorBreadcrumb(); track crumb.objectId ?? crumb.code; let last = $last) {
                @if (!last) {
                  <button type="button" class="ssg-breadcrumb-link" (click)="onBreadcrumbClick(crumb.objectId, crumb.code)">
                    <span>{{ crumb.label }}</span>
                  </button>
                  <span class="ssg-breadcrumb-separator" aria-hidden="true">/</span>
                } @else {
                  <span class="ssg-breadcrumb-current" aria-current="page">{{ crumb.label }}</span>
                }
              }
            </nav>
          }
        </div>

        @if (activeWorkspace() === 'frontend') {
        <p-tabs class="ssg-inspector-tabs" [value]="activeInspectorTab()" (valueChange)="onInspectorTabChange($event)">
          <p-tablist>
            <p-tab value="overview">Overview</p-tab>
            <p-tab value="preview">Preview</p-tab>
            <p-tab value="issues">Issues</p-tab>
          </p-tablist>
          <p-tabpanels>
            <p-tabpanel value="overview">
              @if (selectedFrontendComponentInstanceView(); as registryInstance) {
                <div class="ssg-overview-tab">
                  <div class="ssg-fact-sheet">
                    <header class="ssg-fact-header">
                      <div class="ssg-fact-logo-panel">
                        <div class="ssg-fact-logo" aria-hidden="true">CP</div>
                      </div>
                      <div class="ssg-fact-header-content">
                        <div class="ssg-fact-header-actions">
                          @if (!registryEditMode()) {
                            <button type="button" class="tree-toolbar-button" data-registry-action="edit" (click)="enableRegistryEdit()">Edit</button>
                          } @else {
                            <button type="button" class="tree-toolbar-button" [disabled]="registryInstanceSaving()" data-registry-action="save" (click)="saveRegistryInstance()">Save</button>
                            <button type="button" class="tree-toolbar-button" [disabled]="registryInstanceSaving()" data-registry-action="cancel" (click)="cancelRegistryEdit()">Cancel</button>
                          }
                        </div>
                        <h3>{{ registryFactSheetHeader().title }}</h3>
                        <dl class="ssg-fact-meta-list">
                          <div class="ssg-fact-meta-row">
                            <dt>ID</dt>
                            <dd class="ssg-fact-id-value">{{ registryFactSheetHeader().id }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Type</dt>
                            <dd>{{ registryFactSheetHeader().type }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Code</dt>
                            <dd>{{ registryFactSheetHeader().code }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Asset Name</dt>
                            <dd>{{ registryFactSheetHeader().assetName }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Asset Type</dt>
                            <dd>{{ registryFactSheetHeader().assetType }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Implementation Source</dt>
                            <dd class="ssg-fact-id-value">{{ registryFactSheetHeader().implementationSourcePath }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Status</dt>
                            <dd><span class="ssg-status-chip">{{ registryFactSheetHeader().status }}</span></dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Description</dt>
                            <dd>{{ registryFactSheetHeader().description }}</dd>
                          </div>
                        </dl>
                      </div>
                    </header>
                  </div>

                  <section class="ssg-overview-section">
                    <div class="ssg-insights-panel">
                      <div class="ssg-insights-panel-header">
                        <h3>Insights</h3>
                      </div>
                      <div class="ssg-insights-panel-body ssg-insights-grid">
                        @for (item of registryInstanceSummary(); track item.label) {
                          <article class="ssg-insight-stat-panel">
                            <h4>{{ item.label }}</h4>
                            <dl class="ssg-fact-meta-list">
                              <div class="ssg-fact-meta-row">
                                <dt>Value</dt>
                                <dd>{{ item.value }}</dd>
                              </div>
                            </dl>
                          </article>
                        }
                      </div>
                    </div>
                  </section>

                  <section class="ssg-overview-section">
                    <div class="ssg-attributes-panel">
                      <div class="ssg-attributes-panel-header">
                        <h3>Placement</h3>
                      </div>
                      <div class="ssg-attributes-panel-body">
                        @if (!registryEditMode()) {
                          <section class="ssg-overview-panel">
                            <div class="ssg-overview-table">
                              <p-table [value]="registryPlacementRows()" [scrollable]="true" scrollHeight="flex" styleClass="p-datatable-sm">
                                <ng-template pTemplate="header">
                                  <tr>
                                    <th style="width: 34%">Attribute</th>
                                    <th>Value</th>
                                  </tr>
                                </ng-template>
                                <ng-template pTemplate="body" let-row>
                                  <tr>
                                    <td class="ssg-property-key">{{ row.label }}</td>
                                    <td class="ssg-property-value">{{ row.value }}</td>
                                  </tr>
                                </ng-template>
                              </p-table>
                            </div>
                          </section>
                        } @else {
                          <div class="ssg-builder-grid ssg-builder-grid-single">
                            <section class="ssg-builder-section">
                              <h5>Instance Details</h5>
                              <div class="ssg-builder-field">
                                <label for="component-instance-name">Instance Name</label>
                                <input id="component-instance-name" type="text" class="ssg-builder-input" [value]="accordionInstanceName()" (input)="onAccordionInstanceFieldChange('name', $event)">
                              </div>
                              <div class="ssg-builder-field">
                                <label for="component-instance-status">Status</label>
                                <select id="component-instance-status" class="ssg-builder-input" [value]="accordionInstanceStatus()" (change)="onAccordionInstanceFieldChange('status', $event)">
                                  <option value="draft">draft</option>
                                  <option value="active">active</option>
                                  <option value="planned">planned</option>
                                  <option value="hold">hold</option>
                                  <option value="retired">retired</option>
                                </select>
                              </div>
                              <div class="ssg-builder-field">
                                <label for="component-instance-description">Description</label>
                                <textarea id="component-instance-description" class="ssg-builder-input ssg-builder-textarea" (input)="onAccordionInstanceFieldChange('description', $event)">{{ accordionInstanceDescription() }}</textarea>
                              </div>
                              <div class="ssg-builder-field">
                                <label for="component-target-object">Target Object</label>
                                <select id="component-target-object" class="ssg-builder-input" [value]="accordionTargetObjectCode()" (change)="onAccordionInstanceFieldChange('targetObjectCode', $event)">
                                  <option value="">Unassigned</option>
                                  @for (option of registryTargetOptions(); track option.code) {
                                    <option [value]="option.code">{{ option.label }}</option>
                                  }
                                </select>
                              </div>
                            </section>
                          </div>
                        }
                      </div>
                    </div>
                  </section>

                  <section class="ssg-overview-section">
                    <div class="ssg-relationships-panel">
                      <div class="ssg-relationships-panel-header">
                        <h3>Configuration</h3>
                      </div>
                      <div class="ssg-relationships-panel-body">
                        @if (registryInstance.assetType === 'accordion') {
                          @if (!registryEditMode()) {
                            <div class="ssg-registry-tab-panel">
                              <div class="ssg-registry-table-block">
                                <div class="ssg-overview-panel-header">
                                  <h4>Accordion Configuration</h4>
                                </div>
                                <div class="ssg-overview-table">
                                  <p-table [value]="accordionConfigurationRows()" [scrollable]="true" scrollHeight="flex" styleClass="p-datatable-sm">
                                    <ng-template pTemplate="header">
                                      <tr>
                                        <th style="width: 34%">Attribute</th>
                                        <th>Value</th>
                                      </tr>
                                    </ng-template>
                                    <ng-template pTemplate="body" let-row>
                                      <tr>
                                        <td class="ssg-property-key">{{ row.label }}</td>
                                        <td class="ssg-property-value">{{ row.value }}</td>
                                      </tr>
                                    </ng-template>
                                  </p-table>
                                </div>
                              </div>
                              <div class="ssg-registry-table-block">
                                <div class="ssg-overview-panel-header">
                                  <h4>Panels</h4>
                                </div>
                                <div class="ssg-overview-table">
                                  <p-table [value]="accordionBuilderStaticPanels()" [scrollable]="true" scrollHeight="flex" styleClass="p-datatable-sm">
                                    <ng-template pTemplate="header">
                                      <tr>
                                        <th style="width: 14%">Value</th>
                                        <th style="width: 24%">Header</th>
                                        <th>Content</th>
                                        <th style="width: 14%">Disabled</th>
                                      </tr>
                                    </ng-template>
                                    <ng-template pTemplate="body" let-row>
                                      <tr>
                                        <td class="ssg-property-value">{{ row.value }}</td>
                                        <td class="ssg-property-value">{{ row.header }}</td>
                                        <td class="ssg-property-value">{{ row.content }}</td>
                                        <td class="ssg-property-value">{{ row.disabled ? 'true' : 'false' }}</td>
                                      </tr>
                                    </ng-template>
                                  </p-table>
                                </div>
                              </div>
                            </div>
                          } @else {
                            <div class="ssg-registry-builder">
                              <div class="ssg-overview-panel-header">
                                <h4>Accordion Configurator</h4>
                              </div>
                              <div class="ssg-builder-grid ssg-builder-grid-2">
                                <section class="ssg-builder-section">
                                  <h5>Content</h5>
                                  <div class="ssg-builder-field">
                                    <label for="accordion-render-method">Render Method</label>
                                    <select id="accordion-render-method" class="ssg-builder-input" [value]="accordionBuilderRenderMethod()" (change)="onAccordionBuilderRenderMethodChange($event)">
                                      <option value="Static">Static</option>
                                      <option value="Dynamic">Dynamic</option>
                                    </select>
                                  </div>
                                  @if (accordionBuilderRenderMethod() === 'Static') {
                                    <div class="ssg-builder-field">
                                      <label for="accordion-panel-count">Panel Count</label>
                                      <input id="accordion-panel-count" type="number" min="1" max="6" class="ssg-builder-input" [value]="accordionBuilderPanelCount()" (input)="onAccordionBuilderPanelCountChange($event)">
                                    </div>
                                    <div class="ssg-builder-panel-list">
                                      @for (panel of accordionBuilderStaticPanels(); track $index; let i = $index) {
                                        <article class="ssg-builder-panel-card">
                                          <h6>Panel {{ i + 1 }}</h6>
                                          <div class="ssg-builder-field">
                                            <label [attr.for]="'accordion-panel-value-' + i">Value</label>
                                            <input [id]="'accordion-panel-value-' + i" type="text" class="ssg-builder-input" [value]="panel.value" (input)="onAccordionBuilderPanelFieldChange(i, 'value', $event)">
                                          </div>
                                          <div class="ssg-builder-field">
                                            <label [attr.for]="'accordion-panel-header-' + i">Header</label>
                                            <input [id]="'accordion-panel-header-' + i" type="text" class="ssg-builder-input" [value]="panel.header" (input)="onAccordionBuilderPanelFieldChange(i, 'header', $event)">
                                          </div>
                                          <div class="ssg-builder-field">
                                            <label [attr.for]="'accordion-panel-content-' + i">Content</label>
                                            <textarea [id]="'accordion-panel-content-' + i" class="ssg-builder-input ssg-builder-textarea" (input)="onAccordionBuilderPanelFieldChange(i, 'content', $event)">{{ panel.content }}</textarea>
                                          </div>
                                          <label class="ssg-builder-checkbox">
                                            <input type="checkbox" [checked]="panel.disabled" (change)="onAccordionBuilderPanelDisabledChange(i, $event)">
                                            <span>Disabled</span>
                                          </label>
                                        </article>
                                      }
                                    </div>
                                  } @else {
                                    <div class="ssg-builder-field">
                                      <label for="accordion-data-source">Data Source Variable</label>
                                      <input id="accordion-data-source" type="text" class="ssg-builder-input" [value]="accordionBuilderDataSource()" (input)="onAccordionBuilderSimpleFieldChange('dataSource', $event)">
                                    </div>
                                    <div class="ssg-builder-field-grid">
                                      <div class="ssg-builder-field">
                                        <label for="accordion-value-field">Value Field</label>
                                        <input id="accordion-value-field" type="text" class="ssg-builder-input" [value]="accordionBuilderValueField()" (input)="onAccordionBuilderSimpleFieldChange('valueField', $event)">
                                      </div>
                                      <div class="ssg-builder-field">
                                        <label for="accordion-header-field">Header Field</label>
                                        <input id="accordion-header-field" type="text" class="ssg-builder-input" [value]="accordionBuilderHeaderField()" (input)="onAccordionBuilderSimpleFieldChange('headerField', $event)">
                                      </div>
                                      <div class="ssg-builder-field">
                                        <label for="accordion-content-field">Content Field</label>
                                        <input id="accordion-content-field" type="text" class="ssg-builder-input" [value]="accordionBuilderContentField()" (input)="onAccordionBuilderSimpleFieldChange('contentField', $event)">
                                      </div>
                                      <div class="ssg-builder-field">
                                        <label for="accordion-disabled-field">Disabled Field</label>
                                        <input id="accordion-disabled-field" type="text" class="ssg-builder-input" [value]="accordionBuilderDisabledField()" (input)="onAccordionBuilderSimpleFieldChange('disabledField', $event)">
                                      </div>
                                    </div>
                                  }
                                </section>
                                <section class="ssg-builder-section">
                                  <h5>Behavior</h5>
                                  <label class="ssg-builder-checkbox">
                                    <input type="checkbox" [checked]="accordionBuilderMultiple()" (change)="onAccordionBuilderBooleanChange('multiple', $event)">
                                    <span>Allow multiple open panels</span>
                                  </label>
                                  <label class="ssg-builder-checkbox">
                                    <input type="checkbox" [checked]="accordionBuilderSelectOnFocus()" (change)="onAccordionBuilderBooleanChange('selectOnFocus', $event)">
                                    <span>Select on focus</span>
                                  </label>
                                  <div class="ssg-builder-field">
                                    <label for="accordion-default-value">Default Expanded Value</label>
                                    <input id="accordion-default-value" type="text" class="ssg-builder-input" [value]="accordionBuilderDefaultValue()" (input)="onAccordionBuilderSimpleFieldChange('defaultValue', $event)">
                                  </div>
                                  <div class="ssg-builder-field-grid">
                                    <div class="ssg-builder-field">
                                      <label for="accordion-expand-icon">Expand Icon</label>
                                      <input id="accordion-expand-icon" type="text" class="ssg-builder-input" [value]="accordionBuilderExpandIcon()" (input)="onAccordionBuilderSimpleFieldChange('expandIcon', $event)">
                                    </div>
                                    <div class="ssg-builder-field">
                                      <label for="accordion-collapse-icon">Collapse Icon</label>
                                      <input id="accordion-collapse-icon" type="text" class="ssg-builder-input" [value]="accordionBuilderCollapseIcon()" (input)="onAccordionBuilderSimpleFieldChange('collapseIcon', $event)">
                                    </div>
                                  </div>
                                </section>
                              </div>
                            </div>
                          }
                        } @else {
                          <div class="ssg-overview-panel">
                            <div class="ssg-overview-panel-header">
                              <h4>Configuration</h4>
                              <p>No specialized configurator is available for this asset type yet.</p>
                            </div>
                          </div>
                        }
                      </div>
                    </div>
                  </section>
                </div>
              } @else {
                <div class="ssg-overview-tab">
                  <div class="ssg-fact-sheet">
                    <header class="ssg-fact-header">
                      <div class="ssg-fact-logo-panel">
                        <div class="ssg-fact-logo" aria-hidden="true">{{ selectedFactSheetHeader().familyMark }}</div>
                      </div>
                      <div class="ssg-fact-header-content">
                        <h3>{{ selectedFactSheetHeader().title }}</h3>
                        <dl class="ssg-fact-meta-list">
                          <div class="ssg-fact-meta-row">
                            <dt>ID</dt>
                            <dd class="ssg-fact-id-value">{{ selectedFactSheetHeader().id }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Type</dt>
                            <dd>{{ selectedFactSheetHeader().family }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>GUID</dt>
                            <dd>{{ selectedFactSheetHeader().guid }}</dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Status</dt>
                            <dd>
                              <span class="ssg-status-chip">{{ selectedFactSheetHeader().status }}</span>
                            </dd>
                          </div>
                          <div class="ssg-fact-meta-row">
                            <dt>Description</dt>
                            <dd>{{ selectedFactSheetHeader().description }}</dd>
                          </div>
                        </dl>
                      </div>
                    </header>

                  </div>

                  <section class="ssg-overview-section">
                    <div class="ssg-insights-panel">
                      <div class="ssg-insights-panel-header">
                        <h3>Insights</h3>
                      </div>
                      <div class="ssg-insights-panel-body ssg-insights-grid">
                        @for (item of selectedFactSheetSummary(); track item.label) {
                          <article class="ssg-insight-stat-panel">
                            <h4>{{ item.label }}</h4>
                            <dl class="ssg-fact-meta-list">
                              <div class="ssg-fact-meta-row">
                                <dt>Count</dt>
                                <dd>{{ item.value }}</dd>
                              </div>
                            </dl>
                          </article>
                        }
                      </div>
                    </div>
                  </section>

                  <section class="ssg-overview-section">
                    <div class="ssg-relationships-panel">
                      <div class="ssg-relationships-panel-header">
                        <h3>Design Issues</h3>
                        <button type="button" class="ssg-table-link" (click)="openIssuesTab()">
                          Open Issues Tab
                        </button>
                      </div>
                      <div class="ssg-relationships-panel-body">
                        @if (selectedInspectorIssues().length) {
                          <div class="ssg-issue-toolbar">
                            <div class="ssg-issue-toolbar-copy">
                              <label class="ssg-issue-filter-field">
                                <span>Status</span>
                                <select
                                  class="ssg-issue-filter-select"
                                  [value]="inspectorIssueStatusFilter()"
                                  (change)="onInspectorIssueStatusFilterChange($event)"
                                >
                                  @for (option of inspectorIssueStatusOptions; track option.value) {
                                    <option [value]="option.value">{{ option.label }}</option>
                                  }
                                </select>
                              </label>
                              <label class="ssg-issue-filter-field">
                                <span>Category</span>
                                <select
                                  class="ssg-issue-filter-select"
                                  [value]="inspectorIssueCategoryFilter()"
                                  (change)="onInspectorIssueCategoryFilterChange($event)"
                                >
                                  @for (option of inspectorIssueCategoryOptions(); track option.value) {
                                    <option [value]="option.value">{{ option.label }}</option>
                                  }
                                </select>
                              </label>
                            </div>
                            <div class="ssg-issue-toolbar-actions">
                              <button type="button" class="ssg-table-link" (click)="openIssuesTab()">
                                Open Issues Tab
                              </button>
                            </div>
                          </div>
                          <div class="ssg-overview-table">
                            <p-table
                              [value]="filteredSelectedInspectorIssues()"
                              [first]="overviewInspectorIssuesFirst()"
                              [paginator]="true"
                              [rows]="5"
                              [rowsPerPageOptions]="[5, 10, 20]"
                              (onPage)="onOverviewInspectorIssuesPage($event)"
                              sortField="serialNumber"
                              [sortOrder]="1"
                              styleClass="p-datatable-sm"
                            >
                              <ng-template pTemplate="header">
                                <tr>
                                  <th style="width: 10%" pSortableColumn="serialNumber">Issue # <p-sortIcon field="serialNumber" /></th>
                                  <th style="width: 32%" pSortableColumn="targetName">Object <p-sortIcon field="targetName" /></th>
                                  <th style="width: 18%" pSortableColumn="category">Category <p-sortIcon field="category" /></th>
                                  <th style="width: 12%" pSortableColumn="status">Status <p-sortIcon field="status" /></th>
                                  <th style="width: 28%">Actions</th>
                                </tr>
                              </ng-template>
                              <ng-template pTemplate="body" let-issue>
                                <tr>
                                  <td class="ssg-property-value">{{ issue.serialNumber }}</td>
                                  <td class="ssg-property-value">{{ issue.targetName }}</td>
                                  <td class="ssg-property-value">{{ issue.category }}</td>
                                  <td class="ssg-property-value">
                                    <span class="ssg-status-chip" [class.is-closed]="issue.status === 'closed'">{{ issue.status }}</span>
                                  </td>
                                  <td class="ssg-property-value">
                                    <div class="ssg-issue-actions">
                                      <button type="button" class="ssg-table-link" (click)="focusInspectorIssue(issue)">Focus</button>
                                      <button type="button" class="ssg-table-link" (click)="viewInspectorIssue(issue)">View</button>
                                      <button
                                        type="button"
                                        class="ssg-table-link"
                                        [disabled]="issue.status === 'closed'"
                                        (click)="resolveInspectorIssue(issue)"
                                      >
                                        Resolve
                                      </button>
                                    </div>
                                  </td>
                                </tr>
                              </ng-template>
                              <ng-template pTemplate="emptymessage">
                                <tr>
                                  <td class="ssg-property-value" colspan="5">{{ inspectorIssueEmptyMessage() }}</td>
                                </tr>
                              </ng-template>
                            </p-table>
                          </div>
                        } @else {
                          <div class="ssg-overview-table">
                            <div class="ssg-properties-empty">{{ inspectorIssueEmptyMessage() }}</div>
                          </div>
                        }
                      </div>
                    </div>
                  </section>

                  <section class="ssg-overview-section">
                    <div class="ssg-attributes-panel">
                      <div class="ssg-attributes-panel-header">
                        <h3>Attributes</h3>
                      </div>
                      <div class="ssg-attributes-panel-body">
                        <section class="ssg-overview-panel">
                          <div class="ssg-overview-panel-header">
                            <h4>{{ selectedAttributePanelTitle() }}</h4>
                          </div>
                          <div class="ssg-overview-table">
                            <p-table
                              [value]="selectedAttributeRows()"
                              [scrollable]="true"
                              scrollHeight="flex"
                              styleClass="p-datatable-sm"
                            >
                              <ng-template pTemplate="header">
                                <tr>
                                  <th style="width: 34%">Attribute</th>
                                  <th>Value</th>
                                </tr>
                              </ng-template>
                              <ng-template pTemplate="body" let-row>
                                <tr>
                                  <td class="ssg-property-key">{{ row.label }}</td>
                                  <td class="ssg-property-value">
                                    @if (row.editable) {
                                      <p-inplace
                                        styleClass="ssg-attribute-inplace"
                                        (onActivate)="onAttributeEditorActivate(row)"
                                        (onDeactivate)="onAttributeEditorDeactivate(row)"
                                      >
                                        <ng-template pTemplate="display">
                                          <span class="ssg-attribute-display" [class.is-empty]="row.value === 'n/a'">
                                            {{ attributeDisplayValue(row) }}
                                          </span>
                                        </ng-template>
                                        <ng-template pTemplate="content" let-closeCallback="closeCallback">
                                          <div class="ssg-attribute-editor">
                                            @if (row.editorKind === 'enum') {
                                              <p-select
                                                appendTo="body"
                                                styleClass="ssg-builder-input ssg-attribute-editor-select"
                                                [options]="row.options"
                                                optionLabel="label"
                                                optionValue="value"
                                                [filter]="true"
                                                filterBy="label"
                                                filterPlaceholder="Search"
                                                placeholder="Select"
                                                [showClear]="true"
                                                [ngModel]="attributeDraftValue(row)"
                                                (ngModelChange)="onAttributeDraftValueChange(row, $event)"
                                              />
                                            } @else {
                                              <input
                                                type="text"
                                                pInputText
                                                class="ssg-builder-input ssg-attribute-editor-input"
                                                [value]="attributeDraftValue(row)"
                                                (input)="onAttributeDraftInput(row, $event)"
                                                (keydown.enter)="applyAttributeDraft(row, closeCallback, $event)"
                                                (keydown.escape)="cancelAttributeDraft(row, closeCallback, $event)"
                                              >
                                            }
                                            <div class="ssg-attribute-editor-actions">
                                              <button
                                                type="button"
                                                class="ssg-attribute-editor-button is-apply"
                                                aria-label="Apply"
                                                title="Apply"
                                                (click)="applyAttributeDraft(row, closeCallback, $event)"
                                              >
                                                <span class="pi pi-check" aria-hidden="true"></span>
                                              </button>
                                              <button
                                                type="button"
                                                class="ssg-attribute-editor-button is-cancel"
                                                aria-label="Cancel"
                                                title="Cancel"
                                                (click)="cancelAttributeDraft(row, closeCallback, $event)"
                                              >
                                                <span class="pi pi-times" aria-hidden="true"></span>
                                              </button>
                                            </div>
                                          </div>
                                        </ng-template>
                                      </p-inplace>
                                    } @else {
                                      {{ row.value }}
                                    }
                                  </td>
                                </tr>
                              </ng-template>
                              <ng-template pTemplate="emptymessage">
                                <tr>
                                  <td colspan="2" class="ssg-properties-empty">{{ selectedAttributeEmptyMessage() }}</td>
                                </tr>
                              </ng-template>
                            </p-table>
                          </div>
                        </section>
                        @if (selectedRemainingAttributeRows().length) {
                          <section class="ssg-overview-panel">
                            <div class="ssg-overview-panel-header">
                              <h4>Remaining Attributes</h4>
                            </div>
                            <div class="ssg-overview-table">
                              <p-table
                                [value]="selectedRemainingAttributeRows()"
                                [scrollable]="true"
                                scrollHeight="flex"
                                styleClass="p-datatable-sm"
                              >
                                <ng-template pTemplate="header">
                                  <tr>
                                    <th style="width: 34%">Attribute</th>
                                    <th>Value</th>
                                  </tr>
                                </ng-template>
                                <ng-template pTemplate="body" let-row>
                                  <tr>
                                    <td class="ssg-property-key">{{ row.label }}</td>
                                    <td class="ssg-property-value">{{ row.value }}</td>
                                  </tr>
                                </ng-template>
                              </p-table>
                            </div>
                          </section>
                        }
                      </div>
                    </div>
                  </section>

                  <section class="ssg-overview-section">
                    <div class="ssg-relationships-panel">
                      <div class="ssg-relationships-panel-header">
                        <h3>Relationships</h3>
                      </div>
                      <div class="ssg-relationships-panel-body">
                        @if (selectedRelationshipGroups().length) {
                          <p-tabs class="ssg-relationship-tabs" [value]="activeRelationshipTab()" (valueChange)="onRelationshipTabChange($event)">
                            <p-tablist>
                              @for (group of selectedRelationshipGroups(); track group.key) {
                                <p-tab [value]="group.key">{{ group.label }}</p-tab>
                              }
                            </p-tablist>
                            <p-tabpanels>
                              @for (group of selectedRelationshipGroups(); track group.key) {
                                <p-tabpanel [value]="group.key">
                                  <div class="ssg-overview-panel ssg-relationship-group-panel">
                                    <div class="ssg-overview-table">
                                      <p-table
                                        [value]="group.rows"
                                        [scrollable]="true"
                                        scrollHeight="flex"
                                        styleClass="p-datatable-sm"
                                      >
                                        <ng-template pTemplate="header">
                                          <tr>
                                            <th style="width: 22%">Object ID</th>
                                            <th style="width: 24%">Object Name</th>
                                            <th style="width: 18%">Type</th>
                                            <th style="width: 14%">Direction</th>
                                            <th>Object Type</th>
                                          </tr>
                                        </ng-template>
                                        <ng-template pTemplate="body" let-row>
                                          <tr>
                                            <td class="ssg-property-value">
                                              <button type="button" class="ssg-table-link" (click)="onRelatedObjectClick(row.connectedObjectId, row.connectedCode)">
                                                {{ row.connectedObjectId || row.connectedCode }}
                                              </button>
                                            </td>
                                            <td class="ssg-property-value">
                                              <button type="button" class="ssg-table-link" (click)="onRelatedObjectClick(row.connectedObjectId, row.connectedCode)">
                                                {{ row.connectedName }}
                                              </button>
                                            </td>
                                            <td class="ssg-property-value">{{ row.type }}</td>
                                            <td class="ssg-property-value">{{ row.direction }}</td>
                                            <td class="ssg-property-value">{{ row.connectedFamily }}</td>
                                          </tr>
                                        </ng-template>
                                      </p-table>
                                    </div>
                                  </div>
                                </p-tabpanel>
                              }
                            </p-tabpanels>
                          </p-tabs>
                        } @else {
                          <div class="ssg-overview-table">
                            <div class="ssg-properties-empty">No relationships available.</div>
                          </div>
                        }
                      </div>
                    </div>
                  </section>
                </div>
              }
            </p-tabpanel>
            <p-tabpanel value="preview">
              <div class="ssg-preview-shell">
                <div class="ssg-preview-toolbar">
                  <div class="ssg-preview-toolbar-copy">
                    <span class="ssg-preview-toolbar-title">
                      <svg class="ssg-agentic-icon" viewBox="0 0 24 24" aria-hidden="true">
                        <rect x="6.5" y="7" width="11" height="10" rx="2"></rect>
                        <path d="M9 7V5.75C9 4.78 9.78 4 10.75 4h2.5C14.22 4 15 4.78 15 5.75V7"></path>
                        <circle cx="10" cy="11.5" r="1"></circle>
                        <circle cx="14" cy="11.5" r="1"></circle>
                        <path d="M10 15c.45-.42 1.12-.67 2-.67s1.55.25 2 .67"></path>
                        <path d="M4.5 12h-1"></path>
                        <path d="M20.5 12h-1"></path>
                      </svg>
                      <span>Preview</span>
                    </span>
                    <span class="ssg-preview-toolbar-subtitle">
                      Render the selected design artifact in the active viewport.
                    </span>
                  </div>
                  <div class="ssg-preview-toolbar-field">
                    <span>Viewport</span>
                    <div class="ssg-preview-viewport-switch" role="group" aria-label="Viewport profile">
                      @for (profile of previewViewportProfiles; track profile.value) {
                        <button
                          type="button"
                          class="ssg-preview-viewport-button"
                          [class.is-active]="previewViewportProfile() === profile.value"
                          [attr.aria-label]="profile.label"
                          [attr.title]="profile.label"
                          (click)="setPreviewViewportProfile(profile.value)"
                        >
                          @if (profile.value === 'web') {
                            <svg viewBox="0 0 24 24" aria-hidden="true">
                              <rect x="3.5" y="4.5" width="17" height="11" rx="1.8"></rect>
                              <path d="M8 19.5h8"></path>
                              <path d="M12 15.5v4"></path>
                            </svg>
                          } @else if (profile.value === 'tablet') {
                            <svg viewBox="0 0 24 24" aria-hidden="true">
                              <rect x="6.5" y="3.5" width="11" height="17" rx="1.8"></rect>
                              <path d="M11 6h2"></path>
                              <path d="M11 18h2"></path>
                            </svg>
                          } @else {
                            <svg viewBox="0 0 24 24" aria-hidden="true">
                              <rect x="8" y="3.5" width="8" height="17" rx="1.8"></rect>
                              <path d="M11 6h2"></path>
                              <path d="M11 18h2"></path>
                            </svg>
                          }
                        </button>
                      }
                    </div>
                  </div>
                  <div class="ssg-preview-viewport-chip" [attr.aria-label]="previewViewportSummary()">
                    {{ previewViewportSummary() }}
                  </div>
                </div>
                <div class="ssg-preview-body">
                  <div class="ssg-preview-stage" #previewStage>
                    <div
                      class="ssg-preview-canvas-frame"
                      [style.width.px]="previewCanvasWidth() * previewScale()"
                      [style.height.px]="previewCanvasHeight() * previewScale()"
                    >
                      <div
                        class="ssg-preview-canvas"
                        [style.width.px]="previewCanvasWidth()"
                        [style.height.px]="previewCanvasHeight()"
                        [style.transform]="'scale(' + previewScale() + ')'"
                        [style.--ssg-preview-selected-color]="activePreviewInteractionPalette().selected"
                        [style.--ssg-preview-hover-color]="activePreviewInteractionPalette().hover"
                      >
                        <app-app-shell-preview
                          [selectedGuid]="selectedPreviewGuid()"
                          [shellCode]="activeShellCode()"
                          [backgroundConfig]="activeShellBackgroundConfig()"
                          [activeScreenCode]="activeScreenCode()"
                        />
                      </div>
                    </div>
                  </div>
                  <section class="ssg-relationships-panel ssg-preview-issues-panel">
                    <div class="ssg-relationships-panel-header ssg-preview-issues-panel-header">
                      <h3>Selected Object Issues</h3>
                      <span class="ssg-preview-issues-count">
                        {{ selectedPreviewObjectIssues().length }} issue{{ selectedPreviewObjectIssues().length === 1 ? '' : 's' }}
                      </span>
                    </div>
                    <div class="ssg-relationships-panel-body">
                      <div class="ssg-issue-toolbar">
                        <div class="ssg-issue-toolbar-copy">
                          <label class="ssg-issue-filter-field">
                            <span>Status</span>
                            <select
                              class="ssg-issue-filter-select"
                              [value]="inspectorIssueStatusFilter()"
                              (change)="onInspectorIssueStatusFilterChange($event)"
                            >
                              @for (option of inspectorIssueStatusOptions; track option.value) {
                                <option [value]="option.value">{{ option.label }}</option>
                              }
                            </select>
                          </label>
                        </div>
                      </div>
                      @if (selectedPreviewObjectIssues().length) {
                        <div class="ssg-overview-table ssg-preview-issues-table">
                          <p-table
                            [value]="selectedPreviewObjectIssues()"
                            [first]="previewObjectIssuesFirst()"
                            [paginator]="true"
                            [rows]="5"
                            [rowsPerPageOptions]="[5, 10]"
                            (onPage)="onPreviewObjectIssuesPage($event)"
                            sortField="serialNumber"
                            [sortOrder]="1"
                            styleClass="p-datatable-sm"
                          >
                            <ng-template pTemplate="header">
                              <tr>
                                <th style="width: 10%" pSortableColumn="serialNumber">Issue # <p-sortIcon field="serialNumber" /></th>
                                <th style="width: 20%" pSortableColumn="category">Category <p-sortIcon field="category" /></th>
                                <th style="width: 26%" pSortableColumn="rule">Rule <p-sortIcon field="rule" /></th>
                                <th>Message</th>
                                <th style="width: 18%">Actions</th>
                              </tr>
                            </ng-template>
                            <ng-template pTemplate="body" let-issue>
                              <tr>
                                <td class="ssg-property-value">{{ issue.serialNumber }}</td>
                                <td class="ssg-property-value">{{ issue.category }}</td>
                                <td class="ssg-property-value">{{ issue.rule }}</td>
                                <td class="ssg-property-value">{{ issue.message }}</td>
                                <td class="ssg-property-value">
                                  <div class="ssg-issue-actions">
                                    <button type="button" class="ssg-table-link" (click)="viewInspectorIssue(issue)">View</button>
                                    <button
                                      type="button"
                                      class="ssg-table-link"
                                      [disabled]="issue.status === 'closed'"
                                      (click)="resolveInspectorIssue(issue)"
                                    >
                                      Resolve
                                    </button>
                                  </div>
                                </td>
                              </tr>
                            </ng-template>
                          </p-table>
                        </div>
                      } @else {
                        <div class="ssg-overview-table ssg-preview-issues-table">
                          <div class="ssg-properties-empty">{{ previewIssueEmptyMessage() }}</div>
                        </div>
                      }
                    </div>
                  </section>
                </div>
                <div class="ssg-preview-audit" #previewAuditRoot aria-hidden="true" hidden inert>
                  <app-app-shell-preview [selectedGuid]="null" shellCode="SHL01" [backgroundConfig]="shellBackgroundConfigFor('SHL01')" activeScreenCode="SHL01.SCN01" />
                  <app-app-shell-preview [selectedGuid]="null" shellCode="SHL01" [backgroundConfig]="shellBackgroundConfigFor('SHL01')" activeScreenCode="SHL01.SCN02" />
                  <app-app-shell-preview [selectedGuid]="null" shellCode="SHL01" [backgroundConfig]="shellBackgroundConfigFor('SHL01')" activeScreenCode="SHL01.SCN03" />
                  <app-app-shell-preview [selectedGuid]="null" shellCode="SHL02" [backgroundConfig]="shellBackgroundConfigFor('SHL02')" activeScreenCode="SHL02.SCN01" />
                  <app-app-shell-preview [selectedGuid]="null" shellCode="SHL02" [backgroundConfig]="shellBackgroundConfigFor('SHL02')" activeScreenCode="SHL02.SCN02" />
                  <app-app-shell-preview [selectedGuid]="null" shellCode="SHL02" [backgroundConfig]="shellBackgroundConfigFor('SHL02')" activeScreenCode="SHL02.SCN03" />
                </div>
              </div>
            </p-tabpanel>
            <p-tabpanel value="issues">
              <div class="ssg-overview-tab">
                <section class="ssg-overview-section">
                  <div class="ssg-insights-panel">
                    <div class="ssg-insights-panel-header">
                      <h3>Issue Summary</h3>
                    </div>
                    <div class="ssg-insights-panel-body ssg-insights-grid">
                      @for (item of selectedInspectorIssueSummary(); track item.label) {
                        <article class="ssg-insight-stat-panel">
                          <h4>{{ item.label }}</h4>
                          <dl class="ssg-fact-meta-list">
                            <div class="ssg-fact-meta-row">
                              <dt>Value</dt>
                              <dd>{{ item.value }}</dd>
                            </div>
                          </dl>
                        </article>
                      }
                    </div>
                  </div>
                </section>

                <section class="ssg-overview-section">
                  <div class="ssg-relationships-panel">
                    <div class="ssg-relationships-panel-header">
                      <h3>Design Issues</h3>
                    </div>
                    <div class="ssg-relationships-panel-body">
                      @if (selectedInspectorIssues().length) {
                        <div class="ssg-issue-toolbar">
                          <div class="ssg-issue-toolbar-copy">
                            <label class="ssg-issue-filter-field">
                              <span>Status</span>
                              <select
                                class="ssg-issue-filter-select"
                                [value]="inspectorIssueStatusFilter()"
                                (change)="onInspectorIssueStatusFilterChange($event)"
                              >
                                @for (option of inspectorIssueStatusOptions; track option.value) {
                                  <option [value]="option.value">{{ option.label }}</option>
                                }
                              </select>
                            </label>
                            <label class="ssg-issue-filter-field">
                              <span>Category</span>
                              <select
                                class="ssg-issue-filter-select"
                                [value]="inspectorIssueCategoryFilter()"
                                (change)="onInspectorIssueCategoryFilterChange($event)"
                              >
                                @for (option of inspectorIssueCategoryOptions(); track option.value) {
                                  <option [value]="option.value">{{ option.label }}</option>
                                }
                              </select>
                            </label>
                            <span class="ssg-issue-selection-count">
                              {{ selectedInspectorIssueSelection().length }} selected
                            </span>
                          </div>
                          <div class="ssg-issue-toolbar-actions">
                            <button type="button" class="tree-toolbar-button" (click)="selectAllInspectorIssues()">
                              Select All
                            </button>
                            <button
                              type="button"
                              class="tree-toolbar-button"
                              [disabled]="selectedInspectorIssueSelection().length === 0"
                              (click)="clearInspectorIssueSelection()"
                            >
                              Clear
                            </button>
                            <button
                              type="button"
                              class="tree-toolbar-button"
                              [disabled]="selectedOpenInspectorIssueCount() === 0"
                              (click)="resolveSelectedInspectorIssues()"
                            >
                              Resolve Selected
                            </button>
                          </div>
                        </div>
                        <div class="ssg-overview-table">
                          <p-table
                            [value]="filteredSelectedInspectorIssues()"
                            [first]="issuesTabInspectorIssuesFirst()"
                            dataKey="code"
                            selectionMode="multiple"
                            [selection]="selectedInspectorIssueSelection()"
                            (selectionChange)="onInspectorIssueSelectionChange($event)"
                            [paginator]="true"
                            [rows]="10"
                            [rowsPerPageOptions]="[10, 20, 50]"
                            (onPage)="onIssuesTabInspectorIssuesPage($event)"
                            sortField="serialNumber"
                            [sortOrder]="1"
                            styleClass="p-datatable-sm"
                          >
                            <ng-template pTemplate="header">
                              <tr>
                                <th style="width: 5%">
                                  <p-tableHeaderCheckbox />
                                </th>
                                <th style="width: 9%" pSortableColumn="serialNumber">Issue # <p-sortIcon field="serialNumber" /></th>
                                <th style="width: 28%" pSortableColumn="targetName">Object <p-sortIcon field="targetName" /></th>
                                <th style="width: 19%" pSortableColumn="category">Category <p-sortIcon field="category" /></th>
                                <th style="width: 10%" pSortableColumn="status">Status <p-sortIcon field="status" /></th>
                                <th style="width: 29%">Actions</th>
                              </tr>
                            </ng-template>
                            <ng-template pTemplate="body" let-issue>
                              <tr>
                                <td class="ssg-property-value">
                                  <p-tableCheckbox [value]="issue" />
                                </td>
                                <td class="ssg-property-value">{{ issue.serialNumber }}</td>
                                <td class="ssg-property-value">{{ issue.targetName }}</td>
                                <td class="ssg-property-value">{{ issue.category }}</td>
                                <td class="ssg-property-value">
                                  <span class="ssg-status-chip" [class.is-closed]="issue.status === 'closed'">{{ issue.status }}</span>
                                </td>
                                <td class="ssg-property-value">
                                  <div class="ssg-issue-actions">
                                    <button type="button" class="ssg-table-link" (click)="focusInspectorIssue(issue)">Focus</button>
                                    <button type="button" class="ssg-table-link" (click)="viewInspectorIssue(issue)">View</button>
                                    <button
                                      type="button"
                                      class="ssg-table-link"
                                      [disabled]="issue.status === 'closed'"
                                      (click)="resolveInspectorIssue(issue)"
                                    >
                                      Resolve
                                    </button>
                                  </div>
                                </td>
                              </tr>
                            </ng-template>
                            <ng-template pTemplate="emptymessage">
                              <tr>
                                <td class="ssg-property-value" colspan="6">{{ inspectorIssueEmptyMessage() }}</td>
                              </tr>
                            </ng-template>
                          </p-table>
                        </div>
                      } @else {
                        <div class="ssg-overview-table">
                          <div class="ssg-properties-empty">{{ inspectorIssueEmptyMessage() }}</div>
                        </div>
                      }
                    </div>
                  </div>
                </section>
              </div>
            </p-tabpanel>
          </p-tabpanels>
        </p-tabs>
        } @else if (activeComponentInstanceView(); as registryInstance) {
          <div class="ssg-overview-tab">
            <div class="ssg-fact-sheet">
              <header class="ssg-fact-header">
                <div class="ssg-fact-logo-panel">
                  <div class="ssg-fact-logo" aria-hidden="true">CP</div>
                </div>
                <div class="ssg-fact-header-content">
                  <div class="ssg-fact-header-actions">
                    @if (!registryEditMode()) {
                      <button type="button" class="tree-toolbar-button" data-registry-action="edit" (click)="enableRegistryEdit()">Edit</button>
                    } @else {
                      <button type="button" class="tree-toolbar-button" [disabled]="registryInstanceSaving()" data-registry-action="save" (click)="saveRegistryInstance()">Save</button>
                      <button type="button" class="tree-toolbar-button" [disabled]="registryInstanceSaving()" data-registry-action="cancel" (click)="cancelRegistryEdit()">Cancel</button>
                    }
                  </div>
                  <h3>{{ registryFactSheetHeader().title }}</h3>
                  <dl class="ssg-fact-meta-list">
                    <div class="ssg-fact-meta-row">
                      <dt>ID</dt>
                      <dd class="ssg-fact-id-value">{{ registryFactSheetHeader().id }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Type</dt>
                      <dd>{{ registryFactSheetHeader().type }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Code</dt>
                      <dd>{{ registryFactSheetHeader().code }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Asset Name</dt>
                      <dd>{{ registryFactSheetHeader().assetName }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Asset Type</dt>
                      <dd>{{ registryFactSheetHeader().assetType }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Implementation Source</dt>
                      <dd class="ssg-fact-id-value">{{ registryFactSheetHeader().implementationSourcePath }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Status</dt>
                      <dd><span class="ssg-status-chip">{{ registryFactSheetHeader().status }}</span></dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Description</dt>
                      <dd>{{ registryFactSheetHeader().description }}</dd>
                    </div>
                  </dl>
                </div>
              </header>
            </div>

            <section class="ssg-overview-section">
              <div class="ssg-insights-panel">
                <div class="ssg-insights-panel-header">
                  <h3>Insights</h3>
                </div>
                <div class="ssg-insights-panel-body ssg-insights-grid">
                  @for (item of registryInstanceSummary(); track item.label) {
                    <article class="ssg-insight-stat-panel">
                      <h4>{{ item.label }}</h4>
                      <dl class="ssg-fact-meta-list">
                        <div class="ssg-fact-meta-row">
                          <dt>Value</dt>
                          <dd>{{ item.value }}</dd>
                        </div>
                      </dl>
                    </article>
                  }
                </div>
              </div>
            </section>

            <section class="ssg-overview-section">
              <div class="ssg-attributes-panel">
                <div class="ssg-attributes-panel-header">
                  <h3>Placement</h3>
                </div>
                <div class="ssg-attributes-panel-body">
                  @if (!registryEditMode()) {
                    <section class="ssg-overview-panel">
                      <div class="ssg-overview-table">
                        <p-table [value]="registryPlacementRows()" [scrollable]="true" scrollHeight="flex" styleClass="p-datatable-sm">
                          <ng-template pTemplate="header">
                            <tr>
                              <th style="width: 34%">Attribute</th>
                              <th>Value</th>
                            </tr>
                          </ng-template>
                          <ng-template pTemplate="body" let-row>
                            <tr>
                              <td class="ssg-property-key">{{ row.label }}</td>
                              <td class="ssg-property-value">{{ row.value }}</td>
                            </tr>
                          </ng-template>
                        </p-table>
                      </div>
                    </section>
                  } @else {
                    <div class="ssg-builder-grid ssg-builder-grid-single">
                      <section class="ssg-builder-section">
                        <h5>Instance Details</h5>
                        <div class="ssg-builder-field">
                          <label for="component-instance-name">Instance Name</label>
                          <input id="component-instance-name" type="text" class="ssg-builder-input" [value]="accordionInstanceName()" (input)="onAccordionInstanceFieldChange('name', $event)">
                        </div>
                        <div class="ssg-builder-field">
                          <label for="component-instance-status">Status</label>
                          <select id="component-instance-status" class="ssg-builder-input" [value]="accordionInstanceStatus()" (change)="onAccordionInstanceFieldChange('status', $event)">
                            <option value="draft">draft</option>
                            <option value="active">active</option>
                            <option value="planned">planned</option>
                            <option value="hold">hold</option>
                            <option value="retired">retired</option>
                          </select>
                        </div>
                        <div class="ssg-builder-field">
                          <label for="component-instance-description">Description</label>
                          <textarea id="component-instance-description" class="ssg-builder-input ssg-builder-textarea" (input)="onAccordionInstanceFieldChange('description', $event)">{{ accordionInstanceDescription() }}</textarea>
                        </div>
                        <div class="ssg-builder-field">
                          <label for="component-target-object">Target Object</label>
                          <select id="component-target-object" class="ssg-builder-input" [value]="accordionTargetObjectCode()" (change)="onAccordionInstanceFieldChange('targetObjectCode', $event)">
                            <option value="">Unassigned</option>
                            @for (option of registryTargetOptions(); track option.code) {
                              <option [value]="option.code">{{ option.label }}</option>
                            }
                          </select>
                        </div>
                      </section>
                    </div>
                  }
                </div>
              </div>
            </section>

            <section class="ssg-overview-section">
              <div class="ssg-relationships-panel">
                <div class="ssg-relationships-panel-header">
                  <h3>Configuration</h3>
                </div>
                <div class="ssg-relationships-panel-body">
                  @if (registryInstance.assetType === 'accordion') {
                    @if (!registryEditMode()) {
                      <div class="ssg-registry-tab-panel">
                        <div class="ssg-registry-table-block">
                          <div class="ssg-overview-panel-header">
                            <h4>Accordion Configuration</h4>
                          </div>
                          <div class="ssg-overview-table">
                            <p-table [value]="accordionConfigurationRows()" [scrollable]="true" scrollHeight="flex" styleClass="p-datatable-sm">
                              <ng-template pTemplate="header">
                                <tr>
                                  <th style="width: 34%">Attribute</th>
                                  <th>Value</th>
                                </tr>
                              </ng-template>
                              <ng-template pTemplate="body" let-row>
                                <tr>
                                  <td class="ssg-property-key">{{ row.label }}</td>
                                  <td class="ssg-property-value">{{ row.value }}</td>
                                </tr>
                              </ng-template>
                            </p-table>
                          </div>
                        </div>
                        <div class="ssg-registry-table-block">
                          <div class="ssg-overview-panel-header">
                            <h4>Panels</h4>
                          </div>
                          <div class="ssg-overview-table">
                            <p-table [value]="accordionBuilderStaticPanels()" [scrollable]="true" scrollHeight="flex" styleClass="p-datatable-sm">
                              <ng-template pTemplate="header">
                                <tr>
                                  <th style="width: 14%">Value</th>
                                  <th style="width: 24%">Header</th>
                                  <th>Content</th>
                                  <th style="width: 14%">Disabled</th>
                                </tr>
                              </ng-template>
                              <ng-template pTemplate="body" let-row>
                                <tr>
                                  <td class="ssg-property-value">{{ row.value }}</td>
                                  <td class="ssg-property-value">{{ row.header }}</td>
                                  <td class="ssg-property-value">{{ row.content }}</td>
                                  <td class="ssg-property-value">{{ row.disabled ? 'true' : 'false' }}</td>
                                </tr>
                              </ng-template>
                            </p-table>
                          </div>
                        </div>
                      </div>
                    } @else {
                      <div class="ssg-registry-builder">
                        <div class="ssg-overview-panel-header">
                          <h4>Accordion Configurator</h4>
                        </div>
                        <div class="ssg-builder-grid ssg-builder-grid-2">
                          <section class="ssg-builder-section">
                            <h5>Content</h5>
                            <div class="ssg-builder-field">
                              <label for="accordion-render-method">Render Method</label>
                              <select id="accordion-render-method" class="ssg-builder-input" [value]="accordionBuilderRenderMethod()" (change)="onAccordionBuilderRenderMethodChange($event)">
                                <option value="Static">Static</option>
                                <option value="Dynamic">Dynamic</option>
                              </select>
                            </div>
                            @if (accordionBuilderRenderMethod() === 'Static') {
                              <div class="ssg-builder-field">
                                <label for="accordion-panel-count">Panel Count</label>
                                <input id="accordion-panel-count" type="number" min="1" max="6" class="ssg-builder-input" [value]="accordionBuilderPanelCount()" (input)="onAccordionBuilderPanelCountChange($event)">
                              </div>
                              <div class="ssg-builder-panel-list">
                                @for (panel of accordionBuilderStaticPanels(); track $index; let i = $index) {
                                  <article class="ssg-builder-panel-card">
                                    <h6>Panel {{ i + 1 }}</h6>
                                    <div class="ssg-builder-field">
                                      <label [attr.for]="'accordion-panel-value-' + i">Value</label>
                                      <input [id]="'accordion-panel-value-' + i" type="text" class="ssg-builder-input" [value]="panel.value" (input)="onAccordionBuilderPanelFieldChange(i, 'value', $event)">
                                    </div>
                                    <div class="ssg-builder-field">
                                      <label [attr.for]="'accordion-panel-header-' + i">Header</label>
                                      <input [id]="'accordion-panel-header-' + i" type="text" class="ssg-builder-input" [value]="panel.header" (input)="onAccordionBuilderPanelFieldChange(i, 'header', $event)">
                                    </div>
                                    <div class="ssg-builder-field">
                                      <label [attr.for]="'accordion-panel-content-' + i">Content</label>
                                      <textarea [id]="'accordion-panel-content-' + i" class="ssg-builder-input ssg-builder-textarea" (input)="onAccordionBuilderPanelFieldChange(i, 'content', $event)">{{ panel.content }}</textarea>
                                    </div>
                                    <label class="ssg-builder-checkbox">
                                      <input type="checkbox" [checked]="panel.disabled" (change)="onAccordionBuilderPanelDisabledChange(i, $event)">
                                      <span>Disabled</span>
                                    </label>
                                  </article>
                                }
                              </div>
                            } @else {
                              <div class="ssg-builder-field">
                                <label for="accordion-data-source">Data Source Variable</label>
                                <input id="accordion-data-source" type="text" class="ssg-builder-input" [value]="accordionBuilderDataSource()" (input)="onAccordionBuilderSimpleFieldChange('dataSource', $event)">
                              </div>
                              <div class="ssg-builder-field-grid">
                                <div class="ssg-builder-field">
                                  <label for="accordion-value-field">Value Field</label>
                                  <input id="accordion-value-field" type="text" class="ssg-builder-input" [value]="accordionBuilderValueField()" (input)="onAccordionBuilderSimpleFieldChange('valueField', $event)">
                                </div>
                                <div class="ssg-builder-field">
                                  <label for="accordion-header-field">Header Field</label>
                                  <input id="accordion-header-field" type="text" class="ssg-builder-input" [value]="accordionBuilderHeaderField()" (input)="onAccordionBuilderSimpleFieldChange('headerField', $event)">
                                </div>
                                <div class="ssg-builder-field">
                                  <label for="accordion-content-field">Content Field</label>
                                  <input id="accordion-content-field" type="text" class="ssg-builder-input" [value]="accordionBuilderContentField()" (input)="onAccordionBuilderSimpleFieldChange('contentField', $event)">
                                </div>
                                <div class="ssg-builder-field">
                                  <label for="accordion-disabled-field">Disabled Field</label>
                                  <input id="accordion-disabled-field" type="text" class="ssg-builder-input" [value]="accordionBuilderDisabledField()" (input)="onAccordionBuilderSimpleFieldChange('disabledField', $event)">
                                </div>
                              </div>
                            }
                          </section>
                          <section class="ssg-builder-section">
                            <h5>Behavior</h5>
                            <label class="ssg-builder-checkbox">
                              <input type="checkbox" [checked]="accordionBuilderMultiple()" (change)="onAccordionBuilderBooleanChange('multiple', $event)">
                              <span>Allow multiple open panels</span>
                            </label>
                            <label class="ssg-builder-checkbox">
                              <input type="checkbox" [checked]="accordionBuilderSelectOnFocus()" (change)="onAccordionBuilderBooleanChange('selectOnFocus', $event)">
                              <span>Select on focus</span>
                            </label>
                            <div class="ssg-builder-field">
                              <label for="accordion-default-value">Default Expanded Value</label>
                              <input id="accordion-default-value" type="text" class="ssg-builder-input" [value]="accordionBuilderDefaultValue()" (input)="onAccordionBuilderSimpleFieldChange('defaultValue', $event)">
                            </div>
                            <div class="ssg-builder-field-grid">
                              <div class="ssg-builder-field">
                                <label for="accordion-expand-icon">Expand Icon</label>
                                <input id="accordion-expand-icon" type="text" class="ssg-builder-input" [value]="accordionBuilderExpandIcon()" (input)="onAccordionBuilderSimpleFieldChange('expandIcon', $event)">
                              </div>
                              <div class="ssg-builder-field">
                                <label for="accordion-collapse-icon">Collapse Icon</label>
                                <input id="accordion-collapse-icon" type="text" class="ssg-builder-input" [value]="accordionBuilderCollapseIcon()" (input)="onAccordionBuilderSimpleFieldChange('collapseIcon', $event)">
                              </div>
                            </div>
                          </section>
                        </div>
                      </div>
                    }
                  } @else {
                    <div class="ssg-overview-panel">
                      <div class="ssg-overview-panel-header">
                        <h4>Configuration</h4>
                        <p>No specialized configurator is available for this asset type yet.</p>
                      </div>
                    </div>
                  }
                </div>
              </div>
            </section>
          </div>
        } @else {
          <div class="ssg-inspector-empty-state" aria-label="Inspector is empty in Components Registry mode">
            <span>No active fact sheet</span>
          </div>
        }
      </section>

      <p-dock class="ssg-workspace-dock" [model]="workspaceDockItems" position="bottom" />

      @if (viewedInspectorIssue(); as issue) {
        <div class="ssg-issue-modal-backdrop" (click)="closeInspectorIssueModal()">
          <div class="ssg-issue-modal" role="dialog" aria-modal="true" aria-label="Issue details" (click)="$event.stopPropagation()">
            <div class="ssg-fact-sheet">
              <header class="ssg-fact-header">
                <div class="ssg-fact-logo-panel">
                  <div class="ssg-fact-logo" aria-hidden="true">IX</div>
                </div>
                <div class="ssg-fact-header-content">
                  <div class="ssg-fact-header-actions">
                    <span class="ssg-status-chip" [class.is-closed]="issue.status === 'closed'">{{ issue.status }}</span>
                    <button type="button" class="ssg-table-link" (click)="closeInspectorIssueModal()">Close</button>
                  </div>
                  <h3>{{ issue.name }}</h3>
                  <p>{{ issue.message }}</p>
                  <dl class="ssg-fact-meta-list">
                    <div class="ssg-fact-meta-row">
                      <dt>Issue #</dt>
                      <dd>{{ issue.serialNumber }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Object</dt>
                      <dd>{{ issue.targetName }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Object ID</dt>
                      <dd>{{ issue.targetObjectId ?? 'Not recorded' }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>GUID</dt>
                      <dd>{{ resolvedIssueGuid(issue) ?? 'Not recorded' }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Category</dt>
                      <dd>{{ issue.category }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Rule</dt>
                      <dd>{{ issue.rule }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Status</dt>
                      <dd><span class="ssg-status-chip" [class.is-closed]="issue.status === 'closed'">{{ issue.status }}</span></dd>
                    </div>
                  </dl>
                </div>
              </header>
              <section class="ssg-overview-section">
                <div class="ssg-overview-panel">
                  <div class="ssg-overview-panel-header">
                    <h4>Issue Details</h4>
                    <p>X-Ray Agent finding for the selected design object.</p>
                  </div>
                  <dl class="ssg-fact-meta-list">
                    <div class="ssg-fact-meta-row">
                      <dt>Description</dt>
                      <dd>{{ issue.description }}</dd>
                    </div>
                    <div class="ssg-fact-meta-row">
                      <dt>Severity</dt>
                      <dd>{{ issue.severity }}</dd>
                    </div>
                  </dl>
                </div>
              </section>
              <section class="ssg-overview-section">
                <div class="ssg-overview-panel">
                  <div class="ssg-overview-panel-header">
                    <h4>Agent Prompt</h4>
                    <p>Editable guidance for what the issue is and how to fix it.</p>
                  </div>
                  <div class="ssg-issue-prompt-panel">
                    <textarea
                      class="ssg-issue-prompt-textarea"
                      [value]="issuePromptDraft()"
                      (input)="onIssuePromptDraftChange($event)"
                      aria-label="Issue agent prompt"
                    ></textarea>
                    <div class="ssg-issue-prompt-actions">
                      <button
                        type="button"
                        class="tree-toolbar-button"
                        [disabled]="issuePromptSaving()"
                        (click)="saveInspectorIssuePrompt(issue)"
                      >
                        {{ issuePromptSaving() ? 'Saving...' : 'Save Prompt' }}
                      </button>
                    </div>
                  </div>
                </div>
              </section>
              <section class="ssg-overview-section">
                <div class="ssg-issue-modal-actions">
                  <button type="button" class="tree-toolbar-button" (click)="focusInspectorIssue(issue)">Go To Object</button>
                  <button
                    type="button"
                    class="tree-toolbar-button"
                    [disabled]="issuePromptSaving()"
                    (click)="saveInspectorIssuePrompt(issue)"
                  >
                    {{ issuePromptSaving() ? 'Saving...' : 'Save Prompt' }}
                  </button>
                  <button
                    type="button"
                    class="tree-toolbar-button"
                    [disabled]="issue.status === 'closed'"
                    (click)="resolveInspectorIssue(issue)"
                  >
                    Resolve Issue
                  </button>
                </div>
              </section>
            </div>
          </div>
        </div>
      }

      @if (xrayModalOpen()) {
        <div class="ssg-issue-modal-backdrop" (click)="closeXRaySummaryModal()">
          <div class="ssg-issue-modal ssg-xray-summary-modal" role="dialog" aria-modal="true" aria-label="X-Ray summary" (click)="$event.stopPropagation()">
            <div class="ssg-fact-sheet">
              <header class="ssg-fact-header">
                <div class="ssg-fact-logo-panel">
                  <div class="ssg-fact-logo" aria-hidden="true">XR</div>
                </div>
                <div class="ssg-fact-header-content">
                  <div class="ssg-fact-header-actions">
                    <button type="button" class="ssg-table-link" [disabled]="xrayRunning()" (click)="closeXRaySummaryModal()">Close</button>
                  </div>
                  <h3>{{ xrayRunning() ? 'X-Ray Agent Running' : 'X-Ray Agent Completed' }}</h3>
                  <p>{{ xrayRunning() ? xrayProgressMessage() : (xrayConclusion()?.conclusionText ?? 'The full design scan finished and the issue registry was refreshed.') }}</p>
                </div>
              </header>
              <section class="ssg-overview-section">
                <div class="ssg-insights-panel ssg-xray-progress-panel">
                  <div class="ssg-insights-panel-header">
                    <h4>{{ xrayRunning() ? 'Scan Progress' : 'Scan Conclusion' }}</h4>
                    <p>{{ xrayProgressLabel() }}</p>
                  </div>
                  <div class="ssg-xray-progress-track" aria-hidden="true">
                    <div class="ssg-xray-progress-bar" [style.width.%]="xrayProgressPercent()"></div>
                  </div>
                  <div class="ssg-xray-progress-caption">{{ xrayProgressPercent() }}%</div>
                  <div class="ssg-xray-stage-list">
                    @for (stage of xrayStageStates(); track stage.key) {
                      <article class="ssg-xray-stage-row" [class.is-active]="stage.status === 'active'" [class.is-complete]="stage.status === 'completed'">
                        <div class="ssg-xray-stage-mark" aria-hidden="true">
                          @if (stage.status === 'completed') {
                            <span>✓</span>
                          } @else if (stage.status === 'active') {
                            <span>•</span>
                          } @else {
                            <span>○</span>
                          }
                        </div>
                        <div class="ssg-xray-stage-content">
                          <h5>{{ stage.label }}</h5>
                          <p>{{ stage.description }}</p>
                        </div>
                      </article>
                    }
                  </div>
                </div>
              </section>
              @if (registryError()) {
                <section class="ssg-overview-section">
                  <div class="ssg-empty ssg-error">{{ registryError() }}</div>
                </section>
              }
              @if (xrayConclusion(); as conclusion) {
                <section class="ssg-overview-section">
                  <div class="ssg-insights-panel">
                    <div class="ssg-insights-panel-header">
                      <h4>Scan Summary</h4>
                    </div>
                    <div class="ssg-insights-panel-body ssg-insights-grid">
                      <article class="ssg-insight-stat-panel">
                        <h4>Graph Objects</h4>
                        <dl class="ssg-fact-meta-list"><div class="ssg-fact-meta-row"><dt>Value</dt><dd>{{ conclusion.totalObjectsScanned }}</dd></div></dl>
                      </article>
                      <article class="ssg-insight-stat-panel">
                        <h4>Preview Artifacts</h4>
                        <dl class="ssg-fact-meta-list"><div class="ssg-fact-meta-row"><dt>Value</dt><dd>{{ conclusion.totalPreviewArtifactsScanned }}</dd></div></dl>
                      </article>
                      <article class="ssg-insight-stat-panel">
                        <h4>Total Issues</h4>
                        <dl class="ssg-fact-meta-list"><div class="ssg-fact-meta-row"><dt>Value</dt><dd>{{ conclusion.summary?.totalIssues ?? 0 }}</dd></div></dl>
                      </article>
                      <article class="ssg-insight-stat-panel">
                        <h4>New Issues</h4>
                        <dl class="ssg-fact-meta-list"><div class="ssg-fact-meta-row"><dt>Value</dt><dd>{{ conclusion.summary?.newIssues ?? 0 }}</dd></div></dl>
                      </article>
                      <article class="ssg-insight-stat-panel">
                        <h4>Existing Issues</h4>
                        <dl class="ssg-fact-meta-list"><div class="ssg-fact-meta-row"><dt>Value</dt><dd>{{ conclusion.summary?.existingIssues ?? 0 }}</dd></div></dl>
                      </article>
                    </div>
                  </div>
                </section>
                <section class="ssg-overview-section">
                  <div class="ssg-overview-panel">
                    <div class="ssg-overview-panel-header">
                      <h4>Graph Inventory</h4>
                      <p>Neo4j structural object counts for Shell, Screen, Section, Element, and Component.</p>
                    </div>
                    <dl class="ssg-fact-meta-list">
                      @for (row of conclusion.graphObjectRows; track row.label) {
                        <div class="ssg-fact-meta-row">
                          <dt>{{ row.label }}</dt>
                          <dd>{{ row.value }}</dd>
                        </div>
                      }
                    </dl>
                  </div>
                  <div class="ssg-overview-panel">
                    <div class="ssg-overview-panel-header">
                      <h4>Preview Inventory</h4>
                      <p>Rendered preview canvas artifact counts across native HTML and PrimeNG output.</p>
                    </div>
                    <dl class="ssg-fact-meta-list">
                      @for (row of conclusion.previewSummaryRows; track row.label) {
                        <div class="ssg-fact-meta-row">
                          <dt>{{ row.label }}</dt>
                          <dd>{{ row.value }}</dd>
                        </div>
                      }
                    </dl>
                  </div>
                </section>
                <section class="ssg-overview-section">
                  <div class="ssg-overview-panel">
                    <div class="ssg-overview-panel-header">
                      <h4>Completeness Findings</h4>
                      <p>Identity and binding gaps found during the scan.</p>
                    </div>
                    <dl class="ssg-fact-meta-list">
                      <div class="ssg-fact-meta-row"><dt>Missing Object ID</dt><dd>{{ conclusion.missingObjectIdCount }}</dd></div>
                      <div class="ssg-fact-meta-row"><dt>Missing Object GUID</dt><dd>{{ conclusion.missingObjectGuidCount }}</dd></div>
                      <div class="ssg-fact-meta-row"><dt>Missing Preview source-object-id</dt><dd>{{ conclusion.missingPreviewObjectIdCount }}</dd></div>
                      <div class="ssg-fact-meta-row"><dt>Missing Preview guid</dt><dd>{{ conclusion.missingPreviewGuidCount }}</dd></div>
                      <div class="ssg-fact-meta-row"><dt>Unmodeled Preview Artifacts</dt><dd>{{ conclusion.unmodeledPreviewArtifactCount }}</dd></div>
                      <div class="ssg-fact-meta-row"><dt>Unselectable Preview Artifacts</dt><dd>{{ conclusion.unselectablePreviewArtifactCount }}</dd></div>
                    </dl>
                  </div>
                  <div class="ssg-overview-panel">
                    <div class="ssg-overview-panel-header">
                      <h4>Issue Categories</h4>
                      <p>Detected issue totals grouped by X-Ray category.</p>
                    </div>
                    <dl class="ssg-fact-meta-list">
                      @for (row of conclusion.issueCategoryRows; track row.label) {
                        <div class="ssg-fact-meta-row">
                          <dt>{{ row.label }}</dt>
                          <dd>{{ row.value }}</dd>
                        </div>
                      }
                    </dl>
                  </div>
                </section>
                <section class="ssg-overview-section">
                  <div class="ssg-overview-panel ssg-xray-tag-panel">
                    <div class="ssg-overview-panel-header">
                      <h4>Preview Tag Counts</h4>
                      <p>All rendered preview tag totals found during the canvas scan.</p>
                    </div>
                    <div class="ssg-xray-tag-grid">
                      @for (row of conclusion.previewTagRows; track row.label) {
                        <div class="ssg-xray-tag-chip">
                          <span>{{ row.label }}</span>
                          <strong>{{ row.value }}</strong>
                        </div>
                      }
                    </div>
                  </div>
                </section>
                <section class="ssg-overview-section">
                  <div class="ssg-issue-modal-actions">
                    <button type="button" class="tree-toolbar-button" (click)="openIssuesTabFromSummary()">View Issues</button>
                    <button type="button" class="tree-toolbar-button" (click)="closeXRaySummaryModal()">Close</button>
                  </div>
                </section>
              }
            </div>
          </div>
        </div>
      }
    </div>
  `,
  styles: [`
    :host {
      --tp-primary: #428177;
      --tp-primary-dark: #054239;
      --tp-surface: #f2efe9;
      --tp-surface-raised: #faf8f4;
      --tp-surface-light: #faf8f5;
      --tp-bg: #f2efe9;
      --tp-text: #3d3a3b;
      --tp-text-dark: #2a241c;
      --tp-text-muted: #999590;
      --tp-text-secondary: #7a7672;
      --tp-border: #e0ddda;
      --tp-border-light: #e8e5e0;
      --tp-grey: #999590;
      --tp-grey-light: #d4d1cc;
      --tp-danger: #6b1f2a;
      --tp-error: #ef4444;
      --tp-warning: #988561;
      --nm-radius: 16px;
      --nm-radius-sm: 8px;
      --nm-radius-md: 12px;
      --nm-radius-lg: 16px;
      --nm-radius-xl: 24px;
      --nm-radius-pill: 999px;
      --tp-space-1: 0.25rem;
      --tp-space-2: 0.5rem;
      --tp-space-3: 0.75rem;
      --tp-space-4: 1rem;
      --tp-space-5: 1.25rem;
      --tp-space-6: 1.5rem;
      --tp-space-8: 2rem;
      --tp-space-10: 2.5rem;
      --tp-space-12: 3rem;
      --tp-font-sm: 0.82rem;
      --tp-font-md: 1rem;
      --tp-font-lg: 1.15rem;
      --tp-font-xl: 1.35rem;
      --ssg-page-gap: var(--tp-space-4);
      --ssg-panel-radius: var(--nm-radius);
      --ssg-panel-padding: var(--tp-space-4);
      --ssg-shell-width: 840px;
      --ssg-shell-stack-width: 520px;
      --ssg-shell-radius: 1.5rem;
      --ssg-shell-border: 1px solid color-mix(in srgb, var(--tp-border) 56%, transparent);
      --ssg-shell-shadow: var(--tp-elevation-default);
      --ssg-shell-bg: var(--tp-surface);
      --ssg-shell-accent-bg: var(--tp-primary);
      --ssg-shell-accent-text: #ffffff;
      --ssg-login-field-border-color: color-mix(in srgb, var(--tp-surface-light) 10%, transparent);
      --ssg-login-field-focus-border-color: color-mix(in srgb, var(--tp-primary) 60%, transparent);
      --ssg-login-field-radius: 28px;
      --ssg-login-field-placeholder-color: color-mix(in srgb, var(--ssg-shell-accent-text) 74%, transparent);
      --ssg-login-field-icon-color: color-mix(in srgb, var(--ssg-shell-accent-text) 88%, transparent);
      --ssg-login-primary-button-radius: 24px;
      --ssg-login-primary-button-text: var(--ssg-shell-accent-text);
      --ssg-login-primary-button-border: color-mix(in srgb, var(--tp-surface-light) 10%, transparent);
      --ssg-login-secondary-button-text: var(--ssg-shell-accent-text);
      --ssg-outline-focus: 0 0 0 3px color-mix(in srgb, var(--tp-primary) 18%, transparent);

      display: block;
      height: 100vh;
      overflow: auto;
      font-family: var(--tp-font-family, 'Gotham Rounded', 'Nunito', 'Segoe UI', -apple-system, BlinkMacSystemFont, sans-serif);
      background: color-mix(in srgb, var(--tp-surface) 86%, var(--tp-bg));
    }

    .ssg-page {
      display: grid;
      grid-template-columns: minmax(22rem, 29rem) minmax(0, 1fr);
      gap: var(--tp-space-6);
      height: 100%;
      min-width: 1240px;
      padding: var(--tp-space-6);
      padding-bottom: calc(var(--tp-space-6) + 6.5rem);
      position: relative;
    }

    .ssg-panel {
      min-height: 0;
      display: grid;
      grid-template-rows: auto minmax(0, 1fr);
      border: 1px solid color-mix(in srgb, var(--tp-border) 40%, transparent);
      border-radius: var(--nm-radius-lg);
      background: var(--tp-surface-raised);
      box-shadow: var(--nm-shadow-island, 0 2px 8px rgba(0, 0, 0, 0.04), 0 0 0 1px rgba(0, 0, 0, 0.03));
      overflow: hidden;
    }

    .ssg-panel-tree {
      grid-template-rows: auto auto minmax(0, 1fr);
    }

    .ssg-side-menu-title-section,
    .ssg-panel-header {
      display: block;
      padding: var(--tp-space-4) var(--tp-space-5);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 36%, transparent);
      background: var(--tp-surface-raised);
    }

    .ssg-side-menu-controls-section {
      display: block;
      padding: var(--tp-space-4) var(--tp-space-5);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 36%, transparent);
      background: var(--tp-surface-raised);
    }

    .ssg-side-menu-tree-section {
      min-height: 0;
      display: grid;
      grid-template-rows: minmax(0, 1fr) auto;
    }

    .ssg-side-menu-title-section h1,
    .ssg-panel-header h1,
    .ssg-panel-header h2 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: var(--tp-font-xl);
      font-weight: 700;
    }

    .ssg-search-bar {
      margin-top: var(--tp-space-4);
    }

    .ssg-search-input {
      width: 100%;
      min-height: 42px;
      border: 1px solid color-mix(in srgb, var(--tp-border) 72%, transparent);
      border-radius: var(--nm-radius-pill);
      background: var(--tp-surface-light);
      color: var(--tp-text-dark);
      padding: 0.7rem 1rem;
      font: inherit;
      font-size: 0.84rem;
      outline: none;
    }

    .ssg-search-input::placeholder {
      color: var(--tp-text-muted);
    }

    .ssg-search-input:focus {
      border-color: color-mix(in srgb, var(--tp-primary) 32%, transparent);
      box-shadow: 0 0 0 3px color-mix(in srgb, var(--tp-primary) 12%, transparent);
    }

    .ssg-registry-summary {
      margin: 0 0 var(--tp-space-4);
      color: var(--tp-text);
      line-height: 1.6;
    }

    .ssg-registry-tab-panel {
      display: grid;
      gap: var(--tp-space-4);
    }

    .ssg-registry-table-block {
      display: grid;
      gap: var(--tp-space-3);
    }

    .ssg-builder-grid,
    .ssg-builder-output-grid {
      display: grid;
      gap: var(--tp-space-4);
    }

    .ssg-builder-grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
      margin-bottom: var(--tp-space-4);
    }

    .ssg-builder-grid-single {
      grid-template-columns: minmax(0, 1fr);
      margin-bottom: 0;
    }

    .ssg-builder-grid-2 {
      grid-template-columns: repeat(2, minmax(0, 1fr));
      margin-bottom: 0;
    }

    .ssg-builder-output-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .ssg-builder-section {
      display: grid;
      gap: var(--tp-space-3);
      padding: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-border) 42%, transparent);
      border-radius: var(--nm-radius);
      background: var(--tp-surface-raised);
    }

    .ssg-builder-section h5,
    .ssg-builder-panel-card h6 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: var(--tp-font-md);
      font-weight: 700;
    }

    .ssg-builder-field,
    .ssg-builder-field-grid {
      display: grid;
      gap: var(--tp-space-2);
    }

    .ssg-builder-field-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .ssg-builder-field label {
      color: var(--tp-text-secondary);
      font-size: var(--tp-font-sm);
      font-weight: 700;
    }

    .ssg-builder-input {
      width: 100%;
      min-height: 40px;
      padding: 0.65rem 0.85rem;
      border: 1px solid color-mix(in srgb, var(--tp-border) 70%, transparent);
      border-radius: var(--nm-radius-md);
      background: var(--tp-surface-light);
      color: var(--tp-text-dark);
      font: inherit;
      outline: none;
    }

    .ssg-builder-input:focus {
      border-color: color-mix(in srgb, var(--tp-primary) 32%, transparent);
      box-shadow: 0 0 0 3px color-mix(in srgb, var(--tp-primary) 12%, transparent);
    }

    .ssg-builder-textarea {
      min-height: 96px;
      resize: vertical;
    }

    .ssg-builder-checkbox {
      display: inline-flex;
      align-items: center;
      gap: 0.55rem;
      color: var(--tp-text);
      font-size: var(--tp-font-sm);
      font-weight: 600;
    }

    .ssg-builder-panel-list {
      display: grid;
      gap: var(--tp-space-3);
    }

    .ssg-builder-panel-card {
      display: grid;
      gap: var(--tp-space-3);
      padding: var(--tp-space-3);
      border: 1px solid color-mix(in srgb, var(--tp-border) 40%, transparent);
      border-radius: var(--nm-radius-md);
      background: var(--tp-surface);
    }

    .ssg-builder-code {
      margin: 0;
      padding: var(--tp-space-4);
      border-radius: var(--nm-radius-md);
      background: #f3f0ea;
      color: var(--tp-text-dark);
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', monospace;
      font-size: 0.8rem;
      line-height: 1.55;
      white-space: pre-wrap;
      word-break: break-word;
    }

    .ssg-breadcrumb {
      display: flex;
      align-items: center;
      flex-wrap: wrap;
      gap: 0.45rem;
      margin-top: 0.5rem;
      min-height: 1.75rem;
      color: var(--tp-text-secondary);
      font-size: 0.82rem;
      line-height: 1.35;
    }

    .ssg-breadcrumb-link,
    .ssg-breadcrumb-current {
      display: inline-flex;
      align-items: center;
      min-height: 1.75rem;
      border-radius: var(--nm-radius-pill);
      padding: 0.1rem 0.7rem;
    }

    .ssg-breadcrumb-link {
      border: 1px solid color-mix(in srgb, var(--tp-border) 48%, transparent);
      background: color-mix(in srgb, var(--tp-surface-light) 74%, transparent);
      color: var(--tp-primary-dark);
      font: inherit;
      font-weight: 600;
      cursor: pointer;
      transition: 0.18s ease;
    }

    .ssg-breadcrumb-link:hover {
      border-color: color-mix(in srgb, var(--tp-primary) 30%, transparent);
      background: color-mix(in srgb, var(--tp-primary) 8%, var(--tp-surface-light));
      color: var(--tp-primary-dark);
    }

    .ssg-breadcrumb-current {
      border: 1px solid color-mix(in srgb, var(--tp-primary) 20%, transparent);
      background: color-mix(in srgb, var(--tp-primary) 11%, var(--tp-surface-light));
      color: var(--tp-text-dark);
      font-weight: 700;
    }

    .ssg-breadcrumb-separator {
      color: color-mix(in srgb, var(--tp-text-secondary) 84%, transparent);
      font-weight: 700;
    }

    .ssg-toolbar {
      display: flex;
      gap: 0.6rem;
      flex-wrap: wrap;
    }

    .tree-toolbar-button {
      display: inline-flex;
      align-items: center;
      gap: 0.4rem;
      min-height: 38px;
      padding: 0.45rem 0.95rem;
      border-radius: var(--nm-radius-pill);
      border: 1px solid color-mix(in srgb, var(--tp-border) 78%, transparent);
      background: var(--tp-surface-raised);
      color: var(--tp-text-dark);
      font: inherit;
      font-size: 0.8rem;
      font-weight: 600;
      cursor: pointer;
      transition: 0.15s ease;
    }

    .tree-toolbar-button:hover {
      border-color: color-mix(in srgb, var(--tp-primary) 30%, transparent);
      color: var(--tp-primary-dark);
      background: color-mix(in srgb, var(--tp-primary) 5%, var(--tp-surface-raised));
    }

    .tree-toolbar-button.is-active {
      border-color: color-mix(in srgb, var(--tp-warning) 34%, transparent);
      color: var(--tp-warning-dark, color-mix(in srgb, var(--tp-warning) 72%, var(--tp-text-dark)));
      background: color-mix(in srgb, var(--tp-warning) 14%, var(--tp-surface-raised));
      box-shadow: 0 0 0 2px color-mix(in srgb, var(--tp-warning) 14%, transparent);
    }

    .tree-toolbar-button:disabled {
      cursor: not-allowed;
      opacity: 0.5;
    }

    .tree-toolbar-icon {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 1rem;
      height: 1rem;
      font-size: 0.9rem;
      line-height: 1;
    }

    .ssg-agentic-icon {
      width: 1rem;
      height: 1rem;
      fill: none;
      stroke: currentColor;
      stroke-linecap: round;
      stroke-linejoin: round;
      stroke-width: 1.7;
    }

    .ssg-tree {
      min-height: 0;
      overflow: auto;
      padding: var(--tp-space-4);
    }

    :host ::ng-deep .ssg-tree .p-tree {
      width: 100%;
      min-height: 0;
      border: 1px solid color-mix(in srgb, var(--tp-border) 70%, transparent);
      border-radius: var(--nm-radius-md);
      background: color-mix(in srgb, var(--tp-surface) 55%, var(--tp-surface-light));
      padding: var(--tp-space-4);
    }

    :host ::ng-deep .ssg-tree .p-treenode-content,
    :host ::ng-deep .ssg-tree .p-tree-node-content {
      border-radius: 999px;
      padding: 0.15rem 0;
      border: 1px solid transparent;
      background: transparent;
    }

    :host ::ng-deep .ssg-tree .p-tree-root,
    :host ::ng-deep .ssg-tree .p-tree-root-children,
    :host ::ng-deep .ssg-tree .p-treenode-children,
    :host ::ng-deep .ssg-tree .p-tree-node-children {
      display: grid;
      gap: 0.45rem;
    }

    :host ::ng-deep .ssg-tree .p-treenode-children,
    :host ::ng-deep .ssg-tree .p-tree-node-children {
      margin: 0.35rem 0 0 0.9rem;
      padding-left: 0.85rem;
      border-inline-start: 1px solid color-mix(in srgb, var(--tp-border) 72%, transparent);
    }

    :host ::ng-deep .ssg-tree .p-treenode-children .p-treenode,
    :host ::ng-deep .ssg-tree .p-tree-node-children .p-tree-node {
      position: relative;
    }

    :host ::ng-deep .ssg-tree .p-treenode-children .p-treenode::before,
    :host ::ng-deep .ssg-tree .p-tree-node-children .p-tree-node::before {
      content: '';
      position: absolute;
      inset-inline-start: -0.85rem;
      top: 1rem;
      width: 0.62rem;
      border-top: 1px solid color-mix(in srgb, var(--tp-border) 72%, transparent);
    }

    :host ::ng-deep .ssg-tree .p-highlight .p-treenode-content,
    :host ::ng-deep .ssg-tree .p-tree-node-content.p-tree-node-selected,
    :host ::ng-deep .ssg-tree .p-treenode-content:has(.ssg-tree-label:hover),
    :host ::ng-deep .ssg-tree .p-tree-node-content:has(.ssg-tree-label:hover) {
      background: transparent;
      border-color: transparent;
    }

    :host ::ng-deep .ssg-tree .p-tree-toggler,
    :host ::ng-deep .ssg-tree .p-tree-node-toggle-button {
      width: 1.55rem;
      height: 1.55rem;
      margin-right: 0.35rem;
      margin-top: 0.15rem;
      border-radius: 999px;
      border: 1px solid color-mix(in srgb, var(--tp-border) 80%, transparent);
      background: var(--tp-surface-raised);
      color: var(--tp-text-secondary);
      font-size: 0.82rem;
      font-weight: 700;
      padding: 0;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      box-shadow: none;
    }

    :host ::ng-deep .ssg-tree .p-tree-node-toggle-icon {
      width: 1rem;
      height: 1rem;
      color: currentColor;
    }

    .ssg-tree-label {
      min-width: 0;
      display: inline-flex;
      align-items: center;
      gap: var(--tp-space-2);
      width: 100%;
      min-height: 30px;
      padding: 0.3rem 0.65rem;
      border-radius: 999px;
      background: var(--tp-surface-raised);
      border: 1px solid color-mix(in srgb, var(--tp-border) 80%, transparent);
      color: var(--tp-text-dark);
      font-size: 0.74rem;
      font-weight: 600;
      line-height: 1.35;
      font-family: ui-monospace, SFMono-Regular, Menlo, Monaco, Consolas, 'Liberation Mono', monospace;
      cursor: pointer;
    }

    .ssg-tree-label.has-violation {
      border-color: color-mix(in srgb, var(--tp-warning) 34%, transparent);
      background: color-mix(in srgb, var(--tp-warning) 10%, var(--tp-surface-raised));
    }

    .ssg-tree-icon {
      width: 1.25rem;
      height: 1.25rem;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      flex: 0 0 auto;
      color: var(--tp-text-secondary);
    }

    .ssg-tree-icon svg {
      width: 0.95rem;
      height: 0.95rem;
      fill: none;
      stroke: currentColor;
      stroke-width: 1.75;
    }

    .ssg-tree-icon.kind-shell {
      color: color-mix(in srgb, var(--tp-primary-dark) 90%, transparent);
    }

    .ssg-tree-icon.kind-shell svg {
      stroke-width: 1.45;
    }

    .ssg-tree-icon.kind-container {
      color: color-mix(in srgb, var(--tp-primary-dark) 76%, transparent);
    }

    .ssg-tree-icon.kind-element {
      color: color-mix(in srgb, var(--tp-text-secondary) 92%, transparent);
    }

    .ssg-tree-text {
      min-width: 0;
      flex: 1 1 auto;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
      color: var(--tp-text-dark);
      user-select: text;
      -webkit-user-select: text;
      cursor: text;
    }

    .ssg-violation-flag {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      width: 1rem;
      height: 1rem;
      flex: 0 0 auto;
      color: var(--tp-warning-dark, color-mix(in srgb, var(--tp-warning) 72%, var(--tp-text-dark)));
      font-size: 0.75rem;
    }

    .ssg-tree-label.kind-shell-row {
      background: color-mix(in srgb, var(--tp-primary-dark) 10%, var(--tp-surface-raised));
      border-color: color-mix(in srgb, var(--tp-primary-dark) 24%, transparent);
      color: var(--tp-primary-dark);
      font-weight: 700;
    }

    .ssg-tree-label.kind-screen-row {
      background: color-mix(in srgb, var(--tp-primary) 10%, var(--tp-surface-raised));
      border-color: color-mix(in srgb, var(--tp-primary) 24%, transparent);
      color: var(--tp-primary-dark);
    }

    .ssg-tree-label.kind-root-row {
      background: color-mix(in srgb, var(--tp-primary-dark) 6%, var(--tp-surface-raised));
      border-color: color-mix(in srgb, var(--tp-primary-dark) 16%, transparent);
      color: var(--tp-text-dark);
      font-weight: 700;
    }

    .ssg-tree-label.kind-registry-row {
      background: color-mix(in srgb, var(--tp-surface) 70%, var(--tp-surface-light));
      border-color: color-mix(in srgb, var(--tp-border) 75%, transparent);
      color: var(--tp-text-dark);
    }

    .ssg-tree-label.is-selected,
    .ssg-tree-label.is-preview-hovered,
    :host ::ng-deep .ssg-tree .p-highlight .ssg-tree-label,
    :host ::ng-deep .ssg-tree .p-tree-node-content.p-tree-node-selected .ssg-tree-label {
      background: color-mix(in srgb, var(--tp-primary) 16%, var(--tp-surface-raised));
      border-color: color-mix(in srgb, var(--tp-primary) 36%, transparent);
      box-shadow: 0 0 0 2px color-mix(in srgb, var(--tp-primary) 18%, transparent);
      color: var(--tp-primary-dark);
    }

    :host ::ng-deep .ssg-tree .p-treenode-content:has(.ssg-tree-label:hover) .ssg-tree-label,
    :host ::ng-deep .ssg-tree .p-tree-node-content:has(.ssg-tree-label:hover) .ssg-tree-label {
      border-color: color-mix(in srgb, var(--tp-primary) 28%, transparent);
      color: var(--tp-primary-dark);
      background: color-mix(in srgb, var(--tp-primary) 6%, var(--tp-surface-raised));
    }

    .ssg-registry-kind-badge {
      background: color-mix(in srgb, var(--tp-primary) 8%, var(--tp-surface-light));
      color: var(--tp-primary-dark);
      border-color: color-mix(in srgb, var(--tp-primary) 18%, transparent);
    }

    .ssg-preview-shell {
      min-height: 0;
      height: 100%;
      overflow: hidden;
      padding: var(--tp-space-5);
      display: grid;
      grid-template-rows: auto minmax(0, 1fr) auto;
      gap: var(--tp-space-3);
      background: color-mix(in srgb, var(--tp-surface-light) 84%, var(--tp-bg));
    }

    .ssg-preview-body {
      min-height: 0;
      display: grid;
      grid-template-columns: minmax(0, 1fr) minmax(22rem, 28rem);
      gap: var(--tp-space-4);
      align-items: stretch;
    }

    .ssg-preview-toolbar {
      display: grid;
      grid-template-columns: minmax(0, 1fr) auto auto;
      gap: var(--tp-space-3);
      align-items: center;
      padding: var(--tp-space-3) var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-border) 28%, transparent);
      border-radius: var(--nm-radius-md);
      background: color-mix(in srgb, var(--tp-surface-raised) 92%, var(--tp-bg));
    }

    .ssg-preview-toolbar-copy {
      display: grid;
      gap: 0.25rem;
      min-width: 0;
    }

    .ssg-preview-toolbar-field {
      display: grid;
      gap: 0.35rem;
      color: var(--tp-text-secondary);
      font-size: 0.76rem;
      font-weight: 700;
    }

    .ssg-preview-viewport-chip {
      justify-self: start;
      padding: 0.42rem 0.75rem;
      border: 1px solid color-mix(in srgb, var(--tp-border) 38%, transparent);
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-surface-light) 92%, var(--tp-bg));
      color: var(--tp-text-dark);
      font-size: 0.76rem;
      font-weight: 700;
      letter-spacing: 0.02em;
      white-space: nowrap;
    }

    .ssg-preview-viewport-switch {
      display: inline-flex;
      align-items: center;
      gap: 0.35rem;
      padding: 0.25rem;
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-primary) 8%, var(--tp-surface-raised));
    }

    .ssg-preview-viewport-button {
      width: 2.4rem;
      height: 2.4rem;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border: none;
      border-radius: 999px;
      background: transparent;
      color: var(--tp-text-secondary);
      cursor: pointer;
      transition: background-color 140ms ease, color 140ms ease, box-shadow 140ms ease;
    }

    .ssg-preview-viewport-button svg {
      width: 1.15rem;
      height: 1.15rem;
      stroke: currentColor;
      fill: none;
      stroke-width: 1.7;
      stroke-linecap: round;
      stroke-linejoin: round;
    }

    .ssg-preview-viewport-button:hover {
      background: color-mix(in srgb, var(--tp-primary) 10%, var(--tp-surface-raised));
      color: var(--tp-primary-dark);
    }

    .ssg-preview-viewport-button.is-active {
      background: var(--tp-primary);
      color: var(--tp-bg);
      box-shadow: 0 0 0 1px color-mix(in srgb, var(--tp-primary-dark) 18%, transparent);
    }

    .ssg-preview-toolbar-title {
      display: inline-flex;
      align-items: center;
      gap: 0.45rem;
      font-size: 0.9rem;
      font-weight: 700;
      color: var(--tp-text-dark);
    }

    .ssg-preview-toolbar-subtitle {
      font-size: 0.78rem;
      color: var(--tp-text-secondary);
    }

    .ssg-preview-audit {
      display: none;
    }

    .ssg-preview-issues-panel {
      min-height: 0;
      height: 100%;
      overflow: hidden;
    }

    .ssg-preview-issues-panel-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: var(--tp-space-3);
      flex-wrap: wrap;
    }

    .ssg-preview-issues-count {
      display: inline-flex;
      align-items: center;
      min-height: 1.8rem;
      padding: 0.12rem 0.7rem;
      border: 1px solid color-mix(in srgb, var(--tp-border) 68%, transparent);
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-surface-light) 88%, var(--tp-surface));
      color: var(--tp-text-dark);
      font-size: 0.78rem;
      font-weight: 700;
      white-space: nowrap;
    }

    .ssg-preview-issues-table {
      min-height: 12rem;
      height: 100%;
    }

    .ssg-inspector-tabs {
      min-height: 0;
      display: flex;
      flex-direction: column;
      overflow: hidden;
    }

    .ssg-inspector-empty-state {
      min-height: 0;
      height: 100%;
      display: grid;
      place-items: center;
      color: var(--tp-text-muted);
      font-size: 0.9rem;
      font-weight: 600;
      letter-spacing: 0.01em;
    }

    .ssg-overview-tab {
      height: 100%;
      min-height: 0;
      overflow: auto;
      padding: var(--tp-space-4);
      display: grid;
      gap: var(--tp-space-5);
      align-content: start;
    }

    .ssg-fact-sheet {
      display: grid;
      gap: var(--tp-space-3);
    }

    .ssg-fact-header {
      display: grid;
      grid-template-columns: 11rem minmax(0, 1fr);
      align-items: start;
      gap: var(--tp-space-5);
      padding: var(--tp-space-5);
      border: 1px solid color-mix(in srgb, var(--tp-border) 60%, transparent);
      border-radius: var(--nm-radius-lg);
      background: color-mix(in srgb, var(--tp-surface-light) 80%, var(--tp-surface));
    }

    .ssg-fact-logo-panel {
      display: grid;
      place-items: center;
      min-height: 11rem;
      padding: var(--tp-space-3);
      border: 1px solid color-mix(in srgb, var(--tp-border) 68%, transparent);
      border-radius: var(--nm-radius-lg);
      background: var(--tp-surface);
    }

    .ssg-fact-logo {
      display: grid;
      place-items: center;
      width: 100%;
      min-height: 100%;
      border: 1px solid color-mix(in srgb, var(--tp-border) 72%, transparent);
      border-radius: var(--nm-radius-md);
      background: color-mix(in srgb, var(--tp-surface-light) 92%, var(--tp-surface));
      color: var(--tp-text-dark);
      font-size: 1.15rem;
      font-weight: 800;
      letter-spacing: 0.08em;
      text-transform: uppercase;
    }

    .ssg-fact-header-content {
      display: grid;
      gap: var(--tp-space-3);
      min-width: 0;
      align-content: start;
    }

    .ssg-fact-header-actions {
      display: flex;
      justify-content: flex-end;
      align-items: center;
      gap: var(--tp-space-2);
      flex-wrap: wrap;
    }

    .ssg-fact-header-content h3 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 1.65rem;
      font-weight: 800;
      line-height: 1.2;
    }

    .ssg-fact-id-value {
      color: var(--tp-text-dark);
      font-family: var(--tp-font-mono, 'SFMono-Regular', ui-monospace, monospace);
      font-size: 0.92rem;
      line-height: 1.4;
      word-break: break-word;
    }

    .ssg-fact-header-content p {
      margin: 0;
      color: var(--tp-text-secondary);
      font-size: 0.88rem;
      line-height: 1.45;
    }

    .ssg-fact-meta-list {
      display: grid;
      gap: 0;
      margin: 0;
    }

    .ssg-fact-meta-row {
      display: grid;
      grid-template-columns: 12rem minmax(0, 1fr);
      gap: var(--tp-space-4);
      align-items: baseline;
      min-width: 0;
      padding-block: 0.15rem;
    }

    .ssg-fact-meta-row dt,
    .ssg-fact-meta-row dd {
      margin: 0;
    }

    .ssg-fact-meta-row dt {
      color: var(--tp-text-secondary);
      font-size: 0.78rem;
      font-weight: 700;
      letter-spacing: 0.02em;
      text-transform: uppercase;
      white-space: nowrap;
    }

    .ssg-fact-meta-row dd {
      color: var(--tp-text-dark);
      font-size: 0.95rem;
      line-height: 1.4;
      min-width: 0;
      word-break: break-word;
    }

    .ssg-status-chip {
      display: inline-flex;
      align-items: center;
      min-height: 1.8rem;
      padding: 0.1rem 0.65rem;
      border: 1px solid color-mix(in srgb, var(--tp-border) 72%, transparent);
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-surface-light) 88%, var(--tp-surface));
      color: var(--tp-text-dark);
      font-size: 0.82rem;
      font-weight: 700;
      line-height: 1;
      text-transform: capitalize;
    }

    .ssg-status-chip.is-closed {
      background: color-mix(in srgb, var(--tp-primary) 10%, var(--tp-surface));
      color: var(--tp-primary-dark);
      border-color: color-mix(in srgb, var(--tp-primary) 25%, transparent);
    }

    .ssg-insights-panel {
      display: grid;
      gap: var(--tp-space-4);
      padding: var(--tp-space-5);
      border: 1px solid color-mix(in srgb, var(--tp-border) 60%, transparent);
      border-radius: var(--nm-radius-lg);
      background: color-mix(in srgb, var(--tp-surface-light) 80%, var(--tp-surface));
    }

    .ssg-insights-panel-header h3 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 1.1rem;
      font-weight: 800;
      line-height: 1.2;
    }

    .ssg-insights-panel-body {
      display: grid;
    }

    .ssg-insights-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(10rem, 1fr));
      gap: var(--tp-space-3);
    }

    .ssg-insight-stat-panel {
      display: grid;
      gap: 0.65rem;
      min-width: 0;
      padding: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-border) 48%, transparent);
      border-radius: var(--nm-radius-md);
      background: color-mix(in srgb, var(--tp-surface-light) 72%, transparent);
    }

    .ssg-insight-stat-panel h4 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 1rem;
      font-weight: 800;
      line-height: 1.2;
    }

    .ssg-insight-stat-panel .ssg-fact-meta-row dd {
      font-size: 1rem;
      font-weight: 700;
    }

    .ssg-overview-section {
      display: grid;
      gap: var(--tp-space-3);
    }

    .ssg-attributes-panel {
      display: grid;
      gap: var(--tp-space-4);
      padding: var(--tp-space-5);
      border: 1px solid color-mix(in srgb, var(--tp-border) 60%, transparent);
      border-radius: var(--nm-radius-lg);
      background: color-mix(in srgb, var(--tp-surface-light) 80%, var(--tp-surface));
    }

    .ssg-attributes-panel-header h3 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 1.1rem;
      font-weight: 800;
      line-height: 1.2;
    }

    .ssg-attributes-panel-body {
      display: grid;
    }

    .ssg-attribute-groups {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: var(--tp-space-4);
      align-items: start;
    }

    .ssg-overview-panel {
      display: grid;
      gap: var(--tp-space-3);
      min-width: 0;
      padding: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-border) 48%, transparent);
      border-radius: var(--nm-radius-md);
      background: color-mix(in srgb, var(--tp-surface-light) 72%, transparent);
    }

    .ssg-relationships-panel {
      display: grid;
      gap: var(--tp-space-4);
      padding: var(--tp-space-5);
      border: 1px solid color-mix(in srgb, var(--tp-border) 60%, transparent);
      border-radius: var(--nm-radius-lg);
      background: color-mix(in srgb, var(--tp-surface-light) 80%, var(--tp-surface));
    }

    .ssg-relationships-panel-header h3 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 1.1rem;
      font-weight: 800;
      line-height: 1.2;
    }

    .ssg-relationships-panel-body {
      display: grid;
    }

    .ssg-relationship-groups {
      display: grid;
      gap: var(--tp-space-4);
    }

    .ssg-relationship-group-panel {
      background: color-mix(in srgb, var(--tp-surface-light) 82%, transparent);
    }

    .ssg-overview-panel-header {
      display: grid;
      gap: 0.25rem;
    }

    .ssg-overview-panel-header h4 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 0.94rem;
      font-weight: 800;
    }

    .ssg-overview-panel-header p {
      margin: 0;
      color: var(--tp-text-secondary);
      font-size: 0.8rem;
      line-height: 1.4;
    }

    .ssg-overview-section-header {
      display: grid;
      gap: 0.25rem;
    }

    .ssg-overview-section-header h3 {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 1rem;
      font-weight: 700;
    }

    .ssg-overview-section-header p {
      margin: 0;
      color: var(--tp-text-secondary);
      font-size: var(--tp-font-sm);
    }

    .ssg-overview-table {
      min-height: 16rem;
      border: 1px solid var(--tp-border);
      border-radius: var(--nm-radius);
      overflow: hidden;
      background: transparent;
    }

    .ssg-issue-toolbar {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: var(--tp-space-3);
      margin-bottom: var(--tp-space-3);
      flex-wrap: wrap;
    }

    .ssg-issue-toolbar-copy {
      display: inline-flex;
      align-items: center;
      gap: var(--tp-space-3);
      min-height: 2rem;
      flex-wrap: wrap;
    }

    .ssg-issue-selection-count {
      color: var(--tp-text-secondary);
      font-size: 0.84rem;
      font-weight: 700;
    }

    .ssg-issue-filter-field {
      display: inline-flex;
      align-items: center;
      gap: var(--tp-space-2);
      color: var(--tp-text-secondary);
      font-size: 0.84rem;
      font-weight: 700;
    }

    .ssg-issue-filter-select {
      min-width: 12rem;
      border: 1px solid var(--tp-border);
      border-radius: var(--nm-radius-sm);
      background: rgba(255, 255, 255, 0.82);
      color: var(--tp-text-primary);
      padding: 0.45rem 0.65rem;
      font: inherit;
    }

    .ssg-issue-toolbar-actions {
      display: inline-flex;
      gap: var(--tp-space-2);
      flex-wrap: wrap;
    }

    .ssg-properties-empty {
      padding: var(--tp-space-6) var(--tp-space-4);
      text-align: center;
      color: var(--tp-text-secondary);
    }

    .ssg-preview-stage {
      width: 100%;
      height: 100%;
      min-height: 0;
      overflow: hidden;
      display: grid;
      place-items: start start;
    }

    .ssg-preview-canvas-frame {
      position: relative;
      display: grid;
      place-items: start start;
      overflow: hidden;
    }

    .ssg-preview-canvas {
      position: relative;
      transform-origin: top left;
    }

    :host ::ng-deep .ssg-preview-canvas .ssg-focused,
    :host ::ng-deep .ssg-preview-canvas .ssg-hovered {
      position: relative;
    }

    :host ::ng-deep .ssg-preview-canvas .ssg-focused {
      outline: 3px solid var(--ssg-preview-selected-color, color-mix(in srgb, var(--tp-primary-dark) 78%, white 22%));
      outline-offset: 4px;
      box-shadow: none !important;
    }

    :host ::ng-deep .ssg-preview-canvas .ssg-hovered {
      outline: 2px dashed var(--ssg-preview-hover-color, color-mix(in srgb, var(--tp-primary) 68%, white 32%));
      outline-offset: 4px;
    }

    :host ::ng-deep .ssg-preview-canvas .ssg-issue-warning::before {
      content: '';
      position: absolute;
      top: 0.35rem;
      right: 0.35rem;
      width: 1rem;
      height: 0.9rem;
      clip-path: polygon(50% 0%, 0% 100%, 100% 100%);
      background: color-mix(in srgb, #fbbf24 82%, white 18%);
      border: 1px solid color-mix(in srgb, #7c2d12 65%, transparent);
      box-shadow: 0 10px 20px -16px rgba(15, 23, 42, 0.75);
      pointer-events: none;
      z-index: 4;
    }

    :host ::ng-deep .ssg-preview-canvas .ssg-issue-warning::after {
      content: '!';
      position: absolute;
      top: 0.46rem;
      right: 0.65rem;
      color: #7c2d12;
      font-size: 0.72rem;
      font-weight: 800;
      line-height: 1;
      pointer-events: none;
      z-index: 5;
    }

    .ssg-preview-findings {
      height: 12.5rem;
      min-height: 12.5rem;
      border: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      border-radius: var(--nm-radius-md);
      background: color-mix(in srgb, var(--tp-surface-raised) 94%, var(--tp-bg));
      overflow: hidden;
      display: grid;
    }

    .ssg-preview-findings-list {
      height: 100%;
      overflow: auto;
      display: grid;
    }

    .ssg-preview-finding {
      border: none;
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 16%, transparent);
      background: transparent;
      display: grid;
      grid-template-columns: auto auto minmax(0, 1fr) auto;
      gap: 0.75rem;
      align-items: start;
      width: 100%;
      padding: 0.85rem 1rem;
      text-align: left;
      cursor: pointer;
    }

    .ssg-preview-finding:last-child {
      border-bottom: none;
    }

    .ssg-preview-finding:disabled {
      cursor: default;
      opacity: 0.85;
    }

    .ssg-preview-finding:not(:disabled):hover {
      background: color-mix(in srgb, var(--tp-primary) 6%, var(--tp-surface-raised));
    }

    .ssg-preview-finding-level,
    .ssg-preview-finding-rule,
    .ssg-preview-finding-code {
      font-size: 0.72rem;
      font-weight: 800;
      letter-spacing: 0.03em;
      text-transform: uppercase;
    }

    .ssg-preview-finding-level {
      color: var(--tp-bg);
      background: var(--tp-primary-dark);
      padding: 0.2rem 0.45rem;
      border-radius: 999px;
    }

    .ssg-preview-finding.level-aa .ssg-preview-finding-level {
      background: color-mix(in srgb, var(--tp-warning) 78%, var(--tp-primary-dark));
    }

    .ssg-preview-finding.level-aaa .ssg-preview-finding-level {
      background: color-mix(in srgb, var(--tp-danger) 76%, var(--tp-primary-dark));
    }

    .ssg-preview-finding-rule {
      color: var(--tp-primary-dark);
    }

    .ssg-preview-finding-message {
      font-size: 0.82rem;
      line-height: 1.45;
      color: var(--tp-text-dark);
    }

    .ssg-preview-finding-code {
      color: var(--tp-text-secondary);
      text-transform: none;
      letter-spacing: 0;
    }

    .ssg-preview-findings-empty {
      height: 100%;
      padding: 0.85rem 1rem;
      font-size: 0.82rem;
      color: var(--tp-text-secondary);
      display: grid;
      align-content: start;
    }

    .ssg-empty {
      padding: var(--tp-space-6);
      color: var(--tp-text-secondary);
    }

    .ssg-error {
      color: var(--tp-danger);
    }

    :host ::ng-deep .ssg-inspector-tabs .p-tablist {
      padding-inline: var(--tp-space-4);
      padding-top: var(--tp-space-2);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 36%, transparent);
      background: var(--tp-surface-raised);
    }

    :host ::ng-deep .ssg-inspector-tabs .p-tab {
      min-height: 40px;
      padding: 0.6rem 1rem;
      font-size: 0.8rem;
      font-weight: 700;
    }

    :host ::ng-deep .ssg-inspector-tabs .p-tabpanels {
      flex: 1;
      min-height: 0;
      overflow: hidden;
      padding: 0;
      background: var(--tp-surface-raised);
    }

    :host ::ng-deep .ssg-inspector-tabs .p-tabpanel {
      height: 100%;
      min-height: 0;
      padding: 0;
    }

    :host ::ng-deep .ssg-relationship-tabs {
      display: flex;
      flex-direction: column;
      min-height: 0;
      height: 100%;
    }

    :host ::ng-deep .ssg-relationship-tabs .p-tablist {
      padding-inline: var(--tp-space-4);
      padding-top: var(--tp-space-2);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 36%, transparent);
      background: transparent;
      flex-wrap: wrap;
      gap: 0.25rem;
    }

    :host ::ng-deep .ssg-relationship-tabs .p-tab {
      min-height: 36px;
      padding: 0.5rem 0.875rem;
      font-size: 0.78rem;
      font-weight: 700;
    }

    :host ::ng-deep .ssg-relationship-tabs .p-tabpanels {
      flex: 1;
      min-height: 0;
      padding: var(--tp-space-4);
      background: transparent;
    }

    :host ::ng-deep .ssg-relationship-tabs .p-tabpanel {
      height: 100%;
      min-height: 0;
      padding: 0;
    }

    :host ::ng-deep .ssg-overview-table .p-datatable {
      height: 100%;
      background: transparent;
    }

    :host ::ng-deep .ssg-overview-table .p-datatable-wrapper {
      height: 100%;
      background: transparent;
    }

    :host ::ng-deep .ssg-overview-table .p-paginator {
      border-top: 1px solid color-mix(in srgb, var(--tp-border) 50%, transparent);
      background: var(--tp-surface-raised);
    }

    :host ::ng-deep .ssg-overview-table .p-datatable-table {
      width: 100%;
      border-collapse: collapse;
    }

    :host ::ng-deep .ssg-overview-table .p-datatable-thead > tr > th {
      background: var(--tp-surface);
      color: var(--tp-text-dark);
      font-weight: 600;
      padding: var(--tp-space-3) var(--tp-space-4);
      border-bottom: 1px solid var(--tp-border);
      text-align: left;
      font-size: var(--tp-font-sm);
    }

    :host ::ng-deep .ssg-overview-table .p-datatable-tbody > tr > td {
      padding: var(--tp-space-3) var(--tp-space-4);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 50%, transparent);
      font-size: var(--tp-font-md);
      vertical-align: top;
      background: transparent;
    }

    :host ::ng-deep .ssg-overview-table .p-datatable-tbody > tr:hover > td {
      background: color-mix(in srgb, var(--tp-primary) 4%, transparent);
    }

    :host ::ng-deep .ssg-overview-table .p-datatable-tbody > tr:last-child > td {
      border-bottom: none;
    }

    :host ::ng-deep .ssg-overview-table .p-datatable-tbody > tr > td.ssg-properties-empty {
      background: transparent;
    }

    .ssg-table-link {
      display: inline-flex;
      align-items: center;
      padding: 0;
      border: none;
      background: none;
      color: var(--tp-primary);
      font: inherit;
      font-weight: 600;
      text-align: left;
      cursor: pointer;
      text-decoration: underline;
      text-decoration-color: color-mix(in srgb, var(--tp-primary) 45%, transparent);
      text-underline-offset: 0.12rem;
    }

    .ssg-table-link:hover {
      color: var(--tp-primary-dark);
      text-decoration-color: currentColor;
    }

    .ssg-table-link:disabled {
      cursor: not-allowed;
      opacity: 0.45;
      text-decoration: none;
    }

    .ssg-issue-actions {
      display: inline-flex;
      gap: 0.75rem;
      flex-wrap: wrap;
    }

    .ssg-issue-modal-backdrop {
      position: fixed;
      inset: 0;
      z-index: 1100;
      display: grid;
      place-items: center;
      padding: var(--tp-space-6);
      background: color-mix(in srgb, var(--tp-primary-dark) 34%, transparent);
      backdrop-filter: blur(6px);
    }

    .ssg-issue-modal {
      width: min(72rem, calc(100vw - 3rem));
      max-height: calc(100vh - 3rem);
      overflow: auto;
      border-radius: var(--nm-radius-xl);
      box-shadow: 0 24px 70px rgba(0, 0, 0, 0.18);
    }

    .ssg-issue-modal .ssg-fact-sheet {
      min-height: auto;
    }

    .ssg-xray-summary-modal {
      width: min(56rem, calc(100vw - 3rem));
    }

    .ssg-xray-progress-panel {
      display: grid;
      gap: var(--tp-space-4);
    }

    .ssg-xray-progress-track {
      position: relative;
      width: 100%;
      height: 0.75rem;
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-primary) 12%, var(--tp-surface-raised));
      overflow: hidden;
    }

    .ssg-xray-progress-bar {
      height: 100%;
      border-radius: inherit;
      background: linear-gradient(90deg, var(--tp-primary), color-mix(in srgb, var(--tp-primary-dark) 72%, white 28%));
      transition: width 180ms ease;
    }

    .ssg-xray-progress-caption {
      color: var(--tp-text-muted);
      font-size: 0.95rem;
      font-weight: 600;
    }

    .ssg-xray-stage-list {
      display: grid;
      gap: var(--tp-space-3);
    }

    .ssg-xray-stage-row {
      display: grid;
      grid-template-columns: auto 1fr;
      gap: var(--tp-space-3);
      padding: var(--tp-space-3);
      border-radius: var(--nm-radius-md);
      border: 1px solid color-mix(in srgb, var(--tp-border) 88%, transparent);
      background: color-mix(in srgb, var(--tp-surface-raised) 92%, white 8%);
    }

    .ssg-xray-stage-row.is-active {
      border-color: color-mix(in srgb, var(--tp-primary) 44%, var(--tp-border));
      background: color-mix(in srgb, var(--tp-primary) 8%, var(--tp-surface-raised));
    }

    .ssg-xray-stage-row.is-complete {
      border-color: color-mix(in srgb, #2f8f5b 32%, var(--tp-border));
      background: color-mix(in srgb, #2f8f5b 8%, var(--tp-surface-raised));
    }

    .ssg-xray-stage-mark {
      display: grid;
      place-items: center;
      width: 1.75rem;
      height: 1.75rem;
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-primary) 16%, transparent);
      color: var(--tp-primary-dark);
      font-weight: 700;
    }

    .ssg-xray-stage-content {
      display: grid;
      gap: 0.25rem;
    }

    .ssg-xray-stage-content h5 {
      margin: 0;
      font-size: 0.98rem;
      color: var(--tp-text-dark);
    }

    .ssg-xray-stage-content p {
      margin: 0;
      color: var(--tp-text-muted);
      line-height: 1.45;
    }

    .ssg-xray-tag-panel {
      grid-column: 1 / -1;
    }

    .ssg-xray-tag-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(11rem, 1fr));
      gap: var(--tp-space-3);
    }

    .ssg-xray-tag-chip {
      display: flex;
      align-items: center;
      justify-content: space-between;
      gap: var(--tp-space-3);
      padding: var(--tp-space-3);
      border-radius: var(--nm-radius-md);
      border: 1px solid color-mix(in srgb, var(--tp-border) 88%, transparent);
      background: color-mix(in srgb, var(--tp-surface-raised) 94%, white 6%);
    }

    .ssg-xray-tag-chip span {
      color: var(--tp-text-muted);
    }

    .ssg-xray-tag-chip strong {
      color: var(--tp-text-dark);
      font-size: 1rem;
    }

    .ssg-issue-modal-actions {
      display: flex;
      gap: var(--tp-space-3);
      justify-content: flex-end;
      flex-wrap: wrap;
    }

    .ssg-issue-prompt-panel {
      display: grid;
      gap: var(--tp-space-3);
    }

    .ssg-issue-prompt-textarea {
      width: 100%;
      min-height: 14rem;
      resize: vertical;
      border-radius: var(--nm-radius-md);
      border: 1px solid var(--tp-border);
      background: var(--tp-surface-raised);
      color: var(--tp-text-dark);
      padding: var(--tp-space-4);
      font: inherit;
      line-height: 1.5;
    }

    .ssg-issue-prompt-textarea:focus {
      outline: 2px solid color-mix(in srgb, var(--tp-primary) 38%, transparent);
      outline-offset: 2px;
      border-color: color-mix(in srgb, var(--tp-primary) 48%, var(--tp-border));
    }

    .ssg-issue-prompt-actions {
      display: flex;
      justify-content: flex-end;
    }

    .ssg-attribute-display {
      display: inline-flex;
      align-items: center;
      min-height: 1.5rem;
      width: 100%;
      color: var(--tp-text-dark);
      line-height: 1.45;
    }

    .ssg-attribute-display.is-empty {
      color: var(--tp-text-muted);
      font-style: italic;
    }

    .ssg-attribute-editor {
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      min-width: min(28rem, 100%);
    }

    .ssg-attribute-editor-input {
      min-height: 38px;
      flex: 1 1 auto;
      min-width: 0;
    }

    .ssg-attribute-editor-select {
      flex: 1 1 auto;
      min-width: 0;
    }

    :host ::ng-deep .ssg-attribute-editor-select.p-select {
      width: 100%;
      min-height: 38px;
    }

    .ssg-attribute-editor-actions {
      display: flex;
      gap: var(--tp-space-2);
      flex: 0 0 auto;
      align-items: center;
      flex-wrap: nowrap;
    }

    .ssg-attribute-editor-button {
      width: 2.25rem;
      height: 2.25rem;
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border-radius: 999px;
      border: 1px solid color-mix(in srgb, var(--tp-border) 78%, transparent);
      background: var(--tp-surface-raised);
      color: var(--tp-text-dark);
      cursor: pointer;
      transition: 0.15s ease;
      flex: 0 0 auto;
    }

    .ssg-attribute-editor-button:hover {
      border-color: color-mix(in srgb, var(--tp-primary) 30%, transparent);
      background: color-mix(in srgb, var(--tp-primary) 6%, var(--tp-surface-raised));
    }

    .ssg-attribute-editor-button.is-apply {
      color: var(--tp-primary-dark);
    }

    .ssg-attribute-editor-button.is-cancel {
      color: color-mix(in srgb, var(--tp-danger) 82%, var(--tp-text-dark));
    }

    .ssg-attribute-editor-button .pi {
      font-size: 0.78rem;
    }

    :host ::ng-deep .ssg-attribute-inplace {
      display: block;
      width: 100%;
    }

    :host ::ng-deep .ssg-attribute-inplace .p-inplace-display {
      display: block;
      width: 100%;
      padding: 0;
      border: none;
      background: none;
      color: inherit;
      text-align: left;
      box-shadow: none;
    }

    :host ::ng-deep .ssg-attribute-inplace .p-inplace-display:hover {
      background: transparent;
    }

    .ssg-workspace-dock {
      position: fixed;
      inset-inline: 0;
      bottom: var(--tp-space-4);
      z-index: 20;
    }

    :host ::ng-deep .ssg-workspace-dock .p-dock {
      width: fit-content;
      margin-inline: auto;
    }

    @media (max-width: 1180px) {
      .ssg-preview-body {
        grid-template-columns: minmax(0, 1fr);
      }
    }

    @media (max-width: 820px) {
      .ssg-page {
        padding: var(--tp-space-4);
        padding-bottom: calc(var(--tp-space-4) + 7rem);
      }

      .ssg-fact-header {
        grid-template-columns: 1fr;
      }

      .ssg-fact-sheet {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }

      .ssg-insights-grid,
      .ssg-attribute-groups {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class SystemShellGraphPage implements OnInit, AfterViewInit, OnDestroy {
  private readonly api = inject(SystemShellGraphApiService);
  private readonly hostElement = inject<ElementRef<HTMLElement>>(ElementRef);
  private readonly ngZone = inject(NgZone);
  readonly state = inject(SystemShellGraphStateService);
  readonly activeWorkspace = signal<'frontend' | 'components-registry'>('frontend');
  readonly registrySearchTerm = signal('');
  readonly registryExpanded = signal(true);
  readonly registryLoading = signal(false);
  readonly registryError = signal<string | null>(null);
  readonly registryDefinitions = signal<ComponentRegistryDefinition[]>([]);
  readonly registryInstanceLoading = signal(false);
  readonly registryInstanceSaving = signal(false);
  readonly registryInstanceError = signal<string | null>(null);
  readonly registryEditMode = signal(false);
  readonly selectedRegistryDefinition = signal<ComponentRegistryDefinition | null>(null);
  readonly selectedRegistryInstance = signal<ComponentRegistryInstance | null>(null);
  readonly hoveredPreviewGuid = signal<string | null>(null);
  readonly previewHoveredGraphCode = signal<string | null>(null);
  readonly graphAttributeValueOverrides = signal<Record<string, string>>({});
  readonly graphAttributeDrafts = signal<Record<string, string>>({});
  readonly previewScale = signal(1);
  readonly activeInspectorTab = signal<'overview' | 'preview' | 'issues'>('overview');
  readonly xrayRunning = signal(false);
  readonly xrayModalOpen = signal(false);
  readonly xrayStageStates = signal<XRayStageState[]>(this.createInitialXRayStages());
  readonly xrayProgressPercent = signal(0);
  readonly xrayProgressLabel = signal('Ready to scan');
  readonly xrayProgressMessage = signal('Run X-Ray Agent to inventory the graph and preview canvas.');
  readonly xrayScanSummary = signal<SystemShellGraphIssueScanSummary | null>(null);
  readonly xrayConclusion = signal<XRayConclusion | null>(null);
  readonly selectedInspectorIssueCodes = signal<string[]>([]);
  readonly inspectorIssueStatusFilter = signal<'open' | 'all'>('open');
  readonly inspectorIssueCategoryFilter = signal<string>('all');
  readonly viewedIssueCode = signal<string | null>(null);
  readonly issuePromptDraft = signal('');
  readonly issuePromptSaving = signal(false);
  readonly previewViewportProfile = signal<PreviewViewportProfile>('web');
  readonly overviewInspectorIssuesFirst = signal(0);
  readonly previewObjectIssuesFirst = signal(0);
  readonly issuesTabInspectorIssuesFirst = signal(0);
  readonly selectedRegistryTreeNode = signal<TreeNode<SystemShellTreeNodeData> | null>(null);
  readonly accordionBuilderRenderMethod = signal<'Static' | 'Dynamic'>('Static');
  readonly accordionBuilderPanelCount = signal(3);
  readonly accordionBuilderStaticPanels = signal<AccordionBuilderPanel[]>([
    { value: '0', header: 'Header I', content: 'Panel content I', disabled: false },
    { value: '1', header: 'Header II', content: 'Panel content II', disabled: false },
    { value: '2', header: 'Header III', content: 'Panel content III', disabled: false },
  ]);
  readonly accordionBuilderDataSource = signal('panels');
  readonly accordionBuilderValueField = signal('value');
  readonly accordionBuilderHeaderField = signal('header');
  readonly accordionBuilderContentField = signal('content');
  readonly accordionBuilderDisabledField = signal('disabled');
  readonly accordionBuilderMultiple = signal(false);
  readonly accordionBuilderSelectOnFocus = signal(false);
  readonly accordionBuilderDefaultValue = signal('0');
  readonly accordionBuilderExpandIcon = signal('');
  readonly accordionBuilderCollapseIcon = signal('');
  readonly accordionInstanceName = signal('');
  readonly accordionInstanceStatus = signal('draft');
  readonly accordionInstanceDescription = signal('');
  readonly accordionTargetObjectCode = signal('');
  readonly accessibilityTargetLevel: AccessibilityConformanceLevel = 'AAA';
  readonly previewViewportProfiles: readonly PreviewViewportOption[] = PREVIEW_VIEWPORT_OPTIONS;
  readonly activeRelationshipTab = signal<string>('validation-control');
  readonly workspaceDockItems: MenuItem[] = [
    {
      id: 'frontend',
      label: 'Frontend',
      icon: 'pi pi-desktop',
      tooltipOptions: {
        tooltipLabel: 'Frontend',
        tooltipPosition: 'top',
        showDelay: 150,
      },
      command: () => this.activateWorkspace('frontend'),
    },
    {
      id: 'components-registry',
      label: 'Components Registry',
      icon: 'pi pi-box',
      tooltipOptions: {
        tooltipLabel: 'Components Registry',
        tooltipPosition: 'top',
        showDelay: 150,
      },
      command: () => this.activateWorkspace('components-registry'),
    },
  ];
  readonly activeWorkspaceTitle = computed(() =>
    this.activeWorkspace() === 'frontend' ? 'Frontend' : 'Components Registry',
  );
  readonly filteredRegistryItems = computed(() => {
    const searchTerm = this.registrySearchTerm().trim().toLowerCase();
    return this.registryDefinitions()
      .filter((item) =>
        !searchTerm
        || item.assetName.toLowerCase().includes(searchTerm)
        || item.assetType.toLowerCase().includes(searchTerm)
        || item.code.toLowerCase().includes(searchTerm),
      );
  });
  readonly frontendNavigationTree = computed<TreeNode<SystemShellTreeNodeData>[]>(() => this.state.tree());
  readonly registryTree = computed<TreeNode<SystemShellTreeNodeData>[]>(() => this.buildRegistryTree());
  readonly activeTree = computed<TreeNode<SystemShellTreeNodeData>[]>(() =>
    this.activeWorkspace() === 'frontend' ? this.frontendNavigationTree() : this.registryTree(),
  );
  readonly activeTreeSelection = computed<TreeNode<SystemShellTreeNodeData> | null>(() =>
    this.activeWorkspace() === 'frontend' ? this.state.selectedTreeNode() : this.selectedRegistryTreeNode(),
  );
  readonly issueRegistryNodes = computed<SystemShellGraphNode[]>(() =>
    (this.state.graph()?.nodes ?? []).filter((node) => node.layer === 'instance' && node.family === 'Issue'),
  );
  readonly inspectorIssueStatusOptions = [
    { value: 'open', label: 'Open Issues' },
    { value: 'all', label: 'All Issues' },
  ] as const;
  readonly openIssueRegistryNodes = computed<SystemShellGraphNode[]>(() =>
    this.issueRegistryNodes().filter((node) => node.status === 'open'),
  );
  readonly structuralInspectionViolations = computed<Map<string, string[]>>(() => {
    const issueMap = new Map<string, string[]>();
    for (const issue of this.openIssueRegistryNodes()) {
      const targetObjectId = this.issueTargetObjectId(issue);
      if (!targetObjectId) {
        continue;
      }
      const current = issueMap.get(targetObjectId) ?? [];
      current.push(issue.description ?? issue.name ?? issue.code);
      issueMap.set(targetObjectId, current);
    }
    return issueMap;
  });
  readonly directOpenIssueCountByObjectId = computed<Map<string, number>>(() => {
    const counts = new Map<string, number>();
    for (const issue of this.openIssueRegistryNodes()) {
      const targetObjectId = this.issueTargetObjectId(issue);
      if (!targetObjectId) {
        continue;
      }

      counts.set(targetObjectId, (counts.get(targetObjectId) ?? 0) + 1);
    }
    return counts;
  });
  readonly openIssueCountByObjectId = computed<Map<string, number>>(() => {
    const counts = new Map<string, number>();
    for (const issue of this.openIssueRegistryNodes()) {
      const targetObjectId = this.issueTargetObjectId(issue);
      if (!targetObjectId) {
        continue;
      }

      for (const objectId of this.collectStructuralAncestorObjectIds(targetObjectId)) {
        counts.set(objectId, (counts.get(objectId) ?? 0) + 1);
      }
    }
    return counts;
  });
  readonly inspectorIssueObjects = computed<SystemShellGraphIssueNode[]>(() => {
    const nodeMap = this.state.nodeMap();
    return this.issueRegistryNodes()
      .slice()
      .sort((left, right) => {
        const leftTarget = this.issueTargetObjectId(left) ?? '';
        const rightTarget = this.issueTargetObjectId(right) ?? '';
        return leftTarget.localeCompare(rightTarget) || left.code.localeCompare(right.code);
      })
      .map((issue, index) => this.mapPersistedIssueNode(issue, index + 1, nodeMap));
  });
  readonly selectedInspectorIssues = computed<SystemShellGraphIssueNode[]>(() => {
    const selectedObjectId = this.state.selectedGraphNode()?.id ?? null;
    const issues = this.inspectorIssueObjects();
    if (!selectedObjectId) {
      return issues;
    }

    const scopeObjectIds = this.collectStructuralScopeObjectIds(selectedObjectId);
    return issues.filter((issue) => !!issue.targetObjectId && scopeObjectIds.has(issue.targetObjectId));
  });
  readonly statusFilteredSelectedInspectorIssues = computed<SystemShellGraphIssueNode[]>(() => {
    const issues = this.selectedInspectorIssues();
    return this.inspectorIssueStatusFilter() === 'all'
      ? issues
      : issues.filter((issue) => issue.status === 'open');
  });
  readonly inspectorIssueCategoryOptions = computed(() => {
    const categories = Array.from(new Set(this.statusFilteredSelectedInspectorIssues().map((issue) => issue.category)))
      .sort((left, right) => left.localeCompare(right));
    return [
      { value: 'all', label: 'All Categories' },
      ...categories.map((category) => ({ value: category, label: category })),
    ];
  });
  readonly filteredSelectedInspectorIssues = computed<SystemShellGraphIssueNode[]>(() => {
    const filter = this.inspectorIssueCategoryFilter();
    if (filter === 'all') {
      return this.statusFilteredSelectedInspectorIssues();
    }
    return this.statusFilteredSelectedInspectorIssues().filter((issue) => issue.category === filter);
  });
  readonly selectedPreviewIssueCandidates = computed<SystemShellGraphIssueNode[]>(() => {
    const targetObjectIds = new Set<string>();
    const associatedGuids = new Set<string>();
    const selectedNode = this.state.selectedGraphNode();
    if (selectedNode?.id) {
      targetObjectIds.add(selectedNode.id);
    }
    if (selectedNode?.guid) {
      associatedGuids.add(selectedNode.guid);
    }
    if (selectedNode?.family === 'Component') {
      const targetNode = this.componentTargetNode(selectedNode);
      if (targetNode?.id) {
        targetObjectIds.add(targetNode.id);
      }
      if (targetNode?.guid) {
        associatedGuids.add(targetNode.guid);
      }
    }
    const selectedPreviewGuid = this.selectedPreviewGuid()?.trim();
    if (selectedPreviewGuid) {
      associatedGuids.add(selectedPreviewGuid);
      const domTargetNode = this.state.nodeGuidMap().get(selectedPreviewGuid);
      if (domTargetNode?.id) {
        targetObjectIds.add(domTargetNode.id);
      }
    }
    if (!targetObjectIds.size && !associatedGuids.size) {
      return [];
    }

    return this.inspectorIssueObjects().filter((issue) =>
      (!!issue.targetObjectId && targetObjectIds.has(issue.targetObjectId))
      || (!!this.resolvedIssueGuid(issue) && associatedGuids.has(this.resolvedIssueGuid(issue)!)),
    );
  });
  readonly selectedPreviewObjectIssues = computed<SystemShellGraphIssueNode[]>(() =>
    this.inspectorIssueStatusFilter() === 'all'
      ? this.selectedPreviewIssueCandidates()
      : this.selectedPreviewIssueCandidates().filter((issue) => issue.status === 'open'),
  );
  readonly selectedInspectorIssueSelection = computed<SystemShellGraphIssueNode[]>(() => {
    const selectedCodes = new Set(this.selectedInspectorIssueCodes());
    return this.filteredSelectedInspectorIssues().filter((issue) => selectedCodes.has(issue.code));
  });
  readonly selectedOpenInspectorIssueCount = computed<number>(() =>
    this.selectedInspectorIssueSelection().filter((issue) => issue.status === 'open').length,
  );
  readonly selectedInspectorIssueSummary = computed(() => {
    const issues = this.selectedInspectorIssues();
    const countBy = (predicate: (issue: SystemShellGraphIssueNode) => boolean) =>
      issues.filter(predicate).length;

    return [
      { label: 'Open Issues', value: String(countBy((issue) => issue.status === 'open')) },
      { label: 'Closed Issues', value: String(countBy((issue) => issue.status === 'closed')) },
      { label: 'Structure', value: String(countBy((issue) => issue.category === 'Structure')) },
      { label: 'Preview', value: String(countBy((issue) => ['Container Styling', 'Viewport Navigation'].includes(issue.category))) },
      { label: 'HTML', value: String(countBy((issue) => issue.category === 'HTML Element Violation')) },
      { label: 'Parity', value: String(countBy((issue) => issue.category === 'Preview-Tree Parity')) },
      { label: 'Accessibility', value: String(countBy((issue) => issue.category === 'Accessibility')) },
    ];
  });
  readonly viewedInspectorIssue = computed<SystemShellGraphIssueNode | null>(() =>
    this.inspectorIssueObjects().find((issue) => issue.code === this.viewedIssueCode()) ?? null,
  );
  readonly inspectorIssueEmptyMessage = computed(() =>
    this.selectedInspectorIssues().length
      ? 'No design issues matched the selected filters.'
      : 'No design issues detected for the selected object scope.',
  );
  readonly previewIssueEmptyMessage = computed(() =>
    this.selectedPreviewIssueCandidates().length
      ? 'No issues matched the selected status filter for the selected preview object.'
      : 'No issues are associated with the selected preview object.',
  );
  readonly selectedFrontendComponentInstanceView = computed<ComponentRegistryInstance | null>(() => {
    const node = this.state.selectedGraphNode();
    if (!node || node.family !== 'Component') {
      return null;
    }
    const targetNode = this.componentTargetNode(node);

    return {
      code: node.code,
      objectType: node.objectType ?? 'Component',
      assetType: node.assetType ?? '',
      assetName: node.assetName ?? node.name ?? '',
      name: node.name,
      description: node.description,
      id: node.id,
      status: node.status,
      definitionCode: node.definitionCode,
      implementationSourcePath: node.implementationSourcePath,
      targetObjectCode: targetNode?.code ?? null,
      targetObjectName: targetNode ? this.displayNodeName(targetNode) : null,
      targetObjectType: targetNode?.family ?? null,
      configuration: this.parseConfiguration(node.configurationJson),
    };
  });
  readonly activeComponentInstanceView = computed<ComponentRegistryInstance | null>(() => {
    const instance = this.activeWorkspace() === 'frontend'
      ? this.selectedFrontendComponentInstanceView()
      : this.selectedRegistryInstance();

    return instance?.code ? instance : null;
  });
  readonly activeComponentDefinitionView = computed<ComponentRegistryDefinition | null>(() => {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return this.activeWorkspace() === 'components-registry' ? this.selectedRegistryDefinition() : null;
    }

    return this.registryDefinitions().find((item) =>
      item.code === instance.definitionCode
      || item.assetType === instance.assetType
      || item.assetName === instance.assetName,
    ) ?? null;
  });
  readonly activeScreenCode = computed(() => {
    const selected = this.state.selectedGraphNode();
    const componentInstance = this.selectedFrontendComponentInstanceView();
    if (!selected && !componentInstance) {
      return 'SHL01.SCN01';
    }

    return this.resolveAssociatedScreenCode(componentInstance?.targetObjectCode ?? selected?.id ?? selected?.code ?? '') ?? 'SHL01.SCN01';
  });
  readonly activeShellCode = computed(() => {
    const selected = this.state.selectedGraphNode();
    const componentInstance = this.selectedFrontendComponentInstanceView();
    if (!selected && !componentInstance) {
      return 'SHL01';
    }

    return this.resolveAssociatedShellCode(componentInstance?.targetObjectCode ?? selected?.id ?? selected?.code ?? '') ?? 'SHL01';
  });
  readonly activeShellBackgroundConfig = computed<ShellBackgroundConfig | null>(() => {
    const shellNode = this.state.nodeMap().get(this.activeShellCode());
    if (!shellNode || shellNode.family !== 'Shell') {
      return null;
    }

    return {
      backgroundType: this.resolveGraphNodeAttribute(shellNode, 'backgroundType'),
      backgroundColorStyle: this.resolveGraphNodeAttribute(shellNode, 'backgroundColorStyle'),
      backgroundPatternKey: this.resolveGraphNodeAttribute(shellNode, 'backgroundPatternKey'),
      backgroundPatternOpacity: shellNode.backgroundPatternOpacity,
      backgroundImagePath: this.resolveGraphNodeAttribute(shellNode, 'backgroundImagePath'),
    };
  });
  readonly activePreviewInteractionPalette = computed<PreviewInteractionPalette>(() => {
    if (this.activeShellCode() === 'SHL01') {
      return {
        selected: 'color-mix(in srgb, var(--tp-primary-dark) 78%, white 22%)',
        hover: 'color-mix(in srgb, var(--tp-primary) 68%, white 32%)',
      };
    }

    return {
      selected: 'color-mix(in srgb, #f6d36c 82%, white 18%)',
      hover: 'color-mix(in srgb, #ffe3a1 74%, white 26%)',
    };
  });

  shellBackgroundConfigFor(shellCode: string): ShellBackgroundConfig | null {
    const shellNode = this.state.nodeMap().get(shellCode);
    if (!shellNode || shellNode.family !== 'Shell') {
      return null;
    }

    return {
      backgroundType: this.resolveGraphNodeAttribute(shellNode, 'backgroundType'),
      backgroundColorStyle: this.resolveGraphNodeAttribute(shellNode, 'backgroundColorStyle'),
      backgroundPatternKey: this.resolveGraphNodeAttribute(shellNode, 'backgroundPatternKey'),
      backgroundPatternOpacity: shellNode.backgroundPatternOpacity,
      backgroundImagePath: this.resolveGraphNodeAttribute(shellNode, 'backgroundImagePath'),
    };
  }
  readonly inspectorBreadcrumb = computed(() => {
    const selectedObjectId = this.state.selectedTreeNode()?.data?.objectId;
    if (!selectedObjectId) {
      return [{ objectId: null, code: 'none', label: 'Select a screen, section, or element' }];
    }

    const path = this.findTreePathByObjectId(this.state.tree(), selectedObjectId);
    if (!path.length) {
      const selected = this.state.selectedGraphNode();
      return [{ objectId: selectedObjectId, code: selected?.code ?? 'n/a', label: selected?.name ?? selectedObjectId }];
    }

    return path.map((node) => ({
      objectId: node.data?.objectId ?? null,
      code: node.data?.code ?? node.key ?? '',
      label: node.label ?? node.data?.label ?? node.data?.code ?? 'Unknown',
    }));
  });
  readonly selectedFactSheetHeader = computed(() => {
    const node = this.state.selectedGraphNode();
    if (!node) {
      return {
        family: 'Graph Node',
        familyMark: 'GN',
        title: 'Select a node',
        id: 'n/a',
        guid: 'n/a',
        description: 'Choose an object instance from the tree to inspect its canonical details.',
        status: 'n/a',
        shellContext: null as string | null,
      };
    }

    return {
      family: node.family,
      familyMark: this.familyMark(node.family),
      title: this.displayNodeName(node),
      id: this.formatValue(node.id),
      guid: this.formatValue(node.guid),
      displayName: this.displayNodeName(node),
      description: node.description ?? 'No description available.',
      status: this.formatValue(node.status),
    };
  });
  readonly selectedAttributePanelTitle = computed(() => {
    const node = this.state.selectedGraphNode();
    if (!node) {
      return 'Object-Specific Attributes';
    }

    if (node.domain === 'frontend' && ['Shell', 'Screen', 'Section', 'Element'].includes(node.family)) {
      return 'Frontend Rendering Contract';
    }

    return 'Object-Specific Attributes';
  });
  readonly selectedAttributeRows = computed(() => {
    const node = this.state.selectedGraphNode();
    if (!node) {
      return [];
    }

    const rows = this.attributesForNode(node);
    return rows.filter((row) => row.editable || row.value !== 'n/a');
  });
  readonly selectedRemainingAttributeRows = computed(() => {
    const node = this.state.selectedGraphNode();
    if (!node) {
      return [];
    }

    return this.remainingAttributeRowsForNode(node);
  });
  readonly selectedAttributeEmptyMessage = computed(() => {
    const title = this.selectedAttributePanelTitle();
    return title === 'Frontend Rendering Contract'
      ? 'No frontend rendering contract attributes available.'
      : 'No object-specific attributes available.';
  });
  readonly selectedRelationships = computed<RelationshipRow[]>(() => {
    const node = this.state.selectedGraphNode();
    const graph = this.state.graph();
    if (!node || !graph) {
      return [];
    }

    const nodeMap = this.state.nodeMap();
      const relationshipPriority: Record<string, number> = {
        GOVERNED_BY: 10,
        HAS_BLOCKER: 20,
        RAISES: 30,
        USES_RULE_SET: 40,
        USES_VIEWPORT_PROFILE: 45,
        HAS_RULE: 50,
        TARGETS: 60,
        ACTIVATES_SCREEN: 70,
        HAS_SHELL: 80,
        HAS_SCREEN: 90,
        HAS_SECTION: 100,
        HAS_ELEMENT: 110,
        HAS_COMPONENT: 120,
        HAS_STEP: 130,
        CAN_EXECUTE: 140,
        REFERENCES: 150,
      };

    return graph.relationships
      .filter((relationship) => relationship.fromId === node.id || relationship.toId === node.id)
      .map((relationship) => {
        const isOutgoing = relationship.fromId === node.id;
        const connectedObjectId = isOutgoing ? relationship.toId : relationship.fromId;
        const connectedNode = connectedObjectId ? this.state.nodeIdMap().get(connectedObjectId) ?? null : null;
        const connectedCode = connectedNode?.code ?? connectedObjectId ?? 'Unknown';
        const displayType = isOutgoing
          ? relationship.activeName ?? relationship.relationshipType
          : relationship.passiveName ?? relationship.relationshipType;
        return {
          direction: isOutgoing ? 'Outgoing' : 'Incoming',
          canonicalType: relationship.relationshipType,
          type: displayType,
          connectedFamily: connectedNode?.family ?? 'Unknown',
          connectedLayer: connectedNode?.layer ?? 'unknown',
          connectedObjectId: connectedNode?.id ?? connectedObjectId ?? null,
          connectedCode,
          connectedName: connectedNode ? this.displayNodeName(connectedNode) : connectedCode,
        };
      })
      .filter((relationship) => relationship.connectedLayer === 'instance')
      .sort((left, right) =>
        (relationshipPriority[left.canonicalType] ?? 999) - (relationshipPriority[right.canonicalType] ?? 999)
        || left.direction.localeCompare(right.direction)
        || left.connectedFamily.localeCompare(right.connectedFamily)
        || left.connectedCode.localeCompare(right.connectedCode),
      );
  });
  readonly selectedRelationshipGroups = computed<RelationshipGroup[]>(() => {
    const groups = new Map<string, RelationshipGroup>();

    for (const row of this.selectedRelationships()) {
      const key = this.relationshipGroupKey(row);
      const label = this.relationshipGroupLabel(key);
      const existing = groups.get(key);
      if (existing) {
        existing.rows.push(row);
      } else {
        groups.set(key, { key, label, rows: [row] });
      }
    }

    return Array.from(groups.values()).sort(
      (left, right) => this.relationshipGroupOrder(left.key) - this.relationshipGroupOrder(right.key),
    );
  });
  readonly selectedFactSheetSummary = computed(() => {
    const node = this.state.selectedGraphNode();
    if (!node) {
      return [];
    }

    const relationships = this.selectedRelationships();
    const countByFamily = (family: string) =>
      relationships.filter((relationship) => relationship.connectedFamily === family).length;

    return [
      { label: 'Validation Rules', value: String(countByFamily('ValidationRule')) },
      { label: 'Business Rules', value: String(countByFamily('BusinessRule')) },
      { label: 'Blockers', value: String(countByFamily('Blocker')) },
      { label: 'Journeys / Steps', value: String(countByFamily('Journey') + countByFamily('JourneyStep')) },
      { label: 'Relationships', value: String(relationships.length) },
    ];
  });
  readonly selectedPreviewGuid = computed(() => {
    this.activeScreenCode();
    this.activeShellCode();
    return this.resolveSelectedPreviewGuid();
  });
  readonly selectedRegistryDefinitionView = computed(() => this.selectedRegistryDefinition());
  readonly selectedRegistryInstanceView = computed(() => this.selectedRegistryInstance());
  readonly registryFactSheetHeader = computed(() => {
    const instance = this.activeComponentInstanceView();
    const definition = this.activeComponentDefinitionView();
    if (!instance || !definition) {
      return {
        title: 'Select a component',
        id: 'n/a',
        type: 'Component',
        code: 'n/a',
        assetName: 'n/a',
        assetType: 'n/a',
        implementationSourcePath: 'n/a',
        status: 'n/a',
        description: 'Choose a component asset from the registry to inspect and edit its instance configuration.',
      };
    }

    return {
      title: instance.name || definition.assetName,
      id: this.formatValue(instance.id),
      type: this.formatValue(instance.objectType),
      code: this.formatValue(instance.code),
      assetName: this.formatValue(instance.assetName),
      assetType: this.formatValue(instance.assetType),
      implementationSourcePath: this.formatValue(instance.implementationSourcePath ?? definition.implementationSourcePath),
      status: this.formatValue(instance.status),
      description: instance.description ?? definition.description ?? 'No description available.',
    };
  });
  readonly registryInstanceSummary = computed(() => {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return [];
    }

    const configuration = instance.configuration ?? {};
    const panels = Array.isArray(configuration['panels']) ? configuration['panels'].length : 0;

    return [
      { label: 'Target Object', value: instance.targetObjectCode ? '1' : '0' },
      { label: 'Configured Fields', value: String(Object.keys(configuration).length) },
      { label: 'Panels', value: String(panels) },
      { label: 'Edit State', value: this.registryEditMode() ? 'Edit' : 'View' },
    ];
  });
  readonly registryCommonAttributeRows = computed(() => {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return [];
    }

    return [
      { label: 'ID', value: this.formatValue(instance.id) },
      { label: 'Type', value: this.formatValue(instance.objectType) },
      { label: 'Code', value: this.formatValue(instance.code) },
      { label: 'Asset Name', value: this.formatValue(instance.assetName) },
      { label: 'Asset Type', value: this.formatValue(instance.assetType) },
      { label: 'Implementation Source Path', value: this.formatValue(instance.implementationSourcePath) },
      { label: 'Status', value: this.formatValue(instance.status) },
      { label: 'Description', value: this.formatValue(instance.description) },
    ];
  });
  readonly registryPlacementRows = computed(() => {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return [];
    }

    return [
      { label: 'Target Object', value: this.formatValue(instance.targetObjectCode) },
      { label: 'Target Name', value: this.formatValue(instance.targetObjectName) },
      { label: 'Target Type', value: this.formatValue(instance.targetObjectType) },
    ];
  });
  readonly accordionConfigurationRows = computed(() => {
    const instance = this.activeComponentInstanceView();
    if (!instance || instance.assetType !== 'accordion') {
      return [];
    }

    const configuration = instance.configuration ?? {};
    return [
      { label: 'Render Method', value: this.formatValue(configuration['renderMethod']) },
      { label: 'Multiple', value: this.formatValue(configuration['multiple']) },
      { label: 'Default Value', value: this.formatValue(configuration['defaultValue']) },
      { label: 'Select On Focus', value: this.formatValue(configuration['selectOnFocus']) },
      { label: 'Expand Icon', value: this.formatValue(configuration['expandIcon']) },
      { label: 'Collapse Icon', value: this.formatValue(configuration['collapseIcon']) },
      { label: 'Data Source', value: this.formatValue(configuration['dataSource']) },
      { label: 'Value Field', value: this.formatValue(configuration['valueField']) },
      { label: 'Header Field', value: this.formatValue(configuration['headerField']) },
      { label: 'Content Field', value: this.formatValue(configuration['contentField']) },
      { label: 'Disabled Field', value: this.formatValue(configuration['disabledField']) },
    ].filter((row) => row.value !== 'n/a');
  });
  readonly registryTargetOptions = computed(() =>
    (this.state.graph()?.nodes ?? [])
      .filter((node) => node.layer === 'instance' && node.family === 'Element')
      .sort((left, right) => (left.hierarchyCode ?? left.code).localeCompare(right.hierarchyCode ?? right.code))
      .map((node) => ({
        code: node.code,
        label: `${node.code} · ${this.displayNodeName(node)}`,
      })),
  );
  readonly activeScreenNode = computed(() => {
    const screenCode = this.activeScreenCode();
    const node = this.state.nodeMap().get(screenCode);
    return node?.family === 'Screen' ? node : null;
  });
  readonly previewViewportNode = computed(() => {
    const profile = PREVIEW_VIEWPORT_OPTIONS.find((option) => option.value === this.previewViewportProfile()) ?? PREVIEW_VIEWPORT_OPTIONS[0]!;
    return this.resolveViewportProfile(profile.viewportProfileCode ?? null);
  });
  readonly previewCanvasWidth = computed(() => {
    return this.previewViewportNode()?.viewportWidth ?? 1440;
  });
  readonly previewCanvasHeight = computed(() => {
    return this.previewViewportNode()?.viewportHeight ?? 1024;
  });
  readonly previewViewportSummary = computed(() => {
    const profile = PREVIEW_VIEWPORT_OPTIONS.find((option) => option.value === this.previewViewportProfile()) ?? PREVIEW_VIEWPORT_OPTIONS[0]!;
    return `${profile.label} · ${this.previewCanvasWidth()} × ${this.previewCanvasHeight()}`;
  });

  @ViewChild('previewStage') private previewStageRef?: ElementRef<HTMLElement>;
  @ViewChild('previewAuditRoot') private previewAuditRootRef?: ElementRef<HTMLElement>;
  private previewResizeObserver?: ResizeObserver;
  private hostClickListener?: (event: MouseEvent) => void;
  private previewClickListener?: (event: MouseEvent) => void;
  private previewMoveListener?: (event: MouseEvent) => void;
  private previewLeaveListener?: () => void;

  private readonly focusSync = effect(() => {
    const selectedPreviewGuid = this.selectedPreviewGuid();
    if (!selectedPreviewGuid) {
      return;
    }

    requestAnimationFrame(() => {
      this.findPreviewElementByGuid(selectedPreviewGuid)?.scrollIntoView({
        block: 'nearest',
        inline: 'nearest',
      });
    });
  });

  private readonly hoverSync = effect(() => {
    const hoveredPreviewGuid = this.hoveredPreviewGuid();
    document.querySelectorAll<HTMLElement>('.ssg-hovered').forEach((element) => {
      element.classList.remove('ssg-hovered');
    });

    if (!hoveredPreviewGuid || hoveredPreviewGuid === this.selectedPreviewGuid()) {
      return;
    }

    this.findPreviewElementByGuid(hoveredPreviewGuid)?.classList.add('ssg-hovered');
  });

  private readonly previewIssueWarningSync = effect(() => {
    this.activeWorkspace();
    const stage = this.previewStageRef?.nativeElement;
    const selectedPreviewGuid = this.selectedPreviewGuid();
    const hoveredPreviewGuid = this.hoveredPreviewGuid();

    stage?.querySelectorAll<HTMLElement>('.ssg-issue-warning').forEach((element) => {
      element.classList.remove('ssg-issue-warning');
    });

    if (this.activeWorkspace() !== 'frontend' || !stage) {
      return;
    }

    this.togglePreviewIssueWarning(selectedPreviewGuid);
    if (hoveredPreviewGuid && hoveredPreviewGuid !== selectedPreviewGuid) {
      this.togglePreviewIssueWarning(hoveredPreviewGuid);
    }
  });

  private readonly previewScaleSync = effect(() => {
    this.activeScreenCode();
    this.previewCanvasWidth();
    this.previewCanvasHeight();
    requestAnimationFrame(() => this.recalculatePreviewScale());
  });

  private readonly relationshipTabSync = effect(() => {
    const groups = this.selectedRelationshipGroups();
    const current = this.activeRelationshipTab();
    if (!groups.length) {
      if (current !== '') {
        this.activeRelationshipTab.set('');
      }
      return;
    }

    if (!groups.some((group) => group.key === current)) {
      this.activeRelationshipTab.set(groups[0].key);
    }
  });

  private readonly inspectorIssuePaginatorSync = effect(() => {
    this.state.selectedGraphNode()?.id ?? null;
    this.inspectorIssueStatusFilter();
    this.inspectorIssueCategoryFilter();
    this.filteredSelectedInspectorIssues().length;
    this.overviewInspectorIssuesFirst.set(0);
    this.issuesTabInspectorIssuesFirst.set(0);
  });

  private readonly previewIssuePaginatorSync = effect(() => {
    this.state.selectedGraphNode()?.id ?? null;
    this.inspectorIssueStatusFilter();
    this.selectedPreviewGuid();
    this.selectedPreviewObjectIssues().length;
    this.previewObjectIssuesFirst.set(0);
  });

  private readonly registrySelectionSync = effect(() => {
    const selected = this.selectedRegistryTreeNode();
    if (!selected || selected.data?.kind !== 'registry-item') {
      return;
    }

    const visibleCodes = new Set(this.filteredRegistryItems().map((item) => item.code));
    if (!visibleCodes.has(selected.data.code)) {
      this.selectedRegistryTreeNode.set(null);
    }
  });

  private readonly registryInstanceSync = effect(() => {
    const selected = this.selectedRegistryTreeNode();
    const assetType = selected?.data?.registryAssetType;
    const definitionCode = selected?.data?.code;
    if (!assetType || !definitionCode) {
      this.selectedRegistryDefinition.set(null);
      this.selectedRegistryInstance.set(null);
      this.registryEditMode.set(false);
      return;
    }

    const definition = this.registryDefinitions().find((item) => item.code === definitionCode) ?? null;
    this.selectedRegistryDefinition.set(definition);
    this.registryEditMode.set(false);
    void this.loadRegistryInstance(assetType);
  });

  private readonly activeComponentInstanceSync = effect(() => {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return;
    }

    this.hydrateRegistryEditor(instance);
  });

  ngOnInit(): void {
    void this.state.load();
    void this.loadRegistryDefinitions();
  }

  ngAfterViewInit(): void {
    this.bindPreviewStageObserver();
    this.bindHostActions();
    this.bindPreviewInteractions();
  }

  ngOnDestroy(): void {
    this.previewResizeObserver?.disconnect();
    if (this.hostClickListener) {
      this.hostElement.nativeElement.removeEventListener('click', this.hostClickListener);
      this.hostClickListener = undefined;
    }
    this.unbindPreviewInteractions();
  }

  activateWorkspace(workspace: 'frontend' | 'components-registry'): void {
    this.activeWorkspace.set(workspace);
    this.registryEditMode.set(false);
    this.hoveredPreviewGuid.set(null);
    if (workspace === 'frontend') {
      requestAnimationFrame(() => {
        this.bindPreviewStageObserver();
        this.bindPreviewInteractions();
        this.recalculatePreviewScale();
      });
      return;
    }

    this.unbindPreviewInteractions();
    if (!this.registryDefinitions().length && !this.registryLoading()) {
      void this.loadRegistryDefinitions();
    }
  }

  expandActiveTree(): void {
    if (this.activeWorkspace() === 'frontend') {
      this.state.expandAll();
      return;
    }

    this.registryExpanded.set(true);
  }

  collapseActiveTree(): void {
    if (this.activeWorkspace() === 'frontend') {
      this.state.collapseAll();
      return;
    }

    this.registryExpanded.set(false);
  }

  onTreeSelection(node: TreeNode<SystemShellTreeNodeData> | TreeNode<SystemShellTreeNodeData>[] | null | undefined): void {
    const selectedNode = Array.isArray(node) ? node[0] ?? null : node ?? null;
    this.registryEditMode.set(false);
    if (this.activeWorkspace() === 'frontend') {
      this.state.selectNode(selectedNode?.data?.kind === 'graph' ? selectedNode : null);
      return;
    }

    this.selectedRegistryTreeNode.set(selectedNode?.data?.kind === 'registry-item' ? selectedNode : null);
  }

  onTreeNodeClick(node: TreeNode<SystemShellTreeNodeData>, event: Event): void {
    const activeSelection = typeof window !== 'undefined' ? window.getSelection()?.toString().trim() ?? '' : '';
    if (activeSelection) {
      return;
    }

    event.stopPropagation();
    this.registryEditMode.set(false);
    if (this.activeWorkspace() === 'frontend') {
      this.state.selectNode(node.data?.kind === 'graph' ? node : null);
      return;
    }

    this.selectedRegistryTreeNode.set(node.data?.kind === 'registry-item' ? node : null);
  }

  onInspectorTabChange(value: string | number | undefined): void {
    this.activeInspectorTab.set(value === 'preview' || value === 'issues' ? value : 'overview');
    requestAnimationFrame(() => {
      this.bindPreviewStageObserver();
      this.bindPreviewInteractions();
      this.recalculatePreviewScale();
    });
  }

  onBreadcrumbClick(objectId: string | null, fallbackCode: string): void {
    this.activeInspectorTab.set('overview');
    if (objectId) {
      this.state.selectObjectId(objectId);
      return;
    }

    this.state.selectObjectId(this.state.nodeMap().get(fallbackCode)?.id ?? null);
  }

  onRelatedObjectClick(objectId: string | null, fallbackCode: string): void {
    this.activeInspectorTab.set('overview');
    if (objectId) {
      this.state.selectObjectId(objectId);
      return;
    }

    this.state.selectObjectId(this.state.nodeMap().get(fallbackCode)?.id ?? null);
  }

  onRelationshipTabChange(value: string | number | undefined): void {
    this.activeRelationshipTab.set(typeof value === 'string' ? value : '');
  }

  attributeDisplayValue(row: AttributeRow): string {
    return row.value === 'n/a' ? 'Click to edit' : row.value;
  }

  attributeDraftValue(row: AttributeRow): string {
    const key = this.graphAttributeEditKey(row.nodeCode, row.attributeKey);
    const draft = this.graphAttributeDrafts()[key];
    if (typeof draft === 'string') {
      return draft;
    }

    const override = this.graphAttributeValueOverrides()[key];
    if (typeof override === 'string') {
      return override;
    }

    return typeof row.rawValue === 'string' ? row.rawValue : '';
  }

  onAttributeEditorActivate(row: AttributeRow): void {
    if (!row.editable) {
      return;
    }

    const key = this.graphAttributeEditKey(row.nodeCode, row.attributeKey);
    this.graphAttributeDrafts.update((drafts) => ({
      ...drafts,
      [key]: this.attributeDraftValue(row),
    }));
  }

  onAttributeEditorDeactivate(row: AttributeRow): void {
    this.clearAttributeDraft(row);
  }

  onAttributeDraftInput(row: AttributeRow, event: Event): void {
    if (!row.editable) {
      return;
    }

    const value = (event.target as HTMLInputElement | null)?.value ?? '';
    this.updateAttributeDraftValue(row, value);
  }

  onAttributeDraftValueChange(row: AttributeRow, value: string | null | undefined): void {
    if (!row.editable) {
      return;
    }

    this.updateAttributeDraftValue(row, value ?? '');
  }

  private updateAttributeDraftValue(row: AttributeRow, value: string): void {
    const key = this.graphAttributeEditKey(row.nodeCode, row.attributeKey);
    this.graphAttributeDrafts.update((drafts) => ({
      ...drafts,
      [key]: value,
    }));
  }

  applyAttributeDraft(
    row: AttributeRow,
    closeCallback?: (event?: Event) => void,
    event?: Event,
  ): void {
    if (!row.editable) {
      return;
    }

    event?.preventDefault();
    event?.stopPropagation();

    const key = this.graphAttributeEditKey(row.nodeCode, row.attributeKey);
    this.graphAttributeValueOverrides.update((overrides) => ({
      ...overrides,
      [key]: this.attributeDraftValue(row),
    }));
    this.clearAttributeDraft(row);
    closeCallback?.(event);
  }

  cancelAttributeDraft(
    row: AttributeRow,
    closeCallback?: (event?: Event) => void,
    event?: Event,
  ): void {
    event?.preventDefault();
    event?.stopPropagation();
    this.clearAttributeDraft(row);
    closeCallback?.(event);
  }

  onAccordionBuilderRenderMethodChange(event: Event): void {
    const value = (event.target as HTMLSelectElement | null)?.value === 'Dynamic' ? 'Dynamic' : 'Static';
    this.accordionBuilderRenderMethod.set(value);
  }

  onAccordionBuilderPanelCountChange(event: Event): void {
    const raw = Number((event.target as HTMLInputElement | null)?.value ?? this.accordionBuilderPanelCount());
    const nextCount = Math.min(6, Math.max(1, Number.isFinite(raw) ? raw : 1));
    const current = this.accordionBuilderStaticPanels();
    const nextPanels = Array.from({ length: nextCount }, (_, index) => current[index] ?? {
      value: String(index),
      header: `Header ${index + 1}`,
      content: `Panel content ${index + 1}`,
      disabled: false,
    });
    this.accordionBuilderPanelCount.set(nextCount);
    this.accordionBuilderStaticPanels.set(nextPanels);
  }

  onAccordionBuilderPanelFieldChange(index: number, field: 'value' | 'header' | 'content', event: Event): void {
    const value = (event.target as HTMLInputElement | HTMLTextAreaElement | null)?.value ?? '';
    this.accordionBuilderStaticPanels.update((panels) =>
      panels.map((panel, panelIndex) => panelIndex === index ? { ...panel, [field]: value } : panel),
    );
  }

  onAccordionBuilderPanelDisabledChange(index: number, event: Event): void {
    const checked = (event.target as HTMLInputElement | null)?.checked ?? false;
    this.accordionBuilderStaticPanels.update((panels) =>
      panels.map((panel, panelIndex) => panelIndex === index ? { ...panel, disabled: checked } : panel),
    );
  }

  onAccordionBuilderBooleanChange(
    field: 'multiple' | 'selectOnFocus',
    event: Event,
  ): void {
    const checked = (event.target as HTMLInputElement | null)?.checked ?? false;
    if (field === 'multiple') {
      this.accordionBuilderMultiple.set(checked);
      return;
    }
    if (field === 'selectOnFocus') {
      this.accordionBuilderSelectOnFocus.set(checked);
    }
  }

  onAccordionBuilderSimpleFieldChange(
    field: 'dataSource' | 'valueField' | 'headerField' | 'contentField' | 'disabledField' | 'defaultValue' | 'expandIcon' | 'collapseIcon',
    event: Event,
  ): void {
    const value = (event.target as HTMLInputElement | HTMLSelectElement | null)?.value ?? '';
    switch (field) {
      case 'dataSource':
        this.accordionBuilderDataSource.set(value);
        break;
      case 'valueField':
        this.accordionBuilderValueField.set(value);
        break;
      case 'headerField':
        this.accordionBuilderHeaderField.set(value);
        break;
      case 'contentField':
        this.accordionBuilderContentField.set(value);
        break;
      case 'disabledField':
        this.accordionBuilderDisabledField.set(value);
        break;
      case 'defaultValue':
        this.accordionBuilderDefaultValue.set(value);
        break;
      case 'expandIcon':
        this.accordionBuilderExpandIcon.set(value);
        break;
      case 'collapseIcon':
        this.accordionBuilderCollapseIcon.set(value);
        break;
    }
  }

  onAccordionInstanceFieldChange(
    field: 'name' | 'status' | 'description' | 'targetObjectCode',
    event: Event,
  ): void {
    const value = (event.target as HTMLInputElement | HTMLTextAreaElement | HTMLSelectElement | null)?.value ?? '';
    switch (field) {
      case 'name':
        this.accordionInstanceName.set(value);
        break;
      case 'status':
        this.accordionInstanceStatus.set(value);
        break;
      case 'description':
        this.accordionInstanceDescription.set(value);
        break;
      case 'targetObjectCode':
        this.accordionTargetObjectCode.set(value);
        break;
    }
  }

  enableRegistryEdit(): void {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return;
    }

    this.registryEditMode.set(true);
    this.hydrateRegistryEditor(instance);
  }

  cancelRegistryEdit(): void {
    const instance = this.activeComponentInstanceView();
    this.registryEditMode.set(false);
    this.registryInstanceError.set(null);
    if (instance) {
      this.hydrateRegistryEditor(instance);
    }
  }

  async saveRegistryInstance(): Promise<void> {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return;
    }

    this.registryInstanceSaving.set(true);
    this.registryInstanceError.set(null);

    const configuration: Record<string, unknown> = instance.assetType === 'accordion'
      ? this.buildAccordionConfiguration()
      : { ...(instance.configuration ?? {}) };

    try {
      const savedInstance = await firstValueFrom(this.api.saveComponentInstance(instance.code, {
        name: this.accordionInstanceName().trim() || instance.assetName,
        description: this.accordionInstanceDescription().trim() || null,
        status: this.accordionInstanceStatus().trim() || null,
        targetObjectCode: this.accordionTargetObjectCode().trim() || null,
        configuration,
      }));
      if (this.activeWorkspace() === 'components-registry') {
        this.selectedRegistryInstance.set(savedInstance);
      }
      await this.state.load(savedInstance.code);
      this.registryEditMode.set(false);
      this.hydrateRegistryEditor(savedInstance);
    } catch (error) {
      this.registryInstanceError.set(error instanceof Error ? error.message : 'Unable to save the component instance.');
    } finally {
      this.registryInstanceSaving.set(false);
    }
  }

  onRegistrySearch(event: Event): void {
    const value = (event.target as HTMLInputElement | null)?.value ?? '';
    this.registrySearchTerm.set(value);
    this.registryExpanded.set(true);
  }

  async toggleInspectMode(): Promise<void> {
    if (this.activeWorkspace() !== 'frontend') {
      return;
    }

    const graph = this.state.graph();
    if (!graph || this.xrayRunning()) {
      return;
    }

    this.openXRayModal();
    this.xrayRunning.set(true);
    const preferredObjectId = this.activeSelectionObjectId();
    const previousInspectorTab = this.activeInspectorTab();

    try {
      if (previousInspectorTab !== 'preview') {
        this.activeInspectorTab.set('preview');
        await this.ensurePreviewAuditRootReady();
      }

      const report = await this.buildXRayScanReport();
      await this.runXRayStage('issue-persistence', async () => {
        const summary = await firstValueFrom(this.api.scanIssues({ issues: report.issues }));
        await this.state.load(preferredObjectId);
        this.selectedInspectorIssueCodes.set([]);
        this.viewedIssueCode.set(null);
        this.xrayScanSummary.set(summary);
        this.xrayConclusion.set({
          ...report.conclusion,
          summary,
          conclusionText:
            `Scanned ${report.conclusion.totalObjectsScanned} graph objects and `
            + `${report.conclusion.totalPreviewArtifactsScanned} preview artifacts. `
            + `Detected ${summary.totalIssues} persisted issues across ${report.conclusion.issueCategoryRows.length} categories.`,
        });
      });
      this.xrayProgressPercent.set(100);
      this.xrayProgressLabel.set('X-Ray scan completed');
      this.xrayProgressMessage.set(this.xrayConclusion()?.conclusionText ?? 'The X-Ray scan completed.');
    } catch (error) {
      this.registryError.set(error instanceof Error ? error.message : 'Unable to run X-Ray Agent.');
      this.xrayProgressLabel.set('X-Ray scan failed');
      this.xrayProgressMessage.set(this.registryError() ?? 'Unable to run X-Ray Agent.');
      this.xrayConclusion.set(null);
    } finally {
      if (previousInspectorTab !== 'preview') {
        this.activeInspectorTab.set(previousInspectorTab);
      }
      this.xrayRunning.set(false);
    }
  }

  onTreeNodeEnter(node: SystemShellTreeNodeData): void {
    if (this.activeWorkspace() !== 'frontend' || node.kind !== 'graph') {
      this.hoveredPreviewGuid.set(null);
      return;
    }

    this.hoveredPreviewGuid.set(node.domTargetGuid);
  }

  onTreeNodeLeave(): void {
    this.hoveredPreviewGuid.set(null);
  }

  isPreviewHovered(objectId: string | null): boolean {
    if (!objectId) {
      return false;
    }

    return this.state.nodeIdMap().get(objectId)?.code === this.previewHoveredGraphCode();
  }

  updateTreeNodeTooltip(event: Event): void {
    const label = (event.currentTarget as HTMLElement | null)?.querySelector<HTMLElement>('.ssg-tree-text');
    if (!label) {
      return;
    }

    const needsTooltip = label.scrollWidth > label.clientWidth + 1;
    label.title = needsTooltip ? label.textContent?.trim() ?? '' : '';
  }

  isSelected(objectId: string | null): boolean {
    return !!objectId && this.state.selectedObjectId() === objectId;
  }

  hasInspectionViolation(objectId: string | null): boolean {
    return this.openIssueCount(objectId) > 0;
  }

  hasDirectInspectionViolation(objectId: string | null): boolean {
    return objectId ? (this.directOpenIssueCountByObjectId().get(objectId) ?? 0) > 0 : false;
  }

  openIssueCount(objectId: string | null): number {
    return objectId ? this.openIssueCountByObjectId().get(objectId) ?? 0 : 0;
  }

  inspectionViolationTooltip(objectId: string | null): string {
    const messages = objectId ? this.structuralInspectionViolations().get(objectId) ?? [] : [];
    return messages.join('\n');
  }

  inspectionViolationAriaLabel(objectId: string | null): string {
    const count = this.openIssueCount(objectId);
    return count === 1 ? '1 open design issue' : `${count} open design issues`;
  }

  setPreviewViewportProfile(profile: PreviewViewportProfile): void {
    this.previewViewportProfile.set(profile);
  }

  focusInspectorIssue(issue: SystemShellGraphIssueNode): void {
    this.activeWorkspace.set('frontend');
    this.activeInspectorTab.set('preview');
    if (issue.targetObjectId) {
      this.state.selectObjectId(issue.targetObjectId);
      return;
    }

    const resolvedGuid = this.resolvedIssueGuid(issue);
    if (resolvedGuid) {
      this.state.selectGuid(resolvedGuid);
    }
  }

  onInspectorIssueSelectionChange(selection: SystemShellGraphIssueNode[] | SystemShellGraphIssueNode | null | undefined): void {
    const items = Array.isArray(selection) ? selection : selection ? [selection] : [];
    this.selectedInspectorIssueCodes.set(items.map((issue) => issue.code));
  }

  onOverviewInspectorIssuesPage(event: { first?: number | null }): void {
    this.overviewInspectorIssuesFirst.set(event.first ?? 0);
  }

  onPreviewObjectIssuesPage(event: { first?: number | null }): void {
    this.previewObjectIssuesFirst.set(event.first ?? 0);
  }

  onIssuesTabInspectorIssuesPage(event: { first?: number | null }): void {
    this.issuesTabInspectorIssuesFirst.set(event.first ?? 0);
  }

  setInspectorIssueStatusFilter(value: string): void {
    this.inspectorIssueStatusFilter.set(value === 'all' ? 'all' : 'open');
    this.inspectorIssueCategoryFilter.set('all');
    this.selectedInspectorIssueCodes.set([]);
  }

  onInspectorIssueStatusFilterChange(event: Event): void {
    const value = (event.target as HTMLSelectElement | null)?.value ?? 'open';
    this.setInspectorIssueStatusFilter(value);
  }

  setInspectorIssueCategoryFilter(value: string): void {
    this.inspectorIssueCategoryFilter.set(value || 'all');
    this.selectedInspectorIssueCodes.set([]);
  }

  onInspectorIssueCategoryFilterChange(event: Event): void {
    const value = (event.target as HTMLSelectElement | null)?.value ?? 'all';
    this.setInspectorIssueCategoryFilter(value);
  }

  selectAllInspectorIssues(): void {
    this.selectedInspectorIssueCodes.set(this.filteredSelectedInspectorIssues().map((issue) => issue.code));
  }

  clearInspectorIssueSelection(): void {
    this.selectedInspectorIssueCodes.set([]);
  }

  viewInspectorIssue(issue: SystemShellGraphIssueNode): void {
    this.viewedIssueCode.set(issue.code);
    this.issuePromptDraft.set(issue.prompt);
  }

  closeInspectorIssueModal(): void {
    this.viewedIssueCode.set(null);
    this.issuePromptDraft.set('');
  }

  onIssuePromptDraftChange(event: Event): void {
    this.issuePromptDraft.set((event.target as HTMLTextAreaElement | null)?.value ?? '');
  }

  async resolveInspectorIssue(issue: SystemShellGraphIssueNode): Promise<void> {
    await firstValueFrom(this.api.updateIssueStatuses({ issueCodes: [issue.code], status: 'closed' }));
    await this.state.load(this.activeSelectionObjectId());
    this.viewedIssueCode.set(issue.code);
  }

  async resolveSelectedInspectorIssues(): Promise<void> {
    const selectedOpenIssues = this.selectedInspectorIssueSelection().filter((issue) => issue.status === 'open');
    if (!selectedOpenIssues.length) {
      return;
    }

    await firstValueFrom(this.api.updateIssueStatuses({
      issueCodes: selectedOpenIssues.map((issue) => issue.code),
      status: 'closed',
    }));
    await this.state.load(this.activeSelectionObjectId());
    this.selectedInspectorIssueCodes.set([]);
  }

  async saveInspectorIssuePrompt(issue: SystemShellGraphIssueNode): Promise<void> {
    if (this.issuePromptSaving()) {
      return;
    }

    this.issuePromptSaving.set(true);
    try {
      await firstValueFrom(this.api.updateIssue(issue.code, {
        issuePrompt: this.issuePromptDraft().trim() || null,
      }));
      await this.state.load(this.activeSelectionObjectId());
      this.viewedIssueCode.set(issue.code);
    } catch (error) {
      this.registryError.set(error instanceof Error ? error.message : 'Unable to save the issue prompt.');
    } finally {
      this.issuePromptSaving.set(false);
    }
  }

  closeXRaySummaryModal(): void {
    if (this.xrayRunning()) {
      return;
    }

    this.xrayModalOpen.set(false);
    this.xrayScanSummary.set(null);
    this.xrayConclusion.set(null);
  }

  openIssuesTabFromSummary(): void {
    this.activeInspectorTab.set('issues');
    this.closeXRaySummaryModal();
  }

  openIssuesTab(): void {
    this.activeInspectorTab.set('issues');
  }

  private createInitialXRayStages(): XRayStageState[] {
    return X_RAY_STAGE_DEFINITIONS.map((stage) => ({
      ...stage,
      status: 'pending',
    }));
  }

  private openXRayModal(): void {
    this.xrayModalOpen.set(true);
    this.xrayScanSummary.set(null);
    this.xrayConclusion.set(null);
    this.xrayStageStates.set(this.createInitialXRayStages());
    this.xrayProgressPercent.set(0);
    this.xrayProgressLabel.set('Preparing X-Ray scan');
    this.xrayProgressMessage.set('Opening the X-Ray modal and preparing the scan stages.');
    this.registryError.set(null);
  }

  private async runXRayStage<T>(
    key: XRayStageKey,
    task: () => T | Promise<T>,
  ): Promise<T> {
    const stageIndex = X_RAY_STAGE_DEFINITIONS.findIndex((stage) => stage.key === key);
    const stageDefinition = X_RAY_STAGE_DEFINITIONS[stageIndex];
    if (!stageDefinition) {
      return await task();
    }

    this.xrayStageStates.update((stages) =>
      stages.map((stage) => ({
        ...stage,
        status: stage.key === key ? 'active' : stage.status,
      })),
    );
    this.xrayProgressPercent.set(Math.round((stageIndex / X_RAY_STAGE_DEFINITIONS.length) * 100));
    this.xrayProgressLabel.set(stageDefinition.label);
    this.xrayProgressMessage.set(stageDefinition.description);
    await this.waitForPaint();

    const result = await task();

    this.xrayStageStates.update((stages) =>
      stages.map((stage) => ({
        ...stage,
        status: stage.key === key ? 'completed' : stage.status,
      })),
    );
    this.xrayProgressPercent.set(Math.round(((stageIndex + 1) / X_RAY_STAGE_DEFINITIONS.length) * 100));
    await this.waitForPaint();
    return result;
  }

  private waitForPaint(): Promise<void> {
    return new Promise((resolve) => {
      requestAnimationFrame(() => resolve());
    });
  }

  private async ensurePreviewAuditRootReady(): Promise<void> {
    for (let attempt = 0; attempt < 10; attempt += 1) {
      await this.waitForPaint();
      if (this.previewAuditRootRef?.nativeElement) {
        return;
      }
    }
  }

  private async buildXRayScanReport(): Promise<{
    issues: {
      targetObjectId: string;
      targetName: string;
      source: string;
      category: string;
      rule: string;
      message: string;
      severity: string;
    }[];
    conclusion: XRayConclusion;
  }> {
    const graph = this.state.graph();
    if (!graph) {
      return {
        issues: [],
        conclusion: {
          totalObjectsScanned: 0,
          totalPreviewArtifactsScanned: 0,
          graphObjectRows: [],
          previewSummaryRows: [],
          previewTagRows: [],
          issueCategoryRows: [],
          missingObjectIdCount: 0,
          missingObjectGuidCount: 0,
          missingPreviewObjectIdCount: 0,
          missingPreviewGuidCount: 0,
          unmodeledPreviewArtifactCount: 0,
          unselectablePreviewArtifactCount: 0,
          summary: null,
          conclusionText: 'No graph was loaded for the X-Ray scan.',
        },
      };
    }

    const nodeMap = this.state.nodeMap();
    const deduped = new Map<string, {
      targetObjectId: string;
      targetName: string;
      source: string;
      category: string;
      rule: string;
      message: string;
      severity: string;
    }>();

    const addIssue = (targetObjectId: string | null | undefined, message: string): void => {
      const normalizedTargetObjectId = targetObjectId?.trim();
      if (!normalizedTargetObjectId) {
        return;
      }

      const targetNode = this.state.nodeIdMap().get(normalizedTargetObjectId);
      if (!targetNode) {
        return;
      }

      const metadata = this.describeInspectorIssueMessage(message);
      const key = [
        normalizedTargetObjectId,
        metadata.category,
        metadata.rule,
        message,
      ].join('|');

      deduped.set(key, {
        targetObjectId: normalizedTargetObjectId,
        targetName: this.displayNodeName(targetNode),
        source: metadata.source,
        category: metadata.category,
        rule: metadata.rule,
        message,
        severity: 'error',
      });
    };

    const graphInventory = await this.runXRayStage('graph-inventory', async () => this.collectGraphInventorySnapshot(graph));
    const previewAudit = await this.runXRayStage('preview-inventory', async () => this.collectPreviewAuditSnapshot(nodeMap));

    await this.runXRayStage('completeness-checks', async () => {
      for (const node of graphInventory.nodes) {
        const ownerObjectId =
          node.id?.trim()
          ?? this.resolveAssociatedScreenId(node.code)
          ?? this.resolveAssociatedShellId(node.code)
          ?? this.activeSelectionObjectId();
        if (!node.id?.trim()) {
          addIssue(ownerObjectId, `Graph object is missing required id. ${node.family} must persist a UUID id.`);
        }
        if (!node.guid?.trim()) {
          addIssue(ownerObjectId, `Graph object is missing required guid. ${node.family} must persist a preview guid.`);
        }
      }

      for (const artifact of previewAudit.artifacts) {
        if (!artifact.sourceObjectId) {
          addIssue(
            artifact.ownerObjectId,
            `Rendered preview artifact ${artifact.description}${artifact.textSnippet ? ` with text "${artifact.textSnippet}"` : ''} is missing source-object-id. Every visible preview artifact must expose source-object-id and guid.`,
          );
        }
        if (!artifact.guid) {
          addIssue(
            artifact.ownerObjectId,
            `Rendered preview artifact ${artifact.description}${artifact.textSnippet ? ` with text "${artifact.textSnippet}"` : ''} is missing guid. Every visible preview artifact must expose source-object-id and guid.`,
          );
        }
      }
    });

    await this.runXRayStage('parity-checks', async () => {
      for (const [code, messages] of this.buildStructuralInspectionViolations()) {
        const targetNode = nodeMap.get(code);
        if (!targetNode?.id) {
          continue;
        }
        for (const message of messages) {
          addIssue(targetNode.id, message);
        }
      }

      for (const artifact of previewAudit.artifacts) {
        if (artifact.sourceObjectId && artifact.guid) {
          continue;
        }
        addIssue(
          artifact.ownerObjectId,
          `Visible preview artifact ${artifact.description}${artifact.textSnippet ? ` with text "${artifact.textSnippet}"` : ''} is not represented as a Neo4j/tree object. Every visible preview artifact must be modeled and listed in the tree.`,
        );
      }
    });

    await this.runXRayStage('interactivity-checks', async () => {
      for (const artifact of previewAudit.artifacts) {
        if (artifact.isSelectable) {
          continue;
        }

        addIssue(
          artifact.ownerObjectId,
          `Visible preview artifact ${artifact.description}${artifact.textSnippet ? ` with text "${artifact.textSnippet}"` : ''} cannot be independently identified by mouse hover or mouse click. Every visible preview artifact must be independently selectable in the preview canvas.`,
        );
      }
    });

    await this.runXRayStage('issue-aggregation', async () => {
      this.withPreviewAuditRootExposed((auditRoot) => {
        const resolver = new PreviewDomResolver(
          auditRoot,
          nodeMap,
          this.state.nodeIdMap(),
          this.state.nodeGuidMap(),
          this.componentTargetIdMap(),
        );
        const screenNodes = graph.nodes.filter((node) => node.layer === 'instance' && node.family === 'Screen');

        for (const screenNode of screenNodes) {
          const screenElement = resolver.findElementForObjectId(screenNode.id);
          if (!screenElement) {
            continue;
          }

          const shellId = this.resolveAssociatedShellId(screenNode.id);
          const shellElement = shellId ? resolver.findElementForObjectId(shellId) : null;
          const viewportRoot = shellElement ?? screenElement;

          if (this.elementExceedsViewportHeight(viewportRoot) && !this.hasMeaningfulVerticalScrollPath(viewportRoot)) {
            addIssue(
              screenNode.id,
              'Rendered preview content exceeds the viewport height, but no vertical page navigation/scroll path is available; lower content is clipped.',
            );
          }

          for (const issue of auditPreviewAccessibility(
            screenElement,
            this.accessibilityTargetLevel,
            (element) => this.sourceObjectIdFromElement(element),
          )) {
            addIssue(
              issue.graphCode ?? screenNode.id,
              `WCAG ${issue.level}: ${issue.rule}. ${issue.message}`,
            );
          }
        }
      });
    });

    const issues = Array.from(deduped.values());
    const issueCategoryRows = this.toSortedCountRows(
      issues.reduce((counts, issue) => {
        counts.set(issue.category, (counts.get(issue.category) ?? 0) + 1);
        return counts;
      }, new Map<string, number>()),
    );

    return {
      issues,
      conclusion: {
        totalObjectsScanned: graphInventory.totalObjectsScanned,
        totalPreviewArtifactsScanned: previewAudit.artifacts.length,
        graphObjectRows: graphInventory.rows,
        previewSummaryRows: previewAudit.previewSummaryRows,
        previewTagRows: previewAudit.previewTagRows,
        issueCategoryRows,
        missingObjectIdCount: graphInventory.missingObjectIdCount,
        missingObjectGuidCount: graphInventory.missingObjectGuidCount,
        missingPreviewObjectIdCount: previewAudit.missingPreviewObjectIdCount,
        missingPreviewGuidCount: previewAudit.missingPreviewGuidCount,
        unmodeledPreviewArtifactCount: previewAudit.unmodeledPreviewArtifactCount,
        unselectablePreviewArtifactCount: previewAudit.unselectablePreviewArtifactCount,
        summary: null,
        conclusionText:
          `Scanned ${graphInventory.totalObjectsScanned} graph objects and `
          + `${previewAudit.artifacts.length} preview artifacts before persistence.`,
      },
    };
  }

  private collectGraphInventorySnapshot(graph: SystemShellGraphResponse): XRayGraphInventorySnapshot {
    const families = ['Shell', 'Screen', 'Section', 'Element', 'Component'];
    const nodes = graph.nodes.filter((node) =>
      node.layer === 'instance' && families.includes(node.family),
    );
    const rows = families.map((family) => ({
      label: family,
      value: nodes.filter((node) => node.family === family).length,
    }));

    return {
      nodes,
      rows,
      totalObjectsScanned: nodes.length,
      missingObjectIdCount: nodes.filter((node) => !node.id?.trim()).length,
      missingObjectGuidCount: nodes.filter((node) => !node.guid?.trim()).length,
    };
  }

  private collectPreviewAuditSnapshot(
    nodeMap: Map<string, SystemShellGraphNode>,
  ): XRayPreviewAuditSnapshot {
    return this.withPreviewAuditRootExposed((auditRoot) => {
      const artifacts = Array.from(auditRoot.querySelectorAll<HTMLElement>('*'))
        .filter((element) => this.isVisiblePreviewArtifact(element))
        .map((element) => {
          const sourceObjectId = this.sourceObjectIdFromElement(element);
          const guid = this.guidFromElement(element);
          const ownerCode = this.resolveRawHtmlArtifactOwnerCode(element, nodeMap);
          const ownerObjectId = ownerCode ? nodeMap.get(ownerCode)?.id?.trim() ?? null : null;
          return {
            tag: element.tagName.toLowerCase(),
            description: this.describeRawHtmlArtifact(element),
            textSnippet: this.rawHtmlArtifactTextSnippet(element),
            sourceObjectId,
            guid,
            ownerObjectId,
            ownerCode,
            isPrimeArtifact: this.isPrimePreviewArtifact(element),
            isSelectable: !!sourceObjectId || !!guid,
          } satisfies XRayPreviewArtifactSnapshot;
        });

      const tagCounts = artifacts.reduce((counts, artifact) => {
        counts.set(artifact.tag, (counts.get(artifact.tag) ?? 0) + 1);
        return counts;
      }, new Map<string, number>());

      const previewSummaryRows: XRayCountRow[] = [
        { label: 'Total Visible Artifacts', value: artifacts.length },
        { label: 'Native HTML Artifacts', value: artifacts.filter((artifact) => HTML_NATIVE_TAGS.has(artifact.tag)).length },
        { label: 'PrimeNG Artifacts', value: artifacts.filter((artifact) => artifact.isPrimeArtifact).length },
        { label: 'Bound Artifacts', value: artifacts.filter((artifact) => artifact.sourceObjectId && artifact.guid).length },
      ];

      return {
        artifacts,
        previewSummaryRows,
        previewTagRows: this.toSortedCountRows(tagCounts),
        missingPreviewObjectIdCount: artifacts.filter((artifact) => !artifact.sourceObjectId).length,
        missingPreviewGuidCount: artifacts.filter((artifact) => !artifact.guid).length,
        unmodeledPreviewArtifactCount: artifacts.filter((artifact) => !artifact.sourceObjectId || !artifact.guid).length,
        unselectablePreviewArtifactCount: artifacts.filter((artifact) => !artifact.isSelectable).length,
      };
    }) ?? {
      artifacts: [],
      previewSummaryRows: [
        { label: 'Total Visible Artifacts', value: 0 },
        { label: 'Native HTML Artifacts', value: 0 },
        { label: 'PrimeNG Artifacts', value: 0 },
        { label: 'Bound Artifacts', value: 0 },
      ],
      previewTagRows: [],
      missingPreviewObjectIdCount: 0,
      missingPreviewGuidCount: 0,
      unmodeledPreviewArtifactCount: 0,
      unselectablePreviewArtifactCount: 0,
    };
  }

  private toSortedCountRows(counts: Map<string, number>): XRayCountRow[] {
    return Array.from(counts.entries())
      .sort((left, right) => right[1] - left[1] || left[0].localeCompare(right[0]))
      .map(([label, value]) => ({ label, value }));
  }

  private activeSelectionObjectId(): string | null {
    if (this.activeWorkspace() === 'frontend') {
      return this.state.selectedTreeNode()?.data?.objectId ?? null;
    }

    return null;
  }

  private mapPersistedIssueNode(
    issue: SystemShellGraphNode,
    serialIndex: number,
    nodeMap: Map<string, SystemShellGraphNode>,
  ): SystemShellGraphIssueNode {
    const targetNode = this.issueTargetNode(issue);
    const targetObjectId = targetNode?.id?.trim() ?? null;
    const targetName = targetNode ? this.displayNodeName(targetNode) : issue.name;
    return {
      code: issue.code,
      serialNumber: String(serialIndex).padStart(3, '0'),
      name: issue.name,
      family: 'Issue',
      objectType: 'DesignIssue',
      domain: issue.domain ?? 'frontend',
      layer: 'instance',
      description: issue.description ?? issue.name,
      hierarchyCode: issue.hierarchyCode ?? issue.code,
      id: issue.id ?? issue.code,
      status: issue.status === 'closed' ? 'closed' : 'open',
      severity: (issue.issueSeverity ?? 'error') as 'error',
      source: 'X-Ray Agent',
      category: (issue.issueCategory ?? 'Structure') as SystemShellGraphIssueNode['category'],
      rule: issue.issueRule ?? 'Design Rule',
      targetObjectId,
      targetName,
      message: issue.description ?? issue.name,
      prompt: issue.issuePrompt ?? this.defaultIssuePrompt(
        targetName,
        issue.issueRule ?? 'Design Rule',
        issue.description ?? issue.name,
      ),
    };
  }

  private defaultIssuePrompt(targetName: string, rule: string, message: string): string {
    const fixGuidance = rule === 'Plain Container'
      ? [
        `- Reset "${targetName}" back to a plain structural container.`,
        '- Remove decorative styling from the container itself: background, border, shadow, and color overrides.',
        '- If the visual surface is intentional, remodel it as an explicit valid artifact instead of leaving it on the container.',
        '- Run X-Ray Agent again to confirm the container is plain.',
      ]
      : [
        `- Review "${targetName}" against the rule "${rule}".`,
        '- Update the design, structure, styling, or accessibility implementation so the violation no longer appears.',
        '- Run X-Ray Agent again to confirm the issue is resolved.',
      ];

    return [
      'Issue',
      message,
      '',
      'Fix Guidance',
      ...fixGuidance,
    ].join('\n');
  }

  private describeInspectorIssueMessage(message: string): Pick<SystemShellGraphIssueNode, 'name' | 'source' | 'category' | 'rule'> {
    if (message.startsWith('WCAG ')) {
      const [, afterPrefix = message] = message.split(':', 2);
      const rule = afterPrefix.split('.')[0]?.trim() || 'WCAG AAA';
      return {
        name: 'Accessibility Issue',
        source: 'X-Ray Agent',
        category: 'Accessibility',
        rule,
      };
    }

    if (message.startsWith('Section container must remain plain and transparent')) {
      return {
        name: 'Container Styling Issue',
        source: 'X-Ray Agent',
        category: 'Container Styling',
        rule: 'Plain Container',
      };
    }

    if (message.startsWith('Rendered preview content exceeds the viewport height')) {
      return {
        name: 'Viewport Navigation Issue',
        source: 'X-Ray Agent',
        category: 'Viewport Navigation',
        rule: 'Vertical Scroll Path',
      };
    }

    if (message.startsWith('Rendered element uses plain HTML for a component-like UI artifact')) {
      const [, afterPrefix = message] = message.split(':', 2);
      return {
        name: 'HTML Element Violation',
        source: 'X-Ray Agent',
        category: 'HTML Element Violation',
        rule: afterPrefix.split('.')[0]?.trim() || 'Registry-backed UI artifact',
      };
    }

    if (message.startsWith('Rendered element is implemented as plain HTML')) {
      return {
        name: 'HTML Element Violation',
        source: 'X-Ray Agent',
        category: 'HTML Element Violation',
        rule: 'Plain HTML Element',
      };
    }

    if (message.startsWith('Rendered non-container HTML artifact')) {
      return {
        name: 'HTML Element Violation',
        source: 'X-Ray Agent',
        category: 'HTML Element Violation',
        rule: 'Plain HTML Artifact',
      };
    }

    if (message.startsWith('Graph object is missing required id')) {
      return {
        name: 'Structural Issue',
        source: 'X-Ray Agent',
        category: 'Structure',
        rule: 'Missing Object ID',
      };
    }

    if (message.startsWith('Graph object is missing required guid')) {
      return {
        name: 'Structural Issue',
        source: 'X-Ray Agent',
        category: 'Structure',
        rule: 'Missing Object GUID',
      };
    }

    if (message.includes('is missing source-object-id')) {
      return {
        name: 'Preview-Tree Parity Issue',
        source: 'X-Ray Agent',
        category: 'Preview-Tree Parity',
        rule: 'Missing Preview Object ID',
      };
    }

    if (message.includes('is missing guid')) {
      return {
        name: 'Preview-Tree Parity Issue',
        source: 'X-Ray Agent',
        category: 'Preview-Tree Parity',
        rule: 'Missing Preview GUID',
      };
    }

    if (message.startsWith('Visible preview artifact')) {
      if (message.includes('cannot be independently identified by mouse hover or mouse click')) {
        return {
          name: 'Preview-Tree Parity Issue',
          source: 'X-Ray Agent',
          category: 'Preview-Tree Parity',
          rule: 'Unselectable Preview Artifact',
        };
      }
      return {
        name: 'Preview-Tree Parity Issue',
        source: 'X-Ray Agent',
        category: 'Preview-Tree Parity',
        rule: 'Unmodeled Preview Artifact',
      };
    }

    return {
      name: 'Structural Issue',
      source: 'X-Ray Agent',
      category: 'Structure',
      rule: message.split('.')[0]?.trim() || 'Structural Rule',
    };
  }

  private collectStructuralScopeObjectIds(rootObjectId: string): Set<string> {
    const graph = this.state.graph();
    const nodeMap = this.state.nodeIdMap();
    if (!graph || !nodeMap.has(rootObjectId)) {
      return new Set<string>(rootObjectId ? [rootObjectId] : []);
    }

    const outgoing = this.buildRelationshipMap(
      graph.relationships.filter((relationship) =>
        ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_ELEMENT', 'HAS_COMPONENT'].includes(relationship.relationshipType),
      ),
      'fromId',
    );
    const visited = new Set<string>([rootObjectId]);
    const queue = [rootObjectId];

    while (queue.length) {
      const currentObjectId = queue.shift()!;
      for (const relationship of outgoing.get(currentObjectId) ?? []) {
        if (visited.has(relationship.toId) || !nodeMap.has(relationship.toId)) {
          continue;
        }
        visited.add(relationship.toId);
        queue.push(relationship.toId);
      }
    }

    return visited;
  }

  private collectStructuralAncestorObjectIds(objectId: string): string[] {
    const graph = this.state.graph();
    const nodeMap = this.state.nodeIdMap();
    if (!graph || !nodeMap.has(objectId)) {
      return objectId ? [objectId] : [];
    }

    const incoming = this.buildRelationshipMap(
      graph.relationships.filter((relationship) =>
        ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_ELEMENT', 'HAS_COMPONENT'].includes(relationship.relationshipType),
      ),
      'toId',
    );
    const visited = new Set<string>();
    const queue = [objectId];

    while (queue.length) {
      const currentObjectId = queue.shift()!;
      if (visited.has(currentObjectId) || !nodeMap.has(currentObjectId)) {
        continue;
      }
      visited.add(currentObjectId);
      for (const relationship of incoming.get(currentObjectId) ?? []) {
        if (!visited.has(relationship.fromId)) {
          queue.push(relationship.fromId);
        }
      }
    }

    return Array.from(visited);
  }

  private collectXRayScanIssues() {
    const graph = this.state.graph();
    if (!graph) {
      return [];
    }

    const nodeMap = this.state.nodeMap();
    const deduped = new Map<string, {
      targetObjectId: string;
      targetName: string;
      source: string;
      category: string;
      rule: string;
      message: string;
      severity: string;
    }>();

    const addIssue = (
      targetObjectId: string | null | undefined,
      message: string,
    ): void => {
      const normalizedTargetObjectId = targetObjectId?.trim();
      if (!normalizedTargetObjectId) {
        return;
      }

      const targetNode = this.state.nodeIdMap().get(normalizedTargetObjectId);
      if (!targetNode) {
        return;
      }

      const metadata = this.describeInspectorIssueMessage(message);
      const key = [
        normalizedTargetObjectId,
        metadata.category,
        metadata.rule,
        message,
      ].join('|');

      deduped.set(key, {
        targetObjectId: normalizedTargetObjectId,
        targetName: this.displayNodeName(targetNode),
        source: metadata.source,
        category: metadata.category,
        rule: metadata.rule,
        message,
        severity: 'error',
      });
    };

    for (const [code, messages] of this.buildStructuralInspectionViolations()) {
      const targetNode = nodeMap.get(code);
      if (!targetNode?.id) {
        continue;
      }
      for (const message of messages) {
        addIssue(targetNode.id, message);
      }
    }

    this.withPreviewAuditRootExposed((auditRoot) => {
      const resolver = new PreviewDomResolver(
        auditRoot,
        nodeMap,
        this.state.nodeIdMap(),
        this.state.nodeGuidMap(),
        this.componentTargetIdMap(),
      );
      const screenNodes = graph.nodes.filter((node) => node.layer === 'instance' && node.family === 'Screen');

      for (const screenNode of screenNodes) {
        const screenElement = resolver.findElementForObjectId(screenNode.id);
        if (!screenElement) {
          continue;
        }

        const shellId = this.resolveAssociatedShellId(screenNode.id);
        const shellElement = shellId ? resolver.findElementForObjectId(shellId) : null;
        const viewportRoot = shellElement ?? screenElement;

        if (this.elementExceedsViewportHeight(viewportRoot) && !this.hasMeaningfulVerticalScrollPath(viewportRoot)) {
          addIssue(
            screenNode.id,
            'Rendered preview content exceeds the viewport height, but no vertical page navigation/scroll path is available; lower content is clipped.',
          );
        }

        for (const issue of auditPreviewAccessibility(
          screenElement,
          this.accessibilityTargetLevel,
          (element) => this.sourceObjectIdFromElement(element),
        )) {
          addIssue(
            issue.graphCode ?? screenNode.id,
            `WCAG ${issue.level}: ${issue.rule}. ${issue.message}`,
          );
        }
      }
    });

    return Array.from(deduped.values());
  }

  private buildStructuralInspectionViolations(): Map<string, string[]> {
    const graph = this.state.graph();
    if (!graph) {
      return new Map<string, string[]>();
    }

    const relevantFamilies = new Set(['Application', 'Shell', 'Screen', 'Section', 'Element', 'Component', 'ValidationRuleSet', 'ValidationRule', 'ViewportProfile']);
    const nodeMap = new Map(graph.nodes.map((node) => [node.code, node]));
    const nodeById = new Map(
      graph.nodes
        .filter((node) => !!node.id)
        .map((node) => [node.id as string, node]),
    );
    const structuralRelationships = graph.relationships.filter((relationship) =>
      ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_ELEMENT', 'HAS_COMPONENT'].includes(relationship.relationshipType),
    );
    const ruleSetRelationships = graph.relationships.filter((relationship) => relationship.relationshipType === 'USES_RULE_SET');
    const ruleRelationships = graph.relationships.filter((relationship) => relationship.relationshipType === 'HAS_RULE');
    const targetRelationships = graph.relationships.filter((relationship) => relationship.relationshipType === 'TARGETS');
    const outgoing = this.buildRelationshipMap(structuralRelationships, 'fromId');
    const incoming = this.buildRelationshipMap(structuralRelationships, 'toId');
    const ruleSetOutgoing = this.buildRelationshipMap(ruleSetRelationships, 'fromId');
    const ruleSetIncoming = this.buildRelationshipMap(ruleSetRelationships, 'toId');
    const ruleOutgoing = this.buildRelationshipMap(ruleRelationships, 'fromId');
    const ruleIncoming = this.buildRelationshipMap(ruleRelationships, 'toId');
    const targetOutgoing = this.buildRelationshipMap(targetRelationships, 'fromId');
    const violations = new Map<string, string[]>();

    const addViolation = (code: string, message: string): void => {
      const current = violations.get(code) ?? [];
      if (!current.includes(message)) {
        current.push(message);
        violations.set(code, current);
      }
    };

    const blank = (value: string | null | undefined): boolean => !value || !value.trim();
    const ensureExactParent = (
      node: SystemShellGraphNode,
      parent: SystemShellGraphNode | undefined,
      segment: string,
      message: string,
    ): void => {
      if (!parent) {
        return;
      }

      if (node.code !== `${parent.code}.${segment}` && !node.code.startsWith(`${parent.code}.${segment}`)) {
        addViolation(node.code, message);
      }
    };

    const validateSharedAttributes = (node: SystemShellGraphNode): void => {
      for (const [key, value] of [
        ['name', node.name],
        ['description', node.description],
        ['id', node.id],
        ['status', node.status],
        ['domain', node.domain],
        ['hierarchyCode', node.hierarchyCode],
      ] as const) {
        if (value === null || value === undefined || (typeof value === 'string' && !value.trim())) {
          addViolation(node.code, `${node.family} is missing required attribute: ${key}.`);
        }
      }
      if (node.family === 'Screen' && !blank(node.backgroundType)) {
        addViolation(node.code, 'Screen must not own shell background attributes.');
      }
    };

    for (const node of graph.nodes) {
      if (node.layer !== 'instance' || !relevantFamilies.has(node.family)) {
        continue;
      }

      const nodeId = node.id ?? '';
      const children = (outgoing.get(nodeId) ?? [])
        .map((relationship) => nodeById.get(relationship.toId))
        .filter((child): child is SystemShellGraphNode => !!child);
      const parents = (incoming.get(nodeId) ?? [])
        .map((relationship) => nodeById.get(relationship.fromId))
        .filter((parent): parent is SystemShellGraphNode => !!parent);
      const usedRuleSets = (ruleSetOutgoing.get(nodeId) ?? [])
        .map((relationship) => nodeById.get(relationship.toId))
        .filter((child): child is SystemShellGraphNode => !!child);
      const owningScreens = (ruleSetIncoming.get(nodeId) ?? [])
        .map((relationship) => nodeById.get(relationship.fromId))
        .filter((parent): parent is SystemShellGraphNode => !!parent);
      const ownedRules = (ruleOutgoing.get(nodeId) ?? [])
        .map((relationship) => nodeById.get(relationship.toId))
        .filter((child): child is SystemShellGraphNode => !!child);
      const owningRuleSets = (ruleIncoming.get(nodeId) ?? [])
        .map((relationship) => nodeById.get(relationship.fromId))
        .filter((parent): parent is SystemShellGraphNode => !!parent);
      const targets = (targetOutgoing.get(nodeId) ?? [])
        .map((relationship) => nodeById.get(relationship.toId))
        .filter((child): child is SystemShellGraphNode => !!child);
      validateSharedAttributes(node);

      switch (node.family) {
        case 'Application': {
          if (!/^APP\d{2}$/.test(node.code)) {
            addViolation(node.code, 'Application code must follow APP##.');
          }
          if (parents.length > 0) {
            addViolation(node.code, 'Application must be the top structural object and cannot have a structural parent.');
          }
          if (children.length === 0) {
            addViolation(node.code, 'Application must contain at least one Shell.');
          }
          for (const child of children) {
            if (child.family !== 'Shell') {
              addViolation(node.code, `Application may contain Shell only; found ${child.family}.`);
            }
          }
          break;
        }
        case 'Shell': {
          if (!/^SHL\d{2}$/.test(node.code)) {
            addViolation(node.code, 'Shell code must follow SHL##.');
          }
          if (parents.length !== 1 || parents[0]?.family !== 'Application') {
            addViolation(node.code, 'Shell must have exactly one Application parent.');
          }
          if (children.length === 0) {
            addViolation(node.code, 'Shell must contain at least one Section.');
          }
          for (const child of children) {
            if (child.family !== 'Section') {
              addViolation(node.code, `Shell may contain Section only; found ${child.family}.`);
            }
          }
          break;
        }
        case 'Screen': {
          if (parents.length !== 1 || !['Shell', 'Section'].includes(parents[0]?.family ?? '')) {
            addViolation(node.code, 'Screen must have exactly one Shell or Section parent.');
          }
          if (children.length === 0) {
            addViolation(node.code, 'Screen should contain at least one Section.');
          }
          for (const child of children) {
            if (child.family !== 'Section') {
              addViolation(node.code, `Screen may contain Section only; found ${child.family}.`);
            }
          }
          if (this.screenHasConditionalDescendants(node.code, graph.nodes) && usedRuleSets.length !== 1) {
            addViolation(node.code, 'Screen with conditional UI nodes must use exactly one ValidationRuleSet.');
          }
          if (usedRuleSets.some((ruleSet) => ruleSet.family !== 'ValidationRuleSet')) {
            addViolation(node.code, 'Screen may use ValidationRuleSet only.');
          }
          break;
        }
        case 'Section': {
          if (parents.length !== 1 || !['Shell', 'Screen', 'Section'].includes(parents[0]?.family ?? '')) {
            addViolation(node.code, 'Section must have exactly one Shell, Screen, or Section parent.');
          }
          for (const child of children) {
            if (!['Screen', 'Section', 'Element'].includes(child.family)) {
              addViolation(node.code, `Section may contain Screen, Section, or Element only; found ${child.family}.`);
            }
          }
          const hasScreenChildren = children.some((child) => child.family === 'Screen');
          const hasSectionChildren = children.some((child) => child.family === 'Section');
          const hasElementChildren = children.some((child) => child.family === 'Element');
          const childModes = (hasScreenChildren ? 1 : 0) + (hasSectionChildren ? 1 : 0) + (hasElementChildren ? 1 : 0);
          if (childModes > 1) {
            addViolation(node.code, 'Section must not mix child screens, sections, and elements at the same level.');
          }
          if (!node.repeatable && children.length === 1 && children[0].family === 'Section') {
            addViolation(node.code, 'Section is an orphan single-child section and should be flattened.');
          }
          if (node.repeatable && !this.isMeaningfulRepeatableSection(node)) {
            addViolation(node.code, 'Repeatable Section must represent a meaningful repeated pattern, not a generic section.');
          }
          if (blank(node.sectionType)) {
            addViolation(node.code, 'Section is missing required attribute: section_type.');
          }
          if (!blank(node.semanticLevel)) {
            addViolation(node.code, 'Semantic heading levels belong to Element, not Section.');
          }
          if (node.repeatable === null) {
            addViolation(node.code, 'Section is missing required attribute: repeatable.');
          }
          if (blank(node.renderMode)) {
            addViolation(node.code, 'Section is missing required attribute: render_mode.');
          }
          if (blank(node.defaultState)) {
            addViolation(node.code, 'Section is missing required attribute: default_state.');
          }
          if (blank(node.controlSource)) {
            addViolation(node.code, 'Section is missing required attribute: control_source.');
          }
          if (node.renderMode === 'conditional' && node.controlSource !== 'validation_rule_set') {
            addViolation(node.code, 'Conditional Section must use control_source = validation_rule_set.');
          }
          if (node.renderMode === 'static' && node.controlSource !== 'none') {
            addViolation(node.code, 'Static Section must use control_source = none.');
          }
          break;
        }
        case 'Element': {
          if (parents.length !== 1 || parents[0]?.family !== 'Section') {
            addViolation(node.code, 'Element must have exactly one Section parent.');
          }
          for (const child of children) {
            if (child.family !== 'Component') {
              addViolation(node.code, `Element may contain Component instances only; found ${child.family}.`);
            }
          }
          if (blank(node.elementType)) {
            addViolation(node.code, 'Element is missing required attribute: element_type.');
          }
          if (node.elementType === 'title' && blank(node.semanticLevel)) {
            addViolation(node.code, 'Title Element should define semantic_level.');
          }
          if (blank(node.renderMode)) {
            addViolation(node.code, 'Element is missing required attribute: render_mode.');
          }
          if (blank(node.defaultState)) {
            addViolation(node.code, 'Element is missing required attribute: default_state.');
          }
          if (blank(node.controlSource)) {
            addViolation(node.code, 'Element is missing required attribute: control_source.');
          }
          if (node.renderMode === 'conditional' && node.controlSource !== 'validation_rule_set') {
            addViolation(node.code, 'Conditional Element must use control_source = validation_rule_set.');
          }
          if (node.renderMode === 'static' && node.controlSource !== 'none') {
            addViolation(node.code, 'Static Element must use control_source = none.');
          }
          break;
        }
        case 'Component': {
          if (parents.length !== 1 || parents[0]?.family !== 'Element') {
            addViolation(node.code, 'Component instance must be attached to exactly one Element parent.');
          }
          if (children.length > 0) {
            addViolation(node.code, 'Component instance must remain a leaf.');
          }
          if (blank(node.assetType)) {
            addViolation(node.code, 'Component instance is missing asset_type.');
          }
          if (blank(node.assetName)) {
            addViolation(node.code, 'Component instance is missing asset_name.');
          }
          const target = this.componentTargetNode(node);
          if (!target || target.family !== 'Element') {
            addViolation(node.code, 'Component instance must target an Element through HAS_COMPONENT.');
          } else if (parents[0] && target.id !== parents[0].id) {
            addViolation(node.code, 'Component HAS_COMPONENT target must match its parent Element.');
          }
          break;
        }
        case 'ValidationRuleSet': {
          if (owningScreens.length !== 1 || owningScreens[0]?.family !== 'Screen') {
            addViolation(node.code, 'ValidationRuleSet must be referenced by exactly one Screen.');
          }
          if (blank(node.ruleSetType)) {
            addViolation(node.code, 'ValidationRuleSet is missing required attribute: rule_set_type.');
          }
          if (blank(node.ruleSetScope)) {
            addViolation(node.code, 'ValidationRuleSet is missing required attribute: rule_set_scope.');
          }
          if (owningScreens.length === 1
            && this.screenHasConditionalDescendants(owningScreens[0].code, graph.nodes)
            && ownedRules.length === 0) {
            addViolation(node.code, 'ValidationRuleSet must contain at least one ValidationRule when its screen has conditional UI nodes.');
          }
          for (const rule of ownedRules) {
            if (rule.family !== 'ValidationRule') {
              addViolation(node.code, `ValidationRuleSet may contain ValidationRule only; found ${rule.family}.`);
            }
          }
          break;
        }
        case 'ValidationRule': {
          if (!/^SHL\d{2}\.SCN\d{2}\.VRS\d{2}\.R\d{2,3}$/.test(node.code)) {
            addViolation(node.code, 'ValidationRule code must extend its parent ValidationRuleSet code.');
          }
          if (owningRuleSets.length !== 1 || owningRuleSets[0]?.family !== 'ValidationRuleSet') {
            addViolation(node.code, 'ValidationRule must be owned by exactly one ValidationRuleSet.');
          } else if (!node.code.startsWith(`${owningRuleSets[0].code}.R`)) {
            addViolation(node.code, 'ValidationRule code must extend its parent ValidationRuleSet code.');
          }
          if (blank(node.conditionExpression)) {
            addViolation(node.code, 'ValidationRule is missing required attribute: condition_expression.');
          }
          if (blank(node.actionType)) {
            addViolation(node.code, 'ValidationRule is missing required attribute: action_type.');
          }
          if (node.priority === null || node.priority === undefined) {
            addViolation(node.code, 'ValidationRule is missing required attribute: priority.');
          }
          if (node.stopProcessing === null || node.stopProcessing === undefined) {
            addViolation(node.code, 'ValidationRule is missing required attribute: stop_processing.');
          }
          if (targets.length === 0) {
            addViolation(node.code, 'ValidationRule must target at least one Screen, Section, or Element.');
          }
          for (const target of targets) {
            if (!['Screen', 'Section', 'Element'].includes(target.family)) {
              addViolation(node.code, `ValidationRule may target Screen, Section, or Element only; found ${target.family}.`);
            }
          }
          break;
        }
        case 'ViewportProfile': {
          if (!/^VPR\d{2}$/.test(node.code)) {
            addViolation(node.code, 'ViewportProfile code must follow VPR##.');
          }
          if (!Number.isFinite(node.viewportWidth) || (node.viewportWidth ?? 0) <= 0) {
            addViolation(node.code, 'ViewportProfile must define a positive viewportWidth.');
          }
          if (!Number.isFinite(node.viewportHeight) || (node.viewportHeight ?? 0) <= 0) {
            addViolation(node.code, 'ViewportProfile must define a positive viewportHeight.');
          }
          if (blank(node.viewportCategory)) {
            addViolation(node.code, 'ViewportProfile is missing required attribute: viewport_category.');
          }
          break;
        }
      }
    }

    const renderedGraphCodes = this.collectRenderedPreviewGraphCodes();
    if (renderedGraphCodes.size > 0) {
      for (const code of renderedGraphCodes) {
        if (!nodeMap.has(code)) {
          addViolation(this.activeScreenCode(), `Preview audit host contains unmapped graph code ${code}.`);
        }
      }

      for (const node of graph.nodes) {
        if (node.layer !== 'instance' || !['Shell', 'Screen', 'Section', 'Element'].includes(node.family)) {
          continue;
        }
        if (!this.isNodeExpectedInPreviewAudit(node)) {
          continue;
        }
        if (node.renderMode === 'conditional' || node.defaultState === 'hidden') {
          continue;
        }
        if (this.hasConditionalHiddenAncestor(node, nodeMap)) {
          continue;
        }
        if (!renderedGraphCodes.has(node.code)) {
          addViolation(node.code, 'Rendered preview does not expose this object via source-object-id (UUID binding).');
        }
      }
    }

    for (const [code, messages] of this.collectSectionContainerPresentationViolations(nodeMap)) {
      for (const message of messages) {
        addViolation(code, message);
      }
    }

    for (const [code, messages] of this.collectAnonymousIntermediateWrapperViolations(nodeMap, outgoing)) {
      for (const message of messages) {
        addViolation(code, message);
      }
    }

    for (const [code, messages] of this.collectComponentParityViolations(nodeMap, outgoing)) {
      for (const message of messages) {
        addViolation(code, message);
      }
    }

    for (const [code, messages] of this.collectRawHtmlArtifactViolations(nodeMap)) {
      for (const message of messages) {
        addViolation(code, message);
      }
    }

    for (const [code, messages] of this.collectPreviewTreeParityViolations(nodeMap)) {
      for (const message of messages) {
        addViolation(code, message);
      }
    }

    return violations;
  }

  private buildRelationshipMap(
    relationships: SystemShellGraphRelationship[],
    direction: 'fromId' | 'toId',
  ): Map<string, SystemShellGraphRelationship[]> {
    const map = new Map<string, SystemShellGraphRelationship[]>();

    for (const relationship of relationships) {
      const key = relationship[direction];
      const existing = map.get(key) ?? [];
      existing.push(relationship);
      map.set(key, existing);
    }

    return map;
  }

  private screenHasConditionalDescendants(screenCode: string, nodes: SystemShellGraphNode[]): boolean {
    return nodes
      .filter((node) => node.layer === 'instance')
      .filter((node) => node.code.startsWith(`${screenCode}.`))
      .filter((node) => ['Section', 'Element'].includes(node.family))
      .some((node) => node.renderMode === 'conditional');
  }

  private collectRenderedPreviewGraphCodes(): Set<string> {
    const auditRoot = this.previewAuditRootRef?.nativeElement;
    if (!auditRoot) {
      return new Set<string>();
    }

    return new Set(
      Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))
        .map((element) => this.sourceObjectCodeFromElement(element))
        .filter((code): code is string => !!code),
    );
  }

  private collectSectionContainerPresentationViolations(
    nodeMap: Map<string, SystemShellGraphNode>,
  ): Map<string, string[]> {
    const auditRoot = this.previewAuditRootRef?.nativeElement;
    if (!auditRoot) {
      return new Map<string, string[]>();
    }

    const violations = new Map<string, string[]>();

    for (const element of Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))) {
      const code = this.sourceObjectCodeFromElement(element);
      if (!code) {
        continue;
      }

      const node = nodeMap.get(code);
      if (!node || node.family !== 'Section' || node.layer !== 'instance') {
        continue;
      }

      const reasons = this.sectionContainerPresentationReasons(element);
      if (!reasons.length) {
        continue;
      }

      violations.set(
        code,
        [`Section container must remain plain and transparent in preview; found ${reasons.join(', ')}.`],
      );
    }

    return violations;
  }

  private collectAnonymousIntermediateWrapperViolations(
    nodeMap: Map<string, SystemShellGraphNode>,
    outgoing: Map<string, SystemShellGraphRelationship[]>,
  ): Map<string, string[]> {
    const auditRoot = this.previewAuditRootRef?.nativeElement;
    if (!auditRoot) {
      return new Map<string, string[]>();
    }

    const violations = new Map<string, string[]>();

    for (const ownerElement of Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))) {
      const ownerObjectId = this.sourceObjectIdFromElement(ownerElement);
      if (!ownerObjectId) {
        continue;
      }

      const ownerNode = this.state.nodeIdMap().get(ownerObjectId);
      if (!ownerNode || ownerNode.layer !== 'instance' || !['Section', 'Element'].includes(ownerNode.family)) {
        continue;
      }

      const directChildCodes = new Set(
        (outgoing.get(ownerObjectId) ?? [])
          .map((relationship) => this.state.nodeIdMap().get(relationship.toId))
          .filter((child): child is SystemShellGraphNode =>
            !!child
            && child.layer === 'instance'
            && ['Section', 'Element', 'Component'].includes(child.family),
          )
          .map((child) => child.code),
      );

      if (!directChildCodes.size) {
        continue;
      }

      const findings: string[] = [];

      for (const childElement of Array.from(ownerElement.children)) {
        if (!(childElement instanceof HTMLElement)) {
          continue;
        }

        if (this.sourceObjectCodeFromElement(childElement)) {
          continue;
        }

        const wrappedDirectChildCodes = Array.from(childElement.querySelectorAll<HTMLElement>('[source-object-id]'))
          .map((element) => this.sourceObjectCodeFromElement(element) ?? '')
          .filter((code) => directChildCodes.has(code));

        const uniqueWrappedCodes = Array.from(new Set(wrappedDirectChildCodes));
        if (!uniqueWrappedCodes.length) {
          continue;
        }

        const wrappedNames = uniqueWrappedCodes
          .map((code) => nodeMap.get(code))
          .filter((node): node is SystemShellGraphNode => !!node)
          .map((node) => this.displayNodeName(node));

        findings.push(
          `Anonymous HTML wrapper ${this.describeAnonymousWrapper(childElement)} wraps direct child artifacts ${wrappedNames.join(', ')}`,
        );
      }

      if (findings.length) {
        violations.set(
          ownerNode.code,
          findings.map((finding) =>
            `${finding}. ${ownerNode.family} must render its direct child Sections, Elements, or Components without an unmodeled intermediate wrapper.`,
          ),
        );
      }
    }

    return violations;
  }

  private collectComponentParityViolations(
    nodeMap: Map<string, SystemShellGraphNode>,
    outgoing: Map<string, SystemShellGraphRelationship[]>,
  ): Map<string, string[]> {
    const auditRoot = this.previewAuditRootRef?.nativeElement;
    if (!auditRoot) {
      return new Map<string, string[]>();
    }

    const violations = new Map<string, string[]>();

    for (const renderedElement of Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))) {
      const code = this.sourceObjectCodeFromElement(renderedElement);
      if (!code) {
        continue;
      }

      const node = nodeMap.get(code);
      if (!node || node.layer !== 'instance' || node.family !== 'Element') {
        continue;
      }

      const expectedComponent = this.expectedPrimeComponentForElement(node, renderedElement);
      const hasRenderedPrimeNg = this.hasRenderedPrimeNgEvidence(renderedElement);

      const componentChildren = (outgoing.get(node.id ?? '') ?? [])
        .map((relationship) => this.state.nodeIdMap().get(relationship.toId))
        .filter((child): child is SystemShellGraphNode => !!child && child.family === 'Component');

      if (!hasRenderedPrimeNg) {
        violations.set(code, [
          'Rendered element is implemented as plain HTML. Only Section may remain a plain HTML container; ' +
          'Element must render through a recorded PrimeNG component.',
        ]);
        continue;
      }

      if (!expectedComponent) {
        if (!componentChildren.length) {
          violations.set(code, [
            'Rendered element uses a PrimeNG-backed UI artifact, but no recorded Component occurrence exists for this Element.',
          ]);
        }
        continue;
      }

      if (!this.elementContainsComponentLikeEvidence(renderedElement, expectedComponent)) {
        continue;
      }

      const hasMatchingComponentOccurrence = componentChildren.some((child) =>
        this.matchesExpectedComponent(child, expectedComponent),
      );

      if (hasMatchingComponentOccurrence) {
        continue;
      }

      violations.set(code, [
        `Rendered element uses plain HTML for a component-like UI artifact: expected PrimeNG ${expectedComponent}. ` +
        `This Element must be backed by a recorded Component occurrence instead of HTML-only markup.`,
      ]);
    }

    return violations;
  }

  private collectRawHtmlArtifactViolations(
    nodeMap: Map<string, SystemShellGraphNode>,
  ): Map<string, string[]> {
    const auditRoot = this.previewAuditRootRef?.nativeElement;
    if (!auditRoot) {
      return new Map<string, string[]>();
    }

    const violations = new Map<string, string[]>();

    const addViolation = (code: string, message: string): void => {
      const existing = violations.get(code) ?? [];
      if (!existing.includes(message)) {
        existing.push(message);
        violations.set(code, existing);
      }
    };

    for (const element of Array.from(auditRoot.querySelectorAll<HTMLElement>('*'))) {
      if (!this.isRawHtmlArtifactViolationCandidate(element, nodeMap)) {
        continue;
      }

      const ownerCode = this.resolveRawHtmlArtifactOwnerCode(element, nodeMap);
      if (!ownerCode) {
        continue;
      }

      const artifactDescription = this.describeRawHtmlArtifact(element);
      const textSnippet = this.rawHtmlArtifactTextSnippet(element);
      const textClause = textSnippet ? ` with text "${textSnippet}"` : '';
      addViolation(
        ownerCode,
        `Rendered non-container HTML artifact ${artifactDescription}${textClause} is not allowed. ` +
          'Only container HTML tags may remain native in preview.',
      );
    }

    return violations;
  }

  private collectPreviewTreeParityViolations(
    nodeMap: Map<string, SystemShellGraphNode>,
  ): Map<string, string[]> {
    const auditRoot = this.previewAuditRootRef?.nativeElement;
    if (!auditRoot) {
      return new Map<string, string[]>();
    }

    const violations = new Map<string, string[]>();

    const addViolation = (code: string, message: string): void => {
      const existing = violations.get(code) ?? [];
      if (!existing.includes(message)) {
        existing.push(message);
        violations.set(code, existing);
      }
    };

    for (const element of Array.from(auditRoot.querySelectorAll<HTMLElement>('*'))) {
      if (!this.isPreviewTreeParityViolationCandidate(element, nodeMap)) {
        continue;
      }

      const ownerCode = this.resolveRawHtmlArtifactOwnerCode(element, nodeMap);
      if (!ownerCode) {
        continue;
      }

      const artifactDescription = this.describeRawHtmlArtifact(element);
      const textSnippet = this.rawHtmlArtifactTextSnippet(element);
      const textClause = textSnippet ? ` with text "${textSnippet}"` : '';
      addViolation(
        ownerCode,
        `Visible preview artifact ${artifactDescription}${textClause} is not represented as a Neo4j/tree object. ` +
          'Every visible preview artifact must be modeled and listed in the tree.',
      );
    }

    return violations;
  }

  private collectPreviewViewportNavigationViolations(
    nodeMap: Map<string, SystemShellGraphNode>,
  ): Map<string, string[]> {
    const stage = this.previewStageRef?.nativeElement;
    if (!stage) {
      return new Map<string, string[]>();
    }

    const screenCode = this.activeScreenCode();
    const shellCode = this.activeShellCode();
    const resolver = this.previewDomResolver();
    const shellElement = resolver.findElementForObjectId(shellCode ? this.state.nodeMap().get(shellCode)?.id ?? null : null);
    const screenElement = resolver.findElementForObjectId(screenCode ? this.state.nodeMap().get(screenCode)?.id ?? null : null);
    const viewportRoot = shellElement ?? screenElement;
    if (!viewportRoot) {
      return new Map<string, string[]>();
    }

    if (!this.elementExceedsViewportHeight(viewportRoot)) {
      return new Map<string, string[]>();
    }

    if (this.hasMeaningfulVerticalScrollPath(viewportRoot)) {
      return new Map<string, string[]>();
    }

    const message =
      'Rendered preview content exceeds the viewport height, but no vertical page navigation/scroll path is available; lower content is clipped.';
    const violations = new Map<string, string[]>();
    const add = (code: string | null | undefined): void => {
      if (!code || !nodeMap.has(code)) {
        return;
      }
      violations.set(code, [message]);
    };

    add(screenCode);
    add(this.sourceObjectCodeFromElement(viewportRoot));

    return violations;
  }

  private withPreviewAuditRootExposed<T>(callback: (auditRoot: HTMLElement) => T): T | null {
    const auditRoot = this.previewAuditRootRef?.nativeElement;
    if (!auditRoot) {
      return null;
    }

    const previousHidden = auditRoot.hidden;
    const previousInert = auditRoot.inert;
    const previousStyle = auditRoot.getAttribute('style');

    auditRoot.hidden = false;
    auditRoot.inert = false;
    auditRoot.style.position = 'fixed';
    auditRoot.style.left = '-20000px';
    auditRoot.style.top = '0';
    auditRoot.style.width = `${this.previewCanvasWidth()}px`;
    auditRoot.style.height = `${this.previewCanvasHeight()}px`;
    auditRoot.style.display = 'block';
    auditRoot.style.visibility = 'visible';
    auditRoot.style.opacity = '0';
    auditRoot.style.pointerEvents = 'none';
    auditRoot.style.overflow = 'hidden';

    try {
      return callback(auditRoot);
    } finally {
      auditRoot.hidden = previousHidden;
      auditRoot.inert = previousInert;
      if (previousStyle === null) {
        auditRoot.removeAttribute('style');
      } else {
        auditRoot.setAttribute('style', previousStyle);
      }
    }
  }

  private sectionContainerPresentationReasons(element: HTMLElement): string[] {
    const style = getComputedStyle(element);
    const reasons: string[] = [];
    const parentStyle = element.parentElement instanceof HTMLElement ? getComputedStyle(element.parentElement) : null;
    const hasBackgroundColor = !['transparent', 'rgba(0, 0, 0, 0)'].includes(style.backgroundColor);
    const hasBackgroundImage = style.backgroundImage !== 'none';
    const hasBorder = ['Top', 'Right', 'Bottom', 'Left'].some((side) => {
      const width = Number.parseFloat(style.getPropertyValue(`border-${side.toLowerCase()}-width`));
      const borderStyle = style.getPropertyValue(`border-${side.toLowerCase()}-style`);
      return width > 0 && borderStyle !== 'none';
    });
    const hasShadow = style.boxShadow !== 'none';
    const hasColorOverride = !!parentStyle && style.color !== parentStyle.color;

    if (hasBackgroundColor || hasBackgroundImage) {
      reasons.push('background styling');
    }
    if (hasBorder) {
      reasons.push('border styling');
    }
    if (hasShadow) {
      reasons.push('shadow styling');
    }
    if (hasColorOverride) {
      reasons.push('color styling');
    }

    return reasons;
  }

  private describeAnonymousWrapper(element: HTMLElement): string {
    const tag = element.tagName.toLowerCase();
    const classes = Array.from(element.classList)
      .map((className) => className.trim())
      .filter((className) => !!className)
      .slice(0, 3);

    return classes.length ? `<${tag} class="${classes.join(' ')}">` : `<${tag}>`;
  }

  private sourceObjectIdFromCode(code: string | null | undefined): string | null {
    const normalizedCode = code?.trim();
    if (!normalizedCode) {
      return null;
    }

    return this.state.nodeMap().get(normalizedCode)?.id?.trim() ?? null;
  }

  private guidFromCode(code: string | null | undefined): string | null {
    const normalizedCode = code?.trim();
    if (!normalizedCode) {
      return null;
    }

    return this.state.nodeMap().get(normalizedCode)?.guid?.trim() ?? null;
  }

  private sourceObjectIdFromElement(element: HTMLElement | null): string | null {
    const sourceObjectId = this.boundPreviewElement(element)?.getAttribute('source-object-id')?.trim();
    return sourceObjectId || null;
  }

  private guidFromElement(element: HTMLElement | null): string | null {
    const guid = this.boundPreviewElement(element)?.getAttribute('guid')?.trim();
    return guid || null;
  }

  private sourceObjectCodeFromElement(element: HTMLElement | null): string | null {
    const sourceObjectId = this.sourceObjectIdFromElement(element);
    if (!sourceObjectId) {
      return null;
    }

    return this.state.nodeIdMap().get(sourceObjectId)?.code ?? null;
  }

  private boundPreviewElement(element: HTMLElement | null): HTMLElement | null {
    return element?.closest<HTMLElement>('[source-object-id], [guid]') ?? null;
  }

  private isRawHtmlArtifactViolationCandidate(
    element: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
  ): boolean {
    const tag = element.tagName.toLowerCase();
    if (!HTML_NATIVE_TAGS.has(tag) || HTML_CONTAINER_TAGS.has(tag)) {
      return false;
    }

    if (element.hasAttribute('data-pc-section') || element.hasAttribute('data-pc-name')) {
      return false;
    }

    if (Array.from(element.classList).some((className) => className.startsWith('p-'))) {
      return false;
    }

    const directCode = this.sourceObjectCodeFromElement(element);
    if (directCode) {
      const directNode = nodeMap.get(directCode);
      if (directNode?.layer === 'instance' && directNode.family === 'Element') {
        return false;
      }
    }

    const style = getComputedStyle(element);
    if (style.display === 'none' || style.visibility === 'hidden') {
      return false;
    }

    return true;
  }

  private isVisiblePreviewArtifact(element: HTMLElement): boolean {
    const tag = element.tagName.toLowerCase();
    if (['script', 'style', 'link', 'meta', 'ng-template'].includes(tag)) {
      return false;
    }

    const style = getComputedStyle(element);
    if (style.display === 'none' || style.visibility === 'hidden' || style.visibility === 'collapse') {
      return false;
    }

    return true;
  }

  private isPrimePreviewArtifact(element: HTMLElement): boolean {
    const tag = element.tagName.toLowerCase();
    if (tag.startsWith('p-')) {
      return true;
    }

    if (element.hasAttribute('data-pc-name') || element.hasAttribute('data-pc-section')) {
      return true;
    }

    return Array.from(element.classList).some((className) => className.startsWith('p-'));
  }

  private isPreviewTreeParityViolationCandidate(
    element: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
  ): boolean {
    if (!this.isRawHtmlArtifactViolationCandidate(element, nodeMap)) {
      return false;
    }

    const directCode = this.sourceObjectCodeFromElement(element);
    if (directCode && nodeMap.has(directCode)) {
      return false;
    }

    return true;
  }

  private resolveRawHtmlArtifactOwnerCode(
    element: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
  ): string | null {
    const directCode = this.sourceObjectCodeFromElement(element);
    if (directCode && nodeMap.has(directCode)) {
      return directCode;
    }

    const ancestor = element.parentElement?.closest<HTMLElement>('[source-object-id]');
    const ancestorCode = ancestor ? this.sourceObjectCodeFromElement(ancestor) : null;
    if (ancestorCode && nodeMap.has(ancestorCode)) {
      return ancestorCode;
    }

    const screenCode = this.activeScreenCode();
    if (screenCode && nodeMap.has(screenCode)) {
      return screenCode;
    }

    const shellCode = this.activeShellCode();
    if (shellCode && nodeMap.has(shellCode)) {
      return shellCode;
    }

    return null;
  }

  private describeRawHtmlArtifact(element: HTMLElement): string {
    const tag = element.tagName.toLowerCase();
    const classes = Array.from(element.classList)
      .map((className) => className.trim())
      .filter((className) => !!className)
      .slice(0, 3);

    return classes.length ? `<${tag} class="${classes.join(' ')}">` : `<${tag}>`;
  }

  private rawHtmlArtifactTextSnippet(element: HTMLElement): string | null {
    const normalized = (element.textContent ?? '')
      .replace(/\s+/g, ' ')
      .trim();
    if (!normalized) {
      return null;
    }

    return normalized.length > 80 ? `${normalized.slice(0, 77)}...` : normalized;
  }

  private expectedPrimeComponentForElement(
    node: SystemShellGraphNode,
    renderedElement: HTMLElement,
  ): string | null {
    if (node.primeComponent?.trim()) {
      return node.primeComponent.trim();
    }

    const normalizedName = node.name.trim().toLowerCase();
    const normalizedClasses = Array.from(renderedElement.classList).join(' ').toLowerCase();

    if (normalizedName.includes('chip') || normalizedClasses.includes('chip')) {
      return 'Chip';
    }
    if (normalizedName.includes('badge') || normalizedClasses.includes('badge')) {
      return 'Tag';
    }
    if (normalizedName.includes('tabs')) {
      return 'Tabs';
    }
    if (normalizedName.includes('breadcrumb')) {
      return 'Breadcrumb';
    }
    if (normalizedName.includes('paginator')) {
      return 'Paginator';
    }
    if (normalizedName.includes('table')) {
      return 'Table';
    }
    if (normalizedName.includes('action') || node.elementType === 'button') {
      return 'Button';
    }
    if (normalizedName.includes('toggle') || node.elementType === 'toggle') {
      return 'ToggleButton';
    }
    if (normalizedName.includes('input') || normalizedName.includes('search') || node.elementType === 'input') {
      return 'InputText';
    }
    if (normalizedName.includes('select') || normalizedName.includes('dropdown')) {
      return 'Select';
    }

    return null;
  }

  private elementContainsComponentLikeEvidence(
    renderedElement: HTMLElement,
    expectedComponent: string,
  ): boolean {
    const matchesOrContains = (selector: string): boolean =>
      renderedElement.matches(selector) || !!renderedElement.querySelector(selector);

    switch (expectedComponent) {
      case 'Chip':
        return matchesOrContains('p-chip, .kpi-chip, .p-chip, [class*="chip"]');
      case 'Tag':
        return matchesOrContains('p-tag, .status-badge, .p-tag, [class*="badge"]');
      case 'Button':
        return matchesOrContains('p-button, button, [role="button"], .p-button');
      case 'ToggleButton':
        return matchesOrContains('p-togglebutton, [aria-pressed], .p-togglebutton');
      case 'InputText':
        return matchesOrContains('input, textarea, .p-inputtext');
      case 'Select':
        return matchesOrContains('p-select, select, .p-select, .p-dropdown');
      case 'Tabs':
        return matchesOrContains('p-tabs, [role="tablist"], .p-tabs');
      case 'Table':
        return matchesOrContains('p-table, table, .p-datatable');
      case 'Paginator':
        return matchesOrContains('p-paginator, .p-paginator, [class*="paginator"]');
      case 'Breadcrumb':
        return matchesOrContains('p-breadcrumb, .p-breadcrumb, nav[aria-label*="breadcrumb" i]');
      default:
        return renderedElement.children.length > 0;
    }
  }

  private hasRenderedPrimeNgEvidence(renderedElement: HTMLElement): boolean {
    const selector = [
      'p-button',
      'p-tag',
      'p-breadcrumb',
      'p-tabs',
      'p-table',
      'p-paginator',
      'p-togglebutton',
      'p-select',
      'p-autocomplete',
      'p-checkbox',
      'p-radiobutton',
      'p-message',
      'p-chip',
      '.p-button',
      '.p-tag',
      '.p-breadcrumb',
      '.p-tabs',
      '.p-datatable',
      '.p-paginator',
      '.p-togglebutton',
      '.p-select',
      '.p-autocomplete',
      '.p-checkbox',
      '.p-radiobutton',
      '.p-message',
      '.p-chip',
    ].join(',');

    return renderedElement.matches(selector)
      || renderedElement.hasAttribute('data-pc-name')
      || !!renderedElement.querySelector(selector);
  }

  private matchesExpectedComponent(node: SystemShellGraphNode, expectedComponent: string): boolean {
    const normalizedExpected = expectedComponent.trim().toLowerCase();
    return (node.assetName?.trim().toLowerCase() ?? '') === normalizedExpected
      || (node.assetType?.trim().toLowerCase() ?? '') === normalizedExpected;
  }

  private elementExceedsViewportHeight(element: HTMLElement): boolean {
    return element.scrollHeight > element.clientHeight + 1;
  }

  private hasMeaningfulVerticalScrollPath(root: HTMLElement): boolean {
    if (this.isVerticallyScrollable(root)) {
      return true;
    }

    const minimumScrollableHeight = Math.max(120, root.clientHeight * 0.25);
    for (const element of Array.from(root.querySelectorAll<HTMLElement>('*'))) {
      if (!this.isVerticallyScrollable(element)) {
        continue;
      }
      if (element.clientHeight < minimumScrollableHeight) {
        continue;
      }

      return true;
    }

    return false;
  }

  private isVerticallyScrollable(element: HTMLElement): boolean {
    const style = getComputedStyle(element);
    return ['auto', 'scroll', 'overlay'].includes(style.overflowY) && element.scrollHeight > element.clientHeight + 1;
  }

  private isMeaningfulRepeatableSection(node: SystemShellGraphNode): boolean {
    const sectionType = node.sectionType?.trim().toLowerCase() ?? '';
    const name = node.name.trim().toLowerCase();
    if (!sectionType || sectionType === 'section') {
      return false;
    }

    return ['provider', 'list', 'item', 'card', 'row', 'record', 'tile', 'entry', 'result', 'tab', 'table', 'grid']
      .some((keyword) => sectionType.includes(keyword) || name.includes(keyword));
  }

  private isNodeExpectedInPreviewAudit(node: SystemShellGraphNode): boolean {
    if (!['Shell', 'Screen', 'Section', 'Element'].includes(node.family)) {
      return false;
    }

    return /^SHL\d{2}(\.SCN\d{2}(\.SEC\d{2}(\.SEC\d{2})*(\.ELT\d{2})?)?)?$/.test(node.code);
  }

  private hasConditionalHiddenAncestor(
    node: SystemShellGraphNode,
    nodeMap: Map<string, SystemShellGraphNode>,
  ): boolean {
    const segments = node.code.split('.');
    while (segments.length > 1) {
      segments.pop();
      const ancestor = nodeMap.get(segments.join('.'));
      if (!ancestor || ancestor.family !== 'Section') {
        continue;
      }

      if (ancestor.renderMode === 'conditional' || ancestor.defaultState === 'hidden') {
        return true;
      }
    }

    return false;
  }

  private attributesForNode(node: SystemShellGraphNode): AttributeRow[] {
    const definitions = this.attributeDefinitionsForNode(node);
    const rows = this.buildAttributeRows(node, definitions);

    if (node.family === 'Component') {
      return [
        ...rows,
        this.buildReadonlyAttributeRow(node.code, 'Target Object', this.componentTargetNode(node)?.code ?? null),
      ];
    }

    return rows;
  }

  private attributeDefinitionsForNode(node: SystemShellGraphNode): AttributeDefinition[] {
    switch (node.family) {
      case 'JourneyStep':
        return [
          { label: 'Step Order', attributeKey: 'stepOrder', valueType: 'number' },
          { label: 'Execution Method', attributeKey: 'executionMethod', valueType: 'string' },
        ];
      case 'BusinessRule':
        return [
          { label: 'Rule Scope', attributeKey: 'ruleScope', valueType: 'string' },
          { label: 'Condition Expression', attributeKey: 'conditionExpression', valueType: 'string' },
          { label: 'Execution Effect', attributeKey: 'executionEffect', valueType: 'string' },
        ];
      case 'Blocker':
        return [
          { label: 'Blocker Type', attributeKey: 'blockerType', valueType: 'string' },
          { label: 'Blocking Effect', attributeKey: 'blockingEffect', valueType: 'string' },
        ];
      case 'ValidationRuleSet':
        return [
          { label: 'Rule Set Type', attributeKey: 'ruleSetType', valueType: 'string' },
          { label: 'Rule Set Scope', attributeKey: 'ruleSetScope', valueType: 'string' },
        ];
      case 'ValidationRule':
        return [
          { label: 'Condition Expression', attributeKey: 'conditionExpression', valueType: 'string' },
          { label: 'Action Type', attributeKey: 'actionType', valueType: 'string' },
          { label: 'Action Value', attributeKey: 'actionValue', valueType: 'string' },
          { label: 'Priority', attributeKey: 'priority', valueType: 'number' },
          { label: 'Stop Processing', attributeKey: 'stopProcessing', valueType: 'boolean' },
        ];
      case 'Section':
        return [
          { label: 'Section Type', attributeKey: 'sectionType', valueType: 'string' },
          { label: 'Repeatable', attributeKey: 'repeatable', valueType: 'boolean' },
          { label: 'Render Mode', attributeKey: 'renderMode', valueType: 'string' },
          { label: 'Default State', attributeKey: 'defaultState', valueType: 'string' },
          { label: 'Control Source', attributeKey: 'controlSource', valueType: 'string' },
        ];
      case 'Element':
        return [
          { label: 'Element Type', attributeKey: 'elementType', valueType: 'string' },
          { label: 'Semantic Level', attributeKey: 'semanticLevel', valueType: 'string' },
          { label: 'Render Mode', attributeKey: 'renderMode', valueType: 'string' },
          { label: 'Default State', attributeKey: 'defaultState', valueType: 'string' },
          { label: 'Control Source', attributeKey: 'controlSource', valueType: 'string' },
          { label: 'PrimeNG Source', attributeKey: 'primeComponent', valueType: 'string' },
          { label: 'Token Families', attributeKey: 'tokenFamilies', valueType: 'array' },
        ];
      case 'Component':
        return [
          { label: 'Asset Name', attributeKey: 'assetName', valueType: 'string' },
          { label: 'Asset Type', attributeKey: 'assetType', valueType: 'string' },
        ];
      case 'Shell':
        return [
          { label: 'Background Type', attributeKey: 'backgroundType', valueType: 'string' },
          { label: 'Background Color Style', attributeKey: 'backgroundColorStyle', valueType: 'string' },
          { label: 'Background Pattern', attributeKey: 'backgroundPatternKey', valueType: 'string' },
          { label: 'Background Pattern Opacity', attributeKey: 'backgroundPatternOpacity', valueType: 'number' },
          { label: 'Background Image Path', attributeKey: 'backgroundImagePath', valueType: 'string' },
        ];
      case 'Screen':
        return [];
      case 'ViewportProfile':
        return [
          { label: 'Viewport Width', attributeKey: 'viewportWidth', valueType: 'number' },
          { label: 'Viewport Height', attributeKey: 'viewportHeight', valueType: 'number' },
          { label: 'Viewport Category', attributeKey: 'viewportCategory', valueType: 'string' },
        ];
      default:
        return [];
    }
  }

  private remainingAttributeRowsForNode(node: SystemShellGraphNode): AttributeRow[] {
    const specificAttributeKeys = new Set<keyof SystemShellGraphNode>(
      this.attributeDefinitionsForNode(node).map((definition) => definition.attributeKey),
    );

    return this.buildAttributeRows(
      node,
      REMAINING_ATTRIBUTE_DEFINITIONS.filter((definition) =>
        !COMMON_FACT_SHEET_ATTRIBUTE_KEYS.has(definition.attributeKey)
        && !specificAttributeKeys.has(definition.attributeKey),
      ),
    ).filter((row) => row.value !== 'n/a');
  }

  private familyMark(family: string): string {
    const marks: Record<string, string> = {
      Application: 'APP',
      Persona: 'PE',
      Journey: 'JY',
      JourneyStep: 'JS',
      BusinessRule: 'BR',
      Blocker: 'BL',
      Shell: 'SHL',
      Screen: 'SCN',
      Section: 'SEC',
      Element: 'ELT',
      Component: 'CP',
      ViewportProfile: 'VP',
      ValidationRuleSet: 'RS',
      ValidationRule: 'VR',
    };

    return marks[family] ?? family.slice(0, 2).toUpperCase();
  }

  private displayNodeName(node: SystemShellGraphNode): string {
    const normalized = node.name?.trim() ?? '';
    const codePrefix = `${node.code} `;
    if (normalized.startsWith(codePrefix)) {
      return normalized.slice(codePrefix.length).trim();
    }
    return normalized || node.code;
  }

  private resolveAssociatedScreenCode(code: string): string | null {
    const codeMap = this.state.nodeMap();
    const idMap = this.state.nodeIdMap();
    const startNode = codeMap.get(code) ?? idMap.get(code) ?? null;
    if (!startNode) {
      return null;
    }

    if (startNode.family === 'Screen') {
      return startNode.code;
    }

    const primaryScreenCode = this.primaryScreenCodeForNode(startNode);
    if (primaryScreenCode) {
      return primaryScreenCode;
    }

    const graph = this.state.graph();
    if (!graph) {
      return null;
    }

    const adjacency = new Map<string, Set<string>>();
    for (const relationship of graph.relationships) {
      const fromNode = idMap.get(relationship.fromId);
      const toNode = idMap.get(relationship.toId);
      if (!fromNode || !toNode || fromNode.layer !== 'instance' || toNode.layer !== 'instance') {
        continue;
      }

      (adjacency.get(relationship.fromId) ?? adjacency.set(relationship.fromId, new Set()).get(relationship.fromId)!).add(relationship.toId);
      (adjacency.get(relationship.toId) ?? adjacency.set(relationship.toId, new Set()).get(relationship.toId)!).add(relationship.fromId);
    }

    const startId = startNode.id;
    if (!startId) {
      return null;
    }

    const visited = new Set<string>([startId]);
    const queue = [startId];
    while (queue.length) {
      const currentObjectId = queue.shift()!;
      const currentNode = idMap.get(currentObjectId);
      if (currentNode?.family === 'Screen') {
        return currentNode.code;
      }

      for (const nextObjectId of adjacency.get(currentObjectId) ?? []) {
        if (visited.has(nextObjectId)) {
          continue;
        }
        visited.add(nextObjectId);
        queue.push(nextObjectId);
      }
    }

    return null;
  }

  private primaryScreenCodeForNode(startNode: SystemShellGraphNode): string | null {
    if (!startNode.id) {
      return null;
    }

    if (startNode.family === 'Shell') {
      const mainContainer = this.structuralChildrenOf(startNode.id, 'HAS_SECTION')
        .find((child) => child.family === 'Section' && child.name === 'Main Container');
      if (!mainContainer?.id) {
        return null;
      }

      const firstScreen = this.structuralChildrenOf(mainContainer.id, 'HAS_SCREEN')
        .find((child) => child.family === 'Screen');
      return firstScreen?.code ?? null;
    }

    if (startNode.family === 'Section' && startNode.name === 'Main Container') {
      const firstScreen = this.structuralChildrenOf(startNode.id, 'HAS_SCREEN')
        .find((child) => child.family === 'Screen');
      return firstScreen?.code ?? null;
    }

    return null;
  }

  private structuralChildrenOf(objectId: string, relationshipType: string): SystemShellGraphNode[] {
    const graph = this.state.graph();
    if (!graph) {
      return [];
    }

    const nodeIdMap = this.state.nodeIdMap();
    return graph.relationships
      .filter((relationship) => relationship.fromId === objectId && relationship.relationshipType === relationshipType)
      .map((relationship) => nodeIdMap.get(relationship.toId) ?? null)
      .filter((node): node is SystemShellGraphNode => !!node)
      .sort((left, right) => {
        const leftHierarchy = left.hierarchyCode ?? '';
        const rightHierarchy = right.hierarchyCode ?? '';
        if (leftHierarchy !== rightHierarchy) {
          return leftHierarchy.localeCompare(rightHierarchy);
        }

        const leftName = left.name ?? '';
        const rightName = right.name ?? '';
        if (leftName !== rightName) {
          return leftName.localeCompare(rightName);
        }

        return (left.id ?? '').localeCompare(right.id ?? '');
      });
  }

  private resolveAssociatedScreenId(objectId: string | null | undefined): string | null {
    const normalizedObjectId = objectId?.trim();
    if (!normalizedObjectId) {
      return null;
    }

    const code = this.resolveAssociatedScreenCode(normalizedObjectId);
    return code ? this.state.nodeMap().get(code)?.id ?? null : null;
  }

  private resolveAssociatedShellCode(code: string): string | null {
    const codeMap = this.state.nodeMap();
    const idMap = this.state.nodeIdMap();
    const startNode = codeMap.get(code) ?? idMap.get(code) ?? null;
    if (!startNode) {
      return null;
    }

    if (startNode.family === 'Shell') {
      return startNode.code;
    }

    const graph = this.state.graph();
    if (!graph) {
      return null;
    }

    const adjacency = new Map<string, Set<string>>();
    for (const relationship of graph.relationships) {
      const fromNode = idMap.get(relationship.fromId);
      const toNode = idMap.get(relationship.toId);
      if (!fromNode || !toNode || fromNode.layer !== 'instance' || toNode.layer !== 'instance') {
        continue;
      }

      (adjacency.get(relationship.fromId) ?? adjacency.set(relationship.fromId, new Set()).get(relationship.fromId)!).add(relationship.toId);
      (adjacency.get(relationship.toId) ?? adjacency.set(relationship.toId, new Set()).get(relationship.toId)!).add(relationship.fromId);
    }

    const startId = startNode.id;
    if (!startId) {
      return null;
    }

    const visited = new Set<string>([startId]);
    const queue = [startId];
    while (queue.length) {
      const currentObjectId = queue.shift()!;
      const currentNode = idMap.get(currentObjectId);
      if (currentNode?.family === 'Shell') {
        return currentNode.code;
      }

      for (const nextObjectId of adjacency.get(currentObjectId) ?? []) {
        if (visited.has(nextObjectId)) {
          continue;
        }
        visited.add(nextObjectId);
        queue.push(nextObjectId);
      }
    }

    return null;
  }

  private resolveAssociatedShellId(objectId: string | null | undefined): string | null {
    const normalizedObjectId = objectId?.trim();
    if (!normalizedObjectId) {
      return null;
    }

    const code = this.resolveAssociatedShellCode(normalizedObjectId);
    return code ? this.state.nodeMap().get(code)?.id ?? null : null;
  }

  private resolveViewportProfile(profileCode: string | null): SystemShellGraphNode | null {
    if (!profileCode) {
      return null;
    }

    const node = this.state.nodeMap().get(profileCode);
    return node?.family === 'ViewportProfile' ? node : null;
  }

  private relationshipGroupKey(row: RelationshipRow): string {
    return row.connectedFamily || 'Unknown';
  }

  private relationshipGroupLabel(key: string): string {
    return key;
  }

  private relationshipGroupOrder(key: string): number {
    const order: Record<string, number> = {
      Application: 5,
      Persona: 10,
      Journey: 20,
      JourneyStep: 30,
      BusinessRule: 40,
      Blocker: 50,
      Shell: 60,
      Screen: 70,
      Section: 80,
      Element: 90,
      Component: 100,
      ValidationRuleSet: 110,
      ValidationRule: 120,
      Unknown: 999,
    };

    return order[key] ?? 999;
  }

  private buildAttributeRows(node: SystemShellGraphNode, definitions: AttributeDefinition[]): AttributeRow[] {
    return definitions.map((definition) => {
      const rawValue = this.resolveGraphNodeAttribute(node, definition.attributeKey);
      const options = this.enumOptionsForAttribute(node, definition.attributeKey);
      const editorKind: AttributeEditorKind = definition.readonly
        ? 'readonly'
        : definition.valueType !== 'string'
        ? 'readonly'
        : options.length
          ? 'enum'
          : 'text';
      return {
        nodeCode: node.code,
        label: definition.label,
        attributeKey: definition.attributeKey,
        valueType: definition.valueType,
        rawValue,
        value: this.formatValue(rawValue),
        editable: editorKind !== 'readonly',
        editorKind,
        options,
      };
    });
  }

  private buildReadonlyAttributeRow(nodeCode: string, label: string, value: unknown): AttributeRow {
    return {
      nodeCode,
      label,
      attributeKey: 'hierarchyCode',
      valueType: 'string',
      rawValue: value,
      value: this.formatValue(value),
      editable: false,
      editorKind: 'readonly',
      options: [],
    };
  }

  private graphAttributeEditKey(nodeCode: string, attributeKey: keyof SystemShellGraphNode): string {
    return `${nodeCode}::${String(attributeKey)}`;
  }

  private resolveGraphNodeAttribute<K extends keyof SystemShellGraphNode>(
    node: SystemShellGraphNode,
    attributeKey: K,
  ): SystemShellGraphNode[K] {
    const override = this.graphAttributeValueOverrides()[this.graphAttributeEditKey(node.code, attributeKey)];
    if (override !== undefined) {
      return override as SystemShellGraphNode[K];
    }

    return node[attributeKey];
  }

  private clearAttributeDraft(row: AttributeRow): void {
    const key = this.graphAttributeEditKey(row.nodeCode, row.attributeKey);
    this.graphAttributeDrafts.update((drafts) => {
      if (!(key in drafts)) {
        return drafts;
      }

      const next = { ...drafts };
      delete next[key];
      return next;
    });
  }

  private enumOptionsForAttribute(
    _node: SystemShellGraphNode,
    attributeKey: keyof SystemShellGraphNode,
  ): AttributeOption[] {
    switch (attributeKey) {
      case 'status':
        return this.attributeOptions(['active', 'planned', 'hold', 'retired']);
      case 'domain':
        return this.attributeOptions(['business', 'frontend', 'backend']);
      case 'executionMethod':
        return this.attributeOptions(['mandatory', 'conditional']);
      case 'ruleScope':
        return this.attributeOptions(['journey', 'journey_step']);
      case 'executionEffect':
        return this.attributeOptions(['allow_step', 'require_step', 'skip_step', 'raise_blocker', 'redirect_outcome']);
      case 'blockerType':
        return this.attributeOptions(['business', 'access', 'policy', 'data', 'technical']);
      case 'blockingEffect':
        return this.attributeOptions(['prevent_execution', 'prevent_completion', 'redirect_outcome']);
      case 'backgroundType':
        return this.attributeOptions(['color', 'image', 'pattern', 'color_pattern', 'overlay_color_pattern']);
      case 'sectionType':
        return this.attributeOptions([
          'section',
          'header',
          'logo',
          'surface',
          'step',
          'message',
          'field',
          'panel',
          'action_bar',
          'support',
          'modal',
          'outcome',
          'provider',
        ]);
      case 'renderMode':
        return this.attributeOptions(['static', 'conditional']);
      case 'defaultState':
        return this.attributeOptions(['visible', 'hidden', 'disabled']);
      case 'controlSource':
        return this.attributeOptions(['none', 'validation_rule_set']);
      case 'elementType':
        return this.attributeOptions(['title', 'text', 'banner', 'visual', 'checkbox', 'toggle', 'input', 'button', 'indicator', 'message', 'display']);
      case 'semanticLevel':
        return this.attributeOptions(['H1', 'H2', 'H3', 'H4', 'H5', 'H6', 'none']);
      case 'primeComponent':
        return this.attributeOptions(
          Array.from(
            new Set(
              this.registryDefinitions()
                .map((definition) => definition.assetName)
                .filter((value): value is string => typeof value === 'string' && value.trim().length > 0),
            ),
          ).sort((left, right) => left.localeCompare(right)),
        );
      case 'ruleSetType':
        return this.attributeOptions(['screen_runtime']);
      case 'ruleSetScope':
        return this.attributeOptions(['screen']);
      case 'actionType':
        return this.attributeOptions(['show', 'hide', 'enable', 'disable', 'set_value', 'set_text', 'transition']);
      default:
        return [];
    }
  }

  private attributeOptions(values: readonly string[]): AttributeOption[] {
    return values.map((value) => ({ label: value, value }));
  }

  private formatValue(value: unknown): string {
    if (Array.isArray(value)) {
      return value.length ? value.join(', ') : 'n/a';
    }

    if (typeof value === 'boolean') {
      return value ? 'true' : 'false';
    }

    if (value === null || value === undefined || value === '') {
      return 'n/a';
    }

    return String(value);
  }

  private buildRegistryTree(): TreeNode<SystemShellTreeNodeData>[] {
    return [
      {
        key: 'primeng-components-registry',
        label: `Components Registry (${this.filteredRegistryItems().length})`,
        expanded: this.registryExpanded(),
        selectable: false,
        data: {
          kind: 'registry-root',
          code: 'PRIMENG_COMPONENTS_REGISTRY',
          label: 'Components Registry',
          family: 'Components Registry',
          layer: 'catalog',
          objectId: null,
          guid: null,
          domTargetGuid: null,
        },
        children: this.filteredRegistryItems().map((item) => ({
          key: item.code,
          label: item.assetName,
          expanded: false,
          selectable: true,
          data: {
            kind: 'registry-item',
            code: item.code,
            label: item.assetName,
            family: 'Component',
            layer: 'catalog',
            objectId: null,
            guid: null,
            domTargetGuid: null,
            registryKind: item.objectType,
            registryImplementationSourcePath: item.implementationSourcePath,
            registryDescription: item.description || null,
            registryAssetType: item.assetType,
            registryObjectType: item.objectType,
            registryInstanceCode: item.defaultInstanceCode,
          },
        })),
      },
    ];
  }

  private async loadRegistryDefinitions(): Promise<void> {
    this.registryLoading.set(true);
    this.registryError.set(null);

    try {
      const definitions = await firstValueFrom(this.api.getComponentRegistry());
      this.registryDefinitions.set(definitions);
    } catch (error) {
      this.registryError.set(error instanceof Error ? error.message : 'Unable to load the component registry.');
    } finally {
      this.registryLoading.set(false);
    }
  }

  private async loadRegistryInstance(assetType: string): Promise<void> {
    const current = this.selectedRegistryInstance();
    if (current?.assetType === assetType && !this.registryInstanceError()) {
      this.hydrateRegistryEditor(current);
      return;
    }

    this.registryInstanceLoading.set(true);
    this.registryInstanceError.set(null);

    try {
      const instance = await firstValueFrom(this.api.getComponentInstance(assetType));
      this.selectedRegistryInstance.set(instance);
      this.hydrateRegistryEditor(instance);
    } catch (error) {
      this.selectedRegistryInstance.set(null);
      this.registryInstanceError.set(error instanceof Error ? error.message : 'Unable to load the component instance.');
    } finally {
      this.registryInstanceLoading.set(false);
    }
  }

  private hydrateRegistryEditor(instance: ComponentRegistryInstance): void {
    const configuration = instance.configuration ?? {};
    this.accordionInstanceName.set(instance.name ?? instance.assetName);
    this.accordionInstanceStatus.set(instance.status ?? 'draft');
    this.accordionInstanceDescription.set(instance.description ?? '');
    this.accordionTargetObjectCode.set(instance.targetObjectCode ?? '');
    this.accordionBuilderRenderMethod.set(configuration['renderMethod'] === 'Dynamic' ? 'Dynamic' : 'Static');
    this.accordionBuilderMultiple.set(Boolean(configuration['multiple']));
    this.accordionBuilderSelectOnFocus.set(Boolean(configuration['selectOnFocus']));
    this.accordionBuilderDefaultValue.set(typeof configuration['defaultValue'] === 'string' ? configuration['defaultValue'] : '');
    this.accordionBuilderExpandIcon.set(typeof configuration['expandIcon'] === 'string' ? configuration['expandIcon'] : '');
    this.accordionBuilderCollapseIcon.set(typeof configuration['collapseIcon'] === 'string' ? configuration['collapseIcon'] : '');
    this.accordionBuilderDataSource.set(typeof configuration['dataSource'] === 'string' ? configuration['dataSource'] : 'panels');
    this.accordionBuilderValueField.set(typeof configuration['valueField'] === 'string' ? configuration['valueField'] : 'value');
    this.accordionBuilderHeaderField.set(typeof configuration['headerField'] === 'string' ? configuration['headerField'] : 'header');
    this.accordionBuilderContentField.set(typeof configuration['contentField'] === 'string' ? configuration['contentField'] : 'content');
    this.accordionBuilderDisabledField.set(typeof configuration['disabledField'] === 'string' ? configuration['disabledField'] : 'disabled');

    const rawPanels = Array.isArray(configuration['panels']) ? configuration['panels'] : [];
    const panels = rawPanels.map((panel, index) => {
      const typedPanel = typeof panel === 'object' && panel !== null ? panel as Record<string, unknown> : {};
      return {
        value: typeof typedPanel['value'] === 'string' ? typedPanel['value'] : String(index),
        header: typeof typedPanel['header'] === 'string' ? typedPanel['header'] : `Header ${index + 1}`,
        content: typeof typedPanel['content'] === 'string' ? typedPanel['content'] : '',
        disabled: Boolean(typedPanel['disabled']),
      };
    });
    this.accordionBuilderStaticPanels.set(panels.length ? panels : [{ value: '0', header: 'Header I', content: 'Panel content I', disabled: false }]);
    this.accordionBuilderPanelCount.set(this.accordionBuilderStaticPanels().length);
  }

  private buildAccordionConfiguration(): Record<string, unknown> {
    return this.accordionBuilderRenderMethod() === 'Static'
      ? {
          renderMethod: this.accordionBuilderRenderMethod(),
          multiple: this.accordionBuilderMultiple(),
          defaultValue: this.accordionBuilderDefaultValue().trim(),
          selectOnFocus: this.accordionBuilderSelectOnFocus(),
          expandIcon: this.accordionBuilderExpandIcon().trim(),
          collapseIcon: this.accordionBuilderCollapseIcon().trim(),
          panels: this.accordionBuilderStaticPanels().map((panel) => ({
            value: panel.value.trim(),
            header: panel.header.trim(),
            content: panel.content.trim(),
            disabled: panel.disabled,
          })),
        }
      : {
          renderMethod: this.accordionBuilderRenderMethod(),
          multiple: this.accordionBuilderMultiple(),
          defaultValue: this.accordionBuilderDefaultValue().trim(),
          selectOnFocus: this.accordionBuilderSelectOnFocus(),
          expandIcon: this.accordionBuilderExpandIcon().trim(),
          collapseIcon: this.accordionBuilderCollapseIcon().trim(),
          dataSource: this.accordionBuilderDataSource().trim(),
          valueField: this.accordionBuilderValueField().trim(),
          headerField: this.accordionBuilderHeaderField().trim(),
          contentField: this.accordionBuilderContentField().trim(),
          disabledField: this.accordionBuilderDisabledField().trim(),
        };
  }

  private parseConfiguration(configurationJson: string | null): Record<string, unknown> {
    if (!configurationJson) {
      return {};
    }

    try {
      const parsed = JSON.parse(configurationJson) as unknown;
      return typeof parsed === 'object' && parsed !== null ? parsed as Record<string, unknown> : {};
    } catch {
      return {};
    }
  }

  private recalculatePreviewScale(): void {
    const stage = this.previewStageRef?.nativeElement;
    if (!stage) {
      return;
    }

    const availableWidth = Math.max(stage.clientWidth - 16, 1);
    const availableHeight = Math.max(stage.clientHeight - 16, 1);
    const scale = Math.min(
      availableWidth / this.previewCanvasWidth(),
      availableHeight / this.previewCanvasHeight(),
      1,
    );

    this.previewScale.set(scale > 0 ? scale : 1);
  }

  private bindPreviewStageObserver(): void {
    const stage = this.previewStageRef?.nativeElement;
    if (!stage) {
      return;
    }

    this.previewResizeObserver?.disconnect();
    this.previewResizeObserver = new ResizeObserver(() => this.recalculatePreviewScale());
    this.previewResizeObserver.observe(stage);
    requestAnimationFrame(() => this.recalculatePreviewScale());
  }

  private bindPreviewInteractions(): void {
    const stage = this.previewStageRef?.nativeElement;
    if (!stage || this.activeWorkspace() !== 'frontend') {
      return;
    }

    this.unbindPreviewInteractions();

    this.previewClickListener = (event: MouseEvent) => {
      const resolver = this.previewDomResolver();
      const objectId = resolver.resolveObjectIdFromEvent(event.target);
      const guid = resolver.resolveGuidFromEvent(event.target);
      if (!objectId && !guid) {
        return;
      }

      event.preventDefault();
      event.stopPropagation();
      this.ngZone.run(() => {
        this.activeInspectorTab.set('preview');
        if (objectId) {
          this.state.selectObjectId(objectId);
        } else {
          this.state.selectGuid(guid);
        }
      });
    };

    this.previewMoveListener = (event: MouseEvent) => {
      const resolver = this.previewDomResolver();
      const objectId = resolver.resolveObjectIdFromEvent(event.target);
      const guid = resolver.resolveGuidFromEvent(event.target);
      const hoveredNode = objectId
        ? this.state.nodeIdMap().get(objectId) ?? null
        : guid
          ? this.state.nodeGuidMap().get(guid) ?? null
          : null;
      const hoveredPreviewGuid = hoveredNode?.family === 'Component'
        ? this.linkedGuidForNode(hoveredNode)
        : hoveredNode?.guid?.trim() ?? null;
      this.ngZone.run(() => {
        this.previewHoveredGraphCode.set(hoveredNode?.code ?? null);
        this.hoveredPreviewGuid.set(hoveredPreviewGuid);
      });
    };

    this.previewLeaveListener = () => {
      this.ngZone.run(() => {
        this.previewHoveredGraphCode.set(null);
        this.hoveredPreviewGuid.set(null);
      });
    };

    stage.addEventListener('click', this.previewClickListener);
    stage.addEventListener('mousemove', this.previewMoveListener);
    stage.addEventListener('mouseleave', this.previewLeaveListener);
  }

  private unbindPreviewInteractions(): void {
    const stage = this.previewStageRef?.nativeElement;
    if (!stage) {
      return;
    }

    if (this.previewClickListener) {
      stage.removeEventListener('click', this.previewClickListener);
      this.previewClickListener = undefined;
    }
    if (this.previewMoveListener) {
      stage.removeEventListener('mousemove', this.previewMoveListener);
      this.previewMoveListener = undefined;
    }
    if (this.previewLeaveListener) {
      stage.removeEventListener('mouseleave', this.previewLeaveListener);
      this.previewLeaveListener = undefined;
    }
  }

  private bindHostActions(): void {
    this.hostClickListener = (event: MouseEvent) => {
      const actionTarget = event.target instanceof HTMLElement
        ? event.target.closest<HTMLElement>('[data-registry-action]')
        : null;
      const action = actionTarget?.dataset['registryAction'];

      if (!action || !this.activeComponentInstanceView() || actionTarget.hasAttribute('disabled')) {
        return;
      }

      event.preventDefault();
      event.stopPropagation();

      this.ngZone.run(() => {
        switch (action) {
          case 'edit':
            this.enableRegistryEdit();
            break;
          case 'save':
            void this.saveRegistryInstance();
            break;
          case 'cancel':
            this.cancelRegistryEdit();
            break;
        }
      });
    };

    this.hostElement.nativeElement.addEventListener('click', this.hostClickListener);
  }

  private findPreviewElementByGuid(guid: string | null): HTMLElement | null {
    return this.previewDomResolver().findElementForGuid(guid);
  }

  private togglePreviewIssueWarning(guid: string | null): void {
    const objectId = guid ? this.state.nodeGuidMap().get(guid)?.id?.trim() ?? null : null;
    if (!objectId || !this.hasDirectInspectionViolation(objectId)) {
      return;
    }

    this.findPreviewElementByGuid(guid)?.classList.add('ssg-issue-warning');
  }

  private resolveSelectedPreviewGuid(): string | null {
    const selectedNode = this.state.selectedGraphNode();
    if (!selectedNode) {
      return null;
    }

    if (selectedNode.family === 'Application') {
      const activeShellCode = this.activeShellCode();
      return activeShellCode ? this.state.nodeMap().get(activeShellCode)?.guid?.trim() ?? null : null;
    }

    if (selectedNode.family === 'Component') {
      return this.linkedGuidForNode(selectedNode);
    }

    return selectedNode.guid?.trim() ?? null;
  }

  resolvedIssueGuid(issue: Pick<SystemShellGraphIssueNode, 'targetObjectId'>): string | null {
    const targetObjectId = issue.targetObjectId?.trim();
    if (!targetObjectId) {
      return null;
    }

    return this.state.nodeIdMap().get(targetObjectId)?.guid?.trim() ?? null;
  }

  private linkedGuidForNode(node: Pick<SystemShellGraphNode, 'id' | 'family' | 'guid'>): string | null {
    if (node.family === 'Component') {
      const linkedGuid = this.componentTargetNode(node)?.guid?.trim() ?? null;
      if (linkedGuid) {
        return linkedGuid;
      }
    }

    return node.guid?.trim() ?? null;
  }

  private componentTargetNode(node: Pick<SystemShellGraphNode, 'id' | 'family'>): SystemShellGraphNode | null {
    if (node.family !== 'Component' || !node.id) {
      return null;
    }

    const graph = this.state.graph();
    if (!graph) {
      return null;
    }

    const incoming = graph.relationships.find((relationship) =>
      relationship.relationshipType === 'HAS_COMPONENT' && relationship.toId === node.id,
    );
    return incoming ? this.state.nodeIdMap().get(incoming.fromId) ?? null : null;
  }

  private issueTargetNode(node: Pick<SystemShellGraphNode, 'id' | 'family'>): SystemShellGraphNode | null {
    if (node.family !== 'Issue' || !node.id) {
      return null;
    }

    const graph = this.state.graph();
    if (!graph) {
      return null;
    }

    const incoming = graph.relationships.find((relationship) =>
      relationship.relationshipType === 'HAS_ISSUE' && relationship.toId === node.id,
    );
    return incoming ? this.state.nodeIdMap().get(incoming.fromId) ?? null : null;
  }

  private issueTargetObjectId(node: Pick<SystemShellGraphNode, 'id' | 'family'>): string | null {
    return this.issueTargetNode(node)?.id?.trim() ?? null;
  }

  private componentTargetIdMap(): Map<string, string> {
    const graph = this.state.graph();
    if (!graph) {
      return new Map<string, string>();
    }

    return new Map(
      graph.relationships
        .filter((relationship) => relationship.relationshipType === 'HAS_COMPONENT')
        .map((relationship) => [relationship.toId, relationship.fromId]),
    );
  }

  private previewDomResolver(): PreviewDomResolver {
    return new PreviewDomResolver(
      this.previewStageRef?.nativeElement ?? null,
      this.state.nodeMap(),
      this.state.nodeIdMap(),
      this.state.nodeGuidMap(),
      this.componentTargetIdMap(),
    );
  }

  private findTreePathByObjectId(
    nodes: TreeNode<SystemShellTreeNodeData>[],
    objectId: string,
    path: TreeNode<SystemShellTreeNodeData>[] = [],
  ): TreeNode<SystemShellTreeNodeData>[] {
    for (const node of nodes) {
      const nextPath = [...path, node];
      if (node.data?.objectId === objectId) {
        return nextPath;
      }

      if (node.children?.length) {
        const childPath = this.findTreePathByObjectId(node.children, objectId, nextPath);
        if (childPath.length) {
          return childPath;
        }
      }
    }

    return [];
  }
}
