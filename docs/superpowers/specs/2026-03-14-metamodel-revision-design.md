# Meta-Model Revision Design Spec

**Date:** 2026-03-14
**Status:** Approved
**Scope:** Graph meta-model hierarchy, BPMN alignment, four-verb edge model, propagation to 10 documentation files

---

## 1. Problem Statement

The Design Hub graph model evolved incrementally from 26 objects in a flat table to 60 elements across 3 tiers. During this growth, several gaps emerged:

- **Missing delivery spine**: No `Task` node existed despite both Azure DevOps and Jira modeling tasks as core work items. The delivery hierarchy stopped at `UserStory`.
- **Missing domain anchor**: No `BusinessDomain` existed to group `BusinessCapability` nodes, leaving the top of the architecture spine unanchored.
- **Overloaded process node**: `ProcessStep` carried business action semantics (CREATE, APPROVE), control-flow semantics (gateway routing), and lifecycle semantics (start/end events) in a single node — violating BPMN 2.0.2 separation.
- **Inconsistent edge verbs**: Multiple edges described the same linkage with different verbs and opposite directions (e.g., `IMPLEMENTS_STORY` vs `DELIVERS`, `ON_SCREEN` vs `HAS_INTERACTION`).
- **Divergent baselines**: Documentation files referenced different counts (product-vision: 50/46, modeling-taxonomy: 60/56, azure-jira-benchmark: 46) due to incremental drafting without normalization.

## 2. Design Decisions (Frozen)

### 2.1 Frozen Decision Register

| # | Decision | Resolution |
|---|----------|------------|
| 1 | BusinessDomain | T2 registry (facet, not lifecycle) |
| 2 | BusinessCapability | T1, enduring top-level concept |
| 3 | BusinessObjective | T1, stays separate (measurable outcome) |
| 4 | ProcessStep renamed | ProcessActivity (T1) for BPMN fidelity |
| 5 | ProcessGateway | T1, new — BPMN routing/branching node |
| 6 | ProcessEvent | T1, new — BPMN trigger/signal/timer node |
| 7 | Task | T1, standalone execution node |
| 8 | BacklogItem concept | Dropped — use concrete Epic/Feature/UserStory |
| 9 | Requirement (separate node) | Not needed now — backlog items carry requirement semantics |
| 10 | CRUD | Attribute on ProcessActivity (`actionType`), not a node |
| 11 | Edge verbs | REALIZES, DELIVERS, IMPLEMENTS, VERIFIED_BY |
| 12 | HAS_FLOW_NODE | Canonical process containment edge (replaces HAS_STEP on process spine; Journey HAS_STEP unchanged) |
| 13 | FLOWS_TO | Edge with properties (conditionExpression, isDefault, name) |
| 14 | EXPANDS_TO | Only for activityType=SUBPROCESS |
| 15 | CALLS_PROCESS | For activityType=CALL_ACTIVITY |
| 16 | Diagram support | 5 optional attributes on BusinessProcess |
| 17 | Two anchors | Operational (BusinessProcess/ProcessActivity) and Delivery (UserStory/Task) |
| 18 | BPMN source | OMG BPMN 2.0.2 canonical, bpmn.io/bpmn-moddle practical |

### 2.2 Revised Tier Counts

| Tier | Count | Benchmarkable |
|------|-------|---------------|
| T1 (First-Class) | 52 | Yes |
| T2 (Registry) | 9 | Yes |
| T3 (Value Object) | 4 | No |
| **Total** | **65** | **61** |

### 2.3 T1 Categories (7)

| # | Category | Count | Objects |
|---|----------|-------|---------|
| 1 | Strategic & Governance | 8 | BusinessObjective, Decision, Assumption, Constraint, SourceReference, Finding, Bug, Risk |
| 2 | Business & Experience | 7 | Persona, BusinessRole, ValidationRole, Journey, JourneyStep, Topic, Touchpoint |
| 3 | Delivery & Execution | 4 | Epic, Feature, UserStory, Task |
| 4 | Requirement & Design | 9 | AcceptanceCriterion, Rule, ValidationRule, EdgeCase, ExceptionCase, Screen, ScreenState, Interaction, Transition |
| 5 | Engineering | 8 | ApiContract, RequestSchema, ResponseSchema, ErrorContract, DataEntity, DataField, Integration, TestCase |
| 6 | Architecture & EA | 12 | BusinessCapability, BusinessProcess, ProcessActivity, ProcessGateway, ProcessEvent, Organization, Application, ApplicationComponent, BusinessObject, InformationFlow, Deployment, InfrastructureNode |
| 7 | Cross-cutting | 4 | ExternalArtifact, OpenQuestion, Gap, Message |

