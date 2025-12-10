package sk.stuba.collab.study.client.api;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class ApiClient {

    private final HttpClient client = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    private final String BASE_URL = "http://localhost:8080/api";

    public List<Map<String, Object>> getList(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            return mapper.readValue(response.body(), List.class);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Response get(String path) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BASE_URL + path))
                    .GET()
                    .build();

            HttpResponse<String> response =
                    client.send(request, HttpResponse.BodyHandlers.ofString());

            Map<String, Object> body = null;
            if (!response.body().isEmpty()) {
                body = mapper.readValue(response.body(), Map.class);
            }

            return new Response(response.statusCode(), body);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public record Response(int statusCode, Map<String, Object> body) { }
}
