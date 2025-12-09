package sk.stuba.collab.study.client.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sk.stuba.collab.study.client.api.InvitationApi;
import sk.stuba.collab.study.client.api.TaskApi;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class TasksController {

    @FXML private Label groupTitleLabel;
    @FXML private ListView<String> tasksList;
    @FXML private TextField newTaskTitleField;
    @FXML private TextArea newTaskDescField;
    @FXML private Label statusLabel;

    private final TaskApi taskApi = new TaskApi();
    private final InvitationApi invitationApi = new InvitationApi();

    private Long userId;
    private Long groupId;
    private String groupName;

    private List<Map<String, Object>> currentTasks;

    /** Викликається з GroupsController після завантаження FXML */
    public void init(Long userId, Long groupId, String groupName) {
        this.userId = userId;
        this.groupId = groupId;
        this.groupName = groupName;

        groupTitleLabel.setText("Group: " + groupName);
        attachContextMenu();
        refreshTasks();
    }

    /** Підвантажити задачі для групи */
    @FXML
    public void refreshTasks() {
        try {
            currentTasks = taskApi.getTasksForGroup(groupId);
            tasksList.getItems().clear();

            if (currentTasks.isEmpty()) {
                tasksList.getItems().add("[No tasks in this group]");
            } else {
                for (Map<String, Object> t : currentTasks) {
                    String status = String.valueOf(t.getOrDefault("status", "OPEN"));
                    String title = String.valueOf(t.getOrDefault("title", "(no title)"));
                    tasksList.getItems().add("[" + status + "] " + title);
                }
            }
            statusLabel.setText("");

        } catch (Exception e) {
            e.printStackTrace();
            statusLabel.setText("Cannot load tasks.");
        }
    }

    /** Додати нову задачу */
    @FXML
    public void addTask() {
        String title = newTaskTitleField.getText().trim();
        String desc  = newTaskDescField.getText().trim();

        if (title.isEmpty()) {
            statusLabel.setText("Title cannot be empty.");
            return;
        }

        boolean ok = taskApi.createTask(groupId, title, desc, userId);
        if (ok) {
            statusLabel.setText("Task created.");
            newTaskTitleField.clear();
            newTaskDescField.clear();
            refreshTasks();
        } else {
            statusLabel.setText("Cannot create task.");
        }
    }

    /** Контекстне меню для списку задач (ПРАВА КНОПКА МИШІ) */
    private void attachContextMenu() {
        ContextMenu menu = new ContextMenu();

        MenuItem backItem = new MenuItem("Back to groups");
        backItem.setOnAction(e -> goBack());

        MenuItem editItem = new MenuItem("Edit task");
        editItem.setOnAction(e -> editSelectedTask());

        MenuItem closeItem = new MenuItem("Close task");
        closeItem.setOnAction(e -> closeSelectedTask());

        MenuItem deleteItem = new MenuItem("Delete task");
        deleteItem.setOnAction(e -> deleteSelectedTask());

        MenuItem infoItem = new MenuItem("Task details");
        infoItem.setOnAction(e -> showTaskInfo());

        menu.getItems().addAll(backItem, editItem, closeItem, deleteItem, infoItem);
        tasksList.setContextMenu(menu);
    }

    /** Повернення до списку груп */
    private void goBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/groups.fxml"));
            Scene scene = new Scene(loader.load());

            GroupsController controller = loader.getController();
            controller.setUserId(userId);

            Stage stage = (Stage) tasksList.getScene().getWindow();
            stage.setScene(scene);
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            statusLabel.setText("Cannot go back to groups.");
        }
    }

    /** Закрити задачу (поставити статус CLOSED) */
    private void closeSelectedTask() {
        Integer idx = getRealTaskIndex();
        if (idx == null) return;

        Map<String, Object> t = currentTasks.get(idx);
        Long taskId = ((Number) t.get("id")).longValue();

        boolean ok = taskApi.closeTask(taskId);
        if (ok) {
            statusLabel.setText("Task closed.");
            refreshTasks();
        } else {
            statusLabel.setText("Cannot close task.");
        }
    }

    /** Видалити задачу */
    private void deleteSelectedTask() {
        Integer idx = getRealTaskIndex();
        if (idx == null) return;

        Map<String, Object> t = currentTasks.get(idx);
        Long taskId = ((Number) t.get("id")).longValue();

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Delete task");
        alert.setHeaderText("Are you sure you want to delete this task?");
        alert.setContentText(String.valueOf(t.get("title")));

        Optional<ButtonType> res = alert.showAndWait();
        if (res.isEmpty() || res.get() != ButtonType.OK) return;

        boolean ok = taskApi.deleteTask(taskId);
        if (ok) {
            statusLabel.setText("Task deleted.");
            refreshTasks();
        } else {
            statusLabel.setText("Cannot delete task.");
        }
    }

    /** Редагувати задачу (тільки title/description, простий варіант) */
    private void editSelectedTask() {
        Integer idx = getRealTaskIndex();
        if (idx == null) return;

        Map<String, Object> t = currentTasks.get(idx);
        Long taskId = ((Number) t.get("id")).longValue();
        String oldTitle = String.valueOf(t.get("title"));
        String oldDesc  = String.valueOf(t.getOrDefault("description", ""));

        TextInputDialog dialog = new TextInputDialog(oldTitle);
        dialog.setTitle("Edit task");
        dialog.setHeaderText("Edit task title");
        dialog.setContentText("Title:");
        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty() || res.get().trim().isEmpty()) {
            return;
        }
        String newTitle = res.get().trim();

        // простий варіант: опис не змінюємо, або можна зробити окремий діалог.
        boolean ok = taskApi.updateTask(taskId, newTitle, oldDesc);
        if (ok) {
            statusLabel.setText("Task updated.");
            refreshTasks();
        } else {
            statusLabel.setText("Cannot update task.");
        }
    }

    /** Показати деталі задачі */
    private void showTaskInfo() {
        Integer idx = getRealTaskIndex();
        if (idx == null) return;

        Map<String, Object> t = currentTasks.get(idx);

        StringBuilder sb = new StringBuilder();
        sb.append("Title: ").append(t.get("title")).append("\n");
        sb.append("Status: ").append(t.get("status")).append("\n");
        sb.append("Description: ").append(
                String.valueOf(t.getOrDefault("description", ""))).append("\n");
        sb.append("Created by: ").append(
                String.valueOf(t.getOrDefault("createdBy", "unknown"))).append("\n");
        sb.append("Created at: ").append(
                String.valueOf(t.getOrDefault("createdAt", "unknown"))).append("\n");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Task details");
        alert.setHeaderText("Task information");
        alert.setContentText(sb.toString());
        alert.showAndWait();
    }

    /** Повертає індекс обраної задачі в currentTasks, або null якщо обраний псевдо-рядок */
    private Integer getRealTaskIndex() {
        int idx = tasksList.getSelectionModel().getSelectedIndex();
        if (idx < 0) {
            statusLabel.setText("Select a task.");
            return null;
        }
        if (currentTasks == null || currentTasks.isEmpty()) {
            statusLabel.setText("No real task selected.");
            return null;
        }
        // якщо показували "[No tasks in this group]" – currentTasks порожній
        return idx;
    }

    /** Invite: запитуємо email і шлемо інвайт */
    @FXML
    public void inviteUser() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Invite user");
        dialog.setHeaderText("Invite user to this group");
        dialog.setContentText("User email:");

        Optional<String> res = dialog.showAndWait();
        if (res.isEmpty()) return;

        String email = res.get().trim();
        if (email.isEmpty()) {
            statusLabel.setText("Email cannot be empty.");
            return;
        }

        // 🔧 ТУТ ЛИШЕ 2 ПАРАМЕТРИ
        boolean ok = invitationApi.sendInvite(groupId, email);

        if (ok) {
            statusLabel.setText("Invitation sent.");
        } else {
            statusLabel.setText("Cannot send invitation.");
        }
    }

}
