import json
import httpx
from typing import Any
from providers.base_provider import BaseAIProvider
from schemas.request import BridgeAnalysisRequest
from schemas.response import AnalysisResult, AnnotationResult
from prompts.system import SYSTEM_PROMPT
from exceptions import ProviderException

class LocalProvider(BaseAIProvider):

    async def analyze_image(
        self,
        request: BridgeAnalysisRequest,
    ) -> AnalysisResult:
        api_key = request.api_key or "local"
        base_url = request.local_endpoint or "http://localhost:11434/v1"
        model_version = request.model_name or "local-model"

        image_url = request.image_url
        if not image_url.startswith("http") and not image_url.startswith("data:"):
            image_url = f"data:image/png;base64,{image_url}"

        try:
            formatted_prompt = SYSTEM_PROMPT.format(
                project_context=request.project_context
            )
            
            headers = {
                "Authorization": f"Bearer {api_key}",
                "Content-Type": "application/json"
            }
            
            payload = {
                "model": model_version,
                "messages": [
                    {
                        "role": "system",
                        "content": formatted_prompt
                    },
                    {
                        "role": "user",
                        "content": [
                            {
                                "type": "image_url",
                                "image_url": {
                                    "url": image_url
                                }
                            }
                        ]
                    }
                ],
                "response_format": {"type": "json_object"}
            }

            async with httpx.AsyncClient(base_url=base_url) as client:
                response = await client.post(
                    "/chat/completions",
                    headers=headers,
                    json=payload,
                    timeout=120.0
                )
                response.raise_for_status()
                response_data = response.json()

            content = response_data.get("choices", [{}])[0].get("message", {}).get("content")
            if not content:
                raise ProviderException("Empty response from local API.")

            content_clean = content.strip()
            if content_clean.startswith("```"):
                lines = content_clean.splitlines()
                if lines[0].startswith("```"):
                    lines = lines[1:]
                if lines and lines[-1].strip() == "```":
                    lines = lines[:-1]
                content_clean = "\n".join(lines).strip()

            data = json.loads(content_clean)
            raw_annotations = data.get("annotations", [])
            annotations = []

            for ann in raw_annotations:
                annotations.append(
                    AnnotationResult(
                        x=float(ann.get("x", 0.0)),
                        y=float(ann.get("y", 0.0)),
                        issue=str(ann.get("issue", "")),
                        severity=str(ann.get("severity", "MEDIUM"))
                    )
                )

            return AnalysisResult(
                provider_used="LOCAL",
                model_version=model_version,
                annotations=annotations,
                raw_response=data
            )

        except Exception as e:
            raise ProviderException(f"Local API analysis failed: {str(e)}")
