# Technical Execution Context Extension — Design Spec

**Date:** 2026-03-14
**Status:** Approved
**Scope:** Application and ApplicationComponent attribute extensions, new edges, Implementation Pack traversal, agent-readiness MCR
**Depends on:** Meta-Model Revision (2026-03-14) — this spec extends the 65-element / 76-edge model

---

## 1. Problem Statement

The current graph model supports **traceability** — a UserStory can be traced to its business origin (REALIZES), its deliverables (DELIVERS), its work decomposition (HAS_TASK), and its verification (VERIFIED_BY). But the model does not support **technical execution resolution** — it cannot answer:

- Which microservice/module should change?
- What framework governs that module?
- Where is the code (repo path, module path)?
- How do I build it? How do I test it?
- What other services depend on this component?
- What data entities and rules does this component own?

Without this, a coding agent (Claude, Codex, Copilot) must infer architecture from repo exploration. That works often but is not governed, not benchmarkable, and not 0-drift.

### What the agent needs

A story documented in Git is **necessary but not sufficient**. For agent-safe code generation, each story must resolve to an **Implementation Pack** — a traversable subgraph that gives the agent:

| Concern | Source |
|---------|--------|
| Story intent | UserStory attributes |
| Business context | REALIZES targets (ProcessActivity, JourneyStep) |
| Deliverables | DELIVERS targets (Screen, ApiContract, DataEntity, Rule, Message) |
| Owning component | ApplicationComponent via SUPPORTS_SCREEN, EXPOSES, OWNS_DATA_ENTITY, ENFORCES_RULE, or transitively via HAS_MESSAGE→Screen→SUPPORTS_SCREEN for Message deliverables |
| Framework/runtime/module | ApplicationComponent execution attributes |
| Build/test commands | ApplicationComponent overrides, falling back to Application defaults via COALESCE |
| Work decomposition | Task nodes via HAS_TASK |
| Task target components | Task -[IMPLEMENTS]-> ApplicationComponent |
| Acceptance criteria | AcceptanceCriterion via HAS_CRITERION |
| Governing rules | Rule via GOVERNED_BY_RULE |
| Verification targets | TestCase via VERIFIED_BY |

### What the agent already gets from the repo

Codex/Claude can infer technical architecture from 4 evidence sources:

1. **Repo structure** — folders, module boundaries, service boundaries, source layout
2. **Build/runtime manifests** — `pom.xml`, `package.json`, `angular.json`, `tsconfig*`, `*.csproj`, `application*.yml`, `Dockerfile`, `docker-compose.yml`
3. **Contracts and infra artifacts** — OpenAPI specs, DB migrations, event schemas, gateway configs, CI workflows
4. **Architecture metadata** — which service owns what, which API belongs to which service, which frontend calls which backend

The graph extension makes this architecture **explicit, queryable, and benchmarkable** rather than inferred.

---

## 2. Design Decisions (Frozen)

| # | Decision | Resolution |
|---|----------|------------|
| 1 | Where to put execution metadata | ApplicationComponent (not Application). One Application can contain mixed tech (Angular frontend + Spring services + .NET workers). |
| 2 | Application role | Repo/workspace defaults only. Human-readable `technologyStack` summary stays. |
| 3 | Framework typing | `frameworkFamily` (enum) + `frameworkName` (string) + `frameworkVersion` (string). Not rigid enum-only — frameworks drift faster than the metamodel. |
| 4 | Inter-component dependencies | `DEPENDS_ON_COMPONENT` (not DEPENDS_ON_SERVICE). Component types include frontends, libraries, gateways, workers — SERVICE is too narrow. |
| 5 | DataEntity/Rule resolution | Add `OWNS_DATA_ENTITY` and `ENFORCES_RULE` edges from ApplicationComponent. Without these, stories delivering DataEntity or Rule dead-end at the architecture layer. |
| 6 | Implementation Pack | Computed traversal result, NOT a first-class node. The graph is source of truth. Snapshot/export artifacts can be generated from the traversal for reproducibility. |
| 7 | Agent-readiness MCR | ADVISORY by default. BLOCKING only when `executionMode = AGENT_FIRST`. |
| 8 | OWNS_API vs EXPOSES | Keep EXPOSES. Do not add duplicate OWNS_API — same semantics. |
| 9 | Task IMPLEMENTS targets | Extend to include ApplicationComponent alongside Screen, ApiContract, DataEntity, Rule, Message, TestCase. |

