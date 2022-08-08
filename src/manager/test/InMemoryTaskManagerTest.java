package manager.test;

import manager.Managers;
import manager.TaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest {
    TaskManager manager = Managers.getDefault();

    @Override
    TaskManager createTaskManager() {
        return manager;
    }
}