# Architecture Blueprint

**Status:** Draft

**Related documents:**

- `modeling-taxonomy.md` (3-tier classification: 58 T1 + 13 T2 + 4 T3 = 75 model elements with current-to-target entity mapping)
- `graph-object-catalog.md` (full per-object specifications)
- `product-vision.md` (traversal spine, canonical views)
- `vision-benchmark.md` (8-dimension scoring, string-to-edge migration cost)
- `implementation-readiness-graph-model.md` (status, readiness, completenessScore)
- `feature-capability-map.md` (capability model, view registry)
- `design-testing-strategy.md` (6 test layers)
- `ci-quality-gates.md` (CI enforcement model)

---

## 1. System Role

Design Hub is a graph-centric requirements and delivery intelligence system. It sits between source artifacts and implementation teams. It does not replace backlog tools, but it should provide the richer domain model that backlog tools usually lack.

---

## 2. Logical Architecture

```mermaid
graph TD
    subgraph L1["1. Source Layer"]
        SRC[Source Artifacts]
    end
    subgraph L2["2. Normalization Layer"]
        NORM[Object Normalization]
    end
    subgraph L3["3. Graph Persistence"]
        NEO[Neo4j Graph DB]
    end
    subgraph L4["4. Query & Service Layer"]
        SVC[Spring Boot Services]
    end
    subgraph L5["5. Presentation Layer"]
        ANG[Angular Frontend]
    end
    subgraph L6["6. Verification Layer"]
        PW[Playwright Tests]
    end
    subgraph L7["7. CI Enforcement"]
        CI[GitHub Actions]
    end

    SRC --> NORM
    NORM --> NEO
    NEO --> SVC
    SVC --> ANG
    ANG --> PW
    PW --> CI
    CI -->|gates| NEO
```

### 2.1 Source layer

**Inputs:**

- EMSIST requirement and UX specifications
- Consolidated story inventory
- Design and prototype artifacts
- External delivery systems such as Azure DevOps and Jira

**Responsibilities:** source parsing, identity extraction, reference capture, change detection

**Implementation status:** `[PLANNED]` — no source ingestion pipeline exists. Data is seeded via `DataInitializer.java`.

### 2.2 Normalization layer

**Responsibilities:**

- Map source records into canonical graph objects
- Assign stable identifiers
- Resolve aliases and duplicates
- Enforce required attributes
- Record provenance in `SourceReference`

**Outputs:** canonical object records, typed relationship records, sync metadata

**Implementation status:** `[PARTIAL]` — normalization is still largely implicit in seed data, but `SourceReference` now exists and selected artifacts are linked through `HAS_SOURCE`.

### 2.3 Graph persistence layer

**Store:** Neo4j (Community Edition)

**Responsibilities:**

- Persist first-class nodes and typed edges
- Support bidirectional traversal
- Support object-centric filtering by status, readiness, module, topic, and external sync state

**Implementation status:** `[IMPLEMENTED — partial]`

| Aspect | Status | Evidence |
|--------|--------|----------|
| Node persistence | `[IMPLEMENTED]` | 74 `@Node` entities in `backend/src/main/java/com/emsist/designhub/domain/` |
| Typed edges | `[PARTIAL]` | 111 SDN `@Relationship` declarations + 1 Cypher-only `ASSESSES` edge; the registry, engineering, process-spine, failure-path, traceability, screen-flow, canonical journey/story traversal, implementation-pack execution-context core, and the remaining T1 failure/question/topic/integration families are now edge-backed |
| Bidirectional traversal | `[PARTIAL]` | Forward traversal works for implemented edges; reverse requires Cypher |
| Status filtering | `[IMPLEMENTED — reshape required]` | 3-enum status model; target is universal 10-value `status` |
| Readiness filtering | `[PLANNED]` | No readiness flags on entities |

### 2.4 Query and service layer

**Current implementation:**

| Component | Status | Evidence |
|-----------|--------|----------|
| Spring Boot backend | `[IMPLEMENTED]` | `backend/pom.xml` — Spring Boot 3.4.1 |
| Screen REST API | `[IMPLEMENTED]` | `ScreenController.java` — CRUD + resolution |
| Journey REST API | `[IMPLEMENTED]` | `JourneyController.java` |
| UserStory REST API | `[IMPLEMENTED]` | `UserStoryController.java` |
| Interaction REST API | `[IMPLEMENTED]` | `InteractionController.java` |
| Role REST API | `[IMPLEMENTED]` | `RoleController.java` |
| Touchpoint REST API | `[IMPLEMENTED]` | `TouchpointController.java` |
| Stats API | `[IMPLEMENTED]` | `StatsController.java` |
| OpenAPI docs | `[IMPLEMENTED]` | Springdoc OpenAPI 2.3.0 |

**Target responsibilities (implementation status):**

