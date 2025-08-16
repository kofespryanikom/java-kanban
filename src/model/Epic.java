package model;

import manager.Status;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtasks = new ArrayList<>();

    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus());
        subtasks = epic.getSubtasks();
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

    public List<Integer> getSubtasks() {
        return subtasks;
    }
}
