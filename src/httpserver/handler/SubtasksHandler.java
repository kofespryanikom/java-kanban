package httpserver.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import exceptions.NotFoundException;
import exceptions.TaskIntersectionException;
import httpserver.adapter.DurationAdapter;
import httpserver.adapter.LocalDateTimeAdapter;
import manager.Status;
import manager.TaskManager;
import model.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class SubtasksHandler extends BaseHttpHandler {

    TaskManager taskManager;

    public SubtasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /subtasks запроса от клиента.");
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
                JsonObject subtaskAsJsonObject;

                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
                JsonElement requestBodyAsJsonElement = JsonParser.parseString(requestBody);

                if (requestBodyAsJsonElement.isJsonObject()) {
                    subtaskAsJsonObject = requestBodyAsJsonElement.getAsJsonObject();
                } else {
                    sendInternalError(httpExchange);
                    return;
                }

                if (subtaskAsJsonObject.has("id")) {
                    if (subtaskAsJsonObject.has("startTime")) {
                        try {
                            taskManager.renewSubtask(new Subtask(taskManager.getEpicIdBySubtaskId(subtaskAsJsonObject
                                    .get("id").getAsInt()),
                                    subtaskAsJsonObject
                                            .get("name").getAsString(),
                                    subtaskAsJsonObject.get("description").getAsString(),
                                    subtaskAsJsonObject
                                            .get("id").getAsInt(),
                                    Status.valueOf(subtaskAsJsonObject.get("status").getAsString()),
                                    subtaskAsJsonObject.get("durationOfSubtask").getAsString(),
                                    subtaskAsJsonObject.get("startTime").getAsString()
                            ));
                        } catch (TaskIntersectionException e) {
                            sendHasOverlaps(httpExchange);
                            return;
                        }
                    } else {
                        try {
                            taskManager.renewSubtask(new Subtask(taskManager.getEpicIdBySubtaskId(subtaskAsJsonObject
                                    .get("id").getAsInt()),
                                    subtaskAsJsonObject
                                            .get("name").getAsString(),
                                    subtaskAsJsonObject.get("description").getAsString(),
                                    subtaskAsJsonObject
                                            .get("id").getAsInt(),
                                    Status.valueOf(subtaskAsJsonObject.get("status").getAsString())
                            ));
                        } catch (TaskIntersectionException e) {
                            sendHasOverlaps(httpExchange);
                            return;
                        }
                    }
                } else {
                    if (subtaskAsJsonObject.has("startTime")) {
                        try {
                            taskManager.createSubtask(taskManager.formulateSubtaskForCreation(subtaskAsJsonObject
                                            .get("epicID").getAsInt(),
                                    subtaskAsJsonObject
                                            .get("name").getAsString(),
                                    subtaskAsJsonObject.get("description").getAsString(),
                                    Status.valueOf(subtaskAsJsonObject.get("status").getAsString()),
                                    subtaskAsJsonObject.get("durationOfSubtask").getAsString(),
                                    subtaskAsJsonObject.get("startTime").getAsString()
                            ));
                        } catch (TaskIntersectionException e) {
                            sendHasOverlaps(httpExchange);
                            return;
                        }
                    } else {
                        try {
                            taskManager.createSubtask(taskManager.formulateSubtaskForCreation(subtaskAsJsonObject
                                            .get("id").getAsInt(),
                                    subtaskAsJsonObject
                                            .get("name").getAsString(),
                                    subtaskAsJsonObject.get("description").getAsString(),
                                    Status.valueOf(subtaskAsJsonObject.get("status").getAsString())
                            ));
                        } catch (TaskIntersectionException e) {
                            sendHasOverlaps(httpExchange);
                            return;
                        }
                    }
                }
                sendCreated(httpExchange);
                break;

            case "GET":
                if (pathAsArray.length > 2) {
                    try {
                        int subtaskId = Integer.parseInt(pathAsArray[2]);
                        try {
                            Subtask subtask = taskManager.returnSubtaskByID(subtaskId);
                            responseBody = gson.toJson(subtask);
                        } catch (NotFoundException e) {
                            sendNotFound(httpExchange);
                            return;
                        }
                    } catch (NumberFormatException e) {
                        sendInternalError(httpExchange);
                        return;
                    }
                } else {
                    List<Subtask> subtasks = taskManager.returnSubtasksList();
                    responseBody = gson.toJson(subtasks);
                }
                sendText(httpExchange, responseBody);
                break;

            case "DELETE":
                if (pathAsArray.length > 2) {
                    try {
                        int subtaskId = Integer.parseInt(pathAsArray[2]);
                        try {
                            Subtask subtask = taskManager.returnSubtaskByID(subtaskId);
                            taskManager.deleteSubtaskByID(subtaskId);
                        } catch (NotFoundException e) {
                            sendNotFound(httpExchange);
                            return;
                        }
                        sendText(httpExchange, "OK");
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
