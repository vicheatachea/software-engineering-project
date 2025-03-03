package view.controllers.components;

import dto.Event;
import javafx.scene.control.Label;

public class EventLabel extends Label {
    private Event event;

    // All the following values represent percentages of the cell size
    private final double height;
    private final double top_offset;
    private int labelPosition = 1;
    private int numberOfLabels = 1;

    public EventLabel(Event event, double height, double top_offset) {
        // Placeholder
        super("Event");
        this.event = event;
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

        this.height = height;
        this.top_offset = top_offset;
    }

    public Event getEvent() {
        return event;
    }

    public void updateLabelHeight(double cellHeight) {
        this.setMaxHeight(cellHeight * height);
        this.setTranslateY(cellHeight * top_offset);
    }

    public void updateLabelWidth(double cellWidth) {
        this.setMaxWidth((double) labelPosition / numberOfLabels * cellWidth);
        this.setTranslateX((double) (labelPosition - 1) / numberOfLabels * cellWidth);
    }

    public void updateLabelPosition(int labelPosition, int numberOfLabels) {
        this.labelPosition = labelPosition;
        this.numberOfLabels = numberOfLabels;
    }
}
