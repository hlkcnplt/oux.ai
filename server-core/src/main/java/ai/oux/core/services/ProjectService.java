package ai.oux.core.services;

import ai.oux.core.dtos.requests.CreateProjectRequest;
import ai.oux.core.dtos.responses.ProjectResponse;
import java.util.List;
import java.util.UUID;

public interface ProjectService {

    /**
     * Creates a new project.
     *
     * @param request the create project request DTO
     * @return the created project response DTO
     */
    ProjectResponse createProject(CreateProjectRequest request);

    /**
     * Retrieves all projects.
     *
     * @return the list of projects
     */
    List<ProjectResponse> getAllProjects();

    /**
     * Retrieves a project by ID.
     *
     * @param id the project UUID
     * @return the project response DTO
     */
    ProjectResponse getProject(UUID id);

    /**
     * Renames an existing project.
     *
     * @param id the project UUID
     * @param name the new project name
     * @return the updated project response DTO
     */
    ProjectResponse renameProject(UUID id, String name);

    /**
     * Deletes a project by ID.
     *
     * @param id the project UUID
     */
    void deleteProject(UUID id);
}
