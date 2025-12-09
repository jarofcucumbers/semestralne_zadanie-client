package sk.stuba.collab.study.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sk.stuba.collab.study.client.api.UserApi;

import java.io.IOException;

public class LoginController {

    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserApi userApi = new UserApi();

    @FXML
    public void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        var result = userApi.login(email, password);

        if (!result.success()) {
            statusLabel.setText("Invalid login.");
            return;
        }

        try {
            // поточне вікно
            Stage stage = (Stage) emailField.getScene().getWindow();

            // завантажуємо вікно груп
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groups.fxml"));
            Scene scene = new Scene(loader.load());

            // передаємо userId у контролер груп
            GroupsController controller = loader.getController();
            controller.setUserId(result.userId());

            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Cannot open groups window.");
        }
    }

}
