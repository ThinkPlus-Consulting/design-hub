# D5a Process Spine Completion Implementation Plan

> **Status: CLOSED** — Both chunks committed. Chunk 1 committed by user (pre-a54efeb), Chunk 2 committed as a54efeb. Final verified baseline: 48 @Node / 78 SDN @Relationship / 1 Cypher-only edge / 281 tests / 0 failures.

**Goal:** Complete the BPMN-aligned process spine by adding 5 new nodes (BusinessDomain, BusinessProcess, ProcessActivity, ProcessGateway, ProcessEvent) and wiring 12 new edges, including 1 on UserStory (HAS_TASK) and 1 on BusinessCapability (REALIZED_BY_PROCESS).

**Architecture:** Process spine follows OMG BPMN 2.0.2 semantics. BusinessProcess contains flow nodes via HAS_FLOW_NODE. Flow nodes connect via FLOWS_TO with edge properties. ProcessActivity supports SUBPROCESS/CALL_ACTIVITY expansion. ProcessEvent supports BOUNDARY attachment.

**Tech Stack:** Java 23, Spring Boot 3.4.1, Spring Data Neo4j, Lombok, JUnit 5, Mockito, Neo4jClient

**Implementation Baseline (pre-plan):** `43 @Node / 66 SDN @Relationship / 1 Cypher-only edge / 247 tests`

**Target Baseline (post-plan):** `48 @Node / 78 SDN @Relationship / 1 Cypher-only edge / ≥265 tests`

**Actual Final Baseline:** `48 @Node / 78 SDN @Relationship / 1 Cypher-only edge / 281 tests / 0 failures`

**Edge count correction:** The original plan listed 11 new annotations totaling 77. Actual count is 12 new annotations (the table rows sum to 12, not 11) totaling 78. The plan's "Total new: 11" was an arithmetic error in the original table.

---

## Frozen Decisions

### In Scope

**New nodes (5)**
- `BusinessDomain` (T2 — registry)
- `BusinessProcess` (T1)
- `ProcessActivity` (T1)
- `ProcessGateway` (T1)
- `ProcessEvent` (T1)

**New edges (11)**

| Source | Edge | Target | Notes |
|--------|------|--------|-------|
| BusinessDomain | HAS_CAPABILITY | BusinessCapability | T2→T1 containment |
| BusinessCapability | REALIZED_BY_PROCESS | BusinessProcess | Existing entity, new edge |
| BusinessProcess | HAS_FLOW_NODE | ProcessActivity | Polymorphic — also targets Gateway/Event |
| BusinessProcess | HAS_FLOW_NODE | ProcessGateway | Same edge type, different target |
| BusinessProcess | HAS_FLOW_NODE | ProcessEvent | Same edge type, different target |
| ProcessActivity | FLOWS_TO | ProcessActivity | Self-referential sequence flow |
| ProcessActivity | EXPANDS_TO | BusinessProcess | activityType=SUBPROCESS only |
| ProcessActivity | CALLS_PROCESS | BusinessProcess | activityType=CALL_ACTIVITY only |
| ProcessEvent | ATTACHED_TO | ProcessActivity | eventPosition=BOUNDARY only |
| UserStory | HAS_TASK | Task | Delivery spine completion |
| ProcessGateway | FLOWS_TO | ProcessActivity | Gateway→Activity flow |

**Note on FLOWS_TO:** The plan treats FLOWS_TO as a single edge type that can connect any flow-node pair. SDN models this as separate `@Relationship` fields per target type on each source entity. The count above reflects unique `@Relationship` annotations, not unique edge-type names.

### Out of Scope

- Remaining 25 stub entities (D5b)
- FLOWS_TO edge properties (conditionExpression, isDefault, name) — requires SDN `@RelationshipProperties`, deferred
- Frontend model updates
- REST controllers or DTOs for process entities
- Seed data beyond minimal edge coverage

### Hard Rules

