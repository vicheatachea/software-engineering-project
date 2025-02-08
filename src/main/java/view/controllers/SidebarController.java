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

    public StringProperty currentViewProperty() {
        return currentView;
    }

    private void addButton(String text) {
        SidebarButton button = new SidebarButton(text);

        sidebar.getChildren().add(button);
    }
}
