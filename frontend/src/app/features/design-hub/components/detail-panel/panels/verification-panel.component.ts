import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { ExternalParityAudit, ExternalSyncJobResult, ExternalSyncSourceStatus, ReadinessDiagnostics } from '../../../../../models';
import { VERIFICATION_SNAPSHOT } from '../../../data/verification-snapshot';
import { DesignHubApiService } from '../../../services/design-hub-api.service';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

interface PollFeedback {
  jobId: string | null;
  status: string;
  detail: string;
  tone: 'pass' | 'warn' | 'fail';
}

@Component({
  selector: 'app-verification-panel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="verification" data-testid="verification-panel">
      <section class="verification__hero">
        <div class="verification__hero-copy">
          <p class="verification__eyebrow">Verification View</p>
          <h4 class="verification__title">Evidence across build, behavior, tokens, and live readiness</h4>
          <p class="verification__meta">
            Snapshot refreshed {{ verificationSnapshot.generatedAt }}.
          </p>
        </div>

        <div class="verification__hero-metrics">
          <article class="verification__metric">
            <span class="verification__metric-label">Checks passing</span>
            <strong class="verification__metric-value">{{ passingChecks() }}/{{ verificationSnapshot.checks.length }}</strong>
          </article>
          <article class="verification__metric">
            <span class="verification__metric-label">Benchmark score</span>
            <strong class="verification__metric-value">
              {{ formatScore(state.benchmark()?.summary?.overallScore ?? 0) }}
            </strong>
          </article>
        </div>
      </section>

      <section class="verification__section" data-testid="verification-checks">
        <div class="verification__section-head">
          <div>
            <p class="verification__section-kicker">Automation snapshot</p>
            <h5 class="verification__section-title">Current verification commands</h5>
          </div>
        </div>

        <div class="verification__check-grid">
          @for (check of verificationSnapshot.checks; track check.key) {
            <article
              class="verification__check"
              [attr.data-testid]="'verification-check-' + check.key"
            >
              <div class="verification__check-head">
                <strong class="verification__check-title">{{ check.label }}</strong>
                <span class="verification__badge" [class]="'verification__badge--' + check.status.toLowerCase()">
                  {{ check.status }}
                </span>
              </div>
              <code class="verification__command">{{ check.command }}</code>
              <p class="verification__detail">{{ check.detail }}</p>
              <p class="verification__scope">Scope: {{ check.scope }}</p>
            </article>
          }
        </div>
      </section>

      <div class="verification__grid">
        <section class="verification__section" data-testid="verification-story-readiness">
          <div class="verification__section-head">
            <div>
              <p class="verification__section-kicker">Live backend evidence</p>
              <h5 class="verification__section-title">Selected story readiness</h5>
            </div>
          </div>

          @if (state.selectedStoryReadiness(); as readiness) {
            <article class="verification__summary">
              <div class="verification__summary-head">
                <div>
                  <p class="verification__summary-label">{{ readiness.artifactType }}</p>
                  <h6 class="verification__summary-title">{{ readiness.artifactId }}</h6>
                </div>
                <span class="verification__badge" [class]="'verification__badge--' + readinessBadgeClass(readiness)">
                  {{ readiness.completenessLevel }}
                </span>
              </div>

              <div class="verification__summary-grid">
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Completeness score</span>
                  <strong class="verification__summary-value">{{ formatScore(readiness.completenessScore) }}</strong>
                </div>
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Lifecycle status</span>
                  <strong class="verification__summary-value">{{ readiness.status || 'UNSPECIFIED' }}</strong>
                </div>
              </div>

              <div class="verification__summary-grid">
                @for (entry of readinessEntries(readiness); track entry.key) {
                  <div class="verification__summary-stat">
                    <span class="verification__summary-label">{{ formatLabel(entry.key) }}</span>
                    <strong class="verification__summary-value">{{ entry.value ? 'Yes' : 'No' }}</strong>
                  </div>
                }
              </div>

              <div class="verification__summary-lists">
                <div class="verification__issue-column">
                  <h6 class="verification__issue-title">Blocking rules</h6>
                  @if (readiness.missingBlockingRules.length > 0) {
                    <ul class="verification__issue-list">
                      @for (rule of readiness.missingBlockingRules; track rule) {
                        <li>{{ rule }}</li>
                      }
                    </ul>
                  } @else {
                    <p class="verification__summary-empty">None</p>
                  }
                </div>

                <div class="verification__issue-column">
                  <h6 class="verification__issue-title">Optional rules</h6>
                  @if (readiness.missingOptionalRules.length > 0) {
                    <ul class="verification__issue-list">
                      @for (rule of readiness.missingOptionalRules; track rule) {
                        <li>{{ rule }}</li>
                      }
                    </ul>
                  } @else {
                    <p class="verification__summary-empty">None</p>
                  }
                </div>

                <div class="verification__issue-column">
                  <h6 class="verification__issue-title">Artifacts and advisories</h6>
                  @if (readiness.missingArtifacts.length > 0 || readiness.advisoryRulesViolated.length > 0) {
                    <ul class="verification__issue-list">
                      @for (artifact of readiness.missingArtifacts; track artifact) {
                        <li>{{ artifact }}</li>
                      }
                      @for (advisory of readiness.advisoryRulesViolated; track advisory) {
                        <li>{{ advisory }}</li>
                      }
                    </ul>
                  } @else {
                    <p class="verification__summary-empty">None</p>
                  }
                </div>
              </div>
            </article>
          } @else {
            <p class="verification__empty-block">
              Select a delivery story to inspect story-level readiness diagnostics.
            </p>
          }
        </section>

        <section class="verification__section" data-testid="verification-screen-readiness">
          <div class="verification__section-head">
            <div>
              <p class="verification__section-kicker">Live backend evidence</p>
              <h5 class="verification__section-title">Selected screen readiness</h5>
            </div>
          </div>

          @if (state.selectedScreenReadiness(); as readiness) {
            <article class="verification__summary">
              <div class="verification__summary-head">
                <div>
                  <p class="verification__summary-label">{{ readiness.artifactType }}</p>
                  <h6 class="verification__summary-title">{{ readiness.artifactId }}</h6>
                </div>
                <span class="verification__badge" [class]="'verification__badge--' + readinessBadgeClass(readiness)">
                  {{ readiness.completenessLevel }}
                </span>
              </div>

              <div class="verification__summary-grid">
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Completeness score</span>
                  <strong class="verification__summary-value">{{ formatScore(readiness.completenessScore) }}</strong>
                </div>
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Lifecycle status</span>
                  <strong class="verification__summary-value">{{ readiness.status || 'UNSPECIFIED' }}</strong>
                </div>
              </div>

              <div class="verification__summary-grid">
                @for (entry of readinessEntries(readiness); track entry.key) {
                  <div class="verification__summary-stat">
                    <span class="verification__summary-label">{{ formatLabel(entry.key) }}</span>
                    <strong class="verification__summary-value">{{ entry.value ? 'Yes' : 'No' }}</strong>
                  </div>
                }
              </div>

              <div class="verification__summary-lists">
                <div class="verification__issue-column">
                  <h6 class="verification__issue-title">Blocking rules</h6>
                  @if (readiness.missingBlockingRules.length > 0) {
                    <ul class="verification__issue-list">
                      @for (rule of readiness.missingBlockingRules; track rule) {
                        <li>{{ rule }}</li>
                      }
                    </ul>
                  } @else {
                    <p class="verification__summary-empty">None</p>
                  }
                </div>

                <div class="verification__issue-column">
                  <h6 class="verification__issue-title">Optional rules</h6>
                  @if (readiness.missingOptionalRules.length > 0) {
                    <ul class="verification__issue-list">
                      @for (rule of readiness.missingOptionalRules; track rule) {
                        <li>{{ rule }}</li>
                      }
                    </ul>
                  } @else {
                    <p class="verification__summary-empty">None</p>
                  }
                </div>

                <div class="verification__issue-column">
                  <h6 class="verification__issue-title">Artifacts and advisories</h6>
                  @if (readiness.missingArtifacts.length > 0 || readiness.advisoryRulesViolated.length > 0) {
                    <ul class="verification__issue-list">
                      @for (artifact of readiness.missingArtifacts; track artifact) {
                        <li>{{ artifact }}</li>
                      }
                      @for (advisory of readiness.advisoryRulesViolated; track advisory) {
                        <li>{{ advisory }}</li>
                      }
                    </ul>
                  } @else {
                    <p class="verification__summary-empty">None</p>
                  }
                </div>
              </div>
            </article>
          } @else {
            <p class="verification__empty-block">
              Select a screen to inspect screen-level readiness diagnostics.
            </p>
          }
        </section>

        <section
          class="verification__section verification__section--full"
          data-testid="verification-external-parity"
        >
          <div class="verification__section-head">
            <div>
              <p class="verification__section-kicker">External alignment audit</p>
              <h5 class="verification__section-title">Canonical field parity across external delivery systems</h5>
            </div>
          </div>

          @if (state.externalParityAudit(); as audit) {
            <article class="verification__summary">
              <div class="verification__summary-head">
                <div>
                  <p class="verification__summary-label">Overall parity</p>
                  <h6 class="verification__summary-title">
                    {{ audit.summary.totalArtifacts }} artifacts across {{ audit.summary.trackedFields }} tracked fields
                  </h6>
                </div>
                <span class="verification__badge" [class]="'verification__badge--' + statusBadgeClass(audit.summary.status)">
                  {{ audit.summary.status }}
                </span>
              </div>

              <div class="verification__summary-grid verification__summary-grid--quad">
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Coverage score</span>
                  <strong class="verification__summary-value">{{ formatScore(audit.summary.overallCoverageScore) }}</strong>
                </div>
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Hierarchy-linked</span>
                  <strong class="verification__summary-value">{{ audit.summary.hierarchyArtifacts }}</strong>
                </div>
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Dependency-linked</span>
                  <strong class="verification__summary-value">{{ audit.summary.dependencyArtifacts }}</strong>
                </div>
                <div class="verification__summary-stat">
                  <span class="verification__summary-label">Related / duplicate</span>
                  <strong class="verification__summary-value">
                    {{ audit.summary.relatedArtifacts }} / {{ audit.summary.duplicateArtifacts }}
                  </strong>
                </div>
              </div>

              <div class="verification__system-grid">
                @for (system of audit.systems; track system.system) {
                  <article class="verification__subcard">
                    <div class="verification__summary-head">
                      <div>
                        <p class="verification__summary-label">System</p>
                        <h6 class="verification__summary-title">{{ system.system }}</h6>
                      </div>
                      <span class="verification__badge" [class]="'verification__badge--' + statusBadgeClass(statusForScore(system.coverageScore))">
                        {{ formatScore(system.coverageScore) }}
                      </span>
                    </div>

                    <div class="verification__summary-grid">
                      <div class="verification__summary-stat">
                        <span class="verification__summary-label">Artifacts</span>
                        <strong class="verification__summary-value">{{ system.artifactCount }}</strong>
                      </div>
                      <div class="verification__summary-stat">
                        <span class="verification__summary-label">Hierarchy / dependency</span>
                        <strong class="verification__summary-value">
                          {{ system.hierarchyArtifacts }} / {{ system.dependencyArtifacts }}
                        </strong>
                      </div>
                    </div>

                    <div class="verification__issue-column">
                      <h6 class="verification__issue-title">Weakest fields</h6>
                      @if (system.weakestFields.length > 0) {
                        <ul class="verification__issue-list">
                          @for (field of system.weakestFields; track field) {
                            <li>{{ formatLabel(field) }}</li>
                          }
                        </ul>
                      } @else {
                        <p class="verification__summary-empty">None</p>
                      }
                    </div>
                  </article>
                }
              </div>

              <div class="verification__field-list">
                @for (field of topParityFields(audit); track field.field) {
                  <article class="verification__field-row">
                    <div class="verification__field-main">
                      <strong class="verification__check-title">{{ formatLabel(field.field) }}</strong>
                      <p class="verification__detail">
                        {{ field.populatedArtifacts }} populated / {{ field.missingArtifacts }} missing
                      </p>
                    </div>
                    <div class="verification__field-meta">
                      <span class="verification__badge" [class]="'verification__badge--' + statusBadgeClass(statusForScore(field.coverageScore))">
                        {{ formatScore(field.coverageScore) }}
                      </span>
                      @if (field.exampleMissingArtifacts.length > 0) {
                        <p class="verification__scope">
                          Missing: {{ field.exampleMissingArtifacts.join(', ') }}
                        </p>
                      } @else {
                        <p class="verification__scope">Missing: none</p>
                      }
                    </div>
                  </article>
                }
              </div>
            </article>
          } @else {
            <p class="verification__empty-block">
              External parity audit data is unavailable for the current session.
            </p>
          }
        </section>

        <section
          class="verification__section verification__section--full"
          data-testid="verification-external-sync"
        >
          <div class="verification__section-head">
            <div>
              <p class="verification__section-kicker">Operational sync status</p>
              <h5 class="verification__section-title">External source configuration and latest persisted jobs</h5>
            </div>
          </div>

          @if (state.externalSyncSourceStatuses().length > 0) {
            <div class="verification__system-grid">
              @for (source of state.externalSyncSourceStatuses(); track source.sourceSystem) {
                <article
                  class="verification__subcard"
                  [attr.data-testid]="'verification-external-sync-' + source.sourceSystem.toLowerCase()"
                >
                  <div class="verification__summary-head">
                    <div>
                      <p class="verification__summary-label">Source</p>
                      <h6 class="verification__summary-title">{{ formatLabel(source.sourceSystem) }}</h6>
                    </div>
                    <span
                      class="verification__badge"
                      [class]="'verification__badge--' + externalSyncBadgeClass(source)"
                    >
                      {{ source.latestJob?.status ?? (source.pollingConfigured ? 'READY' : 'CONFIG PENDING') }}
                    </span>
                  </div>

                  <div class="verification__summary-grid verification__summary-grid--quad">
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Webhook</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.webhookEnabled) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Polling</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.pollingEnabled) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Configured endpoint</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.pollingConfigured) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Dry run</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.pollingDryRun) }}</strong>
                    </div>
                  </div>

                  <div class="verification__summary-grid verification__summary-grid--quad">
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Base URL</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.baseUrlConfigured) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Poll path</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.pollPathConfigured) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Token</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.tokenConfigured) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Scheduler</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.schedulerEnabled) }}</strong>
                    </div>
                  </div>

                  <div class="verification__summary-grid verification__summary-grid--quad">
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Scope</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.scopeConfigured) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Query filter</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.filterConfigured) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Webhook secret</span>
                      <strong class="verification__summary-value">{{ booleanLabel(source.webhookSecretConfigured) }}</strong>
                    </div>
                    <div class="verification__summary-stat">
                      <span class="verification__summary-label">Transport ready</span>
                      <strong class="verification__summary-value">
                        {{ booleanLabel(source.pollingConfigured && source.scopeConfigured) }}
                      </strong>
                    </div>
                  </div>

                  <div class="verification__actions">
                    <button
                      type="button"
                      class="verification__action"
                      [attr.data-testid]="'verification-poll-' + source.sourceSystem.toLowerCase()"
                      [disabled]="!source.enabled || !source.pollingEnabled || activePollSource() === source.sourceSystem"
                      (click)="runPoll(source)"
                    >
                      {{ activePollSource() === source.sourceSystem ? 'Polling…' : 'Run poll' }}
                    </button>
                    <p class="verification__scope">
                      Uses the current application.yml source config with requestedBy=ui-verification.
                    </p>
                  </div>

                  @if (pollFeedback(source.sourceSystem); as feedback) {
                    <div
                      class="verification__feedback"
                      [class]="'verification__feedback--' + feedback.tone"
                      [attr.data-testid]="'verification-poll-feedback-' + source.sourceSystem.toLowerCase()"
                      aria-live="polite"
                      role="status"
                    >
                      <strong>{{ feedback.status }}</strong>
                      <span>{{ feedback.detail }}</span>
                    </div>
                  }

                  @if (source.latestJob; as latestJob) {
                    <div class="verification__issue-column">
                      <h6 class="verification__issue-title">Latest persisted job</h6>
                      <p class="verification__detail">
                        {{ latestJob.jobId }} · {{ latestJob.transportMode || 'UNKNOWN' }} · {{ latestJob.receivedAt }}
                      </p>
                      <p class="verification__scope">
                        Requested by: {{ latestJob.requestedBy || 'n/a' }}
                      </p>
                      <p class="verification__scope">
                        Trigger: {{ latestJob.triggerRef || 'n/a' }}
                      </p>
                    </div>
                  } @else {
                    <p class="verification__summary-empty">No persisted sync jobs recorded yet.</p>
                  }
                </article>
              }
            </div>

            @if (state.externalSyncJobs().length > 0) {
              <div class="verification__actions verification__actions--history">
                @for (filter of syncHistoryFilters; track filter.value) {
                  <button
                    type="button"
                    class="verification__action verification__action--secondary"
                    [class.verification__action--selected]="state.selectedExternalSyncHistorySource() === filter.value"
                    [attr.aria-pressed]="state.selectedExternalSyncHistorySource() === filter.value"
                    [attr.data-testid]="'verification-history-filter-' + filter.value.toLowerCase()"
                    (click)="selectSyncHistoryFilter(filter.value)"
                  >
                    {{ filter.label }}
                  </button>
                }
              </div>

              <div class="verification__field-list" data-testid="verification-sync-history">
                @for (job of recentSyncJobRows(); track job?.jobId ?? $index) {
                  @if (job) {
                    <article class="verification__field-row verification__field-row--history verification__field-row--stack">
                      <div class="verification__field-main">
                        <strong class="verification__check-title">
                          {{ formatLabel(job.sourceSystem) }} · {{ job.transportMode }}
                        </strong>
                        <p class="verification__detail">
                          {{ job.jobId || 'PENDING' }} · requestedBy={{ job.requestedBy || 'n/a' }}
                        </p>
                        <p class="verification__scope">
                          {{ job.triggerRef || 'no trigger reference' }}
                        </p>
                        @if (job.warnings.length > 0) {
                          <p class="verification__scope">
                            {{ job.warnings.join(' · ') }}
                          </p>
                        }
                      </div>
                      <div class="verification__field-meta">
                        <span class="verification__badge" [class]="'verification__badge--' + jobStatusBadgeClass(job.status)">
                          {{ job.status }}
                        </span>
                        <p class="verification__scope">{{ job.receivedAt || 'Pending receipt' }}</p>
                      </div>
                    </article>
                  } @else {
                    <article class="verification__field-row verification__field-row--history verification__field-row--stack verification__field-row--placeholder">
                      <div class="verification__field-main">
                        <strong class="verification__check-title">Reserved sync history slot</strong>
                        <p class="verification__scope">Recent jobs will appear here as additional poll or webhook activity is recorded.</p>
                      </div>
                    </article>
                  }
                }
              </div>
            } @else {
              <div class="verification__actions verification__actions--history">
                @for (filter of syncHistoryFilters; track filter.value) {
                  <button
                    type="button"
                    class="verification__action verification__action--secondary"
                    [class.verification__action--selected]="state.selectedExternalSyncHistorySource() === filter.value"
                    [attr.aria-pressed]="state.selectedExternalSyncHistorySource() === filter.value"
                    [attr.data-testid]="'verification-history-filter-' + filter.value.toLowerCase()"
                    (click)="selectSyncHistoryFilter(filter.value)"
                  >
                    {{ filter.label }}
                  </button>
                }
              </div>
              <p class="verification__summary-empty">No persisted sync jobs for the current history filter.</p>
            }
          } @else {
            <p class="verification__empty-block">
              External sync source status is unavailable for the current session.
            </p>
          }
        </section>
      </div>
    </div>
  `,
  styles: [`
    .verification {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
      color: var(--tp-text);
    }

    .verification__hero,
    .verification__section,
    .verification__check,
    .verification__summary,
    .verification__metric {
      border: 1px solid var(--tp-border);
      border-radius: var(--nm-radius);
      background: color-mix(in srgb, var(--tp-white) 76%, var(--tp-surface));
      box-shadow: var(--tp-elevation-default);
    }

    .verification__hero {
      display: grid;
      grid-template-columns: minmax(0, 1.45fr) minmax(0, 1fr);
      gap: var(--tp-space-4);
      padding: var(--tp-space-4);
    }

    .verification__hero-copy,
    .verification__section-head,
    .verification__issue-column {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
      min-width: 0;
    }

    .verification__eyebrow,
    .verification__section-kicker,
    .verification__metric-label,
    .verification__summary-label,
    .verification__scope {
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
    }

    .verification__title,
    .verification__section-title,
    .verification__check-title,
    .verification__summary-title {
      margin: 0;
      color: var(--tp-text-dark);
    }

    .verification__title {
      font-size: 1rem;
    }

    .verification__section-title,
    .verification__summary-title {
      font-size: 0.9rem;
    }

    .verification__meta,
    .verification__detail {
      font-size: 0.8rem;
      line-height: 1.45;
      color: var(--tp-text);
      margin: 0;
    }

    .verification__hero-metrics,
    .verification__check-grid,
    .verification__grid,
    .verification__summary-grid,
    .verification__summary-lists {
      display: grid;
      gap: var(--tp-space-3);
    }

    .verification__hero-metrics {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .verification__metric {
      display: grid;
      gap: var(--tp-space-1);
      padding: var(--tp-space-3);
    }

    .verification__metric-value {
      font-size: 1.15rem;
      color: var(--tp-primary-dark);
    }

    .verification__section {
      display: grid;
      gap: var(--tp-space-4);
      padding: var(--tp-space-4);
    }

    .verification__section--full {
      grid-column: 1 / -1;
    }

    .verification__check-grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }

    .verification__grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .verification__check {
      display: grid;
      gap: var(--tp-space-2);
      padding: var(--tp-space-3);
      background: var(--tp-white);
    }

    .verification__check-head,
    .verification__summary-head {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-3);
      align-items: start;
    }

    .verification__badge {
      display: inline-flex;
      align-items: center;
      min-height: var(--tp-touch-target-min-size);
      padding-inline: var(--tp-space-3);
      border-radius: 999px;
      font-size: 0.72rem;
      font-weight: 700;
      white-space: nowrap;
    }

    .verification__badge--pass {
      background: var(--tp-toast-success-bg);
      color: var(--tp-primary-dark);
    }

    .verification__badge--warn {
      background: var(--tp-toast-warn-bg);
      color: var(--tp-warning-dark);
    }

    .verification__badge--fail {
      background: var(--tp-danger-bg);
      color: var(--tp-danger);
    }

    .verification__command {
      display: inline-flex;
      align-items: center;
      min-height: var(--tp-touch-target-min-size);
      width: fit-content;
      max-width: 100%;
      padding: 0 var(--tp-space-3);
      border-radius: var(--tp-space-3);
      background: color-mix(in srgb, var(--tp-surface) 68%, var(--tp-white));
      color: var(--tp-primary-dark);
      overflow-wrap: anywhere;
    }

    .verification__scope {
      margin: 0;
    }

    .verification__summary {
      display: grid;
      gap: var(--tp-space-3);
      padding: var(--tp-space-3);
      background: var(--tp-white);
    }

    .verification__actions {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
      align-items: center;
    }

    .verification__action {
      min-height: var(--tp-touch-target-min-size);
      padding: 0 var(--tp-space-3);
      border: 1px solid var(--tp-primary);
      border-radius: 999px;
      background: var(--tp-primary);
      color: var(--tp-white);
      font: inherit;
      font-weight: 700;
      cursor: pointer;
      transition: background-color 120ms ease, border-color 120ms ease, opacity 120ms ease;
    }

    .verification__action:disabled {
      cursor: progress;
      opacity: 0.7;
    }

    .verification__action:not(:disabled):hover {
      background: var(--tp-primary-dark);
      border-color: var(--tp-primary-dark);
    }

    .verification__action--secondary {
      background: var(--tp-white);
      color: var(--tp-primary-dark);
    }

    .verification__action--secondary:not(:disabled):hover,
    .verification__action--selected {
      background: color-mix(in srgb, var(--tp-primary) 14%, var(--tp-white));
      border-color: var(--tp-primary-dark);
      color: var(--tp-primary-dark);
    }

    .verification__actions--history {
      justify-content: flex-start;
    }

    .verification__feedback {
      display: grid;
      gap: var(--tp-space-1);
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-3);
      font-size: 0.8rem;
      line-height: 1.4;
    }

    .verification__feedback--pass {
      background: var(--tp-toast-success-bg);
      color: var(--tp-primary-dark);
    }

    .verification__feedback--warn {
      background: var(--tp-toast-warn-bg);
      color: var(--tp-warning-dark);
    }

    .verification__feedback--fail {
      background: var(--tp-danger-bg);
      color: var(--tp-danger);
    }

    .verification__summary-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .verification__summary-grid--quad {
      grid-template-columns: repeat(4, minmax(0, 1fr));
    }

    .verification__summary-stat {
      display: grid;
      gap: var(--tp-space-1);
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-3);
      background: color-mix(in srgb, var(--tp-surface) 72%, var(--tp-white));
    }

    .verification__summary-value {
      font-size: 1rem;
      color: var(--tp-primary-dark);
      font-weight: 700;
    }

    .verification__summary-lists {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }

    .verification__system-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: var(--tp-space-3);
    }

    .verification__subcard,
    .verification__field-row {
      display: grid;
      gap: var(--tp-space-3);
      padding: var(--tp-space-3);
      border: 1px solid var(--tp-border);
      border-radius: var(--tp-space-3);
      background: color-mix(in srgb, var(--tp-surface) 72%, var(--tp-white));
    }

    .verification__field-list {
      display: grid;
      gap: var(--tp-space-2);
    }

    .verification__field-row {
      grid-template-columns: minmax(0, 1fr) auto;
      align-items: center;
    }

    .verification__field-row--stack {
      align-items: start;
    }

    .verification__field-row--history .verification__field-main,
    .verification__field-row--history .verification__field-meta {
      min-width: 0;
    }

    .verification__field-row--history .verification__detail,
    .verification__field-row--history .verification__scope {
      max-width: 100%;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .verification__field-row--placeholder {
      min-height: 5.75rem;
      background: color-mix(in srgb, var(--tp-surface) 82%, var(--tp-white));
    }

    .verification__field-main,
    .verification__field-meta {
      display: grid;
      gap: var(--tp-space-1);
      min-width: 0;
    }

    .verification__field-meta {
      justify-items: end;
      text-align: right;
    }

    .verification__issue-title {
      margin: 0;
      font-size: 0.78rem;
      color: var(--tp-text-dark);
    }

    .verification__issue-list {
      margin: 0;
      padding-inline-start: var(--tp-space-4);
      line-height: 1.45;
      color: var(--tp-text);
    }

    .verification__summary-empty,
    .verification__empty-block {
      padding: var(--tp-space-4);
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-surface) 86%, var(--tp-white));
      color: var(--tp-text-muted);
      text-align: center;
      font-style: italic;
    }

    @media (max-width: 1100px) {
      .verification__hero,
      .verification__grid,
      .verification__check-grid,
      .verification__summary-grid,
      .verification__summary-lists,
      .verification__summary-grid--quad,
      .verification__system-grid,
      .verification__hero-metrics {
        grid-template-columns: 1fr;
      }

      .verification__field-row {
        grid-template-columns: 1fr;
      }

      .verification__field-meta {
        justify-items: start;
        text-align: left;
      }
    }
  `],
})
export class VerificationPanelComponent {
  private readonly api = inject(DesignHubApiService);
  readonly state = inject(DesignHubStateService);
  readonly verificationSnapshot = VERIFICATION_SNAPSHOT;
  readonly activePollSource = signal<string | null>(null);
  readonly pollFeedbackBySource = signal<Record<string, PollFeedback>>({});
  readonly syncHistoryFilters = [
    { value: 'ALL', label: 'All jobs' },
    { value: 'JIRA', label: 'Jira' },
    { value: 'AZURE_DEVOPS', label: 'Azure DevOps' },
  ] as const;
  readonly recentSyncJobRows = computed(() => {
    const jobs = this.state.externalSyncJobs().slice(0, 6);
    if (jobs.length === 0) {
      return [];
    }
    const placeholders = Array.from({ length: Math.max(0, 6 - jobs.length) }, () => null as ExternalSyncJobResult | null);
    return [...jobs, ...placeholders];
  });
  readonly passingChecks = computed(() =>
    this.verificationSnapshot.checks.filter((check) => check.status === 'PASS').length
  );

  formatScore(value: number): string {
    return value.toFixed(1);
  }

  formatLabel(value: string): string {
    if (value === 'AZURE_DEVOPS') {
      return 'Azure DevOps';
    }
    if (value === 'JIRA') {
      return 'Jira';
    }

    if (/^[A-Z0-9_\-\s]+$/.test(value)) {
      return value
        .toLowerCase()
        .replace(/[_-]+/g, ' ')
        .replace(/\s+/g, ' ')
        .trim()
        .replace(/\b\w/g, (character) => character.toUpperCase());
    }

    return value
      .replace(/[_-]+/g, ' ')
      .replace(/([A-Z])/g, ' $1')
      .replace(/\s+/g, ' ')
      .trim()
      .replace(/^./, (first) => first.toUpperCase());
  }

  readinessEntries(readiness: ReadinessDiagnostics): Array<{ key: string; value: boolean }> {
    return Object.entries(readiness.readiness ?? {}).map(([key, value]) => ({ key, value }));
  }

  readinessBadgeClass(readiness: ReadinessDiagnostics): 'pass' | 'warn' | 'fail' {
    return this.statusBadgeClass(readiness.completenessLevel);
  }

  statusBadgeClass(status: string | null | undefined): 'pass' | 'warn' | 'fail' {
    return status?.toLowerCase() === 'green'
      ? 'pass'
      : status?.toLowerCase() === 'amber'
        ? 'warn'
        : 'fail';
  }

  statusForScore(score: number): 'GREEN' | 'AMBER' | 'RED' {
    return score >= 80 ? 'GREEN' : score >= 60 ? 'AMBER' : 'RED';
  }

  topParityFields(audit: ExternalParityAudit) {
    return [...audit.fields]
      .sort((left, right) => left.coverageScore - right.coverageScore || left.field.localeCompare(right.field))
      .slice(0, 6);
  }

  booleanLabel(value: boolean): string {
    return value ? 'Yes' : 'No';
  }

  pollFeedback(sourceSystem: string): PollFeedback | null {
    return this.pollFeedbackBySource()[sourceSystem] ?? null;
  }

  selectSyncHistoryFilter(sourceSystem: string): void {
    this.state.setSelectedExternalSyncHistorySource(sourceSystem);
  }

  externalSyncBadgeClass(source: ExternalSyncSourceStatus): 'pass' | 'warn' | 'fail' {
    if (source.latestJob?.status) {
      return this.jobStatusBadgeClass(source.latestJob.status);
    }
    return source.pollingConfigured && source.scopeConfigured ? 'pass' : 'warn';
  }

  runPoll(source: ExternalSyncSourceStatus): void {
    if (!source.enabled || !source.pollingEnabled || this.activePollSource() === source.sourceSystem) {
      return;
    }

    this.activePollSource.set(source.sourceSystem);
    this.api.triggerExternalSyncPoll(source.sourceSystem, {
      dryRun: source.pollingDryRun,
      requestedBy: 'ui-verification',
      triggerRef: `ui/verification/${source.sourceSystem.toLowerCase()}`,
    }).subscribe({
      next: (job) => {
        this.activePollSource.set(null);
        this.setPollFeedback(source.sourceSystem, job);
        this.state.refreshExternalSyncOperations();
      },
      error: () => {
        this.activePollSource.set(null);
        this.pollFeedbackBySource.update((feedback) => ({
          ...feedback,
          [source.sourceSystem]: {
            jobId: null,
            status: 'FAILED',
            detail: 'The poll request could not be completed from the verification view.',
            tone: 'fail',
          },
        }));
      },
    });
  }

  jobStatusBadgeClass(status: string): 'pass' | 'warn' | 'fail' {
    const normalized = status.toLowerCase();
    if (normalized === 'success' || normalized === 'ready') {
      return 'pass';
    }
    if (normalized === 'skipped' || normalized === 'config pending') {
      return 'warn';
    }
    return 'fail';
  }

  private setPollFeedback(sourceSystem: string, job: ExternalSyncJobResult): void {
    const warnings = job.warnings.join(' ').trim();
    const detail = [
      job.jobId ?? 'pending job id',
      warnings || `${job.artifactCount} artifacts inspected`,
    ].join(' · ');

    this.pollFeedbackBySource.update((feedback) => ({
      ...feedback,
      [sourceSystem]: {
        jobId: job.jobId,
        status: job.status,
        detail,
        tone: this.jobStatusBadgeClass(job.status),
      },
    }));
  }
}
