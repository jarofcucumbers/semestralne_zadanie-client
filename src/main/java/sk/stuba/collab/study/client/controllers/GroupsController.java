package sk.stuba.collab.study.client.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import sk.stuba.collab.study.client.api.GroupApi;

import java.util.List;
import java.util.Map;

public class GroupsController {

    @FXML private ListView<String> groupsList;
    @FXML private Label statusLabel;

    private final GroupApi groupApi = new GroupApi();

    private Long userId;
    private List<Map<String, Object>> currentGroups; // тут лежать і id, і name

    public void setUserId(Long id) {
        this.userId = id;
        loadGroups();
    }

    private void loadGroups() {
        try {
            currentGroups = groupApi.getGroupsForUser(userId);

            groupsList.getItems().clear();

            for (Map<String, Object> g : currentGroups) {
                // DEBUG: можна глянути, що реально приходить
                System.out.println("Group map: " + g);

                String name = null;

                // 1) пробуємо "name"
                Object n1 = g.get("name");
                if (n1 != null) {
                    name = n1.toString();
                }

                // 2) якщо нема - пробуємо "groupName"
                if (name == null) {
                    Object n2 = g.get("groupName");
                    if (n2 != null) {
                        name = n2.toString();
                    }
                }

                // 3) якщо все одно null – fallback
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
        Long groupId = ((Number) group.get("id")).longValue();

        // nazva tak, yak vona vidobrazaetsja u ListView
        String groupName = groupsList.getItems().get(idx);

        System.out.println("Opening group " + groupId + " : " + groupName);

        // tut dali zrobymo perexid na tasks.fxml i peredamo userId + groupId + groupName
    }

}
