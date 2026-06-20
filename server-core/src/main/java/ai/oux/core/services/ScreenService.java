package ai.oux.core.services;

import ai.oux.core.dtos.requests.CreateScreenRequest;
import ai.oux.core.dtos.responses.ScreenResponse;
import java.util.List;
import java.util.UUID;

public interface ScreenService {

    /**
     * Creates a new screen for a project.
     *
     * @param request the create screen request DTO
     * @return the created screen response DTO
     */
    ScreenResponse createScreen(CreateScreenRequest request);

    /**
     * Retrieves all screens for a specific project.
     *
     * @param projectId the project UUID
     * @return the list of screens belonging to the project
     */
    List<ScreenResponse> getScreensByProjectId(UUID projectId);

    /**
     * Retrieves a screen by ID.
     *
     * @param id the screen UUID
     * @return the screen response DTO
     */
    ScreenResponse getScreen(UUID id);

    /**
     * Deletes a screen by ID.
     *
     * @param id the screen UUID
     */
    void deleteScreen(UUID id);
}
