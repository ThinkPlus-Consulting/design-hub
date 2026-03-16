# Graph Object Catalog

**Status:** Draft
**Purpose:** Full specification of all graph model elements for Design Hub, classified by the three-tier taxonomy defined in `modeling-taxonomy.md`.

**Related documents:**

- `modeling-taxonomy.md` (classification rules, current-to-target mapping, string-to-edge migration)
- `implementation-readiness-graph-model.md` (status, readiness, and completeness governance)
- `vision-benchmark.md` (scoring against this catalog)

---

## 1. Object Design Rules

- Every Tier 1 object has a pattern-based stable identifier and universal `status`
- Every Tier 2 object has a code or key identifier; no independent `status` lifecycle
- Every Tier 3 object inherits identity and lifecycle from its parent
- Only implementation-driving objects carry `readiness` flags
- Every implementation-driving object should support `sourceRefs`
- Relationship edges are typed, directional, and queryable in both directions
- Objects are classified into Tier 1 (first-class node), Tier 2 (registry), or Tier 3 (value object) per `modeling-taxonomy.md`

---

## 2. Tier Summary

| Tier | Count | Benchmarkable | Objects |
|------|-------|---------------|---------|
| Tier 1 — First-Class Node | 58 | Yes | See sections 3.1 through 3.7 |
| Tier 2 — Registry Node | 13 | Yes | See section 4 |
| Tier 3 — Value Object | 4 | No (scored via parent) | See section 5 |
| **Total** | **75** | **71** | |

---

## 3. Tier 1 — First-Class Nodes (58)

### 3.1 Strategic & Governance (9)

---

### BusinessObjective

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Business intent and outcome target
**Implementation Status**: `[IMPLEMENTED]` `domain/ApiContract.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `objectiveId` | String | Yes | Stable identifier | Pattern: `OBJ-{module}-{seq}` |
| `title` | String | Yes | Short descriptive title | Max 200 chars |
| `description` | String | Yes | Full description of the objective | |
| `module` | String | Yes | Owning module | Enum: defined per project |
| `topic` | String | No | Thematic grouping | |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED, IN_DEFINITION, DEFINED, IN_REVIEW, APPROVED, IN_IMPLEMENTATION, IMPLEMENTED, VERIFIED, DEPRECATED, RETIRED |
| `sourceRefs` | List | No | Provenance links | List of SourceReference IDs |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_FEATURE` | OUTGOING | Feature | 1:N | Yes | BLOCKING | `[PLANNED]` |
| `DRIVES_STORY` | OUTGOING | UserStory | 1:N | No | OPTIONAL | `[PLANNED]` |

---

### Decision

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Recorded decision with rationale and status
**Implementation Status**: `[IMPLEMENTED]` `domain/Message.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `decisionId` | String | Yes | Stable identifier | Pattern: `DEC-{seq}` |
| `title` | String | Yes | Short descriptive title | Max 200 chars |
| `context` | String | Yes | Context and drivers | |
| `outcome` | String | Yes | What was decided | |
| `rationale` | String | No | Why this was chosen | |
| `alternatives` | List | No | Rejected alternatives | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `AFFECTS_FEATURE` | OUTGOING | Feature | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Assumption

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Stated assumption underlying design or scope
**Implementation Status**: `[IMPLEMENTED]` `domain/SourceReference.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `assumptionId` | String | Yes | Stable identifier | Pattern: `ASM-{seq}` |
| `statement` | String | Yes | The assumption | |
| `impact` | String | No | What breaks if wrong | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `UNDERLIES_FEATURE` | OUTGOING | Feature | N:M | No | OPTIONAL | `[PLANNED]` |
| `UNDERLIES_STORY` | OUTGOING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Constraint

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Technical, business, or regulatory constraint
**Implementation Status**: `[IMPLEMENTED]` `domain/Bug.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `constraintId` | String | Yes | Stable identifier | Pattern: `CON-{seq}` |
| `constraintType` | String | Yes | Type of constraint | Enum: TECHNICAL, BUSINESS, REGULATORY, OPERATIONAL |
| `statement` | String | Yes | The constraint | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `CONSTRAINS_FEATURE` | OUTGOING | Feature | N:M | No | OPTIONAL | `[PLANNED]` |
| `CONSTRAINS_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |

---

### SourceReference

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Traceable provenance link to external artifact
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `sourceId` | String | Yes | Stable identifier | Pattern: `SRC-{seq}` |
| `artifactPath` | String | Yes | File path or document reference | |
| `section` | String | No | Section within the artifact | |
| `lineRef` | String | No | Line or range reference | |
| `url` | String | No | External URL | Valid URL format |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_SOURCE` | INCOMING | Screen, UserStory, Bug | N:M | No | OPTIONAL | `[EDGE]` |

---

### Assessment

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Polymorphic evaluation of an assessable T1 target. `assessmentType` is the evaluation lens; `targetKind` is the assessed node kind discriminator used by the Cypher-only `ASSESSES` edge.
**Implementation Status**: `[IMPLEMENTED]` `domain/Assessment.java` + `service/AssessmentService.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `assessmentId` | String | Yes | Stable identifier | Pattern: `ASSESS-{targetKind}-{seq}` |
| `name` | String | Yes | Assessment title | Max 200 chars |
| `assessmentType` | Enum | Yes | Assessment lens | Enum: CAPABILITY, PROCESS, APPLICATION, COMPONENT, SECURITY, DATA |
| `targetKind` | Enum | Yes | Assessed node kind | Enum: CAP, PROC, ACT, APP, CMP, API, DE |
| `assessmentDate` | Date | Yes | Assessment date | |
| `assessor` | String | Yes | Agent or person identifier | |
| `maturityLevel` | Enum | No | Maturity/state level | Enum: NONE, INITIAL, DEVELOPING, DEFINED, MANAGED, OPTIMIZING |
| `currentStateDescription` | String | No | Current-state summary | |
| `targetStateDescription` | String | No | Target-state summary | |
| `score` | Integer | No | Normalized score | 0-100 |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `ASSESSES` | OUTGOING | Assessable T1 node | 1:1 | Yes | BLOCKING | `[CYPHER]` — polymorphic edge resolved through `AssessmentService` |
| `IDENTIFIES_GAP` | OUTGOING | Gap | 1:N | No | OPTIONAL | `[EDGE]` |

---

### Finding

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Review observation, issue, or concern discovered during human analysis
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `findingId` | String | Yes | Stable identifier | Pattern: `FND-{seq}` |
| `findingType` | String | Yes | Classification | Enum: GAP, ISSUE, OBSERVATION, CONCERN |
| `summary` | String | Yes | Short description | Max 500 chars |
| `severity` | String | Yes | Impact severity | Enum: CRITICAL, HIGH, MEDIUM, LOW |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `AFFECTS_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_STORY` | OUTGOING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Bug

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Delivery defect linked to graph artifacts
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `bugId` | String | Yes | Stable identifier | Pattern: `BUG-{seq}` |
| `externalKey` | String | No | External tracker key | e.g., JIRA-1234, AB#5678 |
| `summary` | String | Yes | Short description | Max 500 chars |
| `severity` | String | Yes | Impact severity | Enum: CRITICAL, HIGH, MEDIUM, LOW |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `AFFECTS_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[EDGE]` |
| `AFFECTS_STORY` | OUTGOING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |
| `HAS_SOURCE` | OUTGOING | SourceReference | N:M | No | OPTIONAL | `[EDGE]` |
| `TRACKED_IN_EXTERNAL_SYSTEM` | OUTGOING | ExternalArtifact | N:1 | No | OPTIONAL | `[PLANNED]` |

---

### Risk

**Tier**: 1 (First-Class Node)
**Category**: Strategic & Governance
**Purpose**: Identified risk with probability and impact
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `riskId` | String | Yes | Stable identifier | Pattern: `RSK-{seq}` |
| `title` | String | Yes | Short description | Max 200 chars |
| `probability` | String | Yes | Likelihood | Enum: HIGH, MEDIUM, LOW |
| `impact` | String | Yes | Consequence severity | Enum: CRITICAL, HIGH, MEDIUM, LOW |
| `mitigation` | String | No | Mitigation strategy | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `THREATENS_FEATURE` | OUTGOING | Feature | N:M | No | OPTIONAL | `[PLANNED]` |
| `THREATENS_STORY` | OUTGOING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |

---

### 3.2 Business and Experience (7)

---

### Persona

**Tier**: 1 (First-Class Node)
**Category**: Business and Experience
**Purpose**: User archetype and operational context (19 unique in EMSIST source)
**Implementation Status**: `[IMPLEMENTED]` `domain/Persona.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `personaId` | String | Yes | Stable identifier | Pattern: `PER-{domain}-{seq}` (e.g., PER-UX-001) |
| `name` | String | Yes | Display name | |
| `summary` | String | Yes | Role and context summary | |
| `goals` | List | No | Key goals | |
| `painPoints` | List | No | Key pain points | |
| `roleKeys` | List | No | Associated business roles | List of BusinessRole keys |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `PERFORMS_JOURNEY` | OUTGOING | Journey | 1:N | Yes | BLOCKING | `[PLANNED]` |
| `USES_SCREEN` | OUTGOING | Screen | 1:N | No | OPTIONAL | `[STRING_REF]` — `personaIds` on Screen |
| `AFFECTED_BY_FINDING` | INCOMING | Finding | N:M | No | OPTIONAL | `[PLANNED]` |

---

### BusinessRole

**Tier**: 1 (First-Class Node)
**Category**: Business and Experience
**Purpose**: Domain or business responsibility (split from current `Role.java`)
**Implementation Status**: `[IMPLEMENTED]` `domain/BusinessRole.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `roleKey` | String | Yes | Stable identifier | Pattern: uppercase key (e.g., ADMIN, USER, ARCHITECT) |
| `displayName` | String | Yes | Human-readable name | |
| `roleGroup` | String | No | Grouping category | |
| `scope` | String | No | Scope of authority | |
| `sortOrder` | Integer | No | Display ordering | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `ACTS_IN_JOURNEY` | OUTGOING | Journey | N:M | No | OPTIONAL | `[PLANNED]` |
| `CAN_ACCESS_SCREEN` | INCOMING | Screen | N:M | No | OPTIONAL | `[EDGE]` — Screen `ACCESSIBLE_BY_ROLE` now targets BusinessRole |
| `OWNS_RULE` | OUTGOING | Rule | N:M | No | OPTIONAL | `[PLANNED]` |

---

### ValidationRole

**Tier**: 1 (First-Class Node)
**Category**: Business and Experience
**Purpose**: Role responsible for approval or governance checks (split from current `Role.java`)
**Implementation Status**: `[IMPLEMENTED]` `domain/ValidationRole.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `validationRoleKey` | String | Yes | Stable identifier | Pattern: uppercase key |
| `displayName` | String | Yes | Human-readable name | |
| `scope` | String | No | Scope of validation authority | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `VALIDATES_STORY` | OUTGOING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |
| `VALIDATES_RULE` | OUTGOING | Rule | N:M | No | OPTIONAL | `[PLANNED]` |
| `VALIDATES_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Journey

**Tier**: 1 (First-Class Node)
**Category**: Business and Experience
**Purpose**: End-to-end user goal path
**Implementation Status**: `[IMPLEMENTED]` `domain/Journey.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `journeyId` | String | Yes | Stable identifier | Pattern: `JRN-{module}-{seq}` |
| `title` | String | Yes | Journey name | Max 200 chars |
| `topic` | String | No | Thematic grouping | |
| `module` | String | No | Owning module | |
| `personaId` | String | Yes | Owning persona | Persona ID reference |
| `roleKey` | String | No | Primary role context | BusinessRole key |
| `goalStatement` | String | Yes | What the persona aims to achieve | |
| `designStatus` | String | Yes | Current design status | **Current code**: Enum: NOT_STARTED, IN_PROGRESS, COMPLETE. **Target**: Universal status enum |
| `prototypeStatus` | String | Yes | Current prototype status | Same migration needed |
| `deliveryStatus` | String | Yes | Current delivery status | Same migration needed |
| `readiness` | Object | No | Readiness flags | Applicable: requirementsReady, designReady, qaReady |
| `sourceRefs` | List | No | Provenance links | **Missing from code** |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_STEP` | OUTGOING | JourneyStep | 1:N | Yes | BLOCKING | `[EDGE]` |
| `PERFORMED_BY_PERSONA` | OUTGOING | Persona | N:1 | Yes | BLOCKING | `[EDGE]` — legacy `personaId` retained for migration compatibility |
| `REALIZES` | INCOMING | Epic, Feature, UserStory | N:M | No | OPTIONAL | `[PLANNED]` — four-verb traceability |

---

### JourneyStep

