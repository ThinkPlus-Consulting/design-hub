# PrimeNG Inventory

This file is the app-owned inventory for PrimeNG usage in the frontend.

It answers:
- where PrimeNG package files are physically located
- where PrimeIcons files are physically located
- where the app configures PrimeNG
- which PrimeNG modules are actually imported by app source
- how the Design Hub registry stores PrimeNG metadata
- whether any other third-party UI component library is imported by `src/app`

## 1. Physical package locations

PrimeNG package code:
- `frontend/node_modules/primeng`
- compiled module files used by the build are under:
  - `frontend/node_modules/primeng/fesm2022`

PrimeIcons package:
- `frontend/node_modules/primeicons`
- icon stylesheet:
  - `frontend/node_modules/primeicons/primeicons.css`
- icon font files:
  - `frontend/node_modules/primeicons/fonts/primeicons.eot`
  - `frontend/node_modules/primeicons/fonts/primeicons.svg`
  - `frontend/node_modules/primeicons/fonts/primeicons.ttf`
  - `frontend/node_modules/primeicons/fonts/primeicons.woff`
  - `frontend/node_modules/primeicons/fonts/primeicons.woff2`

PrimeUIX theme package:
- `frontend/node_modules/@primeuix/themes`
- Aura preset source used by this app:
  - `frontend/node_modules/@primeuix/themes/dist/aura/index.mjs`

## 2. App-owned PrimeNG wiring

Global PrimeIcons import:
- `frontend/src/styles.scss`

PrimeNG runtime configuration:
- `frontend/src/app/app.config.ts`

App theme preset override:
- `frontend/src/app/core/theme/default-preset.ts`

Registry metadata source:
- `backend/src/main/resources/system-shell-graph-component-registry.json`

Current exact registry contract:
- package-based metadata, not `node_modules/fesm2022` file paths
- `packageName`
- `packageExport`
- `packageVersion`
- `iconPackage`
- `themePackage`

Current exact registry coverage:
- 99 PrimeNG UI module definitions are registered
- registry points to package exports like `primeng/breadcrumb`
- registry does not use `implementationSourcePath`

## 3. PrimeNG modules imported by app source

These are the PrimeNG modules currently imported from `src/app`.

- `primeng/api`
- `primeng/avatar`
- `primeng/badge`
- `primeng/breadcrumb`
- `primeng/button`
- `primeng/config`
- `primeng/chip`
- `primeng/divider`
- `primeng/image`
- `primeng/inplace`
- `primeng/inputotp`
- `primeng/inputtext`
- `primeng/message`
- `primeng/paginator`
- `primeng/select`
- `primeng/table`
- `primeng/tabs`
- `primeng/tag`
- `primeng/togglebutton`
- `primeng/tree`

## 4. PrimeNG import locations in app source

PrimeNG config:
- `frontend/src/app/app.config.ts`

Feature page:
- `frontend/src/app/features/design-hub/design-hub.page.ts`

Shell:
- `frontend/src/app/features/design-hub/components/shell/breadcrumb/design-hub-breadcrumb.component.ts`
- `frontend/src/app/features/design-hub/components/shell/main/inspector-pane/overview-tab/design-hub-inspector-overview-tab.component.ts`
- `frontend/src/app/features/design-hub/components/shell/main/inspector-pane/issues-tab/design-hub-inspector-issues-tab.component.ts`

Preview canvas:
- `frontend/src/app/features/design-hub/components/shell/main/inspector-pane/preview-tab/preview-canvas-renderer/application-shell/application-shell-preview.component.ts`
- `frontend/src/app/features/design-hub/components/shell/main/inspector-pane/preview-tab/preview-canvas-renderer/application-shell/tenant-list-screen-preview/tenant-list-screen-preview.component.ts`
- `frontend/src/app/features/design-hub/components/shell/main/inspector-pane/preview-tab/preview-canvas-renderer/application-shell/tenant-factsheet-screen-preview/tenant-factsheet-screen-preview.component.ts`
- `frontend/src/app/features/design-hub/components/shell/main/inspector-pane/preview-tab/preview-canvas-renderer/login-shell/login-screen-preview/login-screen-preview.component.ts`
- `frontend/src/app/features/design-hub/components/shell/main/inspector-pane/preview-tab/preview-canvas-renderer/login-shell/mfa-screen-preview/mfa-screen-preview.component.ts`

State:
- `frontend/src/app/features/design-hub/services/design-hub-state.service.ts`

## 5. PrimeIcons actually referenced in app source

PrimeIcons CSS is globally loaded from `primeicons.css`.

Direct `pi pi-*` icon references currently present in app source:
- `pi pi-android`
- `pi pi-arrow-left`
- `pi pi-book`
- `pi pi-chart-pie`
- `pi pi-check`
- `pi pi-check-circle`
- `pi pi-cloud-upload`
- `pi pi-database`
- `pi pi-exclamation-triangle`
- `pi pi-external-link`
- `pi pi-eye`
- `pi pi-filter`
- `pi pi-filter-slash`
- `pi pi-heart`
- `pi pi-heart-fill`
- `pi pi-history`
- `pi pi-home`
- `pi pi-id-card`
- `pi pi-inbox`
- `pi pi-link`
- `pi pi-list`
- `pi pi-microsoft`
- `pi pi-minus`
- `pi pi-objects-column`
- `pi pi-palette`
- `pi pi-pause`
- `pi pi-pencil`
- `pi pi-play`
- `pi pi-plus`
- `pi pi-search`
- `pi pi-server`
- `pi pi-share-alt`
- `pi pi-shield`
- `pi pi-sitemap`
- `pi pi-times`
- `pi pi-times-circle`
- `pi pi-user-plus`
- `pi pi-users`

Note:
- these icon references come from component templates and graph-backed screen configuration JSON
- local sample-model files for tenant list and tenant factsheet were removed

## 6. Other third-party UI component libraries used by app source

Current exact state:
- no other third-party UI component library is imported by `src/app`
- PrimeNG is the only third-party UI component library imported by app source

Important distinction:
- Angular framework packages are used, but they are not a third-party UI component library in this inventory
- `@primeuix/themes` is a PrimeNG theme package, not a separate component library
- `primeicons` is the icon package for PrimeNG

## 7. Not done

This file is an explicit inventory only.

It does not:
- guarantee PrimeNG-only rendering patterns across the entire app
- guarantee every preview icon string is semantically validated beyond source inventory

## 8. Still wrong

PrimeNG remains a package dependency under `frontend/node_modules`.

That is correct.

What is not acceptable is referencing internal compiled vendor paths in the registry contract.

That part was fixed by migrating the registry to package/export metadata.
