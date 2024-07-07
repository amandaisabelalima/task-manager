package lima.amanda.task_manager.service;

import lima.amanda.task_manager.domain.entity.TaskEntity;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.mapper.TaskMapper;
import lima.amanda.task_manager.domain.request.TaskRequest;
import lima.amanda.task_manager.domain.request.TaskUpdateRequest;
import lima.amanda.task_manager.domain.response.TaskResponse;
import lima.amanda.task_manager.exceptions.TaskNotFoundException;
import lima.amanda.task_manager.repository.TaskRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TaskService {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;

    private final TaskMapper taskMapper;

    private final S3Service s3Service;

    public TaskService(TaskRepository taskRepository, TaskMapper taskMapper, S3Service s3Service) {
        this.taskRepository = taskRepository;
        this.taskMapper = taskMapper;
        this.s3Service = s3Service;
    }

    public Mono<TaskResponse> create(final TaskRequest task) {
        Flux<FilePart> attachments = task.getAttachments();

        return attachments.collectList()
                .flatMap(attachmentList -> {
                    List<String> attachmentKeys = new ArrayList<>();
                    if (attachmentList.isEmpty()) { // ou size = 1 mas o value for null
                        return saveTask(task, null);
                    } else {
                        return saveAttachments(attachmentList, attachmentKeys)
                                .then(Mono.defer(() -> saveTask(task, attachmentKeys)));
                    }
                });
    }

    private Flux<String> saveAttachments(List<FilePart> attachmentList, List<String> attachmentKeys) {
        LOGGER.info("M=saveAttachments, I=Starting to save attachments");
        return Flux.fromIterable(attachmentList)
                .flatMap(filePart -> s3Service.uploadFileToS3(Flux.just(filePart))
                        .doOnNext(attachmentKeys::add));
    }

    private Flux<String> deleteAttachments(List<FilePart> attachmentList, List<String> attachmentKeys) {
        LOGGER.info("M=saveAttachments, I=Starting to save attachments");
        return Flux.fromIterable(attachmentList)
                .flatMap(filePart -> s3Service.uploadFileToS3(Flux.just(filePart))
                        .doOnNext(attachmentKeys::add));
    }

    private Mono<TaskResponse> saveTask(TaskRequest task, List<String> attachment) {
        return save(taskMapper.buildTaskEntity(task, attachment))
                .map(m -> taskMapper.buildTaskResponse(m, m.getAttachments()))
                .doOnSuccess(it -> LOGGER.info("M=prepareToSave, I=Task created successfully, title={}", it.getTitle()))
                .doOnError(error -> LOGGER.info("M=prepareToSave, E=Error creating task, title={}", task.getTitle(), error));
    }

    private Mono<TaskEntity> save(final TaskEntity task) {
        return Mono.just(task)
                .doOnNext(it -> LOGGER.info("M=save, I=Starting task saving, title={}", it.getTitle()))
                .flatMap(taskRepository::save);
    }

    public Flux<TaskResponse> findActiveTasks() {
        return taskRepository.findByIndActiveTrue()
                .doOnError(error -> LOGGER.error("M=findActiveTasks, E=Error when searching for tasks", error))
                .flatMap(this::complementResponseWithAttachmentLinks)
                .switchIfEmpty(Flux.error(new TaskNotFoundException()));
    }

    public Flux<TaskResponse> findTasksByStatus(final TaskStatus status) {
        return taskRepository.findTasksByIndActiveTrueAndStatus(status)
                .doOnError(error -> LOGGER.error("M=findTasksByStatus, E=Error when searching for tasks by status"))
                .flatMap(this::complementResponseWithAttachmentLinks)
                .switchIfEmpty(Flux.error(new TaskNotFoundException()));
    }

    public Mono<TaskResponse> update(final TaskUpdateRequest task) {
        return taskRepository.findById(task.getId())
                .flatMap(existingTask -> {
                    existingTask.setTitle(task.getTitle());
                    existingTask.setDescription(task.getDescription());
                    existingTask.setStatus(task.getStatus());
                    existingTask.setDatUpdate(LocalDateTime.now());

                    return save(existingTask)
                            .map(m -> taskMapper.buildTaskResponse(m, m.getAttachments()))
                            .doOnSuccess(it -> LOGGER.info("M=update, I=Task updated successfully, title={}", it.getTitle()))
                            .doOnError(error -> LOGGER.info("M=update, E=Error when updating task, id={}", task.getId(), error));
                })
                .switchIfEmpty(Mono.error(new TaskNotFoundException(task.getId())));
    }

    public Mono<Void> delete(final String id) {
        return taskRepository.findById(id)
                .switchIfEmpty(Mono.error(new TaskNotFoundException(id)))
                .flatMap(task -> {
                    task.setIndActive(false);
                    task.setDatUpdate(LocalDateTime.now());
                    return taskRepository.save(task);
                })
                .doOnError(error -> LOGGER.error("M=delete, E=Error when deleting task, id={}", id, error))
                .then();
    }

    private Mono<TaskResponse> complementResponseWithAttachmentLinks(TaskEntity task) {
        List<String> attachmentKeys = task.getAttachments();
        if (attachmentKeys == null || attachmentKeys.isEmpty()) {
            return Mono.just(taskMapper.buildTaskResponse(task, null));
        } else {
            return Flux.fromIterable(attachmentKeys)
                    .flatMap(s3Service::generateUrlToFileDownload)
                    .collectList()
                    .map(urls -> taskMapper.buildTaskResponse(task, urls));
        }
    }
}
