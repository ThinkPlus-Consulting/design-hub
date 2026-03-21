import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';
import { Interaction } from '../../../../../models';

@Component({
  selector: 'app-interaction-panel',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="interactions" data-testid="interaction-panel">
      @for (ix of filteredInteractions(); track ix.interactionId) {
        <div class="interactions__item" [attr.data-testid]="'ix-' + ix.interactionId">
          <div class="interactions__header">
            <span class="interactions__element">{{ ix.element }}</span>
            <span class="interactions__trigger">{{ ix.trigger }}</span>
          </div>

          @if (ix.effects.length > 0) {
            <div class="interactions__effects">
              @for (fx of ix.effects; track fx.type + (fx.target ?? '')) {
                <span class="interactions__effect-badge" [attr.data-testid]="'fx-' + fx.type">
                  {{ fx.type }}
                  @if (fx.target) {
                    <span class="interactions__effect-target"> -> {{ fx.target }}</span>
                  }
                  @if (fx.targetMode !== 'static') {
                    <span class="interactions__effect-mode">({{ fx.targetMode }})</span>
                  }
                </span>
              }
            </div>
          }

          @if (ix.apiCalls.length > 0) {
            <div class="interactions__api">
              API: {{ ix.apiCalls.join(', ') }}
            </div>
          }

          @if (ix.permission) {
            <div class="interactions__permission">
              Permission: {{ ix.permission }}
            </div>
          }

          @if (ix.confirmationCode) {
            <div class="interactions__confirmation">
              Confirmation: {{ ix.confirmationCode }}
            </div>
          }

          <div class="interactions__outcomes">
            @if (ix.outcomes.success) {
              <span class="interactions__outcome interactions__outcome--success">{{ ix.outcomes.success }}</span>
            }
            @if (ix.outcomes.error) {
              <span class="interactions__outcome interactions__outcome--error">{{ ix.outcomes.error }}</span>
            }
            @if (ix.outcomes.loading) {
              <span class="interactions__outcome interactions__outcome--loading">{{ ix.outcomes.loading }}</span>
            }
          </div>

          @if (ix.roleKeys.length > 0) {
            <div class="interactions__roles">
              Roles: {{ ix.roleKeys.join(', ') }}
            </div>
          }
        </div>
      } @empty {
        <p class="interactions__empty" data-testid="interactions-empty">No interactions for selected screen</p>
      }
    </div>
  `,
  styles: [`
    .interactions {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-3);
    }

    .interactions__item {
      padding: var(--tp-space-3);
      border-radius: 8px;
      background: color-mix(in srgb, var(--tp-surface) 56%, transparent);
      border: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
    }

    .interactions__header {
      display: flex;
      justify-content: space-between;
      align-items: center;
      margin-bottom: var(--tp-space-2);
    }

    .interactions__element {
      font-size: 0.82rem;
      font-weight: 600;
      font-family: monospace;
    }

    .interactions__trigger {
      font-size: 0.72rem;
      padding: 1px 6px;
      border-radius: 6px;
      background: color-mix(in srgb, var(--tp-primary) 17%, transparent);
      color: var(--tp-primary-dark);
    }

    .interactions__effects {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-1);
      margin-bottom: var(--tp-space-2);
    }

    .interactions__effect-badge {
      font-size: 0.7rem;
      padding: 2px 6px;
      border-radius: 6px;
      background: color-mix(in srgb, var(--tp-border) 10%, transparent);
      color: var(--tp-text);
    }

    .interactions__effect-target {
      color: var(--tp-primary);
      font-weight: 600;
    }

    .interactions__effect-mode {
      font-size: 0.65rem;
      color: var(--tp-text-muted);
      opacity: 0.8;
    }

    .interactions__api {
      font-size: 0.72rem;
      font-family: monospace;
      color: var(--tp-text-muted);
      margin-bottom: var(--tp-space-1);
    }

    .interactions__permission {
      font-size: 0.72rem;
      color: var(--tp-warning);
      margin-bottom: var(--tp-space-1);
    }

    .interactions__confirmation {
      font-size: 0.72rem;
      color: var(--tp-info);
      margin-bottom: var(--tp-space-1);
    }

    .interactions__outcomes {
      display: flex;
      gap: var(--tp-space-2);
      flex-wrap: wrap;
    }

    .interactions__outcome {
      font-size: 0.68rem;
      padding: 1px 6px;
      border-radius: 4px;

      &--success { background: color-mix(in srgb, var(--tp-success) 10%, transparent); color: var(--tp-success); }
      &--error { background: color-mix(in srgb, var(--tp-danger) 8%, transparent); color: var(--tp-danger); }
      &--loading { background: color-mix(in srgb, var(--tp-warning) 10%, transparent); color: var(--tp-warning); }
    }

    .interactions__roles {
      font-size: 0.7rem;
      color: var(--tp-text-muted);
      margin-top: var(--tp-space-1);
    }

    .interactions__empty {
      text-align: center;
      color: var(--tp-text-muted);
      font-style: italic;
      padding: var(--tp-space-8);
    }
  `],
})
export class InteractionPanelComponent {
  private readonly state = inject(DesignHubStateService);

  readonly filteredInteractions = computed<Interaction[]>(() => {
    const selectedId = this.state.selectedScreenId();
    const all = this.state.interactions();
    return selectedId ? all.filter((ix) => ix.surfaceId === selectedId) : all;
  });
}
