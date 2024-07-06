package lima.amanda.task_manager.controller;

import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.mapper.TaskMapper;
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
    public Mono<TaskResponse> create(@RequestParam("title") String title,
                                     @RequestParam("description") String description,
                                     @RequestPart(value = "attachments", required = false) Flux<FilePart> attachment) {
        return taskService.create(taskMapper.buildTaskRequest(title, description, attachment));
    }

    @GetMapping
    public Flux<TaskResponse> findActiveTasks() {
        return taskService.findActiveTasks()
                .doOnComplete(() -> LOGGER.info("M=findActiveTasks, I=Task search completed successfully"));
    }

    @GetMapping("/status")
    public Flux<TaskResponse> findTasksByStatus(@RequestParam TaskStatus status) {
        return taskService.findTasksByStatus(status)
                .doOnComplete(() -> LOGGER.info("M=findTasksByStatus, I=Task search by status completed successfully, status={}", status.name()));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return taskService.delete(id)
                .doOnSuccess(it -> LOGGER.info("M=delete, I=Task deleted successfully, id={}", id));
    }

}
