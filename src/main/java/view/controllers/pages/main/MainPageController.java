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
import view.controllers.components.SidebarController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/*
* The controller of the application is initialized here
* The controller should be passed to other view controllers as needed
*/
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

        sidebarController.currentViewProperty().addListener((observableValue, oldValue, newValue) -> {
            switch (newValue) {
                case "home":
                    loadContent("/layouts/pages/main/home.fxml");
                    break;
                case "timetable":
                    loadContent("/layouts/pages/main/timetable.fxml");
                    break;
                case "groups":
                    loadContent("/layouts/pages/main/groups.fxml");
                    break;
                case "settings":
                    loadContent("/layouts/pages/main/settings.fxml");
                    break;
                case "quit":
                    stage.close();
                    break;
            }
        });
    }

    private void loadContent(String fxmlFilePath) {
        try {
            FXMLLoader FXMLLoader = new FXMLLoader(getClass().getResource(fxmlFilePath));
            Node content = FXMLLoader.load();
            Object subController = FXMLLoader.getController();

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
