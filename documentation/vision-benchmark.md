# Vision Benchmark

**Status:** Draft
**Purpose:** Score the current state of Design Hub's graph model across 8 dimensions in two separate baselines, identify gaps, and prioritize remediation.

**Related documents:**

- `modeling-taxonomy.md` (tier classification rules, string-to-edge migration map)
- `graph-object-catalog.md` (full 75-node taxonomy with 106 edge types and 71 benchmarkable nodes)
- `implementation-readiness-graph-model.md` (status, readiness, and completeness governance)
- `product-vision.md` (product thesis, users, outcomes, north-star queries)
- `azure-jira-benchmark.md` (Azure DevOps and Jira field and relationship analysis)
- `architecture-blueprint.md` (system architecture and integration model)

---

## 1. Two Baselines

This benchmark scores two separate baselines because they measure different concerns.

| Baseline | Scope | Ground Truth Source | What It Scores |
|----------|-------|--------------------|--------------------|
| **Domain benchmark** | EMSIST source completeness and traversability | `2026-03-13-screen-flow-playground-remediation.md`, `CONSOLIDATED-STORY-INVENTORY.md` | Personas, journeys, screens, interactions, channels, permissions, outcomes, messages, error codes, confirmation dialogs, rules, validations, API contracts, data entities, test cases |
| **Delivery-tool benchmark** | Azure DevOps and Jira interoperability | `azure-jira-benchmark.md`, official Azure Boards and Jira Cloud documentation | External work-item fields, dependency link types, hierarchy mapping, sync metadata, ExternalArtifact links |

**Why separate?**

- Domain benchmark tells you if Channel, JourneyStep, ConfirmationDialog, InteractionOutcome are modeled correctly for product and UX traversal.
- Delivery-tool benchmark tells you if work items, dependencies, and sync metadata are captured for delivery-system integration.
- A system could score GREEN on delivery interoperability while completely missing journey semantics, or vice versa.

---

## 2. Eight Benchmark Dimensions

| # | Dimension | Baseline | What It Measures |
|---|-----------|----------|------------------|
| 1 | Documentation completeness | Domain | Are all 71 benchmarkable nodes specified with typed attributes in the catalog? |
| 2 | Implementation completeness | Domain | Do Neo4j entities exist in code? Includes reshape detection via current-to-target mapping. |
| 3 | Attribute depth | Domain | Code attributes / target spec attributes per implemented entity |
| 4 | Relationship coverage | Domain | `[EDGE]` count / total target relationships per entity |
| 5 | Queryability and traversability | Domain | Can key queries execute via edge walks, not string parsing? |
| 6 | Source traceability | Domain | Does each artifact link to SourceReference? |
| 7 | Delivery-tool interoperability | Delivery | Are ExternalArtifact links modeled for Azure DevOps and Jira sync? |
| 8 | UX implementation support | Both | Do frontend models and API responses expose what the UI needs? |

---

## 3. Dimension Scoring

### 3.1 Documentation Completeness

**Question:** Are all 71 benchmarkable objects (58 T1 + 13 T2) specified in `graph-object-catalog.md` with typed attributes, constraints, and relationship tables?

| Category | Total | Documented | Coverage |
|----------|-------|-----------|----------|
| Strategic & Governance (T1) | 9 | 9 | 100% |
| Business & Experience (T1) | 7 | 7 | 100% |
| Delivery & Execution (T1) | 7 | 7 | 100% |
| Requirement & Design (T1) | 10 | 10 | 100% |
| Engineering (T1) | 9 | 9 | 100% |
| Architecture & EA (T1) | 12 | 12 | 100% |
| Cross-cutting (T1) | 4 | 4 | 100% |
| Registry (T2) | 13 | 13 | 100% |
| **Total** | **71** | **71** | **100%** |

**Score: GREEN** — All 71 benchmarkable nodes are documented with typed attributes and relationship tables in the catalog.

**Current approved taxonomy baseline:** 75 total nodes, 106 edge types, 71 benchmarkable. This includes the agent-ready additions, the operational near-zero-drift additions (`AgentPolicy`, `EvidenceRecord`), and the capability/project meta-model additions (`Assessment`, `RequirementPortfolio`, `ProjectInstance`, `Milestone`).

**Note:** Tier 3 value objects (4) are documented as part of their parent objects and are not independently scored.

---

### 3.2 Implementation Completeness

**Question:** Do Neo4j entities exist in code for the 71 benchmarkable objects?

| Status | Count | Objects |
|--------|-------|---------|
| `[IMPLEMENTED]` — direct match | 44 | All current benchmarkable code entities except Gap |
| `[IMPLEMENTED — reshape required]` | 1 | Gap (reshape to target schema) |
| `[PLANNED]` — no code entity | 26 | Remaining benchmarkable T1/T2 objects |

Tier 3 value objects (not counted in 71):

| Status | Count | Objects |
|--------|-------|---------|
| `[IMPLEMENTED]` | 3 | Effect, EntryMode, ContentElement |
| `[PLANNED]` | 1 | InteractionOutcome |

**Implementation ratio:** 45 benchmarkable nodes implemented / 71 benchmarkable = **63.4%**

**Score: AMBER** — The implementation baseline is now materially beyond the original seed model. The repo now has **48 `@Node` entities**, **78 SDN `@Relationship` declarations**, **1 Cypher-only polymorphic edge**, and **281 passing tests**. Coverage is still incomplete, but the graph now contains the agent-ready layer, safety layer, capability/project meta-model, registry/role split, D4 engineering entities, and the D5a BPMN-aligned process spine.

**Reshape notes:**

- The role split is now direct in code: `BusinessRole.java` and `ValidationRole.java` replace the old single `Role.java` shape.
- `Gap.java` maps to Gap (T1) with field rename required (`type` → `gapType`, add `gapId`, `sourceRefs`, relationships). Finding (T1) is `[PLANNED]` — it has no current code entity.

**Benchmark note:** The detailed attribute-depth table below still focuses on the original UX seed entities plus the two reshape cases, because those remain the biggest depth gaps even after the newer meta-model entities were added.

---

### 3.3 Attribute Depth

**Question:** For each implemented entity, how many target attributes are present in code?

