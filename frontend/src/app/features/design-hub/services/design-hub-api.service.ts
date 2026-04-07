import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable, map } from 'rxjs';
import { environment } from '../../../../environments/environment';
import {
  AgentPack,
  AgentPackApplicationTarget,
  AgentPackCodeTarget,
  AgentPackCompleteness,
  AgentPackComponentTarget,
  AgentPackConvention,
  AgentPackPolicy,
  AgentPackQualityConstraint,
  AgentPackTestCase,
  ApplicationArchitecture,
  ApplicationArchitectureComponentSummary,
  ApplicationArchitectureDependencySummary,
  ApplicationSummary,
  BusinessArchitecture,
  BusinessArchitectureOrganization,
  BusinessCapabilitySummary,
  BenchmarkTypeScore,
  ChannelSummary,
  ChannelTraversal,
  ChannelTouchpointSummary,
  ChannelCoverageGap,
  DataArchitecture,
  DataArchitectureEntitySummary,
  DataArchitectureFlowSummary,
  DataArchitectureObjectSummary,
  DeliveryStory,
  DesignHubStats,
  ExternalArtifactDetail,
  ExternalParityAudit,
  ExternalSyncJobResult,
  ExternalSyncSourceStatus,
  ExternalArtifactLinkSummary,
  ExternalArtifactSummary,
  GraphBenchmark,
  GraphNodeReference,
  GraphNodeReferenceWithSummary,
  InfrastructureArchitecture,
  InfrastructureDeploymentSummary,
  InfrastructureNodeSummary,
  JourneyTraversal,
  JourneyTraversalStep,
  ObjectDefinitionAttribute,
  ObjectDefinitionDetail,
  ObjectDefinitionRelationship,
  ObjectDefinitionSummary,
  PersonaSummary,
  PersonaJourneySummary,
  PersonaTraversal,
  ReadinessDiagnostics,
  RoleSummary,
  Screen,
  StoryTraceability,
  UserStorySummary,
} from '../../../models';
import { Touchpoint } from '../../../models';
import { Interaction } from '../../../models';
import { Journey } from '../../../models';

interface RawGap {
  type?: 'warning' | 'info' | 'error';
  severity?: string;
  description?: string | null;
}

interface RawContentElement {
  element?: string;
  type?: string;
  description?: string;
}

interface RawScreenTransition {
  surfaceId?: string;
}

interface RawScreen {
  surfaceId?: string;
  label?: string;
  module?: string;
  routePath?: string | null;
  storyRefs?: string[];
  stories?: RawUserStory[];
  roleKeys?: string[];
  roles?: RawRole[];
  personaIds?: string[];
  designStatus?: 'COMPLETE' | 'SPECIFIED' | 'NOT_STARTED';
  prototypeStatus?: 'PROTOTYPED' | 'NOT_STARTED';
  deliveryStatus?: 'INTEGRATED' | 'TESTED' | 'NOT_STARTED';
  wcag?: string;
  responsive?: boolean;
  roleAdaptive?: boolean;
  deepLinkable?: boolean;
  loadingStates?: boolean;
  messageRegistryCount?: number;
  notes?: string | null;
  gaps?: RawGap[];
  contentElements?: RawContentElement[];
  transitionsTo?: RawScreenTransition[];
}

interface RawEntryMode {
  channelId?: string;
  mechanism?: string;
}

interface RawTouchpoint {
  touchpointId?: string;
  label?: string;
  surfaceId?: string;
  personaIds?: string[];
  roleKeys?: string[];
  entryModes?: RawEntryMode[];
}

interface RawEffect {
  type?: Interaction['effects'][number]['type'];
  target?: string | null;
  targetMode?: Interaction['effects'][number]['targetMode'];
  resolutionRule?: string;
  defaultTarget?: string;
}

interface RawInteraction {
  interactionId?: string;
  surfaceId?: string;
  element?: string;
  trigger?: string;
  permission?: string | null;
  confirmationCode?: string | null;
  personaIds?: string[];
  roleKeys?: string[];
  apiCalls?: string[];
  effects?: RawEffect[];
}

interface RawJourneyStep {
  stepId?: string;
  interactionRef?: string | null;
  label?: string;
  preCondition?: string;
  postCondition?: string;
  orderIndex?: number;
}

interface RawJourney {
  journeyId?: string;
  title?: string;
  personaId?: string | null;
  roleKey?: string | null;
  goalStatement?: string;
  sourceRefs?: string[];
  designStatus?: Journey['designStatus'];
  prototypeStatus?: Journey['prototypeStatus'];
  deliveryStatus?: Journey['deliveryStatus'];
  steps?: RawJourneyStep[];
}

interface RawStats {
  totalScreens?: number;
  designComplete?: number;
  designSpecified?: number;
  designNotStarted?: number;
  designCompletePercent?: number;
}

interface RawRole {
  roleKey?: string;
  displayName?: string;
  roleGroup?: string | null;
  sortOrder?: number | null;
  screenCount?: number;
  touchpointCount?: number;
  interactionCount?: number;
  journeyCount?: number;
}

interface RawUserStory {
  storyId?: string;
  label?: string;
  module?: string | null;
  domain?: string | null;
  storyNumber?: string | null;
  screenCount?: number;
  externalWorkflowState?: string | null;
  externalPriority?: string | null;
  externalOwner?: string | null;
  externalLabels?: string[];
  externalRefs?: string[];
}

interface RawDeliveryFeature {
  featureId?: string;
  title?: string;
  status?: string | null;
}

interface RawDeliveryScreen {
  surfaceId?: string;
  label?: string;
  routePath?: string | null;
  status?: string | null;
}

interface RawDeliveryApi {
  contractId?: string;
  method?: string | null;
  path?: string | null;
  status?: string | null;
}

interface RawDeliveryBug {
  bugId?: string;
  externalKey?: string | null;
  summary?: string | null;
  severity?: string | null;
  status?: string | null;
}

interface RawDeliveryFinding {
  findingId?: string;
  summary?: string | null;
  status?: string | null;
}

interface RawDeliveryGap {
  gapId?: string;
  gapType?: string | null;
  severity?: string | null;
  description?: string | null;
  status?: string | null;
}

interface RawDeliveryExternalArtifact {
  externalId?: string;
  system?: string | null;
  externalType?: string | null;
  key?: string | null;
  title?: string | null;
  projectScope?: string | null;
  workflowState?: string | null;
  priority?: string | null;
  owner?: string | null;
  reporter?: string | null;
  labels?: string[];
  url?: string | null;
  syncStatus?: string | null;
  status?: string | null;
}

interface RawDeliveryDiagnostics {
  artifactType?: string;
  artifactId?: string;
  status?: string | null;
  readiness?: Record<string, boolean>;
  completenessScore?: number;
  completenessLevel?: string;
  missingBlockingRules?: string[];
  missingOptionalRules?: string[];
  missingArtifacts?: string[];
  advisoryRulesViolated?: string[];
}

type RawReadinessDiagnostics = RawDeliveryDiagnostics;

interface RawDeliveryStory {
  storyId?: string;
  label?: string;
  module?: string | null;
  domain?: string | null;
  storyNumber?: string | null;
  status?: string | null;
  ready?: boolean;
  feature?: RawDeliveryFeature | null;
  screens?: RawDeliveryScreen[];
  apis?: RawDeliveryApi[];
  bugs?: RawDeliveryBug[];
  findings?: RawDeliveryFinding[];
  gaps?: RawDeliveryGap[];
  externalArtifacts?: RawDeliveryExternalArtifact[];
  diagnostics?: RawDeliveryDiagnostics | null;
}

interface RawExternalArtifactSummary {
  externalId?: string;
  system?: string | null;
  externalType?: string | null;
  key?: string | null;
  title?: string | null;
  projectScope?: string | null;
  workflowState?: string | null;
  priority?: string | null;
  owner?: string | null;
  reporter?: string | null;
  labels?: string[];
  customFields?: Record<string, string> | null;
  url?: string | null;
  syncStatus?: string | null;
  lastSyncedAt?: string | null;
  status?: string | null;
  representedObjectCount?: number;
  childCount?: number;
  dependencyCount?: number;
  relatedCount?: number;
}

interface RawExternalArtifactLinkSummary {
  externalId?: string;
  system?: string | null;
  externalType?: string | null;
  key?: string | null;
  title?: string | null;
  workflowState?: string | null;
  syncStatus?: string | null;
  status?: string | null;
}

interface RawExternalArtifactDetail {
  externalId?: string;
  system?: string | null;
  externalType?: string | null;
  key?: string | null;
  title?: string | null;
  projectScope?: string | null;
  workflowState?: string | null;
  priority?: string | null;
  owner?: string | null;
  reporter?: string | null;
  labels?: string[];
  customFields?: Record<string, string> | null;
  url?: string | null;
  syncStatus?: string | null;
  lastSyncedAt?: string | null;
  status?: string | null;
  parents?: RawExternalArtifactLinkSummary[];
  children?: RawExternalArtifactLinkSummary[];
  dependencies?: RawExternalArtifactLinkSummary[];
  relatedArtifacts?: RawExternalArtifactLinkSummary[];
  duplicates?: RawExternalArtifactLinkSummary[];
  representedObjects?: RawGraphNodeReference[];
}

