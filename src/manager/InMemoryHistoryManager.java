package manager;

import task.Task;

import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    int initialCapacity = 10;

    private List<Task> listLastTasks = new ArrayList<>(initialCapacity);

    public List<Task> getHistory() {
        return listLastTasks;
    }

    public void add(Task task) {
        if (listLastTasks.size() == initialCapacity) {
            listLastTasks.remove(0);
        }
        listLastTasks.add(task);
    }
}