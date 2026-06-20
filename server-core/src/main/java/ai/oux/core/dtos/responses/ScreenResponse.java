package ai.oux.core.dtos.responses;

import ai.oux.core.dtos.AnnotationDto;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record ScreenResponse(
    UUID id,
    UUID projectId,
    String versionTag,
    String imageUrl,
    double canvasX,
    double canvasY,
    double canvasScale,
    Instant createdAt,
    List<AnnotationDto> annotations
) {}
