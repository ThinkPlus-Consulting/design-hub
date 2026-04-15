# D4 Engineering Entity Completion Implementation Plan

> **Status: CLOSED** — All 3 chunks committed (cfec0b7, b7692dc, 254a139). Final verified baseline: `43 @Node / 66 SDN @Relationship / 1 Cypher-only edge / 247 tests / 0 failures`.

**Goal:** Close the remaining engineering-entity gaps by adding 7 missing T1 nodes and wiring 10 new SDN `@Relationship` edges across 5 existing entities.

**Architecture:** This is a domain-surface + edge-wiring increment. Each new entity is a standard `@Node` with `@Id`, Lombok builder, and `Status` enum. Edges are `@Relationship` annotations on the owning entity. Seed coverage ensures every new edge family has at least one live example in Neo4j.

**Tech Stack:** Java 23, Spring Boot 3.4.1, Spring Data Neo4j, Lombok, JUnit 5, Mockito, Neo4jClient

**Implementation Baseline (pre-plan):** `36 @Node / 56 SDN @Relationship / 1 Cypher-only edge / 216 tests`

**Final Baseline (verified):** `43 @Node / 66 SDN @Relationship / 1 Cypher-only edge / 247 tests`

---

## Frozen Decisions

### In Scope

**New nodes (7)**
- `AcceptanceCriterion` (T1)
- `DataField` (T1)
- `Message` (T1)
- `ValidationRule` (T1)
- `RequestSchema` (T1)
- `ResponseSchema` (T1)
- `ErrorContract` (T1)

**New edges (10)**

| Source | Edge | Target |
|--------|------|--------|
| UserStory | HAS_CRITERION | AcceptanceCriterion |
| UserStory | GOVERNED_BY_RULE | Rule |
| DataEntity | HAS_FIELD | DataField |
| ApiContract | HAS_REQUEST | RequestSchema |
| ApiContract | HAS_RESPONSE | ResponseSchema |
| ApiContract | HAS_ERROR | ErrorContract |
| Screen | HAS_MESSAGE | Message |
| Screen | ENFORCES_VALIDATION | ValidationRule |
| Rule | HAS_VALIDATION_RULE | ValidationRule |
| TestCase | VERIFIES | Screen (via outgoing) |

### Out of Scope

- Removing deprecated string-ref fields (compatibility preserved)
- Frontend model updates (separate track)
- Exhaustive seed data (minimal: one example per edge family)
- New REST controllers or DTOs for the 7 new entities
- Process spine entities (D5)

### Hard Rules

1. Every new entity uses `Status status` (universal 10-value enum).
2. Every new entity uses `@Id private String <entityId>` with a pattern prefix.
3. Every new `@Relationship` must have at least one seeded example in `RegistryGraphMigrationService`.
4. Tests must cover: entity builder, relationship population, and seed query execution.
5. Run full Maven suite under Java 23 after each chunk.

---

## File Structure

### New Files (7 entities + 7 tests)

| File | Responsibility |
|------|----------------|
| `domain/AcceptanceCriterion.java` | T1 acceptance criterion node |
| `domain/DataField.java` | T1 data field node |
| `domain/Message.java` | T1 message node |
| `domain/ValidationRule.java` | T1 validation rule node |
| `domain/RequestSchema.java` | T1 request schema node |
| `domain/ResponseSchema.java` | T1 response schema node |
| `domain/ErrorContract.java` | T1 error contract node |
| `domain/AcceptanceCriterionTest.java` | Builder + relationship tests |
| `domain/DataFieldTest.java` | Builder tests |
| `domain/MessageTest.java` | Builder tests |
| `domain/ValidationRuleTest.java` | Builder tests |
| `domain/RequestSchemaTest.java` | Builder tests |
| `domain/ResponseSchemaTest.java` | Builder tests |
| `domain/ErrorContractTest.java` | Builder tests |

### Modified Files