- Graph-object query endpoints by node type `[IMPLEMENTED]` — `/api/v1/graph/objects` now supports type-scoped and graph-wide object queries across Screen, ScreenState, Transition, Topic, EdgeCase, ExceptionCase, UserStory, Integration, ProjectInstance, Milestone, Journey, Persona, BusinessDomain, Organization, BusinessObjective, RequirementPortfolio, Epic, Feature, Decision, Assumption, Constraint, Assessment, Risk, Finding, Bug, Task, ApiContract, DataEntity, Interaction, Touchpoint, Channel, BusinessCapability, BusinessProcess, Application, ApplicationComponent, BusinessObject, Deployment, CodeAsset, TestCase, Rule, Message, Gap, OpenQuestion, AcceptanceCriterion, DataField, CodingConvention, QualityConstraint, AgentPolicy, SourceReference, BusinessRole, ValidationRole, Permission, ConfirmationDialog, and ErrorCode
- Relation expansion endpoints `[IMPLEMENTED]` — `/api/v1/graph/objects/{type}/{id}/relations` exposes incoming and outgoing graph edges for supported node types
- Persona and journey traversal endpoints `[IMPLEMENTED]` — `/api/v1/graph/personas/{personaId}` and `/api/v1/graph/journeys/{journeyId}` now expose graph-backed traversal payloads
- Artifact traversal/detail endpoints `[IMPLEMENTED]` — `/api/v1/graph/screen-states/{stateId}`, `/api/v1/graph/transitions/{transitionId}`, `/api/v1/graph/topics/{topicId}`, `/api/v1/graph/touchpoints/{touchpointId}`, `/api/v1/graph/interactions/{interactionId}`, `/api/v1/graph/apis/{contractId}`, `/api/v1/graph/data-entities/{entityId}`, `/api/v1/graph/objectives/{objectiveId}`, `/api/v1/graph/features/{featureId}`, `/api/v1/graph/decisions/{decisionId}`, `/api/v1/graph/assumptions/{assumptionId}`, `/api/v1/graph/governance-constraints/{constraintId}`, `/api/v1/graph/assessments/{assessmentId}`, `/api/v1/graph/risks/{riskId}`, `/api/v1/graph/edge-cases/{edgeCaseId}`, `/api/v1/graph/exception-cases/{exceptionId}`, `/api/v1/graph/epics/{epicId}`, `/api/v1/graph/portfolios/{portfolioId}`, `/api/v1/graph/projects/{projectId}`, `/api/v1/graph/integrations/{integrationId}`, `/api/v1/graph/milestones/{milestoneId}`, `/api/v1/graph/business-domains/{domainCode}`, `/api/v1/graph/organizations/{orgId}`, `/api/v1/graph/code-assets/{codeAssetId}`, `/api/v1/graph/test-cases/{testCaseId}`, `/api/v1/graph/rules/{ruleId}`, `/api/v1/graph/messages/{messageId}`, `/api/v1/graph/gaps/{gapId}`, `/api/v1/graph/conventions/{conventionCode}`, `/api/v1/graph/quality-constraints/{constraintId}`, `/api/v1/graph/policies/{policyId}`, `/api/v1/graph/sources/{sourceId}`, `/api/v1/graph/findings/{findingId}`, `/api/v1/graph/open-questions/{questionId}`, `/api/v1/graph/acceptance-criteria/{criterionId}`, `/api/v1/graph/data-fields/{fieldId}`, `/api/v1/graph/roles/{roleKey}`, `/api/v1/graph/validation-roles/{validationRoleKey}`, `/api/v1/graph/permissions/{permissionKey}`, `/api/v1/graph/dialogs/{dialogId}`, and `/api/v1/graph/error-codes/{code}` now expose dedicated typed traversal payloads for the remaining benchmarked artifact families
- Business architecture traversal endpoints `[IMPLEMENTED — partial]` — `/api/v1/graph/architecture/business/capabilities` and `/api/v1/graph/architecture/business/capabilities/{capabilityId}` now expose graph-backed capability, process, application, feature, and ownership traversal for the seeded architecture slice
- Traceability spine endpoints `[IMPLEMENTED — partial]` — `/api/v1/graph/traceability/stories/{storyId}` now exposes the live story spine with seeded `BusinessObjective` / `RequirementPortfolio` / `Epic` / `Feature` coverage for current implementation stories, and still reports explicit missing upper-spine segments where that data is absent
- Benchmark aggregation endpoints `[IMPLEMENTED]` — `/api/v1/graph/benchmark` now returns per-type attribute depth, relationship coverage, source traceability, queryability, and overall scores across the full 71-node benchmarkable taxonomy. The live slice measures `100.0` overall, with all four dimensions at `100.0`, after the catalog-level backfill, dedicated traversal endpoints, metadata-family traversal endpoints, API-contract schema backfill, persona-usage backfill, journey persona normalization, component code-asset coverage, explicit auth-task implementation coverage, planning/ownership traversal coverage, data-quality coverage, registry-breadth expansion, external-artifact hierarchy normalization, BPMN/data-flow traversal expansion, and the final journey-step/import-snapshot/evidence-record/enum/event/locale/translation-key benchmark closures.
- External-artifact mapping endpoints `[IMPLEMENTED — partial]` — `/api/v1/graph/external-artifacts`, `/api/v1/graph/external-artifacts/{externalId}`, and `/api/v1/graph/external-artifacts/parity-audit` now expose live hierarchy, dependency, sync, custom-field, and field-parity evidence for the seeded external slice, `/api/v1/external-sync/artifacts` provides a generic dry-run/apply sync path with relationship and custom-field persistence, `/api/v1/external-sync/azure-devops/work-items` plus `/api/v1/external-sync/jira/issues` provide source-specific adapter request surfaces, `/api/v1/external-sync/jobs` now provides stored poll/webhook/manual orchestration envelopes plus recent history with optional `sourceSystem` filtering, `POST /api/v1/external-sync/jobs/poll/{sourceSystem}` now provides a config-aware polling trigger with persisted results, `/api/v1/external-sync/sources` now exposes effective source configuration plus latest persisted job status, including `webhookSecretConfigured`, `scopeConfigured`, and `filterConfigured`, Verification View can now trigger that poll path from the UI while surfacing source-filtered sync history, and upper-spine external normalization now reaches epic coverage with live traversal evidence via `EXT-JIRA-EPIC-001`; broader remote source coverage is still pending
- Agent-pack export endpoints `[IMPLEMENTED — partial]` — `/api/v1/stories/{storyId}/agent-pack` now exposes a graph-backed story implementation-pack export contract, and `/api/v1/stories/{storyId}/agent-pack/completeness` remains available for readiness-only scoring
- Delivery aggregate endpoints `[PARTIAL]` — `/api/v1/delivery/stories` and `/api/v1/delivery/stories/{storyId}` now expose graph-backed story delivery context
- Readiness and missing-artifact diagnostics `[PARTIAL]` — screen and story diagnostics available via `/api/v1/readiness/screens/{surfaceId}` and `/api/v1/readiness/stories/{storyId}`; the current seeded delivery slice now resolves complete packs for `US-AI-078`, `US-AI-090`, `US-AI-137`, `US-AI-139`, and `US-SCREEN-COVERAGE-001`, while `US-AUTH-001` remains intentionally incomplete to surface missing-artifact diagnostics
- completenessScore computation endpoint `[PARTIAL]` — exposed as part of the readiness diagnostics response for screens and stories

