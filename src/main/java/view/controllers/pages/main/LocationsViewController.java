package view.controllers.pages.main;

import controller.BaseController;
import controller.LocationController;
import dto.LocationDTO;
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

public class LocationsViewController implements ControllerAware {
	private ResourceBundle viewText;
	private LocationController locationController;
	private List<LocationDTO> locations;
	private boolean isEditingMode;
	private int currentIndex;
	private TextField nameTextField;
	private TextField buildingTextField;
	private TextField campusTextField;

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
			titleLabel.setText(viewText.getString("locations.title"));
			newButton.setText(viewText.getString("locations.new"));
			addButton.setText(viewText.getString("locations.add"));
			saveButton.setText(viewText.getString("locations.save"));
			deleteButton.setText(viewText.getString("locations.delete"));

			String[] components = {
					viewText.getString("common.name"),
					viewText.getString("locations.building"),
					viewText.getString("locations.campus")
			};
			String[] prompts = {
					viewText.getString("common.promptName"),
					viewText.getString("locations.promptBuilding"),
					viewText.getString("locations.promptCampus")
			};
			String[] ids = {
					"nameTextField",
					"buildingTextField",
					"campusTextField"
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

			loadLocations();
			nameTextField = (TextField) contentVBox.lookup("#nameTextField");
			buildingTextField = (TextField) contentVBox.lookup("#buildingTextField");
			campusTextField = (TextField) contentVBox.lookup("#campusTextField");

			changeButtonVisibility(false);
		});
	}

	public void setBaseController(BaseController baseController) {
		this.locationController = baseController.getLocationController();
		this.viewText = baseController.getLocaleController().getUIBundle();
	}

	private void loadLocations() {
		locations = locationController.fetchAllLocations();
		itemView.getItems().clear();
		locations.forEach(location -> itemView.getItems().add(location.name()));
	}

	@FXML
	private void handleNew() {
		if (resolveIsNotSaved()) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(viewText.getString("confirmation.location.create"));
			alert.setHeaderText(null);
			alert.setContentText(String.format("%s\n%s",
					viewText.getString("confirmation.location.createPrompt"),
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
			alert.setTitle(viewText.getString("error.title"));
			alert.setHeaderText(null);
			alert.setContentText(viewText.getString("error.fillAllFields"));
			alert.showAndWait();
			return;
		}

		String name = nameTextField.getText();
		String building = buildingTextField.getText();
		String campus = campusTextField.getText();

		for (LocationDTO location : locations) {
			if (location.name().equals(name)) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle(viewText.getString("error.title"));
				alert.setHeaderText(null);
				alert.setContentText(viewText.getString("error.location.exists"));
				alert.showAndWait();
				return;
			}
		}

		LocationDTO locationDTO = new LocationDTO(name, campus, building);
		locationController.addLocation(locationDTO);
		loadLocations();
		clearFields();
	}

	@FXML
	private void handleSave() {
		if (areFieldsEmpty()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(viewText.getString("error.title"));
			alert.setHeaderText(null);
			alert.setContentText(viewText.getString("error.fillAllFields"));
			alert.showAndWait();
			return;
		}

		int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
		if (selectedIndex != -1) {
			String name = nameTextField.getText();
			String building = buildingTextField.getText();
			String campus = campusTextField.getText();
			String currentName = locations.get(selectedIndex).name();

			for (LocationDTO location : locations) {
				if (location.name().equals(name) && !name.equals(currentName)) {
					Alert alert = new Alert(Alert.AlertType.ERROR);
					alert.setTitle(viewText.getString("error.title"));
					alert.setHeaderText(null);
					alert.setContentText(viewText.getString("error.location.exists"));
					alert.showAndWait();
					return;
				}
			}

			LocationDTO locationDTO = new LocationDTO(name, campus, building);
			locationController.updateLocation(locationDTO, currentName);
			loadLocations();

			itemView.getSelectionModel().select(selectedIndex);
		}
	}

	@FXML
	private void handleDelete() {
		int selectedIndex = itemView.getSelectionModel().getSelectedIndex();
		if (selectedIndex != -1) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(viewText.getString("confirmation.location.delete"));
			alert.setHeaderText(null);
			alert.setContentText(viewText.getString("confirmation.location.deletePrompt"));

			Optional<ButtonType> result = alert.showAndWait();
			if (result.isPresent() && result.get() == ButtonType.OK) {
				locationController.deleteLocation(locations.get(selectedIndex));
				loadLocations();
				clearFields();
				changeButtonVisibility(false);
			}
		}
	}

	@FXML
	private void handleItemSelection() {
		if (resolveIsNotSaved()) {
			Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
			alert.setTitle(viewText.getString("confirmation.location.select"));
			alert.setHeaderText(null);
			alert.setContentText(String.format("%s\n%s",
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
			nameTextField.setText(locations.get(selectedIndex).name());
			buildingTextField.setText(locations.get(selectedIndex).building());
			campusTextField.setText(locations.get(selectedIndex).campus());

			changeButtonVisibility(true);
			currentIndex = selectedIndex;
		}
	}

	private void clearFields() {
		nameTextField.clear();
		buildingTextField.clear();
		campusTextField.clear();
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
			String savedName = locations.get(currentIndex).name();
			String savedBuilding = locations.get(currentIndex).building();
			String savedCampus = locations.get(currentIndex).campus();

			return !nameTextField.getText().equals(savedName) || !buildingTextField.getText().equals(savedBuilding) ||
			       !campusTextField.getText().equals(savedCampus);
		} else {
			return !nameTextField.getText().isEmpty() || !buildingTextField.getText().isEmpty() ||
			       !campusTextField.getText().isEmpty();
		}
	}

	private boolean areFieldsEmpty() {
		return nameTextField.getText().isEmpty() || buildingTextField.getText().isEmpty() ||
		       campusTextField.getText().isEmpty();
	}
}
