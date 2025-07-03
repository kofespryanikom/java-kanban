import java.util.HashMap;

public class Epic extends Task {
    HashMap<Integer, Subtask> subtasks = new HashMap<>();

    Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
    }
}