**API resolution layer** (current):

`ScreenController.java` now returns graph-backed `stories[]` and `roles[]` directly from `deliveredByStories` and `accessibleByRoles` without the earlier in-memory lookup join. The compatibility `storyRefs` / `roleKeys` arrays remain in the payload as IDs only, but the detail-surface summaries are now graph-backed rather than mixed-mode lookup projections.

### 2.5 Presentation layer

**Current implementation:**

| Component | Status | Evidence |
|-----------|--------|----------|
| Angular 21 frontend | `[IMPLEMENTED]` | `frontend/package.json` |
| Flow canvas | `[IMPLEMENTED]` | `flow-canvas.component.ts` |
| Screen sidebar | `[IMPLEMENTED]` | `screen-sidebar.component.ts` |
| Detail panel | `[IMPLEMENTED]` | `detail-panel.component.ts` with 6 sub-panels |
| State management | `[IMPLEMENTED]` | `design-hub-state.service.ts` — computed signals |
| API service | `[IMPLEMENTED]` | `design-hub-api.service.ts` |
| PrimeNG components | `[IMPLEMENTED]` | PrimeNG 21 in `package.json` |

**Target responsibilities (current status):**

- Persona and journey views `[IMPLEMENTED — partial]` — the Angular Journeys tab now exposes a persona-first explorer backed by `/api/v1/graph/personas`, `/api/v1/graph/personas/{personaId}`, and `/api/v1/graph/journeys/{journeyId}`, so users can enter via persona and drill into linked journeys and steps; topic filters and broader exploration polish are still pending
- Channel view `[IMPLEMENTED — partial]` — the Angular Channels tab now exposes graph-backed channel summaries plus live touchpoint, reachable-screen, persona-reach, and coverage-gap traversal via `/api/v1/graph/channels` and `/api/v1/graph/channels/{channelCode}`; dedicated landing surfaces and channel-type filtering are still pending
- Delivery view `[IMPLEMENTED — partial]` — the Angular detail panel now exposes a Delivery tab backed by `/api/v1/delivery/stories`, including external-artifact hierarchy, dependency, represented-object, and custom-field detail; broader view breadth and dedicated entry surfaces are still pending
- Traceability view `[IMPLEMENTED — partial]` — the Angular detail panel now exposes a Traceability tab backed by `/api/v1/graph/traceability/stories/{storyId}` for live upstream/downstream story traversal
- Business Architecture View `[IMPLEMENTED — partial]` — the Angular Architecture tab now exposes a graph-backed business capability explorer via `/api/v1/graph/architecture/business/capabilities` and `/api/v1/graph/architecture/business/capabilities/{capabilityId}`
- Application Architecture View `[IMPLEMENTED — partial]` — the Angular Architecture tab now exposes a graph-backed application explorer via `/api/v1/graph/architecture/applications` and `/api/v1/graph/architecture/applications/{applicationId}`, including components, APIs, supported screens, realized features, owners, and cross-application dependencies
- Data Architecture View `[IMPLEMENTED — partial]` — the Angular Architecture tab now exposes a graph-backed data-object explorer via `/api/v1/graph/architecture/data/business-objects` and `/api/v1/graph/architecture/data/business-objects/{objectId}`, including mapped entities, information flows, API exposure, reachable screens, and child business objects
- Infrastructure Architecture View `[IMPLEMENTED — partial]` — the Angular Architecture tab now exposes deployment topology via `/api/v1/graph/architecture/infrastructure/deployments` and `/api/v1/graph/architecture/infrastructure/deployments/{deploymentId}`, including hosted components, infrastructure nodes, and deployed applications
- Benchmark view `[IMPLEMENTED — partial]` — the Angular detail panel now exposes a Benchmark tab backed by `/api/v1/graph/benchmark`; dedicated verification surfaces and full 71-node coverage are still pending
- Verification view `[IMPLEMENTED — partial]` — the Angular detail panel now exposes a Verification tab combining live `/api/v1/readiness/*` diagnostics, current build/e2e/token-audit evidence, and the live external parity audit
- i18n / bilingual support `[IMPLEMENTED — partial]` — the Angular shell now exposes a persisted locale switch with translated shell labels and root `lang` / `dir` switching; broader panel-level translation coverage is still pending
- Design token compatibility `[PARTIAL]` — current Design Hub shell and detail surfaces align to EMSIST ThinkPLUS `--tp-*` and `--nm-*` tokens from `Emsist-app/frontend/src/styles.scss`, and `npm run check:design-tokens` now audits 16 shell/detail files; broader automated coverage is still pending

