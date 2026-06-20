from fastapi import FastAPI, Request
from fastapi.responses import JSONResponse
from exceptions import ProviderException
from routers.analyze import router as analyze_router

app = FastAPI(title="oux.ai AI Bridge", version="1.0.0")

@app.exception_handler(ProviderException)
async def provider_exception_handler(request: Request, exc: ProviderException) -> JSONResponse:
    return JSONResponse(
        status_code=502,
        content={"detail": exc.message},
    )

app.include_router(analyze_router)

@app.get("/health")
async def health_check() -> dict[str, str]:
    return {"status": "ok"}
