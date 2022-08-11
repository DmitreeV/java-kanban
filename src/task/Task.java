package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType taskType;
    protected long duration;
    protected LocalDateTime startTime;

    public Task(String name, TaskStatus status, String description, LocalDateTime startTime,
                long duration) {
        this.taskType = TaskType.TASK;
        this.name = name;
        this.description = description;
        this.status = status;
        this.duration = duration;
        this.startTime = startTime;
    }

    public Task(String name, TaskStatus status, String description, LocalDateTime startTime,
                long duration, int id) {  // конструктор для обновления эпика
        this(name, status, description, startTime, duration);
        this.taskType = TaskType.TASK;
        this.id = id;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getEndTime() {
        return this.startTime.plusMinutes(this.duration);
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public void setStatus(TaskStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + startTime.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ", duration=" + duration +
                ", endTime=" + getEndTime() +
                '}';
    }

    public String getDescriptionTask() {
        return getId() + "," + TaskType.TASK + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                "," + getDuration();
    }
}
