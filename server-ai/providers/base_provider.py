from abc import ABC, abstractmethod
from schemas.request import BridgeAnalysisRequest
from schemas.response import AnalysisResult

class BaseAIProvider(ABC):

    @abstractmethod
    async def analyze_image(
        self,
        request: BridgeAnalysisRequest,
    ) -> AnalysisResult:
        pass
