package sk.stuba.collab.study.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sk.stuba.collab.study.client.api.GroupApi;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class GroupsController {

    @FXML private ListView<String> groupsList;
    @FXML private Label statusLabel;
    @FXML private TextField groupNameField;
    @FXML private TextField groupDescField;

    private final GroupApi groupApi = new GroupApi();
    private Long userId;
    private List<Map<String, Object>> currentGroups;

    public void setUserId(Long id) {
        this.userId = id;
        loadGroups();
    }

    private void loadGroups() {
        try {
            currentGroups = groupApi.getGroupsForUser(userId);

            groupsList.getItems().clear();

            for (Map<String, Object> g : currentGroups) {
                System.out.println("Group map: " + g);

                String name = null;

                Object n1 = g.get("name");
                if (n1 != null) {
                    name = n1.toString();
                }

                if (name == null) {
                    Object n2 = g.get("groupName");
                    if (n2 != null) {
                        name = n2.toString();
                    }
                }

                if (name == null) {
                    Object idObj = g.get("id");
                    name = "Group #" + (idObj != null ? idObj.toString() : "?");
                }

                groupsList.getItems().add(name);
            }

            if (currentGroups.isEmpty()) {
                statusLabel.setText("You are not a member of any group.");
            } else {
                statusLabel.setText("");
            }

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Cannot load groups.");
        }
    }

    @FXML
    public void openSelectedGroup() {
        int idx = groupsList.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            statusLabel.setText("Select a group!");
            return;
        }

        Map<String, Object> group = currentGroups.get(idx);

        Object gidObj = group.get("groupId");

        if (gidObj == null) {
            gidObj = group.get("group_id");
        }

        if (gidObj == null) {
            gidObj = group.get("id");
        }

        if (!(gidObj instanceof Number)) {
            System.out.println("Cannot determine groupId from map: " + group);
            statusLabel.setText("Cannot open group (no groupId).");
            return;
        }

        Long groupId = ((Number) gidObj).longValue();

        String groupName = groupsList.getItems().get(idx);

        System.out.println("Opening group " + groupId + " : " + groupName);

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tasks.fxml"));
            Scene scene = new Scene(loader.load());

            TasksController controller = loader.getController();
            controller.init(userId, groupId, groupName);

            Stage stage = (Stage) groupsList.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Cannot open tasks view.");
        }
    }

    @FXML
    public void createGroup() {
        String name = groupNameField.getText().trim();
        String desc = groupDescField.getText().trim();

        if (name.isEmpty()) {
            statusLabel.setText("Group name required.");
            return;
        }

        boolean ok = groupApi.createGroup(userId, name, desc);

        if (ok) {
            statusLabel.setText("Group created.");
            groupNameField.clear();
            groupDescField.clear();
            loadGroups();
        } else {
            statusLabel.setText("Cannot create group.");
        }
    }
}