| File | Change |
|------|--------|
| `domain/UserStory.java` | Add `HAS_CRITERION → AcceptanceCriterion`, `GOVERNED_BY_RULE → Rule` |
| `domain/DataEntity.java` | Add `HAS_FIELD → DataField` |
| `domain/ApiContract.java` | Add `HAS_REQUEST → RequestSchema`, `HAS_RESPONSE → ResponseSchema`, `HAS_ERROR → ErrorContract` |
| `domain/Screen.java` | Add `HAS_MESSAGE → Message`, `ENFORCES_VALIDATION → ValidationRule` |
| `domain/Rule.java` | Add `HAS_VALIDATION_RULE → ValidationRule` |
| `domain/TestCase.java` | Add `VERIFIES → Screen` |
| `service/RegistryGraphMigrationService.java` | Seed new entities + wire new edges |
| `test/.../service/RegistryGraphMigrationServiceTest.java` | Tests for new seed/edge queries |
| `test/.../domain/EngineeringEdgeTraversalTest.java` | Source-level traversal tests for all 10 edges |

---

## Chunk 1: AcceptanceCriterion + DataField + Message + Edges (Tasks 1–4)

### Task 1: Add `AcceptanceCriterion`

- [x] Create `AcceptanceCriterion.java`:
  - `@Id criterionId` (Pattern: `AC-{storyId}-{seq}`)
  - `description: String`
  - `givenWhenThen: String`
  - `status: Status`
- [x] Create `AcceptanceCriterionTest.java` covering builder/defaults.
- [x] Run focused tests.

### Task 2: Add `DataField`

- [x] Create `DataField.java`:
  - `@Id fieldId` (Pattern: `DF-{entityId}-{seq}`)
  - `name: String`
  - `dataType: String`
  - `required: boolean`
  - `constraints: String`
  - `status: Status`
- [x] Create `DataFieldTest.java` covering builder/defaults.
- [x] Run focused tests.

### Task 3: Add `Message`

- [x] Create `Message.java`:
  - `@Id messageId` (Pattern: `MSG-{module}-{seq}`)
  - `messageText: String`
  - `messageType: String` (INFO, WARNING, SUCCESS, VALIDATION)
  - `severity: String`
  - `status: Status`
- [x] Create `MessageTest.java` covering builder/defaults.
- [x] Run focused tests.

### Task 4: Wire Chunk 1 Edges

- [x] Modify `UserStory.java`:
  - Add `@Relationship(type = "HAS_CRITERION", direction = OUTGOING) List<AcceptanceCriterion> criteria`
- [x] Modify `DataEntity.java`:
  - Add `@Relationship(type = "HAS_FIELD", direction = OUTGOING) List<DataField> fields`
- [x] Modify `Screen.java`:
  - Add `@Relationship(type = "HAS_MESSAGE", direction = OUTGOING) List<Message> messages`
- [x] Create or extend `EngineeringEdgeTraversalTest.java` covering:
  - `UserStory → HAS_CRITERION → AcceptanceCriterion`
  - `DataEntity → HAS_FIELD → DataField`
  - `Screen → HAS_MESSAGE → Message`
- [x] Run full Maven suite under Java 23.
- [x] Commit:

```bash
cd <repo-root> && git add \
  backend/src/main/java/com/emsist/designhub/domain/AcceptanceCriterion.java \
  backend/src/main/java/com/emsist/designhub/domain/DataField.java \
  backend/src/main/java/com/emsist/designhub/domain/Message.java \
  backend/src/main/java/com/emsist/designhub/domain/UserStory.java \
  backend/src/main/java/com/emsist/designhub/domain/DataEntity.java \
  backend/src/main/java/com/emsist/designhub/domain/Screen.java \
  backend/src/test/java/com/emsist/designhub/domain/AcceptanceCriterionTest.java \
  backend/src/test/java/com/emsist/designhub/domain/DataFieldTest.java \
  backend/src/test/java/com/emsist/designhub/domain/MessageTest.java \
  backend/src/test/java/com/emsist/designhub/domain/EngineeringEdgeTraversalTest.java \
&& git commit -m "feat: add AcceptanceCriterion DataField Message entities with edges"
```

