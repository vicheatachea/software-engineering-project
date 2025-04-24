package view.controllers.pages.main;

import controller.BaseController;
import controller.EventController;
import controller.LocaleController;
import controller.UserController;
import dto.AssignmentDTO;
import dto.Event;
import dto.TeachingSessionDTO;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import view.controllers.ControllerAware;
import view.controllers.components.EventLabel;
import view.controllers.components.EventPopupViewController;

import java.io.IOException;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.WeekFields;
import java.util.*;

public class TimetableViewController implements ControllerAware {
	private static final String TIMETABLE_ALL_LANGUAGES = "timetable.allLanguages";
	private static final Logger logger = LoggerFactory.getLogger(TimetableViewController.class);
	private static final int NUMBER_OF_BUTTONS = 2;
	int currentWeek;
	private ResourceBundle viewText;
	private BaseController baseController;
	private EventController eventController;
	private LocaleController localeController;
	private UserController userController;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	private double cellWidth;

	@FXML
	private HBox topbar;
	@FXML
	private GridPane timetableGrid;
	@FXML
	private ComboBox<String> languageComboBox;
	@FXML
	private Label dateLabel;
	@FXML
	private DatePicker datePicker;
	@FXML
	private Label weekLabel;

	@FXML
	private void initialize() {
		Platform.runLater(() -> {
			addButtons();

			languageComboBox.getItems().add(viewText.getString(TIMETABLE_ALL_LANGUAGES));
			Locale currentLocale = baseController.getLocaleController().getUserLocale();

			List<Locale> locales = localeController.getAvailableLocales();
			locales.forEach(locale ->
					                languageComboBox.getItems().add(locale.getDisplayLanguage(currentLocale))
			);
			languageComboBox.setValue(viewText.getString(TIMETABLE_ALL_LANGUAGES));
			languageComboBox.addEventHandler(ActionEvent.ACTION, event -> loadTimetable());

			datePicker.setValue(LocalDate.now());
			Locale.setDefault(baseController.getLocaleController().getUserLocale());
			handleDatePick();

			updateTimetableHeaders();
			updateTimetableHours();
			loadTimetable();

			timetableGrid.heightProperty().addListener((observable, oldValue, newValue) -> updateEventHeight());
			timetableGrid.widthProperty().addListener((observable, oldValue, newValue) -> updateEventWidth());
		});
	}

	@Override
	public void setBaseController(BaseController baseController) {
		this.baseController = baseController;
		this.eventController = baseController.getEventController();
		this.localeController = baseController.getLocaleController();
		this.userController = baseController.getUserController();
		this.viewText = baseController.getLocaleController().getUIBundle();
	}

	private void addButtons() {
		try {
			Button[] buttons = new Button[NUMBER_OF_BUTTONS];
			for (int i = 0; i < NUMBER_OF_BUTTONS; i++) {
				buttons[i] =
						FXMLLoader.load(getClass().getResource("/layouts/components/timetable/timetable-button.fxml"));
			}

			buttons[0].setText(viewText.getString("timetable.newEvent"));
			buttons[1].setText("\uD83D\uDD27");

			buttons[0].setOnAction(event -> handleNewEvent());
			// Settings buttons are not yet implemented
			buttons[1].setDisable(true);

			topbar.getChildren().addFirst(buttons[0]);
			topbar.getChildren().add(buttons[1]);
		} catch (IOException e) {
			logger.error("Error adding buttons: {}", e.getMessage());
		}
	}

	@FXML
	private void handleDatePick() {
		LocalDate newDate = datePicker.getValue();

		if (newDate == null) {
			return;
		}

		LocalDateTime previousStartDate = startDate;
		startDate = newDate.with(DayOfWeek.MONDAY).atStartOfDay();
		endDate = newDate.with(DayOfWeek.SUNDAY).atTime(23, 59, 59);
		currentWeek = newDate.get(WeekFields.ISO.weekOfWeekBasedYear());

		String startDay = formatNumber(startDate.getDayOfMonth());
		String endDay = formatNumber(endDate.getDayOfMonth());
		String startMonth = startDate.getMonth().toString().toLowerCase();
		String endMonth = endDate.getMonth().toString().toLowerCase();
		int startYear = startDate.getYear();
		int endYear = endDate.getYear();

		Map<String, Object> values = new HashMap<>();
		values.put("startDay", startDay);
		values.put("endDay", endDay);
		values.put("startMonth", viewText.getString("timetable.shortMonth." + startMonth));
		values.put("endMonth", viewText.getString("timetable.shortMonth." + endMonth));
		values.put("startYear", startYear);
		values.put("endYear", endYear);

		if (startYear != endYear) {
			dateLabel.setText(implementPattern(viewText.getString("timetable.longDate"), values));
		} else if (!Objects.equals(startMonth, endMonth)) {
			dateLabel.setText(implementPattern(viewText.getString("timetable.mediumDate"), values));
		} else {
			dateLabel.setText(implementPattern(viewText.getString("timetable.shortDate"), values));
		}

		weekLabel.setText(MessageFormat.format(viewText.getString("timetable.week"), currentWeek));

		if (!Objects.equals(previousStartDate, startDate)) {
			updateTimetableHeaders();
			loadTimetable();
		}
	}


