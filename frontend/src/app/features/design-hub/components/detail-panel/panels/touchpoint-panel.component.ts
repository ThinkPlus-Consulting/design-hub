import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';
import { Touchpoint } from '../../../../../models';

interface ChannelGroup {
  channelId: string;
  touchpoints: Touchpoint[];
}

@Component({
  selector: 'app-touchpoint-panel',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="touchpoints" data-testid="touchpoint-panel">
      @for (group of groupedTouchpoints(); track group.channelId) {
        <section class="touchpoints__group">
          <h4 class="touchpoints__channel" data-testid="channel-heading">{{ group.channelId }}</h4>
          @for (tp of group.touchpoints; track tp.touchpointId) {
            <div class="touchpoints__item" [attr.data-testid]="'tp-' + tp.touchpointId">
              <div class="touchpoints__item-header">
                <span class="touchpoints__item-label">{{ tp.label }}</span>
                <span class="touchpoints__item-surface">{{ tp.surfaceId }}</span>
              </div>
              @if (tp.entryModes.length > 0) {
                <div class="touchpoints__entries">
                  @for (em of tp.entryModes; track em.mechanism) {
                    <span class="touchpoints__entry-badge">{{ em.mechanism }}</span>
                  }
                </div>
              }
              @if (tp.roleKeys.length > 0) {
                <div class="touchpoints__roles">
                  Roles: {{ tp.roleKeys.join(', ') }}
                </div>
              }
              @if (tp.personaIds.length > 0) {
                <div class="touchpoints__personas">
                  Personas: {{ tp.personaIds.join(', ') }}
                </div>
              }
              @if (tp.journeyStepRefs.length > 0) {
                <div class="touchpoints__journey-refs">
                  Journey steps: {{ tp.journeyStepRefs.join(', ') }}
                </div>
              }
            </div>
          }
        </section>
      } @empty {
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

    .touchpoints__channel {
      font-size: 0.75rem;
      font-weight: 600;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      color: var(--tp-primary-dark);
      margin-bottom: var(--tp-space-2);
      padding-bottom: var(--tp-space-1);
      border-bottom: 1px solid rgba(152, 133, 97, 0.18);
    }

    .touchpoints__item {
      padding: var(--tp-space-2);
      border-radius: 8px;
      background: rgba(237, 235, 224, 0.56);
      margin-bottom: var(--tp-space-2);
    }

    .touchpoints__item-header {
      display: flex;
      justify-content: space-between;
      align-items: center;
    }

    .touchpoints__item-label {
      font-size: 0.82rem;
      font-weight: 600;
    }

    .touchpoints__item-surface {
      font-size: 0.68rem;
      color: var(--tp-text-muted);
      font-family: monospace;
    }

    .touchpoints__entries {
      display: flex;
      gap: var(--tp-space-1);
      flex-wrap: wrap;
      margin-top: var(--tp-space-1);
    }

    .touchpoints__entry-badge {
      font-size: 0.68rem;
      padding: 1px 6px;
      border-radius: 6px;
      background: rgba(66, 129, 119, 0.17);
      color: var(--tp-primary-dark);
    }

    .touchpoints__roles,
    .touchpoints__personas,
    .touchpoints__journey-refs {
      font-size: 0.72rem;
      color: var(--tp-text-muted);
      margin-top: var(--tp-space-1);
    }

    .touchpoints__empty {
      text-align: center;
      color: var(--tp-text-muted);
      font-style: italic;
      padding: var(--tp-space-8);
    }
  `],
})
export class TouchpointPanelComponent {
  private readonly state = inject(DesignHubStateService);

  readonly groupedTouchpoints = computed<ChannelGroup[]>(() => {
    const selectedId = this.state.selectedScreenId();
    const all = this.state.touchpoints();
    const filtered = selectedId ? all.filter((tp) => tp.surfaceId === selectedId) : all;

    const map = new Map<string, Touchpoint[]>();
    for (const tp of filtered) {
      for (const em of tp.entryModes) {
        const list = map.get(em.channelId) ?? [];
        if (!list.some((t) => t.touchpointId === tp.touchpointId)) {
          list.push(tp);
        }
        map.set(em.channelId, list);
      }
      if (tp.entryModes.length === 0) {
        const list = map.get('unknown') ?? [];
        list.push(tp);
        map.set('unknown', list);
      }
    }

    return [...map.entries()]
      .sort(([a], [b]) => a.localeCompare(b))
      .map(([channelId, touchpoints]) => ({ channelId, touchpoints }));
  });
}
