package view.controllers.pages.main;

import controller.BaseController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import view.controllers.ControllerAware;
import view.controllers.components.SidebarController;
import view.controllers.pages.user.LoginController;
import view.controllers.pages.user.UserProfileController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainPageController implements Initializable {
	Stage stage;
	BaseController baseController;

	@FXML
	private SidebarController sidebarController;
	@FXML
	private VBox sidebar;
	@FXML
	private StackPane mainContent;

	public MainPageController() {
		this.baseController = new BaseController();
	}

	public void setStage(Stage stage) {
		this.stage = stage;
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
                case "quit":
                    stage.close();
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
                        fxmlLoader.setController(new GroupsController());
                        break;
                    case "subjects":
                        fxmlLoader.setController(new SubjectsController());
                        break;
                    case "locations":
                        fxmlLoader.setController(new LocationsController());
                        break;
                }
            }

            Node content = fxmlLoader.load();
            Object subController = fxmlLoader.getController();

			if (subController instanceof ControllerAware) {
				((ControllerAware) subController).setBaseController(baseController);
			}

			mainContent.getChildren().setAll(content);
		} catch (NullPointerException e) {
			System.out.println("MainPageController loadContent() could not load " + fxmlFilePath);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}