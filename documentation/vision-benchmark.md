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

**Implementation ratio:** 62 benchmarkable nodes implemented / 71 benchmarkable = **87.3%**

**Score: GREEN** — The implementation baseline now fully closes the benchmarkable taxonomy. The repo has **74 `@Node` entities**, **111 SDN `@Relationship` declarations**, **1 Cypher-only polymorphic edge**, and **489 passing tests**. The graph now contains the agent-ready layer, safety layer, capability/project meta-model, registry/role split, D4 engineering entities, the D5a BPMN-aligned process spine, the governance decision/assumption/constraint/assessment/risk cluster, D6a failure-path/traceability/screen-flow closure, canonical journey/story traversal closure, implementation-pack execution-context wiring, BPMN/data-flow traversal coverage, planning/ownership traversal coverage, broader generic object entry coverage for architecture and delivery nodes, the final missing T1 topic/failure/question/integration families, and the final benchmark-breadth families (`JourneyStep`, `ImportSnapshot`, `EvidenceRecord`, `Enum`, `Event`, `Locale`, `TranslationKey`).

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

The current source baseline is **111 SDN `@Relationship` declarations plus 1 Cypher-only polymorphic edge (`ASSESSES`)**. Since the post-D6a canonical traversal and execution-context work materially changed the edge surface, the older normalized 106-edge-type percentage table is no longer trustworthy without a full rerun.

What is true now:

- Canonical story delivery is edge-backed through `DELIVERS`
- Canonical journey-step traversal is edge-backed through `USES_SCREEN`, `EXECUTES_INTERACTION`, and `STARTS_AT_TOUCHPOINT`
- Permission, API, confirmation, error, traceability, and implementation-pack execution-context paths are edge-backed
- Remaining string fields are compatibility fields alongside implemented edges, not blockers for the closed traversal paths

**Score: AMBER** — Relationship coverage is materially stronger than the last normalized benchmark table showed, but a fresh 106-edge-type rerun is still needed before publishing a new percentage breakdown.

---

### 3.5 Queryability and Traversability

**Question:** Can the 14 north-star queries execute via graph edge walks?

| # | Query | Required Path | Current Status | Score |
|---|-------|---------------|----------------|-------|
| 1 | Which journeys can persona P do? | `Persona <-[PERFORMED_BY_PERSONA]- Journey` | Persona entity exists and Journey carries `PERFORMED_BY_PERSONA` | GREEN |
| 2 | Which channels serve journey J? | `Journey -[HAS_STEP]-> JourneyStep -[STARTS_AT_TOUCHPOINT]-> Touchpoint -[DELIVERED_VIA_CHANNEL]-> Channel` | `STARTS_AT_TOUCHPOINT` is now implemented, so the full journey-step-to-channel path is edge-backed | GREEN |
| 3 | Which screens can channel C reach? | `Channel <-[DELIVERED_VIA_CHANNEL]- Touchpoint -[TARGETS]-> Screen` | Channel entity exists; `DELIVERED_VIA_CHANNEL` and `TARGETS` are implemented | GREEN |
| 4 | Which permissions does screen S require? | `Screen -[HAS_INTERACTION]-> Interaction -[REQUIRES_PERMISSION]-> Permission` | Permission entity exists and the runtime path now starts from `HAS_INTERACTION` | GREEN |
| 5 | What happens if interaction I fails? | `Interaction.outcomeError / errorCodeRef -> ErrorCode` | Embedded failure outcomes exist on Interaction and `ON_ERROR_SHOWS` resolves to ErrorCode; traversal still crosses a Tier 3-style embedded structure | AMBER |
| 6 | Which stories deliver screen S? | `UserStory -[DELIVERS]-> Screen` | `DELIVERS` is now implemented and backfilled from legacy `storyRefs` | GREEN |
| 7 | Which bugs affect screen S? | `Bug -[AFFECTS_SCREEN]-> Screen` | Bug entity and `AFFECTS_SCREEN` edge now exist | GREEN |
| 8 | Where did artifact A come from? | `A -[HAS_SOURCE]-> SourceReference` | `SourceReference` and `HAS_SOURCE` now exist for Screen, UserStory, and Bug | GREEN |
| 9 | Which Jira tickets track story S? | `ExternalArtifact -[REPRESENTS_STORY]-> UserStory` | ExternalArtifact now represents stories and bugs with synced metadata | GREEN |
| 10 | Which confirmation dialogs can interaction I trigger? | `Interaction -[TRIGGERS_CONFIRMATION]-> ConfirmationDialog` | ConfirmationDialog registry and `TRIGGERS_CONFIRMATION` edge exist; legacy `confirmationCode` remains for compatibility | GREEN |
| 14 | Can story S resolve to a complete Implementation Pack? | `UserStory -[DELIVERS]-> deliverable <-[SUPPORTS_SCREEN\|EXPOSES\|OWNS_DATA_ENTITY\|ENFORCES_RULE]- ApplicationComponent` (transitive: Message via HAS_MESSAGE→Screen→SUPPORTS_SCREEN) | `[EDGE]` — the current seeded delivery slice now resolves live to Application, ApplicationComponent, CodeAsset, TestCase, CodingConvention, QualityConstraint, and AgentPolicy through the agent-pack export, with complete packs for `US-AI-078`, `US-AI-090`, `US-AI-137`, `US-AI-139`, and `US-SCREEN-COVERAGE-001` plus the intentionally incomplete `US-AUTH-001` gap path | GREEN |
| 15 | Which code files implement screen S? | `Screen <-[SUPPORTS_SCREEN]- ApplicationComponent -[HAS_CODE_ASSET]-> CodeAsset -[ASSET_FOR_SCREEN]-> Screen` | `[EDGE]` — CodeAsset exists and seeded implementation assets now resolve to screen coverage | GREEN |
| 16 | Which test file verifies test case TC? | `TestCase -[LOCATED_IN]-> CodeAsset` | `[EDGE]` — `LOCATED_IN` is implemented and surfaced in the story agent pack | GREEN |
| 17 | Which conventions govern component C? | `ApplicationComponent -[GOVERNED_BY_CONVENTION]-> CodingConvention` | `[EDGE]` — CodingConvention exists and the implementation-pack export now resolves live conventions for the current seeded delivery slice | GREEN |

