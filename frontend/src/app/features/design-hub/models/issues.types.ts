export interface SystemShellGraphIssueNode {
  serialNumber: string;
  name: string;
  family: 'Issue';
  objectType: 'DesignIssue';
  domain: 'frontend' | string;
  layer: 'instance';
  description: string;
  id: string;
  status: 'open' | 'closed';
  severity: 'error';
  source: 'X-Ray Agent';
  category: 'Structure' | 'Viewport Navigation' | 'Accessibility' | 'HTML Element Violation' | 'Preview-Tree Parity';
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
  issueIds: string[];
  status: 'open' | 'closed';
}

export interface SystemShellGraphIssueUpdateRequest {
  issuePrompt: string | null;
}