**Post-Chunk 1 target:** `39 @Node / 59 SDN @Relationship / ≥226 tests`

---

## Chunk 2: ValidationRule + API Schema Entities + Edges (Tasks 5–8)

### Task 5: Add `ValidationRule`

- [x] Create `ValidationRule.java`:
  - `@Id validationRuleId` (Pattern: `VR-{domain}-{seq}`)
  - `fieldPath: String`
  - `validationType: String` (REQUIRED, PATTERN, LENGTH, RANGE, CUSTOM)
  - `expression: String`
  - `errorMessage: String`
  - `status: Status`
- [x] Create `ValidationRuleTest.java`.

### Task 6: Add `RequestSchema`, `ResponseSchema`, `ErrorContract`

- [x] Create `RequestSchema.java`:
  - `@Id schemaId` (Pattern: `REQ-{contractId}-{seq}`)
  - `contentType: String`
  - `status: Status`
- [x] Create `ResponseSchema.java`:
  - `@Id schemaId` (Pattern: `RES-{contractId}-{seq}`)
  - `contentType: String`
  - `statusCode: int`
  - `status: Status`
- [x] Create `ErrorContract.java`:
  - `@Id errorContractId` (Pattern: `EC-{contractId}-{seq}`)
  - `httpStatus: int`
  - `errorCode: String`
  - `description: String`
  - `status: Status`
- [x] Create tests for all three.

### Task 7: Wire Chunk 2 Edges

- [x] Modify `ApiContract.java`:
  - Add `@Relationship(type = "HAS_REQUEST", direction = OUTGOING) List<RequestSchema> requestSchemas`
  - Add `@Relationship(type = "HAS_RESPONSE", direction = OUTGOING) List<ResponseSchema> responseSchemas`
  - Add `@Relationship(type = "HAS_ERROR", direction = OUTGOING) List<ErrorContract> errorContracts`
- [x] Modify `Screen.java`:
  - Add `@Relationship(type = "ENFORCES_VALIDATION", direction = OUTGOING) List<ValidationRule> validationRules`
- [x] Modify `Rule.java`:
  - Add `@Relationship(type = "HAS_VALIDATION_RULE", direction = OUTGOING) List<ValidationRule> validationRules`
  - Add `import` for `Relationship` and `List`
- [x] Extend `EngineeringEdgeTraversalTest.java` covering:
  - `ApiContract → HAS_REQUEST → RequestSchema`
  - `ApiContract → HAS_RESPONSE → ResponseSchema`
  - `ApiContract → HAS_ERROR → ErrorContract`
  - `Screen → ENFORCES_VALIDATION → ValidationRule`
  - `Rule → HAS_VALIDATION_RULE → ValidationRule`
- [x] Run full Maven suite under Java 23.
- [x] Commit:

```bash
cd <repo-root> && git add \
  backend/src/main/java/com/emsist/designhub/domain/ValidationRule.java \
  backend/src/main/java/com/emsist/designhub/domain/RequestSchema.java \
  backend/src/main/java/com/emsist/designhub/domain/ResponseSchema.java \
  backend/src/main/java/com/emsist/designhub/domain/ErrorContract.java \
  backend/src/main/java/com/emsist/designhub/domain/ApiContract.java \
  backend/src/main/java/com/emsist/designhub/domain/Screen.java \
  backend/src/main/java/com/emsist/designhub/domain/Rule.java \
  backend/src/test/java/com/emsist/designhub/domain/ValidationRuleTest.java \
  backend/src/test/java/com/emsist/designhub/domain/RequestSchemaTest.java \
  backend/src/test/java/com/emsist/designhub/domain/ResponseSchemaTest.java \
  backend/src/test/java/com/emsist/designhub/domain/ErrorContractTest.java \
  backend/src/test/java/com/emsist/designhub/domain/EngineeringEdgeTraversalTest.java \
&& git commit -m "feat: add ValidationRule RequestSchema ResponseSchema ErrorContract with edges"
```

