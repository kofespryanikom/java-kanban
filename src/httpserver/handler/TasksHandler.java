package httpserver.handler;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import exceptions.TaskIntersectionException;
import exceptions.NotFoundException;
import httpserver.adapter.DurationAdapter;
import httpserver.adapter.LocalDateTimeAdapter;
import manager.Status;
import manager.TaskManager;
import model.Task;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import java.util.List;

public class TasksHandler extends BaseHttpHandler {

    private TaskManager taskManager;

    public TasksHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /tasks запроса от клиента.");
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
                JsonObject taskAsJsonObject;

                try (InputStream inputStream = httpExchange.getRequestBody()) {
                    requestBody = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
                }
                JsonElement requestBodyAsJsonElement = JsonParser.parseString(requestBody);

                if (requestBodyAsJsonElement.isJsonObject()) {
                    taskAsJsonObject = requestBodyAsJsonElement.getAsJsonObject();
                } else {
                    sendInternalError(httpExchange);
                    return;
                }
                if (taskAsJsonObject.has("id")) {
                    if (taskAsJsonObject.has("startTime")) {
                        try {
                            taskManager.renewTask(new Task(taskAsJsonObject
                                    .get("name").getAsString(),
                                    taskAsJsonObject.get("description").getAsString(),
                                    taskAsJsonObject
                                            .get("id").getAsInt(),
                                    Status.valueOf(taskAsJsonObject.get("status").getAsString()),
                                    taskAsJsonObject.get("durationOfTask").getAsString(),
                                    taskAsJsonObject.get("startTime").getAsString()
                            ));
                        } catch (TaskIntersectionException e) {
                            sendHasOverlaps(httpExchange);
                            return;
                        }
                    } else {
                        try {
                            taskManager.renewTask(new Task(taskAsJsonObject
                                    .get("name").getAsString(),
                                    taskAsJsonObject.get("description").getAsString(),
                                    taskAsJsonObject
                                            .get("id").getAsInt(),
                                    Status.valueOf(taskAsJsonObject.get("status").getAsString())
                            ));
                        } catch (TaskIntersectionException e) {
                            sendHasOverlaps(httpExchange);
                            return;
                        }
                    }
                } else {
                    if (taskAsJsonObject.has("startTime")) {
                        try {
                            taskManager.createTask(taskManager.formulateTaskForCreation(taskAsJsonObject
                                            .get("name").getAsString(),
                                    taskAsJsonObject.get("description").getAsString(),
                                    Status.valueOf(taskAsJsonObject.get("status").getAsString()),
                                    taskAsJsonObject.get("durationOfTask").getAsString(),
                                    taskAsJsonObject.get("startTime").getAsString()
                            ));
                        } catch (TaskIntersectionException e) {
                            sendHasOverlaps(httpExchange);
                            return;
                        }
                    } else {
                        try {
                            taskManager.createTask(taskManager.formulateTaskForCreation(taskAsJsonObject
                                            .get("name").getAsString(),
                                    taskAsJsonObject.get("description").getAsString(),
                                    Status.valueOf(taskAsJsonObject.get("status").getAsString())
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
                        int taskId = Integer.parseInt(pathAsArray[2]);
                        try {
                            Task task = taskManager.returnTaskByID(taskId);
                            responseBody = gson.toJson(task);
                        } catch (NotFoundException e) {
                            sendNotFound(httpExchange);
                            return;
                        }

                    } catch (NumberFormatException e) {
                        sendInternalError(httpExchange);
                        return;
                    }
                } else {
                    List<Task> tasks = taskManager.returnTasksList();
                    responseBody = gson.toJson(tasks);
                }
                sendText(httpExchange, responseBody);
                break;

            case "DELETE":
                if (pathAsArray.length > 2) {
                    try {
                        int taskId = Integer.parseInt(pathAsArray[2]);
                        try {
                            Task task = taskManager.returnTaskByID(taskId);
                            taskManager.deleteTaskByID(taskId);
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
