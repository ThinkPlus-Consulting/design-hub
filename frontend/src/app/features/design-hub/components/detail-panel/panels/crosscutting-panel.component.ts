import { Component, ChangeDetectionStrategy, inject, computed } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';
import { Screen } from '../../../../../models';

interface CrosscuttingRow {
  screen: Screen;
  wcag: string;
  responsive: boolean;
  roleAdaptive: boolean;
  deepLinkable: boolean;
  loadingStates: boolean;
  messageRegistryCount: number;
}

@Component({
  selector: 'app-crosscutting-panel',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="crosscutting" data-testid="crosscutting-panel">
      @if (selectedRow(); as row) {
        <!-- Single screen view -->
        <h4 class="crosscutting__heading">{{ row.screen.label }}</h4>
        <dl class="crosscutting__props">
          <dt>WCAG</dt><dd data-testid="xc-wcag">{{ row.wcag }}</dd>
          <dt>Responsive</dt><dd data-testid="xc-responsive">{{ row.responsive ? 'Yes' : 'No' }}</dd>
          <dt>Role Adaptive</dt><dd data-testid="xc-role-adaptive">{{ row.roleAdaptive ? 'Yes' : 'No' }}</dd>
          <dt>Deep Linkable</dt><dd data-testid="xc-deep-linkable">{{ row.deepLinkable ? 'Yes' : 'No' }}</dd>
          <dt>Loading States</dt><dd data-testid="xc-loading">{{ row.loadingStates ? 'Yes' : 'No' }}</dd>
          <dt>Message Registry</dt><dd data-testid="xc-messages">{{ row.messageRegistryCount }}</dd>
        </dl>
      } @else {
        <!-- Matrix view -->
        <div class="crosscutting__matrix" data-testid="crosscutting-matrix">
          <table class="crosscutting__table">
            <thead>
              <tr>
                <th>Screen</th>
                <th>WCAG</th>
                <th>Resp</th>
                <th>Role</th>
                <th>Link</th>
                <th>Load</th>
                <th>Msgs</th>
              </tr>
            </thead>
            <tbody>
              @for (row of matrixRows(); track row.screen.surfaceId) {
                <tr
                  (click)="state.selectScreen(row.screen.surfaceId)"
                  style="cursor: pointer"
                  [attr.data-testid]="'xc-row-' + row.screen.surfaceId"
                >
                  <td class="crosscutting__screen-cell">{{ row.screen.label }}</td>
                  <td>{{ row.wcag }}</td>
                  <td><span [class.crosscutting__check]="row.responsive" [class.crosscutting__cross]="!row.responsive">{{ row.responsive ? 'Y' : '-' }}</span></td>
                  <td><span [class.crosscutting__check]="row.roleAdaptive" [class.crosscutting__cross]="!row.roleAdaptive">{{ row.roleAdaptive ? 'Y' : '-' }}</span></td>
                  <td><span [class.crosscutting__check]="row.deepLinkable" [class.crosscutting__cross]="!row.deepLinkable">{{ row.deepLinkable ? 'Y' : '-' }}</span></td>
                  <td><span [class.crosscutting__check]="row.loadingStates" [class.crosscutting__cross]="!row.loadingStates">{{ row.loadingStates ? 'Y' : '-' }}</span></td>
                  <td>{{ row.messageRegistryCount }}</td>
                </tr>
              } @empty {
                <tr>
                  <td colspan="7" style="text-align: center; color: var(--tp-text-muted); padding: 2rem">No screens</td>
                </tr>
              }
            </tbody>
          </table>
        </div>
      }
    </div>
  `,
  styles: [`
    .crosscutting__heading {
      font-size: 0.82rem;
      font-weight: 600;
      margin-bottom: var(--tp-space-3);
      color: var(--tp-primary-dark);
    }

    .crosscutting__props {
      display: grid;
      grid-template-columns: auto 1fr;
      gap: var(--tp-space-2) var(--tp-space-4);
      font-size: 0.8rem;

      dt { font-weight: 600; color: var(--tp-text-muted); }
      dd { margin: 0; }
    }

    .crosscutting__table {
      width: 100%;
      border-collapse: collapse;
      font-size: 0.72rem;

      th, td {
        padding: var(--tp-space-1) var(--tp-space-2);
        text-align: center;
        border-bottom: 1px solid rgba(152, 133, 97, 0.18);
      }

      th {
        font-weight: 600;
        text-transform: uppercase;
        font-size: 0.68rem;
        color: var(--tp-text-muted);
        position: sticky;
        top: 0;
        background: var(--tp-surface);
      }
    }

    .crosscutting__screen-cell {
      text-align: left !important;
      font-weight: 500;
      max-width: 120px;
      overflow: hidden;
      text-overflow: ellipsis;
      white-space: nowrap;
    }

    .crosscutting__check { color: var(--tp-success); font-weight: 700; }
    .crosscutting__cross { color: var(--tp-text-muted); opacity: 0.5; }

    .crosscutting__matrix {
      overflow-x: auto;
    }
  `],
})
export class CrosscuttingPanelComponent {
  readonly state = inject(DesignHubStateService);

  readonly selectedRow = computed<CrosscuttingRow | null>(() => {
    const screen = this.state.selectedScreen();
    if (!screen) return null;
    return this.toRow(screen);
  });

  readonly matrixRows = computed<CrosscuttingRow[]>(() => {
    return this.state.filteredScreens().map((s) => this.toRow(s));
  });

  private toRow(s: Screen): CrosscuttingRow {
    return {
      screen: s,
      wcag: s.crossCutting.wcag,
      responsive: s.crossCutting.responsive,
      roleAdaptive: s.crossCutting.roleAdaptive,
      deepLinkable: s.crossCutting.deepLinkable,
      loadingStates: s.crossCutting.loadingStates,
      messageRegistryCount: s.crossCutting.messageRegistryCount,
    };
  }
}
