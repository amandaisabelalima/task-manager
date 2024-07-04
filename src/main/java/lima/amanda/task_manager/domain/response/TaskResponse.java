package lima.amanda.task_manager.domain.response;

import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskResponse {

    private String id;
    private String title;
    private String description;
    private TaskStatus status;
    private LocalDateTime datCreation;
    private LocalDateTime datUpdate; //só pra teste, remover depois

}