| Entity | Code Attributes | Target Attributes | Depth | Key Gaps |
|--------|----------------|-------------------|-------|----------|
| Screen | 14 | 20 | 70% | Missing: `status` (universal), `readiness` flags, `wcagLevel`, `screenType`, `module` semantics |
| Journey | 6 | 12 | 50% | Missing: `status`, `readiness`, `description`, `alternatePathRefs`, `exceptionPathRefs` |
| JourneyStep | 5 | 10 | 50% | Missing: `status`, `trigger`, `expectedOutcome`, `failurePath` |
| UserStory | 5 | 12 | 42% | Missing: `status`, `readiness`, `acceptanceSummary`, `priority`, `owner`, `persona`, `labels` |
| Interaction | 8 | 14 | 57% | Missing: `status`, `interactionType`, `outcomes` (T3 InteractionOutcome) |
| Touchpoint | 5 | 10 | 50% | Missing: `status`, `touchpointType`, `description`, `preCondition` |
| Role | 4 | 6 (per target split) | 67% | Missing: `roleType` differentiation (business vs validation), `status`, `permissions` |
| Gap | 3 | 8 | 38% | Missing: `gapId`, `gapType`, `status`, `sourceRefs`, relationships |
| Effect (T3) | 5 | 5 | 100% | Complete |
| EntryMode (T3) | 2 | 2 | 100% | Complete |
| ContentElement (T3) | 4 | 4 | 100% | Complete |

**Weighted average (T1 only, excluding T3):** ~53%

**Score: AMBER** — Most implemented entities have 40-70% of target attributes but are missing universal `status`, `readiness` flags, and key domain fields.

**Critical gap: Status model migration.** All 8 implemented T1 entities use a 3-enum status model (`designStatus`, `prototypeStatus`, `deliveryStatus`) instead of the target universal 10-value `status` enum + selective `readiness` flags. This affects every entity uniformly.

---

### 3.4 Relationship Coverage

**Question:** What proportion of target relationships are implemented as Neo4j `@Relationship` edges?

| Category | Count | Percentage |
|----------|-------|-----------|
| Implemented SDN edges `[EDGE]` | 46 | 43.4% |
| Implemented Cypher edges `[CYPHER]` | 1 | 0.9% |
| String-encoded relationships `[STRING_REF]` | 9 | 8.5% |
| Planned relationships `[PLANNED]` | 50 | 47.2% |
| **Total target relationships** | **106** | |

**BLOCKING relationships breakdown:**

| Status | BLOCKING Count | Examples |
|--------|---------------|----------|
| `[EDGE]` | 7 | HAS_STEP, TARGETS, HAS_ENTRY_MODE, HAS_INTERACTION, TRANSITIONS_TO, PERFORMED_BY_PERSONA, DELIVERED_VIA_CHANNEL |
| `[STRING_REF]` | 1 | DELIVERS (storyRefs) |
| `[PLANNED]` | 12 | HAS_FEATURE, HAS_STORY, USES_SCREEN, HAS_CRITERION, PERFORMS_JOURNEY, BELONGS_TO_SCREEN, FROM_SCREEN, TO_SCREEN, BELONGS_TO_API (x3), HAS_FIELD |

**Score: AMBER** — Relationship coverage has improved materially, but a smaller residual set of string-encoded relationships still blocks full traversal and 50 approved edge types remain unimplemented.

---

### 3.5 Queryability and Traversability

**Question:** Can the 14 north-star queries execute via graph edge walks?

| # | Query | Required Path | Current Status | Score |
|---|-------|---------------|----------------|-------|
| 1 | Which journeys can persona P do? | `Persona <-[PERFORMED_BY_PERSONA]- Journey` | Persona entity exists and Journey carries `PERFORMED_BY_PERSONA` | GREEN |
| 2 | Which channels serve journey J? | `Journey -[HAS_STEP]-> JourneyStep -[STARTS_AT_TOUCHPOINT]-> Touchpoint -[DELIVERED_VIA_CHANNEL]-> Channel` | `DELIVERED_VIA_CHANNEL` and Channel exist, but `STARTS_AT_TOUCHPOINT` is still `[PLANNED]` | RED |
| 3 | Which screens can channel C reach? | `Channel <-[DELIVERED_VIA_CHANNEL]- Touchpoint -[TARGETS]-> Screen` | Channel entity exists; `DELIVERED_VIA_CHANNEL` and `TARGETS` are implemented | GREEN |
| 4 | Which permissions does screen S require? | `Screen -[HAS_INTERACTION]-> Interaction -[REQUIRES_PERMISSION]-> Permission` | Permission entity and `REQUIRES_PERMISSION` exist, but the current screen-to-interaction traversal still relies on the legacy `ON_SCREEN` side of the model | AMBER |
| 5 | What happens if interaction I fails? | `Interaction.outcomes[error].errorCodeRef -> ErrorCode` | No InteractionOutcome structure; no ErrorCode entity | RED |
| 6 | Which stories deliver screen S? | `UserStory -[DELIVERS]-> Screen` | `storyRefs` array on Screen — `[STRING_REF]` (note: direction reversed from old IMPLEMENTS_STORY) | AMBER |
| 7 | Which bugs affect screen S? | `Bug -[AFFECTS]-> Screen` | No Bug entity | RED |
| 8 | Where did artifact A come from? | `A -[HAS_SOURCE]-> SourceReference` | No SourceReference entity | RED |
| 9 | Which Jira tickets track story S? | `ExternalArtifact -[REPRESENTS]-> UserStory` | No ExternalArtifact entity | RED |
| 10 | Which confirmation dialogs can interaction I trigger? | `Interaction -[TRIGGERS_CONFIRMATION]-> ConfirmationDialog` | ConfirmationDialog registry and `TRIGGERS_CONFIRMATION` edge exist; legacy `confirmationCode` remains for compatibility | GREEN |
| 14 | Can story S resolve to a complete Implementation Pack? | `UserStory -[DELIVERS]-> deliverable <-[SUPPORTS_SCREEN\|EXPOSES\|OWNS_DATA_ENTITY\|ENFORCES_RULE]- ApplicationComponent` (transitive: Message via HAS_MESSAGE→Screen→SUPPORTS_SCREEN) | `[PLANNED]` — no ApplicationComponent execution metadata populated | RED |
| 15 | Which code files implement screen S? | `Screen <-[SUPPORTS_SCREEN]- ApplicationComponent -[HAS_CODE_ASSET]-> CodeAsset -[ASSET_FOR_SCREEN]-> Screen` | `[PLANNED]` — no CodeAsset entity | RED |
| 16 | Which test file verifies test case TC? | `TestCase -[LOCATED_IN]-> CodeAsset` | `[PLANNED]` — no LOCATED_IN edge | RED |
| 17 | Which conventions govern component C? | `ApplicationComponent <-[GOVERNED_BY_CONVENTION]- CodingConvention` | `[PLANNED]` — no CodingConvention entity | RED |

