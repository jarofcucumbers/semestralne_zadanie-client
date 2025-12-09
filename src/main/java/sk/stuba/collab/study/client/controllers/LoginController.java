package sk.stuba.collab.study.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.stuba.collab.study.client.api.InvitationApi;
import sk.stuba.collab.study.client.api.UserApi;
import sk.stuba.collab.study.client.api.UserApi.LoginResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserApi userApi = new UserApi();
    private final InvitationApi invitationApi = new InvitationApi();

    @FXML
    public void handleLogin() {
        String email = emailField.getText().trim();
        String password = passwordField.getText().trim();

        if (email.isEmpty() || password.isEmpty()) {
            statusLabel.setText("Email and password are required.");
            return;
        }

        LoginResponse result = userApi.login(email, password);

        if (!result.success()) {
            statusLabel.setText("Login failed: " + result.message());
            return;
        }

        Long userId = result.userId();
        String username = result.username(); // якщо захочеш десь показувати

        // 1) Перевіряємо, чи є запрошення для цього email
        try {
            handleInvitationsForUser(email, userId);
        } catch (Exception e) {
            // якщо щось впало з інвайтами, логінимось далі, просто покажемо помилку
            e.printStackTrace();
            statusLabel.setText("Login OK, but cannot load invitations.");
        }

        // 2) Переходимо на екран груп
        try {
            openGroupsWindow(userId);
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Login ok, but cannot open groups.");
        }
    }

    /**
     * Обробка pending-запрошень для цього email:
     * показуємо діалог по кожному інвайту:
     *  - OK → accept (додає у group)
     *  - Cancel → decline
     */
    private void handleInvitationsForUser(String email, Long userId) {
        List<Map<String, Object>> invites = invitationApi.getPendingForEmail(email);

        for (Map<String, Object> inv : invites) {
            Object idObj = inv.get("id");
            Object gidObj = inv.get("groupId");
            if (!(idObj instanceof Number) || !(gidObj instanceof Number)) {
                continue;
            }

            long invId = ((Number) idObj).longValue();
            long groupId = ((Number) gidObj).longValue();

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Group invitation");
            alert.setHeaderText("You were invited to group #" + groupId);
            alert.setContentText("Do you want to join this group?");

            var res = alert.showAndWait();
            if (res.isPresent() && res.get() == ButtonType.OK) {
                invitationApi.accept(invId, userId);
            } else {
                invitationApi.decline(invId);
            }
        }
    }

    /**
     * Відкриваємо groups.fxml і передаємо userId в GroupsController.
     */
    private void openGroupsWindow(Long userId) throws IOException {
        Stage stage = (Stage) emailField.getScene().getWindow();

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/groups.fxml"));
        Scene scene = new Scene(loader.load());

        GroupsController controller = loader.getController();
        controller.setUserId(userId);

        stage.setScene(scene);
        stage.show();
    }

    /**
     * Обробка кнопки "Register" на екрані логіну.
     */
    @FXML
    public void goToRegister() {
        try {
            Stage stage = (Stage) emailField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Cannot open register screen.");
        }
    }
}
