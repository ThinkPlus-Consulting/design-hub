# Design Hub

Design Hub is a graph-backed product, architecture, and delivery intelligence application built with Spring Boot, Neo4j, and Angular.

## Current State

- Backend graph model: substantial and actively implemented
- Frontend shell: implemented and buildable
- Seed data: enabled by default on backend startup
- Delivery, automation, traceability, benchmark, channel, journey/persona, and business/application/data-architecture UI surfaces: implemented
- Verification view, expanded Design Hub token audit, desktop/mobile detail baselines, graph-to-UI drift checks, and a GitHub Actions verification workflow: implemented
- Localization and RTL coverage now includes non-shell graph-backed detail behavior plus Arabic desktop/mobile detail baselines; GitHub Actions verification is enforced on protected `main`

## Prerequisites

- Java 23 JDK
- Node.js 25.8.1
- Docker Desktop or another Docker runtime

## Quick Start

1. Start Neo4j:

```bash
docker compose up -d neo4j
```

The local Neo4j container is tuned for a smaller Docker Desktop footprint via `docker-compose.yml` (`256M` heap, `128M` page cache) so the seeded graph and verification flows stay stable on constrained local environments.

2. Start the backend on port `8091`:

```bash
cd backend
./mvnw spring-boot:run
```

Make sure `JAVA_HOME` points to a Java 23 **JDK**, not a JRE, before running the wrapper.

3. Start the frontend on port `4300`:

```bash
cd frontend
./npmw install
./npmw start
```

4. Open the application:

- Frontend: `http://localhost:4300`
- Backend health: `http://localhost:8091/actuator/health`
- Swagger UI: `http://localhost:8091/swagger-ui.html`
- Neo4j Browser: `http://localhost:7474`

For a repo-level startup proof that bootstraps Neo4j, starts the backend and frontend if needed, and probes the live endpoints, run:

```bash
./scripts/verify-startup.sh
```

If `frontend/node_modules` is missing, the script will install dependencies through `./npmw ci` before starting the frontend. Add `--keep-running` if you want the script to leave the backend and frontend running after the checks pass.
This startup proof also passed from a clean isolated temp copy on this machine with no prior frontend install state.

## Runtime Defaults

The backend reads Neo4j settings from [`backend/src/main/resources/application.yml`](/Users/mksulty/Claude/Projects/design-hub/backend/src/main/resources/application.yml):

- `NEO4J_URI=bolt://localhost:7687`
- `NEO4J_USER=neo4j`
- `NEO4J_PASSWORD=password`
- `NEO4J_DATABASE=neo4j`

Seed data is enabled by default through `designhub.seed-data=true`.

External sync orchestration is also configured in `application.yml` under `designhub.external-sync`:

- webhook and polling modes are both modeled per source system
- polling jobs are persisted in Neo4j as `ExternalSyncJob` nodes
- `POST /api/v1/external-sync/jobs/poll/{sourceSystem}` records a poll job immediately
- `GET /api/v1/external-sync/sources` exposes effective source configuration plus the latest persisted job summary, including `requestedBy`
- `GET /api/v1/external-sync/jobs?limit=` exposes recent persisted sync history for operational review, and `sourceSystem=` narrows that history to Jira or Azure DevOps
- the scheduler only auto-polls sources that have both polling enabled and a configured `baseUrl + pollPath`
- Verification View now exposes live source status, recent sync-job history, and a manual poll action using the current `application.yml` source config
- upper-spine external normalization now reaches epic coverage, and the live external epic slice traverses to both the internal epic and feature it represents

Use [`.env.external-sync.example`](/Users/mksulty/Claude/Projects/design-hub/.env.external-sync.example) as the starting point for live source rollout. The current known Jira scope is already wired there from the provided board URL:

- `DESIGNHUB_EXTERNAL_SYNC_JIRA_BASE_URL=https://thinkplus.atlassian.net`
- `DESIGNHUB_EXTERNAL_SYNC_JIRA_PROJECT_KEY=DPAA`
- `DESIGNHUB_EXTERNAL_SYNC_JIRA_ACCOUNT_EMAIL=info@thinkplus.ae`

Jira now supports direct Jira Cloud polling when `base-url`, `account email`, `token`, and `poll-path` are set. The checked-in default is:

