import { ChangeDetectionStrategy, Component, OnInit, inject } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { DesignHubStateService } from './services/design-hub-state.service';
import { ScreenSidebarComponent } from './components/screen-sidebar/screen-sidebar.component';
import { FlowCanvasComponent } from './components/flow-canvas/flow-canvas.component';
import { DetailPanelComponent } from './components/detail-panel/detail-panel.component';

@Component({
  selector: 'app-design-hub-workspace-page',
  standalone: true,
  imports: [ScreenSidebarComponent, FlowCanvasComponent, DetailPanelComponent],
  providers: [DesignHubStateService],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="design-hub-viewport" data-testid="design-hub-workspace-root">
      <div class="design-hub-layout">
        <aside class="dh-sidebar" data-testid="sidebar">
          <app-screen-sidebar />
        </aside>
        <main class="dh-canvas" data-testid="flow-canvas">
          <app-flow-canvas />
        </main>
        <section class="dh-detail" data-testid="detail-panel">
          <app-detail-panel />
        </section>
      </div>
    </div>
  `,
  styles: [`
    :host {
      display: block;
      height: 100vh;
      overflow: hidden;
    }

    .design-hub-viewport {
      height: 100%;
      overflow: auto;
    }

    .design-hub-layout {
      display: grid;
      grid-template-columns: minmax(248px, 280px) minmax(0, 1fr) minmax(320px, 380px);
      min-height: 100%;
      transform-origin: top left;
    }

    .dh-sidebar {
      height: 100%;
      overflow-y: auto;
      border-right: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      background: var(--tp-surface);
    }

    .dh-canvas {
      height: 100%;
      min-width: 0;
      overflow: hidden;
    }

    .dh-detail {
      height: 100%;
      overflow-y: auto;
      border-left: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      background: var(--tp-surface);
    }

    @media (max-width: 1200px) {
      .design-hub-layout {
        grid-template-columns: minmax(232px, 260px) minmax(0, 1fr) minmax(300px, 340px);
      }
    }

    @media (max-width: 920px) {
      .design-hub-layout {
        grid-template-columns: 1fr;
        grid-template-areas:
          'sidebar'
          'canvas'
          'detail';
        min-width: 0;
      }

      .dh-sidebar {
        grid-area: sidebar;
        border-right: none;
        border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      }

      .dh-canvas {
        grid-area: canvas;
        min-height: 420px;
        border-bottom: 1px solid color-mix(in srgb, var(--tp-border) 22%, transparent);
      }

      .dh-detail {
        grid-area: detail;
        border-left: none;
      }
    }
  `],
})
export class DesignHubWorkspacePage implements OnInit {
  readonly state = inject(DesignHubStateService);
  private readonly route = inject(ActivatedRoute);

  ngOnInit(): void {
    const initialScreenId = this.route.snapshot.queryParamMap.get('screen');
    if (initialScreenId) {
      this.state.selectedScreenId.set(initialScreenId);
      this.state.activeTab.set('detail');
    }

    this.state.loadAll();
  }
}
