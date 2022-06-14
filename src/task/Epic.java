package task;

import java.util.ArrayList;
import java.util.List;

public class Epic extends Task {

    private List<Integer> subtasks = new ArrayList<>();

    public void addIdOfSubtasks(Subtask subtask) {
        subtasks.add(subtask.getId());
    }

    public List<Integer> getSubtasksID() {
        return subtasks;
    }

    public Epic(String name, String description, TaskStatus status) {
        super(name, description, status);
    }

    public Epic(String name, TaskStatus status, String description, int id
            , List<Integer> subtasks) { // конструктор для обновления эпика
        super(name, description, status, id);
        this.subtasks = subtasks;
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