import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: 'system-shell-graph',
    loadComponent: () =>
      import('./features/system-shell-graph/system-shell-graph.page').then((m) => m.SystemShellGraphPage),
  },
  {
    path: '',
    loadComponent: () =>
      import('./features/design-hub/design-hub.page').then((m) => m.DesignHubPage),
  },
  { path: '**', redirectTo: '' },
];
