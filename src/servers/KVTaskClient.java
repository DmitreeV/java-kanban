package servers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private static final String URN_REGISTER = "/register";
    private static final String URN_SAVE = "/save/";
    private static final String URN_LOAD = "/load/";
    private final String API_TOKEN;
    private final HttpClient client = HttpClient.newHttpClient();
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    private final String url;
    private HttpResponse<String> response;

    public KVTaskClient(String url) {
        this.url = url;
        URI uri = URI.create(url + URN_REGISTER);
        HttpRequest request = HttpRequest.newBuilder().GET().uri(uri)
                .version(HttpClient.Version.HTTP_1_1).header("Accept", "application/json")
                .build();
        try {
            response = client.send(request, handler);

            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
        API_TOKEN = response.body();
    }

    public void put(String key, String json) {
        URI uri = URI.create(url + URN_SAVE + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().POST(HttpRequest.BodyPublishers.ofString(json))
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        try {
            response = client.send(request, handler);
            System.out.println(response.statusCode());
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
    }

    public String load(String key) {
        URI uri = URI.create(url + URN_LOAD + key + "?API_TOKEN=" + API_TOKEN);
        HttpRequest request = HttpRequest.newBuilder().GET()
                .uri(uri).version(HttpClient.Version.HTTP_1_1)
                .header("Accept", "application/json").build();
        try {
            response = client.send(request, handler);
            if (response.statusCode() != 200) {
                throw new RuntimeException();
            }
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException();
        }
        return response.body();
    }
}
