# Design Hub UI Parity Audit

Date: 2026-03-13

## Scope

This audit compares the current Angular Design Hub UI with the prototype playground UI.

Compared sources:

- Playground: `/Users/mksulty/Claude/Projects/Emsist-app/Documentation/prototypes/screen-flow-playground.html`
- Angular page shell: `/Users/mksulty/Claude/Projects/design-hub/frontend/src/app/features/design-hub/design-hub.page.ts`
- Angular sidebar: `/Users/mksulty/Claude/Projects/design-hub/frontend/src/app/features/design-hub/components/screen-sidebar/screen-sidebar.component.ts`
- Angular canvas: `/Users/mksulty/Claude/Projects/design-hub/frontend/src/app/features/design-hub/components/flow-canvas/flow-canvas.component.ts`
- Angular detail/tabs: `/Users/mksulty/Claude/Projects/design-hub/frontend/src/app/features/design-hub/components/detail-panel/detail-panel.component.ts`
- Angular detail panels under `/Users/mksulty/Claude/Projects/design-hub/frontend/src/app/features/design-hub/components/detail-panel/panels/`

## Summary

Overall parity status: partial.

The Angular app matches the playground at the high-level shell only:

- 3-column layout
- left filters/sidebar
- center flow canvas
- right multi-tab detail area

The main gaps are in:

- canvas instrumentation and analytics
- sidebar workflow affordances
- selected-screen context actions
- notes/annotation workflows
- prompt/export tooling
- statistics breadth
- screen-row metadata density

Several missing UI features are also blocked by current API/data-contract drift, but a meaningful UI parity pass can still be planned independently.

## UI Comparison Matrix

### 1. Overall Layout

| Area | Playground | Angular | Status |
|---|---|---|---|
| 3-column shell | yes | yes | matched |
| Left panel width/behavior | fixed controls + scrollable list | fixed sidebar + scroll | matched |
| Center canvas | svg canvas with overlays | svg canvas with toolbar | partial |
| Right panel | tabs + dynamic panel content | PrimeNG tabs + panels | matched |

Notes:

- Angular shell in `design-hub.page.ts` is structurally correct.
- Playground has more overlay utilities in the center column than Angular currently exposes.

### 2. Left Sidebar

| Feature | Playground | Angular | Status |
|---|---|---|---|
| module filters | yes | yes | matched |
| status filters | yes | yes | matched |
| search | yes | yes | matched |
| display toggles | yes | yes | matched |
| screen count | yes | yes | matched |
| gap badge per screen row | yes | no | missing |
| annotation summary count | yes | no | missing |
| export all notes button | yes | no | missing |
| feature-colored screen list emphasis | stronger | weaker | partial |

Impact:

- Angular sidebar is functionally smaller and less useful for design review.
- Screen-list rows currently hide valuable density that exists in the playground.

### 3. Canvas

| Feature | Playground | Angular | Status |
|---|---|---|---|
| node rendering | yes | yes | matched |
| edge rendering | yes | yes | matched |
| pan by drag | yes | yes | matched |
| zoom buttons | yes | yes | matched |
| fit/reset view | fit button | reset button | partial |
| module legend | yes | no | missing |
| stats bar overlay | yes | no | missing |
| gap summary overlay | yes | no | missing |
| feature-based node coloring | yes | no, status-based only | drift |
| selected edge highlighting | richer | simpler | partial |
| wheel-to-zoom | yes | yes, now removed | intentionally changed |

Decision applied:

- Mouse wheel zoom has been disabled in Angular because it is annoying during review. Zoom remains available via explicit toolbar controls.

### 4. Right Panel Shell

| Feature | Playground | Angular | Status |
|---|---|---|---|
| selected context bar | richer | minimal | partial |
| link/jump from context bar | yes | no | missing |
| close selection | implicit via interactions | yes | acceptable |
| tab set | detail, inventory, touchpoints, interactions, journeys, cross-cutting | detail, inventory, touch, actions, journeys, x-cut | matched |

Notes:

