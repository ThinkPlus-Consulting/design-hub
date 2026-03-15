# Interaction Edge Completion and Remaining String Migration Implementation Plan

> **For agentic workers:** Use subagents if available; otherwise execute sequentially. Steps use checkbox syntax for tracking.

**Goal:** Close the remaining high-value string-to-edge gaps on interaction-oriented objects by adding `ConfirmationDialog`, wiring `CALLS_API` and `TRIGGERS_CONFIRMATION`, migrating `Interaction.personaIds`, `Interaction.roleKeys`, and `Touchpoint.roleKeys` to canonical edges, and retiring `Journey.roleKey` as legacy compatibility data.

**Architecture:** This is an implementation-only increment against an already-published taxonomy. The work is split into three layers: (1) missing registry/entity surface (`ConfirmationDialog`), (2) edge declarations and repository loading on `Interaction` / `Touchpoint`, and (3) seed + migration wiring that upgrades persisted string values into canonical graph relationships without breaking current API consumers.

**Tech Stack:** Java 23 for verification, Spring Boot 3.4.1, Spring Data Neo4j, Lombok, JUnit 5, Mockito, Neo4jClient

**Implementation Baseline (pre-plan):** `35 @Node / 51 SDN @Relationship / 1 Cypher-only edge / 182 tests`

**Final Implementation State (verified):** `36 @Node / 57 SDN @Relationship / 1 Cypher-only edge / 216 tests`

> **Plan Status: CLOSED** — All 9 tasks complete. Verified with commit `4d55b2c` (2026-03-15). The actual SDN @Relationship count is 57 (one more than the original target of 56), reflecting the correct edge inventory post-implementation.

**Design-Model Note:** This plan does **not** change the approved taxonomy counts (`75 nodes / 106 edge types / 71 benchmarkable`). It closes implementation gaps against already-published node and edge types.

---

## Frozen Decisions

### In Scope

**New node**
- `ConfirmationDialog` (T2)

**New or completed edge set**
- `TRIGGERS_CONFIRMATION` (`Interaction -> ConfirmationDialog`)
- `CALLS_API` (`Interaction -> ApiContract`)
- `USED_BY_PERSONA` (`Interaction -> Persona`)
- `ACCESSIBLE_BY_ROLE` (`Interaction -> BusinessRole`)
- `ACCESSIBLE_BY_ROLE` (`Touchpoint -> BusinessRole`)

**Legacy-field migration scope**
- `Interaction.confirmationCode`
- `Interaction.apiCalls`
- `Interaction.personaIds`
- `Interaction.roleKeys`
- `Touchpoint.roleKeys`
- `Journey.roleKey` (cleanup/deprecation only; no new canonical role edge)

### Out of Scope

- `ErrorCode` registry
- `ON_ERROR_SHOWS`
- `Transition` as a first-class node
- frontend removal of legacy fields from TypeScript models
- taxonomy/count changes beyond the implementation baseline

### Hard Rules

1. `ApiContract` already exists as a T1 node. This plan must **reuse** it rather than reshape it.
2. `ConfirmationDialog` is the only new node in this increment.
3. `Journey.roleKey` is **not** replaced by a new direct journey-role edge in this increment. Journey actor semantics stay on `PERFORMED_BY_PERSONA`; access semantics stay on screen/touchpoint/interaction role edges.
4. Legacy string fields may remain temporarily for compatibility, but canonical traversal/query logic must use the new edges.
5. Runtime verification must include the live `/interactions`, `/touchpoints`, and `/journeys` endpoints, not just source-level tests.

### Implementation Assumptions

1. `ConfirmationDialog.dialogId` will initially mirror the currently seeded Design Hub confirmation codes (`CONFIRM-AGT-DELETE`, `CONFIRM-AGT-PUBLISH`) instead of forcing a speculative remap to a broader enterprise code system.
2. `ApiContract` nodes will be created/upserted from existing `apiCalls` strings by parsing `METHOD path` and generating deterministic contract IDs as `API-{METHOD}-{SANITIZED_PATH}`, where `SANITIZED_PATH` is the request path uppercased with non-alphanumeric runs collapsed to `-` and leading/trailing `-` trimmed. Example: `GET /api/v1/agents/{id}` -> `API-GET-API-V1-AGENTS-ID`.
3. Existing frontend compatibility is preserved by keeping legacy string fields during this increment.

---

## File Structure

### New Files

| File | Responsibility |
|------|----------------|
| `backend/src/main/java/com/emsist/designhub/domain/ConfirmationDialog.java` | T2 confirmation-dialog registry node |
| `backend/src/main/java/com/emsist/designhub/repository/ConfirmationDialogRepository.java` | ConfirmationDialog repository |
| `backend/src/main/java/com/emsist/designhub/repository/ApiContractRepository.java` | ApiContract repository for deterministic lookup if needed |
| `backend/src/test/java/com/emsist/designhub/domain/ConfirmationDialogTest.java` | ConfirmationDialog model tests |
| `backend/src/test/java/com/emsist/designhub/domain/InteractionEdgeTraversalTest.java` | Source-level traversal/model tests for the new interaction/touchpoint edges |

