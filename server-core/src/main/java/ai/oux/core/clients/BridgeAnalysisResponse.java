package ai.oux.core.clients;

import ai.oux.core.dtos.AnnotationDto;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Map;

public record BridgeAnalysisResponse(
    @JsonProperty("provider_used") String providerUsed,
    @JsonProperty("model_version") String modelVersion,
    List<AnnotationDto> annotations,
    @JsonProperty("raw_response") Map<String, Object> rawResponse
) {}
