module sk.stuba.collab.study.client {
    requires javafx.controls;
    requires javafx.fxml;

    requires java.net.http;
    requires com.fasterxml.jackson.core;
    requires com.fasterxml.jackson.databind;

    opens sk.stuba.collab.study.client to javafx.fxml;
    opens sk.stuba.collab.study.client.controllers to javafx.fxml;
    opens sk.stuba.collab.study.client.api to com.fasterxml.jackson.databind;

    exports sk.stuba.collab.study.client;
}
