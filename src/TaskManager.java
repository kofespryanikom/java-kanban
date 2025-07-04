import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    public int idCounter;
    public HashMap<Integer, Task> tasks = new HashMap<>();
    public HashMap<Integer, Epic> epics = new HashMap<>();
    public HashMap<Integer, Subtask> subtasks = new HashMap<>();

    TaskManager() {
        idCounter = -1;
    }

    public ArrayList<Task> returnTasksList() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    public ArrayList<Epic> returnEpicsList() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicsList.add(epic);
        }
        return epicsList;
    }

    public ArrayList<Subtask> returnSubtasksList() {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtasksList.add(subtask);
        }
        return subtasksList;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.status = Status.NEW;
        }
    }

    public Task returnTaskByID(int id) {
        return tasks.get(id);
    }

    public Epic returnEpicByID(int id) {
        return epics.get(id);
    }

    public Subtask returnSubtaskByID(int id) {
        return subtasks.get(id);
    }

    public void createTask(Task task) {
        tasks.put(task.id, task);
    }

    public void createEpic(Epic epic) {
        epics.put(epic.id, epic);
    }

    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.epicID);
        subtasks.put(subtask.id, subtask);
        if (subtask.status.equals(Status.IN_PROGRESS)) {
            epic.status = Status.IN_PROGRESS;
            return;
        }
        epic.status = checkStatus(subtask.epicID);
    }

    public void renewTask(Task task) {
        tasks.put(task.id, task);
    }

    public void renewEpic(Epic epic) {
        for (int subtaskID : subtasks.keySet()) {
            if (subtasks.get(subtaskID).epicID == epic.id) {
                subtasks.remove(subtaskID);
            }
        }
        epics.put(epic.id, epic);
    }

    public void renewSubtask(Subtask subtask) {
        int epicID = subtask.epicID;
            if (subtasks.containsKey(subtask.id)) {
                subtasks.put(subtask.id, subtask);
                epics.get(epicID).status = checkStatus(epics.get(epicID).id);
            }
    }

    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(int id) {
        for (int subtaskID : subtasks.keySet()) {
            if (subtasks.get(subtaskID).epicID == id) {
                subtasks.remove(subtaskID);
            }
        }
        epics.remove(id);
    }

    public void deleteSubtaskByID(int subtaskID) {
        int epicID = subtasks.get(subtaskID).epicID;
        if (subtasks.containsKey(subtaskID)) {
            subtasks.remove(subtaskID);
            epics.get(epicID).status = checkStatus(epics.get(epicID).id);
        }
    }

    public Status checkStatus(int epicID) {
        int inProgressTasksCounter = 0;
        int newTasksCounter = 0;
        int doneTasksCounter = 0;
        Epic epic = epics.get(epicID);
        for (Subtask subtaskCheck : subtasks.values()) {

            if (subtaskCheck.epicID == epicID) {

                if (subtaskCheck.status.equals(Status.NEW)) {
                    newTasksCounter++;
                } else if (subtaskCheck.status.equals(Status.DONE)) {
                    doneTasksCounter++;
                } else {
                    return Status.IN_PROGRESS;
                }
            }
        }
        if (newTasksCounter == subtasks.size()) {
            return Status.NEW;
        } else if (doneTasksCounter == subtasks.size()) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    public Task formulateTaskForCreation(String name, String description, Status status) {
        int id = ++idCounter;
        Task task = new Task(name, description, id, status);
        return task;
    }

    public Epic formulateEpicForCreation(String name, String description) {
        int id = ++idCounter;
        Epic epic = new Epic(name, description, id);
        return epic;
    }

    public Subtask formulateSubtaskForCreation(int epicID, String name, String description, Status status) {
        int subtaskID = ++idCounter;
        Subtask subtask = new Subtask(epicID, name, description, subtaskID, status);
        return subtask;
    }

    public Task formulateTaskForRenewal(String name, String description, int id, Status status) {
        Task task = new Task(name, description, id, status);
        return task;
    }

    public Epic formulateEpicForRenewal(String name, String description, int id) {
        Epic epic = new Epic(name, description, id);
        return epic;
    }

    public Subtask formulateSubtaskForRenewal(String name, String description, int subtaskID,
                                              Status status) {
        Subtask subtask = new Subtask(subtasks.get(subtaskID).epicID, name, description, subtaskID, status);
        return subtask;
    }
}
