package lima.amanda.task_manager.exceptions;

public class InvalidFileExtensionException extends RuntimeException {

    public InvalidFileExtensionException(String fileName) {
        super("Invalid file extension: " + fileName);
    }
}
