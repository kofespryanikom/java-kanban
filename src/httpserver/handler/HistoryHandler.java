package httpserver.handler;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpExchange;
import httpserver.adapter.DurationAdapter;
import httpserver.adapter.LocalDateTimeAdapter;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.time.Duration;
import java.time.LocalDateTime;

public class HistoryHandler extends BaseHttpHandler {

    TaskManager taskManager;

    public HistoryHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    @Override
    public void handle(HttpExchange httpExchange) throws IOException {
        System.out.println("Началась обработка /history запроса от клиента.");
        String requestMethod = httpExchange.getRequestMethod();

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())
                .registerTypeAdapter(Duration.class, new DurationAdapter())
                .create();

        if (requestMethod.equals("GET")) {
            String responseBody = gson.toJson(taskManager.getHistory());
            sendText(httpExchange, responseBody);
        } else {
            sendInternalError(httpExchange);
        }
    }
}