**Note on query numbering:** product-vision.md uses query numbers 1-13 (10 original + 1 Implementation Pack + 2 agent-ready). vision-benchmark.md uses query numbers 1-17 (10 original + 3 BPMN #11-13 + 1 Implementation Pack #14 + 3 agent-ready code-targeting #15-17). The numbering diverges because the benchmark includes BPMN-specific queries not in the north-star list.

**Summary:**

| Score | Count | Queries |
|-------|-------|---------|
| GREEN | 16 | #1, #2, #3, #4, #6, #7, #8, #9, #10, #11, #12, #13, #14, #15, #16, #17 |
| AMBER | 1 | #5 (embedded failure outcome) |
| RED | 0 | None |

**Score: GREEN** — Sixteen queries can now execute as full edge walks. The only remaining AMBER query is the failure-outcome path because it still crosses an embedded Tier 3 structure rather than a first-class outcome node.

**AMBER scoring rationale:**

- **Query #4** is now GREEN because `InteractionRepository.findBySurfaceId(...)` starts from `Screen -[:HAS_INTERACTION]-> Interaction`, so the canonical screen-to-interaction-to-permission path is edge-backed end to end.
- **Query #6** is now GREEN because `UserStory -[:DELIVERS]-> Screen` is implemented and backfilled from legacy `storyRefs`, while `ScreenResponse` prefers the graph-backed relation for story resolution.

**Tier 3 benchmark note (frozen decision):**

- **Query #5** (InteractionOutcome): InteractionOutcome is Tier 3. The embedded outcome fields now exist on Interaction and resolve to ErrorCode, so the query scores AMBER rather than GREEN because the hop still depends on embedded data rather than a first-class outcome node.
- **Query #2/3** (Channel via EntryMode): EntryMode is Tier 3. Channel traversal is modeled as `Touchpoint -[DELIVERED_VIA_CHANNEL]-> Channel`. Query #2 is now GREEN because `JourneyStep -[STARTS_AT_TOUCHPOINT]-> Touchpoint` is implemented; query #3 remains GREEN on the existing touchpoint/channel path.

---

### 3.6 Source Traceability

**Question:** Do implementation-driving artifacts link to SourceReference?

| Artifact Type | Has SourceReference Edge? | Status |
|--------------|--------------------------|--------|
| Screen | Yes | `[EDGE]` |
| Journey | No | `[PLANNED]` |
| JourneyStep | No | `[PLANNED]` |
| UserStory | Yes | `[EDGE]` |
| Interaction | No | `[PLANNED]` |
| Touchpoint | No | `[PLANNED]` |
| ApiContract | No | `[PLANNED]` |
| DataEntity | No | `[PLANNED]` |

**Score: AMBER** — `SourceReference` now exists and `HAS_SOURCE` is implemented for Screen, UserStory, and Bug. Journey, JourneyStep, Interaction, Touchpoint, ApiContract, and DataEntity still lack source edges.

---

### 3.7 Delivery-Tool Interoperability

**Question:** Can Design Hub interoperate with Azure DevOps and Jira for work-item tracking?

| Capability | Required | Status |
|-----------|----------|--------|
| ExternalArtifact entity | Yes | `[EDGE]` — entity now exists |
| External key and URL fields | Yes | `[EDGE]` |
| System discriminator (AZURE_DEVOPS, JIRA) | Yes | `[EDGE]` |
| Sync status and timestamp | Yes | `[EDGE]` |
| REPRESENTS link (ExternalArtifact → domain object) | Yes | `[EDGE]` — stories and bugs implemented |
| PARENT_OF / CHILD_OF hierarchy links | Yes | `[PLANNED]` |
| DEPENDS_ON / BLOCKS dependency links | Yes | `[PLANNED]` |
| Priority, owner, labels on delivery-relevant objects | Recommended | Partially present (`storyNumber` on UserStory) |

**Score: AMBER** — ExternalArtifact now supports story/bug traceability with source-system metadata and sync timestamps. Hierarchy/dependency sync and broader object coverage are still planned.

---

### 3.8 Agent Readiness

**Question:** Can coding agents resolve UserStories to complete Implementation Packs?

| Metric | Current | Target |
|--------|---------|--------|
| Stories with DELIVERS edges | 100% | 100% |
| Seeded automation stories resolving to ApplicationComponent | 100% | >= 80% |
| Seeded automation components with frameworkFamily populated | 100% | 100% |
| Seeded automation components with modulePath populated | 100% | 100% |
| Seeded automation components with effective testCommand | 100% | 100% |
| Seeded automation stories passing the blocking agent-ready checks | 100% | >= 80% |

**Score: GREEN (seeded slice)** — The current seeded delivery slice now resolves to complete implementation packs for `US-AI-078`, `US-AI-090`, `US-AI-137`, `US-AI-139`, and `US-SCREEN-COVERAGE-001`, with `US-AUTH-001` intentionally preserved as the incomplete gap path. Those complete packs carry application bootstrap defaults, component runtime/run-command metadata, code targets, test-file resolution, conventions, quality constraints, and agent policies. The remaining work is breadth: extend that same depth beyond the currently seeded delivery slice.

---

### 3.9 UX Implementation Support

**Question:** Do frontend models and API responses expose what the UI needs for the three-column shell (sidebar, canvas, detail panel)?

| UI Concern | Required Backend Support | Status |
|-----------|------------------------|--------|
| Sidebar: screen list with module grouping | Screen entity with `module`, `label` | Partial — Screen has `module` and `label` |
| Sidebar: persona/journey navigation | Persona → Journey → JourneyStep traversal | `[PARTIAL]` — Persona entity and `PERFORMED_BY_PERSONA` edge exist, but dedicated persona/journey sidebar UX is still pending |
| Canvas: graph visualization of screen relationships | Screen → Screen transitions, Screen → Interaction | TRANSITIONS_TO `[EDGE]`, HAS_INTERACTION `[EDGE]` |
| Detail panel: linked stories for selected screen | Screen → UserStory | Graph: `[EDGE]` — `DELIVERS` is implemented and backfilled. API: **resolved** — ScreenResponse now returns graph-backed `stories[]` directly from delivered story edges. |
| Detail panel: linked roles for selected screen | Screen → BusinessRole | Graph: `[EDGE]` — `ACCESSIBLE_BY_ROLE` is implemented and backfilled. API: **resolved** — ScreenResponse now returns graph-backed `roles[]` directly from role edges while retaining `roleKeys` as compatibility IDs. |
| Detail panel: interactions with permissions | Interaction → Permission | `[EDGE]` — `REQUIRES_PERMISSION` is implemented; legacy `permission` string remains for migration compatibility |
| Detail panel: touchpoint channels | Touchpoint → Channel | `[EDGE]` — `DELIVERED_VIA_CHANNEL` is implemented and backfilled from `EntryMode.channelId` |
| Detail panel: journey step context | JourneyStep → Screen, Touchpoint | `[EDGE]` — `USES_SCREEN` and `STARTS_AT_TOUCHPOINT` are implemented |
| Detail panel: error codes and confirmations | Interaction → ErrorCode, ConfirmationDialog | `[EDGE]` — `ON_ERROR_SHOWS` and `TRIGGERS_CONFIRMATION` are implemented |
| Filtering: by channel, persona, role | Channel, Persona, BusinessRole entities as filter facets | `[PARTIAL]` — entities now exist, but dedicated filter UI and traversal views are still pending |

**Score: AMBER** — The canvas and detail views now have graph-backed support for transitions, interactions, story delivery, journey-step screen context, touchpoint channels, confirmations, and error codes. The remaining UX gap is not missing graph structure; it is that several views still resolve some facets through lookup/projection layers rather than dedicated graph-backed UI entry points.

---

**Benchmark note:** The detailed per-query statuses below are the last formal queryability capture. They predate the most recent capability/project meta-model rollout, so they should be rerun before treating the GREEN/AMBER/RED distribution as current.

## 4. Dimension Summary

| # | Dimension | Score | Rationale |
|---|-----------|-------|-----------|
| 1 | Documentation completeness | **GREEN** | All 71 benchmarkable nodes are documented with typed attributes |
| 2 | Implementation completeness | **GREEN** | 71/71 benchmarkable nodes now exist in code (100.0%) |
| 3 | Attribute depth | **GREEN** | Current live 71-type benchmark slice averages `100.0`; universal status migration outside the benchmark slice is still broader than this view |
| 4 | Relationship coverage | **GREEN** | Current live 71-type benchmark slice averages `100.0`; the engineering, process, governance, failure-path, traceability, screen-flow, canonical journey/story traversal, implementation-pack execution-context spines, BPMN/data-flow families, planning/ownership families, and the active metadata families are now fully edge-backed in the live slice |
| 5 | Queryability | **GREEN** | Current live 71-type benchmark slice reports `100.0` after adding dedicated traversal/detail endpoints for the final remaining benchmarked artifact and metadata families; the older 17-query formal rerun still needs refreshing |
| 6 | Source traceability | **GREEN** | SourceReference exists and the current live benchmark slice now reports `100.0` source coverage after the catalog-level `HAS_SOURCE` backfill across the seeded implementation-driving artifact families |
| 7 | Delivery-tool interoperability | **AMBER** | ExternalArtifact exists with story/bug representation, but sync hierarchy and dependency edges are still missing |
| 8 | UX implementation support | **AMBER** | Screen API now resolves stories[] and roles[] directly from graph-backed screen edges; Persona/Channel/Permission registries now exist, but several exploration views, filter facets, and traversal paths are still pending |

**Overall assessment:** Documentation is complete and the implementation baseline is now benchmark-complete rather than merely substantial. Design Hub is operating against a **75-node / 106-edge-type / 71-benchmarkable** target taxonomy with a current implementation baseline of **74 `@Node` entities**, **111 SDN `@Relationship` declarations**, **1 Cypher-only polymorphic edge**, and **489 passing tests**. The live 71-type benchmark slice is fully green at `100.0`. The largest remaining gaps are outside the benchmark slice: the remaining non-benchmarked long-tail stubs, the open string migrations, broader localization/accessibility verification, and Neo4j deprecation cleanup.

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

**Score: GREEN** — Persona exists as a first-class node and Journey carries `PERFORMED_BY_PERSONA`, so the query runs as a full edge walk.

#### Query 2: Which channels serve journey J?

```cypher
-- TARGET: Full edge walk
MATCH (j:Journey)-[:HAS_STEP]->(s:JourneyStep)-[:STARTS_AT_TOUCHPOINT]->(tp:Touchpoint)
      -[:DELIVERED_VIA_CHANNEL]->(ch:Channel)
WHERE j.journeyId = $journeyId
RETURN DISTINCT ch.channelCode, ch.name

-- CURRENT: HAS_STEP, STARTS_AT_TOUCHPOINT, and DELIVERED_VIA_CHANNEL are all implemented.
```

**Score: GREEN** — Journey-to-channel traversal is now a full edge walk through JourneyStep and Touchpoint.

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

-- CURRENT: HAS_INTERACTION is now the canonical repository path and Permission is a first-class registry node.
```

**Score: GREEN** — Screen-to-interaction-to-permission traversal is now edge-backed end to end.

#### Query 5: What happens if interaction I fails?

```cypher
-- TARGET: Embedded traversal + registry hop
MATCH (i:Interaction)
WHERE i.interactionId = $interactionId
WITH i, i.outcomes.error AS errorOutcome
OPTIONAL MATCH (ec:ErrorCode) WHERE ec.code = errorOutcome.errorCodeRef
RETURN errorOutcome, ec

-- CURRENT: Interaction has embedded outcome fields (outcomeSuccess, outcomeError, outcomeLoading, errorCodeRef).
-- ErrorCode entity exists. ON_ERROR_SHOWS edge links Interaction → ErrorCode.
-- WORKAROUND: MATCH (i:Interaction)-[:ON_ERROR_SHOWS]->(ec:ErrorCode)
--             WHERE i.interactionId = $interactionId
--             RETURN i.outcomeSuccess, i.outcomeError, i.outcomeLoading, ec
```

**Score: AMBER** — InteractionOutcome fields are embedded (T3) on Interaction, not a separate node. ErrorCode entity and ON_ERROR_SHOWS edge exist. The embedded-to-registry hop prevents GREEN; promotion requires InteractionOutcome becoming Tier 1.

#### Query 6: Which stories deliver screen S?

```cypher
-- TARGET: Full edge walk (direction: UserStory → Screen via DELIVERS)
MATCH (us:UserStory)-[:DELIVERS]->(scr:Screen)
WHERE scr.surfaceId = $surfaceId
RETURN us.storyId, us.label

-- CURRENT: DELIVERS is implemented and backfilled from legacy storyRefs for compatibility.
```

**Score: GREEN** — Stories now resolve through the canonical `UserStory -[:DELIVERS]-> Screen` edge. Legacy `storyRefs` remains only as a compatibility field.

#### Query 7: Which bugs affect screen S?

```cypher
-- TARGET: Full edge walk
MATCH (b:Bug)-[:AFFECTS]->(scr:Screen)
WHERE scr.surfaceId = $surfaceId
RETURN b.bugId, b.summary, b.severity

-- CURRENT: Bug entity exists. AFFECTS_SCREEN edge links Bug → Screen.
-- MATCH (b:Bug)-[:AFFECTS_SCREEN]->(scr:Screen)
-- WHERE scr.surfaceId = $surfaceId
-- RETURN b.bugId, b.summary, b.severity
```

**Score: GREEN** — Bug entity and AFFECTS_SCREEN edge are implemented. Full edge walk is available.

#### Query 8: Where did artifact A come from?

```cypher
-- TARGET: Full edge walk
MATCH (a)-[:HAS_SOURCE]->(sr:SourceReference)
WHERE a.surfaceId = $artifactId OR a.storyId = $artifactId
RETURN sr.sourceType, sr.documentPath, sr.lineReference

-- CURRENT: SourceReference entity exists. HAS_SOURCE edge links multiple entity types → SourceReference.
-- MATCH (a)-[:HAS_SOURCE]->(sr:SourceReference)
-- WHERE a.surfaceId = $artifactId OR a.storyId = $artifactId
-- RETURN sr.artifactPath, sr.section, sr.lineRef
```

**Score: GREEN** — SourceReference entity and HAS_SOURCE edge are implemented on UserStory, Screen, and Bug.

#### Query 9: Which Jira tickets track story S?

```cypher
-- TARGET: Full edge walk
MATCH (ea:ExternalArtifact)-[:REPRESENTS]->(us:UserStory)
WHERE us.storyId = $storyId AND ea.system = 'JIRA'
RETURN ea.key, ea.url, ea.syncStatus

-- CURRENT: ExternalArtifact entity exists. REPRESENTS_STORY edge links ExternalArtifact → UserStory.
-- MATCH (ea:ExternalArtifact)-[:REPRESENTS_STORY]->(us:UserStory)
-- WHERE us.storyId = $storyId AND ea.system = 'JIRA'
-- RETURN ea.key, ea.url, ea.syncStatus
```

**Score: GREEN** — ExternalArtifact entity and REPRESENTS_STORY edge are implemented. Edge name uses typed suffix (REPRESENTS_STORY not generic REPRESENTS) per SDN convention.

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
| GREEN | 12 | 71% | #1 (persona journeys), #2 (journey-step touchpoints), #3 (channel reach), #4 (permissions), #6 (stories), #7 (bugs affect screen), #8 (source reference), #9 (external artifacts), #10 (confirmation dialogs), #11 (process activities), #12 (process gateways), #13 (process events) |
| AMBER | 1 | 6% | #5 (failure path via embedded InteractionOutcome) |
| RED | 4 | 24% | #14, #15, #16, #17 |

---

## 6. Artifact Type Coverage Matrix

| Artifact Type | Tier | Documented | Attr Depth | Implemented | Mapping | Rel Coverage | Queryable | Notes |
|--------------|------|-----------|------------|-------------|---------|--------------|-----------|-------|
| BusinessObjective | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | Objective-to-feature wiring is implemented and live in the traceability spine |
| Feature | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edges | GREEN | Epic→Feature and Feature→Story delivery spine is implemented |
| Decision | T1 | Yes | — | `[IMPL]` | Direct | 3/3 edge families | GREEN | AFFECTS_FEATURE, AFFECTS_SCREEN, and AFFECTS_API are seeded and queryable |
| Assumption | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | UNDERLIES_FEATURE and UNDERLIES_STORY are seeded and queryable |
| Constraint | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | CONSTRAINS_FEATURE and CONSTRAINS_API are seeded and queryable |
| SourceReference | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | HAS_SOURCE is implemented for Screen, UserStory, and Bug |
| Finding | T1 | Yes | 83% | `[IMPL]` | Direct | 1/1 edge family | AMBER | Finding now has seeded screen impact plus external-artifact representation; richer review coverage is still pending |
| Bug | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edges | GREEN | AFFECTS_SCREEN and external-artifact traceability are both implemented |
| Risk | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | THREATENS_FEATURE and THREATENS_STORY are seeded and queryable |
| Persona | T1 | Yes | 63% | `[IMPL]` | Direct | 0/1 edges | AMBER | Node exists; outgoing Persona-centric exploration edges still pending |
| BusinessRole | T1 | Yes | 100% | `[IMPL]` | Direct | 1/1 edge family | GREEN | Role split landed; Screen ACCESSIBLE_BY_ROLE now targets BusinessRole |
| ValidationRole | T1 | Yes | 100% | `[IMPL]` | Direct | 0/0 target | GREEN | Role split landed; resolved via RoleService union query |
| Journey | T1 | Yes | 63% | `[IMPL]` | Direct | 2/2 edges | GREEN | HAS_STEP and PERFORMED_BY_PERSONA are both implemented |
| JourneyStep | T1 | Yes | 50% | `[IMPL]` | Direct | 3/3 edges | GREEN | EXECUTES_INTERACTION, USES_SCREEN, and STARTS_AT_TOUCHPOINT are implemented |
| Topic | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | GROUPS_JOURNEY and GROUPS_FEATURE are seeded and queryable |
| Touchpoint | T1 | Yes | 63% | `[IMPL]` | Direct | 5/5 modeled edges | GREEN | TARGETS, HAS_ENTRY_MODE, USED_BY_PERSONA, DELIVERED_VIA_CHANNEL, and ACCESSIBLE_BY_ROLE are implemented |
| UserStory | T1 | Yes | 42% | `[IMPL]` | Direct | 4/4 edges | GREEN | DELIVERS, HAS_CRITERION, GOVERNED_BY_RULE, and HAS_TASK are all implemented |
| AcceptanceCriterion | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | Criterion node exists and is linked from UserStory |
| Rule | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | UserStory governance and ValidationRule linkage are both implemented |
| ValidationRule | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | Screen and Rule both connect to ValidationRule |
| EdgeCase | T1 | Yes | — | `[IMPL]` | Direct | 3/3 edge families | GREEN | Story, screen, and journey-step impact edges are seeded and queryable |
| ExceptionCase | T1 | Yes | — | `[IMPL]` | Direct | 3/3 edge families | GREEN | Interaction, API, and journey-step impact edges are seeded and queryable |
| Screen | T1 | Yes | 75% | `[IMPL]` | Direct | 10/10 edge families | GREEN | Screen interaction traversal, delivered-story traversal, failure-path wiring, messages, and state semantics are all edge-backed |
| ScreenState | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | ScreenState exists and is linked to Screen |
| Interaction | T1 | Yes | 63% | `[IMPL]` | Direct | 7/7 edge families | GREEN | Permission, API, confirmation, failure path, persona, role, and screen traversal are all edge-backed |
| Transition | T1 | Yes | — | `[IMPL]` | Direct | 3/3 edge families | GREEN | FROM_SCREEN, TO_SCREEN, and CAUSED_BY_INTERACTION are implemented |
| ApiContract | T1 | Yes | — | `[IMPL]` | Direct | 4/4 edge families | GREEN | CALLS_API plus request/response/error schema wiring is implemented |
| RequestSchema | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | RequestSchema exists and is linked from ApiContract |
| ResponseSchema | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | ResponseSchema exists and is linked from ApiContract |
| ErrorContract | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | ErrorContract exists and is linked from ApiContract |
| DataEntity | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | DataEntity exists and HAS_FIELD is implemented |
| DataField | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | DataField exists and is linked from DataEntity |
| Integration | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | USES_API is seeded and queryable; Event registry linkage remains future breadth work |
| TestCase | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | VERIFIES links are implemented for Screen and ApiContract |
| ExternalArtifact | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | ExternalArtifact now represents both stories and bugs |
| OpenQuestion | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | BLOCKS_ARTIFACT is seeded across feature/screen/story targets and queryable |
| Gap | T1 | Yes | 38% | `[RESHAPE]` | Reshape | 1/2 edges | AMBER | HAS_GAP edge; missing gapId, gapType |
| Message | T1 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | Message nodes exist and are linked from Screen |
| ProcessActivity | T1 | Yes | — | `[IMPL]` | Direct | 3/3 edge families | GREEN | FLOWS_TO, EXPANDS_TO, and CALLS_PROCESS are implemented |
| ProcessGateway | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | Gateways are linked into the process spine and flow onward to activities |
| ProcessEvent | T1 | Yes | — | `[IMPL]` | Direct | 3/3 edge families | GREEN | Events are linked into the process spine, attach to activities, and flow onward |
| Task | T1 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | Tasks now participate in both HAS_TASK and IMPLEMENTS traversal |
| BusinessDomain | T2 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | BusinessDomain exists and HAS_CAPABILITY is implemented |
| Channel | T2 | Yes | 100% | `[IMPL]` | Direct | 1/1 edge family | GREEN | Registry node exists and Touchpoint DELIVERED_VIA_CHANNEL is implemented |
| Permission | T2 | Yes | 100% | `[IMPL]` | Direct | 1/1 edge family | GREEN | Registry node exists and Interaction REQUIRES_PERMISSION is implemented |
| ErrorCode | T2 | Yes | — | `[IMPL]` | Direct | 2/2 edge families | GREEN | ErrorCode registry exists and is wired from Screen and Interaction |
| ConfirmationDialog | T2 | Yes | — | `[IMPL]` | Direct | 1/1 edge family | GREEN | ConfirmationDialog registry exists and is wired from Interaction |
| Enum | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| Event | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| Locale | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| TranslationKey | T2 | Yes | — | `[PLANNED]` | — | — | RED | No code entity |
| CodeAsset | T1 | Yes | 0% | `[IMPL]` | Direct | 2/7 edge families | AMBER | CodeAsset exists; HAS_CODE_ASSET and IMPLEMENTS are wired, broader pack metadata remains open |
| QualityConstraint | T1 | Yes | 0% | `[IMPL]` | Direct | 0/2 edges | RED | Node exists; quality-governance edge wiring is still deferred |
| ImportSnapshot | T2 | Yes | 0% | `[IMPL]` | Direct | 0/1 edges | RED | Node exists; import lineage edges are still deferred |
| CodingConvention | T2 | Yes | 0% | `[IMPL]` | Direct | 0/1 edges | RED | Node exists; convention-governance edges are still deferred |

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
| `interactionRef` | JourneyStep | `[IMPLEMENTED]` Interaction exists | `EXECUTES_INTERACTION` | — | Closed for graph traversal; legacy field remains for migration compatibility |

### 8.1 Migration Priority

| Priority | Migrations | Rationale |
|----------|-----------|-----------|
| P0 | — | Canonical story delivery and journey-step interaction traversal are now implemented |
| P1 | `personaId`, `personaIds`, `roleKeys`, `channelId`, `permission`, `apiCalls`, `confirmationCode`, `storyRefs`, `interactionRef` | Closed for graph traversal; optional cleanup remains to retire legacy compatibility fields after API consumers stop depending on them |
| P2 | — | Reserved for future cleanup once the remaining compatibility fields are removed from API and seed payloads |
| P3 | — | No additional string-to-edge migration family is currently queued |

---

## 9. Target API Projection Expectations

This section captures what API responses SHOULD expose when the graph model reaches its target state. These are benchmark-level projection expectations, not endpoint specifications. Full endpoint shapes are deferred to `architecture-blueprint.md`.

### 9.1 Screen Response Projection

When a client requests a Screen, the response should expose linked graph objects as resolved projections, not raw string references.

| Field | Current Response | Target Projection | Source |
|-------|-----------------|-------------------|--------|
| `stories` | Graph: `DELIVERS` edge is canonical. API: `ScreenResponse` now prefers graph-backed `deliveredByStories`, while `storyRefs` remains only for compatibility. | `stories: [{ storyId, label, status }]` (resolved via graph edge walk, not in-memory lookup) | `DELIVERS` reverse edge walk (UserStory→Screen) |
| `roles` | Graph: `roleKeys` string array. API: **already resolved** — `ScreenResponse` returns `roles: RoleResponse[]` via in-memory lookup in `ScreenController`. Frontend prefers resolved objects. | `roles: [{ roleKey, displayName, roleGroup }]` (resolved via graph edge walk, not in-memory lookup) | `ACCESSIBLE_BY_ROLE` edge walk |
| `transitions` | Implicit via `TRANSITIONS_TO` edge | `transitions: [{ targetSurfaceId, targetLabel }]` (resolved targets) | `TRANSITIONS_TO` edge walk |
| `interactions` | Graph-backed by the canonical `HAS_INTERACTION` edge and exposed through the by-screen interaction endpoint; inline screen projection remains minimal. | `interactions: [{ interactionId, element, trigger }]` (resolved objects) | `HAS_INTERACTION` edge walk (Screen→Interaction) |
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

**Current overall projection score: AMBER** — Canonical graph support now exists for delivered stories, screen interactions, journey-step context, channels, permissions, API contracts, confirmation dialogs, and error codes. The main remaining gap is not graph availability; it is endpoint-level projection usage. Several responses still lean on compatibility fields or app-layer joins instead of returning the full graph-backed projection directly from the API surface.

---

## 10. Gap Prioritization and Recommendations

### 10.1 Priority 0 — Critical Path (Enables queryability)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| No universal `status` enum | Every entity uses wrong status model | Create Status enum, Readiness embedded object, migrate all 8 entities |
| Legacy `storyRefs` compatibility field | Cleanup debt after successful DELIVERS migration | Retire the field once all API consumers rely on graph-backed delivered stories |
| Legacy `interactionRef` compatibility field | Cleanup debt after successful EXECUTES_INTERACTION migration | Retire the field once all API consumers rely on graph-backed journey-step traversal |
| `personaId` / `personaIds` legacy compatibility fields | Cleanup debt after successful persona edge migration | Retire the legacy fields once all API consumers rely on `PERFORMED_BY_PERSONA` / `USED_BY_PERSONA` |

### 10.2 Priority 1 — Registry Entities (Enables filtering and facet queries)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| Legacy permission compatibility field on Interaction | Query #4 remains mixed-mode until API payloads fully prefer graph-backed permission projection | Keep `REQUIRES_PERMISSION` as source of truth and retire the compatibility field later |
| Embedded InteractionOutcome remains Tier 3 | Query #5 is still partial, not a full graph walk | Keep embedded outcome fields for now; promote only if direct querying becomes necessary |

### 10.3 Priority 2 — Structural Completeness (Enables full traversal spine)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| Journey-step edges are not yet projected through all UI surfaces | Canonical traversal exists, but some consumers still rely on compatibility fields or secondary lookups | Prefer `USES_SCREEN`, `EXECUTES_INTERACTION`, and `STARTS_AT_TOUCHPOINT` in API projections and UI adapters |
| Legacy `journeyStepRefs` strings on Screen | Screen → JourneyStep traversal still depends on string matching in some compatibility paths | Retire the compatibility field after consumers switch to canonical traversal |
| InteractionOutcome remains embedded Tier 3 | Query #5 is AMBER because the failure path still crosses embedded data instead of a first-class node | Keep the embedded model for now; promote only if direct outcome querying becomes necessary |

### 10.4 Priority 3 — Strategic & Governance (Enables upstream context)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| BusinessObjective is still a stub with no working edges | Upstream business context exists as a node but not as an active traversal surface | Add objective-to-capability or objective-to-project wiring when strategic reporting becomes active |
| Catalog-level source coverage is now in place, but deeper business-document traceability is still partial on Journey/JourneyStep/Interaction/Touchpoint | Query #8 is structurally closed for the current slice, but source depth still varies by artifact family | Extend `HAS_SOURCE` from repo-local catalog provenance toward richer business-document evidence where traceability matters most |
| No hierarchy/dependency sync on ExternalArtifact | Query #9 is only partially closed | Add work-item hierarchy and dependency edges after the basic sync shape is stable |

### 10.5 Priority 4 — Engineering Layer (Enables contract-level queries)

| Gap | Impact | Recommendation |
|-----|--------|----------------|
| Remaining compatibility fields still shadow canonical delivery traversal | Screen-to-story traversal is graph-backed, but legacy payload fields remain | Remove the compatibility fields once downstream consumers stop depending on them |
| No `LOCATED_IN` edge | Query #16 cannot resolve test cases to code files | Add `TestCase -[LOCATED_IN]-> CodeAsset` |
| No CodingConvention governance edge | Query #17 cannot resolve convention governance | Wire CodingConvention to ApplicationComponent when convention governance becomes active |

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
| Queryability | GREEN (current 71-type benchmark slice has dedicated traversal/detail coverage) |
| Source traceability | GREEN (SourceReference entity exists and current benchmark-slice artifacts are source-linked) |
| Delivery-tool interoperability | AMBER (ExternalArtifact entity exists, basic sync) |
| UX implementation support | AMBER (sidebar and detail panel projections resolved) |
