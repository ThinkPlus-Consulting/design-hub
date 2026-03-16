# Registry / Role Split and Edge Migration Implementation Plan

> **For agentic workers:** Use subagents if available; otherwise execute sequentially. Steps use checkbox syntax for tracking.

**Goal:** Implement the frozen D2 + D3 registry and role-split scope in code: add `Persona`, `Channel`, `Permission`, `BusinessRole`, and `ValidationRole`; wire the five core registry edges; retire `GraphMetadataService`; and move the graph baseline from legacy string-backed role/persona/channel/permission references toward canonical edges.

**Architecture:** This is an implementation-only increment. The approved taxonomy already contains these nodes and edge types. The work is split into three layers: (1) new registry / split-node types, (2) edge declarations on current source entities, (3) seed and migration wiring that replaces the old Neo4j backfill service.

**Tech Stack:** Java 21, Spring Boot 3.4.1, Spring Data Neo4j, Lombok, JUnit 5, Mockito, Neo4jClient

**Implementation Baseline:** `31 @Node / 46 SDN @Relationship / 1 Cypher-only edge / 142 tests`

**Target Implementation State:** `35 @Node / 52 SDN @Relationship / 1 Cypher-only edge / ~165 tests`

**Design-Model Note:** This plan does **not** change the approved taxonomy counts (`75 nodes / 106 edge types / 71 benchmarkable`). It only closes implementation gaps against that already-published target.

---

## Frozen Decisions

### In Scope

**New nodes**
- `Persona` (T1)
- `Channel` (T2)
- `Permission` (T2)
- `BusinessRole` (T1)
- `ValidationRole` (T1)

**Core edge set**
- `PERFORMED_BY_PERSONA` (`Journey -> Persona`)
- `USED_BY_PERSONA` (`Screen -> Persona`, `Touchpoint -> Persona`)
- `DELIVERED_VIA_CHANNEL` (`Touchpoint -> Channel`)
- `REQUIRES_PERMISSION` (`Interaction -> Permission`)
- `ACCESSIBLE_BY_ROLE` (`Screen -> BusinessRole`)

### Deferred

- `TRIGGERS_CONFIRMATION` (`Interaction -> ConfirmationDialog`)
- `CALLS_API` (`Interaction -> ApiContract`)
- `Interaction.personaIds` to `USED_BY_PERSONA`
- `Interaction.roleKeys` to a role edge
- `Touchpoint.roleKeys` to a role edge
- `Journey.roleKey` to a role edge

### Hard Rules

1. `DELIVERED_VIA_CHANNEL` belongs on `Touchpoint`, not `EntryMode`.
2. `ACCESSIBLE_BY_ROLE` targets `BusinessRole`, not legacy `Role`.
3. `GraphMetadataService` is retired in this increment. It must not survive as a second source of truth for role/story graph wiring.
4. Legacy string fields used for this migration may remain temporarily as compatibility scaffolding, but new traversal/query logic must rely on the canonical edges.

### Role Split Mapping

| Key | Target Node |
|-----|-------------|
| `SUPER_ADMIN` | `BusinessRole` |
| `ADMIN` | `BusinessRole` |
| `ARCHITECT` | `BusinessRole` |
| `AGENT_DESIGNER` | `BusinessRole` |
| `USER` | `BusinessRole` |
| `VIEWER` | `BusinessRole` |
| `HITL_REVIEWER` | `ValidationRole` |
| `AUDITOR` | `ValidationRole` |

---

## File Structure

### New Files