1. Every new entity uses `Status status` (universal 10-value enum).
2. Every new entity uses `@Id private String <entityId>` with a pattern prefix.
3. Every new `@Relationship` must have at least one seeded example in `RegistryGraphMigrationService`.
4. Tests must cover: entity builder, relationship population, and seed query execution.
5. Run full Maven suite under Java 23 after each chunk.

---

## File Structure

### New Files (5 entities + 5 tests)

| File | Responsibility |
|------|----------------|
| `domain/BusinessDomain.java` | T2 registry — domain container for capabilities |
| `domain/BusinessProcess.java` | T1 BPMN process — contains flow nodes |
| `domain/ProcessActivity.java` | T1 BPMN task/subprocess/call-activity |
| `domain/ProcessGateway.java` | T1 BPMN gateway (exclusive/parallel/inclusive) |
| `domain/ProcessEvent.java` | T1 BPMN event (start/end/intermediate/boundary) |
| `test/.../domain/BusinessDomainTest.java` | Builder tests |
| `test/.../domain/BusinessProcessTest.java` | Builder tests |
| `test/.../domain/ProcessActivityTest.java` | Builder tests |
| `test/.../domain/ProcessGatewayTest.java` | Builder tests |
| `test/.../domain/ProcessEventTest.java` | Builder tests |

### Modified Files

| File | Change |
|------|--------|
| `domain/BusinessCapability.java` | Add `REALIZED_BY_PROCESS → BusinessProcess` |
| `domain/UserStory.java` | Add `HAS_TASK → Task` |
| `service/RegistryGraphMigrationService.java` | Seed process spine + HAS_TASK edge |
| `test/.../service/RegistryGraphMigrationServiceTest.java` | Tests for new seed methods |
| `test/.../domain/ProcessSpineTraversalTest.java` | New — full spine traversal tests |

---

## Chunk 1: BusinessDomain + BusinessProcess + ProcessActivity + Edges (Tasks 1–4)

### Task 1: Add `BusinessDomain`

- [x] Create `BusinessDomain.java`:
  - `@Id domainCode` (Pattern: `DOM-{code}`)
  - `name: String`
  - `description: String`
  - `activeStatus: String` (ACTIVE, DEPRECATED)
  - `@Relationship(type = "HAS_CAPABILITY") List<BusinessCapability> capabilities`
- [x] Create `BusinessDomainTest.java` covering builder/defaults.

### Task 2: Add `BusinessProcess`

- [x] Create `BusinessProcess.java`:
  - `@Id processId` (Pattern: `PROC-{code}`)
  - `name: String`
  - `description: String`
  - `diagramFormat: String` (BPMN_XML, SVG, PNG, PDF, DRAWIO)
  - `diagramPath: String`
  - `diagramVersion: String`
  - `diagramSource: String` (OMG_BPMN, CAMUNDA, DRAWIO, MANUAL, BPMN_IO)
  - `isExecutableModel: boolean`
  - `status: Status`
  - `@Relationship(type = "HAS_FLOW_NODE") List<ProcessActivity> activities`
  - `@Relationship(type = "HAS_FLOW_NODE") List<ProcessGateway> gateways`
  - `@Relationship(type = "HAS_FLOW_NODE") List<ProcessEvent> events`
- [x] Create `BusinessProcessTest.java` covering builder/defaults.

### Task 3: Add `ProcessActivity`

- [x] Create `ProcessActivity.java`:
  - `@Id activityId` (Pattern: `ACT-{processId}-{seq}`)
  - `name: String`
  - `description: String`
  - `activityType: String` (TASK, SUBPROCESS, CALL_ACTIVITY)
  - `actionType: String` (CREATE, READ, UPDATE, DELETE, APPROVE, REJECT, ARCHIVE, SUBMIT, REVIEW, NOTIFY)
  - `taskNature: String` (USER, SERVICE, MANUAL, RULE, SCRIPT, SEND, RECEIVE)
  - `orderIndex: int`
  - `trigger: String`
  - `preCondition: String`
  - `postCondition: String`
  - `status: Status`
  - `@Relationship(type = "FLOWS_TO") List<ProcessActivity> flowsToActivities`
  - `@Relationship(type = "EXPANDS_TO") List<BusinessProcess> expandsTo`
  - `@Relationship(type = "CALLS_PROCESS") List<BusinessProcess> callsProcess`