### 2.6 Verification layer

**Strategy:** Defined in `design-testing-strategy.md` (6 test layers, 8 anti-drift scenarios)

**Implementation status:** `[IMPLEMENTED — partial]` — Playwright Layers 1-2 are present in `frontend/tests/`, visual baselines now exist for the shell and high-signal detail panels, Arabic RTL shell baselines now exist for desktop/mobile, graph-to-UI drift assertions now exist in `frontend/tests/drift/`, initial localization/RTL shell assertions now exist in `frontend/tests/i18n/`, and the suites run against the live backend; broader coverage is still pending.

| Test Layer | Status |
|-----------|--------|
| 1. Contract/route smoke | `[IMPLEMENTED]` |
| 2. Semantic interaction | `[IMPLEMENTED]` |
| 3. Visual baselines | `[IMPLEMENTED — partial]` — snapshot baselines now exist for the shell and high-signal detail panels in desktop/mobile Chromium |
| 4. Token compliance | `[IMPLEMENTED — partial]` — `npm run check:design-tokens` audits 16 Design Hub shell/detail files and required EMSIST root tokens; broader workspace coverage is still pending |
| 5. Localization/RTL | `[IMPLEMENTED — partial]` — root locale switching, persisted `lang` / `dir`, and initial Arabic shell assertions now exist; broader translation coverage is still pending |
| 6. Graph-UI drift | `[IMPLEMENTED — partial]` — `frontend/tests/drift/graph-ui-drift.spec.ts` now verifies screen detail parity, delivery aggregate counts, and traceability/readiness parity against backend APIs |

### 2.7 CI enforcement layer

**Strategy:** Defined in `ci-quality-gates.md` (PR validation + merge protection lanes)

**Implementation status:** `[IMPLEMENTED — partial]` — `.github/workflows/verification.yml` now runs backend `mvn test` and frontend `npm run verify:ui` with Neo4j on pull requests and pushes to `main`, and `.github/workflows/release-verification.yml` now adds a tag/manual release-readiness lane with packaged frontend/backend artifacts; protected-branch policy and broader localization gates are still pending.

### 2.8 Agent-ready layer (extension)

The agent-ready and operational safety layer adds code-targeting, convention compliance, policy, and evidence capabilities to the graph model, enabling coding agents to resolve from delivery artifacts to exact filesystem paths, applicable standards, and proof artifacts.

**Code targeting:**

- **CodeAsset** (T1): File-level code targeting. Resolves `Application.repoPath + ApplicationComponent.modulePath + CodeAsset.filePath` for full filesystem path. 10 attributes, 7 relationships.
- **ImportSnapshot** (T2): Records point-in-time imports from Git docs to graph nodes. Enables drift detection via `contentHash` comparison between stored and current document content.
- **RequirementSyncContract**: Protocol for maintaining doc↔graph consistency. See agent-ready spec Section 9.

**Convention compliance:**

- **CodingConvention** (T2 Hybrid): Stores queryable metadata in graph, detailed rules in Git-tracked Markdown via `docRef`. Resolution is edge-only via `GOVERNED_BY_CONVENTION` — no implicit matching. When multiple conventions apply, narrower scope overrides broader: `COMPONENT > SERVICE > FRONTEND/BACKEND > GLOBAL`.
- **QualityConstraint** (T1): Instance-specific non-functional requirement with measurable threshold. Bound to Screen/ApiContract/DataEntity/ApplicationComponent via `HAS_QUALITY_CONSTRAINT`. Verified via `SATISFIED_BY → TestCase`.

