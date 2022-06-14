
import manager.*;
import task.*;

public class Main {

    public static void main(String[] args) {
        Manager manager = new Manager();

        Task firstTask = new Task("Погулять с собакой", "Погулять с собакой час", TaskStatus.NEW);
        manager.saveTask(firstTask);

        Task secondTask = new Task("Пойти в бассейн", "Хорошо поплавать", TaskStatus.NEW);
        manager.saveTask(secondTask);

        Epic firstEpic = new Epic("Приготовить ужин",
                "Приготовить ужин для всей семьи", TaskStatus.NEW);
        manager.saveEpics(firstEpic);

        Subtask firstSubtask = new Subtask(
                "Купить продукты", "Купить всё и ничего не забыть", TaskStatus.DONE, 1);
        manager.saveSubtask(firstSubtask);

        Subtask secondSubtask = new Subtask(
                "Купить вино"
                , "Вино белое полусладкое", TaskStatus.DONE, 2);
        manager.saveSubtask(secondSubtask);

        System.out.println("2.1 Получение списка всех задач");
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

        System.out.println("2.3 Получение по идентификатору");
        System.out.println(manager.getTaskByIdNumber(1));
        System.out.println(manager.getEpicTaskByIdNumber(3));
        System.out.println(manager.getSubTaskByIdNumber(5));

        manager.deleteTasks();
        manager.deleteEpic();
        manager.deleteSubtask();

        System.out.println("2.2 Удаление всех задач");
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

        System.out.println("2.4 Создание");
        Task newFirstTask = manager.creationTask(firstTask);
        manager.saveTask(newFirstTask);
        Task newSecondTask = manager.creationTask(secondTask);
        manager.saveTask(newSecondTask);

        Epic newEpic = manager.creationEpic(firstEpic);
        manager.saveEpics(firstEpic);
        Subtask newSub = manager.creationSubtask(firstSubtask);
        manager.saveSubtask(firstSubtask);

        System.out.println(newFirstTask);
        System.out.println(newSecondTask);
        System.out.println(newEpic);
        System.out.println(newSub);

        System.out.println("2.5 Обновление");

        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

        System.out.println("3.1 Получение списка всех подзадач определённого эпика");
        System.out.println(manager.subtaskList(1));

        System.out.println("2.6 Удаление по идентификатору");

        manager.deleteTaskById(6);
        manager.deleteTaskById(7);
        manager.deleteEpicById(8);
        manager.deleteSubtaskById(9);

        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

    }
}
