package servers;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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

import static java.nio.charset.StandardCharsets.UTF_8;

public class HttpTaskServer {

    protected static final int PORT = 7878;
    protected TaskManager taskManager;
    protected HttpServer httpServer;

    private final Gson gson = new GsonBuilder()
            .setPrettyPrinting()
            .serializeNulls()
            .create();

    public Gson getGson() {
        return gson;
    }

    public HttpTaskServer() throws IOException {
        httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);
        httpServer.createContext("/tasks", new TaskHandler());
    }
    public static void main(String[] args) throws IOException {
       HttpTaskServer server = new HttpTaskServer();
        server.start();
    }

    public void start() {
        System.out.println("HTTP-сервер запущен на " + PORT + " порту!");
        System.out.println("Открой в браузере http://localhost:" + PORT + "/");
        httpServer.start();
    }

    public void stop() {
        System.out.println("Завершение работы сервера");
        httpServer.stop(0);
    }

    private class TaskHandler implements HttpHandler {

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] splitPath = path.split("/");

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
                        outputStream(exchange, "Страница не найдена.", 404);
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
                        outputStream(exchange, "Страница не найдена.", 404);
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
                        outputStream(exchange, "Страница не найдена.", 404);
                    }
                    break;
                default:
                    outputStream(exchange, "Неизвестный запрос", 405);
            }
        }

        void outputStream(HttpExchange h, String response, int code) throws IOException {
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
                int id = Integer.parseInt(exchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
                if (!taskManager.getTasksList().isEmpty()) {
                    Task task = taskManager.getTaskByIdNumber(id);
                    outputStream(exchange, gson.toJson(task), 200);
                } else {
                    outputStream(exchange, "Задача не найдена.", 404);
                }
            } else {
                if (!taskManager.getTasksList().isEmpty()) {
                    outputStream(exchange, gson.toJson(taskManager.getTasksList()), 200);
                } else {
                    outputStream(exchange, "Список задач не найден .", 404);
                }
            }
        }

        public void handleEpicGet(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int id = Integer.parseInt(exchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
                if (!taskManager.getEpicsList().isEmpty()) {
                    Epic epic = taskManager.getEpicTaskByIdNumber(id);
                    outputStream(exchange, gson.toJson(epic), 200);
                } else {
                    outputStream(exchange, "Эпик не найден.", 404);
                }
            } else {
                if (!taskManager.getEpicsList().isEmpty()) {
                    outputStream(exchange, gson.toJson(taskManager.getEpicsList()), 200);
                } else {
                    outputStream(exchange, "Список эпиков не найден .", 404);
                }
            }
        }

        public void handleSubtaskGet(HttpExchange exchange) throws IOException {
            if (exchange.getRequestURI().getQuery() != null) {
                int id = Integer.parseInt(exchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
                if (!taskManager.getSubtaskList().isEmpty()) {
                    Subtask subtask = taskManager.getSubTaskByIdNumber(id);
                    outputStream(exchange, gson.toJson(subtask), 200);
                } else {
                    outputStream(exchange, "Подзадача не найдена.", 404);
                }
            } else {
                if (!taskManager.getSubtaskList().isEmpty()) {
                    outputStream(exchange, gson.toJson(taskManager.getSubtaskList()), 200);
                } else {
                    outputStream(exchange, "Список подзадач не найден .", 404);
                }
            }
        }

        private void handleTaskPost(HttpExchange exchange) throws IOException {
            String body = readText(exchange);
            if (body.isBlank()) {
                outputStream(exchange, "В теле запроса необходимо передать Task в формате JSON", 400);
                return;
            }
            Task task = gson.fromJson(body, Task.class);
            Integer id = task.getId();
            if (id == null) {
                taskManager.creationTask(task);
                outputStream(exchange, "Task задача успешно добавлена!", 200);
            } else {
                if (!taskManager.getTasksList().isEmpty()) {
                    taskManager.updateTask(task);
                    outputStream(exchange, "Task задача успешно обновлена!", 200);
                } else {
                    outputStream(exchange, "Произошла ошибка", 404);
                }
            }
        }

        private void handleEpicPost(HttpExchange exchange) throws IOException {
            String body = readText(exchange);
            if (body.isBlank()) {
                outputStream(exchange, "В теле запроса необходимо передать Epic в формате JSON", 400);
                return;
            }
            Epic epic = gson.fromJson(body, Epic.class);
            Integer id = epic.getId();
            if (id == null) {
                taskManager.creationEpic(epic);
                outputStream(exchange, "Epic задача успешно добавлена!", 200);
            } else {
                if (!taskManager.getEpicsList().isEmpty()) {
                    taskManager.updateEpic(epic);
                    outputStream(exchange, "Epic задача успешно обновлена!", 200);
                } else {
                    outputStream(exchange, "Произошла ошибка", 404);
                }
            }
        }

        public void handleSubtaskPost(HttpExchange exchange) throws IOException {
            String body = readText(exchange);
            if (body.isBlank()) {
                outputStream(exchange, "В теле запроса необходимо передать Subtask в формате JSON", 400);
                return;
            }
            Subtask subtask = gson.fromJson(body, Subtask.class);
            Integer id = subtask.getId();
            if (id == null) {
                if (!taskManager.getEpicsList().isEmpty()) {
                    taskManager.creationSubtask(subtask);
                    outputStream(exchange, "Subtask задача успешно добавлена!", 200);
                } else {
                    if (!taskManager.getSubtaskList().isEmpty()) {
                        taskManager.updateSubtask(subtask);
                        outputStream(exchange, "Subtask задача успешно обновлена!", 200);
                    } else {
                        outputStream(exchange, "Произошла ошибка", 404);
                    }
                }
            }
        }

        private void handleTaskDelete(HttpExchange exchange) throws IOException {
            int id = 0;
            if (exchange.getRequestURI().getQuery() != null) {
                id = Integer.parseInt(exchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
            }
            if (exchange.getRequestURI().getQuery() == null) {
                taskManager.deleteTasks();
                outputStream(exchange, "Все задачи удалены.", 200);
            } else {
                Task task = taskManager.getTaskByIdNumber(id);
                taskManager.deleteTaskById(task.getId());
                if (task == null) {
                    outputStream(exchange, "Task задача не найдена", 404);
                } else {
                    outputStream(exchange, "Task задача с номером " + id + "удалена" , 200);
                }
            }
        }

        private void handleEpicDelete(HttpExchange exchange) throws IOException {
            int id = 0;
            if (exchange.getRequestURI().getQuery() != null) {
                id = Integer.parseInt(exchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
            }
            if (exchange.getRequestURI().getQuery() == null) {
                taskManager.deleteEpics();
                outputStream(exchange, "Все задачи удалены.", 200);
            } else {
                Epic epic = taskManager.getEpicTaskByIdNumber(id);
                taskManager.deleteEpicById(epic.getId());
                if (epic == null) {
                    outputStream(exchange, "Epic задача не найдена", 404);
                } else {
                    outputStream(exchange, "Epic задача с номером " + id + "удалена" , 200);
                }
            }
        }

        private void handleSubtaskDelete(HttpExchange exchange) throws IOException {
            int id = 0;
            if (exchange.getRequestURI().getQuery() != null) {
                id = Integer.parseInt(exchange.getRequestURI().toString()
                        .split("\\?")[1].split("=")[1]);
            }
            if (exchange.getRequestURI().getQuery() == null) {
                taskManager.deleteSubtasks();
                outputStream(exchange, "Все задачи удалены.", 200);
            } else {
                Subtask subtask = taskManager.getSubTaskByIdNumber(id);
                taskManager.deleteSubtaskById(subtask.getId());
                if (subtask == null) {
                    outputStream(exchange, "Subtask задача не найдена", 404);
                } else {
                    outputStream(exchange, "Subtask задача с номером " + id + "удалена" , 200);
                }
            }
        }

        private void handleHistoryGet(HttpExchange exchange) throws IOException {
            if (!taskManager.getHistory().isEmpty()) {
                outputStream(exchange, gson.toJson(taskManager.getHistory()), 200);
            } else {
                outputStream(exchange, "История просмотра задач пуста.", 404);
            }
        }
    }
}

