package lima.amanda.task_manager.domain.request;

import jakarta.validation.constraints.NotBlank;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskUpdateRequest {

    @NotBlank(message = "Id is mandatory")
    private String id;

    @NotBlank(message = "Title is mandatory")
    private String title;

    private String description;

    private TaskStatus status;


}
