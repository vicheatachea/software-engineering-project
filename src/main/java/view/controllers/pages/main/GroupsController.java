package view.controllers.pages.main;

import controller.BaseController;
import controller.GroupController;
import controller.SubjectController;
import controller.UserController;
import dto.GroupDTO;
import dto.SubjectDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import view.controllers.ControllerAware;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

public class GroupsController implements ControllerAware {
    private GroupController groupController;
    private SubjectController subjectController;
    private UserController userController;
    private List<GroupDTO> groups;
    private final LinkedHashMap<String, String> components = new LinkedHashMap<>() {{
        put("Name", "field");
        put("Code", "field");
        put("Capacity", "field");
        put("Subject", "comboBox");
    }};
    private boolean isEditingMode;
    private int currentIndex;

    private TextField nameTextField;
    private TextField codeTextField;
    private TextField capacityTextField;
    private ComboBox<String> subjectComboBox;

    @FXML
    private Label titleLabel;
    @FXML
    private VBox contentVBox;
    @FXML
    private Button newButton;
    @FXML
    private Button addButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Region optionalRegion;
    @FXML
    private ListView<String> itemView;

    @FXML
    public void initialize() {
        String title = "Group";

        titleLabel.setText(title + "s");
        newButton.setText("New " + title);
        addButton.setText("Add " + title);
        saveButton.setText("Save " + title);
        deleteButton.setText("Delete " + title);

        for (String key : components.keySet()) {
            HBox componentBox = new HBox();
            Label label = new Label(key + ":");

            switch (components.get(key)) {
                case "field" -> {
                    TextField textField = new TextField();
                    textField.setPromptText("Enter " + key);
                    textField.setId(key.toLowerCase() + "TextField");
                    componentBox.getChildren().addAll(label, textField);
                }
                case "comboBox" -> {
                    ComboBox<String> comboBox = new ComboBox<>();
                    comboBox.setPromptText("Select " + key);
                    comboBox.setId(key.toLowerCase() + "ComboBox");
                    componentBox.getChildren().addAll(label, comboBox);
                }
            }
            contentVBox.getChildren().add(contentVBox.getChildren().size() - 1, componentBox);
        }

        Platform.runLater(() -> {
            loadGroups();
            nameTextField = (TextField) contentVBox.lookup("#nameTextField");
            codeTextField = (TextField) contentVBox.lookup("#codeTextField");
            capacityTextField = (TextField) contentVBox.lookup("#capacityTextField");
            subjectComboBox = (ComboBox<String>) contentVBox.lookup("#subjectComboBox");

            changeButtonVisibility(false);

            List<SubjectDTO> subjects = subjectController.fetchAllSubjects();
            subjects.forEach(subject -> subjectComboBox.getItems().add(subject.code()));
        });
    }

    public void setBaseController(BaseController baseController) {
        this.groupController = baseController.getGroupController();
        this.subjectController = baseController.getSubjectController();
        this.userController = baseController.getUserController();
    }

    private void loadGroups() {
        groups = groupController.fetchAllGroups();
        itemView.getItems().clear();
        groups.forEach(group -> itemView.getItems().add(group.name()));
    }

    @FXML
    private void handleNew() {
        if (resolveIsNotSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Create Group");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to create a new group?\n" +
                    "Any unsaved changes will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }
        }
        clearFields();
        changeButtonVisibility(false);
        itemView.getSelectionModel().clearSelection();
    }

