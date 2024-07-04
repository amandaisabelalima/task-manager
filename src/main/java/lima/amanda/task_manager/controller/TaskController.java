package lima.amanda.task_manager.controller;

import jakarta.validation.Valid;
import lima.amanda.task_manager.domain.entity.TaskEntity;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.mapper.TaskMapper;
import lima.amanda.task_manager.domain.request.TaskRequest;
import lima.amanda.task_manager.domain.response.TaskResponse;
import lima.amanda.task_manager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    TaskService taskService;

    @Autowired
    TaskMapper taskMapper;

    //TODO falta ajustar mensagem de validação
    @PostMapping
    public Mono<TaskResponse> create(@RequestBody @Valid TaskRequest task) {
        return taskService.create(task)
                .doOnNext(it -> LOGGER.info("M=create, I=Task criada com sucesso, id={}", it.getId()))
                .map(taskMapper::buildTaskResponse);

    }

    @PatchMapping("/{id}/delete")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> delete(@PathVariable String id) {
        return Mono.just(id)
                .doOnNext(it -> LOGGER.info("M=delete, I=Task sera deletada, id={}", id))
                .flatMap(taskService::delete);
    }

    @GetMapping
    public Flux<TaskResponse> findActiveTasks() {
        return taskService.findActiveTasks()
                .doOnNext(it -> LOGGER.info("M=findActiveTasks, I=Consulta de tasks realizada com sucesso"))
                .map(taskMapper::buildTaskResponse);
    }

    @GetMapping("/status")
    public Flux<TaskResponse> findTasksByStatus(@RequestParam TaskStatus status) {
        return taskService.findTasksByStatus(status)
                .doOnNext(it -> LOGGER.info("M=findTasksByStatus, I=Consulta de tasks realizada com sucesso"))
                .map(taskMapper::buildTaskResponse);
    }


    //atualizar tarefa existente
    //s3

}
