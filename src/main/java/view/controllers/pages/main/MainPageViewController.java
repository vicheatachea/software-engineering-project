package view.controllers.pages.main;

import controller.BaseController;
import datasource.MariaDBConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.controllers.ControllerAware;
import view.controllers.components.SidebarViewController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageViewController implements Initializable {
	private static final Logger logger = LoggerFactory.getLogger(MainPageViewController.class);
	private static final String GENERAL_PAGE_PATH = "/layouts/pages/main/general-page.fxml";
	private static final String GROUPS = "groups";
	private static final String SUBJECTS = "subjects";
	private static final String LOCATIONS = "locations";

	Stage stage;
	BaseController baseController;
	@FXML
	private SidebarViewController sidebarController;
	@FXML
	private VBox sidebar;
	@FXML
	private StackPane mainContent;

	public MainPageViewController() {
		this.baseController = new BaseController();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
		stage.setOnCloseRequest(event -> sidebarController.shutdownNotifications());
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		SplitPane.setResizableWithParent(sidebar, false);
		SplitPane.setResizableWithParent(mainContent, false);

		sidebarController.setBaseController(baseController);

		sidebarController.currentViewProperty().addListener((observableValue, oldValue, newValue) -> {
			switch (newValue) {
				case "timetable":
					loadContent("/layouts/pages/main/timetable.fxml", null);
					break;
				case GROUPS:
					loadContent(GENERAL_PAGE_PATH, GROUPS);
					break;
				case SUBJECTS:
					loadContent(GENERAL_PAGE_PATH, SUBJECTS);
					break;
				case LOCATIONS:
					loadContent(GENERAL_PAGE_PATH, LOCATIONS);
					break;
				case "settings":
					loadContent("/layouts/pages/main/settings.fxml", null);
					break;
				case "clear":
					break;
				case "quit":
					sidebarController.shutdownNotifications();
					new MariaDBConnection().terminate();
					stage.close();
					break;
				default:
					logger.error("MainPageController initialize() could not load {} ", newValue);
					break;
			}
		});
	}

	private void loadContent(String fxmlFilePath, String name) {
		try {
			FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));

			if (name != null) {
				switch (name) {
					case GROUPS:
						fxmlLoader.setController(new GroupsViewController());
						break;
					case SUBJECTS:
						fxmlLoader.setController(new SubjectsViewController());
						break;
					case LOCATIONS:
						fxmlLoader.setController(new LocationsViewController());
						break;
					default:
						break;
				}
			}

			Node content = fxmlLoader.load();
			if (content == null) {
				logger.error("MainPageController loadContent() could not load {}", fxmlFilePath);
				return;
			}

			Object subController = fxmlLoader.getController();

			if (subController instanceof ControllerAware controllerAware) {
				controllerAware.setBaseController(baseController);
			}
			if (subController instanceof SettingsViewController settingsViewController) {
				settingsViewController.setSidebarViewController(sidebarController);
			}

			mainContent.getChildren().setAll(content);
		} catch (IOException e) {
			logger.error("Error loading FXML file: {}", e.getMessage());
		}
	}
}