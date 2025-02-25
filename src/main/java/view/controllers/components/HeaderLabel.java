package view.controllers.components;

import javafx.scene.control.Label;

public class HeaderLabel extends Label {
    public HeaderLabel(String weekDay, String monthDay, String month) {
        this.setText(weekDay + ". " + monthDay + "/" + month);
        this.getStyleClass().add("header-label");
    }
}
