package manager.test;

import manager.Managers;
import manager.TaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest {
    TaskManager manager = Managers.getDefault1();

    @Override
    TaskManager createTaskManager() {
        return manager;
    }
}