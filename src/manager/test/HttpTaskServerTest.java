package manager.test;

import com.google.gson.reflect.TypeToken;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import servers.HttpTaskServer;
import servers.KVServer;
import task.Epic;
import task.Subtask;
import task.Task;
import task.TaskStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static manager.HttpTaskManager.gson;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static servers.HttpTaskServer.GSON;
import static task.TaskStatus.NEW;

public class HttpTaskServerTest {

    KVServer kvServer;
    HttpTaskServer httpTaskServer;
    Task firstTask;
    Task secondTask;
    Epic firstEpic;
    Epic secondEpic;
    Epic thirdEpic;
    Subtask firstSubtask;
    Subtask secondSubtask;
    Subtask thirdSubtask;
    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final static DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("HH:mm - dd.MM.yyyy");

    @BeforeEach
    public void startServers() throws IOException {
        httpTaskServer = new HttpTaskServer();
        kvServer = new KVServer();
        httpTaskServer.start();
        kvServer.start();
    }

    @BeforeEach
    public void allTasksForTests() {

        firstTask = new Task("Таск 1", NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10);

        secondTask = new Task("Таск 2", NEW,
                "Описание Таск 2", LocalDateTime.of(2000, 6, 10, 11, 25),
                50);

        firstEpic = new Epic("Эпик 1", TaskStatus.NEW,
                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
                10);

        secondEpic = new Epic("Эпик 2", TaskStatus.NEW,
                "Описание Эпик 2", LocalDateTime.now().minusMinutes(30), 20);

        firstSubtask = new Subtask("Сабтаск 1", NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3);

        secondSubtask = new Subtask("Сабтаск 2",
                TaskStatus.DONE, "Описание Сабтаск 2", LocalDateTime.now().minusMinutes(30), 40,
                3);

        thirdSubtask = new Subtask("Сабтаск 3",
                TaskStatus.DONE, "Описание Сабтаск 3",
                LocalDateTime.of(2015, 6, 14, 11, 30), 40, 4);

        thirdEpic = new Epic("Эпик 3", NEW,
                "Для теста статусов", LocalDateTime.of(2020, 2, 20, 20, 20),
                20);
    }

    @AfterEach
    public void stopAllServers() {
        kvServer.stop();
        httpTaskServer.stop();
    }

    public HttpRequest createGetRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().GET().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpRequest createDeleteRequest(String path) {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        return HttpRequest.newBuilder().DELETE().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
    }

    public HttpResponse<String> addTaskToServer(Task task, String path) throws IOException, InterruptedException {
        URI uri = URI.create("http://localhost:8080/tasks" + path);
        String body = gson.toJson(task);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        return client.send(request, handler);
    }

    @Test
    public void testSaveTask() throws IOException, InterruptedException {

        HttpResponse<String> response = addTaskToServer(firstTask, "/task");

        assertEquals(201, response.statusCode());
        assertEquals("Task задача добавлена.", response.body());
    }

