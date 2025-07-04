public class Subtask extends Task {
    public int epicID;
    Subtask(int epicID, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epicID = epicID;
    }
}
