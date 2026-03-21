# Design Testing Strategy

**Status:** Draft  
**Purpose:** Define a strict design-testing and anti-drift strategy for Design Hub using Playwright as the primary end-to-end and visual verification harness.

## Why this exists

Design Hub is intended to become an implementation-readiness system, not just a catalog of screens and stories. That means the product needs a verification layer that detects:

- design drift between EMSIST source artifacts and rendered UI
- implementation drift between the graph model and the UI surface
- token drift between the EMSIST design system and Design Hub styling
- localization and RTL drift between English and Arabic renderings

Without this layer, documentation can improve while the shipped UI still diverges from the intended model.

## Current State

- The backend graph baseline now includes **74 `@Node` entities**, **111 SDN `@Relationship` declarations**, **1 Cypher-only polymorphic edge**, and **489 passing tests**.
- A Playwright harness now exists in `frontend/` with semantic, visual, drift, mobile, keyboard-accessibility, external-sync breadth, and localization/RTL coverage against the live backend, plus approved baselines for the shell and high-signal detail panels, including Arabic RTL shell and delivery-detail baselines for desktop/mobile.
- The frontend now exposes build, token-audit, visual, drift, and full verification scripts in `frontend/package.json`.
- `npm run check:design-tokens` now audits the Design Hub UI surface and the broader frontend source tree, with raw color literals allowed only in approved token-source files and required EMSIST root tokens enforced in `src/styles.scss`.

## Testing Position

Playwright should be treated as a **strict design-verification harness**, not only as a smoke-test tool.

Its role in Design Hub is to verify:

- page-level rendering
- graph-driven navigation behavior
- design-system token usage
- user-visible text and localization behavior
- responsive layout and RTL behavior
- visual stability against approved baselines

## Test Layers

### 1. Contract and route smoke

These tests verify the application boots and key routes render without runtime failure.

Examples:

- app shell renders
- sidebar, canvas, and detail panel render
- backend unavailable state is handled explicitly
- seeded data loads when backend is available

### 2. Semantic interaction tests

These tests verify user-visible behavior and graph navigation, not implementation details.

Examples:

- selecting a screen updates the detail panel
- selecting a persona reveals its journeys and steps
- selecting a bug or finding reveals linked story and screen context
- filters change the visible graph and list consistently
- selecting a business process reveals its flow nodes: activities, gateways, and events connected via `FLOWS_TO` sequence
- process flow traversal: `BusinessProcess → HAS_FLOW_NODE → ProcessActivity/ProcessGateway/ProcessEvent → FLOWS_TO → next node` renders correctly in the Process Flow View

### 3. Visual design baselines

These tests verify layout, spacing, token usage, and presentation against approved screenshots.

Scope:

- full page baselines
- stable sub-region baselines for sidebar, canvas, detail panel, and tab panels
- breakpoint-specific baselines
- LTR and RTL baselines

### 4. Design-system compliance tests

These tests verify the rendered UI is using canonical tokens rather than drifting into ad hoc values.

Examples:

- critical elements resolve `var(--tp-*)` token-backed colors
- typography, spacing, and focus styles match approved design-system expectations
- status badges and canvas accents resolve from tokenized theme values

### 5. Localization and RTL tests

These tests verify:

- shell-visible strings come from translation keys
- Arabic locale flips the document direction to RTL
- locale selection persists across reloads
- layout still works under RTL for the currently verified shell and graph-backed detail surfaces

### 6. Drift detection against the graph model

These tests verify the UI exposes the same semantic objects and relationships that the backend graph declares.

Examples:

- if a screen has linked stories, they are rendered in the detail panel
- if a touchpoint links to a channel, the UI can surface the channel dimension
- if an interaction has a confirmation dialog or error outcome, the UI renders or references it correctly

## Strict Playwright Rules

### Rule 1: Test user-visible behavior only

Tests should assert what the user can see and do, using stable locators and visible semantics.

Do not assert internal implementation details such as CSS class names or framework internals unless the test is explicitly a token-compliance check.

### Rule 2: Use stable selectors

Primary selectors should be:

- `getByRole(...)`
- `getByLabel(...)`
- `getByText(...)` for stable content
- `data-testid` only where user-facing selectors are not sufficient

### Rule 3: Separate visual tests from behavior tests

Behavior tests should fail on behavioral regressions. Visual tests should fail on visual drift. Do not mix the two unless the scenario truly requires both.

### Rule 4: Baselines are reviewed artifacts

Golden screenshots are not disposable snapshots. They are reviewable assets and must be approved when intentionally updated.

### Rule 5: Disable volatility in screenshots

Animations, transient carets, unstable overlays, and timestamp-like noise should be neutralized for screenshot capture.

### Rule 6: Design drift is a CI blocker

For Design Hub, failing design-verification tests should block merges in the same way broken functional tests do.

## Recommended Playwright Structure

```text
frontend/
  playwright.config.ts
  tests/
    smoke/
    graph/
    drift/
    process/
    visual/
    i18n/
    fixtures/
    styles/
```

Recommended test groups:

- `smoke/`: boot, routes, empty states, API-available states
- `graph/`: object selection, relation traversal, filter behavior
- `drift/`: backend-to-UI parity checks for graph-backed counts, relationships, and readiness surfaces
- `process/`: BusinessProcess flow traversal — activity/gateway/event sequence, FLOWS_TO edge integrity, process view rendering
- `visual/`: screenshot baselines and component-region baselines
- `i18n/`: English, Arabic, and RTL assertions
- `fixtures/`: stable seeded scenarios and API mocks when needed
- `styles/`: screenshot stabilization CSS

## Recommended Playwright Configuration

The baseline setup should include:

- explicit `testDir`
- `forbidOnly` on CI
- retries only on CI
- trace collection on first retry
- a `webServer` entry to start Angular automatically
- browser projects for at least Chromium
- dedicated visual-comparison options for screenshot assertions

## Screenshot Policy

Visual assertions should use Playwright screenshot comparisons on:

- the full page for major views
- clipped regions for high-signal components
- named snapshots per route, locale, and viewport

Baseline rules:

- baseline screenshots must be generated in a consistent environment
- snapshot updates require human review
- intentional design changes must update baselines in the same change set
- visual tolerances should be small and explicit

## Required Test Matrix

At minimum, Design Hub should run this matrix:

| Dimension | Required Coverage |
|---|---|
| Browser | Chromium |
| Viewport | Desktop and mobile |
| Locale | English and Arabic |
| Direction | LTR and RTL |
| Data state | backend available and backend unavailable |
| Theme compliance | tokenized theme assertions on critical UI |

## Critical Anti-Drift Scenarios

The following scenarios should be treated as mandatory:

1. Shell renders with sidebar, canvas, and detail panel.
2. Screen selection updates the detail panel consistently.
3. Screen detail renders linked roles and stories from graph-backed data.
4. Journey and touchpoint views render linked relationships without relying on hardcoded placeholders.
5. Empty and backend-unavailable states render the correct user-visible message.
6. English and Arabic shell surfaces both render without clipped or broken layout.
7. Key views match approved visual baselines.
8. Critical token-backed UI elements render the approved theme values.
9. Process Flow View renders BusinessProcess flow nodes (ProcessActivity, ProcessGateway, ProcessEvent) and their `FLOWS_TO` sequence edges faithfully from graph-backed data.
10. Implementation Pack resolution: a UserStory with DELIVERS edges resolves through deliverable→ApplicationComponent to yield frameworkFamily, modulePath, and effective testCommand. Dead-end deliverables (e.g., Message without HAS_MESSAGE→Screen→SUPPORTS_SCREEN) are flagged.
11. CodeAsset LOCATED_IN resolution: TestCase → LOCATED_IN → CodeAsset resolves to a valid file path (Application.repoPath + ApplicationComponent.modulePath + CodeAsset.filePath).
12. Import drift detection: ImportSnapshot.contentHash matches current Git doc content hash. Mismatch indicates doc-to-graph drift requiring re-import.
13. Convention governance: ApplicationComponent GOVERNED_BY_CONVENTION edges resolve to valid CodingConvention nodes with accessible docRef files.

## Drift Gates

Design drift should be measured on these gates:

- `UI graph drift`: UI fails to render required linked objects that exist in API responses
- `process flow drift`: Process Flow View fails to render the correct FLOWS_TO sequence or omits gateway/event nodes present in the graph
- `agent readiness drift`: Implementation Pack query returns empty owningComponents for a story that has DELIVERS edges, indicating missing SUPPORTS_SCREEN/EXPOSES/OWNS_DATA_ENTITY/ENFORCES_RULE edges or unpopulated execution metadata
- `visual drift`: screenshots diverge from approved baselines
- `token drift`: rendered values bypass canonical design tokens
- `text drift`: hardcoded UI strings appear outside translation files
- `RTL drift`: Arabic layout breaks or direction is not applied
- `code-asset drift`: CodeAsset filePath resolves to a non-existent file (Application.repoPath + ApplicationComponent.modulePath + CodeAsset.filePath)
- `import drift`: ImportSnapshot.contentHash mismatches current Git doc content, indicating stale graph data
- `convention drift`: CodingConvention.docRef points to a missing or moved Markdown file

## CI Policy

Recommended merge policy:

1. `build` must pass
2. semantic Playwright tests must pass
3. visual Playwright tests must pass
4. translation structure checks must pass
5. token-compliance checks must pass

If visual baselines are intentionally updated, the PR should explicitly note why the visual contract changed.

## Architecture Implication

Design testing adds a new verification concern to the platform:

- source artifacts define expected semantics
- the graph model defines expected relationships
- the frontend renders those relationships
- Playwright verifies the rendered behavior and presentation stay aligned

This makes Playwright part of the anti-drift architecture, not just QA tooling.

## Implementation Sequence

1. Add Playwright to the frontend workspace.
2. Create smoke and semantic tests first. **Status:** done for Layers 1-2.
3. Add visual baselines for the three-column shell and the highest-signal panels.
4. Add English and Arabic locale tests.
5. Add token-compliance and anti-drift checks.
6. Gate merges on the full suite.

## Source Notes

Playwright guidance that directly supports this strategy:

- visual comparison via `expect(page).toHaveScreenshot()`
- configuration support for `webServer`, `projects`, retries, and trace collection
- best-practice guidance to test user-visible behavior and keep tests isolated