	private String implementPattern(String pattern, Map<String, Object> values) {
		for (Map.Entry<String, Object> entry : values.entrySet()) {
			pattern = pattern.replace("{" + entry.getKey() + "}", entry.getValue().toString());
		}
		return pattern;
	}

	private void handleNewEvent() {
		if (!userController.isUserLoggedIn()) {
			Alert alert = new Alert(Alert.AlertType.ERROR);
			alert.setTitle(viewText.getString("error.title"));
			alert.setHeaderText(null);
			alert.setContentText(viewText.getString("error.event.loggedIn"));
			alert.showAndWait();
			return;
		}
		openEventPopup(null);
	}

	private void handleEditEvent(Event event) {
		openEventPopup(event);
	}

	private void openEventPopup(Event event) {
		try {
			FXMLLoader loader =
					new FXMLLoader(getClass().getResource("/layouts/components/timetable/event-popup.fxml"));
			Parent content = loader.load();

			EventPopupViewController popupViewController = loader.getController();
			popupViewController.setUp(event, baseController, this);

			Stage popupStage = new Stage();
			popupStage.setScene(new Scene(content));

			popupStage.setTitle(event == null ? viewText.getString("timetable.newEvent") :
			                    viewText.getString("timetable.editEvent"));
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.initOwner(topbar.getScene().getWindow());
			popupStage.showAndWait();
		} catch (IOException e) {
			logger.error("Error opening event popup: {}", e.getMessage());
		}
	}

	private void updateTimetableHeaders() {
		timetableGrid.getChildren().removeIf(node -> GridPane.getColumnIndex(node) != null &&
		                                             GridPane.getRowIndex(node) == 0 &&
		                                             GridPane.getColumnIndex(node) > 0);
		int daysBetween = (int) ChronoUnit.DAYS.between(startDate, endDate) + 1;

		for (int i = 0; i < daysBetween; i++) {

			String weekDay = startDate.plusDays(i).getDayOfWeek().toString().toLowerCase();
			String monthDay = formatNumber(startDate.plusDays(i).getDayOfMonth());
			String month = formatNumber(startDate.plusDays(i).getMonthValue());

			Map<String, Object> values = new HashMap<>();
			values.put("weekDay", viewText.getString("timetable.shortDay." + weekDay));
			values.put("monthDay", monthDay);
			values.put("month", month);

			Label header = new Label(implementPattern(viewText.getString("timetable.simpleDate"), values));
			timetableGrid.add(header, i + 1, 0);

			GridPane.setHalignment(header, javafx.geometry.HPos.CENTER);
			GridPane.setValignment(header, javafx.geometry.VPos.CENTER);
		}
	}

	private void updateTimetableHours() {
		int rowCount = timetableGrid.getRowCount();
		double timeStep = 24.0 / (rowCount - 1);

		for (int i = 0; i < rowCount - 1; i++) {
			int hours = (int) (i * timeStep) % 24;
			int minutes = (int) ((i * timeStep % 24 - hours) * 60);

			Label timeLabel = new Label(formatNumber(hours) + ":" + formatNumber(minutes));
			timeLabel.setTranslateY(8);
			timetableGrid.add(timeLabel, 0, i);

			timeLabel.getStyleClass().add("time-label");

			GridPane.setHalignment(timeLabel, javafx.geometry.HPos.CENTER);
			GridPane.setValignment(timeLabel, javafx.geometry.VPos.BOTTOM);
		}
	}

	private String formatNumber(int number) {
		return String.format("%02d", number);
	}

	public void loadTimetable() {
		if (!userController.isUserLoggedIn()) {
			return;
		}

		clearTimetable();
		List<Event> events = fetchEvents();

		for (Event event : events) {
			processEvent(event);
		}

		updateEventHeight();
		updateEventWidth();
	}

	private List<Event> fetchEvents() {
		String language = languageComboBox.getValue();
		Locale currentLocale = baseController.getLocaleController().getUserLocale();

		if (language.equals(viewText.getString(TIMETABLE_ALL_LANGUAGES))) {
			return eventController.fetchEventsByUser(startDate, endDate);
		}

		Locale selectedLocale = localeController.getAvailableLocales().stream()
		                                        .filter(locale -> locale.getDisplayLanguage(currentLocale)
		                                                                .equals(language))
		                                        .findFirst()
		                                        .orElse(null);

		return selectedLocale != null
		       ? eventController.fetchEventsByLocale(startDate, endDate, selectedLocale.toLanguageTag())
		       : eventController.fetchEventsByUser(startDate, endDate);
	}

