import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { RouterLink } from '@angular/router';
import { DesignHubStateService } from '../../services/design-hub-state.service';

@Component({
  selector: 'app-object-definitions-explorer',
  standalone: true,
  imports: [RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="definitions-shell" data-testid="object-definitions-root">
      <header class="definitions-shell__header">
        <div>
          <p class="definitions-shell__eyebrow">Metamodel</p>
          <h1>Object Definitions</h1>
          <p class="definitions-shell__intro">
            Start from the object definitions, then inspect each definition's attributes,
            relationships, and inherited instances.
          </p>
        </div>
        <div class="definitions-shell__meta">
          <a class="app-btn app-btn--secondary" routerLink="/system-shell">View System Shell</a>
          <span class="definitions-shell__metric">{{ state.objectDefinitions().length }} definitions</span>
          <span class="definitions-shell__metric">
            {{ state.selectedObjectDefinition()?.instanceCount ?? 0 }} instances in selected definition
          </span>
        </div>
      </header>

      <div class="definitions-layout">
        <section class="definitions-list">
          <div class="definitions-list__toolbar">
            <label class="definitions-list__search">
              <span>Search definitions</span>
              <input
                type="search"
                [value]="state.definitionSearchTerm()"
                (input)="onSearchInput($event)"
                placeholder="Screen, Process, Capability..."
              />
            </label>
          </div>

          <div class="definitions-list__table-wrap">
            <table class="definitions-table">
              <thead>
                <tr>
                  <th>Definition</th>
                  <th>Category</th>
                  <th>Tier</th>
                  <th>Instances</th>
                  <th>Relationships</th>
                  <th></th>
                </tr>
              </thead>
              <tbody>
                @for (definition of state.filteredObjectDefinitions(); track definition.type) {
                  <tr [class.is-selected]="state.selectedObjectDefinitionType() === definition.type">
                    <td>
                      <div class="definitions-table__primary">
                        <strong>{{ definition.displayName }}</strong>
                        <span>{{ definition.label }}</span>
                      </div>
                    </td>
                    <td>{{ definition.category }}</td>
                    <td>{{ definition.tier }}</td>
                    <td>{{ definition.instanceCount }}</td>
                    <td>{{ definition.relationshipTypeCount }}</td>
                    <td class="definitions-table__actions">
                      <button type="button" class="app-btn app-btn--secondary" (click)="state.selectObjectDefinition(definition.type)">
                        View
                      </button>
                    </td>
                  </tr>
                } @empty {
                  <tr>
                    <td colspan="6" class="definitions-table__empty">No object definitions match the current search.</td>
                  </tr>
                }
              </tbody>
            </table>
          </div>
        </section>

        <section class="definition-detail">
          @if (state.selectedObjectDefinition(); as definition) {
            <header class="definition-detail__header">
              <div>
                <p class="definition-detail__eyebrow">{{ definition.category }}</p>
                <h2>{{ definition.displayName }}</h2>
                <p class="definition-detail__purpose">{{ definition.purpose }}</p>
              </div>
              <div class="definition-detail__badges">
                <span class="definition-detail__badge">{{ definition.tier }}</span>
                <span class="definition-detail__badge">{{ definition.implementationStatus || 'Status unavailable' }}</span>
              </div>
            </header>

            <div class="definition-detail__aliases">
              <span class="definition-detail__aliases-label">Aliases</span>
              <span class="definition-detail__aliases-value">{{ definition.aliases.join(', ') }}</span>
            </div>

            <section class="definition-section">
              <div class="definition-section__header">
                <h3>Attributes</h3>
                <span>{{ definition.attributes.length }}</span>
              </div>
              <div class="definition-section__table-wrap">
                <table class="definition-section__table">
                  <thead>
                    <tr>
                      <th>Attribute</th>
                      <th>Type</th>
                      <th>Required</th>
                      <th>Description</th>
                      <th>Constraints</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (attribute of definition.attributes; track attribute.name) {
                      <tr>
                        <td>{{ attribute.name }}</td>
                        <td>{{ attribute.type }}</td>
                        <td>{{ attribute.required ? 'Yes' : 'No' }}</td>
                        <td>{{ attribute.description }}</td>
                        <td>{{ attribute.constraints }}</td>
                      </tr>
                    } @empty {
                      <tr>
                        <td colspan="5" class="definition-section__empty">No attributes are defined for this object.</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </section>

            <section class="definition-section">
              <div class="definition-section__header">
                <h3>Relationships</h3>
                <span>{{ definition.relationships.length }}</span>
              </div>
              <div class="definition-section__table-wrap">
                <table class="definition-section__table">
                  <thead>
                    <tr>
                      <th>Relationship</th>
                      <th>Direction</th>
                      <th>Target</th>
                      <th>Cardinality</th>
                      <th>Required</th>
                      <th>Severity</th>
                      <th>Implementation</th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (relationship of definition.relationships; track relationship.name + relationship.target) {
                      <tr>
                        <td>{{ relationship.name }}</td>
                        <td>{{ relationship.direction }}</td>
                        <td>{{ relationship.target }}</td>
                        <td>{{ relationship.cardinality }}</td>
                        <td>{{ relationship.required ? 'Yes' : 'No' }}</td>
                        <td>{{ relationship.severity }}</td>
                        <td>{{ relationship.implementation }}</td>
                      </tr>
                    } @empty {
                      <tr>
                        <td colspan="7" class="definition-section__empty">No relationships are defined for this object.</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </section>

            <section class="definition-section">
              <div class="definition-section__header">
                <h3>Instances</h3>
                <span>{{ definition.instanceCount }}</span>
              </div>
              <div class="definition-section__table-wrap">
                <table class="definition-section__table">
                  <thead>
                    <tr>
                      <th>ID</th>
                      <th>Name</th>
                      <th>Status</th>
                      <th>Module</th>
                      <th>Domain</th>
                      <th>Relations</th>
                      <th></th>
                    </tr>
                  </thead>
                  <tbody>
                    @for (instance of definition.instances; track instance.id) {
                      <tr>
                        <td>{{ instance.id }}</td>
                        <td>{{ instance.displayName }}</td>
                        <td>{{ instance.status || 'N/A' }}</td>
                        <td>{{ instance.module || 'N/A' }}</td>
                        <td>{{ instance.domain || 'N/A' }}</td>
                        <td>{{ instance.relationCount ?? 0 }}</td>
                        <td class="definitions-table__actions">
                          @if (definition.type === 'screen') {
                            <a
                              class="app-btn app-btn--secondary"
                              [routerLink]="['/workspace']"
                              [queryParams]="{ screen: instance.id }"
                            >
                              Open Screen
                            </a>
                          }
                        </td>
                      </tr>
                    } @empty {
                      <tr>
                        <td colspan="7" class="definition-section__empty">No instances are available for this object definition.</td>
                      </tr>
                    }
                  </tbody>
                </table>
              </div>
            </section>
          } @else {
            <div class="definition-detail__empty">
              Select a definition from the list to inspect its attributes, relationships, and instances.
            </div>
          }
        </section>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      min-height: 100vh;
      background:
        radial-gradient(circle at top right, color-mix(in srgb, var(--tp-primary-light) 45%, transparent), transparent 38%),
        linear-gradient(180deg, color-mix(in srgb, var(--tp-white) 94%, var(--tp-surface)), var(--tp-surface));
      color: var(--tp-text);
    }

    .definitions-shell {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-6);
      padding: var(--tp-space-6);
    }

    .definitions-shell__header,
    .definitions-list,
    .definition-detail {
      border: 1px solid color-mix(in srgb, var(--tp-border) 24%, transparent);
      border-radius: var(--tp-space-5);
      background: color-mix(in srgb, var(--tp-white) 86%, transparent);
    }

    .definitions-shell__header {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-5);
      align-items: flex-start;
      padding: var(--tp-space-6);
    }

    .definitions-shell__eyebrow,
    .definition-detail__eyebrow {
      margin: 0 0 var(--tp-space-2);
      font-size: 0.75rem;
      font-weight: 700;
      letter-spacing: 0.08em;
      text-transform: uppercase;
      color: var(--tp-primary-dark);
    }

    .definitions-shell__header h1,
    .definition-detail__header h2 {
      margin: 0;
      font-size: 1.8rem;
      line-height: 1.1;
      color: var(--tp-primary-dark);
    }

    .definitions-shell__intro,
    .definition-detail__purpose {
      margin: var(--tp-space-3) 0 0;
      max-width: 68ch;
      color: var(--tp-text-secondary);
    }

    .definitions-shell__meta {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
      justify-content: flex-end;
    }

    .definitions-shell__metric,
    .definition-detail__badge {
      display: inline-flex;
      align-items: center;
      min-height: var(--tp-touch-target-min-size);
      padding: 0 var(--tp-space-3);
      border-radius: 999px;
      background: color-mix(in srgb, var(--tp-primary-light) 40%, var(--tp-white));
      color: var(--tp-primary-dark);
      font-size: 0.85rem;
      font-weight: 600;
    }

    .definitions-layout {
      display: grid;
      grid-template-columns: minmax(360px, 480px) minmax(0, 1fr);
      gap: var(--tp-space-5);
      align-items: start;
    }

    .definitions-list,
    .definition-detail {
      overflow: hidden;
    }

    .definitions-list__toolbar {
      padding: var(--tp-space-4);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
    }

    .definitions-list__search {
      display: grid;
      gap: var(--tp-space-2);
      font-size: 0.82rem;
      color: var(--tp-text-secondary);
    }

    .definitions-list__search input {
      min-height: var(--tp-touch-target-min-size);
      padding: 0 var(--tp-space-3);
      border: 1px solid var(--tp-border);
      border-radius: var(--tp-space-3);
      background: var(--tp-white);
      color: var(--tp-text);
    }

    .definitions-list__table-wrap,
    .definition-section__table-wrap {
      overflow: auto;
    }

    .definitions-table,
    .definition-section__table {
      width: 100%;
      border-collapse: collapse;
    }

    .definitions-table th,
    .definitions-table td,
    .definition-section__table th,
    .definition-section__table td {
      padding: var(--tp-space-3);
      border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 18%, transparent);
      text-align: left;
      vertical-align: top;
      font-size: 0.9rem;
    }

    .definitions-table thead th,
    .definition-section__table thead th {
      position: sticky;
      top: 0;
      background: color-mix(in srgb, var(--tp-white) 90%, var(--tp-surface));
      color: var(--tp-text-secondary);
      font-size: 0.78rem;
      text-transform: uppercase;
      letter-spacing: 0.04em;
      z-index: 1;
    }

    .definitions-table__primary {
      display: grid;
      gap: var(--tp-space-1);
    }

    .definitions-table__primary span {
      color: var(--tp-text-muted);
      font-size: 0.82rem;
    }

    .definitions-table__actions {
      white-space: nowrap;
    }

    .definitions-table tr.is-selected {
      background: color-mix(in srgb, var(--tp-primary-light) 24%, var(--tp-white));
    }

    .definition-detail {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
      padding: var(--tp-space-5);
    }

    .definition-detail__header {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-4);
      align-items: flex-start;
    }

    .definition-detail__badges {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
      justify-content: flex-end;
    }

    .definition-detail__aliases {
      display: flex;
      flex-wrap: wrap;
      gap: var(--tp-space-2);
      align-items: center;
      padding: var(--tp-space-3) var(--tp-space-4);
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-surface) 82%, var(--tp-white));
    }

    .definition-detail__aliases-label {
      font-size: 0.78rem;
      font-weight: 700;
      text-transform: uppercase;
      letter-spacing: 0.05em;
      color: var(--tp-text-secondary);
    }

    .definition-detail__aliases-value {
      color: var(--tp-text);
    }

    .definition-section {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-3);
      padding: var(--tp-space-4);
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-white) 88%, var(--tp-surface));
    }

    .definition-section__header {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-3);
      align-items: baseline;
    }

    .definition-section__header h3 {
      margin: 0;
      color: var(--tp-primary-dark);
    }

    .definition-section__header span {
      color: var(--tp-text-muted);
      font-size: 0.85rem;
    }

    .definition-detail__empty,
    .definition-section__empty,
    .definitions-table__empty {
      color: var(--tp-text-muted);
      text-align: center;
      padding: var(--tp-space-5);
    }

    @media (max-width: 1080px) {
      .definitions-layout {
        grid-template-columns: 1fr;
      }

      .definitions-shell__header,
      .definition-detail__header {
        flex-direction: column;
      }

      .definitions-shell__meta,
      .definition-detail__badges {
        justify-content: flex-start;
      }
    }

    @media (max-width: 720px) {
      .definitions-shell {
        padding: var(--tp-space-3);
      }

      .definitions-shell__header,
      .definition-detail {
        padding: var(--tp-space-4);
      }

      .definitions-table th,
      .definitions-table td,
      .definition-section__table th,
      .definition-section__table td {
        padding: var(--tp-space-2);
      }
    }
  `],
})
export class ObjectDefinitionsExplorerComponent {
  readonly state = inject(DesignHubStateService);

  onSearchInput(event: Event): void {
    const value = event.target instanceof HTMLInputElement ? event.target.value : '';
    this.state.setDefinitionSearchTerm(value);
  }
}