**Tier**: 1 (First-Class Node)
**Category**: Business and Experience
**Purpose**: Ordered step inside a journey
**Implementation Status**: `[IMPLEMENTED]` `domain/JourneyStep.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `stepId` | String | Yes | Stable identifier | Pattern: `{journeyId}.{seq}` |
| `journeyId` | String | Yes | Parent journey | Journey ID reference |
| `orderIndex` | Integer | Yes | Step ordering | >= 0 |
| `label` | String | Yes | Step description | |
| `trigger` | String | No | What initiates this step | |
| `preCondition` | String | No | Required precondition | |
| `postCondition` | String | No | Expected postcondition | |
| `interactionRef` | String | No | Referenced interaction | **Current code**: string. **Target**: edge |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `readiness` | Object | No | Readiness flags | Applicable: requirementsReady, designReady, qaReady |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BELONGS_TO_JOURNEY` | INCOMING | Journey | N:1 | Yes | BLOCKING | `[EDGE]` — via Journey.HAS_STEP |
| `USES_SCREEN` | OUTGOING | Screen | N:1 | Yes | BLOCKING | `[PLANNED]` |
| `EXECUTES_INTERACTION` | OUTGOING | Interaction | N:1 | No | OPTIONAL | `[STRING_REF]` — `interactionRef` |
| `STARTS_AT_TOUCHPOINT` | OUTGOING | Touchpoint | N:M | No | OPTIONAL | `[PLANNED]` |
| `REALIZES` | INCOMING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` — UserStory REALIZES JourneyStep |

---

### Topic

**Tier**: 1 (First-Class Node)
**Category**: Business and Experience
**Purpose**: Thematic grouping for journeys and features
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `topicId` | String | Yes | Stable identifier | Pattern: `TOP-{seq}` |
| `name` | String | Yes | Topic name | |
| `description` | String | No | Description | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `GROUPS_JOURNEY` | OUTGOING | Journey | 1:N | No | OPTIONAL | `[PLANNED]` |
| `GROUPS_FEATURE` | OUTGOING | Feature | 1:N | No | OPTIONAL | `[PLANNED]` |

---

### Touchpoint

**Tier**: 1 (First-Class Node)
**Category**: Business and Experience
**Purpose**: Entry point into a journey or screen
**Implementation Status**: `[IMPLEMENTED]` `domain/Touchpoint.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `touchpointId` | String | Yes | Stable identifier | Pattern: `TP-{surfaceId}-{seq}` |
| `label` | String | Yes | Display label | |
| `surfaceId` | String | Yes | Target screen reference | Screen ID |
| `personaIds` | List | No | Associated personas | **Current code**: string list. **Target**: edge |
| `roleKeys` | List | No | Associated roles | **Current code**: string list. **Target**: edge |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `TARGETS` | OUTGOING | Screen | N:1 | Yes | BLOCKING | `[EDGE]` |
| `HAS_ENTRY_MODE` | OUTGOING | EntryMode (T3) | 1:N | Yes | BLOCKING | `[EDGE]` |
| `DELIVERED_VIA_CHANNEL` | OUTGOING | Channel (T2) | N:M | Yes | BLOCKING | `[EDGE]` — backfilled from `EntryMode.channelId` |
| `USED_BY_PERSONA` | OUTGOING | Persona | N:M | No | OPTIONAL | `[EDGE]` — legacy `personaIds` retained for migration compatibility |

---

### 3.3 Delivery & Execution (7)

---

### RequirementPortfolio

**Tier**: 1 (First-Class Node)
**Category**: Delivery & Execution
**Purpose**: Backlog container that owns the Epic → Feature → UserStory hierarchy for a single ProjectInstance.
**Implementation Status**: `[IMPLEMENTED]` `domain/RequirementPortfolio.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `portfolioId` | String | Yes | Stable identifier | Pattern: `PORT-{projectCode}-{seq}` |
| `name` | String | Yes | Portfolio name | Max 200 chars |
| `description` | String | No | Portfolio scope summary | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_EPIC` | OUTGOING | Epic | 1:N | Yes | BLOCKING | `[EDGE]` |
| `HAS_PORTFOLIO` | INCOMING | ProjectInstance | 1:1 | Yes | BLOCKING | `[EDGE]` |

---

### Epic

**Tier**: 1 (First-Class Node)
**Category**: Delivery & Execution
**Purpose**: Key hierarchy level between BusinessObjective and Feature. Enables traceability from strategic intent to delivery.
**Implementation Status**: `[IMPLEMENTED]` `domain/Epic.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `epicId` | String | Yes | Stable identifier | Pattern: `EPC-{seq}` |
| `title` | String | Yes | Epic title | Max 200 chars |
| `description` | String | No | Full description | |
| `owner` | String | No | Epic owner | |
| `priority` | String | No | Priority level | Enum: CRITICAL, HIGH, MEDIUM, LOW |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_FEATURE` | OUTGOING | Feature | 1:N | Yes | BLOCKING | `[EDGE]` |
| `REALIZED_BY` | INCOMING | BusinessObjective | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS` | OUTGOING | Application | 1:N | No | OPTIONAL | `[PLANNED]` |

---

### Feature

**Tier**: 1 (First-Class Node)
**Category**: Delivery & Execution
**Purpose**: Cohesive delivery capability grouping
**Implementation Status**: `[IMPLEMENTED]` `domain/Feature.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `featureId` | String | Yes | Stable identifier | Pattern: `FEAT-{module}-{seq}` |
| `title` | String | Yes | Short descriptive title | Max 200 chars |
| `module` | String | Yes | Owning module | |
| `scope` | String | No | Scope description | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BELONGS_TO_OBJECTIVE` | OUTGOING | BusinessObjective | N:1 | Yes | BLOCKING | `[PLANNED]` |
| `HAS_STORY` | OUTGOING | UserStory | 1:N | Yes | BLOCKING | `[EDGE]` |
| `HAS_SCREEN` | OUTGOING | Screen | 1:N | No | OPTIONAL | `[PLANNED]` |

---

### UserStory

**Tier**: 1 (First-Class Node)
**Category**: Delivery & Execution
**Purpose**: Deliverable requirement unit
**Implementation Status**: `[IMPLEMENTED]` `domain/UserStory.java` — minimal attribute set, but the delivery spine is now edge-backed

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `storyId` | String | Yes | Stable identifier | Pattern: `US-{domain}-{seq}` (e.g., US-DM-001) |
| `title` | String | Yes | Story title | Max 200 chars. **Current code**: `label` |
| `module` | String | Yes | Owning module | |
| `domain` | String | No | Business domain | **Current code**: exists |
| `storyNumber` | Integer | No | Sequential number | **Current code**: exists |
| `storyType` | String | No | Classification | Enum: FUNCTIONAL, NON_FUNCTIONAL, ENABLER, SPIKE |
| `originType` | String | No | Where the story originates | Enum: PROCESS, EXPERIENCE, TECHNICAL, CROSS_CUTTING |
| `natureType` | String | No | Functional classification | Enum: FUNCTIONAL, NON_FUNCTIONAL |
| `description` | String | No | Full description | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `readiness` | Object | No | Readiness flags | All 7 flags applicable |
| `sourceRefs` | List | No | Provenance links | |
| `executionMode` | Enum | No | How the story will be implemented | Enum: HUMAN_ONLY, AGENT_ASSISTED, AGENT_FIRST. Default: HUMAN_ONLY |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_STORY` | INCOMING | Feature | N:1 | Yes | BLOCKING | `[EDGE]` — delivery spine: Feature HAS_STORY UserStory |
| `USES_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[STRING_REF]` — `storyRefs` on Screen (reverse) |
| `REQUIRES_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |
| `GOVERNED_BY_RULE` | OUTGOING | Rule | N:M | No | OPTIONAL | `[EDGE]` |
| `HAS_CRITERION` | OUTGOING | AcceptanceCriterion | 1:N | Yes | BLOCKING | `[EDGE]` |
| `REALIZES` | OUTGOING | ProcessActivity, JourneyStep | N:M | No | OPTIONAL | `[PLANNED]` |
| `DELIVERS` | OUTGOING | Screen, ApiContract, DataEntity, Rule, Message | N:M | No | OPTIONAL | `[PLANNED]` |
| `HAS_TASK` | OUTGOING | Task | 1:N | No | OPTIONAL | `[EDGE]` |
| `VERIFIED_BY` | OUTGOING | TestCase | 1:N | Yes | BLOCKING | `[PLANNED]` |

---

### Milestone

**Tier**: 1 (First-Class Node)
**Category**: Delivery & Execution
**Purpose**: Project timebox or checkpoint. Sprint is modeled as `milestoneType = SPRINT`, not as a separate node.
**Implementation Status**: `[IMPLEMENTED]` `domain/Milestone.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `milestoneId` | String | Yes | Stable identifier | Pattern: `MS-{projectCode}-{seq}` |
| `name` | String | Yes | Milestone name | Max 200 chars |
| `description` | String | No | Milestone summary | |
| `milestoneType` | Enum | Yes | Planning checkpoint type | Enum: SPRINT, PHASE, RELEASE_CUT, CHECKPOINT |
| `startDate` | Date | No | Planned start | |
| `endDate` | Date | No | Planned end | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_MILESTONE` | INCOMING | ProjectInstance | N:1 | Yes | BLOCKING | `[EDGE]` |
| `HAS_TASK` | OUTGOING | Task | 1:N | No | OPTIONAL | `[EDGE]` |

---

### Task

**Tier**: 1 (First-Class Node)
**Category**: Delivery & Execution
**Purpose**: Atomic unit of work assigned to an individual or team, decomposed from a UserStory
**Implementation Status**: `[IMPLEMENTED]` `domain/Task.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `taskId` | String | Yes | Stable identifier | Pattern: `TSK-{module}-{seq}` |
| `title` | String | Yes | Task title | Max 200 chars |
| `description` | String | No | Full description | |
| `taskType` | String | Yes | Classification | Enum: FRONTEND, BACKEND, API, DATA, TEST, DEVOPS, UX, DOCUMENTATION |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED, IN_DEFINITION, DEFINED, IN_REVIEW, APPROVED, IN_IMPLEMENTATION, IMPLEMENTED, VERIFIED, DEPRECATED, RETIRED |
| `priority` | String | No | Priority level | Enum: CRITICAL, HIGH, MEDIUM, LOW |
| `estimate` | String | No | Effort estimate | |
| `actualEffort` | String | No | Actual effort spent | |
| `assigneeName` | String | No | Assigned individual | Temporary until Person T1 |
| `teamName` | String | No | Assigned team | Temporary until team modeled |
| `dueDate` | Date | No | Target completion date | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_TASK` | INCOMING | UserStory | N:1 | Yes | BLOCKING | `[EDGE]` |
| `IMPLEMENTS` | OUTGOING | Screen, ApiContract, DataEntity, Rule, Message, TestCase, ApplicationComponent | N:M | No | OPTIONAL | `[PLANNED]` |
| `DEPENDS_ON` | OUTGOING | Task | N:M | No | OPTIONAL | `[PLANNED]` |
| `ASSIGNED_TO` | OUTGOING | Organization | N:1 | No | OPTIONAL | `[PLANNED]` |

---

### ProjectInstance

**Tier**: 1 (First-Class Node)
**Category**: Delivery & Execution
**Purpose**: Temporary delivery container that bridges assessed gaps and capability targets to scoped project work across backlog, milestones, tasks, applications, and components.
**Implementation Status**: `[IMPLEMENTED]` `domain/ProjectInstance.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `projectId` | String | Yes | Stable identifier | Pattern: `PROJ-{code}-{seq}` |
| `name` | String | Yes | Project name | Max 200 chars |
| `description` | String | No | Project summary | |
| `projectType` | String | No | Delivery mode | Enum: GREENFIELD, ENHANCEMENT, MIGRATION, INTEGRATION |
| `startDate` | Date | No | Planned start | |
| `targetDate` | Date | No | Planned target completion | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `TARGETS_CAPABILITY` | OUTGOING | BusinessCapability | 1:N | Yes | BLOCKING | `[EDGE]` |
| `ADDRESSES_GAP` | OUTGOING | Gap | 1:N | Yes | BLOCKING | `[EDGE]` |
| `HAS_PORTFOLIO` | OUTGOING | RequirementPortfolio | 1:1 | Yes | BLOCKING | `[EDGE]` |
| `HAS_TASK` | OUTGOING | Task | 1:N | Yes | BLOCKING | `[EDGE]` |
| `HAS_MILESTONE` | OUTGOING | Milestone | 1:N | No | OPTIONAL | `[EDGE]` |
| `CREATES_APPLICATION` | OUTGOING | Application | 1:N | No | OPTIONAL | `[EDGE]` |
| `ENHANCES_APPLICATION` | OUTGOING | Application | 1:N | No | OPTIONAL | `[EDGE]` |
| `INTEGRATES_WITH` | OUTGOING | Application | 1:N | No | OPTIONAL | `[EDGE]` |
| `CREATES_COMPONENT` | OUTGOING | ApplicationComponent | 1:N | No | OPTIONAL | `[EDGE]` |
| `ENHANCES_COMPONENT` | OUTGOING | ApplicationComponent | 1:N | No | OPTIONAL | `[EDGE]` |

---

### 3.4 Requirement & Design (10)

---

### AcceptanceCriterion

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Verifiable story condition
**Implementation Status**: `[IMPLEMENTED]` `domain/AcceptanceCriterion.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `criterionId` | String | Yes | Stable identifier | Pattern: `AC-{storyId}-{seq}` |
| `storyId` | String | Yes | Parent story | UserStory ID |
| `statement` | String | Yes | Verifiable condition | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BELONGS_TO_STORY` | INCOMING | UserStory | N:1 | Yes | BLOCKING | `[EDGE]` |
| `VERIFIED_BY` | OUTGOING | TestCase | 1:N | No | OPTIONAL | `[PLANNED]` — consistent with four-verb model |

---

### Rule

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Business rule or domain rule
**Implementation Status**: `[IMPLEMENTED]` `domain/Rule.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `ruleId` | String | Yes | Stable identifier | Pattern: `RULE-{seq}` |
| `ruleType` | String | Yes | Classification | Enum: BUSINESS, DOMAIN, REGULATORY, OPERATIONAL |
| `statement` | String | Yes | Rule description | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `GOVERNED_BY_RULE` | INCOMING | UserStory | N:M | No | OPTIONAL | `[EDGE]` |
| `GOVERNS_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[PLANNED]` |
| `HAS_VALIDATION_RULE` | OUTGOING | ValidationRule | 1:N | No | OPTIONAL | `[EDGE]` |

---

### ValidationRule

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Explicit validation behavior
**Implementation Status**: `[IMPLEMENTED]` `domain/ValidationRule.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `validationId` | String | Yes | Stable identifier | Pattern: `VAL-{seq}` |
| `scopeType` | String | Yes | What is validated | Enum: SCREEN, API, DATA_FIELD |
| `scopeRef` | String | Yes | Reference to validated object | |
| `condition` | String | Yes | Validation condition | |
| `messageCode` | String | No | Error message code | ErrorCode reference |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `ENFORCES_VALIDATION` | INCOMING | Screen | N:M | No | OPTIONAL | `[EDGE]` |
| `HAS_VALIDATION_RULE` | INCOMING | Rule | N:1 | No | OPTIONAL | `[EDGE]` |
| `REFERENCES_ERROR_CODE` | OUTGOING | ErrorCode (T2) | N:1 | No | OPTIONAL | `[PLANNED]` |

