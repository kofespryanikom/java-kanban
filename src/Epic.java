import java.util.HashMap;

public class Epic extends Task {
//    public HashMap<Integer, Subtask> subtasks = new HashMap<>();

    Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }
}
