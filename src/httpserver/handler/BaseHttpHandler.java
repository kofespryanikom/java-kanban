package httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public abstract class BaseHttpHandler implements HttpHandler {

    private static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;

    public void sendText(HttpExchange exchange,
                         String responseString) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
            exchange.sendResponseHeaders(200, 0);
            os.write(responseString.getBytes(DEFAULT_CHARSET));
        }
    }

    public void sendCreated(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(201, 0);
            os.write("Created".getBytes(DEFAULT_CHARSET));
        }
    }

    public void sendNotFound(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(404, 0);
            os.write("Not Found".getBytes(DEFAULT_CHARSET));
        }
    }

    public void sendHasOverlaps(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(406, 0);
            os.write("Not Acceptable".getBytes(DEFAULT_CHARSET));
        }
    }

    public void sendInternalError(HttpExchange exchange) throws IOException {
        try (OutputStream os = exchange.getResponseBody()) {
            exchange.sendResponseHeaders(500, 0);
            os.write("Internal Server Error".getBytes(DEFAULT_CHARSET));
        }
    }
}