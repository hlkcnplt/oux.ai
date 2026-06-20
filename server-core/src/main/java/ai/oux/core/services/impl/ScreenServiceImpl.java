package ai.oux.core.services.impl;

import ai.oux.core.dtos.AnnotationDto;
import ai.oux.core.dtos.requests.CreateScreenRequest;
import ai.oux.core.dtos.responses.ScreenResponse;
import ai.oux.core.entities.AiReport;
import ai.oux.core.entities.Project;
import ai.oux.core.entities.Screen;
import ai.oux.core.exceptions.ResourceNotFoundException;
import ai.oux.core.repositories.ProjectRepository;
import ai.oux.core.repositories.ScreenRepository;
import ai.oux.core.services.ScreenService;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ScreenServiceImpl implements ScreenService {

    private final ScreenRepository screenRepository;
    private final ProjectRepository projectRepository;

    public ScreenServiceImpl(ScreenRepository screenRepository, ProjectRepository projectRepository) {
        this.screenRepository = screenRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public ScreenResponse createScreen(CreateScreenRequest request) {
        Project project = projectRepository.findById(request.projectId())
            .orElseThrow(() -> new ResourceNotFoundException("Project not found with id: " + request.projectId()));

        Screen screen = new Screen();
        screen.setProject(project);
        screen.setVersionTag(request.versionTag());
        screen.setImageUrl(request.imageUrl());
        screen.setCanvasX(request.canvasX());
        screen.setCanvasY(request.canvasY());
        screen.setCanvasScale(request.canvasScale());

        Screen saved = screenRepository.save(screen);
        return toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScreenResponse> getScreensByProjectId(UUID projectId) {
        if (!projectRepository.existsById(projectId)) {
            throw new ResourceNotFoundException("Project not found with id: " + projectId);
        }
        return screenRepository.findByProject_Id(projectId).stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ScreenResponse getScreen(UUID id) {
        Screen screen = screenRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Screen not found with id: " + id));
        return toResponse(screen);
    }

    @Override
    public void deleteScreen(UUID id) {
        if (!screenRepository.existsById(id)) {
            throw new ResourceNotFoundException("Screen not found with id: " + id);
        }
        screenRepository.deleteById(id);
    }

    private ScreenResponse toResponse(Screen screen) {
        List<AnnotationDto> annotations = List.of();
        if (screen.getReports() != null && !screen.getReports().isEmpty()) {
            AiReport latestReport = screen.getReports().stream()
                .max((r1, r2) -> r1.getCreatedAt().compareTo(r2.getCreatedAt()))
                .orElse(null);
            if (latestReport != null && latestReport.getAnnotations() != null) {
                annotations = latestReport.getAnnotations().stream()
                    .map(a -> new AnnotationDto(a.getCanvasX(), a.getCanvasY(), a.getIssue(), a.getSeverity()))
                    .collect(Collectors.toList());
            }
        }

        return new ScreenResponse(
            screen.getId(),
            screen.getProjectId(),
            screen.getVersionTag(),
            screen.getImageUrl(),
            screen.getCanvasX(),
            screen.getCanvasY(),
            screen.getCanvasScale(),
            screen.getCreatedAt(),
            annotations
        );
    }
}
