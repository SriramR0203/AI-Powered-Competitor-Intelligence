#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Render / Docker start script for the Spring Boot backend.
#
# Responsibilities:
#   1. Optionally convert a legacy postgres:// URL to jdbc:postgresql://.
#      This conversion is NOT needed for Neon — Neon provides a ready-to-use
#      JDBC URL which is set directly as SPRING_DATASOURCE_URL.
#      The block is kept as a no-op fallback for any environment that still
#      supplies DATABASE_URL in the postgres:// scheme.
#   2. Locate the application JAR via a glob so version bumps in pom.xml
#      do not require a matching change here.
#   3. Hand off to the JVM with exec so the JVM runs as PID 1 and receives
#      SIGTERM directly on container shutdown.
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

# ── 1. Legacy postgres:// → jdbc:postgresql:// conversion (no-op for Neon) ───
# Neon provides SPRING_DATASOURCE_URL directly as a JDBC URL, so this block
# is skipped entirely in normal production use.
# It only activates if DATABASE_URL is set AND starts with "postgres://" —
# useful if the deployment is ever pointed back at a Render-managed database.
if [[ -n "${DATABASE_URL:-}" && "${DATABASE_URL}" == postgres://* ]]; then
  STRIPPED="${DATABASE_URL#postgres://}"
  CREDENTIALS="${STRIPPED%%@*}"
  DB_USER="${CREDENTIALS%%:*}"
  DB_PASS="${CREDENTIALS#*:}"
  HOSTPATH="${STRIPPED#*@}"
  HOST_PORT="${HOSTPATH%%/*}"
  DBNAME="${HOSTPATH#*/}"
  DBNAME="${DBNAME%%\?*}"

  export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOST_PORT}/${DBNAME}?sslmode=require"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASS}"

  echo "[start.sh] Converted DATABASE_URL → SPRING_DATASOURCE_URL (credentials redacted)"
fi

# ── 2. Locate the application JAR ────────────────────────────────────────────
# Glob is resilient to version changes in pom.xml.
JAR_PATH=""
for f in /app/target/*.jar target/*.jar; do
  if [[ -f "$f" ]]; then
    JAR_PATH="$f"
    break
  fi
done

if [[ -z "$JAR_PATH" ]]; then
  echo "[start.sh] ERROR: No JAR found in /app/target/ or target/. Did the build stage run?" >&2
  exit 1
fi

echo "[start.sh] Starting: ${JAR_PATH}"

# ── 3. Launch ─────────────────────────────────────────────────────────────────
exec java \
  -Dserver.port="${PORT:-8080}" \
  -Dspring.profiles.active="${SPRING_PROFILES_ACTIVE:-prod}" \
  -Djava.security.egd=file:/dev/./urandom \
  -XX:+UseContainerSupport \
  -XX:MaxRAMPercentage=75.0 \
  -jar "${JAR_PATH}"
