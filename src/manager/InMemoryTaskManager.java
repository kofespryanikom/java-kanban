package manager;

import datastructures.Node;
import model.Epic;
import model.Subtask;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter;
    private final Map<Integer, Task> tasks = new HashMap<>();
    private final Map<Integer, Epic> epics = new HashMap<>();
    private final Map<Integer, Subtask> subtasks = new HashMap<>();
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
        deleteAllTasksFromHistory();
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllEpicsFromHistory();
        subtasks.clear();
        epics.clear();
    }

    @Override
    public void deleteAllSubtasks() {
        deleteAllSubtasksFromHistory();
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
        inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getNodeMap().get(id));
        tasks.remove(id);
    }

    @Override
    public void deleteEpicByID(int id) {
        List<Integer> subtasksIDToRemove = new ArrayList<>();

        inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getNodeMap().get(id));
        deleteAllSubtasksOfEpicFromHistory(epics.get(id));
        for (int subtaskID : subtasks.keySet()) {
            if (subtasks.get(subtaskID).getEpicID() == id) {
                subtasksIDToRemove.add(subtaskID);
            }
        }
        for (int subtaskID : subtasksIDToRemove) {
            subtasks.remove(subtaskID);
        }
        epics.remove(id);
    }

    @Override
    public void deleteSubtaskByID(int subtaskID) {
        inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getNodeMap().get(subtaskID));
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
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getTasks();
    }

    public void deleteAllTasksFromHistory() {
        List<Node<Task>> nodesToDelete = new ArrayList<>();
        for (Map.Entry<Integer, Node<Task>> entry : inMemoryHistoryManager.getNodeMap().entrySet()) {
            if (tasks.containsKey(entry.getKey())) {
                nodesToDelete.add(entry.getValue());
            }
        }
        for (Node<Task> node : nodesToDelete) {
            inMemoryHistoryManager.removeNode(node);
        }
    }

    public void deleteAllEpicsFromHistory() {
        List<Node<Task>> epicNodesToDelete = new ArrayList<>();
        for (Map.Entry<Integer, Node<Task>> entry : inMemoryHistoryManager.getNodeMap().entrySet()) {
            if (epics.containsKey(entry.getKey())) {
                epicNodesToDelete.add(entry.getValue());
            }
        }
        for (Node<Task> node : epicNodesToDelete) {
            inMemoryHistoryManager.removeNode(node);
            if (node.getData() instanceof Epic) {
                deleteAllSubtasksOfEpicFromHistory((Epic) node.getData());
            }
        }
    }

    public void deleteAllSubtasksFromHistory() {
        List<Node<Task>> subtaskNodesToDelete = new ArrayList<>();
        for (Map.Entry<Integer, Node<Task>> entry : inMemoryHistoryManager.getNodeMap().entrySet()) {
            if (subtasks.containsKey(entry.getKey())) {
                subtaskNodesToDelete.add(entry.getValue());
            }
        }
        for (Node<Task> node : subtaskNodesToDelete) {
            inMemoryHistoryManager.removeNode(node);
        }
    }

    public void deleteAllSubtasksOfEpicFromHistory(Epic epic) {
        for (Integer subtaskID : epic.getSubtasks()) {
            inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getNodeMap().get(subtaskID));
        }
    }
}
