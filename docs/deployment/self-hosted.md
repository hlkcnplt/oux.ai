# Self-Hosted Deployment Guide

This guide is for developers and small teams who want to host oux.ai on their own infrastructure or run it locally for evaluation.

## Prerequisites

- [Docker](https://docs.docker.com/get-docker/) (v24+)
- [Docker Compose](https://docs.docker.com/compose/install/) (v2+)
- Access to an AI Vision model (Gemini, or a local provider like Ollama)

## Setup Process

### 1. Repository Setup

Clone the repository and enter the directory:

```bash
git clone https://github.com/hlkcnplt/oux.ai.git
cd oux.ai
```

### 2. Configure Environment

Copy the example environment file:

```bash
cp .env.example .env
```

Edit the `.env` file with your specific configuration. For local use, the defaults in `.env.example` should work for the database and internal service communication.

### 3. Launch Services

Use the included `Makefile` to simplify deployment:

```bash
make up
```

This command will:
1. Build all Docker images (Client, Java Server, Python AI Bridge).
2. Start the PostgreSQL database.
3. Network all containers together.
4. Launch the application in the background.

## Architecture

In a self-hosted Docker setup, the services are mapped as follows:

| Service | Container Internal Port | Host Port |
|---------|-------------------------|-----------|
| Frontend | 80 | 5173 |
| Core API | 8080 | 8080 |
| AI API | 8000 | 8000 |
| Database | 5432 | 5432 |

## Local AI (Ollama / LM Studio)

To use oux.ai without cloud costs, you can link it to a local AI runner.

1. Ensure Ollama is running on your host machine.
2. In the `.env` file, ensure `LOCAL_AI_ENDPOINT` is set to `http://host.docker.internal:11434/v1` (this lets the Docker container talk back to your host machine).
3. Access oux.ai at `http://localhost:5173`.
4. Go to Settings and select **LOCAL** as the provider.

## Maintenance

- **View Logs:** `make logs`
- **Check Status:** `make ps`
- **Stop services:** `make down`
- **Update:** `git pull && make up`
