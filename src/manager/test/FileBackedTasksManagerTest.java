package manager.test;

import manager.FileBackedTasksManager;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTasksManagerTest extends TaskManagerTest<FileBackedTasksManager> {

    @Override
    public FileBackedTasksManager createTaskManager() {
        return new FileBackedTasksManager(new File("testFiles.csv"));
    }

    @Test
    public void testLoadFromFile() {

        //a. Со стандартным поведением.
        FileBackedTasksManager standartManager = FileBackedTasksManager.loadFromFile(new File(
                "src/manager/test/testFiles.csv"));

        assertEquals(2, standartManager.getEpicsList().size());
        assertEquals(2, standartManager.getTasksList().size());
        assertEquals(2, standartManager.getSubtaskList().size());
        assertEquals(4, standartManager.getHistory().size());
    }

    @Test
    public void testLoadWithEmptyEpicAndSubtaskList() {
        //a. Пустой список Epic и Subtask задач.
        FileBackedTasksManager managerWithEmptyEpicsList = FileBackedTasksManager.loadFromFile(new File(
                "src/manager/test/testFiles2.csv"));

        assertEquals(0, managerWithEmptyEpicsList.getEpicsList().size());
        assertEquals(1, managerWithEmptyEpicsList.getTasksList().size());
        assertEquals(1, managerWithEmptyEpicsList.getHistory().size());
    }

    @Test
    public void testLoadWithEmptyAllTasksList() {
        //a. Пустой список задач.
        FileBackedTasksManager managerWithEmptyAllTasksList = FileBackedTasksManager.loadFromFile(new File(
                "src/manager/test/testFiles1.csv"));

        assertEquals(0, managerWithEmptyAllTasksList.getEpicsList().size());
        assertEquals(0, managerWithEmptyAllTasksList.getTasksList().size());
        assertEquals(0, managerWithEmptyAllTasksList.getSubtaskList().size());
    }

    @Test
    public void testLoadWithEmptyHistory() {
        //c. Пустой список истории.
        FileBackedTasksManager managerWithEmptyHistoryList = FileBackedTasksManager.loadFromFile(new File(
                "src/manager/test/testFiles3.csv"));

        assertEquals(1, managerWithEmptyHistoryList.getEpicsList().size());
        assertEquals(1, managerWithEmptyHistoryList.getTasksList().size());
        assertEquals(1, managerWithEmptyHistoryList.getSubtaskList().size());
        assertEquals(0, managerWithEmptyHistoryList.getHistory().size());
    }
}