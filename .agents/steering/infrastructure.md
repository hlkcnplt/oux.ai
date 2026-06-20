# OpenDesign — Infrastructure

## Deployment Strategy

OpenDesign is built for two distinct use cases:
1. **Self-Hosted:** Local or small-team use, services exposed on unique ports.
2. **Production:** SaaS-ready hosting behind an Nginx gateway, all internal ports blocked.

All management should be done via the root `Makefile`.

---

## Command Shortcuts

| Command | Action |
|---------|--------|
| `make up` | Start self-hosted stack (`docker-compose.yml`) |
| `make down` | Stop self-hosted stack |
| `make prod-up` | Start production stack (base + `docker-compose.prod.yml`) |
| `make prod-down` | Stop production stack |
| `make logs` | View all container logs |
| `make clean` | Remove all containers, volumes, and orphans |

---

## Local Development Stack

All services are orchestrated via Docker Compose for local development. A developer must be able to run the entire stack with a single command after configuring `.env`.

```bash
cp .env.example .env
# Edit .env with your API keys if using cloud providers
make up
```

### Service URLs (Local / Self-Hosted)

| Service       | URL                      | Mapping |
|---------------|--------------------------|---------|
| React Client  | http://localhost:5173    | 3000:80 |
| Java Core API | http://localhost:8080    | 8080:8080 |
| Python AI API | http://localhost:8000    | 8000:8000 |
| PostgreSQL    | localhost:5432           | 5432:5432 |

---

## Production / SaaS Architecture

In production, an internal Nginx container (`nginx-gateway`) serves as the single public exposure on port 80.

### Gateway Routing

| Entry Path | Backend Container | Internal Port |
|------------|-------------------|---------------|
| `/`        | `client`          | 80            |
| `/api/`    | `server-core`     | 8080          |
| `/ai/`     | `server-ai`       | 8000          |

### Configuration Rules (Production)

- Use `.env.prod` for all secure credentials.
- All internal service ports (5432, 8080, 8000) **MUST NOT** be exposed to the public internet.
- Ensure `VITE_API_BASE_URL` in `.env.prod` is set to the full public domain.
- Docker images use multi-stage builds (`eclipse-temurin` for Java, `node:20-alpine` for frontend).

---

## Environment Variables

All environment variables are documented in `.env.example`. No service may read a configuration value that is not listed there.

### Core Variables

| Variable | Description |
|----------|-------------|
| `POSTGRES_PASSWORD` | Strong password for production |
| `AI_PROVIDER` | Default AI provider (LOCAL, GEMINI) |
| `VITE_API_BASE_URL` | Public domain URL (critical for production client build) |
| `LOCAL_AI_ENDPOINT` | URL for Ollama/LM Studio if using LOCAL provider |

---

## CI/CD (GitHub Actions)

Pipeline file: `.github/workflows/ci.yml`

The CI pipeline runs automated checks on every PR to verify that the build-ready states are maintained for all three services.

### Rules

- All jobs must pass before a PR can be merged.
- Integration tests use Testcontainers and only run on pushes to `main`.
- Secrets for CI/CD must be stored in GitHub Actions Secrets.

---

## Observability

- Spring Boot Actuator endpoints are exposed for health checks.
- Structured JSON logging is used for production logs.
- Future: A `docker-compose.observability.yml` overlay for Prometheus + Grafana.