### Modified Files

| File | Change |
|------|--------|
| `backend/src/main/java/com/emsist/designhub/domain/Interaction.java` | add `USED_BY_PERSONA`, `ACCESSIBLE_BY_ROLE`, `CALLS_API`, `TRIGGERS_CONFIRMATION` |
| `backend/src/main/java/com/emsist/designhub/domain/Touchpoint.java` | add `ACCESSIBLE_BY_ROLE` |
| `backend/src/main/java/com/emsist/designhub/domain/Journey.java` | deprecate / prepare removal of `roleKey` compatibility field |
| `backend/src/main/java/com/emsist/designhub/repository/InteractionRepository.java` | fetch new relationships for `findBySurfaceId` |
| `backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java` | seed confirmation dialogs, upsert ApiContracts, backfill new edges |
| `backend/src/main/java/com/emsist/designhub/config/DataInitializer.java` | seed confirmation dialogs and stable API-call sources for fresh DBs |
| `backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java` | add tests for new seed/backfill query paths |

---

## Chunk 1: Domain Surface (Tasks 1–3)

### Task 1: Add `ConfirmationDialog`

- [x] Create `ConfirmationDialog.java` as a T2 registry node with:
  - `dialogId`
  - `triggerAction`
  - `confirmLabel`
  - `cancelLabel`
  - `consequenceText`
- [x] Create `ConfirmationDialogRepository.java`.
- [x] Add `ConfirmationDialogTest.java` covering builder/default population.
- [x] Run focused tests, then full Maven suite under Java 23.
- [x] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/ConfirmationDialog.java backend/src/main/java/com/emsist/designhub/repository/ConfirmationDialogRepository.java backend/src/test/java/com/emsist/designhub/domain/ConfirmationDialogTest.java && git commit -m "feat: add confirmation dialog registry node"
```

### Task 2: Complete `Interaction` Edge Surface

- [x] Modify `Interaction.java` to add:
  - `@Relationship(type = "USED_BY_PERSONA", direction = OUTGOING)` to `List<Persona>`
  - `@Relationship(type = "ACCESSIBLE_BY_ROLE", direction = OUTGOING)` to `List<BusinessRole>`
  - `@Relationship(type = "CALLS_API", direction = OUTGOING)` to `List<ApiContract>`
  - `@Relationship(type = "TRIGGERS_CONFIRMATION", direction = OUTGOING)` to `ConfirmationDialog`
- [x] Keep `personaIds`, `roleKeys`, `apiCalls`, and `confirmationCode` temporarily as compatibility scaffolding; annotate deprecated if helpful.
- [x] Do **not** remove `REQUIRES_PERMISSION` or the compatibility `permission` string in this task.
- [x] Add or extend model tests to prove builder population of the new relationships.
- [x] Run full Maven suite under Java 23.
- [x] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Interaction.java backend/src/test/java/com/emsist/designhub/domain/InteractionEdgeTraversalTest.java && git commit -m "feat: add interaction persona role api and confirmation edges"
```

### Task 3: Complete `Touchpoint` and `Journey` Compatibility Cleanup

- [x] Modify `Touchpoint.java` to add:
  - `@Relationship(type = "ACCESSIBLE_BY_ROLE", direction = OUTGOING)` to `List<BusinessRole>`
- [x] Modify `Journey.java`:
  - mark `roleKey` as deprecated or add a comment indicating it is compatibility-only and slated for removal
- [x] Do **not** introduce a new direct `Journey -> BusinessRole` edge.
- [x] Extend traversal tests to cover:
  - `Touchpoint -> BusinessRole`
  - continued `Journey -> Persona`
- [x] Run full Maven suite under Java 23.
- [x] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Touchpoint.java backend/src/main/java/com/emsist/designhub/domain/Journey.java backend/src/test/java/com/emsist/designhub/domain/InteractionEdgeTraversalTest.java && git commit -m "feat: complete touchpoint role edge and journey role cleanup"
```

---

## Chunk 2: Seed and Migration Wiring (Tasks 4–6)

### Task 4: Seed `ConfirmationDialog` and Upsert `ApiContract`

- [x] Extend `RegistryGraphMigrationService` with:
  - `seedConfirmationDialogs()`
  - `upsertApiContractsFromInteractions()`
- [x] `seedConfirmationDialogs()` should create at least the currently used Design Hub dialogs:
  - `CONFIRM-AGT-DELETE`
  - `CONFIRM-AGT-PUBLISH`
- [x] `upsertApiContractsFromInteractions()` should:
  - parse `Interaction.apiCalls` values as `METHOD path`
  - merge deterministic `ApiContract` nodes
  - populate `method`, `path`, and generated `contractId`
- [x] Add focused query-builder / Neo4jClient tests for both service paths.
- [x] Run full Maven suite under Java 23.
- [x] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java backend/src/main/java/com/emsist/designhub/repository/ApiContractRepository.java && git commit -m "feat: seed confirmation dialogs and upsert api contracts"
```

