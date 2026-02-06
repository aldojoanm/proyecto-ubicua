#!/bin/sh
set -eu

ROOT_DIR="$(CDPATH= cd -- "$(dirname -- "$0")/.." && pwd)"

IAM_DIR="$ROOT_DIR/iam-service"
BACKEND_DIR="$ROOT_DIR/backend-service"
AI_DIR="$ROOT_DIR/ai-service"

echo "Starting Docker dependencies..."

if [ -f "$IAM_DIR/docker-compose.yml" ]; then
  docker compose -f "$IAM_DIR/docker-compose.yml" up -d
else
  echo "IAM docker-compose.yml not found at $IAM_DIR"
  exit 1
fi

if [ -f "$BACKEND_DIR/docker-compose.yml" ]; then
  docker compose -f "$BACKEND_DIR/docker-compose.yml" up -d
else
  echo "Backend docker-compose.yml not found at $BACKEND_DIR"
  exit 1
fi

if [ -f "$AI_DIR/docker-compose.yml" ]; then
  docker compose -f "$AI_DIR/docker-compose.yml" up -d
else
  echo "AI docker-compose.yml not found at $AI_DIR"
  exit 1
fi

echo "Docker dependencies started."
echo ""
echo "Start services in separate terminals:"
echo "1) IAM:     cd $IAM_DIR && ./mvnw spring-boot:run"
echo "2) Backend: cd $BACKEND_DIR && ./mvnw spring-boot:run"
echo "3) AI:      cd $AI_DIR && ./mvnw -pl modules/ai-bootstrap spring-boot:run"
echo ""
echo "Swagger URLs:"
echo "- IAM:     http://localhost:8080/swagger-ui.html"
echo "- Backend: http://localhost:8082/swagger-ui.html"
echo "- AI:      http://localhost:8091/swagger-ui.html"