---

## 3. Application — Extended Attributes

**Current attributes** (unchanged): `applicationId`, `name`, `description`, `applicationType`, `technologyStack`, `owner`, `status`, `sourceRefs`

**New attributes:**

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `repoPath` | String | No | Relative path from workspace root or absolute path | e.g., `.` for monorepo root, `backend/` for backend subtree |
| `repoUrl` | String | No | Git clone URL | For multi-repo setups |
| `workspaceType` | Enum | No | Repository structure | `MONOREPO`, `POLYREPO` |
| `defaultBuildCommand` | String | No | Fallback build command if component doesn't override | e.g., `mvn clean verify` |
| `defaultTestCommand` | String | No | Fallback test command if component doesn't override | e.g., `mvn test` |

**`technologyStack`** remains as free-text String — a human-readable summary like "Spring Boot 3.4 + Angular 21 + Neo4j". Structured detail lives on ApplicationComponent.

**Relationships**: No new edges on Application.

---

## 4. ApplicationComponent — Extended Attributes

**Current attributes retained**: `componentId`, `name`, `description`, `status`, `sourceRefs`

**`componentType`** — enum expanded:

Old: `FRONTEND`, `BACKEND`, `SERVICE`, `LIBRARY`, `DATABASE`, `INTEGRATION`

New: `FRONTEND_APP`, `BFF`, `MICROSERVICE`, `LIBRARY`, `WORKER`, `DB_ADAPTER`, `GATEWAY`, `SERVICE_REGISTRY`

**`technologyStack`** — removed (free-text). Replaced by structured fields below.

**New attributes:**

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `frameworkFamily` | Enum | Yes | Framework family classification | `ANGULAR`, `SPRING_BOOT`, `ASP_NET_CORE`, `NODE_EXPRESS`, `NODE_NEST`, `REACT`, `VUE`, `FASTAPI`, `DJANGO`, `FLASK`, `SVELTE`, `NEXTJS` |
| `frameworkName` | String | No | Exact framework name (for non-standard or emerging frameworks) | Free text. e.g., `Spring Boot`, `ASP.NET Core`, `Analog` |
| `frameworkVersion` | String | No | Pinned framework version | e.g., `3.4.1`, `21.0.0`, `8.0` |
| `runtime` | Enum | No | Execution environment | `BROWSER`, `JVM`, `DOTNET_CLR`, `NODE`, `PYTHON`, `CONTAINER` (use CONTAINER only for infrastructure components without a language-level runtime, e.g., Envoy, Nginx) |
| `language` | Enum | Yes | Primary implementation language | `TYPESCRIPT`, `JAVA`, `CSHARP`, `JAVASCRIPT`, `PYTHON`, `KOTLIN`, `GO` |
| `languageVersion` | String | No | Language version | e.g., `Java 23`, `TypeScript 5.4`, `.NET 8` |
| `modulePath` | String | No | Path relative to Application's repoPath | e.g., `backend/auth-facade/`, `frontend/src/app/features/design-hub/` |
| `manifestPath` | String | No | Build manifest relative to modulePath | e.g., `pom.xml`, `package.json`, `*.csproj` |
| `buildCommand` | String | No | Overrides Application default | e.g., `mvn clean verify -pl auth-facade` |
| `testCommand` | String | No | Overrides Application default | e.g., `npx vitest run`, `dotnet test` |
| `entrypointPath` | String | No | Main entry file relative to modulePath | e.g., `src/main/java/.../AuthFacadeApplication.java`, `src/main.ts` |