**Verification:** 8+7+4+9+8+12+4 = 52

### 2.4 T2 Registry (9)

| Family | Objects |
|--------|---------|
| Architecture & EA | BusinessDomain |
| Business & Experience | Channel |
| Requirement & Governance | Permission, ErrorCode, ConfirmationDialog |
| Engineering | Enum, Event |
| Cross-cutting / Localization | Locale, TranslationKey |

### 2.5 Implementation Counts

| Status | Count |
|--------|-------|
| `[EDGE]` | 8 |
| `[STRING_REF]` | 9 |
| `[PLANNED]` | 59 |
| **Total** | **76** |

## 3. Four-Verb Edge Model

| Family | Verb | Source Level | Targets | Count |
|--------|------|-------------|---------|-------|
| Traceability | `REALIZES` | Epic, Feature, UserStory | BusinessCapability, BusinessProcess, Journey, ProcessActivity, JourneyStep | 5 |
| Delivery | `DELIVERS` | UserStory | Screen, ApiContract, DataEntity, Rule, Message | 5 |
| Execution | `IMPLEMENTS` | Task | Screen, ApiContract, DataEntity, Rule, Message, TestCase | 6 |
| Verification | `VERIFIED_BY` | UserStory | TestCase | 1 |
| Verification | `VERIFIES` | TestCase | Screen, ApiContract | 2 |

### Four-Concern Story Gate

| Stage | Required Edges | Status Gate |
|-------|---------------|-------------|
| Traced | >=1 REALIZES | Can enter IN_DEFINITION |
| Deliverable | >=1 DELIVERS | Can enter APPROVED |
| Executable | >=1 HAS_TASK | Can enter IN_IMPLEMENTATION |
| Verifiable | >=1 VERIFIED_BY | Can enter VERIFIED |

### Key MCRs

- **MCR-STORY-DELIVERS-001**: UserStory must have >=1 DELIVERS edge. Family-level BLOCKING.
- **MCR-STORY-VERIFIED-001**: UserStory must have >=1 VERIFIED_BY edge before status=VERIFIED. BLOCKING.
- **MCR-PROCESS-FLOW-001**: BusinessProcess must have >=1 HAS_FLOW_NODE edge. BLOCKING.

## 4. Process Spine (BPMN-Aligned)

```
BusinessDomain(T2) -[HAS_CAPABILITY]-> BusinessCapability(T1)
BusinessCapability -[REALIZED_BY_PROCESS]-> BusinessProcess(T1)
BusinessProcess -[HAS_FLOW_NODE]-> ProcessActivity | ProcessGateway | ProcessEvent
ProcessActivity|ProcessGateway|ProcessEvent -[FLOWS_TO]-> ProcessActivity|ProcessGateway|ProcessEvent
ProcessActivity(SUBPROCESS) -[EXPANDS_TO]-> BusinessProcess
ProcessActivity(CALL_ACTIVITY) -[CALLS_PROCESS]-> BusinessProcess
ProcessEvent(BOUNDARY) -[ATTACHED_TO]-> ProcessActivity
```

### Delivery Spine

```
Epic -[HAS_FEATURE]-> Feature -[HAS_STORY]-> UserStory -[HAS_TASK]-> Task
Task -[DEPENDS_ON]-> Task
Task -[ASSIGNED_TO]-> Organization
```

### BPMN Adoption Profile

| Layer | BPMN Elements | Treatment | Priority |
|-------|--------------|-----------|----------|
| A: Process Essentials | Process, Task subtypes, SubProcess, SequenceFlow | BusinessProcess, ProcessActivity, FLOWS_TO | P0 |
| B: Control Flow | Gateways, Events | ProcessGateway, ProcessEvent T1 nodes | P0 |
| C: Collaboration | Participant, Lane, MessageFlow | Participant->Organization, Lane->BusinessRole | P2 |
| D: Data | DataObject, DataStore | DataObject->BusinessObject | P2 |
| E: Advanced | Choreography, Conversation, Compensation | Out of scope | -- |

## 5. Deprecated Edges

| Edge | Replacement | Reason |
|------|-------------|--------|
| ON_SCREEN (Interaction->Screen) | HAS_INTERACTION (Screen->Interaction) | Duplicate inverse |
| IMPLEMENTS_STORY (Screen->UserStory) | DELIVERS (UserStory->Screen) | Direction reversal + new verb |
| DEPLOYS (Application->Deployment) | HOSTS + DEPLOYED_ON | Directional fix |
| DETECTED_BY_BENCHMARK (Gap->computed) | `detectedBy` property on Gap | Not a real edge |
| HAS_STEP (BusinessProcess->ProcessActivity) | HAS_FLOW_NODE | Semantic correction for BPMN alignment |

