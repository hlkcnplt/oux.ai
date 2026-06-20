import base64
import json
from fastapi import HTTPException
from google import genai
from google.genai import types
from providers.base_provider import BaseAIProvider
from schemas.request import BridgeAnalysisRequest
from schemas.response import AnalysisResult, AnnotationResult
from prompts.system import SYSTEM_PROMPT
from config import settings
from exceptions import ProviderException
from pydantic import BaseModel, Field

class GeminiAnnotationList(BaseModel):
    annotations: list[AnnotationResult] = Field(description="List of UI/UX annotations.")

class GeminiProvider(BaseAIProvider):

    async def analyze_image(
        self,
        request: BridgeAnalysisRequest,
    ) -> AnalysisResult:
        api_key = request.api_key or settings.gemini_api_key
        if not api_key:
            raise HTTPException(
                status_code=422,
                detail="API Key is missing for Gemini provider."
            )

        image_url = request.image_url
        if image_url.startswith("data:image/"):
            _, base64_data = image_url.split(",", 1)
        else:
            base64_data = image_url

        try:
            image_bytes = base64.b64decode(base64_data)
        except Exception as e:
            raise ProviderException(f"Failed to decode base64 image: {str(e)}")

        model_version = request.model_name or "gemini-2.5-flash"

        client = genai.Client(api_key=api_key)
        try:
            formatted_prompt = SYSTEM_PROMPT.format(
                project_context=request.project_context
            )

            response = await client.aio.models.generate_content(
                model=model_version,
                contents=[
                    types.Part.from_bytes(
                        data=image_bytes,
                        mime_type="image/png"
                    ),
                    formatted_prompt
                ],
                config=types.GenerateContentConfig(
                    response_mime_type="application/json",
                    response_schema=GeminiAnnotationList.model_json_schema()
                )
            )

            content = response.text
            if not content:
                raise ProviderException("Empty response from Gemini API.")

            try:
                parsed_data = GeminiAnnotationList.model_validate_json(content)
                data = json.loads(content)
            except Exception as e:
                raise ProviderException(f"JSON decode error: {e}. Raw: {content}")

            return AnalysisResult(
                provider_used="GEMINI",
                model_version=model_version,
                annotations=parsed_data.annotations,
                raw_response=data
            )

        except Exception as e:
            if isinstance(e, HTTPException):
                raise e
            raise ProviderException(f"Gemini API analysis failed: {str(e)}")
