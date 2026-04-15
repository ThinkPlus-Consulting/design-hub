import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import {
  ComponentRegistryDefinition,
  ComponentRegistryInstance,
  ComponentRegistryInstanceUpdateRequest,
} from '../models/component-registry.types';
import {
  SystemShellGraphIssueScanRequest,
  SystemShellGraphIssueScanSummary,
  SystemShellGraphIssueStatusUpdateRequest,
  SystemShellGraphIssueUpdateRequest,
} from '../models/issues.types';
import { SystemShellGraphResponse } from '../models/graph.types';

@Injectable({ providedIn: 'root' })
export class DesignHubApiService {
  private readonly http = inject(HttpClient);

  getGraph() {
    return this.http.get<SystemShellGraphResponse>('/api/v1/system-shell-graph/graph');
  }

  scanIssues(request: SystemShellGraphIssueScanRequest) {
    return this.http.post<SystemShellGraphIssueScanSummary>('/api/v1/system-shell-graph/issues/scan', request);
  }

  updateIssueStatuses(request: SystemShellGraphIssueStatusUpdateRequest) {
    return this.http.put<void>('/api/v1/system-shell-graph/issues/status', request);
  }

  updateIssue(issueId: string, request: SystemShellGraphIssueUpdateRequest) {
    return this.http.put<void>(`/api/v1/system-shell-graph/issues/${issueId}`, request);
  }

  getComponentRegistry() {
    return this.http.get<ComponentRegistryDefinition[]>('/api/v1/system-shell-graph/components');
  }

  getComponentInstance(assetType: string) {
    return this.http.get<ComponentRegistryInstance>(`/api/v1/system-shell-graph/components/${assetType}/instance`);
  }

  saveComponentInstance(instanceId: string, request: ComponentRegistryInstanceUpdateRequest) {
    return this.http.put<ComponentRegistryInstance>(`/api/v1/system-shell-graph/components/instances/${instanceId}`, request);
  }
}
