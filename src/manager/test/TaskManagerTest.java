package manager.test;

import manager.HistoryManager;
import manager.InMemoryHistoryManager;
import manager.TaskManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import task.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;
import static task.TaskStatus.*;

abstract class TaskManagerTest<T extends TaskManager> {
    public T manager;

    abstract T createTaskManager();

    Task firstTask;
    Task secondTask;
    Epic firstEpic;
    Epic secondEpic;
    Epic thirdEpic;
    Subtask firstSubtask;
    Subtask secondSubtask;
    Subtask thirdSubtask;
    HistoryManager historyManager;

    @BeforeEach
    public void allTasksForTests() {

        manager = createTaskManager();

        firstTask = new Task("Таск 1", NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10);

        secondTask = new Task("Таск 2", NEW,
                "Описание Таск 2", LocalDateTime.of(2000, 6, 10, 11, 25),
                50);

        firstEpic = new Epic("Эпик 1", TaskStatus.NEW,
                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
                10);

        secondEpic = new Epic("Эпик 2", TaskStatus.NEW,
                "Описание Эпик 2", LocalDateTime.now().minusMinutes(30), 20);

        firstSubtask = new Subtask("Сабтаск 1", NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3);

        secondSubtask = new Subtask("Сабтаск 2",
                TaskStatus.DONE, "Описание Сабтаск 2", LocalDateTime.now().minusMinutes(30), 40,
                3);

        thirdSubtask = new Subtask("Сабтаск 3",
                TaskStatus.DONE, "Описание Сабтаск 3",
                LocalDateTime.of(2015, 6, 14, 11, 30), 40, 4);

        thirdEpic = new Epic("Эпик 3", NEW,
                "Для теста статусов", LocalDateTime.of(2020, 2, 20, 20, 20),
                20);
    }

    void saveTasks(){
        manager.saveTask(firstTask);
        manager.saveTask(secondTask);
        manager.saveEpic(firstEpic);
        manager.saveEpic(secondEpic);
        manager.saveSubtask(firstSubtask);
        manager.saveSubtask(secondSubtask);
        manager.saveSubtask(thirdSubtask);
        manager.saveEpic(thirdEpic);
    }

