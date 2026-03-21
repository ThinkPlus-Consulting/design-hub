import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-screen-detail',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    @if (state.selectedScreen(); as s) {
      <div class="screen-detail" data-testid="screen-detail">
        <!-- Properties -->
        <section class="screen-detail__section">
          <h4 class="screen-detail__heading">Properties</h4>
          <dl class="screen-detail__props">
            <dt>Surface ID</dt><dd data-testid="prop-surfaceId">{{ s.surfaceId }}</dd>
            <dt>Module</dt><dd data-testid="prop-module">{{ s.module }}</dd>
            <dt>Route</dt><dd data-testid="prop-route">{{ s.routePath ?? 'N/A' }}</dd>
            <dt>Design</dt>
            <dd>
              <span class="screen-detail__badge" [class]="'screen-detail__badge--' + s.designStatus.toLowerCase()" data-testid="prop-design">
                {{ s.designStatus }}
              </span>
            </dd>
            <dt>Prototype</dt>
            <dd>
              <span class="screen-detail__badge" [class]="'screen-detail__badge--' + s.prototypeStatus.toLowerCase()" data-testid="prop-prototype">
                {{ s.prototypeStatus }}
              </span>
            </dd>
            <dt>Delivery</dt>
            <dd>
              <span class="screen-detail__badge" [class]="'screen-detail__badge--' + s.deliveryStatus.toLowerCase()" data-testid="prop-delivery">
                {{ s.deliveryStatus }}
              </span>
            </dd>
            <dt>UX Spec</dt><dd data-testid="prop-ux">{{ s.uxSpecRef || 'None' }}</dd>
            <dt>Roles</dt>
            <dd data-testid="prop-roles">
              @if (state.selectedScreenRoles().length > 0) {
                <div class="screen-detail__chips">
                  @for (role of state.selectedScreenRoles(); track role.roleKey) {
                    <span
                      class="screen-detail__chip"
                      [attr.title]="role.roleKey"
                      [attr.data-testid]="'role-chip-' + role.roleKey"
                    >
                      <span class="screen-detail__chip-label">{{ role.displayName }}</span>
                      @if (role.roleGroup) {
                        <span class="screen-detail__chip-meta">{{ role.roleGroup }}</span>
                      }
                    </span>
                  }
                </div>
              } @else {
                None
              }
            </dd>
            <dt>Personas</dt><dd data-testid="prop-personas">{{ s.personaIds.join(', ') || 'None' }}</dd>
          </dl>
        </section>

        <!-- Stories -->
        @if (state.selectedScreenStories().length > 0) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Stories</h4>
            <ul class="screen-detail__entity-list">
              @for (story of state.selectedScreenStories(); track story.storyId) {
                <li>
                  <button
                    class="screen-detail__entity-card screen-detail__entity-card--button"
                    data-testid="story-item"
                    (click)="state.focusStory(story.storyId)"
                  >
                    <span class="screen-detail__entity-title">{{ story.storyId }}</span>
                    <span class="screen-detail__entity-meta">
                      {{ story.module || 'Unknown module' }}
                      @if (story.domain) {
                        <span> · {{ story.domain }}</span>
                      }
                      @if (story.storyNumber) {
                        <span> · #{{ story.storyNumber }}</span>
                      }
                    </span>
                  </button>
                </li>
              }
            </ul>
          </section>
        }

        <!-- Gaps -->
        @if (s._legacy.gaps.length > 0) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Gaps ({{ s._legacy.gaps.length }})</h4>
            @for (gap of s._legacy.gaps; track gap.desc) {
              <div class="screen-detail__gap" [class]="'screen-detail__gap--' + gap.type" data-testid="gap-item">
                <span class="screen-detail__gap-severity">{{ gap.severity }}</span>
                <span class="screen-detail__gap-desc">{{ gap.desc }}</span>
              </div>
            }
          </section>
        }

        <!-- Content elements -->
        @if (s._legacy.content.length > 0) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Content ({{ s._legacy.content.length }})</h4>
            <table class="screen-detail__table" data-testid="content-table">
              <thead>
                <tr><th>Element</th><th>Type</th><th>Description</th></tr>
              </thead>
              <tbody>
                @for (c of s._legacy.content; track c.element) {
                  <tr>
                    <td>{{ c.element }}</td>
                    <td>{{ c.type }}</td>
                    <td>{{ c.description }}</td>
                  </tr>
                }
              </tbody>
            </table>
          </section>
        }

        <!-- Transitions -->
        @if (s._legacy.transitions.length > 0) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Transitions</h4>
            <ul class="screen-detail__list">
              @for (t of s._legacy.transitions; track t) {
                <li>{{ t }}</li>
              }
            </ul>
          </section>
        }

        <!-- Error codes -->
        @if (s._legacy.errorCodes.length > 0) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Error Codes</h4>
            <ul class="screen-detail__list screen-detail__list--code">
              @for (e of s._legacy.errorCodes; track e) {
                <li>{{ e }}</li>
              }
            </ul>
          </section>
        }

        <!-- Confirmations -->
        @if (s._legacy.confirmations.length > 0) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Confirmations</h4>
            <ul class="screen-detail__list">
              @for (c of s._legacy.confirmations; track c) {
                <li>{{ c }}</li>
              }
            </ul>
          </section>
        }

        <!-- Source Refs -->
        @if (s.sourceRefs.length > 0) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Source References</h4>
            <ul class="screen-detail__list screen-detail__list--code">
              @for (ref of s.sourceRefs; track ref) {
                <li>{{ ref }}</li>
              }
            </ul>
          </section>
        }

        <!-- Notes -->
        @if (s.notes) {
          <section class="screen-detail__section">
            <h4 class="screen-detail__heading">Notes</h4>
            <p class="screen-detail__notes" data-testid="notes">{{ s.notes }}</p>
          </section>
        }
      </div>
    } @else {
      <p class="screen-detail__empty" data-testid="no-selection">Select a screen to view details</p>
    }
  `,
  styles: [`
    .screen-detail {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
    }

    .screen-detail__section {
      padding-bottom: var(--tp-space-3);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);

      &:last-child { border-bottom: none; }
    }

    .screen-detail__heading {
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      color: var(--tp-text-muted);
      margin-bottom: var(--tp-space-2);
    }

    .screen-detail__props {
      display: grid;
      grid-template-columns: auto 1fr;
      gap: var(--tp-space-1) var(--tp-space-3);
      font-size: 0.8rem;

      dt { font-weight: 600; color: var(--tp-text-muted); }
      dd { margin: 0; overflow-wrap: anywhere; }
    }

    .screen-detail__chips {
      display: flex;
      flex-wrap: wrap;
      gap: 0.4rem;
    }

    .screen-detail__chip {
      display: inline-flex;
      align-items: center;
      gap: 0.35rem;
      padding: 0.22rem 0.55rem;
      border-radius: 999px;
      background: var(--tp-primary-bg);
      border: 1px solid color-mix(in srgb, var(--tp-primary) 16%, transparent);
      color: var(--tp-primary-dark);
      line-height: 1.2;
    }

    .screen-detail__chip-label {
      font-weight: 600;
    }

    .screen-detail__chip-meta {
      font-size: 0.68rem;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      color: var(--tp-text-muted);
    }

    .screen-detail__badge {
      font-size: 0.7rem;
      font-weight: 600;
      padding: 2px 8px;
      border-radius: 10px;
      text-transform: uppercase;

      &--complete { background: color-mix(in srgb, var(--dh-complete) 15%, transparent); color: var(--dh-complete); }
      &--specified { background: color-mix(in srgb, var(--dh-specified) 15%, transparent); color: var(--dh-specified); }
      &--not_started { background: color-mix(in srgb, var(--dh-not-started) 15%, transparent); color: var(--dh-not-started); }
      &--prototyped { background: color-mix(in srgb, var(--dh-complete) 15%, transparent); color: var(--dh-complete); }
      &--integrated { background: color-mix(in srgb, var(--dh-complete) 15%, transparent); color: var(--dh-complete); }
      &--tested { background: color-mix(in srgb, var(--dh-complete) 22%, transparent); color: var(--tp-primary-dark); }
    }

    .screen-detail__list {
      margin: 0;
      padding-left: var(--tp-space-4);
      font-size: 0.8rem;
      line-height: 1.6;

      &--code {
        font-family: monospace;
        font-size: 0.75rem;
      }
    }

    .screen-detail__entity-list {
      display: flex;
      flex-direction: column;
      gap: 0.55rem;
      margin: 0;
      padding: 0;
      list-style: none;
    }

    .screen-detail__entity-card {
      display: flex;
      flex-direction: column;
      gap: 0.18rem;
      padding: 0.65rem 0.8rem;
      border-radius: 0.7rem;
      background: color-mix(in srgb, var(--tp-primary) 5%, transparent);
      border: 1px solid color-mix(in srgb, var(--tp-primary) 12%, transparent);
    }

    .screen-detail__entity-card--button {
      width: 100%;
      text-align: left;
      cursor: pointer;
      font: inherit;
      transition: border-color 120ms ease, transform 120ms ease;

      &:hover {
        border-color: color-mix(in srgb, var(--tp-primary) 38%, transparent);
        transform: translateY(-1px);
      }
    }

    .screen-detail__entity-title {
      font-size: 0.84rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
    }

    .screen-detail__entity-meta {
      font-size: 0.75rem;
      color: var(--tp-text-muted);
    }

    .screen-detail__gap {
      display: flex;
      gap: var(--tp-space-2);
      padding: var(--tp-space-2);
      border-radius: 6px;
      font-size: 0.78rem;
      margin-bottom: var(--tp-space-1);

      &--warning { background: color-mix(in srgb, var(--tp-warning) 18%, transparent); }
      &--error { background: color-mix(in srgb, var(--tp-danger) 8%, transparent); }
      &--info { background: color-mix(in srgb, var(--tp-primary) 12%, transparent); }
    }

    .screen-detail__gap-severity {
      font-weight: 700;
      font-size: 0.7rem;
      text-transform: uppercase;
      flex-shrink: 0;
    }

    .screen-detail__gap-desc {
      flex: 1;
    }

    .screen-detail__table {
      width: 100%;
      border-collapse: collapse;
      font-size: 0.78rem;

      th, td {
        padding: var(--tp-space-1) var(--tp-space-2);
        text-align: left;
        border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
      }

      th {
        font-weight: 600;
        font-size: 0.7rem;
        text-transform: uppercase;
        color: var(--tp-text-muted);
      }
    }

    .screen-detail__notes {
      font-size: 0.8rem;
      line-height: 1.5;
      color: var(--tp-text);
      font-style: italic;
    }

    .screen-detail__empty {
      text-align: center;
      color: var(--tp-text-muted);
      padding: var(--tp-space-8);
      font-style: italic;
    }
  `],
})
export class ScreenDetailComponent {
  readonly state = inject(DesignHubStateService);
}
