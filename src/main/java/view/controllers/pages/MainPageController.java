package view.controllers.pages;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import view.controllers.components.SidebarController;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/*
* The controller of the application is initialized here
* The controller should be passed to other view controllers as needed
*/
public class MainPageController implements Initializable {
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

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        SplitPane.setResizableWithParent(sidebar, false);
        SplitPane.setResizableWithParent(mainContent, false);

        sidebarController.currentViewProperty().addListener((observableValue, oldValue, newValue) -> {
            switch (newValue) {
                case "home":
                    loadContent("/layouts/pages/home.fxml");
                    break;
                case "timetable":
                    loadContent("/layouts/pages/timetable.fxml");
                    break;
                case "settings":
                    loadContent("/layouts/pages/settings.fxml");
                    break;
            }
        });
    }

    // This function is not handling errors properly now
    private void loadContent(String fxmlFilePath) {
        try {
            Node content = FXMLLoader.load(getClass().getResource(fxmlFilePath));
            if (content == null) {
                System.out.println("MainPageController loadContent() could not load " + fxmlFilePath);
                return;
            }
            mainContent.getChildren().setAll(content);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
