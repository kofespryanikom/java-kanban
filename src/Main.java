import manager.Managers;
import manager.Status;
import manager.TaskManager;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Scanner scanner = new Scanner(System.in);
    private static TaskManager taskManager;

    public static void main(String[] args) throws IOException {

        printManagerChoice();

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
                    getPrioritizedTasks();
                    break;
                case "8":
                    return;
                default:
                    System.out.println("Такой команды нет!");
            }
        }
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

    public static void printMenu() {
        System.out.println("Выберите команду:");
        System.out.println("1 - Создать задачу/эпик/подзадачу");
        System.out.println("2 - Получить список всех задач/эпиков/подзадач");
        System.out.println("3 - Удалить задачу/эпик/подзадачу (все/по ID)");
        System.out.println("4 - Получить задачу/эпик/подзадачу по ID");
        System.out.println("5 - Обновить задачу/эпик/подзадачу по ID");
        System.out.println("6 - Получить историю просмотра задач");
        System.out.println("7 - Получить список задач в порядке приоритета");
        System.out.println("8 - Выход");
    }

    public static void createTasks() {
        System.out.println("Выберите тип создаваемой задачи:");
        System.out.println("1 - Задача");
        System.out.println("2 - Эпик");
        System.out.println("3 - Подзадача");
        String command = scanner.nextLine();
        switch (command) {
            case "1":
                System.out.println("Имеет ли эта задача дату начала выполнения и продолжительность?");
                System.out.println("1 - имеет");
                System.out.println("2 - не имеет");
                command = scanner.nextLine();
                System.out.println("Введите название задачи:");
                String nameOfTask = scanner.nextLine();
                System.out.println("Введите описание задачи:");
                String descriptionOfTask = scanner.nextLine();
                System.out.println("Напишите статус задачи, выбор следующий:");
                System.out.println("- NEW");
                System.out.println("- IN_PROGRESS");
                System.out.println("- DONE");
                String statusOfTask = scanner.nextLine();
                switch (command) {
                    case "1":
                        System.out.println("Введите длительность задачи в минутах");
                        String taskDuration = scanner.nextLine();
                        System.out.println("Введите время и дату начала выполнения в формате \"HH:mm dd.MM.yyyy\"");
                        String dateTimeOfTask = scanner.nextLine();
                        Task taskWithDate = taskManager.formulateTaskForCreation(nameOfTask, descriptionOfTask,
                                Status.valueOf(statusOfTask), taskDuration, dateTimeOfTask);
                        taskManager.createTask(taskWithDate);
                        break;
                    case "2":
                        Task task = taskManager.formulateTaskForCreation(nameOfTask, descriptionOfTask,
                                Status.valueOf(statusOfTask));
                        taskManager.createTask(task);
                        break;
                    default:
                        System.out.println("Такой команды нет!");
                }
                break;
            case "2":
                System.out.println("Введите название эпика:");
                String nameOfEpic = scanner.nextLine();
                System.out.println("Введите описание эпика:");
                String descriptionOfEpic = scanner.nextLine();
                Epic epic = taskManager.formulateEpicForCreation(nameOfEpic, descriptionOfEpic);
                taskManager.createEpic(epic);
                break;
            case "3":
                System.out.println("Имеет ли эта подзадача дату начала выполнения и продолжительность?");
                System.out.println("1 - имеет");
                System.out.println("2 - не имеет");
                command = scanner.nextLine();
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
                switch (command) {
                    case "1":
                        System.out.println("Введите длительность подзадачи в минутах");
                        String subtaskDuration = scanner.nextLine();
                        System.out.println("Введите время и дату начала выполнения в формате \"HH:mm dd.MM.yyyy\"");
                        String dateTimeOfSubtask = scanner.nextLine();
                        Subtask subtaskWithDate = taskManager.formulateSubtaskForCreation(subtaskID, nameOfSubtask,
                                descriptionOfSubtask, Status.valueOf(statusOfSubtask), subtaskDuration,
                                dateTimeOfSubtask);
                        taskManager.createSubtask(subtaskWithDate);
                        break;
                    case "2":
                        Subtask subtask = taskManager.formulateSubtaskForCreation(subtaskID, nameOfSubtask,
                                descriptionOfSubtask, Status.valueOf(statusOfSubtask));
                        taskManager.createSubtask(subtask);
                        break;
                    default:
                        System.out.println("Такой команды нет!");
                }
                break;
            default:
                System.out.println("Такого типа задачи нет!");
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
                List<Task> taskList = taskManager.returnTasksList();
                System.out.println(taskList);
                break;
            case "2":
                List<Epic> epicList = taskManager.returnEpicsList();
                System.out.println(epicList);
                break;
            case "3":
                List<Subtask> subtaskList = taskManager.returnSubtasksList();
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
                        taskManager.deleteAllTasks();
                        break;
                    case "2":
                        System.out.println("Введите ID задачи");
                        int taskID = scanner.nextInt();
                        taskManager.deleteTaskByID(taskID);
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
                        taskManager.deleteAllEpics();
                        break;
                    case "2":
                        System.out.println("Введите ID эпика");
                        int epicID = scanner.nextInt();
                        taskManager.deleteEpicByID(epicID);
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
                        taskManager.deleteAllSubtasks();
                        break;
                    case "2":
                        System.out.println("Введите ID подзадачи:");
                        int subtaskID = scanner.nextInt();
                        taskManager.deleteSubtaskByID(subtaskID);
                        scanner.nextLine();
                        break;
                    default:
                        System.out.println("Такой команды нет!");
                }
            default:
                System.out.println("Такого типа задачи нет!");
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
                Task task = taskManager.returnTaskByID(taskID);
                scanner.nextLine();
                System.out.println(task);
                break;
            case "2":
                System.out.println("Введите ID эпика");
                int epicID = scanner.nextInt();
                Epic epic = taskManager.returnEpicByID(epicID);
                scanner.nextLine();
                System.out.println(epic);
                break;
            case "3":
                System.out.println("Введите ID подзадачи");
                int subtaskID = scanner.nextInt();
                Subtask subtask = taskManager.returnSubtaskByID(subtaskID);
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
                System.out.println("Будет ли теперь задача иметь время и срок выполнения?");
                System.out.println("1 - да");
                System.out.println("2 - нет");
                command = scanner.nextLine();
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
                switch (command) {
                    case "1":
                        System.out.println("Введите длительность задачи в минутах");
                        String taskDuration = scanner.nextLine();
                        System.out.println("Введите время и дату начала выполнения в формате \"HH:mm dd.MM.yyyy\"");
                        String dateTimeOfTask = scanner.nextLine();
                        taskManager.renewTask(new Task(nameOfTask, descriptionOfTask, taskID,
                                Status.valueOf(statusOfTask), taskDuration, dateTimeOfTask));
                        break;
                    case "2":
                        taskManager.renewTask(new Task(nameOfTask, descriptionOfTask, taskID,
                                Status.valueOf(statusOfTask)));
                        break;
                }
                break;
            case "2":
                System.out.println("Введите название эпика:");
                String nameOfEpic = scanner.nextLine();
                System.out.println("Введите описание эпика:");
                String descriptionOfEpic = scanner.nextLine();
                System.out.println("Введите ID эпика");
                int epicID = scanner.nextInt();
                scanner.nextLine();
                taskManager.renewEpic(new Epic(nameOfEpic, descriptionOfEpic, epicID));
                break;
            case "3":
                System.out.println("Будет ли теперь задача иметь время и срок выполнения?");
                System.out.println("1 - да");
                System.out.println("2 - нет");
                command = scanner.nextLine();
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
                switch (command) {
                    case "1":
                        System.out.println("Введите длительность подзадачи в минутах");
                        String subtaskDuration = scanner.nextLine();
                        System.out.println("Введите время и дату начала выполнения в формате \"HH:mm dd.MM.yyyy\"");
                        String dateTimeOfSubtask = scanner.nextLine();
                        taskManager.renewSubtask(new Subtask(taskManager.getEpicIdBySubtaskId(subtaskID),
                                nameOfSubtask, descriptionOfSubtask, subtaskID, Status.valueOf(statusOfSubtask),
                                subtaskDuration, dateTimeOfSubtask));
                        break;
                    case "2":
                        taskManager.renewSubtask(new Subtask(taskManager.getEpicIdBySubtaskId(subtaskID),
                                nameOfSubtask, descriptionOfSubtask, subtaskID, Status.valueOf(statusOfSubtask)));
                        break;
                }
                break;
            default:
                System.out.println("Такого типа задачи нет!");
        }
    }

    public static void getHistory() {
        List<Task> history = taskManager.getHistory();
        System.out.println(history);
    }

    public static void getPrioritizedTasks() {
        System.out.println(taskManager.getPrioritizedTasks());
    }
}
