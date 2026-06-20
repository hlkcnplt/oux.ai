---
trigger: always_on
---

# OpenDesign — AI Assistant Context

**OpenDesign** is a hybrid, AI-agnostic UI/UX auditing platform with a Figma-like infinite canvas. Users upload application screenshots, an AI model analyzes them for UX heuristics, and annotates issues directly on the canvas. Users can compare screen versions and switch between AI providers freely.

## Critical Architecture Rule

**API Key Flow:** Frontend (React) → server-core (Java) → server-ai (Python).
The user selects the AI provider and enters their API key from the UI. The key travels in the request body only, is never stored in the database, and is never logged at any layer.

## Stack (Non-Negotiable)

| Layer       | Technology                              |
|-------------|-----------------------------------------|
| Frontend    | React 19, Vite 8, Tailwind CSS v4, Zustand 5, React-Konva 19 |
| Backend     | Java 21, Spring Boot 3.3, PostgreSQL 15 |
| AI Bridge   | Python 3.10, FastAPI, Pydantic v2       |
| Deployment  | Docker Compose                          |

## Mandatory Rules (Summary)

- Java DTOs must be `record` types. Services must have interfaces. Constructor injection only.
- Python: all functions typed, Pydantic for all I/O, `async def` everywhere, `BaseAIProvider` for all adapters.
- React: all data-fetching in custom hooks, no logic in components, API key only in Zustand memory (never localStorage).
- Endpoints versioned under `/api/v1/`.
- Commit messages follow Conventional Commits format.
- No comments in code. No emojis in code or files.

## Detailed Steering Documents

Before making any significant change, read the relevant steering document from `.agents/steering/`:

| Task involves...             | Read this file first                          |
|------------------------------|-----------------------------------------------|
| System design, data flow     | `.agents/steering/architecture.md`            |
| React, UI, Zustand, hooks    | `.agents/steering/frontend.md`                |
| Java, Spring Boot, JPA       | `.agents/steering/backend.md`                 |
| Python, FastAPI, AI adapters | `.agents/steering/ai-bridge.md`               |
| Writing or fixing tests      | `.agents/steering/testing.md`                 |
| Docker, CI/CD, env vars      | `.agents/steering/infrastructure.md`          |
| Naming anything              | `.agents/steering/naming.md`                  |
| Linting, code review, security | `.agents/steering/code-quality.md`          |
| UI Design, System Spec       | `.agents/steering/ui-inventory.md`            |