---

### QualityConstraint

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Artifact-bound non-functional requirement with measurable threshold. Bound to Screen, ApiContract, DataEntity, or ApplicationComponent. Verified via SATISFIED_BY → TestCase (distinct from VERIFIED_BY which proves functional correctness).
**Implementation Status**: `[IMPLEMENTED]` `domain/QualityConstraint.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `constraintId` | String | Yes | Stable identifier | Pattern: `QC-{artifact}-{seq}` |
| `name` | String | Yes | Short label | e.g., "Page load time" |
| `constraintType` | String | Yes | Quality dimension | Enum: PERFORMANCE, ACCESSIBILITY, SECURITY, RELIABILITY, USABILITY |
| `metric` | String | Yes | Measurable metric | e.g., "LCP", "WCAG level", "p99 latency" |
| `threshold` | String | Yes | Pass/fail threshold | e.g., "< 2000ms", "AAA", "< 500ms" |
| `measurementMethod` | String | No | How to measure | e.g., "Lighthouse", "axe-core", "k6 load test" |
| `priority` | String | No | Priority level | Enum: CRITICAL, HIGH, MEDIUM, LOW |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_QUALITY_CONSTRAINT` | INCOMING | Screen, ApiContract, DataEntity, ApplicationComponent | N:M | Yes | BLOCKING | `[EDGE]` |
| `SATISFIED_BY` | OUTGOING | TestCase | N:M | No | OPTIONAL | `[EDGE]` |

---

### EdgeCase

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Non-happy-path scenario
**Implementation Status**: `[IMPLEMENTED]` `domain/TestCase.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `edgeCaseId` | String | Yes | Stable identifier | Pattern: `EDGE-{seq}` |
| `context` | String | Yes | When this occurs | |
| `behavior` | String | Yes | Expected behavior | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `AFFECTS_STORY` | OUTGOING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_JOURNEY_STEP` | OUTGOING | JourneyStep | N:M | No | OPTIONAL | `[PLANNED]` |

---

### ExceptionCase

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Failure or exceptional path
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `exceptionId` | String | Yes | Stable identifier | Pattern: `EXC-{seq}` |
| `context` | String | Yes | When this occurs | |
| `behavior` | String | Yes | Expected behavior | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `AFFECTS_INTERACTION` | OUTGOING | Interaction | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |
| `AFFECTS_JOURNEY_STEP` | OUTGOING | JourneyStep | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Screen

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Navigable UI surface or always-present surface
**Implementation Status**: `[IMPLEMENTED]` `domain/Screen.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `surfaceId` | String | Yes | Stable identifier | Pattern: `SURF-{module}-{seq}` |
| `label` | String | Yes | Display label | |
| `module` | String | Yes | Owning module | |
| `routePath` | String | No | Frontend route path | |
| `screenType` | String | No | Surface classification | Enum: PAGE, GLOBAL, MODAL, DRAWER, OVERLAY |
| `designStatus` | String | Yes | Current design status | **Current code**: NOT_STARTED, IN_PROGRESS, COMPLETE. **Target**: Universal status |
| `prototypeStatus` | String | Yes | Prototype status | Same migration |
| `deliveryStatus` | String | Yes | Delivery status | Same migration |
| `wcag` | Boolean | No | WCAG compliance flag | |
| `responsive` | Boolean | No | Responsive design flag | |
| `roleAdaptive` | Boolean | No | Role-adaptive behavior | |
| `deepLinkable` | Boolean | No | Deep-linkable | |
| `loadingStates` | Boolean | No | Has loading states | |
| `messageRegistryCount` | Integer | No | Count of registered messages | |
| `notes` | String | No | Additional notes | |
| `readiness` | Object | No | Readiness flags | Applicable: requirementsReady, designReady, frontendReady, integrationReady, qaReady |
| `sourceRefs` | List | No | Provenance links | **Missing from code** |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_INTERACTION` | OUTGOING | Interaction | 1:N | Yes | BLOCKING | `[EDGE]` — replaces deprecated ON_SCREEN (direction reversed: Screen→Interaction) |
| `DELIVERS` | INCOMING | UserStory | N:M | Yes | BLOCKING | `[STRING_REF]` — `storyRefs` (UserStory DELIVERS Screen) |
| `ACCESSIBLE_BY_ROLE` | OUTGOING | BusinessRole | N:M | No | OPTIONAL | `[EDGE]` — legacy `roleKeys` retained for migration compatibility |
| `USED_BY_PERSONA` | OUTGOING | Persona | N:M | No | OPTIONAL | `[EDGE]` — legacy `personaIds` retained for migration compatibility |
| `HAS_MESSAGE` | OUTGOING | Message | 1:N | No | OPTIONAL | `[EDGE]` |
| `HAS_GAP` | OUTGOING | Gap | 1:N | No | OPTIONAL | `[EDGE]` |
| `HAS_CONTENT` | OUTGOING | ContentElement (T3) | 1:N | No | OPTIONAL | `[EDGE]` |
| `TRANSITIONS_TO` | OUTGOING | Screen | N:M | No | OPTIONAL | `[EDGE]` |
| `CAN_PRODUCE_ERROR` | OUTGOING | ErrorCode (T2) | N:M | No | OPTIONAL | `[EDGE]` |
| `HAS_SOURCE` | OUTGOING | SourceReference | N:M | No | OPTIONAL | `[EDGE]` |

---

### ScreenState

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Distinct state of a screen
**Implementation Status**: `[IMPLEMENTED]` `domain/ScreenState.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `stateId` | String | Yes | Stable identifier | Pattern: `STATE-{surfaceId}-{seq}` |
| `surfaceId` | String | Yes | Parent screen | Screen ID |
| `stateType` | String | Yes | Classification | Enum: DEFAULT, LOADING, EMPTY, ERROR, SUCCESS, ROLE_VARIANT |
| `description` | String | No | State description | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BELONGS_TO_SCREEN` | OUTGOING | Screen | N:1 | Yes | BLOCKING | `[EDGE]` |
| `TRIGGERED_BY_INTERACTION` | INCOMING | Interaction | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Interaction

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: User or system action on a surface
**Implementation Status**: `[IMPLEMENTED]` `domain/Interaction.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `interactionId` | String | Yes | Stable identifier | Pattern: `INT-{surfaceId}-{seq}` |
| `surfaceId` | String | Yes | Parent screen | Screen ID |
| `element` | String | Yes | UI element acted upon | |
| `trigger` | String | Yes | What initiates the interaction | Enum: click, type, drag-drop, hover, scroll, keyboard-shortcut, system, timer, 401-response, sse-event, jwt-expiry |
| `permission` | String | No | Required permission | **Current code**: string. **Target**: edge to Permission (T2) |
| `confirmationCode` | String | No | Confirmation dialog code | **Current code**: string. **Target**: edge to ConfirmationDialog (T2) |
| `personaIds` | List | No | Associated personas | **Current code**: string list. **Target**: edge |
| `roleKeys` | List | No | Associated roles | **Current code**: string list. **Target**: edge |
| `apiCalls` | List | No | Called APIs | **Current code**: string list. **Target**: edge to ApiContract |
| `outcomeSuccess` | String | No | Success outcome text | |
| `outcomeError` | String | No | Error outcome text | |
| `outcomeLoading` | String | No | Loading outcome text | |
| `errorCodeRef` | String | No | Referenced error code | Legacy compatibility field retained alongside `ON_ERROR_SHOWS` |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `readiness` | Object | No | Readiness flags | All 7 flags applicable |
| `sourceRefs` | List | No | Provenance links | **Missing from code** |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_INTERACTION` | INCOMING | Screen | N:1 | Yes | BLOCKING | `[EDGE]` — Screen HAS_INTERACTION Interaction (replaces deprecated ON_SCREEN) |
| `HAS_EFFECT` | OUTGOING | Effect (T3) | 1:N | No | OPTIONAL | `[EDGE]` |
| `REQUIRES_PERMISSION` | OUTGOING | Permission (T2) | N:1 | No | OPTIONAL | `[EDGE]` — legacy `permission` retained for migration compatibility |
| `TRIGGERS_CONFIRMATION` | OUTGOING | ConfirmationDialog (T2) | N:1 | No | OPTIONAL | `[EDGE]` — legacy `confirmationCode` source field retained |
| `CALLS_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[EDGE]` — legacy `apiCalls` source field retained |
| `ON_ERROR_SHOWS` | OUTGOING | ErrorCode (T2) | N:M | No | OPTIONAL | `[EDGE]` |

---

### Transition

**Tier**: 1 (First-Class Node)
**Category**: Requirement & Design
**Purpose**: Screen-to-screen or state transition
**Implementation Status**: `[IMPLEMENTED]` `domain/Transition.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `transitionId` | String | Yes | Stable identifier | Pattern: `TRN-{from}-{to}` |
| `name` | String | Yes | Transition name | |
| `description` | String | No | Transition description | |
| `transitionType` | String | Yes | Transition kind | Enum: NAVIGATION, MODAL_OPEN, MODAL_CLOSE, TAB_SWITCH, REDIRECT |
| `guard` | String | No | Condition that allows the transition | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `FROM_SCREEN` | OUTGOING | Screen | N:1 | Yes | BLOCKING | `[EDGE]` |
| `TO_SCREEN` | OUTGOING | Screen | N:1 | Yes | BLOCKING | `[EDGE]` |
| `CAUSED_BY_INTERACTION` | OUTGOING | Interaction | N:1 | No | OPTIONAL | `[EDGE]` |

---

### 3.5 Engineering (9)

---

### ApiContract

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Backend contract needed by a story or interaction
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `apiId` | String | Yes | Stable identifier | Pattern: `API-{service}-{seq}` |
| `method` | String | Yes | HTTP method | Enum: GET, POST, PUT, PATCH, DELETE |
| `path` | String | Yes | Endpoint path | |
| `authModel` | String | No | Authentication model | Enum: BEARER, API_KEY, NONE |
| `requestSchemaRef` | String | No | Request schema reference | RequestSchema ID |
| `responseSchemaRef` | String | No | Response schema reference | ResponseSchema ID |
| `errorContractRef` | String | No | Error contract reference | ErrorContract ID |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `readiness` | Object | No | Readiness flags | Applicable: requirementsReady, contractReady, backendReady, integrationReady, qaReady |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `REQUIRED_BY_STORY` | INCOMING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |
| `CALLS_API` | INCOMING | Interaction | N:M | No | OPTIONAL | `[EDGE]` — legacy `apiCalls` source field retained |
| `USES_DATA_ENTITY` | OUTGOING | DataEntity | N:M | No | OPTIONAL | `[PLANNED]` |
| `HAS_REQUEST` | OUTGOING | RequestSchema | 1:1 | No | OPTIONAL | `[EDGE]` |
| `HAS_RESPONSE` | OUTGOING | ResponseSchema | 1:1 | No | OPTIONAL | `[EDGE]` |
| `HAS_ERROR` | OUTGOING | ErrorContract | 1:1 | No | OPTIONAL | `[EDGE]` |

---

### RequestSchema

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Input schema for an API contract
**Implementation Status**: `[IMPLEMENTED]` `domain/RequestSchema.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `schemaId` | String | Yes | Stable identifier | Pattern: `REQSCH-{apiId}` |
| `contentType` | String | Yes | Media type | Enum: application/json, multipart/form-data |
| `fields` | List | Yes | Schema fields | List of field definitions |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_REQUEST` | INCOMING | ApiContract | 1:1 | Yes | BLOCKING | `[EDGE]` |

---

### ResponseSchema

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Output schema for an API contract
**Implementation Status**: `[IMPLEMENTED]` `domain/ResponseSchema.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `schemaId` | String | Yes | Stable identifier | Pattern: `RESSCH-{apiId}` |
| `contentType` | String | Yes | Media type | Enum: application/json |
| `fields` | List | Yes | Schema fields | List of field definitions |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_RESPONSE` | INCOMING | ApiContract | 1:1 | Yes | BLOCKING | `[EDGE]` |

---

### ErrorContract

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Error response structure for an API contract
**Implementation Status**: `[IMPLEMENTED]` `domain/ErrorContract.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `errorContractId` | String | Yes | Stable identifier | Pattern: `ERRCON-{apiId}` |
| `errorCodes` | List | Yes | Applicable error codes | List of ErrorCode references |
| `responseShape` | String | No | Error response format | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_ERROR` | INCOMING | ApiContract | 1:1 | Yes | BLOCKING | `[EDGE]` |
| `REFERENCES_ERROR_CODE` | OUTGOING | ErrorCode (T2) | 1:N | No | OPTIONAL | `[PLANNED]` |

---

### DataEntity

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Domain data object
**Implementation Status**: `[IMPLEMENTED]` `domain/DataEntity.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `entityId` | String | Yes | Stable identifier | Pattern: `ENT-{seq}` |
| `name` | String | Yes | Entity name | |
| `ownership` | String | No | Owning service or bounded context | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `readiness` | Object | No | Readiness flags | Applicable: requirementsReady, contractReady, backendReady, integrationReady, qaReady |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `USED_BY_API` | INCOMING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |
| `USED_BY_STORY` | INCOMING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |
| `HAS_FIELD` | OUTGOING | DataField | 1:N | Yes | BLOCKING | `[EDGE]` |

