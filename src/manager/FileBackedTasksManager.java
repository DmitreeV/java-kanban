package manager;

import exception.ManagerSaveException;
import task.*;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTasksManager extends InMemoryTaskManager implements TaskManager {

    private static final String TOP_STRING = "id,type,name,status,description,startTime,duration,epic";

    public File file;

    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");

    public FileBackedTasksManager(File file) {
        this.file = file;
    }

    public FileBackedTasksManager() {}

    public void save() {

        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {

            bufferedWriter.write(TOP_STRING + "\n");

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
    public String toStringTask(Task task) {
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
        LocalDateTime startTime = LocalDateTime.parse(arTask[5], FORMATTER);
        long duration = Long.parseLong(arTask[6]);
        Task task = null;

        switch (taskType) {
            case TASK:
                task = new Task(name, taskStatus, description, startTime, duration);
                break;

            case EPIC:
                task = new Epic(name, taskStatus, description, startTime, duration);
                break;

            case SUBTASK:
                task = new Subtask(name, taskStatus, description, startTime, duration,
                        Integer.parseInt(arTask[7]));
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
    public static FileBackedTasksManager loadFromFile(File file) {
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
                if (!history.isEmpty()) {
                    List<Integer> list = fromString(history);
                    for (Integer id : list) {
                        manager.inMemoryHistoryManager.add(taskHashMap.get(id));
                    }
                }
            }

            manager.idNumber = newID;
            return manager;

        } catch (IOException exception) {
            throw new ManagerSaveException(exception.getMessage(), exception.getCause());
        }
    }

    // Тест
//    public static void main(String[] args) {
//
//        File testFile = new File("src/manager/testFiles.csv");
//        FileBackedTasksManager fbTasksManager = new FileBackedTasksManager(testFile);
//
//        fbTasksManager.saveTask(new Task("Погулять с собакой", NEW,
//                "Погулять с собакой час", LocalDateTime.now().minusMinutes(30), 20));
//
//        fbTasksManager.saveTask(new Task("Пойти в бассейн", NEW,
//                "Хорошо поплавать",LocalDateTime.of(2000,5,5,10,20), 10));
//
//        fbTasksManager.saveEpic(new Epic("Приготовить ужин", NEW,
//                "Приготовить ужин для всей семьи",LocalDateTime.now().plusMinutes(30), 35));
//
//        fbTasksManager.saveEpic(new Epic("Купить продукты", DONE,
//                "Купить всё и ничего не забыть",LocalDateTime.of(2000,3,30,
//                10,20), 10));
//
//        fbTasksManager.saveSubtask(new Subtask("Купить вино", DONE,
//                "Вино белое полусладкое",LocalDateTime.of(2016,7,1,10,20), 40, 3));
//
//        fbTasksManager.saveSubtask(new Subtask("Найденная сабтаска", DONE,
//                "Эта сабтаска читается",LocalDateTime.of(2015,6,1,10,20), 40, 3));
//
//        fbTasksManager.getTaskByIdNumber(1);
//        fbTasksManager.getEpicTaskByIdNumber(3);
//        fbTasksManager.getSubTaskByIdNumber(5);
//        fbTasksManager.getSubTaskByIdNumber(6);
//
//        FileBackedTasksManager fbTasksManager2 = loadFromFile(new File("src/manager/testFiles.csv"));
//        Collection<Task> tasks = fbTasksManager2.getTasksList();
//        for (Task line : tasks)
//            System.out.println(line);
//        Collection<Epic> epics = fbTasksManager2.getEpicsList();
//        for (Task line : epics)
//            System.out.println(line);
//        Collection<Subtask> subtasks = fbTasksManager2.getSubtaskList();
//        for (Task line : subtasks)
//            System.out.println(line);
//
//       System.out.println("История просмотров:");
//        for (Task line : fbTasksManager2.getHistory())
//            System.out.println(line);
//
//        File testFile1 = new File("src/manager/testFiles2.csv");
//        FileBackedTasksManager fbTasksManager1 = new FileBackedTasksManager(testFile1);
//
//        fbTasksManager1.saveTask(new Task("Тестовая таска", NEW,
//                "Описание", LocalDateTime.now().minusMinutes(30), 20));
//
//        fbTasksManager1.getTaskByIdNumber(1);
//
//        File testFile3 = new File("src/manager/testFiles3.csv");
//        FileBackedTasksManager fbTasksManager3 = new FileBackedTasksManager(testFile3);
//
//        fbTasksManager3.saveTask(new Task("Тестовая таска", NEW,
//                "Описание", LocalDateTime.now().minusMinutes(30), 20));
//        fbTasksManager3.saveEpic(new Epic("Тестовый эпик", NEW,
//                "Описание", LocalDateTime.now().minusMinutes(40), 40));
//        fbTasksManager3.saveSubtask(new Subtask("Тестовая сабтаска", NEW,
//                "Описание", LocalDateTime.now().minusMinutes(50), 10,2));
//    }

    @Override
    public int saveTask(Task task) {
        int id = super.saveTask(task);
        save();
        return id;
    }

    @Override
    public int saveEpic(Epic epic) {
        int id = super.saveEpic(epic);
        save();
        return id;
    }

    @Override
    public int saveSubtask(Subtask subtask) {
        int id = super.saveSubtask(subtask);
        save();
        return id;
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
    public int updateTask(Task task) {
        int id = super.updateTask(task);
        save();
        return id;
    }

    @Override
    public int updateEpic(Epic epic) {
        int id = super.updateEpic(epic);
        save();
        return id;
    }

    @Override
    public int updateSubtask(Subtask subtask) {
        int id = super.updateSubtask(subtask);
        save();
        return id;
    }

    @Override
    public Task deleteTaskById(int idNumber) {
        Task task = super.deleteTaskById(idNumber);
        save();
        return task;
    }

    @Override
    public Epic deleteEpicById(int idNumber) {
        Epic epic = super.deleteEpicById(idNumber);
        save();
        return epic;
    }

    @Override
    public Subtask deleteSubtaskById(int idNumber) {
        Subtask subtask = super.deleteSubtaskById(idNumber);
        save();
        return subtask;
    }
}


