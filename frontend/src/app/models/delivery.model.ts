import { ReadinessDiagnostics } from './verification.model';

export interface DeliveryFeatureSummary {
  featureId: string;
  title: string;
  status: string | null;
}

export interface DeliveryScreenSummary {
  surfaceId: string;
  label: string;
  routePath: string | null;
  status: string | null;
}

export interface DeliveryApiSummary {
  contractId: string;
  method: string | null;
  path: string | null;
  status: string | null;
}

export interface DeliveryBugSummary {
  bugId: string;
  externalKey: string | null;
  summary: string | null;
  severity: string | null;
  status: string | null;
}

export interface DeliveryFindingSummary {
  findingId: string;
  summary: string | null;
  status: string | null;
}

export interface DeliveryGapSummary {
  gapId: string;
  gapType: string | null;
  severity: string | null;
  description: string | null;
  status: string | null;
}

export interface DeliveryExternalArtifactSummary {
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
  status: string | null;
}

export interface DeliveryDiagnostics extends ReadinessDiagnostics {}

export interface DeliveryStory {
  storyId: string;
  label: string;
  module: string | null;
  domain: string | null;
  storyNumber: string | null;
  status: string | null;
  ready: boolean;
  feature: DeliveryFeatureSummary | null;
  screens: DeliveryScreenSummary[];
  apis: DeliveryApiSummary[];
  bugs: DeliveryBugSummary[];
  findings: DeliveryFindingSummary[];
  gaps: DeliveryGapSummary[];
  externalArtifacts: DeliveryExternalArtifactSummary[];
  diagnostics: DeliveryDiagnostics | null;
}
