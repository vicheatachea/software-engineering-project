package view.controllers.pages.main;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
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
    Controller controller;
    @FXML
    private SidebarController sidebarController;
    @FXML
    private VBox sidebar;
    @FXML
    private StackPane mainContent;

    public MainPageController() {
        this.controller = new Controller();
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
            Node content = FXMLLoader.load(getClass().getResource(fxmlFilePath));
            mainContent.getChildren().setAll(content);
        } catch (NullPointerException e) {
            System.out.println("MainPageController loadContent() could not load " + fxmlFilePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
