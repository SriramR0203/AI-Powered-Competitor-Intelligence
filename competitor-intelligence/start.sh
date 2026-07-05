#!/usr/bin/env bash
# ─────────────────────────────────────────────────────────────────────────────
# Render / Docker start script for the Spring Boot backend.
#
# Responsibilities:
#   1. Convert Render's DATABASE_URL (postgres://) to the JDBC form that
#      Spring Boot requires (jdbc:postgresql://).
#   2. Locate the application JAR via a glob so version bumps in pom.xml
#      do not require a matching change here.
#   3. Hand off to the JVM with exec so the JVM runs as PID 1 and receives
#      SIGTERM directly on container shutdown.
# ─────────────────────────────────────────────────────────────────────────────
set -euo pipefail

# ── 1. Convert postgres:// → jdbc:postgresql:// ──────────────────────────────
# Render's managed PostgreSQL always provides DATABASE_URL in the legacy
# "postgres://" scheme.  Leave the variable alone if it is already in jdbc:
# form (useful for local Docker testing with a manual override).
if [[ -n "${DATABASE_URL:-}" && "${DATABASE_URL}" == postgres://* ]]; then
  # Strip scheme
  STRIPPED="${DATABASE_URL#postgres://}"

  # Credentials live before the first @
  CREDENTIALS="${STRIPPED%%@*}"
  DB_USER="${CREDENTIALS%%:*}"
  DB_PASS="${CREDENTIALS#*:}"

  # Host:port/dbname live after the first @
  HOSTPATH="${STRIPPED#*@}"
  HOST_PORT="${HOSTPATH%%/*}"
  DBNAME="${HOSTPATH#*/}"
  DBNAME="${DBNAME%%\?*}"   # strip any trailing query string

  export SPRING_DATASOURCE_URL="jdbc:postgresql://${HOST_PORT}/${DBNAME}?sslmode=require"
  export SPRING_DATASOURCE_USERNAME="${DB_USER}"
  export SPRING_DATASOURCE_PASSWORD="${DB_PASS}"

  echo "[start.sh] Converted DATABASE_URL → SPRING_DATASOURCE_URL (credentials redacted)"
fi

# ── 2. Locate the application JAR ────────────────────────────────────────────
# Use a glob so the script is resilient to version changes in pom.xml.
# The build stage always produces exactly one fat JAR in /app/target/.
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