interface RawExternalParityAuditSummary {
  totalArtifacts?: number;
  trackedFields?: number;
  overallCoverageScore?: number;
  status?: string;
  hierarchyArtifacts?: number;
  dependencyArtifacts?: number;
  relatedArtifacts?: number;
  duplicateArtifacts?: number;
}

interface RawExternalParityAuditSystem {
  system?: string;
  artifactCount?: number;
  coverageScore?: number;
  hierarchyArtifacts?: number;
  dependencyArtifacts?: number;
  weakestFields?: string[];
}

interface RawExternalParityAuditField {
  field?: string;
  populatedArtifacts?: number;
  missingArtifacts?: number;
  coverageScore?: number;
  exampleMissingArtifacts?: string[];
}

interface RawExternalSyncLatestJobSummary {
  jobId?: string;
  status?: string;
  receivedAt?: string;
  requestedBy?: string | null;
  triggerRef?: string | null;
  transportMode?: string | null;
}

interface RawExternalSyncSourceStatus {
  sourceSystem?: string;
  enabled?: boolean;
  webhookEnabled?: boolean;
  webhookSecretConfigured?: boolean;
  pollingEnabled?: boolean;
  pollingConfigured?: boolean;
  baseUrlConfigured?: boolean;
  pollPathConfigured?: boolean;
  scopeConfigured?: boolean;
  filterConfigured?: boolean;
  tokenConfigured?: boolean;
  schedulerEnabled?: boolean;
  pollingDryRun?: boolean;
  latestJob?: RawExternalSyncLatestJobSummary | null;
}

interface RawExternalSyncJobResult {
  jobId?: string | null;
  sourceSystem?: string;
  transportMode?: string;
  requestedBy?: string | null;
  receivedAt?: string | null;
  triggerRef?: string | null;
  dryRun?: boolean;
  status?: string;
  artifactCount?: number;
  warnings?: string[];
}

interface RawExternalParityAudit {
  summary?: RawExternalParityAuditSummary | null;
  systems?: RawExternalParityAuditSystem[];
  fields?: RawExternalParityAuditField[];
}

interface RawGraphNodeReference {
  id?: string;
  nodeType?: string;
  displayName?: string;
  status?: string | null;
  module?: string | null;
  domain?: string | null;
  routePath?: string | null;
  relationCount?: number;
}

interface RawObjectDefinitionSummary {
  type?: string;
  label?: string;
  displayName?: string;
  category?: string;
  tier?: string;
  benchmarkable?: boolean;
  instanceCount?: number;
  relationshipTypeCount?: number;
}

interface RawObjectDefinitionAttribute {
  name?: string;
  type?: string;
  required?: boolean;
  description?: string;
  constraints?: string;
}

interface RawObjectDefinitionRelationship {
  name?: string;
  direction?: string;
  target?: string;
  cardinality?: string;
  required?: boolean;
  severity?: string;
  implementation?: string;
}

interface RawObjectDefinitionDetail {
  type?: string;
  label?: string;
  displayName?: string;
  category?: string;
  tier?: string;
  benchmarkable?: boolean;
  purpose?: string;
  implementationStatus?: string;
  aliases?: string[];
  attributes?: RawObjectDefinitionAttribute[];
  relationships?: RawObjectDefinitionRelationship[];
  instanceCount?: number;
  instances?: RawGraphNodeReference[];
}

interface RawAgentPackCompleteness {
  complete?: boolean;
  missingConcerns?: string[];
  missingFields?: string[];
  readinessScore?: number;
}

interface RawAgentPackComponentTarget {
  id?: string;
  nodeType?: string;
  displayName?: string;
  status?: string | null;
  applicationId?: string | null;
  applicationName?: string | null;
  frameworkFamily?: string | null;
  frameworkName?: string | null;
  frameworkVersion?: string | null;
  runtime?: string | null;
  language?: string | null;
  languageVersion?: string | null;
  modulePath?: string | null;
  manifestPath?: string | null;
  buildCommand?: string | null;
  testCommand?: string | null;
  entrypointPath?: string | null;
  localRunCommand?: string | null;
  secretPrerequisites?: string[];
  fixturePrerequisites?: string[];
  localRunPrerequisites?: string[];
}

interface RawAgentPackApplicationTarget {
  id?: string;
  name?: string | null;
  applicationType?: string | null;
  workspaceType?: string | null;
  repoPath?: string | null;
  repoUrl?: string | null;
  defaultBuildCommand?: string | null;
  defaultTestCommand?: string | null;
  bootstrapSteps?: string[];
}

interface RawAgentPackCodeTarget {
  id?: string;
  nodeType?: string;
  displayName?: string;
  status?: string | null;
  assetType?: string | null;
  filePath?: string | null;
  language?: string | null;
  layerType?: string | null;
  changePolicy?: string | null;
  componentId?: string | null;
  componentName?: string | null;
  applicationId?: string | null;
  applicationName?: string | null;
}

interface RawAgentPackTestCase {
  id?: string;
  displayName?: string;
  status?: string | null;
  testType?: string | null;
  testCommand?: string | null;
  testFilePath?: string | null;
  locatedInId?: string | null;
  locatedInPath?: string | null;
}

interface RawAgentPackPolicy {
  id?: string;
  name?: string | null;
  allowedRepos?: string[];
  allowedCommands?: string[];
  forbiddenCommands?: string[];
  allowedEnvironments?: string[];
  secretScopes?: string[];
  maxFilesTouched?: number | null;
  requiresHumanApproval?: boolean | null;
  approvalThreshold?: string | null;
}

interface RawAgentPackConvention {
  id?: string;
  name?: string | null;
  category?: string | null;
  enforcement?: string | null;
  scope?: string | null;
  docRef?: string | null;
  activeStatus?: string | null;
}

interface RawAgentPackQualityConstraint {
  id?: string;
  name?: string | null;
  constraintType?: string | null;
  priority?: string | null;
  threshold?: string | null;
  status?: string | null;
}

interface RawAgentPack {
  packId?: string;
  packVersion?: number;
  generatedAt?: string;
  story?: RawGraphNodeReference | null;
  completeness?: RawAgentPackCompleteness | null;
  readinessChecks?: Record<string, boolean>;
  tasks?: RawGraphNodeReference[];
  deliveredScreens?: RawGraphNodeReference[];
  deliveredApis?: RawGraphNodeReference[];
  deliveredEntities?: RawGraphNodeReference[];
  applications?: RawAgentPackApplicationTarget[];
  components?: RawAgentPackComponentTarget[];
  codeTargets?: RawAgentPackCodeTarget[];
  testCases?: RawAgentPackTestCase[];
  policies?: RawAgentPackPolicy[];
  conventions?: RawAgentPackConvention[];
  qualityConstraints?: RawAgentPackQualityConstraint[];
}

interface RawStoryTraceability {
  objective?: RawGraphNodeReference | null;
  portfolio?: RawGraphNodeReference | null;
  epic?: RawGraphNodeReference | null;
  feature?: RawGraphNodeReference | null;
  story?: RawGraphNodeReference | null;
  screens?: RawGraphNodeReference[];
  interactions?: RawGraphNodeReference[];
  apis?: RawGraphNodeReference[];
  dataEntities?: RawGraphNodeReference[];
  messages?: RawGraphNodeReference[];
  tasks?: RawGraphNodeReference[];
  missingSpineSegments?: string[];
}

interface RawBenchmarkDimensionScore {
  dimension?: string;
  score?: number;
  status?: string;
  detail?: string;
}

interface RawBenchmarkSummary {
  scopeNote?: string;
  coveredNodeTypes?: number;
  totalNodes?: number;
  overallScore?: number;
  dimensions?: RawBenchmarkDimensionScore[];
}

interface RawBenchmarkTypeScore {
  nodeType?: string;
  totalNodes?: number;
  targetAttributeCount?: number;
  attributeDepthScore?: number;
  targetRelationshipCount?: number;
  relationshipCoverageScore?: number;
  sourceTraceabilityApplicable?: boolean;
  sourceTraceabilityScore?: number | null;
  queryabilityScore?: number;
  overallScore?: number;
  gapRecommendations?: string[];
}

interface RawGraphBenchmark {
  summary?: RawBenchmarkSummary | null;
  types?: RawBenchmarkTypeScore[];
}

interface RawChannelSummary {
  channelCode?: string;
  displayName?: string;
  channelType?: string | null;
  touchpointCount?: number;
  screenCount?: number;
}

interface RawChannelTouchpointSummary {
  touchpointId?: string;
  label?: string;
  surfaceId?: string | null;
  entryMechanisms?: string[];
  roleKeys?: string[];
  personaIds?: string[];
  targetScreen?: RawGraphNodeReference | null;
}

interface RawChannelCoverageGap {
  touchpointId?: string;
  reason?: string;
}

interface RawChannelTraversal {
  channelCode?: string;
  displayName?: string;
  channelType?: string | null;
  touchpoints?: RawChannelTouchpointSummary[];
  screens?: RawGraphNodeReference[];
  coverageGaps?: RawChannelCoverageGap[];
  personaReach?: RawGraphNodeReference[];
}

interface RawBusinessCapabilitySummary {
  capabilityId?: string;
  name?: string;
  domainCode?: string | null;
  domainName?: string | null;
  processCount?: number;
  applicationCount?: number;
  featureCount?: number;
  organizationCount?: number;
}

