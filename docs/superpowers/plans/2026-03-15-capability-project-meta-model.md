# Capability / Project Meta-Model Implementation Plan

> **For agentic workers:** REQUIRED: Use superpowers:subagent-driven-development (if subagents available) or superpowers:executing-plans to implement this plan. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** Add 7 new T1 domain entities (Assessment, BusinessCapability, Epic, Feature, RequirementPortfolio, ProjectInstance, Milestone) plus TargetKind enum + Gap extension, establishing the "way of thinking" and "way of working" spines with 13 new edge types (12 via @Relationship + 1 Cypher-only ASSESSES).

**Architecture:** New @Node entities follow the existing Lombok + Spring Data Neo4j pattern (manual String @Id, Status enum, @Relationship for edges). The plan is split into 3 phases: (1) foundation entities with no cross-references, (2) container entities that reference phase 1, (3) wiring edges onto existing entities. Each phase produces independently testable, committable code.

**Tech Stack:** Java 21, Spring Boot 3.4.1, Spring Data Neo4j, Lombok, JUnit 5 + Mockito

**Baseline:** 75 nodes / 106 edge types / 71 benchmarkable / 97 tests

---

## Frozen Design Decisions

These decisions were agreed in the brainstorming session and are NOT open for reinterpretation by implementers.

### Canonical Spine

```
<assessable T1> <-[ASSESSES]- Assessment -[IDENTIFIES_GAP]-> Gap
ProjectInstance -[ADDRESSES_GAP]-> Gap
ProjectInstance -[TARGETS_CAPABILITY]-> BusinessCapability
ProjectInstance -[HAS_PORTFOLIO]-> RequirementPortfolio -[HAS_EPIC]-> Epic -[HAS_FEATURE]-> Feature -[HAS_STORY]-> UserStory
ProjectInstance -[HAS_TASK]-> Task                          (canonical ownership)
ProjectInstance -[HAS_MILESTONE]-> Milestone -[HAS_TASK]-> Task   (optional scheduling)
ProjectInstance -[CREATES_APPLICATION|ENHANCES_APPLICATION|INTEGRATES_WITH]-> Application -[HAS_COMPONENT]-> ApplicationComponent
ApplicationComponent -[HAS_CODE_ASSET]-> CodeAsset
Task -[IMPLEMENTS]-> ApplicationComponent | CodeAsset | Screen | ApiContract | DataEntity | Rule | Message | TestCase
```

### Ownership Boundaries

| Owner | Owns | Edge |
|-------|------|------|
| RequirementPortfolio | Epic → Feature → UserStory | HAS_EPIC → HAS_FEATURE → HAS_STORY |
| ProjectInstance | Task (canonical), Milestone (scheduling) | HAS_TASK, HAS_MILESTONE |
| Application | ApplicationComponent | HAS_COMPONENT |
| ApplicationComponent | CodeAsset | HAS_CODE_ASSET |

### Task Ownership — Dual Inbound

Every Task MUST have exactly one ProjectInstance via HAS_TASK. A Task MAY additionally have one Milestone via HAS_TASK. Unscheduled tasks (not yet assigned to a milestone) are valid.

### Story Membership — Portfolio Is Canonical

INCLUDES_STORY (ProjectInstance → UserStory) was dropped. Story membership is derived from: ProjectInstance → HAS_PORTFOLIO → RequirementPortfolio → HAS_EPIC → Epic → HAS_FEATURE → Feature → HAS_STORY → UserStory.

### Assessment Target Matrix

`assessmentType` = lens (what kind of evaluation). `targetKind` = actual node kind (enum, not string).

| assessmentType | Allowed targetKind values | Allowed ASSESSES targets |
|----------------|--------------------------|--------------------------|
| CAPABILITY | CAP | BusinessCapability |
| PROCESS | PROC, ACT | BusinessProcess, ProcessActivity |
| APPLICATION | APP | Application |
| COMPONENT | CMP | ApplicationComponent |
| SECURITY | APP, CMP, API | Application, ApplicationComponent, ApiContract |
| DATA | DE | DataEntity |

### CapabilityRealizationSlice — Deferred

Not implemented in this plan. Direct edges sufficient until proven otherwise.

### Metric Separation

| Metric | Value | Category |
|--------|-------|----------|
| Test count | Implementation deliverable | Verification |
| @Node / @Relationship | SDN code declarations | Implementation |
| 75 nodes / 106 edges | Approved target taxonomy | Design model |
| Benchmarkable | 71 | Design model |

---

## File Structure

### New Files (Create)

| File | Responsibility |
|------|---------------|
| `domain/TargetKind.java` | Enum: CAP, PROC, ACT, APP, CMP, API, DE |
| `domain/Assessment.java` | T1 @Node: assessment of any assessable T1 |
| `domain/BusinessCapability.java` | T1 @Node: enduring business capability |
| `domain/RequirementPortfolio.java` | T1 @Node: backlog container owning Epics |
| `domain/Epic.java` | T1 @Node: delivery grouping owning Features |
| `domain/Feature.java` | T1 @Node: feature grouping owning UserStories |
| `domain/ProjectInstance.java` | T1 @Node: delivery project container |
| `domain/Milestone.java` | T1 @Node: time-boxed scheduling container |
| `domain/AssessmentTest.java` (test) | Tests for Assessment + TargetKind |
| `domain/BusinessCapabilityTest.java` (test) | Tests for BusinessCapability |
| `domain/RequirementPortfolioTest.java` (test) | Tests for RequirementPortfolio + Epic + Feature |
| `domain/ProjectInstanceTest.java` (test) | Tests for ProjectInstance |
| `domain/MilestoneTest.java` (test) | Tests for Milestone |
| `domain/GapExtensionTest.java` (test) | Tests for Gap with new gapType values and inbound edges |
| `service/AssessmentService.java` | Cypher query-builder for ASSESSES polymorphic edge |
| `service/AssessmentServiceTest.java` (test) | Tests for AssessmentService query builders + resolvers |

### Modified Files

| File | Change |
|------|--------|
| `domain/Gap.java` | Add CAPABILITY_GAP, PROCESS_GAP to gapType documentation comment |

Note: UserStory.java, Task.java, and Application.java gain new inbound edges via outgoing @Relationship declarations on Feature, Milestone, and ProjectInstance respectively. SDN does not require incoming @Relationship annotations on target-side entities — the outgoing declarations are sufficient for persistence and query. No code changes to these files are needed.

### Unchanged Files (Read-Only Reference)

| File | Why Referenced |
|------|--------------|
| `domain/Status.java` | All new entities use this enum |
| `domain/ApplicationComponent.java` | CREATES_COMPONENT/ENHANCES_COMPONENT target |
| `domain/CodeAsset.java` | HAS_CODE_ASSET already exists |
| `domain/ApiContract.java` | ASSESSES target for SECURITY assessments |
| `domain/DataEntity.java` | ASSESSES target for DATA assessments |

All paths relative to `backend/src/main/java/com/emsist/designhub/` (source) and `backend/src/test/java/com/emsist/designhub/` (test).

