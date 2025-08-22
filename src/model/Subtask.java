package model;

import manager.Status;
import manager.TaskTypes;

public class Subtask extends Task {

    private final int epicID;

    public Subtask(int epicID, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epicID = epicID;
        className = TaskTypes.SUBTASK;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getEpicID(), subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getStatus());
        className = TaskTypes.SUBTASK;
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public String toString() {
        return id + ","
                + className + ","
                + name + ","
                + status + ","
                + description + ","
                + epicID;
    }
}
