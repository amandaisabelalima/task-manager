package lima.amanda.task_manager.controller;

import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.mapper.TaskMapper;
import lima.amanda.task_manager.domain.request.TaskRequest;
import lima.amanda.task_manager.domain.response.TaskResponse;
import lima.amanda.task_manager.exceptions.TaskNotFoundException;
import lima.amanda.task_manager.service.TaskService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@WebFluxTest(TaskController.class)
class TaskControllerTest {

    @MockBean
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Autowired
    private WebTestClient webTestClient;


    @Test
    void shouldSearchTaskByStatusCompleted() {
        TaskResponse taskResponse = TaskResponse.builder()
                .id("123456")
                .title("Ir ao supermercado")
                .description("Fazer lista de compras")
                .status(TaskStatus.COMPLETED)
                .build();

        when(taskService.findTasksByStatus(any(TaskStatus.class))).thenReturn(Flux.just(taskResponse));

        List<TaskResponse> responseBody = webTestClient.get()
                .uri("/api/tasks/status?status=COMPLETED")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskResponse.class)
                .hasSize(1)
                .contains(taskResponse)
                .returnResult()
                .getResponseBody();

        assert responseBody != null;
        assertEquals(TaskStatus.COMPLETED, responseBody.get(0).getStatus());

    }

    @Test
    void shouldSearchTaskByStatusNotCompleted() {
        TaskResponse taskResponse = TaskResponse.builder()
                .id("123456")
                .title("Ligar para Joao")
                .description(null)
                .status(TaskStatus.NOT_COMPLETED)
                .build();

        when(taskService.findTasksByStatus(any(TaskStatus.class))).thenReturn(Flux.just(taskResponse));

        List<TaskResponse> responseBody = webTestClient.get()
                .uri("/api/tasks/status?status=NOT_COMPLETED")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskResponse.class)
                .hasSize(1)
                .contains(taskResponse)
                .returnResult()
                .getResponseBody();

        assert responseBody != null;
        assertEquals(TaskStatus.NOT_COMPLETED, responseBody.get(0).getStatus());
    }


    @Test
    void shouldReturnBadRequestWhenSearchingForInvalidStatus() {
        webTestClient.get()
                .uri("/api/tasks/status?status=INVALID_STATUS")
                .exchange()
                .expectStatus().isBadRequest();

    }

    @Test
    void shouldSearchAllTasks() {
        TaskResponse taskResponse = TaskResponse.builder().id("123").title("Ligar para Joao").description(null).status(TaskStatus.NOT_COMPLETED).build();
        TaskResponse taskResponse2 = TaskResponse.builder().id("456").title("Ir ao supermercado").description(null).status(TaskStatus.NOT_COMPLETED).build();
        TaskResponse taskResponse3 = TaskResponse.builder().id("789").title("Comprar cafe").description(null).status(TaskStatus.NOT_COMPLETED).build();

        when(taskService.findActiveTasks()).thenReturn(Flux.just(taskResponse, taskResponse2, taskResponse3));

        webTestClient.get()
                .uri("/api/tasks")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskResponse.class)
                .hasSize(3)
                .contains(taskResponse, taskResponse2, taskResponse3);
    }

    @Test
    void shouldReturnEmptyListWhenItFindsNoTask() {
        when(taskService.findActiveTasks()).thenReturn(Flux.error(new TaskNotFoundException()));

        webTestClient.get()
                .uri("/api/tasks")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void shouldDeleteATask() {
        String taskId = "123456";

        when(taskService.delete(taskId)).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri("/api/tasks/{id}", taskId)
                .exchange()
                .expectStatus().isNoContent();
    }

    @Test
    void shouldThrowTaskNotFoundExceptionWhenTryingDeleteATaskThatDoesNotExist() {
        String taskId = "789";

        when(taskService.delete(taskId)).thenReturn(Mono.error(new TaskNotFoundException(taskId)));

        webTestClient.delete()
                .uri("/api/tasks/{id}", taskId)
                .exchange()
                .expectStatus().isNotFound();
    }


    void shouldCreateATaskWithoutAttachments() {
        String title = "Tarefa um";
        String description = "descrevendo minha nova tarefa";

        TaskResponse taskResponse = TaskResponse.builder()
                .id("123456")
                .title(title)
                .description(description)
                .status(TaskStatus.NOT_COMPLETED)
                .urlsAttachmentDownload(null)
                .build();

        when(taskService.create(eq(title), eq(description), ArgumentMatchers.<Flux<FilePart>>isNull())).thenReturn(Mono.just(taskResponse));

        webTestClient.post()
                .uri("/api/tasks")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData("title", title)
                        .with("description", description)
                )
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskResponse.class)
                .value(response -> {
                    assertEquals(taskResponse.getId(), response.getId());
                    assertEquals(taskResponse.getTitle(), response.getTitle());
                    assertEquals(taskResponse.getDescription(), response.getDescription());
                    assertEquals(taskResponse.getUrlsAttachmentDownload(), response.getUrlsAttachmentDownload());
                });

    }
}
