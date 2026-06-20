package ai.oux.core.dtos.requests;

import java.util.UUID;

public record AnalysisRequest(
    UUID screenId,
    String provider,
    String apiKey,
    String localEndpoint,
    String modelName,
    String imageBase64
) {}