---

## Chunk 1: Foundation Entities (Tasks 1–4)

Phase 1 entities have no cross-references to other new entities. Each can be built and tested independently.

### Task 1: Frozen Enums (TargetKind, AssessmentType, MaturityLevel, MilestoneType)

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/domain/TargetKind.java`
- Create: `backend/src/main/java/com/emsist/designhub/domain/AssessmentType.java`
- Create: `backend/src/main/java/com/emsist/designhub/domain/MaturityLevel.java`
- Create: `backend/src/main/java/com/emsist/designhub/domain/MilestoneType.java`

- [ ] **Step 1: Write all four enums**

```java
package com.emsist.designhub.domain;

public enum TargetKind {
    CAP,   // BusinessCapability
    PROC,  // BusinessProcess
    ACT,   // ProcessActivity
    APP,   // Application
    CMP,   // ApplicationComponent
    API,   // ApiContract
    DE     // DataEntity
}
```

```java
package com.emsist.designhub.domain;

public enum AssessmentType {
    CAPABILITY,
    PROCESS,
    APPLICATION,
    COMPONENT,
    SECURITY,
    DATA
}
```

```java
package com.emsist.designhub.domain;

public enum MaturityLevel {
    NONE,
    INITIAL,
    DEVELOPING,
    DEFINED,
    MANAGED,
    OPTIMIZING
}
```

```java
package com.emsist.designhub.domain;

public enum MilestoneType {
    SPRINT,
    PHASE,
    RELEASE_CUT,
    CHECKPOINT
}
```

- [ ] **Step 2: Run tests to verify no regressions**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 97 tests, 0 failures

- [ ] **Step 3: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/TargetKind.java backend/src/main/java/com/emsist/designhub/domain/AssessmentType.java backend/src/main/java/com/emsist/designhub/domain/MaturityLevel.java backend/src/main/java/com/emsist/designhub/domain/MilestoneType.java && git commit -m "feat: add frozen enums (TargetKind, AssessmentType, MaturityLevel, MilestoneType)"
```

---