---

### DataField

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Field inside a data entity
**Implementation Status**: `[IMPLEMENTED]` `domain/DataField.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `fieldId` | String | Yes | Stable identifier | Pattern: `FLD-{entityId}-{seq}` |
| `entityId` | String | Yes | Parent entity | DataEntity ID |
| `name` | String | Yes | Field name | |
| `dataType` | String | Yes | Data type | Enum: STRING, INTEGER, FLOAT, BOOLEAN, DATE, DATETIME, ENUM, OBJECT, ARRAY |
| `required` | Boolean | Yes | Whether field is required | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_FIELD` | INCOMING | DataEntity | N:1 | Yes | BLOCKING | `[EDGE]` |
| `VALIDATED_BY_RULE` | OUTGOING | ValidationRule | N:M | No | OPTIONAL | `[PLANNED]` |
| `USES_ENUM` | OUTGOING | Enum (T2) | N:1 | No | OPTIONAL | `[PLANNED]` |

---

### Integration

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Cross-system or cross-service integration point
**Implementation Status**: `[IMPLEMENTED]` `domain/TestCase.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `integrationId` | String | Yes | Stable identifier | Pattern: `INTG-{seq}` |
| `name` | String | Yes | Integration name | |
| `integrationType` | String | Yes | Classification | Enum: REST, GRAPHQL, GRPC, KAFKA, WEBHOOK, SSE |
| `sourceSystem` | String | Yes | Source system | |
| `targetSystem` | String | Yes | Target system | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `USES_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |
| `FIRES_EVENT` | OUTGOING | Event (T2) | N:M | No | OPTIONAL | `[PLANNED]` |

---

### TestCase

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: Verifiable test scenario linked to acceptance criteria
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `testCaseId` | String | Yes | Stable identifier | Pattern: `TC-{seq}` |
| `title` | String | Yes | Test description | |
| `testType` | String | Yes | Classification | Enum: UNIT, INTEGRATION, E2E, VISUAL, ACCESSIBILITY, PERFORMANCE |
| `preconditions` | String | No | Required preconditions | |
| `steps` | List | No | Test steps | |
| `expectedResult` | String | Yes | Expected outcome | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `VERIFIES_CRITERION` | OUTGOING | AcceptanceCriterion | N:M | No | OPTIONAL | `[PLANNED]` |
| `TESTS_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[PLANNED]` |
| `TESTS_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[PLANNED]` |
| `LOCATED_IN` | OUTGOING | CodeAsset | N:1 | No | OPTIONAL | `[EDGE]` |

**Agent-ready enrichment (7 attributes):** TestCase is enriched with execution metadata to enable agents to locate and run verification tests. These attributes are added by the agent-ready information model spec:

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `testFilePath` | String | No | Relative file path from component modulePath | |
| `testClassName` | String | No | Fully qualified class or describe block | |
| `testMethodName` | String | No | Method or it() block | |
| `testFramework` | String | No | Test framework | Enum: JUNIT5, VITEST, PLAYWRIGHT, JEST, CYPRESS |
| `suiteName` | String | No | Logical grouping | |
| `tags` | List | No | Categorization tags | |
| `testCommand` | String | No | Override command to run this test | Fallback: `ApplicationComponent.testCommand` |

---

### CodeAsset

**Tier**: 1 (First-Class Node)
**Category**: Engineering
**Purpose**: File-level code targeting for agent-safe implementation. Curated subset of repo files that are explicit targets of stories, tasks, or tests. Not an exhaustive model of every file in the repo.
**Implementation Status**: `[IMPLEMENTED]` `domain/CodeAsset.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `assetId` | String | Yes | Stable identifier | Pattern: `CA-{component}-{seq}` |
| `filePath` | String | Yes | Relative file path from component modulePath | Must resolve: `Application.repoPath + ApplicationComponent.modulePath + CodeAsset.filePath` |
| `fileType` | String | Yes | Source classification | Enum: SOURCE, TEST, CONFIG, MIGRATION, TEMPLATE, STYLE, SPEC |
| `language` | String | No | Programming language | e.g., JAVA, TYPESCRIPT, SCSS, SQL |
| `className` | String | No | Primary class or export name | |
| `moduleName` | String | No | Logical module grouping | |
| `layerTag` | String | No | Architecture layer | Enum: DOMAIN, SERVICE, CONTROLLER, DTO, CONFIG, COMPONENT, DIRECTIVE, PIPE, STORE, TEST, MIGRATION |
| `description` | String | No | Purpose note | |
| `lastKnownHash` | String | No | Git blob SHA for drift detection | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_CODE_ASSET` | INCOMING | ApplicationComponent | N:1 | Yes | BLOCKING | `[EDGE]` |
| `ASSET_FOR_SCREEN` | OUTGOING | Screen | N:M | No | OPTIONAL | `[EDGE]` |
| `ASSET_FOR_API` | OUTGOING | ApiContract | N:M | No | OPTIONAL | `[EDGE]` |
| `ASSET_FOR_ENTITY` | OUTGOING | DataEntity | N:M | No | OPTIONAL | `[EDGE]` |
| `ASSET_FOR_RULE` | OUTGOING | Rule | N:M | No | OPTIONAL | `[EDGE]` |
| `LOCATED_IN` | INCOMING | TestCase | N:1 | No | OPTIONAL | `[EDGE]` |
| `IMPLEMENTS` | INCOMING | Task | N:M | No | OPTIONAL | `[PLANNED]` |

---

### 3.6 Cross-cutting (4)

---

### ExternalArtifact

**Tier**: 1 (First-Class Node)
**Category**: Cross-cutting
**Purpose**: Synced or linked record from Azure DevOps, Jira, or other tools
**Implementation Status**: `[IMPLEMENTED]` `domain/ExternalArtifact.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `externalId` | String | Yes | Stable identifier | Pattern: `EXT-{system}-{seq}` |
| `system` | String | Yes | Source system | Enum: AZURE_DEVOPS, JIRA, GITHUB |
| `externalType` | String | Yes | Item type in source system | |
| `key` | String | Yes | External key | e.g., AB#1234, PROJ-567 |
| `url` | String | No | External URL | Valid URL |
| `syncStatus` | String | No | Synchronization state | Enum: SYNCED, STALE, CONFLICT |
| `lastSyncedAt` | DateTime | No | Last sync timestamp | ISO 8601 |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `REPRESENTS_STORY` | OUTGOING | UserStory | N:1 | No | OPTIONAL | `[EDGE]` |
| `REPRESENTS_BUG` | OUTGOING | Bug | N:1 | No | OPTIONAL | `[EDGE]` |
| `LINKS_TO_OBJECT` | OUTGOING | Any Tier 1 | N:M | No | OPTIONAL | `[PLANNED]` |

---

### OpenQuestion

**Tier**: 1 (First-Class Node)
**Category**: Cross-cutting
**Purpose**: Unresolved question blocking design or implementation
**Implementation Status**: `[IMPLEMENTED]` `domain/Application.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `questionId` | String | Yes | Stable identifier | Pattern: `OQ-{seq}` |
| `question` | String | Yes | The question | |
| `context` | String | No | Why this matters | |
| `resolution` | String | No | Answer when resolved | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BLOCKS_ARTIFACT` | OUTGOING | Any Tier 1 | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Gap

**Tier**: 1 (First-Class Node)
**Category**: Cross-cutting
**Purpose**: Structural incompleteness detectable by benchmark engine
**Implementation Status**: `[IMPLEMENTED — reshape required]` `domain/Gap.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `gapId` | String | Yes | Stable identifier | Pattern: `GAP-{seq}`. **Current code**: uses Long `id` |
| `gapType` | String | Yes | Classification | Enum: MISSING_ARTIFACT, MISSING_RELATIONSHIP, MISSING_ATTRIBUTE, MISSING_RULE. **Current code**: `type` (free-form) |
| `severity` | String | Yes | Impact severity | Enum: CRITICAL, HIGH, MEDIUM, LOW |
| `description` | String | Yes | Gap description | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | **Missing from code** |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BLOCKS_ARTIFACT` | OUTGOING | Any Tier 1 | N:M | No | OPTIONAL | `[PLANNED]` |
| — | — | — | — | — | — | `detectedBy` property on Gap replaces deprecated DETECTED_BY_BENCHMARK edge |
| `BELONGS_TO_SCREEN` | INCOMING | Screen | N:1 | No | OPTIONAL | `[EDGE]` — via Screen.HAS_GAP |

---

### Message

**Tier**: 1 (First-Class Node)
**Category**: Cross-cutting
**Purpose**: User-visible confirmation, warning, info, or error text
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `messageId` | String | Yes | Stable identifier | Pattern: `MSG-{seq}` |
| `messageType` | String | Yes | Classification | Enum: INFO, WARNING, SUCCESS, VALIDATION, ERROR |
| `code` | String | No | Message code | |
| `text` | String | Yes | Display text | Should reference TranslationKey |
| `triggerCondition` | String | No | When this appears | |
| `status` | String | Yes | Lifecycle status | Universal status enum |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_MESSAGE` | INCOMING | Screen | N:M | No | OPTIONAL | `[EDGE]` |
| `TRIGGERED_BY_INTERACTION` | INCOMING | Interaction | N:M | No | OPTIONAL | `[PLANNED]` |
| `BACKED_BY_VALIDATION` | OUTGOING | ValidationRule | N:1 | No | OPTIONAL | `[PLANNED]` |

---

### 3.7 Architecture & EA (12)

---

### BusinessCapability

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Stable functional classification of what the business does ("Onboarding", "KYC", "Compliance"). Not time-bound like BusinessObjective.
**Implementation Status**: `[IMPLEMENTED]` `domain/BusinessCapability.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `capabilityId` | String | Yes | Stable identifier | Pattern: `CAP-{domain}-{seq}` |
| `name` | String | Yes | Capability name | Max 200 chars |
| `domain` | String | Yes | Business domain grouping | |
| `description` | String | No | Full description | |
| `maturityLevel` | String | No | Current maturity assessment | Enum: INITIAL, DEVELOPING, DEFINED, MANAGED, OPTIMIZED |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_CAPABILITY` | INCOMING | BusinessDomain (T1) | N:1 | No | OPTIONAL | `[EDGE]` |
| `REALIZED_BY_PROCESS` | OUTGOING | BusinessProcess | 1:N | No | OPTIONAL | `[EDGE]` |
| `ENABLED_BY` | OUTGOING | Application | 1:N | Yes | BLOCKING | `[PLANNED]` |
| `REALIZES` | INCOMING | Feature | N:M | No | OPTIONAL | `[PLANNED]` — Feature REALIZES BusinessCapability |
| `REQUIRES_CAPABILITY` | INCOMING | BusinessObjective | N:M | No | OPTIONAL | `[PLANNED]` — BusinessObjective REQUIRES_CAPABILITY |

---

