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

public class PrioritizedHandlerTest {
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
    public void prioritizedListReturnedShouldBeCorrect() throws IOException, InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "40", "20:30 09.12.2025"));
        taskManager.createEpic(taskManager.formulateEpicForCreation("1", "1"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1, "2", "2", Status.NEW,
                "10", "20:10 09.12.2025"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1, "3", "3", Status.DONE,
                "100", "22:30 09.12.2025"));

        taskManager.returnEpicByID(1);
        taskManager.returnTaskByID(0);
        taskManager.returnSubtaskByID(2);

        URI uri = URI.create("http://localhost:8080/prioritized");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[{\"epicID\":1,\"durationOfSubtask\":10,\"id\":2,\"name\":\"2\",\"description" +
                        "\":\"2\",\"status\":\"NEW\",\"className\":\"SUBTASK\",\"startTime\":\"20:10 09.12.2025\"},{\"id\":0," +
                        "\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\",\"className\":\"TASK\",\"durationOfTask\":40," +
                        "\"startTime\":\"20:30 09.12.2025\"},{\"epicID\":1,\"durationOfSubtask\":100,\"id\":3,\"name\":\"3\"," +
                        "\"description\":\"3\",\"status\":\"DONE\",\"className\":\"SUBTASK\",\"startTime\":\"22:30 09.12.2025\"}]",
                response.body());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void prioritizedHandlerShouldSentInternalErrorIfRequestMethodIsIncorrect() throws IOException, InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW, "40", "20:30 09.12.2025"));
        taskManager.createEpic(taskManager.formulateEpicForCreation("1", "1"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1, "2", "2", Status.NEW,
                "10", "20:10 09.12.2025"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(1, "3", "3", Status.DONE,
                "100", "22:30 09.12.2025"));

        taskManager.returnEpicByID(1);
        taskManager.returnTaskByID(0);
        taskManager.returnSubtaskByID(2);

        URI uri = URI.create("http://localhost:8080/prioritized");

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
