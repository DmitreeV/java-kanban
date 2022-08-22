import servers.HttpTaskServer;
import servers.KVServer;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException {
        KVServer kVServer = new KVServer();
        HttpTaskServer httpTaskServer = new HttpTaskServer();
        kVServer.start();
        httpTaskServer.start();
    }
}


//import manager.*;
//import task.*;
//
//import java.time.LocalDateTime;
//
//import static task.TaskStatus.NEW;
//
//public class Main {
//    static TaskManager manager = Managers.getDefault();
//    public static void main(String[] args) {
//
//        Task firstTask = new Task("Таск 1", TaskType.TASK, NEW,
//                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
//                10);
//        manager.saveTask(firstTask);
//        Task secondTask = new Task("Таск 2", TaskType.TASK, NEW,
//                "Описание Таск 2", LocalDateTime.of(2000, 6, 10, 11, 25),
//                50);
//        manager.saveTask(secondTask);
//        Epic firstEpic = new Epic("Эпик 1", TaskType.EPIC, TaskStatus.NEW,
//                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
//                10);
//        manager.saveEpics(firstEpic);
//
//        Subtask firstSubtask = new Subtask("Сабтаск 1", TaskType.SUBTASK, NEW,
//                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
//                50, 3);
//        manager.saveSubtask(firstSubtask);
//
//        Subtask secondSubtask = new Subtask("Сабтаск 2", TaskType.SUBTASK,
//                TaskStatus.DONE, "Описание Сабтаск 2", LocalDateTime.now().minusMinutes(30), 40,
//                3);
//        manager.saveSubtask(secondSubtask);
//
//        Subtask thirdSubtask = new Subtask("Сабтаск 3", TaskType.SUBTASK,
//                TaskStatus.DONE, "Описание Сабтаск 3", LocalDateTime.now().minusMinutes(35), 45,
//                3);
//        manager.saveSubtask(thirdSubtask);
//
//        Epic secondEpic = new Epic("Эпик 2", TaskType.EPIC, TaskStatus.NEW,
//                "Описание Эпик 2", LocalDateTime.now().minusMinutes(30), 20);
//        manager.saveEpics(secondEpic);
//
//       Subtask firstSubtaskOfSecondEpic = new Subtask("Сабтаск 7.1", TaskType.SUBTASK,
//               TaskStatus.DONE, "Описание Сабтаск 7.1", LocalDateTime.now().minusMinutes(50), 50,
//               7);
//        manager.saveSubtask(firstSubtaskOfSecondEpic);
//
//        Subtask secondSubtaskOfSecondEpic = new Subtask("Сабтаск 7.2", TaskType.SUBTASK,
//                TaskStatus.DONE, "Описание Сабтаск 7.2", LocalDateTime.now().minusMinutes(50), 60,
//                7);
//        manager.saveSubtask(secondSubtaskOfSecondEpic);
//
//
//        System.out.println("2.1 Получение списка всех задач");
//        System.out.println(manager.getTasksList());
//        System.out.println(manager.getEpicsList());
//        System.out.println(manager.getSubtaskList());
//
//        System.out.println("2.3 Получение по идентификатору");
//        System.out.println(manager.getTaskByIdNumber(1));
//        System.out.println("История просмотров:");
//        printHistoryByLine();
//        System.out.println("Получение по идентификатору:");
//        System.out.println(manager.getEpicTaskByIdNumber(3));
//        System.out.println("История просмотров:");
//        printHistoryByLine();
//        System.out.println("Получение по идентификатору:");
//        System.out.println(manager.getSubTaskByIdNumber(5));
//        System.out.println("История просмотров:");
//        printHistoryByLine();
//        System.out.println("Получение по идентификатору:");
//        System.out.println(manager.getEpicTaskByIdNumber(3));
//        System.out.println("История просмотров:");
//        printHistoryByLine();
//        System.out.println("Получение по идентификатору:");
//        System.out.println(manager.getEpicTaskByIdNumber(7));
//        System.out.println("История просмотров:");
//        printHistoryByLine();
//
//        System.out.println("Удалите задачу из истории:");
//        manager.deleteSubtaskById(5);
//        System.out.println("История просмотров:");
//        printHistoryByLine();
//
//        System.out.println("Удалите эпик с тремя подзадачами:");
//        manager.deleteEpicById(3);
//        System.out.println("История просмотров:");
//        printHistoryByLine();
//
//        manager.deleteTasks();
//        manager.deleteEpics();
//        manager.deleteSubtasks();
//
//        System.out.println("2.2 Удаление всех задач");
//        System.out.println(manager.getTasksList());
//        System.out.println(manager.getEpicsList());
//        System.out.println(manager.getSubtaskList());
//
//        System.out.println("2.4 Создание");
//        Task newFirstTask = manager.creationTask(firstTask);
//        manager.saveTask(newFirstTask);
//        Task newSecondTask = manager.creationTask(secondTask);
//        manager.saveTask(newSecondTask);
//
//        Epic newEpic = manager.creationEpic(firstEpic);
//        manager.saveEpics(firstEpic);
//        Subtask newSub = manager.creationSubtask(firstSubtask);
//        manager.saveSubtask(firstSubtask);
//
//        System.out.println(newFirstTask);
//        System.out.println(newSecondTask);
//        System.out.println(newEpic);
//        System.out.println(newSub);
//
//        System.out.println("2.5 Обновление");
//
//        System.out.println(manager.getTasksList());
//        System.out.println(manager.getEpicsList());
//        System.out.println(manager.getSubtaskList());
//
//        System.out.println("3.1 Получение списка всех подзадач определённого эпика");
//        System.out.println(manager.subtaskList(11));
//
//        System.out.println("2.6 Удаление по идентификатору");
//
//        manager.deleteTaskById(9);
//        manager.deleteTaskById(10);
//        manager.deleteEpicById(11);
//
//
//        System.out.println(manager.getTasksList());
//        System.out.println(manager.getEpicsList());
//        System.out.println(manager.getSubtaskList());
//
//    }
//    private static void printHistoryByLine() { //печатает историю построчно
//        for (Task line : manager.getHistory())
//        {
//            System.out.println(line);
//        }
//    }
//}
