# Framework-Agnostic Preview Plan

## Purpose

The current `system-shell-graph` preview is Angular-native. The preview canvas, screen switching, and X-Ray audit all assume Angular components rendered directly inside the app DOM.

This document defines the target architecture for making the preview framework-agnostic while preserving:

- Neo4j-driven structure
- tree selection and focus
- X-Ray parity checks
- hover and click inspection
- shell/screen/container/section/component mapping

## Current State

The current implementation is hard-wired to Angular:

- `system-shell-graph.page.ts` mounts `app-app-shell-preview`
- `app-shell-preview.component.html` switches on screen code and renders Angular preview components directly
- X-Ray audits the same Angular-rendered DOM subtree

That means React or Vue cannot be rendered automatically today. The system only knows how to render what the Angular preview components implement.

## Target Architecture

The preview system should be split into four layers:

1. Neo4j graph model
2. framework-neutral preview host
3. renderer adapters
4. X-Ray DOM contract auditor

### 1. Neo4j Graph Model

Neo4j remains the source of truth for:

- `Shell`
- `Container`
- `Screen`
- `Section`
- `Component`

Neo4j should not care whether the preview implementation is Angular, React, or Vue.

### 2. Framework-Neutral Preview Host

Replace the direct Angular preview mount with a generic host component.

Suggested responsibility:

- accept `shellCode`, `screenCode`, viewport info, selection state, and background config
- choose the correct renderer adapter
- mount and unmount the renderer
- expose a consistent DOM subtree for X-Ray

Suggested name:

- `preview-render-host`

### 3. Renderer Adapters

Introduce one adapter per framework:

- `AngularPreviewRendererAdapter`
- `ReactPreviewRendererAdapter`
- `VuePreviewRendererAdapter`
- optional `IframePreviewRendererAdapter`

Suggested interface:

```ts
export interface PreviewRendererAdapter {
  mount(container: HTMLElement, context: PreviewRenderContext): Promise<void> | void;
  unmount(): Promise<void> | void;
  updateSelection?(guid: string | null): void;
  getRootElement?(): HTMLElement | null;
}
```

Suggested render context:

```ts
export interface PreviewRenderContext {
  shellCode: string;
  screenCode: string;
  selectedGuid: string | null;
  backgroundConfig: ShellBackgroundConfig | null;
  viewportProfile: 'web' | 'tablet' | 'mobile';
  graph: SystemShellGraphResponse;
}
```

### 4. X-Ray DOM Contract Auditor

X-Ray must stop assuming Angular implementation details.

It should inspect only:

- the rendered DOM subtree
- graph bindings
- structural/tag parity
- accessibility
- component evidence

It should not treat framework host tags as Neo4j artifacts.

## Non-Negotiable DOM Contract

All frameworks must emit the same DOM bindings for mapped artifacts:

- `source-object-id`
- `guid`
- optional `data-inspect-id`

This contract is the bridge between:

- rendered DOM
- Neo4j nodes
- tree selection
- hover/click inspection
- X-Ray audits

If React or Vue renders without these attributes, visual preview may work, but the inspector and parity logic will fail.

## Rendering Strategy

Two options exist.

### Option A: Same-DOM Adapters

Render Angular, React, or Vue into the same document subtree.

Benefits:

- best option for X-Ray
- direct DOM inspection
- easiest hover/click mapping
- no cross-window bridge

Recommendation:

- use this as the default architecture

### Option B: Iframe Renderer

Render the preview inside an `iframe`.

Benefits:

- cleaner framework isolation
- easy to host external apps

Costs:

- X-Ray cannot inspect the preview the same way
- hover/click selection becomes message-based
- parity and artifact inventory require a bridge
- `source-object-id` and `guid` must be exported across frame boundaries

Recommendation:

- use only for visual preview mode
- do not use as the main inspection/X-Ray path

## Required Refactor in This Repo

### A. Replace Angular-Specific Mount

Current:

- `system-shell-graph.page.ts` mounts `<app-app-shell-preview ... />`

Target:

- `system-shell-graph.page.ts` mounts a renderer host

Example direction:

```html
<app-preview-render-host
  [shellCode]="activeShellCode()"
  [activeScreenCode]="activeScreenCode()"
  [selectedGuid]="selectedPreviewGuid()"
  [backgroundConfig]="activeShellBackgroundConfig()"
/>
```

### B. Remove Hardcoded Screen Switching from Template

Current:

- `app-shell-preview.component.html` uses Angular `@if` branches for each screen

Target:

- `screenCode -> renderer adapter + renderer entry`

Example registry shape:

```ts
type PreviewRendererKind = 'angular' | 'react' | 'vue';

interface PreviewScreenRegistration {
  screenCode: string;
  shellCode: string;
  renderer: PreviewRendererKind;
  entry: unknown;
}
```

### C. Introduce a Renderer Registry

Suggested responsibility:

- map `screenCode` to renderer strategy
- allow mixed-framework previews in the same project

Example:

```ts
SHL02.SCN02 -> angular -> TenantListAngularPreview
SHL02.SCN03 -> react -> TenantFactsheetReactPreview
SHL01.SCN02 -> vue -> MfaVuePreview
```

### D. Keep Shell Chrome Framework-Neutral

The shell host should not assume Angular-only screen internals.

Two valid approaches:

- shell is rendered by the same adapter as the screen
- shell remains framework-neutral host markup, and screen content is adapter-rendered inside `main`

Recommended for this repo:

- keep shell-level preview hosting in the platform
- allow the routed screen region to be adapter-rendered

That keeps `Header`, `Breadcrumb`, `Main`, and `Footer` stable.

## X-Ray Changes Required

X-Ray should be updated to follow these rules:

1. inspect rendered DOM, not framework type
2. ignore framework host tags unless they are modeled artifacts
3. validate only the DOM contract and graph contract
4. treat adapter root as an implementation boundary, not a graph node

Specific rules:

- custom tags like Angular/React/Vue host elements are not automatically parity violations
- parity issues should be based on missing graph bindings, not framework wrapper tags
- component checks should rely on expected rendered evidence, not Angular component names

## Migration Plan

### Phase 1

- create `PreviewRendererAdapter`
- create `preview-render-host`
- move current Angular preview into `AngularPreviewRendererAdapter`
- keep behavior unchanged

### Phase 2

- move screen registration into a renderer registry
- remove hardcoded `@if` screen switching from the preview shell template

### Phase 3

- update X-Ray to read from renderer host root only
- ensure all preview artifacts still emit `source-object-id` and `guid`

### Phase 4

- add first non-Angular renderer
- start with one screen, not the whole app

Recommended pilot:

- `SHL02.SCN02` or `SHL02.SCN03`

### Phase 5

- add framework selection/configuration in the graph or preview registry
- support mixed-framework screens if needed

## Recommendation

For this project, the best path is:

- same-DOM renderer adapters
- one framework-neutral preview host
- one DOM binding contract
- X-Ray as a framework-neutral auditor

Do not start with iframe-based preview if the goal includes:

- tree-driven selection
- X-Ray parity
- DOM artifact auditing
- hover/click inspection

## Immediate Next Step

Implement Phase 1 only:

- extract the current Angular preview into a renderer adapter
- replace direct preview mounting with a framework-neutral host

That gives a stable base without changing Neo4j semantics or existing preview behavior.
