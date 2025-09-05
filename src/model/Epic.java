package model;

import manager.Status;
import manager.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtasks = new ArrayList<>();
    private Duration durationOfEpic = null;
    private LocalDateTime endTime;

    public Epic(String name, String description, int id) {
        super(name, description, id, Status.NEW);
        className = TaskTypes.EPIC;
    }

    public Epic(Epic epic) {
        super(epic.getName(), epic.getDescription(), epic.getId(), epic.getStatus());
        subtasks = epic.getSubtasks();
        className = TaskTypes.EPIC;
        this.durationOfEpic = epic.getDurationOfEpic();
        this.startTime = epic.getStartTime();
    }

    public Epic(String name, String description, int id, Status status) {
        super(name, description, id, status);
        className = TaskTypes.EPIC;
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

    @Override
    public LocalDateTime getEndTime() {
        return startTime.plus(durationOfEpic);
    }

    public void setEpicDuration(long duration) {
        if (duration >= 0) {
            durationOfEpic = Duration.ofMinutes(duration);
        } else {
            durationOfEpic = null;
        }
    }

    public void setStartTime(LocalDateTime dateTime) {
        startTime = dateTime;
    }

    @Override
    public String toString() {
        if (durationOfEpic != null) {
            return id + ","
                    + className + ","
                    + name + ","
                    + status + ","
                    + description + ","
                    + null + ","
                    + durationOfEpic.toMinutes() + ","
                    + startTime.format(formatter);
        } else {
            return id + ","
                    + className + ","
                    + name + ","
                    + status + ","
                    + description + ","
                    + null + ","
                    + null + ","
                    + null;
        }
    }

    public Duration getDurationOfEpic() {
        return durationOfEpic;
    }
}