- [x] Create `ProcessActivityTest.java` covering builder/defaults.

### Task 4: Wire Chunk 1 Edges + BusinessCapability

- [x] Modify `BusinessCapability.java`:
  - Add `@Relationship(type = "REALIZED_BY_PROCESS") List<BusinessProcess> realizedByProcesses`
  - Add imports for `Relationship` and `List`
- [x] Create `ProcessSpineTraversalTest.java` covering:
  - `BusinessDomain → HAS_CAPABILITY → BusinessCapability`
  - `BusinessCapability → REALIZED_BY_PROCESS → BusinessProcess`
  - `BusinessProcess → HAS_FLOW_NODE → ProcessActivity`
  - `ProcessActivity → FLOWS_TO → ProcessActivity`
  - `ProcessActivity → EXPANDS_TO → BusinessProcess`
  - `ProcessActivity → CALLS_PROCESS → BusinessProcess`
- [x] Run full Maven suite under Java 23.
- [x] Commit:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add \
  backend/src/main/java/com/emsist/designhub/domain/BusinessDomain.java \
  backend/src/main/java/com/emsist/designhub/domain/BusinessProcess.java \
  backend/src/main/java/com/emsist/designhub/domain/ProcessActivity.java \
  backend/src/main/java/com/emsist/designhub/domain/BusinessCapability.java \
  backend/src/test/java/com/emsist/designhub/domain/BusinessDomainTest.java \
  backend/src/test/java/com/emsist/designhub/domain/BusinessProcessTest.java \
  backend/src/test/java/com/emsist/designhub/domain/ProcessActivityTest.java \
  backend/src/test/java/com/emsist/designhub/domain/ProcessSpineTraversalTest.java \
