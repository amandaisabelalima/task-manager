package lima.amanda.task_manager.repository;

import lima.amanda.task_manager.domain.entity.TaskEntity;
import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;


@Repository
public interface TaskRepository  extends ReactiveMongoRepository<TaskEntity, String> {

    Flux<TaskEntity> findByIndActiveTrue();

    Flux<TaskEntity> findTasksByIndActiveTrueAndStatus(TaskStatus status);
}
