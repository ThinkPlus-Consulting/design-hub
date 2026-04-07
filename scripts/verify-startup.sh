#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
BACKEND_DIR="$ROOT_DIR/backend"
FRONTEND_DIR="$ROOT_DIR/frontend"
NEO4J_CONTAINER_NAME="design-hub-neo4j"
NEO4J_HTTP_PORT="${DESIGN_HUB_NEO4J_HTTP_PORT:-27484}"
NEO4J_BOLT_PORT="${DESIGN_HUB_NEO4J_BOLT_PORT:-27697}"
BACKEND_HEALTH_URL="http://localhost:8091/actuator/health"
GRAPH_URL="http://localhost:8091/api/v1/system-shell-graph/graph"
FRONTEND_URL="http://localhost:4300"

KEEP_RUNNING=false
SKIP_FRONTEND_INSTALL=false
STARTED_BACKEND=false
STARTED_FRONTEND=false
BACKEND_PID=""
FRONTEND_PID=""
LOG_DIR="$(mktemp -d "${TMPDIR:-/tmp}/system-shell-graph-startup.XXXXXX")"
BACKEND_LOG="$LOG_DIR/backend.log"
FRONTEND_LOG="$LOG_DIR/frontend.log"
BACKEND_PID_FILE="$LOG_DIR/backend.pid"
FRONTEND_PID_FILE="$LOG_DIR/frontend.pid"

log() {
  printf '[startup-check] %s\n' "$*"
}

fail() {
  log "ERROR: $*"
  log "Logs: $LOG_DIR"
  exit 1
}

usage() {
  cat <<'EOF'
Usage: ./scripts/verify-startup.sh [--keep-running] [--skip-frontend-install]

Bootstraps Neo4j, verifies or starts the Spring backend on port 8091, verifies
or starts the Angular frontend on port 4300, then probes the documented live
endpoints.

Options:
  --keep-running           Leave backend/frontend processes running on success.
  --skip-frontend-install  Do not run `./npmw ci` when frontend/node_modules is missing.
  --help                   Show this message.
EOF
}

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    fail "Missing required command: $1"
  fi
}

port_listening() {
  local port="$1"
  if command -v lsof >/dev/null 2>&1; then
    lsof -tiTCP:"$port" -sTCP:LISTEN >/dev/null 2>&1
    return
  fi

  nc -z localhost "$port" >/dev/null 2>&1
}

wait_for_neo4j() {
  local attempts=45
  local attempt=1
  local health=""

  while [ "$attempt" -le "$attempts" ]; do
    health="$(docker inspect --format '{{if .State.Health}}{{.State.Health.Status}}{{else}}{{.State.Status}}{{end}}' "$NEO4J_CONTAINER_NAME" 2>/dev/null || true)"

    if [ "$health" = "healthy" ] || [ "$health" = "running" ]; then
      return 0
    fi

    sleep 2
    attempt=$((attempt + 1))
  done

  fail "Neo4j container '$NEO4J_CONTAINER_NAME' did not become healthy."
}

probe_backend_health() {
  curl -fsS "$BACKEND_HEALTH_URL" 2>/dev/null | grep -q '"status":"UP"'
}

probe_graph() {
  curl -fsS "$GRAPH_URL" 2>/dev/null | grep -q '"graphScope":"SYSTEM_FRONTEND_GRAPH"'
}

probe_frontend() {
  curl -fsS "$FRONTEND_URL" 2>/dev/null | grep -q '<title>System Shell Graph</title>'
}

wait_for_probe() {
  local probe_name="$1"
  local timeout_seconds="$2"
  local pid="$3"
  local log_file="$4"
  local elapsed=0

  while [ "$elapsed" -lt "$timeout_seconds" ]; do
    if "$probe_name"; then
      return 0
    fi

    if [ -n "$pid" ] && ! kill -0 "$pid" 2>/dev/null; then
      tail -n 80 "$log_file" >&2 || true
      fail "$probe_name failed because the background process exited early."
    fi

    sleep 2
    elapsed=$((elapsed + 2))
  done

  if [ -f "$log_file" ]; then
    tail -n 80 "$log_file" >&2 || true
  fi
  fail "Timed out waiting for $probe_name after ${timeout_seconds}s."
}

