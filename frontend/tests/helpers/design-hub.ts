import { APIRequestContext, expect, Locator, Page } from '@playwright/test';
import { backendBaseUrl } from './backend';

const DEV_SERVER_OVERLAY_CSS = `
  #webpack-dev-server-client-overlay,
  #webpack-dev-server-client-overlay-div {
    display: none !important;
    pointer-events: none !important;
    opacity: 0 !important;
  }
`;

export interface BackendScreen {
  surfaceId: string;
  label: string;
  module: string;
  designStatus: 'COMPLETE' | 'SPECIFIED' | 'NOT_STARTED';
  roleKeys: string[];
  storyRefs: string[];
  gaps?: Array<unknown>;
}

export interface BackendTouchpoint {
  touchpointId: string;
  surfaceId: string;
}

export interface BackendInteraction {
  interactionId: string;
  surfaceId: string;
  permission: string | null;
  apiCalls: string[];
  confirmationCode: string | null;
}

export interface BackendJourneyStep {
  stepId: string;
  interactionRef: string | null;
}

export interface BackendJourney {
  journeyId: string;
  title: string;
  personaId: string | null;
  steps: BackendJourneyStep[];
}

export interface BackendJourneyTraversalStep {
  stepId: string;
  label: string;
  orderIndex: number;
  screen: BackendGraphNodeReference | null;
  touchpoint: BackendGraphNodeReference | null;
  interaction: BackendGraphNodeReference | null;
}

export interface BackendJourneyTraversal {
  journeyId: string;
  title: string;
  goalStatement: string;
  status: string | null;
  persona: BackendGraphNodeReference | null;
  steps: BackendJourneyTraversalStep[];
}

export interface BackendPersonaJourneySummary {
  journeyId: string;
  title: string;
  status: string | null;
  stepCount: number;
  screenCount: number;
}

export interface BackendPersonaSummary {
  personaId: string;
  name: string;
  summary: string | null;
  status: string | null;
  journeyCount: number;
  screenCount: number;
  storyCount: number;
  channelCount: number;
}

export interface BackendPersonaTraversal {
  personaId: string;
  name: string;
  summary: string | null;
  status: string | null;
  roleKeys: string[];
  journeys: BackendPersonaJourneySummary[];
  roles: BackendGraphNodeReference[];
  channelReach: BackendGraphNodeReference[];
  screenCount: number;
  storyCount: number;
}

export interface BackendDeliveryStory {
  storyId: string;
  status: string | null;
  ready: boolean;
  screens: Array<{ surfaceId: string }>;
  apis: Array<{ contractId: string }>;
  externalArtifacts: Array<{ externalId: string; key: string | null }>;
}

export interface BackendGraphNodeReference {
  id: string;
  nodeType: string;
  displayName: string;
  status: string | null;
}

export interface BackendAgentPackCompleteness {
  complete: boolean;
  missingConcerns: string[];
  missingFields: string[];
  readinessScore: number;
}

export interface BackendAgentPackApplication {
  id: string;
  name: string | null;
  applicationType: string | null;
  workspaceType: string | null;
  repoPath: string | null;
  repoUrl: string | null;
  defaultBuildCommand: string | null;
  defaultTestCommand: string | null;
  bootstrapSteps: string[];
}

export interface BackendAgentPackComponent {
  id: string;
  nodeType: string;
  displayName: string;
  status: string | null;
  applicationId: string | null;
  applicationName: string | null;
  frameworkFamily: string | null;
  frameworkName: string | null;
  frameworkVersion: string | null;
  runtime: string | null;
  language: string | null;
  languageVersion: string | null;
  modulePath: string | null;
  manifestPath: string | null;
  buildCommand: string | null;
  testCommand: string | null;
  entrypointPath: string | null;
  localRunCommand: string | null;
  secretPrerequisites: string[];
  fixturePrerequisites: string[];
  localRunPrerequisites: string[];
}

