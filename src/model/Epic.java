package model;

import manager.Status;

import java.util.ArrayList;

public class Epic extends Task {

    private final ArrayList<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }

    public void addSubtask(int id) {
        subtasks.add(id);
    }

    public void clearAllSubtasks() {
        subtasks.clear();
    }

    public void deleteSubtask(Integer id) {
        subtasks.remove(id);
    }

    public ArrayList<Integer> getSubtasks() {
        return subtasks;
    }
}
