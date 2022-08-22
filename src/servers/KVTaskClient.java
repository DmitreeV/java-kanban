package servers;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class KVTaskClient {

    private final String apiToken;
    private final String url;
    HttpClient client = HttpClient.newHttpClient();
    private HttpResponse<String> response;
    private final HttpResponse.BodyHandler<String> handler = HttpResponse.BodyHandlers.ofString();
    public HttpResponse<String> getResponse() {
        return response;
    }

    public KVTaskClient(String url) throws IOException, InterruptedException {
        this.url = url;
        this.apiToken = register();
    }

    public String register() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(url + "/register"))
                .build();
        response = client.send(request, handler);
        return response.body();
    }

    public void put(String key, String json) throws IOException, InterruptedException {
        URI uri = URI.create(url + "save/" + key + "?API_TOKEN=" + apiToken);
        final HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder()
                .POST(body)
                .uri(uri)
                .build();

        response = client.send(request, handler);
    }

    public String load(String key) throws IOException, InterruptedException {
        URI uri = URI.create(url + "load/" + key + "?API_TOKEN=" + apiToken);
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(uri)
                .build();
        response = client.send(request, handler);
        return response.body();
    }
}
