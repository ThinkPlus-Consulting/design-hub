import { Component, ChangeDetectionStrategy, inject, OnInit } from '@angular/core';
import { DesignHubStateService } from './services/design-hub-state.service';
import { ObjectDefinitionsExplorerComponent } from './components/object-definitions/object-definitions-explorer.component';

@Component({
  selector: 'app-design-hub-page',
  standalone: true,
  imports: [ObjectDefinitionsExplorerComponent],
  providers: [DesignHubStateService],
  changeDetection: ChangeDetectionStrategy.OnPush,
  template: `
    <app-object-definitions-explorer />
  `,
  styles: [`
    :host {
      display: block;
      height: 100vh;
      overflow: auto;
    }
  `],
})
export class DesignHubPage implements OnInit {
  readonly state = inject(DesignHubStateService);

  ngOnInit(): void {
    this.state.loadObjectDefinitions();
  }
}
