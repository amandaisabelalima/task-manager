package lima.amanda.task_manager.controller;

import lima.amanda.task_manager.domain.enumeration.TaskStatus;
import lima.amanda.task_manager.domain.mapper.TaskMapper;
import lima.amanda.task_manager.domain.response.TaskResponse;
import lima.amanda.task_manager.exceptions.TaskNotFoundException;
import lima.amanda.task_manager.service.TaskService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when; //pra criar isso automaticamente, aperta ALT + ENTER e seleciona a impportação static

@WebFluxTest(TaskController.class) //a classe que ele vai usar para o teste
class TaskControllerTest {

    @MockBean //as dependencias dessa classe que preciso mockar
    private TaskService taskService;

    @MockBean
    private TaskMapper taskMapper;

    @Autowired
    private WebTestClient webTestClient;

    //endpoint de criação
    //endpoint de edição

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
        when(taskService.findActiveTasks()).thenReturn(Flux.just());

        webTestClient.get()
                .uri("/api/tasks")
                .exchange()
                .expectStatus().isOk();
    }

    //teste para quando nao encontra nenhuma task


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
}
