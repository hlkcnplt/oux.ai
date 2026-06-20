# OpenDesign — Architecture

## Overview

OpenDesign is a polyglot monorepo with three independently deployable services that collaborate to provide hybrid AI-powered UI/UX auditing on an infinite canvas.

```
┌─────────────────────────────────────────────────────────┐
│                      Browser Client                     │
│              React 19 + Vite 8 + Tailwind CSS           │
│       Infinite Canvas (React-Konva 19) + Zustand 5      │
└───────────────────────────┬─────────────────────────────┘
                            │ HTTPS REST
                            ▼
┌─────────────────────────────────────────────────────────┐
│                      server-core                        │
│               Java 21 + Spring Boot 3.3                 │
│         JPA / PostgreSQL  ·  REST API  ·  Proxy         │
└──────────────────┬──────────────────────────────────────┘
                   │ Internal HTTP (non-public)
                   ▼
┌─────────────────────────────────────────────────────────┐
│                      server-ai                          │
│              Python 3.10 + FastAPI + Pydantic           │
│         Adapter Pattern → Gemini / Local                │
└─────────────────────────────────────────────────────────┘
                   │
        ┌──────────┴───────────┐
        ▼                      ▼
   Cloud Providers        Local Providers
   Gemini                 Ollama / LM Studio
                          (OpenAI-compatible)
```

## API Key & Provider Flow

The user selects their AI provider and supplies their API key from the **React Frontend**. This key is held in memory inside Zustand state (never persisted to LocalStorage or cookies). When the user triggers an analysis, the request is:

1. Sent from **React** to **server-core** as a JSON payload containing `{ provider, apiKey, ... }`.
2. **server-core** validates the request, attaches the screen/project metadata, stores the analysis run record in PostgreSQL (without the key), and forwards the payload to **server-ai**.
3. **server-ai** receives `{ provider, apiKey, imageUrl, context }` and routes it through the appropriate adapter class.
4. The analysis result is returned back through the chain: **server-ai → server-core → React**.

The API key must never be persisted to any database or log file at any point in the chain.

## Service Boundaries

### client/
- Pure presentation layer.
- Owns: UI state, canvas viewport, user preferences, in-memory API keys/provider selection.
- Does NOT own: persistence, AI logic, business rules.

### server-core/
- The single authoritative source for business logic and persistence.
- Owns: Projects, Screens, AI Reports, Annotations in PostgreSQL.
- Acts as a transparent proxy to server-ai for AI analysis requests, enriching them with project context.
- Enforces access control and request validation before forwarding.

### server-ai/
- Stateless AI bridge. Has no database of its own.
- Owns: Provider adapters, prompt engineering, response normalization.
- Receives per-request `apiKey` and `provider`. Never reads keys from environment for production requests (env vars are used only as development defaults).

## Monorepo Layout

```
OpenDesign/
├── client/                  # React 18 Vite app
├── server-core/             # Java 21 Spring Boot app
├── server-ai/               # Python 3.10 FastAPI app
├── docs/
│   ├── ADR/                 # Architecture Decision Records
│   ├── architecture/        # Deeper architecture diagrams
│   └── getting-started/     # Developer onboarding
├── .agents/
│   └── steering/            # AI assistant instructions (this directory)
├── .github/
│   └── workflows/           # CI/CD pipelines
├── docker-compose.yml       # Full local stack
└── .env.example             # Environment variable template
```

## Data Model

```
Project
  id            UUID PK
  name          VARCHAR
  description   TEXT
  owner_id      UUID
  created_at    TIMESTAMP

Screen
  id            UUID PK
  project_id    UUID FK → Project
  version_tag   VARCHAR
  image_url     TEXT
  canvas_x      FLOAT
  canvas_y      FLOAT
  canvas_scale  FLOAT
  created_at    TIMESTAMP

AI_Report
  id            UUID PK
  screen_id     UUID FK → Screen
  provider_name VARCHAR      -- e.g. "GEMINI", "LOCAL"
  model_version VARCHAR      -- e.g. "gemini-1.5-pro"
  raw_response  JSONB
  created_at    TIMESTAMP

Annotation
  id            UUID PK
  report_id     UUID FK → AI_Report
  canvas_x      FLOAT
  canvas_y      FLOAT
  issue         TEXT
  severity      VARCHAR      -- "LOW", "MEDIUM", "HIGH", "CRITICAL"
```

## Technology Versions (Pinned)

| Layer      | Technology          | Version  |
|------------|---------------------|----------|
| Frontend   | React               | 19.x     |
| Frontend   | Vite                | 8.x      |
| Frontend   | Tailwind CSS        | 4.x      |
| Frontend   | React-Konva         | 19.x     |
| Frontend   | Zustand             | 5.x      |
| Backend    | Java                | 21       |
| Backend    | Spring Boot         | 3.3.x    |
| Backend    | PostgreSQL          | 15.x     |
| AI Bridge  | Python              | 3.10+    |
| AI Bridge  | FastAPI             | 0.111.x  |
| AI Bridge  | Pydantic            | 2.x      |
| Runtime    | Docker Compose      | v2       |
