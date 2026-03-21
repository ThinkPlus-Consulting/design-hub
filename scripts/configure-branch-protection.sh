#!/usr/bin/env bash
set -euo pipefail

usage() {
  cat <<'EOF'
Usage: ./scripts/configure-branch-protection.sh [owner/repo] [branch]

Applies the Design Hub branch-protection baseline through GitHub CLI:
- require pull requests before merging
- require one approving review
- dismiss stale reviews
- require conversation resolution
- require strict status checks
- require "Backend Tests" and "Frontend Verification"
- block force pushes and branch deletion

If owner/repo is omitted, the script derives it from git remote "origin".
The default branch target is "main".
EOF
}

if [[ "${1:-}" == "--help" || "${1:-}" == "-h" ]]; then
  usage
  exit 0
fi

require_command() {
  if ! command -v "$1" >/dev/null 2>&1; then
    echo "Missing required command: $1" >&2
    exit 1
  fi
}

require_command gh
require_command git

repo="${1:-}"
branch="${2:-main}"

if [[ -z "$repo" ]]; then
  remote_url="$(git remote get-url origin 2>/dev/null || true)"
  if [[ -z "$remote_url" ]]; then
    echo "No origin remote found. Pass owner/repo explicitly." >&2
    exit 1
  fi

  repo="$(printf '%s\n' "$remote_url" | sed -E 's#(git@github.com:|https://github.com/)##; s#\.git$##')"
fi

payload="$(cat <<EOF
{
  "required_status_checks": {
    "strict": true,
    "contexts": [
      "Backend Tests",
      "Frontend Verification"
    ]
  },
  "enforce_admins": true,
  "required_pull_request_reviews": {
    "dismiss_stale_reviews": true,
    "require_code_owner_reviews": false,
    "required_approving_review_count": 1,
    "require_last_push_approval": false
  },
  "restrictions": null,
  "required_linear_history": true,
  "allow_force_pushes": false,
  "allow_deletions": false,
  "block_creations": false,
  "required_conversation_resolution": true,
  "lock_branch": false,
  "allow_fork_syncing": true
}
EOF
)"

echo "Applying branch protection to ${repo}:${branch}"

if ! gh api \
  --method PUT \
  -H "Accept: application/vnd.github+json" \
  "repos/${repo}/branches/${branch}/protection" \
  --input - <<<"$payload"; then
  echo >&2
  echo "Branch protection could not be applied." >&2
  echo "If GitHub returns a 403 for a private repo, switch the repo to public or use a plan that supports private-repo branch protection." >&2
  exit 1
fi

echo "Branch protection applied to ${repo}:${branch}"
