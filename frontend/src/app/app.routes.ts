import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/design-hub/design-hub.page').then((m) => m.DesignHubPage),
  },
  {
    path: 'system-shell',
    redirectTo: '',
    pathMatch: 'full',
  },
  {
    path: 'system-shell-graph',
    redirectTo: '',
    pathMatch: 'full',
  },
  { path: '**', redirectTo: '' },
];
