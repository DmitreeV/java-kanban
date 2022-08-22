package manager;

public class Managers {
    public static TaskManager getDefault1() {
        return new InMemoryTaskManager();
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }

    public static TaskManager getDefault() {
        return new HTTPTaskManager("http://localhost:8078");
    }

}