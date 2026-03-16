import { Component, ChangeDetectionStrategy, inject, signal } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-journey-panel',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="journeys" data-testid="journey-panel">
      @for (journey of state.journeys(); track journey.journeyId) {
        <div class="journeys__item" [attr.data-testid]="'journey-' + journey.journeyId">
          <button
            class="journeys__header"
            (click)="toggle(journey.journeyId)"
            [attr.data-testid]="'journey-toggle-' + journey.journeyId"
          >
            <span class="journeys__expand">{{ expandedIds().has(journey.journeyId) ? '-' : '+' }}</span>
            <span class="journeys__title">{{ journey.title }}</span>
            <span class="journeys__badge" [class]="'journeys__badge--' + journey.designStatus.toLowerCase()">
              {{ journey.designStatus }}
            </span>
          </button>

          @if (journey.goalStatement) {
            <p class="journeys__goal">{{ journey.goalStatement }}</p>
          }

          @if (journey.personaId || journey.roleKey) {
            <div class="journeys__meta">
              @if (journey.personaId) { <span>Persona: {{ journey.personaId }}</span> }
              @if (journey.roleKey) { <span>Role: {{ journey.roleKey }}</span> }
            </div>
          }

          @if (expandedIds().has(journey.journeyId)) {
            <div class="journeys__steps" data-testid="journey-steps">
              @for (step of journey.steps; track step.stepId; let i = $index) {
                <div class="journeys__step" [attr.data-testid]="'journey-step-' + step.stepId">
                  <span class="journeys__step-num">{{ i + 1 }}</span>
                  <div class="journeys__step-body">
                    <span class="journeys__step-label">{{ step.label }}</span>
                    @if (step.preCondition) {
                      <span class="journeys__step-condition journeys__step-condition--pre">Pre: {{ step.preCondition }}</span>
                    }
                    @if (step.postCondition) {
                      <span class="journeys__step-condition journeys__step-condition--post">Post: {{ step.postCondition }}</span>
                    }
                    @if (step.interactionRef) {
                      <span class="journeys__step-ref">Interaction: {{ step.interactionRef }}</span>
                    }
                  </div>
                </div>
              } @empty {
                <p class="journeys__no-steps">No steps defined</p>
              }
            </div>

            @if (journey.sourceRefs.length > 0) {
              <div class="journeys__sources">
                Sources: {{ journey.sourceRefs.join(', ') }}
              </div>
            }
          }
        </div>
      } @empty {
        <p class="journeys__empty" data-testid="journeys-empty">No journeys loaded</p>
      }
    </div>
  `,
  styles: [`
    .journeys {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-3);
    }

    .journeys__item {
      border-radius: 8px;
      background: rgba(237, 235, 224, 0.56);
      border: 1px solid rgba(152, 133, 97, 0.22);
      overflow: hidden;
    }

    .journeys__header {
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      width: 100%;
      padding: var(--tp-space-3);
      border: none;
      background: transparent;
      cursor: pointer;
      text-align: left;
      font-family: inherit;
    }

    .journeys__expand {
      font-weight: 700;
      font-size: 1rem;
      width: 20px;
      text-align: center;
      color: var(--tp-primary);
    }

    .journeys__title {
      flex: 1;
      font-size: 0.82rem;
      font-weight: 600;
    }

    .journeys__badge {
      font-size: 0.68rem;
      font-weight: 600;
      padding: 2px 6px;
      border-radius: 8px;
      text-transform: uppercase;

      &--complete { background: rgba(66, 129, 119, 0.15); color: var(--dh-complete); }
      &--specified { background: rgba(152, 133, 97, 0.15); color: var(--dh-specified); }
      &--not_started { background: rgba(185, 167, 121, 0.15); color: var(--dh-not-started); }
    }

    .journeys__goal {
      font-size: 0.78rem;
      color: var(--tp-text-muted);
      padding: 0 var(--tp-space-3) var(--tp-space-2);
      font-style: italic;
    }

    .journeys__meta {
      display: flex;
      gap: var(--tp-space-3);
      padding: var(--tp-space-2) var(--tp-space-3);
      font-size: 0.7rem;
      color: var(--tp-text-muted);
      border-top: 1px solid rgba(152, 133, 97, 0.18);
    }

    .journeys__steps {
      padding: 0 var(--tp-space-3) var(--tp-space-3);
    }

    .journeys__step {
      display: flex;
      gap: var(--tp-space-2);
      padding: var(--tp-space-2) 0;
      border-bottom: 1px solid rgba(152, 133, 97, 0.18);

      &:last-child { border-bottom: none; }
    }

    .journeys__step-num {
      width: 22px;
      height: 22px;
      border-radius: 50%;
      background: var(--tp-primary);
      color: white;
      font-size: 0.7rem;
      font-weight: 700;
      display: flex;
      align-items: center;
      justify-content: center;
      flex-shrink: 0;
    }

    .journeys__step-body {
      display: flex;
      flex-direction: column;
      gap: 2px;
    }

    .journeys__step-label {
      font-size: 0.8rem;
      font-weight: 500;
    }

    .journeys__step-condition {
      font-size: 0.7rem;
      color: var(--tp-text-muted);

      &--pre { color: var(--tp-warning); }
      &--post { color: var(--tp-success); }
    }

    .journeys__step-ref {
      font-size: 0.68rem;
      color: var(--tp-primary);
      font-family: monospace;
    }

    .journeys__no-steps {
      font-size: 0.78rem;
      color: var(--tp-text-muted);
      font-style: italic;
      padding: var(--tp-space-2);
    }

    .journeys__sources {
      font-size: 0.7rem;
      color: var(--tp-text-muted);
      padding: var(--tp-space-2) var(--tp-space-3);
      border-top: 1px solid rgba(152, 133, 97, 0.18);
      font-family: monospace;
    }

    .journeys__empty {
      text-align: center;
      color: var(--tp-text-muted);
      font-style: italic;
      padding: var(--tp-space-8);
    }
  `],
})
export class JourneyPanelComponent {
  readonly state = inject(DesignHubStateService);
  readonly expandedIds = signal<Set<string>>(new Set());

  toggle(id: string): void {
    this.expandedIds.update((set) => {
      const next = new Set(set);
      if (next.has(id)) {
        next.delete(id);
      } else {
        next.add(id);
      }
      return next;
    });
  }
}
