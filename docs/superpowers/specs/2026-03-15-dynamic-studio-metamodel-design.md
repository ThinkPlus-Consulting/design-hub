# EMSIST Dynamic Studio Meta-Model Design

**Date:** 2026-03-15
**Status:** Approved
**Scope:** Unified dynamic meta-model for `EA Studio` and `Design Studio`, service responsibilities, agent model, and schema overlay strategy

---

## 1. Problem Statement

The current Design Hub model is strong at documenting an implementation-ready graph, but it is still shaped as a static code-defined meta-model. That is not sufficient for EMSIST as a multi-domain platform because:

- organizations share management and support concepts, but differ in core business objects
- the same platform must support courts, property, inspection, legal, and other domain-specific models
- tenant-specific business objects cannot require Java entity changes and redeployment
- coding-agent handoff requires a graph that is both flexible and structurally protected

The target architecture is therefore:

- one EMSIST application
- one unified meta-model
- fully dynamic definitions and instances
- platform-controlled defaults with tenant extension
- AI reasoning on top of a validated graph engine

---

## 2. System Identity and Scope

EMSIST is a single application with licensed feature modules:

- `EA Studio`
  Purpose: capability maps, operating model, portfolios, assessments, gaps, action plans
- `Design Studio`
  Purpose: HLD, application/component structure, requirements generation, safety gating, coding-agent handoff

Both modules operate on the same graph and the same dynamic meta-model.

`Design Studio` is not the code builder. It is the requirements generation and safe handoff system that prepares implementation chunks for coding agents.

---

## 3. Core Decisions

### 3.1 Frozen Decisions

| # | Decision | Resolution |
|---|----------|------------|
| 1 | Application identity | `Application` remains a first-class management concept |
| 2 | One system or two | One EMSIST application with licensed `EA Studio` and `Design Studio` features |
| 3 | Static vs dynamic meta-model | Fully dynamic, data-driven meta-model |
| 4 | Core schema storage | Definitions are stored as data, not Java domain classes |
| 5 | Schema/instance split | Separate schema layer and instance layer |
| 6 | Business terminology in UI | Use `Object Definition`, `Attribute Definition`, `Connection Definition`, `Object`, `Attribute`, `Connection` |
| 7 | Backend naming | Avoid `Object` as a class name; use `EntityDefinition`, `EntityInstance`, `AttributeValue` |
| 8 | Component role truth | Structural role on components, change intent on project scope, explicit realization bridge |
| 9 | Implementation pack | Computed projection, not canonical stored source truth |
| 10 | Audit trail | Persist `SafetyAssessment` and `AgentExchange` as default shared definitions |
| 11 | Service split | `definition-service` is authoritative graph engine; `ai-service` is reasoning/orchestration layer |
| 12 | Overlay strategy | `inherit-and-extend` |
| 13 | Alternative rejected | `copy-on-create` rejected except for explicit exceptional tenants |
| 14 | Connection definition modeling | `Connection Definition` is a first-class node with canonical source/target edges |
| 15 | Attribute value storage | Typed storage, not string-only |
| 16 | Connection instance modeling | Keep as edge for now; promote to node only if lifecycle demands it |
| 17 | Delete policy | Soft delete by default |

### 3.2 Four Truths

The unified model separates four truths:

- `EA truth`
  Capability, process, policy, assessment, gap
- `solution truth`
  Application, component, deliverable, code asset
- `realization truth`
  How capability and process slices are realized through solution structure
- `change truth`
  Project, story, task, handoff, evidence

This prevents the model from mixing enduring structure with temporary delivery work.

---

## 4. Unified Conceptual Model

### 4.1 Canonical Management Concepts

The platform ships default shared definitions for management concepts, including:

- `BusinessDomain`
- `BusinessCapability`
- `BusinessProcess`
- `ProcessActivity`
- `Application`
- `ApplicationComponent`
- `Assessment`
- `ActionPlan`
- `ProjectInstance`
- `CapabilityRealization`
- `Epic`
- `Feature`
- `UserStory`
- `Task`
- `Screen`
- `ApiContract`
- `DataEntity`
- `Rule`
- `TestCase`
- `CodeAsset`
- `Gap`
- `EvidenceRecord`
- `SafetyAssessment`
- `AgentExchange`

