# Getting Started with oux.ai

## Prerequisites

| Tool           | Minimum Version | Install                          |
|----------------|-----------------|----------------------------------|
| Docker         | 24.x            | https://docs.docker.com/get-docker/ |
| Docker Compose | v2              | Bundled with Docker Desktop      |
| Node.js        | 20.x            | https://nodejs.org (for local dev) |
| Java           | 21              | https://adoptium.net (for local dev) |
| Python         | 3.10+           | https://python.org (for local dev) |

---

## Quickstart — Full Stack with Docker

```bash
# 1. Clone the repository
git clone https://github.com/hlkcnplt/oux.ai.git
cd oux.ai

# 2. Copy and configure environment variables
cp .env.example .env
# Optionally add API keys for cloud providers in .env

# 3. Start all services
docker-compose up -d --build

# 4. Open the application
open http://localhost:5173
```

The application will prompt you to configure your AI provider on first launch.

---

## Local Development Setup

For active development, you can start all services with a single command:

```bash
make dev
```

This will:
1. Ensure `.env` exists.
2. Start PostgreSQL in Docker.
3. Start the AI Bridge (Python), Core Service (Java), and Client (React) concurrently with hot-reload enabled.

Alternatively, you can run each service natively:

### 1. Start the Database

```bash
docker-compose up postgres -d
```

### 2. Start the AI Bridge

```bash
cd server-ai
python -m venv venv
source venv/bin/activate       # Windows: venv\Scripts\activate
pip install -r requirements.txt
uvicorn main:app --reload
```

### 3. Start the Java Core

```bash
cd server-core
./mvnw spring-boot:run
```

### 4. Start the React Client

```bash
cd client
npm install
npm run dev
```

---

## Configuring an AI Provider

oux.ai does not require any server-side API key configuration for provider selection. Providers are selected and keys are entered from the **Settings Panel** in the UI.

### Cloud Providers (Gemini)

1. Open the settings panel in the top-right of the application.
2. Select your provider from the dropdown.
3. Paste your API key.
4. Your key is stored only in your browser session memory and never saved.

### Local Providers (Ollama / LM Studio)

1. Ensure Ollama or LM Studio is running locally with a vision model loaded (e.g. `qwen2.5-vl`, `llava`).
2. Select `LOCAL` in the settings panel.
3. Set the endpoint to `http://localhost:11434/v1` (Ollama) or your LM Studio server address.
4. Enter the model name.

---

## Running Tests

```bash
# Frontend
cd client && npm run test

# Backend
cd server-core && ./mvnw test

# AI Bridge
cd server-ai && pytest
```
