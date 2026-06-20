# OpenDesign — Backend (server-core)

## Stack

| Concern        | Technology           | Notes                            |
|----------------|----------------------|----------------------------------|
| Language       | Java 21              | Records, sealed classes, pattern matching |
| Framework      | Spring Boot 3.3.x    | Auto-config, actuator, validation|
| Persistence    | Spring Data JPA      | Hibernate 6.x                    |
| Database       | PostgreSQL 15        | JSONB for raw AI responses       |
| Build          | Maven (Wrapper)      | Always use `./mvnw`, never system Maven |
| Testing        | JUnit 5 + Mockito    | Testcontainers for DB integration tests |

## Package Structure

```
com.opendesign.core/
├── config/             # Spring @Configuration classes
├── controllers/        # REST controllers (@RestController)
├── dtos/               # Java records used as request/response DTOs
├── entities/           # JPA entities (@Entity)
├── repositories/       # Spring Data interfaces
├── services/           # Business logic interfaces + impl/
│   └── impl/
├── clients/            # RestClient / WebClient wrappers for server-ai
└── exceptions/         # Custom exception hierarchy, @ControllerAdvice
```

## DTO Rules

- DTOs must be Java `record` types. No mutable classes for API boundaries.
- Request records go in `dtos/requests/`, response records go in `dtos/responses/`.
- Example:

```java
public record AnalysisRequest(
    String screenId,
    String provider,
    String apiKey,
    String localEndpoint,
    String modelName
) {}

public record AnalysisResponse(
    String reportId,
    List<AnnotationDto> annotations
) {}
```

## Service Rules

- All services must be defined as a `public interface` first, with the implementation class in `impl/`.
- Services must be provider-blind. Business logic in `server-core` must not contain any string-matching of `"GEMINI"` or `"LOCAL"`. That logic belongs exclusively in `server-ai`.
- The service responsible for triggering analysis must delegate fully to the `AIBridgeClient` without interpreting the AI response.

## API Key Handling (Critical)

- The `apiKey` field received in an `AnalysisRequest` is forwarded immediately to `server-ai` and must not be persisted to the database in any form.
- The `AI_Report` entity stores only `provider_name` and `model_version`, never the key itself.
- Never log the `apiKey` field. Add a `@JsonIgnore` / masking layer if needed for observability.

## REST Controller Rules

- Controllers must be thin. A controller method must not contain business logic; it delegates to a service.
- All endpoints must be versioned under `/api/v1/`.
- Standard response structure:

```java
public record ApiResponse<T>(T data, String error) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(data, null);
    }
    public static <T> ApiResponse<T> error(String message) {
        return new ApiResponse<>(null, message);
    }
}
```

- Validation errors must return HTTP 400. Unexpected errors must return HTTP 500 with a non-leaking message.

## Javadoc Rules

- Every `public` method in a service interface must have Javadoc. Implementation classes inherit and may add `@implNote`.
- Controllers and DTOs do not require Javadoc.

## AI Bridge Client

- `AIBridgeClient` in `clients/` wraps Spring's `RestClient` (or `WebClient` for async).
- It reads the bridge base URL from `opendesign.ai-service.url` in `application.yml`.
- It constructs the forwarded request by combining the original `AnalysisRequest` with server-side data (screen image URL, project description fetched from DB).
- Forwarded payload shape:

```java
public record BridgeAnalysisRequest(
    String imageUrl,
    String projectContext,
    String provider,
    String apiKey,
    String localEndpoint,
    String modelName
) {}
```

## Database Migration

- Pending setup: Flyway for schema versioning. Migrations go in `src/main/resources/db/migration/`.
- File naming convention: `V{version}__{description}.sql` (e.g. `V1__create_projects.sql`).
- `spring.jpa.hibernate.ddl-auto` must be `validate` in production. Use `update` only in local dev.

## Error Handling

- Central `@ControllerAdvice` class in `exceptions/` maps all exception types to structured API responses.
- Never expose stack traces in API responses.
- Custom exception hierarchy:

```
OpenDesignException (base)
  ├── ResourceNotFoundException   → 404
  ├── ValidationException         → 400
  └── AIBridgeException           → 502
```
