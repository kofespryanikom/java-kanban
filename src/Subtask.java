public class Subtask extends Task {
    private final int epicID;
    Subtask(int epicID, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epicID = epicID;
    }

    Subtask(int epicID, String name, String description, Status status) {
        super(name, description, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }
}