cleanup() {
  if [ "$KEEP_RUNNING" = true ]; then
    return
  fi

  if [ "$STARTED_FRONTEND" = true ] && [ -n "$FRONTEND_PID" ] && kill -0 "$FRONTEND_PID" 2>/dev/null; then
    kill "$FRONTEND_PID" 2>/dev/null || true
    wait "$FRONTEND_PID" 2>/dev/null || true
  fi

  if [ "$STARTED_BACKEND" = true ] && [ -n "$BACKEND_PID" ] && kill -0 "$BACKEND_PID" 2>/dev/null; then
    kill "$BACKEND_PID" 2>/dev/null || true
    wait "$BACKEND_PID" 2>/dev/null || true
  fi
}

start_backend_process() {
  if [ "$KEEP_RUNNING" = true ]; then
    (
      cd "$BACKEND_DIR"
      nohup ./mvnw spring-boot:run >"$BACKEND_LOG" 2>&1 &
      echo "$!" >"$BACKEND_PID_FILE"
    )
    BACKEND_PID="$(cat "$BACKEND_PID_FILE")"
    return
  fi

  (
    cd "$BACKEND_DIR"
    exec ./mvnw spring-boot:run
  ) >"$BACKEND_LOG" 2>&1 &
  BACKEND_PID="$!"
}

start_frontend_process() {
  if [ "$KEEP_RUNNING" = true ]; then
    (
      cd "$FRONTEND_DIR"
      nohup ./npmw start >"$FRONTEND_LOG" 2>&1 &
      echo "$!" >"$FRONTEND_PID_FILE"
    )
    FRONTEND_PID="$(cat "$FRONTEND_PID_FILE")"
    return
  fi

  (
    cd "$FRONTEND_DIR"
    exec ./npmw start
  ) >"$FRONTEND_LOG" 2>&1 &
  FRONTEND_PID="$!"
}

while [ "$#" -gt 0 ]; do
  case "$1" in
    --keep-running)
      KEEP_RUNNING=true
      ;;
    --skip-frontend-install)
      SKIP_FRONTEND_INSTALL=true
      ;;
    --help|-h)
      usage
      exit 0
      ;;
    *)
      fail "Unknown argument: $1"
      ;;
  esac
  shift
done

trap cleanup EXIT

require_command docker
require_command curl
require_command java

if ! command -v nc >/dev/null 2>&1 && ! command -v lsof >/dev/null 2>&1; then
  fail "Missing required command: nc or lsof"
fi

log "Bootstrapping Neo4j through docker compose."
(cd "$ROOT_DIR" && docker compose up -d neo4j >/dev/null)
wait_for_neo4j

if probe_backend_health; then
  log "Reusing existing backend at $BACKEND_HEALTH_URL."
else
  if port_listening 8091; then
    fail "Port 8091 is already in use, but the System Shell Graph backend health is not reporting UP."
  fi

  log "Starting backend from $BACKEND_DIR."
  start_backend_process
  STARTED_BACKEND=true
  wait_for_probe probe_backend_health 120 "$BACKEND_PID" "$BACKEND_LOG"
fi

probe_graph || fail "System shell graph endpoint did not respond with seeded data."

if [ ! -d "$FRONTEND_DIR/node_modules" ]; then
  if [ "$SKIP_FRONTEND_INSTALL" = true ]; then
    fail "frontend/node_modules is missing and --skip-frontend-install was set."
  fi

  log "Installing frontend dependencies with ./npmw ci."
  (
    cd "$FRONTEND_DIR"
    ./npmw ci
  )
fi

if probe_frontend; then
  log "Reusing existing frontend at $FRONTEND_URL."
else
  if port_listening 4300; then
    fail "Port 4300 is already in use, but the System Shell Graph frontend did not respond as expected."
  fi

  log "Starting frontend from $FRONTEND_DIR."
  start_frontend_process
  STARTED_FRONTEND=true
  wait_for_probe probe_frontend 120 "$FRONTEND_PID" "$FRONTEND_LOG"
fi

log "Startup proof passed."
log "Neo4j: http://localhost:${NEO4J_HTTP_PORT}"
log "Neo4j Bolt: bolt://localhost:${NEO4J_BOLT_PORT}"
log "Backend: $BACKEND_HEALTH_URL"
log "Frontend: $FRONTEND_URL"
log "Logs: $LOG_DIR"

if [ "$KEEP_RUNNING" = true ]; then
  log "Leaving started services running because --keep-running was set."
  if [ -f "$BACKEND_PID_FILE" ]; then
    log "Backend PID: $(cat "$BACKEND_PID_FILE")"
  fi
  if [ -f "$FRONTEND_PID_FILE" ]; then
    log "Frontend PID: $(cat "$FRONTEND_PID_FILE")"
  fi
fi
