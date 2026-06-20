# oux.ai

**Hybrid AI-Agnostic UI/UX Auditing Platform**

oux.ai is a powerful, open-source tool built to help designers and developers refine user interfaces. It provides a Figma-like infinite canvas where users can upload application screens, and it leverages AI to analyze these screens based on UX heuristics. 

It supports various AI backends to fit your workflow and privacy needs: Cloud models like Google Gemini, or Local models using Ollama/LM Studio.

## Architecture & Tech Stack

This repository is a polyglot monorepo containing three distinct services:

1. **Client (`/client`)**
   - React 18+ leveraging Vite.
   - UI styled with Tailwind CSS.
   - Figma-like map managed by React-Konva.
   - Global state driven by Zustand.

2. **Server-Core (`/server-core`)**
   - Java 21 powered by Spring Boot 3.3+.
   - Robust orchestration and persistent metadata storage inside PostgreSQL.

3. **Server-AI Bridge (`/server-ai`)**
   - Python 3.10+ using FastAPI.
   - Implements abstract adapters to connect seamlessly with diverse AI Vision models.

## Deployment Options

oux.ai is designed for both personal/community use and production-grade hosting.

### 1. Community / Self-Hosted (Quickstart)

Ideal for individuals running the stack on their local machine or a private server.

```bash
# 1. Clone & enter repository
git clone https://github.com/hlkcnplt/oux.ai.git
cd oux.ai

# 2. Configure environment
cp .env.example .env

# 3. Start stack
make up
```

Access at: `http://localhost:5173`

### 2. Production / Custom Domain

Designed for multi-user SaaS deployments behind a reverse proxy (e.g., Cloudflare, Nginx). Includes a root-level gateway to handle routing.

```bash
# 1. Prepare production environment
cp .env.example .env.prod
# Edit .env.prod with your domain and secure passwords

# 2. Start production stack
make prod-up
```

*For more details see: [Production Deployment Guide](docs/deployment/production.md)*

## Detailed Documentation

- [Getting Started](docs/getting-started/index.md)
- [Self-Hosted Setup](docs/deployment/self-hosted.md)
- [Production Setup](docs/deployment/production.md)

## Contributing

We welcome community contributions! Please view our [CONTRIBUTING.md](CONTRIBUTING.md) to understand our branching strategy and code standards.
