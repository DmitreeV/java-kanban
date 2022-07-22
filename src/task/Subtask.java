package task;

import manager.FileBackedTasksManager;

public class Subtask extends Task{

    private int epicID;

    public Subtask(String name, FileBackedTasksManager.TaskType taskType, TaskStatus status, String description,
             int epicID) {
        super(name, taskType, status, description);
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}
