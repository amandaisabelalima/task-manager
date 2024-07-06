package lima.amanda.task_manager.domain.mapper;

import lima.amanda.task_manager.domain.entity.TaskEntity;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.request.TaskRequest;
import lima.amanda.task_manager.domain.response.TaskResponse;
import lima.amanda.task_manager.service.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Component
public class TaskMapper {

    public TaskEntity buildTaskEntity(TaskRequest taskRequest, List<String> attachmentKeyS3) {
        return Optional.ofNullable(taskRequest)
                .map(source ->
                        TaskEntity.builder()
                                .title(taskRequest.getTitle())
                                .description(taskRequest.getDescription())
                                .attachments(attachmentKeyS3)
                                .indActive(true)
                                .status(TaskStatus.NOT_COMPLETED)
                                .datCreation(LocalDateTime.now())
                                .datUpdate(LocalDateTime.now())
                                .build()
                ).orElse(null);

    }

    public TaskResponse buildTaskResponse(TaskEntity taskEntity, List<String> urlsAttachmentDownload) {
        return Optional.ofNullable(taskEntity)
                .map(source ->
                        TaskResponse.builder()
                                .id(taskEntity.getId())
                                .title(taskEntity.getTitle())
                                .description(taskEntity.getDescription())
                                .status(taskEntity.getStatus())
                                .urlsAttachmentDownload(urlsAttachmentDownload)
                                .datCreation(taskEntity.getDatCreation())
                                .build()
                ).orElse(null);
    }

    public TaskRequest buildTaskRequest(String title, String description, Flux<FilePart> attachment) {
        return TaskRequest.builder()
                .title(title)
                .description(description)
                .attachments(attachment)
                .build();
    }
}
