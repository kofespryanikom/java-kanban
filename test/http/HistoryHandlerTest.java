package http;

import httpserver.HttpTaskServer;
import manager.InMemoryTaskManager;
import manager.Status;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HistoryHandlerTest {
    private TaskManager taskManager;
    private HttpTaskServer server;

    @BeforeEach
    public void serverCreation() throws IOException {
        taskManager = new InMemoryTaskManager();
        server = new HttpTaskServer(taskManager);
        server.start();
    }

    @AfterEach
    public void stopServer() {
        server.stop();
    }

    @Test
    public void historyListReturnedShouldBeCorrect() throws IOException, InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW));
        taskManager.createEpic(taskManager.formulateEpicForCreation("1", "1"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1, "2", "2", Status.NEW));

        taskManager.returnEpicByID(1);
        taskManager.returnTaskByID(0);
        taskManager.returnSubtaskByID(2);

        URI uri = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[{\"epicID\":1,\"id\":2,\"name\":\"2\",\"description\":\"2\",\"status\":\"NEW\"," +
                "\"className\":\"SUBTASK\"},{\"id\":0,\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\"," +
                "\"className\":\"TASK\"},{\"subtasks\":[2],\"id\":1,\"name\":\"1\",\"description\":\"1\",\"status\":\"NEW\"," +
                "\"className\":\"EPIC\"}]", response.body());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void historyHandlerShouldSendInternalErrorWhenRequestMethodIsIncorrect() throws IOException, InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW));
        taskManager.createEpic(taskManager.formulateEpicForCreation("1", "1"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1, "2", "2", Status.NEW));

        taskManager.returnEpicByID(1);
        taskManager.returnTaskByID(0);
        taskManager.returnSubtaskByID(2);

        URI uri = URI.create("http://localhost:8080/history");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals(500, response.statusCode());
    }
}
