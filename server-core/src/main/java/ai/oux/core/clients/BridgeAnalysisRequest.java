package ai.oux.core.clients;

import com.fasterxml.jackson.annotation.JsonProperty;

public record BridgeAnalysisRequest(
    @JsonProperty("image_url") String imageUrl,
    @JsonProperty("project_context") String projectContext,
    String provider,
    @JsonProperty("api_key") String apiKey,
    @JsonProperty("local_endpoint") String localEndpoint,
    @JsonProperty("model_name") String modelName
) {}
