package manager;

import exceptions.ManagerSaveException;
import model.Epic;
import model.Subtask;
import model.Task;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class FileBackedTaskManager extends InMemoryTaskManager implements TaskManager {

    private static String savedTasksFilePath;

    public FileBackedTaskManager(String savedTasksFilePath) {
        FileBackedTaskManager.savedTasksFilePath = savedTasksFilePath;
    }

    public FileBackedTaskManager(Map<Integer, Task> backedTasksMap, Map<Integer, Epic> backedEpicsMap,
                                 Map<Integer, Subtask> backedSubtasksMap) {
        tasks = backedTasksMap;
        epics = backedEpicsMap;
        subtasks = backedSubtasksMap;
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.deleteAllEpics();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.deleteAllSubtasks();
        save();
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void createSubtask(Subtask subtask) {
        super.createSubtask(subtask);
        save();
    }

    @Override
    public void renewTask(Task task) {
        super.renewTask(task);
        save();
    }

    @Override
    public void renewEpic(Epic epic) {
        super.renewEpic(epic);
        save();
    }

    @Override
    public void renewSubtask(Subtask subtask) {
        super.renewSubtask(subtask);
        save();
    }

    @Override
    public void deleteTaskByID(int id) {
        super.deleteTaskByID(id);
        save();
    }

    @Override
    public void deleteEpicByID(int id) {
        super.deleteEpicByID(id);
        save();
    }

    @Override
    public void deleteSubtaskByID(int subtaskID) {
        super.deleteSubtaskByID(subtaskID);
        save();
    }

    public static FileBackedTaskManager loadFromFile(File file) throws IOException {
        Map<Integer, Task> backedTasksMap = new HashMap<>();
        Map<Integer, Epic> backedEpicsMap = new HashMap<>();
        Map<Integer, Subtask> backedSubtasksMap = new HashMap<>();

        try (BufferedReader tasksFileBufferedReader = new BufferedReader(new FileReader(file))) {
            tasksFileBufferedReader.readLine();
            while (tasksFileBufferedReader.ready()) {
                String tasksFileCurrentString = tasksFileBufferedReader.readLine();
                String[] elements = tasksFileCurrentString.split(",");
                if (elements[1].equals(TaskTypes.TASK.toString())) {
                    backedTasksMap.put(Integer.parseInt(elements[0]), fromString(tasksFileCurrentString));
                } else if (elements[1].equals(TaskTypes.EPIC.toString())) {
                    if (fromString(tasksFileCurrentString) instanceof Epic) {
                        backedEpicsMap.put(Integer.parseInt(elements[0]), (Epic) fromString(tasksFileCurrentString));
                    }
                } else if (elements[1].equals(TaskTypes.SUBTASK.toString())) {
                    if (fromString(tasksFileCurrentString) instanceof Subtask) {
                        Subtask subtask = (Subtask) fromString(tasksFileCurrentString);
                        Epic epic = backedEpicsMap.get(subtask.getEpicID());
                        epic.addSubtask(subtask.getId());
                        backedSubtasksMap.put(Integer.parseInt(elements[0]), subtask);
                    }
                }
            }
        }
        return new FileBackedTaskManager(backedTasksMap, backedEpicsMap, backedSubtasksMap);
    }

    public static Task fromString(String value) {
        String[] elements = value.split(",");
        if (elements[1].equals(TaskTypes.TASK.toString())) {
            return new Task(elements[2], elements[4], Integer.parseInt(elements[0]), Status.valueOf(elements[3]));
        } else if (elements[1].equals(TaskTypes.EPIC.toString())) {
            return new Epic(elements[2], elements[4], Integer.parseInt(elements[0]), Status.valueOf(elements[3]));
        } else if (elements[1].equals(TaskTypes.SUBTASK.toString())) {
            return new Subtask(Integer.parseInt(elements[5]), elements[2], elements[4], Integer.parseInt(elements[0]),
                    Status.valueOf(elements[3]));
        }
        return null;
    }

    public void save() {
        try (Writer tasksFileWriter = new FileWriter(savedTasksFilePath)) {
            tasksFileWriter.write("id,type,name,status,description,epic\n");
            writeElementsInFile(tasks, tasksFileWriter);
            writeElementsInFile(epics, tasksFileWriter);
            writeElementsInFile(subtasks, tasksFileWriter);
        } catch (IOException e) {
            throw new ManagerSaveException("При сохранении файла произошла ошибка.");
        }
    }

    public <T> void writeElementsInFile(Map<Integer, T> map, Writer tasksFileWriter) throws IOException {
        for (T task : map.values()) {
            tasksFileWriter.write(task.toString() + "\n");
        }
    }
}
