package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

public interface TaskManager {

    int getIdCounter();

    int getEpicIdBySubtaskId(int subtaskID);

    ArrayList<Task> returnTasksList();

    ArrayList<Epic> returnEpicsList();

    ArrayList<Subtask> returnSubtasksList();

    void deleteAllTasks();

    void deleteAllEpics();

    void deleteAllSubtasks();

    Task returnTaskByID(int id);

    Epic returnEpicByID(int id);

    Subtask returnSubtaskByID(int id);

    void createTask(Task task);

    void createEpic(Epic epic);

    void createSubtask(Subtask subtask);

    void renewTask(Task task);

    void renewEpic(Epic epic);

    void renewSubtask(Subtask subtask);

    void deleteTaskByID(int id);

    void deleteEpicByID(int id);

    void deleteSubtaskByID(int subtaskID);

    Status checkStatus(int epicID);

    void deleteAllTasksFromHistory();

    void deleteAllEpicsFromHistory();

    void deleteAllSubtasksFromHistory();

    void deleteAllSubtasksOfEpicFromHistory(Epic epic);

    List<Task> getHistory();

    Task formulateTaskForCreation(String name, String description, Status status);

    Epic formulateEpicForCreation(String name, String description);

    Subtask formulateSubtaskForCreation(int epicID, String name, String description, Status status);

    Task formulateTaskForCreation(String name, String description, Status status, String duration, String startTime);

    Subtask formulateSubtaskForCreation(int epicID, String name, String description, Status status, String duration,
                                        String startTime);

    boolean areDatesIntersecting(Task task1, Task task2);

    boolean hasAnyDateIntersection(Task task);

    TreeSet<Task> getPrioritizedTasks();
}
