# Planned Items Closeout Roadmap

**Status:** Active  
**Purpose:** Close the remaining documented `PLANNED` and `PARTIAL` items without reopening already-verified slices.

## Position

Design Hub is now a running, verified application rather than a foundation-only codebase.

**Current verified baseline:**

- backend: `489` passing tests
- frontend: `44 / 44` passing verification scenarios
- benchmark gate: `100.0` overall on the full 71-type slice
- CI lanes: verification + release workflows active
- runtime: Java 23, Node 25.8.1, Neo4j bootstrap via `docker compose`, backend Maven wrapper verified

**Overall completion:** approximately **85-88%**.

The remaining work is concentrated in five tracks:

1. runtime hardening
2. graph breadth and normalization
3. verification and CI hardening
4. external alignment depth
5. agent-ready automation depth

## Closure Rule

An item only moves from `PLANNED` or `PARTIAL` to closed when all three are true:

- implementation exists in code
- verification exists and passes
- documentation is updated to match the current repo state

## Track Status

| Track | Status | Completion | What Remains |
|-------|--------|------------|--------------|
| T0: Runtime hardening | Strong | ~98% | second-host fresh-machine rerun still outstanding; deterministic frontend Node `25.8.1` wrapper is in place via `frontend/npmw`, `scripts/verify-startup.sh` is locally verified from the repo root and from a clean isolated temp copy with no prior frontend install, and local Neo4j now runs with a smaller Docker memory footprint to avoid desktop OOM churn during verification |
| T1: Graph breadth and normalization | Strong | ~96% | wider implemented-node registry entry coverage, residual normalization cleanup, broader status/readiness alignment, and cleanup of remaining unknown-label/type warning noise that is no longer tied to deprecated Cypher |
| T2: Verification and CI hardening | Strong | ~92% | protected-branch enforcement |
| T3: External alignment depth | Partial | ~76% | live remote adapters, broader normalized external-field coverage |
| T4: Agent-ready automation depth | Partial | ~90% | broader implementation-pack resolution beyond the current six-story seeded slice |

## Closed In Current Passes

- top-level runtime instructions and pinned toolchains
- screen and story readiness diagnostics
- delivery, traceability, benchmark, verification, automation, journey/persona, channel, and architecture UI surfaces
- external sync orchestration, source status, parity audit, and verification-panel evidence
- story agent-pack export contract
- story agent-pack export v2 with application bootstrap context, execution policies, and richer component runtime metadata
- visual baselines, graph-to-UI drift checks, broader token audit, and desktop/mobile localization/RTL coverage beyond shell-only behavior
- CI verification workflow and release workflow
- benchmark integrity gate with persisted benchmark artifact output
- Neo4j live query rendering now uses Neo4j 5 `elementId(...)` instead of deprecated `id(...)`, and legacy unscoped `CALL { WITH ... }` subqueries have been rewritten to explicit scoped subqueries
- frontend initial hydration no longer blocks the screen list on the heaviest optional benchmark/external summary bundle; those summaries now load after the core graph data is visible
- benchmark improvements to `100.0` overall with all four dimensions green across the full 71-type benchmarkable slice
- generic graph object entry coverage expanded to 71 first-entry types, now also including journey-step, import-snapshot, evidence-record, enum, event, locale, and translation-key along with the previously closed topic, edge-case, exception-case, integration, open-question, governance, process, architecture, and verification families
- governance coverage is now live: `Decision`, `Assumption`, `Constraint`, `Assessment`, and `Risk` have seeded graph evidence, typed traversal endpoints, object registry entry coverage, and benchmark support
- upper-spine external normalization now reaches epic coverage, and external artifact traversal now shows that live mapping
- screen detail stories and roles now resolve directly from graph-backed `DELIVERS` and `ACCESSIBLE_BY_ROLE` edges; compatibility `storyRefs` and `roleKeys` remain as ID arrays only
- verification breadth now includes graph-backed RTL detail behavior, mobile detail/delivery behavior, Arabic desktop/mobile detail baselines, and a broader frontend token-governance audit
- external verification now checks the live parity audit, source-status cards, and persisted sync history across both Jira and Azure DevOps
- automation verification now covers all six seeded delivery stories, including five complete agent-pack paths and the intentionally incomplete auth gap path
- the startup proof now passes from a clean isolated temp copy on this machine, including a fresh `./npmw ci` frontend install path with no prior `node_modules`

## Remaining Execution Order

### 1. Runtime hardening

Close when:

- fresh-machine startup works from the documented runbook without local toolchain guesswork on a second host or clean VM

### 2. Graph breadth and normalization

Close when:

- benchmark breadth is now closed across the full 71-node taxonomy
- the generic object registry now covers the full 71 first-entry graph node types in the benchmarkable taxonomy
- compatibility-only projections are retired where graph-backed replacements already exist for the current screen detail slice; broader normalization still remains
- status/readiness semantics are normalized further across implemented entities

### 3. Verification and CI hardening

Close when:

- protected-branch policy is enforced outside the repo files themselves

### 4. External alignment depth

Close when:

- remote webhook and polling adapters are broader than the current config-backed orchestration layer
- normalized external-field coverage expands beyond the current story/bug/feature/task/finding/api/epic slice
- parity audit remains current as that coverage broadens

### 5. Agent-ready automation depth

Close when:

- broader story paths resolve to implementation packs
- agent-pack export moves beyond the current six-story seeded slice
- application/component execution metadata remains broad instead of only the current seeded automation flows

## Recommended Next Order

1. re-run the documented startup path in a clean environment
2. expand graph registry + benchmark breadth
3. deepen remote external adapters
4. expand agent-pack resolution
5. enforce protected-branch policy outside repo files

## Anti-Drift Rule

After each closure slice:

- rerun the benchmark or verification evidence
- update the status docs immediately
- remove or rewrite any stale claims that contradict the current repo state