These are platform defaults, not hardcoded domain entities.

### 4.2 Core Realization Chain

The full cross-layer traversal is:

`Assessment -> Gap -> ActionPlan -> ProjectInstance -> Application -> ApplicationComponent -> deliverables -> CodeAsset -> Evidence`

With realization context:

`CapabilityRealization -> BusinessCapability -> BusinessProcess -> ProcessActivity -> ApplicationComponent`

And delivery context:

`Epic -> Feature -> UserStory -> Task`

---

## 5. Dynamic Meta-Model

### 5.1 Business Terminology and Technical Mapping

| UI Term | Backend Term | Purpose |
|---------|--------------|---------|
| Object Definition | `EntityDefinition` | Defines a type of thing |
| Attribute Definition | `AttributeDefinition` | Defines a property |
| Connection Definition | `ConnectionDefinition` | Defines an allowed relationship |
| Object | `EntityInstance` | Actual data instance |
| Attribute | `AttributeValue` | Actual value on an object |
| Connection | `CONNECTED_TO` edge | Actual relationship between objects |

### 5.2 Schema Layer

The schema layer defines what can exist.

#### EntityDefinition

Represents a platform or tenant-defined type.

Key attributes:

- `definitionId`
- `tenantId`
- `name`
- `typeKey`
- `code`
- `description`
- `iconName`
- `iconColor`
- `status`
- `state`
- `overlayMode`
- `extendsDefinitionId`
- `forkedFromDefinitionId`
- `createdAt`
- `updatedAt`

Canonical edges:

- `HAS_ATTRIBUTE_DEF -> AttributeDefinition`
- `HAS_CONNECTION_DEF -> ConnectionDefinition`
- `IS_SUBTYPE_OF -> EntityDefinition`

#### AttributeDefinition

Represents a reusable attribute definition.

Key attributes:

- `attributeDefinitionId`
- `tenantId`
- `name`
- `attributeKey`
- `dataType`
- `attributeGroup`
- `description`
- `defaultValue`
- `validationRules`
- `origin`
- `locked`
- `createdAt`
- `updatedAt`

`HAS_ATTRIBUTE_DEF` relationship properties:

- `isRequired`
- `displayOrder`
- `origin`
- `locked`
- `unique`
- `indexed`
- `allowedValues`

#### ConnectionDefinition

Represents an allowed connection type as a first-class node.

Key attributes:

- `connectionDefinitionId`
- `tenantId`
- `connectionKey`
- `activeName`
- `passiveName`
- `cardinality`
- `minSource`
- `maxSource`
- `minTarget`
- `maxTarget`
- `isDirected`
- `description`
- `origin`
- `locked`
- `createdAt`
- `updatedAt`

Canonical edges:

- `SOURCE_TYPE -> EntityDefinition`
- `TARGET_TYPE -> EntityDefinition`

`sourceTypeKey` and `targetTypeKey` may be denormalized for query speed, but they are not canonical truth.

### 5.3 Instance Layer

The instance layer defines what does exist.

#### EntityInstance

Represents an actual object in a tenant graph.

Key attributes:

- `entityInstanceId`
- `tenantId`
- `typeKey`
- `label`
- `code`
- `status`
- `createdAt`
- `updatedAt`
- `createdBy`
- `updatedBy`

Canonical edges:

- `INSTANCE_OF -> EntityDefinition`
- `HAS_ATTRIBUTE -> AttributeValue`

#### AttributeValue

Represents a typed value attached to an object.

Key attributes:

- `attributeValueId`
- `tenantId`
- `attributeKey`
- `dataType`
- `stringValue`
- `numberValue`
- `booleanValue`
- `dateValue`
- `dateTimeValue`
- `jsonValue`
- `normalizedValue`
- `createdAt`
- `updatedAt`

Canonical edge:

- `FOR_ATTRIBUTE_DEF -> AttributeDefinition`

Only the matching typed value field is populated. `normalizedValue` is populated for search, matching, and uniqueness checks.

#### Connection Instance

Connection instances remain graph edges for now:

`(source:EntityInstance)-[:CONNECTED_TO]->(target:EntityInstance)`

Required edge properties:

