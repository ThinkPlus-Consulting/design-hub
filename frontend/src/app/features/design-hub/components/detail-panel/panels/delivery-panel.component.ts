import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { DeliveryStory, ExternalArtifactDetail, ExternalArtifactLinkSummary } from '../../../../../models';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

type ReadinessFilter = 'all' | 'ready' | 'not-ready';

@Component({
  selector: 'app-delivery-panel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="delivery" data-testid="delivery-panel">
      <section class="delivery__filters">
        <label class="delivery__field">
          <span class="delivery__label">Search</span>
          <input
            class="delivery__input"
            data-testid="delivery-search"
            [value]="search()"
            (input)="search.set(inputValue($event))"
            placeholder="Story, module, feature"
          />
        </label>

        <label class="delivery__field">
          <span class="delivery__label">Status</span>
          <select
            class="delivery__select"
            data-testid="delivery-status-filter"
            [value]="statusFilter()"
            (change)="statusFilter.set(inputValue($event))"
          >
            @for (status of statusOptions(); track status) {
              <option [value]="status">{{ formatStatus(status) }}</option>
            }
          </select>
        </label>

        <label class="delivery__field">
          <span class="delivery__label">Readiness</span>
          <select
            class="delivery__select"
            data-testid="delivery-readiness-filter"
            [value]="readinessFilter()"
            (change)="setReadinessFilter(inputValue($event))"
          >
            <option value="all">All</option>
            <option value="ready">Ready</option>
            <option value="not-ready">Not Ready</option>
          </select>
        </label>
      </section>

      @if (groupedStories().length > 0) {
        <section class="delivery__groups">
          @for (group of groupedStories(); track group.status) {
            <div class="delivery__group" [attr.data-testid]="'delivery-group-' + group.testId">
              <div class="delivery__group-header">
                <h4 class="delivery__group-title">{{ formatStatus(group.status) }}</h4>
                <span class="delivery__group-count">{{ group.stories.length }}</span>
              </div>

              <div class="delivery__story-list">
                @for (story of group.stories; track story.storyId) {
                  <button
                    class="delivery__story-card"
                    [class.delivery__story-card--selected]="story.storyId === state.selectedDeliveryStoryId()"
                    [attr.data-testid]="'delivery-story-' + story.storyId"
                    (click)="state.selectDeliveryStory(story.storyId)"
                  >
                    <div class="delivery__story-header">
                      <span class="delivery__story-id">{{ story.storyId }}</span>
                      <span class="delivery__badge" [class]="'delivery__badge--' + levelClass(story.diagnostics?.completenessLevel)">
                        {{ story.diagnostics?.completenessLevel ?? 'RED' }}
                      </span>
                    </div>
                    <div class="delivery__story-title">{{ story.label }}</div>
                    <div class="delivery__story-meta">
                      <span>{{ story.module || 'Unknown module' }}</span>
                      @if (story.feature) {
                        <span>Feature: {{ story.feature.title }}</span>
                      }
                    </div>
                    <div class="delivery__story-foot">
                      <span class="delivery__pill" [class.delivery__pill--ready]="story.ready" [class.delivery__pill--blocked]="!story.ready">
                        {{ story.ready ? 'Ready' : 'Blocked' }}
                      </span>
                      <span class="delivery__count">{{ story.screens.length }} screens</span>
                      <span class="delivery__count">{{ story.apis.length }} APIs</span>
                    </div>
                  </button>
                }
              </div>
            </div>
          }
        </section>
      } @else {
        <p class="delivery__empty" data-testid="delivery-empty">No delivery stories match the current filters</p>
      }

      @if (state.selectedDeliveryStory(); as story) {
        <section class="delivery__detail" data-testid="delivery-detail">
          <div class="delivery__detail-header">
            <div>
              <p class="delivery__eyebrow">Delivery Detail</p>
              <h4 class="delivery__detail-title">{{ story.label }}</h4>
              <div class="delivery__detail-meta">
                <span data-testid="delivery-story-id">{{ story.storyId }}</span>
                @if (story.feature) {
                  <span>{{ story.feature.title }}</span>
                }
                @if (story.domain) {
                  <span>{{ story.domain }}</span>
                }
              </div>
            </div>

            <div class="delivery__detail-actions">
              <button
                type="button"
                class="delivery__action-btn"
                data-testid="delivery-open-traceability"
                (click)="state.setActiveTab('traceability')"
              >
                Open traceability
              </button>
              <span class="delivery__pill" [class.delivery__pill--ready]="story.ready" [class.delivery__pill--blocked]="!story.ready">
                {{ story.ready ? 'Ready' : 'Not Ready' }}
              </span>
              <span class="delivery__badge" [class]="'delivery__badge--' + levelClass(story.diagnostics?.completenessLevel)">
                {{ formatScore(story.diagnostics?.completenessScore ?? 0) }}%
              </span>
            </div>
          </div>

          <div class="delivery__section-grid">
            <section class="delivery__section" data-testid="delivery-readiness">
              <h5 class="delivery__section-title">Readiness</h5>
              @if (story.diagnostics) {
                <div class="delivery__readiness-grid">
                  @for (entry of readinessEntries(story); track entry.key) {
                    <span class="delivery__readiness-chip" [class.delivery__readiness-chip--true]="entry.value">
                      {{ formatReadinessKey(entry.key) }}: {{ entry.value ? 'Yes' : 'No' }}
                    </span>
                  }
                </div>
              } @else {
                <p class="delivery__muted">No readiness diagnostics available</p>
              }
            </section>

            <section class="delivery__section" data-testid="delivery-blockers">
              <h5 class="delivery__section-title">Missing Artifacts</h5>
              @if ((story.diagnostics?.missingArtifacts?.length ?? 0) > 0) {
                <ul class="delivery__list">
                  @for (artifact of story.diagnostics?.missingArtifacts ?? []; track artifact) {
                    <li>{{ artifact }}</li>
                  }
                </ul>
              } @else {
                <p class="delivery__muted">No structural blockers reported</p>
              }
            </section>

            <section class="delivery__section" data-testid="delivery-screens">
              <h5 class="delivery__section-title">Screens</h5>
              @if (story.screens.length > 0) {
                <ul class="delivery__cards">
                  @for (screen of story.screens; track screen.surfaceId) {
                    <li class="delivery__card">
                      <span class="delivery__card-title">{{ screen.surfaceId }}</span>
                      <span class="delivery__card-body">{{ screen.label }}</span>
                      <span class="delivery__card-meta">{{ screen.routePath || 'No route path' }}</span>
                    </li>
                  }
                </ul>
              } @else {
                <p class="delivery__muted">No linked screens</p>
              }
            </section>

            <section class="delivery__section" data-testid="delivery-apis">
              <h5 class="delivery__section-title">APIs</h5>
              @if (story.apis.length > 0) {
                <ul class="delivery__cards">
                  @for (api of story.apis; track api.contractId) {
                    <li class="delivery__card">
                      <span class="delivery__card-title">{{ api.contractId }}</span>
                      <span class="delivery__card-body">{{ api.method || 'CALL' }} {{ api.path || 'Unspecified path' }}</span>
                    </li>
                  }
                </ul>
              } @else {
                <p class="delivery__muted">No linked APIs</p>
              }
            </section>

            <section class="delivery__section" data-testid="delivery-gaps">
              <h5 class="delivery__section-title">Graph Gaps</h5>
              @if (story.gaps.length > 0) {
                <ul class="delivery__cards">
                  @for (gap of story.gaps; track gap.gapId) {
                    <li class="delivery__card delivery__card--warning">
                      <span class="delivery__card-title">{{ gap.gapId }}</span>
                      <span class="delivery__card-body">{{ gap.description || gap.gapType || 'Gap recorded' }}</span>
                      <span class="delivery__card-meta">{{ gap.severity || 'UNSPECIFIED' }}</span>
                    </li>
                  }
                </ul>
              } @else {
                <p class="delivery__muted">No graph gaps linked</p>
              }
            </section>

            <section class="delivery__section" data-testid="delivery-bugs">
              <h5 class="delivery__section-title">Bugs</h5>
              @if (story.bugs.length > 0) {
                <ul class="delivery__cards">
                  @for (bug of story.bugs; track bug.bugId) {
                    <li class="delivery__card delivery__card--danger">
                      <span class="delivery__card-title">{{ bug.externalKey || bug.bugId }}</span>
                      <span class="delivery__card-body">{{ bug.summary || 'Bug linked to this story' }}</span>
                      <span class="delivery__card-meta">{{ bug.severity || 'UNSPECIFIED' }}</span>
                    </li>
                  }
                </ul>
              } @else {
                <p class="delivery__muted">No linked bugs</p>
              }
            </section>

            <section class="delivery__section" data-testid="delivery-external">
              <h5 class="delivery__section-title">External Artifacts</h5>
              @if (story.externalArtifacts.length > 0) {
                <div class="delivery__external-stack">
                  <ul class="delivery__cards">
                    @for (artifact of story.externalArtifacts; track artifact.externalId) {
                      <li>
                        <button
                          type="button"
                          class="delivery__card delivery__card--interactive"
                          [class.delivery__card--selected]="artifact.externalId === state.selectedExternalArtifactId()"
                          [attr.data-testid]="'delivery-external-' + artifact.externalId"
                          (click)="state.selectExternalArtifact(artifact.externalId)"
                        >
                          <span class="delivery__card-title">{{ artifact.title || artifact.externalType || artifact.externalId }}</span>
                          <span class="delivery__card-body">{{ artifact.system || 'External' }} · {{ artifact.key || artifact.externalId }}</span>
                          <div class="delivery__meta-row">
                            <span class="delivery__chip">{{ artifact.workflowState || 'Unmapped state' }}</span>
                            <span class="delivery__chip">{{ artifact.syncStatus || 'UNKNOWN' }}</span>
                            @if (artifact.priority) {
                              <span class="delivery__chip">Priority {{ artifact.priority }}</span>
                            }
                          </div>
                          @if (artifact.owner || artifact.projectScope) {
                            <span class="delivery__card-meta">
                              {{ artifact.owner || 'Unassigned' }}
                              @if (artifact.projectScope) {
                                <span> · {{ artifact.projectScope }}</span>
                              }
                            </span>
                          }
                        </button>
                      </li>
                    }
                  </ul>

                  @if (state.selectedExternalArtifact(); as externalArtifact) {
                    <div class="delivery__external-detail" data-testid="delivery-external-detail">
                      <div class="delivery__external-header">
                        <div>
                          <p class="delivery__eyebrow">External Alignment Detail</p>
                          <h6 class="delivery__external-title">{{ externalArtifact.title || externalArtifact.key || externalArtifact.externalId }}</h6>
                          <div class="delivery__detail-meta">
                            <span>{{ externalArtifact.system || 'External' }}</span>
                            <span>{{ externalArtifact.externalType || 'Artifact' }}</span>
                            @if (externalArtifact.key) {
                              <span>{{ externalArtifact.key }}</span>
                            }
                          </div>
                        </div>

                        @if (externalArtifact.url) {
                          <a
                            class="delivery__action-btn delivery__action-btn--link"
                            [href]="externalArtifact.url"
                            target="_blank"
                            rel="noreferrer"
                          >
                            Open source
                          </a>
                        }
                      </div>

                      <div class="delivery__meta-row">
                        <span class="delivery__chip">{{ externalArtifact.syncStatus || 'UNKNOWN' }}</span>
                        @if (externalArtifact.workflowState) {
                          <span class="delivery__chip">{{ externalArtifact.workflowState }}</span>
                        }
                        @if (externalArtifact.priority) {
                          <span class="delivery__chip">Priority {{ externalArtifact.priority }}</span>
                        }
                        @if (externalArtifact.lastSyncedAt) {
                          <span class="delivery__chip">Synced {{ formatTimestamp(externalArtifact.lastSyncedAt) }}</span>
                        }
                      </div>

                      @if (externalArtifact.owner || externalArtifact.reporter || externalArtifact.projectScope) {
                        <p class="delivery__external-copy">
                          {{ externalArtifact.owner || 'Unassigned' }}
                          @if (externalArtifact.reporter) {
                            <span> · reported by {{ externalArtifact.reporter }}</span>
                          }
                          @if (externalArtifact.projectScope) {
                            <span> · {{ externalArtifact.projectScope }}</span>
                          }
                        </p>
                      }

                      @if (externalArtifact.labels.length > 0) {
                        <div class="delivery__meta-row">
                          @for (label of externalArtifact.labels; track label) {
                            <span class="delivery__chip delivery__chip--soft">{{ label }}</span>
                          }
                        </div>
                      }

                      @if (customFieldEntries(externalArtifact).length > 0) {
                        <section class="delivery__external-custom-fields" data-testid="delivery-external-custom-fields">
                          <h6 class="delivery__external-group-title">Custom Fields</h6>
                          <div class="delivery__external-kv-list">
                            @for (entry of customFieldEntries(externalArtifact); track entry.key) {
                              <div class="delivery__external-kv">
                                <span class="delivery__external-kv-key">{{ entry.key }}</span>
                                <span class="delivery__external-kv-value">{{ entry.value }}</span>
                              </div>
                            }
                          </div>
                        </section>
                      }

                      <div class="delivery__external-grid">
                        @for (group of artifactLinkGroups(externalArtifact); track group.key) {
                          <section class="delivery__external-group" [attr.data-testid]="'delivery-external-group-' + group.key">
                            <h6 class="delivery__external-group-title">{{ group.label }}</h6>
                            <ul class="delivery__list delivery__list--plain">
                              @for (item of group.items; track item.externalId) {
                                <li>
                                  <strong>{{ item.title || item.key || item.externalId }}</strong>
                                  <span class="delivery__card-meta"> · {{ item.system || 'External' }} · {{ item.syncStatus || 'UNKNOWN' }}</span>
                                </li>
                              }
                            </ul>
                          </section>
                        }

                        @if (externalArtifact.representedObjects.length > 0) {
                          <section class="delivery__external-group" data-testid="delivery-external-group-represents">
                            <h6 class="delivery__external-group-title">Represents</h6>
                            <ul class="delivery__list delivery__list--plain">
                              @for (item of externalArtifact.representedObjects; track item.id) {
                                <li>
                                  <strong>{{ item.displayName }}</strong>
                                  <span class="delivery__card-meta"> · {{ item.nodeType }}</span>
                                </li>
                              }
                            </ul>
                          </section>
                        }
                      </div>
                    </div>
                  }
                </div>
              } @else {
                <p class="delivery__muted">No external artifacts linked</p>
              }
            </section>
          </div>
        </section>
      }
    </div>
  `,
  styles: [`
    .delivery {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
    }

    .delivery__filters {
      display: grid;
      grid-template-columns: 1.4fr 1fr 1fr;
      gap: var(--tp-space-2);
      padding-bottom: var(--tp-space-3);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
    }

    .delivery__field {
      display: flex;
      flex-direction: column;
      gap: 0.3rem;
    }

    .delivery__label {
      font-size: 0.68rem;
      font-weight: 700;
      letter-spacing: 0.04em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
    }

    .delivery__input,
    .delivery__select {
      width: 100%;
      min-height: var(--tp-touch-target-min-size);
      border: 1px solid var(--tp-border);
      border-radius: var(--tp-space-3);
      background: var(--tp-white);
      color: var(--tp-text);
      font: inherit;
      font-size: 0.78rem;
      padding: var(--tp-space-2) var(--tp-space-3);
    }

    .delivery__input:focus-visible,
    .delivery__select:focus-visible,
    .delivery__story-card:focus-visible,
    .delivery__action-btn:focus-visible,
    .delivery__card--interactive:focus-visible {
      outline: none;
      box-shadow: var(--tp-focus-ring);
    }

    .delivery__groups {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-3);
    }

    .delivery__group {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
    }

    .delivery__group-header {
      display: flex;
      align-items: center;
      justify-content: space-between;
    }

    .delivery__group-title {
      margin: 0;
      font-size: 0.78rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      color: var(--tp-primary-dark);
    }

    .delivery__group-count {
      font-size: 0.72rem;
      font-weight: 700;
      color: var(--tp-text-muted);
    }

    .delivery__story-list {
      display: flex;
      flex-direction: column;
      gap: 0.55rem;
    }

    .delivery__story-card {
      display: flex;
      flex-direction: column;
      gap: 0.45rem;
      width: 100%;
      text-align: left;
      border: 1px solid color-mix(in srgb, var(--tp-border) 72%, var(--tp-white));
      border-radius: var(--nm-radius);
      background: linear-gradient(
        180deg,
        var(--tp-white),
        color-mix(in srgb, var(--tp-surface) 76%, var(--tp-white))
      );
      padding: 0.8rem 0.85rem;
      cursor: pointer;
      transition: border-color 120ms ease, transform 120ms ease, box-shadow 120ms ease, background-color 120ms ease;
    }

    .delivery__story-card:hover {
      border-color: var(--tp-primary);
      background: var(--tp-primary-bg-hover);
      transform: translateY(-1px);
      box-shadow: var(--tp-elevation-hover);
    }

    .delivery__story-card--selected {
      border-color: var(--tp-primary);
      background: var(--tp-primary-bg);
      box-shadow: var(--tp-elevation-default);
    }

    .delivery__story-header,
    .delivery__story-foot,
    .delivery__detail-meta,
    .delivery__detail-actions {
      display: flex;
      align-items: center;
      gap: 0.45rem;
      flex-wrap: wrap;
    }

    .delivery__story-id,
    .delivery__eyebrow {
      font-family: ui-monospace, SFMono-Regular, Menlo, monospace;
      font-size: 0.68rem;
      letter-spacing: 0.04em;
      color: var(--tp-text-muted);
      text-transform: uppercase;
    }

    .delivery__story-title,
    .delivery__detail-title {
      margin: 0;
      font-size: 0.88rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
    }

    .delivery__story-meta,
    .delivery__detail-meta,
    .delivery__muted,
    .delivery__card-meta {
      font-size: 0.72rem;
      color: var(--tp-text-muted);
    }

    .delivery__story-meta {
      display: flex;
      flex-wrap: wrap;
      gap: 0.45rem;
    }

    .delivery__count,
    .delivery__group-count,
    .delivery__badge,
    .delivery__pill,
    .delivery__readiness-chip {
      font-size: 0.68rem;
      font-weight: 700;
      letter-spacing: 0.03em;
    }

    .delivery__badge,
    .delivery__pill,
    .delivery__readiness-chip {
      display: inline-flex;
      align-items: center;
      min-height: var(--tp-touch-target-min-size);
      border-radius: 999px;
      padding-inline: var(--tp-space-3);
    }

    .delivery__badge--green {
      background: var(--tp-toast-success-bg);
      color: var(--dh-complete);
    }

    .delivery__badge--amber {
      background: var(--tp-toast-warn-bg);
      color: var(--dh-specified);
    }

    .delivery__badge--red {
      background: var(--tp-danger-bg);
      color: var(--dh-gap);
    }

    .delivery__pill--ready,
    .delivery__readiness-chip--true {
      background: var(--tp-primary-bg);
      color: var(--dh-complete);
    }

    .delivery__pill--blocked {
      background: var(--tp-danger-bg);
      color: var(--dh-gap);
    }

    .delivery__detail {
      padding-top: var(--tp-space-2);
      border-top: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
    }

    .delivery__detail-header {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-3);
      align-items: flex-start;
      margin-bottom: var(--tp-space-3);
    }

    .delivery__section-grid {
      display: grid;
      gap: var(--tp-space-3);
    }

    .delivery__section {
      padding: 0.85rem 0.9rem;
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-surface) 72%, var(--tp-white));
      border: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
    }

    .delivery__section-title {
      margin: 0 0 0.55rem;
      font-size: 0.73rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      color: var(--tp-text-muted);
    }

    .delivery__readiness-grid,
    .delivery__cards {
      display: flex;
      flex-direction: column;
      gap: 0.45rem;
      margin: 0;
      padding: 0;
      list-style: none;
    }

    .delivery__card {
      display: flex;
      flex-direction: column;
      gap: 0.18rem;
      padding: 0.55rem 0.65rem;
      border-radius: var(--tp-space-3);
      background: var(--tp-white);
      border: 1px solid color-mix(in srgb, var(--tp-border) 16%, transparent);
    }

    .delivery__card--interactive {
      width: 100%;
      text-align: left;
      font: inherit;
      cursor: pointer;
      transition:
        border-color 120ms ease,
        background-color 120ms ease,
        box-shadow 120ms ease,
        transform 120ms ease;
    }

    .delivery__card--interactive:hover {
      border-color: var(--tp-primary);
      transform: translateY(-1px);
      box-shadow: var(--tp-elevation-hover);
    }

    .delivery__card--selected {
      border-color: var(--tp-primary);
      background: var(--tp-primary-bg-hover);
    }

    .delivery__card--warning {
      border-color: var(--tp-toast-warn-border);
    }

    .delivery__card--danger {
      border-color: var(--tp-danger-border);
    }

    .delivery__card-title {
      font-size: 0.74rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
    }

    .delivery__card-body {
      font-size: 0.8rem;
      color: var(--tp-text);
    }

    .delivery__meta-row {
      display: flex;
      flex-wrap: wrap;
      gap: 0.4rem;
    }

    .delivery__chip {
      display: inline-flex;
      align-items: center;
      min-height: 1.65rem;
      border-radius: 999px;
      padding: 0 0.6rem;
      background: color-mix(in srgb, var(--tp-surface) 78%, var(--tp-white));
      color: var(--tp-text-muted);
      font-size: 0.68rem;
      font-weight: 700;
      letter-spacing: 0.02em;
    }

    .delivery__chip--soft {
      background: var(--tp-primary-bg);
      color: var(--tp-primary-dark);
    }

    .delivery__external-stack {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-3);
    }

    .delivery__external-detail {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-3);
      padding: 0.8rem 0.85rem;
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-surface) 74%, var(--tp-white));
      border: 1px solid color-mix(in srgb, var(--tp-primary) 18%, transparent);
    }

    .delivery__external-header {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-3);
      align-items: flex-start;
    }

    .delivery__external-title {
      margin: 0;
      font-size: 0.86rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
    }

    .delivery__external-copy {
      margin: 0;
      font-size: 0.76rem;
      color: var(--tp-text-muted);
    }

    .delivery__external-grid {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(12rem, 1fr));
      gap: var(--tp-space-2);
    }

    .delivery__external-custom-fields {
      padding: 0.65rem 0.7rem;
      border-radius: var(--tp-space-3);
      background: var(--tp-white);
      border: 1px solid color-mix(in srgb, var(--tp-border) 16%, transparent);
    }

    .delivery__external-kv-list {
      display: grid;
      grid-template-columns: repeat(auto-fit, minmax(9.5rem, 1fr));
      gap: 0.5rem;
    }

    .delivery__external-kv {
      display: flex;
      flex-direction: column;
      gap: 0.2rem;
      padding: 0.55rem 0.6rem;
      border-radius: var(--tp-space-3);
      background: color-mix(in srgb, var(--tp-surface) 78%, var(--tp-white));
    }

    .delivery__external-kv-key {
      font-size: 0.68rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      color: var(--tp-text-muted);
    }

    .delivery__external-kv-value {
      font-size: 0.8rem;
      color: var(--tp-text);
    }

    .delivery__external-group {
      padding: 0.65rem 0.7rem;
      border-radius: var(--tp-space-3);
      background: var(--tp-white);
      border: 1px solid color-mix(in srgb, var(--tp-border) 16%, transparent);
    }

    .delivery__external-group-title {
      margin: 0 0 0.4rem;
      font-size: 0.72rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      color: var(--tp-text-muted);
    }

    .delivery__list {
      margin: 0;
      padding-left: 1rem;
      display: flex;
      flex-direction: column;
      gap: 0.35rem;
      font-size: 0.8rem;
    }

    .delivery__empty {
      text-align: center;
      color: var(--tp-text-muted);
      font-style: italic;
      padding: var(--tp-space-6);
    }

    .delivery__action-btn {
      min-height: var(--tp-touch-target-min-size);
      padding: 0 var(--tp-space-4);
      border: 1px solid var(--tp-border);
      border-radius: 999px;
      background: var(--tp-white);
      color: var(--tp-primary-dark);
      font: inherit;
      font-size: 0.75rem;
      font-weight: 700;
      cursor: pointer;
      transition:
        border-color 120ms ease,
        background-color 120ms ease,
        box-shadow 120ms ease;
    }

    .delivery__action-btn--link {
      text-decoration: none;
      align-items: center;
    }

    .delivery__action-btn:hover {
      border-color: var(--tp-primary);
      background: var(--tp-primary-bg-hover);
    }

    .delivery__list--plain {
      list-style: none;
      padding-left: 0;
    }
  `],
})
export class DeliveryPanelComponent {
  readonly state = inject(DesignHubStateService);

  readonly search = signal('');
  readonly statusFilter = signal('all');
  readonly readinessFilter = signal<ReadinessFilter>('all');

  readonly statusOptions = computed(() => {
    const options = new Set<string>(['all']);
    for (const story of this.state.deliveryStories()) {
      options.add(story.status ?? 'unknown');
    }
    return [...options];
  });

  readonly filteredStories = computed(() => {
    const query = this.search().trim().toLowerCase();
    const status = this.statusFilter();
    const readiness = this.readinessFilter();

    return this.state.deliveryStories().filter((story) => {
      if (status !== 'all' && (story.status ?? 'unknown') !== status) {
        return false;
      }

      if (readiness === 'ready' && !story.ready) {
        return false;
      }

      if (readiness === 'not-ready' && story.ready) {
        return false;
      }

      if (!query) {
        return true;
      }

      return [
        story.storyId,
        story.label,
        story.module,
        story.domain,
        story.feature?.title,
      ]
        .filter((value): value is string => Boolean(value))
        .some((value) => value.toLowerCase().includes(query));
    });
  });

  readonly groupedStories = computed(() => {
    const groups = new Map<string, DeliveryStory[]>();
    for (const story of this.filteredStories()) {
      const status = story.status ?? 'unknown';
      const existing = groups.get(status) ?? [];
      existing.push(story);
      groups.set(status, existing);
    }

    return [...groups.entries()].map(([status, stories]) => ({
      status,
      testId: status.toLowerCase().replace(/[^a-z0-9]+/g, '-'),
      stories,
    }));
  });

  inputValue(event: Event): string {
    return (event.target as HTMLInputElement | HTMLSelectElement | null)?.value ?? '';
  }

  setReadinessFilter(value: string): void {
    if (value === 'ready' || value === 'not-ready') {
      this.readinessFilter.set(value);
      return;
    }
    this.readinessFilter.set('all');
  }

  formatStatus(value: string | null | undefined): string {
    const text = value && value.trim().length > 0 ? value : 'UNKNOWN';
    return text
      .toLowerCase()
      .split('_')
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }

  levelClass(value: string | null | undefined): string {
    return (value ?? 'RED').toLowerCase();
  }

  formatScore(value: number): string {
    return value.toFixed(1);
  }

  readinessEntries(story: DeliveryStory): Array<{ key: string; value: boolean }> {
    return Object.entries(story.diagnostics?.readiness ?? {}).map(([key, value]) => ({ key, value }));
  }

  formatReadinessKey(key: string): string {
    return key
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, (value) => value.toUpperCase());
  }

  formatTimestamp(value: string): string {
    return value.replace('T', ' ').replace(/Z$/, ' UTC');
  }

  customFieldEntries(artifact: ExternalArtifactDetail): Array<{ key: string; value: string }> {
    return Object.entries(artifact.customFields ?? {}).map(([key, value]) => ({ key, value: String(value) }));
  }

  artifactLinkGroups(artifact: ExternalArtifactDetail): Array<{
    key: string;
    label: string;
    items: ExternalArtifactLinkSummary[];
  }> {
    return [
      { key: 'parents', label: 'Parents', items: artifact.parents },
      { key: 'children', label: 'Children', items: artifact.children },
      { key: 'dependencies', label: 'Dependencies', items: artifact.dependencies },
      { key: 'related', label: 'Related', items: artifact.relatedArtifacts },
      { key: 'duplicates', label: 'Duplicates', items: artifact.duplicates },
    ].filter((group) => group.items.length > 0);
  }
}
