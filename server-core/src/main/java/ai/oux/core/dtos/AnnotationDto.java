package ai.oux.core.dtos;

public record AnnotationDto(
    double x,
    double y,
    String issue,
    String severity
) {}
