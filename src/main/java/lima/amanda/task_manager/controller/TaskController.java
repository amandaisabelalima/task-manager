package lima.amanda.task_manager.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.mapper.TaskMapper;
import lima.amanda.task_manager.domain.request.TaskUpdateRequest;
import lima.amanda.task_manager.domain.response.TaskResponse;
import lima.amanda.task_manager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    private final TaskService taskService;

    private final TaskMapper taskMapper;

    public TaskController(final TaskService taskService, final TaskMapper taskMapper) {
        this.taskService = taskService;
        this.taskMapper = taskMapper;
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new task with attachments")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input")
    })
    public Mono<TaskResponse> create(@RequestParam("title") String title,
                                     @RequestParam(value = "description", required = false) String description,
                                     @RequestPart(value = "attachments", required = false) Flux<FilePart> attachment) {
        return taskService.create(taskMapper.buildTaskRequest(title, description, attachment));
    }

    @GetMapping
    @Operation(summary = "Search all created tasks")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Tasks not found")
    })
    public Flux<TaskResponse> findActiveTasks() {
        return taskService.findActiveTasks()
                .doOnComplete(() -> LOGGER.info("M=findActiveTasks, I=Tasks search completed successfully"));
    }

    @GetMapping("/status")
    @Operation(summary = "Search tasks by a specific status: COMPLETED or NOT_COMPLETED")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tasks found",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Tasks not found")
    })
    public Flux<TaskResponse> findTasksByStatus(@RequestParam TaskStatus status) {
        return taskService.findTasksByStatus(status)
                .doOnComplete(() -> LOGGER.info("M=findTasksByStatus, I=Task search by status completed successfully, status={}", status.name()));
    }

    @PutMapping
    @Operation(summary = "To change the data of a task. Possible change: title, description and status")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Task updated",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = TaskResponse.class)) }),
            @ApiResponse(responseCode = "404", description = "Tasks not found")
    })
    public Mono<TaskResponse> update(@Valid @RequestBody TaskUpdateRequest taskUpdateRequest) {
        return taskService.update(taskUpdateRequest);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete a task based on an id")
    @Parameter(name = "id", description = "Task id", required = true)
    public Mono<Void> delete(@PathVariable String id) {
        return taskService.delete(id)
                .doOnSuccess(it -> LOGGER.info("M=delete, I=Task deleted successfully, id={}", id));
    }

}
