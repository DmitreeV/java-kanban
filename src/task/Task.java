package task;

public class Task {

    protected int id;
    protected String name;
    protected String description;
    protected TaskStatus status;
    protected TaskType taskType;

    public Task(String name, TaskType taskType, TaskStatus status, String description) {
        this.taskType = taskType;
        this.name = name;
        this.description = description;
        this.status = status;
    }

    public Task(String name, TaskType taskType, TaskStatus status, String description, int id) {  // конструктор для обновления эпика

        this(name, taskType, status, description);
        this.id = id;
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
                '}';
    }

    public String getDescriptionTask(){
        return getId() + "," + TaskType.TASK + "," + getName() + "," + getStatus() + ","
                + getDescription();
    }
}