interface RawBusinessArchitectureOrganization {
  orgId?: string;
  name?: string;
  organizationType?: string | null;
  status?: string | null;
}

interface RawBusinessArchitecture {
  capabilityId?: string;
  name?: string;
  description?: string | null;
  status?: string | null;
  domainCode?: string | null;
  domainName?: string | null;
  processes?: RawGraphNodeReference[];
  applications?: RawGraphNodeReference[];
  features?: RawGraphNodeReference[];
  organizations?: RawBusinessArchitectureOrganization[];
}

interface RawApplicationSummary {
  applicationId?: string;
  name?: string;
  applicationType?: string | null;
  status?: string | null;
  componentCount?: number;
  apiCount?: number;
  screenCount?: number;
  featureCount?: number;
  dependencyCount?: number;
  ownerNames?: string[];
}

interface RawApplicationArchitectureComponent {
  componentId?: string;
  name?: string;
  componentType?: string | null;
  frameworkFamily?: string | null;
  runtime?: string | null;
  modulePath?: string | null;
  status?: string | null;
  apis?: RawGraphNodeReference[];
  screens?: RawGraphNodeReference[];
  dependencies?: RawGraphNodeReference[];
}

interface RawApplicationArchitectureDependency {
  applicationId?: string;
  name?: string;
  direction?: string | null;
  status?: string | null;
}

interface RawApplicationArchitecture {
  applicationId?: string;
  name?: string;
  description?: string | null;
  applicationType?: string | null;
  status?: string | null;
  ownerNames?: string[];
  components?: RawApplicationArchitectureComponent[];
  apis?: RawGraphNodeReference[];
  screens?: RawGraphNodeReference[];
  features?: RawGraphNodeReference[];
  dependencies?: RawApplicationArchitectureDependency[];
}

interface RawDataArchitectureObjectSummary {
  objectId?: string;
  name?: string;
  domain?: string | null;
  sensitivity?: string | null;
  status?: string | null;
  mappedEntityCount?: number;
  flowCount?: number;
  apiCount?: number;
  screenCount?: number;
}

interface RawDataArchitectureEntity {
  entityId?: string;
  name?: string;
  entityType?: string | null;
  fieldCount?: number;
  status?: string | null;
}

interface RawDataArchitectureFlow {
  flowId?: string;
  name?: string;
  direction?: string | null;
  status?: string | null;
  sourceApplicationId?: string | null;
  sourceApplicationName?: string | null;
  targetApplicationId?: string | null;
  targetApplicationName?: string | null;
}

interface RawDataArchitecture {
  objectId?: string;
  name?: string;
  domain?: string | null;
  description?: string | null;
  sensitivity?: string | null;
  status?: string | null;
  entities?: RawDataArchitectureEntity[];
  flows?: RawDataArchitectureFlow[];
  apis?: RawGraphNodeReference[];
  screens?: RawGraphNodeReference[];
  children?: RawGraphNodeReference[];
}

interface RawInfrastructureDeploymentSummary {
  deploymentId?: string;
  name?: string;
  environment?: string | null;
  status?: string | null;
  componentCount?: number;
  applicationCount?: number;
  infrastructureCount?: number;
}

interface RawInfrastructureNode {
  nodeId?: string;
  name?: string;
  nodeType?: string | null;
  location?: string | null;
  status?: string | null;
}

interface RawInfrastructureArchitecture {
  deploymentId?: string;
  name?: string;
  environment?: string | null;
  description?: string | null;
  status?: string | null;
  components?: RawGraphNodeReference[];
  infrastructureNodes?: RawInfrastructureNode[];
  applications?: RawGraphNodeReference[];
  elements?: RawGraphNodeReference[];
}

interface RawPersonaJourneySummary {
  journeyId?: string;
  title?: string;
  status?: string | null;
  stepCount?: number;
  screenCount?: number;
}

interface RawPersonaTraversal {
  personaId?: string;
  name?: string;
  summary?: string | null;
  status?: string | null;
  roleKeys?: string[];
  journeys?: RawPersonaJourneySummary[];
  roles?: RawGraphNodeReference[];
  channelReach?: RawGraphNodeReference[];
  screenCount?: number;
  storyCount?: number;
}

interface RawPersonaSummary {
  personaId?: string;
  name?: string;
  summary?: string | null;
  status?: string | null;
  journeyCount?: number;
  screenCount?: number;
  storyCount?: number;
  channelCount?: number;
}

interface RawJourneyTraversalStep {
  stepId?: string;
  label?: string;
  orderIndex?: number;
  screen?: RawGraphNodeReference | null;
  touchpoint?: RawGraphNodeReference | null;
  interaction?: RawGraphNodeReference | null;
}

interface RawJourneyTraversal {
  journeyId?: string;
  title?: string;
  goalStatement?: string;
  status?: string | null;
  persona?: RawGraphNodeReference | null;
  steps?: RawJourneyTraversalStep[];
}

@Injectable({ providedIn: 'root' })
export class DesignHubApiService {
  private readonly http = inject(HttpClient);
  private readonly baseUrl = `${environment.apiBaseUrl}/api/v1/design-hub`;
  private readonly deliveryUrl = `${environment.apiBaseUrl}/api/v1/delivery`;
  private readonly externalSyncUrl = `${environment.apiBaseUrl}/api/v1/external-sync`;
  private readonly graphUrl = `${environment.apiBaseUrl}/api/v1/graph`;
  private readonly readinessUrl = `${environment.apiBaseUrl}/api/v1/readiness`;
  private readonly storiesUrl = `${environment.apiBaseUrl}/api/v1/stories`;

  getScreens(): Observable<Screen[]> {
    return this.http
      .get<RawScreen[]>(`${this.baseUrl}/screens`)
      .pipe(map((screens) => screens.map((screen) => this.adaptScreen(screen))));
  }

  getScreen(surfaceId: string): Observable<Screen> {
    return this.http
      .get<RawScreen>(`${this.baseUrl}/screens/${surfaceId}`)
      .pipe(map((screen) => this.adaptScreen(screen)));
  }

  getFilteredScreens(module?: string, status?: string): Observable<Screen[]> {
    let params = new HttpParams();
    if (module && module !== 'all') {
      params = params.set('module', module);
    }
    if (status && status !== 'all') {
      params = params.set('status', status);
    }
    return this.http
      .get<RawScreen[]>(`${this.baseUrl}/screens/filtered`, { params })
      .pipe(map((screens) => screens.map((screen) => this.adaptScreen(screen))));
  }

  getTouchpoints(): Observable<Touchpoint[]> {
    return this.http
      .get<RawTouchpoint[]>(`${this.baseUrl}/touchpoints`)
      .pipe(map((touchpoints) => touchpoints.map((touchpoint) => this.adaptTouchpoint(touchpoint))));
  }

  getInteractions(): Observable<Interaction[]> {
    return this.http
      .get<RawInteraction[]>(`${this.baseUrl}/interactions`)
      .pipe(map((interactions) => interactions.map((interaction) => this.adaptInteraction(interaction))));
  }

  getInteractionsBySurface(surfaceId: string): Observable<Interaction[]> {
    return this.http
      .get<RawInteraction[]>(`${this.baseUrl}/interactions/by-screen/${surfaceId}`)
      .pipe(map((interactions) => interactions.map((interaction) => this.adaptInteraction(interaction))));
  }

  getJourneys(): Observable<Journey[]> {
    return this.http
      .get<RawJourney[]>(`${this.baseUrl}/journeys`)
      .pipe(map((journeys) => journeys.map((journey) => this.adaptJourney(journey))));
  }

  getRoles(): Observable<RoleSummary[]> {
    return this.http
      .get<RawRole[]>(`${this.baseUrl}/roles`)
      .pipe(map((roles) => roles.map((role) => this.adaptRole(role))));
  }

  getStories(): Observable<UserStorySummary[]> {
    return this.http
      .get<RawUserStory[]>(`${this.baseUrl}/stories`)
      .pipe(map((stories) => stories.map((story) => this.adaptStory(story))));
  }

  getDeliveryStories(): Observable<DeliveryStory[]> {
    return this.http
      .get<RawDeliveryStory[]>(`${this.deliveryUrl}/stories`)
      .pipe(map((stories) => stories.map((story) => this.adaptDeliveryStory(story))));
  }

  getStoryTraceability(storyId: string): Observable<StoryTraceability> {
    return this.http
      .get<RawStoryTraceability>(`${this.graphUrl}/traceability/stories/${storyId}`)
      .pipe(map((traceability) => this.adaptStoryTraceability(traceability)));
  }

  getAgentPack(storyId: string): Observable<AgentPack> {
    return this.http
      .get<RawAgentPack>(`${this.storiesUrl}/${storyId}/agent-pack`)
      .pipe(map((pack) => this.adaptAgentPack(pack)));
  }

  getBenchmark(): Observable<GraphBenchmark> {
    return this.http
      .get<RawGraphBenchmark>(`${this.graphUrl}/benchmark`)
      .pipe(map((benchmark) => this.adaptBenchmark(benchmark)));
  }

  getObjectDefinitions(): Observable<ObjectDefinitionSummary[]> {
    return this.http
      .get<RawObjectDefinitionSummary[]>(`${this.graphUrl}/object-definitions`)
      .pipe(map((definitions) => definitions.map((definition) => this.adaptObjectDefinitionSummary(definition))));
  }