### BusinessProcess

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Formal process model (BPMN-aligned). NOT the same as Journey — Journey is UX-focused, BusinessProcess is operations-focused.
**Implementation Status**: `[IMPLEMENTED]` `domain/BusinessProcess.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `processId` | String | Yes | Stable identifier | Pattern: `BPR-{domain}-{seq}` |
| `name` | String | Yes | Process name | Max 200 chars |
| `description` | String | No | Full description | |
| `processType` | String | No | Process classification | Enum: CORE, SUPPORT, MANAGEMENT |
| `owner` | String | No | Process owner | |
| `diagramFormat` | String | No | Source diagram format | Enum: BPMN, CMMN, DMN, FREEFORM |
| `diagramPath` | String | No | File path to the diagram source | e.g., `docs/bpmn/onboarding.bpmn` |
| `diagramVersion` | String | No | Diagram file version | Semantic version or commit hash |
| `diagramSource` | String | No | Tooling origin | Enum: CAMUNDA, SIGNAVIO, LUCIDCHART, DRAW_IO, MANUAL |
| `isExecutableModel` | Boolean | No | Whether the BPMN model is engine-executable | Default: false |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

> **Critical rule:** If `diagramPath` is set but the process has zero `HAS_FLOW_NODE` edges, the benchmark engine MUST flag the process as `GAP: DIAGRAM_WITHOUT_GRAPH` — indicating the BPMN was imported as a file reference but its internal structure has not been decomposed into graph nodes.

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `REALIZED_BY_PROCESS` | INCOMING | BusinessCapability | N:1 | Yes | BLOCKING | `[EDGE]` |
| `SUPPORTED_BY` | OUTGOING | Application | 1:N | No | OPTIONAL | `[PLANNED]` |
| `HAS_FLOW_NODE` | OUTGOING | ProcessActivity, ProcessGateway, ProcessEvent | 1:N | No | OPTIONAL | `[EDGE]` |

---

### ProcessActivity

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: BPMN-aligned task or subprocess step within a BusinessProcess. Replaces any prior "ProcessStep" concept.
**Implementation Status**: `[IMPLEMENTED]` `domain/ProcessActivity.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `activityId` | String | Yes | Stable identifier | Pattern: `ACT-{processId}-{seq}` |
| `name` | String | Yes | Activity name | Max 200 chars |
| `description` | String | No | Full description | |
| `activityType` | String | Yes | BPMN activity classification | Enum: TASK, SUBPROCESS, CALL_ACTIVITY |
| `actionType` | String | No | Business action performed | Enum: CREATE, READ, UPDATE, DELETE, APPROVE, REJECT, ARCHIVE, SUBMIT, REVIEW, NOTIFY |
| `taskNature` | String | No | Who or what performs the task | Enum: USER, SERVICE, MANUAL, RULE, SCRIPT, SEND, RECEIVE |
| `orderIndex` | Integer | No | Display ordering within parent process | >= 0 |
| `trigger` | String | No | What initiates this activity | |
| `preCondition` | String | No | Required precondition | |
| `postCondition` | String | No | Expected postcondition | |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_FLOW_NODE` | INCOMING | BusinessProcess | N:1 | Yes | BLOCKING | `[EDGE]` |
| `FLOWS_TO` | OUTGOING | ProcessActivity, ProcessGateway, ProcessEvent | N:M | No | OPTIONAL | `[EDGE]` |
| `EXPANDS_TO` | OUTGOING | BusinessProcess | N:1 | No | OPTIONAL | `[EDGE]` — only when activityType = SUBPROCESS |
| `CALLS_PROCESS` | OUTGOING | BusinessProcess | N:1 | No | OPTIONAL | `[EDGE]` — only when activityType = CALL_ACTIVITY |
| `REALIZES` | INCOMING | UserStory | N:M | No | OPTIONAL | `[PLANNED]` |

---

### ProcessGateway

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: BPMN-aligned decision or fork/join point within a BusinessProcess
**Implementation Status**: `[IMPLEMENTED]` `domain/ProcessGateway.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `gatewayId` | String | Yes | Stable identifier | Pattern: `GW-{processId}-{seq}` |
| `name` | String | No | Gateway label | Max 200 chars |
| `gatewayType` | String | Yes | BPMN gateway classification | Enum: EXCLUSIVE, PARALLEL, INCLUSIVE, EVENT_BASED, COMPLEX |
| `defaultFlowTarget` | String | No | Default outgoing flow target | Reference to a flow node ID |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_FLOW_NODE` | INCOMING | BusinessProcess | N:1 | Yes | BLOCKING | `[EDGE]` |
| `FLOWS_TO` | OUTGOING | ProcessActivity, ProcessGateway, ProcessEvent | N:M | Yes | BLOCKING | `[EDGE]` |
| `FLOWS_TO` | INCOMING | ProcessActivity, ProcessGateway, ProcessEvent | N:M | Yes | BLOCKING | `[EDGE]` |

---

### ProcessEvent

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: BPMN-aligned event (start, end, intermediate) within a BusinessProcess
**Implementation Status**: `[IMPLEMENTED]` `domain/ProcessEvent.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `eventId` | String | Yes | Stable identifier | Pattern: `EVN-{processId}-{seq}` |
| `name` | String | No | Event label | Max 200 chars |
| `eventPosition` | String | Yes | Position in process lifecycle | Enum: START, END, INTERMEDIATE_CATCH, INTERMEDIATE_THROW, BOUNDARY |
| `eventTrigger` | String | No | What triggers the event | Enum: NONE, MESSAGE, TIMER, ERROR, SIGNAL, ESCALATION, CONDITIONAL, COMPENSATION, CANCEL, TERMINATE, LINK |
| `isInterrupting` | Boolean | No | Whether event interrupts current flow | Default: true |
| `attachedToRef` | String | No | Import-only metadata from BPMN source | Canonical representation is the ATTACHED_TO edge |
| `status` | String | Yes | Lifecycle status | Universal status enum |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_FLOW_NODE` | INCOMING | BusinessProcess | N:1 | Yes | BLOCKING | `[EDGE]` |
| `FLOWS_TO` | OUTGOING | ProcessActivity, ProcessGateway, ProcessEvent | N:M | No | OPTIONAL | `[EDGE]` |
| `ATTACHED_TO` | OUTGOING | ProcessActivity | N:1 | No | OPTIONAL | `[EDGE]` — only when eventPosition = BOUNDARY |

---

### Organization

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Business units, CoEs, departments, vendors, partners, teams. Carries `organizationType` enum. Vendor is a subtype (organizationType = VENDOR), NOT a separate node.
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `orgId` | String | Yes | Stable identifier | Pattern: `ORG-{seq}` |
| `name` | String | Yes | Organization name | Max 200 chars |
| `organizationType` | String | Yes | Type classification | Enum: BUSINESS_UNIT, COE, VENDOR, PARTNER, TEAM, DEPARTMENT |
| `description` | String | No | Full description | |
| `vendorStatus` | String | No | Vendor-specific status (only when organizationType = VENDOR) | Enum: ACTIVE, UNDER_REVIEW, SUSPENDED |
| `contractRef` | String | No | Vendor contract reference (only when organizationType = VENDOR) | |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `OWNS` | OUTGOING | Application | 1:N | No | OPTIONAL | `[PLANNED]` |

---

### Application

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Central node in EA model. Represents a deployable software system. Even for single-product models, Application should be explicit to support architecture views.
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `applicationId` | String | Yes | Stable identifier | Pattern: `APP-{seq}` |
| `name` | String | Yes | Application name | Max 200 chars |
| `description` | String | No | Full description | |
| `applicationType` | String | No | Application classification | Enum: WEB, API, BATCH, MOBILE, INTEGRATION |
| `technologyStack` | String | No | Primary technology | |
| `owner` | String | No | Application owner | |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |
| `repoPath` | String | No | Relative path from workspace root or absolute path | e.g., `.` for monorepo root, `backend/` for backend subtree |
| `repoUrl` | String | No | Git clone URL | For multi-repo setups |
| `workspaceType` | Enum | No | Repository structure | Enum: MONOREPO, POLYREPO |
| `defaultBuildCommand` | String | No | Fallback build command if component doesn't override | e.g., `mvn clean verify` |
| `defaultTestCommand` | String | No | Fallback test command if component doesn't override | e.g., `mvn test` |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_COMPONENT` | OUTGOING | ApplicationComponent | 1:N | Yes | BLOCKING | `[EDGE]` |
| `REALIZES` | OUTGOING | Feature | 1:N | No | OPTIONAL | `[PLANNED]` |
| `ENABLED_BY` | INCOMING | BusinessCapability | N:M | No | OPTIONAL | `[PLANNED]` |
| `OWNS` | INCOMING | Organization | N:1 | No | OPTIONAL | `[PLANNED]` |
| `SOURCE_OF` | OUTGOING | InformationFlow | 1:N | No | OPTIONAL | `[PLANNED]` |
| `TARGET_OF` | INCOMING | InformationFlow | N:1 | No | OPTIONAL | `[PLANNED]` |
| `DEPLOYED_VIA` | INCOMING | Deployment | N:1 | No | OPTIONAL | `[PLANNED]` — replaces deprecated DEPLOYS; traversal: Deployment -[HOSTS]-> ApplicationComponent, Application reached via HAS_COMPONENT reverse |

---

### ApplicationComponent

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Technical component within an application. Links to Screen and ApiContract for cross-family traversal.
**Implementation Status**: `[IMPLEMENTED]` `domain/ApplicationComponent.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `componentId` | String | Yes | Stable identifier | Pattern: `CMP-{app}-{seq}` |
| `name` | String | Yes | Component name | Max 200 chars |
| `description` | String | No | Full description | |
| `componentType` | Enum | Yes | Component classification | Enum: FRONTEND_APP, BFF, MICROSERVICE, LIBRARY, WORKER, DB_ADAPTER, GATEWAY, SERVICE_REGISTRY |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |
| `frameworkFamily` | Enum | Yes | Framework family classification | Enum: ANGULAR, SPRING_BOOT, ASP_NET_CORE, NODE_EXPRESS, NODE_NEST, REACT, VUE, FASTAPI, DJANGO, FLASK, SVELTE, NEXTJS |
| `frameworkName` | String | No | Exact framework name (for non-standard or emerging frameworks) | Free text. e.g., `Spring Boot`, `ASP.NET Core`, `Analog` |
| `frameworkVersion` | String | No | Pinned framework version | e.g., `3.4.1`, `21.0.0`, `8.0` |
| `runtime` | Enum | No | Execution environment | Enum: BROWSER, JVM, DOTNET_CLR, NODE, PYTHON, CONTAINER (use CONTAINER only for infrastructure components without a language-level runtime, e.g., Envoy, Nginx) |
| `language` | Enum | Yes | Primary implementation language | Enum: TYPESCRIPT, JAVA, CSHARP, JAVASCRIPT, PYTHON, KOTLIN, GO |
| `languageVersion` | String | No | Language version | e.g., `Java 23`, `TypeScript 5.4`, `.NET 8` |
| `modulePath` | String | No | Path relative to Application's repoPath | e.g., `backend/auth-facade/`, `frontend/src/app/features/design-hub/` |
| `manifestPath` | String | No | Build manifest relative to modulePath | e.g., `pom.xml`, `package.json`, `*.csproj` |
| `buildCommand` | String | No | Overrides Application default | e.g., `mvn clean verify -pl auth-facade` |
| `testCommand` | String | No | Overrides Application default | e.g., `npx vitest run`, `dotnet test` |
| `entrypointPath` | String | No | Main entry file relative to modulePath | e.g., `src/main/java/.../AuthFacadeApplication.java`, `src/main.ts` |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_COMPONENT` | INCOMING | Application | N:1 | Yes | BLOCKING | `[EDGE]` |
| `SUPPORTS_SCREEN` | OUTGOING | Screen | 1:N | No | OPTIONAL | `[PLANNED]` |
| `EXPOSES` | OUTGOING | ApiContract | 1:N | No | OPTIONAL | `[PLANNED]` |
| `HOSTS` | INCOMING | Deployment | N:M | No | OPTIONAL | `[PLANNED]` |
| `DEPENDS_ON_COMPONENT` | OUTGOING | ApplicationComponent | N:M | No | OPTIONAL | `[PLANNED]` — edge properties: dependencyType (SYNC_API, ASYNC_EVENT, SHARED_DB, SHARED_LIBRARY, GATEWAY_ROUTE), protocol, required |
| `OWNS_DATA_ENTITY` | OUTGOING | DataEntity | 1:N | No | OPTIONAL | `[PLANNED]` — closes resolution dead-end for DELIVERS→DataEntity |
| `ENFORCES_RULE` | OUTGOING | Rule | N:M | No | OPTIONAL | `[PLANNED]` — closes resolution dead-end for DELIVERS→Rule |

---

### BusinessObject

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Business-level data concept ("Customer", "Contract", "Invoice"). NOT the same as DataEntity — BusinessObject is business semantics, DataEntity is engineering schema. Connected via `MAPPED_TO`.
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `objectId` | String | Yes | Stable identifier | Pattern: `BOB-{domain}-{seq}` |
| `name` | String | Yes | Business object name | Max 200 chars |
| `description` | String | No | Full description | |
| `dataClassification` | String | No | Data sensitivity classification | Enum: PUBLIC, INTERNAL, CONFIDENTIAL, RESTRICTED |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `MAPPED_TO` | OUTGOING | DataEntity | 1:N | Yes | BLOCKING | `[PLANNED]` |
| `STRUCTURED_IN` | OUTGOING | BusinessObject | 1:N | No | OPTIONAL | `[PLANNED]` |
| `CARRIES` | INCOMING | InformationFlow | N:M | No | OPTIONAL | `[PLANNED]` |

---

### InformationFlow

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Data movement between applications. Links source and target applications via carried BusinessObjects.
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `flowId` | String | Yes | Stable identifier | Pattern: `IFL-{seq}` |
| `name` | String | Yes | Flow name | Max 200 chars |
| `description` | String | No | Full description | |
| `flowType` | String | No | Flow classification | Enum: SYNCHRONOUS, ASYNCHRONOUS, BATCH, EVENT |
| `protocol` | String | No | Communication protocol | Enum: REST, GRAPHQL, GRPC, KAFKA, FILE |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `SOURCE_OF` | INCOMING | Application | N:1 | Yes | BLOCKING | `[PLANNED]` |
| `TARGET_OF` | OUTGOING | Application | 1:1 | Yes | BLOCKING | `[PLANNED]` |
| `CARRIES` | OUTGOING | BusinessObject | 1:N | No | OPTIONAL | `[PLANNED]` |
| `EXPOSED_VIA` | OUTGOING | ApiContract | 1:1 | No | OPTIONAL | `[PLANNED]` |

---

### Deployment

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Deployment configuration — which components are deployed where.
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `deploymentId` | String | Yes | Stable identifier | Pattern: `DEP-{env}-{seq}` |
| `name` | String | Yes | Deployment name | Max 200 chars |
| `environment` | String | Yes | Target environment | Enum: DEV, STAGING, PRODUCTION |
| `description` | String | No | Full description | |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HOSTS` | OUTGOING | ApplicationComponent | 1:N | Yes | BLOCKING | `[PLANNED]` |
| `DEPLOYED_ON` | OUTGOING | InfrastructureNode | 1:N | Yes | BLOCKING | `[PLANNED]` |
| `DEPLOYED_VIA` | INCOMING | Application | 1:1 | No | OPTIONAL | `[PLANNED]` — reverse traversal: Application reached via Deployment -[HOSTS]-> ApplicationComponent |

---

### InfrastructureNode

**Tier**: 1 (First-Class Node)
**Category**: Architecture & EA
**Purpose**: Server, VM, container host, or cloud instance.
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `nodeId` | String | Yes | Stable identifier | Pattern: `INF-{seq}` |
| `name` | String | Yes | Node name | Max 200 chars |
| `nodeType` | String | Yes | Infrastructure type | Enum: PHYSICAL, VIRTUAL, CONTAINER, CLOUD_INSTANCE, SERVERLESS |
| `location` | String | No | Datacenter, region, or availability zone | |
| `description` | String | No | Full description | |
| `status` | String | Yes | Lifecycle status | Enum: IDENTIFIED through RETIRED |
| `sourceRefs` | List | No | Provenance links | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `DEPLOYED_ON` | INCOMING | Deployment | N:M | No | OPTIONAL | `[PLANNED]` |

---

## 4. Tier 2 — Registry Nodes (13)

---

### Channel

**Tier**: 2 (Registry)
**Category**: Delivery channel
**Purpose**: Controlled vocabulary of delivery channels
**Implementation Status**: `[IMPLEMENTED]` `domain/Channel.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `channelCode` | String | Yes | Stable code | Values: CH-WEB-DSK, CH-WEB-TAB, CH-WEB-MOB, CH-API, CH-WEBHOOK, CH-AI-CHAT, CH-AI-BG, CH-EMAIL, CH-INAPP |
| `displayName` | String | Yes | Human-readable name | |
| `channelType` | String | Yes | Classification | Enum: WEB, API, AI, NOTIFICATION |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `DELIVERED_VIA_CHANNEL` | INCOMING | Touchpoint | N:M | No | OPTIONAL | `[EDGE]` — Touchpoint `DELIVERED_VIA_CHANNEL` now backfilled from `EntryMode.channelId` |