&& git commit -m "feat: add BusinessDomain BusinessProcess ProcessActivity with process spine edges"
```

**Post-Chunk 1 actual:** `48 @Node / 74 SDN @Relationship / 269 tests` (includes ProcessGateway/ProcessEvent as attribute-only stubs — edges added in Chunk 2)

---

## Chunk 2: ProcessGateway + ProcessEvent + HAS_TASK + Seed (Tasks 5–8)

### Task 5: Add `ProcessGateway`

- [x] Create `ProcessGateway.java`:
  - `@Id gatewayId` (Pattern: `GW-{processId}-{seq}`)
  - `name: String`
  - `gatewayType: String` (EXCLUSIVE, PARALLEL, INCLUSIVE, EVENT_BASED, COMPLEX)
  - `defaultFlowTarget: String`
  - `status: Status`
  - `@Relationship(type = "FLOWS_TO") List<ProcessActivity> flowsToActivities`
- [x] Create `ProcessGatewayTest.java` covering builder/defaults.

### Task 6: Add `ProcessEvent`

- [x] Create `ProcessEvent.java`:
  - `@Id eventId` (Pattern: `EVT-{processId}-{seq}`)
  - `name: String`
  - `eventPosition: String` (START, END, INTERMEDIATE_CATCH, INTERMEDIATE_THROW, BOUNDARY)
  - `eventTrigger: String` (NONE, MESSAGE, TIMER, ERROR, SIGNAL, ESCALATION, CONDITIONAL, COMPENSATION, CANCEL, TERMINATE, LINK)
  - `isInterrupting: boolean`
  - `attachedToRef: String`
  - `status: Status`
  - `@Relationship(type = "ATTACHED_TO") List<ProcessActivity> attachedTo`
  - `@Relationship(type = "FLOWS_TO") List<ProcessActivity> flowsToActivities`
- [x] Create `ProcessEventTest.java` covering builder/defaults.

### Task 7: Wire HAS_TASK + Extend Traversal Tests

- [x] Modify `UserStory.java`:
  - Add `@Relationship(type = "HAS_TASK", direction = OUTGOING) List<Task> tasks`
- [x] Extend `ProcessSpineTraversalTest.java` covering:
  - `BusinessProcess → HAS_FLOW_NODE → ProcessGateway`
  - `BusinessProcess → HAS_FLOW_NODE → ProcessEvent`
  - `ProcessGateway → FLOWS_TO → ProcessActivity`
  - `ProcessEvent → ATTACHED_TO → ProcessActivity`
  - `ProcessEvent → FLOWS_TO → ProcessActivity`
  - `UserStory → HAS_TASK → Task`
- [x] Run full Maven suite under Java 23.

### Task 8: Seed Coverage for All New Edge Families

- [x] Extend `RegistryGraphMigrationService.java` with seed methods:
  - `seedBusinessDomains()` — at least 1 BusinessDomain with HAS_CAPABILITY to existing BusinessCapability
  - `seedBusinessProcesses()` — at least 1 BusinessProcess with HAS_FLOW_NODE to ProcessActivity/Gateway/Event
  - `seedProcessFlows()` — at least 1 FLOWS_TO chain (Start → Activity → Gateway → Activity → End)
  - `seedProcessExpansion()` — at least 1 EXPANDS_TO (subprocess) and 1 CALLS_PROCESS (call-activity)
  - `seedBoundaryEvent()` — at least 1 ProcessEvent ATTACHED_TO ProcessActivity
  - `seedStoryTasks()` — at least 1 UserStory → HAS_TASK → Task
- [x] Update `runFullMigration()` to call new seed methods after phase 6.
- [x] Add tests for each new seed method in `RegistryGraphMigrationServiceTest.java`.
- [x] Run full Maven suite.
- [x] Commit:

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add \
  backend/src/main/java/com/emsist/designhub/domain/ProcessGateway.java \
  backend/src/main/java/com/emsist/designhub/domain/ProcessEvent.java \
  backend/src/main/java/com/emsist/designhub/domain/UserStory.java \
  backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java \
  backend/src/test/java/com/emsist/designhub/domain/ProcessGatewayTest.java \
  backend/src/test/java/com/emsist/designhub/domain/ProcessEventTest.java \
  backend/src/test/java/com/emsist/designhub/domain/ProcessSpineTraversalTest.java \
  backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java \
&& git commit -m "feat: add ProcessGateway ProcessEvent with seed coverage and HAS_TASK edge"
```

**Post-Chunk 2 actual:** `48 @Node / 78 SDN @Relationship / 1 Cypher-only edge / 281 tests / 0 failures`

---

## Edge Count Reconciliation

| Entity | New @Relationship annotations | Edge types |
|--------|-------------------------------|------------|
| BusinessDomain | 1 | HAS_CAPABILITY |
| BusinessProcess | 3 | HAS_FLOW_NODE (×3 targets) |
| ProcessActivity | 3 | FLOWS_TO, EXPANDS_TO, CALLS_PROCESS |
| ProcessGateway | 1 | FLOWS_TO |
| ProcessEvent | 2 | ATTACHED_TO, FLOWS_TO |
| BusinessCapability | 1 | REALIZED_BY_PROCESS |
| UserStory | 1 | HAS_TASK |
| **Total new** | **12** | |

Pre-plan: 66. Post-plan: 66 + 12 = **78**.

---

## Expected Outcomes

After this plan lands, Design Hub gains:

- Full BPMN-aligned process spine: `BusinessDomain → HAS_CAPABILITY → BusinessCapability → REALIZED_BY_PROCESS → BusinessProcess → HAS_FLOW_NODE → ProcessActivity|ProcessGateway|ProcessEvent`
- Sequence flow topology: `FLOWS_TO` connecting activities, gateways, and events
- Process decomposition: `EXPANDS_TO` (subprocess) and `CALLS_PROCESS` (call-activity)
- Boundary event attachment: `ProcessEvent → ATTACHED_TO → ProcessActivity`
- Delivery spine completion: `UserStory → HAS_TASK → Task`

Every new edge family has at least one live seeded example, verifiable via the migration service tests.