- `DESIGNHUB_EXTERNAL_SYNC_JIRA_POLL_PATH=/rest/api/3/search/jql`

The fastest rollout path is:

```bash
./scripts/check-external-sync.sh --env-file .env.external-sync.local --source jira
./scripts/check-external-sync.sh --env-file .env.external-sync.local --source jira --probe-jira
```

The first command validates the effective env, and the second issues a real authenticated GET against the configured Jira poll endpoint before you bring the backend up.

Azure DevOps is still different. The current Azure polling client still expects an adapter-style `poll-path` that accepts `organization/project` plus `wiql` and optional `updatedSince`, so a plain Azure Portal home URL is not a complete polling configuration by itself.

## Blocker Helpers

- Branch protection is active on `main`; use [`scripts/configure-branch-protection.sh`](/Users/mksulty/Claude/Projects/design-hub/scripts/configure-branch-protection.sh) if the required checks or review policy need to change.
- External-sync rollout can start from [`.env.external-sync.example`](/Users/mksulty/Claude/Projects/design-hub/.env.external-sync.example) and [`scripts/check-external-sync.sh`](/Users/mksulty/Claude/Projects/design-hub/scripts/check-external-sync.sh).
- The current public GitHub repo is `https://github.com/ThinkPlus-Consulting/design-hub`.

## Verification

Backend:

```bash
cd backend
./mvnw test
```

Current backend verification baseline: `491` passing tests.

Current live benchmark baseline: `98.6` overall across the full `71` benchmarked node types and `664` live nodes, with all four dimensions green and `sourceTraceability` currently at `94.4` for the six-story seeded slice.

Frontend:

```bash
cd frontend
./npmw run build
```

Combined frontend verification:

```bash
cd frontend
./npmw run verify:ui
```

Current frontend verification baseline: `44` Playwright scenarios passing.

The frontend shell now renders core graph data first and defers heavier benchmark/external summary requests so the screen list no longer blocks on the slowest optional summaries during local startup.

Playwright:

```bash
cd frontend
./npmw run test:e2e
```

Playwright expects the backend to already be running on `http://localhost:8091`.

The repository now includes [`.github/workflows/verification.yml`](/Users/mksulty/Claude/Projects/design-hub/.github/workflows/verification.yml) for pull requests and pushes to `main`, plus [`.github/workflows/release-verification.yml`](/Users/mksulty/Claude/Projects/design-hub/.github/workflows/release-verification.yml) for tag- and manually-triggered release readiness checks with packaged artifacts.
Both lanes now also enforce the repo benchmark gate through [`scripts/check-benchmark.mjs`](/Users/mksulty/Claude/Projects/design-hub/scripts/check-benchmark.mjs) and upload the live benchmark snapshot as a workflow artifact.

Backend wrapper:

```bash
cd backend
./mvnw -B -q -DskipTests compile
```

The backend Maven wrapper is now checked in and verified.

## Frontend Toolchain Note

The frontend is pinned to Node `25.8.1` via the repo [`.nvmrc`](/Users/mksulty/Claude/Projects/design-hub/.nvmrc), [frontend `.nvmrc`](/Users/mksulty/Claude/Projects/design-hub/frontend/.nvmrc), and `frontend/package.json`. Use [`frontend/npmw`](/Users/mksulty/Claude/Projects/design-hub/frontend/npmw) for install/build/test/start commands when your current shell has not switched to Node `25.8.1`; it prefers the matching Homebrew or `nvm` runtime and fails fast otherwise. The Angular workspace currently uses the Webpack-based `@angular-devkit/build-angular:browser` build target for both `ng build` and `ng serve`. Fresh `ng build`, `ng build --configuration development`, and `ng serve` run on this repo under Node `25.8.1`; if a transient PrimeUIX export error recurs, treat it as dependency/cache state rather than a separate production-vs-dev builder path and clear `node_modules/.cache` before restarting the dev server.

## Closeout Roadmap

The next-stage closure plan for the remaining documented `PLANNED` items is tracked in [`documentation/closeout-roadmap.md`](/Users/mksulty/Claude/Projects/design-hub/documentation/closeout-roadmap.md).
