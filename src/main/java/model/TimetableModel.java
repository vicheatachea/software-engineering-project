package model;

import dao.TimetableDAO;

/**
 * Manages timetable operations for users and groups.
 * Provides functionality to retrieve and delete timetables.
 */
public class TimetableModel {

	private static final TimetableDAO timetableDAO = new TimetableDAO();

	private static final UserModel userModel = new UserModel();

	/**
	 * Retrieves the timetable ID for the currently logged-in user.
	 *
	 * @return The timetable ID for the current user
	 * @throws IllegalArgumentException if the user or timetable is not found
	 */
	public Long fetchTimetableForUser() {
		long userId = userModel.fetchCurrentUserId();

		if (userId == -1) {
			throw new IllegalArgumentException("User not found");
		}

		Long timetableId = timetableDAO.findByUserId(userId).getId();

		if (timetableId == null) {
			throw new IllegalArgumentException("Timetable not found");
		}

		return timetableId;
	}

	/**
	 * Retrieves the timetable ID for a specific group.
	 *
	 * @param groupName The name of the group
	 * @return The timetable ID for the specified group
	 * @throws IllegalArgumentException if the timetable is not found
	 */
	public Long fetchTimetableForGroup(String groupName) {
		Long timetableId = timetableDAO.findByGroupName(groupName).getId();

		if (timetableId == null) {
			throw new IllegalArgumentException("Timetable not found");
		}

		return timetableId;
	}

	/**
	 * Deletes all timetables in the system and logs out the current user.
	 */
	public void deleteAllTimetables() {
		timetableDAO.deleteAll();
		userModel.logout();
	}
}
