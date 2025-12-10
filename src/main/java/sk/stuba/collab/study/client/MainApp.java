package sk.stuba.collab.study.client;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage stage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                MainApp.class.getResource("/login.fxml")
        );

        if (loader.getLocation() == null) {
            throw new IllegalStateException("Cannot find /login.fxml on classpath");
        }

        Scene scene = new Scene(loader.load());
        stage.setTitle("Study Client");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
