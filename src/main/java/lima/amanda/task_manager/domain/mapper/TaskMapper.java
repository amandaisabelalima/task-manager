package lima.amanda.task_manager.domain.mapper;

import lima.amanda.task_manager.domain.entity.TaskEntity;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.request.TaskRequest;
import lima.amanda.task_manager.domain.response.TaskResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class TaskMapper {

    public TaskEntity buildTaskEntity(TaskRequest taskRequest) {
        return Optional.ofNullable(taskRequest)
                .map(source ->
                        TaskEntity.builder()
                                .title(taskRequest.getTitle())
                                .description(taskRequest.getDescription())
                                .indActive(true)
                                .status(TaskStatus.NOT_COMPLETED)
                                .datCreation(LocalDateTime.now())
                                .datUpdate(LocalDateTime.now())
                                .build()
                ).orElse(null);

    }

    public TaskResponse buildTaskResponse(TaskEntity taskEntity) {
        return Optional.ofNullable(taskEntity)
                .map(source ->
                        TaskResponse.builder()
                                .id(taskEntity.getId())
                                .title(taskEntity.getTitle())
                                .description(taskEntity.getDescription())
                                .status(taskEntity.getStatus())
                                .datCreation(taskEntity.getDatCreation())
                                .datUpdate(taskEntity.getDatUpdate()) //só pra teste, remover depois
                                .build()
                ).orElse(null);
    }
}