    @Test
    public void testNotSaveEmptyTask() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/task");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Task задача отсутствует в теле запроса.", response.body());
    }

    @Test
    public void testUpdateTask() throws IOException, InterruptedException {

        addTaskToServer(firstTask, "/task");
        secondTask.setId(0);

        HttpResponse<String> response = addTaskToServer(secondTask, "/task?id=0");

        assertEquals(201, response.statusCode());
        assertEquals("Task задача 0 обновлена.", response.body());
    }

    @Test
    public void testDeleteTask() throws IOException, InterruptedException {

        addTaskToServer(firstTask, "/task");

        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=1"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Задача 1 удалена.", response.body());
    }

    @Test
    public void testDeleteTaskWithNonexistentId() throws IOException, InterruptedException {

        addTaskToServer(firstTask, "/task");

        HttpResponse<String> response = client.send(createDeleteRequest("/task?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Task задача не найдена.", response.body());
    }

    @Test
    public void testDeleteAllTasks() throws IOException, InterruptedException {

        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");

        HttpResponse<String> response = client.send(createDeleteRequest("/task"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все Task задачи удалены.", response.body());
    }

    @Test
    public void testGetAllTasks() throws IOException, InterruptedException {

        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");

        HttpResponse<String> response = client.send(createGetRequest("/task"), handler);
        List<Task> tasks = GSON.fromJson(response.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(2, tasks.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetTaskById() throws IOException, InterruptedException {

        addTaskToServer(firstTask, "/task");
        firstTask.setId(1);

        HttpResponse<String> response = client.send(createGetRequest("/task?id=1"), handler);
        Task task = GSON.fromJson(response.body(), Task.class);

        assertEquals(200, response.statusCode());
        assertEquals(firstTask.getId(), task.getId());
        assertEquals(firstTask.getName(), task.getName());
        assertEquals(firstTask.getDescription(), task.getDescription());
        assertEquals(firstTask.getStatus(), task.getStatus());
        assertEquals(firstTask.getStartTime(), task.getStartTime());
        assertEquals(firstTask.getDuration(), task.getDuration());
    }

    @Test
    public void testGetTaskWithNonexistentId() throws IOException, InterruptedException {

        addTaskToServer(firstTask, "/task");

        HttpResponse<String> response = client.send(createGetRequest("/task?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Task задача не найдена.", response.body());
    }

    @Test
    public void testSaveEpic() throws IOException, InterruptedException {

        HttpResponse<String> response = addTaskToServer(firstEpic, "/epic");

        assertEquals(201, response.statusCode());
        assertEquals("Epic задача добавлена.", response.body());
    }

    @Test
    public void testNotSaveEmptyEpic() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/epic");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Epic задача отсутствует в теле запроса.", response.body());
    }

    @Test
    public void testUpdateEpic() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");

        HttpResponse<String> response = addTaskToServer(firstEpic, "/epic?id=1");

        assertEquals(201, response.statusCode());
        assertEquals("Epic задача 1 обновлена.", response.body());
    }

    @Test
    public void testDeleteEpic() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");

        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=1"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Epic задача 1 удалена.", response.body());
    }

    @Test
    public void testDeleteEpicWithNonexistentId() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");

        HttpResponse<String> response = client.send(createDeleteRequest("/epic?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Epic задача не найдена.", response.body());
    }

    @Test
    public void testDeleteAllEpics() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(secondEpic, "/epic");

        HttpResponse<String> response = client.send(createDeleteRequest("/epic"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все Epic задачи удалены.", response.body());
    }

    @Test
    public void testGetAllEpics() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(secondEpic, "/epic");

        HttpResponse<String> response = client.send(createGetRequest("/epic"), handler);
        List<Epic> taskList = GSON.fromJson(response.body(), new TypeToken<List<Epic>>() {
        }.getType());

        assertEquals(2, taskList.size());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicById() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        firstEpic.setId(1);

        HttpResponse<String> response = client.send(createGetRequest("/epic?id=1"), handler);
        Epic epic = GSON.fromJson(response.body(), Epic.class);

        assertEquals(firstEpic.getId(), epic.getId());
        assertEquals(firstEpic.getName(), epic.getName());
        assertEquals(firstEpic.getDescription(), epic.getDescription());
        assertEquals(firstEpic.getStatus(), epic.getStatus());
        assertEquals(firstEpic.getStartTime(), epic.getStartTime());
        assertEquals(firstEpic.getDuration(), epic.getDuration());
        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetEpicWithNonexistentId() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");

        HttpResponse<String> response = client.send(createGetRequest("/epic?id=10"), handler);

        assertEquals("Epic задача не найдена.", response.body());
        assertEquals(404, response.statusCode());
    }

    @Test
    public void testSaveSubtask() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");

        HttpResponse<String> response = addTaskToServer(firstSubtask, "/subtask");

        assertEquals(201, response.statusCode());
        assertEquals("Subtask задача добавлена.", response.body());
    }

    @Test
    public void testNotSaveEmptySubtask() throws IOException, InterruptedException {

        URI uri = URI.create("http://localhost:8080/tasks/subtask");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        HttpResponse<String> response = client.send(request, handler);

        assertEquals(404, response.statusCode());
        assertEquals("Subtask задача отсутствует в теле запроса.", response.body());
    }

    @Test
    public void testUpdateSubtask() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");

        HttpResponse<String> response = addTaskToServer(secondSubtask, "/subtask?id=1");

        assertEquals(201, response.statusCode());
        assertEquals("Subtask задача 1 обновлена.", response.body());
    }

    @Test
    public void testDeleteSubtaskWithNonexistentId() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");

        HttpResponse<String> response = client.send(createDeleteRequest("/subtask?id=2"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Subtask задача не найдена.", response.body());
    }

    @Test
    public void testDeleteAllSubtasks() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        addTaskToServer(secondSubtask, "/subtask");

        HttpResponse<String> response = client.send(createDeleteRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
        assertEquals("Все Subtask задачи удалены.", response.body());
    }

    @Test
    public void testGetAllSubtasks() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        addTaskToServer(secondSubtask, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask"), handler);

        assertEquals(200, response.statusCode());
    }

    @Test
    public void testGetSubtaskWithNonexistentId() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask?id=10"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Subtask задача не найдена.", response.body());
    }

    @Test
    public void testGetSubtasksInEpic() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        addTaskToServer(secondSubtask, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=1"), handler);

        assertEquals(200, response.statusCode());
    }

    @Test
    public void testNotGetSubtaskWithNonexistentId() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        addTaskToServer(secondSubtask, "/subtask");

        HttpResponse<String> response = client.send(createGetRequest("/subtask/epic?id=3"), handler);

        assertEquals(404, response.statusCode());
        assertEquals("Epic задача не найдена.", response.body());
    }

    @Test
    public void testGetHistory() throws IOException, InterruptedException {

        addTaskToServer(firstEpic, "/epic");
        addTaskToServer(firstSubtask, "/subtask");
        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");
        client.send(createGetRequest("/task?id=3"), handler);
        client.send(createGetRequest("/task?id=4"), handler);

        HttpResponse<String> history = client.send(createGetRequest("/history"), handler);

        assertEquals(200, history.statusCode());
    }

    @Test
    public void testGetPrioritizedTasks() throws IOException, InterruptedException {

        firstTask.setStartTime(LocalDateTime.parse("21:00 - 07.03.2019", FORMATTER));
        secondTask.setStartTime(LocalDateTime.parse("09:30 - 07.03.2019", FORMATTER));
        addTaskToServer(firstTask, "/task");
        addTaskToServer(secondTask, "/task");
        secondTask.setId(2);

        HttpResponse<String> prioritized = client.send(createGetRequest(""), handler);
        List<Task> tasks = GSON.fromJson(prioritized.body(), new TypeToken<List<Task>>() {
        }.getType());

        assertEquals(2, tasks.size());
        assertEquals(secondTask.getId(), tasks.get(0).getId());
        assertEquals(secondTask.getName(), tasks.get(0).getName());
        assertEquals(secondTask.getDescription(), tasks.get(0).getDescription());
        assertEquals(secondTask.getTaskType(), tasks.get(0).getTaskType());
        assertEquals(secondTask.getDuration(), tasks.get(0).getDuration());
        assertEquals(200, prioritized.statusCode());
    }
}