  getObjectDefinition(type: string): Observable<ObjectDefinitionDetail> {
    return this.http
      .get<RawObjectDefinitionDetail>(`${this.graphUrl}/object-definitions/${encodeURIComponent(type)}`)
      .pipe(map((definition) => this.adaptObjectDefinitionDetail(definition)));
  }

  getExternalArtifacts(system?: string, syncStatus?: string): Observable<ExternalArtifactSummary[]> {
    let params = new HttpParams();
    if (system) {
      params = params.set('system', system);
    }
    if (syncStatus) {
      params = params.set('syncStatus', syncStatus);
    }
    return this.http
      .get<RawExternalArtifactSummary[]>(`${this.graphUrl}/external-artifacts`, { params })
      .pipe(map((artifacts) => artifacts.map((artifact) => this.adaptExternalArtifactSummary(artifact))));
  }

  getExternalArtifact(externalId: string): Observable<ExternalArtifactDetail> {
    return this.http
      .get<RawExternalArtifactDetail>(`${this.graphUrl}/external-artifacts/${externalId}`)
      .pipe(map((artifact) => this.adaptExternalArtifactDetail(artifact)));
  }

  getExternalParityAudit(): Observable<ExternalParityAudit> {
    return this.http
      .get<RawExternalParityAudit>(`${this.graphUrl}/external-artifacts/parity-audit`)
      .pipe(map((audit) => this.adaptExternalParityAudit(audit)));
  }

  getExternalSyncSourceStatuses(): Observable<ExternalSyncSourceStatus[]> {
    return this.http
      .get<RawExternalSyncSourceStatus[]>(`${this.externalSyncUrl}/sources`)
      .pipe(map((statuses) => statuses.map((status) => this.adaptExternalSyncSourceStatus(status))));
  }

  getExternalSyncJobs(limit = 10, sourceSystem?: string): Observable<ExternalSyncJobResult[]> {
    let params = new HttpParams().set('limit', String(limit));
    if (sourceSystem) {
      params = params.set('sourceSystem', sourceSystem);
    }
    return this.http
      .get<RawExternalSyncJobResult[]>(`${this.externalSyncUrl}/jobs`, { params })
      .pipe(map((jobs) => jobs.map((job) => this.adaptExternalSyncJobResult(job))));
  }

  triggerExternalSyncPoll(
    sourceSystem: string,
    options?: { dryRun?: boolean; requestedBy?: string; triggerRef?: string }
  ): Observable<ExternalSyncJobResult> {
    let params = new HttpParams();
    if (options?.dryRun !== undefined) {
      params = params.set('dryRun', String(options.dryRun));
    }
    if (options?.requestedBy) {
      params = params.set('requestedBy', options.requestedBy);
    }
    if (options?.triggerRef) {
      params = params.set('triggerRef', options.triggerRef);
    }

    return this.http
      .post<RawExternalSyncJobResult>(`${this.externalSyncUrl}/jobs/poll/${encodeURIComponent(sourceSystem)}`, null, { params })
      .pipe(map((job) => this.adaptExternalSyncJobResult(job)));
  }

  getPersonas(status?: string): Observable<PersonaSummary[]> {
    let params = new HttpParams();
    if (status) {
      params = params.set('status', status);
    }
    return this.http
      .get<RawPersonaSummary[]>(`${this.graphUrl}/personas`, { params })
      .pipe(map((personas) => personas.map((persona) => this.adaptPersonaSummary(persona))));
  }

  getPersonaTraversal(personaId: string): Observable<PersonaTraversal> {
    return this.http
      .get<RawPersonaTraversal>(`${this.graphUrl}/personas/${personaId}`)
      .pipe(map((persona) => this.adaptPersonaTraversal(persona)));
  }

  getJourneyTraversal(journeyId: string): Observable<JourneyTraversal> {
    return this.http
      .get<RawJourneyTraversal>(`${this.graphUrl}/journeys/${journeyId}`)
      .pipe(map((journey) => this.adaptJourneyTraversal(journey)));
  }

  getChannels(channelType?: string): Observable<ChannelSummary[]> {
    let params = new HttpParams();
    if (channelType) {
      params = params.set('channelType', channelType);
    }
    return this.http
      .get<RawChannelSummary[]>(`${this.graphUrl}/channels`, { params })
      .pipe(map((channels) => channels.map((channel) => this.adaptChannelSummary(channel))));
  }

  getChannelTraversal(channelCode: string): Observable<ChannelTraversal> {
    return this.http
      .get<RawChannelTraversal>(`${this.graphUrl}/channels/${channelCode}`)
      .pipe(map((channel) => this.adaptChannelTraversal(channel)));
  }

  getBusinessCapabilities(domain?: string): Observable<BusinessCapabilitySummary[]> {
    let params = new HttpParams();
    if (domain) {
      params = params.set('domain', domain);
    }
    return this.http
      .get<RawBusinessCapabilitySummary[]>(`${this.graphUrl}/architecture/business/capabilities`, { params })
      .pipe(map((capabilities) => capabilities.map((capability) => this.adaptBusinessCapabilitySummary(capability))));
  }

  getBusinessArchitecture(capabilityId: string): Observable<BusinessArchitecture> {
    return this.http
      .get<RawBusinessArchitecture>(`${this.graphUrl}/architecture/business/capabilities/${capabilityId}`)
      .pipe(map((architecture) => this.adaptBusinessArchitecture(architecture)));
  }

  getApplications(applicationType?: string): Observable<ApplicationSummary[]> {
    let params = new HttpParams();
    if (applicationType) {
      params = params.set('applicationType', applicationType);
    }
    return this.http
      .get<RawApplicationSummary[]>(`${this.graphUrl}/architecture/applications`, { params })
      .pipe(map((applications) => applications.map((application) => this.adaptApplicationSummary(application))));
  }

  getApplicationArchitecture(applicationId: string): Observable<ApplicationArchitecture> {
    return this.http
      .get<RawApplicationArchitecture>(`${this.graphUrl}/architecture/applications/${applicationId}`)
      .pipe(map((architecture) => this.adaptApplicationArchitecture(architecture)));
  }

  getDataObjects(domain?: string): Observable<DataArchitectureObjectSummary[]> {
    let params = new HttpParams();
    if (domain) {
      params = params.set('domain', domain);
    }
    return this.http
      .get<RawDataArchitectureObjectSummary[]>(`${this.graphUrl}/architecture/data/business-objects`, { params })
      .pipe(map((objects) => objects.map((object) => this.adaptDataObjectSummary(object))));
  }

  getDataArchitecture(objectId: string): Observable<DataArchitecture> {
    return this.http
      .get<RawDataArchitecture>(`${this.graphUrl}/architecture/data/business-objects/${objectId}`)
      .pipe(map((architecture) => this.adaptDataArchitecture(architecture)));
  }

  getInfrastructureDeployments(environment?: string): Observable<InfrastructureDeploymentSummary[]> {
    let params = new HttpParams();
    if (environment) {
      params = params.set('environment', environment);
    }
    return this.http
      .get<RawInfrastructureDeploymentSummary[]>(`${this.graphUrl}/architecture/infrastructure/deployments`, { params })
      .pipe(map((deployments) => deployments.map((deployment) => this.adaptInfrastructureDeploymentSummary(deployment))));
  }

  getInfrastructureArchitecture(deploymentId: string): Observable<InfrastructureArchitecture> {
    return this.http
      .get<RawInfrastructureArchitecture>(`${this.graphUrl}/architecture/infrastructure/deployments/${deploymentId}`)
      .pipe(map((architecture) => this.adaptInfrastructureArchitecture(architecture)));
  }

  getScreenReadiness(surfaceId: string): Observable<ReadinessDiagnostics> {
    return this.http
      .get<RawReadinessDiagnostics>(`${this.readinessUrl}/screens/${surfaceId}`)
      .pipe(map((diagnostics) => this.adaptReadinessDiagnostics(diagnostics)));
  }

  getStoryReadiness(storyId: string): Observable<ReadinessDiagnostics> {
    return this.http
      .get<RawReadinessDiagnostics>(`${this.readinessUrl}/stories/${storyId}`)
      .pipe(map((diagnostics) => this.adaptReadinessDiagnostics(diagnostics)));
  }

  getStats(): Observable<DesignHubStats> {
    return this.http
      .get<RawStats>(`${this.baseUrl}/stats`)
      .pipe(
        map((stats) => ({
          totalScreens: stats.totalScreens ?? 0,
          completeCount: stats.designComplete ?? 0,
          specifiedCount: stats.designSpecified ?? 0,
          notStartedCount: stats.designNotStarted ?? 0,
          totalGaps: 0,
          coveragePercent: stats.designCompletePercent ?? 0,
        }))
      );
  }

  saveNotes(surfaceId: string, text: string): Observable<Screen> {
    return this.http
      .put<RawScreen>(`${this.baseUrl}/screens/${surfaceId}/notes`, { text })
      .pipe(map((screen) => this.adaptScreen(screen)));
  }

  getNotes(surfaceId: string): Observable<{ text: string }> {
    return this.http.get<{ text: string }>(`${this.baseUrl}/screens/${surfaceId}/notes`);
  }

