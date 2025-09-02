package model;

import manager.Status;
import manager.TaskTypes;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected Status status;
    protected TaskTypes className;
    private Duration durationOfTask = null;
    protected LocalDateTime startTime = null;
    protected final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm dd.MM.yyyy");

    public Task(String name, String description, int id, Status status, String duration, String startTime) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        className = TaskTypes.TASK;
        this.durationOfTask = Duration.ofMinutes(Integer.parseInt(duration));
        this.startTime = LocalDateTime.parse(startTime, formatter);
    }

    public Task(String name, String description, int id, Status status) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = status;
        className = TaskTypes.TASK;
    }

    public Task(Task task) {
        this.name = task.getName();
        this.description = task.getDescription();
        this.id = task.getId();
        this.status = task.getStatus();
        className = TaskTypes.TASK;
        this.durationOfTask = task.getDurationOfTask();
        this.startTime = task.getStartTime();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (this.getClass() != obj.getClass()) return false;
        Task otherTask = (Task) obj;
        return id == otherTask.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        if (durationOfTask != null) {
            return id + ","
                    + className + ","
                    + name + ","
                    + status + ","
                    + description + ","
                    + null + ","
                    + durationOfTask.toMinutes() + ","
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

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return startTime.plus(durationOfTask);
    }

    public Duration getDurationOfTask() {
        return durationOfTask;
    }
}
