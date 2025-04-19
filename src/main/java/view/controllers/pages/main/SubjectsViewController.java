package view.controllers.pages.main;

import controller.BaseController;
import controller.SubjectController;
import dto.SubjectDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import view.controllers.ControllerAware;

import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class SubjectsViewController implements ControllerAware {
	private static final String ERROR_TITLE = "error.title";

	private ResourceBundle viewText;
	private SubjectController subjectController;
	private List<SubjectDTO> subjects;
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
	private GridPane componentGrid;

	@FXML
	public void initialize() {
		Platform.runLater(() -> {
			titleLabel.setText(viewText.getString("subjects.title"));
			newButton.setText(viewText.getString("subjects.new"));
			addButton.setText(viewText.getString("subjects.add"));
			saveButton.setText(viewText.getString("subjects.save"));
			deleteButton.setText(viewText.getString("subjects.delete"));

			String[] components = {
					viewText.getString("common.name"),
					viewText.getString("common.code"),
			};
			String[] prompts = {
					viewText.getString("common.promptName"),
					viewText.getString("common.promptCode"),
			};
			String[] ids = {
					"nameTextField",
					"codeTextField",
			};

			int i = 0;
			for (String component : components) {
				Label label = new Label(component);
				label.setFont(new javafx.scene.text.Font("Verdana Bold", 18));
				label.setStyle("-fx-text-fill: #e36486;");


				TextField textField = new TextField();
				textField.setFont(new javafx.scene.text.Font("Verdana", 16));

				textField.setPromptText(prompts[i]);
				textField.setId(ids[i]);

				componentGrid.add(label, 0, i);
				componentGrid.add(textField, 1, i);
				i++;
			}

			loadSubjects();
			nameTextField = (TextField) contentVBox.lookup("#nameTextField");
			codeTextField = (TextField) contentVBox.lookup("#codeTextField");

			changeButtonVisibility(false);
		});
	}

	public void setBaseController(BaseController baseController) {
		this.subjectController = baseController.getSubjectController();
		this.viewText = baseController.getLocaleController().getUIBundle();
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
			alert.setTitle(viewText.getString("confirmation.subject.create"));
			alert.setHeaderText(null);
			alert.setContentText(String.format("%s%n%s",
					viewText.getString("confirmation.subject.createPrompt"),
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
		if (areFieldsEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(viewText.getString(ERROR_TITLE));
			alert.setHeaderText(null);
			alert.setContentText(viewText.getString("error.fillAllFields"));
			alert.showAndWait();
			return;
		}

		String name = nameTextField.getText();
		String code = codeTextField.getText();

		for (SubjectDTO subject : subjects) {
			if (subject.code().equals(code)) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(viewText.getString(ERROR_TITLE));
				alert.setHeaderText(null);
				alert.setContentText(viewText.getString("error.subject.exists"));
				alert.showAndWait();
				return;
			}
		}

		SubjectDTO subject = new SubjectDTO(name, code);
		subjectController.addSubject(subject);
		loadSubjects();
		clearFields();
	}

	@FXML
	private void handleSave() {
		if (areFieldsEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(viewText.getString(ERROR_TITLE));
			alert.setHeaderText(null);
			alert.setContentText(viewText.getString("error.fillAllFields"));
			alert.showAndWait();
			return;
		}

		int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
		if (selectedIndex != -1) {
			String name = nameTextField.getText();
			String code = codeTextField.getText();
			String currentCode = subjects.get(selectedIndex).code();

			for (SubjectDTO subject : subjects) {
				if (subject.code().equals(code) && !code.equals(currentCode)) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle(viewText.getString(ERROR_TITLE));
					alert.setHeaderText(null);
					alert.setContentText(viewText.getString("error.location.exists"));
					alert.showAndWait();
					return;
				}
			}

			SubjectDTO subject = new SubjectDTO(name, code);
			subjectController.updateSubject(subject, currentCode);
			loadSubjects();

			itemView.getSelectionModel().select(selectedIndex);
		}
	}

	@FXML
	private void handleDelete() {
		int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
		if (selectedIndex != -1) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(viewText.getString("confirmation.subject.delete"));
			alert.setHeaderText(null);
			alert.setContentText(viewText.getString("confirmation.subject.deletePrompt"));

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
			alert.setTitle(viewText.getString("confirmation.subject.select"));
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