### 4.1 Framework typing rationale

A rigid enum-only approach breaks when:
- A team uses NestJS (not Express) on Node
- A new Angular-like framework (Analog) appears
- A team pins Spring Boot 3.4 but another pins 3.2

The three-field design handles this:
- `frameworkFamily: SPRING_BOOT` — enough for the agent to know patterns
- `frameworkName: "Spring Boot"` — exact name for human readability
- `frameworkVersion: "3.4.1"` — precise version for dependency-aware decisions

---

## 5. New Edges

### 5.1 DEPENDS_ON_COMPONENT

| Property | Value |
|----------|-------|
| Source | ApplicationComponent |
| Target | ApplicationComponent |
| Cardinality | N:M |
| Severity | OPTIONAL |
| Implementation | `[PLANNED]` |

**Edge properties:**

| Property | Type | Required | Description |
|----------|------|----------|-------------|
| `dependencyType` | Enum | No | `SYNC_API`, `ASYNC_EVENT`, `SHARED_DB`, `SHARED_LIBRARY`, `GATEWAY_ROUTE` |
| `protocol` | String | No | e.g., `REST`, `gRPC`, `Kafka`, `RabbitMQ` |
| `required` | Boolean | No | Is this dependency required for the component to function? Default: true |

### 5.2 OWNS_DATA_ENTITY

| Property | Value |
|----------|-------|
| Source | ApplicationComponent |
| Target | DataEntity |
| Cardinality | 1:N |
| Severity | OPTIONAL |
| Implementation | `[PLANNED]` |

Closes the resolution dead-end for stories that DELIVERS a DataEntity. Without this edge, `UserStory -[DELIVERS]-> DataEntity` cannot resolve to an owning component.

### 5.3 ENFORCES_RULE

| Property | Value |
|----------|-------|
| Source | ApplicationComponent |
| Target | Rule |
| Cardinality | N:M |
| Severity | OPTIONAL |
| Implementation | `[PLANNED]` |

Closes the resolution dead-end for stories that DELIVERS a Rule.

### 5.4 IMPLEMENTS — Extended Target Set

Task `IMPLEMENTS` edge targets expanded from:
- Old: `Screen, ApiContract, DataEntity, Rule, Message, TestCase`
- New: `Screen, ApiContract, DataEntity, Rule, Message, TestCase, ApplicationComponent`

This lets a Task point directly to the component it modifies.

**Note:** This amends the canonical plan's Task IMPLEMENTS target set (plan line 196). The plan file must be updated to reflect this expanded target set.

### 5.5 GOVERNED_BY_RULE — Normalization Required

