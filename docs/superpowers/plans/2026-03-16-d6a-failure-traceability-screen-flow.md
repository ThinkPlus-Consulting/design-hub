# D6a Failure, Traceability, and Screen-Flow Closure Plan

> **Status: CLOSED** — Chunks 1-3 are implemented and verified.

**Goal:** Close the remaining high-signal query gaps by adding failure-path registries/edges, external traceability wiring, and first-class screen-flow semantics.

**Implementation Baseline (pre-plan):** `61 @Node / 78 SDN @Relationship / 1 Cypher-only edge / 307 tests`

**Current Baseline (after D6a):** `65 @Node / 90 SDN @Relationship / 1 Cypher-only edge / 340 tests / 0 failures`

**Planned D6a Scope**
- **Chunk 1 — Failure path**
  - `ErrorCode` (T2)
  - embedded `InteractionOutcome` fields on `Interaction`
  - `Interaction -[ON_ERROR_SHOWS]-> ErrorCode`
  - `Screen -[CAN_PRODUCE_ERROR]-> ErrorCode`
- **Chunk 2 — Traceability**
  - `ExternalArtifact` (T1)
  - `ExternalArtifact -[REPRESENTS_STORY]-> UserStory`
  - `ExternalArtifact -[REPRESENTS_BUG]-> Bug`
  - `Bug -[AFFECTS_SCREEN]-> Screen`
  - `HAS_SOURCE` wiring to `SourceReference`
- **Chunk 3 — Screen flow**
  - `ScreenState` (T1)
  - `Transition` (T1)
  - `Transition -[FROM_SCREEN]-> Screen`
  - `Transition -[TO_SCREEN]-> Screen`
  - `Transition -[CAUSED_BY_INTERACTION]-> Interaction`
  - `ScreenState -[BELONGS_TO_SCREEN]-> Screen`

**Hard rules**
1. `InteractionOutcome` remains embedded/T3 in this increment, not a separate `@Node`.
2. Existing compatibility fields stay in place when new edges are added.
3. Runtime paths that already expose `Interaction` must hydrate the new error edges.
4. Existing seeded databases must be patched via `RegistryGraphMigrationService`, not only fresh reseeds.

---

## Chunk 1 — Failure Path

- [x] Add `ErrorCode.java` and `ErrorCodeTest.java`
- [x] Extend `Interaction.java` with embedded outcome fields and `ON_ERROR_SHOWS`
- [x] Extend `Screen.java` with `CAN_PRODUCE_ERROR`
- [x] Update `InteractionRepository` query contract to hydrate `ON_ERROR_SHOWS`
- [x] Patch existing and fresh seeded interactions via `RegistryGraphMigrationService.patchInteractionOutcomes()` and `runFullMigration()`
- [x] Extend `RegistryGraphMigrationService` with:
  - [x] `seedErrorCodes()`
  - [x] `patchInteractionOutcomes()`
  - [x] `backfillOnErrorShowsEdges()`
  - [x] `backfillCanProduceErrorEdges()`
- [x] Add/extend tests:
  - [x] `InteractionTest`
  - [x] `ScreenTest`
  - [x] `InteractionRepositoryQueryTest`
  - [x] `RegistryGraphMigrationServiceTest`
- [x] Run focused and full Java 23 test suite

## Chunk 2 — Traceability

- [x] Add `ExternalArtifact.java` and builder tests
- [x] Wire `REPRESENTS_STORY`, `REPRESENTS_BUG`, `AFFECTS_SCREEN`, and `HAS_SOURCE`
- [x] Add seed and migration coverage

## Chunk 3 — Screen Flow

- [x] Add `ScreenState.java`, `Transition.java`, and builder tests
- [x] Wire `BELONGS_TO_SCREEN`, `FROM_SCREEN`, `TO_SCREEN`, `CAUSED_BY_INTERACTION`
- [x] Add seed and migration coverage
