import { ChangeDetectionStrategy, Component, computed, inject } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-journey-panel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="journeys" data-testid="journey-panel">
      @if (state.personas().length > 0 || state.journeys().length > 0) {
        <div class="journeys__layout">
          <section class="journeys__rail" data-testid="persona-list">
            <div class="journeys__section-head">
              <div>
                <p class="journeys__eyebrow">Exploration</p>
                <h4 class="journeys__section-title">Persona explorer</h4>
              </div>
              <span class="journeys__section-note">{{ state.personas().length }} personas</span>
            </div>

            @for (persona of state.personas(); track persona.personaId) {
              <button
                type="button"
                class="journeys__item journeys__item--persona"
                [class.journeys__item--selected]="persona.personaId === state.selectedPersonaId()"
                [attr.data-testid]="'persona-select-' + persona.personaId"
                (click)="state.selectPersona(persona.personaId)"
              >
                <div class="journeys__item-head">
                  <strong class="journeys__title">{{ persona.name }}</strong>
                  <span class="journeys__badge" [class]="'journeys__badge journeys__badge--' + badgeTone(persona.status)">
                    {{ formatStatus(persona.status) }}
                  </span>
                </div>

                @if (persona.summary) {
                  <p class="journeys__goal">{{ persona.summary }}</p>
                }

                <div class="journeys__meta">
                  <span>{{ persona.journeyCount }} journeys</span>
                  <span>{{ persona.screenCount }} screens</span>
                  <span>{{ persona.storyCount }} stories</span>
                </div>
              </button>
            } @empty {
              <p class="journeys__empty-block">No personas are available in the graph yet.</p>
            }
          </section>

          <section class="journeys__rail" data-testid="journey-list">
            <div class="journeys__section-head">
              <div>
                <p class="journeys__eyebrow">Persona scope</p>
                <h4 class="journeys__section-title">Journeys</h4>
              </div>
              <span class="journeys__section-note">{{ personaJourneys().length }} journeys</span>
            </div>

            @if (state.selectedPersonaSummary(); as personaSummary) {
              <p class="journeys__rail-copy">
                {{ personaSummary.summary || (personaSummary.name + ' has no summary yet.') }}
              </p>
            }

            @for (journey of personaJourneys(); track journey.journeyId) {
              <button
                type="button"
                class="journeys__item"
                [class.journeys__item--selected]="journey.journeyId === state.selectedJourneyId()"
                [attr.data-testid]="'journey-select-' + journey.journeyId"
                (click)="state.selectJourney(journey.journeyId)"
              >
                <div class="journeys__item-head">
                  <strong class="journeys__title">{{ journey.title }}</strong>
                  <span class="journeys__badge" [class]="'journeys__badge journeys__badge--' + badgeTone(journey.status)">
                    {{ formatStatus(journey.status) }}
                  </span>
                </div>

                @if (journey.goalStatement) {
                  <p class="journeys__goal">{{ journey.goalStatement }}</p>
                }

                <div class="journeys__meta">
                  <span>{{ journey.stepCount }} steps</span>
                  @if (journey.screenCount > 0) {
                    <span>{{ journey.screenCount }} screens</span>
                  }
                  @if (journey.roleKey) {
                    <span>{{ journey.roleKey }}</span>
                  }
                </div>
              </button>
            } @empty {
              <p class="journeys__empty-block" data-testid="journeys-empty">
                Select a persona with linked journeys to inspect its graph-backed flow.
              </p>
            }
          </section>

          <div class="journeys__detail-stack">
            @if (state.selectedPersonaTraversal(); as persona) {
              <section class="journeys__hero journeys__hero--persona" data-testid="persona-hero">
                <div class="journeys__hero-copy">
                  <p class="journeys__eyebrow">Persona coverage</p>
                  <h4 class="journeys__hero-title" data-testid="persona-hero-name">{{ persona.name }}</h4>
                  <p class="journeys__hero-body">
                    {{ persona.summary || (persona.name + ' is linked into the current delivery graph without a summary.') }}
                  </p>
                  <span class="journeys__persona-meta">{{ persona.personaId }} · {{ formatStatus(persona.status) }}</span>
                </div>

                <div class="journeys__hero-stats">
                  <article class="journeys__stat">
                    <span class="journeys__stat-label">Journeys</span>
                    <strong class="journeys__stat-value">{{ persona.journeys.length }}</strong>
                  </article>
                  <article class="journeys__stat">
                    <span class="journeys__stat-label">Screens</span>
                    <strong class="journeys__stat-value">{{ persona.screenCount }}</strong>
                  </article>
                  <article class="journeys__stat">
                    <span class="journeys__stat-label">Stories</span>
                    <strong class="journeys__stat-value">{{ persona.storyCount }}</strong>
                  </article>
                  <article class="journeys__stat">
                    <span class="journeys__stat-label">Channels</span>
                    <strong class="journeys__stat-value">{{ persona.channelReach.length }}</strong>
                  </article>
                </div>
              </section>

              <section class="journeys__section" data-testid="journey-persona">
                <div class="journeys__section-head">
                  <div>
                    <p class="journeys__eyebrow">Persona reach</p>
                    <h5 class="journeys__section-title">Context and coverage</h5>
                  </div>
                  <span class="journeys__section-note">{{ persona.screenCount }} screens · {{ persona.storyCount }} stories</span>
                </div>

                <div class="journeys__persona-grid">
                  <article class="journeys__persona-card">
                    <span class="journeys__link-label">Persona</span>
                    <strong class="journeys__persona-title" data-testid="journey-persona-id">{{ persona.name }}</strong>
                    <span class="journeys__persona-meta">{{ persona.personaId }} · {{ formatStatus(persona.status) }}</span>
                    @if (persona.summary) {
                      <p class="journeys__persona-body">{{ persona.summary }}</p>
                    }
                  </article>

                  <article class="journeys__persona-card">
                    <span class="journeys__link-label">Channel reach</span>
                    @if (persona.channelReach.length > 0) {
                      <div class="journeys__chip-row" data-testid="journey-persona-channels">
                        @for (channel of persona.channelReach; track channel.id) {
                          <span class="journeys__graph-chip">{{ channel.displayName }}</span>
                        }
                      </div>
                    } @else {
                      <p class="journeys__empty-inline">No channels linked.</p>
                    }
                  </article>

                  <article class="journeys__persona-card">
                    <span class="journeys__link-label">Role reach</span>
                    @if (persona.roles.length > 0) {
                      <div class="journeys__chip-row">
                        @for (role of persona.roles; track role.id) {
                          <span class="journeys__graph-chip">{{ role.displayName }}</span>
                        }
                      </div>
                    } @else {
                      <p class="journeys__empty-inline">No roles linked.</p>
                    }
                  </article>

                  <article class="journeys__persona-card journeys__persona-card--wide">
                    <span class="journeys__link-label">Related journeys</span>
                    @if (persona.journeys.length > 0) {
                      <ul class="journeys__mini-list" data-testid="journey-persona-journeys">
                        @for (journey of persona.journeys; track journey.journeyId) {
                          <li>
                            <button
                              type="button"
                              class="journeys__mini-link"
                              (click)="state.selectJourney(journey.journeyId)"
                            >
                              <span>{{ journey.title }}</span>
                              <span class="journeys__mini-meta">{{ journey.stepCount }} steps · {{ journey.screenCount }} screens</span>
                            </button>
                          </li>
                        }
                      </ul>
                    } @else {
                      <p class="journeys__empty-inline">No related journeys linked.</p>
                    }
                  </article>
                </div>
              </section>
            } @else if (state.selectedPersonaSummary(); as personaSummary) {
              <p class="journeys__empty-block" data-testid="journey-persona-loading">
                Persona graph data is loading for {{ personaSummary.name }}.
              </p>
            } @else {
              <p class="journeys__empty-block" data-testid="journey-persona-empty">
                Select a persona to inspect its linked journeys and coverage.
              </p>
            }

            @if (state.selectedJourneyTraversal(); as traversal) {
              <section class="journeys__hero" data-testid="journey-traversal">
                <div class="journeys__hero-copy">
                  <p class="journeys__eyebrow">Live graph traversal</p>
                  <h4 class="journeys__hero-title" data-testid="journey-traversal-title">{{ traversal.title }}</h4>
                  @if (traversal.goalStatement) {
                    <p class="journeys__hero-body">{{ traversal.goalStatement }}</p>
                  }
                </div>

                <div class="journeys__hero-stats">
                  <article class="journeys__stat">
                    <span class="journeys__stat-label">Ordered steps</span>
                    <strong class="journeys__stat-value">{{ traversal.steps.length }}</strong>
                  </article>
                  <article class="journeys__stat">
                    <span class="journeys__stat-label">Linked screens</span>
                    <strong class="journeys__stat-value">{{ uniqueScreenCount() }}</strong>
                  </article>
                  <article class="journeys__stat">
                    <span class="journeys__stat-label">Status</span>
                    <strong class="journeys__stat-value">{{ formatStatus(traversal.status) }}</strong>
                  </article>
                </div>
              </section>

              <section class="journeys__section" data-testid="journey-steps">
                <div class="journeys__section-head">
                  <div>
                    <p class="journeys__eyebrow">Sequence</p>
                    <h5 class="journeys__section-title">Journey steps</h5>
                  </div>
                  <span class="journeys__section-note">{{ traversal.steps.length }} linked nodes</span>
                </div>

                <div class="journeys__steps">
                  @for (step of traversal.steps; track step.stepId) {
                    <article class="journeys__step-card" [attr.data-testid]="'journey-traversal-step-' + step.stepId">
                      <div class="journeys__step-index">{{ step.orderIndex + 1 }}</div>
                      <div class="journeys__step-content">
                        <div class="journeys__step-head">
                          <strong class="journeys__step-label">{{ step.label }}</strong>
                          <span class="journeys__step-id">{{ step.stepId }}</span>
                        </div>

                        <div class="journeys__step-links">
                          <div class="journeys__link-block">
                            <span class="journeys__link-label">Screen</span>
                            @if (step.screen; as screen) {
                              <button
                                type="button"
                                class="journeys__graph-chip journeys__graph-chip--action"
                                [attr.data-testid]="'journey-screen-' + screen.id"
                                (click)="openScreen(screen.id)"
                              >
                                {{ screen.displayName }}
                              </button>
                            } @else {
                              <span class="journeys__graph-empty">No screen linked</span>
                            }
                          </div>

                          <div class="journeys__link-block">
                            <span class="journeys__link-label">Touchpoint</span>
                            @if (step.touchpoint; as touchpoint) {
                              <span class="journeys__graph-chip">{{ touchpoint.displayName }}</span>
                            } @else {
                              <span class="journeys__graph-empty">No touchpoint linked</span>
                            }
                          </div>

                          <div class="journeys__link-block">
                            <span class="journeys__link-label">Interaction</span>
                            @if (step.interaction; as interaction) {
                              <span class="journeys__graph-chip">{{ interaction.displayName }}</span>
                            } @else {
                              <span class="journeys__graph-empty">No interaction linked</span>
                            }
                          </div>
                        </div>
                      </div>
                    </article>
                  } @empty {
                    <p class="journeys__empty-block">No graph-backed steps are linked to this journey.</p>
                  }
                </div>
              </section>
            } @else if (state.selectedPersonaId()) {
              <p class="journeys__empty-block" data-testid="journey-traversal-empty">
                Select a journey to inspect its ordered steps and linked screens.
              </p>
            }
          </div>
        </div>
      } @else {
        <p class="journeys__empty" data-testid="journeys-empty">No journeys or personas loaded</p>
      }
    </div>
  `,
  styles: [`
    .journeys {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
    }

    .journeys__layout {
      display: grid;
      grid-template-columns: minmax(220px, 280px) minmax(240px, 320px) minmax(0, 1fr);
      gap: var(--tp-space-4);
      align-items: start;
    }

    .journeys__rail,
    .journeys__hero,
    .journeys__section {
      display: grid;
      gap: var(--tp-space-3);
      padding: var(--tp-space-4);
      border: 1px solid var(--tp-border);
      border-radius: var(--nm-radius);
      background: color-mix(in srgb, var(--tp-white) 76%, var(--tp-surface));
      box-shadow: var(--tp-elevation-default);
    }

    .journeys__detail-stack {
      display: grid;
      gap: var(--tp-space-4);
    }

    .journeys__section-head,
    .journeys__hero-copy {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
    }

    .journeys__eyebrow,
    .journeys__link-label,
    .journeys__stat-label {
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
    }

    .journeys__section-title,
    .journeys__hero-title,
    .journeys__persona-title,
    .journeys__title {
      margin: 0;
      color: var(--tp-text-dark);
    }

    .journeys__section-note,
    .journeys__hero-body,
    .journeys__persona-body,
    .journeys__persona-meta,
    .journeys__empty-inline,
    .journeys__graph-empty,
    .journeys__step-id,
    .journeys__goal,
    .journeys__rail-copy,
    .journeys__mini-meta {
      font-size: 0.8rem;
      color: var(--tp-text-muted);
    }

    .journeys__item {
      display: grid;
      gap: var(--tp-space-2);
      width: 100%;
      text-align: left;
      border: 1px solid var(--tp-border);
      border-radius: var(--tp-space-4);
      background: var(--tp-white);
      padding: var(--tp-space-3);
      cursor: pointer;
      font: inherit;
      transition: border-color 120ms ease, transform 120ms ease, box-shadow 120ms ease, background-color 120ms ease;
    }

    .journeys__item:hover {
      border-color: color-mix(in srgb, var(--tp-primary) 36%, var(--tp-border));
      transform: translateY(-1px);
      box-shadow: var(--tp-elevation-hover);
    }

    .journeys__item:focus-visible,
    .journeys__graph-chip--action:focus-visible,
    .journeys__mini-link:focus-visible {
      outline: none;
      box-shadow: var(--tp-focus-ring);
    }

    .journeys__item--selected {
      border-color: var(--tp-primary);
      box-shadow: 0 12px 24px color-mix(in srgb, var(--tp-primary) 14%, transparent);
      background: var(--tp-primary-bg);
    }

    .journeys__item-head,
    .journeys__hero,
    .journeys__hero-stats,
    .journeys__step-head,
    .journeys__step-links,
    .journeys__chip-row,
    .journeys__meta {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
      align-items: center;
    }

    .journeys__hero {
      grid-template-columns: minmax(0, 1.4fr) minmax(0, 1fr);
      align-items: start;
    }

    .journeys__hero--persona {
      background: linear-gradient(
        145deg,
        color-mix(in srgb, var(--tp-primary-bg) 82%, var(--tp-white)),
        color-mix(in srgb, var(--tp-white) 82%, var(--tp-surface))
      );
    }

    .journeys__hero-stats {
      display: grid;
      grid-template-columns: repeat(4, minmax(0, 1fr));
      gap: var(--tp-space-3);
    }

    .journeys__stat,
    .journeys__persona-card,
    .journeys__step-card {
      display: grid;
      gap: var(--tp-space-2);
      border-radius: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-primary) 18%, var(--tp-border));
      background: var(--tp-white);
      padding: var(--tp-space-3);
    }

    .journeys__stat-value {
      font-size: 1.15rem;
      color: var(--tp-primary-dark);
    }

    .journeys__badge {
      font-size: 0.68rem;
      font-weight: 700;
      padding: 0.2rem 0.55rem;
      border-radius: 999px;
      text-transform: uppercase;
    }

    .journeys__badge--good {
      background: color-mix(in srgb, var(--tp-success) 18%, transparent);
      color: var(--tp-success-dark);
    }

    .journeys__badge--warn {
      background: color-mix(in srgb, var(--tp-warning) 18%, transparent);
      color: var(--tp-warning-dark);
    }

    .journeys__badge--danger {
      background: color-mix(in srgb, var(--tp-danger) 14%, transparent);
      color: var(--tp-danger);
    }

    .journeys__badge--neutral {
      background: color-mix(in srgb, var(--tp-text-muted) 12%, transparent);
      color: var(--tp-text-muted);
    }

    .journeys__steps,
    .journeys__persona-grid {
      display: grid;
      gap: var(--tp-space-3);
    }

    .journeys__persona-grid {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }

    .journeys__persona-card--wide {
      grid-column: 1 / -1;
    }

    .journeys__step-card {
      grid-template-columns: auto minmax(0, 1fr);
      align-items: start;
    }

    .journeys__step-index {
      width: 2rem;
      height: 2rem;
      border-radius: 999px;
      display: grid;
      place-items: center;
      background: var(--tp-primary);
      color: var(--tp-white);
      font-size: 0.82rem;
      font-weight: 700;
    }

    .journeys__step-content {
      display: grid;
      gap: var(--tp-space-2);
      min-width: 0;
    }

    .journeys__link-block {
      display: grid;
      gap: var(--tp-space-1);
      min-width: 0;
      flex: 1 1 180px;
    }

    .journeys__graph-chip {
      display: inline-flex;
      align-items: center;
      gap: var(--tp-space-1);
      padding: 0.4rem 0.7rem;
      border-radius: 999px;
      border: 1px solid color-mix(in srgb, var(--tp-primary) 20%, var(--tp-border));
      background: color-mix(in srgb, var(--tp-primary-bg) 64%, var(--tp-white));
      color: var(--tp-primary-dark);
      font-size: 0.78rem;
      font-weight: 600;
    }

    .journeys__graph-chip--action,
    .journeys__mini-link {
      cursor: pointer;
      font: inherit;
      transition: border-color 120ms ease, transform 120ms ease;
    }

    .journeys__graph-chip--action:hover,
    .journeys__mini-link:hover {
      border-color: var(--tp-primary);
      transform: translateY(-1px);
    }

    .journeys__mini-list {
      display: grid;
      gap: var(--tp-space-2);
      margin: 0;
      padding: 0;
      list-style: none;
    }

    .journeys__mini-link {
      display: flex;
      justify-content: space-between;
      align-items: center;
      gap: var(--tp-space-2);
      width: 100%;
      text-align: left;
      padding: 0.55rem 0.75rem;
      border-radius: var(--tp-space-3);
      border: 1px solid var(--tp-border);
      background: color-mix(in srgb, var(--tp-surface) 50%, var(--tp-white));
      color: var(--tp-text-dark);
    }

    .journeys__meta {
      font-size: 0.74rem;
      color: var(--tp-text-muted);
    }

    .journeys__empty,
    .journeys__empty-block {
      padding: var(--tp-space-4);
      border-radius: var(--tp-space-4);
      border: 1px dashed var(--tp-border);
      background: color-mix(in srgb, var(--tp-surface) 68%, var(--tp-white));
      color: var(--tp-text-muted);
      text-align: center;
    }

    @media (max-width: 1320px) {
      .journeys__layout {
        grid-template-columns: minmax(220px, 280px) minmax(0, 1fr);
      }

      .journeys__detail-stack {
        grid-column: 1 / -1;
      }
    }

    @media (max-width: 1100px) {
      .journeys__hero {
        grid-template-columns: minmax(0, 1fr);
      }

      .journeys__hero-stats,
      .journeys__persona-grid {
        grid-template-columns: repeat(2, minmax(0, 1fr));
      }
    }

    @media (max-width: 820px) {
      .journeys__layout,
      .journeys__hero-stats,
      .journeys__persona-grid,
      .journeys__mini-link {
        grid-template-columns: minmax(0, 1fr);
      }

      .journeys__mini-link {
        display: grid;
      }
    }

    @media (max-width: 680px) {
      .journeys__step-card {
        grid-template-columns: 1fr;
      }

      .journeys__step-index {
        width: 1.8rem;
        height: 1.8rem;
      }
    }
  `],
})
export class JourneyPanelComponent {
  readonly state = inject(DesignHubStateService);

  readonly personaJourneys = computed(() => {
    const selectedPersonaId = this.state.selectedPersonaId();
    const traversal = this.state.selectedPersonaTraversal();
    const goalById = new Map(this.state.journeys().map((journey) => [journey.journeyId, journey.goalStatement]));
    const roleById = new Map(this.state.journeys().map((journey) => [journey.journeyId, journey.roleKey]));

    if (traversal && traversal.personaId === selectedPersonaId && traversal.journeys.length > 0) {
      return traversal.journeys.map((journey) => ({
        journeyId: journey.journeyId,
        title: journey.title,
        status: journey.status,
        stepCount: journey.stepCount,
        screenCount: journey.screenCount,
        goalStatement: goalById.get(journey.journeyId) ?? null,
        roleKey: roleById.get(journey.journeyId) ?? null,
      }));
    }

    return this.state.journeys()
      .filter((journey) => !selectedPersonaId || journey.personaId === selectedPersonaId)
      .map((journey) => ({
        journeyId: journey.journeyId,
        title: journey.title,
        status: journey.designStatus,
        stepCount: journey.steps.length,
        screenCount: 0,
        goalStatement: journey.goalStatement || null,
        roleKey: journey.roleKey,
      }));
  });

  readonly uniqueScreenCount = computed(() => {
    const traversal = this.state.selectedJourneyTraversal();
    if (!traversal) {
      return 0;
    }

    return new Set(
      traversal.steps
        .map((step) => step.screen?.id)
        .filter((screenId): screenId is string => Boolean(screenId))
    ).size;
  });

  openScreen(surfaceId: string): void {
    this.state.selectScreen(surfaceId);
  }

  badgeTone(status: string | null | undefined): string {
    const normalized = status?.toLowerCase() ?? '';

    if (['complete', 'approved', 'green', 'ready', 'implemented'].some((token) => normalized.includes(token))) {
      return 'good';
    }

    if (['specified', 'identified', 'amber', 'definition', 'defined', 'implementation'].some((token) => normalized.includes(token))) {
      return 'warn';
    }

    if (['not_started', 'red', 'blocked', 'critical'].some((token) => normalized.includes(token))) {
      return 'danger';
    }

    return 'neutral';
  }

  formatStatus(status: string | null | undefined): string {
    return status?.replaceAll('_', ' ') ?? 'Unspecified';
  }
}
