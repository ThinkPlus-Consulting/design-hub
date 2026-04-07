import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/system-shell-graph/system-shell-graph.page').then((m) => m.SystemShellGraphPage),
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
