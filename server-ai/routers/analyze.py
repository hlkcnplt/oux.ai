from fastapi import APIRouter, HTTPException
from schemas.request import BridgeAnalysisRequest
from schemas.response import AnalysisResult
from providers import get_provider

router = APIRouter()

@router.post("/analyze", response_model=AnalysisResult)
async def analyze_screen(request: BridgeAnalysisRequest) -> AnalysisResult:
    try:
        provider = get_provider(request.provider)
        return await provider.analyze_image(request)
    except ValueError as e:
        raise HTTPException(status_code=400, detail=str(e))
