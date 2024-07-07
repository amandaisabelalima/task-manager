package lima.amanda.task_manager.exceptions;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String login) {
        super("User already exists with login: " + login);
    }

}
