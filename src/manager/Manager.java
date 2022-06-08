package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;

public class Manager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idNumber = 0;

    //Генерирование Id
    private int id(Task task) {

        task.setId(++idNumber);
        return idNumber;
    }

    // 1. Сохранение задач

    public void saveTask(Task task) {
        tasks.put(id(task), task);
    }

    public void saveEpics(Epic epic) {
        epics.put(id(epic), epic);
    }

    public void saveSubtask(Subtask subtask) {
        subtasks.put(id(subtask), subtask);
    }

    // 2.1 Получение списка задач

    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    public ArrayList<Subtask> getSubtaksList() {
        return new ArrayList<>(subtasks.values());
    }

    // 2.2 Удаление задач

    public void deleteTasks() {
        tasks.clear();
    }

    public void deleteEpic() {
        epics.clear();
        subtasks.clear();
    }

   public void deleteSubtask() {
        subtasks.clear();
    }

    //2.3 Получение по идентификатору

    public Task getTaskByIdNumber(int idNumber) {
        Task task = tasks.get(idNumber);
        return task;
    }

    public Epic getEpicTaskByIdNumber(int idNumber) {
        Epic epic = epics.get(idNumber);
        return epic;
    }

    public Subtask getSubTaskByIdNumber(int idNumber) {
        Subtask subtask = subtasks.get(idNumber);
        return subtask;
    }

    // 2.4 Создание задачи

    public Task creationTask(Task task) {
        return new Task(task.getName(), task.getDescription(), task.getStatus());
    }

    public Epic creationEpic(Epic epic) {
        return new Epic(epic.getName(), epic.getDescription(), epic.getStatus());
    }

    public Subtask creationSubtask(Subtask subtask) {
        return new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicID());
    }

    // 2.5 Обновление задачи

    public void updateTask(Task task) {
        Task newTask = new Task(task.getName(), task.getDescription(), task.getStatus());
        tasks.put(task.getId(), newTask);
    }

    public void updateEpic(Epic epic) {
        Epic newEpic = new Epic(epic.getName(), epic.getDescription(), epic.getStatus());
        epics.put(epic.getId(), newEpic);
    }

    public void updateSubtask(Subtask subtask) {
        Subtask newSub = new Subtask(subtask.getName(), subtask.getDescription(), subtask.getStatus(), subtask.getEpicID());
        subtasks.put(subtask.getId(), newSub);
    }
    // 2.6 Удаление по идентификатору

    public void deleteTaskById(int idNumber) {
        tasks.remove(idNumber);
    }

    public void deleteEpicById(int idNumber) {
        epics.remove(idNumber);
    }
    public void deleteSubtaskById(int idNumber) {
        subtasks.remove(idNumber);
    }

     // 3.1 Получение списка подзадач определённого эпика

    public ArrayList<Subtask> subtaksList(int idNumber) {
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (int subtaskIdNumber : subtasks.keySet()) {
            Subtask subtask = subtasks.get(subtaskIdNumber);
            if (subtask != null && idNumber == subtask.getEpicID()) {
                listSubtasks.add(subtask);
            }
        }
        return listSubtasks;
    }
}
