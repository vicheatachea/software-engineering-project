package view.controllers.pages.main;

import controller.BaseController;
import controller.GroupController;
import controller.SubjectController;
import controller.UserController;
import dto.GroupDTO;
import dto.SubjectDTO;
import dto.UserDTO;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.Set;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.controllers.ControllerAware;

public class GroupsViewController implements ControllerAware {
    private static final Logger logger = LoggerFactory.getLogger(GroupsViewController.class);
    private static final String FIELD = "field";
    private static final String ERROR_TITLE = "error.title";
    private static final String ERROR_GROUP_NOT_TEACHER = "error.group.notTeacher";
    private static final String CSS_STYLING = "-fx-font: 16px \"Verdana\";";

    private ResourceBundle viewText;
    private GroupController groupController;
    private SubjectController subjectController;
    private UserController userController;
    private List<GroupDTO> groups;
    private boolean isEditingMode;
    private int currentIndex;
    private int rowIndex = 0;

    private TextField nameTextField;
    private TextField codeTextField;
    private TextField capacityTextField;
    private ComboBox<String> subjectComboBox;

    private final ComboBox<String> addStudentComboBox = new ComboBox<>();
    private final Button addStudentButton = new Button();
    private final ComboBox<String> removeStudentComboBox = new ComboBox<>();
    private final Button removeStudentButton = new Button();

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
    private GridPane componentGrid;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            titleLabel.setText(viewText.getString("groups.title"));
            newButton.setText(viewText.getString("groups.new"));
            addButton.setText(viewText.getString("groups.add"));
            saveButton.setText(viewText.getString("groups.save"));
            deleteButton.setText(viewText.getString("groups.delete"));

            LinkedHashMap<String, String> components = new LinkedHashMap<>();
            components.put(viewText.getString("common.name"), FIELD);
            components.put(viewText.getString("common.code"), FIELD);
            components.put(viewText.getString("groups.capacity"), FIELD);
            components.put(viewText.getString("groups.subject"), "comboBox");

            String[] prompts = {
                    viewText.getString("common.promptName"),
                    viewText.getString("common.promptCode"),
                    viewText.getString("groups.promptCapacity"),
                    viewText.getString("groups.promptSubject")
            };
            String[] ids = {
                    "nameTextField",
                    "codeTextField",
                    "capacityTextField",
                    "subjectComboBox"
            };

            for (Map.Entry<String, String> component : components.entrySet()) {
                Label label = new Label(component.getKey());
                label.setFont(new Font("Verdana Bold", 18));
                label.setStyle("-fx-text-fill: #e36486;");

                componentGrid.add(label, 0, rowIndex);

                switch (component.getValue()) {
                    case FIELD -> {
                        TextField textField = new TextField();
                        textField.setStyle(CSS_STYLING);
                        textField.setPromptText(prompts[rowIndex]);
                        textField.setId(ids[rowIndex]);

                        componentGrid.add(textField, 1, rowIndex);
                    }
                    case "comboBox" -> {
                        ComboBox<String> comboBox = new ComboBox<>();
                        comboBox.setStyle(CSS_STYLING);
                        comboBox.setPromptText(prompts[rowIndex]);
                        comboBox.setId(ids[rowIndex]);

                        componentGrid.add(comboBox, 1, rowIndex);
                    }
                    default -> logger.info("Unknown component type: {}", component.getValue());
                }
                rowIndex++;
            }

            HBox addHBox = new HBox();
            Label addLabel = new Label(viewText.getString("groups.label.addStudent"));
            addLabel.setStyle("-fx-font: bold 18px \"Verdana\"; -fx-text-fill: #e36486;");

            addStudentComboBox.setStyle(CSS_STYLING);

            addStudentButton.setText(viewText.getString("groups.button.addStudent"));
            addStudentButton.setStyle(CSS_STYLING);
            addStudentButton.setOnAction(event -> handleAddStudent());

            addHBox.getChildren().addAll(addStudentComboBox, addStudentButton);

            HBox removeHBox = new HBox();
            Label removeLabel = new Label(viewText.getString("groups.label.removeStudent"));
            removeLabel.setStyle("-fx-font: bold 18px \"Verdana\"; -fx-text-fill: #e36486;");

