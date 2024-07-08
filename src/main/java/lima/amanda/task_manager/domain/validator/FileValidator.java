package lima.amanda.task_manager.domain.validator;

import lima.amanda.task_manager.exceptions.InvalidFileExtensionException;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class FileValidator {

    private static final String[] ALLOWED_EXTENSIONS = {"jpg", "png", "pdf", "txt"};

    public Mono<Void> validateFileParts(Flux<FilePart> attachments) {
        return attachments
                .flatMap(filePart -> {
                    if (!isValidFileExtension(filePart.filename())) {
                        return Mono.error(new InvalidFileExtensionException(filePart.filename()));
                    }
                    return Mono.empty();
                })
                .then();
    }

    private boolean isValidFileExtension(String filename) {
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        for (String allowedExtension : ALLOWED_EXTENSIONS) {
            if (extension.equals(allowedExtension)) {
                return true;
            }
        }
        return false;
    }
}
