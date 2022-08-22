package manager;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import servers.KVTaskClient;
import task.Epic;
import task.Subtask;
import task.Task;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class HTTPTaskManager extends FileBackedTasksManager {
    private final KVTaskClient kvTaskClient;
    private final Gson gson = new Gson();

    public HTTPTaskManager(String url) {
        super(null);
        this.kvTaskClient = new KVTaskClient(url);
        loadFromServer();
    }

    @Override
    public void save() {
        String jsonTasks = gson.toJson(new ArrayList<>(super.getTasksList()));
        kvTaskClient.put("tasks", jsonTasks);

        String jsonEpics = gson.toJson(new ArrayList<>(super.getEpicsList()));
        kvTaskClient.put("epics", jsonEpics);

        String jsonSubTasks = gson.toJson(new ArrayList<>(super.getSubtaskList()));
        kvTaskClient.put("subtasks", jsonSubTasks);

        String jsonHistory = gson.toJson(getHistory().stream().map(Task::getId).collect(Collectors.toList()));
        kvTaskClient.put("history", jsonHistory);
    }

    private void loadFromServer() {
        String jsonTasks = this.kvTaskClient.load("tasks");
        if (!jsonTasks.isEmpty()) {
            tasks.clear();
            ArrayList<Task> taskList = gson.fromJson(jsonTasks, new TypeToken<ArrayList<Task>>() {
            }.getType());
            for (Task task : taskList) {
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
        }

        String jsonEpics = this.kvTaskClient.load("epics");
        if (!jsonEpics.isEmpty()) {
            epics.clear();
            ArrayList<Epic> epicList = gson.fromJson(jsonEpics, new TypeToken<ArrayList<Epic>>() {
            }.getType());
            for (Epic epic : epicList) {
                epics.put(epic.getId(), epic);
            }
        }

        String jsonSubTasks = this.kvTaskClient.load("subtasks");
        if (!jsonSubTasks.isEmpty()) {
            subtasks.clear();
            ArrayList<Subtask> subTaskList = gson.fromJson(jsonSubTasks, new TypeToken<ArrayList<Subtask>>() {
            }.getType());
            for (Subtask subtask : subTaskList) {
                Epic epic = epics.get(subtask.getEpicID());
                if (epic != null) {
                    subtasks.put(subtask.getId(), subtask);
                    prioritizedTasks.add(subtask);
                    epic.getSubtasks().add(subtask.getId());
                    changeEpicStatus(epic);
                    timeChangeEpic(epic);
                } else {
                    System.out.println("Эпик не найден.");
                }
            }
        }

        String jsonHistory = this.kvTaskClient.load("history");
        if (!jsonHistory.isEmpty()) {
            ArrayList<Integer> taskHistoryIds = gson.fromJson(jsonHistory, new TypeToken<ArrayList<Task>>() {
            }.getType());
            for (Integer id : taskHistoryIds) {

                    inMemoryHistoryManager.add(getTaskByIdNumber(id));
                    inMemoryHistoryManager.add(getEpicTaskByIdNumber(id));
                    inMemoryHistoryManager.add(getSubTaskByIdNumber(id));
            }
        }
    }
}