**Note on query numbering:** product-vision.md uses query numbers 1-13 (10 original + 1 Implementation Pack + 2 agent-ready). vision-benchmark.md uses query numbers 1-17 (10 original + 3 BPMN #11-13 + 1 Implementation Pack #14 + 3 agent-ready code-targeting #15-17). The numbering diverges because the benchmark includes BPMN-specific queries not in the north-star list.

**Summary:**

| Score | Count | Queries |
|-------|-------|---------|
| GREEN | 6 | #1, #3, #10, #11, #12, #13 |
| AMBER | 2 | #4 (permission), #6 (stories) — partial edge walk, partial string ref |
| RED | 9 | #2, #5, #7, #8, #9, #14, #15, #16, #17 |

**Score: AMBER** — Six queries can now execute as full edge walks, including confirmation-dialog and BPMN process traversal. Two more have partial coverage (`REQUIRES_PERMISSION` and `storyRefs` remain mixed edge/string paths). Nine queries remain blocked by missing entities, missing edge families, or unpopulated implementation-pack metadata. Queries #11-#13 cover BPMN process traversal. Query #14 covers Implementation Pack resolution. Queries #15-#17 cover agent-ready code-targeting (code files, test file location, convention governance).

**AMBER scoring rationale:**

- **Query #4** scores AMBER because Permission and `REQUIRES_PERMISSION` now exist, but the current screen-to-interaction traversal still depends on the legacy `ON_SCREEN` side of the model. The query can partially resolve "which interactions are on screen S?" and "which permissions do they require?" but not yet as a clean end-to-end canonical edge walk.
- **Query #6** scores AMBER because Screen and UserStory both exist as entities, but the link between them is `storyRefs: List<String>` — a string array, not a graph edge. The target edge is `DELIVERS` (UserStory→Screen), replacing the old `IMPLEMENTS_STORY` (Screen→UserStory) with reversed direction. The query can be answered by string matching but not by edge traversal.

**Tier 3 benchmark note (frozen decision):**

- **Query #5** (InteractionOutcome): InteractionOutcome is Tier 3. Even if the `outcomes` structure existed on Interaction, the benchmark would score the embedded-to-registry hop (`errorCodeRef` → ErrorCode) as AMBER, not GREEN. Currently it scores RED because neither the embedded structure nor the registry entity exists.
- **Query #2/3** (Channel via EntryMode): EntryMode is Tier 3. Channel traversal is modeled as `Touchpoint -[DELIVERED_VIA_CHANNEL]-> Channel`. That parent edge now exists, which is why query #3 is GREEN. Query #2 remains RED only because `JourneyStep -[STARTS_AT_TOUCHPOINT]-> Touchpoint` is still missing.

---

### 3.6 Source Traceability

**Question:** Do implementation-driving artifacts link to SourceReference?

| Artifact Type | Has SourceReference Edge? | Status |
|--------------|--------------------------|--------|
| Screen | No | `[PLANNED]` |
| Journey | No | `[PLANNED]` |
| JourneyStep | No | `[PLANNED]` |
| UserStory | No | `[PLANNED]` |
| Interaction | No | `[PLANNED]` |
| Touchpoint | No | `[PLANNED]` |
| ApiContract | No (entity does not exist) | `[PLANNED]` |
| DataEntity | No (entity does not exist) | `[PLANNED]` |

**Score: RED** — SourceReference entity does not exist. No artifact has source traceability edges. All `[PLANNED]`.

---

### 3.7 Delivery-Tool Interoperability

**Question:** Can Design Hub interoperate with Azure DevOps and Jira for work-item tracking?

| Capability | Required | Status |
|-----------|----------|--------|
| ExternalArtifact entity | Yes | `[PLANNED]` — entity does not exist |
| External key and URL fields | Yes | `[PLANNED]` |
| System discriminator (AZURE_DEVOPS, JIRA) | Yes | `[PLANNED]` |
| Sync status and timestamp | Yes | `[PLANNED]` |
| REPRESENTS link (ExternalArtifact → domain object) | Yes | `[PLANNED]` |
| PARENT_OF / CHILD_OF hierarchy links | Yes | `[PLANNED]` |
| DEPENDS_ON / BLOCKS dependency links | Yes | `[PLANNED]` |
| Priority, owner, labels on delivery-relevant objects | Recommended | Partially present (`storyNumber` on UserStory) |

**Score: RED** — No delivery-tool interoperability exists. ExternalArtifact and all sync infrastructure are `[PLANNED]`.

---

### 3.8 Agent Readiness

**Question:** Can coding agents resolve UserStories to complete Implementation Packs?

| Metric | Current | Target |
|--------|---------|--------|
| Stories with DELIVERS edges | 0% | 100% |
| Deliverables resolving to ApplicationComponent | 0% | >= 80% |
| ApplicationComponents with frameworkFamily populated | 0% | 100% |
| ApplicationComponents with modulePath populated | 0% | 100% |
| ApplicationComponents with effective testCommand | 0% | 100% |
| MCR-STORY-AGENT-READY-001 pass rate | 0% | >= 80% |

**Score: RED** — No ApplicationComponent execution metadata exists. Implementation Pack resolution is entirely `[PLANNED]`.

---

### 3.9 UX Implementation Support

**Question:** Do frontend models and API responses expose what the UI needs for the three-column shell (sidebar, canvas, detail panel)?

| UI Concern | Required Backend Support | Status |
|-----------|------------------------|--------|
| Sidebar: screen list with module grouping | Screen entity with `module`, `label` | Partial — Screen has `module` and `label` |
| Sidebar: persona/journey navigation | Persona → Journey → JourneyStep traversal | `[PARTIAL]` — Persona entity and `PERFORMED_BY_PERSONA` edge exist, but dedicated persona/journey sidebar UX is still pending |
| Canvas: graph visualization of screen relationships | Screen → Screen transitions, Screen → Interaction | TRANSITIONS_TO `[EDGE]`, HAS_INTERACTION `[EDGE]` |
| Detail panel: linked stories for selected screen | Screen → UserStory | Graph: `[STRING_REF]` — storyRefs array. API: **resolved** — ScreenResponse returns `stories[]` as `UserStoryResponse` objects via in-memory lookup. Frontend prefers resolved objects. |
| Detail panel: linked roles for selected screen | Screen → BusinessRole | Graph: `[STRING_REF]` — roleKeys array. API: **resolved** — ScreenResponse returns `roles[]` as `RoleResponse` objects via in-memory lookup. Frontend prefers resolved objects. |
| Detail panel: interactions with permissions | Interaction → Permission | `[EDGE]` — `REQUIRES_PERMISSION` is implemented; legacy `permission` string remains for migration compatibility |
| Detail panel: touchpoint channels | Touchpoint → Channel | `[EDGE]` — `DELIVERED_VIA_CHANNEL` is implemented and backfilled from `EntryMode.channelId` |
| Detail panel: journey step context | JourneyStep → Screen, Touchpoint | `[PLANNED]` — no edges |
| Detail panel: error codes and confirmations | Interaction → ErrorCode, ConfirmationDialog | `[PLANNED]` / `[STRING_REF]` |
| Filtering: by channel, persona, role | Channel, Persona, BusinessRole entities as filter facets | `[PARTIAL]` — entities now exist, but dedicated filter UI and traversal views are still pending |

**Score: AMBER** — The canvas has edge support (transitions, interactions on screens). The Screen API already returns resolved `stories[]` and `roles[]` via application-level lookup maps in `ScreenController`, and the frontend prefers these resolved objects. However, the graph model underneath is still `[STRING_REF]` (resolution relies on fetching all stories/roles per request, not targeted Cypher traversal), and the sidebar, journey step context, filtering facets, and registry entities (Channel, Permission, ErrorCode, ConfirmationDialog) remain unresolved.

---

**Benchmark note:** The detailed per-query statuses below are the last formal queryability capture. They predate the most recent capability/project meta-model rollout, so they should be rerun before treating the GREEN/AMBER/RED distribution as current.

## 4. Dimension Summary

| # | Dimension | Score | Rationale |
|---|-----------|-------|-----------|
| 1 | Documentation completeness | **GREEN** | All 71 benchmarkable nodes are documented with typed attributes |
| 2 | Implementation completeness | **AMBER** | 45/71 benchmarkable nodes exist in code (63.4%) |
| 3 | Attribute depth | **AMBER** | ~53% average depth on implemented entities; universal status migration pending |
| 4 | Relationship coverage | **AMBER** | 78 SDN + 1 Cypher relationship declarations are implemented; the engineering and process spines are now edge-backed, with a smaller residual string-backed set remaining |
| 5 | Queryability | **AMBER** | 6/17 GREEN, 2/17 AMBER, 9/17 RED after D5a process-spine closure |
| 6 | Source traceability | **RED** | SourceReference entity does not exist |
| 7 | Delivery-tool interoperability | **RED** | ExternalArtifact entity does not exist |
| 8 | UX implementation support | **AMBER** | Screen API resolves stories[] and roles[] via lookup maps; Persona/Channel/Permission registries now exist, but several exploration views and traversal paths are still pending |

**Overall assessment:** Documentation is complete and the implementation baseline is now substantial rather than skeletal. Design Hub is operating against a **75-node / 106-edge-type / 71-benchmarkable** target taxonomy with a current implementation baseline of **48 `@Node` entities**, **78 SDN `@Relationship` declarations**, **1 Cypher-only polymorphic edge**, and **281 passing tests**. The largest remaining gaps are the deferred string-to-edge migrations around journeys, transitions, and error-code handling; missing registries such as ErrorCode, Enum, Event, Locale, and TranslationKey; and a full benchmark rerun against the post-D5a model.

---

## 5. Queryability Test Suite

### 5.1 Test Definitions

Each query is scored GREEN (full edge walk) / AMBER (partial — some edges, some string refs) / RED (string parsing or entity missing).

#### Query 1: Which journeys can persona P do?

```cypher
-- TARGET: Full edge walk
MATCH (p:Persona)<-[:PERFORMED_BY_PERSONA]-(j:Journey)
WHERE p.personaId = $personaId
RETURN j.journeyId, j.title

-- CURRENT: Persona entity and PERFORMED_BY_PERSONA edge are implemented.
-- REMAINING GAP: the broader Persona → Journey → JourneyStep → Screen exploration path still lacks the journey-step edges.
```

**Score: RED** — Persona entity does not exist. Journey stores `personaId` as string. No edge walk possible.

#### Query 2: Which channels serve journey J?

```cypher
-- TARGET: Full edge walk
MATCH (j:Journey)-[:HAS_STEP]->(s:JourneyStep)-[:STARTS_AT_TOUCHPOINT]->(tp:Touchpoint)
      -[:DELIVERED_VIA_CHANNEL]->(ch:Channel)
WHERE j.journeyId = $journeyId
RETURN DISTINCT ch.channelCode, ch.name

-- CURRENT: HAS_STEP edge exists. STARTS_AT_TOUCHPOINT is [PLANNED]. channelId is string in EntryMode.
```

**Score: RED** — Only 1 of 3 required edges exists.

#### Query 3: Which screens can channel C reach?

```cypher
-- TARGET: Full edge walk
MATCH (ch:Channel)<-[:DELIVERED_VIA_CHANNEL]-(tp:Touchpoint)-[:TARGETS]->(scr:Screen)
WHERE ch.channelCode = $channelCode
RETURN DISTINCT scr.surfaceId, scr.label

-- CURRENT: TARGETS and DELIVERED_VIA_CHANNEL are implemented. Query #3 is GREEN.
```

**Score: GREEN** — Channel traversal is now possible through `Channel <- DELIVERED_VIA_CHANNEL - Touchpoint - TARGETS -> Screen`.

#### Query 4: Which permissions does screen S require?

```cypher
-- TARGET: Full edge walk
MATCH (scr:Screen)-[:HAS_INTERACTION]->(i:Interaction)-[:REQUIRES_PERMISSION]->(perm:Permission)
WHERE scr.surfaceId = $surfaceId
RETURN DISTINCT perm.permissionKey

-- CURRENT: HAS_INTERACTION exists and Permission is a registry entity, but the canonical screen-to-interaction path still depends on the legacy ON_SCREEN side for some repository loading paths.
-- WORKAROUND: MATCH (scr:Screen)-[:HAS_INTERACTION]->(i:Interaction) WHERE scr.surfaceId = $surfaceId RETURN DISTINCT i.permission, i.requiresPermission
```

**Score: AMBER** — Interaction-to-permission traversal is edge-backed, but the screen-to-interaction hop is not yet a clean canonical edge walk everywhere in the runtime path.

#### Query 5: What happens if interaction I fails?

```cypher
-- TARGET: Embedded traversal + registry hop
MATCH (i:Interaction)
WHERE i.interactionId = $interactionId
WITH i, i.outcomes.error AS errorOutcome
OPTIONAL MATCH (ec:ErrorCode) WHERE ec.code = errorOutcome.errorCodeRef
RETURN errorOutcome, ec

-- CURRENT: No outcomes structure on Interaction. No ErrorCode entity.
```

**Score: RED** — InteractionOutcome structure does not exist. ErrorCode entity does not exist.

**Tier 3 note:** Even when implemented, this query traverses an embedded value object (InteractionOutcome), scoring AMBER at best due to the embedded-to-registry hop. Promotion to GREEN would require InteractionOutcome becoming Tier 1.

#### Query 6: Which stories deliver screen S?

```cypher
-- TARGET: Full edge walk (direction: UserStory → Screen via DELIVERS)
MATCH (us:UserStory)-[:DELIVERS]->(scr:Screen)
WHERE scr.surfaceId = $surfaceId
RETURN us.storyId, us.label

-- CURRENT: storyRefs is List<String> on Screen. UserStory entity exists.
-- WORKAROUND: MATCH (scr:Screen) WHERE scr.surfaceId = $surfaceId
--             UNWIND scr.storyRefs AS ref
--             MATCH (us:UserStory) WHERE us.storyId = ref
--             RETURN us
```

**Score: AMBER** — Both entities exist. Link is a string array, not an edge. Workaround requires UNWIND + string matching. Note: direction reversed from old IMPLEMENTS_STORY (was Screen→UserStory, now UserStory→Screen via DELIVERS).

#### Query 7: Which bugs affect screen S?

```cypher
-- TARGET: Full edge walk
MATCH (b:Bug)-[:AFFECTS]->(scr:Screen)
WHERE scr.surfaceId = $surfaceId
RETURN b.bugId, b.summary, b.severity

-- CURRENT: No Bug entity exists.
```

**Score: RED** — Bug entity does not exist.

#### Query 8: Where did artifact A come from?

```cypher
-- TARGET: Full edge walk
MATCH (a)-[:HAS_SOURCE]->(sr:SourceReference)
WHERE a.surfaceId = $artifactId OR a.storyId = $artifactId
RETURN sr.sourceType, sr.documentPath, sr.lineReference

-- CURRENT: No SourceReference entity exists.
```

**Score: RED** — SourceReference entity does not exist.

#### Query 9: Which Jira tickets track story S?

```cypher
-- TARGET: Full edge walk
MATCH (ea:ExternalArtifact)-[:REPRESENTS]->(us:UserStory)
WHERE us.storyId = $storyId AND ea.system = 'JIRA'
RETURN ea.key, ea.url, ea.syncStatus

-- CURRENT: No ExternalArtifact entity exists.
```

**Score: RED** — ExternalArtifact entity does not exist.

#### Query 10: Which confirmation dialogs can interaction I trigger?

```cypher
-- TARGET: Full edge walk
MATCH (i:Interaction)-[:TRIGGERS_CONFIRMATION]->(cd:ConfirmationDialog)
WHERE i.interactionId = $interactionId
RETURN cd.dialogId, cd.triggerAction, cd.confirmLabel, cd.cancelLabel

-- CURRENT: ConfirmationDialog registry and TRIGGERS_CONFIRMATION are implemented; legacy confirmationCode remains only for compatibility.
```

**Score: GREEN** — ConfirmationDialog registry node exists and `TRIGGERS_CONFIRMATION` is now a graph edge from Interaction.

#### Query 11: Which activities does process P contain?

```cypher
-- TARGET: Full edge walk (BPMN process traversal)
MATCH (bp:BusinessProcess)-[:HAS_FLOW_NODE]->(pa:ProcessActivity)
WHERE bp.processId = $processId
RETURN pa.activityId, pa.name, pa.activityType

-- CURRENT: BusinessProcess and ProcessActivity entities now exist and are linked via HAS_FLOW_NODE.
```

**Score: GREEN** — BPMN process traversal is now possible through `BusinessCapability -> REALIZED_BY_PROCESS -> BusinessProcess -> HAS_FLOW_NODE -> ProcessActivity`.

#### Query 12: Which gateways route process P?

```cypher
-- TARGET: Full edge walk (BPMN gateway routing)
MATCH (bp:BusinessProcess)-[:HAS_FLOW_NODE]->(pg:ProcessGateway)
WHERE bp.processId = $processId
RETURN pg.gatewayId, pg.gatewayType, pg.name

-- CURRENT: BusinessProcess and ProcessGateway entities now exist and are linked via HAS_FLOW_NODE.
```

**Score: GREEN** — Gateway routing is now traversable through `BusinessProcess -> HAS_FLOW_NODE -> ProcessGateway`.

#### Query 13: Which events trigger in process P?

```cypher
-- TARGET: Full edge walk (BPMN event triggering)
MATCH (bp:BusinessProcess)-[:HAS_FLOW_NODE]->(pe:ProcessEvent)
WHERE bp.processId = $processId
RETURN pe.eventId, pe.eventType, pe.name

-- CURRENT: BusinessProcess and ProcessEvent entities now exist and are linked via HAS_FLOW_NODE.
```

**Score: GREEN** — Event traversal is now possible through `BusinessProcess -> HAS_FLOW_NODE -> ProcessEvent`.

### 5.2 Queryability Summary

| Score | Count | % | Queries |
|-------|-------|---|---------|
| GREEN | 6 | 35% | #1 (persona journeys), #3 (channel reach), #10 (confirmation dialogs), #11 (process activities), #12 (process gateways), #13 (process events) |
| AMBER | 2 | 12% | #4 (permissions), #6 (stories) |
| RED | 9 | 53% | #2, #5, #7, #8, #9, #14, #15, #16, #17 |

---

## 6. Artifact Type Coverage Matrix

| Artifact Type | Tier | Documented | Attr Depth | Implemented | Mapping | Rel Coverage | Queryable | Notes |
|--------------|------|-----------|------------|-------------|---------|--------------|-----------|-------|
| BusinessObjective | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| Feature | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| Decision | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Assumption | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Constraint | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| SourceReference | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Finding | T1 | Yes | — | `[PLANNED]` | — | 0/3 edges | RED | No code entity |
| Bug | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| Risk | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Persona | T1 | Yes | 63% | `[IMPL]` | Direct | 0/1 edges | AMBER | Node exists; outgoing Persona-centric exploration edges still pending |
| BusinessRole | T1 | Yes | 100% | `[IMPL]` | Direct | 1/1 edge family | GREEN | Role split landed; Screen ACCESSIBLE_BY_ROLE now targets BusinessRole |
| ValidationRole | T1 | Yes | 100% | `[IMPL]` | Direct | 0/0 target | GREEN | Role split landed; resolved via RoleService union query |
| Journey | T1 | Yes | 63% | `[IMPL]` | Direct | 2/2 edges | GREEN | HAS_STEP and PERFORMED_BY_PERSONA are both implemented |
| JourneyStep | T1 | Yes | 50% | `[IMPL]` | Direct | 0/3 edges | RED | No screen, touchpoint, or interaction edges |
| Topic | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Touchpoint | T1 | Yes | 63% | `[IMPL]` | Direct | 4/4 modeled edges | GREEN | TARGETS, HAS_ENTRY_MODE, USED_BY_PERSONA, DELIVERED_VIA_CHANNEL implemented; role edge still deferred |
| UserStory | T1 | Yes | 42% | `[IMPL]` | Direct | 0/3 edges | AMBER | Entity exists but minimal attributes |
| AcceptanceCriterion | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| Rule | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| ValidationRule | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| EdgeCase | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| ExceptionCase | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Screen | T1 | Yes | 75% | `[IMPL]` | Direct | 7/8 edges | GREEN | registry/role edges implemented; story delivery remains string-backed |
| ScreenState | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Interaction | T1 | Yes | 63% | `[IMPL]` | Direct | 3/5 edges | AMBER | REQUIRES_PERMISSION implemented; confirmation and API edges remain deferred |
| Transition | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| ApiContract | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| RequestSchema | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| ResponseSchema | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| ErrorContract | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| DataEntity | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| DataField | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Integration | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| TestCase | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| ExternalArtifact | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| OpenQuestion | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| Gap | T1 | Yes | 38% | `[RESHAPE]` | Reshape | 1/2 edges | AMBER | HAS_GAP edge; missing gapId, gapType |
| Message | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| ProcessActivity | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity; renamed from ProcessStep |
| ProcessGateway | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| ProcessEvent | T1 | Yes | — | `[PLANNED]` | — | 0/2 edges | RED | No code entity |
| Task | T1 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity |
| BusinessDomain | T2 | Yes | — | `[PLANNED]` | — | 0/1 edges | RED | No code entity; T2 registry node |
| Channel | T2 | Yes | 100% | `[IMPL]` | Direct | 1/1 edge family | GREEN | Registry node exists and Touchpoint DELIVERED_VIA_CHANNEL is implemented |
| Permission | T2 | Yes | 100% | `[IMPL]` | Direct | 1/1 edge family | GREEN | Registry node exists and Interaction REQUIRES_PERMISSION is implemented |
| ErrorCode | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| ConfirmationDialog | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| Enum | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| Event | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| Locale | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| TranslationKey | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| CodeAsset | T1 | Yes | 0% | `[PLANNED]` | — | 0/7 edges | RED | Agent-ready extension — no code entity |
| QualityConstraint | T1 | Yes | 0% | `[PLANNED]` | — | 0/2 edges | RED | Agent-ready extension — no code entity |
| ImportSnapshot | T2 | Yes | 0% | `[PLANNED]` | — | 0/1 edges | RED | Agent-ready extension — no code entity |
| CodingConvention | T2 | Yes | 0% | `[PLANNED]` | — | 0/1 edges | RED | Agent-ready extension — no code entity |

---

## 7. Status Model Migration Cost

### 7.1 Current State

All implemented T1 entities use a 3-field status model:

```java
private String designStatus;     // "Not Started" | "In Progress" | "Done"
private String prototypeStatus;  // "Not Started" | "In Progress" | "Done"
private String deliveryStatus;   // "Not Started" | "In Progress" | "Done"
```

### 7.2 Target State

Universal 10-value `status` enum + selective `readiness` flags:

```java
private Status status;  // IDENTIFIED | IN_DEFINITION | DEFINED | IN_REVIEW | APPROVED |
                        // IN_IMPLEMENTATION | IMPLEMENTED | VERIFIED | DEPRECATED | RETIRED

private Readiness readiness;  // Only on implementation-driving objects
// readiness.requirementsReady, readiness.designReady, readiness.contractReady,
// readiness.frontendReady, readiness.backendReady, readiness.integrationReady, readiness.qaReady
```

### 7.3 Migration Effort

| Concern | Effort | Impact |
|---------|--------|--------|
| Status enum creation | Low | 1 new enum class |
| Readiness flag object | Low | 1 new embedded class |
| Entity field migration (8 entities) | Medium | Remove 3 String fields, add Status + optional Readiness per entity |
| Seed data migration | Medium | Translate existing seed JSON values to new enum values |
| Frontend model update | Medium | Update DTOs, display logic, filter facets |
| Backward compatibility | Low | One-time migration, no old API consumers to support |
| Status-to-status value mapping | Low | See mapping below |

### 7.4 Value Mapping

| Current 3-Field State | Target `status` |
|----------------------|-----------------|
| All three "Not Started" | `IDENTIFIED` |
| designStatus = "In Progress" | `IN_DEFINITION` |
| designStatus = "Done" | `DEFINED` |
| designStatus = "Done", prototypeStatus = "In Progress" | `IN_REVIEW` |
| designStatus = "Done", prototypeStatus = "Done" | `APPROVED` |
| deliveryStatus = "In Progress" | `IN_IMPLEMENTATION` |
| deliveryStatus = "Done" | `IMPLEMENTED` |

**Note:** The 3-field model captures parallel progress across design, prototype, and delivery. The universal status model captures sequential lifecycle. The mapping is a simplification — the parallel semantics of "design done but prototype not started" become a single point on the lifecycle. Readiness flags (`designReady`, `frontendReady`) recover the parallel dimension.

---

## 8. String-to-Edge Migration Cost

Each string-encoded relationship must become a Neo4j `@Relationship` edge. The effort includes creating the target entity (if missing), defining the relationship annotation, migrating seed data, and updating queries.

| String Field | Entity | Target Entity Status | Target Edge | Affected Queries | Effort |
|-------------|--------|---------------------|-------------|-----------------|--------|
| `storyRefs` | Screen | `[IMPLEMENTED]` UserStory exists | `DELIVERS` (UserStory→Screen) | #6 | Medium — both entities exist, create edge, migrate array to edges; note direction reversal from old IMPLEMENTS_STORY |
| `personaId` | Journey | `[IMPLEMENTED]` Persona exists | `PERFORMED_BY_PERSONA` | #1 | Closed for graph traversal; legacy field remains for migration compatibility |
| `personaIds` | Screen, Touchpoint, Interaction | `[IMPLEMENTED]` Persona exists | `USED_BY_PERSONA` | #1 | Closed for graph traversal; legacy arrays remain for migration compatibility |
| `roleKeys` | Screen, Interaction, Touchpoint | `[IMPLEMENTED]` BusinessRole exists | `ACCESSIBLE_BY_ROLE` | — | Closed for graph traversal; legacy arrays remain for migration compatibility |
| `permission` | Interaction | `[IMPLEMENTED]` Permission exists | `REQUIRES_PERMISSION` | #4 | Closed for graph traversal; legacy field remains for migration compatibility |
| `channelId` | EntryMode (in Touchpoint) | `[IMPLEMENTED]` Channel exists | `DELIVERED_VIA_CHANNEL` | #2, #3 | Closed for graph traversal; legacy field remains in EntryMode for compatibility |
| `apiCalls` | Interaction | `[IMPLEMENTED]` ApiContract exists | `CALLS_API` | — | Closed for graph traversal; legacy field remains for migration compatibility |
| `confirmationCode` | Interaction | `[IMPLEMENTED]` ConfirmationDialog exists | `TRIGGERS_CONFIRMATION` | #10 | Closed for graph traversal; legacy field remains for migration compatibility |
| `interactionRef` | JourneyStep | `[IMPLEMENTED]` Interaction exists | `EXECUTES_INTERACTION` | — | Low — both entities exist, create edge |

### 8.1 Migration Priority

| Priority | Migrations | Rationale |
|----------|-----------|-----------|
| P0 | `storyRefs` → DELIVERS (UserStory→Screen), `interactionRef` → EXECUTES_INTERACTION | Both source and target entities exist — lowest cost, highest value for queryability |
| P1 | `personaId`, `personaIds`, `roleKeys`, `channelId`, `permission`, `apiCalls`, `confirmationCode` | Closed for graph traversal; optional cleanup remains to retire legacy compatibility fields after API consumers stop depending on them |
| P2 | — | Reserved for future cleanup once `DELIVERS` and `EXECUTES_INTERACTION` are migrated off the remaining string fields |
| P3 | — | No additional string-to-edge migration family is currently queued beyond the open P0 items |

---

## 9. Target API Projection Expectations

This section captures what API responses SHOULD expose when the graph model reaches its target state. These are benchmark-level projection expectations, not endpoint specifications. Full endpoint shapes are deferred to `architecture-blueprint.md`.

### 9.1 Screen Response Projection

When a client requests a Screen, the response should expose linked graph objects as resolved projections, not raw string references.

| Field | Current Response | Target Projection | Source |
|-------|-----------------|-------------------|--------|
| `stories` | Graph: `storyRefs` string array. API: **already resolved** — `ScreenResponse` returns `stories: UserStoryResponse[]` via in-memory lookup in `ScreenController`. Frontend prefers resolved objects in `design-hub-state.service.ts`. | `stories: [{ storyId, label, status }]` (resolved via graph edge walk, not in-memory lookup) | `DELIVERS` reverse edge walk (UserStory→Screen) |
| `roles` | Graph: `roleKeys` string array. API: **already resolved** — `ScreenResponse` returns `roles: RoleResponse[]` via in-memory lookup in `ScreenController`. Frontend prefers resolved objects. | `roles: [{ roleKey, displayName, roleGroup }]` (resolved via graph edge walk, not in-memory lookup) | `ACCESSIBLE_BY_ROLE` edge walk |
| `transitions` | Implicit via `TRANSITIONS_TO` edge | `transitions: [{ targetSurfaceId, targetLabel }]` (resolved targets) | `TRANSITIONS_TO` edge walk |
| `interactions` | Not projected | `interactions: [{ interactionId, element, trigger }]` (resolved objects) | `HAS_INTERACTION` edge walk (Screen→Interaction) |
| `personas` | `personaIds: ["PER-001"]` (string array) | `personas: [{ personaId, name, archetype }]` (resolved objects) | `USED_BY_PERSONA` edge walk |
| `gaps` | Available via `HAS_GAP` edge | `gaps: [{ gapId, gapType, severity }]` (resolved objects) | `HAS_GAP` edge walk |
| `content` | Available via `HAS_CONTENT` edge | `content: [{ element, type, orderIndex }]` (resolved objects) | `HAS_CONTENT` edge walk |

**Benchmark test:** A Screen response passes the projection benchmark when ALL linked object arrays return resolved objects with at least `id` + `label`/`name` + `status`, not string identifiers.

### 9.2 Touchpoint Response Projection

| Field | Current Response | Target Projection | Source |
|-------|-----------------|-------------------|--------|
| `screen` | Via `TARGETS` edge | `screen: { surfaceId, label, module }` (resolved object) | `TARGETS` edge walk |
| `channels` | `channelId` string in embedded EntryMode | `channels: [{ channelCode, name, entryMechanism }]` (resolved objects with mechanism from EntryMode) | `DELIVERED_VIA_CHANNEL` edge walk + EntryMode embedded data |
| `personas` | `personaIds: ["PER-001"]` (string array) | `personas: [{ personaId, name }]` (resolved objects) | `USED_BY_PERSONA` edge walk |

**Benchmark test:** A Touchpoint response passes when `channels[]` returns resolved Channel objects with their delivery mechanism, not bare `channelId` strings.

### 9.3 Interaction Response Projection

| Field | Current Response | Target Projection | Source |
|-------|-----------------|-------------------|--------|
| `permission` | `permission: "ADMIN"` (string) | `permission: { permissionKey, displayName, level }` (resolved object) | `REQUIRES_PERMISSION` edge walk |
| `apiContracts` | `apiCalls: ["POST /api/x"]` (string array) | `apiContracts: [{ contractId, method, path, status }]` (resolved objects) | `CALLS_API` edge walk |
| `confirmationDialog` | `confirmationCode: "DEF-C-001"` (string) | `confirmationDialog: { dialogId, triggerAction, confirmLabel, cancelLabel, consequenceText }` (resolved object) | `TRIGGERS_CONFIRMATION` edge walk |
| `outcomes` | Not present | `outcomes: { success, error: { description, errorCode: { code, severity, messageText } }, loading }` (embedded T3 with registry hop) | Embedded InteractionOutcome + `errorCodeRef` → ErrorCode |
| `screen` | Via `HAS_INTERACTION` reverse edge (Screen→Interaction) | `screen: { surfaceId, label }` (resolved object) | `HAS_INTERACTION` reverse edge walk |
| `effects` | Via `HAS_EFFECT` edge | `effects: [{ type, target, targetMode }]` (resolved objects) | `HAS_EFFECT` edge walk |

**Benchmark test:** An Interaction response passes when `permission`, `apiContracts[]`, and `confirmationDialog` return resolved objects, and `outcomes` includes the embedded structure with resolved ErrorCode where applicable.

### 9.4 Journey Response Projection

| Field | Current Response | Target Projection | Source |
|-------|-----------------|-------------------|--------|
| `persona` | `personaId: "PER-R04-001"` (string) | `persona: { personaId, name, archetype, department }` (resolved object) | `PERFORMED_BY_PERSONA` edge walk |
| `steps` | Via `HAS_STEP` edge | `steps: [{ stepId, label, orderIndex, screen: { surfaceId, label }, touchpoints: [{ touchpointId, label }] }]` (resolved with nested projections) | `HAS_STEP` → JourneyStep → `USES_SCREEN` → Screen, `STARTS_AT_TOUCHPOINT` → Touchpoint |

**Benchmark test:** A Journey response passes when `persona` is a resolved object (not a string ID), and `steps[]` includes resolved screen and touchpoint projections per step.

### 9.5 Projection Benchmark Scoring

| Criterion | GREEN | AMBER | RED |
|-----------|-------|-------|-----|
| Linked objects resolved | All linked arrays return `{ id, label, status }` minimum | Some resolved, some still string IDs | All string IDs |
| Registry hops resolved | T2 registries return full objects | T2 registries return code only | T2 references are bare strings |
| Nested projections | Steps include screen and touchpoint | Steps include screen only | Steps are flat IDs |
| Value objects projected | InteractionOutcome, Effect, EntryMode, ContentElement embedded with structure | Partially structured | Missing or flat |

**Current overall projection score: AMBER** — Screen responses already resolve `stories[]` and `roles[]` via application-level lookup maps (`ScreenController` builds in-memory maps from `roleService.getAll()` and `userStoryService.getAll()`). The frontend in `design-hub-state.service.ts` prefers these resolved objects with fallback to string-ref resolution. Additionally, `HAS_STEP`, `HAS_INTERACTION`, `TARGETS`, `HAS_EFFECT`, `HAS_CONTENT`, `HAS_GAP`, and `TRANSITIONS_TO` edges allow graph-backed projections. However, the stories/roles resolution is application-level (not graph-edge-backed), and all other linked objects (personas, channels, permissions, apiContracts, confirmationDialogs, errorCodes) remain as string IDs or are entirely missing.

---

## 10. Gap Prioritization and Recommendations

### 10.1 Priority 0 — Critical Path (Enables queryability)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| No universal `status` enum | Every entity uses wrong status model | Create Status enum, Readiness embedded object, migrate all 8 entities |
| `storyRefs` as strings | Query #6 blocked from GREEN | Create `DELIVERS` edge (UserStory→Screen), replacing old IMPLEMENTS_STORY direction |
| `interactionRef` as string | JourneyStep → Interaction link broken | Create `EXECUTES_INTERACTION` edge |
| `personaId` / `personaIds` legacy compatibility fields | Cleanup debt after successful persona edge migration | Retire the legacy fields once all API consumers rely on `PERFORMED_BY_PERSONA` / `USED_BY_PERSONA` |

### 10.2 Priority 1 — Registry Entities (Enables filtering and facet queries)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| No `STARTS_AT_TOUCHPOINT` edge | Query #2 remains blocked even though Channel traversal is now edge-backed | Create `JourneyStep -[STARTS_AT_TOUCHPOINT]-> Touchpoint` |
| Legacy permission compatibility field on Interaction | Query #4 remains mixed-mode until the canonical screen-to-interaction path is fully cleaned up | Keep `REQUIRES_PERMISSION` as source of truth and retire the compatibility field later |
| No ErrorCode entity | Query #5 blocked | Create ErrorCode T2 registry from EMSIST 80+ codes |

### 10.3 Priority 2 — Structural Completeness (Enables full traversal spine)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| No `USES_SCREEN` edge | JourneyStep → Screen traversal broken | Create edge after both entities exist |
| No `STARTS_AT_TOUCHPOINT` edge | JourneyStep → Touchpoint traversal broken | Create edge, canonical direction JourneyStep → Touchpoint |
| Legacy `journeyStepRefs` strings on Screen | Screen → JourneyStep traversal still depends on string matching | Create canonical screen/journey-step edge or introduce a dedicated process/journey linking edge |
| InteractionOutcome not modeled | Query #5 has no outcome structure | Add embedded InteractionOutcome to Interaction |

### 10.4 Priority 3 — Strategic & Governance (Enables upstream context)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| No BusinessObjective entity | No upstream business context in graph | Create entity; seed from EMSIST source |
| No SourceReference entity | Query #8 blocked; zero traceability | Create entity with SUPPORTS edge to any T1 |
| No ExternalArtifact entity | Query #9 blocked; no delivery-tool integration | Create entity per `azure-jira-benchmark.md` spec |

### 10.5 Priority 4 — Engineering Layer (Enables contract-level queries)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| No ErrorCode entity | Interaction failure paths still cannot resolve to a typed registry | Create ErrorCode entity and wire `InteractionOutcome.errorCodeRef` migration |
| No `LOCATED_IN` edge | Query #16 cannot resolve test cases to code files | Add `TestCase -[LOCATED_IN]-> CodeAsset` |
| No CodingConvention entity | Query #17 cannot resolve convention governance | Create CodingConvention entity and scope edges to ApplicationComponent |

---

## 11. Scoring Formula Reference

This benchmark uses the severity-weighted `completenessScore` formula defined in `implementation-readiness-graph-model.md`:

```
completenessScore = (
    sum(satisfied_blocking_edges * 3) +
    sum(satisfied_optional_edges * 1) +
    sum(populated_required_attrs * 2) +
    sum(populated_optional_attrs * 1)
) / (
    sum(total_blocking_edges * 3) +
    sum(total_optional_edges * 1) +
    sum(total_required_attrs * 2) +
    sum(total_optional_attrs * 1)
) * 100
```

- Weights: BLOCKING edge = 3x, required attribute = 2x, optional = 1x
- Only `[EDGE]` counts as "satisfied" — `[STRING_REF]` counts as 0
- Threshold: RED (<40%), AMBER (40-79%), GREEN (>=80%)
- **Critical distinction:** `completenessScore` is a severity-weighted diagnostic metric. It does NOT replace or contribute to `status` or `readiness` flags. It measures structural completeness of the graph model, not governance state.

---

## 12. Benchmark Evolution

This benchmark should be re-scored when:

1. New Neo4j entities are created
2. String references are migrated to graph edges
3. Status model is migrated to universal enum
4. Registry entities (T2) are populated
5. Tier promotions or demotions occur (per `modeling-taxonomy.md` section 12)

**Target state for 1.0 release:**

| Dimension | Target Score |
|-----------|-------------|
| Documentation completeness | GREEN (maintain) |
| Implementation completeness | AMBER (>50% of 71 benchmarkable entities) |
| Attribute depth | GREEN (>80% average depth) |
| Relationship coverage | AMBER (>50% as edges, 0 BLOCKING string refs) |
| Queryability | AMBER (>50% queries at GREEN or AMBER) |
| Source traceability | AMBER (SourceReference entity exists, key artifacts linked) |
| Delivery-tool interoperability | AMBER (ExternalArtifact entity exists, basic sync) |
| UX implementation support | AMBER (sidebar and detail panel projections resolved) |
