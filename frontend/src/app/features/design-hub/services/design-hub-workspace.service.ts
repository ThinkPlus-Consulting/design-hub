import { DestroyRef, Injectable, NgZone, computed, effect, inject, signal, untracked } from '@angular/core';
import { TreeNode } from 'primeng/api';
import { firstValueFrom } from 'rxjs';
import { DesignHubApiService } from './design-hub-api.service';
import { DesignHubStateService } from './design-hub-state.service';
import { ComponentRegistryDefinition, ComponentRegistryInstance } from '../models/component-registry.types';
import { ShellBackgroundConfig, SystemShellGraphNode, SystemShellGraphRelationship, SystemShellGraphResponse, SystemShellTreeNodeData } from '../models/graph.types';
import { SystemShellGraphIssueNode, SystemShellGraphIssueScanSummary } from '../models/issues.types';
import { AccessibilityConformanceLevel, auditPreviewAccessibility } from '../utils/preview-accessibility-audit';
import { PreviewDomResolver } from '../utils/preview-dom-resolver';

interface RelationshipRow {
  direction: string;
  canonicalType: string;
  type: string;
  connectedFamily: string;
  connectedLayer: string;
  connectedObjectId: string | null;
  connectedReference: string;
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

interface PreviewAuditRenderContext {
  screenObjectId: string | null;
  shellObjectId: string | null;
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
  nodeId: string;
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
  { label: 'Domain', attributeKey: 'domain', valueType: 'string', readonly: true },
  { label: 'Layer', attributeKey: 'layer', valueType: 'string', readonly: true },
  { label: 'Object Type', attributeKey: 'objectType', valueType: 'string', readonly: true },
  { label: 'Definition ID', attributeKey: 'definitionId', valueType: 'string', readonly: true },
  { label: 'Package Name', attributeKey: 'packageName', valueType: 'string', readonly: true },
  { label: 'Package Export', attributeKey: 'packageExport', valueType: 'string', readonly: true },
  { label: 'Package Version', attributeKey: 'packageVersion', valueType: 'string', readonly: true },
  { label: 'Icon Package', attributeKey: 'iconPackage', valueType: 'string', readonly: true },
  { label: 'Theme Package', attributeKey: 'themePackage', valueType: 'string', readonly: true },
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
  { label: 'HTML Tag', attributeKey: 'htmlTag', valueType: 'string', readonly: true },
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
  { label: 'Priority', attributeKey: 'priority', valueType: 'number', readonly: true },
  { label: 'Stop Processing', attributeKey: 'stopProcessing', valueType: 'boolean', readonly: true },
];

type PreviewViewportProfile = 'web' | 'mobile';

interface PreviewViewportOption {
  value: PreviewViewportProfile;
  label: string;
  viewportCategory: PreviewViewportProfile;
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
  { value: 'web', label: 'Web', viewportCategory: 'web' },
  { value: 'mobile', label: 'Mobile', viewportCategory: 'mobile' },
];

const X_RAY_STAGE_DEFINITIONS: readonly Omit<XRayStageState, 'status'>[] = [
  {
    key: 'graph-inventory',
    label: 'Graph Inventory',
    description: 'Count Shell, Screen, Container, Section, and Component objects from Neo4j.',
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

@Injectable()
export class DesignHubWorkspaceService {
  private readonly api = inject(DesignHubApiService);
  private readonly ngZone = inject(NgZone);
  private readonly destroyRef = inject(DestroyRef);
  readonly state = inject(DesignHubStateService);
  readonly activeWorkspace = signal<'frontend' | 'components-registry'>('frontend');
  readonly frontendTreeExpanded = signal(false);
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
  readonly previewHoveredObjectId = signal<string | null>(null);
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
  readonly selectedInspectorIssueIds = signal<string[]>([]);
  readonly inspectorIssueStatusFilter = signal<'open' | 'all'>('open');
  readonly inspectorIssueCategoryFilter = signal<string>('all');
  readonly viewedIssueId = signal<string | null>(null);
  readonly issuePromptDraft = signal('');
  readonly issuePromptSaving = signal(false);
  readonly previewViewportProfile = signal<PreviewViewportProfile>('web');
  readonly auditPreviewShellObjectId = signal<string | null>(null);
  readonly auditPreviewScreenObjectId = signal<string | null>(null);
  readonly pinnedPreviewScreenObjectId = signal<string | null>(null);
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
  readonly accordionTargetObjectId = signal('');
  readonly accessibilityTargetLevel: AccessibilityConformanceLevel = 'AAA';
  readonly activeRelationshipTab = signal<string>('validation-control');
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
        || (item.id ?? '').toLowerCase().includes(searchTerm),
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
  readonly activeTreeExpanded = computed(() =>
    this.activeWorkspace() === 'frontend' ? this.frontendTreeExpanded() : this.registryExpanded(),
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
      current.push(issue.description ?? issue.name ?? issue.id ?? issue.family);
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
    return this.issueRegistryNodes()
      .slice()
      .sort((left, right) => {
        const leftTarget = this.issueTargetObjectId(left) ?? '';
        const rightTarget = this.issueTargetObjectId(right) ?? '';
        return leftTarget.localeCompare(rightTarget) || this.graphNodeRef(left).localeCompare(this.graphNodeRef(right));
      })
      .map((issue, index) => this.mapPersistedIssueNode(issue, index + 1));
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
    const selectedIssueIds = new Set(this.selectedInspectorIssueIds());
    return this.filteredSelectedInspectorIssues().filter((issue) => selectedIssueIds.has(issue.id));
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
      { label: 'Viewport', value: String(countBy((issue) => issue.category === 'Viewport Navigation')) },
      { label: 'HTML', value: String(countBy((issue) => issue.category === 'HTML Element Violation')) },
      { label: 'Parity', value: String(countBy((issue) => issue.category === 'Preview-Tree Parity')) },
      { label: 'Accessibility', value: String(countBy((issue) => issue.category === 'Accessibility')) },
    ];
  });
  readonly viewedInspectorIssue = computed<SystemShellGraphIssueNode | null>(() =>
    this.inspectorIssueObjects().find((issue) => issue.id === this.viewedIssueId()) ?? null,
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
      objectType: node.objectType ?? 'Component',
      assetType: node.assetType ?? '',
      assetName: node.assetName ?? node.name ?? '',
      name: node.name,
      description: node.description,
      id: node.id,
      status: node.status,
      definitionId: node.definitionId,
      packageName: node.packageName,
      packageExport: node.packageExport,
      packageVersion: node.packageVersion,
      iconPackage: node.iconPackage,
      themePackage: node.themePackage,
      targetObjectId: targetNode?.id ?? null,
      targetObjectName: targetNode ? this.displayNodeName(targetNode) : null,
      targetObjectType: targetNode?.family ?? null,
      configuration: this.parseConfiguration(node.configurationJson),
    };
  });
  readonly activeComponentInstanceView = computed<ComponentRegistryInstance | null>(() => {
    const instance = this.activeWorkspace() === 'frontend'
      ? this.selectedFrontendComponentInstanceView()
      : this.selectedRegistryInstance();

    return instance?.id ? instance : null;
  });
  readonly activeComponentDefinitionView = computed<ComponentRegistryDefinition | null>(() => {
    const instance = this.activeComponentInstanceView();
    if (!instance) {
      return this.activeWorkspace() === 'components-registry' ? this.selectedRegistryDefinition() : null;
    }

    return this.registryDefinitions().find((item) =>
      item.id === instance.definitionId
      || item.assetType === instance.assetType
      || item.assetName === instance.assetName,
    ) ?? null;
  });
  readonly activeScreenObjectId = computed(() => this.pinnedPreviewScreenObjectId() ?? this.defaultScreenObjectId());
  readonly activeShellObjectId = computed(() => {
    const selected = this.state.selectedGraphNode();
    const componentInstance = this.selectedFrontendComponentInstanceView();
    if (!selected && !componentInstance) {
      return this.defaultShellObjectId();
    }

    return this.resolveAssociatedShellId(componentInstance?.targetObjectId ?? selected?.id ?? null) ?? this.defaultShellObjectId();
  });
  readonly activeShellBackgroundConfig = computed<ShellBackgroundConfig | null>(() => {
    const shellNode = this.state.nodeIdMap().get(this.activeShellObjectId() ?? '');
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
  readonly displayedPreviewShellObjectId = computed(() => this.auditPreviewShellObjectId() ?? this.activeShellObjectId());
  readonly displayedPreviewScreenObjectId = computed(() => this.auditPreviewScreenObjectId() ?? this.activeScreenObjectId());
  readonly displayedPreviewSelectedGuid = computed(() =>
    this.auditPreviewShellObjectId() || this.auditPreviewScreenObjectId() ? null : this.selectedPreviewGuid(),
  );
  readonly displayedPreviewBackgroundConfig = computed<ShellBackgroundConfig | null>(() =>
    this.shellBackgroundConfigForObjectId(this.displayedPreviewShellObjectId()),
  );
  readonly activePreviewInteractionPalette = computed<PreviewInteractionPalette>(() => {
    if (this.isAuthenticationShellObjectId(this.displayedPreviewShellObjectId())) {
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

  private defaultShellObjectId(): string | null {
    return (this.state.graph()?.nodes ?? [])
      .filter((node) => node.layer === 'instance' && node.family === 'Shell')
      .sort((left, right) => this.sortGraphNodes(left, right))[0]?.id ?? null;
  }

  private defaultScreenObjectId(): string | null {
    return (this.state.graph()?.nodes ?? [])
      .filter((node) => node.layer === 'instance' && node.family === 'Screen')
      .sort((left, right) => this.sortGraphNodes(left, right))[0]?.id ?? null;
  }

  private isAuthenticationShellObjectId(shellObjectId: string | null | undefined): boolean {
    const normalizedShellObjectId = shellObjectId?.trim();
    if (!normalizedShellObjectId) {
      return false;
    }

    const shellNode = this.state.nodeIdMap().get(normalizedShellObjectId);
    return shellNode?.name?.trim().toLowerCase() === 'authentication-shell';
  }

  shellBackgroundConfigForObjectId(shellObjectId: string | null | undefined): ShellBackgroundConfig | null {
    const normalizedShellObjectId = shellObjectId?.trim();
    if (!normalizedShellObjectId) {
      return null;
    }

    const shellNode = this.state.nodeIdMap().get(normalizedShellObjectId);
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
      return [{ objectId: null, label: 'Select a screen, section, or component' }];
    }

    const path = this.findTreePathByObjectId(this.state.tree(), selectedObjectId);
    if (!path.length) {
      const selected = this.state.selectedGraphNode();
      return [{ objectId: selectedObjectId, label: selected?.name ?? selectedObjectId }];
    }

    return path.map((node) => ({
      objectId: node.data?.objectId ?? null,
      label: node.label ?? node.data?.label ?? node.data?.objectId ?? 'Unknown',
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

    if (node.domain === 'frontend' && ['Shell', 'Container', 'Screen', 'Section', 'Component'].includes(node.family)) {
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
        HAS_COMPONENT: 110,
        HAS_STEP: 120,
        CAN_EXECUTE: 130,
        REFERENCES: 140,
      };

    return graph.relationships
      .filter((relationship) => relationship.fromId === node.id || relationship.toId === node.id)
      .map((relationship) => {
        const isOutgoing = relationship.fromId === node.id;
        const connectedObjectId = isOutgoing ? relationship.toId : relationship.fromId;
        const connectedNode = connectedObjectId ? this.state.nodeIdMap().get(connectedObjectId) ?? null : null;
        const connectedReference = connectedObjectId ?? 'Unknown';
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
          connectedReference,
          connectedName: connectedNode ? this.displayNodeName(connectedNode) : connectedReference,
        };
      })
      .filter((relationship) => relationship.connectedLayer === 'instance')
      .sort((left, right) =>
        (relationshipPriority[left.canonicalType] ?? 999) - (relationshipPriority[right.canonicalType] ?? 999)
        || left.direction.localeCompare(right.direction)
        || left.connectedFamily.localeCompare(right.connectedFamily)
        || left.connectedReference.localeCompare(right.connectedReference),
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
    this.activeScreenObjectId();
    this.activeShellObjectId();
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
        definitionId: 'n/a',
        assetName: 'n/a',
        assetType: 'n/a',
        packageName: 'n/a',
        packageExport: 'n/a',
        packageVersion: 'n/a',
        iconPackage: 'n/a',
        themePackage: 'n/a',
        status: 'n/a',
        description: 'Choose a component asset from the registry to inspect and edit its instance configuration.',
      };
    }

    return {
      title: instance.name || definition.assetName,
      id: this.formatValue(instance.id),
      type: this.formatValue(instance.objectType),
      definitionId: this.formatValue(instance.definitionId ?? definition.id),
      assetName: this.formatValue(instance.assetName),
      assetType: this.formatValue(instance.assetType),
      packageName: this.formatValue(instance.packageName ?? definition.packageName),
      packageExport: this.formatValue(instance.packageExport ?? definition.packageExport),
      packageVersion: this.formatValue(instance.packageVersion ?? definition.packageVersion),
      iconPackage: this.formatValue(instance.iconPackage ?? definition.iconPackage),
      themePackage: this.formatValue(instance.themePackage ?? definition.themePackage),
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
      { label: 'Target Object', value: instance.targetObjectId ? '1' : '0' },
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
      { label: 'Definition ID', value: this.formatValue(instance.definitionId) },
      { label: 'Asset Name', value: this.formatValue(instance.assetName) },
      { label: 'Asset Type', value: this.formatValue(instance.assetType) },
      { label: 'Package Name', value: this.formatValue(instance.packageName) },
      { label: 'Package Export', value: this.formatValue(instance.packageExport) },
      { label: 'Package Version', value: this.formatValue(instance.packageVersion) },
      { label: 'Icon Package', value: this.formatValue(instance.iconPackage) },
      { label: 'Theme Package', value: this.formatValue(instance.themePackage) },
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
      { label: 'Target Object', value: this.formatValue(instance.targetObjectId) },
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
      .filter((node) => node.layer === 'instance' && this.isStructuralContainerFamily(node.family))
      .sort((left, right) => (left.sortOrder ?? Number.MAX_SAFE_INTEGER) - (right.sortOrder ?? Number.MAX_SAFE_INTEGER))
      .map((node) => ({
        objectId: node.id ?? '',
        label: `${this.displayNodeName(node)} · ${node.id ?? 'n/a'}`,
      })),
  );
  readonly activeScreenNode = computed(() => {
    const screenObjectId = this.activeScreenObjectId();
    const node = screenObjectId ? this.state.nodeIdMap().get(screenObjectId) ?? null : null;
    return node?.family === 'Screen' ? node : null;
  });
  readonly previewViewportNode = computed(() => {
    const profile = PREVIEW_VIEWPORT_OPTIONS.find((option) => option.value === this.previewViewportProfile()) ?? PREVIEW_VIEWPORT_OPTIONS[0]!;
    return this.resolveViewportProfile(profile.viewportCategory ?? null);
  });
  readonly previewCanvasWidth = computed(() => {
    return this.previewViewportNode()?.viewportWidth ?? 1440;
  });
  readonly previewCanvasHeight = computed(() => {
    return this.previewViewportNode()?.viewportHeight ?? 1024;
  });
  private previewStageElement: HTMLElement | null = null;
  private previewCanvasRootElement: HTMLElement | null = null;
  private previewResizeObserver?: ResizeObserver;
  private previewClickListener?: (event: MouseEvent) => void;
  private previewMoveListener?: (event: MouseEvent) => void;
  private previewLeaveListener?: () => void;

  private readonly previewScreenContextSync = effect(() => {
    const selected = this.state.selectedGraphNode();
    const componentInstance = this.selectedFrontendComponentInstanceView();
    const preferredScreenObjectId = untracked(() => this.pinnedPreviewScreenObjectId());
    const selectionObjectId = componentInstance?.targetObjectId ?? selected?.id ?? null;
    const nextScreenObjectId =
      this.resolvePreviewScreenObjectIdForSelection(selectionObjectId, preferredScreenObjectId)
      ?? this.defaultScreenObjectId();

    if (nextScreenObjectId && preferredScreenObjectId !== nextScreenObjectId) {
      this.pinnedPreviewScreenObjectId.set(nextScreenObjectId);
    }
  });

  private readonly focusSync = effect(() => {
    this.selectedPreviewGuid();
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
    const stage = this.previewStageElement;
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
    this.activeScreenObjectId();
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

    const visibleDefinitionIds = new Set(
      this.filteredRegistryItems()
        .map((item) => item.id?.trim() ?? '')
        .filter((itemId) => !!itemId),
    );
    if (!visibleDefinitionIds.has(selected.data.objectId ?? '')) {
      this.selectedRegistryTreeNode.set(null);
    }
  });

  private readonly registryInstanceSync = effect(() => {
    const selected = this.selectedRegistryTreeNode();
    const assetType = selected?.data?.registryAssetType;
    const definitionId = selected?.data?.objectId;
    if (!assetType || !definitionId) {
      this.selectedRegistryDefinition.set(null);
      this.selectedRegistryInstance.set(null);
      this.registryEditMode.set(false);
      return;
    }

    const definition = this.registryDefinitions().find((item) => item.id === definitionId) ?? null;
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

  constructor() {
    this.destroyRef.onDestroy(() => {
      this.destroy();
    });
  }

  initialize(): void {
    void this.state.load();
    void this.loadRegistryDefinitions();
  }

  attachPreviewDom(stage: HTMLElement | null, canvasRoot: HTMLElement | null): void {
    this.previewStageElement = stage;
    this.previewCanvasRootElement = canvasRoot;
    this.bindPreviewStageObserver();
    this.bindPreviewInteractions();
    this.recalculatePreviewScale();
  }

  detachPreviewDom(): void {
    this.previewResizeObserver?.disconnect();
    this.previewResizeObserver = undefined;
    this.unbindPreviewInteractions();
    this.previewStageElement = null;
    this.previewCanvasRootElement = null;
  }

  destroy(): void {
    this.detachPreviewDom();
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
      this.frontendTreeExpanded.set(true);
      this.state.expandAll();
      return;
    }

    this.registryExpanded.set(true);
  }

  collapseActiveTree(): void {
    if (this.activeWorkspace() === 'frontend') {
      this.frontendTreeExpanded.set(false);
      this.state.collapseAll();
      return;
    }

    this.registryExpanded.set(false);
  }

  setActiveTreeExpanded(expanded: boolean): void {
    if (expanded) {
      this.expandActiveTree();
      return;
    }

    this.collapseActiveTree();
  }

  onTreeSelection(node: TreeNode<SystemShellTreeNodeData> | TreeNode<SystemShellTreeNodeData>[] | null | undefined): void {
    const selectedNode = Array.isArray(node) ? node[0] ?? null : node ?? null;
    this.registryEditMode.set(false);
    if (this.activeWorkspace() === 'frontend') {
      this.state.selectObjectId(selectedNode?.data?.kind === 'graph' ? selectedNode.data.objectId : null);
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
      this.state.selectObjectId(node.data?.kind === 'graph' ? node.data.objectId : null);
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

  onBreadcrumbClick(objectId: string | null): void {
    this.activeInspectorTab.set('overview');
    this.state.selectObjectId(objectId);
  }

  onRelatedObjectClick(objectId: string | null): void {
    this.activeInspectorTab.set('overview');
    this.state.selectObjectId(objectId);
  }

  onRelationshipTabChange(value: string | number | undefined): void {
    this.activeRelationshipTab.set(typeof value === 'string' ? value : '');
  }

  attributeDisplayValue(row: AttributeRow): string {
    return row.value === 'n/a' ? 'Click to edit' : row.value;
  }

  attributeDraftValue(row: AttributeRow): string {
    const key = this.graphAttributeEditKey(row.nodeId, row.attributeKey);
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

    const key = this.graphAttributeEditKey(row.nodeId, row.attributeKey);
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
    const key = this.graphAttributeEditKey(row.nodeId, row.attributeKey);
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

    const key = this.graphAttributeEditKey(row.nodeId, row.attributeKey);
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
    field: 'name' | 'status' | 'description' | 'targetObjectId',
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
      case 'targetObjectId':
        this.accordionTargetObjectId.set(value);
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
      const savedInstance = await firstValueFrom(this.api.saveComponentInstance(instance.id ?? '', {
        name: this.accordionInstanceName().trim() || instance.assetName,
        description: this.accordionInstanceDescription().trim() || null,
        status: this.accordionInstanceStatus().trim() || null,
        targetObjectId: this.accordionTargetObjectId().trim() || null,
        configuration,
      }));
      if (this.activeWorkspace() === 'components-registry') {
        this.selectedRegistryInstance.set(savedInstance);
      }
      await this.state.load(savedInstance.targetObjectId ?? this.state.selectedObjectId());
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
        await this.ensurePreviewCanvasReady();
      }

      const report = await this.buildXRayScanReport();
      await this.runXRayStage('issue-persistence', async () => {
        const summary = await firstValueFrom(this.api.scanIssues({ issues: report.issues }));
        await this.state.load(preferredObjectId);
        this.selectedInspectorIssueIds.set([]);
        this.viewedIssueId.set(null);
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

    return objectId === this.previewHoveredObjectId();
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
    this.selectedInspectorIssueIds.set(items.map((issue) => issue.id));
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
    this.selectedInspectorIssueIds.set([]);
  }

  onInspectorIssueStatusFilterChange(event: Event): void {
    const value = (event.target as HTMLSelectElement | null)?.value ?? 'open';
    this.setInspectorIssueStatusFilter(value);
  }

  setInspectorIssueCategoryFilter(value: string): void {
    this.inspectorIssueCategoryFilter.set(value || 'all');
    this.selectedInspectorIssueIds.set([]);
  }

  onInspectorIssueCategoryFilterChange(event: Event): void {
    const value = (event.target as HTMLSelectElement | null)?.value ?? 'all';
    this.setInspectorIssueCategoryFilter(value);
  }

  selectAllInspectorIssues(): void {
    this.selectedInspectorIssueIds.set(this.filteredSelectedInspectorIssues().map((issue) => issue.id));
  }

  clearInspectorIssueSelection(): void {
    this.selectedInspectorIssueIds.set([]);
  }

  viewInspectorIssue(issue: SystemShellGraphIssueNode): void {
    this.viewedIssueId.set(issue.id);
    this.issuePromptDraft.set(issue.prompt);
  }

  closeInspectorIssueModal(): void {
    this.viewedIssueId.set(null);
    this.issuePromptDraft.set('');
  }

  onIssuePromptDraftChange(event: Event): void {
    this.issuePromptDraft.set((event.target as HTMLTextAreaElement | null)?.value ?? '');
  }

  async resolveInspectorIssue(issue: SystemShellGraphIssueNode): Promise<void> {
    await firstValueFrom(this.api.updateIssueStatuses({ issueIds: [issue.id], status: 'closed' }));
    await this.state.load(this.activeSelectionObjectId());
    this.viewedIssueId.set(issue.id);
  }

  async resolveSelectedInspectorIssues(): Promise<void> {
    const selectedOpenIssues = this.selectedInspectorIssueSelection().filter((issue) => issue.status === 'open');
    if (!selectedOpenIssues.length) {
      return;
    }

    await firstValueFrom(this.api.updateIssueStatuses({
      issueIds: selectedOpenIssues.map((issue) => issue.id),
      status: 'closed',
    }));
    await this.state.load(this.activeSelectionObjectId());
    this.selectedInspectorIssueIds.set([]);
  }

  async saveInspectorIssuePrompt(issue: SystemShellGraphIssueNode): Promise<void> {
    if (this.issuePromptSaving()) {
      return;
    }

    this.issuePromptSaving.set(true);
    try {
      await firstValueFrom(this.api.updateIssue(issue.id, {
        issuePrompt: this.issuePromptDraft().trim() || null,
      }));
      await this.state.load(this.activeSelectionObjectId());
      this.viewedIssueId.set(issue.id);
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

  private async ensurePreviewCanvasReady(): Promise<void> {
    for (let attempt = 0; attempt < 10; attempt += 1) {
      await this.waitForPaint();
      if (this.previewCanvasRootElement) {
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

    const nodeMap = this.state.nodeIdMap();
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

    try {
      const graphInventory = await this.runXRayStage('graph-inventory', async () => this.collectGraphInventorySnapshot(graph));
      const previewAudit = await this.runXRayStage('preview-inventory', async () => this.collectPreviewAuditSnapshot(nodeMap));

      await this.runXRayStage('completeness-checks', async () => {
        for (const node of graphInventory.nodes) {
          const ownerObjectId =
            node.id?.trim()
            ?? this.resolveAssociatedScreenId(node.id)
            ?? this.resolveAssociatedShellId(node.id)
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
        for (const [objectId, messages] of await this.buildStructuralInspectionViolations()) {
          const targetNode = nodeMap.get(objectId);
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
        for (const screenNode of this.previewAuditScreenNodes(graph)) {
          await this.withRenderedPreviewAuditScreen(screenNode, async (auditRoot) => {
            const resolver = this.previewDomResolverForAuditRoot(auditRoot);
            const screenElement = resolver.findElementForObjectId(screenNode.id);
            if (!screenElement) {
              return;
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
                issue.graphObjectId ?? screenNode.id,
                `WCAG ${issue.level}: ${issue.rule}. ${issue.message}`,
              );
            }
          });
        }
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
    } finally {
      await this.clearPreviewAuditRender();
    }
  }

  private collectGraphInventorySnapshot(graph: SystemShellGraphResponse): XRayGraphInventorySnapshot {
    const families = ['Shell', 'Screen', 'Container', 'Section', 'Component'];
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

  private async collectPreviewAuditSnapshot(
    nodeMap: Map<string, SystemShellGraphNode>,
  ): Promise<XRayPreviewAuditSnapshot> {
    const graph = this.state.graph();
    if (!graph) {
      return {
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

    const artifacts: XRayPreviewArtifactSnapshot[] = [];

    try {
      for (const screenNode of this.previewAuditScreenNodes(graph)) {
        const screenArtifacts = await this.withRenderedPreviewAuditScreen(screenNode, (auditRoot, context) =>
          this.collectPreviewArtifactsForAuditRoot(auditRoot, nodeMap, context),
        );
        if (screenArtifacts?.length) {
          artifacts.push(...screenArtifacts);
        }
      }
    } finally {
      await this.clearPreviewAuditRender();
    }

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
  }

  private collectPreviewArtifactsForAuditRoot(
    auditRoot: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
    context: PreviewAuditRenderContext,
  ): XRayPreviewArtifactSnapshot[] {
    return Array.from(auditRoot.querySelectorAll<HTMLElement>('*'))
      .filter((element) => this.isVisiblePreviewArtifact(element))
      .map((element) => {
        const sourceObjectId = this.sourceObjectIdFromElement(element);
        const guid = this.guidFromElement(element);
        const ownerObjectId = this.resolveRawHtmlArtifactOwnerObjectId(element, nodeMap, context);
        return {
          tag: element.tagName.toLowerCase(),
          description: this.describeRawHtmlArtifact(element),
          textSnippet: this.rawHtmlArtifactTextSnippet(element),
          sourceObjectId,
          guid,
          ownerObjectId,
          isPrimeArtifact: this.isPrimePreviewArtifact(element),
          isSelectable: !!sourceObjectId || !!guid,
        } satisfies XRayPreviewArtifactSnapshot;
      });
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
  ): SystemShellGraphIssueNode {
    const targetNode = this.issueTargetNode(issue);
    const targetObjectId = targetNode?.id?.trim() ?? null;
    const targetName = targetNode ? this.displayNodeName(targetNode) : issue.name;
    return {
      serialNumber: String(serialIndex).padStart(3, '0'),
      name: issue.name,
      family: 'Issue',
      objectType: 'DesignIssue',
      domain: issue.domain ?? 'frontend',
      layer: 'instance',
      description: issue.description ?? issue.name,
      id: issue.id ?? `ISSUE-${serialIndex}`,
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
    const fixGuidance = [
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

    if (message.startsWith('Rendered preview content exceeds the viewport height')) {
      return {
        name: 'Viewport Navigation Issue',
        source: 'X-Ray Agent',
        category: 'Viewport Navigation',
        rule: 'Vertical Scroll Path',
      };
    }

    if (message.startsWith('Preview structural node renders as <')) {
      return {
        name: 'Preview-Tree Parity Issue',
        source: 'X-Ray Agent',
        category: 'Preview-Tree Parity',
        rule: 'HTML Tag Parity',
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
        ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_COMPONENT'].includes(relationship.relationshipType),
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
        ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_COMPONENT'].includes(relationship.relationshipType),
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

  private async collectXRayScanIssues() {
    const graph = this.state.graph();
    if (!graph) {
      return [];
    }

    const nodeMap = this.state.nodeIdMap();
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

    for (const [objectId, messages] of await this.buildStructuralInspectionViolations()) {
      const targetNode = nodeMap.get(objectId);
      if (!targetNode?.id) {
        continue;
      }
      for (const message of messages) {
        addIssue(targetNode.id, message);
      }
    }

    try {
      for (const screenNode of this.previewAuditScreenNodes(graph)) {
        await this.withRenderedPreviewAuditScreen(screenNode, (auditRoot) => {
          const resolver = this.previewDomResolverForAuditRoot(auditRoot);
          const screenElement = resolver.findElementForObjectId(screenNode.id);
          if (!screenElement) {
            return;
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
              issue.graphObjectId ?? screenNode.id,
              `WCAG ${issue.level}: ${issue.rule}. ${issue.message}`,
            );
          }
        });
      }
    } finally {
      await this.clearPreviewAuditRender();
    }

    return Array.from(deduped.values());
  }

  private async buildStructuralInspectionViolations(): Promise<Map<string, string[]>> {
    const graph = this.state.graph();
    if (!graph) {
      return new Map<string, string[]>();
    }

    const relevantFamilies = new Set(['Application', 'Shell', 'Container', 'Screen', 'Section', 'Component', 'ValidationRuleSet', 'ValidationRule', 'ViewportProfile']);
    const nodeById = new Map(
      graph.nodes
        .filter((node) => !!node.id)
        .map((node) => [node.id as string, node]),
    );
    const structuralRelationships = graph.relationships.filter((relationship) =>
      ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_COMPONENT'].includes(relationship.relationshipType),
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

    const addViolation = (objectId: string, message: string): void => {
      const current = violations.get(objectId) ?? [];
      if (!current.includes(message)) {
        current.push(message);
        violations.set(objectId, current);
      }
    };

    const blank = (value: string | null | undefined): boolean => !value || !value.trim();

    const validateSharedAttributes = (node: SystemShellGraphNode): void => {
      const objectId = node.id?.trim();
      if (!objectId) {
        return;
      }

      for (const [key, value] of [
        ['name', node.name],
        ['description', node.description],
        ['id', node.id],
        ['status', node.status],
        ['domain', node.domain],
      ] as const) {
        if (value === null || value === undefined || (typeof value === 'string' && !value.trim())) {
          addViolation(objectId, `${node.family} is missing required attribute: ${key}.`);
        }
      }
      if (['Shell', 'Screen', 'Container', 'Section', 'Component'].includes(node.family) && blank(node.guid)) {
        addViolation(objectId, `${node.family} is missing required attribute: guid.`);
      }
      if (node.family === 'Screen' && !blank(node.backgroundType)) {
        addViolation(objectId, 'Screen must not own shell background attributes.');
      }
    };

    for (const node of graph.nodes) {
      if (node.layer !== 'instance' || !relevantFamilies.has(node.family)) {
        continue;
      }

      const nodeId = node.id?.trim();
      if (!nodeId) {
        continue;
      }
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
          if (parents.length > 0) {
            addViolation(nodeId, 'Application must be the top structural object and cannot have a structural parent.');
          }
          if (children.length === 0) {
            addViolation(nodeId, 'Application must contain at least one Shell.');
          }
          for (const child of children) {
            if (child.family !== 'Shell') {
              addViolation(nodeId, `Application may contain Shell only; found ${child.family}.`);
            }
          }
          break;
        }
        case 'Shell': {
          if (parents.length !== 1 || parents[0]?.family !== 'Application') {
            addViolation(nodeId, 'Shell must have exactly one Application parent.');
          }
          if (children.length === 0) {
            addViolation(nodeId, 'Shell must contain at least one Container.');
          }
          for (const child of children) {
            if (child.family !== 'Container') {
              addViolation(nodeId, `Shell may contain Container only; found ${child.family}.`);
            }
          }
          break;
        }
        case 'Screen': {
          if (parents.length !== 1 || !this.isMainContainerNode(parents[0] ?? null)) {
            addViolation(nodeId, 'Screen must have exactly one Main Container parent.');
          }
          if (children.length === 0) {
            addViolation(nodeId, 'Screen should contain at least one Container.');
          }
          for (const child of children) {
            if (child.family !== 'Container') {
              addViolation(nodeId, `Screen may contain Container only; found ${child.family}.`);
            }
          }
          if (this.screenHasConditionalDescendants(nodeId) && usedRuleSets.length !== 1) {
            addViolation(nodeId, 'Screen with conditional UI nodes must use exactly one ValidationRuleSet.');
          }
          if (usedRuleSets.some((ruleSet) => ruleSet.family !== 'ValidationRuleSet')) {
            addViolation(nodeId, 'Screen may use ValidationRuleSet only.');
          }
          break;
        }
        case 'Container':
        case 'Section': {
          if (blank(node.sectionType)) {
            addViolation(nodeId, `${node.family} is missing required attribute: section_type.`);
          }
          if (blank(node.htmlTag)) {
            addViolation(nodeId, `${node.family} is missing required attribute: html_tag.`);
          }
          if (!blank(node.semanticLevel)) {
            addViolation(nodeId, `Semantic heading levels belong to Component, not ${node.family}.`);
          }
          if (node.repeatable === null) {
            addViolation(nodeId, `${node.family} is missing required attribute: repeatable.`);
          }
          if (blank(node.renderMode)) {
            addViolation(nodeId, `${node.family} is missing required attribute: render_mode.`);
          }
          if (blank(node.defaultState)) {
            addViolation(nodeId, `${node.family} is missing required attribute: default_state.`);
          }
          if (blank(node.controlSource)) {
            addViolation(nodeId, `${node.family} is missing required attribute: control_source.`);
          }
          if (node.renderMode === 'conditional' && node.controlSource !== 'validation_rule_set') {
            addViolation(nodeId, `Conditional ${node.family} must use control_source = validation_rule_set.`);
          }
          if (node.renderMode === 'static' && node.controlSource !== 'none') {
            addViolation(nodeId, `Static ${node.family} must use control_source = none.`);
          }
          if (node.repeatable && !this.isMeaningfulRepeatableSection(node)) {
            addViolation(nodeId, `Repeatable ${node.family} must represent a meaningful repeated pattern, not a generic container.`);
          }

          if (node.family === 'Container') {
            if (parents.length !== 1 || !['Shell', 'Screen'].includes(parents[0]?.family ?? '')) {
              addViolation(nodeId, 'Container must have exactly one Shell or Screen parent.');
            }
            if (node.htmlTag === 'section') {
              addViolation(nodeId, 'Container must not declare htmlTag = section.');
            }

            const hasScreenChildren = children.some((child) => child.family === 'Screen');
            const hasSectionChildren = children.some((child) => child.family === 'Section');
            const hasComponentChildren = children.some((child) => child.family === 'Component');

            if (this.isMainContainerNode(node)) {
              if (children.length === 0) {
                addViolation(nodeId, 'Main Container should contain at least one Screen.');
              }
              for (const child of children) {
                if (child.family !== 'Screen') {
                  addViolation(nodeId, `Main Container may contain Screen only; found ${child.family}.`);
                }
              }
            } else {
              const childModes = (hasScreenChildren ? 1 : 0) + (hasSectionChildren ? 1 : 0) + (hasComponentChildren ? 1 : 0);
              if (childModes > 1) {
                addViolation(nodeId, 'Container must not mix child screens, sections, and components at the same level.');
              }
              for (const child of children) {
                if (!['Section', 'Component'].includes(child.family)) {
                  addViolation(nodeId, `Container may contain Section or Component only; found ${child.family}.`);
                }
              }
              if (!node.repeatable && children.length === 1 && children[0].family === 'Section') {
                addViolation(nodeId, 'Container is an orphan single-child container and should be flattened.');
              }
            }
          } else {
            if (parents.length !== 1 || parents[0]?.family !== 'Container') {
              addViolation(nodeId, 'Section must have exactly one Container parent.');
            }
            if (node.htmlTag !== 'section') {
              addViolation(nodeId, 'Section must declare htmlTag = section.');
            }
            if (children.length === 0) {
              addViolation(nodeId, 'Section should contain at least one Component.');
            }
            for (const child of children) {
              if (child.family !== 'Component') {
                addViolation(nodeId, `Section may contain Component only; found ${child.family}.`);
              }
            }
          }
          break;
        }
        case 'Component': {
          if (parents.length !== 1 || !this.isStructuralContainerFamily(parents[0]?.family ?? null)) {
            addViolation(nodeId, 'Component instance must be attached to exactly one Container or Section parent.');
          }
          if (children.length > 0) {
            addViolation(nodeId, 'Component instance must remain a leaf.');
          }
          if (blank(node.elementType)) {
            addViolation(nodeId, 'Component is missing required attribute: element_type.');
          }
          if (node.elementType === 'title' && blank(node.semanticLevel)) {
            addViolation(nodeId, 'Title Component should define semantic_level.');
          }
          if (blank(node.renderMode)) {
            addViolation(nodeId, 'Component is missing required attribute: render_mode.');
          }
          if (blank(node.defaultState)) {
            addViolation(nodeId, 'Component is missing required attribute: default_state.');
          }
          if (blank(node.controlSource)) {
            addViolation(nodeId, 'Component is missing required attribute: control_source.');
          }
          if (node.renderMode === 'conditional' && node.controlSource !== 'validation_rule_set') {
            addViolation(nodeId, 'Conditional Component must use control_source = validation_rule_set.');
          }
          if (node.renderMode === 'static' && node.controlSource !== 'none') {
            addViolation(nodeId, 'Static Component must use control_source = none.');
          }
          if (blank(node.assetType)) {
            addViolation(nodeId, 'Component instance is missing asset_type.');
          }
          if (blank(node.assetName)) {
            addViolation(nodeId, 'Component instance is missing asset_name.');
          }
          const target = this.componentTargetNode(node);
          if (!target || !this.isStructuralContainerFamily(target.family)) {
            addViolation(nodeId, 'Component instance must target a Container or Section through HAS_COMPONENT.');
          } else if (parents[0] && target.id !== parents[0].id) {
            addViolation(nodeId, 'Component HAS_COMPONENT target must match its parent structural container.');
          }
          break;
        }
        case 'ValidationRuleSet': {
          if (owningScreens.length !== 1 || owningScreens[0]?.family !== 'Screen') {
            addViolation(nodeId, 'ValidationRuleSet must be referenced by exactly one Screen.');
          }
          if (blank(node.ruleSetType)) {
            addViolation(nodeId, 'ValidationRuleSet is missing required attribute: rule_set_type.');
          }
          if (blank(node.ruleSetScope)) {
            addViolation(nodeId, 'ValidationRuleSet is missing required attribute: rule_set_scope.');
          }
          if (owningScreens.length === 1
            && this.screenHasConditionalDescendants(owningScreens[0].id)
            && ownedRules.length === 0) {
            addViolation(nodeId, 'ValidationRuleSet must contain at least one ValidationRule when its screen has conditional UI nodes.');
          }
          for (const rule of ownedRules) {
            if (rule.family !== 'ValidationRule') {
              addViolation(nodeId, `ValidationRuleSet may contain ValidationRule only; found ${rule.family}.`);
            }
          }
          break;
        }
        case 'ValidationRule': {
          if (owningRuleSets.length !== 1 || owningRuleSets[0]?.family !== 'ValidationRuleSet') {
            addViolation(nodeId, 'ValidationRule must be owned by exactly one ValidationRuleSet.');
          }
          if (blank(node.conditionExpression)) {
            addViolation(nodeId, 'ValidationRule is missing required attribute: condition_expression.');
          }
          if (blank(node.actionType)) {
            addViolation(nodeId, 'ValidationRule is missing required attribute: action_type.');
          }
          if (node.priority === null || node.priority === undefined) {
            addViolation(nodeId, 'ValidationRule is missing required attribute: priority.');
          }
          if (node.stopProcessing === null || node.stopProcessing === undefined) {
            addViolation(nodeId, 'ValidationRule is missing required attribute: stop_processing.');
          }
          if (targets.length === 0) {
            addViolation(nodeId, 'ValidationRule must target at least one Screen, Container, Section, or Component.');
          }
          for (const target of targets) {
            if (!['Screen', 'Container', 'Section', 'Component'].includes(target.family)) {
              addViolation(nodeId, `ValidationRule may target Screen, Container, Section, or Component only; found ${target.family}.`);
            }
          }
          break;
        }
        case 'ViewportProfile': {
          if (!Number.isFinite(node.viewportWidth) || (node.viewportWidth ?? 0) <= 0) {
            addViolation(nodeId, 'ViewportProfile must define a positive viewportWidth.');
          }
          if (!Number.isFinite(node.viewportHeight) || (node.viewportHeight ?? 0) <= 0) {
            addViolation(nodeId, 'ViewportProfile must define a positive viewportHeight.');
          }
          if (blank(node.viewportCategory)) {
            addViolation(nodeId, 'ViewportProfile is missing required attribute: viewport_category.');
          }
          break;
        }
      }
    }

    for (const [objectId, messages] of await this.collectPreviewStructuralInspectionViolations(graph, nodeById, outgoing)) {
      for (const message of messages) {
        addViolation(objectId, message);
      }
    }

    return violations;
  }

  private async collectPreviewStructuralInspectionViolations(
    graph: SystemShellGraphResponse,
    nodeMap: Map<string, SystemShellGraphNode>,
    outgoing: Map<string, SystemShellGraphRelationship[]>,
  ): Promise<Map<string, string[]>> {
    const violations = new Map<string, string[]>();
    const addViolation = (objectId: string, message: string): void => {
      const current = violations.get(objectId) ?? [];
      if (!current.includes(message)) {
        current.push(message);
        violations.set(objectId, current);
      }
    };

    try {
      for (const screenNode of this.previewAuditScreenNodes(graph)) {
        await this.withRenderedPreviewAuditScreen(screenNode, (auditRoot, context) => {
          const renderedObjectIds = this.collectRenderedPreviewObjectIds(auditRoot);
          if (renderedObjectIds.size > 0) {
            for (const objectId of renderedObjectIds) {
              if (!nodeMap.has(objectId)) {
                const ownerObjectId = context.screenObjectId ?? context.shellObjectId;
                if (ownerObjectId) {
                  addViolation(ownerObjectId, `Preview audit host contains unmapped object ${objectId}.`);
                }
              }
            }

            for (const node of graph.nodes) {
              const nodeObjectId = node.id?.trim();
              if (!nodeObjectId || node.layer !== 'instance' || !['Shell', 'Container', 'Screen', 'Section', 'Component'].includes(node.family)) {
                continue;
              }
              if (!this.isNodeExpectedInAuditContext(node, context)) {
                continue;
              }
              if (node.renderMode === 'conditional' || node.defaultState === 'hidden') {
                continue;
              }
              if (this.hasConditionalHiddenAncestor(node)) {
                continue;
              }
              if (!renderedObjectIds.has(nodeObjectId)) {
                addViolation(nodeObjectId, 'Rendered preview does not expose this object via source-object-id (UUID binding).');
              }
            }
          }

          for (const [objectId, messages] of this.collectStructuralHtmlTagParityViolations(auditRoot, nodeMap)) {
            for (const message of messages) {
              addViolation(objectId, message);
            }
          }

          for (const [objectId, messages] of this.collectAnonymousIntermediateWrapperViolations(auditRoot, nodeMap, outgoing)) {
            for (const message of messages) {
              addViolation(objectId, message);
            }
          }

          for (const [objectId, messages] of this.collectComponentParityViolations(auditRoot, nodeMap, outgoing)) {
            for (const message of messages) {
              addViolation(objectId, message);
            }
          }

          for (const [objectId, messages] of this.collectRawHtmlArtifactViolations(auditRoot, nodeMap, context)) {
            for (const message of messages) {
              addViolation(objectId, message);
            }
          }

          for (const [objectId, messages] of this.collectPreviewTreeParityViolations(auditRoot, nodeMap, context)) {
            for (const message of messages) {
              addViolation(objectId, message);
            }
          }
        });
      }
    } finally {
      await this.clearPreviewAuditRender();
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

  private screenHasConditionalDescendants(screenObjectId: string | null | undefined): boolean {
    const normalizedScreenObjectId = screenObjectId?.trim();
    if (!normalizedScreenObjectId) {
      return false;
    }

    const nodeById = this.state.nodeIdMap();
    const screenNode = nodeById.get(normalizedScreenObjectId);
    if (!screenNode || screenNode.layer !== 'instance' || screenNode.family !== 'Screen') {
      return false;
    }

    const queue = [normalizedScreenObjectId];
    const visited = new Set<string>(queue);

    while (queue.length) {
      const currentId = queue.shift()!;
      for (const relationship of this.state.outgoingRelationshipMap().get(currentId) ?? []) {
        if (!['HAS_SCREEN', 'HAS_SECTION', 'HAS_COMPONENT'].includes(relationship.relationshipType) || visited.has(relationship.toId)) {
          continue;
        }

        visited.add(relationship.toId);
        queue.push(relationship.toId);

        const descendant = nodeById.get(relationship.toId);
        if (descendant && ['Container', 'Section', 'Component'].includes(descendant.family) && descendant.renderMode === 'conditional') {
          return true;
        }
      }
    }

    return false;
  }

  private previewAuditScreenNodes(graph: SystemShellGraphResponse): SystemShellGraphNode[] {
    return graph.nodes
      .filter((node) =>
        node.layer === 'instance'
        && node.family === 'Screen'
      )
      .sort((left, right) => this.sortGraphNodes(left, right));
  }

  private async withRenderedPreviewAuditScreen<T>(
    screenNode: SystemShellGraphNode,
    callback: (auditRoot: HTMLElement, context: PreviewAuditRenderContext) => T | Promise<T>,
  ): Promise<T | null> {
    const context = await this.renderPreviewAuditScreen(screenNode);
    if (!context) {
      return null;
    }

    const auditRoot = this.previewCanvasRootElement;
    if (!auditRoot) {
      return null;
    }

    return callback(auditRoot, context);
  }

  private async renderPreviewAuditScreen(screenNode: SystemShellGraphNode): Promise<PreviewAuditRenderContext | null> {
    const shellObjectId = this.resolveAssociatedShellId(screenNode.id);
    if (!shellObjectId) {
      return null;
    }

    const screenObjectId = screenNode.id?.trim() ?? null;
    this.auditPreviewShellObjectId.set(shellObjectId);
    this.auditPreviewScreenObjectId.set(screenObjectId);

    for (let attempt = 0; attempt < 10; attempt += 1) {
      await this.waitForPaint();
      const auditRoot = this.previewCanvasRootElement;
      if (!auditRoot) {
        continue;
      }
      if (!screenObjectId || auditRoot.querySelector(`[source-object-id="${screenObjectId}"]`)) {
        return {
          screenObjectId,
          shellObjectId,
        };
      }
    }

    return {
      screenObjectId,
      shellObjectId,
    };
  }

  private async clearPreviewAuditRender(): Promise<void> {
    this.auditPreviewScreenObjectId.set(null);
    this.auditPreviewShellObjectId.set(null);
    await this.waitForPaint();
  }

  private previewDomResolverForAuditRoot(
    auditRoot: HTMLElement,
  ): PreviewDomResolver {
    return new PreviewDomResolver(
      auditRoot,
      this.state.nodeIdMap(),
      this.state.nodeGuidMap(),
    );
  }

  private isNodeExpectedInAuditContext(
    node: SystemShellGraphNode,
    context: PreviewAuditRenderContext,
  ): boolean {
    if (!this.isNodeExpectedInPreviewAudit(node)) {
      return false;
    }

    const nodeObjectId = node.id?.trim() ?? null;
    if (!nodeObjectId) {
      return false;
    }

    if (context.screenObjectId && this.resolveAssociatedScreenId(nodeObjectId) === context.screenObjectId) {
      return true;
    }

    return !!context.shellObjectId && this.resolveAssociatedShellId(nodeObjectId) === context.shellObjectId;
  }

  private collectRenderedPreviewObjectIds(auditRoot: HTMLElement): Set<string> {
    return new Set(
      Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))
        .map((element) => this.sourceObjectIdFromElement(element))
        .filter((objectId): objectId is string => !!objectId),
    );
  }

  private collectStructuralHtmlTagParityViolations(
    auditRoot: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
  ): Map<string, string[]> {
    const violations = new Map<string, string[]>();

    for (const element of Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))) {
      const objectId = this.sourceObjectIdFromElement(element);
      if (!objectId) {
        continue;
      }

      const node = nodeMap.get(objectId);
      if (!node || node.layer !== 'instance' || !['Container', 'Section'].includes(node.family)) {
        continue;
      }

      const expectedTag = node.htmlTag?.trim().toLowerCase();
      const actualTag = element.tagName.toLowerCase();
      if (!expectedTag || expectedTag === actualTag) {
        continue;
      }

      violations.set(
        objectId,
        [`Preview structural node renders as <${actualTag}> but Neo4j records htmlTag = ${expectedTag}.`],
      );
    }

    return violations;
  }

  private collectAnonymousIntermediateWrapperViolations(
    auditRoot: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
    outgoing: Map<string, SystemShellGraphRelationship[]>,
  ): Map<string, string[]> {
    const violations = new Map<string, string[]>();

    for (const ownerElement of Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))) {
      const ownerObjectId = this.sourceObjectIdFromElement(ownerElement);
      if (!ownerObjectId) {
        continue;
      }

      const ownerNode = this.state.nodeIdMap().get(ownerObjectId);
      if (!ownerNode || ownerNode.layer !== 'instance' || !['Shell', 'Screen', 'Container', 'Section'].includes(ownerNode.family)) {
        continue;
      }

      const expectedChildFamilies = this.expectedStructuralChildFamilies(ownerNode);
      const directChildObjectIds = new Set(
        (outgoing.get(ownerObjectId) ?? [])
          .map((relationship) => this.state.nodeIdMap().get(relationship.toId))
          .filter((child): child is SystemShellGraphNode =>
            !!child
            && child.layer === 'instance'
            && expectedChildFamilies.includes(child.family),
          )
          .map((child) => child.id?.trim() ?? '')
          .filter((childObjectId) => !!childObjectId),
      );

      if (!directChildObjectIds.size) {
        continue;
      }

      const findings: string[] = [];

      for (const childElement of Array.from(ownerElement.children)) {
        if (!(childElement instanceof HTMLElement)) {
          continue;
        }

        if (this.sourceObjectIdFromElement(childElement)) {
          continue;
        }

        if (childElement.tagName.includes('-')) {
          continue;
        }

        const wrappedDirectChildObjectIds = Array.from(childElement.querySelectorAll<HTMLElement>('[source-object-id]'))
          .map((element) => this.sourceObjectIdFromElement(element) ?? '')
          .filter((objectId) => directChildObjectIds.has(objectId));

        const uniqueWrappedObjectIds = Array.from(new Set(wrappedDirectChildObjectIds));
        if (!uniqueWrappedObjectIds.length) {
          continue;
        }

        const wrappedNames = uniqueWrappedObjectIds
          .map((objectId) => nodeMap.get(objectId))
          .filter((node): node is SystemShellGraphNode => !!node)
          .map((node) => this.displayNodeName(node));

        findings.push(
          `Anonymous HTML wrapper ${this.describeAnonymousWrapper(childElement)} wraps direct child artifacts ${wrappedNames.join(', ')}`,
        );
      }

      if (findings.length) {
        violations.set(
          ownerObjectId,
          findings.map((finding) =>
            `${finding}. ${ownerNode.family} must render its direct child ${this.expectedStructuralChildFamilyLabel(ownerNode)} without an unmodeled intermediate wrapper.`,
          ),
        );
      }
    }

    return violations;
  }

  private collectComponentParityViolations(
    auditRoot: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
    outgoing: Map<string, SystemShellGraphRelationship[]>,
  ): Map<string, string[]> {
    const violations = new Map<string, string[]>();

    for (const renderedElement of Array.from(auditRoot.querySelectorAll<HTMLElement>('[source-object-id]'))) {
      const objectId = this.sourceObjectIdFromElement(renderedElement);
      if (!objectId) {
        continue;
      }

      const node = nodeMap.get(objectId);
      if (!node || node.layer !== 'instance' || node.family !== 'Component') {
        continue;
      }

      const expectedComponent = this.expectedPrimeComponentForElement(node, renderedElement);
      const hasRenderedPrimeNg = this.hasRenderedPrimeNgEvidence(renderedElement);

      if (!hasRenderedPrimeNg) {
        violations.set(objectId, [
          'Rendered component is implemented as plain HTML. Only Container or Section may remain a plain HTML structural wrapper; ' +
          'Component must render through a recorded PrimeNG component when a PrimeNG source is recorded.',
        ]);
        continue;
      }

      if (!expectedComponent) {
        continue;
      }

      if (!this.elementContainsComponentLikeEvidence(renderedElement, expectedComponent)) {
        continue;
      }

      violations.set(objectId, [
        `Rendered component uses plain HTML for a component-like UI artifact: expected PrimeNG ${expectedComponent}. ` +
        `This Component must be backed by its recorded PrimeNG source instead of HTML-only markup.`,
      ]);
    }

    return violations;
  }

  private collectRawHtmlArtifactViolations(
    auditRoot: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
    context: PreviewAuditRenderContext,
  ): Map<string, string[]> {
    const violations = new Map<string, string[]>();

    const addViolation = (violationKey: string, message: string): void => {
      const existing = violations.get(violationKey) ?? [];
      if (!existing.includes(message)) {
        existing.push(message);
        violations.set(violationKey, existing);
      }
    };

    for (const element of Array.from(auditRoot.querySelectorAll<HTMLElement>('*'))) {
      if (!this.isRawHtmlArtifactViolationCandidate(element, nodeMap)) {
        continue;
      }

      const ownerObjectId = this.resolveRawHtmlArtifactOwnerObjectId(element, nodeMap, context);
      if (!ownerObjectId) {
        continue;
      }

      const artifactDescription = this.describeRawHtmlArtifact(element);
      const textSnippet = this.rawHtmlArtifactTextSnippet(element);
      const textClause = textSnippet ? ` with text "${textSnippet}"` : '';
      addViolation(
        ownerObjectId,
        `Rendered non-container HTML artifact ${artifactDescription}${textClause} is not allowed. ` +
          'Only container HTML tags may remain native in preview.',
      );
    }

    return violations;
  }

  private collectPreviewTreeParityViolations(
    auditRoot: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
    context: PreviewAuditRenderContext,
  ): Map<string, string[]> {
    const violations = new Map<string, string[]>();

    const addViolation = (violationKey: string, message: string): void => {
      const existing = violations.get(violationKey) ?? [];
      if (!existing.includes(message)) {
        existing.push(message);
        violations.set(violationKey, existing);
      }
    };

    for (const element of Array.from(auditRoot.querySelectorAll<HTMLElement>('*'))) {
      if (!this.isPreviewTreeParityViolationCandidate(element, nodeMap)) {
        continue;
      }

      const ownerObjectId = this.resolveRawHtmlArtifactOwnerObjectId(element, nodeMap, context);
      if (!ownerObjectId) {
        continue;
      }

      const artifactDescription = this.describeRawHtmlArtifact(element);
      const textSnippet = this.rawHtmlArtifactTextSnippet(element);
      const textClause = textSnippet ? ` with text "${textSnippet}"` : '';
      addViolation(
        ownerObjectId,
        `Visible preview artifact ${artifactDescription}${textClause} is not represented as a Neo4j/tree object. ` +
          'Every visible preview artifact must be modeled and listed in the tree.',
      );
    }

    return violations;
  }

  private collectPreviewViewportNavigationViolations(
    nodeMap: Map<string, SystemShellGraphNode>,
  ): Map<string, string[]> {
    const stage = this.previewStageElement;
    if (!stage) {
      return new Map<string, string[]>();
    }

    const screenObjectId = this.activeScreenObjectId();
    const shellObjectId = this.activeShellObjectId();
    const resolver = this.previewDomResolver();
    const shellElement = resolver.findElementForObjectId(shellObjectId);
    const screenElement = resolver.findElementForObjectId(screenObjectId);
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
    const add = (objectId: string | null | undefined): void => {
      if (!objectId) {
        return;
      }
      const node = this.state.nodeIdMap().get(objectId);
      if (!node) {
        return;
      }
      violations.set(objectId, [message]);
    };

    add(screenObjectId);
    add(this.sourceObjectIdFromElement(viewportRoot));

    return violations;
  }

  private describeAnonymousWrapper(element: HTMLElement): string {
    const tag = element.tagName.toLowerCase();
    const classes = Array.from(element.classList)
      .map((className) => className.trim())
      .filter((className) => !!className)
      .slice(0, 3);

    return classes.length ? `<${tag} class="${classes.join(' ')}">` : `<${tag}>`;
  }

  private sourceObjectIdFromElement(element: HTMLElement | null): string | null {
    const sourceObjectId = this.boundPreviewElement(element)?.getAttribute('source-object-id')?.trim();
    return sourceObjectId || null;
  }

  private guidFromElement(element: HTMLElement | null): string | null {
    const guid = this.boundPreviewElement(element)?.getAttribute('guid')?.trim();
    return guid || null;
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

    const directObjectId = this.sourceObjectIdFromElement(element);
    if (directObjectId) {
      const directNode = nodeMap.get(directObjectId);
      if (directNode?.layer === 'instance' && directNode.family === 'Component') {
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
    if (tag.includes('-') && !tag.startsWith('p-')) {
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

    const directObjectId = this.sourceObjectIdFromElement(element);
    if (directObjectId && nodeMap.has(directObjectId)) {
      return false;
    }

    return true;
  }

  private resolveRawHtmlArtifactOwnerObjectId(
    element: HTMLElement,
    nodeMap: Map<string, SystemShellGraphNode>,
    context?: PreviewAuditRenderContext,
  ): string | null {
    const directObjectId = this.sourceObjectIdFromElement(element);
    if (directObjectId && nodeMap.has(directObjectId)) {
      return directObjectId;
    }

    const ancestor = element.parentElement?.closest<HTMLElement>('[source-object-id]');
    const ancestorObjectId = ancestor ? this.sourceObjectIdFromElement(ancestor) : null;
    if (ancestorObjectId && nodeMap.has(ancestorObjectId)) {
      return ancestorObjectId;
    }

    const screenObjectId = context?.screenObjectId ?? this.activeScreenObjectId();
    if (screenObjectId && nodeMap.has(screenObjectId)) {
      return screenObjectId;
    }

    const shellObjectId = context?.shellObjectId ?? this.activeShellObjectId();
    if (shellObjectId && nodeMap.has(shellObjectId)) {
      return shellObjectId;
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

  private isStructuralContainerFamily(family: string | null | undefined): boolean {
    return family === 'Container' || family === 'Section';
  }

  private isMainContainerNode(node: SystemShellGraphNode | null | undefined): boolean {
    return !!node && node.family === 'Container' && node.name === 'Main Container';
  }

  private expectedStructuralChildFamilies(ownerNode: SystemShellGraphNode): string[] {
    switch (ownerNode.family) {
      case 'Shell':
      case 'Screen':
        return ['Container'];
      case 'Section':
        return ['Component'];
      case 'Container':
        return this.isMainContainerNode(ownerNode) ? ['Screen'] : ['Section', 'Component'];
      default:
        return [];
    }
  }

  private expectedStructuralChildFamilyLabel(ownerNode: SystemShellGraphNode): string {
    return this.expectedStructuralChildFamilies(ownerNode)
      .map((family) => {
        switch (family) {
          case 'Section':
            return 'Sections';
          case 'Component':
            return 'Components';
          case 'Container':
            return 'Containers';
          case 'Screen':
            return 'Screens';
          default:
            return family;
        }
      })
      .join(' or ');
  }

  private isNodeExpectedInPreviewAudit(node: SystemShellGraphNode): boolean {
    return !!node.id && ['Shell', 'Container', 'Screen', 'Section', 'Component'].includes(node.family);
  }

  private hasConditionalHiddenAncestor(node: SystemShellGraphNode): boolean {
    const objectId = node.id?.trim();
    if (!objectId) {
      return false;
    }

    const visited = new Set<string>([objectId]);
    const queue = [objectId];
    while (queue.length) {
      const currentObjectId = queue.shift()!;
      for (const parent of this.structuralParentsOf(currentObjectId)) {
        const parentObjectId = parent.id?.trim();
        if (!parentObjectId || visited.has(parentObjectId)) {
          continue;
        }
        if (this.isStructuralContainerFamily(parent.family)
          && (parent.renderMode === 'conditional' || parent.defaultState === 'hidden')) {
          return true;
        }

        visited.add(parentObjectId);
        queue.push(parentObjectId);
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
        this.buildReadonlyAttributeRow(
          node.id ?? this.graphNodeRef(node),
          'Target Object',
          this.componentTargetNode(node)?.id ?? null,
        ),
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
          { label: 'Priority', attributeKey: 'priority', valueType: 'number' },
          { label: 'Stop Processing', attributeKey: 'stopProcessing', valueType: 'boolean' },
        ];
      case 'Container':
      case 'Section':
        return [
          { label: 'Section Type', attributeKey: 'sectionType', valueType: 'string' },
          { label: 'Layout Region', attributeKey: 'layoutRegion', valueType: 'string' },
          { label: 'HTML Tag', attributeKey: 'htmlTag', valueType: 'string' },
          { label: 'Display Mode', attributeKey: 'displayMode', valueType: 'string' },
          { label: 'Position Mode', attributeKey: 'positionMode', valueType: 'string' },
          { label: 'Top', attributeKey: 'top', valueType: 'string' },
          { label: 'Right', attributeKey: 'right', valueType: 'string' },
          { label: 'Bottom', attributeKey: 'bottom', valueType: 'string' },
          { label: 'Left', attributeKey: 'left', valueType: 'string' },
          { label: 'Width', attributeKey: 'width', valueType: 'string' },
          { label: 'Height', attributeKey: 'height', valueType: 'string' },
          { label: 'Min Width', attributeKey: 'minWidth', valueType: 'string' },
          { label: 'Min Height', attributeKey: 'minHeight', valueType: 'string' },
          { label: 'Max Width', attributeKey: 'maxWidth', valueType: 'string' },
          { label: 'Max Height', attributeKey: 'maxHeight', valueType: 'string' },
          { label: 'Margin Top', attributeKey: 'marginTop', valueType: 'string' },
          { label: 'Margin Right', attributeKey: 'marginRight', valueType: 'string' },
          { label: 'Margin Bottom', attributeKey: 'marginBottom', valueType: 'string' },
          { label: 'Margin Left', attributeKey: 'marginLeft', valueType: 'string' },
          { label: 'Gap', attributeKey: 'gap', valueType: 'string' },
          { label: 'Row Gap', attributeKey: 'rowGap', valueType: 'string' },
          { label: 'Column Gap', attributeKey: 'columnGap', valueType: 'string' },
          { label: 'Padding Top', attributeKey: 'paddingTop', valueType: 'string' },
          { label: 'Padding Right', attributeKey: 'paddingRight', valueType: 'string' },
          { label: 'Padding Bottom', attributeKey: 'paddingBottom', valueType: 'string' },
          { label: 'Padding Left', attributeKey: 'paddingLeft', valueType: 'string' },
          { label: 'Justify Content', attributeKey: 'justifyContent', valueType: 'string' },
          { label: 'Align Items', attributeKey: 'alignItems', valueType: 'string' },
          { label: 'Align Self', attributeKey: 'alignSelf', valueType: 'string' },
          { label: 'Flex Direction', attributeKey: 'flexDirection', valueType: 'string' },
          { label: 'Flex Wrap', attributeKey: 'flexWrap', valueType: 'string' },
          { label: 'Overflow X', attributeKey: 'overflowX', valueType: 'string' },
          { label: 'Overflow Y', attributeKey: 'overflowY', valueType: 'string' },
          { label: 'Z-Index', attributeKey: 'zIndex', valueType: 'number' },
          { label: 'Repeatable', attributeKey: 'repeatable', valueType: 'boolean' },
          { label: 'Render Mode', attributeKey: 'renderMode', valueType: 'string' },
          { label: 'Default State', attributeKey: 'defaultState', valueType: 'string' },
          { label: 'Control Source', attributeKey: 'controlSource', valueType: 'string' },
        ];
      case 'Component':
        return [
          { label: 'Element Type', attributeKey: 'elementType', valueType: 'string' },
          { label: 'Semantic Level', attributeKey: 'semanticLevel', valueType: 'string' },
          { label: 'Render Mode', attributeKey: 'renderMode', valueType: 'string' },
          { label: 'Default State', attributeKey: 'defaultState', valueType: 'string' },
          { label: 'Control Source', attributeKey: 'controlSource', valueType: 'string' },
          { label: 'PrimeNG Source', attributeKey: 'primeComponent', valueType: 'string' },
          { label: 'Token Families', attributeKey: 'tokenFamilies', valueType: 'array' },
          { label: 'Asset Name', attributeKey: 'assetName', valueType: 'string' },
          { label: 'Asset Type', attributeKey: 'assetType', valueType: 'string' },
        ];
      case 'Shell':
        return [
          { label: 'Display Mode', attributeKey: 'displayMode', valueType: 'string' },
          { label: 'Position Mode', attributeKey: 'positionMode', valueType: 'string' },
          { label: 'Width', attributeKey: 'width', valueType: 'string' },
          { label: 'Height', attributeKey: 'height', valueType: 'string' },
          { label: 'Gap', attributeKey: 'gap', valueType: 'string' },
          { label: 'Row Gap', attributeKey: 'rowGap', valueType: 'string' },
          { label: 'Column Gap', attributeKey: 'columnGap', valueType: 'string' },
          { label: 'Padding Left', attributeKey: 'paddingLeft', valueType: 'string' },
          { label: 'Padding Right', attributeKey: 'paddingRight', valueType: 'string' },
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
      Container: 'CON',
      Section: 'SEC',
      Component: 'CP',
      ViewportProfile: 'VP',
      ValidationRuleSet: 'RS',
      ValidationRule: 'VR',
    };

    return marks[family] ?? family.slice(0, 2).toUpperCase();
  }

  private displayNodeName(node: SystemShellGraphNode): string {
    const normalized = node.name?.trim() ?? '';
    return normalized || this.graphNodeRef(node);
  }

  private resolveAssociatedScreenId(objectId: string | null | undefined): string | null {
    const normalizedObjectId = objectId?.trim();
    if (!normalizedObjectId) {
      return null;
    }

    const idMap = this.state.nodeIdMap();
    const startNode = idMap.get(normalizedObjectId) ?? null;
    if (!startNode) {
      return null;
    }

    if (startNode.family === 'Screen') {
      return startNode.id?.trim() ?? null;
    }

    const primaryScreenObjectId = this.primaryScreenObjectIdForNode(startNode);
    if (primaryScreenObjectId) {
      return primaryScreenObjectId;
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
        return currentNode.id?.trim() ?? null;
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

  private resolvePreviewScreenObjectIdForSelection(
    objectId: string | null | undefined,
    preferredScreenObjectId: string | null,
  ): string | null {
    const startNode = this.resolveGraphNodeById(objectId);
    if (!startNode) {
      return null;
    }

    if (startNode.family === 'Application') {
      const preferredShellObjectId = preferredScreenObjectId
        ? this.resolveAssociatedShellId(preferredScreenObjectId)
        : null;
      return preferredShellObjectId
        ? this.preferredScreenObjectIdForShell(preferredShellObjectId, preferredScreenObjectId)
        : this.primaryScreenObjectIdForShellObjectId(this.defaultShellObjectId()) ?? this.defaultScreenObjectId();
    }

    if (startNode.family === 'Screen') {
      return startNode.id?.trim() ?? null;
    }

    if (!this.isShellFrameNode(startNode)) {
      return this.resolveAssociatedScreenId(startNode.id);
    }

    const shellObjectId = this.resolveAssociatedShellId(startNode.id);
    if (!shellObjectId) {
      return this.resolveAssociatedScreenId(startNode.id);
    }

    return this.preferredScreenObjectIdForShell(shellObjectId, preferredScreenObjectId)
      ?? this.primaryScreenObjectIdForShellObjectId(shellObjectId);
  }

  private primaryScreenObjectIdForNode(startNode: SystemShellGraphNode): string | null {
    if (!startNode.id) {
      return null;
    }

    if (startNode.family === 'Shell') {
      const mainContainer = this.structuralChildrenOf(startNode.id, 'HAS_SECTION')
        .find((child) => this.isStructuralContainerFamily(child.family) && child.name === 'Main Container');
      if (!mainContainer?.id) {
        return null;
      }

      const firstScreen = this.structuralChildrenOf(mainContainer.id, 'HAS_SCREEN')
        .find((child) => child.family === 'Screen');
      return firstScreen?.id?.trim() ?? null;
    }

    if (this.isStructuralContainerFamily(startNode.family) && startNode.name === 'Main Container') {
      const firstScreen = this.structuralChildrenOf(startNode.id, 'HAS_SCREEN')
        .find((child) => child.family === 'Screen');
      return firstScreen?.id?.trim() ?? null;
    }

    return null;
  }

  private primaryScreenObjectIdForShellObjectId(shellObjectId: string | null | undefined): string | null {
    const normalizedShellObjectId = shellObjectId?.trim();
    if (!normalizedShellObjectId) {
      return null;
    }

    const shellNode = this.state.nodeIdMap().get(normalizedShellObjectId);
    if (!shellNode || shellNode.family !== 'Shell') {
      return null;
    }

    return this.primaryScreenObjectIdForNode(shellNode);
  }

  private preferredScreenObjectIdForShell(
    shellObjectId: string | null | undefined,
    preferredScreenObjectId: string | null,
  ): string | null {
    const normalizedShellObjectId = shellObjectId?.trim();
    if (
      normalizedShellObjectId
      && preferredScreenObjectId
      && this.resolveAssociatedShellId(preferredScreenObjectId) === normalizedShellObjectId
    ) {
      return preferredScreenObjectId;
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
      .sort((left, right) => this.sortGraphNodes(left, right));
  }

  private sortGraphNodes(left: SystemShellGraphNode, right: SystemShellGraphNode): number {
    const leftSortOrder = left.sortOrder ?? Number.MAX_SAFE_INTEGER;
    const rightSortOrder = right.sortOrder ?? Number.MAX_SAFE_INTEGER;
    if (leftSortOrder !== rightSortOrder) {
      return leftSortOrder - rightSortOrder;
    }

    const leftName = left.name ?? '';
    const rightName = right.name ?? '';
    if (leftName !== rightName) {
      return leftName.localeCompare(rightName);
    }

    return (left.id ?? '').localeCompare(right.id ?? '');
  }

  private resolveAssociatedShellId(objectId: string | null | undefined): string | null {
    const normalizedObjectId = objectId?.trim();
    if (!normalizedObjectId) {
      return null;
    }

    const idMap = this.state.nodeIdMap();
    const startNode = idMap.get(normalizedObjectId) ?? null;
    if (!startNode) {
      return null;
    }

    if (startNode.family === 'Shell') {
      return startNode.id?.trim() ?? null;
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
        return currentNode.id?.trim() ?? null;
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

  private resolveGraphNodeById(objectId: string | null | undefined): SystemShellGraphNode | null {
    const normalizedObjectId = objectId?.trim();
    if (!normalizedObjectId) {
      return null;
    }

    return this.state.nodeIdMap().get(normalizedObjectId) ?? null;
  }

  private isShellFrameNode(node: SystemShellGraphNode | null | undefined): boolean {
    if (!node?.id) {
      return false;
    }

    if (node.family === 'Shell') {
      return true;
    }

    if (!['Container', 'Section', 'Component'].includes(node.family)) {
      return false;
    }

    return this.hasStructuralAncestorFamily(node.id, 'Shell') && !this.hasStructuralAncestorFamily(node.id, 'Screen');
  }

  private hasStructuralAncestorFamily(objectId: string, family: string): boolean {
    const visited = new Set<string>([objectId]);
    const queue = [objectId];

    while (queue.length) {
      const currentObjectId = queue.shift()!;
      for (const parent of this.structuralParentsOf(currentObjectId)) {
        if (!parent.id || visited.has(parent.id)) {
          continue;
        }
        if (parent.family === family) {
          return true;
        }

        visited.add(parent.id);
        queue.push(parent.id);
      }
    }

    return false;
  }

  private structuralParentsOf(objectId: string): SystemShellGraphNode[] {
    const graph = this.state.graph();
    if (!graph) {
      return [];
    }

    const nodeIdMap = this.state.nodeIdMap();
    return graph.relationships
      .filter((relationship) =>
        relationship.toId === objectId
        && ['HAS_SHELL', 'HAS_SCREEN', 'HAS_SECTION', 'HAS_COMPONENT'].includes(relationship.relationshipType),
      )
      .map((relationship) => nodeIdMap.get(relationship.fromId) ?? null)
      .filter((node): node is SystemShellGraphNode => !!node && node.layer === 'instance');
  }

  private resolveViewportProfile(viewportCategory: string | null): SystemShellGraphNode | null {
    if (!viewportCategory) {
      return null;
    }

    return (this.state.graph()?.nodes ?? []).find((node) =>
      node.family === 'ViewportProfile'
      && node.layer === 'instance'
      && node.viewportCategory === viewportCategory,
    ) ?? null;
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
      Container: 80,
      Section: 90,
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
        nodeId: node.id ?? this.graphNodeRef(node),
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

  private buildReadonlyAttributeRow(nodeId: string, label: string, value: unknown): AttributeRow {
    return {
      nodeId,
      label,
      attributeKey: 'description',
      valueType: 'string',
      rawValue: value,
      value: this.formatValue(value),
      editable: false,
      editorKind: 'readonly',
      options: [],
    };
  }

  private graphAttributeEditKey(nodeId: string, attributeKey: keyof SystemShellGraphNode): string {
    return `${nodeId}::${String(attributeKey)}`;
  }

  private graphNodeRef(
    node: Pick<SystemShellGraphNode, 'id' | 'guid' | 'name' | 'family'> | null | undefined,
  ): string {
    return node?.id?.trim()
      || node?.guid?.trim()
      || node?.name?.trim()
      || node?.family
      || 'unknown';
  }

  private resolveGraphNodeAttribute<K extends keyof SystemShellGraphNode>(
    node: SystemShellGraphNode,
    attributeKey: K,
  ): SystemShellGraphNode[K] {
    const override = this.graphAttributeValueOverrides()[this.graphAttributeEditKey(node.id ?? this.graphNodeRef(node), attributeKey)];
    if (override !== undefined) {
      return override as SystemShellGraphNode[K];
    }

    return node[attributeKey];
  }

  private clearAttributeDraft(row: AttributeRow): void {
    const key = this.graphAttributeEditKey(row.nodeId, row.attributeKey);
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
      case 'layoutRegion':
        return this.attributeOptions([
          'header',
          'breadcrumb',
          'main',
          'footer',
          'content',
          'section',
          'surface',
          'panel',
          'field',
          'action_bar',
          'step',
          'message',
          'provider',
          'support',
          'outcome',
          'logo',
        ]);
      case 'displayMode':
        return this.attributeOptions(['block', 'flex', 'grid', 'inline-flex', 'none']);
      case 'positionMode':
        return this.attributeOptions(['static', 'relative', 'absolute', 'fixed', 'sticky']);
      case 'justifyContent':
        return this.attributeOptions(['start', 'end', 'center', 'space-between', 'space-around', 'space-evenly', 'stretch']);
      case 'alignItems':
      case 'alignSelf':
        return this.attributeOptions(['auto', 'start', 'end', 'center', 'stretch', 'baseline']);
      case 'flexDirection':
        return this.attributeOptions(['row', 'column']);
      case 'flexWrap':
        return this.attributeOptions(['nowrap', 'wrap']);
      case 'overflowX':
      case 'overflowY':
        return this.attributeOptions(['visible', 'hidden', 'auto', 'scroll', 'clip']);
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
          label: 'Components Registry',
          family: 'Components Registry',
          layer: 'catalog',
          objectId: null,
          guid: null,
          domTargetGuid: null,
        },
        children: this.filteredRegistryItems().map((item) => ({
          key: item.id ?? item.assetType,
          label: item.assetName,
          expanded: false,
          selectable: true,
          data: {
            kind: 'registry-item',
            label: item.assetName,
            family: 'Component',
            layer: 'catalog',
            objectId: item.id,
            guid: null,
            domTargetGuid: null,
            registryKind: item.objectType,
            registryPackageName: item.packageName,
            registryPackageExport: item.packageExport,
            registryPackageVersion: item.packageVersion,
            registryIconPackage: item.iconPackage,
            registryThemePackage: item.themePackage,
            registryDescription: item.description || null,
            registryAssetType: item.assetType,
            registryObjectType: item.objectType,
            registryDefaultInstanceId: item.defaultInstanceId,
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
    this.accordionTargetObjectId.set(instance.targetObjectId ?? '');
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
    const stage = this.previewStageElement;
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

    const resolvedScale = scale > 0 ? scale : 1;
    this.previewScale.set(resolvedScale);
  }

  private bindPreviewStageObserver(): void {
    const stage = this.previewStageElement;
    if (!stage) {
      return;
    }

    this.previewResizeObserver?.disconnect();
    this.previewResizeObserver = new ResizeObserver(() => this.recalculatePreviewScale());
    this.previewResizeObserver.observe(stage);
    requestAnimationFrame(() => this.recalculatePreviewScale());
  }

  private bindPreviewInteractions(): void {
    const stage = this.previewStageElement;
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
      const hoveredPreviewGuid = hoveredNode?.guid?.trim() ?? null;
      this.ngZone.run(() => {
        this.previewHoveredObjectId.set(hoveredNode?.id?.trim() ?? null);
        this.hoveredPreviewGuid.set(hoveredPreviewGuid);
      });
    };

    this.previewLeaveListener = () => {
      this.ngZone.run(() => {
        this.previewHoveredObjectId.set(null);
        this.hoveredPreviewGuid.set(null);
      });
    };

    stage.addEventListener('click', this.previewClickListener);
    stage.addEventListener('mousemove', this.previewMoveListener);
    stage.addEventListener('mouseleave', this.previewLeaveListener);
  }

  private unbindPreviewInteractions(): void {
    const stage = this.previewStageElement;
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
      const activeShellObjectId = this.activeShellObjectId();
      return activeShellObjectId ? this.state.nodeIdMap().get(activeShellObjectId)?.guid?.trim() ?? null : null;
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

  private previewDomResolver(): PreviewDomResolver {
    return new PreviewDomResolver(
      this.previewStageElement ?? null,
      this.state.nodeIdMap(),
      this.state.nodeGuidMap(),
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
