import { Component, ChangeDetectionStrategy, inject } from '@angular/core';
import { Tabs } from 'primeng/tabs';
import { TabList } from 'primeng/tabs';
import { Tab } from 'primeng/tabs';
import { TabPanels } from 'primeng/tabs';
import { TabPanel } from 'primeng/tabs';
import { DesignHubStateService } from '../../services/design-hub-state.service';
import { DetailTab } from '../../../../models';
import { ScreenDetailComponent } from './panels/screen-detail.component';
import { InventoryPanelComponent } from './panels/inventory-panel.component';
import { TouchpointPanelComponent } from './panels/touchpoint-panel.component';
import { InteractionPanelComponent } from './panels/interaction-panel.component';
import { JourneyPanelComponent } from './panels/journey-panel.component';
import { CrosscuttingPanelComponent } from './panels/crosscutting-panel.component';

@Component({
  selector: 'app-detail-panel',
  standalone: true,
  imports: [
    Tabs,
    TabList,
    Tab,
    TabPanels,
    TabPanel,
    ScreenDetailComponent,
    InventoryPanelComponent,
    TouchpointPanelComponent,
    InteractionPanelComponent,
    JourneyPanelComponent,
    CrosscuttingPanelComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="detail" data-testid="detail-panel-root">
      <!-- Context bar -->
      <div class="detail__context" data-testid="detail-context">
        <span class="detail__context-label">Selected:</span>
        <span class="detail__context-name" data-testid="detail-context-name">{{ state.selectedScreen()?.label ?? 'None' }}</span>
        @if (state.selectedScreen()) {
          <button
            class="detail__close-btn"
            data-testid="detail-close"
            (click)="state.selectScreen(null)"
            aria-label="Close detail panel"
          >x</button>
        }
      </div>

      <p-tabs [value]="state.activeTab()" (valueChange)="onTabChange($event)" class="detail__tabs">
        <p-tablist>
          <p-tab value="detail" data-testid="tab-detail">Detail</p-tab>
          <p-tab value="inventory" data-testid="tab-inventory">Inventory</p-tab>
          <p-tab value="touchpoints" data-testid="tab-touchpoints">Touch</p-tab>
          <p-tab value="interactions" data-testid="tab-interactions">Actions</p-tab>
          <p-tab value="journeys" data-testid="tab-journeys">Journeys</p-tab>
          <p-tab value="crosscutting" data-testid="tab-crosscutting">X-Cut</p-tab>
        </p-tablist>
        <p-tabpanels>
          <p-tabpanel value="detail"><app-screen-detail /></p-tabpanel>
          <p-tabpanel value="inventory"><app-inventory-panel /></p-tabpanel>
          <p-tabpanel value="touchpoints"><app-touchpoint-panel /></p-tabpanel>
          <p-tabpanel value="interactions"><app-interaction-panel /></p-tabpanel>
          <p-tabpanel value="journeys"><app-journey-panel /></p-tabpanel>
          <p-tabpanel value="crosscutting"><app-crosscutting-panel /></p-tabpanel>
        </p-tabpanels>
      </p-tabs>
    </div>
  `,
  styles: [`
    .detail {
      display: flex;
      flex-direction: column;
      height: 100%;
    }

    .detail__context {
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      padding: var(--tp-space-3) var(--tp-space-4);
      border-bottom: 1px solid rgba(152, 133, 97, 0.18);
    }

    .detail__context-label {
      font-size: 0.75rem;
      color: var(--tp-text-muted);
    }

    .detail__context-name {
      font-size: 0.85rem;
      font-weight: 600;
      color: var(--tp-primary-dark);
      flex: 1;
      white-space: nowrap;
      overflow: hidden;
      text-overflow: ellipsis;
    }

    .detail__close-btn {
      border: none;
      background: transparent;
      font-size: 1rem;
      cursor: pointer;
      color: var(--tp-text-muted);
      padding: 2px 6px;
      border-radius: 4px;

      &:hover {
        background: rgba(107, 31, 42, 0.1);
        color: var(--tp-danger);
      }
    }

    .detail__tabs {
      flex: 1;
      overflow: hidden;
    }

    :host ::ng-deep .p-tablist .p-tab {
      font-size: 0.72rem;
      padding: 0.5rem 0.65rem;
    }

    :host ::ng-deep .p-tabpanels {
      overflow-y: auto;
      flex: 1;
    }

    :host ::ng-deep .p-tabpanel {
      padding: var(--tp-space-3);
    }
  `],
})
export class DetailPanelComponent {
  readonly state = inject(DesignHubStateService);

  onTabChange(value: string | number | undefined): void {
    if (value != null) {
      this.state.setActiveTab(value as DetailTab);
    }
  }
}