| File | Responsibility |
|------|----------------|
| `backend/src/main/java/com/emsist/designhub/domain/Persona.java` | T1 persona node |
| `backend/src/main/java/com/emsist/designhub/domain/Channel.java` | T2 channel registry node |
| `backend/src/main/java/com/emsist/designhub/domain/Permission.java` | T2 permission registry node |
| `backend/src/main/java/com/emsist/designhub/domain/BusinessRole.java` | T1 business-access role |
| `backend/src/main/java/com/emsist/designhub/domain/ValidationRole.java` | T1 validation / governance role |
| `backend/src/main/java/com/emsist/designhub/repository/PersonaRepository.java` | Persona repository |
| `backend/src/main/java/com/emsist/designhub/repository/ChannelRepository.java` | Channel repository |
| `backend/src/main/java/com/emsist/designhub/repository/PermissionRepository.java` | Permission repository |
| `backend/src/main/java/com/emsist/designhub/repository/BusinessRoleRepository.java` | BusinessRole repository |
| `backend/src/main/java/com/emsist/designhub/repository/ValidationRoleRepository.java` | ValidationRole repository |
| `backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java` | Seed + migration service replacing `GraphMetadataService` |
| `backend/src/test/java/com/emsist/designhub/domain/PersonaTest.java` | Persona tests |
| `backend/src/test/java/com/emsist/designhub/domain/ChannelTest.java` | Channel tests |
| `backend/src/test/java/com/emsist/designhub/domain/PermissionTest.java` | Permission tests |
| `backend/src/test/java/com/emsist/designhub/domain/BusinessRoleTest.java` | BusinessRole tests |
| `backend/src/test/java/com/emsist/designhub/domain/ValidationRoleTest.java` | ValidationRole tests |
| `backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java` | Neo4jClient-backed query and migration tests |

### Modified Files

| File | Change |
|------|--------|
| `backend/src/main/java/com/emsist/designhub/domain/Journey.java` | add `PERFORMED_BY_PERSONA` edge |
| `backend/src/main/java/com/emsist/designhub/domain/Screen.java` | add `USED_BY_PERSONA`, `ACCESSIBLE_BY_ROLE` edges |
| `backend/src/main/java/com/emsist/designhub/domain/Touchpoint.java` | add `USED_BY_PERSONA`, `DELIVERED_VIA_CHANNEL` edges |
| `backend/src/main/java/com/emsist/designhub/domain/Interaction.java` | add `REQUIRES_PERMISSION` edge |
| `backend/src/main/java/com/emsist/designhub/config/DataInitializer.java` | replace `GraphMetadataService` usage with registry seed/migration service |

### Deleted Files

| File | Reason |
|------|--------|
| `backend/src/main/java/com/emsist/designhub/domain/Role.java` | superseded by `BusinessRole` + `ValidationRole` |
| `backend/src/main/java/com/emsist/designhub/service/GraphMetadataService.java` | legacy backfill service retired |

---

## Chunk 1: Registry and Split Nodes (Tasks 1–4)

### Task 1: Add `Persona`, `Channel`, and `Permission`

- [ ] Create the three domain nodes using the published catalog shapes:
  - `Persona`: `personaId`, `name`, `summary`, `goals`, `painPoints`, `roleKeys`, `status`, `sourceRefs`
  - `Channel`: `channelCode`, `displayName`, `channelType`
  - `Permission`: `permissionKey`, `displayName`, `sortOrder`
- [ ] Add focused builder/default tests for all three.
- [ ] Run full Maven test suite.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Persona.java backend/src/main/java/com/emsist/designhub/domain/Channel.java backend/src/main/java/com/emsist/designhub/domain/Permission.java backend/src/test/java/com/emsist/designhub/domain/PersonaTest.java backend/src/test/java/com/emsist/designhub/domain/ChannelTest.java backend/src/test/java/com/emsist/designhub/domain/PermissionTest.java && git commit -m "feat: add persona channel and permission nodes"
```

### Task 2: Add `BusinessRole` and `ValidationRole`

- [ ] Create the split role nodes:
  - `BusinessRole`: `roleKey`, `displayName`, `roleGroup`, `scope`, `sortOrder`, `status`, `sourceRefs`
  - `ValidationRole`: `validationRoleKey`, `displayName`, `scope`, `status`, `sourceRefs`
- [ ] Add builder/default tests for both nodes.
- [ ] Do **not** delete `Role.java` in this task. Keep the cutover isolated to Task 8.
- [ ] Run full test suite.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/BusinessRole.java backend/src/main/java/com/emsist/designhub/domain/ValidationRole.java backend/src/test/java/com/emsist/designhub/domain/BusinessRoleTest.java backend/src/test/java/com/emsist/designhub/domain/ValidationRoleTest.java && git commit -m "feat: add business and validation role nodes"
```

