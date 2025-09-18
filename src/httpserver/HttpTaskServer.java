package httpserver;

import com.sun.net.httpserver.HttpServer;
import httpserver.handler.*;
import manager.Managers;
import manager.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Scanner;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private HttpServer server;
    private static TaskManager taskManager;
    private static Scanner scanner = new Scanner(System.in);

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(8080), 0);
        HttpTaskServer.taskManager = taskManager;
        server.createContext("/tasks", new TasksHandler(taskManager));
        server.createContext("/epics", new EpicsHandler(taskManager));
        server.createContext("/subtasks", new SubtasksHandler(taskManager));
        server.createContext("/history", new HistoryHandler(taskManager));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }

    public static void main(String[] args) throws IOException {
        printManagerChoice();

        HttpTaskServer httpTaskServer = new HttpTaskServer(taskManager);
        httpTaskServer.start();
    }

    public static void printManagerChoice() throws IOException {
        System.out.println("Выберите менеджер:");
        System.out.println("1 - InMemoryTaskManager");
        System.out.println("2 - FileBackedTaskManager");
        System.out.println("3 - Восстановленный FileBackedTaskManager");
        String command = scanner.nextLine();

        switch (command) {
            case "1":
                taskManager = Managers.getDefault();
                break;
            case "2":
                System.out.println("Укажите путь файла, куда будет сохраняться бэкап:");
                String filePathForSaving = scanner.nextLine();
                taskManager = Managers.getBackedTaskManager(filePathForSaving);
                break;
            case "3":
                System.out.println("Укажите путь файла, из которого будет происходить бэкап:");
                String filePathForBacking = scanner.nextLine();
                taskManager = Managers.getRecoveredBackedManager(filePathForBacking);
                break;
            default:
                System.out.println("Такого менеджера нет!");
        }
    }

    public void start() {
        server.start();
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
    }

    public void stop() {
        server.stop(0);
        System.out.println("HTTP-сервер остановлен");
    }
}
