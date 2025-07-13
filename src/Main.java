import manager.Status;
import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static TaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {

        while (true) {
            printMenu();
            String command = scanner.nextLine();

            switch (command) {
                case "1":
                    createTasks();
                    break;
                case "2":
                    returnTasksList();
                    break;
                case "3":
                    deleteTasks();
                    break;
                case "4":
                    returnTaskByID();
                    break;
                case "5":
                    renewTasks();
                    break;
                case "6":
                    getHistory();
                    break;
                case "7":
                    return;
                default:
                    System.out.println("Такой команды нет!");
            }
        }
    }

    public static void printMenu() {
        System.out.println("Выберите команду:");
        System.out.println("1 - Создать задачу/эпик/подзадачу");
        System.out.println("2 - Получить список всех задач/эпиков/подзадач");
        System.out.println("3 - Удалить задачу/эпик/подзадачу (все/по ID)");
        System.out.println("4 - Получить задачу/эпик/подзадачу по ID");
        System.out.println("5 - Обновить задачу/эпик/подзадачу по ID");
        System.out.println("6 - Получить историю просмотра задач");
        System.out.println("7 - Выход");
    }

    public static void createTasks() {
        System.out.println("Выберите тип создаваемой задачи:");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        String command = scanner.nextLine();
        switch (command) {
            case "1":
                System.out.println("Введите название задачи:");
                String nameOfTask = scanner.nextLine();
                System.out.println("Введите описание задачи:");
                String descriptionOfTask = scanner.nextLine();
                System.out.println("Напишите статус задачи, выбор следующий:");
                System.out.println("- NEW");
                System.out.println("- IN_PROGRESS");
                System.out.println("- DONE");
                String statusOfTask = scanner.nextLine();
                Task task = inMemoryTaskManager.formulateTaskForCreation(nameOfTask, descriptionOfTask,
                        Status.valueOf(statusOfTask));
                inMemoryTaskManager.createTask(task);
                break;
            case "2":
                System.out.println("Введите название эпика:");
                String nameOfEpic = scanner.nextLine();
                System.out.println("Введите описание эпика:");
                String descriptionOfEpic = scanner.nextLine();
                Epic epic = inMemoryTaskManager.formulateEpicForCreation(nameOfEpic, descriptionOfEpic);
                inMemoryTaskManager.createEpic(epic);
                break;
            case "3":
                System.out.println("Введите ID эпика, к которому относится подзадача");
                int subtaskID = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Введите название подзадачи:");
                String nameOfSubtask = scanner.nextLine();
                System.out.println("Введите описание подзадачи:");
                String descriptionOfSubtask = scanner.nextLine();
                System.out.println("Напишите статус подзадачи, выбор следующий:");
                System.out.println("- NEW");
                System.out.println("- IN_PROGRESS");
                System.out.println("- DONE");
                String statusOfSubtask = scanner.nextLine();
                Subtask subtask = inMemoryTaskManager.formulateSubtaskForCreation(subtaskID, nameOfSubtask,
                        descriptionOfSubtask, Status.valueOf(statusOfSubtask));
                inMemoryTaskManager.createSubtask(subtask);
                break;
        }
    }

    public static void returnTasksList() {
        System.out.println("Выберите тип получаемой задачи:");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        String command = scanner.nextLine();
        switch (command) {
            case "1":
                ArrayList<Task> taskList = inMemoryTaskManager.returnTasksList();
                System.out.println(taskList);
                break;
            case "2":
                ArrayList<Epic> epicList = inMemoryTaskManager.returnEpicsList();
                System.out.println(epicList);
                break;
            case "3":
                ArrayList<Subtask> subtaskList = inMemoryTaskManager.returnSubtasksList();
                System.out.println(subtaskList);
                break;
            default:
                System.out.println("Такой команды нет!");
        }
    }

    public static void deleteTasks() {
        System.out.println("Выберите тип удаляемой задачи/задач:");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        String command = scanner.nextLine();
        switch (command) {
            case "1":
                System.out.println("Удалить все задачи или одну по ID?");
                System.out.println("1 - Все задачи");
                System.out.println("2 - Одну");
                command = scanner.nextLine();
                switch (command) {
                    case "1":
                        inMemoryTaskManager.deleteAllTasks();
                        break;
                    case "2":
                        System.out.println("Введите ID задачи");
                        int taskID = scanner.nextInt();
                        inMemoryTaskManager.deleteTaskByID(taskID);
                        scanner.nextLine();
                        break;
                    default:
                        System.out.println("Такой команды нет!");
                }
                break;
            case "2":
                System.out.println("Удалить все эпики или один по ID?");
                System.out.println("1 - Все эпики");
                System.out.println("2 - Один");
                command = scanner.nextLine();
                switch (command) {
                    case "1":
                        inMemoryTaskManager.deleteAllEpics();
                        break;
                    case "2":
                        System.out.println("Введите ID эпика");
                        int epicID = scanner.nextInt();
                        inMemoryTaskManager.deleteEpicByID(epicID);
                        scanner.nextLine();
                        break;
                    default:
                        System.out.println("Такой команды нет!");
                }
                break;
            case "3":
                System.out.println("Удалить все подзадачи или одну по ID?");
                System.out.println("1 - Все подзадачи");
                System.out.println("2 - Одну");
                command = scanner.nextLine();
                switch (command) {
                    case "1":
                        inMemoryTaskManager.deleteAllSubtasks();
                        break;
                    case "2":
                        System.out.println("Введите ID подзадачи:");
                        int subtaskID = scanner.nextInt();
                        inMemoryTaskManager.deleteSubtaskByID(subtaskID);
                        scanner.nextLine();
                        break;
                    default:
                        System.out.println("Такой команды нет!");
                }
        }
    }

    public static void returnTaskByID() {
        System.out.println("Выберите тип получаемой задачи:");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        String command = scanner.nextLine();
        switch (command) {
            case "1":
                System.out.println("Введите ID задачи");
                int taskID = scanner.nextInt();
                Task task = inMemoryTaskManager.returnTaskByID(taskID);
                scanner.nextLine();
                System.out.println(task);
                break;
            case "2":
                System.out.println("Введите ID эпика");
                int epicID = scanner.nextInt();
                Epic epic = inMemoryTaskManager.returnEpicByID(epicID);
                scanner.nextLine();
                System.out.println(epic);
                break;
            case "3":
                System.out.println("Введите ID подзадачи");
                int subtaskID = scanner.nextInt();
                Subtask subtask = inMemoryTaskManager.returnSubtaskByID(subtaskID);
                scanner.nextLine();
                System.out.println(subtask);
                break;
            default:
                System.out.println("Такой команды не существует!");
        }
    }

    public static void renewTasks() {
        System.out.println("Выберите тип обновляемой задачи:");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        String command = scanner.nextLine();
        switch (command) {
            case "1":
                System.out.println("Введите название задачи:");
                String nameOfTask = scanner.nextLine();
                System.out.println("Введите описание задачи:");
                String descriptionOfTask = scanner.nextLine();
                System.out.println("Введите ID задачи");
                int taskID = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Напишите статус задачи, выбор следующий:");
                System.out.println("- NEW");
                System.out.println("- IN_PROGRESS");
                System.out.println("- DONE");
                String statusOfTask = scanner.nextLine();
                inMemoryTaskManager.renewTask(new Task(nameOfTask, descriptionOfTask, taskID,
                        Status.valueOf(statusOfTask)));
                break;
            case "2":
                System.out.println("Введите название эпика:");
                String nameOfEpic = scanner.nextLine();
                System.out.println("Введите описание эпика:");
                String descriptionOfEpic = scanner.nextLine();
                System.out.println("Введите ID эпика");
                int epicID = scanner.nextInt();
                scanner.nextLine();
                inMemoryTaskManager.renewEpic(new Epic(nameOfEpic, descriptionOfEpic, epicID));
                break;
            case "3":
                System.out.println("Введите название подзадачи:");
                String nameOfSubtask = scanner.nextLine();
                System.out.println("Введите описание подзадачи:");
                String descriptionOfSubtask = scanner.nextLine();
                System.out.println("Введите ID подзадачи");
                int subtaskID = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Напишите статус подзадачи, выбор следующий:");
                System.out.println("- NEW");
                System.out.println("- IN_PROGRESS");
                System.out.println("- DONE");
                String statusOfSubtask = scanner.nextLine();
                inMemoryTaskManager.renewSubtask(new Subtask(inMemoryTaskManager.getEpicIdBySubtaskId(subtaskID),
                        nameOfSubtask, descriptionOfSubtask, subtaskID, Status.valueOf(statusOfSubtask)));
                break;
        }
    }

    public static void getHistory() {
        ArrayList<Task> history = inMemoryTaskManager.getHistory();
        System.out.println(history);
    }
}
