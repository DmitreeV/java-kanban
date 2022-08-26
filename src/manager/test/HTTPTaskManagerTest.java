package manager.test;

import manager.HttpTaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.KVServer;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskManagerTest extends TaskManagerTest<HttpTaskManager> {
    KVServer kvServer;

    @Override
    public HttpTaskManager createTaskManager() {
        return new HttpTaskManager("http://localhost:8078", "key");
    }

    @BeforeEach
    public void startServer() throws IOException {
        kvServer = new KVServer();
        kvServer.start();
    }

    @AfterEach
    public void stopServer() {
        kvServer.stop();
    }

    @Test
    public void testLoadFromEmptyServer() {

        saveTasks();

        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();

        HttpTaskManager emptyManager = HttpTaskManager.load("http://localhost:8078", "key");

        assertEquals(0, emptyManager.getTasksList().size());
        assertEquals(0, emptyManager.getSubtaskList().size());
        assertEquals(0, emptyManager.getEpicsList().size());
        assertEquals(0, emptyManager.getHistory().size());

        assertNotNull(emptyManager);
    }

    @Test
    public void testLoadWithEpicSubtasks() {

        saveTasks();

        manager.getEpicTaskByIdNumber(3);
        manager.getEpicTaskByIdNumber(4);

        HttpTaskManager oneEpicManager = HttpTaskManager.load("http://localhost:8078", "key");

        assertEquals(3, oneEpicManager.getEpicsList().size());
        assertEquals(2, oneEpicManager.getHistory().size());
        assertEquals(2, oneEpicManager.getEpicTaskByIdNumber(3).getSubtasks().size());
    }

    @Test
    public void testLoadWithEmptyHistory() {

        saveTasks();

        HttpTaskManager emptyHistoryManager = HttpTaskManager.load("http://localhost:8078", "key");

        assertEquals(2, emptyHistoryManager.getTasksList().size());
        assertEquals(3, emptyHistoryManager.getEpicsList().size());
        assertEquals(3, emptyHistoryManager.getSubtaskList().size());
        assertEquals(0, emptyHistoryManager.getHistory().size());
    }
}
