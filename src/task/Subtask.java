package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Subtask extends Task {

    private int epicID;

    public Subtask(String name, TaskStatus status, String description, LocalDateTime startTime,
                   long duration, int epicID) {
        super(name, status, description, startTime, duration);
        this.taskType = TaskType.SUBTASK;
        this.epicID = epicID;
    }

    public Subtask(int subId, String name, TaskStatus status, String description, LocalDateTime startTime,
                   long duration, int epicID) {
        super(name, status, description, startTime, duration);
        this.taskType = TaskType.SUBTASK;
        this.setId(subId);
        this.epicID = epicID;
    }

    public int getEpicID() {
        return epicID;
    }

    public void setEpicID(int epicID) {
        this.epicID = epicID;
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "epicID=" + epicID +
                ", id=" + id +
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                ", startTime=" + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                ", duration=" + duration +
                ", endTime=" + getEndTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                '}';
    }

    @Override
    public String getDescriptionTask() {
        return getId() + "," + TaskType.SUBTASK + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                "," + getDuration() + "," + getEpicID();
    }
}
