package lima.amanda.task_manager.exceptions;

public class TaskNotFoundException extends RuntimeException {

    public TaskNotFoundException(String id) {
        super("Task not found with id: " + id);
    }

    public TaskNotFoundException() {
        super("Tasks not found.");
    }

}