### Task 5: Backfill Remaining String-to-Edge Migrations

- [x] Extend `RegistryGraphMigrationService` with backfill methods for:
  - `Interaction.personaIds -> USED_BY_PERSONA`
  - `Interaction.roleKeys -> ACCESSIBLE_BY_ROLE`
  - `Touchpoint.roleKeys -> ACCESSIBLE_BY_ROLE`
  - `Interaction.apiCalls -> CALLS_API`
  - `Interaction.confirmationCode -> TRIGGERS_CONFIRMATION`
- [x] Ensure all backfill uses already-seeded `Persona`, `BusinessRole`, `ApiContract`, and `ConfirmationDialog` nodes.
- [x] Add tests proving the Cypher includes the correct edge names and merge targets.
- [x] Update `runFullMigration()` to call the new seed/backfill steps in deterministic order.
- [x] Run full Maven suite under Java 23.
- [x] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java && git commit -m "feat: backfill remaining interaction and touchpoint edges"
```

### Task 6: Fresh-Seed Alignment in `DataInitializer`

- [x] Update `DataInitializer.java` so fresh DB seeds contain:
  - the same confirmation-dialog codes seeded in Task 4
  - stable `apiCalls` values that parse cleanly into `ApiContract`
  - unchanged compatibility fields for current frontend/runtime consumers
- [x] Keep existing interaction permission seeds and channel seeds intact.
- [x] Run full Maven suite under Java 23.
- [x] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/config/DataInitializer.java && git commit -m "feat: align fresh seed data for interaction edge migration"
```

---

## Chunk 3: Repository Loading and Verification (Tasks 7–9)

### Task 7: Upgrade Runtime Loading for Interaction Queries

- [x] Keep the existing custom `@Query` approach on `InteractionRepository.findBySurfaceId(...)`; do **not** drop to the derived SDN loader in this task.
- [x] Expand the Cypher so it loads:
  - `HAS_EFFECT`
  - `REQUIRES_PERMISSION`
  - `USED_BY_PERSONA`
  - `ACCESSIBLE_BY_ROLE`
  - `CALLS_API`
  - `TRIGGERS_CONFIRMATION`
- [x] Use explicit `OPTIONAL MATCH` + `collect(DISTINCT ...)` clauses for each new relationship so the runtime loading behavior stays deterministic and reviewable.
- [x] Confirm the generic `findAll()` path still returns new relationships correctly; only add extra repository methods if needed.
- [x] Add a focused repository/service regression test if coverage is absent.
- [x] Run full Maven suite under Java 23.
- [x] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/repository/InteractionRepository.java backend/src/main/java/com/emsist/designhub/service/InteractionService.java backend/src/test/java/com/emsist/designhub/service && git commit -m "feat: load interaction graph for new runtime edges"
```

### Task 8: Runtime Smoke Verification

- [x] Restart the backend under Java 23.
- [x] Verify live endpoints:
  - `/api/v1/design-hub/interactions`
  - `/api/v1/design-hub/interactions/by-screen/{surfaceId}`
  - `/api/v1/design-hub/touchpoints`
  - `/api/v1/design-hub/journeys`
- [x] Confirm:
  - at least one interaction returns a non-null `requiresPermission`
  - at least one interaction returns non-empty `callsApi`
  - at least one interaction returns a non-null `triggersConfirmation`
  - touchpoints return `accessibleByRoles` once wired
  - journeys still return `performedByPersona`
- [x] Record any runtime caveat separately from source/test completion.

### Task 9: Final Verification and Closeout

- [x] Run:
  - `JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home PATH=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home/bin:$PATH mvn test -q`
- [x] Verify target baseline:
  - `36 @Node` — verified
  - `57 SDN @Relationship` — verified (one above original 56 target)
  - `1 Cypher-only edge` — verified
  - `216 tests, 0 failures` — verified
- [x] Check remaining legacy fields are limited to compatibility-only cases, not active graph semantics.
- [x] Commit final verification and any cleanup adjustments.

---

## Expected Outcomes

After this plan lands, Design Hub should be able to answer the remaining interaction-centric traversal questions through real graph edges instead of strings:

- `Interaction -> Permission`
- `Interaction -> ApiContract`
- `Interaction -> ConfirmationDialog`
- `Interaction -> Persona`
- `Interaction -> BusinessRole`
- `Touchpoint -> BusinessRole`

The practical result is a cleaner runtime graph for interaction, permission, API, and confirmation flows, while preserving backward compatibility for the current frontend until a dedicated cleanup increment removes the deprecated string fields.
