# OpenDesign — AI Bridge (server-ai)

## Stack

| Concern       | Library            | Notes                                    |
|---------------|--------------------|------------------------------------------|
| Language      | Python 3.10+       | Type hints mandatory everywhere          |
| Framework     | FastAPI 0.111.x    | Async-first, automatic OpenAPI docs      |
| Validation    | Pydantic v2        | All request/response models must use it  |
| AI SDK        | google-genai       | Used for Gemini                        |
| Env Config    | python-dotenv      | Dev defaults only, never used in prod    |
| Testing       | Pytest             | With `respx` for mocking HTTP calls      |

## Module Layout

```
server-ai/
├── main.py                  # FastAPI app instantiation and router registration
├── config.py                # Reads env vars, builds settings object
├── requirements.txt
├── Dockerfile
├── providers/
│   ├── __init__.py
│   ├── base_provider.py     # Abstract base class
│   ├── gemini_provider.py   # Gemini adapter
│   └── local_provider.py    # Ollama / LM Studio adapter (OpenAI-compatible)
├── routers/
│   └── analyze.py           # /analyze endpoint
├── schemas/
│   ├── request.py           # Pydantic request models
│   └── response.py          # Pydantic response models
└── tests/
    └── test_analyze.py
```

## Adapter Pattern Rules

### BaseAIProvider

Every provider adapter must extend `BaseAIProvider` and implement at minimum:

```python
from abc import ABC, abstractmethod
from typing import Any
from schemas.request import BridgeAnalysisRequest
from schemas.response import AnalysisResult

class BaseAIProvider(ABC):

    @abstractmethod
    async def analyze_image(
        self,
        request: BridgeAnalysisRequest,
    ) -> AnalysisResult:
        pass
```

### Provider Factory

A factory function selects the concrete provider based on the `provider` field from the incoming request. This is the only place in the codebase that contains provider-switching logic:

```python
def get_provider(provider: str) -> BaseAIProvider:
    match provider.upper():
        case "GEMINI":
            return GeminiProvider()
        case "LOCAL":
            return LocalProvider()
        case _:
            raise ValueError(f"Unknown provider: {provider}")
```

### Adding a New Provider

1. Create a new file `providers/{name}_provider.py`.
2. Implement `BaseAIProvider`.
3. Add a case branch in the factory function.
4. No other files need modification.

## Pydantic Schema Rules

- All incoming request data must be modeled as a Pydantic `BaseModel` in `schemas/request.py`.
- All outgoing data must be modeled in `schemas/response.py`.
- Use `model_config = ConfigDict(str_strip_whitespace=True)` in base models.
- Required request schema:

```python
from pydantic import BaseModel, HttpUrl, ConfigDict
from typing import Optional

class BridgeAnalysisRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    image_url: HttpUrl
    project_context: str
    provider: str
    api_key: str
    local_endpoint: Optional[str] = None
    model_name: Optional[str] = None

class AnnotationResult(BaseModel):
    x: float
    y: float
    issue: str
    severity: str  # "LOW" | "MEDIUM" | "HIGH" | "CRITICAL"

class AnalysisResult(BaseModel):
    provider_used: str
    model_version: str
    annotations: list[AnnotationResult]
    raw_response: dict
```

## API Key Handling (Critical)

- The `api_key` from the request is passed directly into the provider adapter's SDK call at runtime.
- It must never be written to disk, printed to stdout or stderr, or included in any log output.
- If `api_key` is empty and the env var default is also empty, raise HTTP 422 (Unprocessable Entity) with a clear message.
- Dev default fallback order: `request.api_key` → environment variable → raise error.

## Prompt Engineering Rules

- Prompts are stored as module-level constants or in a dedicated `prompts/` directory.
- Never hardcode prompts inside provider adapter methods.
- The system prompt must always include the `project_context` passed from `server-core` so the AI understands the domain of the application being audited.
- Required UX heuristics to evaluate (included in base system prompt):
  1. Visibility of system status
  2. Match between system and real world
  3. User control and freedom
  4. Consistency and standards
  5. Error prevention
  6. Recognition rather than recall
  7. Flexibility and efficiency of use
  8. Aesthetic and minimalist design
  9. Help users recognize, diagnose, and recover from errors
  10. Help and documentation

## Fallback & Error Handling

- If the primary provider's API call fails (network error, invalid key, rate limit), the adapter must raise a `ProviderException` with a structured message.
- `ProviderException` is mapped by FastAPI's exception handler to HTTP 502.
- The fallback mechanism is intentionally left to the **client side**: if the server returns 502, the React UI shows the user a prompt to switch providers.

## Async Rules

- All route handlers and provider adapter methods must be `async def`.
- Use `httpx.AsyncClient` for any outbound HTTP calls inside adapters (not the `requests` library).
