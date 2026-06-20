import pytest
from fastapi.testclient import TestClient
from main import app
from providers import get_provider

client = TestClient(app)

def test_health_check() -> None:
    response = client.get("/health")
    assert response.status_code == 200
    assert response.json() == {"status": "ok"}

def test_get_provider_validation() -> None:
    with pytest.raises(ValueError):
        get_provider("UNKNOWN")

def test_get_provider_valid() -> None:
    from providers.gemini_provider import GeminiProvider
    from providers.local_provider import LocalProvider

    assert isinstance(get_provider("GEMINI"), GeminiProvider)
    assert isinstance(get_provider("LOCAL"), LocalProvider)

def test_missing_api_key_gemini() -> None:
    payload = {
        "image_url": "iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNk+M9QDwADhgGAWjR9awAAAABJRU5ErkJggg==",
        "project_context": "Test Context",
        "provider": "GEMINI",
        "api_key": ""
    }
    response = client.post("/analyze", json=payload)
    assert response.status_code == 422

    # None value API key
    payload["api_key"] = None
    response = client.post("/analyze", json=payload)
    assert response.status_code == 422

    # Omitted API key
    del payload["api_key"]
    response = client.post("/analyze", json=payload)
    assert response.status_code == 422
