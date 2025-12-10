package sk.stuba.collab.study.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sk.stuba.collab.study.client.api.UserApi;

import java.io.IOException;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final UserApi userApi = new UserApi();

    @FXML
    public void handleRegister() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String pass = passwordField.getText().trim();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            statusLabel.setText("All fields are required.");
            return;
        }

        boolean ok = userApi.register(name, email, pass);
        if (ok) {
            statusLabel.setText("Account created. You can login now.");
            goToLogin();
        } else {
            statusLabel.setText("Cannot register (email already used?).");
        }
    }

    @FXML
    public void goToLogin() {
        try {
            Stage stage = (Stage) nameField.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Scene scene = new Scene(loader.load());
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Cannot open login screen.");
        }
    }
}