  private adaptScreen(screen: RawScreen): Screen {
    return {
      surfaceId: screen.surfaceId ?? '',
      label: screen.label ?? '',
      module: screen.module ?? '',
      routePath: screen.routePath ?? null,
      storyRefs: screen.storyRefs ?? [],
      stories: (screen.stories ?? []).map((story) => this.adaptStory(story)),
      uxSpecRef: '',
      roleKeys: screen.roleKeys ?? [],
      roles: (screen.roles ?? []).map((role) => this.adaptRole(role)),
      personaIds: screen.personaIds ?? [],
      designStatus: screen.designStatus ?? 'NOT_STARTED',
      prototypeStatus: screen.prototypeStatus ?? 'NOT_STARTED',
      deliveryStatus: screen.deliveryStatus ?? 'NOT_STARTED',
      crossCutting: {
        wcag: screen.wcag ?? 'N/A',
        responsive: screen.responsive ?? false,
        roleAdaptive: screen.roleAdaptive ?? false,
        deepLinkable: screen.deepLinkable ?? false,
        loadingStates: screen.loadingStates ?? false,
        messageRegistryCount: screen.messageRegistryCount ?? 0,
      },
      gapRefs: [],
      sourceRefs: [],
      notes: screen.notes ?? undefined,
      _legacy: {
        stories: screen.storyRefs ?? [],
        errorCodes: [],
        confirmations: [],
        emptyState: false,
        transitions: (screen.transitionsTo ?? [])
          .map((transition) => transition.surfaceId)
          .filter((surfaceId): surfaceId is string => Boolean(surfaceId)),
        gaps: (screen.gaps ?? []).map((gap) => ({
          type: gap.type ?? 'info',
          severity: gap.severity ?? '',
          desc: gap.description ?? '',
        })),
        content: (screen.contentElements ?? []).map((content) => ({
          element: content.element ?? '',
          type: content.type ?? '',
          description: content.description ?? '',
        })),
      },
    };
  }

  private adaptTouchpoint(touchpoint: RawTouchpoint): Touchpoint {
    return {
      touchpointId: touchpoint.touchpointId ?? '',
      label: touchpoint.label ?? '',
      surfaceId: touchpoint.surfaceId ?? '',
      personaIds: touchpoint.personaIds ?? [],
      roleKeys: touchpoint.roleKeys ?? [],
      entryModes: (touchpoint.entryModes ?? []).map((entryMode) => ({
        channelId: entryMode.channelId ?? 'unknown',
        mechanism: entryMode.mechanism ?? '',
      })),
      journeyStepRefs: [],
      sourceRefs: [],
    };
  }

  private adaptInteraction(interaction: RawInteraction): Interaction {
    return {
      interactionId: interaction.interactionId ?? '',
      surfaceId: interaction.surfaceId ?? '',
      element: interaction.element ?? '',
      trigger: interaction.trigger ?? '',
      permission: interaction.permission ?? null,
      personaIds: interaction.personaIds ?? [],
      roleKeys: interaction.roleKeys ?? [],
      effects: (interaction.effects ?? []).map((effect) => ({
        type: effect.type ?? 'toast',
        target: effect.target ?? effect.defaultTarget ?? null,
        targetMode: effect.targetMode ?? 'static',
        resolutionRule: effect.resolutionRule,
        defaultTarget: effect.defaultTarget,
      })),
      apiCalls: interaction.apiCalls ?? [],
      outcomes: {
        success: null,
        error: null,
        loading: null,
      },
      confirmationCode: interaction.confirmationCode ?? null,
      journeyStepRefs: [],
      sourceRefs: [],
    };
  }

  private adaptJourney(journey: RawJourney): Journey {
    return {
      journeyId: journey.journeyId ?? '',
      title: journey.title ?? '',
      personaId: journey.personaId ?? null,
      roleKey: journey.roleKey ?? null,
      goalStatement: journey.goalStatement ?? '',
      sourceRefs: journey.sourceRefs ?? [],
      designStatus: journey.designStatus ?? 'NOT_STARTED',
      prototypeStatus: journey.prototypeStatus ?? 'NOT_STARTED',
      deliveryStatus: journey.deliveryStatus ?? 'NOT_STARTED',
      steps: [...(journey.steps ?? [])]
        .sort((a, b) => (a.orderIndex ?? 0) - (b.orderIndex ?? 0))
        .map((step) => ({
          stepId: step.stepId ?? '',
          interactionRef: step.interactionRef ?? null,
          label: step.label ?? '',
          preCondition: step.preCondition ?? '',
          postCondition: step.postCondition ?? '',
        })),
    };
  }

  private adaptRole(role: RawRole): RoleSummary {
    return {
      roleKey: role.roleKey ?? '',
      displayName: role.displayName ?? role.roleKey ?? '',
      roleGroup: role.roleGroup ?? null,
      sortOrder: role.sortOrder ?? null,
      screenCount: role.screenCount ?? 0,
      touchpointCount: role.touchpointCount ?? 0,
      interactionCount: role.interactionCount ?? 0,
      journeyCount: role.journeyCount ?? 0,
    };
  }

  private adaptStory(story: RawUserStory): UserStorySummary {
    return {
      storyId: story.storyId ?? '',
      label: story.label ?? story.storyId ?? '',
      module: story.module ?? null,
      domain: story.domain ?? null,
      storyNumber: story.storyNumber ?? null,
      screenCount: story.screenCount ?? 0,
      externalWorkflowState: story.externalWorkflowState ?? null,
      externalPriority: story.externalPriority ?? null,
      externalOwner: story.externalOwner ?? null,
      externalLabels: story.externalLabels ?? [],
      externalRefs: story.externalRefs ?? [],
    };
  }

  private adaptDeliveryStory(story: RawDeliveryStory): DeliveryStory {
    return {
      storyId: story.storyId ?? '',
      label: story.label ?? story.storyId ?? '',
      module: story.module ?? null,
      domain: story.domain ?? null,
      storyNumber: story.storyNumber ?? null,
      status: story.status ?? null,
      ready: story.ready ?? false,
      feature: story.feature
        ? {
            featureId: story.feature.featureId ?? '',
            title: story.feature.title ?? story.feature.featureId ?? '',
            status: story.feature.status ?? null,
          }
        : null,
      screens: (story.screens ?? []).map((screen) => ({
        surfaceId: screen.surfaceId ?? '',
        label: screen.label ?? screen.surfaceId ?? '',
        routePath: screen.routePath ?? null,
        status: screen.status ?? null,
      })),
      apis: (story.apis ?? []).map((api) => ({
        contractId: api.contractId ?? '',
        method: api.method ?? null,
        path: api.path ?? null,
        status: api.status ?? null,
      })),
      bugs: (story.bugs ?? []).map((bug) => ({
        bugId: bug.bugId ?? '',
        externalKey: bug.externalKey ?? null,
        summary: bug.summary ?? null,
        severity: bug.severity ?? null,
        status: bug.status ?? null,
      })),
      findings: (story.findings ?? []).map((finding) => ({
        findingId: finding.findingId ?? '',
        summary: finding.summary ?? null,
        status: finding.status ?? null,
      })),
      gaps: (story.gaps ?? []).map((gap) => ({
        gapId: gap.gapId ?? '',
        gapType: gap.gapType ?? null,
        severity: gap.severity ?? null,
        description: gap.description ?? null,
        status: gap.status ?? null,
      })),
      externalArtifacts: (story.externalArtifacts ?? []).map((artifact) => ({
        externalId: artifact.externalId ?? '',
        system: artifact.system ?? null,
        externalType: artifact.externalType ?? null,
        key: artifact.key ?? null,
        title: artifact.title ?? artifact.key ?? artifact.externalId ?? null,
        projectScope: artifact.projectScope ?? null,
        workflowState: artifact.workflowState ?? null,
        priority: artifact.priority ?? null,
        owner: artifact.owner ?? null,
        reporter: artifact.reporter ?? null,
        labels: artifact.labels ?? [],
        url: artifact.url ?? null,
        syncStatus: artifact.syncStatus ?? null,
        status: artifact.status ?? null,
      })),
      diagnostics: story.diagnostics
        ? this.adaptReadinessDiagnostics(story.diagnostics)
        : null,
    };
  }

  private adaptReadinessDiagnostics(diagnostics: RawReadinessDiagnostics): ReadinessDiagnostics {
    return {
      artifactType: diagnostics.artifactType ?? '',
      artifactId: diagnostics.artifactId ?? '',
      status: diagnostics.status ?? null,
      readiness: diagnostics.readiness ?? {},
      completenessScore: diagnostics.completenessScore ?? 0,
      completenessLevel: diagnostics.completenessLevel ?? 'RED',
      missingBlockingRules: diagnostics.missingBlockingRules ?? [],
      missingOptionalRules: diagnostics.missingOptionalRules ?? [],
      missingArtifacts: diagnostics.missingArtifacts ?? [],
      advisoryRulesViolated: diagnostics.advisoryRulesViolated ?? [],
    };
  }

