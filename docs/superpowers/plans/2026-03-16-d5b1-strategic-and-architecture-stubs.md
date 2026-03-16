# D5b1 Strategic and Architecture Stub Completion Plan

> **Status: CLOSED** — Implementation committed as `c2e2b12`. Final verified baseline: `61 @Node / 78 SDN @Relationship / 1 Cypher-only edge / 307 tests / 0 failures`.

**Goal:** Add 13 attribute-only stub nodes with canonical IDs, one primary descriptive field, and universal `Status status`, without introducing new edges.

**Scope**
- Strategic & Governance (8): `BusinessObjective`, `Decision`, `Assumption`, `Constraint`, `SourceReference`, `Finding`, `Bug`, `Risk`
- Architecture & EA (5): `Organization`, `BusinessObject`, `InformationFlow`, `Deployment`, `InfrastructureNode`

**Out of Scope**
- Remaining D5b entities and registries
- Any new `@Relationship` annotations
- Repositories, services, DTOs, controllers, or seed data
- Full attribute depth beyond stub fields

**Implementation Baseline (pre-plan):** `48 @Node / 78 SDN @Relationship / 1 Cypher-only edge / 281 tests`

**Target Baseline (post-plan):** `61 @Node / 78 SDN @Relationship / 1 Cypher-only edge / >=307 tests`

**Actual Final Baseline:** `61 @Node / 78 SDN @Relationship / 1 Cypher-only edge / 307 tests / 0 failures`

---

## Chunk 1: Strategic & Governance Stubs

- [x] `BusinessObjective.java` + `BusinessObjectiveTest.java`
- [x] `Decision.java` + `DecisionTest.java`
- [x] `Assumption.java` + `AssumptionTest.java`
- [x] `Constraint.java` + `ConstraintTest.java`
- [x] `SourceReference.java` + `SourceReferenceTest.java`
- [x] `Finding.java` + `FindingTest.java`
- [x] `Bug.java` + `BugTest.java`
- [x] `Risk.java` + `RiskTest.java`

## Chunk 2: Architecture & EA Stubs

- [x] `Organization.java` + `OrganizationTest.java`
- [x] `BusinessObject.java` + `BusinessObjectTest.java`
- [x] `InformationFlow.java` + `InformationFlowTest.java`
- [x] `Deployment.java` + `DeploymentTest.java`
- [x] `InfrastructureNode.java` + `InfrastructureNodeTest.java`

## Chunk 3: Verification and Closeout

- [x] Run focused builder tests under Java 23
- [x] Run full Maven suite under Java 23
- [x] Record actual baseline and close the plan
- [x] Commit D5b1 implementation
