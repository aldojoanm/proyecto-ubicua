# SolverIA – Workspace (IAM + Core Platform + AI + Backend)

Este workspace agrupa varios proyectos Java/Spring Boot que se desarrollan en paralelo:

- **core-plataform**: librería reusable con el core enterprise (dominio, casos de uso, puertos y adapters).
- **iam-service**: microservicio de Identity & Access Management (API + orquestación) que consume el core-platform.
- **backend-service**: microservicio “backend domain” que consume el core-platform.
- **ai-service**: microservicio de IA multi-módulo con Clean Architecture + Hexagonal estricta, y Postgres+pgvector.

> Nota: el repo incluye utilidades orientadas a Windows (PowerShell, `mvnw.cmd`) y también wrappers Unix en algunos subproyectos.

En general, el flujo recomendado es:
1) construir/instalar `core-platform` en tu repo Maven local, y
2) construir/arrancar los microservicios que lo consumen.

## Mapa rápido

| Componente | Tipo | Stack base | Qué contiene | Qué NO contiene |
|---|---|---|---|---|
| core-plataform/core-platform | Librería (JAR) | Spring Boot + JPA + (Mongo/Redis opcional) | Dominio + use cases + puertos + adapters + i18n/configs compartidas | Controllers REST específicos de un microservicio |
| iam-service | Microservicio (JAR) | Spring Boot Web/Security + OpenAPI + Pact tests | API REST + DTOs API + orquestadores + config Spring | Lógica de negocio y repos JPA del dominio (eso vive en core-platform) |
| backend-service | Microservicio (JAR) | Spring Boot Web/Security + JPA + Mongo + Redis | API/Aplicación/Dominio/Infra del servicio | (Depende del core-platform para capacidades compartidas) |
| ai-service | Microservicio multi-módulo | Spring Boot 3.5.x + Spring AI (en capas externas) | Capas separadas por módulos (domain/application/api/infrastructure/bootstrap) | Spring en domain/application (prohibido) |

## Estructura del workspace

```
.
├── ai-service/
│   ├── modules/
│   │   ├── ai-domain/
│   │   ├── ai-application/
│   │   ├── ai-infrastructure/
│   │   ├── ai-api/
│   │   └── ai-bootstrap/
│   ├── docker-compose.yml
│   ├── docker/postgres/init/
│   ├── scripts/                # scripts PowerShell para dev local
│   └── README.md / README-DEV.md
│
├── backend-service/
│   ├── src/main/java/com/solveria/backendservice/
│   │   ├── api/
│   │   ├── application/
│   │   ├── config/
│   │   ├── domain/
│   │   └── infrastructure/
│   └── src/main/resources/application.yml
│
├── core-plataform/
│   ├── adr/
│   └── core-platform/
│       ├── src/main/java/com/solveria/core/
│       │   ├── iam/             # comandos, puertos, use cases y adapters
│       │   ├── audit/
│       │   ├── security/
│       │   ├── observability/
│       │   └── shared/
│       └── src/test/java/.../CoreArchitectureTest.java  # ArchUnit
│
├── iam-service/
│   ├── docs/
│   │   ├── prompts/
│   │   ├── runbooks/
│   │   └── README.md
│   └── src/main/java/com/solveria/iamservice/
│       ├── api/
│       ├── application/
│       └── config/
│
├── iam-core-AI.code-workspace   # workspace multi-carpeta para VS Code/Cursor
└── init_runtime_dirs.ps1        # crea .runtime (logs/pids/tmp/etc.)
```

## Filosofía (lo importante)

### 1) Negocio central reusable (core-platform)
El **core-platform** concentra el negocio reusable (DDD + puertos/use cases) para evitar duplicación entre microservicios.

- Los **microservicios** (iam-service, backend-service, ai-service) se enfocan en **API, orquestación, wiring y concerns operativos**.
- El **core-platform** concentra **modelo de dominio**, **casos de uso** y **adapters de persistencia**.

Esto habilita consistencia (mismas reglas/casos de uso), y reduce el “copiar/pegar” entre servicios.

### 2) Arquitectura limpia/hexagonal, pero pragmática
- En **ai-service** la separación es **estricta** por módulos (domain/application/api/infrastructure/bootstrap), y está **prohibido** usar Spring en domain/application.
- En **core-platform** se adopta una decisión **enterprise-pragmática**: se acepta JPA en el dominio (ver ADR-001).

### 3) Microservicios delgados y trazables
En **iam-service** hay reglas explícitas de:
- **Separación de capas** (API/orchestration en el servicio; negocio en core-platform).
- **Logs estructurados**: `event=... key=value`.
- **i18n obligatorio**: no hardcodear mensajes; resolver por `errorCode` en bundles i18n.
- **Testing**: MockMvc/contract testing y (en IAM) perfil para pact provider.

## Puertos y configuración (según config actual)

- **iam-service**: `server.port=8080` (ver `src/main/resources/application.yml`).
- **backend-service**: `server.port=8082` (ver `src/main/resources/application.yml`).
- **ai-service**: `server.port=${AI_SERVICE_PORT:8091}` (ver `modules/ai-bootstrap/src/main/resources/application.yml`).

### Postgres/pgvector para AI
- Docker Compose: `ai-service/docker-compose.yml`
- Puerto host por defecto: `${AI_PG_HOST_PORT:-5434}`
- Variables típicas en `ai-service/.env`.

> Ojo: la guía de desarrollo de AI puede mencionar puertos antiguos (p. ej. 8081/5433). La referencia “fuente de verdad” es la configuración YAML y el docker-compose del repo.

## Cómo construir/ejecutar (mínimo y sin suposiciones)

### core-plataform (librería)
Desde la carpeta core-plataform:

```bash
./mvnw clean install
```

### iam-service
Desde la carpeta iam-service:

```bash
./mvnw clean test
./mvnw spring-boot:run
```

Documentación operativa y prompts en `iam-service/docs`.

### backend-service
Backend-service no trae wrapper en este workspace, así que usa Maven instalado:

```bash
mvn -f backend-service/pom.xml clean test
mvn -f backend-service/pom.xml spring-boot:run
```

### ai-service
En Linux, este repo trae `mvnw.cmd` y scripts PowerShell (orientados a Windows). Usa Maven instalado:

```bash
# DB (pgvector)
docker compose -f ai-service/docker-compose.yml up -d

# app
mvn -f ai-service/pom.xml -pl modules/ai-bootstrap -Dspring-boot.run.profiles=dev spring-boot:run
```

## Documentación y referencias

- AI Service: `ai-service/README.md` y `ai-service/README-DEV.md`
- IAM Docs: `iam-service/docs/README.md` + `docs/runbooks/MASTER-RUNBOOK.md`
- ADR Core: `core-plataform/adr/ADR-001-Core-Pragmatic-JPA.md`

## Runtime local

El script `init_runtime_dirs.ps1` crea una estructura `.runtime/` (logs/pids/tmp/dumps/reports) para el workspace.
