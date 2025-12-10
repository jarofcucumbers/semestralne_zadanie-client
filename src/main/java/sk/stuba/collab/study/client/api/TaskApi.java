package sk.stuba.collab.study.client.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class TaskApi {

    private final ApiClient api = new ApiClient();

    public List<Map<String, Object>> getTasksForGroup(Long groupId) {
        return api.getList("/tasks/group/" + groupId);
    }

    public boolean createTask(Long groupId,
                              String title,
                              String description,
                              Long createdBy) {

        String encTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String encDesc  = URLEncoder.encode(description, StandardCharsets.UTF_8);

        String path = "/tasks/create"
                + "?groupId=" + groupId
                + "&title=" + encTitle
                + "&description=" + encDesc
                + "&createdBy=" + createdBy;

        ApiClient.Response resp = api.get(path);
        return resp.statusCode() == 200;
    }

    public boolean closeTask(Long taskId) {
        ApiClient.Response resp = api.get("/tasks/" + taskId + "/close");
        return resp.statusCode() == 200;
    }

    public boolean updateTask(Long taskId,
                              String title,
                              String description) {

        String encTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);
        String encDesc  = URLEncoder.encode(description, StandardCharsets.UTF_8);

        String path = "/tasks/" + taskId
                + "/update?title=" + encTitle
                + "&description=" + encDesc;

        ApiClient.Response resp = api.get(path);
        return resp.statusCode() == 200;
    }

    public boolean deleteTask(Long taskId) {
        ApiClient.Response resp = api.get("/tasks/" + taskId + "/delete");
        return resp.statusCode() == 200;
    }
}
