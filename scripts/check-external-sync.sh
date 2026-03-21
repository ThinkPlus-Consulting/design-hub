#!/usr/bin/env bash

set -euo pipefail

usage() {
  cat <<'EOF'
Usage: ./scripts/check-external-sync.sh [--env-file PATH] [--source all|jira|azure] [--probe-jira]

Validates the effective external-sync environment for Jira and Azure DevOps.
Use --source jira while Jira is being rolled out independently from Azure DevOps.
Use --probe-jira to issue a real authenticated GET against the configured Jira poll endpoint.
EOF
}

ENV_FILE=""
SELECTED_SOURCE="all"
PROBE_JIRA=false

while [[ $# -gt 0 ]]; do
  case "$1" in
    --env-file)
      shift
      if [[ $# -eq 0 ]]; then
        echo "Missing value for --env-file" >&2
        usage >&2
        exit 1
      fi
      ENV_FILE="$1"
      ;;
    --source)
      shift
      if [[ $# -eq 0 ]]; then
        echo "Missing value for --source" >&2
        usage >&2
        exit 1
      fi
      SELECTED_SOURCE="$(printf '%s' "$1" | tr '[:upper:]' '[:lower:]')"
      ;;
    --probe-jira)
      PROBE_JIRA=true
      ;;
    -h|--help)
      usage
      exit 0
      ;;
    *)
      echo "Unknown argument: $1" >&2
      usage >&2
      exit 1
      ;;
  esac
  shift
done

case "$SELECTED_SOURCE" in
  all|jira|azure)
    ;;
  *)
    echo "Unsupported --source value: $SELECTED_SOURCE" >&2
    usage >&2
    exit 1
    ;;
esac