	private void processEvent(Event event) {
		EventLabel eventLabel = createEventLabel(event);
		if (eventLabel == null) {
			return;
		}

		int column = calculateEventColumn(event);
		if (!isColumnValid(column)) {
			logger.info("An event with a date outside the timetable range was found");
			return;
		}

		int[] rows = calculateEventRows(event);
		int startRow = rows[0];
		int endRow = rows[1];

		if (startRow == -1 || endRow == -1) {
			logger.error("An invalid event was found");
			return;
		}

		handleEventOverlap(eventLabel, column, startRow, endRow);
		addEventToGrid(eventLabel, column, startRow, endRow);
	}

	private EventLabel createEventLabel(Event event) {
		return switch (event) {
			case TeachingSessionDTO teachingSession -> createTeachingSessionLabel(teachingSession);
			case AssignmentDTO assignment -> createAssignmentLabel(assignment);
			default -> {
				logger.info("An event with an unknown type was found");
				yield null;
			}
		};
	}

	private EventLabel createTeachingSessionLabel(TeachingSessionDTO teachingSession) {
		LocalDateTime start = teachingSession.startDate();
		LocalDateTime end = teachingSession.endDate();
		double[] sizeAndOffset = getEventHeightAndOffset(start, end);

		EventLabel label = new EventLabel(teachingSession, sizeAndOffset[0], sizeAndOffset[1]);
		label.getStyleClass().add("teaching-session-label");
		return label;
	}

	private EventLabel createAssignmentLabel(AssignmentDTO assignment) {
		LocalDateTime deadline = assignment.deadline();
		TimeRange timeRange = calculateAssignmentTimeRange(deadline);
		double[] sizeAndOffset = getEventHeightAndOffset(timeRange.start(), timeRange.end());

		EventLabel label = new EventLabel(assignment, sizeAndOffset[0], sizeAndOffset[1]);
		label.getStyleClass().add("assignment-label");
		return label;
	}

	private TimeRange calculateAssignmentTimeRange(LocalDateTime deadline) {
		LocalDateTime deadlineBuffer = deadline.minusHours(1);

		if (deadline.getDayOfMonth() == deadlineBuffer.getDayOfMonth()) {
			return new TimeRange(deadlineBuffer, deadline);
		}

		if (deadline.getHour() == 0 && deadline.getMinute() == 0) {
			return new TimeRange(
					deadlineBuffer,
					LocalDateTime.of(deadlineBuffer.getYear(), deadlineBuffer.getMonth(),
					                 deadlineBuffer.getDayOfMonth(), 23, 59)
			);
		}

		return new TimeRange(
				LocalDateTime.of(deadline.getYear(), deadline.getMonth(), deadline.getDayOfMonth(), 0, 0),
				deadline
		);
	}

	private int calculateEventColumn(Event event) {
		LocalDateTime eventStart = switch (event) {
			case TeachingSessionDTO teachingSession -> teachingSession.startDate();
			case AssignmentDTO assignment -> assignment.deadline();
			default -> null;
		};
		if (eventStart == null) {
			return -1;
		}
		return (int) ChronoUnit.DAYS.between(startDate, eventStart) + 1;
	}

	private boolean isColumnValid(int column) {
		return column >= 1 && column <= (timetableGrid.getColumnCount() - 1);
	}

	private int[] calculateEventRows(Event event) {
		LocalDateTime start;
		LocalDateTime end;

		switch (event) {
			case TeachingSessionDTO teachingSession -> {
				start = teachingSession.startDate();
				end = teachingSession.endDate();
			}
			case AssignmentDTO assignment -> {
				TimeRange timeRange = calculateAssignmentTimeRange(assignment.deadline());
				start = timeRange.start();
				end = timeRange.end();
			}
			default -> {
				return new int[]{-1, -1};
			}
		}

		return new int[]{
				getRowIndex(start, true),
				getRowIndex(end, false)
		};
	}

