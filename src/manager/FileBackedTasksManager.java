package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private static final String topString = "id,type,name,status,description,epic";

    private final File file;

    private FileBackedTasksManager(File file) {
        super();
        this.file = file;
    }

    private void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

            bufferedWriter.write(topString + "\n");

            for (Task task : getTasksList()) {
                bufferedWriter.write(toStringTask(task) + "\n");
            }

            for (Epic epic : getEpicsList()) {
                bufferedWriter.write(toStringTask(epic) + "\n");
            }

            for (Subtask subtask : getSubtaskList()) {
                bufferedWriter.write(toStringTask(subtask) + "\n");
            }

            bufferedWriter.write("\n" + toString(getHistory()));

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage(), exception.getCause());
        }
    }

    // Метод для сохранения задачи в строку
    private String toStringTask(Task task) {
        return task.getDescriptionTask();
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
                break;

            case EPIC:
                task = new Epic(name, taskType, taskStatus, description);
                break;

            case SUBTASK:
                task = new Subtask(name, taskType, taskStatus, description, Integer.parseInt(arTask[5]));
                break;
        }
        task.setId(id);
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

        List<String> stringList;
        Map<Integer, Task> taskHashMap = new HashMap<>();
        int newID = 0;

        try {
            stringList = Files.readAllLines(file.toPath());

            for (int i = 1; i < stringList.size(); i++) {
                String line = stringList.get(i);

                if (line.isBlank()) {
                    break;
                }

                Task task = taskFromString(stringList.get(i));

                taskHashMap.put(task.getId(), task);

                if (TaskType.TASK == task.getTaskType()) {
                    manager.tasks.put(task.getId(), task);
                }
                if (TaskType.EPIC == task.getTaskType()) {
                    Epic epic = (Epic) task;
                    manager.epics.put(epic.getId(), epic);
                }
                if (TaskType.SUBTASK == task.getTaskType()) {
                    Subtask subtask = (Subtask) task;
                    manager.subtasks.put(subtask.getId(), subtask);

                    if (!manager.epics.isEmpty()) {
                        Epic epicInSub = manager.epics.get(subtask.getEpicID());
                        epicInSub.getSubtasks().add(subtask.getId());
                    }
                }

                if (task.getId() > newID) {
                    newID = task.getId();
                }
            }
            
            if (stringList.size() > 1) {
            String history = stringList.get(stringList.size() - 1);
            List<Integer> list = fromString(history);
            for (Integer id : list) {
                manager.inMemoryHistoryManager.add(taskHashMap.get(id));
            }
            }

            manager.idNumber = newID;
            return manager;

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage(), exception.getCause());
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

        fbTasksManager.saveSubtask(new Subtask("Найденная сабтаска", TaskType.SUBTASK, TaskStatus.DONE,
                "Эта сабтаска читается", 3));

        fbTasksManager.getTaskByIdNumber(1);
        fbTasksManager.getEpicTaskByIdNumber(3);
        fbTasksManager.getSubTaskByIdNumber(5);
        fbTasksManager.getSubTaskByIdNumber(6);

        FileBackedTasksManager fbTasksManager2 = loadFromFile(new File("src/manager/testFiles.csv"));
        Collection<Task> tasks = fbTasksManager2.getTasksList();
        for (Task line : tasks)
            System.out.println(line);
        Collection<Epic> epics = fbTasksManager2.getEpicsList();
        for (Task line : epics)
            System.out.println(line);
        Collection<Subtask> subtasks = fbTasksManager2.getSubtaskList();
        for (Task line : subtasks)
            System.out.println(line);

       System.out.println("История просмотров:");
        for (Task line : fbTasksManager2.getHistory())
            System.out.println(line);
    }

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
}