- `connectionDefinitionId`
- `connectionKey`
- `tenantId`
- `createdAt`
- `createdBy`

Optional edge properties:

- any connection-level properties defined by the referenced `ConnectionDefinition`

Connection instances should only be promoted to nodes if they need an independent lifecycle, evidence, approval, versioning, or commentary model.

---

## 6. Protection and Validation Rules

### 6.1 Protection Model

Protection exists at three levels:

- `platform lock`
  Protect default platform definitions and default structural semantics
- `referential integrity`
  Prevent destructive operations that would orphan live objects or invalidate live connections
- `schema validation`
  Validate every object, attribute, and connection write against the schema

### 6.2 Protection Rules

#### Platform Defaults

Platform defaults are locked.

- platform `EntityDefinition`
  Cannot be hard deleted
- platform `AttributeDefinition`
  Cannot be hard deleted
- platform `ConnectionDefinition`
  Cannot be hard deleted

Tenants may:

- add tenant-owned attributes to a platform definition
- add tenant-owned connections to a platform definition
- override allowed presentation metadata where policy permits

Tenants may not:

- delete platform defaults
- change platform identity keys
- change protected cardinality semantics
- silently diverge from platform definitions

#### Delete Policy

Soft delete is the default.

| Item | Default Behavior | Hard Delete Allowed When |
|------|------------------|--------------------------|
| Platform `EntityDefinition` | Soft delete only | Never |
| Tenant `EntityDefinition` | Soft delete | Draft, no instances, no references |
| `EntityInstance` | Soft delete | Draft, no connections |
| Platform `AttributeDefinition` | Soft delete only | Never |
| Tenant `AttributeDefinition` | Soft delete | Not used by any definition or instance |
| Platform `ConnectionDefinition` | Soft delete only | Never |
| Tenant `ConnectionDefinition` | Soft delete | No connection instances exist |

### 6.3 Validation on Every Write

#### Create Object

`definition-service` must check:

- target `EntityDefinition` exists
- tenant is allowed to use that definition
- required attributes are satisfied

#### Set Attribute

`definition-service` must check:

- `AttributeDefinition` is linked to the object's `EntityDefinition`
- data type matches
- required rules are satisfied
- allowed values are respected
- uniqueness rules are satisfied

#### Create Connection

`definition-service` must check:

- referenced `ConnectionDefinition` exists
- source and target instances match the canonical source and target definitions
- cardinality rules are respected
- minimum and maximum connection rules are not violated

#### Delete

`definition-service` must check:

- object or definition is not protected
- deletion will not violate referential integrity
- minimum connection rules are not broken

No AI component may bypass these checks.

---

## 7. Schema Overlay Model

### 7.1 Decision

**Decision:** `inherit-and-extend`

**Alternative rejected:** `copy-on-create`

**Fallback usage:** only for exceptional tenants requiring fully disconnected schema ownership

### 7.2 Platform Library

Platform defaults live in a global library tenant:

- `tenantId = PLATFORM`

This library contains all shared management definitions.

### 7.3 Tenant Effective Schema Resolution

For a tenant using inheritance:

1. start with platform defaults
2. apply tenant extensions
3. apply tenant overrides where policy allows
4. resolve effective schema

Overlay fields on `EntityDefinition`:

- `overlayMode = INHERIT | FORK`
- `extendsDefinitionId`
- `forkedFromDefinitionId`

Resolution precedence:

1. tenant override metadata where allowed
2. tenant-added definitions
3. platform defaults

### 7.4 Platform Upgrade Propagation

For `INHERIT` tenants:

- new platform definitions become visible automatically
- new platform attributes become visible automatically
- new platform connections become visible automatically
- allowed platform metadata updates propagate automatically

For `FORK` tenants:

- platform updates do not propagate automatically
- tenant owns the divergent definition

### 7.5 Fork Action

Forking must be explicit and audited.

Expected behavior:

- create tenant-owned copy
- set `overlayMode = FORK`
- populate `forkedFromDefinitionId`
- record an audit event
- stop future automatic propagation for that forked definition

---

## 8. Internal Agent Model

All agents run in `ai-service`.

All graph reads and writes are performed through `definition-service`.

### 8.1 Internal Agent Roles

- `Studio Orchestrator`
  Coordinates sessions, approvals, sequencing, and output selection
