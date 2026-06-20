package ai.oux.core.controllers;

import ai.oux.core.dtos.ApiResponse;
import ai.oux.core.dtos.requests.CreateProjectRequest;
import ai.oux.core.dtos.responses.ProjectResponse;
import ai.oux.core.services.ProjectService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/projects")
@CrossOrigin
public class ProjectController {

    private final ProjectService projectService;

    public ProjectController(ProjectService projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ApiResponse<ProjectResponse> createProject(@RequestBody CreateProjectRequest request) {
        return ApiResponse.ok(projectService.createProject(request));
    }

    @GetMapping
    public ApiResponse<List<ProjectResponse>> getAllProjects() {
        return ApiResponse.ok(projectService.getAllProjects());
    }

    @GetMapping("/{id}")
    public ApiResponse<ProjectResponse> getProject(@PathVariable UUID id) {
        return ApiResponse.ok(projectService.getProject(id));
    }

    @PutMapping("/{id}/rename")
    public ApiResponse<ProjectResponse> renameProject(@PathVariable UUID id, @RequestParam String name) {
        return ApiResponse.ok(projectService.renameProject(id, name));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteProject(@PathVariable UUID id) {
        projectService.deleteProject(id);
        return ApiResponse.ok(null);
    }
}
