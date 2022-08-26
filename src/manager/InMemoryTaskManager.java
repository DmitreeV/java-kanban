package manager;

import task.*;

import java.time.LocalDateTime;
import java.util.*;


public class InMemoryTaskManager implements TaskManager {

    protected HashMap<Integer, Task> tasks = new HashMap<>();
    protected HashMap<Integer, Epic> epics = new HashMap<>();
    protected HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected int idNumber = 0;

    final HistoryManager inMemoryHistoryManager = Managers.getDefaultHistory();

    protected final Set<Task> prioritizedTasks = new TreeSet<>((task1, task2) -> {
        if ((task1.getStartTime() != null) && (task2.getStartTime() != null)) {
            return task1.getStartTime().compareTo(task2.getStartTime());
        } else if (task1.getStartTime() == null) {
            return 1;
        } else if (task2.getStartTime() == null) {
            return -1;
        } else {
            return 0;
        }
    });

    //Генерирование Id
    protected int id(Task task) {
        task.setId(++idNumber);
        return idNumber;
    }

    // 1. Сохранение задач
    @Override
    public int saveTask(Task task) {
        tasksWithoutIntersectionsInTime(task);
        task.setId(++idNumber);
        tasks.put(task.getId(), task);
        prioritizedTasks.add(task);
        return task.getId();
    }

    @Override
    public int saveEpic(Epic epic) {
        epic.setId(++idNumber);
        epics.put(epic.getId(), epic);
        changeEpicStatus(epic);
        return epic.getId();
    }

    @Override
    public int saveSubtask(Subtask subtask) {
        tasksWithoutIntersectionsInTime(subtask);
        int epicIdOfSubTask = subtask.getEpicID();
        Epic epic = epics.get(epicIdOfSubTask);
        if (epic != null) {
            subtasks.put(id(subtask), subtask);
            epic.addIdOfSubtasks(subtask);
            changeEpicStatus(epic);
            timeChangeEpic(epic);
            prioritizedTasks.add(subtask);
        }
        return subtask.getId();
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
        for (Task task : tasks.values()) {
            prioritizedTasks.remove(task);
            inMemoryHistoryManager.remove(task.getId());
        }
        tasks.clear();
    }

    @Override
    public void deleteEpics() {
        for (Epic epic : epics.values()) {
            prioritizedTasks.remove(epic);
            inMemoryHistoryManager.remove(epic.getId());
        }
        epics.clear();

        for (Subtask subtask : subtasks.values()) {
            prioritizedTasks.remove(subtask);
            inMemoryHistoryManager.remove(subtask.getEpicID());
        }
        subtasks.clear();
    }

    @Override
    public void deleteSubtasks() {
        for (Subtask sub : subtasks.values()) {
            prioritizedTasks.remove(sub);
            inMemoryHistoryManager.remove(sub.getId());
            Subtask subtask = subtasks.get(sub.getId());
            if (subtask != null) {
                Epic epic = epics.get(subtask.getEpicID());
                if (epic != null) {
                    epic.getSubtasks().clear();
                    changeEpicStatus(epic);
                    timeChangeEpic(epic);
                }
            }
        }
        subtasks.clear();
    }

    //2.3 Получение по идентификатору
    @Override
    public Task getTaskByIdNumber(int idNumber) {
        Task task = tasks.get(idNumber);
        if (task == null) {
            return null;
        }
        inMemoryHistoryManager.add(task);
        return task;
    }

    @Override
    public Epic getEpicTaskByIdNumber(int idNumber) {
        Epic epic = epics.get(idNumber);
        if (epic == null) {
            return null;
        }
        inMemoryHistoryManager.add(epic);
        return epic;
    }

    @Override
    public Subtask getSubTaskByIdNumber(int idNumber) {
        Subtask subtask = subtasks.get(idNumber);
        if (subtask == null) {
            return null;
        }
        inMemoryHistoryManager.add(subtask);
        return subtask;
    }

    // 2.4 Создание задачи
    public Task creationTask(Task task) {
        tasksWithoutIntersectionsInTime(task);
        return new Task(task.getName(), task.getStatus(), task.getDescription(), task.getStartTime(),
                task.getDuration());
    }

    public Epic creationEpic(Epic epic) {
        return new Epic(epic.getName(), epic.getStatus(), epic.getDescription(), epic.getStartTime(),
                epic.getDuration());
    }

    public Subtask creationSubtask(Subtask subtask) {
        tasksWithoutIntersectionsInTime(subtask);
        return new Subtask(subtask.getName(), subtask.getStatus(), subtask.getDescription(),
                subtask.getStartTime(), subtask.getDuration(), subtask.getEpicID());
    }

    // 2.5 Обновление задачи
    @Override
    public int updateTask(Task task) {
        tasksWithoutIntersectionsInTime(task);
        int idUpdatedTask = task.getId();

        if (tasks.containsKey(idUpdatedTask)) {
            tasks.put(idUpdatedTask, task);
            prioritizedTasks.removeIf(task1 -> task1.getId() == task.getId());
            prioritizedTasks.add(task);
        }
        return idUpdatedTask;
    }