export interface BackendAgentPackCodeTarget {
  id: string;
  nodeType: string;
  displayName: string;
  status: string | null;
  assetType: string | null;
  filePath: string | null;
  language: string | null;
  layerType: string | null;
  changePolicy: string | null;
  componentId: string | null;
  componentName: string | null;
  applicationId: string | null;
  applicationName: string | null;
}

export interface BackendAgentPackTestCase {
  id: string;
  displayName: string;
  status: string | null;
  testType: string | null;
  testCommand: string | null;
  testFilePath: string | null;
  locatedInId: string | null;
  locatedInPath: string | null;
}

export interface BackendAgentPackPolicy {
  id: string;
  name: string | null;
  allowedRepos: string[];
  allowedCommands: string[];
  forbiddenCommands: string[];
  allowedEnvironments: string[];
  secretScopes: string[];
  maxFilesTouched: number | null;
  requiresHumanApproval: boolean | null;
  approvalThreshold: string | null;
}

export interface BackendAgentPackConvention {
  id: string;
  name: string | null;
  category: string | null;
  enforcement: string | null;
  scope: string | null;
  docRef: string | null;
  activeStatus: string | null;
}

export interface BackendAgentPackQualityConstraint {
  id: string;
  name: string | null;
  constraintType: string | null;
  priority: string | null;
  threshold: string | null;
  status: string | null;
}

export interface BackendAgentPack {
  packId: string;
  packVersion: number;
  generatedAt: string;
  story: BackendGraphNodeReference;
  completeness: BackendAgentPackCompleteness;
  readinessChecks: Record<string, boolean>;
  tasks: BackendGraphNodeReference[];
  deliveredScreens: BackendGraphNodeReference[];
  deliveredApis: BackendGraphNodeReference[];
  deliveredEntities: BackendGraphNodeReference[];
  applications: BackendAgentPackApplication[];
  components: BackendAgentPackComponent[];
  codeTargets: BackendAgentPackCodeTarget[];
  testCases: BackendAgentPackTestCase[];
  policies: BackendAgentPackPolicy[];
  conventions: BackendAgentPackConvention[];
  qualityConstraints: BackendAgentPackQualityConstraint[];
}

export interface BackendStoryTraceability {
  objective: BackendGraphNodeReference | null;
  portfolio: BackendGraphNodeReference | null;
  epic: BackendGraphNodeReference | null;
  feature: BackendGraphNodeReference | null;
  story: BackendGraphNodeReference;
  screens: BackendGraphNodeReference[];
  interactions: BackendGraphNodeReference[];
  apis: BackendGraphNodeReference[];
  dataEntities: BackendGraphNodeReference[];
  messages: BackendGraphNodeReference[];
  tasks: BackendGraphNodeReference[];
  missingSpineSegments: string[];
}

export interface BackendBenchmarkType {
  nodeType: string;
  totalNodes: number;
  overallScore: number;
  gapRecommendations: string[];
}

export interface BackendGraphBenchmark {
  summary: {
    coveredNodeTypes: number;
    totalNodes: number;
    overallScore: number;
  };
  types: BackendBenchmarkType[];
}

export interface BackendChannelSummary {
  channelCode: string;
  displayName: string;
  channelType: string | null;
  touchpointCount: number;
  screenCount: number;
}

export interface BackendChannelTouchpointSummary {
  touchpointId: string;
  label: string;
  surfaceId: string | null;
  entryMechanisms: string[];
  roleKeys: string[];
  personaIds: string[];
  targetScreen: BackendGraphNodeReference | null;
}

export interface BackendChannelCoverageGap {
  touchpointId: string;
  reason: string;
}

export interface BackendChannelTraversal {
  channelCode: string;
  displayName: string;
  channelType: string | null;
  touchpoints: BackendChannelTouchpointSummary[];
  screens: BackendGraphNodeReference[];
  coverageGaps: BackendChannelCoverageGap[];
  personaReach: BackendGraphNodeReference[];
}

export interface BackendExternalParityAuditSummary {
  totalArtifacts: number;
  trackedFields: number;
  overallCoverageScore: number;
  status: string;
  hierarchyArtifacts: number;
  dependencyArtifacts: number;
  relatedArtifacts: number;
  duplicateArtifacts: number;
}

