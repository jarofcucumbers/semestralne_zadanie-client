package sk.stuba.collab.study.client.api;

import java.util.Map;

public class UserApi {

    private final ApiClient api = new ApiClient();

    public LoginResponse login(String email, String password) {
        try {
            ApiClient.Response resp =
                    api.get("/users/login?email=" + email + "&password=" + password);

            // якщо бекенд повернув помилку або 401
            if (resp.statusCode() != 200 || resp.body() == null) {
                String msg = resp.body() != null && resp.body().get("error") != null
                        ? (String) resp.body().get("error")
                        : "Login failed";
                return new LoginResponse(false, null, null, msg);
            }

            Map<String, Object> body = resp.body();

            Object userObj = body.get("user");
            if (!(userObj instanceof Map)) {
                return new LoginResponse(false, null, null, "Invalid response format");
            }

            Map<String, Object> user = (Map<String, Object>) userObj;
            Long userId = ((Number) body.get("userId")).longValue();
            String username = (String) user.get("name");

            return new LoginResponse(true, userId, username, "OK");

        } catch (Exception e) {
            e.printStackTrace();
            return new LoginResponse(false, null, null, "Exception: " + e.getMessage());
        }
    }

    public record LoginResponse(boolean success,
                                Long userId,
                                String username,
                                String message) {}
}
