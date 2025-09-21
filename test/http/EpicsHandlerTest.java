package http;

import httpserver.HttpTaskServer;
import manager.InMemoryTaskManager;
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

public class EpicsHandlerTest {
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
    public void epicAddedShouldBeAddedToTaskManagerCorrectly() throws IOException, InterruptedException {
        String epic = "{\n" +
                "  \"name\": \"0\",\n" +
                "  \"description\": \"0\"\n" +
                "}";
        URI uri = URI.create("http://localhost:8080/epics");

        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(epic);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Content-Type", "application/json")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("0,EPIC,0,NEW,0,null,null,null", taskManager.returnEpicByID(0).toString());
        Assertions.assertEquals(201, response.statusCode());
    }

    @Test
    public void epicsRequestedShouldBeEqualToThoseWhichWasInTaskManager() throws IOException, InterruptedException {
        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        taskManager.createEpic(taskManager.formulateEpicForCreation("1", "1"));
        URI uri = URI.create("http://localhost:8080/epics");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[{\"subtasks\":[],\"id\":0,\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\"," +
                "\"className\":\"EPIC\"},{\"subtasks\":[],\"id\":1,\"name\":\"1\",\"description\":\"1\"," +
                "\"status\":\"NEW\",\"className\":\"EPIC\"}]", response.body());
        Assertions.assertEquals(200, response.statusCode());
    }

    @Test
    public void epicRequestedByIdShouldBeEqualToThatWhichWasInTaskManager() throws IOException, InterruptedException {
        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        URI uri = URI.create("http://localhost:8080/epics/0");

        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("{\"subtasks\":[],\"id\":0,\"name\":\"0\",\"description\":\"0\",\"status\":\"NEW\"," +
                "\"className\":\"EPIC\"}", response.body());
        Assertions.assertEquals(200, response.statusCode());

        URI anotherUri = URI.create("http://localhost:8080/epics/1");

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
    public void epicsListReturnedShouldBeBlankAfterRemovalRequest() throws IOException, InterruptedException {
        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        URI uri = URI.create("http://localhost:8080/epics/0");

        HttpRequest request = HttpRequest.newBuilder()
                .DELETE()
                .uri(uri)
                .version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "text/html")
                .build();

        HttpClient client = HttpClient.newHttpClient();
        HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
        HttpResponse<String> response = client.send(request, handler);

        Assertions.assertEquals("[]", taskManager.returnEpicsList().toString());
        Assertions.assertEquals(200, response.statusCode());

        URI anotherUri = URI.create("http://localhost:8080/epics/1");

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
    public void serverShouldSendInternalErrorIfThereIsMistakeInUriInEpicsHandler() throws IOException,
            InterruptedException {
        taskManager.createEpic(taskManager.formulateEpicForCreation("0", "0"));
        URI uri = URI.create("http://localhost:8080/epics/a");

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