export interface BackendExternalParityAuditSystem {
  system: string;
  artifactCount: number;
  coverageScore: number;
  hierarchyArtifacts: number;
  dependencyArtifacts: number;
  weakestFields: string[];
}

export interface BackendExternalParityAuditField {
  field: string;
  populatedArtifacts: number;
  missingArtifacts: number;
  coverageScore: number;
  exampleMissingArtifacts: string[];
}

export interface BackendExternalParityAudit {
  summary: BackendExternalParityAuditSummary;
  systems: BackendExternalParityAuditSystem[];
  fields: BackendExternalParityAuditField[];
}

export interface BackendExternalSyncLatestJob {
  jobId: string;
  status: string;
  receivedAt: string;
  requestedBy: string | null;
  triggerRef: string | null;
  transportMode: string | null;
}

export interface BackendExternalSyncSourceStatus {
  sourceSystem: string;
  enabled: boolean;
  webhookEnabled: boolean;
  webhookSecretConfigured: boolean;
  pollingEnabled: boolean;
  pollingConfigured: boolean;
  baseUrlConfigured: boolean;
  pollPathConfigured: boolean;
  scopeConfigured: boolean;
  filterConfigured: boolean;
  tokenConfigured: boolean;
  schedulerEnabled: boolean;
  pollingDryRun: boolean;
  latestJob: BackendExternalSyncLatestJob | null;
}

export interface BackendExternalSyncJobResult {
  jobId: string | null;
  sourceSystem: string;
  transportMode: string;
  requestedBy: string | null;
  receivedAt: string | null;
  triggerRef: string | null;
  dryRun: boolean;
  status: string;
  artifactCount: number;
  warnings: string[];
}

export interface BackendBusinessCapabilitySummary {
  capabilityId: string;
  name: string;
  domainCode: string | null;
  domainName: string | null;
  processCount: number;
  applicationCount: number;
  featureCount: number;
  organizationCount: number;
}

export interface BackendBusinessArchitectureOrganization {
  orgId: string;
  name: string;
  organizationType: string | null;
  status: string | null;
}

export interface BackendBusinessArchitecture {
  capabilityId: string;
  name: string;
  description: string | null;
  status: string | null;
  domainCode: string | null;
  domainName: string | null;
  processes: BackendGraphNodeReference[];
  applications: BackendGraphNodeReference[];
  features: BackendGraphNodeReference[];
  organizations: BackendBusinessArchitectureOrganization[];
}

export interface BackendApplicationSummary {
  applicationId: string;
  name: string;
  applicationType: string | null;
  status: string | null;
  componentCount: number;
  apiCount: number;
  screenCount: number;
  featureCount: number;
  dependencyCount: number;
  ownerNames: string[];
}

export interface BackendApplicationArchitectureComponent {
  componentId: string;
  name: string;
  componentType: string | null;
  frameworkFamily: string | null;
  runtime: string | null;
  modulePath: string | null;
  status: string | null;
  apis: BackendGraphNodeReference[];
  screens: BackendGraphNodeReference[];
  dependencies: BackendGraphNodeReference[];
}

export interface BackendApplicationArchitectureDependency {
  applicationId: string;
  name: string;
  direction: string | null;
  status: string | null;
}

export interface BackendApplicationArchitecture {
  applicationId: string;
  name: string;
  description: string | null;
  applicationType: string | null;
  status: string | null;
  ownerNames: string[];
  components: BackendApplicationArchitectureComponent[];
  apis: BackendGraphNodeReference[];
  screens: BackendGraphNodeReference[];
  features: BackendGraphNodeReference[];
  dependencies: BackendApplicationArchitectureDependency[];
}

export interface BackendDataArchitectureObjectSummary {
  objectId: string;
  name: string;
  domain: string | null;
  sensitivity: string | null;
  status: string | null;
  mappedEntityCount: number;
  flowCount: number;
  apiCount: number;
  screenCount: number;
}

export interface BackendDataArchitectureEntity {
  entityId: string;
  name: string;
  entityType: string | null;
  fieldCount: number;
  status: string | null;
}

