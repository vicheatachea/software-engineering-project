package view.controllers.components;

import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import util.TimeFormatterUtil;

public class EventLabel extends Label {
    private Event event;

    // All the following values represent percentages of the cell size
    private final double height;
    private final double top_offset;
    private int labelPosition = 1;
    private int numberOfLabels = 1;

    public EventLabel(Event event, double height, double top_offset) {
        super();
        this.event = event;
        this.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        this.setPadding(new Insets(5, 5, 5, 5));

        this.height = height;
        this.top_offset = top_offset;

        if (event instanceof TeachingSessionDTO teachingSession) {
            String startTime = TimeFormatterUtil.getTimeFromDateTime(teachingSession.startDate());
            String endTime = TimeFormatterUtil.getTimeFromDateTime(teachingSession.endDate());

            this.setText(teachingSession.subjectCode() + "\n" + startTime + " – " + endTime + "\n" + teachingSession.locationName());
        } else if (event instanceof AssignmentDTO assignment) {
            String dueTime = TimeFormatterUtil.getTimeFromDateTime(assignment.deadline());

            this.setText(assignment.assignmentName() + "\n" + assignment.subjectCode() + "\n" + dueTime);
        } else {
            this.setText("Unknown Event");
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
        this.setTranslateY(cellHeight * top_offset);
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
