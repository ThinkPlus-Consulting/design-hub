export interface SystemShellGraphNode {
  name: string;
  family: string;
  objectType: string | null;
  domain: 'business' | 'frontend' | 'backend' | string;
  layer: 'definition' | 'instance';
  description: string | null;
  sortOrder: number | null;
  id: string | null;
  guid: string | null;
  status: string | null;
  assetName: string | null;
  assetType: string | null;
  definitionId: string | null;
  packageName: string | null;
  packageExport: string | null;
  packageVersion: string | null;
  iconPackage: string | null;
  themePackage: string | null;
  configurationJson: string | null;
  renderMode: string | null;
  defaultState: string | null;
  controlSource: string | null;
  layoutRegion: string | null;
  displayMode: string | null;
  positionMode: string | null;
  top: string | null;
  right: string | null;
  bottom: string | null;
  left: string | null;
  width: string | null;
  height: string | null;
  minWidth: string | null;
  minHeight: string | null;
  maxWidth: string | null;
  maxHeight: string | null;
  marginTop: string | null;
  marginRight: string | null;
  marginBottom: string | null;
  marginLeft: string | null;
  gap: string | null;
  rowGap: string | null;
  columnGap: string | null;
  paddingTop: string | null;
  paddingRight: string | null;
  paddingBottom: string | null;
  paddingLeft: string | null;
  justifyContent: string | null;
  alignItems: string | null;
  alignSelf: string | null;
  flexDirection: string | null;
  flexWrap: string | null;
  overflowX: string | null;
  overflowY: string | null;
  zIndex: number | null;
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
  htmlTag: string | null;
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

export interface SystemShellGraphRelationship {
  fromId: string;
  relationshipType: string;
  toId: string;
  activeName: string | null;
  passiveName: string | null;
}

export interface SystemShellTreeNodeData {
  kind: 'graph' | 'navigation-root' | 'registry-root' | 'registry-item';
  label: string;
  family: string;
  layer: string;
  objectId: string | null;
  guid: string | null;
  domTargetGuid: string | null;
  assetType?: string | null;
  registryKind?: string | null;
  registryPackageName?: string | null;
  registryPackageExport?: string | null;
  registryPackageVersion?: string | null;
  registryIconPackage?: string | null;
  registryThemePackage?: string | null;
  registryDescription?: string | null;
  registryAssetType?: string | null;
  registryObjectType?: string | null;
  registryDefaultInstanceId?: string | null;
}

export interface SystemShellGraphTreeNode {
  key: string;
  label: string;
  expanded: boolean;
  selectable: boolean;
  data: SystemShellTreeNodeData;
  children: SystemShellGraphTreeNode[];
}

export interface SystemShellGraphResponse {
  graphScope: string;
  scenarioCode: string;
  scenarioName: string;
  nodes: SystemShellGraphNode[];
  relationships: SystemShellGraphRelationship[];
  navigationTree: SystemShellGraphTreeNode[];
}