export interface BackendDataArchitectureFlow {
  flowId: string;
  name: string;
  direction: string | null;
  status: string | null;
  sourceApplicationId: string | null;
  sourceApplicationName: string | null;
  targetApplicationId: string | null;
  targetApplicationName: string | null;
}

export interface BackendDataArchitecture {
  objectId: string;
  name: string;
  domain: string | null;
  description: string | null;
  sensitivity: string | null;
  status: string | null;
  entities: BackendDataArchitectureEntity[];
  flows: BackendDataArchitectureFlow[];
  apis: BackendGraphNodeReference[];
  screens: BackendGraphNodeReference[];
  children: BackendGraphNodeReference[];
}

export interface BackendReadinessDiagnostics {
  artifactType: string;
  artifactId: string;
  status: string | null;
  readiness: Record<string, boolean>;
  completenessScore: number;
  completenessLevel: string;
  missingBlockingRules: string[];
  missingOptionalRules: string[];
  missingArtifacts: string[];
  advisoryRulesViolated: string[];
}

function screenButtons(page: Page): Locator {
  return page
    .getByTestId('screen-list')
    .locator('button[data-testid^="screen-"][data-module][data-design-status]');
}

function visibleScreenButtons(page: Page): Locator {
  return page
    .getByTestId('screen-list')
    .locator('button[data-testid^="screen-"][data-module][data-design-status]:visible');
}

export interface BackendInfrastructureDeploymentSummary {
  deploymentId: string;
  name: string;
  environment: string | null;
  status: string | null;
  componentCount: number;
  applicationCount: number;
  infrastructureCount: number;
}

export interface BackendInfrastructureNode {
  nodeId: string;
  name: string;
  nodeType: string | null;
  location: string | null;
  status: string | null;
}

export interface BackendInfrastructureArchitecture {
  deploymentId: string;
  name: string;
  environment: string | null;
  description: string | null;
  status: string | null;
  components: BackendGraphNodeReference[];
  infrastructureNodes: BackendInfrastructureNode[];
  applications: BackendGraphNodeReference[];
  elements: BackendGraphNodeReference[];
}

async function getJson<T>(request: APIRequestContext, path: string): Promise<T> {
  const response = await request.get(`${backendBaseUrl}${path}`);
  expect(response.ok(), `Expected ${path} to return 2xx`).toBeTruthy();
  return response.json() as Promise<T>;
}

export async function fetchScreens(request: APIRequestContext): Promise<BackendScreen[]> {
  return getJson<BackendScreen[]>(request, '/api/v1/design-hub/screens');
}

export async function fetchTouchpoints(request: APIRequestContext): Promise<BackendTouchpoint[]> {
  return getJson<BackendTouchpoint[]>(request, '/api/v1/design-hub/touchpoints');
}

export async function fetchInteractions(request: APIRequestContext): Promise<BackendInteraction[]> {
  return getJson<BackendInteraction[]>(request, '/api/v1/design-hub/interactions');
}

export async function fetchJourneys(request: APIRequestContext): Promise<BackendJourney[]> {
  return getJson<BackendJourney[]>(request, '/api/v1/design-hub/journeys');
}

export async function fetchDeliveryStories(request: APIRequestContext): Promise<BackendDeliveryStory[]> {
  return getJson<BackendDeliveryStory[]>(request, '/api/v1/delivery/stories');
}

export async function fetchStoryTraceability(
  request: APIRequestContext,
  storyId: string
): Promise<BackendStoryTraceability> {
  return getJson<BackendStoryTraceability>(request, `/api/v1/graph/traceability/stories/${storyId}`);
}

export async function fetchAgentPack(
  request: APIRequestContext,
  storyId: string
): Promise<BackendAgentPack> {
  return getJson<BackendAgentPack>(request, `/api/v1/stories/${storyId}/agent-pack`);
}

export async function fetchBenchmark(request: APIRequestContext): Promise<BackendGraphBenchmark> {
  return getJson<BackendGraphBenchmark>(request, '/api/v1/graph/benchmark');
}

