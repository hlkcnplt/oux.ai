package ai.oux.core.dtos.responses;

import java.time.Instant;
import java.util.UUID;

public record ProjectResponse(
    UUID id,
    String name,
    String description,
    Instant createdAt
) {}
