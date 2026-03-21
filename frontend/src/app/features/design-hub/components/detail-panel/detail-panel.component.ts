import { Component, ChangeDetectionStrategy, computed, inject } from '@angular/core';
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
import { ArchitecturePanelComponent } from './panels/architecture-panel.component';
import { CrosscuttingPanelComponent } from './panels/crosscutting-panel.component';
import { DeliveryPanelComponent } from './panels/delivery-panel.component';
import { AutomationPanelComponent } from './panels/automation-panel.component';
import { TraceabilityPanelComponent } from './panels/traceability-panel.component';
import { BenchmarkPanelComponent } from './panels/benchmark-panel.component';
import { VerificationPanelComponent } from './panels/verification-panel.component';
import { LocaleService } from '../../../../core/i18n/locale.service';

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
    ArchitecturePanelComponent,
    CrosscuttingPanelComponent,
    DeliveryPanelComponent,
    AutomationPanelComponent,
    TraceabilityPanelComponent,
    BenchmarkPanelComponent,
    VerificationPanelComponent,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="detail" data-testid="detail-panel-root">
      <!-- Context bar -->
      <div class="detail__context" data-testid="detail-context">
        <span class="detail__context-label" data-testid="detail-context-label">{{ contextLabel() }}</span>
        <span class="detail__context-name" data-testid="detail-context-name">{{ contextName() }}</span>
        @if (hasContext()) {
          <button
            class="detail__close-btn"
            data-testid="detail-close"
            (click)="clearContext()"
            [attr.aria-label]="locale.t('detail.close')"
          >x</button>
        }
      </div>

      <p-tabs [value]="state.activeTab()" (valueChange)="onTabChange($event)" class="detail__tabs">
        <p-tablist>
          <p-tab value="detail" data-testid="tab-detail">{{ locale.t('tab.detail') }}</p-tab>
          <p-tab value="inventory" data-testid="tab-inventory">{{ locale.t('tab.inventory') }}</p-tab>
          <p-tab value="touchpoints" data-testid="tab-touchpoints">{{ locale.t('tab.touchpoints') }}</p-tab>
          <p-tab value="interactions" data-testid="tab-interactions">{{ locale.t('tab.interactions') }}</p-tab>
          <p-tab value="journeys" data-testid="tab-journeys">{{ locale.t('tab.journeys') }}</p-tab>
          <p-tab value="architecture" data-testid="tab-architecture">{{ locale.t('tab.architecture') }}</p-tab>
          <p-tab value="delivery" data-testid="tab-delivery">{{ locale.t('tab.delivery') }}</p-tab>
          <p-tab value="automation" data-testid="tab-automation">{{ locale.t('tab.automation') }}</p-tab>
          <p-tab value="traceability" data-testid="tab-traceability">{{ locale.t('tab.traceability') }}</p-tab>
          <p-tab value="benchmark" data-testid="tab-benchmark">{{ locale.t('tab.benchmark') }}</p-tab>
          <p-tab value="verification" data-testid="tab-verification">{{ locale.t('tab.verification') }}</p-tab>
          <p-tab value="crosscutting" data-testid="tab-crosscutting">{{ locale.t('tab.crosscutting') }}</p-tab>
        </p-tablist>
        <p-tabpanels>
          <p-tabpanel value="detail"><app-screen-detail /></p-tabpanel>
          <p-tabpanel value="inventory"><app-inventory-panel /></p-tabpanel>
          <p-tabpanel value="touchpoints"><app-touchpoint-panel /></p-tabpanel>
          <p-tabpanel value="interactions"><app-interaction-panel /></p-tabpanel>
          <p-tabpanel value="journeys"><app-journey-panel /></p-tabpanel>
          <p-tabpanel value="architecture"><app-architecture-panel /></p-tabpanel>
          <p-tabpanel value="delivery"><app-delivery-panel /></p-tabpanel>
          <p-tabpanel value="automation"><app-automation-panel /></p-tabpanel>
          <p-tabpanel value="traceability"><app-traceability-panel /></p-tabpanel>
          <p-tabpanel value="benchmark"><app-benchmark-panel /></p-tabpanel>
          <p-tabpanel value="verification"><app-verification-panel /></p-tabpanel>
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
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      background: color-mix(in srgb, var(--tp-white) 34%, transparent);
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
      min-width: var(--tp-touch-target-min-size);
      min-height: var(--tp-touch-target-min-size);
      padding: var(--tp-space-1);
      border-radius: var(--tp-space-3);

      &:hover {
        background: var(--tp-danger-bg);
        color: var(--tp-danger);
      }

      &:focus-visible {
        outline: none;
        box-shadow: var(--tp-focus-ring);
      }
    }

    .detail__tabs {
      flex: 1;
      overflow: hidden;
    }

    :host ::ng-deep .p-tablist .p-tab {
      font-size: 0.72rem;
      min-height: var(--tp-touch-target-min-size);
      padding: var(--tp-space-3) var(--tp-space-4);
    }

    :host ::ng-deep .p-tabpanels {
      overflow-y: auto;
      flex: 1;
    }

    :host ::ng-deep .p-tabpanel {
      padding: var(--tp-space-3);
    }

    @media (max-width: 960px) {
      .detail__context {
        padding-inline: var(--tp-space-3);
      }

      :host ::ng-deep .p-tabpanel {
        padding: var(--tp-space-2);
      }
    }
  `],
})
export class DetailPanelComponent {
  readonly state = inject(DesignHubStateService);
  readonly locale = inject(LocaleService);
  readonly contextLabel = computed(() => {
    switch (this.state.activeTab()) {
      case 'touchpoints':
        return this.locale.t('detail.context.channel');
      case 'journeys':
        return this.locale.t('detail.context.journey');
      case 'architecture':
        if (this.state.selectedArchitectureView() === 'application') {
          return this.locale.t('detail.context.application');
        }
        if (this.state.selectedArchitectureView() === 'data') {
          return this.locale.t('detail.context.dataObject');
        }
        if (this.state.selectedArchitectureView() === 'infrastructure') {
          return this.locale.t('detail.context.deployment');
        }
        return this.locale.t('detail.context.capability');
      case 'delivery':
      case 'automation':
      case 'traceability':
        return this.locale.t('detail.context.story');
      case 'benchmark':
        return this.locale.t('detail.context.benchmark');
      case 'verification':
        return this.locale.t('detail.context.verification');
      default:
        return this.locale.t('detail.context.screen');
    }
  });
  readonly contextName = computed(() => {
    if (this.state.activeTab() === 'touchpoints') {
      return this.state.selectedChannelTraversal()?.displayName ?? this.state.selectedChannel()?.displayName ?? this.locale.t('detail.context.none');
    }
    if (this.state.activeTab() === 'journeys') {
      return this.state.selectedJourneyTraversal()?.title ?? this.state.selectedJourney()?.title ?? this.locale.t('detail.context.none');
    }
    if (this.state.activeTab() === 'architecture') {
      if (this.state.selectedArchitectureView() === 'application') {
        return this.state.selectedApplicationArchitecture()?.name ?? this.state.selectedApplicationSummary()?.name ?? this.locale.t('detail.context.none');
      }
      if (this.state.selectedArchitectureView() === 'data') {
        return this.state.selectedDataArchitecture()?.name ?? this.state.selectedDataObjectSummary()?.name ?? this.locale.t('detail.context.none');
      }
      if (this.state.selectedArchitectureView() === 'infrastructure') {
        return this.state.selectedInfrastructureArchitecture()?.name ?? this.state.selectedInfrastructureDeployment()?.name ?? this.locale.t('detail.context.none');
      }
      return this.state.selectedBusinessArchitecture()?.name ?? this.state.selectedBusinessCapability()?.name ?? this.locale.t('detail.context.none');
    }
    if (this.state.activeTab() === 'delivery' || this.state.activeTab() === 'automation' || this.state.activeTab() === 'traceability') {
      return this.state.selectedDeliveryStory()?.label ?? this.locale.t('detail.context.none');
    }
    if (this.state.activeTab() === 'benchmark') {
      return this.locale.t('detail.context.globalCoverage');
    }
    if (this.state.activeTab() === 'verification') {
      return this.locale.t('detail.context.liveEvidence');
    }
    return this.state.selectedScreen()?.label ?? this.locale.t('detail.context.none');
  });
  readonly hasContext = computed(() => {
    if (this.state.activeTab() === 'touchpoints') {
      return this.state.selectedChannelCode() !== null;
    }
    if (this.state.activeTab() === 'journeys') {
      return this.state.selectedJourneyId() !== null;
    }
    if (this.state.activeTab() === 'architecture') {
      if (this.state.selectedArchitectureView() === 'application') {
        return this.state.selectedApplicationId() !== null;
      }
      if (this.state.selectedArchitectureView() === 'data') {
        return this.state.selectedDataObjectId() !== null;
      }
      if (this.state.selectedArchitectureView() === 'infrastructure') {
        return this.state.selectedDeploymentId() !== null;
      }
      return this.state.selectedBusinessCapabilityId() !== null;
    }
    if (this.state.activeTab() === 'delivery' || this.state.activeTab() === 'automation' || this.state.activeTab() === 'traceability') {
      return this.state.selectedDeliveryStory() !== null;
    }
    if (this.state.activeTab() === 'benchmark' || this.state.activeTab() === 'verification') {
      return false;
    }
    return this.state.selectedScreen() !== null;
  });

  onTabChange(value: string | number | undefined): void {
    if (value != null) {
      this.state.setActiveTab(value as DetailTab);
    }
  }

  clearContext(): void {
    if (this.state.activeTab() === 'touchpoints') {
      this.state.selectChannel(null);
      return;
    }
    if (this.state.activeTab() === 'journeys') {
      this.state.selectJourney(null);
      return;
    }
    if (this.state.activeTab() === 'architecture') {
      if (this.state.selectedArchitectureView() === 'application') {
        this.state.selectApplication(null);
        return;
      }
      if (this.state.selectedArchitectureView() === 'data') {
        this.state.selectDataObject(null);
        return;
      }
      if (this.state.selectedArchitectureView() === 'infrastructure') {
        this.state.selectInfrastructureDeployment(null);
        return;
      }
      this.state.selectBusinessCapability(null);
      return;
    }
    if (this.state.activeTab() === 'delivery' || this.state.activeTab() === 'traceability') {
      this.state.selectDeliveryStory(null);
      return;
    }
    this.state.selectScreen(null);
  }
}