**Operational safety/intelligence additions:**

- **AgentPolicy** (T2): Agent execution guardrails. Bound from Application and ApplicationComponent via `GOVERNED_BY_POLICY`.
- **EvidenceRecord** (T2): Proof registry for test results, screenshots, and baselines. Bound from Screen and ApiContract via `BASELINED_BY`.

**Implementation status:** `[IMPLEMENTED]` — CodeAsset, ImportSnapshot, QualityConstraint, CodingConvention, AgentPolicy, and EvidenceRecord now exist in code. Remaining work is broader coverage and UI/product surfacing, not entity creation.

### 2.9 Capability/project meta-model layer

The capability/project layer separates assessment/governance from delivery execution:

- **Assessment** (T1): Polymorphic evaluation of an assessable T1 target via Cypher-only `ASSESSES`
- **RequirementPortfolio** (T1): Owns the Epic → Feature → UserStory tree for one project
- **Milestone** (T1): Sprint/phase/release checkpoint with optional task assignment
- **ProjectInstance** (T1): Bridges gaps/capabilities to scoped application/component change work

This layer is implemented in code and raises the approved taxonomy to **75 nodes / 106 edge types / 71 benchmarkable**.

---

## 3. Current-to-Target Entity Mapping

From `modeling-taxonomy.md` section 3 — the 11 current code entities and their target model mappings:

| Current Entity | File | Target Object(s) | Mapping Type |
|---------------|------|------------------|-------------|
| `Screen.java` | `domain/Screen.java` | Screen (T1) | Direct — attribute depth gap + string refs |
| `Journey.java` | `domain/Journey.java` | Journey (T1) | Direct — `personaId` as string |
| `JourneyStep.java` | `domain/JourneyStep.java` | JourneyStep (T1) | Direct — missing screen/touchpoint edges |
| `UserStory.java` | `domain/UserStory.java` | UserStory (T1) | Direct — minimal (5 fields vs 8+ target) |
| `Interaction.java` | `domain/Interaction.java` | Interaction (T1) | Direct — `permission` string, `apiCalls` strings |
| `Touchpoint.java` | `domain/Touchpoint.java` | Touchpoint (T1) | Direct — `channelId` string in EntryMode |
| `Role.java` | `domain/Role.java` | BusinessRole (T1) + ValidationRole (T1) | **Split** |
| `Gap.java` | `domain/Gap.java` | Gap (T1) | **Reshape** — `type`/`severity` → `gapType`/`severity` + relationships |
| `EntryMode.java` | `domain/EntryMode.java` | EntryMode (T3) | Direct — value object |
| `ContentElement.java` | `domain/ContentElement.java` | ContentElement (T3) | Direct — value object |
| `Effect.java` | `domain/Effect.java` | Effect (T3) | Direct — value object |

**Benchmark rule:** An entity with a different shape (e.g., Role) is scored as `[IMPLEMENTED — reshape required]`, not `[PLANNED]`.

---

## 4. String-to-Edge Migration Map

From `modeling-taxonomy.md` section 4 — the 10 string fields that must become graph edges:

| # | Current String Field | Entity | Target Node | New Edge | Priority |
|---|---------------------|--------|-------------|----------|----------|
| 1 | `storyRefs: List<String>` | Screen | UserStory (T1) | `DELIVERS` | P0 |
| 2 | `personaId: String` | Journey | Persona (T1) | `PERFORMED_BY_PERSONA` | P0 |
| 3 | `permission: String` | Interaction | Permission (T2) | `REQUIRES_PERMISSION` | P0 |
| 4 | `channelId: String` | EntryMode (Touchpoint) | Channel (T2) | `DELIVERED_VIA_CHANNEL` | P0 |
| 5 | `apiCalls: List<String>` | Interaction | ApiContract (T1) | `CALLS_API` | P1 |
| 6 | `roleKeys: List<String>` | Screen, Interaction | BusinessRole (T1) | `ACCESSIBLE_BY_ROLE` | P1 |
| 7 | `personaIds: List<String>` | Screen, Interaction, Touchpoint | Persona (T1) | `USED_BY_PERSONA` | P2 |
| 8 | `journeyStepRefs: List<String>` | Touchpoint (frontend) | JourneyStep (T1) | `STARTS_AT_TOUCHPOINT` | P2 |
| 9 | `confirmationCode: String` | Interaction | ConfirmationDialog (T2) | `TRIGGERS_CONFIRMATION` | P3 |
| 10 | `interactionRef: String` | Effect | Interaction (T1) | `BELONGS_TO_INTERACTION` | P3 |

**P0 migrations** unblock 4 of 10 north-star queries. See `vision-benchmark.md` section 7 for full migration cost analysis.

---

## 5. Component Architecture

### 5.1 Current state

