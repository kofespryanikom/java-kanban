package manager;

import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    public InMemoryTaskManager() {
        idCounter = -1;
    }

    @Override
    public int getIdCounter() {
        return idCounter;
    }

    @Override
    public int getEpicIdBySubtaskId(int subtaskID) {
        return subtasks.get(subtaskID).getEpicID();
    }

    @Override
    public ArrayList<Task> returnTasksList() {
        ArrayList<Task> tasksList = new ArrayList<>();
        for (Task task : tasks.values()) {
            tasksList.add(task);
        }
        return tasksList;
    }

    @Override
    public ArrayList<Epic> returnEpicsList() {
        ArrayList<Epic> epicsList = new ArrayList<>();
        for (Epic epic : epics.values()) {
            epicsList.add(epic);
        }
        return epicsList;
    }

    @Override
    public ArrayList<Subtask> returnSubtasksList() {
        ArrayList<Subtask> subtasksList = new ArrayList<>();
        for (Subtask subtask : subtasks.values()) {
            subtasksList.add(subtask);
        }
        return subtasksList;
    }

    @Override
    public void deleteAllTasks() {
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        subtasks.clear();
        for (Epic epic : epics.values()) {
            epic.clearAllSubtasks();
            epic.setStatus(Status.NEW);
        }
    }

    @Override
    public Task returnTaskByID(int id) {
        inMemoryHistoryManager.add(tasks.get(id));
        return tasks.get(id);
    }

    @Override
    public Epic returnEpicByID(int id) {
        inMemoryHistoryManager.add(epics.get(id));
        return epics.get(id);
    }

    @Override
    public Subtask returnSubtaskByID(int id) {
        inMemoryHistoryManager.add(subtasks.get(id));
        return subtasks.get(id);
    }

    @Override
    public void createTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void createEpic(Epic epic) {
        epics.put(epic.getId(), epic);
    }

    @Override
    public void createSubtask(Subtask subtask) {
        Epic epic = epics.get(subtask.getEpicID());
        epic.addSubtask(subtask.getId());
        subtasks.put(subtask.getId(), subtask);
        if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
            epic.setStatus(Status.IN_PROGRESS);
            return;
        }
        epic.setStatus(checkStatus(subtask.getEpicID()));
    }

    @Override
    public void renewTask(Task task) {
        tasks.put(task.getId(), task);
    }

    @Override
    public void renewEpic(Epic epic) {
        for (int subtaskID : subtasks.keySet()) {
            if (subtasks.get(subtaskID).getEpicID() == epic.getId()) {
                subtasks.remove(subtaskID);
            }
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void renewSubtask(Subtask subtask) {
        int epicID = subtask.getEpicID();
        if (subtasks.containsKey(subtask.getId())) {
            subtasks.put(subtask.getId(), subtask);
            epics.get(epicID).addSubtask(subtask.getId());
            epics.get(epicID).setStatus(checkStatus(epics.get(epicID).getId()));
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        for (int subtaskID : subtasks.keySet()) {
            if (subtasks.get(subtaskID).getEpicID() == id) {
                subtasks.remove(subtaskID);
            }
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int subtaskID) {
        int epicID = subtasks.get(subtaskID).getEpicID();
        Epic epic = epics.get(epicID);
        if (subtasks.containsKey(subtaskID)) {
            epic.deleteSubtask(subtaskID);
            subtasks.remove(subtaskID);
            epics.get(epicID).setStatus(checkStatus(epics.get(epicID).getId()));
        }
    }

    @Override
    public Status checkStatus(int epicID) {
        int newTasksCounter = 0;
        int doneTasksCounter = 0;
        Epic epic = epics.get(epicID);
        for (Subtask subtaskCheck : subtasks.values()) {

            if (subtaskCheck.getEpicID() == epicID) {

                if (subtaskCheck.getStatus().equals(Status.NEW)) {
                    newTasksCounter++;
                } else if (subtaskCheck.getStatus().equals(Status.DONE)) {
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
        idCounter++;
        Task task = new Task(name, description, idCounter, status);
        return task;
    }

    public Epic formulateEpicForCreation(String name, String description) {
        idCounter++;
        Epic epic = new Epic(name, description, idCounter);
        return epic;
    }

    public Subtask formulateSubtaskForCreation(int epicID, String name, String description, Status status) {
        idCounter++;
        Subtask subtask = new Subtask(epicID, name, description, idCounter, status);
        return subtask;
    }

    @Override
    public ArrayList<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}
