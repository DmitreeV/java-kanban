package task;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    protected List<Integer> subtasks = new ArrayList<>();

    protected LocalDateTime endTime;

    public Epic(String name, TaskType taskType, TaskStatus status, String description, LocalDateTime startTime,
                long duration) {
        super(name, taskType, status, description, startTime, duration);
        this.endTime = getEndTime();
    }

    public Epic(String name, TaskType taskType, TaskStatus status, String description, LocalDateTime startTime,
                long duration, int id, List<Integer> subtasks) { // конструктор для обновления эпика
        super(name, taskType, status, description, startTime, duration, id);
        this.subtasks = subtasks;
        this.endTime = getEndTime();
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void addIdOfSubtasks(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    public List<Integer> getSubtasks() {
        return subtasks;
    }

    public void setSubtasks(ArrayList<Integer> subtasks) {
        this.subtasks = subtasks;
    }

    @Override
    public String toString() {
        return "Epic{" +
                "subtasks=" + subtasks +
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
        return getId() + "," + TaskType.EPIC + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getStartTime().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm")) +
                "," + getDuration();
    }
}