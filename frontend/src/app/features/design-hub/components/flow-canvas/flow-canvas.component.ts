import {
  Component,
  ChangeDetectionStrategy,
  inject,
  computed,
  signal,
  ElementRef,
  viewChild,
} from '@angular/core';
import { DesignHubStateService } from '../../services/design-hub-state.service';
import { Screen } from '../../../../models';

interface NodePosition {
  screen: Screen;
  x: number;
  y: number;
}

interface TransitionArrow {
  from: NodePosition;
  to: NodePosition;
  label: string;
}

const NODE_WIDTH = 180;
const NODE_HEIGHT = 72;
const GRID_GAP_X = 240;
const GRID_GAP_Y = 120;
const COLUMNS = 4;

@Component({
  selector: 'app-flow-canvas',
  standalone: true,
  imports: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div
      class="canvas"
      data-testid="canvas-root"
      (mousedown)="onPanStart($event)"
      (mousemove)="onPanMove($event)"
      (mouseup)="onPanEnd()"
      (mouseleave)="onPanEnd()"
    >
      <!-- Toolbar -->
      <div class="canvas__toolbar" data-testid="canvas-toolbar">
        <button class="canvas__tool-btn" data-testid="zoom-in" (click)="zoomIn()">+</button>
        <span class="canvas__zoom-label" data-testid="zoom-level">{{ zoomPercent() }}%</span>
        <button class="canvas__tool-btn" data-testid="zoom-out" (click)="zoomOut()">-</button>
        <button class="canvas__tool-btn" data-testid="zoom-reset" (click)="resetView()">Reset</button>
      </div>

      <svg
        #canvasSvg
        class="canvas__svg"
        [attr.viewBox]="viewBox()"
        data-testid="canvas-svg"
      >
        <defs>
          <marker
            id="arrowhead"
            markerWidth="10"
            markerHeight="7"
            refX="10"
            refY="3.5"
            orient="auto"
          >
            <polygon points="0 0, 10 3.5, 0 7" fill="var(--tp-text)" opacity="0.4" />
          </marker>
        </defs>

        <!-- Transition arrows -->
        @if (state.displayOptions().showTransitions) {
          @for (arrow of arrows(); track arrow.label) {
            <line
              [attr.x1]="arrow.from.x + 180"
              [attr.y1]="arrow.from.y + 36"
              [attr.x2]="arrow.to.x"
              [attr.y2]="arrow.to.y + 36"
              stroke="var(--tp-text)"
              stroke-opacity="0.25"
              stroke-width="1.5"
              marker-end="url(#arrowhead)"
              data-testid="transition-arrow"
            />
          }
        }

        <!-- Screen nodes -->
        @for (node of nodePositions(); track node.screen.surfaceId) {
          <g
            class="canvas__node"
            [class.canvas__node--selected]="node.screen.surfaceId === state.selectedScreenId()"
            [attr.transform]="'translate(' + node.x + ',' + node.y + ')'"
            [attr.data-testid]="'node-' + node.screen.surfaceId"
            (click)="state.selectScreen(node.screen.surfaceId)"
            style="cursor: pointer"
          >
            <rect
              width="180"
              height="72"
              rx="12"
              ry="12"
              [attr.fill]="getNodeColor(node.screen.designStatus)"
              [attr.stroke]="node.screen.surfaceId === state.selectedScreenId() ? 'var(--tp-primary-dark)' : 'transparent'"
              stroke-width="2"
              opacity="0.9"
            />
            <text
              x="90"
              y="28"
              text-anchor="middle"
              fill="white"
              font-size="12"
              font-weight="600"
              font-family="inherit"
            >{{ truncate(node.screen.label, 22) }}</text>
            <text
              x="90"
              y="46"
              text-anchor="middle"
              fill="white"
              font-size="10"
              font-family="inherit"
              opacity="0.75"
            >{{ node.screen.module }}</text>

            <!-- Status row -->
            <text
              x="90"
              y="62"
              text-anchor="middle"
              fill="white"
              font-size="8"
              font-family="inherit"
              opacity="0.6"
            >D:{{ statusAbbrev(node.screen.designStatus) }} P:{{ statusAbbrev(node.screen.prototypeStatus) }} V:{{ statusAbbrev(node.screen.deliveryStatus) }}</text>

            <!-- Gap indicator -->
            @if (state.displayOptions().showGaps && node.screen._legacy.gaps.length > 0) {
              <circle
                cx="172"
                cy="8"
                r="10"
                fill="var(--tp-danger)"
              />
              <text
                x="172"
                y="12"
                text-anchor="middle"
                fill="white"
                font-size="9"
                font-weight="700"
              >{{ node.screen._legacy.gaps.length }}</text>
            }

            <!-- Empty state indicator -->
            @if (state.displayOptions().showEmptyStates && node.screen._legacy.emptyState) {
              <circle cx="12" cy="8" r="6" fill="var(--tp-primary-dark)" />
              <text x="12" y="11" text-anchor="middle" fill="white" font-size="7" font-weight="700">E</text>
            }
          </g>
        } @empty {
          <text x="50%" y="50%" text-anchor="middle" fill="var(--tp-text)" font-size="16">
            No screens loaded. Is the backend running?
          </text>
        }
      </svg>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      height: 100%;
    }

    .canvas {
      position: relative;
      width: 100%;
      height: 100%;
      background:
        radial-gradient(circle, color-mix(in srgb, var(--tp-border) 22%, transparent) 1px, transparent 1px);
      background-size: 24px 24px;
      cursor: grab;

      &:active {
        cursor: grabbing;
      }
    }

    .canvas__toolbar {
      position: absolute;
      top: var(--tp-space-3);
      right: var(--tp-space-3);
      display: flex;
      align-items: center;
      gap: var(--tp-space-2);
      padding: var(--tp-space-2) var(--tp-space-3);
      background: var(--tp-surface);
      border-radius: 0.72rem;
      box-shadow: 0 4px 12px color-mix(in srgb, var(--tp-text-dark) 8%, transparent);
      z-index: 10;
    }

    .canvas__tool-btn {
      border: 1px solid var(--tp-border);
      border-radius: 6px;
      background: var(--tp-surface);
      color: var(--tp-text);
      padding: 4px 10px;
      font-size: 0.8rem;
      font-weight: 600;
      cursor: pointer;
      font-family: inherit;

      &:hover {
        background: var(--tp-primary-bg-hover);
      }
    }

    .canvas__zoom-label {
      font-size: 0.75rem;
      font-weight: 600;
      min-width: 40px;
      text-align: center;
    }

    .canvas__svg {
      width: 100%;
      height: 100%;
    }

    .canvas__node {
      transition: opacity 0.15s ease;

      &:hover rect {
        opacity: 1 !important;
      }
    }
  `],
})
export class FlowCanvasComponent {
  readonly state = inject(DesignHubStateService);

  private readonly svgRef = viewChild<ElementRef<SVGSVGElement>>('canvasSvg');

  private isPanning = signal(false);
  private panStartX = 0;
  private panStartY = 0;
  private panStartPanX = 0;
  private panStartPanY = 0;

  readonly zoomPercent = computed(() => Math.round(this.state.zoom() * 100));

  readonly nodePositions = computed<NodePosition[]>(() => {
    const screens = this.state.filteredScreens();
    return screens.map((screen, i) => ({
      screen,
      x: 40 + (i % COLUMNS) * GRID_GAP_X,
      y: 40 + Math.floor(i / COLUMNS) * GRID_GAP_Y,
    }));
  });

  readonly arrows = computed<TransitionArrow[]>(() => {
    const nodes = this.nodePositions();
    const nodeMap = new Map(nodes.map((n) => [n.screen.surfaceId, n]));
    const result: TransitionArrow[] = [];

    for (const node of nodes) {
      for (const t of node.screen._legacy.transitions) {
        const target = nodeMap.get(t);
        if (target) {
          result.push({ from: node, to: target, label: `${node.screen.surfaceId}->${t}` });
        }
      }
    }
    return result;
  });

  readonly viewBox = computed(() => {
    const z = this.state.zoom();
    const px = this.state.panX();
    const py = this.state.panY();
    const screens = this.state.filteredScreens();
    const maxCol = Math.min(screens.length, COLUMNS);
    const maxRow = Math.ceil(screens.length / COLUMNS);
    const w = Math.max(800, maxCol * GRID_GAP_X + 100);
    const h = Math.max(500, maxRow * GRID_GAP_Y + 100);
    return `${-px / z} ${-py / z} ${w / z} ${h / z}`;
  });

  getNodeColor(status: string): string {
    switch (status) {
      case 'COMPLETE':
        return 'var(--dh-complete)';
      case 'SPECIFIED':
        return 'var(--dh-specified)';
      case 'NOT_STARTED':
        return 'var(--dh-not-started)';
      default:
        return 'var(--tp-border)';
    }
  }

  truncate(text: string, max: number): string {
    return text.length > max ? text.slice(0, max - 1) + '...' : text;
  }

  statusAbbrev(status: string): string {
    switch (status) {
      case 'COMPLETE':
      case 'PROTOTYPED':
      case 'INTEGRATED':
      case 'TESTED':
        return 'OK';
      case 'SPECIFIED':
        return 'SP';
      case 'NOT_STARTED':
        return '--';
      default:
        return '??';
    }
  }

  zoomIn(): void {
    this.state.setZoom(this.state.zoom() + 0.1);
  }

  zoomOut(): void {
    this.state.setZoom(this.state.zoom() - 0.1);
  }

  resetView(): void {
    this.state.resetView();
  }

  onPanStart(e: MouseEvent): void {
    if (e.button !== 0) return;
    this.isPanning.set(true);
    this.panStartX = e.clientX;
    this.panStartY = e.clientY;
    this.panStartPanX = this.state.panX();
    this.panStartPanY = this.state.panY();
  }

  onPanMove(e: MouseEvent): void {
    if (!this.isPanning()) return;
    const dx = e.clientX - this.panStartX;
    const dy = e.clientY - this.panStartY;
    this.state.setPan(this.panStartPanX + dx, this.panStartPanY + dy);
  }

  onPanEnd(): void {
    this.isPanning.set(false);
  }
}
