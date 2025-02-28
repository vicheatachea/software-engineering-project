package view.controllers.components;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class SidebarController {
    private final StringProperty currentView = new SimpleStringProperty();
    @FXML
    private VBox sidebar;

    @FXML
    private void initialize() {
        addButton("\uD83C\uDFE0 Home", "home");
        addButton("\uD83D\uDCC6 Timetable", "timetable");
        addButton("\uD83D\uDC65 Groups", "groups");
        addButton("\uD83D\uDEE0 Settings", "settings");
        addButton("\uD83D\uDEAA Quit", "quit");

        Platform.runLater(() -> currentView.set("home"));
    }

    public StringProperty currentViewProperty() {
        return currentView;
    }

    private void addButton(String name, String view) {
        try {
            Button button = FXMLLoader.load(getClass().getResource("/layouts/components/sidebar/sidebar-button.fxml"));;

            button.setText(name);
            button.setOnAction(event -> currentView.set(view));
            VBox.setMargin(button, new Insets(30, 0 , 0, 0));
            sidebar.getChildren().add(button);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
