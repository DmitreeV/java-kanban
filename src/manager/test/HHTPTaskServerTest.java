package manager.test;

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
import static task.TaskStatus.NEW;

public class HHTPTaskServerTest {

    KVServer kvServer;
    HTTPTaskManager httpTaskManager;
    HttpTaskServer httpTaskServer;
    Task firstTask;
    Epic firstEpic;
    Subtask firstSubtask;
    Subtask secondSubtask;
    Subtask thirdSubtask;

    @BeforeEach
    public void allTasksForTests() throws IOException {

        kvServer = new KVServer();
        kvServer.start();
        httpTaskManager = (HTTPTaskManager) Managers.getDefault();
        httpTaskServer = new HttpTaskServer();
        httpTaskServer.start();


        firstTask = httpTaskManager.creationTask(new Task("Таск 1", NEW,
                "Описание Таск 1", LocalDateTime.of(2000, 5, 5, 10, 20),
                10));
        httpTaskManager.saveTask(firstTask);

        firstEpic = httpTaskManager.creationEpic(new Epic("Эпик 1", TaskStatus.NEW,
                "Описание Эпик 1", LocalDateTime.of(2001, 9, 11, 10, 20),
                10));
        httpTaskManager.saveEpic(firstEpic);


        firstSubtask = httpTaskManager.creationSubtask(new Subtask("Сабтаск 1", NEW,
                "Описание Сабтаск 1", LocalDateTime.of(2010, 1, 11, 11, 40),
                50, 3));
        httpTaskManager.saveSubtask(firstSubtask);

        secondSubtask = httpTaskManager.creationSubtask(new Subtask("Сабтаск 2",
                TaskStatus.DONE, "Описание Сабтаск 2", LocalDateTime.now().minusMinutes(30), 40,
                3));
        httpTaskManager.saveSubtask(secondSubtask);

        thirdSubtask = httpTaskManager.creationSubtask(new Subtask("Сабтаск 3",
                TaskStatus.DONE, "Описание Сабтаск 3",
                LocalDateTime.of(2015, 6, 14, 11, 30), 40, 4));
        httpTaskManager.saveSubtask(thirdSubtask);
    }

    @AfterEach
    public void stopAllServers() {
        kvServer.stop();
        httpTaskServer.stop();
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


}


