package manager.test;

import com.google.gson.Gson;
import manager.HTTPTaskManager;
import manager.Managers;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static servers.HttpTaskServer.gson;
import static task.TaskStatus.NEW;

public class HHTPTaskServerTest {

    KVServer kvServer;
    HTTPTaskManager httpTaskManager;
    HttpTaskServer httpTaskServer;

    HttpClient client = HttpClient.newHttpClient();
    HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    Task firstTask;
    Epic firstEpic;
    Subtask firstSubtask;
    Subtask secondSubtask;
    Subtask thirdSubtask;



    @BeforeEach
    public void startServers() throws IOException {
        httpTaskServer = new HttpTaskServer();
        kvServer = new KVServer();
        httpTaskServer.start();
        kvServer.start();
    }



    @BeforeEach
    public void allTasksForTests() throws IOException {

//        kvServer = new KVServer();
//        kvServer.start();
//        httpTaskManager = (HTTPTaskManager) Managers.getDefault("http://localhost:8078");
//        httpTaskServer = new HttpTaskServer();
//        httpTaskServer.start();


        firstTask = new Task("Таск 1", NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10);


        firstEpic = new Epic("Эпик 1", TaskStatus.NEW,
                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
                10);



        firstSubtask = new Subtask("Сабтаск 1", NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3);


        secondSubtask = new Subtask("Сабтаск 2",
                TaskStatus.DONE, "Описание Сабтаск 2", LocalDateTime.now().minusMinutes(30), 40,
                3);


        thirdSubtask = new Subtask("Сабтаск 3",
                TaskStatus.DONE, "Описание Сабтаск 3",
                LocalDateTime.of(2015, 6, 14, 11, 30), 40, 4);

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
    public void shouldReturnTaskById() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        URI uri = URI.create("http://localhost:8078/tasks/task/?id=1");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(uri)
                .header("Accept", "application/json")
                .version(HttpClient.Version.HTTP_1_1)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        Task taskFromServer = httpTaskServer.getGson().fromJson(response.body(), Task.class);
        assertEquals(firstTask, taskFromServer);
    }

    @Test
    public void shouldAddTask() throws IOException, InterruptedException {
        // Act
        HttpResponse<String> response = addTaskToServer(firstTask, "/task");

        // Assert
        assertEquals(201, response.statusCode());
        assertEquals("Задача успешно добавлена!", response.body());
    }

    @Test
    public void shouldNotAddEmptyTask() throws IOException, InterruptedException {
        // Arrange
        URI uri = URI.create("http://localhost:8080/tasks/task");
        String body = "";
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(body))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();

        // Act
        HttpResponse<String> response = client.send(request, handler);

        // Assert
        assertEquals(404, response.statusCode());
        assertEquals("В теле запроса необходимо передать Task в формате JSON", response.body());
    }

}