            removeStudentComboBox.setStyle(CSS_STYLING);

            removeStudentButton.setText(viewText.getString("groups.button.removeStudent"));
            removeStudentButton.setStyle(CSS_STYLING);
            removeStudentButton.setOnAction(event -> handleRemoveStudent());

            removeHBox.getChildren().addAll(removeStudentComboBox, removeStudentButton);

            componentGrid.add(addLabel, 0, rowIndex);
            componentGrid.add(addHBox, 1, rowIndex);
            componentGrid.add(removeLabel, 0, rowIndex + 1);
            componentGrid.add(removeHBox, 1, rowIndex + 1);

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
        this.viewText = baseController.getLocaleController().getUIBundle();
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
            alert.setTitle(viewText.getString("confirmation.group.create"));
            alert.setHeaderText(null);
            alert.setContentText(String.format("%s%n%s",
                    viewText.getString("confirmation.group.createPrompt"),
                    viewText.getString("confirmation.unsavedChanges"))
            );

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
        if (teacherPermissionError()) {
            return;
        }

        if (areFieldsEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("error.fillAllFields"));
            alert.showAndWait();
            return;
        }

        int capacity;
        try {
             capacity = Integer.parseInt(capacityTextField.getText());
        } catch (NumberFormatException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("error.group.capacityNumber"));
            alert.showAndWait();
            return;
        }
        if (capacity < 1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("error.group.capacityPositive"));
            alert.showAndWait();
            return;
        }

        String name = nameTextField.getText();
        String code = codeTextField.getText();
        String subjectCode = subjectComboBox.getValue();

        for (GroupDTO group : groups) {
            if (group.name().equals(name)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle(viewText.getString(ERROR_TITLE));
                alert.setHeaderText(null);
                alert.setContentText(viewText.getString("error.group.exists"));
                alert.showAndWait();
                return;
            }
        }

        long userId = userController.fetchCurrentUserId();
        if (userId == -1) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("error.group.loggedIn"));
            alert.showAndWait();
            return;
        }

        if (!userController.isCurrentUserTeacher()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString(ERROR_GROUP_NOT_TEACHER));
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
        if (teacherPermissionError()) {
            return;
        }
        if (areFieldsEmpty()) {
            showErrorAlert(viewText.getString(ERROR_TITLE), viewText.getString("error.fillAllFields"));
            return;
        }

        int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
        if (selectedIndex == -1) return;

        try {
            GroupDTO updatedGroup = createUpdatedGroup(selectedIndex);
            updateExistingGroup(updatedGroup, selectedIndex);
            loadGroups();
            itemView.getSelectionModel().select(selectedIndex);
        } catch (ValidationException e) {
            showErrorAlert(ERROR_TITLE, e.getMessage());
        }
    }

    private void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(viewText.getString(title));
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private GroupDTO createUpdatedGroup(int selectedIndex) throws ValidationException {
        String name = nameTextField.getText();
        String code = codeTextField.getText();
        String currentName = groups.get(selectedIndex).name();
        String subjectCode = subjectComboBox.getValue();

        int capacity = validateCapacity(capacityTextField.getText());
        validateUserPermissions();
        validateGroupName(name, currentName);

        long userId = userController.fetchCurrentUserId();
        return new GroupDTO(name, code, capacity, userId, subjectCode);
    }

    private void updateExistingGroup(GroupDTO updatedGroup, int selectedIndex) {
        String currentName = groups.get(selectedIndex).name();
        groupController.updateGroup(updatedGroup, currentName);
    }


    private int validateCapacity(String capacityStr) throws ValidationException {
        try {
            int capacity = Integer.parseInt(capacityStr);
            if (capacity < 1) {
                throw new ValidationException(viewText.getString("error.group.capacityPositive"));
            }
            return capacity;
        } catch (NumberFormatException e) {
            throw new ValidationException(viewText.getString("error.group.capacityNumber"));
        }
    }

    private void validateUserPermissions() throws ValidationException {
        long userId = userController.fetchCurrentUserId();
        if (userId == -1) {
            throw new ValidationException(viewText.getString("error.group.loggedIn"));
        }
        if (!userController.isCurrentUserTeacher()) {
            throw new ValidationException(viewText.getString(ERROR_GROUP_NOT_TEACHER));
        }
    }

    private void validateGroupName(String newName, String currentName) throws ValidationException {
        for (GroupDTO group : groups) {
            if (group.name().equals(newName) && !newName.equals(currentName)) {
                throw new ValidationException(viewText.getString("error.group.exists"));
            }
        }
    }

    @FXML
    private void handleDelete() {
        if (teacherPermissionError()) {
            return;
        }

        int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String name = itemView.getSelectionModel().getSelectedItem();
            GroupDTO group = groupController.fetchGroupByName(name);

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle(viewText.getString("confirmation.group.delete"));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("confirmation.group.deletePrompt"));

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
            alert.setTitle(viewText.getString("confirmation.group.select"));
            alert.setHeaderText(null);
            alert.setContentText(String.format("%s%n%s",
                    viewText.getString("confirmation.selectPrompt"),
                    viewText.getString("confirmation.unsavedChanges"))
            );

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
            updateStudentBoxes();
            currentIndex = selectedIndex;
        }
    }

    private void handleAddStudent() {
        if (addStudentComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("error.group.addStudent"));
            alert.showAndWait();
            return;
        }

        if (teacherPermissionError()) {
            return;
        }

        String groupName = itemView.getSelectionModel().getSelectedItem();
        GroupDTO group = groupController.fetchGroupByName(groupName);

        String studentUsername = addStudentComboBox.getValue();
        groupController.addStudentToGroup(group, studentUsername);
        updateStudentBoxes();
    }

    private void handleRemoveStudent() {
        if (removeStudentComboBox.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString("error.group.removeStudent"));
            alert.showAndWait();
            return;
        }

        if (teacherPermissionError()) {
            return;
        }

        String groupName = itemView.getSelectionModel().getSelectedItem();
        GroupDTO group = groupController.fetchGroupByName(groupName);

        String studentUsername = removeStudentComboBox.getValue();
        groupController.removeStudentFromGroup(group, studentUsername);
        updateStudentBoxes();
    }

    private void updateStudentBoxes() {
        addStudentComboBox.getItems().clear();
        removeStudentComboBox.getItems().clear();

        UserDTO currentUser = userController.getLoggedInUser();
        List<UserDTO> students = userController.fetchAllStudents();
        Set<UserDTO> groupStudents = userController.fetchStudentsInGroup(itemView.getSelectionModel().getSelectedItem());

        if (students != null) {
            students.forEach(student -> {
                if (!student.username().equals(currentUser.username()) && student.role().equals("STUDENT") && !groupStudents.contains(student)) {
                    addStudentComboBox.getItems().add(student.username());
                }
            });
        }

        if (groupStudents != null) {
            groupStudents.forEach(student -> removeStudentComboBox.getItems().add(student.username()));
        }
    }

    private void clearFields() {
        nameTextField.clear();
        codeTextField.clear();
        capacityTextField.clear();
        subjectComboBox.getSelectionModel().clearSelection();
    }

    private void changeButtonVisibility(boolean editingMode) {
        // In editing mode, the addButton is not visible, and the edit and delete buttons are visible
        isEditingMode = editingMode;

        // Hide or show the options to add and remove students
        for (Node child : componentGrid.getChildren()) {
            if (GridPane.getRowIndex(child) == rowIndex || GridPane.getRowIndex(child) == rowIndex + 1) {
                child.setManaged(editingMode);
                child.setVisible(editingMode);
            }
        }

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

    private boolean teacherPermissionError() {
        if (!userController.isCurrentUserTeacher()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(viewText.getString(ERROR_TITLE));
            alert.setHeaderText(null);
            alert.setContentText(viewText.getString(ERROR_GROUP_NOT_TEACHER));
            alert.showAndWait();
            return true;
        }
        return false;
    }

    private static class ValidationException extends Exception {
        public ValidationException(String message) {
            super(message);
        }
    }
}
