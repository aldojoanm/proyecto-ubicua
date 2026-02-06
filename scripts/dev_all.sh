#!/usr/bin/env bash
set -euo pipefail

ROOT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"
LOG_DIR="$ROOT_DIR/logs"

require_cmd() {
  command -v "$1" >/dev/null 2>&1 || {
    echo "Missing command: $1" >&2
    exit 1
  }
}

require_cmd docker
require_cmd mvn

compose_cmd() {
  if docker compose version >/dev/null 2>&1; then
    echo "docker compose"
    return
  fi
  if command -v docker-compose >/dev/null 2>&1; then
    echo "docker-compose"
    return
  fi
  echo "Missing docker compose" >&2
  exit 1
}

mkdir -p "$LOG_DIR"

echo "[1/5] Installing core-platform to local Maven repo..."
(cd "$ROOT_DIR/core-plataform" && bash ./mvnw -f pom.xml clean install)

echo "[2/5] Starting databases (AI + Backend)..."
COMPOSE_CMD=$(compose_cmd)
$COMPOSE_CMD -f "$ROOT_DIR/ai-service/docker-compose.yml" up -d
$COMPOSE_CMD -f "$ROOT_DIR/backend-service/docker-compose.yml" up -d

echo "[3/5] Starting IAM Service..."
(cd "$ROOT_DIR/iam-service" && SPRING_PROFILES_ACTIVE=dev bash ./mvnw -f pom.xml spring-boot:run > "$LOG_DIR/iam-service.log" 2>&1) &
IAM_PID=$!

echo "[4/5] Starting AI Service..."
mvn -f "$ROOT_DIR/ai-service/pom.xml" -pl modules/ai-bootstrap spring-boot:run > "$LOG_DIR/ai-service.log" 2>&1 &
AI_PID=$!

echo "[5/5] Starting Backend Service..."
mvn -f "$ROOT_DIR/backend-service/pom.xml" spring-boot:run > "$LOG_DIR/backend-service.log" 2>&1 &
BACKEND_PID=$!

echo "All services are starting."
echo "IAM PID: $IAM_PID"
echo "AI PID: $AI_PID"
echo "Backend PID: $BACKEND_PID"
echo "Logs: $LOG_DIR"