---

### Permission

**Tier**: 2 (Registry)
**Category**: Access control
**Purpose**: Closed registry of permission levels
**Implementation Status**: `[IMPLEMENTED]` `domain/Permission.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `permissionKey` | String | Yes | Stable key | Values: SUPER_ADMIN, ADMIN, ARCHITECT, AGENT_DESIGNER, USER, VIEWER, HITL_REVIEWER, AUDITOR |
| `displayName` | String | Yes | Human-readable name | |
| `sortOrder` | Integer | No | Display ordering | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `REQUIRED_BY_INTERACTION` | INCOMING | Interaction | N:M | No | OPTIONAL | `[STRING_REF]` — `permission` string |

---

### ErrorCode

**Tier**: 2 (Registry)
**Category**: Error and message registry
**Purpose**: Registry of 80+ error, warning, and success codes
**Implementation Status**: `[IMPLEMENTED]` `domain/ErrorCode.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `code` | String | Yes | Stable code | Pattern: `{domain}-{severity}-{seq}` (e.g., DEF-E-001, AI-E-101) |
| `severity` | String | Yes | Code severity | Enum: ERROR, WARNING, SUCCESS, INFO |
| `messageText` | String | Yes | Default message text | |
| `triggerCondition` | String | No | When this code fires | |
| `resolutionHint` | String | No | How to resolve | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `CAN_PRODUCE_ERROR` | INCOMING | Screen | N:M | No | OPTIONAL | `[EDGE]` |
| `ON_ERROR_SHOWS` | INCOMING | Interaction | N:M | No | OPTIONAL | `[EDGE]` |
| `REFERENCED_BY_VALIDATION` | INCOMING | ValidationRule | N:M | No | OPTIONAL | `[PLANNED]` |

---

### ConfirmationDialog

**Tier**: 2 (Registry)
**Category**: UX registry
**Purpose**: Registry of 25+ confirmation dialogs
**Implementation Status**: `[IMPLEMENTED]` `domain/ConfirmationDialog.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `dialogId` | String | Yes | Stable code | Pattern: `{domain}-C-{seq}` (e.g., DEF-C-001) |
| `triggerAction` | String | Yes | What triggers this dialog | |
| `confirmLabel` | String | Yes | Confirm button text | |
| `cancelLabel` | String | Yes | Cancel button text | |
| `consequenceText` | String | No | What happens on confirm | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `TRIGGERS_CONFIRMATION` | INCOMING | Interaction | N:M | No | OPTIONAL | `[EDGE]` — legacy `confirmationCode` source field retained |

---

### Enum

**Tier**: 2 (Registry)
**Category**: Data vocabulary
**Purpose**: Controlled value sets for dropdown and select fields
**Implementation Status**: `[IMPLEMENTED]` `domain/BusinessDomain.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `enumId` | String | Yes | Stable identifier | Pattern: `ENUM-{name}` |
| `name` | String | Yes | Enum name | |
| `values` | List | Yes | Allowed values | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `USED_BY_FIELD` | INCOMING | DataField | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Event

**Tier**: 2 (Registry)
**Category**: Domain events
**Purpose**: Named domain events referenced by integrations
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `eventCode` | String | Yes | Stable code | Pattern: `EVT-{domain}-{name}` |
| `displayName` | String | Yes | Human-readable name | |
| `payload` | String | No | Payload description | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `FIRED_BY_INTEGRATION` | INCOMING | Integration | N:M | No | OPTIONAL | `[PLANNED]` |

---

### Locale

**Tier**: 2 (Registry)
**Category**: Internationalization
**Purpose**: Controlled language codes for i18n support
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `localeCode` | String | Yes | Stable code | Values: en, ar |
| `displayName` | String | Yes | Human-readable name | |
| `direction` | String | Yes | Text direction | Enum: LTR, RTL |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_TRANSLATIONS` | OUTGOING | TranslationKey | 1:N | No | OPTIONAL | `[PLANNED]` |

---

### TranslationKey

**Tier**: 2 (Registry)
**Category**: Internationalization
**Purpose**: Registry of translatable strings linking Locale to UI elements
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `key` | String | Yes | Translation key | Dot-notation: `screen.detail.title` |
| `defaultText` | String | Yes | Default (English) text | |
| `context` | String | No | Usage context for translators | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BELONGS_TO_LOCALE` | INCOMING | Locale | N:1 | Yes | BLOCKING | `[PLANNED]` |
| `USED_BY_MESSAGE` | INCOMING | Message | N:M | No | OPTIONAL | `[PLANNED]` |

---

### BusinessDomain

**Tier**: 2 (Registry)
**Category**: Architecture & EA
**Purpose**: Top-level grouping of business capabilities into named domains (e.g., "Customer Management", "Compliance", "Risk")
**Implementation Status**: `[PLANNED]`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `domainCode` | String | Yes | Stable code | Pattern: `DOM-{code}` |
| `name` | String | Yes | Domain display name | |
| `description` | String | No | Domain description | |
| `activeStatus` | String | No | Domain lifecycle | Enum: ACTIVE, DEPRECATED |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `HAS_CAPABILITY` | OUTGOING | BusinessCapability | 1:N | Yes | BLOCKING | `[EDGE]` |

---

### ImportSnapshot

**Tier**: 2 (Registry)
**Category**: Cross-cutting
**Purpose**: Append-only audit record for batch imports into the graph. Captures what was imported, when, from where, and whether it succeeded. One snapshot per import operation.
**Implementation Status**: `[IMPLEMENTED]` `domain/ImportSnapshot.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `snapshotId` | String | Yes | Stable identifier | Pattern: `IMP-{YYYYMMDD}-{seq}` |
| `sourceType` | String | Yes | Import source classification | Enum: GIT_DOC, JIRA_SYNC, MANUAL_ENTRY |
| `sourcePath` | String | No | Path or URL of source | e.g., `docs/requirements/story-inventory.md` |
| `importedAt` | Instant | Yes | Import timestamp | ISO 8601 |
| `importedBy` | String | Yes | Actor who triggered import | User ID or system identifier |
| `result` | String | Yes | Import outcome | Enum: SUCCESS, PARTIAL, FAILED, CONFLICTED |
| `itemCount` | Integer | No | Number of items processed | |
| `errorSummary` | String | No | Error details for non-SUCCESS results | |
| `contentHash` | String | No | SHA-256 hash for drift detection | Pattern: `sha256:{hex}` |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `IMPORTED_BY` | INCOMING | Importable T1 nodes | N:M | No | OPTIONAL | `[EDGE]` — materialized on source T1 nodes |

**Note:** The IMPORTED_BY edge is modeled on source T1 nodes (outgoing from the importable entity, incoming to ImportSnapshot), not on ImportSnapshot itself. This follows the convention that the entity being described carries its own provenance edge.

---

### CodingConvention

**Tier**: 2 (Registry — Hybrid)
**Category**: Cross-cutting
**Purpose**: Queryable metadata for coding conventions. Structured categories stored in graph (conventionCode, category, enforcement, scope); detailed rules via docRef pointing to Markdown files in Git. This hybrid approach keeps the graph lightweight while enabling "which conventions apply to this component?" queries.
**Implementation Status**: `[IMPLEMENTED]` `domain/CodingConvention.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `conventionCode` | String | Yes | Stable identifier | Pattern: `CONV-{category}-{seq}` |
| `name` | String | Yes | Convention display name | |
| `category` | String | Yes | Convention category | Enum: NAMING, STRUCTURE, DEPENDENCY_INJECTION, ERROR_HANDLING, TESTING, LOGGING, SECURITY, PERFORMANCE |
| `enforcement` | String | Yes | Enforcement level | Enum: MANDATORY, RECOMMENDED, ADVISORY |
| `scope` | String | Yes | Applicability scope | Enum: GLOBAL, BACKEND, FRONTEND, SERVICE, COMPONENT |
| `docRef` | String | Yes | Relative path to convention Markdown file | e.g., `docs/conventions/naming.md` |
| `summary` | String | No | One-line summary of the convention | |
| `activeStatus` | String | No | Registry lifecycle | Enum: ACTIVE, DEPRECATED |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `GOVERNED_BY_CONVENTION` | INCOMING | Application, ApplicationComponent, CodeAsset | N:M | No | OPTIONAL | `[EDGE]` — materialized on source entities |

**Note:** The GOVERNED_BY_CONVENTION edge is modeled on source entities (Application, ApplicationComponent, CodeAsset → CodingConvention), not on CodingConvention itself. All bindings are materialized as explicit edges — no implicit attribute matching.

---

### AgentPolicy