**Post-Chunk 2 target:** `43 @Node / 64 SDN @Relationship / ≥236 tests`

---

## Chunk 3: TestCase VERIFIES + GOVERNED_BY_RULE + Seed + Verification (Tasks 8–10)

### Task 8: Wire Remaining Edges

- [x] Modify `TestCase.java`:
  - Add `@Relationship(type = "VERIFIES", direction = OUTGOING) List<Screen> verifiesScreens`
- [x] Modify `UserStory.java`:
  - Add `@Relationship(type = "GOVERNED_BY_RULE", direction = OUTGOING) List<Rule> governedByRules`
- [x] Extend `EngineeringEdgeTraversalTest.java` covering:
  - `TestCase → VERIFIES → Screen`
  - `UserStory → GOVERNED_BY_RULE → Rule`
- [x] Run full Maven suite.

### Task 9: Seed Coverage for All 10 Edge Families

- [x] Extend `RegistryGraphMigrationService.java` with seed methods:
  - `seedAcceptanceCriteria()` — at least 1 AC linked to an existing UserStory via HAS_CRITERION
  - `seedDataFields()` — at least 1 DataField linked to an existing DataEntity via HAS_FIELD
  - `seedMessages()` — at least 1 Message linked to an existing Screen via HAS_MESSAGE
  - `seedValidationRules()` — at least 1 ValidationRule linked via ENFORCES_VALIDATION and HAS_VALIDATION_RULE
  - `seedApiSchemas()` — at least 1 each of RequestSchema/ResponseSchema/ErrorContract linked to an existing ApiContract
  - `seedTestCaseVerifies()` — at least 1 TestCase → VERIFIES → Screen
  - `seedStoryRuleEdges()` — at least 1 UserStory → GOVERNED_BY_RULE → Rule
- [x] Update `runFullMigration()` to call new seed methods in deterministic order (after existing seeds).
- [x] Add tests for each new seed method in `RegistryGraphMigrationServiceTest.java`.
- [x] Run full Maven suite.

### Task 10: Final Verification

- [x] Run: `JAVA_HOME=/Library/Java/JavaVirtualMachines/zulu-23.jdk/Contents/Home mvn test -q`
- [x] Verify target baseline:
  - `43 @Node` — 36 + 7 new ✅
  - `66 SDN @Relationship` — 56 + 10 new ✅
  - `1 Cypher-only edge` — unchanged ✅
  - `247 tests, 0 failures` ✅
- [x] Commit final chunk:

```bash
cd <repo-root> && git add \
  backend/src/main/java/com/emsist/designhub/domain/TestCase.java \
  backend/src/main/java/com/emsist/designhub/domain/UserStory.java \
  backend/src/main/java/com/emsist/designhub/service/RegistryGraphMigrationService.java \
  backend/src/test/java/com/emsist/designhub/service/RegistryGraphMigrationServiceTest.java \
  backend/src/test/java/com/emsist/designhub/domain/EngineeringEdgeTraversalTest.java \
&& git commit -m "feat: wire TestCase VERIFIES and GOVERNED_BY_RULE with seed coverage"
```

**Post-Chunk 3 target:** `43 @Node / 66 SDN @Relationship / 1 Cypher-only edge / ≥240 tests`

---

## Expected Outcomes

After this plan lands, Design Hub gains:

- Full API contract decomposition: `ApiContract → RequestSchema / ResponseSchema / ErrorContract`
- Story verification chain: `UserStory → HAS_CRITERION → AcceptanceCriterion`, `TestCase → VERIFIES → Screen`
- Data entity structure: `DataEntity → HAS_FIELD → DataField`
- Rule enforcement: `Rule → HAS_VALIDATION_RULE → ValidationRule`, `Screen → ENFORCES_VALIDATION → ValidationRule`
- Message registry: `Screen → HAS_MESSAGE → Message`
- Story governance: `UserStory → GOVERNED_BY_RULE → Rule`

Every new edge family has at least one live seeded example, verifiable via the migration service tests.
