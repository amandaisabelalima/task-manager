package lima.amanda.task_manager.service;

import lima.amanda.task_manager.domain.entity.TaskEntity;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.mapper.TaskMapper;
import lima.amanda.task_manager.domain.request.TaskRequest;
import lima.amanda.task_manager.exceptions.TaskNotFoundException;
import lima.amanda.task_manager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    TaskMapper taskMapper;

    //TODO ajustar os métodos para nao devolver a Entity para o controler. O mapper que está no controller joga pra cá!!!!!

    public Mono<TaskEntity> create(final TaskRequest task) {
        return Mono.just(task)
                .map(m -> taskMapper.buildTaskEntity(task)) //mapper
                .flatMap(this::save)
                .doOnError(error -> LOGGER.error("M=create, E=Erro ao criar a task, title={}", task.getTitle(), error));
    }

    private Mono<TaskEntity> save(final TaskEntity task) {
        return Mono.just(task)
                .doOnNext(it -> LOGGER.info("M=save, I=Iniciando a criacao da task, title={}", it.getTitle()))
                .flatMap(taskRepository::save);
    }

    public Flux<TaskEntity> findActiveTasks() {
        return taskRepository.findByIndActiveTrue()
                .doOnError(error -> LOGGER.error("M=findActiveTasks, E=Erro ao buscar as tasks na base", error));
    }

    public Flux<TaskEntity> findTasksByStatus(final TaskStatus status) {
        return taskRepository.findTasksByIndActiveTrueAndStatus(status)
                .doOnError(error -> LOGGER.error("M=findTasksByStatus, E=Erro ao buscar tasks por status"));
    }

    public Mono<Void> delete(final String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)))
                .flatMap(task -> {
                    task.setIndActive(false);
                    task.setDatUpdate(LocalDateTime.now());
                    return taskRepository.save(task);
                })
                .doOnError(error -> LOGGER.error("M=delete, E=Erro ao deletar a task, id={}", id, error))
                .then();
    }
}
