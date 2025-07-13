import java.util.HashMap;

public class Epic extends Task {
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();

    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }

    public void addSubtask(int id, Subtask subtask) {
        subtasks.put(id, subtask);
    }

    public void clearAllSubtasks() {
        subtasks.clear();
    }

    public void deleteSubtask(int id) {
        subtasks.remove(id);
    }

    public HashMap<Integer, Subtask> getSubtasks() {
        return subtasks;
    }
}
