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

mkdir -p "$LOG_DIR"

echo "[1/5] Installing core-platform to local Maven repo..."
"$ROOT_DIR/core-plataform/mvnw" -f "$ROOT_DIR/core-plataform/core-platform/pom.xml" clean install

echo "[2/5] Starting databases (AI + Backend)..."
docker compose -f "$ROOT_DIR/ai-service/docker-compose.yml" up -d
docker compose -f "$ROOT_DIR/backend-service/docker-compose.yml" up -d

echo "[3/5] Starting IAM Service..."
"$ROOT_DIR/iam-service/mvnw" -f "$ROOT_DIR/iam-service/pom.xml" spring-boot:run > "$LOG_DIR/iam-service.log" 2>&1 &
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
