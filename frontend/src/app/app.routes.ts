import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./features/landing/design-hub-landing.page').then((m) => m.DesignHubLandingPage),
  },
  {
    path: 'object-definitions',
    loadComponent: () =>
      import('./features/design-hub/design-hub.page').then((m) => m.DesignHubPage),
  },
  {
    path: 'system-shell',
    loadComponent: () =>
      import('./features/design-hub/design-hub-workspace.page').then((m) => m.DesignHubWorkspacePage),
  },
  {
    path: 'workspace',
    redirectTo: 'system-shell',
    pathMatch: 'full',
  },
  { path: '**', redirectTo: '' },
];