The Implementation Pack traversal uses `GOVERNED_BY_RULE` as the edge from UserStory to Rule (constraints that govern the story's implementation). Before implementation planning, this edge name must be normalized in the canonical catalog and relationship registry. If the live catalog uses a different name (e.g., `HAS_RULE`, `CONSTRAINED_BY`), it must be reconciled to `GOVERNED_BY_RULE` to match this spec's Cypher queries.

---

## 6. UserStory — New Attribute

| Attribute | Type | Required | Description | Constraints |
|-----------|------|----------|-------------|-------------|
| `executionMode` | Enum | No | How the story will be implemented | `HUMAN_ONLY`, `AGENT_ASSISTED`, `AGENT_FIRST`. Default: `HUMAN_ONLY` |

This field controls the MCR-STORY-AGENT-READY-001 severity. When `AGENT_FIRST`, the MCR becomes BLOCKING — the story cannot proceed to implementation without a resolvable Implementation Pack.

---

## 7. Implementation Pack — Computed Traversal

The Implementation Pack is a **computed traversal result**, not a stored node. It resolves a UserStory to its full execution context via the following chain:

```
UserStory
  -[REALIZES]-> ProcessActivity | JourneyStep               // business context
  -[DELIVERS]-> Screen | ApiContract | DataEntity | Rule | Message  // deliverables
  -[HAS_TASK]-> Task                                         // work decomposition
  -[VERIFIED_BY]-> TestCase                                  // verification
  -[HAS_CRITERION]-> AcceptanceCriterion                     // acceptance criteria
  -[GOVERNED_BY_RULE]-> Rule                                 // constraints

Screen       <-[SUPPORTS_SCREEN]-   ApplicationComponent     // owning component (direct)
ApiContract  <-[EXPOSES]-           ApplicationComponent     // owning component (direct)
DataEntity   <-[OWNS_DATA_ENTITY]-  ApplicationComponent     // owning component (direct)
Rule         <-[ENFORCES_RULE]-     ApplicationComponent     // owning component (direct)
Message      <-[HAS_MESSAGE]- Screen <-[SUPPORTS_SCREEN]- ApplicationComponent  // owning component (transitive)

ApplicationComponent
  -> frameworkFamily, language, modulePath, buildCommand, testCommand  // execution context
  <-[HAS_COMPONENT]- Application -> repoPath, repoUrl                  // repo location
  -[DEPENDS_ON_COMPONENT]-> ApplicationComponent                      // service dependencies

Task
  -[IMPLEMENTS]-> ApplicationComponent                        // which component to change
```

### 7.1 Canonical Cypher Query

```cypher
// Stage 1: Aggregate task target components per task (avoids nested collect)
MATCH (us:UserStory {storyId: $storyId})
OPTIONAL MATCH (us)-[:HAS_TASK]->(task:Task)
OPTIONAL MATCH (task)-[:IMPLEMENTS]->(taskComp:ApplicationComponent)
WITH us, task, collect(DISTINCT taskComp {.componentId, .name, .modulePath, .frameworkFamily}) AS taskTargets
WITH us, collect(DISTINCT task {.*, targetComponents: taskTargets}) AS tasks

// Stage 2: Business context
OPTIONAL MATCH (us)-[:REALIZES]->(origin)
WHERE origin:ProcessActivity OR origin:JourneyStep
WITH us, tasks, collect(DISTINCT origin {.*}) AS businessContext

// Stage 3: Deliverables and owning components (direct + transitive via Message→Screen)
OPTIONAL MATCH (us)-[:DELIVERS]->(deliverable)
OPTIONAL MATCH (deliverable)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-(directComp:ApplicationComponent)
// Transitive: Message <-[HAS_MESSAGE]- Screen <-[SUPPORTS_SCREEN]- ApplicationComponent
OPTIONAL MATCH (deliverable)<-[:HAS_MESSAGE]-(msgScreen:Screen)<-[:SUPPORTS_SCREEN]-(transitiveComp:ApplicationComponent)
WHERE deliverable:Message
WITH us, tasks, businessContext, deliverable,
     COALESCE(directComp, transitiveComp) AS comp
OPTIONAL MATCH (comp)<-[:HAS_COMPONENT]-(app:Application)
WITH us, tasks, businessContext,
     collect(DISTINCT deliverable {.*}) AS deliverables,
     collect(DISTINCT comp {
         .componentId, .name, .componentType,
         .frameworkFamily, .frameworkName, .frameworkVersion,
         .runtime, .language, .languageVersion,
         .modulePath, .manifestPath,
         buildCommand: COALESCE(comp.buildCommand, app.defaultBuildCommand),
         testCommand: COALESCE(comp.testCommand, app.defaultTestCommand),
         .entrypointPath,
         application: app {.applicationId, .name, .repoPath, .repoUrl, .workspaceType,
                           .defaultBuildCommand, .defaultTestCommand}
     }) AS owningComponents

// Stage 4: Component dependencies (re-match including transitive Message path)
OPTIONAL MATCH (us)-[:DELIVERS]->(d2)
OPTIONAL MATCH (d2)<-[:SUPPORTS_SCREEN|EXPOSES|OWNS_DATA_ENTITY|ENFORCES_RULE]-(directC2:ApplicationComponent)
OPTIONAL MATCH (d2)<-[:HAS_MESSAGE]-(ms2:Screen)<-[:SUPPORTS_SCREEN]-(transitiveC2:ApplicationComponent)
WHERE d2:Message
WITH us, tasks, businessContext, deliverables, owningComponents,
     COALESCE(directC2, transitiveC2) AS c2
OPTIONAL MATCH (c2)-[dep:DEPENDS_ON_COMPONENT]->(depComp:ApplicationComponent)
WITH us, tasks, businessContext, deliverables, owningComponents,
     collect(DISTINCT depComp {
         .componentId, .name, .frameworkFamily, .modulePath,
         dependencyType: dep.dependencyType,
         protocol: dep.protocol,
         required: dep.required
     }) AS componentDependencies

// Stage 5: Verification and criteria
OPTIONAL MATCH (us)-[:VERIFIED_BY]->(tc:TestCase)
OPTIONAL MATCH (us)-[:HAS_CRITERION]->(ac:AcceptanceCriterion)
OPTIONAL MATCH (us)-[:GOVERNED_BY_RULE]->(rule:Rule)

RETURN us {
    .*,
    businessContext: businessContext,
    deliverables: deliverables,
    owningComponents: owningComponents,
    componentDependencies: componentDependencies,
    tasks: tasks,
    testCases: collect(DISTINCT tc {.*}),
    acceptanceCriteria: collect(DISTINCT ac {.*}),
    governingRules: collect(DISTINCT rule {.*})
}
```

### 7.2 What the resolved pack gives an agent

| Concern | Resolved from | Example |
|---------|---------------|---------|
| Story intent | `us.title`, `us.description` | "Add role-based filtering to screen sidebar" |
| Execution mode | `us.executionMode` | `AGENT_ASSISTED` |
| Business context | `businessContext` | ProcessActivity "Filter Screens by Role" |
| What to deliver | `deliverables` | Screen `SCR-DH-SIDEBAR`, ApiContract `API-SCREEN-LIST` |
| Which service | `owningComponents[].frameworkFamily` | `SPRING_BOOT` (definition-service) |
| Which module | `owningComponents[].modulePath` | `backend/definition-service/` |
| Which framework | `owningComponents[].frameworkFamily` + `frameworkVersion` | `SPRING_BOOT` / `3.4.1` |
| Which language | `owningComponents[].language` + `languageVersion` | `JAVA` / `Java 23` |
| How to build | `owningComponents[].buildCommand` (component override → Application default) | `mvn clean verify -pl definition-service` |
| How to test | `owningComponents[].testCommand` (component override → Application default) | `mvn test -pl definition-service` |
| Service dependencies | `componentDependencies` | auth-facade (SYNC_API, REST, required) |
| What code to write | `tasks[].title`, `tasks[].targetComponents` | "Add roleKeys filter" → definition-service |
| Acceptance criteria | `acceptanceCriteria` | "Given admin role, sidebar shows all screens" |
| Rules/constraints | `governingRules` | "Screens without roles are visible to all" |
| Test expectations | `testCases` | "TC-SCREEN-FILTER-001: verify role filtering" |

---

## 8. Agent-Readiness MCR

**MCR-STORY-AGENT-READY-001**

| Property | Value |
|----------|-------|
| Applies to | UserStory |
| Condition | At least one DELIVERS target resolves (via SUPPORTS_SCREEN, EXPOSES, OWNS_DATA_ENTITY, ENFORCES_RULE, or transitively via HAS_MESSAGE→Screen→SUPPORTS_SCREEN) to an ApplicationComponent with `frameworkFamily`, `modulePath`, and effective `testCommand` (component override or Application default) populated |
| Default severity | **ADVISORY** |
| Conditional severity | **BLOCKING** when `executionMode = AGENT_FIRST` |
| Gate | Cannot enter `IN_IMPLEMENTATION` when BLOCKING |

**Cypher check:**

```cypher
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
```

**Command precedence rule:** `COALESCE(comp.testCommand, app.defaultTestCommand)` — component-level overrides Application-level defaults. Same precedence applies to `buildCommand`.

---

## 9. Example: Design Hub Mapped

### 9.1 Application

| Attribute | Value |
|-----------|-------|
| applicationId | `APP-DH-001` |
| name | Design Hub |
| technologyStack | "Spring Boot 3.4 + Angular 21 + Neo4j + PostgreSQL" |
| repoPath | `.` |
| workspaceType | `MONOREPO` |
| defaultBuildCommand | `mvn clean verify` |
| defaultTestCommand | `mvn test` |

### 9.2 ApplicationComponents

| componentId | name | componentType | frameworkFamily | language | modulePath | buildCommand | testCommand |
|-------------|------|---------------|----------------|----------|------------|--------------|-------------|
| `CMP-DH-FE` | Design Hub Frontend | FRONTEND_APP | ANGULAR | TYPESCRIPT | `frontend/` | `ng build` | `npx vitest run` |
| `CMP-DH-DEF` | Definition Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/definition-service/` | `mvn clean verify -pl definition-service` | `mvn test -pl definition-service` |
| `CMP-DH-AUTH` | Auth Facade | MICROSERVICE | SPRING_BOOT | JAVA | `backend/auth-facade/` | `mvn clean verify -pl auth-facade` | `mvn test -pl auth-facade` |
| `CMP-DH-GW` | API Gateway | GATEWAY | SPRING_BOOT | JAVA | `backend/api-gateway/` | `mvn clean verify -pl api-gateway` | `mvn test -pl api-gateway` |
| `CMP-DH-TENANT` | Tenant Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/tenant-service/` | `mvn clean verify -pl tenant-service` | `mvn test -pl tenant-service` |
| `CMP-DH-USER` | User Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/user-service/` | `mvn clean verify -pl user-service` | `mvn test -pl user-service` |
| `CMP-DH-LICENSE` | License Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/license-service/` | `mvn clean verify -pl license-service` | `mvn test -pl license-service` |
| `CMP-DH-NOTIF` | Notification Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/notification-service/` | `mvn clean verify -pl notification-service` | `mvn test -pl notification-service` |
| `CMP-DH-AUDIT` | Audit Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/audit-service/` | `mvn clean verify -pl audit-service` | `mvn test -pl audit-service` |
| `CMP-DH-AI` | AI Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/ai-service/` | `mvn clean verify -pl ai-service` | `mvn test -pl ai-service` |
| `CMP-DH-PROC` | Process Service | MICROSERVICE | SPRING_BOOT | JAVA | `backend/process-service/` | `mvn clean verify -pl process-service` | `mvn test -pl process-service` |
| `CMP-DH-EUREKA` | Service Registry | SERVICE_REGISTRY | SPRING_BOOT | JAVA | `backend/eureka-server/` | `mvn clean verify -pl eureka-server` | `mvn test -pl eureka-server` |

### 9.3 Cross-framework example (.NET + Angular)

| componentId | name | componentType | frameworkFamily | language | modulePath | buildCommand | testCommand |
|-------------|------|---------------|----------------|----------|------------|--------------|-------------|
| `CMP-INV-FE` | Inventory UI | FRONTEND_APP | ANGULAR | TYPESCRIPT | `apps/inventory-ui/` | `ng build inventory-ui` | `ng test inventory-ui` |
| `CMP-INV-API` | Inventory API | MICROSERVICE | ASP_NET_CORE | CSHARP | `services/inventory-api/` | `dotnet build` | `dotnet test` |
| `CMP-INV-WORKER` | Inventory Worker | WORKER | ASP_NET_CORE | CSHARP | `services/inventory-worker/` | `dotnet build` | `dotnet test` |

### 9.4 Implementation Pack — Resolved Example

Story: `US-DH-042` "Add role-based filtering to screen sidebar"

```json
{
  "story": {
    "storyId": "US-DH-042",
    "title": "Add role-based filtering to screen sidebar",
    "executionMode": "AGENT_ASSISTED",
    "status": "APPROVED"
  },
  "businessContext": [
    { "type": "ProcessActivity", "name": "Filter Screens by Role", "activityId": "ACT-SCR-003" }
  ],
  "deliverables": [
    { "type": "Screen", "surfaceId": "SCR-DH-SIDEBAR", "label": "Screen Sidebar" },
    { "type": "ApiContract", "contractId": "API-SCREEN-LIST", "method": "GET", "path": "/api/screens" }
  ],
  "owningComponents": [
    {
      "componentId": "CMP-DH-FE",
      "name": "Design Hub Frontend",
      "frameworkFamily": "ANGULAR",
      "language": "TYPESCRIPT",
      "modulePath": "frontend/src/app/features/design-hub/components/screen-sidebar/",
      "buildCommand": "ng build",
      "testCommand": "npx vitest run",
      "application": { "repoPath": ".", "workspaceType": "MONOREPO", "defaultBuildCommand": "mvn clean verify", "defaultTestCommand": "mvn test" }
    },
    {
      "componentId": "CMP-DH-DEF",
      "name": "Definition Service",
      "frameworkFamily": "SPRING_BOOT",
      "language": "JAVA",
      "modulePath": "backend/definition-service/",
      "buildCommand": "mvn clean verify -pl definition-service",
      "testCommand": "mvn test -pl definition-service",
      "application": { "repoPath": ".", "workspaceType": "MONOREPO", "defaultBuildCommand": "mvn clean verify", "defaultTestCommand": "mvn test" }
    }
  ],
  "componentDependencies": [
    {
      "component": { "componentId": "CMP-DH-AUTH", "frameworkFamily": "SPRING_BOOT", "modulePath": "backend/auth-facade/" },
      "dependencyType": "SYNC_API",
      "protocol": "REST",
      "required": true
    }
  ],
  "tasks": [
    {
      "taskId": "TSK-DH-084",
      "title": "Add roleKeys filter parameter to ScreenController.getScreens()",
      "taskType": "BACKEND",
      "targetComponents": [{ "componentId": "CMP-DH-DEF", "frameworkFamily": "SPRING_BOOT" }]
    },
    {
      "taskId": "TSK-DH-085",
      "title": "Add role filter dropdown to screen-sidebar component",
      "taskType": "FRONTEND",
      "targetComponents": [{ "componentId": "CMP-DH-FE", "frameworkFamily": "ANGULAR" }]
    }
  ],
  "testCases": [
    { "testCaseId": "TC-SCREEN-FILTER-001", "title": "Verify role-based screen filtering returns only accessible screens" }
  ],
  "acceptanceCriteria": [
    { "criterionId": "AC-042-001", "description": "Given admin role, sidebar shows all screens" },
    { "criterionId": "AC-042-002", "description": "Given viewer role, sidebar hides admin-only screens" }
  ],
  "governingRules": [
    { "ruleId": "RULE-SCR-ACCESS-001", "description": "Screens without roleKeys are visible to all roles" }
  ]
}
```

---

## 10. Impact on Model Counts

| Change | Impact |
|--------|--------|
| New attributes on Application | +5 attributes, 0 new nodes |
| New/modified attributes on ApplicationComponent | +11 new attributes, -1 removed (`technologyStack`), `componentType` enum expanded |
| New attribute on UserStory | +1 (`executionMode`) |
| New edge: `DEPENDS_ON_COMPONENT` | +1 edge type |
| New edge: `OWNS_DATA_ENTITY` | +1 edge type |
| New edge: `ENFORCES_RULE` | +1 edge type |
| Extended edge: `IMPLEMENTS` target set | 0 new edge types (target set grows to include ApplicationComponent) |
| **Net node count** | **65 unchanged** |
| **Net benchmarkable** | **61 unchanged** |
| **Net edge count** | **79** (was 76, +3: DEPENDS_ON_COMPONENT, OWNS_DATA_ENTITY, ENFORCES_RULE) |

---

## 11. Product Vision Addition

Add to `product-vision.md` after section 8.5 (Four-verb edge model):

> ### 8.7 Implementation Pack resolution
>
> Every UserStory must be resolvable to a complete **Implementation Pack** — a traversable subgraph that gives a human or coding agent everything needed to change code safely. The resolution chain is:
>
> ```
> Story → deliverables → owning ApplicationComponent → execution context
> ```
>
> The pack includes: story intent, business context, deliverables, owning component(s) with framework/module/build/test metadata, work decomposition (tasks), acceptance criteria, governing rules, and verification targets (test cases).
>
> A story that cannot resolve to at least one ApplicationComponent with populated execution metadata (`frameworkFamily`, `modulePath`, and effective `testCommand` — component override or Application default) is scored as **not agent-ready** by the benchmark (MCR-STORY-AGENT-READY-001).
>
> The Implementation Pack is a **computed traversal**, not a stored node. The graph is the source of truth.

---

## 12. Propagation Matrix

| # | File | Key Changes |
|---|------|-------------|
| 1 | `graph-object-catalog.md` | Update Application spec (+5 attrs), update ApplicationComponent spec (+11 attrs, -1, enum expansion), add DEPENDS_ON_COMPONENT/OWNS_DATA_ENTITY/ENFORCES_RULE specs, extend Task IMPLEMENTS targets, add UserStory `executionMode`, update relationship registry (76→79 edges) |
| 2 | `modeling-taxonomy.md` | Update edge count 76→79, add three new edges to edge taxonomy |
| 3 | `product-vision.md` | Add section 8.7 (Implementation Pack resolution) |
| 4 | `vision-benchmark.md` | Add agent-readiness dimension, add Implementation Pack queryability test, update edge count |
| 5 | `implementation-readiness-graph-model.md` | Add MCR-STORY-AGENT-READY-001, update edge inventory 76→79 |
| 6 | `feature-capability-map.md` | Update edge count references |
| 7 | `design-testing-strategy.md` | Add Implementation Pack resolution test |
| 8 | Canonical plan file | Update edge count 76→79, update Task IMPLEMENTS target set to include ApplicationComponent |

---

## 13. Verification

After propagation, verify:

1. All files reference **79 edge types** (was 76)
2. Application has `repoPath`, `repoUrl`, `workspaceType`, `defaultBuildCommand`, `defaultTestCommand`
3. ApplicationComponent has `frameworkFamily`, `frameworkName`, `frameworkVersion`, `runtime`, `language`, `languageVersion`, `modulePath`, `manifestPath`, `buildCommand`, `testCommand`, `entrypointPath`
4. UserStory has `executionMode` attribute
5. DEPENDS_ON_COMPONENT, OWNS_DATA_ENTITY, ENFORCES_RULE are in all relationship registries
6. Task IMPLEMENTS targets include ApplicationComponent
7. MCR-STORY-AGENT-READY-001 documented with ADVISORY/BLOCKING conditional logic
8. Implementation Pack Cypher query documented in at least `graph-object-catalog.md` and `implementation-readiness-graph-model.md`
9. No file claims Implementation Pack is a stored node — it is always described as a computed traversal
10. Message deliverables resolve transitively via `Message <-[HAS_MESSAGE]- Screen <-[SUPPORTS_SCREEN]- ApplicationComponent` — no dead-end
11. Application `defaultBuildCommand`/`defaultTestCommand` are projected into the pack with `COALESCE(comp.*, app.default*)` precedence
12. `GOVERNED_BY_RULE` edge name is normalized in the catalog before Implementation Pack query is integrated