if [[ -n "$ENV_FILE" ]]; then
  if [[ ! -f "$ENV_FILE" ]]; then
    echo "Env file not found: $ENV_FILE" >&2
    exit 1
  fi
  while IFS= read -r line || [[ -n "$line" ]]; do
    [[ -z "$line" || "$line" =~ ^[[:space:]]*# ]] && continue
    if [[ "$line" != *=* ]]; then
      echo "Invalid env line in $ENV_FILE: $line" >&2
      exit 1
    fi
    key="${line%%=*}"
    value="${line#*=}"
    key="${key#"${key%%[![:space:]]*}"}"
    key="${key%"${key##*[![:space:]]}"}"
    if [[ ${!key+x} == x ]]; then
      continue
    fi
    export "$key=$value"
  done < "$ENV_FILE"
fi

is_configured() {
  [[ -n "${1:-}" ]]
}

print_value() {
  local label="$1"
  local value="$2"
  printf '%-16s %s\n' "$label" "$value"
}

print_list() {
  local label="$1"
  shift
  local value="$*"
  printf '%-16s %s\n' "$label" "$value"
}

collect_jira_missing() {
  local result=()

  is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_BASE_URL:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_JIRA_BASE_URL")
  is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_PROJECT_KEY:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_JIRA_PROJECT_KEY")
  is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_TOKEN:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_JIRA_TOKEN")
  is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_POLL_PATH:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_JIRA_POLL_PATH")

  local jira_base_url="${DESIGNHUB_EXTERNAL_SYNC_JIRA_BASE_URL:-}"
  local direct_atlassian_host=false
  if [[ "$jira_base_url" == *".atlassian.net"* || "$jira_base_url" == *"api.atlassian.com"* ]]; then
    direct_atlassian_host=true
  fi

  if $direct_atlassian_host; then
    is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_ACCOUNT_EMAIL:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_JIRA_ACCOUNT_EMAIL")
  fi

  if ! is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_JQL:-}" && ! is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_UPDATED_SINCE:-}"; then
    result+=("DESIGNHUB_EXTERNAL_SYNC_JIRA_JQL or DESIGNHUB_EXTERNAL_SYNC_JIRA_UPDATED_SINCE")
  fi

  if [[ ${#result[@]} -gt 0 ]]; then
    printf '%s\n' "${result[@]}"
  fi
}

collect_azure_missing() {
  local result=()

  is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_BASE_URL:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_BASE_URL")
  is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_ORGANIZATION:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_ORGANIZATION")
  is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_PROJECT:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_PROJECT")
  is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_TOKEN:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_TOKEN")
  is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_POLL_PATH:-}" || result+=("DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_POLL_PATH")

  if ! is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_WIQL:-}" && ! is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_UPDATED_SINCE:-}"; then
    result+=("DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_WIQL or DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_UPDATED_SINCE")
  fi

  if [[ ${#result[@]} -gt 0 ]]; then
    printf '%s\n' "${result[@]}"
  fi
}

probe_jira() {
  if ! command -v curl >/dev/null 2>&1; then
    echo "curl is required for --probe-jira" >&2
    return 1
  fi

  local base_url="${DESIGNHUB_EXTERNAL_SYNC_JIRA_BASE_URL%/}"
  local poll_path="${DESIGNHUB_EXTERNAL_SYNC_JIRA_POLL_PATH:-}"
  local jql="${DESIGNHUB_EXTERNAL_SYNC_JIRA_JQL:-project = ${DESIGNHUB_EXTERNAL_SYNC_JIRA_PROJECT_KEY:-} ORDER BY updated DESC}"
  local endpoint="${base_url}${poll_path}"

  echo
  echo "Probing Jira poll endpoint..."

  local curl_args=(
    --silent
    --show-error
    --fail
    --user "${DESIGNHUB_EXTERNAL_SYNC_JIRA_ACCOUNT_EMAIL}:${DESIGNHUB_EXTERNAL_SYNC_JIRA_TOKEN}"
    --header "Accept: application/json"
    --get
    "$endpoint"
    --data-urlencode "projectKey=${DESIGNHUB_EXTERNAL_SYNC_JIRA_PROJECT_KEY}"
    --data-urlencode "jql=${jql}"
    --data-urlencode "maxResults=1"
    --data-urlencode "fields=summary"
  )

  if is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_UPDATED_SINCE:-}"; then
    curl_args+=(--data-urlencode "updatedSince=${DESIGNHUB_EXTERNAL_SYNC_JIRA_UPDATED_SINCE}")
  fi

  curl "${curl_args[@]}" >/dev/null
  echo "Jira probe succeeded."
}

selected_matches() {
  [[ "$SELECTED_SOURCE" == "all" || "$SELECTED_SOURCE" == "$1" ]]
}

echo "External Sync Readiness"
echo "Selected source: $SELECTED_SOURCE"
if [[ -n "$ENV_FILE" ]]; then
  echo "Loaded env file: $ENV_FILE"
fi

failure=0

if selected_matches "jira"; then
  jira_missing=()
  while IFS= read -r item; do
    [[ -n "$item" ]] && jira_missing+=("$item")
  done < <(collect_jira_missing)
  jira_ready=true
  if [[ ${#jira_missing[@]} -gt 0 ]]; then
    jira_ready=false
    failure=1
  fi

  jira_auth_mode="bearer token"
  jira_base_url="${DESIGNHUB_EXTERNAL_SYNC_JIRA_BASE_URL:-}"
  if [[ "$jira_base_url" == *".atlassian.net"* || "$jira_base_url" == *"api.atlassian.com"* ]]; then
    jira_auth_mode="basic auth via account email + API token"
  fi

  echo
  echo "Jira"
  print_value "base-url" "${DESIGNHUB_EXTERNAL_SYNC_JIRA_BASE_URL:-<missing>}"
  print_value "project-key" "${DESIGNHUB_EXTERNAL_SYNC_JIRA_PROJECT_KEY:-<missing>}"
  print_value "account-email" "${DESIGNHUB_EXTERNAL_SYNC_JIRA_ACCOUNT_EMAIL:-<missing>}"
  print_value "auth-mode" "$jira_auth_mode"
  print_value "poll-path" "${DESIGNHUB_EXTERNAL_SYNC_JIRA_POLL_PATH:-<missing>}"
  print_value "token" "$(is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_TOKEN:-}" && printf 'configured' || printf '<missing>')"
  print_value "filter" "$(is_configured "${DESIGNHUB_EXTERNAL_SYNC_JIRA_JQL:-}" && printf 'jql' || printf 'updatedSince-only')"
  print_value "ready" "$( $jira_ready && printf 'yes' || printf 'no' )"
  if [[ ${#jira_missing[@]} -gt 0 ]]; then
    print_list "missing" "${jira_missing[@]}"
  fi

  if $PROBE_JIRA; then
    if ! $jira_ready; then
      echo
      echo "Jira probe skipped because the Jira rollout config is incomplete." >&2
      exit 1
    fi
    probe_jira
  fi
fi

if selected_matches "azure"; then
  azure_missing=()
  while IFS= read -r item; do
    [[ -n "$item" ]] && azure_missing+=("$item")
  done < <(collect_azure_missing)
  azure_ready=true
  if [[ ${#azure_missing[@]} -gt 0 ]]; then
    azure_ready=false
    failure=1
  fi

  echo
  echo "Azure DevOps"
  print_value "base-url" "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_BASE_URL:-<missing>}"
  print_value "organization" "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_ORGANIZATION:-<missing>}"
  print_value "project" "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_PROJECT:-<missing>}"
  print_value "poll-path" "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_POLL_PATH:-<missing>}"
  print_value "token" "$(is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_TOKEN:-}" && printf 'configured' || printf '<missing>')"
  print_value "filter" "$(is_configured "${DESIGNHUB_EXTERNAL_SYNC_AZURE_DEVOPS_WIQL:-}" && printf 'wiql' || printf 'updatedSince-only')"
  print_value "ready" "$( $azure_ready && printf 'yes' || printf 'no' )"
  if [[ ${#azure_missing[@]} -gt 0 ]]; then
    print_list "missing" "${azure_missing[@]}"
  fi
fi

exit "$failure"
