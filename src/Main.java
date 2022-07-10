import manager.*;
import task.*;

public class Main {
    static TaskManager manager = Managers.getDefault();
    public static void main(String[] args) {

        Task firstTask = new Task("Погулять с собакой", "Погулять с собакой час", TaskStatus.NEW);
        manager.saveTask(firstTask);

        Task secondTask = new Task("Пойти в бассейн", "Хорошо поплавать", TaskStatus.NEW);
        manager.saveTask(secondTask);

        Epic firstEpic = new Epic("Приготовить ужин",
                "Приготовить ужин для всей семьи", TaskStatus.NEW);
        manager.saveEpics(firstEpic);

        Subtask firstSubtask = new Subtask(
                "Купить продукты", "Купить всё и ничего не забыть", TaskStatus.DONE, firstEpic.getId());
        manager.saveSubtask(firstSubtask);

        Subtask secondSubtask = new Subtask(
                "Купить вино"
                , "Вино белое полусладкое", TaskStatus.DONE, firstEpic.getId());
        manager.saveSubtask(secondSubtask);

        Subtask thirdSubtask = new Subtask(
                "Купить фрукты"
                , "Яблоки, апельсины, бананы", TaskStatus.DONE, firstEpic.getId());
        manager.saveSubtask(thirdSubtask);

        Epic secondEpic = new Epic("Собрать вещи для отпуска",
                "Собрать вещи и ничего не забыть", TaskStatus.NEW);
        manager.saveEpics(secondEpic);

        /*Subtask firstSubtaskOfSecondEpic = new Subtask(
                "Купить надувной матрас", "Двухместный матрас", TaskStatus.DONE, secondEpic.getId());
        manager.saveSubtask(firstSubtaskOfSecondEpic);

        Subtask secondSubtaskOfSecondEpic = new Subtask(
                "Купить палатку"
                , "Двухместную палатку", TaskStatus.DONE, secondEpic.getId());
        manager.saveSubtask(secondSubtaskOfSecondEpic);*/


        System.out.println("2.1 Получение списка всех задач");
        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());

        System.out.println("2.3 Получение по идентификатору");
        System.out.println(manager.getTaskByIdNumber(1));
        System.out.println("История просмотров:");
        printHistoryByLine();
        System.out.println("Получение по идентификатору:");
        System.out.println(manager.getEpicTaskByIdNumber(3));
        System.out.println("История просмотров:");
        printHistoryByLine();
        System.out.println("Получение по идентификатору:");
        System.out.println(manager.getSubTaskByIdNumber(5));
        System.out.println("История просмотров:");
        printHistoryByLine();
        System.out.println("Получение по идентификатору:");
        System.out.println(manager.getEpicTaskByIdNumber(3));
        System.out.println("История просмотров:");
        printHistoryByLine();
        System.out.println("Получение по идентификатору:");
        System.out.println(manager.getEpicTaskByIdNumber(7));
        System.out.println("История просмотров:");
        printHistoryByLine();

        System.out.println("Удалите задачу из истории:");
        manager.deleteSubtaskById(5);
        System.out.println("История просмотров:");
        printHistoryByLine();

        System.out.println("Удалите эпик с тремя подзадачами:");
        manager.deleteEpicById(3);
        System.out.println("История просмотров:");
        printHistoryByLine();

       /* manager.deleteTasks();
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
        System.out.println(manager.subtaskList(11));

        System.out.println("2.6 Удаление по идентификатору");

        manager.deleteTaskById(9);
        manager.deleteTaskById(10);
        manager.deleteEpicById(11);


        System.out.println(manager.getTasksList());
        System.out.println(manager.getEpicsList());
        System.out.println(manager.getSubtaskList());
*/
    }
    private static void printHistoryByLine() { //печатает историю построчно
        for (Task line : manager.getHistory())
        {
            System.out.println(line);
        }
    }
}
