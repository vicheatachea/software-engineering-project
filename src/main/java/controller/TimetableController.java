package controller;

import model.TimetableModel;

/**
 * Controller class for managing timetables in the system.
 * Provides methods to fetch and delete timetables by interacting with the TimetableModel.
 */
public class TimetableController {

	private static final TimetableModel timetableModel = new TimetableModel();

	/**
	 * Retrieves the timetable ID associated with the currently logged-in user.
	 *
	 * @return The timetable ID for the current user or null if not found
	 */
	public Long fetchTimetableForUser() {
		return timetableModel.fetchTimetableForUser();
	}

	/**
	 * Retrieves the timetable ID for a specific group.
	 *
	 * @param groupName The name of the group whose timetable is requested
	 * @return The timetable ID for the specified group or null if not found
	 */
	public Long fetchTimetableForGroup(String groupName) {
		return timetableModel.fetchTimetableForGroup(groupName);
	}

	/**
	 * Deletes all timetables from the system.
	 * This is a maintenance operation and should be used with caution.
	 */
	public void deleteAllTimetables() {
		timetableModel.deleteAllTimetables();
	}
}
