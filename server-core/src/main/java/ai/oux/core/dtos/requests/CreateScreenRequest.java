package ai.oux.core.dtos.requests;

import java.util.UUID;

public record CreateScreenRequest(
    UUID projectId,
    String versionTag,
    String imageUrl,
    double canvasX,
    double canvasY,
    double canvasScale
) {}