export async function fetchExternalParityAudit(request: APIRequestContext): Promise<BackendExternalParityAudit> {
  return getJson<BackendExternalParityAudit>(request, '/api/v1/graph/external-artifacts/parity-audit');
}

export async function fetchExternalSyncSourceStatuses(
  request: APIRequestContext
): Promise<BackendExternalSyncSourceStatus[]> {
  return getJson<BackendExternalSyncSourceStatus[]>(request, '/api/v1/external-sync/sources');
}

export async function fetchExternalSyncJobs(
  request: APIRequestContext,
  options?: { limit?: number; sourceSystem?: string }
): Promise<BackendExternalSyncJobResult[]> {
  const params = new URLSearchParams();
  params.set('limit', String(options?.limit ?? 10));
  if (options?.sourceSystem) {
    params.set('sourceSystem', options.sourceSystem);
  }

  return getJson<BackendExternalSyncJobResult[]>(request, `/api/v1/external-sync/jobs?${params.toString()}`);
}

export async function fetchPersonas(request: APIRequestContext): Promise<BackendPersonaSummary[]> {
  return getJson<BackendPersonaSummary[]>(request, '/api/v1/graph/personas');
}

export async function fetchChannels(request: APIRequestContext): Promise<BackendChannelSummary[]> {
  return getJson<BackendChannelSummary[]>(request, '/api/v1/graph/channels');
}

export async function fetchChannelTraversal(
  request: APIRequestContext,
  channelCode: string
): Promise<BackendChannelTraversal> {
  return getJson<BackendChannelTraversal>(request, `/api/v1/graph/channels/${channelCode}`);
}

export async function fetchBusinessCapabilities(
  request: APIRequestContext
): Promise<BackendBusinessCapabilitySummary[]> {
  return getJson<BackendBusinessCapabilitySummary[]>(request, '/api/v1/graph/architecture/business/capabilities');
}

export async function fetchBusinessArchitecture(
  request: APIRequestContext,
  capabilityId: string
): Promise<BackendBusinessArchitecture> {
  return getJson<BackendBusinessArchitecture>(
    request,
    `/api/v1/graph/architecture/business/capabilities/${capabilityId}`
  );
}

export async function fetchApplications(
  request: APIRequestContext
): Promise<BackendApplicationSummary[]> {
  return getJson<BackendApplicationSummary[]>(request, '/api/v1/graph/architecture/applications');
}

export async function fetchApplicationArchitecture(
  request: APIRequestContext,
  applicationId: string
): Promise<BackendApplicationArchitecture> {
  return getJson<BackendApplicationArchitecture>(
    request,
    `/api/v1/graph/architecture/applications/${applicationId}`
  );
}

export async function fetchDataObjects(
  request: APIRequestContext
): Promise<BackendDataArchitectureObjectSummary[]> {
  return getJson<BackendDataArchitectureObjectSummary[]>(
    request,
    '/api/v1/graph/architecture/data/business-objects'
  );
}

export async function fetchDataArchitecture(
  request: APIRequestContext,
  objectId: string
): Promise<BackendDataArchitecture> {
  return getJson<BackendDataArchitecture>(
    request,
    `/api/v1/graph/architecture/data/business-objects/${objectId}`
  );
}

export async function fetchInfrastructureDeployments(
  request: APIRequestContext
): Promise<BackendInfrastructureDeploymentSummary[]> {
  return getJson<BackendInfrastructureDeploymentSummary[]>(
    request,
    '/api/v1/graph/architecture/infrastructure/deployments'
  );
}

export async function fetchInfrastructureArchitecture(
  request: APIRequestContext,
  deploymentId: string
): Promise<BackendInfrastructureArchitecture> {
  return getJson<BackendInfrastructureArchitecture>(
    request,
    `/api/v1/graph/architecture/infrastructure/deployments/${deploymentId}`
  );
}

export async function fetchJourneyTraversal(
  request: APIRequestContext,
  journeyId: string
): Promise<BackendJourneyTraversal> {
  return getJson<BackendJourneyTraversal>(request, `/api/v1/graph/journeys/${journeyId}`);
}

