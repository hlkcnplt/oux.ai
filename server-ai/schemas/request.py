from pydantic import BaseModel, ConfigDict
from typing import Optional

class BridgeAnalysisRequest(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)

    image_url: str
    project_context: str
    provider: str
    api_key: Optional[str] = None
    local_endpoint: Optional[str] = None
    model_name: Optional[str] = None
