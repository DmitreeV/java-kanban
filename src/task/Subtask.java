package task;

public class Subtask extends Task{

    private int epicID;

    public Subtask(String name, TaskType taskType, TaskStatus status, String description,
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
                ", taskType=" + taskType +
                ", name='" + name + '\'' +
                ", status=" + status +
                ", description='" + description + '\'' +
                '}';
    }

    @Override
    public String getDescriptionTask(){
        return getId() + "," + TaskType.SUBTASK + "," + getName() + "," + getStatus() + ","
                + getDescription() + "," + getEpicID();
    }
}