- `Architecture Agent`
  Creates or updates application, component, and realization structure
- `Requirements Agent`
  Generates stories, tasks, deliverables, and verification artifacts
- `Discovery Agent`
  Reconciles manual entry and repo scan results
- `Safety Gate Agent`
  Assesses whether a chunk is safe for coding-agent handoff
- `Dispatch and Reconciliation Agent`
  Builds handoff packs, dispatches them, ingests completion evidence, updates the graph

### 8.2 Autonomy Rules

Agents may autonomously:

- create draft objects
- create draft connections
- add tenant-defined attributes
- add tenant-defined connections
- enrich draft scope during analysis

Agents require approval before:

- creating new core application components
- changing capability realization structure
- replacing or retiring existing components
- releasing a blocked chunk to a coding agent

Locked platform definitions cannot be edited or deleted by agents.

---

## 9. A2A Protocol

The protocol is contract-first and transport-agnostic.

Possible transports:

- file-based
- API-based
- MCP-based
- graph-backed exchange

### 9.1 Canonical Lifecycle

1. `PrepareChunk`
2. `ImplementationPackIssued`
3. `Accepted` or `Rejected`
4. `ClarificationRequested`
5. `ProgressReported`
6. `CompletionReported`
7. `EvidenceValidated`
8. `Closed` or `Reopened`

### 9.2 Implementation Pack

`Implementation Pack` remains a computed projection, not a canonical stored object.

Minimum contents:

- pack id
- version
- project scope
- application scope
- component scope
- stories, tasks, and deliverables
- resolved code assets and repo paths
- commands and bootstrap steps
- constraints, conventions, and policies
- dependency list
- safety verdict
- required evidence

### 9.3 Audit Objects

The platform persists audit history through shared default definitions:

- `SafetyAssessment`
- `AgentExchange`

These objects provide governance history without turning the `Implementation Pack` itself into canonical stored truth.

---

## 10. Service Responsibilities

### 10.1 definition-service

`definition-service` is the authoritative graph engine.

Responsibilities:

- manage definitions
- manage instances
- enforce tenant isolation
- enforce overlay resolution
- enforce lock rules
- validate required attributes
- validate cardinality and connection legality
- enforce referential integrity

### 10.2 ai-service

`ai-service` is the reasoning and orchestration layer.

Responsibilities:

- requirements generation
- capability-to-solution reasoning
- gap interpretation
- repo scan interpretation
- completeness analysis
- readiness assessment
- safety gating
- chunk preparation
- coding-agent protocol orchestration
- evidence reconciliation

`ai-service` must never mutate the graph except through `definition-service`.

---

## 11. Migration Path

### Phase 1

Extend `definition-service` from schema registry to full schema-plus-instance graph engine.

Deliver:

- `EntityInstance`
- `AttributeValue`
- dynamic connection-instance validation

### Phase 2

Seed platform default management definitions into the `PLATFORM` library.

Deliver:

- shared management meta-model as data
- overlay resolution for tenants

### Phase 3

Migrate current static domain entities gradually to dynamic definitions and instances.

Deliver:

- one type family at a time
- compatibility shims where needed

### Phase 4

Remove static domain entity dependence from the implementation path.

Deliver:

- generic graph engine behavior
- domain knowledge held in data, not Java entity classes

### Phase 5

Enable tenant-defined core domain modeling as a normal EMSIST capability.

Deliver:

- tenant domain object creation
- tenant domain connection modeling
- Design Studio requirement generation over mixed shared and tenant-defined models

---

## 12. Immediate Implementation Priorities

Priority order:

1. strengthen `definition-service` protection semantics
2. add dynamic instance layer
3. implement overlay resolution
4. seed platform default management definitions
5. move agent reasoning to `ai-service` against the dynamic graph

---

## 13. Summary

The approved target architecture is:

- one EMSIST application
- one fully dynamic meta-model
- platform-shipped shared management definitions
- tenant-defined business-core extensions
- strict schema validation in `definition-service`
- reasoning and orchestration in `ai-service`
- explicit separation between enduring structure, realization, and delivery change

This design keeps the platform extensible for different organizations while preserving enough structural integrity for safe requirements generation and coding-agent execution.
