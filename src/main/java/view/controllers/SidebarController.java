package view.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import view.components.SidebarButton;

public class SidebarController {
    private final StringProperty currentView = new SimpleStringProperty();
    @FXML
    private VBox sidebar;

    @FXML
    private void initialize() {
//        addButton("Really long text because I need to see if this thing is resizing properly", "stuff");
        addButton("Dashboard", "dashboard");
        addButton("Timetable", "timetable");
        addButton("Settings", "settings");
    }

    public StringProperty currentViewProperty() {
        return currentView;
    }

    private void addButton(String name, String view) {
        SidebarButton button = new SidebarButton(name);
        button.setOnAction(event -> currentView.set(view));
        sidebar.getChildren().add(button);
    }
}