### Task 2: Assessment Entity + Tests

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/domain/Assessment.java`
- Create: `backend/src/test/java/com/emsist/designhub/domain/AssessmentTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class AssessmentTest {

    @Test
    void shouldBuildAssessmentWithRequiredFields() {
        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-CAP-001")
                .name("Auth Capability Maturity")
                .assessmentType(AssessmentType.CAPABILITY)
                .targetKind(TargetKind.CAP)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("arch-agent")
                .status(Status.DEFINED)
                .build();

        assertEquals("ASSESS-CAP-001", assessment.getAssessmentId());
        assertEquals(AssessmentType.CAPABILITY, assessment.getAssessmentType());
        assertEquals(TargetKind.CAP, assessment.getTargetKind());
        assertEquals("arch-agent", assessment.getAssessor());
        assertEquals(Status.DEFINED, assessment.getStatus());
    }

    @Test
    void shouldBuildAssessmentWithOptionalFields() {
        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-APP-001")
                .name("Design Hub App Health")
                .assessmentType(AssessmentType.APPLICATION)
                .targetKind(TargetKind.APP)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("sa-agent")
                .maturityLevel(MaturityLevel.DEVELOPING)
                .currentStateDescription("11 entities, 14 edges")
                .targetStateDescription("75 nodes, 106 edges")
                .score(13)
                .status(Status.IN_REVIEW)
                .build();

        assertEquals(MaturityLevel.DEVELOPING, assessment.getMaturityLevel());
        assertEquals(13, assessment.getScore());
    }

    @Test
    void shouldSupportAllTargetKindValues() {
        assertEquals(7, TargetKind.values().length);
        assertNotNull(TargetKind.valueOf("CAP"));
        assertNotNull(TargetKind.valueOf("PROC"));
        assertNotNull(TargetKind.valueOf("ACT"));
        assertNotNull(TargetKind.valueOf("APP"));
        assertNotNull(TargetKind.valueOf("CMP"));
        assertNotNull(TargetKind.valueOf("API"));
        assertNotNull(TargetKind.valueOf("DE"));
    }

    @Test
    void shouldAttachGapsViaIdentifiesGap() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001")
                .gapType("CAPABILITY_GAP")
                .severity("HIGH")
                .description("No MFA capability")
                .status(Status.IDENTIFIED)
                .build();

        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-CAP-002")
                .name("Auth Gap Assessment")
                .assessmentType(AssessmentType.CAPABILITY)
                .targetKind(TargetKind.CAP)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("arch-agent")
                .status(Status.DEFINED)
                .identifiedGaps(List.of(gap))
                .build();

        assertEquals(1, assessment.getIdentifiedGaps().size());
        assertEquals("CAPABILITY_GAP", assessment.getIdentifiedGaps().get(0).getGapType());
    }

    @Test
    void shouldSupportSecurityAssessmentWithApiTarget() {
        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-API-001")
                .name("Auth API Security Review")
                .assessmentType(AssessmentType.SECURITY)
                .targetKind(TargetKind.API)
                .assessmentDate(LocalDate.of(2026, 3, 15))
                .assessor("sec-agent")
                .status(Status.IN_REVIEW)
                .build();

        assertEquals(AssessmentType.SECURITY, assessment.getAssessmentType());
        assertEquals(TargetKind.API, assessment.getTargetKind());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=AssessmentTest -pl . -q`
Expected: FAIL — `Assessment` class not found

- [ ] **Step 3: Write Assessment entity**

```java
package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Assessment {

    @Id
    private String assessmentId;      // Pattern: ASSESS-{targetKind}-{seq}

    private String name;
    private AssessmentType assessmentType;  // Frozen enum: CAPABILITY, PROCESS, APPLICATION, COMPONENT, SECURITY, DATA
    private TargetKind targetKind;          // Enum discriminator for query/indexing
    private LocalDate assessmentDate;
    private String assessor;                // Agent or person identifier
    private MaturityLevel maturityLevel;    // Frozen enum: NONE, INITIAL, DEVELOPING, DEFINED, MANAGED, OPTIMIZING
    private String currentStateDescription;
    private String targetStateDescription;
    private Integer score;            // 0-100 normalized
    private Status status;

    @Relationship(type = "IDENTIFIES_GAP", direction = Relationship.Direction.OUTGOING)
    private List<Gap> identifiedGaps;
}
```

Note: The ASSESSES edge (Assessment → assessable T1) is polymorphic in Neo4j. It is NOT modeled as a Java @Relationship because SDN requires a typed target. Instead, it is implemented in **Task 11** via `AssessmentService` (Cypher query-builders following the `ImpactAnalysisService` pattern). The `targetKind` enum + `assessmentId` pattern provide the discriminator for efficient queries.

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=AssessmentTest -pl . -q`
Expected: 5 tests, 0 failures

- [ ] **Step 5: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 102 tests, 0 failures

- [ ] **Step 6: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Assessment.java backend/src/test/java/com/emsist/designhub/domain/AssessmentTest.java && git commit -m "feat: add Assessment T1 entity with TargetKind discrimination and IDENTIFIES_GAP edge"
```

---

### Task 3: Gap Extension Tests

Gap.java already has the right shape (`gapType` is a String, so new values like `CAPABILITY_GAP` and `PROCESS_GAP` need no code change). This task adds tests proving those new values work and documenting the extension.

**Files:**
- Create: `backend/src/test/java/com/emsist/designhub/domain/GapExtensionTest.java`
- Modify: `backend/src/main/java/com/emsist/designhub/domain/Gap.java` (comment only)

- [ ] **Step 1: Write tests for extended gapType values**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class GapExtensionTest {

    @Test
    void shouldSupportCapabilityGapType() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001")
                .gapType("CAPABILITY_GAP")
                .severity("HIGH")
                .description("Authentication capability lacks MFA support")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("CAPABILITY_GAP", gap.getGapType());
        assertEquals("HIGH", gap.getSeverity());
    }

    @Test
    void shouldSupportProcessGapType() {
        Gap gap = Gap.builder()
                .gapId("GAP-PROC-ONBOARD-001")
                .gapType("PROCESS_GAP")
                .severity("MEDIUM")
                .description("Onboarding process has no automated verification step")
                .status(Status.IDENTIFIED)
                .build();

        assertEquals("PROCESS_GAP", gap.getGapType());
    }

    @Test
    void shouldRetainExistingGapTypes() {
        for (String gapType : new String[]{
                "MISSING_ARTIFACT", "MISSING_RELATIONSHIP",
                "MISSING_ATTRIBUTE", "MISSING_RULE",
                "CAPABILITY_GAP", "PROCESS_GAP"}) {
            Gap gap = Gap.builder()
                    .gapId("GAP-TEST-" + gapType)
                    .gapType(gapType)
                    .severity("LOW")
                    .status(Status.IDENTIFIED)
                    .build();
            assertEquals(gapType, gap.getGapType());
        }
    }
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=GapExtensionTest -pl . -q`
Expected: 3 tests, 0 failures (Gap.java already supports any string gapType)

- [ ] **Step 3: Update Gap.java comment to document extended values**

In `Gap.java`, update the `gapType` comment from:
```java
    private String gapType;     // MISSING_ARTIFACT, MISSING_RELATIONSHIP, MISSING_ATTRIBUTE, MISSING_RULE
```
to:
```java
    private String gapType;     // MISSING_ARTIFACT, MISSING_RELATIONSHIP, MISSING_ATTRIBUTE, MISSING_RULE, CAPABILITY_GAP, PROCESS_GAP
```

- [ ] **Step 4: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 105 tests, 0 failures

- [ ] **Step 5: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Gap.java backend/src/test/java/com/emsist/designhub/domain/GapExtensionTest.java && git commit -m "feat: extend Gap with CAPABILITY_GAP and PROCESS_GAP types + regression tests"
```

---

### Task 4: BusinessCapability Entity + Tests

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/domain/BusinessCapability.java`
- Create: `backend/src/test/java/com/emsist/designhub/domain/BusinessCapabilityTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BusinessCapabilityTest {

    @Test
    void shouldBuildCapabilityWithRequiredFields() {
        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-AUTH")
                .name("Authentication & Identity")
                .status(Status.DEFINED)
                .build();

        assertEquals("CAP-AUTH", cap.getCapabilityId());
        assertEquals("Authentication & Identity", cap.getName());
        assertEquals(Status.DEFINED, cap.getStatus());
    }

    @Test
    void shouldBuildCapabilityWithOptionalDescription() {
        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-GRAPH")
                .name("Graph Intelligence")
                .description("Ability to traverse and query the design graph for delivery insights")
                .status(Status.APPROVED)
                .build();

        assertEquals("Graph Intelligence", cap.getName());
        assertNotNull(cap.getDescription());
    }

    @Test
    void shouldFollowIdPattern() {
        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-DRIFT-DETECT")
                .name("Drift Detection")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(cap.getCapabilityId().startsWith("CAP-"),
                "capabilityId must follow pattern CAP-{code}");
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=BusinessCapabilityTest -pl . -q`
Expected: FAIL — `BusinessCapability` class not found

- [ ] **Step 3: Write BusinessCapability entity**

```java
package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BusinessCapability {

    @Id
    private String capabilityId;  // Pattern: CAP-{code}

    private String name;
    private String description;
    private Status status;
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=BusinessCapabilityTest -pl . -q`
Expected: 3 tests, 0 failures

- [ ] **Step 5: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 108 tests, 0 failures

- [ ] **Step 6: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/BusinessCapability.java backend/src/test/java/com/emsist/designhub/domain/BusinessCapabilityTest.java && git commit -m "feat: add BusinessCapability T1 entity"
```

---

## Chunk 2: Backlog Hierarchy (Tasks 5–7)

This phase builds the RequirementPortfolio → Epic → Feature ownership chain. Epic and Feature are leaf-first (no outbound edges to other new entities), then RequirementPortfolio wires to Epic.

### Task 5: Epic Entity + Tests

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/domain/Epic.java`
- Create: `backend/src/test/java/com/emsist/designhub/domain/EpicTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @Test
    void shouldBuildEpicWithRequiredFields() {
        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication & Authorization")
                .status(Status.APPROVED)
                .build();

        assertEquals("EPIC-AUTH-001", epic.getEpicId());
        assertEquals("Authentication & Authorization", epic.getTitle());
        assertEquals(Status.APPROVED, epic.getStatus());
    }

    @Test
    void shouldAttachFeaturesViaHasFeature() {
        Feature feat = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication & Authorization")
                .status(Status.APPROVED)
                .features(List.of(feat))
                .build();

        assertEquals(1, epic.getFeatures().size());
        assertEquals("FEAT-AUTH-001", epic.getFeatures().get(0).getFeatureId());
    }

    @Test
    void shouldFollowIdPattern() {
        Epic epic = Epic.builder()
                .epicId("EPIC-GRAPH-002")
                .title("Graph Intelligence")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(epic.getEpicId().startsWith("EPIC-"),
                "epicId must follow pattern EPIC-{module}-{seq}");
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=EpicTest -pl . -q`
Expected: FAIL — `Epic` class not found

- [ ] **Step 3: Write Feature entity first (Epic depends on it)**

Note: This is a pragmatic TDD deviation — Feature.java is created here because Epic's test references it. Feature gets its own dedicated test class in Task 6. The TDD cycle for Feature is: compile-time dependency (here) → full test coverage (Task 6).

```java
package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Feature {

    @Id
    private String featureId;     // Pattern: FEAT-{module}-{seq}

    private String title;
    private String description;
    private Status status;

    @Relationship(type = "HAS_STORY", direction = Relationship.Direction.OUTGOING)
    private List<UserStory> stories;
}
```

- [ ] **Step 4: Write Epic entity**

```java
package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Epic {

    @Id
    private String epicId;        // Pattern: EPIC-{module}-{seq}

    private String title;
    private String description;
    private Status status;

    @Relationship(type = "HAS_FEATURE", direction = Relationship.Direction.OUTGOING)
    private List<Feature> features;
}
```

- [ ] **Step 5: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=EpicTest -pl . -q`
Expected: 3 tests, 0 failures

- [ ] **Step 6: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 111 tests, 0 failures

- [ ] **Step 7: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Epic.java backend/src/main/java/com/emsist/designhub/domain/Feature.java backend/src/test/java/com/emsist/designhub/domain/EpicTest.java && git commit -m "feat: add Epic and Feature T1 entities with HAS_FEATURE and HAS_STORY edges"
```

---

### Task 6: Feature → UserStory Edge Test

Feature.java (from Task 5) declares `HAS_STORY → UserStory`. This task adds a focused test proving that edge works with the existing UserStory entity.

**Files:**
- Create: `backend/src/test/java/com/emsist/designhub/domain/FeatureTest.java`

- [ ] **Step 1: Write the test**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class FeatureTest {

    @Test
    void shouldBuildFeatureWithRequiredFields() {
        Feature feature = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals("FEAT-AUTH-001", feature.getFeatureId());
        assertEquals("Login Flow", feature.getTitle());
    }

    @Test
    void shouldAttachStoriesViaHasStory() {
        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001")
                .label("User can log in with email")
                .module("auth")
                .build();

        Feature feature = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.APPROVED)
                .stories(List.of(story))
                .build();

        assertEquals(1, feature.getStories().size());
        assertEquals("US-AUTH-001", feature.getStories().get(0).getStoryId());
    }

    @Test
    void shouldSupportFeatureWithoutStories() {
        Feature feature = Feature.builder()
                .featureId("FEAT-GRAPH-001")
                .title("Graph Traversal View")
                .status(Status.IDENTIFIED)
                .build();

        assertNull(feature.getStories());
    }
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=FeatureTest -pl . -q`
Expected: 3 tests, 0 failures

- [ ] **Step 3: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 114 tests, 0 failures

- [ ] **Step 4: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/test/java/com/emsist/designhub/domain/FeatureTest.java && git commit -m "test: add Feature entity tests including HAS_STORY edge to UserStory"
```

---

### Task 7: RequirementPortfolio Entity + Tests

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/domain/RequirementPortfolio.java`
- Create: `backend/src/test/java/com/emsist/designhub/domain/RequirementPortfolioTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class RequirementPortfolioTest {

    @Test
    void shouldBuildPortfolioWithRequiredFields() {
        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .build();

        assertEquals("RPORT-DH-001", portfolio.getPortfolioId());
        assertEquals("Design Hub Backlog", portfolio.getName());
        assertEquals(Status.APPROVED, portfolio.getStatus());
    }

    @Test
    void shouldAttachEpicsViaHasEpic() {
        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication")
                .status(Status.APPROVED)
                .build();

        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .epics(List.of(epic))
                .build();

        assertEquals(1, portfolio.getEpics().size());
        assertEquals("EPIC-AUTH-001", portfolio.getEpics().get(0).getEpicId());
    }

    @Test
    void shouldTraversePortfolioToStory() {
        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001")
                .label("Login with email")
                .module("auth")
                .build();

        Feature feat = Feature.builder()
                .featureId("FEAT-AUTH-001")
                .title("Login Flow")
                .status(Status.APPROVED)
                .stories(List.of(story))
                .build();

        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001")
                .title("Authentication")
                .status(Status.APPROVED)
                .features(List.of(feat))
                .build();

        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .epics(List.of(epic))
                .build();

        // Full traversal: Portfolio → Epic → Feature → Story
        String storyId = portfolio.getEpics().get(0)
                .getFeatures().get(0)
                .getStories().get(0)
                .getStoryId();
        assertEquals("US-AUTH-001", storyId);
    }

    @Test
    void shouldFollowIdPattern() {
        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-PLATFORM-001")
                .name("Platform Backlog")
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(portfolio.getPortfolioId().startsWith("RPORT-"),
                "portfolioId must follow pattern RPORT-{code}-{seq}");
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=RequirementPortfolioTest -pl . -q`
Expected: FAIL — `RequirementPortfolio` class not found

- [ ] **Step 3: Write RequirementPortfolio entity**

```java
package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequirementPortfolio {

    @Id
    private String portfolioId;   // Pattern: RPORT-{code}-{seq}

    private String name;
    private String description;
    private Status status;

    @Relationship(type = "HAS_EPIC", direction = Relationship.Direction.OUTGOING)
    private List<Epic> epics;
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=RequirementPortfolioTest -pl . -q`
Expected: 4 tests, 0 failures

- [ ] **Step 5: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 118 tests, 0 failures

- [ ] **Step 6: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/RequirementPortfolio.java backend/src/test/java/com/emsist/designhub/domain/RequirementPortfolioTest.java && git commit -m "feat: add RequirementPortfolio T1 entity with HAS_EPIC edge to Epic"
```

---

## Chunk 3: Delivery Containers (Tasks 8–10)

This phase builds Milestone, ProjectInstance, and wires edges onto existing entities (Application, Task).

### Task 8: Milestone Entity + Tests

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/domain/Milestone.java`
- Create: `backend/src/test/java/com/emsist/designhub/domain/MilestoneTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class MilestoneTest {

    @Test
    void shouldBuildMilestoneWithRequiredFields() {
        Milestone milestone = Milestone.builder()
                .milestoneId("MS-DH-001")
                .name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT)
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals("MS-DH-001", milestone.getMilestoneId());
        assertEquals("Sprint 1", milestone.getName());
        assertEquals(MilestoneType.SPRINT, milestone.getMilestoneType());
    }

    @Test
    void shouldSupportAllMilestoneTypes() {
        for (MilestoneType type : MilestoneType.values()) {
            Milestone ms = Milestone.builder()
                    .milestoneId("MS-TEST-" + type.name())
                    .name(type.name() + " milestone")
                    .milestoneType(type)
                    .status(Status.IDENTIFIED)
                    .build();
            assertEquals(type, ms.getMilestoneType());
        }
    }

    @Test
    void shouldAttachTasksViaHasTask() {
        Task task = Task.builder()
                .taskId("TSK-AUTH-001")
                .title("Implement login endpoint")
                .taskType("BACKEND")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        Milestone milestone = Milestone.builder()
                .milestoneId("MS-DH-001")
                .name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT)
                .status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task))
                .build();

        assertEquals(1, milestone.getTasks().size());
        assertEquals("TSK-AUTH-001", milestone.getTasks().get(0).getTaskId());
    }

    @Test
    void shouldSupportOptionalDates() {
        Milestone milestone = Milestone.builder()
                .milestoneId("MS-DH-002")
                .name("Phase 1 Release")
                .milestoneType(MilestoneType.RELEASE_CUT)
                .startDate(LocalDate.of(2026, 3, 15))
                .endDate(LocalDate.of(2026, 4, 15))
                .status(Status.APPROVED)
                .build();

        assertEquals(LocalDate.of(2026, 3, 15), milestone.getStartDate());
        assertEquals(LocalDate.of(2026, 4, 15), milestone.getEndDate());
    }

    @Test
    void shouldFollowIdPattern() {
        Milestone milestone = Milestone.builder()
                .milestoneId("MS-PLAT-003")
                .name("Checkpoint 3")
                .milestoneType(MilestoneType.CHECKPOINT)
                .status(Status.IDENTIFIED)
                .build();

        assertTrue(milestone.getMilestoneId().startsWith("MS-"),
                "milestoneId must follow pattern MS-{projectCode}-{seq}");
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=MilestoneTest -pl . -q`
Expected: FAIL — `Milestone` class not found

- [ ] **Step 3: Write Milestone entity**

```java
package com.emsist.designhub.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Milestone {

    @Id
    private String milestoneId;   // Pattern: MS-{projectCode}-{seq}

    private String name;
    private String description;
    private MilestoneType milestoneType; // Frozen enum: SPRINT, PHASE, RELEASE_CUT, CHECKPOINT
    private LocalDate startDate;
    private LocalDate endDate;
    private Status status;

    @Relationship(type = "HAS_TASK", direction = Relationship.Direction.OUTGOING)
    private List<Task> tasks;
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=MilestoneTest -pl . -q`
Expected: 5 tests, 0 failures

- [ ] **Step 5: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 123 tests, 0 failures

- [ ] **Step 6: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/Milestone.java backend/src/test/java/com/emsist/designhub/domain/MilestoneTest.java && git commit -m "feat: add Milestone T1 entity with HAS_TASK edge (optional scheduling)"
```

---

### Task 9: ProjectInstance Entity + Tests

ProjectInstance is the most connected new entity. It references: BusinessCapability, Gap, RequirementPortfolio, Task, Milestone, Application, ApplicationComponent (all from earlier tasks or existing entities).

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/domain/ProjectInstance.java`
- Create: `backend/src/test/java/com/emsist/designhub/domain/ProjectInstanceTest.java`

- [ ] **Step 1: Write the failing tests**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProjectInstanceTest {

    @Test
    void shouldBuildProjectWithRequiredFields() {
        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals("PROJ-DH-001", project.getProjectId());
        assertEquals("Design Hub v1", project.getName());
        assertEquals("ENHANCEMENT", project.getProjectType());
    }

    @Test
    void shouldSupportAllProjectTypes() {
        for (String type : new String[]{"GREENFIELD", "ENHANCEMENT", "MIGRATION", "INTEGRATION"}) {
            ProjectInstance proj = ProjectInstance.builder()
                    .projectId("PROJ-TEST-" + type)
                    .name(type + " project")
                    .projectType(type)
                    .status(Status.IDENTIFIED)
                    .build();
            assertEquals(type, proj.getProjectType());
        }
    }

    @Test
    void shouldTargetCapabilities() {
        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-AUTH")
                .name("Authentication")
                .status(Status.DEFINED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .targetCapabilities(List.of(cap))
                .build();

        assertEquals(1, project.getTargetCapabilities().size());
        assertEquals("CAP-AUTH", project.getTargetCapabilities().get(0).getCapabilityId());
    }

    @Test
    void shouldAddressGaps() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001")
                .gapType("CAPABILITY_GAP")
                .severity("HIGH")
                .status(Status.IDENTIFIED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .addressedGaps(List.of(gap))
                .build();

        assertEquals(1, project.getAddressedGaps().size());
        assertEquals("CAPABILITY_GAP", project.getAddressedGaps().get(0).getGapType());
    }

    @Test
    void shouldOwnPortfolio() {
        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001")
                .name("Design Hub Backlog")
                .status(Status.APPROVED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .portfolio(portfolio)
                .build();

        assertNotNull(project.getPortfolio());
        assertEquals("RPORT-DH-001", project.getPortfolio().getPortfolioId());
    }

    @Test
    void shouldOwnTasksCanonically() {
        Task task = Task.builder()
                .taskId("TSK-AUTH-001")
                .title("Implement login")
                .taskType("BACKEND")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task))
                .build();

        assertEquals(1, project.getTasks().size());
    }

    @Test
    void shouldOwnMilestones() {
        Milestone ms = Milestone.builder()
                .milestoneId("MS-DH-001")
                .name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT)
                .status(Status.IN_IMPLEMENTATION)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .milestones(List.of(ms))
                .build();

        assertEquals(1, project.getMilestones().size());
    }

    @Test
    void shouldLinkToApplicationsWithThreeEdgeTypes() {
        Application newApp = Application.builder()
                .applicationId("APP-NEW")
                .name("New Service")
                .status(Status.IDENTIFIED)
                .build();

        Application existingApp = Application.builder()
                .applicationId("APP-DH")
                .name("Design Hub")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        Application externalApp = Application.builder()
                .applicationId("APP-KEYCLOAK")
                .name("Keycloak")
                .status(Status.IMPLEMENTED)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .createdApplications(List.of(newApp))
                .enhancedApplications(List.of(existingApp))
                .integratedApplications(List.of(externalApp))
                .build();

        assertEquals(1, project.getCreatedApplications().size());
        assertEquals("APP-NEW", project.getCreatedApplications().get(0).getApplicationId());
        assertEquals(1, project.getEnhancedApplications().size());
        assertEquals("APP-DH", project.getEnhancedApplications().get(0).getApplicationId());
        assertEquals(1, project.getIntegratedApplications().size());
        assertEquals("APP-KEYCLOAK", project.getIntegratedApplications().get(0).getApplicationId());
    }

    @Test
    void shouldLinkToComponentsWithCreateAndEnhanceEdges() {
        ApplicationComponent newComp = ApplicationComponent.builder()
                .componentId("CMP-NEW-001")
                .name("New Backend Service")
                .status(Status.IDENTIFIED)
                .build();

        ApplicationComponent existingComp = ApplicationComponent.builder()
                .componentId("CMP-DH-BACKEND")
                .name("Design Hub Backend")
                .status(Status.IN_IMPLEMENTATION)
                .build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .status(Status.IN_IMPLEMENTATION)
                .createdComponents(List.of(newComp))
                .enhancedComponents(List.of(existingComp))
                .build();

        assertEquals(1, project.getCreatedComponents().size());
        assertEquals("CMP-NEW-001", project.getCreatedComponents().get(0).getComponentId());
        assertEquals(1, project.getEnhancedComponents().size());
        assertEquals("CMP-DH-BACKEND", project.getEnhancedComponents().get(0).getComponentId());
    }

    @Test
    void shouldSupportOptionalDates() {
        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001")
                .name("Design Hub v1")
                .projectType("ENHANCEMENT")
                .startDate(LocalDate.of(2026, 1, 1))
                .targetDate(LocalDate.of(2026, 6, 30))
                .status(Status.IN_IMPLEMENTATION)
                .build();

        assertEquals(LocalDate.of(2026, 1, 1), project.getStartDate());
        assertEquals(LocalDate.of(2026, 6, 30), project.getTargetDate());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=ProjectInstanceTest -pl . -q`
Expected: FAIL — `ProjectInstance` class not found

- [ ] **Step 3: Write ProjectInstance entity**

```java
package com.emsist.designhub.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.neo4j.core.schema.Id;
import org.springframework.data.neo4j.core.schema.Node;
import org.springframework.data.neo4j.core.schema.Relationship;

import java.time.LocalDate;
import java.util.List;

@Node
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProjectInstance {

    @Id
    private String projectId;     // Pattern: PROJ-{code}-{seq}

    private String name;
    private String description;
    private String projectType;   // GREENFIELD, ENHANCEMENT, MIGRATION, INTEGRATION
    private LocalDate startDate;
    private LocalDate targetDate;
    private Status status;

    @Relationship(type = "TARGETS_CAPABILITY", direction = Relationship.Direction.OUTGOING)
    private List<BusinessCapability> targetCapabilities;

    @Relationship(type = "ADDRESSES_GAP", direction = Relationship.Direction.OUTGOING)
    private List<Gap> addressedGaps;

    @Relationship(type = "HAS_PORTFOLIO", direction = Relationship.Direction.OUTGOING)
    private RequirementPortfolio portfolio;

    @Relationship(type = "HAS_TASK", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"implementsAssets"})
    private List<Task> tasks;

    @Relationship(type = "HAS_MILESTONE", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"tasks"})
    private List<Milestone> milestones;

    @Relationship(type = "CREATES_APPLICATION", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"components", "conventions", "policies"})
    private List<Application> createdApplications;

    @Relationship(type = "ENHANCES_APPLICATION", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"components", "conventions", "policies"})
    private List<Application> enhancedApplications;

    @Relationship(type = "INTEGRATES_WITH", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"components", "conventions", "policies"})
    private List<Application> integratedApplications;

    @Relationship(type = "CREATES_COMPONENT", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"codeAssets", "conventions", "qualityConstraints", "policies"})
    private List<ApplicationComponent> createdComponents;

    @Relationship(type = "ENHANCES_COMPONENT", direction = Relationship.Direction.OUTGOING)
    @JsonIgnoreProperties({"codeAssets", "conventions", "qualityConstraints", "policies"})
    private List<ApplicationComponent> enhancedComponents;
}
```

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=ProjectInstanceTest -pl . -q`
Expected: 10 tests, 0 failures

- [ ] **Step 5: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 133 tests, 0 failures

- [ ] **Step 6: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/domain/ProjectInstance.java backend/src/test/java/com/emsist/designhub/domain/ProjectInstanceTest.java && git commit -m "feat: add ProjectInstance T1 entity with all 10 outbound edges"
```

---

### Task 10: Spine Traversal Integration Test

This task adds a single test class that proves the full canonical spine can be built in-memory: ProjectInstance → Portfolio → Epic → Feature → UserStory, and ProjectInstance → Milestone → Task → CodeAsset.

**Files:**
- Create: `backend/src/test/java/com/emsist/designhub/domain/SpineTraversalTest.java`

- [ ] **Step 1: Write the integration test**

```java
package com.emsist.designhub.domain;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class SpineTraversalTest {

    @Test
    void shouldTraverseBacklogSpine() {
        // Portfolio → Epic → Feature → UserStory
        UserStory story = UserStory.builder()
                .storyId("US-AUTH-001").label("Login with email").module("auth").build();

        Feature feat = Feature.builder()
                .featureId("FEAT-AUTH-001").title("Login Flow")
                .status(Status.APPROVED).stories(List.of(story)).build();

        Epic epic = Epic.builder()
                .epicId("EPIC-AUTH-001").title("Authentication")
                .status(Status.APPROVED).features(List.of(feat)).build();

        RequirementPortfolio portfolio = RequirementPortfolio.builder()
                .portfolioId("RPORT-DH-001").name("Design Hub Backlog")
                .status(Status.APPROVED).epics(List.of(epic)).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .portfolio(portfolio).build();

        // Traverse: Project → Portfolio → Epic → Feature → Story
        String resolvedStoryId = project.getPortfolio()
                .getEpics().get(0)
                .getFeatures().get(0)
                .getStories().get(0)
                .getStoryId();
        assertEquals("US-AUTH-001", resolvedStoryId);
    }

    @Test
    void shouldTraverseExecutionSpine() {
        // Project → Task (canonical) + Milestone → Task (scheduling)
        CodeAsset asset = CodeAsset.builder()
                .codeAssetId("CA-CMP-BACKEND-001")
                .filePath("src/main/java/Screen.java")
                .assetType("SOURCE").status(Status.IMPLEMENTED).build();

        Task task = Task.builder()
                .taskId("TSK-AUTH-001").title("Implement login endpoint")
                .taskType("BACKEND").status(Status.IN_IMPLEMENTATION)
                .implementsAssets(List.of(asset)).build();

        Milestone sprint = Milestone.builder()
                .milestoneId("MS-DH-001").name("Sprint 1")
                .milestoneType(MilestoneType.SPRINT).status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task)).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .tasks(List.of(task)).milestones(List.of(sprint)).build();

        // Canonical: Project → Task → CodeAsset
        String assetId = project.getTasks().get(0)
                .getImplementsAssets().get(0).getCodeAssetId();
        assertEquals("CA-CMP-BACKEND-001", assetId);

        // Scheduling: Project → Milestone → Task
        String scheduledTaskId = project.getMilestones().get(0)
                .getTasks().get(0).getTaskId();
        assertEquals("TSK-AUTH-001", scheduledTaskId);
    }

    @Test
    void shouldTraverseAssessmentSpine() {
        Gap gap = Gap.builder()
                .gapId("GAP-CAP-AUTH-001").gapType("CAPABILITY_GAP")
                .severity("HIGH").status(Status.IDENTIFIED).build();

        Assessment assessment = Assessment.builder()
                .assessmentId("ASSESS-CAP-001").name("Auth Maturity")
                .assessmentType(AssessmentType.CAPABILITY).targetKind(TargetKind.CAP)
                .assessmentDate(LocalDate.of(2026, 3, 15)).assessor("arch-agent")
                .status(Status.DEFINED).identifiedGaps(List.of(gap)).build();

        BusinessCapability cap = BusinessCapability.builder()
                .capabilityId("CAP-AUTH").name("Authentication")
                .status(Status.DEFINED).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .targetCapabilities(List.of(cap))
                .addressedGaps(List.of(gap)).build();

        // Assessment → Gap ← Project (converge on the same gap)
        assertEquals("GAP-CAP-AUTH-001",
                assessment.getIdentifiedGaps().get(0).getGapId());
        assertEquals("GAP-CAP-AUTH-001",
                project.getAddressedGaps().get(0).getGapId());
        assertEquals("CAP-AUTH",
                project.getTargetCapabilities().get(0).getCapabilityId());
    }

    @Test
    void shouldSupportApplicationEdgeVariants() {
        Application ownedApp = Application.builder()
                .applicationId("APP-DH").name("Design Hub")
                .status(Status.IN_IMPLEMENTATION).build();

        Application externalApp = Application.builder()
                .applicationId("APP-KEYCLOAK").name("Keycloak")
                .status(Status.IMPLEMENTED).build();

        ProjectInstance project = ProjectInstance.builder()
                .projectId("PROJ-DH-001").name("Design Hub v1")
                .projectType("ENHANCEMENT").status(Status.IN_IMPLEMENTATION)
                .enhancedApplications(List.of(ownedApp))
                .integratedApplications(List.of(externalApp))
                .build();

        assertEquals("APP-DH", project.getEnhancedApplications().get(0).getApplicationId());
        assertEquals("APP-KEYCLOAK", project.getIntegratedApplications().get(0).getApplicationId());
    }
}
```

- [ ] **Step 2: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=SpineTraversalTest -pl . -q`
Expected: 4 tests, 0 failures

- [ ] **Step 3: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: 137 tests, 0 failures

- [ ] **Step 4: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/test/java/com/emsist/designhub/domain/SpineTraversalTest.java && git commit -m "test: add spine traversal integration tests (backlog, execution, assessment, application)"
```

---

## Chunk 4: ASSESSES Edge + Final Verification (Tasks 11–12)

### Task 11: AssessmentService — ASSESSES Polymorphic Edge (Cypher-Only)

The ASSESSES edge (Assessment → assessable T1) is polymorphic — the target node type varies by `targetKind`. SDN's `@Relationship` requires a fixed target type, so this edge is created and queried via Cypher using `Neo4jClient`, following the established `ReconciliationService` pattern (Mockito with `RETURNS_DEEP_STUBS` for chained Neo4jClient calls).

**Files:**
- Create: `backend/src/main/java/com/emsist/designhub/service/AssessmentService.java`
- Create: `backend/src/test/java/com/emsist/designhub/service/AssessmentServiceTest.java`
- Reference: `backend/src/test/java/com/emsist/designhub/service/ReconciliationServiceTest.java` (mock pattern)
- Reference: `backend/src/main/java/com/emsist/designhub/service/RoleService.java` (Neo4jClient fetch pattern)

- [ ] **Step 1: Write the failing tests**

```java
package com.emsist.designhub.service;

import com.emsist.designhub.domain.TargetKind;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.neo4j.core.Neo4jClient;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AssessmentServiceTest {

    @Mock private Neo4jClient neo4jClient;

    @InjectMocks
    private AssessmentService service;

    // --- Resolver tests (pure logic, no Neo4j) ---

    @Test
    void shouldResolveTargetLabel() {
        assertEquals("BusinessCapability", service.resolveTargetLabel(TargetKind.CAP));
        assertEquals("BusinessProcess", service.resolveTargetLabel(TargetKind.PROC));
        assertEquals("ProcessActivity", service.resolveTargetLabel(TargetKind.ACT));
        assertEquals("Application", service.resolveTargetLabel(TargetKind.APP));
        assertEquals("ApplicationComponent", service.resolveTargetLabel(TargetKind.CMP));
        assertEquals("ApiContract", service.resolveTargetLabel(TargetKind.API));
        assertEquals("DataEntity", service.resolveTargetLabel(TargetKind.DE));
    }

    @Test
    void shouldResolveTargetIdField() {
        assertEquals("capabilityId", service.resolveTargetIdField(TargetKind.CAP));
        assertEquals("processId", service.resolveTargetIdField(TargetKind.PROC));
        assertEquals("activityId", service.resolveTargetIdField(TargetKind.ACT));
        assertEquals("applicationId", service.resolveTargetIdField(TargetKind.APP));
        assertEquals("componentId", service.resolveTargetIdField(TargetKind.CMP));
        assertEquals("contractId", service.resolveTargetIdField(TargetKind.API));
        assertEquals("entityId", service.resolveTargetIdField(TargetKind.DE));
    }

    // --- createAssessesEdge (executes Neo4jClient) ---

    @Test
    @SuppressWarnings("unchecked")
    void shouldCreateAssessesEdgeViaNeo4jClient() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.run()).thenReturn(null);

        service.createAssessesEdge("ASSESS-CAP-001", TargetKind.CAP, "CAP-AUTH");

        // Verify Neo4jClient was called with a query containing ASSESSES
        verify(neo4jClient).query(argThat(cypher ->
                cypher.contains("MERGE (a)-[:ASSESSES]->(target)")
                && cypher.contains("Assessment")));
        // Verify all 3 bind calls: assessmentId, targetIdField, targetId
        verify(runnableSpec, atLeast(3)).bind(any());
        verify(runnableSpec).run();
    }

    // --- findAssessmentsForTarget (executes Neo4jClient) ---

    @Test
    @SuppressWarnings("unchecked")
    void shouldFindAssessmentsForTargetViaNeo4jClient() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((Collection) List.of(
                Map.of("assessmentId", "ASSESS-CAP-001", "name", "Auth Maturity")));

        var results = service.findAssessmentsForTarget(TargetKind.CAP, "CAP-AUTH");

        assertEquals(1, results.size());
        assertEquals("ASSESS-CAP-001", results.get(0).get("assessmentId"));
        verify(neo4jClient).query(argThat(cypher ->
                cypher.contains("ASSESSES") && cypher.contains("RETURN")));
    }

    // --- findAssessmentsForTarget returns empty when no edges exist ---

    @Test
    @SuppressWarnings("unchecked")
    void shouldReturnEmptyWhenNoAssessmentsExist() {
        var runnableSpec = mock(Neo4jClient.UnboundRunnableSpec.class, RETURNS_DEEP_STUBS);
        when(neo4jClient.query(anyString())).thenReturn(runnableSpec);
        when(runnableSpec.bind(any()).to(anyString())).thenReturn(runnableSpec);
        var fetchSpec = mock(Neo4jClient.RecordFetchSpec.class);
        when(runnableSpec.fetch()).thenReturn(fetchSpec);
        when(fetchSpec.all()).thenReturn((Collection) List.of());

        var results = service.findAssessmentsForTarget(TargetKind.APP, "APP-DH");

        assertTrue(results.isEmpty());
    }
}
```

- [ ] **Step 2: Run tests to verify they fail**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=AssessmentServiceTest -pl . -q`
Expected: FAIL — `AssessmentService` class not found

- [ ] **Step 3: Write AssessmentService**

```java
package com.emsist.designhub.service;

import com.emsist.designhub.domain.TargetKind;
import lombok.RequiredArgsConstructor;
import org.springframework.data.neo4j.core.Neo4jClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AssessmentService {

    private final Neo4jClient neo4jClient;

    /**
     * Creates an ASSESSES edge from an Assessment to its polymorphic target.
     * The target node label is resolved from targetKind via the label/idField resolvers.
     */
    @Transactional
    public void createAssessesEdge(String assessmentId, TargetKind targetKind, String targetId) {
        String targetLabel = resolveTargetLabel(targetKind);
        String targetIdField = resolveTargetIdField(targetKind);

        neo4jClient.query("""
                    MATCH (a:Assessment {assessmentId: $assessmentId})
                    MATCH (target:%s {%s: $targetId})
                    MERGE (a)-[:ASSESSES]->(target)
                    """.formatted(targetLabel, targetIdField))
                .bind(assessmentId).to("assessmentId")
                .bind(targetId).to("targetId")
                .run();
    }

    /**
     * Finds all assessments targeting a specific node, regardless of assessment type.
     * Returns raw result maps with assessment properties.
     */
    @Transactional(readOnly = true)
    public List<Map<String, Object>> findAssessmentsForTarget(TargetKind targetKind, String targetId) {
        String targetLabel = resolveTargetLabel(targetKind);
        String targetIdField = resolveTargetIdField(targetKind);

        Collection<Map<String, Object>> results = neo4jClient.query("""
                    MATCH (a:Assessment)-[:ASSESSES]->(target:%s {%s: $targetId})
                    RETURN a.assessmentId AS assessmentId, a.name AS name,
                           a.assessmentType AS assessmentType, a.targetKind AS targetKind,
                           a.maturityLevel AS maturityLevel, a.score AS score,
                           a.status AS status
                    """.formatted(targetLabel, targetIdField))
                .bind(targetId).to("targetId")
                .fetch().all();

        return List.copyOf(results);
    }

    /** Resolves TargetKind enum to Neo4j node label. */
    public String resolveTargetLabel(TargetKind kind) {
        return switch (kind) {
            case CAP  -> "BusinessCapability";
            case PROC -> "BusinessProcess";
            case ACT  -> "ProcessActivity";
            case APP  -> "Application";
            case CMP  -> "ApplicationComponent";
            case API  -> "ApiContract";
            case DE   -> "DataEntity";
        };
    }

    /** Resolves TargetKind enum to the @Id field name on the target node. */
    public String resolveTargetIdField(TargetKind kind) {
        return switch (kind) {
            case CAP  -> "capabilityId";
            case PROC -> "processId";
            case ACT  -> "activityId";
            case APP  -> "applicationId";
            case CMP  -> "componentId";
            case API  -> "contractId";
            case DE   -> "entityId";
        };
    }
}
```

Note: The `targetLabel` and `targetIdField` are injected into the Cypher via `String.formatted()` (not as parameters) because Neo4j does not support parameterized labels or property keys. This is safe because both values come from a closed switch on the `TargetKind` enum — no user input reaches the query string.

- [ ] **Step 4: Run tests to verify they pass**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -Dtest=AssessmentServiceTest -pl . -q`
Expected: 5 tests, 0 failures

- [ ] **Step 5: Run full suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: ≥142 tests, 0 failures

- [ ] **Step 6: Commit**

```bash
cd /Users/mksulty/Claude/Projects/design-hub && git add backend/src/main/java/com/emsist/designhub/service/AssessmentService.java backend/src/test/java/com/emsist/designhub/service/AssessmentServiceTest.java && git commit -m "feat: add AssessmentService with ASSESSES polymorphic edge via Neo4jClient"
```

---

### Task 12: Full Suite Verification + Metric Checkpoint

This is a verification-only task. No new code. Confirms the plan delivered what was promised.

- [ ] **Step 1: Run full test suite**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -pl . -q`
Expected: ≥142 tests, 0 failures

- [ ] **Step 2: Count @Node entities**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && grep -rl "@Node" src/main/java/com/emsist/designhub/domain/ | wc -l`
Expected: 31 (24 existing + 7 new: Assessment, BusinessCapability, Epic, Feature, RequirementPortfolio, ProjectInstance, Milestone. TargetKind is an enum, not @Node.)

- [ ] **Step 3: Count @Relationship edges**

Run: `cd /Users/mksulty/Claude/Projects/design-hub/backend && grep -c '@Relationship' src/main/java/com/emsist/designhub/domain/*.java | grep -v ':0$'`
Expected: ≥42 (was 31 + 11 new edges declared in Java: IDENTIFIES_GAP, HAS_FEATURE, HAS_STORY, HAS_EPIC, HAS_TASK(Milestone), TARGETS_CAPABILITY, ADDRESSES_GAP, HAS_PORTFOLIO, HAS_TASK(Project), HAS_MILESTONE, CREATES_APPLICATION, ENHANCES_APPLICATION, INTEGRATES_WITH, CREATES_COMPONENT, ENHANCES_COMPONENT = 15 new. But ASSESSES is Cypher-only = not counted. Total: 31 + 14 = 45)

Count by entity: Assessment(1) + Epic(1) + Feature(1) + RequirementPortfolio(1) + Milestone(1) + ProjectInstance(10) = 15 new @Relationship declarations. Total: 31 + 15 = 46.

Note: ASSESSES is the 16th new edge type in the taxonomy but is Cypher-only (polymorphic target), not a Java @Relationship. It is implemented in Task 11 via AssessmentService query-builders. SDN @Relationship declarations: 46. Taxonomy edge count: 106.

- [ ] **Step 4: Verify metric separation**

Record and confirm:

| Metric | Expected | Category |
|--------|----------|----------|
| Tests | ≥142 | Verification (test suite size) |
| @Node entities | 31 | Implementation (SDN declarations) |
| @Relationship edges | 46 | Implementation (SDN declarations) |
| Cypher-only edges | 1 (ASSESSES) | Implementation (AssessmentService) |
| Target taxonomy nodes | 75 | Design model (approved) |
| Target taxonomy edges | 106 | Design model (approved) |
| Benchmarkable | 71 | Design model (approved) |

---

## Summary

| Task | Entity/Artifact | New @Node | New @Relationship | New Tests |
|------|----------------|-----------|-------------------|-----------|
| 1 | Frozen enums (TargetKind, AssessmentType, MaturityLevel, MilestoneType) | 0 | 0 | 0 |
| 2 | Assessment | 1 | 1 (IDENTIFIES_GAP) | 5 |
| 3 | Gap extension tests | 0 | 0 | 3 |
| 4 | BusinessCapability | 1 | 0 | 3 |
| 5 | Epic + Feature | 2 | 2 (HAS_FEATURE, HAS_STORY) | 3 |
| 6 | Feature tests | 0 | 0 | 3 |
| 7 | RequirementPortfolio | 1 | 1 (HAS_EPIC) | 4 |
| 8 | Milestone | 1 | 1 (HAS_TASK) | 5 |
| 9 | ProjectInstance | 1 | 10 (TARGETS_CAPABILITY, ADDRESSES_GAP, HAS_PORTFOLIO, HAS_TASK, HAS_MILESTONE, CREATES_APPLICATION, ENHANCES_APPLICATION, INTEGRATES_WITH, CREATES_COMPONENT, ENHANCES_COMPONENT) | 10 |
| 10 | Spine traversal tests | 0 | 0 | 4 |
| 11 | AssessmentService (ASSESSES Cypher edge) | 0 | 0 (+1 Cypher-only) | 5 |
| 12 | Verification | 0 | 0 | 0 |
| **Total** | | **7** | **15 + 1 Cypher** | **45** |

**Post-plan baseline**: 31 @Node / 46 @Relationship + 1 Cypher-only / ~142 tests / 0 failures