    @FXML
    private void handleAdd() {
        if (areFieldsEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required.");
            alert.showAndWait();
            return;
        }

        int capacity;
        try {
             capacity = Integer.parseInt(capacityTextField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Capacity must be a number.");
            alert.showAndWait();
            return;
        }
        if (capacity < 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("Capacity must be greater than 0.");
            alert.showAndWait();
            return;
        }

        String name = nameTextField.getText();
        String code = codeTextField.getText();
        String subjectCode = subjectComboBox.getValue();

        for (GroupDTO group : groups) {
            if (group.name().equals(name)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("A group with the same name already exists.");
                alert.showAndWait();
                return;
            }
        }

        long userId = userController.fetchCurrentUserId();
        if (userId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must be logged in to add a group.");
            alert.showAndWait();
            return;
        }

        if (!userController.isCurrentUserTeacher()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("You must be a teacher to save a group.");
            alert.showAndWait();
            return;
        }

        GroupDTO group = new GroupDTO(name, code, capacity, userId, subjectCode);
        groupController.addGroup(group);
        loadGroups();
        clearFields();
    }

    @FXML
    private void handleSave() {
        if (areFieldsEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText("All fields are required.");
            alert.showAndWait();
            return;
        }

        int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            int capacity;
            try {
                capacity = Integer.parseInt(capacityTextField.getText());
            } catch (NumberFormatException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Capacity must be a number.");
                alert.showAndWait();
                return;
            }
            if (capacity < 1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("Capacity must be greater than 0.");
                alert.showAndWait();
                return;
            }

            String name = nameTextField.getText();
            String code = codeTextField.getText();
            String currentName = groups.get(selectedIndex).name();
            String subjectCode = subjectComboBox.getValue();

            long userId = userController.fetchCurrentUserId();
            if (userId == -1) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You must be logged in to save a group.");
                alert.showAndWait();
                return;
            }

            if (!userController.isCurrentUserTeacher()) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText(null);
                alert.setContentText("You must be a teacher to save a group.");
                alert.showAndWait();
                return;
            }

            GroupDTO group = new GroupDTO(name, code, capacity, userId, subjectCode);
            groupController.updateGroup(group, currentName);
            loadGroups();

            itemView.getSelectionModel().select(selectedIndex);
        }
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String name = itemView.getSelectionModel().getSelectedItem();
            GroupDTO group = groupController.fetchGroupByName(name);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Group");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete the group " + name + "?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                groupController.deleteGroup(group);
                loadGroups();
                clearFields();
                changeButtonVisibility(false);
            }
        }
    }

    @FXML
    private void handleItemSelection() {
        if (resolveIsNotSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Select Group");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to change items?\n" +
                    "Any unsaved changes will be lost.");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                itemView.getSelectionModel().select(currentIndex);
                return;
            }
        }
        int selectedIndex = itemView.getSelectionModel().getSelectedIndex();

        if (selectedIndex != -1) {
            GroupDTO group = groups.get(selectedIndex);

            nameTextField.setText(group.name());
            codeTextField.setText(group.code());
            capacityTextField.setText(String.valueOf(group.capacity()));
            subjectComboBox.setValue(group.subjectCode());

            changeButtonVisibility(true);
            currentIndex = selectedIndex;
        }
    }

    private void clearFields() {
        nameTextField.clear();
        codeTextField.clear();
        capacityTextField.clear();
        subjectComboBox.getSelectionModel().clearSelection();
    }

    private void changeButtonVisibility(boolean editingMode) {
        // In editing mode, the add button is not visible, and the edit and delete buttons are visible
        isEditingMode = editingMode;

        nameTextField.setDisable(editingMode);

        addButton.setVisible(!editingMode);
        addButton.setManaged(!editingMode);

        saveButton.setVisible(editingMode);
        saveButton.setManaged(editingMode);

        optionalRegion.setVisible(editingMode);
        optionalRegion.setManaged(editingMode);

        deleteButton.setVisible(editingMode);
        deleteButton.setManaged(editingMode);
    }

    private boolean resolveIsNotSaved() {
        if (isEditingMode) {
            String savedName = groups.get(currentIndex).name();
            String savedCode = groups.get(currentIndex).code();
            String savedCapacity = String.valueOf(groups.get(currentIndex).capacity());
            String savedSubjectCode = groups.get(currentIndex).subjectCode();

            return !nameTextField.getText().equals(savedName) || !codeTextField.getText().equals(savedCode) || !capacityTextField.getText().equals(savedCapacity) || !subjectComboBox.getValue().equals(savedSubjectCode);
        } else {
            return !nameTextField.getText().isEmpty() || !codeTextField.getText().isEmpty() || !capacityTextField.getText().isEmpty() || subjectComboBox.getValue() != null;
        }
    }

    private boolean areFieldsEmpty() {
        return nameTextField.getText().isEmpty() || codeTextField.getText().isEmpty() || capacityTextField.getText().isEmpty() || subjectComboBox.getValue() == null;
    }
}
