package lima.amanda.task_manager.domain.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequest {

    @NotBlank(message = "Title é obrigatório")
    private String title;

    @NotBlank(message = "Description é obrigatório")
    private String description;

}