	private void handleEventOverlap(EventLabel eventLabel, int column, int startRow, int endRow) {
		Set<EventLabel> overlappingEvents = getEventLabelsInRange(column, startRow, endRow);
		if (overlappingEvents.isEmpty()) {
			return;
		}

		int maxLabelPosition = overlappingEvents.stream()
		                                        .mapToInt(EventLabel::getNumberOfLabels)
		                                        .max()
		                                        .orElse(1);

		boolean positioned = false;
		for (int i = 1; i <= maxLabelPosition; i++) {
			int finalI = i;
			if (overlappingEvents.stream().noneMatch(existing -> existing.getLabelPosition() == finalI)) {
				eventLabel.updateLabelPosition(i, maxLabelPosition);
				positioned = true;
				break;
			}
		}

		if (!positioned) {
			int newMaxPosition = maxLabelPosition + 1;
			eventLabel.updateLabelPosition(newMaxPosition, newMaxPosition);
			overlappingEvents.forEach(existing ->
					                          existing.updateLabelPosition(existing.getLabelPosition(),
					                                                       newMaxPosition));
		}
	}

	private void addEventToGrid(EventLabel eventLabel, int column, int startRow, int endRow) {
		GridPane.setHalignment(eventLabel, javafx.geometry.HPos.LEFT);
		GridPane.setValignment(eventLabel, javafx.geometry.VPos.TOP);

		eventLabel.setOnMouseClicked(mouseEvent -> handleEditEvent(eventLabel.getEvent()));
		timetableGrid.add(eventLabel, column, startRow, 1, endRow - startRow + 1);
	}

	private void clearTimetable() {
		timetableGrid.getChildren().removeIf(node -> {
			Integer column = GridPane.getColumnIndex(node);
			Integer row = GridPane.getRowIndex(node);
			return node instanceof Label && (column != null && column > 0) && (row != null && row > 0);
		});
	}

	private int getRowIndex(LocalDateTime dateTime, boolean isStart) {
		double timeStep = 24.0 / (timetableGrid.getRowCount() - 1);
		int hours = dateTime.getHour();
		int minutes = dateTime.getMinute();
		double timeInHours = hours + minutes / 60.0;
		return isStart ? (int) (timeInHours / timeStep + 1) : (int) Math.ceil(timeInHours / timeStep);
	}

	// The first value is height, the second is offset
	private double[] getEventHeightAndOffset(LocalDateTime start, LocalDateTime end) {
		double timeStep = 24.0 / (timetableGrid.getRowCount() - 1);
		double startHours = start.getHour() + start.getMinute() / 60.0;
		double endHours = end.getHour() + end.getMinute() / 60.0;

		double height = (endHours - startHours) / timeStep;
		double offset = (startHours % timeStep) / timeStep;

		return new double[]{height, offset};
	}

	private void updateEventHeight() {
		double cellHeight;
		cellHeight = timetableGrid.getHeight() / timetableGrid.getRowCount();
		timetableGrid.getChildren().stream()
		             .filter(EventLabel.class::isInstance)
		             .forEach(node -> ((EventLabel) node).updateLabelHeight(cellHeight));

		List<RowConstraints> rowConstraints = timetableGrid.getRowConstraints();
		for (RowConstraints constraint : rowConstraints) {
			constraint.setMinHeight(cellHeight);
			constraint.setPrefHeight(cellHeight);
			constraint.setMaxHeight(cellWidth);
		}
	}

	private void updateEventWidth() {
		// 50 is the width of the first column
		cellWidth = (timetableGrid.getWidth() - 50) / (timetableGrid.getColumnCount() - 1);
		timetableGrid.getChildren().stream()
		             .filter(EventLabel.class::isInstance)
		             .forEach(node -> ((EventLabel) node).updateLabelWidth(cellWidth));

		List<ColumnConstraints> columnConstraints = timetableGrid.getColumnConstraints();
		for (int i = 1; i < columnConstraints.size(); i++) {
			ColumnConstraints constraint = columnConstraints.get(i);
			constraint.setMinWidth(cellWidth);
			constraint.setPrefWidth(cellWidth);
			constraint.setMaxWidth(cellWidth);
		}
	}

	private Set<EventLabel> getEventLabelsInRange(int column, int startRow, int endRow) {
		Set<EventLabel> uniqueEventLabels = new HashSet<>();

		for (Node node : timetableGrid.getChildren()) {
			Integer colIndex = GridPane.getColumnIndex(node);
			Integer rowIndex = GridPane.getRowIndex(node);
			Integer rowSpan = GridPane.getRowSpan(node);

			if (colIndex != null && rowIndex != null && rowSpan != null) {
				int nodeEndRow = rowIndex + rowSpan - 1;

				if (colIndex == column &&
				    ((rowIndex >= startRow && rowIndex <= endRow) || (nodeEndRow >= startRow && nodeEndRow <= endRow) ||
				     (rowIndex <= startRow && nodeEndRow >= endRow)) && node instanceof EventLabel eventLabel) {
					uniqueEventLabels.add(eventLabel);
				}
			}
		}

		return uniqueEventLabels;
	}

	private record TimeRange(LocalDateTime start, LocalDateTime end) {
	}
}
