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

require_file() {
  [[ -f "$1" ]] || {
    echo "Missing file: $1" >&2
    exit 1
  }
}

require_cmd docker
require_cmd cygpath
require_cmd powershell.exe

mkdir -p "$LOG_DIR"

# Ejecuta mvnw.cmd dentro del repo correcto (clave para que encuentre .mvn/wrapper/*)
mvnw_in_repo() {
  local repo_dir_posix="$1"; shift
  local wrapper_posix="$repo_dir_posix/mvnw.cmd"
  require_file "$wrapper_posix"

  local repo_dir_win wrapper_win
  repo_dir_win="$(cygpath -w "$repo_dir_posix")"
  wrapper_win="$(cygpath -w "$wrapper_posix")"

  # Armar argumentos para PowerShell (robusto con comillas)
  local ps_args=()
  local a
  for a in "$@"; do
    # convertir rutas posix absolutas si aparecen
    if [[ "$a" == /* ]]; then
      a="$(cygpath -w "$a")"
    fi
    a="${a//\'/\'\'}"
    ps_args+=("'$a'")
  done

  # cd al repo antes de ejecutar mvnw.cmd
  powershell.exe -NoProfile -ExecutionPolicy Bypass -Command \
    "Set-Location -LiteralPath '$repo_dir_win'; & '$wrapper_win' $(
      IFS=' '; echo "${ps_args[*]}"
    )"
}

echo "[0/5] Checking Maven wrappers..."
require_file "$ROOT_DIR/core-plataform/mvnw.cmd"
require_file "$ROOT_DIR/iam-service/mvnw.cmd"
require_file "$ROOT_DIR/ai-service/mvnw.cmd"
require_file "$ROOT_DIR/backend-service/mvnw.cmd"

echo "[1/5] Installing core-platform to local Maven repo..."
mvnw_in_repo "$ROOT_DIR/core-plataform" \
  -f "core-platform/pom.xml" \
  clean install

echo "[2/5] Starting databases (AI + Backend)..."
docker compose -f "$ROOT_DIR/ai-service/docker-compose.yml" up -d
docker compose -f "$ROOT_DIR/backend-service/docker-compose.yml" up -d

echo "[3/5] Starting IAM Service..."
mvnw_in_repo "$ROOT_DIR/iam-service" \
  -f "pom.xml" \
  spring-boot:run > "$LOG_DIR/iam-service.log" 2>&1 &
IAM_PID=$!

echo "[4/5] Starting AI Service..."
mvnw_in_repo "$ROOT_DIR/ai-service" \
  -f "pom.xml" \
  -pl modules/ai-bootstrap \
  spring-boot:run > "$LOG_DIR/ai-service.log" 2>&1 &
AI_PID=$!

echo "[5/5] Starting Backend Service..."
mvnw_in_repo "$ROOT_DIR/backend-service" \
  -f "pom.xml" \
  spring-boot:run > "$LOG_DIR/backend-service.log" 2>&1 &
BACKEND_PID=$!

echo "All services are starting."
echo "IAM PID: $IAM_PID"
echo "AI PID: $AI_PID"
echo "Backend PID: $BACKEND_PID"
echo "Logs: $LOG_DIR"
