from providers.base_provider import BaseAIProvider
from providers.local_provider import LocalProvider
from providers.gemini_provider import GeminiProvider

def get_provider(provider_name: str) -> BaseAIProvider:
    prov_upper = provider_name.upper()
    if prov_upper == "LOCAL":
        return LocalProvider()
    elif prov_upper == "GEMINI":
        return GeminiProvider()
    else:
        raise ValueError(f"Unknown provider: {provider_name}")