**Tier**: 2 (Registry)
**Category**: Cross-cutting
**Purpose**: Agent execution guardrail registry. Captures allowed repos, commands, environments, secret scopes, file-touch limits, and human-approval thresholds.
**Implementation Status**: `[IMPLEMENTED]` `domain/AgentPolicy.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `policyId` | String | Yes | Stable identifier | Pattern: `POL-{seq}` |
| `name` | String | Yes | Policy display name | |
| `allowedRepos` | List | No | Repo-path allowlist | |
| `allowedCommands` | List | No | Command allowlist | |
| `forbiddenCommands` | List | No | Explicitly blocked commands | |
| `allowedEnvironments` | List | No | Execution environment allowlist | |
| `secretScopes` | List | No | Secrets/config scopes allowed to the agent | |
| `maxFilesTouched` | Integer | No | Safety limit for a single execution | |
| `requiresHumanApproval` | Boolean | No | Human gate indicator | |
| `approvalThreshold` | String | No | Risk threshold label | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `GOVERNED_BY_POLICY` | INCOMING | Application, ApplicationComponent | N:M | No | OPTIONAL | `[EDGE]` — materialized on source entities |

---

### EvidenceRecord

**Tier**: 2 (Registry)
**Category**: Cross-cutting
**Purpose**: Durable proof registry for tests, screenshots, contract snapshots, and visual baselines. Supports verification freshness and baseline traceability.
**Implementation Status**: `[IMPLEMENTED]` `domain/EvidenceRecord.java`

#### Attributes

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `evidenceId` | String | Yes | Stable identifier | Pattern: `EVR-{seq}` |
| `evidenceType` | String | Yes | Proof classification | Enum: TEST_RESULT, SCREENSHOT, CONTRACT_SNAPSHOT, VISUAL_REGRESSION |
| `artifactId` | String | No | Proven artifact/test identifier | |
| `producedAt` | Instant | Yes | Production timestamp | ISO 8601 |
| `producedBy` | String | No | Agent or user identifier | |
| `repoCommit` | String | No | Git SHA when the proof was produced | |
| `result` | String | Yes | Proof outcome | Enum: PASS, FAIL, PARTIAL |
| `artifactPath` | String | No | Filesystem path to proof artifact | |

#### Relationships

| Relationship | Direction | Target | Cardinality | Required | Severity | Implementation |
|-------------|-----------|--------|-------------|----------|----------|----------------|
| `BASELINED_BY` | INCOMING | Screen, ApiContract | N:M | No | OPTIONAL | `[EDGE]` — materialized on source entities |

---

## 5. Tier 3 — Value Objects (4)

Tier 3 objects inherit identity and lifecycle from their parent. They are not independently queryable and are not counted in the 71 benchmarkable nodes. Their attributes are scored as part of their parent's attribute depth.

---

### InteractionOutcome

**Tier**: 3 (Value Object)
**Parent**: Interaction
**Purpose**: Structured outcomes (success, error, loading) embedded in an interaction
**Implementation Status**: `[PLANNED]` — missing from `Interaction.java`

#### Attributes

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `success` | String | No | Success outcome description |
| `error` | String | No | Error outcome description |
| `loading` | String | No | Loading state description |
| `errorCodeRef` | String | No | Reference to ErrorCode (T2) |

---

### Effect

**Tier**: 3 (Value Object)
**Parent**: Interaction
**Purpose**: Navigate, filter, mutation, toast, and other outcomes of an interaction
**Implementation Status**: `[IMPLEMENTED]` `domain/Effect.java`

#### Attributes

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `type` | String | Yes | Effect type. Values: navigate, redirect, open-tab, replace-panel, popout, open-modal, open-drawer, open-overlay, open-menu, close-overlay, toggle, filter, sort, select, deselect, mutation, download, stream-start, stream-stop, toast, analytics-event, badge-update |
| `target` | String | No | Target element or screen |
| `targetMode` | String | No | Target mode |
| `resolutionRule` | String | No | How target is resolved |
| `defaultTarget` | String | No | Fallback target |

#### Relationships (via parent)

| Relationship | Direction | Target | Implementation |
|-------------|-----------|--------|----------------|
| `NAVIGATES_TO` | OUTGOING | Screen | `[EDGE]` |

---

### EntryMode

**Tier**: 3 (Value Object)
**Parent**: Touchpoint
**Purpose**: Channel + mechanism pair describing how a touchpoint is accessed
**Implementation Status**: `[IMPLEMENTED]` `domain/EntryMode.java`

#### Attributes

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `channelId` | String | Yes | Channel reference. **Target**: resolved via parent Touchpoint's edge to Channel (T2) |
| `mechanism` | String | Yes | Entry mechanism. Values: dock-nav, deep-link, notification-click, breadcrumb, search-result, redirect, bookmark, api-redirect, chatbot-suggestion |

---

### ContentElement

**Tier**: 3 (Value Object)
**Parent**: Screen
**Purpose**: Ordered content inventory for a screen
**Implementation Status**: `[IMPLEMENTED]` `domain/ContentElement.java`

#### Attributes

| Attribute | Type | Required | Description |
|-----------|------|----------|-------------|
| `element` | String | Yes | Element identifier |
| `type` | String | Yes | Element type |
| `description` | String | No | Element description |
| `orderIndex` | Integer | Yes | Display ordering |

---

## 6. Relationship Registry

Complete registry of all modeled relationships with implementation status.

### 6.1 Existing Graph Edges

The table below shows the legacy/core implemented edges that were present earliest in the repo. The current implementation baseline is broader: **90 SDN `@Relationship` declarations plus 1 Cypher-only polymorphic edge (`ASSESSES`)**.

| Relationship | Source | Target | Cardinality | Severity | Status |
|-------------|--------|--------|-------------|----------|--------|
| `HAS_STEP` | Journey | JourneyStep | 1:N | BLOCKING | `[EDGE]` |
| `TARGETS` | Touchpoint | Screen | N:1 | BLOCKING | `[EDGE]` |
| `HAS_ENTRY_MODE` | Touchpoint | EntryMode (T3) | 1:N | BLOCKING | `[EDGE]` |
| `HAS_INTERACTION` | Screen | Interaction | 1:N | BLOCKING | `[EDGE]` — replaces deprecated ON_SCREEN (direction reversed) |
| `HAS_EFFECT` | Interaction | Effect (T3) | 1:N | OPTIONAL | `[EDGE]` |
| `NAVIGATES_TO` | Effect (T3) | Screen | N:1 | OPTIONAL | `[EDGE]` |
| `HAS_GAP` | Screen | Gap | 1:N | OPTIONAL | `[EDGE]` |
| `HAS_CONTENT` | Screen | ContentElement (T3) | 1:N | OPTIONAL | `[EDGE]` |
| `TRANSITIONS_TO` | Screen | Screen | N:M | OPTIONAL | `[EDGE]` |

### 6.2 Transitional and Remaining String-Encoded Relationships

| Relationship | Source | Target | Current Field | Severity | Status |
|-------------|--------|--------|--------------|----------|--------|
| `DELIVERS` | UserStory | Screen | `storyRefs` | BLOCKING | `[STRING_REF]` — UserStory DELIVERS Screen (replaces deprecated IMPLEMENTS_STORY, direction reversed) |
| `ACCESSIBLE_BY_ROLE` | Screen | BusinessRole | `roleKeys` | OPTIONAL | `[EDGE]` — legacy source field retained |
| `USED_BY_PERSONA` | Screen | Persona | `personaIds` | OPTIONAL | `[EDGE]` — legacy source field retained |
| `PERFORMED_BY_PERSONA` | Journey | Persona | `personaId` | BLOCKING | `[EDGE]` — legacy source field retained |
| `REQUIRES_PERMISSION` | Interaction | Permission (T2) | `permission` | OPTIONAL | `[EDGE]` — legacy source field retained |
| `CALLS_API` | Interaction | ApiContract | `apiCalls` | OPTIONAL | `[EDGE]` |
| `TRIGGERS_CONFIRMATION` | Interaction | ConfirmationDialog (T2) | `confirmationCode` | OPTIONAL | `[EDGE]` |
| `DELIVERED_VIA_CHANNEL` | Touchpoint | Channel (T2) | `channelId` in EntryMode | BLOCKING | `[EDGE]` — legacy source field retained |
| `EXECUTES_INTERACTION` | JourneyStep | Interaction | `interactionRef` | OPTIONAL | `[STRING_REF]` |

### 6.3 Planned and Extension Relationships

| Relationship | Source | Target | Cardinality | Severity | Status |
|-------------|--------|--------|-------------|----------|--------|
| `HAS_FEATURE` | BusinessObjective | Feature | 1:N | BLOCKING | `[PLANNED]` |
| `BELONGS_TO_OBJECTIVE` | Feature | BusinessObjective | N:1 | BLOCKING | `[PLANNED]` |
| `HAS_STORY` | Feature | UserStory | 1:N | BLOCKING | `[EDGE]` |
| `HAS_CRITERION` | UserStory | AcceptanceCriterion | 1:N | BLOCKING | `[EDGE]` |
| `USES_DATA_ENTITY` | ApiContract | DataEntity | N:M | OPTIONAL | `[PLANNED]` |
| `HAS_FIELD` | DataEntity | DataField | 1:N | BLOCKING | `[EDGE]` |
| `HAS_SOURCE` | Screen, UserStory, Bug | SourceReference | N:M | OPTIONAL | `[EDGE]` |
| `PERFORMS_JOURNEY` | Persona | Journey | 1:N | BLOCKING | `[PLANNED]` |
| `USES_SCREEN` | JourneyStep | Screen | N:1 | BLOCKING | `[PLANNED]` |
| `STARTS_AT_TOUCHPOINT` | JourneyStep | Touchpoint | N:M | OPTIONAL | `[PLANNED]` |
| `HAS_MESSAGE` | Screen | Message | 1:N | OPTIONAL | `[EDGE]` |
| `CAN_PRODUCE_ERROR` | Screen | ErrorCode (T2) | N:M | OPTIONAL | `[EDGE]` |
| `ON_ERROR_SHOWS` | Interaction | ErrorCode (T2) | N:M | OPTIONAL | `[EDGE]` |
| `GOVERNS_STORY` | Rule | UserStory | N:M | OPTIONAL | `[PLANNED]` |
| `AFFECTS_SCREEN` | Finding | Screen | N:M | OPTIONAL | `[PLANNED]` |
| `AFFECTS_SCREEN` | Bug | Screen | N:M | OPTIONAL | `[EDGE]` |
| `TRACKED_IN_EXTERNAL_SYSTEM` | Bug | ExternalArtifact | N:1 | OPTIONAL | `[PLANNED]` |
| `REPRESENTS_STORY` | ExternalArtifact | UserStory | N:1 | OPTIONAL | `[EDGE]` |
| `REPRESENTS_BUG` | ExternalArtifact | Bug | N:1 | OPTIONAL | `[EDGE]` |
| `BELONGS_TO_SCREEN` | ScreenState | Screen | N:1 | BLOCKING | `[PLANNED]` |
| `FROM_SCREEN` | Transition | Screen | N:1 | BLOCKING | `[PLANNED]` |
| `TO_SCREEN` | Transition | Screen | N:1 | BLOCKING | `[PLANNED]` |
| `HAS_REQUEST` | ApiContract | RequestSchema | 1:1 | OPTIONAL | `[EDGE]` |
| `HAS_RESPONSE` | ApiContract | ResponseSchema | 1:1 | OPTIONAL | `[EDGE]` |
| `HAS_ERROR` | ApiContract | ErrorContract | 1:1 | OPTIONAL | `[EDGE]` |
| `VERIFIED_BY` | AcceptanceCriterion | TestCase | 1:N | OPTIONAL | `[PLANNED]` |
| `HAS_TRANSLATIONS` | Locale | TranslationKey | 1:N | OPTIONAL | `[PLANNED]` |
| `REALIZED_BY_PROCESS` | BusinessCapability | BusinessProcess | 1:N | OPTIONAL | `[EDGE]` |
| `ENABLED_BY` | BusinessCapability | Application | 1:N | BLOCKING | `[PLANNED]` |
| `REALIZES` | Feature | BusinessCapability | N:M | OPTIONAL | `[PLANNED]` — four-verb traceability |
| `REQUIRES_CAPABILITY` | BusinessObjective | BusinessCapability | N:M | OPTIONAL | `[PLANNED]` |
| `HAS_COMPONENT` | Application | ApplicationComponent | 1:N | BLOCKING | `[PLANNED]` |
| `REALIZES` | Application | Feature | 1:N | OPTIONAL | `[PLANNED]` |
| `OWNS` | Organization | Application | 1:N | OPTIONAL | `[PLANNED]` |
| `SUPPORTS_SCREEN` | ApplicationComponent | Screen | 1:N | OPTIONAL | `[PLANNED]` |
| `EXPOSES` | ApplicationComponent | ApiContract | 1:N | OPTIONAL | `[PLANNED]` |
| `MAPPED_TO` | BusinessObject | DataEntity | 1:N | BLOCKING | `[PLANNED]` |
| `STRUCTURED_IN` | BusinessObject | BusinessObject | 1:N | OPTIONAL | `[PLANNED]` |
| `SOURCE_OF` | Application | InformationFlow | 1:N | BLOCKING | `[PLANNED]` |
| `TARGET_OF` | InformationFlow | Application | 1:1 | BLOCKING | `[PLANNED]` |
| `CARRIES` | InformationFlow | BusinessObject | 1:N | OPTIONAL | `[PLANNED]` |
| `EXPOSED_VIA` | InformationFlow | ApiContract | 1:1 | OPTIONAL | `[PLANNED]` |
| `HOSTS` | Deployment | ApplicationComponent | 1:N | BLOCKING | `[PLANNED]` |
| `DEPLOYED_ON` | Deployment | InfrastructureNode | 1:N | BLOCKING | `[PLANNED]` |
| `DEPLOYED_VIA` | Deployment | Application | 1:1 | OPTIONAL | `[PLANNED]` — replaces deprecated DEPLOYS |
| `HAS_FEATURE` | Epic | Feature | 1:N | BLOCKING | `[EDGE]` |
| `REALIZED_BY` | BusinessObjective | Epic | 1:N | OPTIONAL | `[PLANNED]` |
| `AFFECTS` | Epic | Application | 1:N | OPTIONAL | `[PLANNED]` |
| `SUPPORTED_BY` | BusinessProcess | Application | 1:N | OPTIONAL | `[PLANNED]` |
| `HAS_FLOW_NODE` | BusinessProcess | ProcessActivity, ProcessGateway, ProcessEvent | 1:N | OPTIONAL | `[EDGE]` |
| `FLOWS_TO` | ProcessActivity, ProcessGateway, ProcessEvent | ProcessActivity, ProcessGateway, ProcessEvent | N:M | OPTIONAL | `[EDGE]` |
| `EXPANDS_TO` | ProcessActivity | BusinessProcess | N:1 | OPTIONAL | `[EDGE]` |
| `CALLS_PROCESS` | ProcessActivity | BusinessProcess | N:1 | OPTIONAL | `[EDGE]` |
| `ATTACHED_TO` | ProcessEvent | ProcessActivity | N:1 | OPTIONAL | `[EDGE]` |
| `REALIZES` | UserStory | ProcessActivity, JourneyStep | N:M | OPTIONAL | `[PLANNED]` |
| `DELIVERS` | UserStory | Screen, ApiContract, DataEntity, Rule, Message | N:M | OPTIONAL | `[PLANNED]` |
| `IMPLEMENTS` | Task | Screen, ApiContract, DataEntity, Rule, Message, TestCase, ApplicationComponent | N:M | OPTIONAL | `[PLANNED]` |
| `DEPENDS_ON_COMPONENT` | ApplicationComponent | ApplicationComponent | N:M | OPTIONAL | `[PLANNED]` — edge properties: dependencyType, protocol, required |
| `OWNS_DATA_ENTITY` | ApplicationComponent | DataEntity | 1:N | OPTIONAL | `[PLANNED]` |
| `ENFORCES_RULE` | ApplicationComponent | Rule | N:M | OPTIONAL | `[PLANNED]` |
| `GOVERNED_BY_RULE` | UserStory | Rule | N:M | OPTIONAL | `[EDGE]` |
| `VERIFIED_BY` | UserStory | TestCase | 1:N | BLOCKING | `[PLANNED]` |
| `VERIFIES` | TestCase | UserStory | N:M | OPTIONAL | `[PLANNED]` |
| `HAS_TASK` | UserStory | Task | 1:N | OPTIONAL | `[EDGE]` |
| `DEPENDS_ON` | Task | Task | N:M | OPTIONAL | `[PLANNED]` |
| `ASSIGNED_TO` | Task | Organization | N:1 | OPTIONAL | `[PLANNED]` |
| `HAS_CAPABILITY` | BusinessDomain | BusinessCapability | 1:N | BLOCKING | `[EDGE]` |
| `REALIZED_BY_PROCESS` | BusinessCapability | BusinessProcess | 1:N | OPTIONAL | `[EDGE]` |
| `HAS_CODE_ASSET` | ApplicationComponent | CodeAsset | 1:N | BLOCKING | `[EDGE]` — agent-ready Phase 1 |
| `LOCATED_IN` | TestCase | CodeAsset | N:1 | OPTIONAL | `[EDGE]` — agent-ready Phase 1 |
| `ASSET_FOR_SCREEN` | CodeAsset | Screen | N:M | OPTIONAL | `[EDGE]` — agent-ready Phase 1 |
| `ASSET_FOR_API` | CodeAsset | ApiContract | N:M | OPTIONAL | `[EDGE]` — agent-ready Phase 1 |
| `ASSET_FOR_ENTITY` | CodeAsset | DataEntity | N:M | OPTIONAL | `[EDGE]` — agent-ready Phase 1 |
| `ASSET_FOR_RULE` | CodeAsset | Rule | N:M | OPTIONAL | `[EDGE]` — agent-ready Phase 1 |
| `IMPORTED_BY` | Importable T1 | ImportSnapshot (T2) | N:M | OPTIONAL | `[PLANNED]` — agent-ready Phase 1 |
| `IMPLEMENTS` | Task | CodeAsset | N:M | OPTIONAL | `[PLANNED]` — agent-ready Phase 1 (extends existing IMPLEMENTS targets) |
| `HAS_QUALITY_CONSTRAINT` | Screen, ApiContract, DataEntity, ApplicationComponent | QualityConstraint | N:M | OPTIONAL | `[EDGE]` — agent-ready Phase 2 |
| `SATISFIED_BY` | QualityConstraint | TestCase | N:M | OPTIONAL | `[EDGE]` — agent-ready Phase 2 |
| `GOVERNED_BY_CONVENTION` | Application, ApplicationComponent, CodeAsset | CodingConvention (T2) | N:M | OPTIONAL | `[EDGE]` — agent-ready Phase 2 |
| `GOVERNED_BY_POLICY` | Application, ApplicationComponent | AgentPolicy (T2) | N:M | OPTIONAL | `[EDGE]` — operational near-zero-drift |
| `BASELINED_BY` | Screen, ApiContract | EvidenceRecord (T2) | N:M | OPTIONAL | `[EDGE]` — operational near-zero-drift |
| `DEPENDS_ON_ASSET` | CodeAsset | CodeAsset | N:M | OPTIONAL | `[PLANNED]` — operational near-zero-drift |
| `ASSESSES` | Assessment | Assessable T1 node | 1:1 | BLOCKING | `[CYPHER]` — capability/project meta-model |
| `IDENTIFIES_GAP` | Assessment | Gap | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |
| `ADDRESSES_GAP` | ProjectInstance | Gap | 1:N | BLOCKING | `[EDGE]` — capability/project meta-model |
| `TARGETS_CAPABILITY` | ProjectInstance | BusinessCapability | 1:N | BLOCKING | `[EDGE]` — capability/project meta-model |
| `HAS_PORTFOLIO` | ProjectInstance | RequirementPortfolio | 1:1 | BLOCKING | `[EDGE]` — capability/project meta-model |
| `HAS_EPIC` | RequirementPortfolio | Epic | 1:N | BLOCKING | `[EDGE]` — capability/project meta-model |
| `HAS_MILESTONE` | ProjectInstance | Milestone | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |
| `HAS_TASK` | Milestone | Task | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |
| `CREATES_APPLICATION` | ProjectInstance | Application | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |
| `ENHANCES_APPLICATION` | ProjectInstance | Application | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |
| `INTEGRATES_WITH` | ProjectInstance | Application | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |
| `CREATES_COMPONENT` | ProjectInstance | ApplicationComponent | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |
| `ENHANCES_COMPONENT` | ProjectInstance | ApplicationComponent | 1:N | OPTIONAL | `[EDGE]` — capability/project meta-model |

### 6.4 Deprecated Edges

| Relationship | Replaced By | Notes |
|-------------|-------------|-------|
| `USES_SCREEN` (UserStory→Screen) | `DELIVERS` (UserStory→Screen) | Verb normalization to four-verb model |
| `REQUIRES_API` (UserStory→ApiContract) | `DELIVERS` (UserStory→ApiContract) | Verb normalization to four-verb model |
| `ON_SCREEN` (Interaction→Screen) | `HAS_INTERACTION` (Screen→Interaction) | Direction reversed. Screen now owns the relationship to its Interactions. Existing `[EDGE]` in code must be migrated. |
| `IMPLEMENTS_STORY` (Screen→UserStory) | `DELIVERS` (UserStory→Screen) | Direction reversed: was Screen→UserStory, now UserStory→Screen. `storyRefs` string field on Screen remains until edge migration. |
| `DEPLOYS` (Application→Deployment) | `HOSTS` + `DEPLOYED_ON` | Directional fix. Deployment hosts components and is deployed on infrastructure. |
| `DETECTED_BY_BENCHMARK` (Gap→computed) | `detectedBy` property on Gap | Not a real edge — replaced by enum property (BENCHMARK_ENGINE, HUMAN_REVIEW, CI_GATE) |
| `HAS_STEP` (BusinessProcess→ProcessActivity) | `HAS_FLOW_NODE` | Semantic correction for BPMN alignment. **Note:** Journey `HAS_STEP` is NOT deprecated. JourneyStep `USES_SCREEN` is NOT deprecated. |

### 6.5 Implementation Pack — Computed Traversal Query

The Implementation Pack resolves a UserStory to its full execution context. It is a **computed traversal result**, NOT a stored node.

**Resolution chain:**
- Direct: `deliverable <-[SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]- ApplicationComponent`
- Transitive (Message): `Message <-[HAS_MESSAGE]- Screen <-[SUPPORTS_SCREEN]- ApplicationComponent`
- Command precedence: `COALESCE(comp.buildCommand, app.defaultBuildCommand)` — component overrides Application default. Same for `testCommand`.

**Canonical Cypher query:** See `docs/superpowers/specs/2026-03-14-technical-execution-context-design.md` section 7.1 for the full staged query.

**MCR-STORY-AGENT-READY-001 Cypher check:**

~~~cypher
// Direct resolution
MATCH (us:UserStory {storyId: $storyId})-[:DELIVERS]->(d)
OPTIONAL MATCH (d)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-(directComp:ApplicationComponent)
// Transitive resolution for Message deliverables
OPTIONAL MATCH (d)<-[:HAS_MESSAGE]-(ms:Screen)<-[:SUPPORTS_SCREEN]-(transitiveComp:ApplicationComponent)
WHERE d:Message
WITH us, COALESCE(directComp, transitiveComp) AS comp
WHERE comp IS NOT NULL
OPTIONAL MATCH (comp)<-[:HAS_COMPONENT]-(app:Application)
WITH comp, app
WHERE comp.frameworkFamily IS NOT NULL
  AND comp.modulePath IS NOT NULL
  AND COALESCE(comp.testCommand, app.defaultTestCommand) IS NOT NULL
RETURN count(comp) > 0 AS agentReady
~~~

---

## 7. Relationship Spine Diagrams

### 7.1 Primary Traversal Spine

```mermaid
graph LR
    BO[BusinessObjective] -->|HAS_FEATURE| FE[Feature]
    FE -->|HAS_STORY| US[UserStory]
    PE[Persona] -->|PERFORMS_JOURNEY| JO[Journey]
    JO -->|HAS_STEP| JS[JourneyStep]
    JS -->|STARTS_AT_TOUCHPOINT| TP[Touchpoint]
    TP -->|DELIVERED_VIA_CHANNEL| CH["Channel T2"]
    TP -->|TARGETS| SC[Screen]
    JS -->|USES_SCREEN| SC
    SC -->|HAS_INTERACTION| IN[Interaction]
    IN -->|REQUIRES_PERMISSION| PM["Permission T2"]
    IN -->|TRIGGERS_CONFIRMATION| CD["ConfirmationDialog T2"]
    IN -->|CALLS_API| AP[ApiContract]
    AP -->|USES_DATA_ENTITY| DA[DataEntity]
    SC -->|HAS_MESSAGE| MS[Message]
    SC -->|CAN_PRODUCE_ERROR| ERC["ErrorCode T2"]
