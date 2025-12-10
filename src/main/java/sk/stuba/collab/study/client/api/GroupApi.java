package sk.stuba.collab.study.client.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class GroupApi {

    private final ApiClient api = new ApiClient();

/**  Get all groups that the user belongs to.
/    We get the list of memberships via:
/    GET /api/memberships/user/{userId}
    Each membership contains groupId → therefore ApiClient.getList returns maps. */

    public List<Map<String, Object>> getGroupsForUser(Long userId) {
        return api.getList("/memberships/user/" + userId);
    }


    public boolean createGroup(Long ownerId, String name, String description) {
        try {
            String encName = URLEncoder.encode(name, StandardCharsets.UTF_8);
            String encDesc = URLEncoder.encode(description, StandardCharsets.UTF_8);

            String url = "/groups/create"
                    + "?name=" + encName
                    + "&description=" + encDesc
                    + "&ownerId=" + ownerId;

            ApiClient.Response resp = api.get(url);

            System.out.println("CreateGroup Request URL = " + url);
            System.out.println("CreateGroup Response Code = " + resp.statusCode());
            System.out.println("CreateGroup Body = " + resp.body());

            return resp.statusCode() == 200;

        } catch (Exception e) {
            System.out.println("Exception in createGroup: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}
