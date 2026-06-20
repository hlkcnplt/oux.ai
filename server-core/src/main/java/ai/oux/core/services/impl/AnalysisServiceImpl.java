package ai.oux.core.services.impl;

import ai.oux.core.clients.AIBridgeClient;
import ai.oux.core.clients.BridgeAnalysisRequest;
import ai.oux.core.clients.BridgeAnalysisResponse;
import ai.oux.core.dtos.AnnotationDto;
import ai.oux.core.dtos.requests.AnalysisRequest;
import ai.oux.core.dtos.responses.AnalysisResponse;
import ai.oux.core.entities.AiReport;
import ai.oux.core.entities.Annotation;
import ai.oux.core.entities.Screen;
import ai.oux.core.exceptions.ResourceNotFoundException;
import ai.oux.core.repositories.AiReportRepository;
import ai.oux.core.repositories.ScreenRepository;
import ai.oux.core.services.AnalysisService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class AnalysisServiceImpl implements AnalysisService {

    private final ScreenRepository screenRepository;
    private final AiReportRepository aiReportRepository;
    private final AIBridgeClient aiBridgeClient;
    private final ObjectMapper objectMapper;

    public AnalysisServiceImpl(
        ScreenRepository screenRepository,
        AiReportRepository aiReportRepository,
        AIBridgeClient aiBridgeClient,
        ObjectMapper objectMapper
    ) {
        this.screenRepository = screenRepository;
        this.aiReportRepository = aiReportRepository;
        this.aiBridgeClient = aiBridgeClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public AnalysisResponse analyzeScreen(AnalysisRequest request) {
        Screen screen = screenRepository.findById(request.screenId())
            .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + request.screenId()));

        String context = screen.getProject().getDescription();
        if (context == null || context.isBlank()) {
            context = "Audit of screen: " + screen.getVersionTag();
        }

        BridgeAnalysisRequest bridgeRequest = new BridgeAnalysisRequest(
            request.imageBase64(),
            context,
            request.provider(),
            request.apiKey(),
            request.localEndpoint(),
            request.modelName()
        );

        BridgeAnalysisResponse bridgeResponse = aiBridgeClient.analyze(bridgeRequest);

        AiReport report = new AiReport();
        report.setScreen(screen);
        report.setProviderName(request.provider());
        report.setModelVersion(bridgeResponse.modelVersion() != null ? bridgeResponse.modelVersion() : request.modelName());

        try {
            String jsonRaw = objectMapper.writeValueAsString(bridgeResponse.rawResponse());
            report.setRawResponse(jsonRaw);
        } catch (Exception e) {
            report.setRawResponse("{}");
        }

        List<Annotation> entityAnnotations = new ArrayList<>();
        if (bridgeResponse.annotations() != null) {
            for (AnnotationDto dto : bridgeResponse.annotations()) {
                Annotation ann = new Annotation();
                ann.setReport(report);
                ann.setCanvasX(dto.x());
                ann.setCanvasY(dto.y());
                ann.setIssue(dto.issue());
                ann.setSeverity(dto.severity());
                entityAnnotations.add(ann);
            }
        }
        report.setAnnotations(entityAnnotations);

        AiReport savedReport = aiReportRepository.save(report);

        List<AnnotationDto> responseAnnotations = savedReport.getAnnotations().stream()
            .map(a -> new AnnotationDto(a.getCanvasX(), a.getCanvasY(), a.getIssue(), a.getSeverity()))
            .collect(Collectors.toList());

        return new AnalysisResponse(savedReport.getId(), responseAnnotations);
    }
}
