package view.controllers.pages.main;

import controller.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import view.controllers.ControllerAware;
import view.controllers.components.SidebarViewController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageViewController implements Initializable {
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
                case "home":
                    loadContent("/layouts/pages/main/home.fxml", null);
                    break;
                case "timetable":
                    loadContent("/layouts/pages/main/timetable.fxml", null);
                    break;
                case "groups":
                    loadContent("/layouts/pages/main/general-page.fxml", "groups");
                    break;
                case "subjects":
                    loadContent("/layouts/pages/main/general-page.fxml", "subjects");
                    break;
                case "locations":
                    loadContent("/layouts/pages/main/general-page.fxml", "locations");
                    break;
                case "settings":
                    loadContent("/layouts/pages/main/settings.fxml", null);
                    break;
                case "clear":
                    break;
                case "quit":
                    sidebarController.shutdownNotifications();
                    stage.close();
                    break;
                default:
                    System.out.println("MainPageController initialize() could not load " + newValue);
                    break;
            }
        });
    }

    private void loadContent(String fxmlFilePath, String name) {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));

            if (name != null) {
                switch (name) {
                    case "groups":
                        fxmlLoader.setController(new GroupsViewController());
                        break;
                    case "subjects":
                        fxmlLoader.setController(new SubjectsViewController());
                        break;
                    case "locations":
                        fxmlLoader.setController(new LocationsViewController());
                        break;
                    default:
                        break;
                }
            }

            Node content = fxmlLoader.load();
            if (content == null) {
                System.out.println("MainPageController loadContent() could not load " + fxmlFilePath);
                return;
            }

            Object subController = fxmlLoader.getController();

			if (subController instanceof ControllerAware) {
				((ControllerAware) subController).setBaseController(baseController);
			}
            if (subController instanceof SettingsViewController) {
                ((SettingsViewController) subController).setSidebarViewController(sidebarController);
            }

			mainContent.getChildren().setAll(content);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}