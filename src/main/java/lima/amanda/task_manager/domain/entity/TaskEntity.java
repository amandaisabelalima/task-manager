package lima.amanda.task_manager.domain.entity;


import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "tasks")
public class TaskEntity {

    @Id
    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    @Builder.Default
    private Boolean indActive = Boolean.FALSE;
    //@CreatedDate não está funcionando
    private LocalDateTime datCreation;
    //@LastModifiedDate //não está funcionando
    private LocalDateTime datUpdate;

}
