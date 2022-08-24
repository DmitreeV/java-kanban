package manager;

import java.io.IOException;

public class Managers {
    public static TaskManager getDefaultInMemoryTaskManager() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault(String url) {
        return new HTTPTaskManager(url);
    }
}