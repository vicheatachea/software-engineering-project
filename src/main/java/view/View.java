package view;

import controller.Controller;
import javafx.application.Application;
import javafx.stage.Stage;

public class View extends Application {
    Controller controller;

    @Override
    public void start(Stage stage) {
        // View implementation through FXML
    }

    @Override
    public void init() {
        controller = new Controller();
    }
}
