import { GraphNodeReference } from './graph.model';

export interface AgentPackCompleteness {
  complete: boolean;
  missingConcerns: string[];
  missingFields: string[];
  readinessScore: number;
}

export interface AgentPackApplicationTarget {
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

export interface AgentPackComponentTarget {
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

export interface AgentPackCodeTarget {
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

export interface AgentPackTestCase {
  id: string;
  displayName: string;
  status: string | null;
  testType: string | null;
  testCommand: string | null;
  testFilePath: string | null;
  locatedInId: string | null;
  locatedInPath: string | null;
}

export interface AgentPackPolicy {
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

export interface AgentPackConvention {
  id: string;
  name: string | null;
  category: string | null;
  enforcement: string | null;
  scope: string | null;
  docRef: string | null;
  activeStatus: string | null;
}

export interface AgentPackQualityConstraint {
  id: string;
  name: string | null;
  constraintType: string | null;
  priority: string | null;
  threshold: string | null;
  status: string | null;
}

export interface AgentPack {
  packId: string;
  packVersion: number;
  generatedAt: string;
  story: GraphNodeReference;
  completeness: AgentPackCompleteness;
  readinessChecks: Record<string, boolean>;
  tasks: GraphNodeReference[];
  deliveredScreens: GraphNodeReference[];
  deliveredApis: GraphNodeReference[];
  deliveredEntities: GraphNodeReference[];
  applications: AgentPackApplicationTarget[];
  components: AgentPackComponentTarget[];
  codeTargets: AgentPackCodeTarget[];
  testCases: AgentPackTestCase[];
  policies: AgentPackPolicy[];
  conventions: AgentPackConvention[];
  qualityConstraints: AgentPackQualityConstraint[];
}