### Task 3: Add Repositories for the Five New Nodes

- [ ] Create Spring Data repository interfaces for:
  - `Persona`
  - `Channel`
  - `Permission`
  - `BusinessRole`
  - `ValidationRole`
- [ ] Keep repositories minimal unless a concrete finder is needed by migration code.
- [ ] Run full test suite to confirm repository scanning remains clean.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/repository/PersonaRepository.java backend/src/main/java/com/emsist/designhub/repository/ChannelRepository.java backend/src/main/java/com/emsist/designhub/repository/PermissionRepository.java backend/src/main/java/com/emsist/designhub/repository/BusinessRoleRepository.java backend/src/main/java/com/emsist/designhub/repository/ValidationRoleRepository.java && git commit -m "feat: add repositories for registry and split role nodes"
```

### Task 4: Verify Chunk 1 Baseline

- [ ] Run:
  - `JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -q`
- [ ] Verify expected shape:
  - `@Node`: `36` temporarily (`31 + 5`) because `Role.java` is still present before cutover
  - tests: baseline increased and green
- [ ] Commit only if corrective changes were needed; otherwise no-op checkpoint.

---

## Chunk 2: Core Edge Wiring (Tasks 5–7)

### Task 5: Wire `Journey`, `Screen`, and `Touchpoint`

- [ ] Modify `Journey.java`:
  - add `@Relationship(type = "PERFORMED_BY_PERSONA", direction = OUTGOING)` to `Persona`
- [ ] Modify `Screen.java`:
  - add `USED_BY_PERSONA` to `Persona`
  - add `ACCESSIBLE_BY_ROLE` to `BusinessRole`
- [ ] Modify `Touchpoint.java`:
  - add `USED_BY_PERSONA` to `Persona`
  - add `DELIVERED_VIA_CHANNEL` to `Channel`
- [ ] Keep `personaId`, `personaIds`, and `roleKeys` fields in place for the migration window; optionally annotate with `@Deprecated`.
- [ ] Add tests proving the new relationship lists can be populated via builders.
- [ ] Run full test suite.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Journey.java backend/src/main/java/com/emsist/designhub/domain/Screen.java backend/src/main/java/com/emsist/designhub/domain/Touchpoint.java backend/src/test/java/com/emsist/designhub/domain/PersonaTest.java backend/src/test/java/com/emsist/designhub/domain/BusinessRoleTest.java && git commit -m "feat: wire persona channel and business role edges"
```

### Task 6: Wire `Interaction -> Permission`

- [ ] Modify `Interaction.java`:
  - add `@Relationship(type = "REQUIRES_PERMISSION", direction = OUTGOING)` to `Permission`
- [ ] Keep `permission` string during the migration window; optionally mark it deprecated.
- [ ] Add test coverage for builder population of `requiresPermissions`.
- [ ] Do **not** add `TRIGGERS_CONFIRMATION` or `CALLS_API` in this plan.
- [ ] Run full test suite.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Interaction.java backend/src/test/java/com/emsist/designhub/domain/PermissionTest.java && git commit -m "feat: wire interaction permission edge"
```

### Task 7: Add a Focused Traversal Regression Test

- [ ] Add one test class or extend an existing graph/traversal test to prove the new core paths are representable:
  - `Journey -> Persona`
  - `Screen -> Persona`
  - `Screen -> BusinessRole`
  - `Touchpoint -> Channel`
  - `Interaction -> Permission`
- [ ] Keep this as a source-level model test; full graph integration belongs to the migration service tests in Chunk 3.
- [ ] Run full test suite.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/test/java/com/emsist/designhub/domain && git commit -m "test: cover core registry and role traversal shapes"
```

---

## Chunk 3: Seed / Migration Cutover (Tasks 8–10)

### Task 8: Replace `GraphMetadataService` with `RegistryGraphMigrationService`