- Angular tab labels are fine.
- The current context bar is much thinner than the playground and loses useful navigation affordances.

### 5. Detail Panel Content

| Feature | Playground | Angular | Status |
|---|---|---|---|
| properties block | yes | yes | matched |
| stories | yes | yes | matched |
| gaps | yes | yes | partial |
| content inventory | yes | yes | partial |
| transitions | yes | yes | partial |
| error codes | yes | yes in UI, but data missing | partial |
| confirmations/dialogs | yes | yes in UI, but data missing | partial |
| source refs | yes | yes in UI, but data missing | partial |
| notes viewer | yes | yes | matched |
| notes editing | yes | no | missing |
| save indicator | yes | no | missing |
| prompt output block | yes | no | missing |
| copy action | yes | no | missing |

Blocking issue:

- Angular detail sections for error codes, confirmations, source refs, and gap detail exist, but the adapter/API are still starving those sections of real data.

### 6. Inventory Tab

| Feature | Playground | Angular | Status |
|---|---|---|---|
| tabular inventory | yes | yes | matched |
| row selection | yes | yes | matched |
| richer context behavior | yes | weaker | partial |
| full metadata coverage | yes | reduced | partial |

### 7. Touchpoints Tab

| Feature | Playground | Angular | Status |
|---|---|---|---|
| touchpoint list | yes | yes | matched |
| grouped presentation | yes | yes | matched |
| journey step references | yes | UI supports, data empty | partial |
| source refs | yes | no rendering | missing |

### 8. Interactions Tab

| Feature | Playground | Angular | Status |
|---|---|---|---|
| interaction list | yes | yes | matched |
| effects | yes | yes | matched |
| API calls | yes | yes | matched |
| permission | yes | yes | partial |
| confirmation code | yes | yes | partial |
| outcomes | yes | UI supports, data mostly empty | partial |
| source refs | yes | no rendering | missing |
| journey step refs | yes | no rendering | missing |

### 9. Journeys Tab

| Feature | Playground | Angular | Status |
|---|---|---|---|
| journey list | yes | yes | matched |
| expandable steps | yes | yes | matched |
| persona/role metadata | yes | yes | matched |
| source refs | yes | yes | matched |
| richer context linking | yes | no | missing |

### 10. Cross-Cutting Tab

| Feature | Playground | Angular | Status |
|---|---|---|---|
| matrix view | yes | yes | matched |
| selected-screen focused view | yes | yes | matched |
| message registry count | yes | yes | partial because data is weak |

## Highest-Value UI Gaps

These are the most valuable UI gaps to close after data parity stabilizes:

1. Canvas overlays
   - legend
   - stats bar
   - gap summary

2. Sidebar review workflow
   - annotation count
   - note export
   - gap badge per screen row

3. Detail workflow affordances
   - notes editing and save feedback
   - selected-context jump action
   - prompt output and copy action if still part of the review workflow

4. Metadata density
   - source refs
   - error codes
   - confirmations
   - gap detail text

## Recommended UI Implementation Order

### Phase 1. Interaction cleanup

- keep wheel zoom disabled
- preserve zoom buttons
- keep drag-pan behavior

### Phase 2. Canvas parity

- add legend
- add stats bar
- add gap summary overlay

### Phase 3. Sidebar parity

- add annotation summary
- add export notes action
- add gap badges to screen rows

### Phase 4. Detail workflow parity

- add notes editor
- add save indicator
- add selected-context action
- add prompt output block if still required

### Phase 5. Data-driven parity

- turn on sections that are already implemented but currently underfed by the API:
  - gap descriptions
  - content details
  - error codes
  - confirmations
  - source refs
  - outcomes
  - journey step refs

## Immediate Observations for Review

- The Angular shell is not the main problem anymore.
- The largest visible UI drift is in the center canvas overlays and the review workflow tooling around notes and prompts.
- The largest content drift is still data-contract related, not presentation related.
- `SCR-02-MAT` remains a good validation specimen because the playground expects rich gap and content detail there, while the Angular path still under-delivers it.
