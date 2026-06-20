package ai.oux.core.dtos.responses;

import ai.oux.core.dtos.AnnotationDto;
import java.util.List;
import java.util.UUID;

public record AnalysisResponse(
    UUID reportId,
    List<AnnotationDto> annotations
) {}
