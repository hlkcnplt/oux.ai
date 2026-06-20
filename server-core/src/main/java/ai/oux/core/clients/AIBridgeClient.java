package ai.oux.core.clients;

import ai.oux.core.exceptions.AIBridgeException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class AIBridgeClient {

    private final RestClient restClient;

    public AIBridgeClient(
        RestClient.Builder restClientBuilder,
        @Value("${oux.ai-service.url:http://localhost:8000}") String baseUrl
    ) {
        this.restClient = restClientBuilder.baseUrl(baseUrl).build();
    }

    public BridgeAnalysisResponse analyze(BridgeAnalysisRequest request) {
        try {
            return restClient.post()
                .uri("/analyze")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .onStatus(HttpStatusCode::isError, (req, res) -> {
                    throw new AIBridgeException("AI Bridge returned status " + res.getStatusCode());
                })
                .body(BridgeAnalysisResponse.class);
        } catch (AIBridgeException e) {
            throw e;
        } catch (Exception e) {
            throw new AIBridgeException("Failed to contact AI Bridge: " + e.getMessage());
        }
    }
}