    @Override
    public int updateEpic(Epic epic) {
        Epic currentEpic = epics.get(epic.getId());
        currentEpic.setName(epic.getName());
        currentEpic.setDescription(epic.getDescription());
        return epic.getId();
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        tasksWithoutIntersectionsInTime(subtask);
        int idUpdatedSubtask = subtask.getId();
        if (subtasks.containsKey(idUpdatedSubtask)) {
            subtasks.put(idUpdatedSubtask, subtask);
        }
        int epicIdForStatus = subtask.getEpicID();
        Epic epic = epics.get(epicIdForStatus);
        if (epic != null) {
            changeEpicStatus(epic);
            timeChangeEpic(epic);
            prioritizedTasks.removeIf(task1 -> task1.getId() == subtask.getId());
            prioritizedTasks.add(subtask);
        }
        return idUpdatedSubtask;
    }

    // 2.6 Удаление по идентификатору
    @Override
    public Task deleteTaskById(int idNumber) {
        Task task = tasks.get(idNumber);
        if(task == null){
            return null;
        }
        prioritizedTasks.remove(task);
        inMemoryHistoryManager.remove(idNumber);
        tasks.remove(idNumber);
        return task;
    }

    @Override
    public Epic deleteEpicById(int idNumber) {
        Epic epic = epics.get(idNumber);
        if(epic == null){
            return null;
        }
        for (int sub : epic.getSubtasks()) {
            inMemoryHistoryManager.remove(sub);
            Subtask subtask = subtasks.get(sub);
            if (subtask != null) {
                prioritizedTasks.remove(subtask);
            }
            subtasks.remove(sub);
        }
        inMemoryHistoryManager.remove(idNumber);
        epics.remove(idNumber);

        return epic;
    }

    @Override
    public Subtask deleteSubtaskById(int idNumber) {
        Subtask sub = subtasks.get(idNumber);
        if(sub == null){
            return null;
        }
        int epicId = sub.getEpicID();
        Epic epic = epics.get(epicId);
        if (epic != null) {
            epic.getSubtasks().remove(Integer.valueOf(idNumber));
            changeEpicStatus(epic);
            timeChangeEpic(epic);
        }
        inMemoryHistoryManager.remove(idNumber);
        prioritizedTasks.remove(sub);
        subtasks.remove(idNumber);

        return sub;
    }

    // 3.1 Получение списка подзадач определённого эпика
    public ArrayList<Subtask> subtaskList(int idNumber) {
        Epic epic = epics.get(idNumber);
        if (epic == null) {
            return null;
        }
        ArrayList<Subtask> listSubtasks = new ArrayList<>();
        for (int subtaskIdNumber : epic.getSubtasks()) {
            Subtask subtask = subtasks.get(subtaskIdNumber);
            listSubtasks.add(subtask);
        }
        return listSubtasks;
    }

    protected void changeEpicStatus(Epic epic) { // метод для смены статуса эпика
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

    //  Метод для замены времени эпика относительно его подзадач
    protected void timeChangeEpic(Epic epic) {
        epicStartTimeBasedSubtask(epic.getSubtasks(),epic);
        epicDurationBasedSubtask(epic.getSubtasks(),epic);
        epicEndTimeBasedSubtask(epic.getSubtasks(),epic);
    }

    //  Время начала — дата старта самой ранней подзадачи
    protected void epicStartTimeBasedSubtask(List<Integer> subtasksID, Epic epic) {
        LocalDateTime epicStart = null;
        if (subtasksID.size() != 0) {
            Subtask theEarliestSubtask = subtasks.get(subtasksID.get(0));
            epicStart = theEarliestSubtask.getStartTime();
        }
        for (int i = 1; i < subtasksID.size(); i++) {
            Subtask subtask = subtasks.get(subtasksID.get(i));
            if (subtask.getStartTime().isBefore(epicStart)) {
                epicStart = subtask.getStartTime();

            }
            epic.setStartTime(epicStart);
        }
    }

    //Продолжительность эпика — сумма продолжительности всех его подзадач
    protected void epicDurationBasedSubtask(List<Integer> subtasksID, Epic epic) {
        long duration = 0;
        for (var id : subtasksID) {
            Subtask subtask = subtasks.get(id);
            duration += subtask.getDuration();
        }
        epic.setDuration(duration);
    }

    // время завершения Эпика — время окончания самой поздней из задач.
    protected void epicEndTimeBasedSubtask(List<Integer> subtasksID, Epic epic) {
        LocalDateTime epicEnd = null;
        if (subtasksID.size() != 0) {
            Subtask theLatestSubtask = subtasks.get(subtasksID.get(0));
            epicEnd = theLatestSubtask.getEndTime();
        }
        for (int i = 1; i < subtasksID.size(); i++) {
            Subtask subtask = subtasks.get(subtasksID.get(i));
            if (subtask.getEndTime().isAfter(epicEnd)) {
                epicEnd = subtask.getEndTime();
            }
            epic.setEndTime(epicEnd);
        }
    }

    //Научите трекер проверять, что задачи и подзадачи не пересекаются по времени выполнения
    private void tasksWithoutIntersectionsInTime(Task task) {
        LocalDateTime startTime = task.getStartTime();
        LocalDateTime endTime = task.getEndTime();
        Set<Task> listOfTasks = getterPrioritizedTasks();

        for (var tasks : listOfTasks) {
            LocalDateTime taskStart = tasks.getStartTime();
            LocalDateTime taskEnd = tasks.getEndTime();
            if ((startTime.isAfter(taskStart) && startTime.isBefore(taskEnd))
                    || (endTime.isAfter(taskStart) && endTime.isBefore(taskEnd))) {
                throw new RuntimeException("Произошло наложение задач по времени!");
            }
        }
    }

    public Set<Task> getterPrioritizedTasks() {
        return prioritizedTasks;
    }
}
