package ai.oux.core.services;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import ai.oux.core.clients.AIBridgeClient;
import ai.oux.core.clients.BridgeAnalysisRequest;
import ai.oux.core.clients.BridgeAnalysisResponse;
import ai.oux.core.dtos.AnnotationDto;
import ai.oux.core.dtos.requests.AnalysisRequest;
import ai.oux.core.dtos.responses.AnalysisResponse;
import ai.oux.core.entities.AiReport;
import ai.oux.core.entities.Project;
import ai.oux.core.entities.Screen;
import ai.oux.core.exceptions.ResourceNotFoundException;
import ai.oux.core.repositories.AiReportRepository;
import ai.oux.core.repositories.ScreenRepository;
import ai.oux.core.services.impl.AnalysisServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AnalysisServiceTest {

    @Mock
    private ScreenRepository screenRepository;

    @Mock
    private AiReportRepository aiReportRepository;

    @Mock
    private AIBridgeClient aiBridgeClient;

    private final ObjectMapper objectMapper = new ObjectMapper();

    private AnalysisService analysisService;

    @BeforeEach
    public void setUp() {
        analysisService = new AnalysisServiceImpl(
            screenRepository,
            aiReportRepository,
            aiBridgeClient,
            objectMapper
        );
    }

    @Test
    public void testAnalyzeScreen_Success() {
        UUID screenId = UUID.randomUUID();

        Project project = new Project();
        project.setDescription("Test Project Description");

        Screen screen = new Screen();
        screen.setProject(project);
        screen.setVersionTag("v1");

        when(screenRepository.findById(screenId)).thenReturn(Optional.of(screen));

        BridgeAnalysisResponse bridgeResponse = new BridgeAnalysisResponse(
            "GEMINI",
            "gemini-1.5-pro",
            List.of(new AnnotationDto(0.1, 0.2, "Contrast error", "HIGH")),
            Map.of("key", "val")
        );
        when(aiBridgeClient.analyze(any(BridgeAnalysisRequest.class))).thenReturn(bridgeResponse);

        AiReport savedReport = new AiReport();
        savedReport.setAnnotations(List.of());
        when(aiReportRepository.save(any(AiReport.class))).thenReturn(savedReport);

        AnalysisRequest request = new AnalysisRequest(
            screenId,
            "GEMINI",
            "key",
            "",
            "gemini-1.5-pro",
            "imageBase64Data"
        );

        AnalysisResponse response = analysisService.analyzeScreen(request);

        assertNotNull(response);
        verify(screenRepository).findById(screenId);
        verify(aiReportRepository).save(any(AiReport.class));
    }

    @Test
    public void testAnalyzeScreen_NotFound() {
        UUID screenId = UUID.randomUUID();
        when(screenRepository.findById(screenId)).thenReturn(Optional.empty());

        AnalysisRequest request = new AnalysisRequest(
            screenId,
            "GEMINI",
            "key",
            "",
            "gemini-1.5-pro",
            "imageBase64Data"
        );

        assertThrows(ResourceNotFoundException.class, () -> {
            analysisService.analyzeScreen(request);
        });
    }
}
