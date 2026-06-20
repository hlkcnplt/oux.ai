# OpenDesign — Testing

## Philosophy

Every service layer in the monorepo has its own isolated testing strategy. Tests are a first-class citizen; features are not considered complete without passing test coverage.

## Frontend (client/)

### Tools

| Tool                    | Purpose                              |
|-------------------------|--------------------------------------|
| Vitest                  | Unit and component test runner       |
| React Testing Library   | Component rendering and interaction  |
| MSW (Mock Service Worker) | Mocking API requests in tests    |

### Test File Location

Tests live adjacent to the code they test:
```
src/hooks/useAIAnalysis.ts
src/hooks/useAIAnalysis.test.ts

src/components/panels/ProviderSettings.tsx
src/components/panels/ProviderSettings.test.tsx
```

### What to Test

- Custom hooks: test the state transitions, not the React internals.
- Provider settings component: test that changing the dropdown updates the Zustand store, and that the API key field is masked.
- Canvas interactions: test that screens are added to the store on drag-and-drop.
- API client wrappers: test against MSW-mocked endpoints.

### Coverage Target

- Minimum 70% line coverage for `hooks/` and `api/`.
- Canvas and pure presentational components are exempt from coverage requirements.

### Running Tests

```bash
cd client
npm run test          # vitest in watch mode
npm run test:coverage # vitest with coverage report
```

---

## Backend (server-core/)

### Tools

| Tool              | Purpose                                  |
|-------------------|------------------------------------------|
| JUnit 5           | Test framework                           |
| Mockito           | Service and repository mocking           |
| Testcontainers    | Real PostgreSQL for integration tests    |
| MockMvc           | Controller layer HTTP testing            |

### Test File Location

Mirrors `main/` inside `test/`:
```
src/main/java/com/opendesign/core/services/AnalysisService.java
src/test/java/com/opendesign/core/services/AnalysisServiceTest.java
```

### Test Layers

**Unit Tests** — Services only, repositories mocked with Mockito:
- Test service method contracts (happy path + error cases).
- Must not spin up a Spring context. Use `@ExtendWith(MockitoExtension.class)`.

**Integration Tests** — Full Spring context with Testcontainers PostgreSQL:
- Live in a dedicated `integration/` sub-package.
- Annotated with `@SpringBootTest` and `@Testcontainers`.
- Test repository queries and controller endpoints end-to-end.

**Controller Tests** — Using `@WebMvcTest` + MockMvc:
- Validate request/response shapes and HTTP status codes.
- Mock the service layer.

### Coverage Target

- Minimum 80% line coverage for `services/` packages.
- Controllers require at minimum a happy-path and validation-failure test per endpoint.

### Running Tests

```bash
cd server-core
./mvnw test                              # all tests
./mvnw test -Dtest=AnalysisServiceTest   # single class
./mvnw verify                            # includes integration tests
```

---

## AI Bridge (server-ai/)

### Tools

| Tool       | Purpose                                         |
|------------|-------------------------------------------------|
| Pytest     | Test runner                                     |
| respx      | Mocking async HTTP calls (httpx-compatible)     |
| pytest-asyncio | Async test support                          |

### Test File Location

```
server-ai/tests/
├── test_analyze.py        # /analyze endpoint integration
├── test_gemini_provider.py
└── test_local_provider.py
```

### What to Test

- Provider adapters: mock the outbound HTTP call with `respx`. Verify the request shape sent to the AI vendor and the parsing of their response.
- Factory function: verify correct adapter is selected based on `provider` string and that unknown values raise `ValueError`.
- `/analyze` endpoint: test with a full `BridgeAnalysisRequest` payload. Use `httpx.AsyncClient` with `app` from `main.py` directly (no real server needed).
- API key omission: verify HTTP 422 is returned when `api_key` is empty.

### Running Tests

```bash
cd server-ai
pytest                          # all tests
pytest -v tests/test_analyze.py # specific file
pytest --cov=. --cov-report=html
```

---

## End-to-End Tests (Future)

A `e2e/` directory at the repo root is reserved for Playwright tests that drive the full stack (client + server-core + server-ai + postgres) against Docker Compose.

Structure (when implemented):
```
e2e/
├── tests/
│   ├── canvas.spec.ts
│   └── analysis.spec.ts
├── playwright.config.ts
└── package.json
```

---

## CI / Test Execution Environments

Tests are run in GitHub Actions on every push and pull request to `main`. See `.github/workflows/ci.yml`.

- Frontend tests must pass before merge.
- Backend tests must pass before merge. Integration tests run only on `main` branch pushes.
- Python tests must pass before merge.
- No `@Disabled` or `pytest.mark.skip` may be merged to `main` without a linked issue comment explaining why.