```mermaid
graph TD
    subgraph Frontend["Angular 21 Frontend"]
        AC[App Component]
        FC[Flow Canvas]
        SS[Screen Sidebar]
        DP[Detail Panel]
        SD[Screen Detail]
        TP[Touchpoint Panel]
        IP[Interaction Panel]
        JP[Journey Panel]
        INV[Inventory Panel]
        CC[Crosscutting Panel]
        API[API Service]
        STATE[State Service]
    end

    subgraph Backend["Spring Boot 3.4.1 Backend"]
        SC_CTRL[Screen Controller]
        JO_CTRL[Journey Controller]
        US_CTRL[UserStory Controller]
        IN_CTRL[Interaction Controller]
        RL_CTRL[Role Controller]
        TP_CTRL[Touchpoint Controller]
        ST_CTRL[Stats Controller]
    end

    subgraph Persistence["Neo4j Community"]
        NEO[(Neo4j)]
    end

    AC --> FC & SS & DP
    DP --> SD & TP & IP & JP & INV & CC
    FC --> API
    SS --> API
    DP --> API
    API --> STATE

    API -->|REST| SC_CTRL & JO_CTRL & US_CTRL & IN_CTRL & RL_CTRL & TP_CTRL & ST_CTRL

    SC_CTRL --> NEO
    JO_CTRL --> NEO
    US_CTRL --> NEO
    IN_CTRL --> NEO
    RL_CTRL --> NEO
    TP_CTRL --> NEO
```

### 5.2 Target state (additions shown)

```mermaid
graph TD
    subgraph Frontend["Angular 21 Frontend"]
        subgraph Views["Canonical Views"]
            SFV[Screen Flow View]
            PV[Persona View]
            JV[Journey View]
            CV[Channel View]
            DV[Delivery View]
        end
        subgraph Core["Core Components"]
            FC[Flow Canvas]
            DP[Detail Panel]
            SS[Screen Sidebar]
        end
        subgraph Services["Services"]
            API[API Service]
            STATE[State Service]
            I18N["i18n Service"]
            THEME[Theme Service]
        end
    end

    subgraph Backend["Spring Boot 3.4.1 Backend"]
        subgraph Controllers["Controllers"]
            SC_CTRL[Screen Controller]
            PE_CTRL["Persona Controller — PLANNED"]
            JO_CTRL[Journey Controller]
            CH_CTRL["Channel Controller — PLANNED"]
            US_CTRL[UserStory Controller]
            BM_CTRL["Benchmark Controller — PLANNED"]
        end
        subgraph Domain["Domain Services"]
            GRAPH["Graph Traversal Service — PLANNED"]
            COMP["Completeness Engine — PLANNED"]
        end
    end

    subgraph Persistence["Neo4j"]
        NEO[(Neo4j Graph)]
    end

    Views --> API
    Core --> API
    API --> STATE
    I18N --> API
    THEME --> FC

    API -->|REST| Controllers
    Controllers --> GRAPH
    GRAPH --> NEO
    COMP --> NEO
```

---

## 6. Core Architecture Decisions

### 6.1 Domain graph over tracker graph

Azure DevOps and Jira center on work items and issue objects. Design Hub must preserve a broader domain graph that includes personas, journeys, steps, screens, interactions, validations, messages, and APIs.

### 6.2 External artifacts as linked nodes

Azure DevOps and Jira records should be modeled as linked `ExternalArtifact` nodes instead of being flattened into primary domain nodes. This preserves:

- tool-specific identity
- field parity
- sync metadata
- many-to-one mappings between domain objects and delivery-tool records

### 6.3 Typed relationships over free-form references

Relationships such as `HAS_STEP`, `USES_SCREEN`, `CALLS_API`, `BLOCKS`, `DUPLICATES`, and `RELATES_TO` should be stored as explicit edge types so traversal logic stays deterministic.

**Current state:** 9 relationships exist as `@Relationship` edges. 9 remain as string references requiring migration. See section 4.

### 6.4 Status and readiness separation

All objects carry universal `status`. Only implementation-driving objects carry `readiness`. This avoids false precision on artifacts where readiness has no SDLC meaning.

**Current state:** `[IMPLEMENTED — reshape required]` — entities use a 3-enum status model (`IDENTIFIED`, `IN_PROGRESS`, `DONE`). Target is the universal 10-value status enum. See `implementation-readiness-graph-model.md` section 4.

### 6.5 Verification as architecture, not tooling

Playwright should be treated as a verification layer in the product architecture. Its job is to prove that the rendered UI still matches:

- graph relationships returned by the backend
- design-system token rules
- localization and RTL rules
- approved visual baselines

**Strategy:** Defined in `design-testing-strategy.md` (6 test layers, 8 anti-drift scenarios).

### 6.6 CI as enforcement, not automation theater

CI should be treated as the merge-control layer that turns architecture, documentation, design-system, and testing rules into required checks. A documented rule that does not fail CI is not a real guardrail.

**Strategy:** Defined in `ci-quality-gates.md` (PR validation + merge protection lanes).

---

## 7. i18n Architecture

### 7.1 Design decisions

| Decision | Choice | Rationale |
|---------|--------|-----------|
| Translation library | `@ngx-translate` | Standard Angular i18n library; runtime switching |
| Supported locales | `en` (English), `ar` (Arabic) | Project requirement |
| Translation storage | JSON files (`en.json`, `ar.json`) | Simple, versionable, diffable |
| RTL support | CSS logical properties + `dir="rtl"` | Standard, no layout duplication |
| Locale registry | `Locale` (T2) in graph model | Graph-aware locale tracking |
| Translation keys | `TranslationKey` (T2) in graph model | Graph-aware key registry |

