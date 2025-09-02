package model;

import manager.Status;
import manager.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;

public class Subtask extends Task {

    private final int epicID;
    private Duration durationOfSubtask = null;

    public Subtask(int epicID, String name, String description, int id, Status status, String duration,
                   String startTime) {
        super(name, description, id, status);
        this.durationOfSubtask = Duration.ofMinutes(Integer.parseInt(duration));
        this.startTime = LocalDateTime.parse(startTime, formatter);
        this.epicID = epicID;
        className = TaskTypes.SUBTASK;
    }

    public Subtask(int epicID, String name, String description, int id, Status status) {
        super(name, description, id, status);
        this.epicID = epicID;
        className = TaskTypes.SUBTASK;
    }

    public Subtask(Subtask subtask) {
        this(subtask.getEpicID(), subtask.getName(), subtask.getDescription(), subtask.getId(), subtask.getStatus());
        className = TaskTypes.SUBTASK;
        this.durationOfSubtask = subtask.getDurationOfSubtask();
        this.startTime = subtask.getStartTime();
    }

    public int getEpicID() {
        return epicID;
    }

    @Override
    public LocalDateTime getEndTime() {
        return startTime.plus(durationOfSubtask);
    }

    public Duration getDurationOfSubtask() {
        return durationOfSubtask;
    }

    @Override
    public String toString() {
        if (durationOfSubtask != null) {
            return id + ","
                    + className + ","
                    + name + ","
                    + status + ","
                    + description + ","
                    + epicID + ","
                    + durationOfSubtask.toMinutes() + ","
                    + startTime.format(formatter);
        } else {
            return id + ","
                    + className + ","
                    + name + ","
                    + status + ","
                    + description + ","
                    + epicID + ","
                    + null + ","
                    + null;
        }
    }
}
