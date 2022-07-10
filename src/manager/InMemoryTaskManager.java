package manager;

import task.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {

    private HashMap<Integer, Task> tasks = new HashMap<>();
    private HashMap<Integer, Epic> epics = new HashMap<>();
    private HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private int idNumber = 0;

    private HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    //Генерирование Id
    private int id(Task task) {
        task.setId(++idNumber);
        return idNumber;
    }

    // 1. Сохранение задач
    @Override
    public void saveTask(Task task) {
        tasks.put(id(task), task);
    }

    @Override
    public void saveEpics(Epic epic) {
        epics.put(id(epic), epic);
        changeEpicStatus(epic);
    }

    @Override
    public void saveSubtask(Subtask subtask) {

        int epicIdOfSubTask = subtask.getEpicID();
        Epic epic = epics.get(epicIdOfSubTask);
        if (epic != null) {
            subtasks.put(id(subtask), subtask);
            epic.addIdOfSubtasks(subtask);
            changeEpicStatus(epic);
        }
    }

    // 2.1 Получение списка задач
    @Override
    public ArrayList<Task> getTasksList() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return new ArrayList<>(subtasks.values());
    }

    // 2.2 Удаление задач
    @Override
    public void deleteTasks() {
        for (Integer id : tasks.keySet()) {
            inMemoryHistoryManager.remove(id);
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Integer id : epics.keySet()) {
            inMemoryHistoryManager.remove(id);
        }
        epics.clear();

        for (Integer id : subtasks.keySet()) {
            inMemoryHistoryManager.remove(id);
        }
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Integer sub : subtasks.keySet()) {
            inMemoryHistoryManager.remove(sub);
            Subtask subtask = subtasks.get(sub);
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicID());
                if (epic != null) {
                    epic.getSubtasks().clear();
                    changeEpicStatus(epic);
                }
            }
        }
        subtasks.clear();
    }

    //2.3 Получение по идентификатору
    @Override
    public Task getTaskByIdNumber(int idNumber) {
        Task task = tasks.get(idNumber);
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicTaskByIdNumber(int idNumber) {
        Epic epic = epics.get(idNumber);
        inMemoryHistoryManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubTaskByIdNumber(int idNumber) {
        Subtask subtask = subtasks.get(idNumber);
        inMemoryHistoryManager.add(subtask);
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
    @Override
    public void updateTask(Task task) {
        int idUpdatedTask = task.getId();
        if (tasks.containsKey(idUpdatedTask)) {
            tasks.put(idUpdatedTask, task);
        }
    }

    @Override
    public void updateEpic(Epic epic) {

        int idUpdatedEpic = epic.getId();
        TaskStatus currentEpicStatus = epics.get(idUpdatedEpic).getStatus();
        Epic newEpic = new Epic(epic.getName(), currentEpicStatus, epic.getDescription()
                , epic.getId(), epic.getSubtasks());
        if (epics.containsKey(idUpdatedEpic)) {
            epics.put(idUpdatedEpic, newEpic);
            changeEpicStatus(newEpic);
        }
    }

    @Override
    public void updateSubtask(Subtask subtask) {

        int idUpdatedSubTask = subtask.getId();
        if (subtasks.containsKey(idUpdatedSubTask)) {
            subtasks.put(idUpdatedSubTask, subtask);
        }
        int epicIdForStatus = subtask.getEpicID();
        Epic epic = epics.get(epicIdForStatus);
        if (epic != null) {
            changeEpicStatus(epic);
        }
    }

    // 2.6 Удаление по идентификатору
    @Override
    public void deleteTaskById(int idNumber) {
        inMemoryHistoryManager.remove(idNumber);
        tasks.remove(idNumber);
    }

    @Override
    public void deleteEpicById(int idNumber) {
        Epic epic = epics.get(idNumber);
        for (int sub : epic.getSubtasks()) {
            inMemoryHistoryManager.remove(sub);
            subtasks.remove(sub);
        }
        inMemoryHistoryManager.remove(idNumber);
        epics.remove(idNumber);

    }

    @Override
    public void deleteSubtaskById(int idNumber) {
        Subtask sub = subtasks.get(idNumber);
        int epicId = sub.getEpicID();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtasks().remove(Integer.valueOf(idNumber));
            changeEpicStatus(epic);
        }
        inMemoryHistoryManager.remove(idNumber);
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

    private void changeEpicStatus(Epic epic) { // метод для смены статуса эпика
        int epicID = epic.getId();
        ArrayList<Subtask> updatedListOfSubtasks = subtaskList(epicID);

        int doneCounter = 0;
        int newCounter = 0;
        for (Subtask subtask : updatedListOfSubtasks) {
            switch (subtask.getStatus()) {
                case NEW:
                    newCounter++;
                    break;
                case IN_PROGRESS:
                    break;
                case DONE:
                    doneCounter++;
                    break;
            }
        }

        if ((updatedListOfSubtasks.size() == 0) || (newCounter == updatedListOfSubtasks.size())) {
            epic.setStatus(TaskStatus.NEW);
        } else if (doneCounter == updatedListOfSubtasks.size()) {
            epic.setStatus(TaskStatus.DONE);
        } else {
            epic.setStatus(TaskStatus.IN_PROGRESS);
        }
    }

    // История просмотров задач

    @Override
    public List<Task> getHistory() {
        return inMemoryHistoryManager.getHistory();
    }
}
