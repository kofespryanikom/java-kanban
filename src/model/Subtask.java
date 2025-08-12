package model;

import manager.Status;

public class Subtask extends Task {

    private final int epicID;

    public Subtask(int epicID, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epicID = epicID;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getEpicID(), subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getStatus());
    }

    public int getEpicID() {
        return epicID;
    }
}
