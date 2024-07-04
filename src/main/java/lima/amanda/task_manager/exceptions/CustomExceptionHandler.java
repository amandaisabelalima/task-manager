package lima.amanda.task_manager.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(TaskNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Mono<ResponseEntity<Map<String, String>>> handleTaskNotFoundException(TaskNotFoundException ex) {
        Map<String, String> response = new HashMap<>();
        response.put("error", ex.getMessage());
        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND)
                .contentType(MediaType.APPLICATION_JSON)
                .body(response));
    }


    //TODO NÃO ESTÁ FUNCIONANDOOOOOO
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationException(MethodArgumentNotValidException ex) {
        return Mono.just(Objects.requireNonNull(ex.getBindingResult().getFieldError()))
                .map(fieldError -> ErrorResponse.builder()
                        .message(fieldError.getDefaultMessage())
                        .status(HttpStatus.BAD_REQUEST.value())
                        .build())
                .map(error -> ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(error)
                ).block();
    }

}