- [ ] Create `RegistryGraphMigrationService` using `Neo4jClient`.
- [ ] Responsibilities:
  - seed `Channel` registry with the frozen 9 values
  - seed `Permission` registry with the frozen 8 values
  - seed `BusinessRole` / `ValidationRole` using the split table
  - create `Persona` nodes from discovered `personaId` / `personaIds` values in seeded data
  - create `PERFORMED_BY_PERSONA`, `USED_BY_PERSONA`, `DELIVERED_VIA_CHANNEL`, `REQUIRES_PERMISSION`, `ACCESSIBLE_BY_ROLE` edges from legacy string fields
- [ ] Keep query builders deterministic and label-safe. No dynamic labels from user input.
- [ ] Add tests for:
  - channel seeding query
  - permission seeding query
  - role split query
  - persona node + edge backfill query
  - full migration invocation path through mocked `Neo4jClient`
- [ ] Run full test suite.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java && git commit -m "feat: add registry graph migration service"
```

### Task 9: Rewire `DataInitializer` and Remove Legacy Backfill

- [ ] Modify `DataInitializer.java`:
  - inject `RegistryGraphMigrationService`
  - replace `graphMetadataService.backfillRolesAndStories()` with the new migration call
  - ensure the existing screen/touchpoint/interaction/journey seed still runs before the migration
- [ ] Delete `GraphMetadataService.java`
- [ ] Delete `Role.java`
- [ ] Confirm no remaining production references to either deleted class.
- [ ] Run:
  - `rg -n "GraphMetadataService|\\bRole\\b" backend/src/main/java`
  - expected remaining hits only in new `BusinessRole` / `ValidationRole` names and deferred comments if any
- [ ] Run full test suite.
- [ ] Commit with:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/config/DataInitializer.java backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java && git rm backend/src/main/java/com/emsist/designhub/service/GraphMetadataService.java backend/src/main/java/com/emsist/designhub/domain/Role.java && git commit -m "refactor: retire legacy role backfill service and role node"
```

### Task 10: Final Verification and Count Check

- [ ] Run serial full-suite verification:

```bash
cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -q
```

- [ ] Verify implementation metrics:
  - `@Node`: `35`
  - `SDN @Relationship`: `52`
  - `Cypher-only polymorphic edges`: still `1` (`ASSESSES`)
  - tests: green, target around `165+`
- [ ] Verify retirement:
  - no production `GraphMetadataService`
  - no production legacy `Role`
- [ ] Verify deferred scope was not accidentally pulled in:
  - no `TRIGGERS_CONFIRMATION`
  - no `CALLS_API`
  - no `Touchpoint -> BusinessRole`
  - no `Interaction -> Persona`
  - no `Interaction -> BusinessRole`
- [ ] Commit final cleanup if needed.

---

## TDD and Execution Rules

1. Write or update the tests first for each task.
2. Run the targeted or full suite and observe failure before implementing.
3. Prefer serial Maven execution. This repo has already shown `surefire` instability under overlapping runs.
4. Keep each task independently committable.
5. Do not fold deferred edges into this plan “while nearby.”

---

## Verification Checklist

- [ ] Five new nodes exist in code
- [ ] Legacy `Role` no longer exists in production code
- [ ] `GraphMetadataService` no longer exists in production code
- [ ] `Journey -> Persona` edge exists
- [ ] `Screen -> Persona` edge exists
- [ ] `Touchpoint -> Persona` edge exists
- [ ] `Touchpoint -> Channel` edge exists
- [ ] `Interaction -> Permission` edge exists
- [ ] `Screen -> BusinessRole` edge exists
- [ ] `DELIVERED_VIA_CHANNEL` is on `Touchpoint`, not `EntryMode`
- [ ] `ACCESSIBLE_BY_ROLE` targets `BusinessRole`
- [ ] `TRIGGERS_CONFIRMATION` remains deferred
- [ ] `CALLS_API` remains deferred
- [ ] Full test suite passes

---

## Expected Outcome

After this plan lands, Design Hub will have the missing registry and role nodes that the published taxonomy already assumes, plus the first clean string-to-edge cutover for persona/channel/permission access semantics. The graph will be able to answer the currently documented traversal questions through real nodes and edges instead of legacy backfill artifacts and ad hoc strings.
