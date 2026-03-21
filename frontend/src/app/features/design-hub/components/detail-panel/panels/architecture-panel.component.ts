import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-architecture-panel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="architecture" data-testid="architecture-panel">
      <header class="architecture__toolbar">
        <div class="architecture__toolbar-copy">
          <p class="architecture__kicker">Architecture Explorer</p>
          <h4 class="architecture__title">Business, application, data, and infra traversal</h4>
          <p class="architecture__description">
            Switch between capability alignment, application implementation topology, data-object mapping, and deployment topology without leaving the graph explorer.
          </p>
        </div>

        <div class="architecture__switcher" data-testid="architecture-view-switcher">
          <button
            type="button"
            class="architecture__switch-btn"
            [class.architecture__switch-btn--active]="state.selectedArchitectureView() === 'business'"
            data-testid="architecture-view-business"
            (click)="state.selectArchitectureView('business')"
          >
            Business
          </button>
          <button
            type="button"
            class="architecture__switch-btn"
            [class.architecture__switch-btn--active]="state.selectedArchitectureView() === 'application'"
            data-testid="architecture-view-application"
            (click)="state.selectArchitectureView('application')"
          >
            Application
          </button>
          <button
            type="button"
            class="architecture__switch-btn"
            [class.architecture__switch-btn--active]="state.selectedArchitectureView() === 'data'"
            data-testid="architecture-view-data"
            (click)="state.selectArchitectureView('data')"
          >
            Data
          </button>
          <button
            type="button"
            class="architecture__switch-btn"
            [class.architecture__switch-btn--active]="state.selectedArchitectureView() === 'infrastructure'"
            data-testid="architecture-view-infrastructure"
            (click)="state.selectArchitectureView('infrastructure')"
          >
            Infra
          </button>
        </div>
      </header>

      @if (state.selectedArchitectureView() === 'business') {
        @if (state.businessCapabilities().length > 0) {
          <div class="architecture__layout" data-testid="architecture-business-layout">
            <aside class="architecture__rail">
              <div class="architecture__section-head">
                <div>
                  <p class="architecture__kicker">Business Architecture</p>
                  <h4 class="architecture__section-title">Capabilities</h4>
                </div>
                <span class="architecture__count">{{ state.businessCapabilities().length }} total</span>
              </div>

              <div class="architecture__selection-list" data-testid="architecture-capability-list">
                @for (capability of state.businessCapabilities(); track capability.capabilityId) {
                  <button
                    type="button"
                    class="architecture__selection-card"
                    [class.architecture__selection-card--selected]="capability.capabilityId === state.selectedBusinessCapabilityId()"
                    [attr.data-testid]="'architecture-capability-' + capability.capabilityId"
                    (click)="state.selectBusinessCapability(capability.capabilityId)"
                  >
                    <strong class="architecture__selection-name">{{ capability.name }}</strong>
                    <span class="architecture__selection-subtitle">
                      {{ capability.domainName ?? capability.domainCode ?? 'Unassigned domain' }}
                    </span>
                    <span class="architecture__selection-meta">
                      {{ capability.applicationCount }} apps · {{ capability.processCount }} processes · {{ capability.featureCount }} features
                    </span>
                  </button>
                }
              </div>
            </aside>

            <section class="architecture__content">
              @if (state.selectedBusinessArchitecture(); as architecture) {
                <section class="architecture__hero">
                  <div class="architecture__hero-copy">
                    <p class="architecture__kicker">Business Architecture</p>
                    <h4 class="architecture__title" data-testid="architecture-title">{{ architecture.name }}</h4>
                    <p class="architecture__description">
                      {{ architecture.description ?? 'Capability context is graph-backed through processes, applications, features, and ownership.' }}
                    </p>
                  </div>

                  <div class="architecture__hero-meta">
                    <span class="architecture__chip">{{ architecture.domainName ?? architecture.domainCode ?? 'No domain' }}</span>
                    <span class="architecture__chip">{{ architecture.status ?? 'No status' }}</span>
                  </div>

                  <div class="architecture__stats">
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Processes</span>
                      <strong class="architecture__stat-value">{{ architecture.processes.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Applications</span>
                      <strong class="architecture__stat-value">{{ architecture.applications.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Features</span>
                      <strong class="architecture__stat-value">{{ architecture.features.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Owners</span>
                      <strong class="architecture__stat-value">{{ architecture.organizations.length }}</strong>
                    </article>
                  </div>
                </section>

                <div class="architecture__grid">
                  <section class="architecture__section" data-testid="architecture-processes">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Process spine</p>
                        <h5 class="architecture__section-title">Realized by processes</h5>
                      </div>
                    </div>
                    @if (architecture.processes.length > 0) {
                      <div class="architecture__item-list">
                        @for (process of architecture.processes; track process.id) {
                          <article class="architecture__item-card">
                            <strong>{{ process.displayName }}</strong>
                            <span>{{ process.id }}</span>
                            <span>{{ process.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No business processes are linked to this capability yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-applications">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Application support</p>
                        <h5 class="architecture__section-title">Enabled by applications</h5>
                      </div>
                    </div>
                    @if (architecture.applications.length > 0) {
                      <div class="architecture__item-list">
                        @for (application of architecture.applications; track application.id) {
                          <article class="architecture__item-card">
                            <strong>{{ application.displayName }}</strong>
                            <span>{{ application.id }}</span>
                            <span>{{ application.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No applications support this capability yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-features">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Delivery linkage</p>
                        <h5 class="architecture__section-title">Realizing features</h5>
                      </div>
                    </div>
                    @if (architecture.features.length > 0) {
                      <div class="architecture__item-list">
                        @for (feature of architecture.features; track feature.id) {
                          <article class="architecture__item-card">
                            <strong>{{ feature.displayName }}</strong>
                            <span>{{ feature.id }}</span>
                            <span>{{ feature.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No features trace to this capability yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-organizations">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Ownership</p>
                        <h5 class="architecture__section-title">Owning organizations</h5>
                      </div>
                    </div>
                    @if (architecture.organizations.length > 0) {
                      <div class="architecture__item-list">
                        @for (organization of architecture.organizations; track organization.orgId) {
                          <article class="architecture__item-card">
                            <strong>{{ organization.name }}</strong>
                            <span>{{ organization.orgId }}</span>
                            <span>{{ organization.organizationType ?? 'No type' }} · {{ organization.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No owning organizations are linked through application ownership yet.</p>
                    }
                  </section>
                </div>
              } @else {
                <p class="architecture__empty-block" data-testid="architecture-empty">
                  Select a capability to explore its business architecture footprint.
                </p>
              }
            </section>
          </div>
        } @else {
          <p class="architecture__empty-block" data-testid="architecture-empty">
            No business architecture capabilities are available in the current graph.
          </p>
        }
      } @else if (state.selectedArchitectureView() === 'application') {
        @if (state.applicationSummaries().length > 0) {
          <div class="architecture__layout" data-testid="architecture-application-layout">
            <aside class="architecture__rail">
              <div class="architecture__section-head">
                <div>
                  <p class="architecture__kicker">Application Architecture</p>
                  <h4 class="architecture__section-title">Applications</h4>
                </div>
                <span class="architecture__count">{{ state.applicationSummaries().length }} total</span>
              </div>

              <div class="architecture__selection-list" data-testid="architecture-application-list">
                @for (application of state.applicationSummaries(); track application.applicationId) {
                  <button
                    type="button"
                    class="architecture__selection-card"
                    [class.architecture__selection-card--selected]="application.applicationId === state.selectedApplicationId()"
                    [attr.data-testid]="'architecture-application-' + application.applicationId"
                    (click)="state.selectApplication(application.applicationId)"
                  >
                    <strong class="architecture__selection-name">{{ application.name }}</strong>
                    <span class="architecture__selection-subtitle">
                      {{ application.applicationType ?? 'Unclassified application' }}
                    </span>
                    <span class="architecture__selection-meta">
                      {{ application.componentCount }} components · {{ application.apiCount }} APIs · {{ application.dependencyCount }} dependencies
                    </span>
                  </button>
                }
              </div>
            </aside>

            <section class="architecture__content">
              @if (state.selectedApplicationArchitecture(); as architecture) {
                <section class="architecture__hero">
                  <div class="architecture__hero-copy">
                    <p class="architecture__kicker">Application Architecture</p>
                    <h4 class="architecture__title" data-testid="architecture-application-title">{{ architecture.name }}</h4>
                    <p class="architecture__description">
                      {{ architecture.description ?? 'Application topology is graph-backed through components, APIs, screens, features, and dependencies.' }}
                    </p>
                  </div>

                  <div class="architecture__hero-meta">
                    <span class="architecture__chip">{{ architecture.applicationType ?? 'No type' }}</span>
                    <span class="architecture__chip">{{ architecture.status ?? 'No status' }}</span>
                    <span class="architecture__chip">{{ architecture.ownerNames.length }} owners</span>
                  </div>

                  <div class="architecture__stats">
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Components</span>
                      <strong class="architecture__stat-value">{{ architecture.components.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">APIs</span>
                      <strong class="architecture__stat-value">{{ architecture.apis.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Screens</span>
                      <strong class="architecture__stat-value">{{ architecture.screens.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Features</span>
                      <strong class="architecture__stat-value">{{ architecture.features.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Dependencies</span>
                      <strong class="architecture__stat-value">{{ architecture.dependencies.length }}</strong>
                    </article>
                  </div>
                </section>

                <div class="architecture__grid architecture__grid--wide">
                  <section class="architecture__section" data-testid="architecture-application-components">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Component map</p>
                        <h5 class="architecture__section-title">Application components</h5>
                      </div>
                    </div>
                    @if (architecture.components.length > 0) {
                      <div class="architecture__item-list">
                        @for (component of architecture.components; track component.componentId) {
                          <article class="architecture__item-card">
                            <strong>{{ component.name }}</strong>
                            <span>{{ component.componentId }}</span>
                            <span>
                              {{ component.componentType ?? 'No type' }} ·
                              {{ component.frameworkFamily ?? 'No framework' }} ·
                              {{ component.runtime ?? 'No runtime' }}
                            </span>
                            <span>{{ component.modulePath ?? 'No module path' }}</span>
                            <span>
                              {{ component.apis.length }} APIs · {{ component.screens.length }} screens · {{ component.dependencies.length }} component deps
                            </span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No components are linked to this application yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-application-apis">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Interface surface</p>
                        <h5 class="architecture__section-title">Exposed APIs</h5>
                      </div>
                    </div>
                    @if (architecture.apis.length > 0) {
                      <div class="architecture__item-list">
                        @for (api of architecture.apis; track api.id) {
                          <article class="architecture__item-card">
                            <strong>{{ api.displayName }}</strong>
                            <span>{{ api.id }}</span>
                            <span>{{ api.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No API contracts are exposed from this application yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-application-screens">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Experience linkage</p>
                        <h5 class="architecture__section-title">Supported screens</h5>
                      </div>
                    </div>
                    @if (architecture.screens.length > 0) {
                      <div class="architecture__item-list">
                        @for (screen of architecture.screens; track screen.id) {
                          <article class="architecture__item-card">
                            <strong>{{ screen.displayName }}</strong>
                            <span>{{ screen.id }}</span>
                            <span>{{ screen.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No screens are linked through components yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-application-features">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Delivery linkage</p>
                        <h5 class="architecture__section-title">Realized features</h5>
                      </div>
                    </div>
                    @if (architecture.features.length > 0) {
                      <div class="architecture__item-list">
                        @for (feature of architecture.features; track feature.id) {
                          <article class="architecture__item-card">
                            <strong>{{ feature.displayName }}</strong>
                            <span>{{ feature.id }}</span>
                            <span>{{ feature.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No features are realized directly by this application yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-application-dependencies">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">System topology</p>
                        <h5 class="architecture__section-title">Application dependencies</h5>
                      </div>
                    </div>
                    @if (architecture.dependencies.length > 0) {
                      <div class="architecture__item-list">
                        @for (dependency of architecture.dependencies; track dependency.applicationId) {
                          <article class="architecture__item-card">
                            <strong>{{ dependency.name }}</strong>
                            <span>{{ dependency.applicationId }}</span>
                            <span>{{ dependency.direction ?? 'No direction' }} · {{ dependency.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No application dependencies are linked from component topology yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-application-owners">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Ownership</p>
                        <h5 class="architecture__section-title">Owning organizations</h5>
                      </div>
                    </div>
                    @if (architecture.ownerNames.length > 0) {
                      <div class="architecture__item-list">
                        @for (ownerName of architecture.ownerNames; track ownerName) {
                          <article class="architecture__item-card">
                            <strong>{{ ownerName }}</strong>
                            <span>Organization owner</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No owning organizations are linked to this application yet.</p>
                    }
                  </section>
                </div>
              } @else {
                <p class="architecture__empty-block" data-testid="architecture-application-empty">
                  Select an application to explore its implementation topology.
                </p>
              }
            </section>
          </div>
        } @else {
          <p class="architecture__empty-block" data-testid="architecture-application-empty">
            No applications are available in the current graph.
          </p>
        }
      } @else if (state.selectedArchitectureView() === 'data') {
        @if (state.dataObjectSummaries().length > 0) {
          <div class="architecture__layout" data-testid="architecture-data-layout">
            <aside class="architecture__rail">
              <div class="architecture__section-head">
                <div>
                  <p class="architecture__kicker">Data Architecture</p>
                  <h4 class="architecture__section-title">Business objects</h4>
                </div>
                <span class="architecture__count">{{ state.dataObjectSummaries().length }} total</span>
              </div>

              <div class="architecture__selection-list" data-testid="architecture-data-list">
                @for (object of state.dataObjectSummaries(); track object.objectId) {
                  <button
                    type="button"
                    class="architecture__selection-card"
                    [class.architecture__selection-card--selected]="object.objectId === state.selectedDataObjectId()"
                    [attr.data-testid]="'architecture-data-' + object.objectId"
                    (click)="state.selectDataObject(object.objectId)"
                  >
                    <strong class="architecture__selection-name">{{ object.name }}</strong>
                    <span class="architecture__selection-subtitle">
                      {{ object.domain ?? 'Unassigned domain' }}
                    </span>
                    <span class="architecture__selection-meta">
                      {{ object.mappedEntityCount }} entities · {{ object.flowCount }} flows · {{ object.screenCount }} screens
                    </span>
                  </button>
                }
              </div>
            </aside>

            <section class="architecture__content">
              @if (state.selectedDataArchitecture(); as architecture) {
                <section class="architecture__hero">
                  <div class="architecture__hero-copy">
                    <p class="architecture__kicker">Data Architecture</p>
                    <h4 class="architecture__title" data-testid="architecture-data-title">{{ architecture.name }}</h4>
                    <p class="architecture__description">
                      {{ architecture.description ?? 'Business-object mapping is graph-backed through entities, flows, APIs, screens, and child object structure.' }}
                    </p>
                  </div>

                  <div class="architecture__hero-meta">
                    <span class="architecture__chip">{{ architecture.domain ?? 'No domain' }}</span>
                    <span class="architecture__chip">{{ architecture.sensitivity ?? 'No sensitivity' }}</span>
                    <span class="architecture__chip">{{ architecture.status ?? 'No status' }}</span>
                  </div>

                  <div class="architecture__stats">
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Entities</span>
                      <strong class="architecture__stat-value">{{ architecture.entities.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Flows</span>
                      <strong class="architecture__stat-value">{{ architecture.flows.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">APIs</span>
                      <strong class="architecture__stat-value">{{ architecture.apis.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Screens</span>
                      <strong class="architecture__stat-value">{{ architecture.screens.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Children</span>
                      <strong class="architecture__stat-value">{{ architecture.children.length }}</strong>
                    </article>
                  </div>
                </section>

                <div class="architecture__grid architecture__grid--wide">
                  <section class="architecture__section" data-testid="architecture-data-entities">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Engineering mapping</p>
                        <h5 class="architecture__section-title">Mapped data entities</h5>
                      </div>
                    </div>
                    @if (architecture.entities.length > 0) {
                      <div class="architecture__item-list">
                        @for (entity of architecture.entities; track entity.entityId) {
                          <article class="architecture__item-card">
                            <strong>{{ entity.name }}</strong>
                            <span>{{ entity.entityId }}</span>
                            <span>{{ entity.entityType ?? 'No type' }} · {{ entity.fieldCount }} fields</span>
                            <span>{{ entity.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No engineering data entities are mapped to this business object yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-data-flows">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Flow topology</p>
                        <h5 class="architecture__section-title">Information flows</h5>
                      </div>
                    </div>
                    @if (architecture.flows.length > 0) {
                      <div class="architecture__item-list">
                        @for (flow of architecture.flows; track flow.flowId) {
                          <article class="architecture__item-card">
                            <strong>{{ flow.name }}</strong>
                            <span>{{ flow.flowId }}</span>
                            <span>
                              {{ flow.sourceApplicationName ?? flow.sourceApplicationId ?? 'No source' }} →
                              {{ flow.targetApplicationName ?? flow.targetApplicationId ?? 'No target' }}
                            </span>
                            <span>{{ flow.direction ?? 'No direction' }} · {{ flow.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No information flows carry this business object yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-data-apis">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Interface linkage</p>
                        <h5 class="architecture__section-title">Exposed APIs</h5>
                      </div>
                    </div>
                    @if (architecture.apis.length > 0) {
                      <div class="architecture__item-list">
                        @for (api of architecture.apis; track api.id) {
                          <article class="architecture__item-card">
                            <strong>{{ api.displayName }}</strong>
                            <span>{{ api.id }}</span>
                            <span>{{ api.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No API contracts expose this business object yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-data-screens">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Experience linkage</p>
                        <h5 class="architecture__section-title">Reachable screens</h5>
                      </div>
                    </div>
                    @if (architecture.screens.length > 0) {
                      <div class="architecture__item-list">
                        @for (screen of architecture.screens; track screen.id) {
                          <article class="architecture__item-card">
                            <strong>{{ screen.displayName }}</strong>
                            <span>{{ screen.id }}</span>
                            <span>{{ screen.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No screens are currently reachable through the mapped application context.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-data-children">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Business hierarchy</p>
                        <h5 class="architecture__section-title">Child objects</h5>
                      </div>
                    </div>
                    @if (architecture.children.length > 0) {
                      <div class="architecture__item-list">
                        @for (child of architecture.children; track child.id) {
                          <article class="architecture__item-card">
                            <strong>{{ child.displayName }}</strong>
                            <span>{{ child.id }}</span>
                            <span>{{ child.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No child business objects are structured under this object yet.</p>
                    }
                  </section>
                </div>
              } @else {
                <p class="architecture__empty-block" data-testid="architecture-data-empty">
                  Select a business object to explore its mapped entities and information flows.
                </p>
              }
            </section>
          </div>
        } @else {
          <p class="architecture__empty-block" data-testid="architecture-data-empty">
            No business objects are available in the current graph.
          </p>
        }
      } @else {
        @if (state.infrastructureDeployments().length > 0) {
          <div class="architecture__layout" data-testid="architecture-infrastructure-layout">
            <aside class="architecture__rail">
              <div class="architecture__section-head">
                <div>
                  <p class="architecture__kicker">Infrastructure Architecture</p>
                  <h4 class="architecture__section-title">Deployments</h4>
                </div>
                <span class="architecture__count">{{ state.infrastructureDeployments().length }} total</span>
              </div>

              <div class="architecture__selection-list" data-testid="architecture-infrastructure-list">
                @for (deployment of state.infrastructureDeployments(); track deployment.deploymentId) {
                  <button
                    type="button"
                    class="architecture__selection-card"
                    [class.architecture__selection-card--selected]="deployment.deploymentId === state.selectedDeploymentId()"
                    [attr.data-testid]="'architecture-infrastructure-' + deployment.deploymentId"
                    (click)="state.selectInfrastructureDeployment(deployment.deploymentId)"
                  >
                    <strong class="architecture__selection-name">{{ deployment.name }}</strong>
                    <span class="architecture__selection-subtitle">
                      {{ deployment.environment ?? 'Unclassified environment' }}
                    </span>
                    <span class="architecture__selection-meta">
                      {{ deployment.componentCount }} components · {{ deployment.infrastructureCount }} nodes · {{ deployment.applicationCount }} apps
                    </span>
                  </button>
                }
              </div>
            </aside>

            <section class="architecture__content">
              @if (state.selectedInfrastructureArchitecture(); as architecture) {
                <section class="architecture__hero">
                  <div class="architecture__hero-copy">
                    <p class="architecture__kicker">Infrastructure Architecture</p>
                    <h4 class="architecture__title" data-testid="architecture-infrastructure-title">{{ architecture.name }}</h4>
                    <p class="architecture__description">
                      {{ architecture.description ?? 'Deployment topology is graph-backed through hosted components, applications, and infrastructure nodes.' }}
                    </p>
                  </div>

                  <div class="architecture__hero-meta">
                    <span class="architecture__chip">{{ architecture.environment ?? 'No environment' }}</span>
                    <span class="architecture__chip">{{ architecture.status ?? 'No status' }}</span>
                  </div>

                  <div class="architecture__stats">
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Components</span>
                      <strong class="architecture__stat-value">{{ architecture.components.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Nodes</span>
                      <strong class="architecture__stat-value">{{ architecture.infrastructureNodes.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Applications</span>
                      <strong class="architecture__stat-value">{{ architecture.applications.length }}</strong>
                    </article>
                    <article class="architecture__stat">
                      <span class="architecture__stat-label">Elements</span>
                      <strong class="architecture__stat-value">{{ architecture.elements.length }}</strong>
                    </article>
                  </div>
                </section>

                <div class="architecture__grid">
                  <section class="architecture__section" data-testid="architecture-infrastructure-components">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Hosted runtime</p>
                        <h5 class="architecture__section-title">Hosted components</h5>
                      </div>
                    </div>
                    @if (architecture.components.length > 0) {
                      <div class="architecture__item-list">
                        @for (component of architecture.components; track component.id) {
                          <article class="architecture__item-card">
                            <strong>{{ component.displayName }}</strong>
                            <span>{{ component.id }}</span>
                            <span>{{ component.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No hosted components are linked to this deployment yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-infrastructure-nodes">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Infrastructure</p>
                        <h5 class="architecture__section-title">Infrastructure nodes</h5>
                      </div>
                    </div>
                    @if (architecture.infrastructureNodes.length > 0) {
                      <div class="architecture__item-list">
                        @for (node of architecture.infrastructureNodes; track node.nodeId) {
                          <article class="architecture__item-card">
                            <strong>{{ node.name }}</strong>
                            <span>{{ node.nodeId }}</span>
                            <span>{{ node.nodeType ?? 'No type' }} · {{ node.location ?? 'No location' }}</span>
                            <span>{{ node.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No infrastructure nodes are linked to this deployment yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-infrastructure-applications">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Application context</p>
                        <h5 class="architecture__section-title">Applications deployed here</h5>
                      </div>
                    </div>
                    @if (architecture.applications.length > 0) {
                      <div class="architecture__item-list">
                        @for (application of architecture.applications; track application.id) {
                          <article class="architecture__item-card">
                            <strong>{{ application.displayName }}</strong>
                            <span>{{ application.id }}</span>
                            <span>{{ application.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">No applications are linked through hosted components yet.</p>
                    }
                  </section>

                  <section class="architecture__section" data-testid="architecture-infrastructure-elements">
                    <div class="architecture__section-head">
                      <div>
                        <p class="architecture__kicker">Fine-grained topology</p>
                        <h5 class="architecture__section-title">Deployment elements</h5>
                      </div>
                    </div>
                    @if (architecture.elements.length > 0) {
                      <div class="architecture__item-list">
                        @for (element of architecture.elements; track element.id) {
                          <article class="architecture__item-card">
                            <strong>{{ element.displayName }}</strong>
                            <span>{{ element.id }}</span>
                            <span>{{ element.status ?? 'No status' }}</span>
                          </article>
                        }
                      </div>
                    } @else {
                      <p class="architecture__empty">Deployment elements are not modeled yet for this deployment.</p>
                    }
                  </section>
                </div>
              } @else {
                <p class="architecture__empty-block" data-testid="architecture-infrastructure-empty">
                  Select a deployment to explore hosted components and infrastructure nodes.
                </p>
              }
            </section>
          </div>
        } @else {
          <p class="architecture__empty-block" data-testid="architecture-infrastructure-empty">
            No deployments are available in the current graph.
          </p>
        }
      }
    </div>
  `,
  styles: [`
    .architecture {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
      color: var(--tp-text);
    }

    .architecture__toolbar,
    .architecture__rail,
    .architecture__hero,
    .architecture__section {
      border-radius: var(--nm-radius);
      border: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      background: color-mix(in srgb, var(--tp-white) 92%, transparent);
      box-shadow: var(--tp-elevation-default);
    }

    .architecture__toolbar,
    .architecture__rail,
    .architecture__hero,
    .architecture__section {
      padding: var(--tp-space-4);
    }

    .architecture__toolbar,
    .architecture__toolbar-copy,
    .architecture__content,
    .architecture__hero-copy,
    .architecture__section-head {
      display: grid;
      gap: var(--tp-space-3);
    }

    .architecture__toolbar {
      grid-template-columns: minmax(0, 1fr) auto;
      align-items: center;
    }

    .architecture__switcher {
      display: inline-flex;
      gap: var(--tp-space-2);
      padding: var(--tp-space-1);
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-bg) 76%, var(--tp-white) 24%);
    }

    .architecture__switch-btn {
      min-height: var(--tp-touch-target-min-size);
      padding: 0 var(--tp-space-4);
      border: 1px solid transparent;
      border-radius: 999px;
      background: transparent;
      color: var(--tp-text-muted);
      font-weight: 600;
      cursor: pointer;
    }

    .architecture__switch-btn:hover {
      color: var(--tp-text-dark);
    }

    .architecture__switch-btn:focus-visible,
    .architecture__selection-card:focus-visible {
      outline: none;
      box-shadow: var(--tp-focus-ring);
    }

    .architecture__switch-btn--active {
      border-color: color-mix(in srgb, var(--tp-primary) 34%, transparent);
      background: var(--tp-primary-bg);
      color: var(--tp-primary-dark);
    }

    .architecture__layout {
      display: grid;
      grid-template-columns: minmax(15rem, 19rem) minmax(0, 1fr);
      gap: var(--tp-space-4);
      align-items: start;
    }

    .architecture__hero,
    .architecture__section,
    .architecture__content {
      display: grid;
      gap: var(--tp-space-4);
    }

    .architecture__selection-list,
    .architecture__grid,
    .architecture__item-list {
      display: grid;
      gap: var(--tp-space-3);
    }

    .architecture__selection-list {
      margin-top: var(--tp-space-3);
      gap: var(--tp-space-2);
    }

    .architecture__grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .architecture__grid--wide {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .architecture__kicker,
    .architecture__stat-label,
    .architecture__count,
    .architecture__selection-subtitle,
    .architecture__selection-meta {
      margin: 0;
      font-size: 0.73rem;
      letter-spacing: 0.06em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
    }

    .architecture__title,
    .architecture__section-title {
      margin: 0;
      color: var(--tp-text-dark);
    }

    .architecture__title {
      font-size: 1.35rem;
    }

    .architecture__section-title {
      font-size: 1rem;
    }

    .architecture__description,
    .architecture__empty,
    .architecture__empty-block {
      margin: 0;
      color: var(--tp-text-muted);
      line-height: 1.6;
    }

    .architecture__hero-meta,
    .architecture__stats {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-3);
    }

    .architecture__chip {
      display: inline-flex;
      align-items: center;
      min-height: var(--tp-touch-target-min-size);
      padding: 0 var(--tp-space-3);
      border-radius: 999px;
      background: var(--tp-primary-bg);
      color: var(--tp-primary-dark);
      font-size: 0.82rem;
      font-weight: 600;
    }

    .architecture__stat,
    .architecture__item-card,
    .architecture__selection-card {
      display: grid;
      gap: var(--tp-space-1);
      border-radius: var(--nm-radius);
      border: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
      background: color-mix(in srgb, var(--tp-white) 84%, var(--tp-bg) 16%);
      box-shadow: var(--tp-elevation-default);
    }

    .architecture__stat,
    .architecture__item-card,
    .architecture__selection-card {
      padding: var(--tp-space-3);
    }

    .architecture__stat-value,
    .architecture__selection-name {
      color: var(--tp-text-dark);
      font-weight: 700;
    }

    .architecture__selection-card {
      text-align: left;
      cursor: pointer;
      color: inherit;
      min-height: var(--tp-touch-target-min-size);
    }

    .architecture__selection-card:hover {
      transform: translateY(-1px);
      box-shadow: var(--tp-elevation-hover);
    }

    .architecture__selection-card--selected {
      border-color: color-mix(in srgb, var(--tp-primary) 44%, transparent);
      background: color-mix(in srgb, var(--tp-primary-bg) 72%, var(--tp-white) 28%);
    }

    .architecture__empty-block {
      padding: var(--tp-space-6);
      text-align: center;
      border-radius: var(--nm-radius);
      border: 1px dashed color-mix(in srgb, var(--tp-border) 32%, transparent);
      background: color-mix(in srgb, var(--tp-bg) 78%, var(--tp-white) 22%);
    }

    @media (max-width: 1180px) {
      .architecture__layout,
      .architecture__grid,
      .architecture__grid--wide,
      .architecture__toolbar {
        grid-template-columns: 1fr;
      }

      .architecture__switcher {
        width: fit-content;
      }
    }
  `],
})
export class ArchitecturePanelComponent {
  protected readonly state = inject(DesignHubStateService);
}
