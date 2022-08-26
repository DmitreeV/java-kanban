package manager.test;

import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;

class InMemoryTaskManagerTest extends TaskManagerTest {
    //TaskManager manager = Managers.getDefaultInMemoryTaskManager();

    @Override
    public InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }
}