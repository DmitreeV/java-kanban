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

        for (Integer id : epic.getSubtasks()) {
            Subtask sub = subtasks.get(id);
            if (sub != null && sub.getStatus().equals(TaskStatus.NEW)) {
                epic.setStatus(TaskStatus.NEW);
            } else if (sub != null && sub.getStatus().equals(TaskStatus.DONE)) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
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

    public ArrayList<Subtask> getSubtaskList() {
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
        for (Integer sub : subtasks.keySet()) {
            Subtask subtask = subtasks.get(sub);
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicID());
                if (epic != null) {
                    epic.getSubtasks().clear();
                }
            }
        }
        subtasks.clear();
    }

    //2.3 Получение по идентификатору

    public Task getTaskByIdNumber(int idNumber) {
        return tasks.get(idNumber);
    }

    public Epic getEpicTaskByIdNumber(int idNumber) {
        return epics.get(idNumber);
    }

    public Subtask getSubTaskByIdNumber(int idNumber) {
        return subtasks.get(idNumber);
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

        for (Integer id : epic.getSubtasks()) {
            Subtask sub = subtasks.get(id);
            if (sub != null && sub.getStatus().equals(TaskStatus.NEW)) {
                epic.setStatus(TaskStatus.NEW);
            } else if (sub != null && sub.getStatus().equals(TaskStatus.DONE)) {
                epic.setStatus(TaskStatus.DONE);
            } else {
                epic.setStatus(TaskStatus.IN_PROGRESS);
            }
        }
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
        Epic epic = epics.get(idNumber);
        for (int sub : epic.getSubtasks()) {
            subtasks.remove(sub);
        }
        epics.remove(idNumber);
    }

    public void deleteSubtaskById(int idNumber) {
        Subtask sub = subtasks.get(idNumber);
        int epicId = sub.getEpicID();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtasks().remove((Integer) idNumber);
        }
        subtasks.remove(idNumber);
    }

    // 3.1 Получение списка подзадач определённого эпика

    public ArrayList<Subtask> subtaskList(int idNumber) {
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
