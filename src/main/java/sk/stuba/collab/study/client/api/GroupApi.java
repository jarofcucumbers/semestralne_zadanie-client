package sk.stuba.collab.study.client.api;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GroupApi {

    private final ApiClient api = new ApiClient();

    // Повертаємо вже СПРАВЖНІ групи, а не Membership
    public List<Map<String, Object>> getGroupsForUser(Long userId) {
        // 1. тягнемо membership
        List<Map<String, Object>> memberships = api.getList("/memberships/user/" + userId);

        List<Map<String, Object>> result = new ArrayList<>();

        for (Map<String, Object> m : memberships) {
            Object gidObj = m.get("groupId");
            if (!(gidObj instanceof Number)) continue;

            long groupId = ((Number) gidObj).longValue();

            // 2. для кожного groupId тягнемо /groups/{id}
            ApiClient.Response groupResp = api.get("/groups/" + groupId);
            if (groupResp.statusCode() == 200 && groupResp.body() != null) {
                result.add(groupResp.body());
            }
        }

        return result;
    }
}
