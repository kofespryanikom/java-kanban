import java.util.ArrayList;
import java.util.HashMap;

public class TaskManager {
    int idCounter;
    HashMap<Integer, Task> tasks = new HashMap<>();
    HashMap<Integer, Epic> epics = new HashMap<>();

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
        for (Epic epic : epics.values()) {
            for (Subtask subtask : epic.subtasks.values()) {
                subtasksList.add(subtask);
            }
        }
        return subtasksList;
    }

    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteAllEpics() {
        epics.clear();
    }

    public void deleteAllSubtasks() {
        for (Epic epic : epics.values()) {
            epic.subtasks.clear();
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
        Subtask subtask = null;
        for (Epic epic : epics.values()) {
            if (epic.subtasks.containsKey(id)) {
                subtask = epic.subtasks.get(id);
                return subtask;
            }
        }
        return subtask;
    }

    public void createTask(String name, String description, Status status) {
        int id = ++idCounter;
        Task task = new Task(name, description, id, status);
        tasks.put(id, task);
    }

    public void createEpic(String name, String description) {
        int id = ++idCounter;
        Epic epic = new Epic(name, description, id, Status.NEW);
        epics.put(id, epic);
    }

    public void createSubtask(int epicID, String name, String description, Status status) {
        int subtaskID = ++idCounter;
        Subtask subtask = new Subtask(name, description, subtaskID, status);
        Epic epic = epics.get(epicID);
        epic.subtasks.put(subtaskID, subtask);
        if (status.equals(Status.IN_PROGRESS)) {
            epic.status = Status.IN_PROGRESS;
            return;
        }
        epic.status = checkStatus(epicID);
    }

    public void renewTask(String name, String description, int id, Status status) {
        Task task = new Task(name, description, id, status);
        tasks.put(id, task);
    }

    public void renewEpic(String name, String description, int id) {
        Epic epic = new Epic(name, description, id, Status.NEW);
        epics.put(id, epic);
    }

    public void renewSubtask(String name, String description, int subtaskID, Status status) {
        Subtask subtask = new Subtask(name, description, subtaskID, status);
        for (Epic epic : epics.values()) {
            if (epic.subtasks.containsKey(subtaskID)) {
                epic.subtasks.put(subtaskID, subtask);
                if (status.equals(Status.IN_PROGRESS)) {
                    epic.status = Status.IN_PROGRESS;
                    return;
                }
                epic.status = checkStatus(epic.id);
                return;
            }
        }

    }

    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    public void deleteEpicByID(int id) {
        epics.remove(id);
    }

    public void deleteSubtaskByID(int subtaskID) {
        for (Epic epic : epics.values()) {
            if (epic.subtasks.containsKey(subtaskID)) {
                epic.subtasks.remove(subtaskID);
                epic.status = checkStatus(epic.id);
                return;
            }
        }
    }

    public Status checkStatus(int epicID) {
        int newTasksCounter = 0;
        int doneTasksCounter = 0;
        Epic epic = epics.get(epicID);
        for (Subtask subtaskCheck : epic.subtasks.values()) {
            if (subtaskCheck.status.equals(Status.NEW)) {
                newTasksCounter++;
            } else if (subtaskCheck.status.equals(Status.DONE)) {
                doneTasksCounter++;
            }
        }
        if (newTasksCounter == epic.subtasks.size()) {
            return Status.NEW;
        } else if (doneTasksCounter == epic.subtasks.size()) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

}
