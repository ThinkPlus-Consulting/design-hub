import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { AgentPack } from '../../../../../models';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-automation-panel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="automation" data-testid="automation-panel">
      @if (state.selectedAgentPack(); as pack) {
        <section class="automation__hero">
          <div>
            <p class="automation__eyebrow">Automation View</p>
            <h4 class="automation__title">{{ pack.story.displayName }}</h4>
            <p class="automation__meta" data-testid="automation-pack-id">
              {{ pack.packId }} · v{{ pack.packVersion }} · {{ formatDate(pack.generatedAt) }}
            </p>
          </div>

          <div class="automation__hero-metrics">
            <article class="automation__metric">
              <span class="automation__metric-label">Readiness</span>
              <strong class="automation__metric-value">{{ pack.completeness.readinessScore }}</strong>
            </article>
            <article class="automation__metric">
              <span class="automation__metric-label">Targets</span>
              <strong class="automation__metric-value">
                {{ pack.applications.length + pack.components.length + pack.codeTargets.length + pack.testCases.length }}
              </strong>
            </article>
            <span
              class="automation__badge"
              [class]="'automation__badge--' + (pack.completeness.complete ? 'green' : 'amber')"
              data-testid="automation-completeness"
            >
              {{ pack.completeness.complete ? 'Complete' : 'Needs work' }}
            </span>
          </div>
        </section>

        <section class="automation__section" data-testid="automation-readiness">
          <div class="automation__section-head">
            <div>
              <p class="automation__section-kicker">Agent readiness</p>
              <h5 class="automation__section-title">Blocking and advisory checks</h5>
            </div>
          </div>

          <div class="automation__readiness-grid">
            <article class="automation__readiness-card" data-testid="automation-readiness-score">
              <span class="automation__readiness-label">Readiness score</span>
              <strong class="automation__readiness-value">{{ pack.completeness.readinessScore }}</strong>
            </article>
            @for (entry of readinessEntries(pack); track entry.key) {
              <article
                class="automation__readiness-card"
                [attr.data-testid]="'automation-check-' + entry.key"
              >
                <span class="automation__readiness-label">{{ formatLabel(entry.key) }}</span>
                <strong class="automation__readiness-value">{{ entry.value ? 'Yes' : 'No' }}</strong>
              </article>
            }
          </div>

          @if (pack.completeness.missingConcerns.length > 0 || pack.completeness.missingFields.length > 0) {
            <div class="automation__issues">
              @if (pack.completeness.missingConcerns.length > 0) {
                <div>
                  <h6 class="automation__issue-title">Missing concerns</h6>
                  <ul class="automation__issue-list">
                    @for (issue of pack.completeness.missingConcerns; track issue) {
                      <li>{{ issue }}</li>
                    }
                  </ul>
                </div>
              }
              @if (pack.completeness.missingFields.length > 0) {
                <div>
                  <h6 class="automation__issue-title">Missing fields</h6>
                  <ul class="automation__issue-list">
                    @for (field of pack.completeness.missingFields; track field) {
                      <li>{{ field }}</li>
                    }
                  </ul>
                </div>
              }
            </div>
          }
        </section>

        <div class="automation__grid">
          <section class="automation__section" data-testid="automation-traceables">
            <div class="automation__section-head">
              <div>
                <p class="automation__section-kicker">Resolved graph scope</p>
                <h5 class="automation__section-title">Story delivery targets</h5>
              </div>
            </div>

            <div class="automation__traceables">
              <div class="automation__traceable-column">
                <h6 class="automation__issue-title">Tasks</h6>
                @if (pack.tasks.length > 0) {
                  <ul class="automation__node-list">
                    @for (task of pack.tasks; track task.id) {
                      <li>{{ task.displayName }}</li>
                    }
                  </ul>
                } @else {
                  <p class="automation__empty">No tasks resolved.</p>
                }
              </div>

              <div class="automation__traceable-column">
                <h6 class="automation__issue-title">Screens</h6>
                @if (pack.deliveredScreens.length > 0) {
                  <ul class="automation__node-list">
                    @for (screen of pack.deliveredScreens; track screen.id) {
                      <li>{{ screen.displayName }}</li>
                    }
                  </ul>
                } @else {
                  <p class="automation__empty">No screens resolved.</p>
                }
              </div>

              <div class="automation__traceable-column">
                <h6 class="automation__issue-title">APIs</h6>
                @if (pack.deliveredApis.length > 0) {
                  <ul class="automation__node-list">
                    @for (api of pack.deliveredApis; track api.id) {
                      <li>{{ api.displayName }}</li>
                    }
                  </ul>
                } @else {
                  <p class="automation__empty">No APIs resolved.</p>
                }
              </div>

              <div class="automation__traceable-column">
                <h6 class="automation__issue-title">Entities</h6>
                @if (pack.deliveredEntities.length > 0) {
                  <ul class="automation__node-list">
                    @for (entity of pack.deliveredEntities; track entity.id) {
                      <li>{{ entity.displayName }}</li>
                    }
                  </ul>
                } @else {
                  <p class="automation__empty">No entities resolved.</p>
                }
              </div>
            </div>
          </section>

          <section class="automation__section" data-testid="automation-components">
            <div class="automation__section-head">
              <div>
                <p class="automation__section-kicker">Implementation surface</p>
                <h5 class="automation__section-title">Components and code targets</h5>
              </div>
            </div>

            <div class="automation__stack">
              @if (pack.components.length > 0) {
                @for (component of pack.components; track component.id) {
                  <article class="automation__card" [attr.data-testid]="'automation-component-' + component.id">
                    <div class="automation__card-head">
                      <strong>{{ component.displayName }}</strong>
                      <span class="automation__pill">{{ component.frameworkFamily || 'Framework unset' }}</span>
                    </div>
                    <p class="automation__card-meta">
                      {{ component.applicationName || component.applicationId || 'Application unset' }}
                    </p>
                    <dl class="automation__property-list">
                      <div><dt>Runtime</dt><dd>{{ component.runtime || 'Unspecified' }}</dd></div>
                      <div><dt>Language</dt><dd>{{ formatRuntime(component.language, component.languageVersion) }}</dd></div>
                      <div><dt>Framework</dt><dd>{{ formatRuntime(component.frameworkName || component.frameworkFamily, component.frameworkVersion) }}</dd></div>
                      <div><dt>Module</dt><dd>{{ component.modulePath || 'Unspecified' }}</dd></div>
                      <div><dt>Manifest</dt><dd>{{ component.manifestPath || 'Unspecified' }}</dd></div>
                      <div><dt>Build</dt><dd>{{ component.buildCommand || 'Unspecified' }}</dd></div>
                      <div><dt>Test</dt><dd>{{ component.testCommand || 'Unspecified' }}</dd></div>
                      <div><dt>Entrypoint</dt><dd>{{ component.entrypointPath || 'Unspecified' }}</dd></div>
                      <div><dt>Local run</dt><dd>{{ component.localRunCommand || 'Unspecified' }}</dd></div>
                    </dl>
                    @if (
                      component.localRunPrerequisites.length > 0 ||
                      component.fixturePrerequisites.length > 0 ||
                      component.secretPrerequisites.length > 0
                    ) {
                      <div class="automation__card-groups">
                        @if (component.localRunPrerequisites.length > 0) {
                          <div>
                            <h6 class="automation__issue-title">Local prerequisites</h6>
                            <ul class="automation__issue-list">
                              @for (prerequisite of component.localRunPrerequisites; track prerequisite) {
                                <li>{{ prerequisite }}</li>
                              }
                            </ul>
                          </div>
                        }
                        @if (component.fixturePrerequisites.length > 0) {
                          <div>
                            <h6 class="automation__issue-title">Fixtures</h6>
                            <ul class="automation__issue-list">
                              @for (fixture of component.fixturePrerequisites; track fixture) {
                                <li>{{ fixture }}</li>
                              }
                            </ul>
                          </div>
                        }
                        @if (component.secretPrerequisites.length > 0) {
                          <div>
                            <h6 class="automation__issue-title">Secret scopes</h6>
                            <ul class="automation__issue-list">
                              @for (secret of component.secretPrerequisites; track secret) {
                                <li>{{ secret }}</li>
                              }
                            </ul>
                          </div>
                        }
                      </div>
                    }
                  </article>
                }
              } @else {
                <p class="automation__empty">No implementation components resolved.</p>
              }

              @if (pack.codeTargets.length > 0) {
                <div class="automation__subsection">
                  <h6 class="automation__issue-title">Code targets</h6>
                  <div class="automation__stack">
                    @for (target of pack.codeTargets; track target.id) {
                      <article class="automation__card" [attr.data-testid]="'automation-code-target-' + target.id">
                        <div class="automation__card-head">
                          <strong>{{ target.displayName }}</strong>
                          <span class="automation__pill">{{ target.assetType || 'ASSET' }}</span>
                        </div>
                        <p class="automation__card-meta">
                          {{ target.componentName || 'Component unresolved' }} · {{ target.layerType || 'Layer unset' }}
                        </p>
                        <p class="automation__path">{{ target.filePath || 'Path unspecified' }}</p>
                        <p class="automation__card-meta">{{ target.changePolicy || 'Change policy unspecified' }}</p>
                      </article>
                    }
                  </div>
                </div>
              }

              @if (pack.testCases.length > 0) {
                <div class="automation__subsection" data-testid="automation-tests">
                  <h6 class="automation__issue-title">Verification targets</h6>
                  <div class="automation__stack">
                    @for (testCase of pack.testCases; track testCase.id) {
                      <article class="automation__card" [attr.data-testid]="'automation-test-case-' + testCase.id">
                        <div class="automation__card-head">
                          <strong>{{ testCase.displayName }}</strong>
                          <span class="automation__pill">{{ testCase.testType || 'TEST' }}</span>
                        </div>
                        <p class="automation__path">{{ testCase.testFilePath || testCase.locatedInPath || 'Path unspecified' }}</p>
                        <p class="automation__card-meta">{{ testCase.testCommand || 'Command unspecified' }}</p>
                      </article>
                    }
                  </div>
                </div>
              }
            </div>
          </section>

          <section class="automation__section" data-testid="automation-applications">
            <div class="automation__section-head">
              <div>
                <p class="automation__section-kicker">Workspace bootstrap</p>
                <h5 class="automation__section-title">Applications and default commands</h5>
              </div>
            </div>

            @if (pack.applications.length > 0) {
              <div class="automation__stack">
                @for (application of pack.applications; track application.id) {
                  <article class="automation__card" [attr.data-testid]="'automation-application-' + application.id">
                    <div class="automation__card-head">
                      <strong>{{ application.name || application.id }}</strong>
                      <span class="automation__pill">{{ application.workspaceType || 'Workspace unset' }}</span>
                    </div>
                    <p class="automation__card-meta">
                      {{ application.applicationType || 'Application' }} · {{ application.repoPath || 'Repo path unset' }}
                    </p>
                    <dl class="automation__property-list">
                      <div><dt>Repo</dt><dd>{{ application.repoUrl || application.repoPath || 'Unspecified' }}</dd></div>
                      <div><dt>Default build</dt><dd>{{ application.defaultBuildCommand || 'Unspecified' }}</dd></div>
                      <div><dt>Default test</dt><dd>{{ application.defaultTestCommand || 'Unspecified' }}</dd></div>
                    </dl>
                    @if (application.bootstrapSteps.length > 0) {
                      <div class="automation__card-groups">
                        <div>
                          <h6 class="automation__issue-title">Bootstrap steps</h6>
                          <ol class="automation__ordered-list">
                            @for (step of application.bootstrapSteps; track step) {
                              <li>{{ step }}</li>
                            }
                          </ol>
                        </div>
                      </div>
                    }
                  </article>
                }
              </div>
            } @else {
              <p class="automation__empty">No applications resolved.</p>
            }
          </section>
        </div>

        <div class="automation__grid">
          <section class="automation__section" data-testid="automation-policies">
            <div class="automation__section-head">
              <div>
                <p class="automation__section-kicker">Execution guardrails</p>
                <h5 class="automation__section-title">Agent policies in scope</h5>
              </div>
            </div>

            @if (pack.policies.length > 0) {
              <div class="automation__stack">
                @for (policy of pack.policies; track policy.id) {
                  <article class="automation__card" [attr.data-testid]="'automation-policy-' + policy.id">
                    <div class="automation__card-head">
                      <strong>{{ policy.name || policy.id }}</strong>
                      <span class="automation__pill">
                        {{ policy.requiresHumanApproval ? 'Approval required' : 'Autonomous' }}
                      </span>
                    </div>
                    <p class="automation__card-meta">
                      {{ policy.approvalThreshold || 'Approval threshold unspecified' }}
                    </p>
                    <dl class="automation__property-list">
                      <div><dt>Max files</dt><dd>{{ policy.maxFilesTouched ?? 'Unspecified' }}</dd></div>
                      <div><dt>Environments</dt><dd>{{ formatList(policy.allowedEnvironments) }}</dd></div>
                      <div><dt>Secrets</dt><dd>{{ formatList(policy.secretScopes) }}</dd></div>
                    </dl>
                    <div class="automation__card-groups">
                      <div>
                        <h6 class="automation__issue-title">Allowed commands</h6>
                        <ul class="automation__issue-list">
                          @for (command of policy.allowedCommands; track command) {
                            <li>{{ command }}</li>
                          }
                        </ul>
                      </div>
                      @if (policy.forbiddenCommands.length > 0) {
                        <div>
                          <h6 class="automation__issue-title">Forbidden commands</h6>
                          <ul class="automation__issue-list">
                            @for (command of policy.forbiddenCommands; track command) {
                              <li>{{ command }}</li>
                            }
                          </ul>
                        </div>
                      }
                    </div>
                  </article>
                }
              </div>
            } @else {
              <p class="automation__empty">No agent policies resolved.</p>
            }
          </section>

          <section class="automation__section" data-testid="automation-conventions">
            <div class="automation__section-head">
              <div>
                <p class="automation__section-kicker">Guardrails</p>
                <h5 class="automation__section-title">Coding conventions</h5>
              </div>
            </div>

            @if (pack.conventions.length > 0) {
              <div class="automation__stack">
                @for (convention of pack.conventions; track convention.id) {
                  <article class="automation__card">
                    <div class="automation__card-head">
                      <strong>{{ convention.name || convention.id }}</strong>
                      <span class="automation__pill">{{ convention.enforcement || 'UNSPECIFIED' }}</span>
                    </div>
                    <p class="automation__card-meta">
                      {{ convention.category || 'General' }} · {{ convention.scope || 'Scope unset' }}
                    </p>
                    <p class="automation__path">{{ convention.docRef || 'Reference unset' }}</p>
                  </article>
                }
              </div>
            } @else {
              <p class="automation__empty">No conventions resolved.</p>
            }
          </section>

          <section class="automation__section" data-testid="automation-quality-constraints">
            <div class="automation__section-head">
              <div>
                <p class="automation__section-kicker">Quality guardrails</p>
                <h5 class="automation__section-title">Constraints in scope</h5>
              </div>
            </div>

            @if (pack.qualityConstraints.length > 0) {
              <div class="automation__stack">
                @for (constraint of pack.qualityConstraints; track constraint.id) {
                  <article class="automation__card">
                    <div class="automation__card-head">
                      <strong>{{ constraint.name || constraint.id }}</strong>
                      <span class="automation__pill">{{ constraint.priority || 'PRIORITY UNSET' }}</span>
                    </div>
                    <p class="automation__card-meta">
                      {{ constraint.constraintType || 'Constraint' }} · {{ constraint.status || 'UNSPECIFIED' }}
                    </p>
                    <p class="automation__path">{{ constraint.threshold || 'Threshold unset' }}</p>
                  </article>
                }
              </div>
            } @else {
              <p class="automation__empty">No quality constraints resolved.</p>
            }
          </section>
        </div>
      } @else {
        <div class="automation__empty-state" data-testid="automation-empty">
          <p class="automation__eyebrow">Automation View</p>
          <h4 class="automation__title">Select a delivery story to inspect its agent pack</h4>
          <p class="automation__meta">
            This view shows readiness checks, implementation targets, test coverage, conventions, and quality constraints.
          </p>
        </div>
      }
    </div>
  `,
  styles: [`
    .automation {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
    }

    .automation__hero,
    .automation__section,
    .automation__empty-state {
      border: 1px solid color-mix(in srgb, var(--tp-border) 26%, transparent);
      border-radius: var(--nm-radius);
      background: linear-gradient(
        180deg,
        color-mix(in srgb, var(--tp-white) 90%, transparent),
        color-mix(in srgb, var(--tp-primary-bg) 42%, var(--tp-white))
      );
      box-shadow: var(--tp-elevation-default);
    }

    .automation__hero,
    .automation__empty-state {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-4);
      padding: var(--tp-space-4);
    }

    .automation__section {
      padding: var(--tp-space-4);
    }

    .automation__eyebrow,
    .automation__section-kicker,
    .automation__metric-label,
    .automation__readiness-label,
    .automation__card-meta,
    .automation__path,
    .automation__empty,
    .automation__meta {
      margin: 0;
      color: var(--tp-text-muted);
    }

    .automation__eyebrow,
    .automation__section-kicker {
      font-size: 0.75rem;
      text-transform: uppercase;
      letter-spacing: 0.08em;
    }

    .automation__title,
    .automation__section-title {
      margin: 0;
      color: var(--tp-text-dark);
    }

    .automation__title {
      font-size: 1.25rem;
    }

    .automation__section-title {
      font-size: 1rem;
    }

    .automation__hero-metrics {
      display: flex;
      align-items: flex-start;
      gap: var(--tp-space-3);
      flex-wrap: wrap;
    }

    .automation__metric,
    .automation__readiness-card {
      min-width: 7.5rem;
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-white) 92%, var(--tp-primary-bg));
      border: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
    }

    .automation__metric-value,
    .automation__readiness-value {
      display: block;
      color: var(--tp-primary-dark);
      font-size: 1.2rem;
      margin-top: var(--tp-space-1);
    }

    .automation__badge,
    .automation__pill {
      display: inline-flex;
      align-items: center;
      justify-content: center;
      border-radius: 999px;
      padding: var(--tp-space-1) var(--tp-space-3);
      font-size: 0.75rem;
      font-weight: 600;
      border: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      background: color-mix(in srgb, var(--tp-white) 78%, transparent);
      color: var(--tp-primary-dark);
    }

    .automation__badge--green {
      background: color-mix(in srgb, var(--tp-toast-success-bg) 76%, var(--tp-white));
      color: var(--tp-text-dark);
    }

    .automation__badge--amber {
      background: color-mix(in srgb, var(--tp-toast-warn-bg) 76%, var(--tp-white));
      color: var(--tp-warning-dark);
    }

    .automation__section-head,
    .automation__card-head {
      display: flex;
      justify-content: space-between;
      align-items: flex-start;
      gap: var(--tp-space-3);
      margin-bottom: var(--tp-space-3);
    }

    .automation__readiness-grid,
    .automation__traceables,
    .automation__grid {
      display: grid;
      gap: var(--tp-space-3);
    }

    .automation__readiness-grid {
      grid-template-columns: repeat(auto-fit, minmax(9rem, 1fr));
    }

    .automation__traceables,
    .automation__grid {
      grid-template-columns: repeat(auto-fit, minmax(18rem, 1fr));
    }

    .automation__traceable-column,
    .automation__subsection,
    .automation__issues {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
    }

    .automation__issues {
      margin-top: var(--tp-space-3);
      padding-top: var(--tp-space-3);
      border-top: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
    }

    .automation__issue-title {
      margin: 0;
      color: var(--tp-text-dark);
      font-size: 0.9rem;
    }

    .automation__node-list,
    .automation__issue-list,
    .automation__ordered-list {
      margin: 0;
      padding-left: 1rem;
      color: var(--tp-text);
    }

    .automation__stack {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-3);
    }

    .automation__card-groups {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(12rem, 1fr));
      gap: var(--tp-space-3);
      margin-top: var(--tp-space-3);
      padding-top: var(--tp-space-3);
      border-top: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
    }

    .automation__card {
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-white) 94%, transparent);
      border: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
      box-shadow: 0 10px 24px color-mix(in srgb, var(--nm-shadow-dark) 10%, transparent);
    }

    .automation__property-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(10rem, 1fr));
      gap: var(--tp-space-2) var(--tp-space-3);
      margin: 0;
    }

    .automation__property-list div {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-1);
    }

    .automation__property-list dt {
      color: var(--tp-text-muted);
      font-size: 0.72rem;
      text-transform: uppercase;
      letter-spacing: 0.06em;
    }

    .automation__property-list dd {
      margin: 0;
      color: var(--tp-text-dark);
      word-break: break-word;
    }

    @media (max-width: 960px) {
      .automation__hero,
      .automation__empty-state {
        flex-direction: column;
      }
    }
  `],
})
export class AutomationPanelComponent {
  readonly state = inject(DesignHubStateService);

  readinessEntries(pack: AgentPack): Array<{ key: string; value: boolean }> {
    return Object.entries(pack.readinessChecks).map(([key, value]) => ({ key, value }));
  }

  formatDate(value: string): string {
    if (!value) {
      return 'Generated date unavailable';
    }

    const parsed = new Date(value);
    if (Number.isNaN(parsed.getTime())) {
      return value;
    }

    return parsed.toLocaleString();
  }

  formatLabel(value: string): string {
    return value
      .replace(/([a-z])([A-Z])/g, '$1 $2')
      .replace(/^./, (char) => char.toUpperCase());
  }

  formatRuntime(primary: string | null, version: string | null): string {
    if (!primary && !version) {
      return 'Unspecified';
    }
    if (!version) {
      return primary ?? 'Unspecified';
    }
    return `${primary ?? 'Runtime'} ${version}`;
  }

  formatList(values: string[]): string {
    return values.length > 0 ? values.join(', ') : 'Unspecified';
  }
}
