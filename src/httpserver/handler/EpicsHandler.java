package httpserver.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import httpserver.adapter.DurationAdapter;
import httpserver.adapter.LocalDateTimeAdapter;
import manager.TaskManager;
import model.Epic;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EpicsHandler extends BaseHttpHandler {

    private TaskManager taskManager;

    public EpicsHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /epics запроса от клиента.");
        String responseBody;
        String requestMethod = httpExchange.getRequestMethod();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        String path = httpExchange.getRequestURI().getPath();
        String[] pathAsArray = path.split("/");

        switch (requestMethod) {
            case "POST":
                String requestBody;
                JsonObject epicAsJsonObject;

                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }

                JsonElement requestBodyAsJsonElement = JsonParser.parseString(requestBody);

                if (requestBodyAsJsonElement.isJsonObject()) {
                    epicAsJsonObject = requestBodyAsJsonElement.getAsJsonObject();
                } else {
                    sendInternalError(httpExchange);
                    return;
                }

                if (epicAsJsonObject.has("id")) {
                    taskManager.renewEpic(new Epic(epicAsJsonObject.get("name")
                            .getAsString(),
                            epicAsJsonObject.get("description").getAsString(),
                            epicAsJsonObject.get("id").getAsInt()));
                } else {
                    taskManager.createEpic(taskManager.formulateEpicForCreation(epicAsJsonObject.get("name")
                                    .getAsString(),
                            epicAsJsonObject.get("description").getAsString()
                    ));
                }

                sendCreated(httpExchange);
                break;

            case "GET":
                if (pathAsArray.length == 3) {
                    try {
                        int epicId = Integer.parseInt(pathAsArray[2]);
                        try {
                            Epic epic = taskManager.returnEpicByID(epicId);
                            responseBody = gson.toJson(epic);
                        } catch (NotFoundException e) {
                            sendNotFound(httpExchange);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        sendInternalError(httpExchange);
                        return;
                    }
                } else if (pathAsArray.length == 4 && pathAsArray[3].equals("subtasks")) {
                    try {
                        int epicId = Integer.parseInt(pathAsArray[2]);
                        Epic epic = taskManager.returnEpicByID(epicId);
                        if (epic != null) {
                            List<Subtask> epicSubtasks = new ArrayList<>();
                            for (Map.Entry<Integer, Subtask> entry : taskManager.getSubtasksMap().entrySet()) {
                                if (epic.getSubtasks().contains(entry.getKey())) {
                                    epicSubtasks.add(entry.getValue());
                                }
                            }
                            responseBody = gson.toJson(epicSubtasks);
                        } else {
                            sendNotFound(httpExchange);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        sendInternalError(httpExchange);
                        return;
                    }
                } else {
                    List<Epic> epics = taskManager.returnEpicsList();
                    responseBody = gson.toJson(epics);
                }
                sendText(httpExchange, responseBody);
                break;

            case "DELETE":
                if (pathAsArray.length > 2) {
                    try {
                        int epicId = Integer.parseInt(pathAsArray[2]);
                        Epic epic = taskManager.returnEpicByID(epicId);
                        if (epic != null) {
                            taskManager.deleteEpicByID(epicId);
                        } else {
                            sendNotFound(httpExchange);
                            return;
                        }
                        sendText(httpExchange, "OK");
                    } catch (NotFoundException e) {
                        sendNotFound(httpExchange);
                    } catch (NumberFormatException e) {
                        sendInternalError(httpExchange);
                    }
                }
                break;

            default:
                sendInternalError(httpExchange);
        }
    }
}
