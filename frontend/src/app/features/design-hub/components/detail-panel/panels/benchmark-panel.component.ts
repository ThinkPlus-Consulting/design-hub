import { ChangeDetectionStrategy, Component, inject } from '@angular/core';
import { BenchmarkDimensionScore, BenchmarkTypeScore } from '../../../../../models';
import { DesignHubStateService } from '../../../services/design-hub-state.service';

@Component({
  selector: 'app-benchmark-panel',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="benchmark" data-testid="benchmark-panel">
      @if (state.benchmark(); as benchmark) {
        <section class="benchmark__hero" data-testid="benchmark-summary">
          <div class="benchmark__copy">
            <p class="benchmark__eyebrow">Graph Benchmark</p>
            <h4 class="benchmark__title">Coverage across the live delivery graph</h4>
            <p class="benchmark__scope">{{ benchmark.summary.scopeNote }}</p>
          </div>

          <div class="benchmark__kpis">
            <article class="benchmark__kpi">
              <span class="benchmark__kpi-label">Overall score</span>
              <strong class="benchmark__kpi-value" data-testid="benchmark-overall-score">
                {{ formatScore(benchmark.summary.overallScore) }}
              </strong>
            </article>
            <article class="benchmark__kpi">
              <span class="benchmark__kpi-label">Node types covered</span>
              <strong class="benchmark__kpi-value">{{ benchmark.summary.coveredNodeTypes }}</strong>
            </article>
            <article class="benchmark__kpi">
              <span class="benchmark__kpi-label">Total nodes</span>
              <strong class="benchmark__kpi-value">{{ benchmark.summary.totalNodes }}</strong>
            </article>
          </div>
        </section>

        <section class="benchmark__section" data-testid="benchmark-dimensions">
          <div class="benchmark__section-head">
            <div>
              <p class="benchmark__section-kicker">Global quality</p>
              <h5 class="benchmark__section-title">Dimension scores</h5>
            </div>
          </div>

          <div class="benchmark__dimension-grid">
            @for (dimension of benchmark.summary.dimensions; track dimension.dimension) {
              <article class="benchmark__dimension-card">
                <div class="benchmark__dimension-head">
                  <strong class="benchmark__dimension-title">{{ formatLabel(dimension.dimension) }}</strong>
                  <span
                    class="benchmark__status"
                    [class]="'benchmark__status--' + dimension.status.toLowerCase()"
                  >
                    {{ dimension.status }}
                  </span>
                </div>
                <div class="benchmark__dimension-score">{{ formatScore(dimension.score) }}</div>
                <p class="benchmark__dimension-detail">{{ dimension.detail }}</p>
              </article>
            }
          </div>
        </section>

        <section class="benchmark__section benchmark__section--detail">
          <div class="benchmark__section-head">
            <div>
              <p class="benchmark__section-kicker">Type-level coverage</p>
              <h5 class="benchmark__section-title">Node type benchmark</h5>
            </div>
          </div>

          <div class="benchmark__detail-layout">
            <div class="benchmark__type-list" data-testid="benchmark-type-list">
              @for (type of benchmark.types; track type.nodeType) {
                <button
                  type="button"
                  class="benchmark__type-card"
                  [class.benchmark__type-card--selected]="type.nodeType === state.selectedBenchmarkNodeType()"
                  [attr.data-testid]="'benchmark-type-' + slug(type.nodeType)"
                  (click)="state.setSelectedBenchmarkNodeType(type.nodeType)"
                >
                  <span class="benchmark__type-name">{{ formatNodeType(type.nodeType) }}</span>
                  <span class="benchmark__type-score">{{ formatScore(type.overallScore) }}</span>
                  <span class="benchmark__type-count">{{ type.totalNodes }} nodes</span>
                </button>
              }
            </div>

            @if (state.selectedBenchmarkType(); as type) {
              <article class="benchmark__type-detail" data-testid="benchmark-type-detail">
                <div class="benchmark__type-detail-head">
                  <div>
                    <p class="benchmark__section-kicker">Selected node type</p>
                    <h5 class="benchmark__type-detail-title">{{ formatNodeType(type.nodeType) }}</h5>
                  </div>
                  <span class="benchmark__status" [class]="'benchmark__status--' + statusClass(type)">
                    {{ statusLabel(type) }}
                  </span>
                </div>

                <dl class="benchmark__metric-grid">
                  <div class="benchmark__metric">
                    <dt>Nodes</dt>
                    <dd>{{ type.totalNodes }}</dd>
                  </div>
                  <div class="benchmark__metric">
                    <dt>Target attributes</dt>
                    <dd>{{ type.targetAttributeCount }}</dd>
                  </div>
                  <div class="benchmark__metric">
                    <dt>Target relationships</dt>
                    <dd>{{ type.targetRelationshipCount }}</dd>
                  </div>
                  <div class="benchmark__metric">
                    <dt>Overall score</dt>
                    <dd>{{ formatScore(type.overallScore) }}</dd>
                  </div>
                </dl>

                <div class="benchmark__score-grid">
                  <article class="benchmark__score-card">
                    <span class="benchmark__score-label">Attribute depth</span>
                    <strong class="benchmark__score-value">{{ formatScore(type.attributeDepthScore) }}</strong>
                  </article>
                  <article class="benchmark__score-card">
                    <span class="benchmark__score-label">Relationship coverage</span>
                    <strong class="benchmark__score-value">{{ formatScore(type.relationshipCoverageScore) }}</strong>
                  </article>
                  <article class="benchmark__score-card">
                    <span class="benchmark__score-label">Source traceability</span>
                    <strong class="benchmark__score-value">
                      {{ formatOptionalScore(type.sourceTraceabilityScore, type.sourceTraceabilityApplicable) }}
                    </strong>
                  </article>
                  <article class="benchmark__score-card">
                    <span class="benchmark__score-label">Queryability</span>
                    <strong class="benchmark__score-value">{{ formatScore(type.queryabilityScore) }}</strong>
                  </article>
                </div>

                <section class="benchmark__recommendations" data-testid="benchmark-recommendations">
                  <div class="benchmark__section-head">
                    <div>
                      <p class="benchmark__section-kicker">Remediation</p>
                      <h6 class="benchmark__recommendations-title">Gap recommendations</h6>
                    </div>
                  </div>

                  @if (type.gapRecommendations.length > 0) {
                    <ul class="benchmark__recommendation-list">
                      @for (recommendation of type.gapRecommendations; track recommendation) {
                        <li class="benchmark__recommendation-item">{{ recommendation }}</li>
                      }
                    </ul>
                  } @else {
                    <p class="benchmark__recommendation-empty">
                      No gap recommendations are currently reported for this node type.
                    </p>
                  }
                </section>
              </article>
            }
          </div>
        </section>
      } @else {
        <p class="benchmark__empty" data-testid="benchmark-empty">
          Benchmark diagnostics are not available yet.
        </p>
      }
    </div>
  `,
  styles: [`
    .benchmark {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-4);
      color: var(--tp-text);
    }

    .benchmark__hero,
    .benchmark__section {
      display: grid;
      gap: var(--tp-space-4);
      padding: var(--tp-space-4);
      border: 1px solid var(--tp-border);
      border-radius: var(--nm-radius);
      background: color-mix(in srgb, var(--tp-white) 76%, var(--tp-surface));
      box-shadow: var(--tp-elevation-default);
    }

    .benchmark__hero {
      grid-template-columns: minmax(0, 1.45fr) minmax(0, 1fr);
    }

    .benchmark__copy,
    .benchmark__section-head {
      display: flex;
      flex-direction: column;
      gap: var(--tp-space-2);
      min-width: 0;
    }

    .benchmark__eyebrow,
    .benchmark__section-kicker,
    .benchmark__kpi-label,
    .benchmark__score-label {
      font-size: 0.72rem;
      font-weight: 700;
      letter-spacing: 0.05em;
      text-transform: uppercase;
      color: var(--tp-text-muted);
    }

    .benchmark__title,
    .benchmark__section-title,
    .benchmark__type-detail-title,
    .benchmark__recommendations-title {
      margin: 0;
      color: var(--tp-text-dark);
    }

    .benchmark__title {
      font-size: 1rem;
    }

    .benchmark__section-title,
    .benchmark__type-detail-title {
      font-size: 0.92rem;
    }

    .benchmark__scope,
    .benchmark__dimension-detail,
    .benchmark__recommendation-empty {
      font-size: 0.8rem;
      color: var(--tp-text);
      line-height: 1.45;
    }

    .benchmark__kpis,
    .benchmark__dimension-grid,
    .benchmark__score-grid {
      display: grid;
      gap: var(--tp-space-3);
    }

    .benchmark__kpis {
      grid-template-columns: repeat(3, minmax(0, 1fr));
    }

    .benchmark__kpi,
    .benchmark__dimension-card,
    .benchmark__type-detail,
    .benchmark__score-card,
    .benchmark__recommendations {
      display: grid;
      gap: var(--tp-space-2);
      border-radius: var(--tp-space-4);
      border: 1px solid color-mix(in srgb, var(--tp-primary) 18%, var(--tp-border));
      background: var(--tp-white);
      padding: var(--tp-space-3);
    }

    .benchmark__kpi-value,
    .benchmark__dimension-score,
    .benchmark__score-value {
      font-size: 1.15rem;
      color: var(--tp-primary-dark);
    }

    .benchmark__dimension-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .benchmark__dimension-head,
    .benchmark__type-detail-head {
      display: flex;
      justify-content: space-between;
      gap: var(--tp-space-3);
      align-items: start;
    }

    .benchmark__dimension-title,
    .benchmark__type-name {
      font-size: 0.82rem;
      font-weight: 700;
      color: var(--tp-text-dark);
    }

    .benchmark__status {
      display: inline-flex;
      align-items: center;
      min-height: var(--tp-touch-target-min-size);
      padding-inline: var(--tp-space-3);
      border-radius: 999px;
      font-size: 0.72rem;
      font-weight: 700;
      white-space: nowrap;
    }

    .benchmark__status--green {
      background: var(--tp-toast-success-bg);
      color: var(--tp-primary-dark);
    }

    .benchmark__status--amber {
      background: var(--tp-toast-warn-bg);
      color: var(--tp-warning-dark);
    }

    .benchmark__status--red {
      background: var(--tp-danger-bg);
      color: var(--tp-danger);
    }

    .benchmark__detail-layout {
      display: grid;
      grid-template-columns: minmax(0, 0.9fr) minmax(0, 1.1fr);
      gap: var(--tp-space-4);
    }

    .benchmark__type-list {
      display: grid;
      gap: var(--tp-space-2);
      align-content: start;
    }

    .benchmark__type-card {
      display: grid;
      gap: var(--tp-space-1);
      min-height: var(--tp-touch-target-min-size);
      padding: var(--tp-space-3);
      border: 1px solid color-mix(in srgb, var(--tp-border) 72%, var(--tp-white));
      border-radius: var(--tp-space-4);
      background: var(--tp-white);
      color: var(--tp-text);
      text-align: left;
      cursor: pointer;
      transition:
        border-color 140ms ease,
        background-color 140ms ease,
        box-shadow 140ms ease,
        transform 140ms ease;
    }

    .benchmark__type-card:hover {
      border-color: var(--tp-primary);
      background: var(--tp-primary-bg-hover);
      box-shadow: var(--tp-elevation-hover);
      transform: translateY(-1px);
    }

    .benchmark__type-card:focus-visible {
      outline: none;
      box-shadow: var(--tp-focus-ring);
    }

    .benchmark__type-card--selected {
      border-color: var(--tp-primary);
      background: var(--tp-primary-bg);
      box-shadow: var(--tp-elevation-default);
    }

    .benchmark__type-score {
      font-size: 0.96rem;
      font-weight: 700;
      color: var(--tp-primary-dark);
    }

    .benchmark__type-count {
      font-size: 0.74rem;
      color: var(--tp-text-muted);
    }

    .benchmark__metric-grid {
      display: grid;
      grid-template-columns: repeat(2, minmax(0, 1fr));
      gap: var(--tp-space-2);
      margin: 0;
    }

    .benchmark__metric {
      display: grid;
      gap: var(--tp-space-1);
      padding: var(--tp-space-3);
      border-radius: var(--tp-space-3);
      background: color-mix(in srgb, var(--tp-surface) 68%, var(--tp-white));
    }

    .benchmark__metric dt {
      font-size: 0.72rem;
      font-weight: 700;
      color: var(--tp-text-muted);
      text-transform: uppercase;
      letter-spacing: 0.05em;
    }

    .benchmark__metric dd {
      margin: 0;
      font-size: 0.92rem;
      font-weight: 700;
      color: var(--tp-text-dark);
    }

    .benchmark__score-grid {
      grid-template-columns: repeat(2, minmax(0, 1fr));
    }

    .benchmark__recommendation-list {
      display: grid;
      gap: var(--tp-space-2);
      margin: 0;
      padding-inline-start: var(--tp-space-4);
      color: var(--tp-text);
    }

    .benchmark__recommendation-item {
      line-height: 1.45;
    }

    .benchmark__empty {
      padding: var(--tp-space-6);
      border-radius: var(--tp-space-4);
      background: color-mix(in srgb, var(--tp-surface) 86%, var(--tp-white));
      color: var(--tp-text-muted);
      text-align: center;
      font-style: italic;
    }

    @media (max-width: 1100px) {
      .benchmark__hero,
      .benchmark__detail-layout,
      .benchmark__dimension-grid,
      .benchmark__score-grid,
      .benchmark__kpis {
        grid-template-columns: 1fr;
      }

      .benchmark__metric-grid {
        grid-template-columns: 1fr;
      }
    }
  `],
})
export class BenchmarkPanelComponent {
  readonly state = inject(DesignHubStateService);

  formatScore(value: number): string {
    return value.toFixed(1);
  }

  formatOptionalScore(value: number | null, applicable: boolean): string {
    if (!applicable) {
      return 'N/A';
    }
    return this.formatScore(value ?? 0);
  }

  formatLabel(value: string): string {
    return value
      .replace(/([A-Z])/g, ' $1')
      .replace(/^./, (first) => first.toUpperCase())
      .trim();
  }

  formatNodeType(value: string): string {
    return this.formatLabel(value);
  }

  slug(value: string): string {
    return value.replace(/([a-z])([A-Z])/g, '$1-$2').toLowerCase();
  }

  statusClass(type: BenchmarkTypeScore | BenchmarkDimensionScore): string {
    const score = 'overallScore' in type ? type.overallScore : type.score;

    if (score >= 80) {
      return 'green';
    }

    if (score >= 60) {
      return 'amber';
    }

    return 'red';
  }

  statusLabel(type: BenchmarkTypeScore): string {
    return this.statusClass(type).toUpperCase();
  }
}
