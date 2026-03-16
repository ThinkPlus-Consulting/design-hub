import { Component, ChangeDetectionStrategy, HostListener, inject, OnInit } from '@angular/core';
import { DesignHubStateService } from './services/design-hub-state.service';
import { ScreenSidebarComponent } from './components/screen-sidebar/screen-sidebar.component';
import { FlowCanvasComponent } from './components/flow-canvas/flow-canvas.component';
import { DetailPanelComponent } from './components/detail-panel/detail-panel.component';

@Component({
  selector: 'app-design-hub-page',
  standalone: true,
  imports: [ScreenSidebarComponent, FlowCanvasComponent, DetailPanelComponent],
  providers: [DesignHubStateService],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <div class="design-hub-viewport" data-testid="design-hub-root">
      <div class="design-hub-layout" [style.zoom]="state.pageZoom()">
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
      --dh-complete: var(--tp-primary, #428177);
      --dh-specified: var(--tp-warning, #988561);
      --dh-not-started: #b9a779;
      --dh-gap: var(--tp-danger, #6b1f2a);

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
      grid-template-columns: 280px 1fr 360px;
      min-height: 100%;
      min-width: 1040px;
      transform-origin: top left;
    }

    .dh-sidebar {
      height: 100%;
      overflow-y: auto;
      border-right: 1px solid rgba(152, 133, 97, 0.18);
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
      border-left: 1px solid rgba(152, 133, 97, 0.18);
      background: var(--tp-surface);
    }
  `],
})
export class DesignHubPage implements OnInit {
  readonly state = inject(DesignHubStateService);

  ngOnInit(): void {
    this.state.loadAll();
  }

  @HostListener('window:keydown', ['$event'])
  onWindowKeydown(event: KeyboardEvent): void {
    if (!(event.metaKey || event.ctrlKey) || this.isEditableTarget(event.target)) {
      return;
    }

    if (event.key === '+' || event.key === '=' || event.code === 'NumpadAdd') {
      event.preventDefault();
      this.state.setPageZoom(this.state.pageZoom() + 0.1);
      return;
    }

    if (event.key === '-' || event.code === 'NumpadSubtract') {
      event.preventDefault();
      this.state.setPageZoom(this.state.pageZoom() - 0.1);
      return;
    }

    if (event.key === '0' || event.code === 'Numpad0') {
      event.preventDefault();
      this.state.resetPageZoom();
    }
  }

  private isEditableTarget(target: EventTarget | null): boolean {
    if (!(target instanceof HTMLElement)) {
      return false;
    }

    return target instanceof HTMLInputElement
      || target instanceof HTMLTextAreaElement
      || target.isContentEditable;
  }
}
