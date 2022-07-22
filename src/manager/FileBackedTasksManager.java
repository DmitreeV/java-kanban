package manager;

import exception.ManagerSaveException;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private static final String topString = "id,type,name,status,description,epic";

    private final File file;

    private FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    public enum TaskType {
        TASK, EPIC, SUBTASK
    }

    private void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

            bufferedWriter.write(topString + "\n");

            for (Task task : getTasksList()) {
                bufferedWriter.write(toStringTask(task) + "\n");
            }
            for (Epic epic : getEpicsList()) {
                bufferedWriter.write(toStringEpic(epic) + "\n");
            }
            for (Subtask subtask : getSubtaskList()) {
                bufferedWriter.write(toStringSubtask(subtask) + "\n");
            }
            bufferedWriter.write("\n" + toString(getHistory()));

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    // Метод для сохранения задачи в строку
    private String toStringTask(Task task) {
        return task.getId() + "," + TaskType.TASK + "," + task.getName() + "," + task.getStatus() + ","
                + task.getDescription();
    }

    private String toStringEpic(Epic epic) {
        return epic.getId() + "," + TaskType.EPIC + "," + epic.getName() + "," + epic.getStatus() + ","
                + epic.getDescription();
    }

    private String toStringSubtask(Subtask subtask) {
        return subtask.getId() + "," + TaskType.SUBTASK + "," + subtask.getName() + "," + subtask.getStatus() + ","
                + subtask.getDescription() + "," + subtask.getEpicID();
    }

    // Метод для создания задачи из строки
    private static Task taskFromString(String value) {

        String[] arTask = value.split(",");
        int id = Integer.parseInt(arTask[0]);
        TaskType taskType = TaskType.valueOf(arTask[1]);
        TaskStatus taskStatus = TaskStatus.valueOf(arTask[3]);
        String name = arTask[2];
        String description = arTask[4];
        Task task = null;

        switch (taskType) {
            case TASK:
                task = new Task(name, taskType, taskStatus, description);
                task.setId(id);
                break;

            case EPIC:
                task = new Epic(name, taskType, taskStatus, description);
                task.setId(id);
                break;

            case SUBTASK:
                task = new Subtask(name, taskType, taskStatus, description, Integer.parseInt(arTask[5]));
                task.setId(id);
                break;
        }
        return task;
    }

    //Метод для сохранения и восстановления менеджера истории из CSV
    private static String toString(List<Task> taskList) {
        String lastLine;
        List<String> newList = new ArrayList<>();

        for (Task task : taskList) {
            newList.add(String.valueOf(task.getId()));
        }
        lastLine = String.join(",", newList);

        return lastLine;
    }

    private static List<Integer> fromString(String value) {
        List<Integer> newList = new ArrayList<>();

        String[] line = value.split(",");

        for (String str : line) {
            newList.add(Integer.parseInt(str));
        }
        return newList;
    }

    // Метод который восстанавливает данные менеджера из файла при запуске программы

    private static FileBackedTasksManager loadFromFile(File file) {
        FileBackedTasksManager manager = new FileBackedTasksManager(file);
        try {
            String line = Files.readString(Path.of(String.valueOf(file)));

            String[] lines = line.split("\n");
            int i = 1;

            while (!lines[i].isEmpty()) {
                Task task = taskFromString(lines[i]);
                ++i;
                manager.saveTask(task);

                if (lines.length == i)
                    break;
            }
            if (i == lines.length) {
                List<Integer> history = fromString(lines[lines.length - 1]);
                for (Integer id : history) {
                    manager.getHistory().add(manager.getTaskByIdNumber(id));
                    manager.getHistory().add(manager.getEpicTaskByIdNumber(id));
                    manager.getHistory().add(manager.getSubTaskByIdNumber(id));
                }
            }
            return manager;
        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage());
        }
    }

    // Тест
    public static void main(String[] args) {

        File testFile = new File("src/manager/testFiles.csv");
        FileBackedTasksManager fbTasksManager = new FileBackedTasksManager(testFile);

        fbTasksManager.saveTask(new Task("Погулять с собакой", TaskType.TASK, TaskStatus.NEW,
                "Погулять с собакой час"));

        fbTasksManager.saveTask(new Task("Пойти в бассейн", TaskType.TASK, TaskStatus.NEW,
                "Хорошо поплавать"));

        fbTasksManager.saveEpics(new Epic("Приготовить ужин", TaskType.EPIC, TaskStatus.NEW,
                "Приготовить ужин для всей семьи"));

        fbTasksManager.saveEpics(new Epic("Купить продукты", TaskType.EPIC, TaskStatus.DONE,
                "Купить всё и ничего не забыть"));

        fbTasksManager.saveSubtask(new Subtask("Купить вино", TaskType.SUBTASK, TaskStatus.DONE,
                "Вино белое полусладкое", 3));

        fbTasksManager.getTaskByIdNumber(1);
        fbTasksManager.getEpicTaskByIdNumber(3);
        fbTasksManager.getSubTaskByIdNumber(5);
        
    }

    // 1. Сохранение задач
    @Override
    public void saveTask(Task task) {
        super.saveTask(task);
        save();
    }

    @Override
    public void saveEpics(Epic epic) {
        super.saveEpics(epic);
        save();
    }

    @Override
    public void saveSubtask(Subtask subtask) {
        super.saveSubtask(subtask);
        save();
    }

    // 2.1 Получение списка задач
    @Override
    public ArrayList<Task> getTasksList() {
        return super.getTasksList();
    }

    @Override
    public ArrayList<Epic> getEpicsList() {
        return super.getEpicsList();
    }

    @Override
    public ArrayList<Subtask> getSubtaskList() {
        return super.getSubtaskList();
    }

    // 2.2 Удаление задач
    @Override
    public void deleteTasks() {
        super.deleteTasks();
        save();
    }

    @Override
    public void deleteEpics() {
        super.deleteEpics();
        save();
    }

    @Override
    public void deleteSubtasks() {
        super.deleteSubtasks();
        save();
    }

    //2.3 Получение по идентификатору
    @Override
    public Task getTaskByIdNumber(int idNumber) {
        Task task = super.getTaskByIdNumber(idNumber);
        save();
        return task;
    }

    @Override
    public Epic getEpicTaskByIdNumber(int idNumber) {
        Epic epic = super.getEpicTaskByIdNumber(idNumber);
        save();
        return epic;
    }

    @Override
    public Subtask getSubTaskByIdNumber(int idNumber) {
        Subtask subtask = super.getSubTaskByIdNumber(idNumber);
        save();
        return subtask;
    }

    // 2.4 Создание задачи
    @Override
    public Task creationTask(Task task) {
        Task task1 = super.creationTask(task);
        save();
        return task1;
    }

    @Override
    public Epic creationEpic(Epic epic) {
        Epic epic1 = super.creationEpic(epic);
        save();
        return epic1;
    }

    @Override
    public Subtask creationSubtask(Subtask subtask) {
        Subtask subtask1 = super.creationSubtask(subtask);
        save();
        return subtask1;
    }

    // 2.5 Обновление задачи
    @Override
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    // 2.6 Удаление по идентификатору
    @Override
    public void deleteTaskById(int idNumber) {
        super.deleteTaskById(idNumber);
        save();
    }

    @Override
    public void deleteEpicById(int idNumber) {
        super.deleteEpicById(idNumber);
        save();
    }

    @Override
    public void deleteSubtaskById(int idNumber) {
        super.deleteSubtaskById(idNumber);
        save();
    }

    // 3.1 Получение списка подзадач определённого эпика
    @Override
    public ArrayList<Subtask> subtaskList(int idNumber) {
        return super.subtaskList(idNumber);
    }

    @Override
    public List<Task> getHistory() {
        return super.getHistory();
    }
}

