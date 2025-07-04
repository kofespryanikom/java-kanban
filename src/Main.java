import java.util.ArrayList;
import java.util.Scanner;

public class Main {

    static Scanner scanner = new Scanner(System.in);
    static TaskManager taskManager = new TaskManager();

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
        System.out.println("6 - Выход");
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
                Task task = taskManager.formulateTaskForCreation(nameOfTask, descriptionOfTask, Status.valueOf(statusOfTask));
                taskManager.createTask(task);
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
                Subtask subtask = taskManager.formulateSubtaskForCreation(subtaskID, nameOfSubtask, descriptionOfSubtask,
                        Status.valueOf(statusOfSubtask));
                taskManager.createSubtask(subtask);
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
                ArrayList<Task> taskList = taskManager.returnTasksList();
                System.out.println(taskList);
                break;
            case "2":
                ArrayList<Epic> epicList = taskManager.returnEpicsList();
                System.out.println(epicList);
                break;
            case "3":
                ArrayList<Subtask> subtaskList = taskManager.returnSubtasksList();
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
                Task task = taskManager.formulateTaskForRenewal(nameOfTask, descriptionOfTask, taskID,
                        Status.valueOf(statusOfTask));
                taskManager.renewTask(task);
                break;
            case "2":
                System.out.println("Введите название эпика:");
                String nameOfEpic = scanner.nextLine();
                System.out.println("Введите описание эпика:");
                String descriptionOfEpic = scanner.nextLine();
                System.out.println("Введите ID эпика");
                int epicID = scanner.nextInt();
                scanner.nextLine();
                Epic epic = taskManager.formulateEpicForRenewal(nameOfEpic, descriptionOfEpic, epicID);
                taskManager.renewEpic(epic);
                break;
            case "3":
                System.out.println("Введите название подзадачи:");
                String nameOfSubtask = scanner.nextLine();
                System.out.println("Введите описание подзадачи:");
                String descriptionOfSubtask = scanner.nextLine();
                System.out.println("Введите ID подзадачи");
                int SubtaskID = scanner.nextInt();
                scanner.nextLine();
                System.out.println("Напишите статус подзадачи, выбор следующий:");
                System.out.println("- NEW");
                System.out.println("- IN_PROGRESS");
                System.out.println("- DONE");
                String statusOfSubtask = scanner.nextLine();
                Subtask subtask = taskManager.formulateSubtaskForRenewal(nameOfSubtask, descriptionOfSubtask, SubtaskID,
                        Status.valueOf(statusOfSubtask));
                taskManager.renewSubtask(subtask);
                break;
        }
    }
}
