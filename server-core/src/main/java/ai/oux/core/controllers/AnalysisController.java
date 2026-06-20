package ai.oux.core.controllers;

import ai.oux.core.dtos.ApiResponse;
import ai.oux.core.dtos.requests.AnalysisRequest;
import ai.oux.core.dtos.responses.AnalysisResponse;
import ai.oux.core.services.AnalysisService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/analysis")
@CrossOrigin
public class AnalysisController {

    private final AnalysisService analysisService;

    public AnalysisController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @PostMapping
    public ApiResponse<AnalysisResponse> analyzeScreen(@RequestBody AnalysisRequest request) {
        return ApiResponse.ok(analysisService.analyzeScreen(request));
    }
}