  private adaptExternalArtifactSummary(artifact: RawExternalArtifactSummary): ExternalArtifactSummary {
    return {
      externalId: artifact.externalId ?? '',
      system: artifact.system ?? null,
      externalType: artifact.externalType ?? null,
      key: artifact.key ?? null,
      title: artifact.title ?? artifact.key ?? artifact.externalId ?? null,
      projectScope: artifact.projectScope ?? null,
      workflowState: artifact.workflowState ?? null,
      priority: artifact.priority ?? null,
      owner: artifact.owner ?? null,
      reporter: artifact.reporter ?? null,
      labels: artifact.labels ?? [],
      url: artifact.url ?? null,
      syncStatus: artifact.syncStatus ?? null,
      lastSyncedAt: artifact.lastSyncedAt ?? null,
      status: artifact.status ?? null,
      representedObjectCount: artifact.representedObjectCount ?? 0,
      childCount: artifact.childCount ?? 0,
      dependencyCount: artifact.dependencyCount ?? 0,
      relatedCount: artifact.relatedCount ?? 0,
    };
  }

  private adaptExternalArtifactLink(link: RawExternalArtifactLinkSummary): ExternalArtifactLinkSummary {
    return {
      externalId: link.externalId ?? '',
      system: link.system ?? null,
      externalType: link.externalType ?? null,
      key: link.key ?? null,
      title: link.title ?? link.key ?? link.externalId ?? null,
      workflowState: link.workflowState ?? null,
      syncStatus: link.syncStatus ?? null,
      status: link.status ?? null,
    };
  }

