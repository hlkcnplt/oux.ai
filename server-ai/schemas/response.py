from pydantic import BaseModel, ConfigDict

class AnnotationResult(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)
    x: float
    y: float
    issue: str
    severity: str

class AnalysisResult(BaseModel):
    model_config = ConfigDict(str_strip_whitespace=True)
    provider_used: str
    model_version: str
    annotations: list[AnnotationResult]
    raw_response: dict
