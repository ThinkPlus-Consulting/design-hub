import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-touchpoint-panel',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="touchpoints" data-testid="touchpoint-panel">
      @if (state.channels().length > 0) {
        <div class="touchpoints__layout">
          <section class="touchpoints__list" data-testid="channel-list">
            <div class="touchpoints__section-head">
              <div>
                <p class="touchpoints__eyebrow">Reach</p>
                <h4 class="touchpoints__section-title">Channel view</h4>
              </div>
              <span class="touchpoints__section-note">{{ state.channels().length }} channels</span>
            </div>

            @for (channel of state.channels(); track channel.channelCode) {
              <button
                type="button"
                class="touchpoints__channel-card"
                [class.touchpoints__channel-card--selected]="channel.channelCode === state.selectedChannelCode()"
                [attr.data-testid]="'channel-select-' + channel.channelCode"
                (click)="state.selectChannel(channel.channelCode)"
              >
                <div class="touchpoints__channel-head">
                  <strong class="touchpoints__channel-title">{{ channel.displayName }}</strong>
                  @if (channel.channelType) {
                    <span class="touchpoints__channel-type">{{ channel.channelType }}</span>
                  }
                </div>
                <div class="touchpoints__channel-metrics">
                  <span>{{ channel.touchpointCount }} touchpoints</span>
                  <span>{{ channel.screenCount }} screens</span>
                </div>
              </button>
            }
          </section>

          <div class="touchpoints__detail-stack">
            @if (state.selectedChannelTraversal(); as channel) {
              <section class="touchpoints__hero" data-testid="channel-traversal">
                <div class="touchpoints__hero-copy">
                  <p class="touchpoints__eyebrow">Graph-backed coverage</p>
                  <h4 class="touchpoints__hero-title" data-testid="channel-title">{{ channel.displayName }}</h4>
                  <p class="touchpoints__hero-body">
                    {{ channel.channelCode }}
                    @if (channel.channelType) {
                      <span> · {{ channel.channelType }}</span>
                    }
                  </p>
                </div>

                <div class="touchpoints__hero-stats">
                  <article class="touchpoints__stat">
                    <span class="touchpoints__stat-label">Touchpoints</span>
                    <strong class="touchpoints__stat-value">{{ channel.touchpoints.length }}</strong>
                  </article>
                  <article class="touchpoints__stat">
                    <span class="touchpoints__stat-label">Screens</span>
                    <strong class="touchpoints__stat-value">{{ channel.screens.length }}</strong>
                  </article>
                  <article class="touchpoints__stat">
                    <span class="touchpoints__stat-label">Personas</span>
                    <strong class="touchpoints__stat-value">{{ channel.personaReach.length }}</strong>
                  </article>
                </div>
              </section>

              <section class="touchpoints__section" data-testid="channel-touchpoints">
                <div class="touchpoints__section-head">
                  <div>
                    <p class="touchpoints__eyebrow">Entry points</p>
                    <h5 class="touchpoints__section-title">Touchpoints</h5>
                  </div>
                  <span class="touchpoints__section-note">{{ channel.touchpoints.length }} linked</span>
                </div>

                @if (channel.touchpoints.length > 0) {
                  <div class="touchpoints__cards">
                    @for (tp of channel.touchpoints; track tp.touchpointId) {
                      <article class="touchpoints__item" [attr.data-testid]="'tp-' + tp.touchpointId">
                        <div class="touchpoints__item-header">
                          <strong class="touchpoints__item-label">{{ tp.label }}</strong>
                          <span class="touchpoints__item-id">{{ tp.touchpointId }}</span>
                        </div>

                        <div class="touchpoints__item-row">
                          <span class="touchpoints__item-meta-label">Target screen</span>
                          @if (tp.targetScreen; as screen) {
                            <button
                              type="button"
                              class="touchpoints__chip touchpoints__chip--action"
                              [attr.data-testid]="'channel-screen-link-' + screen.id"
                              (click)="openScreen(screen.id)"
                            >
                              {{ screen.displayName }}
                            </button>
                          } @else {
                            <span class="touchpoints__empty-inline">No target screen linked</span>
                          }
                        </div>

                        @if (tp.entryMechanisms.length > 0) {
                          <div class="touchpoints__item-row">
                            <span class="touchpoints__item-meta-label">Mechanisms</span>
                            <div class="touchpoints__chip-row">
                              @for (mechanism of tp.entryMechanisms; track mechanism) {
                                <span class="touchpoints__chip">{{ mechanism }}</span>
                              }
                            </div>
                          </div>
                        }

                        @if (tp.roleKeys.length > 0) {
                          <p class="touchpoints__item-meta">Roles: {{ tp.roleKeys.join(', ') }}</p>
                        }
                        @if (tp.personaIds.length > 0) {
                          <p class="touchpoints__item-meta">Personas: {{ tp.personaIds.join(', ') }}</p>
                        }
                      </article>
                    }
                  </div>
                } @else {
                  <p class="touchpoints__empty-block">No touchpoints are linked to this channel.</p>
                }
              </section>

              <section class="touchpoints__section">
                <div class="touchpoints__section-head">
                  <div>
                    <p class="touchpoints__eyebrow">Coverage</p>
                    <h5 class="touchpoints__section-title">Screens and personas</h5>
                  </div>
                </div>

                <div class="touchpoints__coverage-grid">
                  <article class="touchpoints__coverage-card" data-testid="channel-screens">
                    <span class="touchpoints__item-meta-label">Reachable screens</span>
                    @if (channel.screens.length > 0) {
                      <div class="touchpoints__chip-row">
                        @for (screen of channel.screens; track screen.id) {
                          <button
                            type="button"
                            class="touchpoints__chip touchpoints__chip--action"
                            (click)="openScreen(screen.id)"
                          >
                            {{ screen.displayName }}
                          </button>
                        }
                      </div>
                    } @else {
                      <p class="touchpoints__empty-inline">No screens linked.</p>
                    }
                  </article>

                  <article class="touchpoints__coverage-card" data-testid="channel-personas">
                    <span class="touchpoints__item-meta-label">Persona reach</span>
                    @if (channel.personaReach.length > 0) {
                      <div class="touchpoints__chip-row">
                        @for (persona of channel.personaReach; track persona.id) {
                          <span class="touchpoints__chip">{{ persona.displayName }}</span>
                        }
                      </div>
                    } @else {
                      <p class="touchpoints__empty-inline">No personas reachable yet.</p>
                    }
                  </article>
                </div>
              </section>

              <section class="touchpoints__section" data-testid="channel-gaps">
                <div class="touchpoints__section-head">
                  <div>
                    <p class="touchpoints__eyebrow">Coverage gaps</p>
                    <h5 class="touchpoints__section-title">Missing screen targets</h5>
                  </div>
                  <span class="touchpoints__section-note">{{ channel.coverageGaps.length }} gaps</span>
                </div>

                @if (channel.coverageGaps.length > 0) {
                  <ul class="touchpoints__gap-list">
                    @for (gap of channel.coverageGaps; track gap.touchpointId) {
                      <li class="touchpoints__gap-item">{{ gap.touchpointId }} · {{ gap.reason }}</li>
                    }
                  </ul>
                } @else {
                  <p class="touchpoints__empty-block">No channel coverage gaps are currently reported.</p>
                }
              </section>
            } @else {
              <p class="touchpoints__empty" data-testid="touchpoints-empty">
                Select a channel to inspect touchpoints, screens, and persona reach.
              </p>
            }
          </div>
        </div>
      } @else {
        <p class="touchpoints__empty" data-testid="touchpoints-empty">No touchpoints available</p>
      }
    </div>
  `,
  styles: [`
    .touchpoints {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
    }

    .touchpoints__layout {
      display: grid;
      grid-template-columns: minmax(240px, 320px) minmax(0, 1fr);
      gap: var(--tp-space-4);
    }

    .touchpoints__list,
    .touchpoints__hero,
    .touchpoints__section {
      display: grid;
      gap: var(--tp-space-3);
      padding: var(--tp-space-4);
      border: 1px solid var(--tp-border);
      border-radius: var(--nm-radius);
      background: color-mix(in srgb, var(--tp-white) 76%, var(--tp-surface));
      box-shadow: var(--tp-elevation-default);
    }

    .touchpoints__detail-stack,
    .touchpoints__section-head {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
    }

    .touchpoints__eyebrow,
    .touchpoints__stat-label,
    .touchpoints__item-meta-label {
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
    }

    .touchpoints__section-title,
    .touchpoints__hero-title,
    .touchpoints__channel-title,
    .touchpoints__item-label {
      margin: 0;
      color: var(--tp-text-dark);
    }

    .touchpoints__section-note,
    .touchpoints__hero-body,
    .touchpoints__item-id,
    .touchpoints__item-meta,
    .touchpoints__empty-inline {
      font-size: 0.8rem;
      color: var(--tp-text-muted);
    }

    .touchpoints__channel-card {
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
      transition: border-color 120ms ease, transform 120ms ease, box-shadow 120ms ease;
    }

    .touchpoints__channel-card:hover,
    .touchpoints__chip--action:hover {
      border-color: var(--tp-primary);
      transform: translateY(-1px);
    }

    .touchpoints__channel-card:focus-visible,
    .touchpoints__chip--action:focus-visible {
      outline: none;
      box-shadow: var(--tp-focus-ring);
    }

    .touchpoints__channel-card--selected {
      border-color: var(--tp-primary);
      background: var(--tp-primary-bg);
      box-shadow: 0 12px 24px color-mix(in srgb, var(--tp-primary) 14%, transparent);
    }

    .touchpoints__channel-head,
    .touchpoints__channel-metrics,
    .touchpoints__hero,
    .touchpoints__hero-stats,
    .touchpoints__item-header,
    .touchpoints__chip-row,
    .touchpoints__coverage-grid {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
      align-items: center;
    }

    .touchpoints__channel-type {
      font-size: 0.68rem;
      font-weight: 700;
      padding: 0.2rem 0.55rem;
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-primary) 16%, transparent);
      color: var(--tp-primary-dark);
    }

    .touchpoints__hero {
      grid-template-columns: minmax(0, 1.4fr) minmax(0, 1fr);
    }

    .touchpoints__hero-stats {
      display: grid;
      grid-template-columns: repeat(3, minmax(0, 1fr));
      gap: var(--tp-space-3);
    }

    .touchpoints__stat,
    .touchpoints__item,
    .touchpoints__coverage-card {
      display: grid;
      gap: var(--tp-space-2);
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-primary) 18%, var(--tp-border));
      background: var(--tp-white);
    }

    .touchpoints__stat-value {
      font-size: 1.15rem;
      color: var(--tp-primary-dark);
    }

    .touchpoints__cards {
      display: grid;
      gap: var(--tp-space-3);
    }

    .touchpoints__item-row {
      display: grid;
      gap: var(--tp-space-1);
    }

    .touchpoints__chip {
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

    .touchpoints__chip--action {
      cursor: pointer;
      font: inherit;
      transition: border-color 120ms ease, transform 120ms ease;
    }

    .touchpoints__gap-list {
      display: grid;
      gap: var(--tp-space-2);
      margin: 0;
      padding: 0;
      list-style: none;
    }

    .touchpoints__gap-item,
    .touchpoints__empty,
    .touchpoints__empty-block {
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-4);
      border: 1px dashed var(--tp-border);
      background: color-mix(in srgb, var(--tp-surface) 68%, var(--tp-white));
      color: var(--tp-text-muted);
    }

    .touchpoints__empty,
    .touchpoints__empty-block {
      text-align: center;
    }

    @media (max-width: 1100px) {
      .touchpoints__layout,
      .touchpoints__hero {
        grid-template-columns: minmax(0, 1fr);
      }
    }

    @media (max-width: 680px) {
      .touchpoints__hero-stats {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class TouchpointPanelComponent {
  readonly state = inject(DesignHubStateService);

  openScreen(surfaceId: string): void {
    this.state.selectScreen(surfaceId);
  }
}
