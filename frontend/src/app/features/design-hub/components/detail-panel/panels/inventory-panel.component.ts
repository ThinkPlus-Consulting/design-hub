import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { TableModule } from 'primeng/table';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-inventory-panel',
  standalone: true,
  imports: [TableModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="inventory" data-testid="inventory-panel">
      <p-table
        [value]="state.filteredScreens()"
        [scrollable]="true"
        scrollHeight="flex"
        [sortField]="'label'"
        [sortOrder]="1"
        styleClass="p-datatable-sm p-datatable-striped"
        data-testid="inventory-table"
      >
        <ng-template #header>
          <tr>
            <th pSortableColumn="label" style="width: 35%">Label <p-sortIcon field="label" /></th>
            <th pSortableColumn="module" style="width: 20%">Module <p-sortIcon field="module" /></th>
            <th pSortableColumn="designStatus" style="width: 15%">Design <p-sortIcon field="designStatus" /></th>
            <th style="width: 15%">Prototype</th>
            <th style="width: 10%">Delivery</th>
            <th style="width: 5%">Gaps</th>
          </tr>
        </ng-template>
        <ng-template #body let-screen>
          <tr
            [class.inventory__row--selected]="screen.surfaceId === state.selectedScreenId()"
            (click)="state.selectScreen(screen.surfaceId)"
            [attr.data-testid]="'inv-row-' + screen.surfaceId"
            style="cursor: pointer"
          >
            <td>{{ screen.label }}</td>
            <td>{{ screen.module }}</td>
            <td>
              <span class="inventory__badge" [class]="'inventory__badge--' + screen.designStatus.toLowerCase()">
                {{ screen.designStatus }}
              </span>
            </td>
            <td>
              <span class="inventory__badge" [class]="'inventory__badge--' + screen.prototypeStatus.toLowerCase()">
                {{ screen.prototypeStatus }}
              </span>
            </td>
            <td>
              <span class="inventory__badge" [class]="'inventory__badge--' + screen.deliveryStatus.toLowerCase()">
                {{ screen.deliveryStatus }}
              </span>
            </td>
            <td>
              @if (screen._legacy.gaps.length > 0) {
                <span class="inventory__gap-count">{{ screen._legacy.gaps.length }}</span>
              }
            </td>
          </tr>
        </ng-template>
        <ng-template #emptymessage>
          <tr>
            <td colspan="6" style="text-align: center; padding: 2rem; color: var(--tp-text-muted)">
              No screens match current filters
            </td>
          </tr>
        </ng-template>
      </p-table>
    </div>
  `,
  styles: [`
    .inventory {
      height: 100%;
    }

    .inventory__row--selected {
      background: color-mix(in srgb, var(--tp-primary) 12%, transparent) !important;
    }

    .inventory__badge {
      font-size: 0.68rem;
      font-weight: 600;
      padding: 2px 6px;
      border-radius: 8px;
      text-transform: uppercase;
      white-space: nowrap;

      &--complete { background: color-mix(in srgb, var(--dh-complete) 15%, transparent); color: var(--dh-complete); }
      &--specified { background: color-mix(in srgb, var(--dh-specified) 15%, transparent); color: var(--dh-specified); }
      &--not_started { background: color-mix(in srgb, var(--dh-not-started) 15%, transparent); color: var(--dh-not-started); }
      &--prototyped { background: color-mix(in srgb, var(--dh-complete) 15%, transparent); color: var(--dh-complete); }
      &--integrated { background: color-mix(in srgb, var(--dh-complete) 15%, transparent); color: var(--dh-complete); }
      &--tested { background: color-mix(in srgb, var(--dh-complete) 22%, transparent); color: var(--tp-primary-dark); }
    }

    .inventory__gap-count {
      background: color-mix(in srgb, var(--tp-danger) 8%, transparent);
      color: var(--dh-gap);
      font-size: 0.72rem;
      font-weight: 700;
      padding: 2px 8px;
      border-radius: 8px;
    }

    :host ::ng-deep .p-datatable .p-datatable-thead > tr > th {
      font-size: 0.72rem;
      padding: 0.5rem;
    }

    :host ::ng-deep .p-datatable .p-datatable-tbody > tr > td {
      font-size: 0.78rem;
      padding: 0.4rem 0.5rem;
    }
  `],
})
export class InventoryPanelComponent {
  readonly state = inject(DesignHubStateService);
}
