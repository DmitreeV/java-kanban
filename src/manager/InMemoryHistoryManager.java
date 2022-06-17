package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private List<Task> listLastTasks = new ArrayList<>(10);

    public List<Task> getHistory() {
        return listLastTasks;
    }

    public void add(Task task) {
        if (listLastTasks.size() == 10) {
            listLastTasks.remove(0);
        }
        listLastTasks.add(task);
    }
}