export async function fetchPersonaTraversal(
  request: APIRequestContext,
  personaId: string
): Promise<BackendPersonaTraversal> {
  return getJson<BackendPersonaTraversal>(request, `/api/v1/graph/personas/${personaId}`);
}

export async function fetchStoryReadiness(
  request: APIRequestContext,
  storyId: string
): Promise<BackendReadinessDiagnostics> {
  return getJson<BackendReadinessDiagnostics>(request, `/api/v1/readiness/stories/${storyId}`);
}

export async function fetchScreenReadiness(
  request: APIRequestContext,
  surfaceId: string
): Promise<BackendReadinessDiagnostics> {
  return getJson<BackendReadinessDiagnostics>(request, `/api/v1/readiness/screens/${surfaceId}`);
}

export async function gotoAndWaitForData(page: Page): Promise<void> {
  await page.goto('/');
  await page.addStyleTag({ content: DEV_SERVER_OVERLAY_CSS });
  await page.evaluate(() => {
    document.getElementById('webpack-dev-server-client-overlay')?.remove();
    document.getElementById('webpack-dev-server-client-overlay-div')?.remove();
  });
  await expect(page.getByTestId('design-hub-root')).toBeVisible();
  await expect(page.getByTestId('screen-list')).toBeVisible();
  await expect(visibleScreenButtons(page).first()).toBeVisible({ timeout: 15_000 });
}

export async function setLocale(page: Page, locale: 'en' | 'ar'): Promise<void> {
  await page.getByTestId(`locale-option-${locale}`).click();
  await expect(page.getByTestId(`locale-option-${locale}`)).toHaveAttribute('aria-pressed', 'true');
  await expect
    .poll(async () => page.evaluate(() => document.documentElement.lang))
    .toBe(locale);
  await expect
    .poll(async () => page.evaluate(() => document.documentElement.dir))
    .toBe(locale === 'ar' ? 'rtl' : 'ltr');
}

export async function selectScreen(page: Page, surfaceId: string): Promise<void> {
  await page.getByTestId(`screen-${surfaceId}`).click();
  await expect(page.getByTestId('prop-surfaceId')).toHaveText(surfaceId);
}

export async function getVisibleScreenMetadata(page: Page): Promise<Array<{ surfaceId: string; module: string; designStatus: string }>> {
  return screenButtons(page).evaluateAll((elements) =>
    elements
      .filter((element) => {
        const htmlElement = element as HTMLElement;
        const style = window.getComputedStyle(htmlElement);
        return style.display !== 'none'
          && style.visibility !== 'hidden'
          && style.opacity !== '0'
          && htmlElement.getClientRects().length > 0;
      })
      .map((element) => ({
        surfaceId: (element.getAttribute('data-testid') ?? '').replace(/^screen-/, ''),
        module: element.getAttribute('data-module') ?? '',
        designStatus: element.getAttribute('data-design-status') ?? '',
      }))
  );
}

export async function getFirstVisibleScreen(page: Page): Promise<{ surfaceId: string; label: string }> {
  const first = visibleScreenButtons(page).first();
  const surfaceId = ((await first.getAttribute('data-testid')) ?? '').replace(/^screen-/, '');
  const label = (await page.getByTestId(`screen-label-${surfaceId}`).textContent())?.trim() ?? '';
  return { surfaceId, label };
}

export async function readNumericTestId(page: Page, testId: string): Promise<number> {
  const raw = (await page.getByTestId(testId).textContent())?.trim() ?? '';
  const value = Number.parseInt(raw, 10);
  expect(Number.isNaN(value), `Expected ${testId} to contain a number, got "${raw}"`).toBeFalsy();
  return value;
}

export function toStatusLabel(status: BackendScreen['designStatus']): string {
  switch (status) {
    case 'COMPLETE':
      return 'Complete';
    case 'SPECIFIED':
      return 'Specified';
    case 'NOT_STARTED':
      return 'Not Started';
    default:
      return status;
  }
}
