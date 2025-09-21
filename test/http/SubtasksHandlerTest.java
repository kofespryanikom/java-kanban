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

public class SubtasksHandlerTest {
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
    public void subtaskAddedShouldBeAddedToTaskManagerCorrectly() throws IOException, InterruptedException {
        URI epicUri = URI.create("http://localhost:8080/epics");
        URI subtaskUri = URI.create("http://localhost:8080/subtasks");

        String epic = "{\n" +
                "  \"name\": \"0\",\n" +
                "  \"description\": \"0\"\n" +
                "}";

        String subtask = "{\n" +
                "\t\"epicID\": 0, \n" +
                "  \"name\": \"0\",\n" +
                "  \"description\": \"0\",\n" +
                "  \"status\": \"NEW\",\n" +
                "\t\"durationOfSubtask\": \"40\",\n" +
                "\t\"startTime\": \"20:30 09.12.2025\"\n" +
                "}";

        HttpRequest.BodyPublisher epicBody = HttpRequest.BodyPublishers.ofString(epic);
        HttpRequest epicRequest = HttpRequest.newBuilder()
                .POST(epicBody)
                .uri(epicUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpRequest.BodyPublisher subtaskBody = HttpRequest.BodyPublishers.ofString(subtask);
        HttpRequest subtaskRequest = HttpRequest.newBuilder()
                .POST(subtaskBody)
                .uri(subtaskUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> epicResponse = client.send(epicRequest, handler);
        HttpResponse<String> subtaskResponse = client.send(subtaskRequest, handler);

        Assertions.assertEquals("1,SUBTASK,0,NEW,0,0,40,20:30 09.12.2025", taskManager.returnSubtaskByID(1).toString());
        Assertions.assertEquals(201, subtaskResponse.statusCode());

        String anotherSubtask = "{\n" +
                "\t\"epicID\": 0, \n" +
                "  \"name\": \"1\",\n" +
                "  \"description\": \"1\",\n" +
                "  \"status\": \"NEW\",\n" +
                "\t\"durationOfSubtask\": \"40\",\n" +
                "\t\"startTime\": \"20:40 09.12.2025\"\n" +
                "}";

        HttpRequest.BodyPublisher anotherEpicBody = HttpRequest.BodyPublishers.ofString(anotherSubtask);
        HttpRequest anotherRequest = HttpRequest.newBuilder()
                .POST(epicBody)
                .uri(epicUri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> anotherSubtaskResponse = client.send(subtaskRequest, handler);
        Assertions.assertEquals(406, anotherSubtaskResponse.statusCode());
    }

    @Test
    public void subtasksRequestedShouldBeEqualToThoseWhichWasInTaskManager() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks");

        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(0, "0", "0", Status.NEW));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(0, "1", "1", Status.DONE));

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[{\"epicID\":0,\"id\":1,\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\"," +
                "\"className\":\"SUBTASK\"},{\"epicID\":0,\"id\":2,\"name\":\"1\",\"description\":\"1\",\"status\":\"DONE\"," +
                "\"className\":\"SUBTASK\"}]", response.body());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void subtaskRequestedByIdShouldBeEqualToThatWhichWasInTaskManager() throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/1");

        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(0, "0", "0", Status.NEW));

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("{\"epicID\":0,\"id\":1,\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\"," +
                "\"className\":\"SUBTASK\"}", response.body());
        Assertions.assertEquals(200, response.statusCode());

        URI anotherUri = URI.create("http://localhost:8080/subtasks/2");
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
    public void subtasksListReturnedShouldBeBlankAfterRemovalRequest() throws IOException, InterruptedException {
        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(0, "0", "0", Status.NEW));

        URI uri = URI.create("http://localhost:8080/subtasks/1");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[]", taskManager.returnSubtasksList().toString());
        Assertions.assertEquals(200, response.statusCode());

        URI anotherUri = URI.create("http://localhost:8080/subtasks/2");

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
    public void serverShouldSendInternalErrorIfThereIsMistakeInUriInSubtasksHandler() throws IOException,
            InterruptedException {
        URI uri = URI.create("http://localhost:8080/subtasks/g");

        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        taskManager.createSubtask(taskManager.formulateSubtaskForCreation(0, "0", "0", Status.NEW));

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
