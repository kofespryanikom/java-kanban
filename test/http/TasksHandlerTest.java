package http;

import httpserver.HttpTaskServer;
import manager.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class TasksHandlerTest {
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
    public void taskAddedShouldBeAddedToTaskManagerCorrectly() throws IOException, InterruptedException {
        String task = "{\n" +
                "  \"name\": \"0\",\n" +
                "  \"description\": \"0\",\n" +
                "  \"status\": \"NEW\",\n" +
                "\t\"durationOfTask\": \"40\",\n" +
                "\t\"startTime\": \"23:30 09.12.2025\"\n" +
                "}";
        URI uri = URI.create("http://localhost:8080/tasks");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(task);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("0,TASK,0,NEW,0,null,40,23:30 09.12.2025", taskManager.returnTaskByID(0).toString());
        Assertions.assertEquals(201, response.statusCode());

        String anotherTask = "{\n" +
                "  \"name\": \"1\",\n" +
                "  \"description\": \"1\",\n" +
                "  \"status\": \"NEW\",\n" +
                "\t\"durationOfTask\": \"40\",\n" +
                "\t\"startTime\": \"23:40 09.12.2025\"\n" +
                "}";
        URI anotherUri = URI.create("http://localhost:8080/tasks");

        HttpRequest.BodyPublisher anotherBody = HttpRequest.BodyPublishers.ofString(anotherTask);
        HttpRequest anotherRequest = HttpRequest.newBuilder()
                .POST(anotherBody)
                .uri(anotherUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpResponse<String> anotherResponse = client.send(request, handler);
        Assertions.assertEquals(406, anotherResponse.statusCode());
    }

    @Test
    public void tasksRequestedShouldBeEqualToThoseWhichWasInTaskManager() throws IOException, InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW));
        taskManager.createTask(taskManager.formulateTaskForCreation("1", "1",
                Status.DONE));
        URI uri = URI.create("http://localhost:8080/tasks");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[{\"id\":0,\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\"," +
                "\"className\":\"TASK\"},{\"id\":1,\"name\":\"1\",\"description\":\"1\",\"status\":\"DONE\"" +
                ",\"className\":\"TASK\"}]", response.body());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void taskRequestedByIdShouldBeEqualToThatWhichWasInTaskManager() throws IOException, InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW));
        URI uri = URI.create("http://localhost:8080/tasks/0");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("{\"id\":0,\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\"," +
                "\"className\":\"TASK\"}", response.body());
        Assertions.assertEquals(200, response.statusCode());

        URI anotherUri = URI.create("http://localhost:8080/tasks/1");
        HttpRequest anotherRequest = HttpRequest.newBuilder()
                .GET()
                .uri(anotherUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();
        HttpResponse<String> anotherResponse = client.send(anotherRequest, handler);

        Assertions.assertEquals(404, anotherResponse.statusCode());
    }

    @Test
    public void tasksListReturnedShouldBeBlankAfterRemovalRequest() throws IOException, InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW));
        URI uri = URI.create("http://localhost:8080/tasks/0");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[]", taskManager.returnTasksList().toString());
        Assertions.assertEquals(200, response.statusCode());

        URI anotherUri = URI.create("http://localhost:8080/tasks/1");

        HttpRequest anotherRequest = HttpRequest.newBuilder()
                .DELETE()
                .uri(anotherUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();
        HttpResponse<String> anotherResponse = client.send(anotherRequest, handler);
        Assertions.assertEquals(404, anotherResponse.statusCode());
    }

    @Test
    public void serverShouldSendInternalErrorIfThereIsMistakeInUriInTasksHandler() throws IOException,
            InterruptedException {
        taskManager.createTask(taskManager.formulateTaskForCreation("0", "0",
                Status.NEW));
        URI uri = URI.create("http://localhost:8080/tasks/a");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
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