### 7.2 Runtime flow

```mermaid
sequenceDiagram
    participant User
    participant LangSwitcher as Language Switcher
    participant TranslateService as ngx-translate
    participant JSONFile as en.json / ar.json
    participant DOM as Document

    User->>LangSwitcher: Select locale
    LangSwitcher->>TranslateService: use(locale)
    TranslateService->>JSONFile: Load translation file
    JSONFile-->>TranslateService: Translation keys
    TranslateService-->>DOM: Update all {{ key | translate }} bindings
    LangSwitcher->>DOM: Set dir="rtl" or dir="ltr"
```

### 7.3 File structure

```
frontend/src/
  assets/
    i18n/
      en.json          # English translations
      ar.json          # Arabic translations (RTL)
  app/
    app.config.ts      # ngx-translate configuration
    app.component.ts   # dir attribute binding
```

### 7.4 CSS logical properties

All layout CSS must use logical properties for RTL compatibility:

| Physical (avoid) | Logical (use) |
|-----------------|---------------|
| `margin-left` | `margin-inline-start` |
| `margin-right` | `margin-inline-end` |
| `padding-left` | `padding-inline-start` |
| `padding-right` | `padding-inline-end` |
| `text-align: left` | `text-align: start` |
| `float: left` | `float: inline-start` |
| `border-left` | `border-inline-start` |

**Implementation status:** `[PLANNED]` — requires Track B2 completion.

---

## 8. Design System Integration

### 8.1 Token source

Design Hub adopts EMSIST's ThinkPLUS design tokens (`--tp-*`) as the canonical imported source. All UI elements must resolve colors, typography, spacing, and accents from tokenized theme values.

### 8.2 Token contract

| Token | Purpose | Source |
|-------|---------|--------|
| `--tp-primary` | Primary brand color | EMSIST `tokens.css` |
| `--tp-primary-dark` | Primary dark variant | EMSIST `tokens.css` |
| `--tp-primary-light` | Primary light variant | EMSIST `tokens.css` |
| `--tp-danger` | Error / destructive actions | EMSIST `tokens.css` |
| `--tp-warning` | Warning states | EMSIST `tokens.css` |
| `--tp-surface` | Background surfaces | EMSIST `tokens.css` |
| `--tp-text` | Default text color | EMSIST `tokens.css` |
| `--tp-white` | White reference | EMSIST `tokens.css` |

### 8.3 Files requiring token remediation

| File | Current State | Target |
|------|--------------|--------|
| `styles.scss` | Ad-hoc CSS variables (line 3) | Import from single canonical token file |
| `default-preset.ts` | Hardcoded hex values | CSS variable references |
| `default-preset.scss` | Hardcoded shadow values | Token-ized shadows |
| `design-hub.page.ts` | Inline CSS variables | Token references |
| `flow-canvas.component.ts` | Hardcoded SVG colors | Token references |
| `inventory-panel.component.ts` | Hardcoded badge colors | Token references |

**Implementation status:** `[PLANNED]` — requires Track B1 completion.

---

## 9. Integration Model for Azure DevOps and Jira

### 9.1 Integration architecture

```mermaid
graph LR
    subgraph External["External Systems"]
        ADO[Azure DevOps]
        JIRA[Jira Cloud]
    end

    subgraph Adapters["Adapter Layer — PLANNED"]
        ADO_A[Azure DevOps Adapter]
        JIRA_A[Jira Adapter]
    end

    subgraph Graph["Design Hub Graph"]
        EA[ExternalArtifact]
        US[UserStory]
        BG[Bug]
        FE[Feature]
        FI[Finding]
    end

    ADO -->|REST API| ADO_A
    JIRA -->|REST API| JIRA_A
    ADO_A -->|creates| EA
    JIRA_A -->|creates| EA
    EA -->|REPRESENTS| US & BG & FE & FI
```

### 9.2 Azure DevOps adapter

**Design goals:**

- Ingest work item identity, type, state, area, iteration, assignment, tags, priority, parent-child, predecessor-successor, and related links
- Map Azure items to `ExternalArtifact`
- Attach mapped records to `UserStory`, `Bug`, `Feature`, or `Finding`

**Implementation status:** `[PLANNED]`

### 9.3 Jira adapter

**Design goals:**

- Ingest issue identity, issue type, status, parent, issue links, assignee, project scope, labels, and custom fields
- Preserve issue-link types with inward and outward semantics
- Map Jira issues to `ExternalArtifact`

**Implementation status:** `[PLANNED]`

---

## 10. BPMN Alignment

### 10.1 Source standard

Design Hub's process modeling objects align with the **OMG BPMN 2.0.2** specification (Business Process Model and Notation). The graph model uses `BusinessProcess`, `ProcessActivity`, `ProcessGateway`, and `ProcessEvent` as first-class nodes connected by `HAS_FLOW_NODE` and `FLOWS_TO` edges.

