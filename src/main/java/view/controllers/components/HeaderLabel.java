package view.controllers.components;

import javafx.scene.control.Label;

public class HeaderLabel extends Label {
    String formatPattern = "%02d";

    public HeaderLabel(String weekDay, int monthDay, int month) {
        this.setText(weekDay + ". " + formatNumber(monthDay) + "/" + formatNumber(month));
        this.getStyleClass().add("header-label");
    }

    private String formatNumber(int number) {
        return String.format(formatPattern, number);
    }
}