    @Test
    void testSaveTask() {

        saveTasks();
        //a. Со стандартным поведением.
        assertEquals(manager.getTaskByIdNumber(1).getName(), firstTask.getName());
        assertEquals(manager.getTaskByIdNumber(1).getTaskType(), firstTask.getTaskType());
        assertEquals(manager.getTaskByIdNumber(1).getStatus(), firstTask.getStatus());
        assertEquals(manager.getTaskByIdNumber(1).getDescription(), firstTask.getDescription());
        assertEquals(manager.getTaskByIdNumber(1).getStartTime(), firstTask.getStartTime());
        assertEquals(manager.getTaskByIdNumber(1).getDuration(), firstTask.getDuration());

        //b. С пустым списком задач.
        manager.deleteTasks();
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testSaveEpic() {

        saveTasks();
        //a. Со стандартным поведением.
        assertEquals(manager.getEpicTaskByIdNumber(3).getName(), firstEpic.getName());
        assertEquals(manager.getEpicTaskByIdNumber(3).getTaskType(), firstEpic.getTaskType());
        assertEquals(manager.getEpicTaskByIdNumber(3).getStatus(), firstEpic.getStatus());
        assertEquals(manager.getEpicTaskByIdNumber(3).getDescription(), firstEpic.getDescription());
        assertEquals(manager.getEpicTaskByIdNumber(3).getStartTime(), firstEpic.getStartTime());
        assertEquals(manager.getEpicTaskByIdNumber(3).getDuration(), firstEpic.getDuration());

        //b. С пустым списком задач.
        manager.deleteEpics();
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testSaveSubtask() {

        saveTasks();
        //a. Со стандартным поведением.
        assertEquals(manager.getSubTaskByIdNumber(5).getName(), firstSubtask.getName());
        assertEquals(manager.getSubTaskByIdNumber(5).getTaskType(), firstSubtask.getTaskType());
        assertEquals(manager.getSubTaskByIdNumber(5).getStatus(), firstSubtask.getStatus());
        assertEquals(manager.getSubTaskByIdNumber(5).getDescription(), firstSubtask.getDescription());
        assertEquals(manager.getSubTaskByIdNumber(5).getStartTime(), firstSubtask.getStartTime());
        assertEquals(manager.getSubTaskByIdNumber(5).getDuration(), firstSubtask.getDuration());

        //Проверка ID сабтаски
        assertEquals(manager.getEpicTaskByIdNumber(3).getId(), firstSubtask.getEpicID());

        //Проверка статуса и временных параметров эпика относительно сабтаски
        Subtask testSubtask = new Subtask("Саб таск ", NEW,
                "Описание Саб таск ", LocalDateTime.of(2020, 2, 20, 20, 20),
                50, 8);
        manager.saveSubtask(testSubtask);
        assertEquals(manager.getEpicTaskByIdNumber(8).getStatus(), testSubtask.getStatus());
        assertEquals(manager.getEpicTaskByIdNumber(8).getStartTime(), testSubtask.getStartTime());
        assertEquals(manager.getEpicTaskByIdNumber(8).getDuration(), testSubtask.getDuration());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testGetTasksList() {

        saveTasks();
        //a. Со стандартным поведением.
        List<Task> testList1 = new ArrayList<>(List.of(firstTask, secondTask));
        List<Task> testList2 = manager.getTasksList();
        assertEquals(testList1, testList2);

        //b. С пустым списком задач.
        manager.deleteTasks();
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testGetEpicsList() {

        saveTasks();
        //a. Со стандартным поведением.
        List<Epic> testList1 = new ArrayList<>(List.of(firstEpic, secondEpic, thirdEpic));
        List<Epic> testList2 = manager.getEpicsList();
        assertEquals(testList1, testList2);

        //b. С пустым списком задач.
        manager.deleteEpics();
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testGetSubtaskList() {

        saveTasks();
        //a. Со стандартным поведением.
        List<Subtask> testList1 = new ArrayList<>(List.of(firstSubtask, secondSubtask, thirdSubtask));
        List<Subtask> testList2 = manager.getSubtaskList();
        assertEquals(testList1, testList2);

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testDeleteTasks() {

        saveTasks();
        //a. Со стандартным поведением.
        manager.deleteTasks();
        List<Task> testList = manager.getTasksList();
        assertEquals(0, testList.size());

        //b. С пустым списком задач.
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testDeleteEpics() {

        saveTasks();
        //a. Со стандартным поведением.
        manager.deleteEpics();
        List<Epic> testList = manager.getEpicsList();
        assertEquals(0, testList.size());

        //b. С пустым списком задач.
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testDeleteSubtasks() {

        saveTasks();
        //a. Со стандартным поведением.
        manager.deleteSubtasks();
        List<Subtask> testList = manager.getSubtaskList();
        assertEquals(0, testList.size());

        //Проверка статуса эпика относительно сабтаски (после удаления)
        assertEquals(manager.getEpicTaskByIdNumber(3).getStatus(), NEW);
        assertEquals(manager.getEpicTaskByIdNumber(8).getStatus(), NEW);

        //b. С пустым списком задач.
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testGetTaskByIdNumber() {
        saveTasks();
        //c. С неверным идентификатором задачи
        assertNull(manager.getTaskByIdNumber(10));

        //a. Со стандартным поведением.
        assertEquals(manager.getTaskByIdNumber(2).getName(), secondTask.getName());
        assertEquals(manager.getTaskByIdNumber(2).getTaskType(), secondTask.getTaskType());
        assertEquals(manager.getTaskByIdNumber(2).getStatus(), secondTask.getStatus());
        assertEquals(manager.getTaskByIdNumber(2).getDescription(), secondTask.getDescription());
        assertEquals(manager.getTaskByIdNumber(2).getStartTime(), secondTask.getStartTime());
        assertEquals(manager.getTaskByIdNumber(2).getDuration(), secondTask.getDuration());

        //b. С пустым списком задач.
        manager.deleteTasks();
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testGetEpicTaskByIdNumber() {
        saveTasks();
        //c. С неверным идентификатором задачи
        assertNull(manager.getEpicTaskByIdNumber(10));

        //a. Со стандартным поведением.
        assertEquals(manager.getEpicTaskByIdNumber(4).getName(), secondEpic.getName());
        assertEquals(manager.getEpicTaskByIdNumber(4).getTaskType(), secondEpic.getTaskType());
        assertEquals(manager.getEpicTaskByIdNumber(4).getStatus(), secondEpic.getStatus());
        assertEquals(manager.getEpicTaskByIdNumber(4).getDescription(), secondEpic.getDescription());
        assertEquals(manager.getEpicTaskByIdNumber(4).getStartTime(), secondEpic.getStartTime());
        assertEquals(manager.getEpicTaskByIdNumber(4).getDuration(), secondEpic.getDuration());

        //b. С пустым списком задач.
        manager.deleteEpics();
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testGetSubTaskByIdNumber() {
        saveTasks();
        //c. С неверным идентификатором задачи
        assertNull(manager.getSubTaskByIdNumber(10));

        //a. Со стандартным поведением.
        assertEquals(manager.getSubTaskByIdNumber(6).getName(), secondSubtask.getName());
        assertEquals(manager.getSubTaskByIdNumber(6).getTaskType(), secondSubtask.getTaskType());
        assertEquals(manager.getSubTaskByIdNumber(6).getStatus(), secondSubtask.getStatus());
        assertEquals(manager.getSubTaskByIdNumber(6).getDescription(), secondSubtask.getDescription());
        assertEquals(manager.getSubTaskByIdNumber(6).getStartTime(), secondSubtask.getStartTime());
        assertEquals(manager.getSubTaskByIdNumber(6).getDuration(), secondSubtask.getDuration());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testCreationTask() {
        saveTasks();
        //a. Со стандартным поведением.
        Task testTask = new Task("Таск 1", NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10, 1);
        manager.creationTask(testTask);
        assertEquals(manager.getTaskByIdNumber(1).getId(), testTask.getId());
        assertEquals(manager.getTaskByIdNumber(1).getName(), testTask.getName());
        assertEquals(manager.getTaskByIdNumber(1).getTaskType(), testTask.getTaskType());
        assertEquals(manager.getTaskByIdNumber(1).getStatus(), testTask.getStatus());
        assertEquals(manager.getTaskByIdNumber(1).getDescription(), testTask.getDescription());
        assertEquals(manager.getTaskByIdNumber(1).getStartTime(), testTask.getStartTime());
        assertEquals(manager.getTaskByIdNumber(1).getDuration(), testTask.getDuration());

        //b. С пустым списком задач.
        manager.deleteTasks();
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testCreationEpic() {
        saveTasks();
        //a. Со стандартным поведением.
        Epic testEpic = new Epic("Эпик 1", IN_PROGRESS,
                "Описание Эпик 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                90);
        manager.creationEpic(testEpic);
        assertEquals(manager.getEpicTaskByIdNumber(3).getName(), testEpic.getName());
        assertEquals(manager.getEpicTaskByIdNumber(3).getTaskType(), testEpic.getTaskType());
        assertEquals(manager.getEpicTaskByIdNumber(3).getStatus(), testEpic.getStatus());
        assertEquals(manager.getEpicTaskByIdNumber(3).getDescription(), testEpic.getDescription());
        assertEquals(manager.getEpicTaskByIdNumber(3).getStartTime(), testEpic.getStartTime());
        assertEquals(manager.getEpicTaskByIdNumber(3).getDuration(), testEpic.getDuration());

        //b. С пустым списком задач.
        manager.deleteEpics();
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testCreationSubtask() {
        saveTasks();
        //a. Со стандартным поведением.
        Subtask testSubtask = new Subtask("Сабтаск 1", NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3);
        manager.creationSubtask(testSubtask);
        assertEquals(manager.getSubTaskByIdNumber(5).getName(), testSubtask.getName());
        assertEquals(manager.getSubTaskByIdNumber(5).getTaskType(), testSubtask.getTaskType());
        assertEquals(manager.getSubTaskByIdNumber(5).getStatus(), testSubtask.getStatus());
        assertEquals(manager.getSubTaskByIdNumber(5).getDescription(), testSubtask.getDescription());
        assertEquals(manager.getSubTaskByIdNumber(5).getStartTime(), testSubtask.getStartTime());
        assertEquals(manager.getSubTaskByIdNumber(5).getDuration(), testSubtask.getDuration());

        //Проверка статуса и временных параметров эпика относительно сабтаски
        Subtask testSubtask1 = new Subtask("Саб таск ", NEW,
                "Описание Саб таск ", LocalDateTime.of(2020, 2, 20, 20, 20),
                50, 8);
        manager.saveSubtask(testSubtask1);
        assertEquals(manager.getEpicTaskByIdNumber(8).getStatus(), testSubtask1.getStatus());
        assertEquals(manager.getEpicTaskByIdNumber(8).getStartTime(), testSubtask1.getStartTime());
        assertEquals(manager.getEpicTaskByIdNumber(8).getDuration(), testSubtask1.getDuration());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testUpdateTask() {
        saveTasks();
        //a. Со стандартным поведением.
        Task testTask = new Task("Таск 1", NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10L, 1);
        manager.updateTask(testTask);
        assertEquals(manager.getTaskByIdNumber(1).getId(), testTask.getId());
        assertEquals(manager.getTaskByIdNumber(1).getName(), testTask.getName());
        assertEquals(manager.getTaskByIdNumber(1).getTaskType(), testTask.getTaskType());
        assertEquals(manager.getTaskByIdNumber(1).getStatus(), testTask.getStatus());
        assertEquals(manager.getTaskByIdNumber(1).getDescription(), testTask.getDescription());
        assertEquals(manager.getTaskByIdNumber(1).getStartTime(), testTask.getStartTime());
        assertEquals(manager.getTaskByIdNumber(1).getDuration(), testTask.getDuration());

        //b. С пустым списком задач.
        manager.deleteTasks();
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testUpdateEpic() {
        saveTasks();
        List<Integer> list = new ArrayList<>();
        //a. Со стандартным поведением.
        Epic testEpic = new Epic("Эпик 1", IN_PROGRESS,
                "Описание Эпик 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                90, 3, list);
        manager.updateEpic(testEpic);
        assertEquals(manager.getEpicTaskByIdNumber(3).getName(), testEpic.getName());
        assertEquals(manager.getEpicTaskByIdNumber(3).getTaskType(), testEpic.getTaskType());
        assertEquals(manager.getEpicTaskByIdNumber(3).getStatus(), testEpic.getStatus());
        assertEquals(manager.getEpicTaskByIdNumber(3).getDescription(), testEpic.getDescription());
        assertEquals(manager.getEpicTaskByIdNumber(3).getStartTime(), testEpic.getStartTime());
        assertEquals(manager.getEpicTaskByIdNumber(3).getDuration(), testEpic.getDuration());

        //b. С пустым списком задач.
        manager.deleteEpics();
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testUpdateSubtask() {
        saveTasks();
        //a. Со стандартным поведением.
        Subtask testSubtask = new Subtask("Сабтаск 1", DONE,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3);
        manager.updateSubtask(testSubtask);
        assertEquals(manager.getSubTaskByIdNumber(5).getName(), testSubtask.getName());
        assertEquals(manager.getSubTaskByIdNumber(5).getTaskType(), testSubtask.getTaskType());
        assertEquals(manager.getSubTaskByIdNumber(5).getDescription(), testSubtask.getDescription());

        //Проверка статуса и временных параметров эпика относительно сабтаски
        Subtask testSubtask1 = new Subtask("Саб таск ", NEW,
                "Описание Саб таск ", LocalDateTime.of(2020, 2, 20, 20, 20),
                50, 8);
        manager.saveSubtask(testSubtask1);
        assertEquals(manager.getEpicTaskByIdNumber(8).getStatus(), testSubtask1.getStatus());
        assertEquals(manager.getEpicTaskByIdNumber(8).getStartTime(), testSubtask1.getStartTime());
        assertEquals(manager.getEpicTaskByIdNumber(8).getDuration(), testSubtask1.getDuration());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testDeleteTaskById() {
        saveTasks();
        //c. С неверным идентификатором задачи
        manager.deleteTaskById(10);
        assertEquals(2, manager.getTasksList().size());

        //a. Со стандартным поведением.
        manager.deleteTaskById(1);
        manager.deleteTaskById(2);
        assertEquals(0, manager.getTasksList().size());

        //b. С пустым списком задач.
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testDeleteEpicById() {
        saveTasks();
        //c. С неверным идентификатором задачи
        manager.deleteEpicById(10);
        assertEquals(3, manager.getEpicsList().size());

        //a. Со стандартным поведением.
        manager.deleteEpics();
        assertEquals(0, manager.getEpicsList().size());

        //b. С пустым списком задач.
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testDeleteSubtaskById() {
        saveTasks();

        //c. С неверным идентификатором задачи
        manager.deleteSubtaskById(10);
        assertEquals(3, manager.getSubtaskList().size());

        //a. Со стандартным поведением.
        manager.deleteSubtaskById(firstSubtask.getId());
        manager.deleteSubtaskById(secondSubtask.getId());
        manager.deleteSubtaskById(thirdSubtask.getId());
        assertEquals(0, manager.getSubtaskList().size());

        //Проверка статуса эпика относительно сабтаски (после удаления)
        assertEquals(manager.getEpicTaskByIdNumber(3).getStatus(), NEW);
        assertEquals(manager.getEpicTaskByIdNumber(8).getStatus(), NEW);

        //b. С пустым списком задач.
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testSubtaskList() {
        saveTasks();
        //c. С неверным идентификатором задачи
        assertNull(manager.getEpicTaskByIdNumber(10));

        //a. Со стандартным поведением.
        assertEquals(firstEpic.getId(), firstSubtask.getEpicID());
        assertEquals(firstEpic.getId(), secondSubtask.getEpicID());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtaskList().size());
    }

    @Test
    void testChangeEpicStatusAllSubtaskStatusNew() {
        saveTasks();
        //b. Все подзадачи со статусом NEW
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1",
                NEW, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2",
                NEW, "Описание Саб 2",
                LocalDateTime.of(2016, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub2);

        assertEquals(NEW, manager.getEpicTaskByIdNumber(8).getStatus());

        //a. Пустой список подзадач
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtaskList().size());
    }

    @Test
    void testChangeEpicStatusAllSubtaskStatusDone() {
        saveTasks();
        //c. Все подзадачи со статусом DONE
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1",
                DONE, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2",
                DONE, "Описание Саб 2",
                LocalDateTime.of(2016, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub2);

        assertEquals(DONE, manager.getEpicTaskByIdNumber(8).getStatus());

        //a. Пустой список подзадач
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtaskList().size());
    }

    @Test
    void testChangeEpicStatusSubtaskStatusDoneNew() {
        saveTasks();
        //d. Подзадачи со статусом DONE и NEW
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1",
                DONE, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2",
                NEW, "Описание Саб 2",
                LocalDateTime.of(2016, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub2);

        assertEquals(IN_PROGRESS, manager.getEpicTaskByIdNumber(8).getStatus());

        //a. Пустой список подзадач
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtaskList().size());
    }

    @Test
    void testChangeEpicStatusAllSubtaskStatusInProgress() {
        saveTasks();
        //b. Все подзадачи со статусом IN_PROGRESS
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1",
                IN_PROGRESS, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2",
                IN_PROGRESS, "Описание Саб 2",
                LocalDateTime.of(2016, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub2);

        assertEquals(IN_PROGRESS, manager.getEpicTaskByIdNumber(8).getStatus());

        //a. Пустой список подзадач
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtaskList().size());
    }

    @Test
    void testGetPrioritizedTasks() {
        saveTasks();
        //a. Со стандартным поведением.
        Set<Task> expected = new TreeSet<>((o1, o2) -> {
            if ((o1.getStartTime() != null) && (o2.getStartTime() != null)) {
                return o1.getStartTime().compareTo(o2.getStartTime());
            } else if (o1.getStartTime() == null) {
                return 1;
            } else if (o2.getStartTime() == null) {
                return -1;
            } else {
                return 0;
            }
        });
        expected.addAll(manager.getTasksList());
        expected.addAll(manager.getSubtaskList());

        Set<Task> actual = manager.getterPrioritizedTasks();

        assertEquals(expected, actual);

        //b. С пустым списком задач.
        manager.deleteTasks();
        manager.deleteEpics();
        manager.deleteSubtasks();
        assertTrue(manager.getTasksList().isEmpty());
        assertTrue(manager.getEpicsList().isEmpty());
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testGetHistory() {
        saveTasks();
        historyManager = new InMemoryHistoryManager();

        //a. Пустая история задач
        assertTrue(historyManager.getHistory().isEmpty());

        historyManager.add(firstTask);
        historyManager.add(secondTask);
        historyManager.add(firstEpic);
        historyManager.add(firstEpic);
        historyManager.add(firstSubtask);
        historyManager.add(thirdSubtask);

        //b. Дублирование
        assertEquals(5, historyManager.getHistory().size());

        //с. Удаление из истории: начало, середина, конец
        historyManager.remove(1);
        historyManager.remove(3);
        historyManager.remove(7);

        assertEquals(2, historyManager.getHistory().size());
    }
}