package view.components;

import javafx.scene.control.Button;

public class SidebarButton extends Button {
    public SidebarButton(String text) {
        super(text);
        initialise();
    }

    private void initialise() {
        this.getStyleClass().add("sidebar-button");
    }
}
