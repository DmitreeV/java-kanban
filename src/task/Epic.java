package task;

import manager.FileBackedTasksManager;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtasks = new ArrayList<>();

    public Epic(String name, FileBackedTasksManager.TaskType taskType, TaskStatus status, String description) {
        super(name, taskType, status, description);
    }

    public Epic(String name, FileBackedTasksManager.TaskType taskType, TaskStatus status, String description, int id,
             List<Integer> subtasks) { // конструктор для обновления эпика
        super(name, taskType, status, description, id);
        this.subtasks = subtasks;
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
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }
}