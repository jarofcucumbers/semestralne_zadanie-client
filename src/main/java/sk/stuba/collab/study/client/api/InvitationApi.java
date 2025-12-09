package sk.stuba.collab.study.client.api;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

public class InvitationApi {

    private final ApiClient api = new ApiClient();

    // Відправити інвайт
    public boolean sendInvite(Long groupId, String email) {
        String enc = URLEncoder.encode(email, StandardCharsets.UTF_8);
        ApiClient.Response resp =
                api.get("/invitations/send?groupId=" + groupId + "&email=" + enc);
        return resp.statusCode() == 200;
    }

    // Всі PENDING інвайти для email
    public List<Map<String, Object>> getPendingForEmail(String email) {
        String enc = URLEncoder.encode(email, StandardCharsets.UTF_8);
        return api.getList("/invitations/pending?email=" + enc);
    }

    // Прийняти інвайт
    public boolean accept(Long invitationId, Long userId) {
        ApiClient.Response resp =
                api.get("/invitations/" + invitationId + "/accept?userId=" + userId);
        return resp.statusCode() == 200;
    }

    // Відхилити інвайт
    public boolean decline(Long invitationId) {
        ApiClient.Response resp =
                api.get("/invitations/" + invitationId + "/decline");
        return resp.statusCode() == 200;
    }
}