```

### 7.2 Reverse Traversal

```mermaid
graph RL
    BU[Bug] -->|AFFECTS| SC[Screen]
    FI[Finding] -->|AFFECTS| SC
    ERC["ErrorCode T2"] -.->|PRODUCED_BY| SC
    SC -.->|SUPPORTS_STEP| JS[JourneyStep]
    JS -.->|BELONGS_TO| JO[Journey]
    JO -.->|PERFORMED_BY| PE[Persona]
    PE -.->|context| BO[BusinessObjective]
```

### 7.3 Process Spine

```mermaid
graph LR
    BP[BusinessProcess] -->|HAS_FLOW_NODE| PA[ProcessActivity]
    BP -->|HAS_FLOW_NODE| PG[ProcessGateway]
    BP -->|HAS_FLOW_NODE| PE[ProcessEvent]
    PE -->|FLOWS_TO| PA
    PA -->|FLOWS_TO| PG
    PG -->|FLOWS_TO| PA
    PA -->|FLOWS_TO| PE
    PA -->|EXPANDS_TO| BP2[BusinessProcess]
    PA -->|CALLS_PROCESS| BP3[BusinessProcess]
    PE -->|ATTACHED_TO| PA
    BC[BusinessCapability] -->|REALIZED_BY_PROCESS| BP
    BD["BusinessDomain T2"] -->|HAS_CAPABILITY| BC
```

### 7.4 Delivery Spine

```mermaid
graph LR
    BO[BusinessObjective] -->|REALIZED_BY| EP[Epic]
    EP -->|HAS_FEATURE| FE[Feature]
    FE -->|HAS_STORY| US[UserStory]
    US -->|HAS_TASK| TK[Task]
    TK -->|IMPLEMENTS| SC[Screen]
    TK -->|IMPLEMENTS| AP[ApiContract]
    TK -->|DEPENDS_ON| TK2[Task]
    TK -->|ASSIGNED_TO| ORG[Organization]
```

### 7.5 Four-Verb Traceability Spine

```mermaid
graph LR
    US[UserStory] -->|REALIZES| PA[ProcessActivity]
    US[UserStory] -->|REALIZES| JS[JourneyStep]
    US -->|DELIVERS| SC[Screen]
    US -->|DELIVERS| AP[ApiContract]
    US -->|DELIVERS| DE[DataEntity]
    US -->|DELIVERS| RU[Rule]
    US -->|DELIVERS| MSG[Message]
    US -->|VERIFIED_BY| TC[TestCase]
    TK[Task] -->|IMPLEMENTS| SC
    TK -->|IMPLEMENTS| AP
```

### 7.6 Agent-Ready Traversal Spine (Code Targeting)

```mermaid
graph LR
    US[UserStory] -->|DELIVERS| ART["Screen / ApiContract / DataEntity / Rule"]
    ART -->|"SUPPORTS_SCREEN / EXPOSES / OWNS_DATA_ENTITY / ENFORCES_RULE (IN)"| COMP[ApplicationComponent]
    COMP -->|HAS_CODE_ASSET| CA[CodeAsset]
    CA -->|"ASSET_FOR_*"| ART
    TC[TestCase] -->|LOCATED_IN| CA
    TK[Task] -->|IMPLEMENTS| CA
    COMP -->|GOVERNED_BY_CONVENTION| CCN["CodingConvention T2"]
    ART_Q["Screen / ApiContract / DataEntity"] -->|HAS_QUALITY_CONSTRAINT| QC[QualityConstraint]
    QC -->|SATISFIED_BY| TC
```

This spine enables: "Given a UserStory, which code files need to change, which conventions apply, and which test files verify them?"

### 7.7 Implementation Status Map

```mermaid
graph TD
    subgraph Implemented["Existing Edges — GREEN"]
        E1["HAS_STEP: Journey → JourneyStep"]
        E2["TARGETS: Touchpoint → Screen"]
        E3["HAS_ENTRY_MODE: Touchpoint → EntryMode"]
        E4["HAS_INTERACTION: Screen → Interaction"]
        E5["HAS_EFFECT: Interaction → Effect"]
        E6["NAVIGATES_TO: Effect → Screen"]
        E7["HAS_GAP: Screen → Gap"]
        E8["HAS_CONTENT: Screen → ContentElement"]
        E9["TRANSITIONS_TO: Screen → Screen"]
    end

    subgraph StringRef["String References — AMBER"]
        S1["storyRefs: Screen.storyRefs → UserStory (target: UserStory -[DELIVERS]-> Screen)"]
        S2["roleKeys: Screen → BusinessRole"]
        S3["personaIds: Screen → Persona"]
        S4["personaId: Journey → Persona"]
        S5["permission: Interaction → Permission"]
        S6["apiCalls: Interaction → ApiContract"]
        S7["confirmationCode: Interaction → ConfirmationDialog"]
        S8["channelId: Touchpoint → Channel"]
        S9["interactionRef: JourneyStep → Interaction"]
    end

    subgraph Planned["Planned — RED"]
        P1["HAS_FEATURE: Objective → Feature"]
        P2["HAS_STORY: Feature → Story"]
        P3["USES_SCREEN: Step → Screen"]
        P4["HAS_MESSAGE: Screen → Message"]
        P5["USES_DATA_ENTITY: API → DataEntity"]
        P6["HAS_FLOW_NODE / FLOWS_TO / EXPANDS_TO / CALLS_PROCESS / ATTACHED_TO"]
        P7["REALIZES / DELIVERS / IMPLEMENTS / VERIFIED_BY / HAS_TASK"]
        P8["DEPENDS_ON / ASSIGNED_TO / HAS_CAPABILITY"]
        P9["25+ other planned edges"]
    end
```

---

## 8. Benchmark-Driven Additions

Azure DevOps and Jira suggest several delivery-tracking attributes that should exist on selected graph objects or on linked `ExternalArtifact` nodes:

- external numeric or string key
- project or team scope
- assignee or owner
- reporter or author
- priority
- tags or labels
- workflow state
- transition history
- parent-child and dependency link types
- external URL
- synchronization metadata

Those fields should enrich the graph but should not replace domain-native objects such as personas, journeys, screens, validation rules, messages, or API contracts.

---

## 9. Summary Statistics

| Metric | Count |
|--------|-------|
| Total taxonomy nodes | 75 |
| Tier 1 (first-class nodes) | 58 |
| Tier 2 (registry nodes) | 13 |
| Tier 3 (value objects) | 4 |
| Benchmarkable (T1 + T2) | 71 |
| Total taxonomy edge types | 106 |
| Implemented `@Node` entities | 35 |
| Implemented SDN `@Relationship` declarations | 51 |
| Cypher-only polymorphic edges | 1 (`ASSESSES`) |
| String-encoded relationships still requiring migration | 4 canonical relationships + deferred compatibility fields |
| Passing tests | 182 |
| Entities needing reshape | 1 (Gap) |
| Architecture/EA objects | 12 |
| Delivery & Execution objects | 7 |

**Metric split:** This catalog tracks both the **approved design taxonomy** (`75 / 106 / 71`) and the **current implementation baseline** (`35 / 51 / 1 / 182`). Keep those families separate when reporting progress.
