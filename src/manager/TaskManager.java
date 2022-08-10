package manager;

import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public interface TaskManager {

    // 1. Сохранение задач
    void saveTask(Task task);

    void saveEpics(Epic epic);

    void saveSubtask(Subtask subtask);

    // 2.1 Получение списка задач
    ArrayList<Task> getTasksList();

    ArrayList<Epic> getEpicsList();

    ArrayList<Subtask> getSubtaskList();

    // 2.2 Удаление задач
    void deleteTasks();

    void deleteEpics();

    void deleteSubtasks();

    //2.3 Получение по идентификатору
    Task getTaskByIdNumber(int idNumber);

    Epic getEpicTaskByIdNumber(int idNumber);

    Subtask getSubTaskByIdNumber(int idNumber);

// 2.5 Обновление задачи
    void updateTask(Task task);

    void updateEpic(Epic epic);

    void updateSubtask(Subtask subtask);

// 2.6 Удаление по идентификатору
    void deleteTaskById(int idNumber);

    void deleteEpicById(int idNumber);

    void deleteSubtaskById(int idNumber);

    // 3.1 Получение списка подзадач определённого эпика
    ArrayList<Subtask> subtaskList(int idNumber);

    List<Task> getHistory();
    
    Task creationTask(Task task);

    Epic creationEpic(Epic epic);

    Subtask creationSubtask(Subtask subtask);

    Set<Task> getterPrioritizedTasks();
}
