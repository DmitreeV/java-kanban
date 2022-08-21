package servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import manager.TaskManager;
import task.Epic;
import task.Subtask;
import task.Task;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.regex.Pattern;

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    protected static final int PORT = 8080;
    protected TaskManager taskManager;
    protected HttpServer httpServer;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        createHTTPServer();
    }
//    public static void main(String[] args) throws IOException {
//        HttpTaskServer server = new HttpTaskServer();
//        server.createHTTPServer();
//        server.start();
//
//
//    }

    public void start() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        System.out.println("Завершение работы сервера");
        httpServer.stop(0);
    }



    public void createHTTPServer() throws IOException {
        httpServer = HttpServer.create();
        httpServer.bind(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }


    private class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = String.valueOf(exchange.getRequestURI());
            String[] splitPath = path.split("/");

//            if (splitPath.length == 2 && method.equals("GET")) {
//                handleGetPrioritizedTasks(exchange);
//            }

            switch (method) {
                case "GET":
                    if (splitPath[2].equals("task")) {
                        handleTaskGet(exchange);
                    } else if (splitPath[2].equals("epic")) {
                        handleEpicGet(exchange);
                    } else if (splitPath[2].equals("subtask")) {
                        handleSubtaskGet(exchange);
                    } else if (splitPath[2].equals("history")) {
                        handleHistoryGet(exchange);
                    } else {
                        int code = 404;
                        String response = "Страница не найдена, " + code;
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;
                case "POST":
                    if (splitPath[2].equals("task")) {
                        handleTaskPost(exchange);
                    } else if (splitPath[2].equals("epic")) {
                        handleEpicPost(exchange);
                    } else if (splitPath[2].equals("subtask")) {
                        handleSubtaskPost(exchange);
                    } else {
                        int code = 404;
                        String response = "Страница не найдена, " + code;
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;

                case "DELETE":
                    if (splitPath[2].equals("task")) {
                        handleTaskDelete(exchange);
                    } else if (splitPath[2].equals("epic")) {
                        handleEpicDelete(exchange);
                    } else if (splitPath[2].equals("subtask")) {
                        handleSubtaskDelete(exchange);
                    } else {
                        int code = 404;
                        String response = "Страница не найдена, " + code;
                        try (OutputStream os = exchange.getResponseBody()) {
                            os.write(response.getBytes());
                        }
                    }
                    break;
                default:
                    int code = 405;
                    String response = "Неизвестный запрос, " + code;
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(response.getBytes());
                    }
            }
        }

        int setId(HttpExchange httpExchange) {
            int id = Integer.parseInt(httpExchange.getRequestURI().toString()
                    .split("\\?")[1].split("=")[1]);
            return id;
        }

        void outputStreamWrite(HttpExchange h, String response, int code) throws IOException {
            h.sendResponseHeaders(code, 0);
            try (OutputStream os = h.getResponseBody()) {
                os.write(response.getBytes());
            }
        }

        protected String readText(HttpExchange h) throws IOException {
            return new String(h.getRequestBody().readAllBytes(), UTF_8);
        }

        public void handleTaskGet(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int id = setId(exchange);
                if (!taskManager.getTasksList().isEmpty()) {
                    Task task = taskManager.getTaskByIdNumber(id);
                    outputStreamWrite(exchange, gson.toJson(task), 200);
                } else {
                    outputStreamWrite(exchange, "Задача не найдена.", 404);
                }
            } else {
                if (!taskManager.getTasksList().isEmpty()) {
                    outputStreamWrite(exchange, gson.toJson(taskManager.getTasksList()), 200);
                } else {
                    outputStreamWrite(exchange, "Список задач не найден .", 404);
                }
            }
        }

        public void handleEpicGet(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int id = setId(exchange);
                if (!taskManager.getEpicsList().isEmpty()) {
                    Epic epic = taskManager.getEpicTaskByIdNumber(id);
                    outputStreamWrite(exchange, gson.toJson(epic), 200);
                } else {
                    outputStreamWrite(exchange, "Эпик не найден.", 404);
                }
            } else {
                if (!taskManager.getEpicsList().isEmpty()) {
                    outputStreamWrite(exchange, gson.toJson(taskManager.getEpicsList()), 200);
                } else {
                    outputStreamWrite(exchange, "Список эпиков не найден .", 404);
                }
            }
        }

        public void handleSubtaskGet(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int id = setId(exchange);
                if (!taskManager.getSubtaskList().isEmpty()) {
                    Subtask subtask = taskManager.getSubTaskByIdNumber(id);
                    outputStreamWrite(exchange, gson.toJson(subtask), 200);
                } else {
                    outputStreamWrite(exchange, "Подзадача не найдена.", 404);
                }
            } else {
                if (!taskManager.getSubtaskList().isEmpty()) {
                    outputStreamWrite(exchange, gson.toJson(taskManager.getSubtaskList()), 200);
                } else {
                    outputStreamWrite(exchange, "Список подзадач не найден .", 404);
                }
            }
        }

        private void handleTaskPost(HttpExchange exchange) throws IOException {
            String body = readText(exchange);
            if (body.isBlank()) {
                outputStreamWrite(exchange, "В теле запроса необходимо передать Task в формате JSON", 400);
                return;
            }
            Task task = gson.fromJson(body, Task.class);
            Integer id = task.getId();
            if (id == null) {
                taskManager.creationTask(task);
                outputStreamWrite(exchange, "Task задача успешно добавлена!", 200);
            } else {
                if (!taskManager.getTasksList().isEmpty()) {
                    taskManager.updateTask(task);
                    outputStreamWrite(exchange, "Task задача успешно обновлена!", 200);
                } else {
                    outputStreamWrite(exchange, "Произошла ошибка", 404);
                }
            }
        }

        private void handleEpicPost(HttpExchange exchange) throws IOException {
            String body = readText(exchange);
            if (body.isBlank()) {
                outputStreamWrite(exchange, "В теле запроса необходимо передать Epic в формате JSON", 400);
                return;
            }
            Epic epic = gson.fromJson(body, Epic.class);
            Integer id = epic.getId();
            if (id == null) {
                taskManager.creationEpic(epic);
                outputStreamWrite(exchange, "Epic задача успешно добавлена!", 200);
            } else {
                if (!taskManager.getEpicsList().isEmpty()) {
                    taskManager.updateEpic(epic);
                    outputStreamWrite(exchange, "Epic задача успешно обновлена!", 200);
                } else {
                    outputStreamWrite(exchange, "Произошла ошибка", 404);
                }
            }
        }

        public void handleSubtaskPost(HttpExchange exchange) throws IOException {
            String body = readText(exchange);
            if (body.isBlank()) {
                outputStreamWrite(exchange, "В теле запроса необходимо передать Subtask в формате JSON", 400);
                return;
            }
            Subtask subtask = gson.fromJson(body, Subtask.class);
            Integer id = subtask.getId();
            if (id == null) {
                if (!taskManager.getEpicsList().isEmpty()) {
                    taskManager.creationSubtask(subtask);
                    outputStreamWrite(exchange, "Subtask задача успешно добавлена!", 200);
                } else {
                    if (!taskManager.getSubtaskList().isEmpty()) {
                        taskManager.updateSubtask(subtask);
                        outputStreamWrite(exchange, "Subtask задача успешно обновлена!", 200);
                    } else {
                        outputStreamWrite(exchange, "Произошла ошибка", 404);
                    }
                }
            }
        }

        private void handleTaskDelete(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int idTask = setId(exchange);
                if (!taskManager.getTasksList().isEmpty()) {
                    Task task = taskManager.getTaskByIdNumber(idTask);
                    taskManager.deleteTaskById(task.getId());
                    outputStreamWrite(exchange, "Удалили " + gson.toJson(task), 200);
                } else {
                    outputStreamWrite(exchange, "Задача с Id " + idTask + " не найдена в базе.", 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(exchange);
            }
        }

        private void handleEpicDelete(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int idEpic = setId(exchange);
                if (!taskManager.getEpicsList().isEmpty()) {
                    Epic epic = taskManager.getEpicTaskByIdNumber(idEpic);
                    taskManager.deleteEpicById(epic.getId());
                    outputStreamWrite(exchange, "Удалили " + gson.toJson(epic), 200);
                } else {
                    outputStreamWrite(exchange, "Задача с Id " + idEpic + " не найдена в базе.", 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(exchange);
            }
        }

        private void handleSubtaskDelete(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int idSub = setId(exchange);
                if (!taskManager.getSubtaskList().isEmpty()) {
                    Subtask subtask = taskManager.getSubTaskByIdNumber(idSub);
                    taskManager.deleteSubtaskById(subtask.getId());
                    outputStreamWrite(exchange, "Удалили " + gson.toJson(subtask), 200);
                } else {
                    outputStreamWrite(exchange, "Задача с Id " + idSub + " не найдена в базе.", 404);
                }
            } else {
                handleDeleteTasksEpicsSubTasksMap(exchange);
            }
        }

        private void handleDeleteTasksEpicsSubTasksMap(HttpExchange exchange) throws IOException {
            if (!taskManager.getTasksList().isEmpty() ||
                    !taskManager.getEpicsList().isEmpty() ||
                    !taskManager.getSubtaskList().isEmpty()) {
                taskManager.deleteTasks();
                taskManager.deleteEpics();
                taskManager.deleteSubtasks();
                outputStreamWrite(exchange, "Все задачи удалены.", 200);
            } else {
                outputStreamWrite(exchange, "Задач для удаления нет.", 404);
            }
        }

        private void handleHistoryGet(HttpExchange exchange) throws IOException {
            if (!taskManager.getHistory().isEmpty()) {
                outputStreamWrite(exchange, gson.toJson(taskManager.getHistory()), 200);
            } else {
                outputStreamWrite(exchange, "История просмотра задач пуста.", 404);
            }
        }
    }
}