### 10.2 BPMN adoption profile

The meta-model adopts BPMN concepts in layered priority tiers:

| Layer | Name | Priority | Scope | Design Hub Objects |
|-------|------|----------|-------|-------------------|
| A | Process Essentials | P0 | Process, activity, start/end events | `BusinessProcess`, `ProcessActivity`, `ProcessEvent` |
| B | Control Flow | P0 | Gateways (exclusive, parallel, inclusive), sequence flows | `ProcessGateway`, `FLOWS_TO` edge |
| C | Collaboration | P2 | Pools, lanes, message flows between participants | Future — not yet modeled |
| D | Data | P2 | Data objects, data stores, data associations | Future — not yet modeled |
| E | Advanced | Out of scope | Choreography, conversation, complex events | Not planned |

### 10.3 Process layer relationship mapping

| Relationship | Context | Source | Target |
|-------------|---------|--------|--------|
| `HAS_FLOW_NODE` | Process layer | `BusinessProcess` | `ProcessActivity`, `ProcessGateway`, `ProcessEvent` |
| `FLOWS_TO` | Process layer | `ProcessActivity`, `ProcessGateway`, `ProcessEvent` | `ProcessActivity`, `ProcessGateway`, `ProcessEvent` |
| `HAS_STEP` | Journey layer (unchanged) | `Journey` | `JourneyStep` |

**Note:** `HAS_STEP` remains the Journey-to-JourneyStep relationship. Process flow structure uses `HAS_FLOW_NODE` for containment and `FLOWS_TO` for sequencing.

---

## 11. Query Patterns the Architecture Should Support

These patterns correspond to the 10 north-star queries in `product-vision.md`:

| # | Query Pattern | Current Status |
|---|--------------|---------------|
| 1 | `persona -> journeys -> steps -> screens -> stories` | `[EDGE/PARTIAL]` — `PERFORMED_BY_PERSONA`, `HAS_STEP`, `USES_SCREEN`, and `DELIVERS` are implemented; Persona→Journey begins as a reverse traversal |
| 2 | `journey -> steps -> touchpoints -> channels` | `[EDGE]` — `STARTS_AT_TOUCHPOINT`, `DELIVERED_VIA_CHANNEL`, and `TARGETS` are implemented |
| 3 | `channel -> touchpoints -> screens` | `[EDGE]` — `DELIVERED_VIA_CHANNEL` and `TARGETS` are implemented |
| 4 | `screen -> interactions -> permissions` | `[EDGE]` — `HAS_INTERACTION` and `REQUIRES_PERMISSION` are implemented end to end |
| 5 | `interaction -> outcomes -> error codes` | `[PARTIAL]` — embedded Interaction outcomes and `ON_ERROR_SHOWS` now resolve to ErrorCode, but the outcome structure is still Tier 3 rather than a first-class node |
| 6 | `screen -> stories` | `[EDGE]` — `DELIVERS` is implemented and `ScreenResponse` prefers graph-backed delivered stories |
| 7 | `bug -> affected screens` | `[EDGE]` — Bug now exists with `AFFECTS_SCREEN` |
| 8 | `artifact -> source references` | `[PARTIAL]` — `HAS_SOURCE` now exists for Screen, UserStory, and Bug |
| 9 | `external artifact -> domain objects` | `[EDGE/PARTIAL]` — ExternalArtifact now represents stories, bugs, features, tasks, findings, and API contracts, exposes hierarchy/dependency semantics plus a `customFields` bag, has a live parity-audit endpoint, can now be upserted through a generic sync endpoint with custom-field persistence, and now backfills selected normalized external fields onto the represented primary nodes; source-specific adapter automation and broader represented-type coverage remain planned |
| 10 | `interaction -> confirmation dialogs` | `[EDGE]` — ConfirmationDialog registry and `TRIGGERS_CONFIRMATION` are implemented |

---

## 12. Verification Patterns the Architecture Should Support

From `design-testing-strategy.md` — the 6 test layers map to architecture verification:

| Layer | Verification Pattern | Architecture Dependency |
|-------|---------------------|------------------------|
| 1. Smoke | App shell, routes, backend-unavailable state | Layer 5 (Presentation) |
| 2. Semantic | `API graph data -> rendered detail panel` | Layers 4-5 (Query + Presentation) |
| 3. Visual | `Screen -> interactions -> messages -> rendered UI state` | Layer 5 (Presentation) |
| 4. Token | `Token contract -> rendered style value` | Layer 5 + Design System |
| 5. i18n/RTL | `Locale -> RTL/LTR -> rendered layout` | Layer 5 + i18n |
| 6. Graph-UI drift | `persona -> journeys -> steps -> rendered UI state` | Layers 3-5 (Graph + Query + Presentation) |

---

## 13. Implementation Roadmap Implication

The next architectural step is not another specialized screen endpoint. It is a generic graph object and relation service that can support multiple views from the same node and edge model. The 13 canonical views defined in `feature-capability-map.md` section 4 share the same underlying graph — they differ only in entry point, traversal depth, and projection shape.
