import { GraphNodeReference } from './graph.model';

export interface ExternalArtifactSummary {
  externalId: string;
  system: string | null;
  externalType: string | null;
  key: string | null;
  title: string | null;
  projectScope: string | null;
  workflowState: string | null;
  priority: string | null;
  owner: string | null;
  reporter: string | null;
  labels: string[];
  url: string | null;
  syncStatus: string | null;
  lastSyncedAt: string | null;
  status: string | null;
  representedObjectCount: number;
  childCount: number;
  dependencyCount: number;
  relatedCount: number;
}

export interface ExternalArtifactLinkSummary {
  externalId: string;
  system: string | null;
  externalType: string | null;
  key: string | null;
  title: string | null;
  workflowState: string | null;
  syncStatus: string | null;
  status: string | null;
}

export interface ExternalArtifactDetail {
  externalId: string;
  system: string | null;
  externalType: string | null;
  key: string | null;
  title: string | null;
  projectScope: string | null;
  workflowState: string | null;
  priority: string | null;
  owner: string | null;
  reporter: string | null;
  labels: string[];
  customFields: Record<string, string>;
  url: string | null;
  syncStatus: string | null;
  lastSyncedAt: string | null;
  status: string | null;
  parents: ExternalArtifactLinkSummary[];
  children: ExternalArtifactLinkSummary[];
  dependencies: ExternalArtifactLinkSummary[];
  relatedArtifacts: ExternalArtifactLinkSummary[];
  duplicates: ExternalArtifactLinkSummary[];
  representedObjects: GraphNodeReference[];
}

export interface ExternalParityAuditSummary {
  totalArtifacts: number;
  trackedFields: number;
  overallCoverageScore: number;
  status: string;
  hierarchyArtifacts: number;
  dependencyArtifacts: number;
  relatedArtifacts: number;
  duplicateArtifacts: number;
}

export interface ExternalParityAuditSystem {
  system: string;
  artifactCount: number;
  coverageScore: number;
  hierarchyArtifacts: number;
  dependencyArtifacts: number;
  weakestFields: string[];
}

export interface ExternalParityAuditField {
  field: string;
  populatedArtifacts: number;
  missingArtifacts: number;
  coverageScore: number;
  exampleMissingArtifacts: string[];
}

export interface ExternalParityAudit {
  summary: ExternalParityAuditSummary;
  systems: ExternalParityAuditSystem[];
  fields: ExternalParityAuditField[];
}
