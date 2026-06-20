package ai.oux.core.controllers;

import ai.oux.core.dtos.ApiResponse;
import ai.oux.core.dtos.requests.CreateScreenRequest;
import ai.oux.core.dtos.responses.ScreenResponse;
import ai.oux.core.services.ScreenService;
import java.util.List;
import java.util.UUID;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/screens")
@CrossOrigin
public class ScreenController {

    private final ScreenService screenService;

    public ScreenController(ScreenService screenService) {
        this.screenService = screenService;
    }

    @PostMapping
    public ApiResponse<ScreenResponse> createScreen(@RequestBody CreateScreenRequest request) {
        return ApiResponse.ok(screenService.createScreen(request));
    }

    @GetMapping("/project/{projectId}")
    public ApiResponse<List<ScreenResponse>> getScreensByProjectId(@PathVariable UUID projectId) {
        return ApiResponse.ok(screenService.getScreensByProjectId(projectId));
    }

    @GetMapping("/{id}")
    public ApiResponse<ScreenResponse> getScreen(@PathVariable UUID id) {
        return ApiResponse.ok(screenService.getScreen(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> deleteScreen(@PathVariable UUID id) {
        screenService.deleteScreen(id);
        return ApiResponse.ok(null);
    }
}
