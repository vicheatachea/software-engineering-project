package view.components;

import javafx.scene.control.Button;

public class SidebarButton extends Button {
    public SidebarButton(String text) {
        super(text);
        configure();
    }

    private void configure() {
        this.getStyleClass().add("sidebar-button");
        this.setMaxWidth(Double.MAX_VALUE);
    }
}
