# Playground to Angular Hub Drift Plan

Date: 2026-03-13

## Purpose

This document turns the current mismatch between the prototype playground and the Angular design-hub into a concrete validation and implementation backlog.

Source of truth candidates reviewed:

- Playground data and UI behavior: `<Emsist-app-root>/Documentation/prototypes/screen-flow-playground.html`
- Remediation spec: `<Emsist-app-root>/docs/superpowers/specs/2026-03-13-screen-flow-playground-remediation.md`
- Angular hub models and adapter: `<repo-root>/frontend/src/app/models/*.ts`
- Angular hub API adapter and state: `<repo-root>/frontend/src/app/features/design-hub/services/*.ts`
- Backend graph model and seed: `<repo-root>/backend/src/main/java/com/emsist/designhub/**`

## Validation Method

Validate each layer against the playground schema in this order:

1. Prototype data model
2. Backend graph schema
3. API response contract
4. Angular adapter
5. Angular UI rendering

Use a drift matrix with these columns:

| Entity | Playground Expected | Backend Stored | API Returned | Angular Mapped | UI Rendered | Status |
|---|---|---|---|---|---|---|
| `Screen` | full screen schema | partial | partial | partial with placeholders | partial | drift |
| `Touchpoint` | includes `journeyStepRefs`, `sourceRefs` | partial | partial | placeholders | partial | drift |
| `Interaction` | includes `outcomes`, `journeyStepRefs`, `sourceRefs` | partial | partial | placeholders | partial | drift |
| `Journey` | includes `sourceRefs`, ordered steps | partial | mostly present | mostly present | partial | drift |
| Stats | screens, gaps, errors, dialogs, interactions, touchpoints, journeys | partial | partial | partial | partial | drift |

## Confirmed Drift

### 1. Backend schema is narrower than the playground schema

The playground screen records include `uxSpecRef`, `gapRefs`, `sourceRefs`, `_legacy.errorCodes`, `_legacy.confirmations`, and `_legacy.emptyState`.

The backend `Screen` node currently stores only:

- base identifiers and statuses
- cross-cutting fields
- `notes`
- `storyRefs`, `roleKeys`, `personaIds`
- relationships to `gaps`, `contentElements`, `transitionsTo`

Evidence:

- Playground schema and records carry the extra fields near the screen definitions in `screen-flow-playground.html`.
- Backend screen model does not include those fields in `<repo-root>/backend/src/main/java/com/emsist/designhub/domain/Screen.java`.

Impact:

- Even with perfect API mapping, Angular cannot show those fields because the graph does not currently store them.

### 2. The backend seed data itself is incomplete relative to the playground

Example: `SCR-02-MAT` in the playground carries:

- 4 gaps with descriptions
- 6 content elements
- full `uxSpecRef`
- `sourceRefs`
- `errorCodes`
- transitions and other legacy details

The backend seed for `SCR-02-MAT` currently creates:

- 4 gaps with `description(null)`
- 6 content elements
- no `uxSpecRef`
- no `sourceRefs`
- no `errorCodes`
- no `confirmations`
- no `emptyState`

Evidence:

- Seed definition in `<repo-root>/backend/src/main/java/com/emsist/designhub/config/DataInitializer.java`
- Playground record in `<Emsist-app-root>/Documentation/prototypes/screen-flow-playground.html`

Impact:

- The Angular hub cannot recover gap detail text because the source data loaded into Neo4j already dropped it.

### 3. Single-screen API hydration is still not trustworthy

Concrete example:

- `GET /api/v1/design-hub/screens/SCR-02-MAT` currently returns empty `gaps`, `contentElements`, and `transitionsTo`
- The seed data for the same screen includes gaps and content elements

Impact:

- The detail panel cannot be treated as authoritative until graph hydration is validated screen-by-screen.

### 4. Angular adapter currently hardcodes missing fields away

Current adapter behavior in `<repo-root>/frontend/src/app/features/design-hub/services/design-hub-api.service.ts`:

- `uxSpecRef: ''`
- `gapRefs: []`
- `sourceRefs: []`
- `_legacy.errorCodes: []`
- `_legacy.confirmations: []`
- `_legacy.emptyState: false`
- `touchpoint.journeyStepRefs: []`
- `touchpoint.sourceRefs: []`
- `interaction.outcomes` forced to null values
- `interaction.journeyStepRefs: []`
- `interaction.sourceRefs: []`

Impact:

- Even when data exists later, the current mapping will still hide it unless the adapter is replaced with full contract mapping.

### 5. Statistics are not aligned with the playground

Playground stats currently cover:

- screens
- gaps
- error codes
- confirmation dialogs
- interactions
- touchpoints
- journeys

Angular stats currently cover only:

- total screens
- complete
- specified
- not started
- total gaps derived locally
- coverage percent

Impact:

- The Angular sidebar under-reports the design surface and loses operational validation metrics used in the playground.

### 6. Several playground features have no Angular equivalent yet

Missing or partial features compared with the playground:

- notes editing and save state indicator
- notes export
- prompt output panel
- copy prompt action
- selected-context link behavior in non-detail tabs
- gap summary overlay
- richer detail sections for error codes and confirmation dialogs

Impact:

- The Angular hub is currently a subset viewer, not a parity implementation.

### 7. Data ownership is split across incompatible patterns

The remediation spec defines a schema-first model:

- `SCREENS`
- `TOUCHPOINTS`
- `INTERACTIONS`
- `JOURNEYS`

and expects transitions and journey backfill to be derived from interactions and journeys.

The current backend still treats screens as the primary payload and stores transitions directly on screens.

Impact:

- Drift will continue unless one canonical contract is chosen and enforced at every layer.

## Example: `SCR-02-MAT`

Observed drift for `SCR-02-MAT`:

| Field | Playground | Backend Seed | API | Angular UI |
|---|---|---|---|---|
| `routePath` | present | present | present | shown |
| `uxSpecRef` | present | missing | missing | blank |
| `sourceRefs` | present | missing | missing | hidden |
| gaps count | 4 | 4 | 0 on single-screen endpoint | not shown correctly |
| gap descriptions | detailed | null | none | missing |
| content elements | 6 | 6 | 0 on single-screen endpoint | missing |
| error codes | present | missing | missing | missing |
| transitions | present | relationship exists in source model | empty in endpoint | missing |

This screen should be used as the first end-to-end validation specimen.

## Recommended Workstreams

### WS1. Freeze the canonical contract

Goal:

- Decide that the remediation spec schema is the contract to implement

Deliverables:

- typed DTOs for `Screen`, `Touchpoint`, `Interaction`, `Journey`, `Stats`
- explicit field list per entity
- one mapping document from playground field names to backend/API field names

### WS2. Restore source parity in the backend seed

Goal:

- Bring `DataInitializer` up to playground parity before touching more UI

Tasks:

- add `uxSpecRef`, `gapRefs`, `sourceRefs` to graph model
- store gap descriptions, not only severity/type
- store `errorCodes`, `confirmations`, `emptyState`
- add `journeyStepRefs` and `sourceRefs` to touchpoints and interactions
- add interaction outcomes

Exit criteria:

- `SCR-02-MAT` in Neo4j matches the playground record materially, not approximately

### WS3. Fix API contract and hydration

Goal:

- API responses must be stable DTOs, not raw graph entities

Tasks:

- replace raw entity responses for all endpoints with DTOs
- validate `/screens/{id}` returns full graph data
- add a contract test for `SCR-02-MAT`
- add stats DTO that includes playground metrics

Exit criteria:

- API JSON parses cleanly and returns expected fields for representative screens

### WS4. Remove placeholder mapping in Angular

Goal:

- Stop inventing defaults where the API should provide real data

Tasks:

- replace hardcoded empty values in `design-hub-api.service.ts`
- map real `uxSpecRef`, `sourceRefs`, `gapRefs`
- map `errorCodes`, `confirmations`, `emptyState`
- map `journeyStepRefs`, `sourceRefs`, `outcomes`
- keep fallback values only for truly optional fields

Exit criteria:

- no key field is set to a synthetic empty value unless the API field is genuinely absent

### WS5. Implement UI parity incrementally

Goal:

- Bring Angular behavior up to the level needed for design validation

Suggested order:

1. Detail panel data parity
2. Statistics parity
3. Notes editor and persistence
4. Error code and confirmation dialog sections
5. Selected-context navigation affordances
6. Prompt output and copy action, if still required by workflow

### WS6. Add automated drift validation

Goal:

- Catch future drift automatically

Tasks:

- extract canonical records from `screen-flow-playground.html`
- fetch live API JSON from Angular hub backend
- compare field coverage and counts
- emit a markdown or JSON report

Suggested output sections:

- missing fields by entity
- mismatched counts by entity
- screens failing full-detail parity
- fields present in prototype but absent in backend schema

## Implementation Sequence

Use this order to reduce rework:

1. Canonical contract freeze
2. Backend seed parity
3. API DTO parity
4. Angular adapter parity
5. Angular UI parity
6. Automated drift report

Do not start with UI-only fixes. The current drift is mostly data-contract drift.

## Immediate Next Actions

1. Create a field-by-field parity checklist for `Screen`, `Touchpoint`, `Interaction`, `Journey`, and `Stats`.
2. Fix `SCR-02-MAT` end to end as the reference slice.
3. Add a backend contract test that proves `SCR-02-MAT` returns:
   - 4 gaps with descriptions
   - 6 content elements
   - `uxSpecRef`
   - `sourceRefs`
   - transitions
4. Replace Angular placeholder mappings only after the backend contract is complete.
5. Add a generated drift report to the repo so future comparisons are mechanical.

## Definition of Done

The Angular hub reaches acceptable parity when:

- a representative sample of screens from R01, R04, R05, and R06 matches the playground at the API and UI layers
- statistics align with the playground metrics
- no important field is silently dropped between seed, API, and UI
- drift can be re-checked with an automated report
