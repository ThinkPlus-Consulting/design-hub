export interface SystemShellGraphNode {
  code: string;
  name: string;
  family: string;
  objectType: string | null;
  domain: 'business' | 'frontend' | 'backend' | string;
  layer: 'definition' | 'instance';
  description: string | null;
  hierarchyCode: string | null;
  id: string | null;
  guid: string | null;
  status: string | null;
  assetName: string | null;
  assetType: string | null;
  definitionCode: string | null;
  implementationSourcePath: string | null;
  configurationJson: string | null;
  renderMode: string | null;
  defaultState: string | null;
  controlSource: string | null;
  backgroundType: string | null;
  backgroundColorStyle: string | null;
  backgroundPatternKey: string | null;
  backgroundPatternOpacity: number | null;
  backgroundImagePath: string | null;
  viewportWidth: number | null;
  viewportHeight: number | null;
  viewportCategory: string | null;
  executionMethod: string | null;
  stepOrder: number | null;
  ruleScope: string | null;
  conditionExpression: string | null;
  executionEffect: string | null;
  blockerType: string | null;
  blockingEffect: string | null;
  sectionType: string | null;
  repeatable: boolean | null;
  elementType: string | null;
  semanticLevel: string | null;
  primeComponent: string | null;
  tokenFamilies: string[];
  issueSource: string | null;
  issueCategory: string | null;
  issueRule: string | null;
  issueSeverity: string | null;
  issuePrompt: string | null;
  ruleSetType: string | null;
  ruleSetScope: string | null;
  actionType: string | null;
  actionValue: string | null;
  priority: number | null;
  stopProcessing: boolean | null;
}

export interface ShellBackgroundConfig {
  backgroundType: string | null;
  backgroundColorStyle: string | null;
  backgroundPatternKey: string | null;
  backgroundPatternOpacity: number | null;
  backgroundImagePath: string | null;
}

export interface SystemShellGraphIssueNode {
  code: string;
  serialNumber: string;
  name: string;
  family: 'Issue';
  objectType: 'DesignIssue';
  domain: 'frontend' | string;
  layer: 'instance';
  description: string;
  hierarchyCode: string;
  id: string;
  status: 'open' | 'closed';
  severity: 'error';
  source: 'X-Ray Agent';
  category: 'Structure' | 'Container Styling' | 'Viewport Navigation' | 'Accessibility' | 'HTML Element Violation' | 'Preview-Tree Parity';
  rule: string;
  targetObjectId: string | null;
  targetName: string;
  message: string;
  prompt: string;
}

export interface SystemShellGraphIssueScanItemRequest {
  targetObjectId: string;
  targetName: string;
  source: string;
  category: string;
  rule: string;
  message: string;
  severity: string;
}

export interface SystemShellGraphIssueScanRequest {
  issues: SystemShellGraphIssueScanItemRequest[];
}

export interface SystemShellGraphIssueScanSummary {
  totalIssues: number;
  newIssues: number;
  existingIssues: number;
  resolvedByRetest: number;
}

export interface SystemShellGraphIssueStatusUpdateRequest {
  issueCodes: string[];
  status: 'open' | 'closed';
}

export interface SystemShellGraphIssueUpdateRequest {
  issuePrompt: string | null;
}

export interface SystemShellGraphRelationship {
  fromId: string;
  relationshipType: string;
  toId: string;
  activeName: string | null;
  passiveName: string | null;
}

export interface SystemShellGraphResponse {
  graphScope: string;
  scenarioCode: string;
  scenarioName: string;
  nodes: SystemShellGraphNode[];
  relationships: SystemShellGraphRelationship[];
}

export interface ComponentRegistryDefinition {
  code: string;
  objectType: string;
  assetType: string;
  assetName: string;
  description: string | null;
  id: string | null;
  status: string | null;
  implementationSourcePath: string | null;
  defaultInstanceCode: string | null;
}

export interface ComponentRegistryInstance {
  code: string;
  objectType: string;
  assetType: string;
  assetName: string;
  name: string | null;
  description: string | null;
  id: string | null;
  status: string | null;
  definitionCode: string | null;
  implementationSourcePath: string | null;
  targetObjectCode: string | null;
  targetObjectName: string | null;
  targetObjectType: string | null;
  configuration: Record<string, unknown>;
}

export interface ComponentRegistryInstanceUpdateRequest {
  name: string | null;
  description: string | null;
  status: string | null;
  targetObjectCode: string | null;
  configuration: Record<string, unknown>;
}

export interface SystemShellTreeNodeData {
  kind: 'graph' | 'navigation-root' | 'registry-root' | 'registry-item';
  code: string;
  label: string;
  family: string;
  layer: string;
  objectId: string | null;
  guid: string | null;
  domTargetGuid: string | null;
  assetType?: string | null;
  registryKind?: string | null;
  registryImplementationSourcePath?: string | null;
  registryDescription?: string | null;
  registryAssetType?: string | null;
  registryObjectType?: string | null;
  registryInstanceCode?: string | null;
}
