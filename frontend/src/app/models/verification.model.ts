export interface ReadinessDiagnostics {
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

export interface ExternalSyncLatestJobSummary {
  jobId: string;
  status: string;
  receivedAt: string;
  requestedBy: string | null;
  triggerRef: string | null;
  transportMode: string | null;
}

export interface ExternalSyncSourceStatus {
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
  latestJob: ExternalSyncLatestJobSummary | null;
}

export interface ExternalSyncJobResult {
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

export type VerificationCheckStatus = 'PASS' | 'WARN' | 'FAIL';

export interface VerificationCheck {
  key: string;
  label: string;
  status: VerificationCheckStatus;
  command: string;
  detail: string;
  scope: string;
}

export interface VerificationSnapshot {
  generatedAt: string;
  checks: VerificationCheck[];
}
