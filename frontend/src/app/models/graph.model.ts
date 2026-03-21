export interface GraphNodeReference {
  id: string;
  nodeType: string;
  displayName: string;
  status: string | null;
}

export interface StoryTraceability {
  objective: GraphNodeReference | null;
  portfolio: GraphNodeReference | null;
  epic: GraphNodeReference | null;
  feature: GraphNodeReference | null;
  story: GraphNodeReference;
  screens: GraphNodeReference[];
  interactions: GraphNodeReference[];
  apis: GraphNodeReference[];
  dataEntities: GraphNodeReference[];
  messages: GraphNodeReference[];
  tasks: GraphNodeReference[];
  missingSpineSegments: string[];
}

export interface BenchmarkDimensionScore {
  dimension: string;
  score: number;
  status: string;
  detail: string;
}

export interface BenchmarkSummary {
  scopeNote: string;
  coveredNodeTypes: number;
  totalNodes: number;
  overallScore: number;
  dimensions: BenchmarkDimensionScore[];
}

export interface BenchmarkTypeScore {
  nodeType: string;
  totalNodes: number;
  targetAttributeCount: number;
  attributeDepthScore: number;
  targetRelationshipCount: number;
  relationshipCoverageScore: number;
  sourceTraceabilityApplicable: boolean;
  sourceTraceabilityScore: number | null;
  queryabilityScore: number;
  overallScore: number;
  gapRecommendations: string[];
}

export interface GraphBenchmark {
  summary: BenchmarkSummary;
  types: BenchmarkTypeScore[];
}

export interface PersonaJourneySummary {
  journeyId: string;
  title: string;
  status: string | null;
  stepCount: number;
  screenCount: number;
}

export interface PersonaSummary {
  personaId: string;
  name: string;
  summary: string | null;
  status: string | null;
  journeyCount: number;
  screenCount: number;
  storyCount: number;
  channelCount: number;
}

export interface PersonaTraversal {
  personaId: string;
  name: string;
  summary: string | null;
  status: string | null;
  roleKeys: string[];
  journeys: PersonaJourneySummary[];
  roles: GraphNodeReference[];
  channelReach: GraphNodeReference[];
  screenCount: number;
  storyCount: number;
}

export interface JourneyTraversalStep {
  stepId: string;
  label: string;
  orderIndex: number;
  screen: GraphNodeReference | null;
  touchpoint: GraphNodeReference | null;
  interaction: GraphNodeReference | null;
}

export interface JourneyTraversal {
  journeyId: string;
  title: string;
  goalStatement: string;
  status: string | null;
  persona: GraphNodeReference | null;
  steps: JourneyTraversalStep[];
}

export interface ChannelSummary {
  channelCode: string;
  displayName: string;
  channelType: string | null;
  touchpointCount: number;
  screenCount: number;
}

export interface ChannelTouchpointSummary {
  touchpointId: string;
  label: string;
  surfaceId: string | null;
  entryMechanisms: string[];
  roleKeys: string[];
  personaIds: string[];
  targetScreen: GraphNodeReference | null;
}

export interface ChannelCoverageGap {
  touchpointId: string;
  reason: string;
}

export interface ChannelTraversal {
  channelCode: string;
  displayName: string;
  channelType: string | null;
  touchpoints: ChannelTouchpointSummary[];
  screens: GraphNodeReference[];
  coverageGaps: ChannelCoverageGap[];
  personaReach: GraphNodeReference[];
}

export interface BusinessCapabilitySummary {
  capabilityId: string;
  name: string;
  domainCode: string | null;
  domainName: string | null;
  processCount: number;
  applicationCount: number;
  featureCount: number;
  organizationCount: number;
}

export interface BusinessArchitectureOrganization {
  orgId: string;
  name: string;
  organizationType: string | null;
  status: string | null;
}

export interface BusinessArchitecture {
  capabilityId: string;
  name: string;
  description: string | null;
  status: string | null;
  domainCode: string | null;
  domainName: string | null;
  processes: GraphNodeReference[];
  applications: GraphNodeReference[];
  features: GraphNodeReference[];
  organizations: BusinessArchitectureOrganization[];
}

export type ArchitectureView = 'business' | 'application' | 'data' | 'infrastructure';

export interface ApplicationSummary {
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

export interface ApplicationArchitectureComponentSummary {
  componentId: string;
  name: string;
  componentType: string | null;
  frameworkFamily: string | null;
  runtime: string | null;
  modulePath: string | null;
  status: string | null;
  apis: GraphNodeReference[];
  screens: GraphNodeReference[];
  dependencies: GraphNodeReference[];
}

export interface ApplicationArchitectureDependencySummary {
  applicationId: string;
  name: string;
  direction: string | null;
  status: string | null;
}

export interface ApplicationArchitecture {
  applicationId: string;
  name: string;
  description: string | null;
  applicationType: string | null;
  status: string | null;
  ownerNames: string[];
  components: ApplicationArchitectureComponentSummary[];
  apis: GraphNodeReference[];
  screens: GraphNodeReference[];
  features: GraphNodeReference[];
  dependencies: ApplicationArchitectureDependencySummary[];
}

export interface DataArchitectureObjectSummary {
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

export interface DataArchitectureEntitySummary {
  entityId: string;
  name: string;
  entityType: string | null;
  fieldCount: number;
  status: string | null;
}

export interface DataArchitectureFlowSummary {
  flowId: string;
  name: string;
  direction: string | null;
  status: string | null;
  sourceApplicationId: string | null;
  sourceApplicationName: string | null;
  targetApplicationId: string | null;
  targetApplicationName: string | null;
}

export interface DataArchitecture {
  objectId: string;
  name: string;
  domain: string | null;
  description: string | null;
  sensitivity: string | null;
  status: string | null;
  entities: DataArchitectureEntitySummary[];
  flows: DataArchitectureFlowSummary[];
  apis: GraphNodeReference[];
  screens: GraphNodeReference[];
  children: GraphNodeReference[];
}

export interface InfrastructureDeploymentSummary {
  deploymentId: string;
  name: string;
  environment: string | null;
  status: string | null;
  componentCount: number;
  applicationCount: number;
  infrastructureCount: number;
}

export interface InfrastructureNodeSummary {
  nodeId: string;
  name: string;
  nodeType: string | null;
  location: string | null;
  status: string | null;
}

export interface InfrastructureArchitecture {
  deploymentId: string;
  name: string;
  environment: string | null;
  description: string | null;
  status: string | null;
  components: GraphNodeReference[];
  infrastructureNodes: InfrastructureNodeSummary[];
  applications: GraphNodeReference[];
  elements: GraphNodeReference[];
}
