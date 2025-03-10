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
import javafx.scene.text.Font;
import view.controllers.ControllerAware;

import java.util.List;
import java.util.Optional;

public class LocationsViewController implements ControllerAware {
	private LocationController locationController;
	private List<LocationDTO> locations;
	private final List<String> components = List.of("Name", "Building", "Campus");
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
		String title = "Location";

		titleLabel.setText(title + "s");
		newButton.setText("New " + title);
		addButton.setText("Add " + title);
		saveButton.setText("Save " + title);
		deleteButton.setText("Delete " + title);

		int i = 0;
		for (String key : components) {
			Label label = new Label(key + ":");
			label.setFont(new javafx.scene.text.Font("Verdana Bold", 18));
			label.setStyle("-fx-text-fill: #e36486;");

			TextField textField = new TextField();
			textField.setFont(new javafx.scene.text.Font("Verdana", 16));

			textField.setPromptText("Enter " + key);
			textField.setId(key.toLowerCase() + "TextField");

			componentGrid.add(label, 0, i);
			componentGrid.add(textField, 1, i);
			i++;
		}

		Platform.runLater(() -> {
			loadLocations();
			nameTextField = (TextField) contentVBox.lookup("#nameTextField");
			buildingTextField = (TextField) contentVBox.lookup("#buildingTextField");
			campusTextField = (TextField) contentVBox.lookup("#campusTextField");

			changeButtonVisibility(false);
		});
	}

	public void setBaseController(BaseController baseController) {
		this.locationController = baseController.getLocationController();
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
			alert.setTitle("Create Location");
			alert.setHeaderText(null);
			alert.setContentText(
					"Are you sure you want to create a new location?\n" + "Any unsaved changes will be lost.");

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
		String building = buildingTextField.getText();
		String campus = campusTextField.getText();

		for (LocationDTO location : locations) {
			if (location.name().equals(name)) {
				Alert alert = new Alert(Alert.AlertType.ERROR);
				alert.setTitle("Error");
				alert.setHeaderText(null);
				alert.setContentText("Location with this name already exists.");
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
			alert.setTitle("Error");
			alert.setHeaderText(null);
			alert.setContentText("All fields are required.");
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
					alert.setTitle("Error");
					alert.setHeaderText(null);
					alert.setContentText("Location with this name already exists.");
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
			alert.setTitle("Delete Location");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to delete this location?");

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
			alert.setTitle("Select Location");
			alert.setHeaderText(null);
			alert.setContentText("Are you sure you want to change items?\n" + "Any unsaved changes will be lost.");

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
