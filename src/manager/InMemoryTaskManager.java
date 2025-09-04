package manager;

import datastructures.Node;
import model.Epic;
import model.Subtask;
import model.Task;
import java.util.*;

public class InMemoryTaskManager implements TaskManager {

    private int idCounter = -1;
    protected Map<Integer, Task> tasks = new HashMap<>();
    protected Map<Integer, Epic> epics = new HashMap<>();
    protected Map<Integer, Subtask> subtasks = new HashMap<>();
    protected HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();
    protected TreeSet<Task> prioritizedTasks = new TreeSet<>((Task task1, Task task2) -> {
        if (task1.getStartTime().isBefore(task2.getStartTime())) {
            return -1;
        } else if (task1.getStartTime().isEqual(task2.getStartTime())) {
            return 0;
        } else {
            return 1;
        }
    });

    public InMemoryTaskManager() {

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
    public List<Task> returnTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public List<Epic> returnEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public List<Subtask> returnSubtasksList() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void deleteAllTasks() {
        deleteAllTasksFromHistory();
        Set<Task> tasksForRemoval = new HashSet<>();
        for (Task task : prioritizedTasks) {
            if (tasks.containsValue(task)) {
                tasksForRemoval.add(task);
            }
        }
        prioritizedTasks.removeAll(tasksForRemoval);
        tasks.clear();
    }

    @Override
    public void deleteAllEpics() {
        deleteAllEpicsFromHistory();
        subtasks.clear();
        epics.clear();
        Set<Task> tasksForRemoval = new HashSet<>();
        for (Task task : prioritizedTasks) {
            if (task instanceof Epic) {
                tasksForRemoval.add(task);
            }
        }
        prioritizedTasks.removeAll(tasksForRemoval);
    }

    @Override
    public void deleteAllSubtasks() {
        deleteAllSubtasksFromHistory();
        subtasks.clear();
        Set<Task> tasksForRemoval = new HashSet<>();
        for (Task task : prioritizedTasks) {
            if (task instanceof Subtask) {
                tasksForRemoval.add(task);
            }
        }
        prioritizedTasks.removeAll(tasksForRemoval);
        for (Epic epic : epics.values()) {
            epic.clearAllSubtasks();
            epic.setStatus(Status.NEW);
            epic.setEpicDuration(-1);
            epic.setStartTime(null);
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
        if (!hasAnyDateIntersection(task)) {
            tasks.put(task.getId(), task);
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        } else {
            System.out.println("Задача не была создана, потому что пересекается с другими задачами!");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        if (!hasAnyDateIntersection(epic)) {
            epics.put(epic.getId(), epic);
            if (epic.getStartTime() != null) {
                prioritizedTasks.add(epic);
            }
        } else {
            System.out.println("Задача не была создана, потому что пересекается с другими задачами!");
        }
    }

    @Override
    public void createSubtask(Subtask subtask) {
        if (!hasAnyDateIntersection(subtask)) {
            Epic epic = epics.get(subtask.getEpicID());
            epic.addSubtask(subtask.getId());
            subtasks.put(subtask.getId(), subtask);
            if (subtask.getStartTime() != null) {
                prioritizedTasks.add(subtask);
                for (Task task : prioritizedTasks) {
                    if (task instanceof Subtask && ((Subtask) task).getEpicID() == epic.getId()) {
                        epic.setStartTime(task.getStartTime());
                        break;
                    }
                }
                long subtasksDurationsSum = 0;
                for (Task task : prioritizedTasks) {
                    if (task instanceof Subtask && ((Subtask) task).getEpicID() == epic.getId()) {
                        subtasksDurationsSum += ((Subtask) task).getDurationOfSubtask().toMinutes();
                    }
                }
                epic.setEpicDuration(subtasksDurationsSum);
            }
            if (subtask.getStatus().equals(Status.IN_PROGRESS)) {
                epic.setStatus(Status.IN_PROGRESS);
                return;
            }
            epic.setStatus(checkStatus(subtask.getEpicID()));
        } else {
            System.out.println("Задача не была создана, потому что пересекается с другими задачами!");
        }
    }

    @Override
    public void renewTask(Task task) {
        if (!hasAnyDateIntersection(task)) {
            tasks.put(task.getId(), task);
            Task oldTask = null;
            for (Task element : prioritizedTasks) {
                if (task.getId() == element.getId()) {
                    oldTask = element;
                }
            }
            if (oldTask != null) {
                prioritizedTasks.remove(oldTask);
            }
            if (task.getStartTime() != null) {
                prioritizedTasks.add(task);
            }
        } else {
            System.out.println("Задача не была обновлена, потому что пересекается с другими задачами!");
        }

    }

    @Override
    public void renewEpic(Epic epic) {
        Set<Integer> subtasksForRemoval = new HashSet<>();
        for (int subtaskID : subtasks.keySet()) {
            if (subtasks.get(subtaskID).getEpicID() == epic.getId()) {
                subtasksForRemoval.add(subtaskID);
            }
        }
        for (Integer subtaskID : subtasksForRemoval) {
            subtasks.remove(subtaskID);
        }
        epics.put(epic.getId(), epic);
    }

    @Override
    public void renewSubtask(Subtask subtask) {
        int epicID = subtask.getEpicID();
        if (!hasAnyDateIntersection(subtask)) {
            if (subtasks.containsKey(subtask.getId())) {
                subtasks.put(subtask.getId(), subtask);
                epics.get(epicID).addSubtask(subtask.getId());
                epics.get(epicID).setStatus(checkStatus(epics.get(epicID).getId()));
            }
            Subtask oldSubtask = null;
            for (Task element : prioritizedTasks) {
                if (subtask.getId() == element.getId() && element instanceof Subtask) {
                    oldSubtask = (Subtask) element;
                }
            }
            if (oldSubtask != null) {
                prioritizedTasks.remove(oldSubtask);
            }
            if (subtask.getStartTime() != null) {
                Epic epic = epics.get(epicID);
                prioritizedTasks.add(subtask);
                for (Task task : prioritizedTasks) {
                    if (task instanceof Subtask && ((Subtask) task).getEpicID() == epic.getId()) {
                        epic.setStartTime(task.getStartTime());
                        break;
                    }
                }
                long subtasksDurationsSum = 0;
                for (Task task : prioritizedTasks) {
                    if (task instanceof Subtask && ((Subtask) task).getEpicID() == epic.getId()) {
                        subtasksDurationsSum += ((Subtask) task).getDurationOfSubtask().toMinutes();
                    }
                }
                epic.setEpicDuration(subtasksDurationsSum);
            }
        } else {
            System.out.println("Подзадача не была обновлена, потому что пересекается с другими подзадачами!");
        }
    }

    @Override
    public void deleteTaskByID(int id) {
        inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getNodeMap().get(id));
        tasks.remove(id);
        prioritizedTasks.remove(tasks.get(id));
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
        prioritizedTasks.remove(epics.get(id));
    }

    @Override
    public void deleteSubtaskByID(int subtaskID) {
        inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getNodeMap().get(subtaskID));
        int epicID = subtasks.get(subtaskID).getEpicID();
        Epic epic = epics.get(epicID);
        prioritizedTasks.remove(subtasks.get(subtaskID));
        if (subtasks.containsKey(subtaskID)) {
            epic.deleteSubtask(subtaskID);
            subtasks.remove(subtaskID);
            epics.get(epicID).setStatus(checkStatus(epics.get(epicID).getId()));
        }
        for (Task task : prioritizedTasks) {
            if (task instanceof Subtask && ((Subtask) task).getEpicID() == epic.getId()) {
                epic.setStartTime(task.getStartTime());
                break;
            }
        }
        long subtasksDurationsSum = 0;
        for (Task task : prioritizedTasks) {
            if (task instanceof Subtask && ((Subtask) task).getEpicID() == epic.getId()) {
                subtasksDurationsSum += ((Subtask) task).getDurationOfSubtask().toMinutes();
            }
        }
        epic.setEpicDuration(subtasksDurationsSum);
    }

    @Override
    public Status checkStatus(int epicID) {
        int newTasksCounter = 0;
        int doneTasksCounter = 0;
        int subtasksCountInEpic = 0;
        for (Subtask subtaskCheck : subtasks.values()) {

            if (subtaskCheck.getEpicID() == epicID) {
                subtasksCountInEpic++;
                if (subtaskCheck.getStatus().equals(Status.NEW)) {
                    newTasksCounter++;
                } else if (subtaskCheck.getStatus().equals(Status.DONE)) {
                    doneTasksCounter++;
                } else {
                    return Status.IN_PROGRESS;
                }
            }
        }
        if (newTasksCounter == subtasksCountInEpic) {
            return Status.NEW;
        } else if (doneTasksCounter == subtasksCountInEpic) {
            return Status.DONE;
        } else {
            return Status.IN_PROGRESS;
        }
    }

    @Override
    public Task formulateTaskForCreation(String name, String description, Status status) {
        idCounter++;
        Task task = new Task(name, description, idCounter, status);
        return task;
    }

    @Override
    public Epic formulateEpicForCreation(String name, String description) {
        idCounter++;
        Epic epic = new Epic(name, description, idCounter);
        return epic;
    }

    @Override
    public Subtask formulateSubtaskForCreation(int epicID, String name, String description, Status status) {
        idCounter++;
        Subtask subtask = new Subtask(epicID, name, description, idCounter, status);
        return subtask;
    }

    @Override
    public Task formulateTaskForCreation(String name, String description, Status status, String duration,
                                         String startTime) {
        idCounter++;
        Task task = new Task(name, description, idCounter, status, duration, startTime);
        return task;
    }

    @Override
    public Subtask formulateSubtaskForCreation(int epicID, String name, String description, Status status,
                                               String duration, String startTime) {
        idCounter++;
        Subtask subtask = new Subtask(epicID, name, description, idCounter, status, duration, startTime);
        return subtask;
    }

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getTasks();
    }

    @Override
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

    @Override
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

    @Override
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

    @Override
    public void deleteAllSubtasksOfEpicFromHistory(Epic epic) {
        for (Integer subtaskID : epic.getSubtasks()) {
            inMemoryHistoryManager.removeNode(inMemoryHistoryManager.getNodeMap().get(subtaskID));
        }
    }

    @Override
    public TreeSet<Task> getPrioritizedTasks() {
        return prioritizedTasks;
    }

    @Override
    public boolean areDatesIntersecting(Task task1, Task task2) {
        if (task1.getEndTime().isBefore(task2.getStartTime()) || task1.getStartTime().isAfter(task2.getEndTime())) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean hasAnyDateIntersection(Task task) {
        if (task.getStartTime() != null) {
            return prioritizedTasks.stream()
                    .filter(element -> element.getId() != task.getId())
                    .anyMatch(element -> areDatesIntersecting(task, element));
        } else {
            return false;
        }
    }
}
