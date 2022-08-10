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

        firstTask = manager.creationTask(new Task("Таск 1", TaskType.TASK, NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10));
        manager.saveTask(firstTask);

        secondTask = manager.creationTask(new Task("Таск 2", TaskType.TASK, NEW,
                "Описание Таск 2", LocalDateTime.of(2000, 6, 10, 11, 25),
                50));
        manager.saveTask(secondTask);

        firstEpic = manager.creationEpic(new Epic("Эпик 1", TaskType.EPIC, TaskStatus.NEW,
                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
                10));
        manager.saveEpics(firstEpic);

        secondEpic = manager.creationEpic(new Epic("Эпик 2", TaskType.EPIC, TaskStatus.NEW,
                "Описание Эпик 2", LocalDateTime.now().minusMinutes(30), 20));
        manager.saveEpics(secondEpic);

        firstSubtask = manager.creationSubtask(new Subtask("Сабтаск 1", TaskType.SUBTASK, NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3));
        manager.saveSubtask(firstSubtask);

        secondSubtask = manager.creationSubtask(new Subtask("Сабтаск 2", TaskType.SUBTASK,
                TaskStatus.DONE, "Описание Сабтаск 2", LocalDateTime.now().minusMinutes(30), 40,
                3));
        manager.saveSubtask(secondSubtask);

        thirdSubtask = manager.creationSubtask(new Subtask("Сабтаск 3", TaskType.SUBTASK,
                TaskStatus.DONE, "Описание Сабтаск 3",
                LocalDateTime.of(2015, 6, 14, 11, 30), 40, 4));
        manager.saveSubtask(thirdSubtask);

        thirdEpic = manager.creationEpic(new Epic("Эпик 3", TaskType.EPIC, NEW,
                "Для теста статусов", LocalDateTime.of(2020, 2, 20, 20, 20),
                20));
        manager.saveEpics(thirdEpic);
    }

    @Test
    void testSaveTask() {

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getTaskByIdNumber(10));

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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getEpicTaskByIdNumber(10));

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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getSubTaskByIdNumber(10));

        //a. Со стандартным поведением.
        assertEquals(manager.getSubTaskByIdNumber(5).getName(), firstSubtask.getName());
        assertEquals(manager.getSubTaskByIdNumber(5).getTaskType(), firstSubtask.getTaskType());
        assertEquals(manager.getSubTaskByIdNumber(5).getStatus(), firstSubtask.getStatus());
        assertEquals(manager.getSubTaskByIdNumber(5).getDescription(), firstSubtask.getDescription());
        assertEquals(manager.getSubTaskByIdNumber(5).getStartTime(), firstSubtask.getStartTime());
        assertEquals(manager.getSubTaskByIdNumber(5).getDuration(), firstSubtask.getDuration());
        //Проверка ID сабтаски
        assertEquals(manager.getEpicTaskByIdNumber(3).getId(), firstSubtask.getEpicID());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testGetTasksList() {
        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getTaskByIdNumber(10));

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
        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getEpicTaskByIdNumber(10));

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
        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getSubTaskByIdNumber(10));

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

        //a. Со стандартным поведением.
        manager.deleteTasks();
        List<Task> testList = manager.getTasksList();
        assertEquals(0, testList.size());

        //b. С пустым списком задач.
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testDeleteEpics() {

        //a. Со стандартным поведением.
        manager.deleteEpics();
        List<Epic> testList = manager.getEpicsList();
        assertEquals(0, testList.size());

        //b. С пустым списком задач.
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testDeleteSubtasks() {

        //a. Со стандартным поведением.
        manager.deleteSubtasks();
        List<Subtask> testList = manager.getSubtaskList();
        assertEquals(0, testList.size());

        //b. С пустым списком задач.
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testGetTaskByIdNumber() {
        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getTaskByIdNumber(10));

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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getEpicTaskByIdNumber(10));

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
        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getSubTaskByIdNumber(10));

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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getTaskByIdNumber(10));

        //a. Со стандартным поведением.
        Task testTask = new Task("Таск 1", TaskType.TASK, NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10L, 1);
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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getEpicTaskByIdNumber(10));

        //a. Со стандартным поведением.
        Epic testEpic = new Epic("Эпик 1", TaskType.EPIC, IN_PROGRESS,
                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
                10);
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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getSubTaskByIdNumber(10));

        //a. Со стандартным поведением.
        Subtask testSubtask = new Subtask("Сабтаск 1", TaskType.SUBTASK, NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3);
        manager.creationSubtask(testSubtask);
        assertEquals(manager.getSubTaskByIdNumber(5).getName(), testSubtask.getName());
        assertEquals(manager.getSubTaskByIdNumber(5).getTaskType(), testSubtask.getTaskType());
        assertEquals(manager.getSubTaskByIdNumber(5).getStatus(), testSubtask.getStatus());
        assertEquals(manager.getSubTaskByIdNumber(5).getDescription(), testSubtask.getDescription());
        assertEquals(manager.getSubTaskByIdNumber(5).getStartTime(), testSubtask.getStartTime());
        assertEquals(manager.getSubTaskByIdNumber(5).getDuration(), testSubtask.getDuration());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testUpdateTask() {

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getTaskByIdNumber(10));

        //a. Со стандартным поведением.
        Task testTask = new Task("Таск 1", TaskType.TASK, NEW,
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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getEpicTaskByIdNumber(10));

        List<Integer> list = new ArrayList<>();
        //a. Со стандартным поведением.
        Epic testEpic = new Epic("Эпик 1", TaskType.EPIC, IN_PROGRESS,
                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
                10, 3, list);
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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getSubTaskByIdNumber(10));

        //a. Со стандартным поведением.
        Subtask testSubtask = new Subtask("Сабтаск 1", TaskType.SUBTASK, NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3);
        manager.updateSubtask(testSubtask);
        assertEquals(manager.getSubTaskByIdNumber(5).getName(), testSubtask.getName());
        assertEquals(manager.getSubTaskByIdNumber(5).getTaskType(), testSubtask.getTaskType());
        assertEquals(manager.getSubTaskByIdNumber(5).getStatus(), testSubtask.getStatus());
        assertEquals(manager.getSubTaskByIdNumber(5).getDescription(), testSubtask.getDescription());
        assertEquals(manager.getSubTaskByIdNumber(5).getStartTime(), testSubtask.getStartTime());
        assertEquals(manager.getSubTaskByIdNumber(5).getDuration(), testSubtask.getDuration());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testDeleteTaskById() {

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.deleteTaskById(10));

        //a. Со стандартным поведением.
        manager.deleteTaskById(1);
        manager.deleteTaskById(2);
        assertEquals(0, manager.getTasksList().size());

        //b. С пустым списком задач.
        assertTrue(manager.getTasksList().isEmpty());
    }

    @Test
    void testDeleteEpicById() {

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.deleteEpicById(20));

        //a. Со стандартным поведением.
        manager.deleteEpics();
        assertEquals(0, manager.getEpicsList().size());

        //b. С пустым списком задач.
        assertTrue(manager.getEpicsList().isEmpty());
    }

    @Test
    void testDeleteSubtaskById() {

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.deleteSubtaskById(10));

        //a. Со стандартным поведением.
        manager.deleteSubtaskById(firstSubtask.getId());
        manager.deleteSubtaskById(secondSubtask.getId());
        manager.deleteSubtaskById(thirdSubtask.getId());
        assertEquals(0, manager.getSubtaskList().size());

        //b. С пустым списком задач.
        assertTrue(manager.getSubtaskList().isEmpty());
    }

    @Test
    void testSubtaskList() {

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getSubTaskByIdNumber(10));

        //a. Со стандартным поведением.
        assertEquals(firstEpic.getId(), firstSubtask.getEpicID());
        assertEquals(firstEpic.getId(), secondSubtask.getEpicID());

        //b. С пустым списком задач.
        manager.deleteSubtasks();
        assertEquals(0, manager.getSubtaskList().size());
    }

    @Test
    void testChangeEpicStatusAllSubtaskStatusNew() {
        //b. Все подзадачи со статусом NEW
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1", TaskType.SUBTASK,
                NEW, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2", TaskType.SUBTASK,
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
        //c. Все подзадачи со статусом DONE
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1", TaskType.SUBTASK,
                DONE, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2", TaskType.SUBTASK,
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
        //d. Подзадачи со статусом DONE и NEW
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1", TaskType.SUBTASK,
                DONE, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2", TaskType.SUBTASK,
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
        //b. Все подзадачи со статусом IN_PROGRESS
        Subtask sub1 = manager.creationSubtask(new Subtask("Саб 1", TaskType.SUBTASK,
                IN_PROGRESS, "Описание Саб 1",
                LocalDateTime.of(2017, 7, 18, 11, 30), 40, 8));
        manager.saveSubtask(sub1);

        Subtask sub2 = manager.creationSubtask(new Subtask("Саб 2", TaskType.SUBTASK,
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

        //c. С неверным идентификатором задачи
        assertThrows(NullPointerException.class, () -> manager.getTaskByIdNumber(10));
        assertThrows(NullPointerException.class, () -> manager.getEpicTaskByIdNumber(11));
        assertThrows(NullPointerException.class, () -> manager.getSubTaskByIdNumber(12));

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