## 6. New Object Specifications

### 6.1 ProcessActivity (renamed from ProcessStep)

**Tier**: T1 | **Category**: Architecture & EA

| Attribute | Type | Required | Constraints |
|-----------|------|----------|-------------|
| activityId | String | Yes | Pattern: `ACT-{processId}-{seq}` |
| name | String | Yes | |
| description | String | No | |
| activityType | Enum | Yes | TASK, SUBPROCESS, CALL_ACTIVITY |
| actionType | Enum | Yes | CREATE, READ, UPDATE, DELETE, APPROVE, REJECT, ARCHIVE, SUBMIT, REVIEW, NOTIFY |
| taskNature | Enum | No | USER, SERVICE, MANUAL, RULE, SCRIPT, SEND, RECEIVE |
| orderIndex | Integer | No | Presentation hint only -- FLOWS_TO is canonical |
| trigger | String | No | |
| preCondition | String | No | |
| postCondition | String | No | |
| status | Enum | Yes | Universal 10-value |

**Relationships:** HAS_FLOW_NODE(IN, BusinessProcess, BLOCKING), FLOWS_TO(OUT, any flow node, OPTIONAL), EXPANDS_TO(OUT, BusinessProcess, OPTIONAL -- activityType=SUBPROCESS only), CALLS_PROCESS(OUT, BusinessProcess, OPTIONAL -- activityType=CALL_ACTIVITY only), REALIZES(IN, UserStory, OPTIONAL)

### 6.2 ProcessGateway

**Tier**: T1 | **Category**: Architecture & EA

| Attribute | Type | Required | Constraints |
|-----------|------|----------|-------------|
| gatewayId | String | Yes | Pattern: `GW-{processId}-{seq}` |
| name | String | No | |
| gatewayType | Enum | Yes | EXCLUSIVE, PARALLEL, INCLUSIVE, EVENT_BASED, COMPLEX |
| defaultFlowTarget | String | No | References target node ID |
| status | Enum | Yes | Universal 10-value |

**Relationships:** HAS_FLOW_NODE(IN, BusinessProcess, BLOCKING), FLOWS_TO(OUT/IN, any flow node, BLOCKING)

### 6.3 ProcessEvent

**Tier**: T1 | **Category**: Architecture & EA

| Attribute | Type | Required | Constraints |
|-----------|------|----------|-------------|
| eventId | String | Yes | Pattern: `EVT-{processId}-{seq}` |
| name | String | No | |
| eventPosition | Enum | Yes | START, END, INTERMEDIATE_CATCH, INTERMEDIATE_THROW, BOUNDARY |
| eventTrigger | Enum | No | NONE, MESSAGE, TIMER, ERROR, SIGNAL, ESCALATION, CONDITIONAL, COMPENSATION, CANCEL, TERMINATE, LINK |
| isInterrupting | Boolean | No | Default: true |
| attachedToRef | String | No | Import-only metadata from BPMN source. Canonical semantic source is the ATTACHED_TO edge. |
| status | Enum | Yes | Universal 10-value |

**Relationships:** HAS_FLOW_NODE(IN, BusinessProcess, BLOCKING), FLOWS_TO(OUT, any flow node, OPTIONAL), ATTACHED_TO(OUT, ProcessActivity, OPTIONAL)

### 6.4 Task

**Tier**: T1 | **Category**: Delivery & Execution

| Attribute | Type | Required | Constraints |
|-----------|------|----------|-------------|
| taskId | String | Yes | Pattern: `TSK-{module}-{seq}` |
| title | String | Yes | Max 200 chars |
| description | String | No | |
| taskType | Enum | Yes | FRONTEND, BACKEND, API, DATA, TEST, DEVOPS, UX, DOCUMENTATION |
| status | Enum | Yes | Universal 10-value |
| priority | Enum | No | CRITICAL, HIGH, MEDIUM, LOW |
| estimate | String | No | |
| actualEffort | String | No | |
| assigneeName | String | No | Temporary until Person T1 |
| teamName | String | No | Temporary until team modeled |
| dueDate | Date | No | |

