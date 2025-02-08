package view.controllers;

import controller.Controller;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.StackPane;

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
    private StackPane mainContent;

    public MainPageController() {
        this.controller = new Controller();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        sidebarController.currentViewProperty().addListener((observableValue, oldValue, newValue) -> {
            switch (newValue) {
                // Implement the logic for switching views here
            }
        });
    }
}
