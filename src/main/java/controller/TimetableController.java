package controller;

import model.TimetableModel;

public class TimetableController {

	private static final TimetableModel timetableModel = new TimetableModel();

	// Fetch timetable id from current user
	public Long fetchTimetableForUser() {
		return timetableModel.fetchTimetableForUser();
	}

	// Fetch timetable id for a group by group name
	public Long fetchTimetableForGroup(String groupName) {
		return timetableModel.fetchTimetableForGroup(groupName);
	}

	public void deleteAllTimetables() {
		timetableModel.deleteAllTimetables();
	}
}
