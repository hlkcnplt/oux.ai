package ai.oux.core.services;

import ai.oux.core.dtos.requests.AnalysisRequest;
import ai.oux.core.dtos.responses.AnalysisResponse;

public interface AnalysisService {

    /**
     * Triggers AI UX heuristic analysis of a screen.
     *
     * @param request the analysis request DTO containing settings and the base64 image
     * @return the analysis response DTO containing the report and annotations
     */
    AnalysisResponse analyzeScreen(AnalysisRequest request);
}
