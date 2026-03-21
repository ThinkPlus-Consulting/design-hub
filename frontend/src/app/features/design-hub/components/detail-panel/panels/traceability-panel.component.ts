import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { GraphNodeReference } from '../../../../../models';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

interface TraceabilitySpineStep {
  key: string;
  label: string;
  node: GraphNodeReference | null;
}

interface TraceabilityLane {
  key: string;
  label: string;
  emptyLabel: string;
  nodes: GraphNodeReference[];
}

@Component({
  selector: 'app-traceability-panel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="traceability" data-testid="traceability-panel">
      @if (state.selectedDeliveryStory(); as story) {
        <section class="traceability__hero">
          <div class="traceability__hero-copy">
            <p class="traceability__eyebrow">Story Traceability</p>
            <h4 class="traceability__title">{{ story.label }}</h4>
            <div class="traceability__meta">
              <span data-testid="traceability-story-id">{{ story.storyId }}</span>
              @if (story.module) {
                <span>{{ story.module }}</span>
              }
              @if (story.domain) {
                <span>{{ story.domain }}</span>
              }
            </div>
          </div>

          <div class="traceability__hero-stats">
            <article class="traceability__stat">
              <span class="traceability__stat-label">Spine completeness</span>
              <strong class="traceability__stat-value" data-testid="traceability-spine-count">
                {{ linkedSpineCount() }}/{{ spine().length }}
              </strong>
            </article>
            <article class="traceability__stat">
              <span class="traceability__stat-label">Downstream nodes</span>
              <strong class="traceability__stat-value">{{ downstreamCount() }}</strong>
            </article>
          </div>
        </section>

        @if (state.storyTraceability(); as traceability) {
          @if (traceability.missingSpineSegments.length > 0) {
            <section class="traceability__banner" data-testid="traceability-missing">
              <p class="traceability__banner-title">Missing implementation spine coverage</p>
              <p class="traceability__banner-body">
                {{ traceability.missingSpineSegments.join(', ') }}
              </p>
            </section>
          }

          <section class="traceability__section" data-testid="traceability-spine">
            <div class="traceability__section-head">
              <div>
                <p class="traceability__section-kicker">Upstream to delivery</p>
                <h5 class="traceability__section-title">Implementation spine</h5>
              </div>
              <span class="traceability__section-note">Objective to story path</span>
            </div>

            <div class="traceability__spine">
              @for (step of spine(); track step.key) {
                <article
                  class="traceability__node"
                  [class.traceability__node--missing]="step.node === null"
                  [attr.data-testid]="'traceability-node-' + step.key"
                >
                  <span class="traceability__node-label">{{ step.label }}</span>
                  @if (step.node; as node) {
                    <strong class="traceability__node-title">{{ node.displayName }}</strong>
                    <span class="traceability__node-id">{{ node.id }}</span>
                  } @else {
                    <strong class="traceability__node-title">Not linked</strong>
                    <span class="traceability__node-id">{{ step.label }} missing from graph</span>
                  }
                </article>
                @if (!$last) {
                  <span class="traceability__spine-link" aria-hidden="true">→</span>
                }
              }
            </div>
          </section>

          <div class="traceability__grid">
            @for (lane of lanes(); track lane.key) {
              <section class="traceability__section" [attr.data-testid]="'traceability-' + lane.key">
                <div class="traceability__section-head">
                  <div>
                    <p class="traceability__section-kicker">Delivery attachments</p>
                    <h5 class="traceability__section-title">{{ lane.label }}</h5>
                  </div>
                  <span class="traceability__section-note">{{ lane.nodes.length }} linked</span>
                </div>

                @if (lane.nodes.length > 0) {
                  <ul class="traceability__list">
                    @for (node of lane.nodes; track node.id) {
                      <li class="traceability__list-item">
                        <div class="traceability__list-copy">
                          <strong class="traceability__list-title">{{ node.displayName }}</strong>
                          <span class="traceability__list-id">{{ node.id }}</span>
                        </div>
                        @if (node.status) {
                          <span class="traceability__status">{{ formatStatus(node.status) }}</span>
                        }
                      </li>
                    }
                  </ul>
                } @else {
                  <p class="traceability__empty-block">{{ lane.emptyLabel }}</p>
                }
              </section>
            }
          </div>
        } @else {
          <p class="traceability__empty" data-testid="traceability-loading">
            Traceability data is loading for the selected story.
          </p>
        }
      } @else {
        <p class="traceability__empty" data-testid="traceability-empty">
          Select a delivery story to inspect its upstream and downstream traceability.
        </p>
      }
    </div>
  `,
  styles: [`
    .traceability {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
      color: var(--tp-text);
    }

    .traceability__hero,
    .traceability__section {
      border: 1px solid var(--tp-border);
      border-radius: var(--nm-radius);
      background: color-mix(in srgb, var(--tp-white) 76%, var(--tp-surface));
      box-shadow: var(--tp-elevation-default);
    }

    .traceability__hero {
      display: grid;
      grid-template-columns: minmax(0, 1.5fr) minmax(0, 1fr);
      gap: var(--tp-space-4);
      padding: var(--tp-space-4);
    }

    .traceability__hero-copy,
    .traceability__hero-stats,
    .traceability__section-head,
    .traceability__list-copy {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
      min-width: 0;
    }

    .traceability__eyebrow,
    .traceability__section-kicker,
    .traceability__node-label,
    .traceability__stat-label {
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
    }

    .traceability__title,
    .traceability__section-title {
      margin: 0;
      color: var(--tp-text-dark);
    }

    .traceability__title {
      font-size: 1rem;
    }

    .traceability__section-title {
      font-size: 0.9rem;
    }

    .traceability__meta,
    .traceability__section-note {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
      font-size: 0.78rem;
      color: var(--tp-text-muted);
    }

    .traceability__hero-stats {
      align-content: start;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      display: grid;
      gap: var(--tp-space-3);
    }

    .traceability__stat {
      display: grid;
      gap: var(--tp-space-1);
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-primary) 18%, var(--tp-border));
      background: var(--tp-primary-bg);
    }

    .traceability__stat-value {
      font-size: 1.2rem;
      color: var(--tp-primary-dark);
    }

    .traceability__banner {
      display: grid;
      gap: var(--tp-space-1);
      padding: var(--tp-space-3) var(--tp-space-4);
      border-radius: var(--tp-space-4);
      border: 1px solid var(--tp-toast-warn-border);
      background: var(--tp-toast-warn-bg);
      color: var(--tp-warning-dark);
    }

    .traceability__banner-title {
      font-size: 0.8rem;
      font-weight: 700;
    }

    .traceability__banner-body {
      font-size: 0.78rem;
    }

    .traceability__section {
      display: grid;
      gap: var(--tp-space-4);
      padding: var(--tp-space-4);
    }

    .traceability__spine {
      display: grid;
      gap: var(--tp-space-2);
      grid-template-columns: repeat(9, minmax(0, 1fr));
      align-items: center;
    }

    .traceability__node {
      grid-column: span 1;
      display: grid;
      gap: var(--tp-space-1);
      min-height: calc(var(--tp-touch-target-min-size) + var(--tp-space-4));
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-primary) 22%, var(--tp-border));
      background: color-mix(in srgb, var(--tp-primary-bg) 62%, var(--tp-white));
    }

    .traceability__node--missing {
      border-color: var(--tp-toast-warn-border);
      background: color-mix(in srgb, var(--tp-toast-warn-bg) 80%, var(--tp-white));
    }

    .traceability__node-title,
    .traceability__list-title {
      font-size: 0.82rem;
      font-weight: 700;
      color: var(--tp-text-dark);
      overflow-wrap: anywhere;
    }

    .traceability__node-id,
    .traceability__list-id {
      font-size: 0.74rem;
      color: var(--tp-text-muted);
      overflow-wrap: anywhere;
    }

    .traceability__spine-link {
      display: flex;
      align-items: center;
      justify-content: center;
      font-size: 1rem;
      color: var(--tp-primary);
    }

    .traceability__grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: var(--tp-space-4);
    }

    .traceability__list {
      display: grid;
      gap: var(--tp-space-2);
      margin: 0;
      padding: 0;
      list-style: none;
    }

    .traceability__list-item {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-3);
      align-items: start;
      padding: var(--tp-space-3);
      border: 1px solid color-mix(in srgb, var(--tp-border) 68%, var(--tp-white));
      border-radius: var(--tp-space-3);
      background: var(--tp-white);
    }

    .traceability__status {
      display: inline-flex;
      align-items: center;
      min-height: var(--tp-touch-target-min-size);
      padding-inline: var(--tp-space-3);
      border-radius: 999px;
      background: var(--tp-primary-bg);
      color: var(--tp-primary-dark);
      font-size: 0.74rem;
      font-weight: 700;
      white-space: nowrap;
    }

    .traceability__empty,
    .traceability__empty-block {
      padding: var(--tp-space-6);
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-surface) 86%, var(--tp-white));
      color: var(--tp-text-muted);
      text-align: center;
      font-style: italic;
    }

    .traceability__empty-block {
      padding: var(--tp-space-4);
      border: 1px dashed var(--tp-border);
    }

    @media (max-width: 1100px) {
      .traceability__hero,
      .traceability__grid {
        grid-template-columns: 1fr;
      }

      .traceability__spine {
        grid-template-columns: 1fr;
      }

      .traceability__spine-link {
        transform: rotate(90deg);
        min-height: var(--tp-touch-target-min-size);
      }
    }
  `],
})
export class TraceabilityPanelComponent {
  readonly state = inject(DesignHubStateService);

  readonly spine = computed<TraceabilitySpineStep[]>(() => {
    const traceability = this.state.storyTraceability();
    const selectedStory = this.state.selectedDeliveryStory();

    return [
      { key: 'objective', label: 'Objective', node: traceability?.objective ?? null },
      { key: 'portfolio', label: 'Portfolio', node: traceability?.portfolio ?? null },
      { key: 'epic', label: 'Epic', node: traceability?.epic ?? null },
      { key: 'feature', label: 'Feature', node: traceability?.feature ?? null },
      {
        key: 'story',
        label: 'Story',
        node: traceability?.story ?? (selectedStory
          ? {
              id: selectedStory.storyId,
              nodeType: 'UserStory',
              displayName: selectedStory.label,
              status: selectedStory.status ?? null,
            }
          : null),
      },
    ];
  });

  readonly lanes = computed<TraceabilityLane[]>(() => {
    const traceability = this.state.storyTraceability();

    return [
      {
        key: 'screens',
        label: 'Screens',
        emptyLabel: 'No screens are linked to this story yet.',
        nodes: traceability?.screens ?? [],
      },
      {
        key: 'interactions',
        label: 'Interactions',
        emptyLabel: 'No interactions are linked to this story yet.',
        nodes: traceability?.interactions ?? [],
      },
      {
        key: 'apis',
        label: 'APIs',
        emptyLabel: 'No APIs are linked to this story yet.',
        nodes: traceability?.apis ?? [],
      },
      {
        key: 'tasks',
        label: 'Tasks',
        emptyLabel: 'No implementation tasks are linked to this story yet.',
        nodes: traceability?.tasks ?? [],
      },
      {
        key: 'messages',
        label: 'Messages',
        emptyLabel: 'No message contracts are linked to this story yet.',
        nodes: traceability?.messages ?? [],
      },
      {
        key: 'data-entities',
        label: 'Data entities',
        emptyLabel: 'No data entities are linked to this story yet.',
        nodes: traceability?.dataEntities ?? [],
      },
    ];
  });

  readonly linkedSpineCount = computed(() => this.spine().filter((step) => step.node !== null).length);

  readonly downstreamCount = computed(() =>
    this.lanes().reduce((total, lane) => total + lane.nodes.length, 0)
  );

  formatStatus(value: string): string {
    return value
      .toLowerCase()
      .split('_')
      .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
      .join(' ');
  }
}