**Relationships:** HAS_TASK(IN, UserStory, BLOCKING), IMPLEMENTS(OUT, Screen|ApiContract|DataEntity|Rule|Message|TestCase, OPTIONAL), DEPENDS_ON(OUT, Task, OPTIONAL), ASSIGNED_TO(OUT, Organization, OPTIONAL)

### 6.5 BusinessDomain

**Tier**: T2 | **Family**: Architecture & EA

| Attribute | Type | Required | Constraints |
|-----------|------|----------|-------------|
| domainCode | String | Yes | Pattern: `DOM-{code}` |
| name | String | Yes | |
| description | String | No | |
| activeStatus | Enum | No | ACTIVE, DEPRECATED |

**Relationships:** HAS_CAPABILITY(OUT, BusinessCapability, BLOCKING)

### 6.6 UserStory -- New Edges and Attributes

**New attributes:** originType (PROCESS|EXPERIENCE|TECHNICAL|CROSS_CUTTING), natureType (FUNCTIONAL|NON_FUNCTIONAL)

**New edges:** REALIZES(OUT, ProcessActivity|JourneyStep, OPTIONAL), DELIVERS(OUT, Screen|ApiContract|DataEntity|Rule|Message, OPTIONAL), HAS_TASK(OUT, Task, OPTIONAL), VERIFIED_BY(OUT, TestCase, BLOCKING)

### 6.7 BusinessProcess -- New Attributes

diagramFormat (BPMN_XML|SVG|PNG|PDF|DRAWIO), diagramPath (String), diagramVersion (String), diagramSource (OMG_BPMN|CAMUNDA|DRAWIO|MANUAL|BPMN_IO), isExecutableModel (Boolean, default false)

**Critical rule:** A BusinessProcess with diagramPath but no HAS_FLOW_NODE edges is scored as incomplete by the benchmark.

## 7. Propagation Matrix

### Step 0: Baseline Normalization

Documents do not share a single baseline:

| File | Current T1 | Current Benchmarkable | Target |
|------|-----------|----------------------|--------|
| modeling-taxonomy.md | 48 | 56 | 52/61 |
| graph-object-catalog.md | 48 | 56 | 52/61 |
| vision-benchmark.md | 48 | 56 | 52/61 |
| product-vision.md | 38 | 46 | 52/61 |
| azure-jira-benchmark.md | -- | 46 | --/61 |

**Rule:** Each file edit must state what baseline it moves FROM, not just what it moves TO.

### Propagation Steps (10 Files)

| # | File | Priority | Key Changes |
|---|------|----------|-------------|
| 1 | modeling-taxonomy.md | P0 | Rename ProcessStep->ProcessActivity, add ProcessGateway+ProcessEvent+Task+BusinessDomain, create Delivery & Execution category, update counts to 52/9/4/65/61, edge count to 76 |
| 2 | graph-object-catalog.md | P0 | Add new object specs, update UserStory edges, update BusinessProcess, update relationship registry to 76, deprecate 5 edges |
| 3 | vision-benchmark.md | P1 | Update benchmarkable 56->61, add BPMN queryability tests, add new object rows |
| 4 | implementation-readiness-graph-model.md | P1 | Replace deprecated edges in MCRs and Cypher, add 3 new MCRs, add four-concern story gate, update applicability matrix |
| 5 | product-vision.md | P2 | Update counts 50->65/46->61, add BPMN process modeling, update spines |
| 6 | feature-capability-map.md | P2 | Add Process Flow View, update mapping for 61 benchmarkable |
| 7 | architecture-blueprint.md | P2 | Add BPMN alignment section, update process layer |
| 8 | azure-jira-benchmark.md | P2 | Add Task to mapping, update hierarchy, fix REPRESENTS targets |
| 9 | alfabet-alignment-matrix.md | P2 | Update Arch&EA count 10->12 |
| 10 | design-testing-strategy.md | P2 | Add Process Flow View test coverage |

## 8. Verification

After propagation, verify:

1. All 10 files reference 52 T1 / 9 T2 / 4 T3 / 65 total / 61 benchmarkable / 76 edges
2. No file uses ProcessStep, IMPLEMENTS_STORY, ON_SCREEN, DEPLOYS, or DETECTED_BY_BENCHMARK
3. HAS_STEP appears only on Journey spine, never on process spine
4. CALLS_PROCESS appears in ProcessActivity relationship spec
5. attachedToRef is documented as import-only; ATTACHED_TO is canonical
6. Four-verb model (REALIZES/DELIVERS/IMPLEMENTS/VERIFIED_BY) is consistent across all files
7. T1 category totals sum to 52 (8+7+4+9+8+12+4)
