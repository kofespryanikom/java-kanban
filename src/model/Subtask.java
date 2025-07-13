package model;

import manager.Status;

public class Subtask extends Task {

    private int epicID;
    public Subtask(int epicID, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }
}
