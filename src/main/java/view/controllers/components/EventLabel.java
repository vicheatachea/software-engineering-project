package view.controllers.components;

import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import util.TimeFormatterUtil;

/**
 * EventLabel is a custom label class that represents an event in the calendar.
 * It extends the Label class and provides additional functionality for displaying event details.
 */
public class EventLabel extends Label {
    private final Event event;

    // All the following values represent percentages of the cell size
    private final double height;
    private final double topOffset;
    private int labelPosition = 1;
    private int numberOfLabels = 1;

    public EventLabel(Event event, double height, double topOffset) {
        super();
        this.event = event;
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setPadding(new Insets(5, 5, 5, 5));

        this.height = height;
        this.topOffset = topOffset;

        switch (event) {
            case TeachingSessionDTO teachingSession -> {
                String startTime = TimeFormatterUtil.getTimeFromDateTime(teachingSession.startDate());
                String endTime = TimeFormatterUtil.getTimeFromDateTime(teachingSession.endDate());

                this.setText(teachingSession.subjectCode() + "\n" + startTime + " – " + endTime + "\n" + teachingSession.locationName());
            }
            case AssignmentDTO assignment -> {
                String dueTime = TimeFormatterUtil.getTimeFromDateTime(assignment.deadline());

                this.setText(assignment.assignmentName() + "\n" + assignment.subjectCode() + "\n" + dueTime);
            }
            default -> this.setText("Unknown Event");
        }
    }

    public Event getEvent() {
        return event;
    }

    public int getLabelPosition() {
        return labelPosition;
    }

    public int getNumberOfLabels() {
        return numberOfLabels;
    }

    public void updateLabelHeight(double cellHeight) {
        this.setMaxHeight(cellHeight * height);
        this.setTranslateY(cellHeight * topOffset);
    }

    public void updateLabelWidth(double cellWidth) {
        this.setMaxWidth((double) 1 / numberOfLabels * cellWidth);
        this.setTranslateX((double) (labelPosition - 1) / numberOfLabels * cellWidth);
    }

    public void updateLabelPosition(int labelPosition, int numberOfLabels) {
        this.labelPosition = labelPosition;
        this.numberOfLabels = numberOfLabels;
    }
}
