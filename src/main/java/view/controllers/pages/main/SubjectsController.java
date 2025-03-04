package view.controllers.pages.main;

import controller.BaseController;
import controller.SubjectController;
import dto.SubjectDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import view.controllers.ControllerAware;

import java.util.List;
import java.util.Optional;

public class SubjectsController implements ControllerAware {
    private SubjectController subjectController;
    private List<SubjectDTO> subjects;
    private final List<String> components = List.of("Name", "Code");
    private boolean isEditingMode;
    private int currentIndex;

    private TextField nameTextField;
    private TextField codeTextField;

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
        String title = "Subject";

        titleLabel.setText(title + "s");
        newButton.setText("New " + title);
        addButton.setText("Add " + title);
        saveButton.setText("Save " + title);
        deleteButton.setText("Delete " + title);

        for (String key : components) {
            HBox componentBox = new HBox();
            Label label = new Label(key);
            TextField textField = new TextField();

            textField.setPromptText("Enter " + key);
            textField.setId(key.toLowerCase() + "TextField");

            componentBox.getChildren().addAll(label, textField);
            contentVBox.getChildren().add(contentVBox.getChildren().size() - 1, componentBox);
        }

        Platform.runLater(() -> {
            loadSubjects();
            nameTextField = (TextField) contentVBox.lookup("#nameTextField");
            codeTextField = (TextField) contentVBox.lookup("#codeTextField");

            changeButtonVisibility(false);
        });
    }

    public void setBaseController(BaseController baseController) {
        this.subjectController = baseController.getSubjectController();
    }

    private void loadSubjects() {
        subjects = subjectController.fetchAllSubjects();
        itemView.getItems().clear();
        subjects.forEach(subject -> itemView.getItems().add(subject.name()));
    }

    @FXML
    private void handleNew() {
        if (resolveIsNotSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Create Subject");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to create a new subject?\n" +
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

        String name = nameTextField.getText();
        String code = codeTextField.getText();

        SubjectDTO subject = new SubjectDTO(name, code);
        subjectController.saveSubject(subject);
        loadSubjects();
        clearFields();
    }

    @FXML
    private void handleSave() {
        int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String name = nameTextField.getText();
            String code = codeTextField.getText();

            SubjectDTO subject = new SubjectDTO(subjects.get(selectedIndex).name(), code);
            subjectController.saveSubject(subject);
            loadSubjects();

            itemView.getSelectionModel().select(selectedIndex);
        }
    }

    @FXML
    private void handleDelete() {
        int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Delete Subject");
            alert.setHeaderText(null);
            alert.setContentText("Are you sure you want to delete this subject?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                subjectController.deleteSubject(subjects.get(selectedIndex));
                loadSubjects();
                clearFields();
                changeButtonVisibility(false);
            }
        }
    }

    @FXML
    private void handleItemSelection() {
        if (resolveIsNotSaved()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Select Subject");
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
            nameTextField.setText(subjects.get(selectedIndex).name());
            codeTextField.setText(subjects.get(selectedIndex).code());

            changeButtonVisibility(true);
            currentIndex = selectedIndex;
        }
    }

    private void clearFields() {
        nameTextField.clear();
        codeTextField.clear();
    }

    private void changeButtonVisibility(boolean editingMode) {
        // In editing mode, the add button is not visible, and the edit and delete buttons are visible
        isEditingMode = editingMode;

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
            String savedName = subjects.get(currentIndex).name();
            String savedCode = subjects.get(currentIndex).code();

            return !nameTextField.getText().equals(savedName) || !codeTextField.getText().equals(savedCode);
        } else {
            return !nameTextField.getText().isEmpty() || !codeTextField.getText().isEmpty();
        }
    }

    private boolean areFieldsEmpty() {
        return nameTextField.getText().isEmpty() || codeTextField.getText().isEmpty();
    }
}