  private adaptExternalArtifactDetail(artifact: RawExternalArtifactDetail): ExternalArtifactDetail {
    return {
      externalId: artifact.externalId ?? '',
      system: artifact.system ?? null,
      externalType: artifact.externalType ?? null,
      key: artifact.key ?? null,
      title: artifact.title ?? artifact.key ?? artifact.externalId ?? null,
      projectScope: artifact.projectScope ?? null,
      workflowState: artifact.workflowState ?? null,
      priority: artifact.priority ?? null,
      owner: artifact.owner ?? null,
      reporter: artifact.reporter ?? null,
      labels: artifact.labels ?? [],
      customFields: artifact.customFields ?? {},
      url: artifact.url ?? null,
      syncStatus: artifact.syncStatus ?? null,
      lastSyncedAt: artifact.lastSyncedAt ?? null,
      status: artifact.status ?? null,
      parents: (artifact.parents ?? []).map((link) => this.adaptExternalArtifactLink(link)),
      children: (artifact.children ?? []).map((link) => this.adaptExternalArtifactLink(link)),
      dependencies: (artifact.dependencies ?? []).map((link) => this.adaptExternalArtifactLink(link)),
      relatedArtifacts: (artifact.relatedArtifacts ?? []).map((link) => this.adaptExternalArtifactLink(link)),
      duplicates: (artifact.duplicates ?? []).map((link) => this.adaptExternalArtifactLink(link)),
      representedObjects: (artifact.representedObjects ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
    };
  }

  private adaptExternalParityAudit(audit: RawExternalParityAudit): ExternalParityAudit {
    return {
      summary: {
        totalArtifacts: audit.summary?.totalArtifacts ?? 0,
        trackedFields: audit.summary?.trackedFields ?? 0,
        overallCoverageScore: audit.summary?.overallCoverageScore ?? 0,
        status: audit.summary?.status ?? 'RED',
        hierarchyArtifacts: audit.summary?.hierarchyArtifacts ?? 0,
        dependencyArtifacts: audit.summary?.dependencyArtifacts ?? 0,
        relatedArtifacts: audit.summary?.relatedArtifacts ?? 0,
        duplicateArtifacts: audit.summary?.duplicateArtifacts ?? 0,
      },
      systems: (audit.systems ?? []).map((system) => ({
        system: system.system ?? 'UNKNOWN',
        artifactCount: system.artifactCount ?? 0,
        coverageScore: system.coverageScore ?? 0,
        hierarchyArtifacts: system.hierarchyArtifacts ?? 0,
        dependencyArtifacts: system.dependencyArtifacts ?? 0,
        weakestFields: system.weakestFields ?? [],
      })),
      fields: (audit.fields ?? []).map((field) => ({
        field: field.field ?? '',
        populatedArtifacts: field.populatedArtifacts ?? 0,
        missingArtifacts: field.missingArtifacts ?? 0,
        coverageScore: field.coverageScore ?? 0,
        exampleMissingArtifacts: field.exampleMissingArtifacts ?? [],
      })),
    };
  }

  private adaptExternalSyncSourceStatus(status: RawExternalSyncSourceStatus): ExternalSyncSourceStatus {
    return {
      sourceSystem: status.sourceSystem ?? '',
      enabled: status.enabled ?? false,
      webhookEnabled: status.webhookEnabled ?? false,
      webhookSecretConfigured: status.webhookSecretConfigured ?? false,
      pollingEnabled: status.pollingEnabled ?? false,
      pollingConfigured: status.pollingConfigured ?? false,
      baseUrlConfigured: status.baseUrlConfigured ?? false,
      pollPathConfigured: status.pollPathConfigured ?? false,
      scopeConfigured: status.scopeConfigured ?? false,
      filterConfigured: status.filterConfigured ?? false,
      tokenConfigured: status.tokenConfigured ?? false,
      schedulerEnabled: status.schedulerEnabled ?? false,
      pollingDryRun: status.pollingDryRun ?? true,
      latestJob: status.latestJob
        ? {
            jobId: status.latestJob.jobId ?? '',
            status: status.latestJob.status ?? 'UNKNOWN',
            receivedAt: status.latestJob.receivedAt ?? '',
            requestedBy: status.latestJob.requestedBy ?? null,
            triggerRef: status.latestJob.triggerRef ?? null,
            transportMode: status.latestJob.transportMode ?? null,
          }
        : null,
    };
  }

  private adaptExternalSyncJobResult(job: RawExternalSyncJobResult): ExternalSyncJobResult {
    return {
      jobId: job.jobId ?? null,
      sourceSystem: job.sourceSystem ?? '',
      transportMode: job.transportMode ?? 'POLL',
      requestedBy: job.requestedBy ?? null,
      receivedAt: job.receivedAt ?? null,
      triggerRef: job.triggerRef ?? null,
      dryRun: job.dryRun ?? false,
      status: job.status ?? 'UNKNOWN',
      artifactCount: job.artifactCount ?? 0,
      warnings: job.warnings ?? [],
    };
  }

  private adaptNodeReference(node: RawGraphNodeReference | null | undefined): GraphNodeReference | null {
    if (!node?.id) {
      return null;
    }
    return {
      id: node.id,
      nodeType: node.nodeType ?? '',
      displayName: node.displayName ?? node.id,
      status: node.status ?? null,
    };
  }

  private adaptNodeReferenceWithSummary(node: RawGraphNodeReference): GraphNodeReferenceWithSummary {
    return {
      id: node.id ?? '',
      nodeType: node.nodeType ?? '',
      displayName: node.displayName ?? node.id ?? '',
      status: node.status ?? null,
      module: node.module ?? null,
      domain: node.domain ?? null,
      routePath: node.routePath ?? null,
      relationCount: node.relationCount ?? 0,
    };
  }

  private adaptObjectDefinitionSummary(definition: RawObjectDefinitionSummary): ObjectDefinitionSummary {
    return {
      type: definition.type ?? '',
      label: definition.label ?? '',
      displayName: definition.displayName ?? definition.label ?? definition.type ?? '',
      category: definition.category ?? '',
      tier: definition.tier ?? '',
      benchmarkable: definition.benchmarkable ?? false,
      instanceCount: definition.instanceCount ?? 0,
      relationshipTypeCount: definition.relationshipTypeCount ?? 0,
    };
  }

  private adaptObjectDefinitionDetail(definition: RawObjectDefinitionDetail): ObjectDefinitionDetail {
    return {
      type: definition.type ?? '',
      label: definition.label ?? '',
      displayName: definition.displayName ?? definition.label ?? definition.type ?? '',
      category: definition.category ?? '',
      tier: definition.tier ?? '',
      benchmarkable: definition.benchmarkable ?? false,
      purpose: definition.purpose ?? '',
      implementationStatus: definition.implementationStatus ?? '',
      aliases: definition.aliases ?? [],
      attributes: (definition.attributes ?? []).map((attribute) => this.adaptObjectDefinitionAttribute(attribute)),
      relationships: (definition.relationships ?? []).map((relationship) => this.adaptObjectDefinitionRelationship(relationship)),
      instanceCount: definition.instanceCount ?? 0,
      instances: (definition.instances ?? []).map((instance) => this.adaptNodeReferenceWithSummary(instance)),
    };
  }

  private adaptObjectDefinitionAttribute(attribute: RawObjectDefinitionAttribute): ObjectDefinitionAttribute {
    return {
      name: attribute.name ?? '',
      type: attribute.type ?? '',
      required: attribute.required ?? false,
      description: attribute.description ?? '',
      constraints: attribute.constraints ?? '',
    };
  }

  private adaptObjectDefinitionRelationship(relationship: RawObjectDefinitionRelationship): ObjectDefinitionRelationship {
    return {
      name: relationship.name ?? '',
      direction: relationship.direction ?? '',
      target: relationship.target ?? '',
      cardinality: relationship.cardinality ?? '',
      required: relationship.required ?? false,
      severity: relationship.severity ?? '',
      implementation: relationship.implementation ?? '',
    };
  }

  private adaptAgentPack(pack: RawAgentPack): AgentPack {
    return {
      packId: pack.packId ?? '',
      packVersion: pack.packVersion ?? 0,
      generatedAt: pack.generatedAt ?? '',
      story: this.adaptNodeReference(pack.story) ?? {
        id: '',
        nodeType: 'UserStory',
        displayName: '',
        status: null,
      },
      completeness: this.adaptAgentPackCompleteness(pack.completeness),
      readinessChecks: pack.readinessChecks ?? {},
      tasks: (pack.tasks ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      deliveredScreens: (pack.deliveredScreens ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      deliveredApis: (pack.deliveredApis ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      deliveredEntities: (pack.deliveredEntities ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      applications: (pack.applications ?? []).map((application): AgentPackApplicationTarget => ({
        id: application.id ?? '',
        name: application.name ?? null,
        applicationType: application.applicationType ?? null,
        workspaceType: application.workspaceType ?? null,
        repoPath: application.repoPath ?? null,
        repoUrl: application.repoUrl ?? null,
        defaultBuildCommand: application.defaultBuildCommand ?? null,
        defaultTestCommand: application.defaultTestCommand ?? null,
        bootstrapSteps: application.bootstrapSteps ?? [],
      })),
      components: (pack.components ?? []).map((component): AgentPackComponentTarget => ({
        id: component.id ?? '',
        nodeType: component.nodeType ?? 'ApplicationComponent',
        displayName: component.displayName ?? component.id ?? '',
        status: component.status ?? null,
        applicationId: component.applicationId ?? null,
        applicationName: component.applicationName ?? null,
        frameworkFamily: component.frameworkFamily ?? null,
        frameworkName: component.frameworkName ?? null,
        frameworkVersion: component.frameworkVersion ?? null,
        runtime: component.runtime ?? null,
        language: component.language ?? null,
        languageVersion: component.languageVersion ?? null,
        modulePath: component.modulePath ?? null,
        manifestPath: component.manifestPath ?? null,
        buildCommand: component.buildCommand ?? null,
        testCommand: component.testCommand ?? null,
        entrypointPath: component.entrypointPath ?? null,
        localRunCommand: component.localRunCommand ?? null,
        secretPrerequisites: component.secretPrerequisites ?? [],
        fixturePrerequisites: component.fixturePrerequisites ?? [],
        localRunPrerequisites: component.localRunPrerequisites ?? [],
      })),
      codeTargets: (pack.codeTargets ?? []).map((target): AgentPackCodeTarget => ({
        id: target.id ?? '',
        nodeType: target.nodeType ?? 'CodeAsset',
        displayName: target.displayName ?? target.id ?? '',
        status: target.status ?? null,
        assetType: target.assetType ?? null,
        filePath: target.filePath ?? null,
        language: target.language ?? null,
        layerType: target.layerType ?? null,
        changePolicy: target.changePolicy ?? null,
        componentId: target.componentId ?? null,
        componentName: target.componentName ?? null,
        applicationId: target.applicationId ?? null,
        applicationName: target.applicationName ?? null,
      })),
      testCases: (pack.testCases ?? []).map((testCase): AgentPackTestCase => ({
        id: testCase.id ?? '',
        displayName: testCase.displayName ?? testCase.id ?? '',
        status: testCase.status ?? null,
        testType: testCase.testType ?? null,
        testCommand: testCase.testCommand ?? null,
        testFilePath: testCase.testFilePath ?? null,
        locatedInId: testCase.locatedInId ?? null,
        locatedInPath: testCase.locatedInPath ?? null,
      })),
      policies: (pack.policies ?? []).map((policy): AgentPackPolicy => ({
        id: policy.id ?? '',
        name: policy.name ?? null,
        allowedRepos: policy.allowedRepos ?? [],
        allowedCommands: policy.allowedCommands ?? [],
        forbiddenCommands: policy.forbiddenCommands ?? [],
        allowedEnvironments: policy.allowedEnvironments ?? [],
        secretScopes: policy.secretScopes ?? [],
        maxFilesTouched: policy.maxFilesTouched ?? null,
        requiresHumanApproval: policy.requiresHumanApproval ?? null,
        approvalThreshold: policy.approvalThreshold ?? null,
      })),
      conventions: (pack.conventions ?? []).map((convention): AgentPackConvention => ({
        id: convention.id ?? '',
        name: convention.name ?? null,
        category: convention.category ?? null,
        enforcement: convention.enforcement ?? null,
        scope: convention.scope ?? null,
        docRef: convention.docRef ?? null,
        activeStatus: convention.activeStatus ?? null,
      })),
      qualityConstraints: (pack.qualityConstraints ?? []).map((constraint): AgentPackQualityConstraint => ({
        id: constraint.id ?? '',
        name: constraint.name ?? null,
        constraintType: constraint.constraintType ?? null,
        priority: constraint.priority ?? null,
        threshold: constraint.threshold ?? null,
        status: constraint.status ?? null,
      })),
    };
  }

  private adaptAgentPackCompleteness(
    completeness: RawAgentPackCompleteness | null | undefined
  ): AgentPackCompleteness {
    return {
      complete: completeness?.complete ?? false,
      missingConcerns: completeness?.missingConcerns ?? [],
      missingFields: completeness?.missingFields ?? [],
      readinessScore: completeness?.readinessScore ?? 0,
    };
  }

  private adaptStoryTraceability(traceability: RawStoryTraceability): StoryTraceability {
    return {
      objective: this.adaptNodeReference(traceability.objective),
      portfolio: this.adaptNodeReference(traceability.portfolio),
      epic: this.adaptNodeReference(traceability.epic),
      feature: this.adaptNodeReference(traceability.feature),
      story: this.adaptNodeReference(traceability.story) ?? {
        id: '',
        nodeType: 'UserStory',
        displayName: '',
        status: null,
      },
      screens: (traceability.screens ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      interactions: (traceability.interactions ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      apis: (traceability.apis ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      dataEntities: (traceability.dataEntities ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      messages: (traceability.messages ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      tasks: (traceability.tasks ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      missingSpineSegments: traceability.missingSpineSegments ?? [],
    };
  }

  private adaptBenchmark(benchmark: RawGraphBenchmark): GraphBenchmark {
    return {
      summary: {
        scopeNote: benchmark.summary?.scopeNote ?? '',
        coveredNodeTypes: benchmark.summary?.coveredNodeTypes ?? 0,
        totalNodes: benchmark.summary?.totalNodes ?? 0,
        overallScore: benchmark.summary?.overallScore ?? 0,
        dimensions: (benchmark.summary?.dimensions ?? []).map((dimension) => ({
          dimension: dimension.dimension ?? '',
          score: dimension.score ?? 0,
          status: dimension.status ?? 'RED',
          detail: dimension.detail ?? '',
        })),
      },
      types: (benchmark.types ?? []).map((type): BenchmarkTypeScore => ({
        nodeType: type.nodeType ?? '',
        totalNodes: type.totalNodes ?? 0,
        targetAttributeCount: type.targetAttributeCount ?? 0,
        attributeDepthScore: type.attributeDepthScore ?? 0,
        targetRelationshipCount: type.targetRelationshipCount ?? 0,
        relationshipCoverageScore: type.relationshipCoverageScore ?? 0,
        sourceTraceabilityApplicable: type.sourceTraceabilityApplicable ?? false,
        sourceTraceabilityScore: type.sourceTraceabilityScore ?? null,
        queryabilityScore: type.queryabilityScore ?? 0,
        overallScore: type.overallScore ?? 0,
        gapRecommendations: type.gapRecommendations ?? [],
      })),
    };
  }

  private adaptPersonaSummary(persona: RawPersonaSummary): PersonaSummary {
    return {
      personaId: persona.personaId ?? '',
      name: persona.name ?? persona.personaId ?? '',
      summary: persona.summary ?? null,
      status: persona.status ?? null,
      journeyCount: persona.journeyCount ?? 0,
      screenCount: persona.screenCount ?? 0,
      storyCount: persona.storyCount ?? 0,
      channelCount: persona.channelCount ?? 0,
    };
  }

  private adaptPersonaTraversal(persona: RawPersonaTraversal): PersonaTraversal {
    return {
      personaId: persona.personaId ?? '',
      name: persona.name ?? persona.personaId ?? '',
      summary: persona.summary ?? null,
      status: persona.status ?? null,
      roleKeys: persona.roleKeys ?? [],
      journeys: (persona.journeys ?? []).map((journey): PersonaJourneySummary => ({
        journeyId: journey.journeyId ?? '',
        title: journey.title ?? journey.journeyId ?? '',
        status: journey.status ?? null,
        stepCount: journey.stepCount ?? 0,
        screenCount: journey.screenCount ?? 0,
      })),
      roles: (persona.roles ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      channelReach: (persona.channelReach ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      screenCount: persona.screenCount ?? 0,
      storyCount: persona.storyCount ?? 0,
    };
  }

  private adaptJourneyTraversal(journey: RawJourneyTraversal): JourneyTraversal {
    return {
      journeyId: journey.journeyId ?? '',
      title: journey.title ?? journey.journeyId ?? '',
      goalStatement: journey.goalStatement ?? '',
      status: journey.status ?? null,
      persona: this.adaptNodeReference(journey.persona),
      steps: (journey.steps ?? [])
        .map((step): JourneyTraversalStep => ({
          stepId: step.stepId ?? '',
          label: step.label ?? step.stepId ?? '',
          orderIndex: step.orderIndex ?? 0,
          screen: this.adaptNodeReference(step.screen),
          touchpoint: this.adaptNodeReference(step.touchpoint),
          interaction: this.adaptNodeReference(step.interaction),
        }))
        .sort((left, right) => left.orderIndex - right.orderIndex),
    };
  }

  private adaptChannelSummary(channel: RawChannelSummary): ChannelSummary {
    return {
      channelCode: channel.channelCode ?? '',
      displayName: channel.displayName ?? channel.channelCode ?? '',
      channelType: channel.channelType ?? null,
      touchpointCount: channel.touchpointCount ?? 0,
      screenCount: channel.screenCount ?? 0,
    };
  }

  private adaptChannelTraversal(channel: RawChannelTraversal): ChannelTraversal {
    return {
      channelCode: channel.channelCode ?? '',
      displayName: channel.displayName ?? channel.channelCode ?? '',
      channelType: channel.channelType ?? null,
      touchpoints: (channel.touchpoints ?? []).map((touchpoint): ChannelTouchpointSummary => ({
        touchpointId: touchpoint.touchpointId ?? '',
        label: touchpoint.label ?? touchpoint.touchpointId ?? '',
        surfaceId: touchpoint.surfaceId ?? null,
        entryMechanisms: touchpoint.entryMechanisms ?? [],
        roleKeys: touchpoint.roleKeys ?? [],
        personaIds: touchpoint.personaIds ?? [],
        targetScreen: this.adaptNodeReference(touchpoint.targetScreen),
      })),
      screens: (channel.screens ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      coverageGaps: (channel.coverageGaps ?? []).map((gap): ChannelCoverageGap => ({
        touchpointId: gap.touchpointId ?? '',
        reason: gap.reason ?? '',
      })),
      personaReach: (channel.personaReach ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
    };
  }

  private adaptBusinessCapabilitySummary(capability: RawBusinessCapabilitySummary): BusinessCapabilitySummary {
    return {
      capabilityId: capability.capabilityId ?? '',
      name: capability.name ?? capability.capabilityId ?? '',
      domainCode: capability.domainCode ?? null,
      domainName: capability.domainName ?? null,
      processCount: capability.processCount ?? 0,
      applicationCount: capability.applicationCount ?? 0,
      featureCount: capability.featureCount ?? 0,
      organizationCount: capability.organizationCount ?? 0,
    };
  }

  private adaptBusinessArchitecture(architecture: RawBusinessArchitecture): BusinessArchitecture {
    return {
      capabilityId: architecture.capabilityId ?? '',
      name: architecture.name ?? architecture.capabilityId ?? '',
      description: architecture.description ?? null,
      status: architecture.status ?? null,
      domainCode: architecture.domainCode ?? null,
      domainName: architecture.domainName ?? null,
      processes: (architecture.processes ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      applications: (architecture.applications ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      features: (architecture.features ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      organizations: (architecture.organizations ?? []).map((organization) =>
        this.adaptBusinessArchitectureOrganization(organization)
      ),
    };
  }

  private adaptApplicationSummary(application: RawApplicationSummary): ApplicationSummary {
    return {
      applicationId: application.applicationId ?? '',
      name: application.name ?? application.applicationId ?? '',
      applicationType: application.applicationType ?? null,
      status: application.status ?? null,
      componentCount: application.componentCount ?? 0,
      apiCount: application.apiCount ?? 0,
      screenCount: application.screenCount ?? 0,
      featureCount: application.featureCount ?? 0,
      dependencyCount: application.dependencyCount ?? 0,
      ownerNames: application.ownerNames ?? [],
    };
  }

  private adaptApplicationArchitecture(architecture: RawApplicationArchitecture): ApplicationArchitecture {
    return {
      applicationId: architecture.applicationId ?? '',
      name: architecture.name ?? architecture.applicationId ?? '',
      description: architecture.description ?? null,
      applicationType: architecture.applicationType ?? null,
      status: architecture.status ?? null,
      ownerNames: architecture.ownerNames ?? [],
      components: (architecture.components ?? []).map((component) =>
        this.adaptApplicationArchitectureComponent(component)
      ),
      apis: (architecture.apis ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      screens: (architecture.screens ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      features: (architecture.features ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      dependencies: (architecture.dependencies ?? []).map((dependency) =>
        this.adaptApplicationArchitectureDependency(dependency)
      ),
    };
  }

  private adaptApplicationArchitectureComponent(
    component: RawApplicationArchitectureComponent
  ): ApplicationArchitectureComponentSummary {
    return {
      componentId: component.componentId ?? '',
      name: component.name ?? component.componentId ?? '',
      componentType: component.componentType ?? null,
      frameworkFamily: component.frameworkFamily ?? null,
      runtime: component.runtime ?? null,
      modulePath: component.modulePath ?? null,
      status: component.status ?? null,
      apis: (component.apis ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      screens: (component.screens ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      dependencies: (component.dependencies ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
    };
  }

  private adaptApplicationArchitectureDependency(
    dependency: RawApplicationArchitectureDependency
  ): ApplicationArchitectureDependencySummary {
    return {
      applicationId: dependency.applicationId ?? '',
      name: dependency.name ?? dependency.applicationId ?? '',
      direction: dependency.direction ?? null,
      status: dependency.status ?? null,
    };
  }

  private adaptDataObjectSummary(object: RawDataArchitectureObjectSummary): DataArchitectureObjectSummary {
    return {
      objectId: object.objectId ?? '',
      name: object.name ?? object.objectId ?? '',
      domain: object.domain ?? null,
      sensitivity: object.sensitivity ?? null,
      status: object.status ?? null,
      mappedEntityCount: object.mappedEntityCount ?? 0,
      flowCount: object.flowCount ?? 0,
      apiCount: object.apiCount ?? 0,
      screenCount: object.screenCount ?? 0,
    };
  }

  private adaptDataArchitecture(architecture: RawDataArchitecture): DataArchitecture {
    return {
      objectId: architecture.objectId ?? '',
      name: architecture.name ?? architecture.objectId ?? '',
      domain: architecture.domain ?? null,
      description: architecture.description ?? null,
      sensitivity: architecture.sensitivity ?? null,
      status: architecture.status ?? null,
      entities: (architecture.entities ?? []).map((entity) => this.adaptDataArchitectureEntity(entity)),
      flows: (architecture.flows ?? []).map((flow) => this.adaptDataArchitectureFlow(flow)),
      apis: (architecture.apis ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      screens: (architecture.screens ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      children: (architecture.children ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
    };
  }

  private adaptDataArchitectureEntity(entity: RawDataArchitectureEntity): DataArchitectureEntitySummary {
    return {
      entityId: entity.entityId ?? '',
      name: entity.name ?? entity.entityId ?? '',
      entityType: entity.entityType ?? null,
      fieldCount: entity.fieldCount ?? 0,
      status: entity.status ?? null,
    };
  }

  private adaptDataArchitectureFlow(flow: RawDataArchitectureFlow): DataArchitectureFlowSummary {
    return {
      flowId: flow.flowId ?? '',
      name: flow.name ?? flow.flowId ?? '',
      direction: flow.direction ?? null,
      status: flow.status ?? null,
      sourceApplicationId: flow.sourceApplicationId ?? null,
      sourceApplicationName: flow.sourceApplicationName ?? null,
      targetApplicationId: flow.targetApplicationId ?? null,
      targetApplicationName: flow.targetApplicationName ?? null,
    };
  }

  private adaptInfrastructureDeploymentSummary(
    deployment: RawInfrastructureDeploymentSummary
  ): InfrastructureDeploymentSummary {
    return {
      deploymentId: deployment.deploymentId ?? '',
      name: deployment.name ?? deployment.deploymentId ?? '',
      environment: deployment.environment ?? null,
      status: deployment.status ?? null,
      componentCount: deployment.componentCount ?? 0,
      applicationCount: deployment.applicationCount ?? 0,
      infrastructureCount: deployment.infrastructureCount ?? 0,
    };
  }

  private adaptInfrastructureArchitecture(architecture: RawInfrastructureArchitecture): InfrastructureArchitecture {
    return {
      deploymentId: architecture.deploymentId ?? '',
      name: architecture.name ?? architecture.deploymentId ?? '',
      environment: architecture.environment ?? null,
      description: architecture.description ?? null,
      status: architecture.status ?? null,
      components: (architecture.components ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      infrastructureNodes: (architecture.infrastructureNodes ?? []).map((node) =>
        this.adaptInfrastructureNode(node)
      ),
      applications: (architecture.applications ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
      elements: (architecture.elements ?? [])
        .map((node) => this.adaptNodeReference(node))
        .filter((node): node is GraphNodeReference => node !== null),
    };
  }

  private adaptInfrastructureNode(node: RawInfrastructureNode): InfrastructureNodeSummary {
    return {
      nodeId: node.nodeId ?? '',
      name: node.name ?? node.nodeId ?? '',
      nodeType: node.nodeType ?? null,
      location: node.location ?? null,
      status: node.status ?? null,
    };
  }

  private adaptBusinessArchitectureOrganization(
    organization: RawBusinessArchitectureOrganization
  ): BusinessArchitectureOrganization {
    return {
      orgId: organization.orgId ?? '',
      name: organization.name ?? organization.orgId ?? '',
      organizationType: organization.organizationType ?? null,
      status: organization.status ?? null,
    };
  }
}
